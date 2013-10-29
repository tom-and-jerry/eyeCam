package ch.hsr.eyecam.colormodel;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import ch.hsr.eyecam.Debug;

public class ColorRecognizer {
	private final int mHeight;
	private final int mWidth;
	private final byte[] mBuffer;

	public ColorRecognizer(byte[] buffer, int width, int height) {
		mBuffer = buffer;
		mWidth = width;
		mHeight = height;
	}

	public int[] getRgbAt(int x, int y) {
		Bitmap theImage = toBitmap();
		int length = 3;
		int xStart = Math.max(0, x - length);
		int xEnd = Math.min(mWidth - 1, x + length);
		int yStart = Math.max(0, y - length);
		int yEnd = Math.min(mHeight - 1, y + length);

		int r = 0;
		int g = 0;
		int b = 0;

		for (int x2 = xStart; x2 <= xEnd; x2++) {
			for (int y2 = yStart; y2 <= yEnd; y2++) {
				int colorAtPixel = theImage.getPixel(x2, y2);
				r += android.graphics.Color.red(colorAtPixel);
				g += android.graphics.Color.green(colorAtPixel);
				b += android.graphics.Color.blue(colorAtPixel);
			}
		}
		int pixelCount = (xEnd - xStart + 1) * (yEnd - yStart + 1);
		r /= pixelCount;
		g /= pixelCount;
		b /= pixelCount;

		Debug.msg("RGB from android: R:" + r + " G:" + g + " B:" + b);

		return new int[] { r, g, b };
	}

	private Bitmap toBitmap() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		YuvImage yuvImage = new YuvImage(mBuffer, ImageFormat.NV21, mWidth, mHeight, null);
		yuvImage.compressToJpeg(new Rect(0, 0, mWidth, mHeight), 100, out);
		byte[] imageBytes = out.toByteArray();
		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
	}
}
