import { MapContext } from "@react-google-maps/api";
import {
  Children,
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";

const OverlayContext = createContext({
  overlays: {},
  putOverlay(key, mapType) {},
});

export function ImageMapType({ id, options, opacity = 1.0 }) {
  const { putOverlay } = useContext(OverlayContext);
  const tileUrl = options.getTileUrl({ x: 0, y: 0}, 0);
  const imageMapType = useMemo(() => {
    return new window.google.maps.ImageMapType(options);
  }, [
    options.alt,
    tileUrl,
    options.maxZoom,
    options.minZoom,
    options.name,
    options.tileSize,
  ]);

  useEffect(() => {
    putOverlay(id, imageMapType);
  }, [imageMapType, id]);

  useEffect(() => {
    imageMapType.setOpacity(opacity);
  }, [imageMapType, opacity]);
}

export function OverlayMapTypes({ children }) {
  const map = useContext(MapContext);
  const [overlays, setOverlays] = useState({});
  const putOverlay = (key, mapType) => {
    setOverlays((prevOverlays) => ({ ...prevOverlays, [key]: mapType }));
  };

  const overlayContext = {
    overlays,
    putOverlay,
  };

  const overlayOrder = useMemo(() => {
    let overlayOrder = [];
    Children.forEach(children, (child, index) => {
      overlayOrder.push(child.props.id);
    });
    return overlayOrder;
  }, [children]);

  useEffect(() => {
    if (typeof map !== "object") {
      return; // For some odd reason map can be an integer
    }
    map.overlayMapTypes.clear();
    for (let key of overlayOrder) {
      map.overlayMapTypes.push(overlays[key]);
    }
    return () => map.overlayMapTypes.clear();
  }, [JSON.stringify(overlayOrder), map, overlays]);

  return (
    <OverlayContext.Provider value={overlayContext}>
      {children}
    </OverlayContext.Provider>
  );
}
