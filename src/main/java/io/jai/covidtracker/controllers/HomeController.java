package io.jai.covidtracker.controllers;

import io.jai.covidtracker.models.LocationStats;
import io.jai.covidtracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats=coronaVirusDataService.getAllStats();
        int totalReportedCases=allStats.stream().mapToInt(stat-> stat.getLatestTotalCases()).sum();
        model.addAttribute("locationStats",coronaVirusDataService.getAllStats());
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("lastOneWeek",coronaVirusDataService.getPastOneWeek());
        model.addAttribute("graph",coronaVirusDataService.getGraphHeaders());
        return "home";
    }
}
