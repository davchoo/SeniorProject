## Docker
### Commands
`docker compose build` - Rebuild containers with Dockerfiles \
`docker compose up` - Create and start all containers \
`docker compose stop` - Stop all containers \
`docker compose down` - Stop and delete all containers

## Containers
### postgres
A container running Postgres with the PostGIS extension installed. All data is stored in the `postgres` folder.

### mapserver
A container running an Apache HTTP Server with the MapServer CGI program. Map layers are configured through the
`mapserver/weather.map` and additional maps can be added to `mapserver/mapserver.conf`. This server is accessible
through `http://localhost:4242`
#### Viewing the map using WMS and QGIS
Add a new WMS connection with the URL `http://localhost:4242/cgi-bin/mapserv.fcgi?map=weather`

#### Viewing the map using Google Maps
Use the following template tile URL
```
http://localhost:4242/cgi-bin/mapserv.fcgi?
        MAP=weather
        &MODE=tile
        &TILEMODE=gmap
        &TILE={tile x}+{tile y}+{zoom}
        &LAYERS={a layer from mapserver/weather.map EX: conus.temperature}
        &vtit={valid time of the layer to display EX: 2024-03-04T01:00}
```

Available NDFD layers and their `vtit` values can be found [here](https://digital.mdl.nws.noaa.gov/ndfd/wms?REQUEST=GetCapabilities).