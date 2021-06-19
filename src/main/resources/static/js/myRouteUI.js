const RouteStops = [];
const StopConnections = [];

const createLineFromCoordinates = (coordinates) => {
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
                .map(c => [ c[ 1 ], c[ 0 ] ])
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

const addPointFunc = (event) => {
    const point = new ol.geom.Point(event.coordinate);
    addRouteStop(point);
    const feature = new ol.Feature({type: 'icon', geometry: point,});
    vectorSource.addFeature(feature);
};

// === Init UI ===

const FormModes = {Create: "create", Edit: "edit"};
let FORM_MODE = FormModes.Create;

const AnimationDuration = 200;
const routeForm = document.getElementById("route-form");
const routesCol = document.getElementById("routes-sidebar");
const routeView = document.getElementById("route-view");

const hide = (elem) => elem.classList.add("hidden");
const show = (elem) => elem.classList.remove("hidden");
const closeRouteForm = () => routeForm.classList.add("closed");
const showRouteForm = () => routeForm.classList.remove("closed");
const showRouteView = () => routeView.classList.remove("closed");
const closeRouteView = () => routeView.classList.add("closed");
const showRoutesCol = () => routesCol.classList.remove("closed");
const closeRoutesCol = () => routesCol.classList.add("closed");

// on route form open
document.getElementById("btn-add-route").addEventListener("click", () => {
    openRouteForm(document.getElementsByClassName("route-item").length + 1);
    map.on('click', addPointFunc);
});

const openRouteForm = (routeIndex) => {
    closeRoutesCol();
    document.getElementById("new-route-index").innerText = routeIndex;

    if (FORM_MODE !== FormModes.Create) {
        hide(document.getElementById('new-route-label'));
    } else {
        show(document.getElementById('new-route-label'))
    }

    setTimeout(showRouteForm, AnimationDuration);
    setTimeout(clearMap, AnimationDuration * 2);
};

const openRouteView = (routeIndex) => {
    closeRoutesCol();
    document.getElementById("route-index").innerText = routeIndex + 1;
    setTimeout(showRouteView, AnimationDuration);
};

// on route form close
document.getElementById("route-form-cancel-btn").addEventListener("click", () => {
    closeRouteForm();
    map.un('click', addPointFunc);
    setTimeout(showRoutesCol, AnimationDuration);
    setTimeout(() => {
        clearMap();
        drawAllRoutes();
    }, AnimationDuration * 2);
});

// on route view close
document.getElementById("route-back-btn").addEventListener("click", () => {
    closeRouteView();
    setTimeout(showRoutesCol, AnimationDuration);
    setTimeout(() => {
        clearMap();
        drawAllRoutes();
    }, AnimationDuration * 2);
});

const drawRoute = (route) => {
    const coords = route.coordinates;
    const line = new ol.geom.LineString(coords).transform('EPSG:4326', 'EPSG:3857');
    const feature = new ol.Feature({geometry: line, color: route.color});
    vectorSource.addFeature(feature);

    return feature;
};

const centerIn = (coord) => {
    MapView.animate({center: ol.proj.fromLonLat(coord), duration: 600, zoom: 15});
};

const drawAllRoutes = () => {
    if (myRoutes.length === 0) {
        return;
    }

    centerIn(myRoutes[ 0 ].coordinates[ 0 ]);
    myRoutes.forEach(drawRoute);
};

drawAllRoutes();

const displayError = (error) => {
    document.getElementById("route-error").innerText = error;
};

document.getElementById("route-form-save-btn").addEventListener("click", () => {
    if (StopConnections.length < 1) {
        displayError("Будь ласка, додайте точки маршруту");
        return;
    } else {
        displayError("");
    }

    if (FORM_MODE === FormModes.Create) {
        saveNewRoute().then(res => {
            if (res.error) {
                displayError(res.error);
            } else {
                document.location.href = "my-routes";
            }
        });
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
});

const deleteRoute = () => deleteRouteReq(DeleteRouteId).then(() => {
    DeleteRouteId = null;
    close(deleteModal);
});

document.getElementById("btn-confirm-delete").addEventListener("click", deleteRoute);

document.querySelectorAll(".delete-btn").forEach((btn) => {
    btn.addEventListener("click", preDelete);
});

//=== Edit

const onEdit = (e) => {
    FORM_MODE = FormModes.Edit;
    const id = Number(e.path[ 3 ].dataset.id);
    const index = myRoutes.findIndex(elem => elem.id === id);

    openRouteForm(index + 1);
    clearMap();
    drawRoute(myRoutes[ index ]);
};

const onView = (e) => {
    const id = Number(e.path[ 2 ].dataset.id);
    const index = myRoutes.findIndex(elem => elem.id === id);
    const route = myRoutes[ index ];

    openRouteView(index);
    setTimeout(() => {
        clearMap();
        drawRoute(route);
        centerIn(route.coordinates[ 0 ])
    }, AnimationDuration * 2);
};

document.querySelectorAll('.view-btn').forEach(btn => btn.addEventListener('click', onView));
