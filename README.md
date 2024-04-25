# **TripEase**

## Contributors
Emma Zimmerman (https://github.com/EmmaZim, https://www.linkedin.com/in/emma-zimmerman-4aa317267/),<br>
David Choo (https://github.com/davchoo),<br>
Lukas DeLoach (https://github.com/lukasdeloach, https://www.linkedin.com/in/lukas-deloach/),<br>
Kaan Kayis (https://github.com/kaankayis22, https://www.linkedin.com/in/kaan-kayis-b6b708266/),<br>
Yekaterina Saburova (https://github.com/KatSaburova, https://www.linkedin.com/in/yekaterina-saburova-0aa257242/)

## Project Description 
TripEase is a web application tailored for individuals at all levels of travel experience to streamline the process of planning any trip. The sign-up process will guide users through the journey planning process, simplifying tasks from selecting departure and destination points to finalizing itineraries within an intuitive web interface. For budget-conscious travelers, TripEase will locate convenient and cost-effective gas stops, along with their information, along their route. The application will also inform users about unpredictable weather conditions through real-time weather updates, a weather overlay, and a weather radar for up to seven days in advance.
   
TripEase's integration with various APIs guarantees a seamless flow of current information, encompassing weather updates and gas prices. This comprehensive integration will enhance the efficiency of the web application, ensuring users have accurate and up-to-date insights for a more streamlined trip-planning experience.

## User Interaction
The user begins planning their journey by opening the web application and being directed to the Home Page. On the Home Page, they will encounter a welcome message and a Plan Your Trip button. Upon selecting the Plan Your Trip Button, the user is redirected to the Planning Page. On this page, the user provides the origin and destination locations. If the locations are invalid, the system will display an error and they will have to enter another location. If the locations are valid, the user will select the Gas or Weather button. If the user selects the Gas button, they will be able to view the gas stations on the map by selecting the Show Gas Stations Button. If the user selects the Weather button, they will be able to select Weather Overlay or Weather Radar for their route.

## Frontend Design Components
The frontend of this application was created using JavaScript in the React framework. NGINX Version 1.25.4 serves as the frontend’s static files, and the Google Maps API is used to locate gas stations and display maps on the Frontend. TripEase can be accessed through any web browser, including Google Chrome, FireFox, Microsoft Edge, and Safari. 

## Installation
**Steps to deploy TripEase to instance:**
1. Install Docker Engine and Docker Compose. <br>
2. On a local machine, compile the backend and frontend from the main branch of the repository. <br>
3. Transfer the compiled artifacts and the docker folder from the main branch to the instance. <br>
4. Transfer configuration files for the backend and Docker to the instance. <br>
5. Run the startup script to bring up the necessary Docker containers.<br>

**Required Packages for Frontend**
* axios<br>
* react<br>
* react-google-maps/api<br>
* react-icons<br>
* react-rating-stars-component<br>
* react-router-dom<br>
* react-spinners<br>

**Required Configuration Files**
The frontend requires a .env file to be present in the frontend folder during compilation. This file requires the following configuration values:
API Key for Google Maps

