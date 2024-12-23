package de.hka.realtimer.model;

import java.util.List;

public class TripDetails {

    private String routeId;
    private String routeName;
    private String tripId;
    private String headsign;
    private List<StopTime> stopTimes;

    public TripDetails() {
    }

    public String getRouteId() {
        return this.routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return this.routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTripId() {
        return this.tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getHeadsign() {
        return this.headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public List<StopTime> getStopTimes() {
        return this.stopTimes;
    }

    public void setStopTimes(List<StopTime> stopTimes) {
        this.stopTimes = stopTimes;
    }
}
