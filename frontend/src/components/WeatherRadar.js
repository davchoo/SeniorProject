import axios from "axios";
import { useEffect } from "react";
import { ImageMapType, OverlayMapTypes } from "./ImageOverlay";

function parseCapabilities(documentString) {
  let parser = new DOMParser()
  let doc = parser.parseFromString(documentString, "application/xml")
  let layers = {}
  for (let layerElm of doc.querySelectorAll("Capability > Layer > Layer")) {
    let name = layerElm.querySelector("Name").textContent
    let dimensions = {}
    for (let dimensionElm of layerElm.querySelectorAll("Dimension")) {
      let dimensionName = dimensionElm.getAttribute("name")
      dimensions[dimensionName] = {
        name: dimensionName,
        defaultValue: dimensionElm.getAttribute("default"),
        values: dimensionElm.textContent.split(",")
      }
    }
    layers[name] = {name, dimensions}
  }
  return layers
}

export default function WeatherRadar({setAvailableLayers, layerName, time, opacity = 1.0}) {
  useEffect(() => {
    let controller = new AbortController()
    axios.get("/geoserver/capabilities", {withCredentials: false, responseEncoding: "text", signal: controller.signal})
      .then(
        response => parseCapabilities(response.data),
        error => {
          if (!axios.isCancel(error)) {
            console.error(error)
          }
        }
      )
      .then(setAvailableLayers)
    return () => controller.abort()
  }, []) // TODO need periodic updates?

  let overlayOptions = {
    getTileUrl: (coord, zoom) => `${process.env.REACT_APP_API_URL}/geoserver/gmaps?layers=${layerName}&zoom=${zoom}&x=${coord.x}&y=${coord.y}&format=image/png&time=${time}`,
    name: layerName + " " + time,
    time
  }
  return (
    <OverlayMapTypes>
      {layerName && time && (
        <ImageMapType id={layerName} options={overlayOptions} opacity={opacity} />
      )}
    </OverlayMapTypes>
  )
}