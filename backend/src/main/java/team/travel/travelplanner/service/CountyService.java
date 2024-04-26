package team.travel.travelplanner.service;

import com.google.common.collect.ListMultimap;
import team.travel.travelplanner.model.CountyModel;

import java.util.List;

public interface CountyService {
    ListMultimap<String, CountyModel> getCounties(List<String> fipsCodes);
}
