# **TripEase - Frontend**

## Frontend User Interaction
The user begins planning their journey by opening the web application and being directed to the Home Page. On the Home Page, they will encounter a welcome message and a Plan Your Trip button. Upon selecting the Plan Your Trip Button, the user is redirected to the Planning Page. On this page, the user provides the origin and destination locations. If the locations are invalid, the system will display an error and they will have to enter another location. If the locations are valid, the user will select the Gas or Weather button. If the user selects the Gas button, they will be able to view the gas stations on the map by selecting the Show Gas Stations Button. If the user selects the Weather button, they will be able to select Weather Overlay or Weather Radar for their route.

## Frontend Design Components
The frontend of this application was created using JavaScript in the React framework. NGINX Version 1.25.4 serves as the frontend’s static files, and the Google Maps API is used to locate gas stations and display maps on the Frontend. TripEase can be accessed through any web browser, including Google Chrome, FireFox, Microsoft Edge, and Safari. 

## Installation
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
