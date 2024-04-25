# **TripEase**

## Contributors
Emma Zimmerman (https://github.com/EmmaZim, https://www.linkedin.com/in/emma-zimmerman-4aa317267/),<br>
David Choo (https://github.com/davchoo, https://www.linkedin.com/in/choo-david),<br>
Lukas DeLoach (https://github.com/lukasdeloach, https://www.linkedin.com/in/lukas-deloach/),<br>
Kaan Kayis (https://github.com/kaankayis22, https://www.linkedin.com/in/kaan-kayis-b6b708266/),<br>
Yekaterina Saburova (https://github.com/KatSaburova, https://www.linkedin.com/in/yekaterina-saburova-0aa257242/)

## Project Description 
TripEase is a web application tailored for individuals at all levels of travel experience to streamline the process of planning any trip. The sign-up process will guide users through the journey planning process, simplifying tasks from selecting departure and destination points to finalizing itineraries within an intuitive web interface. For budget-conscious travelers, TripEase will locate convenient and cost-effective gas stops, along with their information, along their route. The application will also inform users about unpredictable weather conditions through real-time weather updates, a weather overlay, and a weather radar for up to seven days in advance.
   
TripEase's integration with various APIs guarantees a seamless flow of current information, encompassing weather updates and gas prices. This comprehensive integration will enhance the efficiency of the web application, ensuring users have accurate and up-to-date insights for a more streamlined trip-planning experience.

## Installation
**Steps to deploy TripEase to instance:**
1. Install Docker Engine and Docker Compose. <br>
2. On a local machine, compile the backend and frontend from the main branch of the repository. <br>
3. Transfer the compiled artifacts and the docker folder from the main branch to the instance. <br>
4. Transfer configuration files for the backend and Docker to the instance. <br>
5. Run the startup script to bring up the necessary Docker containers.<br>

## Technical Architecture
![image](https://github.com/davchoo/SeniorProject/assets/99043729/d1e60b06-44e9-4606-9637-345be5cd87e4)

## Assumptions and Limitations
**Required System Specifications** <br>
TripEase is required to work on the Google Chrome and Firefox browsers. Additionally, the application must be deployable to an Amazon Web Services EC2 instance.

**Design Exclusions** <br>
TripEase is not designed to work on Edge, Internet Explorer, or other browsers except those specified above.
TripEase is not designed to be a native mobile application. 

## Supporting References
[1] "Fuel Economy API." U.S. Department of Energy. https://www.fueleconomy.gov/feg/ws/index.shtml (Accessed March 4, 2024)<br>
[2] "Google Maps Platform: Places API Overview." Google. https://developers.google.com/maps/documentation/places/web-service/overview (Accessed March 4, 2024)<br>
[3] "Google Maps Platform: Decoding Polylines." Google. https://developers.google.com/maps/documentation/javascript/reference/geometry#encoding.decodePath (Accessed March 4, 2024)<br>
[4] "Google Maps Platform: Directions API." Google. https://developers.google.com/maps/documentation/directions/overview (Accessed March 4, 2024)<br>
[5] "Google Maps Platform: Places API." Google. https://developers.google.com/maps/documentation/places/web-service/overview (Accessed March 4, 2024)<br>
[6] "Pexels License."  Pexels. https://www.pexels.com/license/ (Accessed March 2, 2024)<br>
[7] "Places API: FuelOptions Object." Google. from https://developers.google.com/maps/documentation/places/web-service/reference/rest/v1/places#fueloptions (Accessed March 4, 2024)<br>
[8] "National Weather Service Web Services." National Weather Service. https://www.weather.gov/gis/cloudgiswebservices (Accessed March 4, 2024)<br>
[9] "National Weather Service: Services Web API." National Weather Service. https://www.weather.gov/documentation/services-web-api (Accessed March 4, 2024)<br>
[10] “NDFD Data Access and Web Map Service (WMS).” National Weather Service. https://digital.mdl.nws.noaa.gov/staticpages/mapservices.php (Accessed March 4, 2024)<br>
[11] "React Icons License Information."  React Icons.  https://react-icons.github.io/react-icons/ (Accessed March 2, 2024)<br>


