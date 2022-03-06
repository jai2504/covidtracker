package io.jai.covidtracker.services;

import io.jai.covidtracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataServiceImpl implements CoronaVirusDataService{
    private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats=new ArrayList<>();
    private List<String> graph=new ArrayList<>();
    private List<Integer> pastOneWeek=new ArrayList<>(7);
    @Override
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats=new ArrayList<>();
        HttpClient httpClient=HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        HttpResponse<String> response=httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader stringReader=new StringReader(response.body());
        Iterable<CSVRecord> records= CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
        List<String> headers=records.iterator().next().getParser().getHeaderNames();

        int one=0,two=0,three=0,four=0,five=0,six=0,seven=0;
        for (CSVRecord record : records) {
            LocationStats locationStats=new LocationStats();
            locationStats.setState(StringUtils.isEmptyOrWhitespace(record.get("Province/State"))?"----":record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            int latestCases=Integer.parseInt(record.get(record.size()-1));
            int prevDayCases=Integer.parseInt(record.get(record.size()-2));

            seven+=Integer.parseInt(record.get(record.size()-1));
            six+=Integer.parseInt(record.get(record.size()-2));
            five+=Integer.parseInt(record.get(record.size()-3));
            four+=Integer.parseInt(record.get(record.size()-4));
            three+=Integer.parseInt(record.get(record.size()-5));
            two+=Integer.parseInt(record.get(record.size()-6));
            one+=Integer.parseInt(record.get(record.size()-7));
            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPrevDay(latestCases-prevDayCases);
            newStats.add(locationStats);
        }
        for(int i=headers.size()-7;i<headers.size();i++){
            graph.add(headers.get(i));
        }
        this.pastOneWeek.add(one);
        this.pastOneWeek.add(two);
        this.pastOneWeek.add(three);
        this.pastOneWeek.add(four);
        this.pastOneWeek.add(five);
        this.pastOneWeek.add(six);
        this.pastOneWeek.add(seven);
        this.allStats=newStats;
    }
    @Override
    public List<LocationStats> getAllStats(){
        return this.allStats;
    }

    @Override
    public List<String> getGraphHeaders() {
        return this.graph;
    }

    @Override
    public List<Integer> getPastOneWeek() {
        return this.pastOneWeek;
    }


}
