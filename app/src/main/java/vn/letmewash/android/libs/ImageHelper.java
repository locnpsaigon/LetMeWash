package vn.letmewash.android.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by camel on 12/3/17.
 */

public class ImageHelper {

    /**
     * Decode image from base 64 string
     *
     * @param imageBase64
     * @return
     */
    public static Bitmap ImageFromBase64(String imageBase64) {
        try {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encode image to base 64 string
     *
     * @param bitmap
     * @return
     */
    public static String ImageToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] byteArrayImage = os.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            return encodedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
