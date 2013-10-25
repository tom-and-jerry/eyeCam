/*
 *
 * +------------------------------------------------------------------------------------+
 * |                                   Name that Color                                  |
 * +------------------------------------------------------------------------------------+
 * | Originally JavaScript implementation by Chirag Mehta - http://chir.ag/projects/ntc |
 * | Ported to Java by Lukas Felber (IFS, www.ifs.hsr.ch)
 * |------------------------------------------------------------------------------------|
 *
 * This code is released under the: Creative Commons License:
 * Attribution 2.5 http://creativecommons.org/licenses/by/2.5/
 *
 */
package ch.hsr.eyecam.colormodel.namethatcolor;

import java.util.Arrays;
import java.util.Locale;

public class Color {

	private int[] rgb;
	private int[] hsl;

	public Color(String hexColor, Locale locale) {
		checkArgs(hexColor, locale);
		initRgb(hexColor, locale);
		initHsl();
	}

	private void checkArgs(String hexColor, Locale locale) {
		if (hexColor.length() != 7 || !hexColor.startsWith("#")) {
			throw new IllegalArgumentException("Color must be initialized with a hex color string (e.g. '#FF0000').");
		}
		hexColor = hexColor.substring(1);
		try {
			int color = Math.abs(Integer.parseInt(hexColor, 16));
			String hexColorAgain = String.format("%6x", color).replace(' ', '0');
			if (!hexColorAgain.equals(hexColor.toLowerCase(locale))) {
				throw new IllegalArgumentException("Color must be initialized with a hex color string (e.g. '#FF0000').");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Color must be initialized with a hex color string (e.g. '#FF0000').");
		}
	}

	private void initRgb(String hexColor, Locale locale) {
		hexColor = hexColor.substring(1).toLowerCase(locale);
		int r = Integer.parseInt(hexColor.substring(0, 2), 16);
		int g = Integer.parseInt(hexColor.substring(2, 4), 16);
		int b = Integer.parseInt(hexColor.substring(4), 16);
		rgb = new int[] { r, g, b };
	}

	private void initHsl() {
		double r = rgb[0] / 255.0;
		double g = rgb[1] / 255.0;
		double b = rgb[2] / 255.0;

		double min = Math.min(r, Math.min(g, b));
		double max = Math.max(r, Math.max(g, b));
		double delta = max - min;
		double l = (min + max) / 2;

		double s = 0;
		if (l > 0 && l < 1) {
			s = delta / (l < 0.5 ? (2 * l) : (2 - 2 * l));
		}

		double h = 0;
		if (delta > 0) {
			if (max == r && max != g) {
				h += (g - b) / delta;
			}
			if (max == g && max != b) {
				h += (2 + (b - r) / delta);
			}
			if (max == b && max != r) {
				h += (4 + (r - g) / delta);
			}
			h /= 6;
		}
		hsl = new int[] { (int) (h * 255.0), (int) (s * 255.0), (int) (l * 255.0) };
	}

	public String getHexColor() {
		return String.format("#%2x%2x%2x", getR(), getG(), getB()).replaceAll(" ", "0");
	}

	/**
	 * @return returns the red part of the RGB color
	 */
	public int getR() {
		return rgb[0];
	}

	/**
	 * @return returns the green part of the RGB color
	 */
	public int getG() {
		return rgb[1];
	}

	/**
	 * @return returns the blue part of the RGB color
	 */
	public int getB() {
		return rgb[2];
	}

	/**
	 * @return returns the hue part of the HSL color
	 */
	public int getH() {
		return hsl[0];
	}

	/**
	 * @return returns the saturation part of the HSL color
	 */
	public int getS() {
		return hsl[1];
	}

	/**
	 * @return returns the lightness part of the HSL color
	 */
	public int getL() {
		return hsl[2];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + Arrays.hashCode(rgb);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Color other = (Color) obj;
		if (!Arrays.equals(rgb, other.rgb)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getHexColor();
	}
}
