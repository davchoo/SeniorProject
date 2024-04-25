# **TripEase - Backend**
## Installation
**Required Packages for Backend**
* Spring Boot
* Hibernate Spatial
* GeoTools
* NetCDF-Java
* AWS SDK

## Required Configuration Files
**Backend** <br>
The backend requires a single application-prod.yml configuration file. This file must provide the following configuration values:
* URL to the PostgreSQL Database with the name of the schema
* Username and password for the database
* A list of allowed origins

**Docker** <br>
The Docker containers for TripEase are configured through the docker-compose.yml file. This following configuration values must be set in this file:
* Username and Password of the Postgres Database
* Name of the initialized schema

