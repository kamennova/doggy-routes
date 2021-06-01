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
    DeleteRouteId = e.path[ 2 ].id.split("-")[ 1 ];
    open(deleteModal);
};

const deleteRouteReq = (id) => fetch(`${API_URL_BASE}/routes/${id}`, {
    method: "DELETE",
}).then(res => res.json());

const deleteRoute = () => deleteRouteReq(DeleteRouteId).then(res => {
    if (res.status < 400) {
        DeleteRouteId = null;
    }
    close(deleteModal);
});

document.getElementById("btn-confirm-delete").addEventListener("click", deleteRoute);

document.querySelectorAll(".delete-btn").forEach((btn) => {
    btn.addEventListener("click", preDelete);
});

// === Init map ===
