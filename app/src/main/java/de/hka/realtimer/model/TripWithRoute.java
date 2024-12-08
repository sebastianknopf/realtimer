package de.hka.realtimer.model;

import org.gtfs.reader.GtfsSimpleDao;
import org.gtfs.reader.model.Route;
import org.gtfs.reader.model.Trip;

import java.util.Optional;

public class TripWithRoute extends Trip {

    private final GtfsSimpleDao dataAccessObject;
    private RouteWithAgency route;

    public TripWithRoute(Trip trip, GtfsSimpleDao dataAccessObject) {
        this.setRouteId(trip.getRouteId());
        this.setServiceId(trip.getServiceId());
        this.setTripId(trip.getTripId());
        this.setHeadsign(trip.getHeadsign());
        this.setShortName(trip.getShortName());
        this.setDirectionId(trip.getDirectionId());
        this.setBlockId(trip.getBlockId());
        this.setShapeId(trip.getShapeId());
        this.setWheelchairAccessible(trip.getWheelchairAccessible());
        this.setBikesAllowed(trip.getBikesAllowed());

        this.dataAccessObject = dataAccessObject;
    }

    public RouteWithAgency getRoute() {
        if (this.route == null) {
            Optional<Route> route = this.dataAccessObject.getRoutes().stream().filter(r -> r.getId().equals(this.getRouteId())).findFirst();
            route.ifPresent(value -> this.route = new RouteWithAgency(value, this.dataAccessObject));
        }

        return this.route;
    }

    public void setRoute(RouteWithAgency route) {
        this.route = route;
    }
}
