/* Script is ran every time on startup, so procedures must be replaced*/
CREATE OR REPLACE PROCEDURE deduplicate_weather_forecast_features()
LANGUAGE sql
AS $$
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

