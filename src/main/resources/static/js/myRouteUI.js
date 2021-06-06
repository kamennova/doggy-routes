const RouteStops = [];
const StopConnections = [];

const createLineFromCoordinates = (coordinates) => {
    console.log(coordinates);
    const line = new ol.geom.LineString(coordinates).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({geometry: line});
    vectorSource.addFeature(feature);

    return feature;
};

// ===

const getConnectionCoordinates = (start, end) => {
    const startParam = `start=${start[ 0 ]},${start[ 1 ]}`;
    const endParam = `end=${end[ 0 ]},${end[ 1 ]}`;

    return fetch(`${ROUTING_API_URL}?${ROUTING_API_KEY}&${startParam}&${endParam}`)
        .then(res => res.json())
        .then(res => res.features[ 0 ].geometry.coordinates);
};

const reverse = (points) => {
    for (let i = 0; i < points.length / 2; i++) {
        const temp = points[ i * 2 ];
        points[ i * 2 ] = points[ i * 2 + 1 ];
        points[ i * 2 + 1 ] = temp;
    }
};

const saveNewRoute = () => {
    return fetch(`${API_URL_BASE}/routes`, {
        method: "POST",
        headers: {
            "Content-Type": 'application/json',
        },
        body: JSON.stringify({
            coordinates: StopConnections.map(
                feature => feature.getGeometry().clone().transform('EPSG:3857', 'EPSG:4326').getCoordinates())
                .flat()
                .map(c => [c[1], c[0]])
                .flat()
        }),
    }).then(res => res.json());
};

const to4326 = (point) => point.clone().transform('EPSG:3857', 'EPSG:4326').flatCoordinates;

const addRouteStop = (point) => {
    const lastPoint = RouteStops[ RouteStops.length - 1 ];
    RouteStops.push(point);

    if (RouteStops.length > 1) {
        getConnectionCoordinates(to4326(lastPoint), to4326(point))
            .then(coordinates => {
                const line = createLineFromCoordinates(coordinates);
                StopConnections.push(line);
            });
    }
};

map.on('click', (event) => {
    const point = new ol.geom.Point(event.coordinate);
    addRouteStop(point);

    const feature = new ol.Feature({type: 'icon', geometry: point,});
    vectorSource.addFeature(feature);
});

// === Init UI ===

const FormModes = {Create: "create", Edit: "edit"};
const FORM_MODE = FormModes.Create;

const AnimationDuration = 200;
const routeForm = document.getElementById("route-form");
const routesCol = document.getElementById("routes-sidebar");

const closeRouteForm = () => routeForm.classList.add("closed");
const showRouteForm = () => routeForm.classList.remove("closed");
const showRoutesCol = () => routesCol.classList.remove("closed");
const closeRoutesCol = () => routesCol.classList.add("closed");

// on route form open
document.getElementById("btn-add-route").addEventListener("click", () => {
    closeRoutesCol();

    const routesNum = document.getElementsByClassName("route-item").length;
    if (routesNum > 0) {
        document.getElementById("new-route-index").innerText = routesNum + 1;
    }

    setTimeout(showRouteForm, AnimationDuration);
    setTimeout(clearMap, AnimationDuration * 2);
});

// on route form close
document.getElementById("route-form-cancel-btn").addEventListener("click", () => {
    closeRouteForm();
    setTimeout(showRoutesCol, AnimationDuration);
    setTimeout(() => {
        clearMap();
        drawAllRoutes();
    }, AnimationDuration * 2);

});

// assign colors to routes

const colors = ["black", "blue", "brown", "green", "yellow", "pink"];

myRoutes.forEach((route, index) => {
    myRoutes[i].color = colors[index%(colors.length - 1)];
});

const drawAllRoutes = () => {
    myRoutes.forEach(route => {
        const coords = route.coords;
        const line = new ol.geom.LineString(coords).transform('EPSG:4326', 'EPSG:3857');
        const feature = new ol.Feature({geometry: line, color: route.color});
        console.log(route.color);
        vectorSource.addFeature(feature);

        return feature;
    });
};

drawAllRoutes();

document.getElementById("route-form-save-btn").addEventListener("click", () => {
    if (FORM_MODE === FormModes.Create) {
        saveNewRoute().then(res => console.log(res));
    } else {
        console.log("save changes");
    }
});

// === Delete init

let DeleteRouteId;

const deleteModal = document.getElementById("delete-route-modal");

const close = (elem) => elem.classList.add("hidden");
const open = (elem) => elem.classList.remove("hidden");

// init modal close buttons
document.querySelectorAll(".close-btn")
    .forEach(elem => elem.addEventListener("click", (evt) => close(evt.path[ 1 ])));

// show confirmation modal
const preDelete = (e) => {
    DeleteRouteId = e.path[ 2 ].dataset.id;
    open(deleteModal);
};

const deleteRouteReq = (id) => fetch(`${API_URL_BASE}/routes/${id}`, {
    method: "DELETE",
}).then(res => res.json());

const deleteRoute = () => deleteRouteReq(DeleteRouteId).then(res => {
    if (!res.error) {
        DeleteRouteId = null;
    }
    close(deleteModal);
});

document.getElementById("btn-confirm-delete").addEventListener("click", deleteRoute);

document.querySelectorAll(".delete-btn").forEach((btn) => {
    btn.addEventListener("click", preDelete);
});
