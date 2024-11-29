package de.hka.realtimer;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hka.realtimer.databinding.FragmentConfigBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

public class ConfigFragment extends Fragment {

    private FragmentConfigBinding dataBinding;
    private ConfigViewModel viewModel;

    public static ConfigFragment newInstance() {
        return new ConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_config, container, false);

        this.dataBinding.swSendVehiclePositions.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                this.dataBinding.txtVehicleIdLayout.setVisibility(View.VISIBLE);
            } else {
                this.dataBinding.txtVehicleIdLayout.setVisibility(View.GONE);
            }
        });

        this.dataBinding.btnSaveConfig.setOnClickListener(view -> {

        });

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
    }
}