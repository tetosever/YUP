function generateQRCode() {
    let text = code;
    let qrcodeContainer = document.getElementById("qr-code-image");
    qrcodeContainer.innerHTML = "";
    new QRCode(qrcodeContainer, {
        text: text,
        width: 100,
        height: 100
    });
    document.getElementById("qr-code-text").innerHTML = text;
}