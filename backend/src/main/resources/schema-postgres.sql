-- Script is ran every time on startup, so procedures & functions must be replaced
CREATE OR REPLACE PROCEDURE deduplicate_weather_features()
    LANGUAGE sql
AS
$$
DELETE
FROM weather_feature AS wf
WHERE wf.id IN
      (SELECT id
       FROM (SELECT *,
                    row_number()
                    OVER (PARTITION BY wf.forecast_day, wf.file_date, wf.weather_feature_type, wf.geometry ORDER BY retrieval_timestamp) AS row_number
             FROM weather_feature AS wf) s
       WHERE row_number >= 2)
$$;
DROP FUNCTION IF EXISTS check_route_weather;
CREATE OR REPLACE FUNCTION check_route_weather(IN route geometry, IN durations int[],
                                               IN start_time timestamp with time zone)
    RETURNS TABLE
            (
                i                    int,
                weather_feature_type varchar(255),
                forecast_day         int,
                file_date            timestamp with time zone,
                start_timestamp      timestamp with time zone,
                end_timestamp        timestamp with time zone
            )
    LANGUAGE sql
AS
$$
WITH route AS (SELECT durations.i,
                      start_time +
                      make_interval(secs := (sum(duration) OVER (ORDER BY durations.i) - duration) / 1000.0) AS start_timestamp,
                      start_time +
                      make_interval(secs := (sum(duration) OVER (ORDER BY durations.i)) / 1000.0)            AS end_timestamp,
                      route_segements.g                                                             AS segment
               FROM unnest(durations) WITH ORDINALITY AS durations(duration, i)
                        JOIN (SELECT path[1] AS i, geom AS g
                              FROM st_dumpsegments(route)) AS route_segements
                             ON durations.i = route_segements.i),
     s(latest_valid_start, latest_valid_end, latest_file_date) AS (SELECT max(wf.valid_start),
                                                                          max(wf.valid_end),
                                                                          max(wf.file_date)
                                                                   FROM weather_feature AS wf
                                                                   WHERE forecast_day = 1)
SELECT route.i - 1,
       wf.weather_feature_type,
       wf.forecast_day,
       wf.file_date,
       route.start_timestamp,
       route.end_timestamp
FROM route
         JOIN s ON TRUE
         INNER JOIN weather_feature AS wf
                    ON
                        (route.start_timestamp, route.end_timestamp) OVERLAPS (wf.valid_start, wf.valid_end) AND -- Segment overlaps with valid period for issuance
                        (
                            forecast_day = greatest(floor(date_part('epoch', route.start_timestamp - s.latest_valid_end) / 86400) + 2, 1) OR -- Match with latest issuance for a time range
                            forecast_day = greatest(floor(date_part('epoch', route.end_timestamp - s.latest_valid_end) / 86400) + 2, 1)
                        ) AND
                        (
                            (forecast_day != 1 AND (s.latest_file_date - wf.file_date) < interval '1 hour') OR -- Segment is in the future, match with latest stored issuance
                            (forecast_day = 1 AND (
                                route.end_timestamp - route.start_timestamp > interval '12 hour' OR -- Duration is greater than 12 hours, match with morning issuance
                                date_part('hour', route.start_timestamp AT TIME ZONE 'EST') < 19 OR -- Duration starts before 7 pm, match with morning issuance
                                date_part('hour', route.end_timestamp AT TIME ZONE 'EST') > 7 OR    -- Duration ends after 7 am, match with morning issuance
                                date_part('hour', wf.file_date AT TIME ZONE 'EST') >= 12 OR       -- Always match with afternoon issuance
                                ((s.latest_file_date - wf.file_date) < interval '1 hour' AND date_part('hour', s.latest_valid_start AT TIME ZONE 'EST') = 7) -- Missing latest afternoon issuance, match with morning issuance
                            ))
                        ) AND
                        st_intersects(route.segment, wf.geometry) -- Segment intersects the weather feature
$$;