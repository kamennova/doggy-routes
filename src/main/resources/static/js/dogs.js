const close = (elem) => elem.classList.add("hidden");
const open = (elem) => elem.classList.remove("hidden");

document.querySelectorAll(".close-btn")
    .forEach(elem => elem.addEventListener("click", (evt) => close(evt.path[ 1 ])));

document.getElementById("btn-add-dog").addEventListener("click", () => open(document.getElementById("add-dog-modal")));

document.getElementById("btn-delete-dog").addEventListener("click", () => {
    deleteDogReq(DeleteDogId).then(res => {
        if (res.status < 400) {
            DeleteDogId = undefined;
        }
        close(document.getElementById("delete-dog-modal"));
    })
});

const dogCreateReq = (params) => {
    return fetch(`${API_URL_BASE}/dogs`, {
        method: "POST",
        body: JSON.stringify(params),
        headers: {
            'Content-Type': 'application/json'
        },
    }).then(res => res.json());
};

const dogForm = document.forms.createDog;

const getFormData = () => {
    const data = new FormData(dogForm);
    return ({
        name: data.get("dog-name"),
        breedId: Number(data.get("dog-breed")),
        yearBorn: Number(data.get("dog-year-born")),
        sex: data.get("dog-sex"),
    });
};

const validateForm = (data) => {
    return false;
    if (data.name.length === 0) {
        return "Будь ласка, введіть імʼя собаки";
    } else if (data.breed == null) {
        return "Будь ласка, оберіть породу собаки";
    } else if (data.yearBorn === "") {
        return "Будь ласка, оберіть приблизну дату народження собаки";
    }

    return null;
};

const setError = (error) => {
    document.getElementById("dog-form-errors").innerText = error;
};

document.getElementById("btn-save-dog").addEventListener("click", () => {
    const data = getFormData();
    const error = validateForm(data);

    if (!error) {
        document.getElementById("add-dog-modal").classList.add("hidden");
        setError("");
        dogForm.reset();
        dogCreateReq(data).then(res => console.log("created", res));
    } else {
        setError(error);
    }
});

const deleteDogReq = (id) => {
    return fetch(`${API_URL_BASE}/dogs/${id}`, {
        method: "DELETE",
    }).then(res => res.json());
};
