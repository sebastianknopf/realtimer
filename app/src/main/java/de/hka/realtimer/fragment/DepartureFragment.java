package de.hka.realtimer.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.R;
import de.hka.realtimer.databinding.FragmentDepartureBinding;
import de.hka.realtimer.model.DepartureWithStopAndTrip;
import de.hka.realtimer.viewmodel.DepartureViewModel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class DepartureFragment extends Fragment {

    public final static String ARG_STATION_ID = "ARG_STATION_ID";
    public final static String ARG_STATION_NAME = "ARG_STATION_NAME";

    private FragmentDepartureBinding dataBinding;
    private DepartureViewModel viewModel;
    private NavController navigationController;

    private String stationId;
    private String stationName;

    public static DepartureFragment newInstance() {
        return new DepartureFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        if (args != null) {
            this.stationId = args.getString(ARG_STATION_ID, null);
            this.stationName = args.getString(ARG_STATION_NAME, null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.setTitle(this.stationName);

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_departure, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(DepartureViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        this.viewModel.getDepartures().observe(this.getViewLifecycleOwner(), departureWithStopAndTrips -> {
            for (DepartureWithStopAndTrip departure : departureWithStopAndTrips) {
                Log.d(this.getClass().getSimpleName(), departure.getStopTime().getDepartureTime() +  ": " + departure.getTrip().getHeadsign());
            }
        });

        this.viewModel.loadDeparturesForStation(this.stationId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.navigationController.navigate(R.id.action_departureFragment_to_mapFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}