package de.hka.realtimer.viewmodel;

import android.app.Application;

import com.google.transit.realtime.GtfsRealtime;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.hka.realtimer.data.GtfsRepository;
import de.hka.realtimer.data.RealtimeRepository;
import de.hka.realtimer.model.StopTimeWithStop;
import de.hka.realtimer.model.TripWithRoute;
import de.hka.realtimer.model.TripWithStopTimesAndRoute;

public class TripViewModel extends AndroidViewModel {

    private MutableLiveData<TripWithStopTimesAndRoute> tripDetails;
    private MutableLiveData<String> currentDelayText;

    private int differenceInMinutes;

    public TripViewModel(@NonNull Application application) {
        super(application);

        this.tripDetails = new MutableLiveData<>();
        this.currentDelayText = new MutableLiveData<>("#");
    }

    public void loadTripDetails(String tripId) {
        Runnable runnable = () -> {
            GtfsRepository repository = GtfsRepository.getInstance();
            this.tripDetails.postValue(repository.getDataAccessObject().getTripDetails(tripId));
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void calculateCurrentDelay(StopTimeWithStop stopTime) {
        Date referenceTimestamp = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceTimestamp);

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stopTime.getDepartureTime().substring(0, 2)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(stopTime.getDepartureTime().substring(3, 5)));
        calendar.set(Calendar.SECOND, Integer.parseInt(stopTime.getDepartureTime().substring(6, 8)));

        Date stopDepartureTimestamp = calendar.getTime();

        long differenceInSeconds = referenceTimestamp.getTime() - stopDepartureTimestamp.getTime();
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

    public void sendTripRealtimeData(StopTimeWithStop stopTime) {
        TripWithRoute trip = this.tripDetails.getValue();
        if (trip != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.sendTripRealtimeData(trip, stopTime, this.differenceInMinutes);
        }
    }

    public void deleteTripRealtimeData() {
        TripWithRoute trip = this.tripDetails.getValue();
        if (trip != null) {
            RealtimeRepository repository = RealtimeRepository.getInstance(this.getApplication().getApplicationContext());
            repository.deleteTripRealtimeData(trip);
        }
    }

    public LiveData<TripWithStopTimesAndRoute> getTripDetails() {
        return this.tripDetails;
    }

    public LiveData<String> getCurrentDelayText() {
        return this.currentDelayText;
    }
}