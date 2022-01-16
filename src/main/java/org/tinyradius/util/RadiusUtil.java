/**
 * $Id: RadiusUtil.java,v 1.2 2006/11/06 19:32:06 wuttke Exp $
 * Created on 09.04.2005
 * @author Matthias Wuttke
 * @version $Revision: 1.2 $
 */
package org.tinyradius.util;

import java.nio.charset.StandardCharsets;

/**
 * This class contains miscellaneous static utility functions.
 */
public class RadiusUtil {

	/**
	 * Returns the passed string as a byte array containing the
	 * string in UTF-8 representation.
	 * @param str Java string
	 * @return UTF-8 byte array
	 */
	public static byte[] getUtf8Bytes(String str) {
		return str.getBytes(StandardCharsets.UTF_8);
	}
	
	/**
	 * Creates a string from the passed byte array containing the
	 * string in UTF-8 representation.
	 * @param utf8 UTF-8 byte array
	 * @return Java string
	 */
	public static String getStringFromUtf8(byte[] utf8) {
		return new String(utf8, StandardCharsets.UTF_8);
	}
	
	/**
	 * Returns the byte array as a hex string in the format
	 * "0x1234".
	 * @param data byte array
	 * @return hex string
	 */
	public static String getHexString(byte[] data) {
		StringBuilder hex = new StringBuilder("0x");
		if (data != null)
			for (byte datum : data) {
				String digit = Integer.toString(datum & 0x0ff, 16);
				if (digit.length() < 2)
					hex.append('0');
				hex.append(digit);
			}
		return hex.toString();
	}
	
}
