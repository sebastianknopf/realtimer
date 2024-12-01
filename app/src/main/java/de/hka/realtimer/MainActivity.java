package de.hka.realtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.hka.realtimer.common.Config;
import de.hka.realtimer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding dataBinding;

    private NavController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.setSupportActionBar(this.dataBinding.toolbar);

        this.navigationController = Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public NavController getNavigationController() {
        return this.navigationController;
    }
}