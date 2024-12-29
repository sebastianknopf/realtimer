package de.hka.realtimer.model;

public class StationWithDistance extends Station {

    private double distance;

    public StationWithDistance(Station station) {
        this.setId(station.getId());
        this.setName(station.getName());
        this.setLatitude(station.getLatitude());
        this.setLongitude(station.getLongitude());
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getFormattedDistanceString() {
        if (this.distance < 1000) {
            return String.format("%dm", (int) this.distance);
        } else {
            return String.format("%.2fkm", (this.distance / 1000));
        }
    }
}
