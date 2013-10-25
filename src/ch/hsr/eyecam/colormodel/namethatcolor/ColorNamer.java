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

import java.util.Collection;
import java.util.Locale;

public class ColorNamer {

	private final Colors colors;
	private Collection<NamedColor> colorsList;
	private final Locale locale;

	public ColorNamer(Colors allColors, Locale locale) {
		this.colors = allColors;
		this.locale = locale;
	}

	public NamedColor findClosestColor(String hexColor) {
		Color color = new Color(hexColor, locale);

		NamedColor bestMatch = null;
		double previousDiff = Double.MAX_VALUE;

		for (NamedColor candidate : getColors()) {
			if (color.equals(candidate)) {
				return candidate;
			}
			double diff = getRgbDiff(color, candidate) + getHslDiff(color, candidate) * 2;
			if (diff < previousDiff) {
				previousDiff = diff;
				bestMatch = candidate;
			}
		}
		return bestMatch;
	}

	private double getHslDiff(Color color, NamedColor candidate) {
		double hDiff = Math.pow((color.getH() - candidate.getH()), 2);
		double sDiff = Math.pow((color.getS() - candidate.getS()), 2);
		double lDiff = Math.pow((color.getL() - candidate.getL()), 2);
		double ndf2 = hDiff + sDiff + lDiff;
		return ndf2;
	}

	private double getRgbDiff(Color color, NamedColor candidate) {
		double rDiff = Math.pow((color.getR() - candidate.getR()), 2);
		double gDiff = Math.pow(color.getG() - candidate.getG(), 2);
		double bDiff = Math.pow(color.getB() - candidate.getB(), 2);
		double ndf1 = rDiff + gDiff + bDiff;
		return ndf1;
	}

	private Collection<NamedColor> getColors() {
		if (colorsList == null) {
			colorsList = colors.getColors(locale);
		}
		return colorsList;
	}
}
