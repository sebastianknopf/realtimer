package de.hka.realtimer.viewmodel;

import android.app.Application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.hka.realtimer.R;
import de.hka.realtimer.common.DataUpdateStatus;
import de.hka.realtimer.data.GtfsRepository;
import de.hka.realtimer.model.DepartureWithStopAndTrip;

public class DepartureViewModel extends AndroidViewModel {

    private MutableLiveData<List<DepartureWithStopAndTrip>> departures;

    public DepartureViewModel(@NonNull Application application) {
        super(application);

        this.departures = new MutableLiveData<>(new ArrayList<>());
    }

    public void loadDeparturesForStation(String stationId) {
        Runnable runnable = () -> {
            GtfsRepository repository = GtfsRepository.getInstance();
            this.departures.postValue(repository.getDataAccessObject().getDeparturesForStation(stationId, new Date()));
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public LiveData<List<DepartureWithStopAndTrip>> getDepartures() {
        return this.departures;
    }
}