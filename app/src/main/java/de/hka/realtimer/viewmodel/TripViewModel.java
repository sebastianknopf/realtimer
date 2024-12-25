package de.hka.realtimer.viewmodel;

import android.app.Application;

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

    private long serviceDay;

    private int differenceInMinutes;
    private int publishedDifferenceInMinutes;

    public TripViewModel(@NonNull Application application) {
        super(application);

        this.currentDelayText = new MutableLiveData<>("#");
    }

    public void loadTripDetails(String tripId, long serviceDay) {
        this.serviceDay = serviceDay;

        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        repository.loadTripDetails(tripId, this.serviceDay);
    }

    public void updateTimetableDifference(StopTime stopTime) {
        Date referenceTimestamp = new Date();

        long differenceInSeconds = referenceTimestamp.getTime() - stopTime.getDepartureTime().getTime();
        this.differenceInMinutes = Math.round(differenceInSeconds / (60 * 1000));
        this.publishedDifferenceInMinutes = this.differenceInMinutes;

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
                this.publishedDifferenceInMinutes = 0;
            } else {
                delayText = String.format("%d", this.differenceInMinutes);
            }
        } else if (this.differenceInMinutes == 0) {
            delayText = String.format("+/-0");
        }

        this.currentDelayText.setValue(delayText);

        // send trip update
        TripDetails tripDetails = this.getTripDetails().getValue();
        if (tripDetails != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.sendTripRealtimeData(tripDetails, stopTime, serviceDay, this.publishedDifferenceInMinutes);
        }
    }

    public void deleteRealtimeData() {
        TripDetails tripDetails = this.getTripDetails().getValue();
        if (tripDetails != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.deleteTripRealtimeData(tripDetails, this.serviceDay);
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