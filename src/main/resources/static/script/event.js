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

const now = new Date();
const year = now.getFullYear();
const month = (now.getMonth() + 1).toString().padStart(2, '0');
const day = now.getDate().toString().padStart(2, '0');
const hours = now.getHours().toString().padStart(2, '0');
const minutes = now.getMinutes().toString().padStart(2, '0');
const datetimeLocalStr = `${year}-${month}-${day}T${hours}:${minutes}`;
document.getElementById('inputStartDateTime').value = datetimeLocalStr;
document.getElementById('inputEndDateTime').value = datetimeLocalStr;

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

const checkRoute = location => {
    const normalizeRoute = route => Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(route.trim())));
    const normalizedRoutes = suggestedRoutes.map(normalizeRoute);
    return normalizedRoutes.includes(location);
};

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".our-form");
    form.addEventListener('submit', (event) => {
        let hasError = false;

        const currentDate = new Date();

        let startDate = new Date(document.getElementById('inputStartDateTime').value);
        let endDate = new Date(document.getElementById('inputEndDateTime').value);

        const name = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputName").value.trim())));
        document.querySelector("#inputName").value = name;
        const location = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#searchBox").value.trim())));
        document.querySelector("#searchBox").value = location;
        const description = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputDescription").value.trim())));
        document.querySelector("#inputDescription").value = description;
        const participantsMaxNumber = document.querySelector("#inputMaxParticipants").value;


        if (event.submitter.id === 'submitButton') {
                hasError = false;

                if (!(name.length >= 5 && name.length <= 20)) {
                    hasError = true;
                    showErrorAlert('The name must be between 5 and 20 characters.');
                } else if (startDate < currentDate) {
                    hasError = true;
                    showErrorAlert('Start date and time must be in the future.');
                } else if (startDate > endDate) {
                    hasError = true;
                    showErrorAlert('End date and time must be after the start date and time.');
                } else if (!checkRoute(location)) {
                    hasError = true;
                    showErrorAlert('The location must be one of the suggested');
                } else if (!(description.length <= 200)) {
                    hasError = true;
                    showErrorAlert('The description must be less than 200 characters.');
                } else if (!(participantsMaxNumber > 1 && participantsMaxNumber < 5001)) {
                    hasError = true;
                    showErrorAlert('The number of participants must be between 2 and 5000.');
                } else if (!!document.querySelector("#inputImage").value && !Utils.isValidImage(document.querySelector("#inputImage"))) {
                    hasError = true;
                    showErrorAlert('The image must be a valid image and with a size under 5MB');
                }

                if (hasError) {
                    event.preventDefault();
                }
        }
    });
});


