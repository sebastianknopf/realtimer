package de.hka.realtimer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.hka.realtimer.data.OpenTripPlannerRepository;
import de.hka.realtimer.model.Station;
import de.hka.realtimer.otp.StationsListQuery;

public class MapViewModel extends AndroidViewModel {

    public MapViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadStations() {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        repository.loadStations();
    }

    public LiveData<List<Station>> getStationList() {
        OpenTripPlannerRepository repository = OpenTripPlannerRepository.getInstance(this.getApplication().getApplicationContext());
        return repository.getStationsList();
    }
}