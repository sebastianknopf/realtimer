package de.hka.realtimer.model;

import org.gtfs.reader.GtfsSimpleDao;
import org.gtfs.reader.model.Route;
import org.gtfs.reader.model.Stop;
import org.gtfs.reader.model.StopTime;

import java.util.Optional;

public class StopTimeWithStop extends StopTime {

    private final GtfsSimpleDao dataAccessObject;
    private Stop stop;

    public StopTimeWithStop(StopTime stopTime, GtfsSimpleDao dataAccessObject) {
        this.setTripId(stopTime.getTripId());
        this.setArrivalTime(stopTime.getArrivalTime());
        this.setDepartureTime(stopTime.getDepartureTime());
        this.setStopId(stopTime.getStopId());
        this.setLocationGroupId(stopTime.getLocationGroupId());
        this.setLocationId(stopTime.getLocationId());
        this.setStopSequence(stopTime.getStopSequence());
        this.setStopHeadsign(stopTime.getStopHeadsign());
        this.setStartPickupDropOffWindow(stopTime.getStartPickupDropOffWindow());
        this.setEndPickupDropOffWindow(stopTime.getEndPickupDropOffWindow());
        this.setPickupType(stopTime.getPickupType());
        this.setDropOffType(stopTime.getDropOffType());
        this.setContinuousPickup(stopTime.getContinuousPickup());
        this.setContinuousDropOff(stopTime.getContinuousDropOff());
        this.setShapeDistTraveled(stopTime.getShapeDistTraveled());
        this.setTimepoint(stopTime.getTimepoint());
        this.setPickupBookingRuleId(stopTime.getPickupBookingRuleId());
        this.setDropOffBookingRuleId(stopTime.getDropOffBookingRuleId());

        this.dataAccessObject = dataAccessObject;
    }

    public Stop getStop() {
        if (this.stop == null) {
            Optional<Stop> stop = this.dataAccessObject.getStops().stream().filter(s -> s.getId().equals(this.getStopId())).findFirst();
            stop.ifPresent(value -> this.stop = stop.get());
        }

        return this.stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }
}
