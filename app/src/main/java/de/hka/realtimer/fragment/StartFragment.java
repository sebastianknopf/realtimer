package de.hka.realtimer.fragment;

import androidx.databinding.DataBindingUtil;
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
import de.hka.realtimer.common.InitializationStatus;
import de.hka.realtimer.databinding.FragmentStartBinding;
import de.hka.realtimer.viewmodel.StartViewModel;
import de.hka.realtimer.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartFragment extends Fragment {

    private FragmentStartBinding dataBinding;
    private StartViewModel viewModel;
    private NavController navigationController;

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    public StartFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.setTitle(R.string.start_title);

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(false);

        // check whether configuration has already been done and data are updated properly
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(Config.CONFIGURATION_DONE, false)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ConfigFragment.ARG_FIRST_START, true);

            this.navigationController.navigate(R.id.action_startFragment_to_configFragmentFirstStart, bundle);
        } else {
            this.viewModel.runDataUpdate();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.btnConfig.setOnClickListener(view -> {
            this.navigationController.navigate(R.id.action_startFragment_to_configFragment);
        });

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(StartViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        this.viewModel.getDataUpdateStatus().observe(this.getViewLifecycleOwner(), initializationStatus -> {
            if (initializationStatus == InitializationStatus.SUCCESS) {
                this.navigationController.navigate(R.id.action_startFragment_to_mapFragment);
            } else if (initializationStatus == InitializationStatus.ERROR) {
                this.dataBinding.btnConfig.setVisibility(View.VISIBLE);
                this.dataBinding.pgbUpdateProgress.setVisibility(View.INVISIBLE);
                this.dataBinding.txtDataUpdateInfo.setText(R.string.start_init_error);
            }
        });
    }
}