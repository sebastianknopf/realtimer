package de.hka.realtimer.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.R;
import de.hka.realtimer.adpater.StopTimeListAdapter;
import de.hka.realtimer.databinding.FragmentTripBinding;
import de.hka.realtimer.viewmodel.TripViewModel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TripFragment extends Fragment {

    public final static String ARG_TRIP_ID = "ARG_TRIP_ID";

    private FragmentTripBinding dataBinding;
    private TripViewModel viewModel;
    private NavController navigationController;

    private String tripId;
    private final StopTimeListAdapter departureListAdapter;

    public static TripFragment newInstance() {
        return new TripFragment();
    }

    public TripFragment() {
        this.departureListAdapter = new StopTimeListAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        if (args != null) {
            this.tripId = args.getString(ARG_TRIP_ID, null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.setTitle(R.string.trip_title);

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        this.dataBinding.lstStopTimes.setAdapter(this.departureListAdapter);

        this.departureListAdapter.setOnItemSelectListener(item -> {
            Log.d(this.getClass().getSimpleName(), item.getStop().getId());
        });

        this.dataBinding.btnLeaveTrip.setOnClickListener(btn -> {
            this.navigationController.navigate(R.id.action_tripFragment_to_mapFragment);
        });

        this.viewModel.getTripDetails().observe(this.getViewLifecycleOwner(), trip -> {
            this.departureListAdapter.setStopTimeList(trip.getStopTimes());
            this.dataBinding.viewTripDetails.setVisibility(View.VISIBLE);
        });

        this.viewModel.loadTripDetails(this.tripId);
    }
}