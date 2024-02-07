-- Script is ran every time on startup, so procedures & functions must be replaced
CREATE OR REPLACE PROCEDURE deduplicate_weather_forecast_features()
    LANGUAGE sql
AS
$$
DELETE
FROM weather_forecast_feature AS wxff
WHERE wxff.id IN
      (SELECT id
       FROM (SELECT *,
                    row_number()
                    OVER (PARTITION BY wxff.forecast_day, wxff.file_date, wxff.weather_feature_type, wxff.geometry ORDER BY retrieval_timestamp) AS row_number
             FROM weather_forecast_feature AS wxff) s
       WHERE row_number >= 2)
$$;
DROP FUNCTION IF EXISTS check_route_weather;
CREATE OR REPLACE FUNCTION check_route_weather(IN route geometry, IN durations int[],
                                               IN start_time timestamp with time zone)
    RETURNS TABLE
            (
                i                           int,
                weather_feature_type        varchar(255),
                forecast_day                int
            )
    LANGUAGE sql
AS
$$
WITH route AS (SELECT durations.i,
                      start_time +
                      make_interval(secs := (sum(duration) OVER (ORDER BY durations.i) - duration)) AS start_timestamp,
                      start_time +
                      make_interval(secs := (sum(duration) OVER (ORDER BY durations.i)))            AS end_timestamp,
                      route_segements.g                                                             AS segment
               FROM unnest(durations) WITH ORDINALITY AS durations(duration, i)
                        JOIN (SELECT path[1] AS i, geom AS g
                              FROM st_dumpsegments(route)) AS route_segements
                             ON durations.i = route_segements.i),
     s(latest_valid_end) AS (SELECT max(wxff.valid_end) FROM weather_forecast_feature AS wxff WHERE forecast_day = 1)
SELECT route.i - 1,
       wxff.weather_feature_type,
       wxff.forecast_day
FROM route
         JOIN s ON TRUE
         INNER JOIN weather_forecast_feature AS wxff
                    ON
                        (
                            forecast_day = greatest(floor(date_part('epoch', route.start_timestamp - s.latest_valid_end) / 86400) + 2, 1) OR
                            forecast_day = greatest(floor(date_part('epoch', route.end_timestamp - s.latest_valid_end) / 86400) + 2, 1)
                        ) AND
                        (route.start_timestamp, route.end_timestamp) OVERLAPS (wxff.valid_start, wxff.valid_end) AND
                        st_intersects(route.segment, wxff.geometry)
$$;