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

typeWriter("Stand out at YUPs, check your data.", 80);

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

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('editButton').addEventListener('click', function() {
        let inputs = document.querySelectorAll('.input-data input');

        if (document.getElementById('inputIsInternal').value === 'true') {
            inputs.forEach(function(input) {
                if (input !== document.getElementById('inputPassword')) {
                    input.disabled = false;
                }
                document.getElementById('changePasswordButton').style.display = 'inline-block';
            });
        } else {
            inputs.forEach(function(input) {
                if (input !== document.getElementById('inputPassword') &&
                    input !== document.getElementById('inputEmail')) {
                    input.disabled = false;
                }
            });
        }

        document.getElementById('editButton').style.display = 'none';
        document.getElementById('deleteButton').style.display = 'none';
        document.getElementById('discardButton').style.display = 'inline-block';
        document.getElementById('confirmUpdateButton').style.display = 'inline-block';
    });

    document.getElementById('discardButton').addEventListener('click', function(event) {
        event.preventDefault();
        window.location.href = 'viewUser';
    });


    document.getElementById('confirmUpdateButton').addEventListener('click', function() {
        let hasError = false;

        const firstName = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputFirstname").value.trim())));
        document.querySelector("#inputFirstname").value = firstName;
        const lastName = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputLastname").value.trim())));
        document.querySelector("#inputLastname").value = lastName;
        const email = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputEmail").value.trim())));
        document.querySelector("#inputEmail").value = email;
        const username = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputUsername").value.trim())));
        document.querySelector("#inputUsername").value = username;

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
        }

        if (hasError) {
            event.preventDefault();
        }else {
            let form = document.getElementById('profileForm');
            form.action = 'api/update';
        }
    });

    const formModal = document.querySelector(".modal-content");
    formModal.addEventListener('submit', (event) =>  {
        let hasError = false;
        const oldPassword = Utils.sanitizeInput(document.querySelector("#oldPassword").value);
        document.querySelector("#oldPassword").value = oldPassword;
        const newPassword = Utils.sanitizeInput(document.querySelector("#newPassword").value);
        document.querySelector("#newPassword").value = newPassword;
        const confirmPassword = Utils.sanitizeInput(document.querySelector("#confirmNewPassword").value);
        document.querySelector("#confirmNewPassword").value = confirmPassword;

        if(oldPassword === ""){
            hasError = true;
            showErrorAlert('The old password is empty.');
        }else if (!newPassword) {
            hasError = true;
            showErrorAlert('The new password is empty.');
        }else if(!Utils.isPasswordStrong(newPassword)){
            hasError = true;
            showErrorAlert('The new password is not strong enough.');
        } if(newPassword !== confirmPassword){
            hasError = true;
            showErrorAlert('The passwords does not match');
        }else if(oldPassword === newPassword){
            hasError = true;
            showErrorAlert('The new password is like the old one');
        }

        if (hasError) {
            event.preventDefault();
        }
    });
});
