package de.hka.realtimer.data;

import android.util.Log;

import org.gtfs.reader.GtfsSimpleDao;
import org.gtfs.reader.model.Calendar;
import org.gtfs.reader.model.CalendarDate;
import org.gtfs.reader.model.Stop;
import org.gtfs.reader.model.StopTime;
import org.gtfs.reader.model.Trip;
import org.gtfs.reader.model.enumeration.ExceptionType;
import org.gtfs.reader.model.enumeration.LocationType;
import org.gtfs.reader.model.enumeration.PickupDropOffType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hka.realtimer.model.DepartureWithStopAndTrip;
import de.hka.realtimer.model.TripWithRoute;

public class GtfsRelationalDao extends GtfsSimpleDao {

    private List<Stop> stationList;

    private Map<String, List<DepartureWithStopAndTrip>> stationDepartureList;

    public GtfsRelationalDao() {
        this.stationList = new ArrayList<>();
        this.stationDepartureList = new HashMap<>();
    }

    public List<Stop> getStations() {
        if (!this.stationList.isEmpty()) {
            return this.stationList;
        }

        for (Stop stop : this.getStops()) {
            if (stop.getLocationType() == LocationType.STATION) {
                this.stationList.add(stop);
            }
        }

        return this.stationList;
    }

    public List<DepartureWithStopAndTrip> getDeparturesForStation(String stationId, Date date) {
        if (this.stationDepartureList.containsKey(stationId)) {
            return this.stationDepartureList.get(stationId);
        }

        // find stops belonging to the station
        List<String> stopIds = new ArrayList<>();
        for (Stop stop : this.getStops()) {
            if (stop.getParentStation().equals(stationId) && stop.getLocationType() == LocationType.STOP) {
                stopIds.add(stop.getId());
            }
        }

        // find services operating at the reference date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        int dateInt = Integer.parseInt(sdf.format(date));

        sdf = new SimpleDateFormat("EEEE");
        String dateDay = sdf.format(date);

        List<String> serviceIds = new ArrayList<>();
        for (Calendar calendar : this.getCalendars()) {
            if (calendar.getStartDate() <= dateInt && calendar.getEndDate() >= dateInt) {
                if (dateDay.toLowerCase().equals("monday") && calendar.getMonday()) {
                    serviceIds.add(calendar.getServiceId());
                } else if (dateDay.toLowerCase().equals("tuesday") && calendar.getTuesday()) {
                    serviceIds.add(calendar.getServiceId());
                } else if (dateDay.toLowerCase().equals("wednesday") && calendar.getWednesday()) {
                    serviceIds.add(calendar.getServiceId());
                } else if (dateDay.toLowerCase().equals("thursday") && calendar.getThursday()) {
                    serviceIds.add(calendar.getServiceId());
                } else if (dateDay.toLowerCase().equals("friday") && calendar.getFriday()) {
                    serviceIds.add(calendar.getServiceId());
                } else if (dateDay.toLowerCase().equals("saturday") && calendar.getSaturday()) {
                    serviceIds.add(calendar.getServiceId());
                } else if (dateDay.toLowerCase().equals("sunday") && calendar.getSunday()) {
                    serviceIds.add(calendar.getServiceId());
                }
            }
        }

        for (CalendarDate calendarDate : this.getCalendarDates()) {
            if (calendarDate.getDate() == dateInt && calendarDate.getExceptionType() == ExceptionType.INCLUDED) {
                serviceIds.add(calendarDate.getServiceId());
            } else if (calendarDate.getDate() == dateInt && calendarDate.getExceptionType() == ExceptionType.EXCLUDED) {
                serviceIds.remove(calendarDate.getServiceId());
            }
        }

        // find trips running on the operation day
        Map<String, TripWithRoute> tripsWithRoutes = new HashMap<>();
        for (Trip trip : this.getTrips()) {
            if (serviceIds.contains(trip.getServiceId())) {
                TripWithRoute tripWithRoute = new TripWithRoute(trip, this);
                tripsWithRoutes.put(trip.getTripId(), tripWithRoute);
            }
        }

        // find departures at one stop of the desired stops
        List<DepartureWithStopAndTrip> stationDepartureList = new ArrayList<>();
        for (StopTime stopTime : this.getStopTimes()) {
            if (stopIds.contains(stopTime.getStopId()) && tripsWithRoutes.containsKey(stopTime.getTripId()) && stopTime.getPickupType() != PickupDropOffType.NOT_AVAILABLE) {
                DepartureWithStopAndTrip departure = new DepartureWithStopAndTrip(stopTime, tripsWithRoutes.get(stopTime.getTripId()));
                stationDepartureList.add(departure);
            }
        }

        stationDepartureList.sort(Comparator.comparing(a -> a.getStopTime().getDepartureTime()));

        return stationDepartureList;
    }

}
