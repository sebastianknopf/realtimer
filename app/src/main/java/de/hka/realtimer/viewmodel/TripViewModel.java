package de.hka.realtimer.viewmodel;

import android.app.Application;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.hka.realtimer.data.OpenTripPlannerRepository;
import de.hka.realtimer.data.RealtimeRepository;
import de.hka.realtimer.model.StopTime;
import de.hka.realtimer.model.TripDetails;

public class TripViewModel extends AndroidViewModel {

    private MutableLiveData<String> currentDelayText;

    private int differenceInMinutes;

    public TripViewModel(@NonNull Application application) {
        super(application);

        this.currentDelayText = new MutableLiveData<>("#");
    }

    public void loadTripDetails(String tripId, long serviceDay) {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        repository.loadTripDetails(tripId, serviceDay);
    }

    public void calculateCurrentDelay(StopTime stopTime) {
        Date referenceTimestamp = new Date();

        long differenceInSeconds = referenceTimestamp.getTime() - stopTime.getDepartureTime().getTime();
        this.differenceInMinutes = Math.round(differenceInSeconds / (60 * 1000));

        String delayText = "#";
        if (this.differenceInMinutes > 0) {
            if (this.differenceInMinutes > 99) {
                delayText = ">99";
            } else {
                delayText = String.format("+%d", this.differenceInMinutes);
            }
        } else if (this.differenceInMinutes < 0) {
            if (stopTime.getStopSequence() == 1) {
                delayText = String.format("#%d", Math.abs(this.differenceInMinutes));
            } else {
                delayText = String.format("%d", this.differenceInMinutes);
            }
        } else if (this.differenceInMinutes == 0) {
            delayText = String.format("+/-0");
        }

        this.currentDelayText.setValue(delayText);
    }

    public void sendTripRealtimeData(StopTime stopTime) {
        TripDetails tripDetails = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext()).getTripDetails().getValue();
        if (tripDetails != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.sendTripRealtimeData(tripDetails, stopTime, this.differenceInMinutes);
        }
    }

    public void deleteTripRealtimeData() {
        TripDetails tripDetails = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext()).getTripDetails().getValue();
        if (tripDetails != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.deleteTripRealtimeData(tripDetails);
        }
    }

    public LiveData<TripDetails> getTripDetails() {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        return repository.getTripDetails();
    }

    public LiveData<String> getCurrentDelayText() {
        return this.currentDelayText;
    }
}