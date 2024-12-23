package de.hka.realtimer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.hka.realtimer.R;
import de.hka.realtimer.common.InitializationStatus;
import de.hka.realtimer.data.RealtimeRepository;

public class StartViewModel extends AndroidViewModel {

    private MutableLiveData<InitializationStatus> dataUpdateStatus;
    private MutableLiveData<Integer> dataUpdateProgress;
    private MutableLiveData<String> dataUpdateMessage;

    public StartViewModel(@NonNull Application application) {
        super(application);

        this.dataUpdateStatus = new MutableLiveData<>(InitializationStatus.INITIAL);
        this.dataUpdateProgress = new MutableLiveData<>(0);
        this.dataUpdateMessage = new MutableLiveData<>("");
    }

    public void runDataUpdate() {
        Runnable runnable = () -> {
            try {
                this.dataUpdateStatus.postValue(InitializationStatus.INITIAL);

                this.dataUpdateMessage.postValue(this.getApplication().getString(R.string.start_mqtt_test));
                this.verifyMqttConnection();

                this.dataUpdateStatus.postValue(InitializationStatus.SUCCESS);
            } catch (Exception exception) {
                this.dataUpdateStatus.postValue(InitializationStatus.ERROR);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void verifyMqttConnection() {
        RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
        repository.runConnectionTest();
    }

    public LiveData<InitializationStatus> getDataUpdateStatus() {
        return this.dataUpdateStatus;
    }

    public LiveData<Integer> getDataUpdateProgress() {
        return this.dataUpdateProgress;
    }

    public LiveData<String> getDataUpdateMessage() {
        return this.dataUpdateMessage;
    }
}