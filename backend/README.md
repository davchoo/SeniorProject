# **TripEase - Backend**
## Compilation
Linux: `./gradlew bootJar` Windows: `gradlew.bat bootJar` \
The resulting jar is located at `build/libs/travel-planner-<version>.jar`

## Required Configuration Files
**Backend** <br>
The backend requires a single application-prod.yml configuration file. An example configuration file can be found at `src/main/resources/application.yml`.
This file must provide the following configuration values:
* spring.datasource.url
  * URL to the PostgreSQL Database with the name of the schema
* spring.datasource.username and spring.datasource.password
  * Username and password for the database
* server.port
* server.allowed-origin-patterns
  * A list of allowed origins
* googlemaps.google-maps-api-key
* travel-planner.weather.raster-data.netcdf-storage-path
  * Path to the ndfd-nc folder in `docker/geoserver`
### Example configuration to link the backend with GeoServer
```yml
travel-planner:
    raster-data:
      geo-server:
        capabilities-endpoint: http://localhost:8085/geoserver/ows?service=WMS&version=1.3.0&request=GetCapabilities
        mass-truncate-endpoint: http://localhost:8085/geoserver/gwc/rest/masstruncate
        reset-endpoint: http://localhost:8085/geoserver/rest/reset
        truncated-layers:
          - ndfd:conus.qpf
          - ndfd:conus.temp
          - ndfd:conus.wx
        username: admin
        password: geoserver
```

## Running
After compiling the backend, the following command can be used to start it up.
`java -jar ./travel-planner-<version>.jar --spring.config.location=<path to config>`

**Packages utilized by the Backend**
* Spring Boot
* Hibernate Spatial
* GeoTools
* NetCDF-Java
* AWS SDK

**Docker** <br>
The Docker containers for TripEase are configured through the `docker-compose.yml` file. This following configuration values must be set in this file:
* Username and Password of the Postgres Database
* Name of the initialized schema

