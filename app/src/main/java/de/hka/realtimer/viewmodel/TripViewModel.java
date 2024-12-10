package de.hka.realtimer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.hka.realtimer.data.GtfsRepository;
import de.hka.realtimer.model.TripWithStopTimesAndRoute;

public class TripViewModel extends ViewModel {

    private MutableLiveData<TripWithStopTimesAndRoute> tripDetails;

    public TripViewModel() {
        this.tripDetails = new MutableLiveData<>();
    }

    public void loadTripDetails(String tripId) {
        Runnable runnable = () -> {
            GtfsRepository repository = GtfsRepository.getInstance();
            this.tripDetails.postValue(repository.getDataAccessObject().getTripDetails(tripId));
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public LiveData<TripWithStopTimesAndRoute> getTripDetails() {
        return this.tripDetails;
    }
}