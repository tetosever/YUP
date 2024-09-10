package app.YUP.utils;

import io.nayuki.qrcodegen.QrCode;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A utility class for generating QR codes.
 */
public class QRCodeService
{
    private QRCodeService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a QR code image from the provided text.
     *
     * @param barcodeText the text to be encoded in the QR code
     * @return a BufferedImage representing the generated QR code
     */
    public static BufferedImage generateQrcode(String barcodeText){
        QrCode qrCode = QrCode.encodeText(barcodeText, QrCode.Ecc.MEDIUM);
        return toImage(qrCode, 4, 10, 0xFFFFFF, 0x000000);
    }

    /**
     * Converts a QrCode object to a BufferedImage.
     *
     * @param qr the QrCode object to be converted
     * @param scale the scale factor for the QR code image
     * @param border the border size for the QR code image
     * @param lightColor the color to be used for the light modules of the QR code
     * @param darkColor the color to be used for the dark modules of the QR code
     * @return a BufferedImage representing the QR code
     * @throws IllegalArgumentException if scale is less than or equal to 0, or if border is less than 0, or if the calculated size exceeds Integer.MAX_VALUE
     */
    public static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale) {
            throw new IllegalArgumentException("Scale or border too large");
        }

        BufferedImage result = new BufferedImage(
                (qr.size + border * 2) * scale,
                (qr.size + border * 2) * scale,
                BufferedImage.TYPE_INT_RGB
        );
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }
}