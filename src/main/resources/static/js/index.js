let Dogs = [];

const hide = (elem) => elem.classList.add('hidden');
const show = (elem) => elem.classList.remove('hidden');

const getRoutesOverview = () => fetch(`${API_URL_BASE}/routes`).then(res => res.json());

const getRoutesMap = (lat, lng) => fetch(`${API_URL_BASE}/routes/details?lat=${lat}&lng=${lng}`)
    .then(res => res.json());

const drawLine = (group) => {
    const multi = new ol.geom.MultiLineString(group.routes).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({geometry: multi, color: 'brown', dogIds: group.dogIds, type: 'line'});
    vectorSource.addFeature(feature);
    return feature;
};

const drawPoint = (point, id) => {
    const geom = new ol.geom.Point(point.coordinate).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({type: 'point', geometry: geom, dogs: point.dogs, id});
    vectorSource.addFeature(feature);
};

const update = () => {
    const point = new ol.geom.Point(MapView.getCenter()).transform('EPSG:3857', 'EPSG:4326').getCoordinates();
    const zoom = MapView.getZoom() - 10;

    if (zoom < 4.5) {
        getRoutesOverview().then(res => {
            clearMap();
            res.overview.forEach(drawPoint);
        });
    } else {
        getRoutesMap(point[ 1 ], point[ 0 ], zoom).then(res => {
            Dogs = res.dogs;
            clearMap();
            res.lines.forEach(drawLine);

        });
    }
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
let OldCenter = MapView.getCenter();
const ReqZoom = 14.5;
const ZoomInAlert = "Зменшіть масштаб, щоб побачити маршрути";

map.on('movestart', () => OldZoom = MapView.getZoom());

map.on('moveend', () => {
    const zoomUpd = MapView.getZoom();
    const zoomDiff = zoomUpd - OldZoom;

    const centerUpd = MapView.getCenter();

    if (zoomDiff !== 0) {
        if (OldZoom < ReqZoom && zoomUpd >= ReqZoom) {
            clearAlerts();
        } else if (OldZoom >= ReqZoom && zoomUpd < ReqZoom) {
            addAlert(ZoomInAlert);
        }

        update();
        OldZoom = zoomUpd;
    }

    if ((centerUpd[ 0 ] !== OldCenter[ 0 ] || centerUpd[ 1 ] !== OldCenter[ 1 ]) && OldZoom >= ReqZoom) {
        console.log('map moved');
    }
});

update();
addAlert(ZoomInAlert);

const dogPopup = document.getElementById('dog-popup');

const getDogBreedPic = (dog) => dog.breed.pic === null ? "https://img.icons8.com/cotton/2x/dog.png" : dog.breed.pic;

const hidePopup = () => {
    hide(dogPopup);
};

const getDogsByIds = (ids) => Dogs.filter(d => ids.includes(d.id));

const displayDogs = (dogs, pixel) => {
    while (dogPopup.hasChildNodes()) {
        dogPopup.removeChild(dogPopup.firstChild);
    }

    console.log(pixel);

    dogs.forEach(dog => {
        const div = document.createElement('div');
        const img = document.createElement('img');
        img.classList.add("avatar");
        img.src = getDogBreedPic(dog);

        const sex = document.createElement('img');
        sex.classList.add('sex-icon');
        sex.src = dog.sex === 'm' ? '/img/male.svg' : '/img/female.svg';

        const age = document.createElement('span');
        age.classList.add('age');
        age.innerText = (dog.age > 0 ? dog.age : '<1') + 'р.';

        const breedName = document.createElement('span');
        breedName.classList.add('breed');
        breedName.innerText = dog.breed.name;

        div.appendChild(img);
        div.appendChild(breedName);
        div.appendChild(sex);
        div.appendChild(age);
        dogPopup.appendChild(div);
    });

    dogPopup.style.left = pixel[ 0 ] - dogPopup.offsetWidth / 2 + 'px';
    dogPopup.style.top = pixel[ 1 ] - dogPopup.offsetHeight + 'px';
    show(dogPopup);
};

let selected = null;

const highlightStyle = new ol.style.Style({
    fill: new ol.style.Fill({
        color: 'rgba(255,255,255,0.7)',
    }),
    stroke: new ol.style.Stroke({
        color: '#3399CC',
        width: 3,
    }),
});

const routeOnHover = (f) => {
    selected = f;
    f.setStyle(highlightStyle);
    return true;
};

map.on('pointermove', (e) => {
    const pixel = map.getEventPixel(e.originalEvent);
    const hit = map.hasFeatureAtPixel(pixel);
    map.getViewport().style.cursor = hit ? 'pointer' : '';

    if (selected !== null) {
        selected.setStyle(undefined);
        selected = null;
    }

    map.forEachFeatureAtPixel(e.pixel, routeOnHover);

    if (selected) {
        const dogs = selected.get('type') === 'point' ? selected.get('dogs') :
            getDogsByIds(selected.get('dogIds'));
        displayDogs(dogs, e.pixel);
    } else {
        hidePopup();
    }
});
