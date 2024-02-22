package team.travel.travelplanner.service;

import team.travel.travelplanner.model.CountyModel;

import java.util.List;
import java.util.Map;

public interface CountyService {
    Map<String, CountyModel> getCounties(List<String> fipsCodes);
}
