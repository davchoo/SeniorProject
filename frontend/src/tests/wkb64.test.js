import { decodeWKB64 } from "../utils/wkb64";

// Expected GeoJSON object is at testCases[2 * i]
// Well-Known Binary formatted geometry that is encoded in Base64 is at testCases[2 * i + 1]
let testCases = [
    {"type": "Point", "coordinates": [100.0, 0.0]}, "AAAAAAFAWQAAAAAAAAAAAAAAAAAA",
    {
        "type": "LineString",
        "coordinates": [[100.0, 0.0], [101.0, 1.0]]
    }, "AAAAAAIAAAACQFkAAAAAAAAAAAAAAAAAAEBZQAAAAAAAP/AAAAAAAAA=",
    {
        "type": "Polygon",
        "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]
    }, "AAAAAAMAAAABAAAABUBZAAAAAAAAAAAAAAAAAABAWUAAAAAAAAAAAAAAAAAAQFlAAAAAAAA/8AAAAAAAAEBZAAAAAAAAP/AAAAAAAABAWQAAAAAAAAAAAAAAAAAA",
    {
        "type": "Polygon",
        "coordinates": [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.8, 0.8], [100.8, 0.2], [100.2, 0.2], [100.2, 0.8], [100.8, 0.8]]]
    }, "AAAAAAMAAAACAAAABUBZAAAAAAAAAAAAAAAAAABAWUAAAAAAAAAAAAAAAAAAQFlAAAAAAAA/8AAAAAAAAEBZAAAAAAAAP/AAAAAAAABAWQAAAAAAAAAAAAAAAAAAAAAABUBZMzMzMzMzP+mZmZmZmZpAWTMzMzMzMz/JmZmZmZmaQFkMzMzMzM0/yZmZmZmZmkBZDMzMzMzNP+mZmZmZmZpAWTMzMzMzMz/pmZmZmZma",
    {
        "type": "MultiPoint",
        "coordinates": [[100.0, 0.0], [101.0, 1.0]]
    }, "AAAAAAQAAAACAAAAAAFAWQAAAAAAAAAAAAAAAAAAAAAAAAFAWUAAAAAAAD/wAAAAAAAA",
    {
        "type": "MultiLineString",
        "coordinates": [[[100.0, 0.0], [101.0, 1.0]], [[102.0, 2.0], [103.0, 3.0]]]
    }, "AAAAAAUAAAACAAAAAAIAAAACQFkAAAAAAAAAAAAAAAAAAEBZQAAAAAAAP/AAAAAAAAAAAAAAAgAAAAJAWYAAAAAAAEAAAAAAAAAAQFnAAAAAAABACAAAAAAAAA==",
    {
        "type": "MultiPolygon",
        "coordinates": [[[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]
    }, "AAAAAAYAAAACAAAAAAMAAAABAAAABUBZgAAAAAAAQAAAAAAAAABAWcAAAAAAAEAAAAAAAAAAQFnAAAAAAABACAAAAAAAAEBZgAAAAAAAQAgAAAAAAABAWYAAAAAAAEAAAAAAAAAAAAAAAAMAAAACAAAABUBZAAAAAAAAAAAAAAAAAABAWUAAAAAAAAAAAAAAAAAAQFlAAAAAAAA/8AAAAAAAAEBZAAAAAAAAP/AAAAAAAABAWQAAAAAAAAAAAAAAAAAAAAAABUBZDMzMzMzNP8mZmZmZmZpAWQzMzMzMzT/pmZmZmZmaQFkzMzMzMzM/6ZmZmZmZmkBZMzMzMzMzP8mZmZmZmZpAWQzMzMzMzT/JmZmZmZma",
    {
        "type": "GeometryCollection",
        "geometries": [{"type": "Point", "coordinates": [100.0, 0.0]}, {
            "type": "LineString",
            "coordinates": [[101.0, 0.0], [102.0, 1.0]]
        }]
    }, "AAAAAAcAAAACAAAAAAFAWQAAAAAAAAAAAAAAAAAAAAAAAAIAAAACQFlAAAAAAAAAAAAAAAAAAEBZgAAAAAAAP/AAAAAAAAA="
];

test('decodeWKB64 decodes to GeoJSON correctly', () => {
    for (let i = 0; i < testCases.length; i += 2) {
        let expectedGeoJSON = testCases[i];
        let wkb64 = testCases[i + 1];
        let result = decodeWKB64(wkb64);
        expect(JSON.stringify(result)).toBe(JSON.stringify(expectedGeoJSON));
    }
})
