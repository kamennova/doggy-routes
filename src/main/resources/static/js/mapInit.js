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
        return [
            new ol.style.Style({
                stroke: new ol.style.Stroke({color: feature.get("color") || '#432828', width: 3})
            }),
            new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 7,
                    fill: new ol.style.Fill({color: '#432828'}),
                    stroke: new ol.style.Stroke({
                        color: 'white',
                        width: 2,
                    }),
                }),
            }) ];
    }

});

map.addLayer(vectorLayer);

const clearMap = () => vectorSource.clear();

const addLineToMap = (polylineStr) => {
    const poly = new ol.format.Polyline();
    const feature = poly.readFeature(polylineStr);
    feature.getGeometry().transform('EPSG:4326', 'EPSG:3857'); // coordinates from API come in diff. format
    vectorSource.addFeature(feature);
};
