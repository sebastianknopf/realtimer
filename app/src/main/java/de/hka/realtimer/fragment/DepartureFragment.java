package de.hka.realtimer.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.R;
import de.hka.realtimer.adpater.DepartureListAdapter;
import de.hka.realtimer.databinding.FragmentDepartureBinding;
import de.hka.realtimer.viewmodel.DepartureViewModel;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class DepartureFragment extends Fragment {

    public final static String ARG_STATION_ID = "ARG_STATION_ID";
    public final static String ARG_STATION_NAME = "ARG_STATION_NAME";

    private FragmentDepartureBinding dataBinding;
    private DepartureViewModel viewModel;
    private NavController navigationController;

    private String stationId;
    private String stationName;
    private final DepartureListAdapter departureListAdapter;

    public static DepartureFragment newInstance() {
        return new DepartureFragment();
    }

    public DepartureFragment() {
        this.departureListAdapter = new DepartureListAdapter();
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

        this.dataBinding.lstDepartures.setAdapter(this.departureListAdapter);

        this.departureListAdapter.setOnItemClickListener(item -> {
            Bundle bundle = new Bundle();
            bundle.putString(TripFragment.ARG_TRIP_ID, item.getTripId());
            bundle.putSerializable(TripFragment.ARG_SERVICE_DAY, item.getOperationDay());

            this.navigationController.navigate(R.id.action_departureFragment_to_tripFragment, bundle);
        });

        this.viewModel.getDeparturesList().observe(this.getViewLifecycleOwner(), this.departureListAdapter::setDepartureList);

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