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
import java.util.LinkedList;
import java.util.Locale;

import ch.hsr.eyecam.R;

public class AllColors implements Colors {

	private Collection<NamedColor> allColors;

	private void initColors(Locale locale) {
		if (allColors != null) {
			return;
		}
		allColors = new LinkedList<NamedColor>();

		// many of these color values have been adapted to work with cell-phones cameras (which are often optimized for much contrast -> colors gets rather darker)
		addColor("#000000", R.string.color_black, locale);
		addColor("#ADADAD", R.string.color_grey, locale);
		addColor("#F0F0F0", R.string.color_white, locale);
		addColor("#C2151C", R.string.color_red, locale);
		addColor("#B74343", R.string.color_red, locale);
		addColor("#774F1A", R.string.color_brown, locale);
		addColor("#BA6D2D", R.string.color_orange, locale);
		addColor("#E3B606", R.string.color_yellow, locale);
		addColor("#AF8756", R.string.color_yellow, locale);
		addColor("#0D1EAA", R.string.color_blue, locale);
		addColor("#44415E", R.string.color_gray_blue, locale);
		addColor("#7FBCFF", R.string.color_light_blue, locale);
		addColor("#11AB00", R.string.color_green, locale);
		addColor("#104E00", R.string.color_dark_green, locale);
		addColor("#228b22", R.string.color_forest_green, locale);
		addColor("#30D5C8", R.string.color_turquoise, locale);
		addColor("#800000", R.string.color_maroon, locale);
		addColor("#E2D9BA", R.string.color_cream, locale);
		addColor("#013F6A", R.string.color_regal_blue, locale);
		addColor("#00FFFF", R.string.color_cyan, locale);
		addColor("#220878", R.string.color_deep_blue, locale);
		addColor("#9317C1", R.string.color_indigo, locale);
		addColor("#9F7490", R.string.color_indigo, locale);
		addColor("#55280C", R.string.color_cioccolato, locale);
		addColor("#800080", R.string.color_purple, locale);
		addColor("#83D93F", R.string.color_bright_green, locale);
		addColor("#808000", R.string.color_olive, locale);
		addColor("#671718", R.string.color_merlot, locale);
		addColor("#B7410E", R.string.color_rust, locale);
		addColor("#DBBB7F", R.string.color_light_yellow, locale);
		addColor("#DC143C", R.string.color_crimson, locale);
		addColor("#CA195F", R.string.color_rose, locale);
		addColor("#D49C33", R.string.color_amber, locale);
		addColor("#D138BB", R.string.color_pink, locale);
	}

	private void addColor(String hexColor, int colorName, Locale locale) {
		allColors.add(new NamedColor(hexColor, colorName, locale));
	}

	@Override
	public Collection<NamedColor> getColors(Locale locale) {
		initColors(locale);
		return allColors;
	}
}