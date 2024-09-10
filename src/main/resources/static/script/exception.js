import {Utils} from "./utils";

const dino = document.getElementById("dino");
const cactus = document.getElementById("cactus");
let ok = false;
let modalOpen = false;

function jump() {
    if (dino.classList != "jump") {
        dino.classList.add("jump");
        setTimeout(function () {
            dino.classList.remove("jump");
        }, 300);
    }
}

document.querySelectorAll(".alert > button").forEach(function (button) {
    button.addEventListener("click", function (event) {
        const alertDiv = document.querySelector('.alert');
        alertDiv.style.display = 'none';
    });
});

let isAlive = setInterval(function () {
    if (ok) {
        let dinoTop = parseInt(window.getComputedStyle(dino).getPropertyValue("top"));
        let cactusLeft = parseInt(window.getComputedStyle(cactus).getPropertyValue("left"));

        if (cactusLeft < 40 && cactusLeft > 0 && dinoTop >= 140) {
            showErrorAlert("There's no peace for the YUPPERs! What do you think about returning to YUP?");
            ok = false;
            cactus.classList.remove("animation");
        }
    }
}, 10);

function showErrorAlert(message) {
    const alertDiv = document.querySelector('.alert');
    const alertText = document.querySelector('#alertText');
    alertText.textContent = message;
    alertDiv.style.display = 'block';
}

document.addEventListener("keydown", function (event) {
    if (!modalOpen) {
        if (ok) {
            jump();
        } else {
            if (window.innerWidth >= 800) {
                ok = true;
                cactus.classList.add("animation");
            }
        }
    }
});

function sendEmail() {
    postData();
}

function gatherBrowserInformation() {
    let info = "";

    // Basic browser information
    info += `The browser name is ${navigator.appName} with the codename ${navigator.appCodeName}. The current version of the browser is ${navigator.appVersion}. `;
    info += `Cookies are ${navigator.cookieEnabled ? "enabled" : "disabled"}.\n\n`;

    // Browser engine and user agent
    info += `The browser is running on the ${navigator.platform} platform. The User Agent of the browser is: ${navigator.userAgent}. `;
    info += `The product is ${navigator.product} and the product version is ${navigator.productSub}.\n\n`;

    // Vendor information
    info += `The browser vendor is ${navigator.vendor} and the vendor version is ${navigator.vendorSub}.\n\n`;

    // Language
    info += `The main language of the browser is ${navigator.language} and it supports the following languages: ${navigator.languages.join(", ")}.\n\n`;

    // Hardware information
    info += `The browser supports ${navigator.hardwareConcurrency} hardware threads. `;
    info += `The maximum number of touch points supported is ${navigator.maxTouchPoints}.\n\n`;

    // Device memory
    if (navigator.deviceMemory) {
        info += `The device memory is ${navigator.deviceMemory}GB.\n\n`;
    }

    // Network information
    if (navigator.connection) {
        info += `The effective connection type is ${navigator.connection.effectiveType}. `;
        info += `The downlink speed is ${navigator.connection.downlink} Mbps and the round-trip time (RTT) is ${navigator.connection.rtt} ms. `;
        info += `The data saver mode is ${navigator.connection.saveData ? "enabled" : "disabled"}.\n\n`;
    }

    return info;
}

const postData = async () => {
    const statusText = status ? status : '';
    const exceptionText = exception ? exception : '';
    const messageText = message ? message : '';
    const url = "/sendErrorEmail";
    const desc = Utils.normalizeWhitespace(Utils.toLowerCase(Utils.sanitizeInput(document.querySelector("#inputDescription").value.trim())));
    document.querySelector("#inputDescription").value = desc;

    const mex = `The day/hours ${new Date().toLocaleString()} an error was encountered in YUP.<br><br>` +
        `It was a ${statusText} error (${exceptionText}) with this specific message: ${messageText}.<br><br>` +
        `Device information.<br>${gatherBrowserInformation()}<br><br>` +
        `The user added this information.<br>${desc}`;

    try {
        await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: mex }),
        });
    } catch (error) {
        console.error("Error:", error);
    }
};

const modal = document.getElementById('sendEmailToYupTeamModal');
modal.addEventListener('shown.bs.modal', function () {
    modalOpen = true;
    ok = false;
    cactus.classList.remove("animation");
});

modal.addEventListener('hidden.bs.modal', function () {
    modalOpen = false;
});
