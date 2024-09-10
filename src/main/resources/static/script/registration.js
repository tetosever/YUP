import {Utils} from './utils.js';

function typeWriter(text, speed) {

    let index = 0;

    function typing() {
        if (index < text.length) {
            document.getElementById("typewriterText").innerHTML += text.charAt(index);
            index++;
            setTimeout(typing, speed);
        }
    }

    typing();
}

typeWriter("Remember, if it's a party it's YUP.", 80);

document.querySelectorAll(".alert > button").forEach(function (button) {
    button.addEventListener("click", function (event) {
        const alertDiv = document.querySelector('.alert');
        alertDiv.style.display = 'none';
    });
});

function showErrorAlert(message) {
    const alertDiv = document.querySelector('.alert');
    const alertText = document.querySelector('#alertText');
    alertText.textContent = message;
    alertDiv.style.display = 'block';
}

document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector(".our-form");
    form.addEventListener('submit', function (event) {
        let hasError = false;

        const firstName = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputFirstname").value.trim())));
        document.querySelector("#inputFirstname").value = firstName;
        const lastName = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputLastname").value.trim())));
        document.querySelector("#inputLastname").value = lastName;
        const email = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputEmail").value.trim())));
        document.querySelector("#inputEmail").value = email;
        const username = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputUsername").value.trim())));
        document.querySelector("#inputUsername").value = username;
        const password = Utils.sanitizeInput(document.querySelector("#inputPassword").value);
        document.querySelector("#inputPassword").value = password;
        const confirmPassword = Utils.sanitizeInput(document.querySelector("#inputCheckPassword").value);
        document.querySelector("#inputCheckPassword").value = confirmPassword;

        hasError = false;

        if (firstName === "") {
            hasError = true;
            showErrorAlert('The first name cannot be empty.');
        } else if (lastName === "") {
            hasError = true;
            showErrorAlert('The last name cannot be empty.');
        } else if (!Utils.isEmail(email)) {
            hasError = true;
            showErrorAlert('The email address is not valid.');
        } else if (username === "") {
            hasError = true;
            showErrorAlert('The username cannot be empty.');
        } else if(username !== ""){
                const xhr = new XMLHttpRequest();
                xhr.open("POST", "/user/checkIfUserExist", false); // Note the third argument here for synchronous request
                xhr.setRequestHeader('Content-Type', 'application/json');

                try {
                    xhr.send(JSON.stringify({ message: username }));

                    if (xhr.status !== 200) {
                        throw new Error(`HTTP error! Status: ${xhr.status}`);
                    }

                    const responseData = JSON.parse(xhr.responseText);
                    if (responseData) {
                        hasError = true;
                        showErrorAlert('The username already exists.');
                    }
                } catch (error) {
                    console.error('Error checking username:', error);
                    showErrorAlert('Generic error.');
                    hasError = true;
                }
        } else if (!Utils.isPasswordStrong(password)) {
            hasError = true;
            showErrorAlert('The password must be at least 8 characters long and contain at least one letter and one number.');
        } else if (confirmPassword !== password) {
            hasError = true;
            showErrorAlert('The passwords do not match.');
        } else if (grecaptcha.getResponse() === "") {
            hasError = true;
            showErrorAlert('Please verify that you are not a robot.');
        }

        if (hasError) {
            event.preventDefault();
        }
    });
});