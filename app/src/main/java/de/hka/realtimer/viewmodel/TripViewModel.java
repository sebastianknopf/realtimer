package de.hka.realtimer.viewmodel;

import android.app.Application;
import android.location.Location;

import java.util.Date;
import java.util.Locale;

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

    private Date operationDay;

    private StopTime currentStopTime;
    private int differenceInMinutes;
    private int publishedDifferenceInMinutes;

    public TripViewModel(@NonNull Application application) {
        super(application);

        this.currentDelayText = new MutableLiveData<>("#");
    }

    public void loadTripDetails(String tripId, Date operationDay) {
        this.operationDay = operationDay;

        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        repository.loadTripDetails(tripId, this.operationDay);
    }

    public void updateTimetableDifference(StopTime stopTime) {
        this.currentStopTime = stopTime;
        Date referenceTimestamp = new Date();

        long differenceInSeconds = referenceTimestamp.getTime() - this.currentStopTime.getDepartureTime().getTime();
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
            repository.sendTripRealtimeData(tripDetails, this.currentStopTime, this.operationDay, this.publishedDifferenceInMinutes);
        }
    }

    public void updateVehiclePosition(Location location, TripDetails tripDetails) {
        if (tripDetails != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.sendVehicleRealtimeData(location, tripDetails, this.currentStopTime, this.operationDay);
        } else {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.sendVehicleRealtimeData(location, null, null, this.operationDay);
        }
    }

    public void deleteRealtimeData() {
        RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());

        TripDetails tripDetails = this.getTripDetails().getValue();
        if (tripDetails != null) {
            repository.deleteTripRealtimeData(tripDetails, this.operationDay);
        }

        repository.deleteVehicleRealtimeData();

        OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext()).clearCachedData();
    }

    public LiveData<TripDetails> getTripDetails() {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        return repository.getTripDetails();
    }

    public LiveData<String> getCurrentDelayText() {
        return this.currentDelayText;
    }
}