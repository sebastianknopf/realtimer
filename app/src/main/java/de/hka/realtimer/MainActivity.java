package de.hka.realtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import de.hka.realtimer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        this.setSupportActionBar(this.dataBinding.toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = this.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("CONFIG_CONFIGURATION_DONE", false)) {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main, new MapFragment());
            transaction.commit();
        } else {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main, new ConfigFragment());
            transaction.commit();
        }
    }
}