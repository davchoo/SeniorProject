// Adapted from https://stackoverflow.com/a/21623206 to use Google LatLng
export function haversineDistance(point1, point2) {
  const lat1 = point1.lat;
  const lon1 = point1.lng;
  const lat2 = point2.lat; 
  const lon2 = point2.lng; 

  const r = 6371; // km
  const p = Math.PI / 180;

  const a = 0.5 - Math.cos((lat2 - lat1) * p) / 2
                + Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                  (1 - Math.cos((lon2 - lon1) * p)) / 2;

  return 2 * r * Math.asin(Math.sqrt(a));
}
