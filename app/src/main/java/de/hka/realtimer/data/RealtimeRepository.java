package de.hka.realtimer.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import de.hka.realtimer.common.Config;

public class RealtimeRepository {

    private Context context;
    private Mqtt5BlockingClient mqttClient;

    private static RealtimeRepository singleInstance;

    public static RealtimeRepository getInstance(Context context) {
        if (RealtimeRepository.singleInstance == null) {
            RealtimeRepository.singleInstance = new RealtimeRepository(context);
        }

        return RealtimeRepository.singleInstance;
    }

    private RealtimeRepository(Context context) {
        this.context = context;

        SharedPreferences sharedPreferences = this.context.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);
        String mqttHost = sharedPreferences.getString(Config.MQTT_HOST, "test.mosquitto.org");
        String mqttPort = sharedPreferences.getString(Config.MQTT_PORT, "1883");

        this.mqttClient = MqttClient.builder()
                .serverHost(mqttHost)
                .serverPort(Integer.parseInt(mqttPort))
                .useMqttVersion5()
                .buildBlocking();
    }

    public void runConnectionTest() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);
        String mqttUsername = sharedPreferences.getString(Config.MQTT_USERNAME, "username");
        String mqttPassword = sharedPreferences.getString(Config.MQTT_PASSWORD, "password");

        this.mqttClient.connectWith()
                .simpleAuth()
                .username(mqttUsername)
                .password(mqttPassword.getBytes())
                .applySimpleAuth()
                .send();

        if (sharedPreferences.getBoolean(Config.SEND_TRIP_UPDATES, false)) {
            String topicTripUpdates = sharedPreferences.getString(Config.MQTT_TOPIC_TRIP_UPDATES, "");

            this.mqttClient.publishWith()
                    .topic(topicTripUpdates)
                    .payload(new byte[]{})
                    .retain(true)
                    .send();
        }

        if (sharedPreferences.getBoolean(Config.SEND_TRIP_UPDATES, false)) {
            String topicVehiclePositions = sharedPreferences.getString(Config.MQTT_TOPIC_VEHICLE_POSITIONS, "");

            this.mqttClient.publishWith()
                    .topic(topicVehiclePositions)
                    .payload(new byte[]{})
                    .retain(true)
                    .send();
        }

        this.mqttClient.disconnect();
    }
}
