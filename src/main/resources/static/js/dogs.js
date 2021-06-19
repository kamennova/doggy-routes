const close = (elem) => elem.classList.add("hidden");
const open = (elem) => elem.classList.remove("hidden");
const deleteModal = document.getElementById("delete-dog-modal");

document.querySelectorAll(".close-btn")
    .forEach(elem => elem.addEventListener("click", (evt) => close(evt.path[ 1 ])));

document.getElementById("btn-add-dog").addEventListener("click", () => open(document.getElementById("add-dog-modal")));

document.getElementById("btn-delete-dog").addEventListener("click", () => {
    deleteDogReq(DeleteDogId).then(() => {
        DeleteDogId = undefined;
        close(deleteModal);
        document.location.href = "my-dogs";
    });
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
    if (data.name.length === 0) {
        return "Будь ласка, введіть імʼя собаки";
    } else if (data.breedId == null) {
        return "Будь ласка, оберіть породу собаки";
    } else if (data.yearBorn === 0) {
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

    if (error == null) {
        document.getElementById("add-dog-modal").classList.add("hidden");
        setError("");
        dogForm.reset();
        dogCreateReq(data).then(res => {
            if (res.error) {
                setError(res.error);
            } else {
                document.location.href = "my-dogs";
            }
        });
    } else {
        setError(error);
    }
});

let DeleteDogId;

const deleteDog = (e) => {
    DeleteDogId = Number(e.path[ 0 ].dataset.id);
    open(deleteModal);
};

document.querySelectorAll(".delete-dog-btn").forEach((btn) => btn.addEventListener("click", deleteDog));

const deleteDogReq = (id) => {
    return fetch(`${API_URL_BASE}/dogs/${id}`, {
        method: "DELETE",
    });
};
