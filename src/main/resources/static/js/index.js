const getRoutes = (lat, lng, zoom) => fetch(`${API_URL_BASE}/routes?lat=${lat}&lng=${lng}&zoom=${zoom}`)
        .then(res => res.json());

const drawLine = (group) => {
    console.log(group);
    const multi = new ol.geom.MultiLineString(group.routes).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({geometry: multi, color: 'brown'});
    vectorSource.addFeature(feature);
    return feature;
};

const drawPoint = (point) => {
    const geom = new ol.geom.Point(point.coordinate).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({type: 'icon', geometry: geom,});
    vectorSource.addFeature(feature);
};

const update = () => {
    const point = new ol.geom.Point(MapView.getCenter()).transform('EPSG:3857', 'EPSG:4326').getCoordinates();
    const zoom = MapView.getZoom() - 10;
    getRoutes(point[ 1 ], point[ 0 ], zoom).then(res => {
        if (res.lines) {
            clearMap();
            res.lines.forEach(drawLine);
        } else if (res.overview) {
            clearMap();
            res.overview.forEach(drawPoint);
        }
    });
};

const alertContainer = document.getElementById("alerts-container");

const clearAlerts = () => {
    alertContainer.innerHTML = '';
};

const addAlert = (text) => {
    const alert = document.createElement("p");
    alert.classList.add("alert");
    alert.innerText = text;
    alertContainer.appendChild(alert);
};

let OldZoom = MapView.getZoom();
const ReqZoom = 14.5;

map.on('movestart', (event) => {
    OldZoom = MapView.getZoom();
});

map.on('moveend', (event) => {
    const upd = MapView.getZoom();
    const diff = upd - OldZoom;

    if (diff !== 0) {
        if (OldZoom < ReqZoom && upd >= ReqZoom) {
            clearAlerts();
        } else if (OldZoom >= ReqZoom && upd < ReqZoom) {
            addAlert("Зменшіть масштаб, щоб побачити маршрути");
        }

        update();
        OldZoom = upd;
    }
});

update();
addAlert("Зменшіть масштаб, щоб побачити маршрути");
