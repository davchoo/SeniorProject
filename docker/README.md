## Docker
### Commands
`docker compose build` - Rebuild containers with Dockerfiles \
`docker compose up` - Create and start all containers \
`docker compose stop` - Stop all containers \
`docker compose down` - Stop and delete all containers

## Containers
### postgres
A container running Postgres with the PostGIS extension installed. All data is stored in the `postgres` folder.

### nginx
A container running NGINX. It is accessible at `http://localhost:9000` and will serve any files in the `nginx/frontend`
folder. \
It is configured to override `Access-Control-Allow-Origin` and `Access-Control-Allow-Methods` headers from proxied servers. \
Requests to `http://localhost:9000/api/**` will be proxied to the backend at `http://localhost:8080`. \
Requests to `http://localhost:9000/geoserver/gmaps` will be proxied to `http://localhost:8085/geoserver/gwc/service/gmaps`. \
Requests to `http://localhost:9000/geoserver/capabilities` will be proxied to `http://localhost:8085/geoserver/ows?service=WMS&version=1.3.0&request=GetCapabilities`.

### geoserver
A container running GeoServer. The web panel is accessible at `http://localhost:8085/geoserver`. This container is
configured to show the qpf, temp, and wx datasets for CONUS.
#### Viewing the map using WMS and QGIS
Add a new WMS connection with the URL `http://localhost:8085/geoserver/ows?service=WMS&version=1.3.0&request=GetCapabilities`

#### Viewing the map using Google Maps
Use the following template tile URL
```
http://localhost:8085/geoserver/gwc/service/gmaps?
        layers={a layer EX: ndfd:conus.wx}
        &zoom={z}
        &x={x}
        &y={y}
        &format=image/png
        &time={valid time of the layer to display EX: 2024-04-03T17:00:00.000Z}
```