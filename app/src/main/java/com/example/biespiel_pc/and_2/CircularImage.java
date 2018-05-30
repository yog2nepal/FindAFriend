package com.example.biespiel_pc.and_2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by Res Non Verba on 1/19/2018.
 */

public class CircularImage {
    public CircularImage() {
    }

    public Bitmap setCircularImage(Bitmap selectedImage){
        //Circular Image
        Bitmap outputPhoto = Bitmap.createBitmap(selectedImage.getWidth(),
                selectedImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputPhoto);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, selectedImage.getWidth(), selectedImage.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(selectedImage.getWidth() / 2, selectedImage.getHeight() / 2,
                selectedImage.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(selectedImage, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);

        return outputPhoto;
    }
}
