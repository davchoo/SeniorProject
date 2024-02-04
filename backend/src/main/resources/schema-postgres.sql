-- Script is ran every time on startup, so procedures & functions must be replaced
CREATE OR REPLACE PROCEDURE deduplicate_weather_forecast_features()
    LANGUAGE sql
AS
$$
DELETE
FROM weather_forecast_feature as wxff
WHERE wxff.id IN
      (SELECT id
       FROM (SELECT *,
                    row_number()
                    OVER (PARTITION BY wxff.forecast_day, wxff.file_date, wxff.weather_feature_type, wxff.geometry ORDER BY retrieval_timestamp) as row_number
             FROM weather_forecast_feature as wxff) s
       WHERE row_number >= 2)
$$;

CREATE OR REPLACE FUNCTION check_route_weather(IN route geometry, IN durations int[],
                                               IN start_time timestamp with time zone)
    RETURNS TABLE
            (
                i                    int,
                weather_feature_type varchar(255),
                forecast_day         int
            )
    LANGUAGE sql
AS
$$
WITH route as (SELECT durations.i,
                      start_time +
                      make_interval(secs := (sum(duration) over (order by durations.i) - duration)) as start_timestamp,
                      start_time +
                      make_interval(secs := (sum(duration) over (order by durations.i)))            as end_timestamp,
                      route_segements.g                                                             as segment
               from unnest(durations) with ordinality as durations(duration, i)
                        join (select path[1] as i, geom as g
                              from st_dumpsegments(route)) as route_segements
                             on durations.i = route_segements.i)
select route.i, wxff.weather_feature_type, wxff.forecast_day
from route
         inner join weather_forecast_feature as wxff
                    on route.start_timestamp < wxff.valid_end and route.end_timestamp > wxff.valid_start and
                       st_intersects(route.segment, wxff.geometry)
$$;