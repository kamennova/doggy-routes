const signInForm = document.forms.signUp;

const getData = () => {
    const data = new FormData(signInForm);

    return {
        email: data.get("user-email"),
        password: data.get("user-password"),
    };
};

const validateData = (data) => {
    if (data.email.length === 0) {
        return "Будь ласка, введіть емейл";
    } else if (data.password.length === 0) {
        return "Будь ласка, введіть пароль";
    } else if (data.password.length < 5) {
        return "Пароль має містити хоча б 5 символів";
    }

    return null;
};

const signUpReq = (data) => {
    return fetch(`${API_URL_BASE}/users`, {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json",
        }
    }).then(res => res.json());
};

const displayError = (error) => {
    document.getElementById("form-errors").innerText = error;
};

document.getElementById("btn-signUp").addEventListener("click", () => {
    const data = getData();
    const error = validateData(data);

    if (error) {
        displayError(error);
    } else {
        displayError("");
        signUpReq(data).then(res => console.log(res));
    }
});
