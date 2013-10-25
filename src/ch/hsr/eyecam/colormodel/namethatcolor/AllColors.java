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

		addColor("#000000", R.string.color_black, locale);
		addColor("#808080", R.string.color_grey, locale);
		addColor("#4D4D4D", R.string.color_dark_grey, locale);
		addColor("#FFFFFF", R.string.color_white, locale);
		addColor("#FF0000", R.string.color_red, locale);
		addColor("#855C33", R.string.color_brown, locale);
		addColor("#AB8F73", R.string.color_light_brown, locale);
		addColor("#9900FF", R.string.color_violet, locale);
		addColor("#FF9900", R.string.color_orange, locale);
		addColor("#FFFF00", R.string.color_yellow, locale);
		addColor("#0000FF", R.string.color_blue, locale);
		addColor("#3F4C69", R.string.color_gray_blue, locale);
		addColor("#7FBCFF", R.string.color_light_blue, locale);
		addColor("#00FF00", R.string.color_green, locale);
		addColor("#105210", R.string.color_dark_green, locale);
		addColor("#228b22", R.string.color_forest_green, locale);
		addColor("#30D5C8", R.string.color_turquoise, locale);
		addColor("#0000C8", R.string.color_dark_blue, locale);
		addColor("#5C0120", R.string.color_bordeaux, locale);
		addColor("#800000", R.string.color_maroon, locale);
		addColor("#4682b4", R.string.color_azure, locale);
		addColor("#BFFF00", R.string.color_lime, locale);
		addColor("#FFFDD0", R.string.color_cream, locale);
		addColor("#013F6A", R.string.color_regal_blue, locale);
		addColor("#008080", R.string.color_teal, locale);
		addColor("#00FFFF", R.string.color_cyan, locale);
		addColor("#220878", R.string.color_deep_blue, locale);
		addColor("#41AA78", R.string.color_ocean_green, locale);
		addColor("#4B0082", R.string.color_indigo, locale);
		addColor("#55280C", R.string.color_cioccolato, locale);
		addColor("#800080", R.string.color_purple, locale);
		addColor("#66FF00", R.string.color_bright_green, locale);
		addColor("#773F1A", R.string.color_walnut, locale);
		addColor("#808000", R.string.color_olive, locale);
		addColor("#831923", R.string.color_merlot, locale);
		addColor("#843179", R.string.color_plum, locale);
		addColor("#B7410E", R.string.color_rust, locale);
		addColor("#CAC6A3", R.string.color_light_yellow, locale);
		addColor("#DC143C", R.string.color_crimson, locale);
		addColor("#FF007F", R.string.color_rose, locale);
		addColor("#FFBF00", R.string.color_amber, locale);
		addColor("#FFC0CB", R.string.color_pink, locale);
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