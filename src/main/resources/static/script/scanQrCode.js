const video = document.getElementById('video');
const canvas = document.getElementById('canvas');
const context = canvas.getContext('2d');
let stream;

document.getElementById('cameraOn').addEventListener('click', () => {
    document.getElementById('cameraOn').classList.add('notDisplayed');
    document.getElementById('cameraOff').classList.remove('notDisplayed');
    document.getElementById('viewText').classList.remove('notDisplayed');
    document.getElementById('viewVideo').classList.add('notDisplayed');
    stopCamera();
});
document.getElementById('cameraOff').addEventListener('click', () => {
    document.getElementById('cameraOff').classList.add('notDisplayed');
    document.getElementById('cameraOn').classList.remove('notDisplayed');
    document.getElementById('viewVideo').classList.remove('notDisplayed');
    document.getElementById('viewText').classList.add('notDisplayed');
    context.clearRect(0, 0, canvas.width, canvas.height);
    navigator.mediaDevices.getUserMedia({video: {facingMode: 'environment'}}).then((mediaStream) => {
        stream = mediaStream;
        video.srcObject = stream;
        video.play();

        video.addEventListener('play', () => {
            requestAnimationFrame(scanQRCode);
        });
    }).catch((err) => {
        console.error('Error accessing camera: ', err);
    });
});
document.getElementById('sendCheckInFirstOpen').addEventListener('click', () => {
    document.getElementById("allDone").classList.add("notDisplayed");
    document.getElementsByClassName("modal-body")[1].classList.remove("notDisplayed");
    document.getElementById('cameraOff').classList.add('notDisplayed');
    document.getElementById('cameraOn').classList.remove('notDisplayed');
    document.getElementById('viewVideo').classList.remove('notDisplayed');
    document.getElementById('viewText').classList.add('notDisplayed');
    navigator.mediaDevices.getUserMedia({
        video: {facingMode: 'environment'}
    }).then((mediaStream) => {
        stream = mediaStream;
        video.srcObject = stream;
        video.play();

        video.addEventListener('play', () => {
            requestAnimationFrame(scanQRCode);
        });
    }).catch((err) => {
        console.error('Error accessing camera: ', err);
    });
});
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
document.getElementById("sendWithText").addEventListener('click', () => {
    let text = document.getElementById("inputTextQrCode").value;
    sendData(text)
});

function scanQRCode() {
    context.drawImage(video, 0, 0, canvas.width, canvas.height);
    const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
    const code = jsQR(imageData.data, imageData.width, imageData.height);

    if (code) {
        sendData(code.data)
    } else {
        requestAnimationFrame(scanQRCode);
    }
}

function stopCamera() {
    if (stream) {
        const tracks = stream.getTracks();
        tracks.forEach(track => track.stop());
        video.srcObject = null;
    }
}

async function sendData(data) {
    try {
        data = data.trim().toUpperCase();
        if (data.length === 21 && data.startsWith("YUP_RES-")) {
            const response = await fetch("/reservation/checkQrCode", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    message: data,
                    eventID: vnt
                }),
            });

            if (!response.ok) {
                showErrorAlert('Generic error!');
                throw new Error(`HTTP error! Status: ${response.status}`);
                context.clearRect(0, 0, canvas.width, canvas.height);
                requestAnimationFrame(scanQRCode);
            }

            const responseData = await response.json();
            if (responseData === -2) {
                showErrorAlert('Reservation not found!');
                context.clearRect(0, 0, canvas.width, canvas.height);
                requestAnimationFrame(scanQRCode);
            }else if(responseData === -1){
                showErrorAlert('Reservation already validated!');
                context.clearRect(0, 0, canvas.width, canvas.height);
                requestAnimationFrame(scanQRCode);
            }else if(responseData === 0){
                document.getElementById("allDone").classList.remove("notDisplayed");
                document.getElementsByClassName("modal-body")[1].classList.add("notDisplayed");
                document.getElementById("inputTextQrCode").value = "";
                const path = document.getElementById('checkmark').querySelector('path');
                path.style.animation = 'none';
                path.getBoundingClientRect();
                path.style.animation = 'draw 1s linear forwards';
                context.clearRect(0, 0, canvas.width, canvas.height);
            }else{
                showErrorAlert('Generic error!');
                context.clearRect(0, 0, canvas.width, canvas.height);
                requestAnimationFrame(scanQRCode);
            }
        } else {
            showErrorAlert('This is not a valid reservation code of YUP!');
        }

    } catch (error) {
        console.error("Error:", error);
        showErrorAlert('An error occurred while processing the request.');
        throw error;
    }
}