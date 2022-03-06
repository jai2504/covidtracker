package io.jai.covidtracker.services;

import io.jai.covidtracker.models.LocationStats;

import java.io.IOException;
import java.util.List;

public interface CoronaVirusDataService {
    void fetchVirusData() throws IOException, InterruptedException;
    List<LocationStats> getAllStats();
    List<String> getGraphHeaders();
    List<Integer> getPastOneWeek();

}
