/**
 * Some functions that help with decoding Well-Known Binary formatted geometry into GeoJSON
 * The main function is decodeWKB64()
 */

class DataInputStream {
    constructor(buffer) {
        this.dataView = new DataView(buffer);
        this.readIndex = 0;
    }

    readUInt8() {
        let result = this.dataView.getUint8(this.readIndex);
        this.readIndex++;
        return result;
    }

    readUInt32(littleEndian = true) {
        let result = this.dataView.getUint32(this.readIndex, littleEndian);
        this.readIndex += 4;
        return result;
    }

    readFloat64(littleEndian = true) {
        let result = this.dataView.getFloat64(this.readIndex, littleEndian);
        this.readIndex += 8;
        return result;
    }
}

const GeometryType = {
    Point: 1,
    LineString: 2,
    Polygon: 3,
    MultiPoint: 4,
    MultiLineString: 5,
    MultiPolygon: 6,
    GeometryCollection: 7
};

export function decodeWKB64(b64encodedStr) {
    let rawString = atob(b64encodedStr);
    let binData = Uint8Array.from(rawString, (m) => m.charCodeAt(0));
    let is = new DataInputStream(binData.buffer);
    return readGeometry(is);
}

function readGeometry(is) {
    let littleEndian = is.readUInt8() === 1; // 0 - Big Endian, 1 - Little Endian
    let type = is.readUInt32(littleEndian);
    switch (type) {
        case GeometryType.Point:
            return readPoint(is, littleEndian);
        case GeometryType.LineString:
            return readLineString(is, littleEndian);
        case GeometryType.Polygon:
            return readPolygon(is, littleEndian);
        case GeometryType.MultiPoint:
            return readMultiPoint(is, littleEndian);
        case GeometryType.MultiLineString:
            return readMultiLineString(is, littleEndian);
        case GeometryType.MultiPolygon:
            return readMultiPolygon(is, littleEndian);
        case GeometryType.GeometryCollection:
            return readGeometryCollection(is, littleEndian);
        default:
            throw new Error(`Unknown geometry type: ${type}`);
    }
}

function readCoordinate(is, littleEndian) {
    return [is.readFloat64(littleEndian), is.readFloat64(littleEndian)]
}

function readCoordinatesArray(is, littleEndian) {
    let numCoordinates = is.readUInt32(littleEndian);
    let coordinates = [];
    for (let i = 0; i < numCoordinates; i++) {
        coordinates.push(readCoordinate(is, littleEndian));
    }
    return coordinates;
}

function readPoint(is, littleEndian) {
    return {
        type: "Point",
        coordinates: readCoordinate(is, littleEndian)
    };
}

function readLineString(is, littleEndian) {
    return {
        type: "LineString",
        coordinates: readCoordinatesArray(is, littleEndian)
    };
}

function readPolygon(is, littleEndian) {
    let numRings = is.readUInt32(littleEndian);
    let rings = [];
    for (let ringI = 0; ringI < numRings; ringI++) {
        rings.push(readCoordinatesArray(is, littleEndian));
    }
    return {
        type: "Polygon",
        coordinates: rings
    };
}

function readMulti(is, littleEndian, reader, typeStr, geometryType) {
    let numElements = is.readUInt32(littleEndian);
    let coordinates = [];
    for (let i = 0; i < numElements; i++) {
        let littleEndian = is.readUInt8() === 1; // 0 - Big Endian, 1 - Little Endian
        let elementType = is.readUInt32(littleEndian);
        if (elementType !== geometryType) {
            throw new Error(`Unexpected type while reading ${typeStr}. Expected ${geometryType} Actual: ${elementType}`);
        }
        coordinates.push(reader(is, littleEndian).coordinates);
    }
    return {
        type: typeStr,
        coordinates
    };
}

function readMultiPoint(is, littleEndian) {
    return readMulti(is, littleEndian, readPoint, "MultiPoint", GeometryType.Point);
}

function readMultiLineString(is, littleEndian) {
    return readMulti(is, littleEndian, readLineString, "MultiLineString", GeometryType.LineString);
}

function readMultiPolygon(is, littleEndian) {
    return readMulti(is, littleEndian, readPolygon, "MultiPolygon", GeometryType.Polygon);
}

function readGeometryCollection(is, littleEndian) {
    let numGeometries = is.readUInt32(littleEndian);
    let geometries = [];
    for (let i = 0; i < numGeometries; i++) {
        geometries.push(readGeometry(is));
    }
    return {
        type: "GeometryCollection",
        geometries
    };
}