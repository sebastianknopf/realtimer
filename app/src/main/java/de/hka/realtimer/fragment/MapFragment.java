package de.hka.realtimer.fragment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.databinding.FragmentMapBinding;
import de.hka.realtimer.model.Station;
import de.hka.realtimer.viewmodel.MapViewModel;
import de.hka.realtimer.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.location.LocationComponent;
import org.maplibre.android.location.LocationComponentActivationOptions;
import org.maplibre.android.location.LocationComponentOptions;
import org.maplibre.android.location.engine.LocationEngineCallback;
import org.maplibre.android.location.engine.LocationEngineRequest;
import org.maplibre.android.location.engine.LocationEngineResult;
import org.maplibre.android.location.modes.CameraMode;
import org.maplibre.android.location.permissions.PermissionsListener;
import org.maplibre.android.location.permissions.PermissionsManager;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.Style;
import org.maplibre.android.plugins.annotation.Symbol;
import org.maplibre.android.plugins.annotation.SymbolManager;
import org.maplibre.android.plugins.annotation.SymbolOptions;
import org.maplibre.android.style.layers.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MapFragment extends Fragment {

    private FragmentMapBinding dataBinding;
    private MapViewModel viewModel;

    private NavController navigationController;

    private MapLibreMap map;
    private Style mapStyle;
    private SymbolManager mapSymbolManager;

    private Location location;
    private ActivityResultLauncher<String> locationPermissionLauncher;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapLibre.getInstance(this.requireContext());

        this.locationPermissionLauncher = this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                this.enableLocationComponent();
            } else {
                Log.d(this.getClass().getSimpleName(), "Location permission refused!");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.map_menu_config) {
            this.navigationController.navigate(R.id.action_mapFragment_to_configFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        // configure map view
        this.dataBinding.mapView.onCreate(savedInstanceState);

        this.dataBinding.mapView.getMapAsync(map -> {
            this.map = map;

            this.map.getUiSettings().setLogoEnabled(false);
            this.map.getUiSettings().setAttributionEnabled(false);

            this.map.setStyle("https://sgx.geodatenzentrum.de/gdz_basemapde_vektor/styles/bm_web_col.json", style -> {
                this.mapStyle = style;

                this.mapStyle.addImage("ic_location", this.getContext().getDrawable(R.drawable.ic_location));

                this.mapSymbolManager = new SymbolManager(this.dataBinding.mapView, this.map, this.mapStyle);
                this.mapSymbolManager.addClickListener(this::onAnnotationClick);

                this.viewModel.getStationList().observe(this.getViewLifecycleOwner(), stations -> {
                    List<SymbolOptions> stationSymbolList = new ArrayList<>();

                    for (Station station : stations) {
                        JsonObject stationJsonObject = new JsonObject();
                        stationJsonObject.addProperty("station_id", station.getId());
                        stationJsonObject.addProperty("station_name", station.getName());

                        SymbolOptions symbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(station.getLatitude(), station.getLongitude()))
                                .withData(stationJsonObject)
                                .withIconImage("ic_location")
                                .withIconAnchor(Property.ICON_ANCHOR_BOTTOM)
                                .withIconSize(2.0f);

                        stationSymbolList.add(symbolOptions);
                    }

                    this.mapSymbolManager.create(stationSymbolList);
                });

                this.checkLocationPermission();
            });

            if (savedInstanceState == null) {
                this.map.setCameraPosition(new CameraPosition.Builder().target(new LatLng(48.8922, 8.6946)).zoom(14.0).build());
            }
        });

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        this.viewModel.loadStations();
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.setTitle(R.string.map_title);

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(true);

        this.dataBinding.mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.dataBinding.mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        this.dataBinding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

        this.dataBinding.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        this.dataBinding.mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.dataBinding.mapView.onDestroy();
    }

    private boolean onAnnotationClick(Symbol symbol) {
        if (symbol.getData() != null) {
            Bundle args = new Bundle();
            JsonObject stationJsonObject = symbol.getData().getAsJsonObject();
            args.putString(DepartureFragment.ARG_STATION_ID, stationJsonObject.get("station_id").getAsString());
            args.putString(DepartureFragment.ARG_STATION_NAME, stationJsonObject.get("station_name").getAsString());

            this.navigationController.navigate(R.id.action_mapFragment_to_departureFragment, args);

            return true;
        }

        return false;
    }

    private void checkLocationPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(this.getContext())) {
            this.enableLocationComponent();
        } else {
            this.locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(this.getContext())
                .pulseEnabled(true)
                .build();

        LocationEngineRequest locationEngineRequest = new LocationEngineRequest.Builder(750)
                .setFastestInterval(750)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions.builder(this.getContext(), this.mapStyle)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .locationEngineRequest(locationEngineRequest)
                .build();

        LocationComponent locationComponent = this.map.getLocationComponent();
        locationComponent.activateLocationComponent(locationComponentActivationOptions);
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.getLocationEngine().requestLocationUpdates(locationEngineRequest, new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult locationEngineResult) {
                findClosestStations(locationEngineResult.getLastLocation());
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(this.getClass().getSimpleName(), "Location update failed!");
            }
        }, null);
    }

    private void findClosestStations(Location location) {
        List<Station> stationList = this.viewModel.getStationList().getValue();
        if (stationList != null) {
            Collections.sort(stationList, (s1, s2) -> {
                Location loc1 = new Location("GPS");
                loc1.setLatitude(s1.getLatitude());
                loc1.setLongitude(s1.getLongitude());

                Location loc2 = new Location("GPS");
                loc2.setLatitude(s2.getLatitude());
                loc2.setLongitude(s2.getLongitude());

                double d1 = location.distanceTo(loc1);
                double d2 = location.distanceTo(loc2);

                return Double.compare(d1, d2);
            });

            Log.d(this.getClass().getSimpleName(), "10 top closest locations are ...");
            for (int i = 0; i < Math.min(10, stationList.size()); i++) {
                Log.d(this.getClass().getSimpleName(), stationList.get(i).getName());
            }
        }
    }
}