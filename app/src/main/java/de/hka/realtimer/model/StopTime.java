package de.hka.realtimer.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StopTime {

    private String stopId;
    private String stopName;
    private int stopSequence;
    private Date arrivalTime;
    private Date departureTime;

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

    public int getStopSequence() {
        return this.stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public Date getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFormattedArrivalTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(this.getArrivalTime());
    }

    public Date getDepartureTime() {
        return this.departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public String getFormattedDepartureTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(this.getDepartureTime());
    }
}
