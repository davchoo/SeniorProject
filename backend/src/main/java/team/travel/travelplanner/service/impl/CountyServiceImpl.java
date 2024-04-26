package team.travel.travelplanner.service.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.entity.County;
import team.travel.travelplanner.model.CountyModel;
import team.travel.travelplanner.repository.CountyRepository;
import team.travel.travelplanner.service.CountyService;

import java.util.List;

@Service
public class CountyServiceImpl implements CountyService {
    private final CountyRepository countyRepository;

    public CountyServiceImpl(CountyRepository countyRepository) {
        this.countyRepository = countyRepository;
    }

    @Override
    public ListMultimap<String, CountyModel> getCounties(List<String> fipsCodes) {
        ListMultimap<String, CountyModel> models = MultimapBuilder
                .hashKeys(fipsCodes.size())
                .arrayListValues()
                .build();
        for (County county : countyRepository.findAllByFipsIn(fipsCodes)) {
            CountyModel model = new CountyModel(county.getFips(), county.getCountyName(), county.getState(), county.getGeometry());
            models.put(model.fips(), model);
        }
        return models;
    }
}
