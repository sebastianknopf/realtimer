package de.hka.realtimer.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.viewmodel.ConfigViewModel;
import de.hka.realtimer.R;
import de.hka.realtimer.databinding.FragmentConfigBinding;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ConfigFragment extends Fragment {

    public final static String ARG_FIRST_START = "ARG_FIRST_START";

    private FragmentConfigBinding dataBinding;
    private ConfigViewModel viewModel;
    private NavController navigationController;

    private boolean firstStart;

    public static ConfigFragment newInstance(boolean firstStart) {
        return new ConfigFragment(firstStart);
    }

    public ConfigFragment() {
        super();
    }

    private ConfigFragment(boolean firstStart) {
        this.firstStart = firstStart;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        if (args != null) {
            this.firstStart = args.getBoolean(ARG_FIRST_START, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.navigationController.navigate(R.id.action_configFragment_to_mapFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(!this.firstStart);

        if (this.firstStart) {
            mainActivity.setTitle(R.string.config_title_first_start);
        } else {
            mainActivity.setTitle(R.string.config_title);
        }

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(!this.firstStart);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_config, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.swSendVehiclePositions.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                this.dataBinding.txtVehicleIdLayout.setVisibility(View.VISIBLE);
            } else {
                this.dataBinding.txtVehicleIdLayout.setVisibility(View.GONE);
            }
        });

        this.dataBinding.btnSaveConfig.setOnClickListener(view -> {
            this.viewModel.setApplicationConfig(
                    this.dataBinding.txtGtfsFeedUrl.getText().toString(),
                    this.dataBinding.txtMqttBrokerHost.getText().toString(),
                    this.dataBinding.txtMqttBrokerPort.getText().toString(),
                    this.dataBinding.txtMqttBrokerUsername.getText().toString(),
                    this.dataBinding.txtMqttBrokerPassword.getText().toString(),
                    this.dataBinding.txtMqttTopicTripUpdates.getText().toString(),
                    this.dataBinding.txtMqttTopicVehiclePositions.getText().toString(),
                    this.dataBinding.swSendTripUpdates.isChecked(),
                    this.dataBinding.swSendVehiclePositions.isChecked(),
                    this.dataBinding.txtVehicleId.getText().toString()
            );

            this.navigationController.navigate(R.id.action_configFragment_to_dataUpdateFragment);
        });

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.viewModel.loadApplicationConfig();
    }
}