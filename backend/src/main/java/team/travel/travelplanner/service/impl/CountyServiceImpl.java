package team.travel.travelplanner.service.impl;

import org.springframework.stereotype.Service;
import team.travel.travelplanner.entity.County;
import team.travel.travelplanner.model.CountyModel;
import team.travel.travelplanner.repository.CountyRepository;
import team.travel.travelplanner.service.CountyService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CountyServiceImpl implements CountyService {
    private final CountyRepository countyRepository;

    public CountyServiceImpl(CountyRepository countyRepository) {
        this.countyRepository = countyRepository;
    }

    @Override
    public Map<String, CountyModel> getCounties(List<String> fipsCodes) {
        Map<String, CountyModel> models = new HashMap<>();
        for (County county : countyRepository.findAllByFipsIn(fipsCodes)) {
            CountyModel model = new CountyModel(county.getFips(), county.getCountyName(), county.getState(), county.getGeometry().toText());
            models.put(model.fips(), model);
        }
        return models;
    }
}
