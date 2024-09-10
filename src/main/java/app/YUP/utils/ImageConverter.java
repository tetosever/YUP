package app.YUP.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Base64;

/**
 * Utility class for converting BufferedImage to Base64 string.
 * This class cannot be instantiated.
 */
public class ImageConverter {

    /**
     * Private constructor to prevent instantiation of utility class.
     * @throws IllegalStateException if an attempt is made to instantiate this class.
     */
    private ImageConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts a BufferedImage to a Base64 string.
     * @param image the BufferedImage to be converted.
     * @return a Base64 string representation of the image.
     * @throws IOException if an error occurs during writing to the ByteArrayOutputStream.
     */
    public static String convertToBase64(BufferedImage image) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", baos);
        byte[] imageBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }
}