package de.hka.realtimer.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import de.hka.realtimer.model.TripDetails;
import de.hka.realtimer.viewmodel.TripViewModel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.maplibre.android.location.permissions.PermissionsManager;

import java.util.Date;

public class TripFragment extends Fragment implements LocationListener {

    public final static String ARG_TRIP_ID = "ARG_TRIP_ID";
    public final static String ARG_SERVICE_DAY = "ARG_SERVICE_DAY";

    private FragmentTripBinding dataBinding;
    private TripViewModel viewModel;
    private NavController navigationController;

    private String tripId;
    private long serviceDay;

    private final StopTimeListAdapter stopTimeListAdapter;

    private ActivityResultLauncher<String> locationPermissionLauncher;

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

        this.locationPermissionLauncher = this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                this.enableLocationUpdates();
            } else {
                Log.d(this.getClass().getSimpleName(), "Location permission refused!");
            }
        });
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
            this.viewModel.updateTimetableDifference(item);
        });

        this.dataBinding.btnLeaveTrip.setOnClickListener(btn -> {
            this.viewModel.deleteRealtimeData();

            this.navigationController.navigate(R.id.action_tripFragment_to_mapFragment);
        });

        this.viewModel.getTripDetails().observe(this.getViewLifecycleOwner(), trip -> {
            if (trip != null) {
                this.stopTimeListAdapter.setStopTimeList(trip.getStopTimes());
                this.dataBinding.viewTripOverview.setVisibility(View.VISIBLE);
                this.dataBinding.viewTripDetails.setVisibility(View.VISIBLE);

                this.stopTimeListAdapter.selectItem(0);
            }
        });

        this.viewModel.loadTripDetails(this.tripId, this.serviceDay);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.enableLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.disableLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RealtimeRepository repository = RealtimeRepository.getInstance(this.getContext());
        repository.disconnectBroker();
    }

    private void enableLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT > 30) {
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 1000, 50, this);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
            }
        } else {
            this.locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void disableLocationUpdates() {
        LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        TripDetails tripDetails = this.viewModel.getTripDetails().getValue();
        if (tripDetails != null) {
            for (int s = 0; s < tripDetails.getStopTimes().size(); s++) {
                StopTime stopTime = tripDetails.getStopTimes().get(s);

                Location stopLocation = new Location("GPS");
                stopLocation.setLatitude(stopTime.getStop().getLatitude());
                stopLocation.setLongitude(stopTime.getStop().getLongitude());

                double distance = stopLocation.distanceTo(location);
                if (distance < 50) {
                    this.stopTimeListAdapter.selectItem(s);
                    break;
                }
            }
        }

        this.viewModel.updateVehiclePosition(location, tripDetails);
    }
}