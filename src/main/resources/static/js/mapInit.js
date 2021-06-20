// === Init Map ===

const MapView = new ol.View({
    center: ol.proj.fromLonLat([ 30.52, 50.44 ]),
    extent: [ 3357395.84, 6482723.88, 3434206.29, 6549087.42 ],
    zoom: 10,
    minZoom: 10,
});

const map = new ol.Map({
    target: 'map',
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        })
    ],
    view: MapView,
});

const vectorSource = new ol.source.Vector({
    features: [],
});

const vectorLayer = new ol.layer.Vector({
    source: vectorSource,
    style: (feature, resolution) => {
        const lineOpacity = feature.get("dogs") ? 0.4 + Math.min(feature.get("dogs").length, 3) * 0.2 : 1;
        return [
            new ol.style.Style({
                stroke: new ol.style.Stroke({
                    color: feature.get("color") || `rgba(113, 43, 22, ${lineOpacity})`,
                    width: resolution < 6 ? 4 : 3,
                })
            }),
            new ol.style.Style({
                image: new ol.style.Icon({
                    src: 'img/paw.svg',
                }),
            })
        ]
    }
});

map.addLayer(vectorLayer);

const clearMap = () => vectorSource.clear();

const addLineToMap = (polylineStr) => {
    const poly = new ol.format.Polyline();
    const feature = poly.readFeature(polylineStr);
    formatOL(feature.getGeometry()); // coordinates from API come in different format
    vectorSource.addFeature(feature);
};
