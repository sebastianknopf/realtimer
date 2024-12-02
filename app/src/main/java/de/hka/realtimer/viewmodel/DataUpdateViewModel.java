package de.hka.realtimer.viewmodel;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putLong(Config.LAST_DATA_UPDATE_TIMESTAMP, System.currentTimeMillis() / 1000L);
                sharedPreferencesEditor.apply();
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

        /*GtfsReader reader = new GtfsReader();
        reader.setInputLocation(gtfsInputFile);

        GtfsDaoImpl dao = new GtfsDaoImpl();
        reader.setEntityStore(dao);

        reader.run();*/

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyMqttConnection() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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