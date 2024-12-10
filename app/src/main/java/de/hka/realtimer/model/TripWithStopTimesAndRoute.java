package de.hka.realtimer.model;

import org.gtfs.reader.GtfsSimpleDao;
import org.gtfs.reader.model.Stop;
import org.gtfs.reader.model.StopTime;
import org.gtfs.reader.model.Trip;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TripWithStopTimesAndRoute extends TripWithRoute {

   private final GtfsSimpleDao dataAccessObject;
   private List<StopTimeWithStop> stopTimes;

   public TripWithStopTimesAndRoute(Trip trip, GtfsSimpleDao dataAccessObject) {
      super(trip, dataAccessObject);

      this.dataAccessObject = dataAccessObject;
   }

   public List<StopTimeWithStop> getStopTimes() {
      if (this.stopTimes == null) {
         this.stopTimes = new ArrayList<>();

         List<StopTime> stopTimes = this.dataAccessObject.getStopTimes().stream().filter(s -> s.getTripId().equals(this.getTripId())).collect(Collectors.toList());
         stopTimes.sort(Comparator.comparing(StopTime::getStopSequence));

         for (StopTime stopTime : stopTimes) {
            this.stopTimes.add(new StopTimeWithStop(stopTime, this.dataAccessObject));
         }
      }

      return this.stopTimes;
   }

   public void setStopTimes(List<StopTimeWithStop> stopTimes) {
      this.stopTimes = stopTimes;
   }
}
