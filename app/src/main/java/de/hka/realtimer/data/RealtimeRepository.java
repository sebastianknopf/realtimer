package de.hka.realtimer.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.transit.realtime.GtfsRealtime;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import org.gtfs.reader.model.StopTime;

import java.util.Date;

import de.hka.realtimer.common.Config;
import de.hka.realtimer.model.TripWithRoute;

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

        /*if (sharedPreferences.getBoolean(Config.SEND_TRIP_UPDATES, false)) {
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
        }*/

        this.mqttClient.disconnect();
    }

    public void connectBroker() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);
        String mqttUsername = sharedPreferences.getString(Config.MQTT_USERNAME, "username");
        String mqttPassword = sharedPreferences.getString(Config.MQTT_PASSWORD, "password");

        this.mqttClient.connectWith()
                .simpleAuth()
                .username(mqttUsername)
                .password(mqttPassword.getBytes())
                .applySimpleAuth()
                .send();
    }

    public void sendTripRealtimeData(TripWithRoute trip, StopTime stopTime, int departureDelayInMinutes) {
        GtfsRealtime.TripDescriptor tripDescriptor = this.createTripDescriptor(trip);

        GtfsRealtime.TripUpdate.StopTimeEvent stopTimeEvent = GtfsRealtime.TripUpdate.StopTimeEvent.newBuilder()
                .setDelay(departureDelayInMinutes * 60)
                .build();

        GtfsRealtime.TripUpdate.StopTimeUpdate stopTimeUpdate = GtfsRealtime.TripUpdate.StopTimeUpdate.newBuilder()
                .setStopId(stopTime.getStopId())
                .setDeparture(stopTimeEvent)
                .build();

        GtfsRealtime.TripUpdate tripUpdate = GtfsRealtime.TripUpdate.newBuilder()
                .setTrip(tripDescriptor)
                .addStopTimeUpdate(stopTimeUpdate)
                .build();

        GtfsRealtime.FeedEntity feedEntity = GtfsRealtime.FeedEntity.newBuilder()
                .setId(trip.getTripId())
                .setTripUpdate(tripUpdate)
                .build();

        GtfsRealtime.FeedHeader feedHeader = GtfsRealtime.FeedHeader.newBuilder()
                .setGtfsRealtimeVersion("2.0")
                .setIncrementality(GtfsRealtime.FeedHeader.Incrementality.DIFFERENTIAL)
                .setTimestamp(new Date().getTime() / 1000L)
                .build();

        GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.newBuilder()
                .setHeader(feedHeader)
                .addEntity(feedEntity)
                .build();

        this.sendTripUpdate(feedMessage, trip.getRouteId(), trip.getTripId());
    }

    public void deleteTripRealtimeData(TripWithRoute trip) {
        GtfsRealtime.TripDescriptor tripDescriptor = this.createTripDescriptor(trip);

        GtfsRealtime.TripUpdate tripUpdate = GtfsRealtime.TripUpdate.newBuilder()
                .setTrip(tripDescriptor)
                .build();

        GtfsRealtime.FeedEntity feedEntity = GtfsRealtime.FeedEntity.newBuilder()
                .setId(trip.getTripId())
                .setTripUpdate(tripUpdate)
                .setIsDeleted(true)
                .build();

        GtfsRealtime.FeedHeader feedHeader = GtfsRealtime.FeedHeader.newBuilder()
                .setGtfsRealtimeVersion("2.0")
                .setIncrementality(GtfsRealtime.FeedHeader.Incrementality.DIFFERENTIAL)
                .setTimestamp(new Date().getTime() / 1000L)
                .build();

        GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.newBuilder()
                .setHeader(feedHeader)
                .addEntity(feedEntity)
                .build();

        this.sendTripUpdate(feedMessage, trip.getRouteId(), trip.getTripId());
    }

    private void sendTripUpdate(GtfsRealtime.FeedMessage feedMessage, String routeId, String tripId) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(Config.SEND_TRIP_UPDATES, false)) {
            String topicTripUpdates = sharedPreferences.getString(Config.MQTT_TOPIC_TRIP_UPDATES, "");
            topicTripUpdates = String.format("%s/%s/%s", topicTripUpdates, routeId, tripId);

            this.mqttClient.publishWith()
                    .topic(topicTripUpdates)
                    .payload(feedMessage.toByteArray())
                    .retain(true)
                    .send();
        }
    }

    private void sendVehiclePosition(GtfsRealtime.FeedMessage feedMessage) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(Config.SEND_VEHICLE_POSITIONS, false)) {
            String vehicleId = sharedPreferences.getString(Config.VEHICLE_ID, "");

            String topicVehiclePositions = sharedPreferences.getString(Config.MQTT_TOPIC_VEHICLE_POSITIONS, "");
            topicVehiclePositions = String.format("%s/%s", topicVehiclePositions, vehicleId);

            this.mqttClient.publishWith()
                    .topic(topicVehiclePositions)
                    .payload(feedMessage.toByteArray())
                    .retain(true)
                    .send();
        }
    }

    private GtfsRealtime.TripDescriptor createTripDescriptor(TripWithRoute trip) {
        GtfsRealtime.TripDescriptor.Builder tripDescriptorBuilder = GtfsRealtime.TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId(trip.getTripId());
        tripDescriptorBuilder.setRouteId(trip.getRouteId());
        tripDescriptorBuilder.setScheduleRelationship(GtfsRealtime.TripDescriptor.ScheduleRelationship.SCHEDULED);

        return tripDescriptorBuilder.build();
    }

    public void disconnectBroker() {
        if (this.mqttClient != null) {
            this.mqttClient.disconnect();
        }
    }
}
