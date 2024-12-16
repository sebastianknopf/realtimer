package de.hka.realtimer.viewmodel;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import org.gtfs.reader.GtfsReader;
import org.gtfs.reader.GtfsSimpleDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.hka.realtimer.R;
import de.hka.realtimer.common.Config;
import de.hka.realtimer.common.DataUpdateStatus;
import de.hka.realtimer.data.GtfsRelationalDao;
import de.hka.realtimer.data.GtfsRepository;
import de.hka.realtimer.data.RealtimeRepository;

public class DataUpdateViewModel extends AndroidViewModel {

    private MutableLiveData<DataUpdateStatus> dataUpdateStatus;
    private MutableLiveData<Integer> dataUpdateProgress;
    private MutableLiveData<String> dataUpdateMessage;

    public DataUpdateViewModel(@NonNull Application application) {
        super(application);

        this.dataUpdateStatus = new MutableLiveData<>(DataUpdateStatus.INITIAL);
        this.dataUpdateProgress = new MutableLiveData<>(0);
        this.dataUpdateMessage = new MutableLiveData<>("");
    }

    public void runDataUpdate() {
        Runnable runnable = () -> {
            try {
                this.dataUpdateMessage.postValue(this.getApplication().getString(R.string.data_update_download));
                this.downloadGtfsFeed();

                this.dataUpdateMessage.postValue(this.getApplication().getString(R.string.data_update_integration));
                this.integrateGtfsFeed();

                this.dataUpdateMessage.postValue(this.getApplication().getString(R.string.data_update_mqtt_test));
                this.verifyMqttConnection();

                this.dataUpdateStatus.postValue(DataUpdateStatus.SUCCESS);
            } catch (Exception exception) {
                this.dataUpdateStatus.postValue(DataUpdateStatus.ERROR);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void downloadGtfsFeed() throws Exception {
        SharedPreferences sharedPreferences = this.getApplication().getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);
        String gtfsDownloadUrl = sharedPreferences.getString(Config.GTFS_FEED_URL, null);

        Exception exception = null;

        if (gtfsDownloadUrl != null) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(gtfsDownloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new NetworkErrorException();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                output = new FileOutputStream(this.getApplication().getFilesDir() + "/gtfs.zip");

                byte[] data = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        this.dataUpdateProgress.postValue((int) (total * 100 / fileLength));
                    }

                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                exception = e;
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }

                    if (input != null) {
                        input.close();
                    }

                } catch (IOException ignored) {
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }

            if (exception != null) {
                throw exception;
            }
        }
    }

    public void integrateGtfsFeed() throws IOException {
        File gtfsInputFile = new File(this.getApplication().getFilesDir() + "/gtfs.zip");

        GtfsRepository repository = GtfsRepository.getInstance();
        repository.readInputFeed(gtfsInputFile.getAbsolutePath());
    }

    public void verifyMqttConnection() {
        RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
        repository.runConnectionTest();
    }

    public LiveData<DataUpdateStatus> getDataUpdateStatus() {
        return this.dataUpdateStatus;
    }

    public LiveData<Integer> getDataUpdateProgress() {
        return this.dataUpdateProgress;
    }

    public LiveData<String> getDataUpdateMessage() {
        return this.dataUpdateMessage;
    }
}