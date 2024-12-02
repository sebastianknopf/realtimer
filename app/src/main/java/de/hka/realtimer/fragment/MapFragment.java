package de.hka.realtimer.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.common.Config;
import de.hka.realtimer.viewmodel.MapViewModel;
import de.hka.realtimer.R;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {

    private MapViewModel viewModel;

    private NavController navigationController;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.setTitle(R.string.map_title);

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(true);
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
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(MapViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);

        long currentUnixTimestamp = System.currentTimeMillis() / 1000L;
        double lastUpdateHours = Math.floor((currentUnixTimestamp - (double) sharedPreferences.getLong(Config.LAST_DATA_UPDATE_TIMESTAMP, 0)) / 3600);

        if (!sharedPreferences.getBoolean(Config.CONFIGURATION_DONE, false)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ConfigFragment.ARG_FIRST_START, true);

            this.navigationController.navigate(R.id.action_mapFragment_to_configFragmentFirstStart, bundle);
        } else if (lastUpdateHours >= 3) {
            this.navigationController.navigate(R.id.action_mapFragment_to_dataUpdateFragment);
        }
    }
}