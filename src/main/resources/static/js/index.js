let Dogs = [];

const hide = (elem) => elem.classList.add('hidden');
const show = (elem) => elem.classList.remove('hidden');

const getRoutesOverview = () => fetch(`${API_URL_BASE}/routes`).then(res => res.json());

const getRoutesMap = (lat, lng) => fetch(`${API_URL_BASE}/routes/details?lat=${lat}&lng=${lng}`)
    .then(res => res.json());

const drawLine = (group) => {
    const multi = new ol.geom.MultiLineString(group.routes).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({geometry: multi, dogs: group.dogs, type: 'line'});
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
    const zoom = MapView.getZoom();

    if (zoom < ReqZoom) {
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
const ReqZoom = 14;
const ZoomInAlert = "Наблизьте, щоб побачити маршрути";

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

    dogs.forEach(dog => {
        const div = document.createElement('div');
        const img = document.createElement('img');
        img.classList.add("avatar");
        img.src = getDogBreedPic(dog);

        const breedName = document.createElement('span');
        breedName.classList.add('breed');
        breedName.innerText = dog.breed.name !== null ? dog.breed.name : "Порода невідома";

        div.appendChild(img);
        div.appendChild(breedName);

        if (dog.sex) {
            const sex = document.createElement('img');
            sex.classList.add('sex-icon');
            sex.src = dog.sex === 'm' ? '/img/male.svg' : '/img/female.svg';
            div.appendChild(sex);
        }

        if (dog.age) {
            const age = document.createElement('span');
            age.classList.add('age');
            age.innerText = (dog.age > 0 ? dog.age : '<1') + 'р.';
            div.appendChild(age);
        }

        dogPopup.appendChild(div);
    });

    dogPopup.style.opacity = '0';
    show(dogPopup);
    dogPopup.style.left = (pixel[ 0 ] - dogPopup.offsetWidth / 2) + 'px';
    dogPopup.style.top = (pixel[ 1 ] - dogPopup.offsetHeight) + 'px';
    dogPopup.style.opacity = '1';
};

let selected = null;

const highlightStyle = [
    new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#252525',
            width: 3,
        }),
    }),
    new ol.style.Style({
        image: new ol.style.Icon({
            src: '/img/paw_hover.svg',
        }),
    }),
];

const routeOnHover = (f, pixel) => {
    if (selected) {
        if (selected.get('id') === f.get('id')) {
            return;
        }

        selected.setStyle(undefined);
    }

    selected = f;
    f.setStyle(highlightStyle);

    const dogs = f.get('type') === 'point' ? f.get('dogs') : getDogsByIds(f.get('dogs'));
    const UnknownDog = [ {id: null, breed: {name: null, pic: null}} ];
    displayDogs(dogs.length > 0 ? dogs : UnknownDog, pixel);

    return true;
};

map.on('pointermove', (e) => {
    const pixel = map.getEventPixel(e.originalEvent);
    const hit = map.hasFeatureAtPixel(pixel);
    map.getViewport().style.cursor = hit ? 'pointer' : '';

    if (hit) {
        map.forEachFeatureAtPixel(e.pixel, f => routeOnHover(f, e.pixel));
    } else if (selected) {
        selected.setStyle(undefined);
        selected = null;
        hidePopup();
    }
});
