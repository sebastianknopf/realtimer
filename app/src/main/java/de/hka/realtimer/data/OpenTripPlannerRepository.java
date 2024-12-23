package de.hka.realtimer.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apollographql.java.client.ApolloClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hka.realtimer.common.Config;
import de.hka.realtimer.model.Departure;
import de.hka.realtimer.model.Station;
import de.hka.realtimer.model.StopTime;
import de.hka.realtimer.model.TripDetails;
import de.hka.realtimer.otp.DeparturesListQuery;
import de.hka.realtimer.otp.StationsListQuery;
import de.hka.realtimer.otp.TripDetailsQuery;

public class OpenTripPlannerRepository {

    private Context context;
    private ApolloClient apolloClient;

    private MutableLiveData<List<Station>> stationsList;
    private MutableLiveData<List<Departure>> departuresList;
    private MutableLiveData<TripDetails> tripDetails;

    private static OpenTripPlannerRepository singleInstance;

    public static OpenTripPlannerRepository getInstance(Context context) {
        if (OpenTripPlannerRepository.singleInstance == null) {
            OpenTripPlannerRepository.singleInstance = new OpenTripPlannerRepository(context);
        }

        return OpenTripPlannerRepository.singleInstance;
    }

    private OpenTripPlannerRepository(Context context) {
        this.context = context;

        SharedPreferences sharedPreferences = this.context.getSharedPreferences("de.hka.realtimer", Context.MODE_PRIVATE);
        String graphqlApiUrl = sharedPreferences.getString(Config.OTP_GRAPHQL_API_URL, "https://otp.svprod01.app/graphiql?flavor=gtfs");

        this.apolloClient = new ApolloClient.Builder()
                .serverUrl(graphqlApiUrl)
                .build();

        this.stationsList = new MutableLiveData<>();
        this.departuresList = new MutableLiveData<>();
        this.tripDetails = new MutableLiveData<>();
    }

    public void loadStations() {
        this.apolloClient.query(new StationsListQuery()).enqueue(response -> {
            List<Station> results = new ArrayList<>();

            if (response.data != null && response.data.stations != null) {
                for (StationsListQuery.Station obj : response.data.stations) {
                    Station station = new Station();
                    station.setId(obj.gtfsId);
                    station.setName(obj.name);
                    station.setLatitude(obj.lat);
                    station.setLongitude(obj.lon);

                    results.add(station);
                }
            }

            this.stationsList.postValue(results);
        });
    }

    public void loadDepartures(String stationId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTime().getTime() / 1000L;

        this.apolloClient.query(new DeparturesListQuery(stationId, startTime)).enqueue(response -> {
            List<Departure> results = new ArrayList<>();

            if (response.data != null && response.data.station != null && response.data.station.stops != null) {
                for (DeparturesListQuery.Stop obj : response.data.station.stops) {
                    for (DeparturesListQuery.StoptimesWithoutPattern stopTime : obj.stoptimesWithoutPatterns) {
                        Departure departure = new Departure();
                        departure.setServiceDay((int) stopTime.serviceDay);
                        departure.setRouteId(stopTime.trip.route.gtfsId);
                        departure.setRouteName(stopTime.trip.route.shortName);
                        departure.setMode(stopTime.trip.route.mode.toString());
                        departure.setTripId(stopTime.trip.gtfsId);
                        departure.setTripHeadsign(stopTime.trip.tripHeadsign);
                        departure.setStopId(obj.gtfsId);
                        departure.setStopName(obj.name);
                        departure.setPlatformCode(obj.platformCode);

                        Date departureTime = new Date();
                        departureTime.setTime(((int) stopTime.serviceDay + stopTime.scheduledDeparture) * 1000L);

                        departure.setDepartureTime(departureTime);
                        departure.setRealtimeAvailable(stopTime.realtime);

                        results.add(departure);
                    }
                }

                Collections.sort(results, Comparator.comparing(Departure::getDepartureTime));

                this.departuresList.postValue(results);
            }

            this.departuresList.postValue(results);
        });
    }

    public void loadTripDetails(String tripId, long serviceDay) {
        this.apolloClient.query(new TripDetailsQuery(tripId)).enqueue(response -> {
            if (response.data != null && response.data.trip != null) {
                TripDetails tripDetails = new TripDetails();
                tripDetails.setRouteId(response.data.trip.route.gtfsId);
                tripDetails.setRouteName(response.data.trip.route.shortName);
                tripDetails.setTripId(response.data.trip.gtfsId);
                tripDetails.setHeadsign(response.data.trip.tripHeadsign);

                List<StopTime> stopTimes = new ArrayList<>();
                for (TripDetailsQuery.Stoptime obj : response.data.trip.stoptimes) {
                    StopTime stopTime = new StopTime();
                    stopTime.setStopId(obj.stop.gtfsId);
                    stopTime.setStopName(obj.stop.name);
                    stopTime.setStopSequence(obj.stopPosition);

                    Date arrivalTime = new Date();
                    arrivalTime.setTime((serviceDay + obj.scheduledArrival) * 1000L);

                    stopTime.setArrivalTime(arrivalTime);

                    Date departureTime = new Date();
                    departureTime.setTime((serviceDay + obj.scheduledDeparture) * 1000L);

                    stopTime.setDepartureTime(departureTime);

                    stopTimes.add(stopTime);
                }

                tripDetails.setStopTimes(stopTimes);

                this.tripDetails.postValue(tripDetails);
            } else {
                this.tripDetails.postValue(null);
            }
        });
    }

    public LiveData<List<Station>> getStationsList() {
        return this.stationsList;
    }

    public LiveData<List<Departure>> getDepartureList() {
        return this.departuresList;
    }

    public LiveData<TripDetails> getTripDetails() {
        return this.tripDetails;
    }
}
