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

public interface Colors {

	public Collection<NamedColor> getColors(Locale locale);
}
