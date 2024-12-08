package de.hka.realtimer.data;

import org.gtfs.reader.GtfsReader;

import java.io.IOException;

public class GtfsRepository {

    private GtfsRelationalDao dataAccessObject;

    private static GtfsRepository singleInstance;

    public static GtfsRepository getInstance() {
        if (GtfsRepository.singleInstance == null) {
            GtfsRepository.singleInstance = new GtfsRepository();
        }

        return GtfsRepository.singleInstance;
    }

    private GtfsRepository() {
    }

    public void readInputFeed(String filename) throws IOException {
        this.dataAccessObject = new GtfsRelationalDao();

        GtfsReader gtfsReader = new GtfsReader();
        gtfsReader.setDataAccessObject(this.dataAccessObject);
        gtfsReader.read(filename);
    }

    public GtfsRelationalDao getDataAccessObject() {
        return this.dataAccessObject;
    }
}
