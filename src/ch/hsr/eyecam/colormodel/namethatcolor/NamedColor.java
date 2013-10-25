/*
 *
 * +------------------------------------------------------------------------------------+
 * |                                   Name that Color                                  |
 * +------------------------------------------------------------------------------------+
 * | Originally JavaScript implementation by Chirag Mehta - http://chir.ag/projects/ntc |
 * | Ported to Java by Lukas Felber (IFS, www.ifs.hsr.ch)
 * |------------------------------------------------------------------------------------|
 *
 *
 * This script is released under the: Creative Commons License:
 * Attribution 2.5 http://creativecommons.org/licenses/by/2.5/
 *
 */
package ch.hsr.eyecam.colormodel.namethatcolor;

import java.util.Locale;

public class NamedColor extends Color {

	private final int colorNameResId;

	public NamedColor(String hexColor, int colorNameResId, Locale locale) {
		super(hexColor, locale);
		this.colorNameResId = colorNameResId;
	}

	// public String getColorName() {
	// return colorNameResId;
	// }

	public int getColorNameResId() {
		return colorNameResId;
	}

	@Override
	public String toString() {
		return super.toString() + " '" + colorNameResId + "'";
	}
}
