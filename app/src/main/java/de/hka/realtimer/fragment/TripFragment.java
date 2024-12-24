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
import de.hka.realtimer.data.RealtimeRepository;
import de.hka.realtimer.databinding.FragmentTripBinding;
import de.hka.realtimer.model.StopTime;
import de.hka.realtimer.viewmodel.TripViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

public class TripFragment extends Fragment {

    public final static String ARG_TRIP_ID = "ARG_TRIP_ID";
    public final static String ARG_SERVICE_DAY = "ARG_SERVICE_DAY";

    private FragmentTripBinding dataBinding;
    private TripViewModel viewModel;
    private NavController navigationController;

    private String tripId;
    private long serviceDay;

    private StopTime currentStopTime;
    private final StopTimeListAdapter stopTimeListAdapter;

    public static TripFragment newInstance() {
        return new TripFragment();
    }

    public TripFragment() {
        this.stopTimeListAdapter = new StopTimeListAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        if (args != null) {
            this.tripId = args.getString(ARG_TRIP_ID, null);
            this.serviceDay = args.getLong(ARG_SERVICE_DAY, (new Date().getTime() / 1000L));
        }

        RealtimeRepository repository = RealtimeRepository.getInstance(this.getContext());
        repository.connectBroker();
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

        this.dataBinding.lstStopTimes.setAdapter(this.stopTimeListAdapter);

        this.stopTimeListAdapter.setOnItemSelectListener(item -> {
            this.currentStopTime = item;

            this.viewModel.calculateCurrentDelay(this.currentStopTime);
            this.viewModel.sendTripRealtimeData(this.currentStopTime, this.serviceDay);
        });

        this.dataBinding.btnLeaveTrip.setOnClickListener(btn -> {
            this.viewModel.deleteTripRealtimeData(this.serviceDay);

            this.navigationController.navigate(R.id.action_tripFragment_to_mapFragment);
        });

        this.viewModel.getTripDetails().observe(this.getViewLifecycleOwner(), trip -> {
            this.stopTimeListAdapter.setStopTimeList(trip.getStopTimes());
            this.dataBinding.viewTripOverview.setVisibility(View.VISIBLE);
            this.dataBinding.viewTripDetails.setVisibility(View.VISIBLE);

            this.stopTimeListAdapter.selectItem(0);
        });

        this.viewModel.loadTripDetails(this.tripId, this.serviceDay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RealtimeRepository repository = RealtimeRepository.getInstance(this.getContext());
        repository.disconnectBroker();
    }
}