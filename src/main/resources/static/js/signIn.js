const signInForm = document.forms.signIn;

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
    }

    return null;
};

const signInReq = (data) => {
    return fetch(`${API_URL_BASE}/auth/signIn`, {
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

document.getElementById("btn-signIn").addEventListener("click", () => {
    const data = getData();
    const error = validateData(data);

    if (error) {
        displayError(error);
    } else {
        displayError("");

        signInReq(data).then(res => {
            if (res.error) {
                displayError(res.error);
            } else {
                document.location.href = 'http://localhost:9090/my-routes'
            }
        });
    }
});
