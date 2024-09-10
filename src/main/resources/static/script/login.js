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

typeWriter("Be ready for the new exclusive party.", 80);

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

        const username = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputUsername").value.trim())));
        document.querySelector("#inputUsername").value = username;
        const password = Utils.sanitizeInput(document.querySelector("#inputPassword").value);
        document.querySelector("#inputPassword").value = password;

        hasError = false;

        if (username === "") {
            hasError = true;
            showErrorAlert('The username cannot be empty.');
        } else if (grecaptcha.getResponse() === "") {
            hasError = true;
            showErrorAlert('Please verify that you are not a robot.');
        }

        if (hasError) {
            event.preventDefault();
        }
    });
});