/**
 * Created on 24/Jun/2016
 * @author Ivan F. Martinez
 */
package org.tinyradius.attribute;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.AddressValueException;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv6.IPv6Address;
import org.tinyradius.util.RadiusException;

import java.util.Arrays;

/**
 * This class represents a Radius attribute for an IPv6 prefix.
 */
public class Ipv6PrefixAttribute extends RadiusAttribute {

	/**
	 * Constructs an empty IP attribute.
	 */
	public Ipv6PrefixAttribute() {
		super();
	}
	
	/**
	 * Constructs an IPv6 prefix attribute.
	 * @param type attribute type code
	 * @param value value, format: "ipv6 address"/prefix
	 */
	public Ipv6PrefixAttribute(int type, String value) {
		setAttributeType(type);
		setAttributeValue(value);
	}
	
	/**
	 * Returns the attribute value (IP number) as a string of the
	 * format "xx.xx.xx.xx".
	 * @see org.tinyradius.attribute.RadiusAttribute#getAttributeValue()
	 */
	public String getAttributeValue() {
		final byte[] data = getAttributeData();
		if (data == null)	throw new RuntimeException("ipv6 prefix attribute: expected 2-18 bytes attribute data and got null.");
		if (data.length < 2 || data.length > 18)
			throw new RuntimeException("ipv6 prefix attribute: expected 2-18 bytes attribute data and got " + data.length);
		try {
			final int prefixSize = (data[1] & 0xff);
			byte[] prefix = Arrays.copyOfRange(data,2,data.length);
			if(prefix.length < 16) {
				// Pad w/ trailing 0's if length not 128 bits (IPv6Address will pad w/ leading 0's if less than 128 bits)
				prefix = Arrays.copyOf(prefix, 16);
			}
			final IPv6Address ipv6prefix = new IPv6Address(prefix, prefixSize);
			return ipv6prefix.toString();
		} catch (AddressValueException e) {
			throw new IllegalArgumentException("bad IPv6 prefix", e);
		}
		
	}
	
	/**
	 * Sets the attribute value (IPv6 number/prefix). String format:
	 * ipv6 address.
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 * @see org.tinyradius.attribute.RadiusAttribute#setAttributeValue(java.lang.String)
	 */
	public void setAttributeValue(String value) {
		if (value == null || value.length() < 3)
			throw new IllegalArgumentException("bad IPv6 address : " + value);
		try {
			IPAddressString ipAddressString = new IPAddressString(value);
			if( !ipAddressString.isIPAddress() || !ipAddressString.isIPv6() )
				throw new IllegalArgumentException("bad IPv6 address : " + value);

			IPv6Address ipv6Prefix = ipAddressString.toAddress().toIPv6();

			final byte[] data = new byte[18];
			data[0] = 0;
			data[1] = (byte) (ipv6Prefix.getPrefixLength() & 0xff);

			byte[] ipData = ipv6Prefix.getNetworkSection().getBytes();
			for (int i = 0; i < ipData.length; i++) {
				data[i + 2] = ipData[i];
			}

			setAttributeData(data);

		} catch (AddressStringException e) {
			throw new IllegalArgumentException("bad IPv6 address : " + value, e);
		}
	}
	

	/**
	 * Check attribute length.
	 * @see org.tinyradius.attribute.RadiusAttribute#readAttribute(byte[], int, int)
	 */
	public void readAttribute(byte[] data, int offset, int attrType, int attrLen, int attrTypeSize, int attrLenSize)
	throws RadiusException {
		if (attrLen > 20 + attrTypeSize + attrLenSize || attrLen < 4 + attrTypeSize + attrLenSize)
			throw new RadiusException("IPv6 prefix attribute: expected 4-20 bytes data");
		super.readAttribute(data, offset, attrType, attrLen, attrTypeSize, attrLenSize);
	}

}
