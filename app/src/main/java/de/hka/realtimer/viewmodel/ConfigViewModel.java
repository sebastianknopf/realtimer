package de.hka.realtimer.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.hka.realtimer.common.Config;

public class ConfigViewModel extends AndroidViewModel {

    private MutableLiveData<String> otpGraphQlApiUrl;
    private MutableLiveData<String> mqttHost;
    private MutableLiveData<String> mqttPort;
    private MutableLiveData<String> mqttUsername;
    private MutableLiveData<String> mqttPassword;
    private MutableLiveData<String> mqttTopicTripUpdates;
    private MutableLiveData<String> mqttTopicVehiclePositions;
    private MutableLiveData<Boolean> sendTripUpdates;
    private MutableLiveData<Boolean> sendVehiclePositions;
    private MutableLiveData<String> vehicleId;

    public ConfigViewModel(@NonNull Application application) {
        super(application);

        this.otpGraphQlApiUrl = new MutableLiveData<>();
        this.mqttHost = new MutableLiveData<>();
        this.mqttPort = new MutableLiveData<>();
        this.mqttUsername = new MutableLiveData<>();
        this.mqttPassword = new MutableLiveData<>();
        this.mqttTopicTripUpdates = new MutableLiveData<>();
        this.mqttTopicVehiclePositions = new MutableLiveData<>();
        this.sendTripUpdates = new MutableLiveData<>();
        this.sendVehiclePositions = new MutableLiveData<>();
        this.vehicleId = new MutableLiveData<>();
    }

    public void setApplicationConfig(String gtfsFeedUrl, String mqttHost, String mqttPort, String mqttUsername, String mqttPassword, String topicTripUpdates, String topicVehiclePositions, boolean sendTripUpdates, boolean sendVehiclePositions, String vehicleId) {
        SharedPreferences.Editor sharedPreferences = this.getApplication().getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE).edit();

        sharedPreferences.putString(Config.OTP_GRAPHQL_API_URL, gtfsFeedUrl);
        sharedPreferences.putString(Config.MQTT_HOST, mqttHost);
        sharedPreferences.putString(Config.MQTT_PORT, mqttPort);
        sharedPreferences.putString(Config.MQTT_USERNAME, mqttUsername);
        sharedPreferences.putString(Config.MQTT_PASSWORD, mqttPassword);
        sharedPreferences.putString(Config.MQTT_TOPIC_TRIP_UPDATES, topicTripUpdates);
        sharedPreferences.putString(Config.MQTT_TOPIC_VEHICLE_POSITIONS, topicVehiclePositions);
        sharedPreferences.putBoolean(Config.SEND_TRIP_UPDATES, sendTripUpdates);
        sharedPreferences.putBoolean(Config.SEND_VEHICLE_POSITIONS, sendVehiclePositions);
        sharedPreferences.putString(Config.VEHICLE_ID, vehicleId);

        sharedPreferences.putBoolean(Config.CONFIGURATION_DONE, true);

        sharedPreferences.commit();
    }

    public void loadApplicationConfig() {
        SharedPreferences sharedPreferences = this.getApplication().getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);

        this.otpGraphQlApiUrl.setValue(sharedPreferences.getString(Config.OTP_GRAPHQL_API_URL, "https://otp.svprod01.app/graphiql?flavor=gtfs"));
        this.mqttHost.setValue(sharedPreferences.getString(Config.MQTT_HOST, "mqtt.svprod01.app"));
        this.mqttPort.setValue(sharedPreferences.getString(Config.MQTT_PORT, "1883"));
        this.mqttUsername.setValue(sharedPreferences.getString(Config.MQTT_USERNAME, ""));
        this.mqttPassword.setValue(sharedPreferences.getString(Config.MQTT_PASSWORD, ""));
        this.mqttTopicTripUpdates.setValue(sharedPreferences.getString(Config.MQTT_TOPIC_TRIP_UPDATES, "realtime/hka/gtfsrt/tripupdates"));
        this.mqttTopicVehiclePositions.setValue(sharedPreferences.getString(Config.MQTT_TOPIC_VEHICLE_POSITIONS, "realtime/hka/gtfsrt/vehiclepositions"));
        this.sendTripUpdates.setValue(sharedPreferences.getBoolean(Config.SEND_TRIP_UPDATES, true));
        this.sendVehiclePositions.setValue(sharedPreferences.getBoolean(Config.SEND_VEHICLE_POSITIONS, false));
        this.vehicleId.setValue(sharedPreferences.getString(Config.VEHICLE_ID, "Vehicle"));
    }

    public LiveData<String> getOtpGraphQlApiUrl() {
        return this.otpGraphQlApiUrl;
    }

    public LiveData<String> getMqttHost() {
        return this.mqttHost;
    }

    public LiveData<String> getMqttPort() {
        return this.mqttPort;
    }

    public LiveData<String> getMqttUsername() {
        return this.mqttUsername;
    }

    public LiveData<String> getMqttPassword() {
        return this.mqttPassword;
    }

    public LiveData<String> getMqttTopicTripUpdates() {
        return this.mqttTopicTripUpdates;
    }

    public LiveData<String> getMqttTopicVehiclePositions() {
        return this.mqttTopicVehiclePositions;
    }

    public LiveData<Boolean> getSendTripUpdates() {
        return this.sendTripUpdates;
    }

    public LiveData<Boolean> getSendVehiclePositions() {
        return this.sendVehiclePositions;
    }

    public LiveData<String> getVehicleId() {
        return this.vehicleId;
    }
}