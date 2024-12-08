package de.hka.realtimer.model;

import org.gtfs.reader.GtfsSimpleDao;
import org.gtfs.reader.model.Agency;
import org.gtfs.reader.model.Route;

import java.util.Optional;

public class RouteWithAgency extends Route {

    private final GtfsSimpleDao dataAccessObject;
    private Agency agency;

    public RouteWithAgency(Route route, GtfsSimpleDao dataAccessObject) {
        this.setId(route.getId());
        this.setAgencyId(route.getAgencyId());
        this.setShortName(route.getShortName());
        this.setLongName(route.getLongName());
        this.setDescription(route.getDescription());
        this.setType(route.getType());
        this.setUrl(route.getUrl());
        this.setColor(route.getColor());
        this.setTextColor(route.getTextColor());
        this.setSortOrder(route.getSortOrder());
        this.setContinuousPickup(route.getContinuousPickup());
        this.setGetContinuousDropOff(route.getGetContinuousDropOff());
        this.setNetworkId(route.getNetworkId());

        this.dataAccessObject = dataAccessObject;
    }

    public Agency getAgency() {
        if (this.agency == null) {
            Optional<Agency> agency = this.dataAccessObject.getAgencies().stream().filter(r -> r.getId().equals(this.getAgencyId())).findFirst();
            agency.ifPresent(value -> this.agency = value);
        }

        return this.agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }
}
