package de.hka.realtimer.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.hka.realtimer.MainActivity;
import de.hka.realtimer.common.DataUpdateStatus;
import de.hka.realtimer.databinding.FragmentDataUpdateBinding;
import de.hka.realtimer.viewmodel.DataUpdateViewModel;
import de.hka.realtimer.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DataUpdateFragment extends Fragment {

    private FragmentDataUpdateBinding dataBinding;
    private DataUpdateViewModel viewModel;
    private NavController navigationController;

    public static DataUpdateFragment newInstance() {
        return new DataUpdateFragment();
    }

    public DataUpdateFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) this.getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.setTitle(R.string.data_update_title);

        this.navigationController = mainActivity.getNavigationController();

        this.setHasOptionsMenu(false);

        this.viewModel.runDataUpdate();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_update, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.btnConfig.setOnClickListener(view -> {
            this.navigationController.navigate(R.id.action_dataUpdateFragment_to_configFragment);
        });

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(DataUpdateViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        this.viewModel.getDataUpdateStatus().observe(this.getViewLifecycleOwner(), dataUpdateStatus -> {
            if (dataUpdateStatus == DataUpdateStatus.SUCCESS) {
                this.navigationController.navigate(R.id.action_dataUpdateFragment_to_mapFragment);
            } else if (dataUpdateStatus == DataUpdateStatus.ERROR) {
                this.dataBinding.btnConfig.setVisibility(View.VISIBLE);
                this.dataBinding.pgbUpdateProgress.setVisibility(View.INVISIBLE);
                this.dataBinding.txtDataUpdateInfo.setText(R.string.data_update_error);
            }
        });
    }
}