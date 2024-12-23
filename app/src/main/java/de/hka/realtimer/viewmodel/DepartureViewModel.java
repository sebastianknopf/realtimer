package de.hka.realtimer.viewmodel;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.hka.realtimer.data.OpenTripPlannerRepository;
import de.hka.realtimer.model.Departure;

public class DepartureViewModel extends AndroidViewModel {

    private MutableLiveData<List<Departure>> departuresList;

    public DepartureViewModel(@NonNull Application application) {
        super(application);

        this.departuresList = new MutableLiveData<>(new ArrayList<>());
    }

    public void loadDeparturesForStation(String stationId) {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());

        repository.getDepartureList().observeForever(departures -> {
            for (Departure departure : departures) {
                Log.d(this.getClass().getSimpleName(), departure.getDepartureTime().toString() + " " + departure.getRouteName() + " " + departure.getTripHeadsign() + " Steig " + departure.getPlatformCode());
            }
        });

        repository.loadDepartures(stationId);
    }

    public LiveData<List<Departure>> getDeparturesList() {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        return repository.getDepartureList();
    }
}