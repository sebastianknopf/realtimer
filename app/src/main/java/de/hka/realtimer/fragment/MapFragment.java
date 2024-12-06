package de.hka.realtimer.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.common.Config;
import de.hka.realtimer.databinding.FragmentMapBinding;
import de.hka.realtimer.viewmodel.MapViewModel;
import de.hka.realtimer.R;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;

import java.nio.charset.StandardCharsets;

public class MapFragment extends Fragment {

    private FragmentMapBinding dataBinding;
    private MapViewModel viewModel;

    private NavController navigationController;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapLibre.getInstance(this.requireContext());
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
        this.dataBinding.mapView.getMapAsync(map -> {
            map.getUiSettings().setLogoEnabled(false);
            map.getUiSettings().setAttributionEnabled(false);

            map.setStyle("https://sgx.geodatenzentrum.de/gdz_basemapde_vektor/styles/bm_web_col.json");
            map.setCameraPosition(new CameraPosition.Builder().target(new LatLng(48.8922, 8.6946)).zoom(14.0).build());
        });

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(MapViewModel.class);
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
}