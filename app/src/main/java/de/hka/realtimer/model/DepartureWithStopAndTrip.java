package de.hka.realtimer.model;

import org.gtfs.reader.model.StopTime;

public class DepartureWithStopAndTrip {

   private StopTime stopTime;

   private TripWithRoute trip;

   public DepartureWithStopAndTrip(StopTime stopTime, TripWithRoute trip) {
      this.stopTime = stopTime;
      this.trip = trip;
   }

   public StopTime getStopTime() {
      return this.stopTime;
   }

   public void setStopTime(StopTime stopTime) {
      this.stopTime = stopTime;
   }

   public TripWithRoute getTrip() {
      return this.trip;
   }

   public void setTrip(TripWithRoute trip) {
      this.trip = trip;
   }
}
