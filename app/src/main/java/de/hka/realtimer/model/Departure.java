package de.hka.realtimer.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Departure {

    private Date operationDay;
    private String routeId;
    private String routeName;
    private String mode;
    private String tripId;
    private String tripHeadsign;
    private String stopId;
    private String stopName;
    private String platformCode;
    private Date departureTime;
    private boolean realtimeAvailable;

    public Departure() {

    }

    public Date getOperationDay() {
        return this.operationDay;
    }

    public void setOperationDay(Date serviceDay) {
        this.operationDay = serviceDay;
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

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTripId() {
        return this.tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripHeadsign() {
        return this.tripHeadsign;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public String getStopId() {
        return this.stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return this.stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getPlatformCode() {
        return this.platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public Date getDepartureTime() {
        return this.departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public boolean isRealtimeAvailable() {
        return this.realtimeAvailable;
    }

    public void setRealtimeAvailable(boolean realtimeAvailable) {
        this.realtimeAvailable = realtimeAvailable;
    }

    public String getFormattedDepartureTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(this.getDepartureTime());
    }
}
