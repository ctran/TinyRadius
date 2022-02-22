/**
 * $Id: RadiusAttribute.java,v 1.4 2006/02/20 23:37:38 wuttke Exp $
 * Created on 07.04.2005
 * Released under the terms of the LGPL
 * 
 * @author Matthias Wuttke
 * @version $Revision: 1.4 $
 */
package org.tinyradius.attribute;

import org.tinyradius.dictionary.AttributeType;
import org.tinyradius.dictionary.DefaultDictionary;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusUtil;

/**
 * This class represents a generic Radius attribute. Subclasses implement
 * methods to access the fields of special attributes.
 */
public class RadiusAttribute {

	/**
	 * Constructs an empty Radius attribute.
	 */
	public RadiusAttribute() {
	}

	/**
	 * Constructs a Radius attribute with the specified
	 * type and data.
	 * 
	 * @param type
	 *            attribute type, see AttributeTypes.*
	 * @param data
	 *            attribute data
	 */
	public RadiusAttribute(int type, byte[] data) {
		setAttributeType(type);
		setAttributeData(data);
	}

	/**
	 * Returns the data for this attribute.
	 * 
	 * @return attribute data
	 */
	public byte[] getAttributeData() {
		return attributeData;
	}

	/**
	 * Sets the data for this attribute.
	 * 
	 * @param attributeData
	 *            attribute data
	 */
	public void setAttributeData(byte[] attributeData) {
		if (attributeData == null)
			throw new NullPointerException("attribute data is null");
		this.attributeData = attributeData;
	}

	/**
	 * Returns the type of this Radius attribute.
	 * 
	 * @return type code, 0-255
	 */
	public int getAttributeType() {
		return attributeType;
	}

	/**
	 * Sets the type of this Radius attribute.
	 * 
	 * @param attributeType
	 *            type code, 0-65535
	 */
	public void setAttributeType(int attributeType) {
		if (attributeType < 0 || attributeType > 65535)
			throw new IllegalArgumentException("attribute type invalid: " + attributeType);
		this.attributeType = attributeType;
	}

	/**
	 * Sets the value of the attribute using a string.
	 * 
	 * @param value
	 *            value as a string
	 */
	public void setAttributeValue(String value) {
		throw new RuntimeException("cannot set the value of attribute " + attributeType + " as a string");
	}

	/**
	 * Gets the value of this attribute as a string.
	 * 
	 * @return value
	 */
	public String getAttributeValue() {
		return RadiusUtil.getHexString(getAttributeData());
	}

	/**
	 * Gets the Vendor-Id of the Vendor-Specific attribute this
	 * attribute belongs to. Returns -1 if this attribute is not
	 * a sub attribute of a Vendor-Specific attribute.
	 * 
	 * @return vendor ID
	 */
	public int getVendorId() {
		return vendorId;
	}

	/**
	 * Sets the Vendor-Id of the Vendor-Specific attribute this
	 * attribute belongs to. The default value of -1 means this attribute
	 * is not a sub attribute of a Vendor-Specific attribute.
	 * 
	 * @param vendorId
	 *            vendor ID
	 */
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	/**
	 * Returns the dictionary this Radius attribute uses.
	 * 
	 * @return Dictionary instance
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * Sets a custom dictionary to use. If no dictionary is set,
	 * the default dictionary is used.
	 * 
	 * @param dictionary
	 *            Dictionary class to use
	 * @see DefaultDictionary
	 */
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Returns this attribute encoded as a byte array.
	 * 
	 * @return attribute
	 */
	public byte[] writeAttribute() {
		if (getAttributeType() == -1)
			throw new IllegalArgumentException("attribute type not set");
		if (attributeData == null)
			throw new NullPointerException("attribute data not set");

		byte[] attr = new byte[2 + attributeData.length];
		attr[0] = (byte) getAttributeType();
		attr[1] = (byte) (2 + attributeData.length);
		System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
		return attr;
	}

	/**
	 * Returns this attribute encoded as a byte array.
	 *
	 * @return attribute
	 */
	public byte[] writeAttribute(int attrTypeSize, int attrLenSize) {
		if (getAttributeType() == -1)
			throw new IllegalArgumentException("attribute type not set");
		if (attributeData == null)
			throw new NullPointerException("attribute data not set");

		byte[] attr = new byte[attrTypeSize + attrLenSize + attributeData.length];
		System.arraycopy(toByteArray(getAttributeType(), attrTypeSize), 0, attr, 0, attrTypeSize);
		System.arraycopy(toByteArray(attrTypeSize + attrLenSize + attributeData.length, attrLenSize), 0, attr, attrTypeSize, attrLenSize);
		System.arraycopy(attributeData, 0, attr, attrTypeSize + attrLenSize, attributeData.length);
		return attr;
	}

	/**
	 * Reads in this attribute from the passed byte array.
	 * @param data  data buffer
	 * @param offset the offset to read
	 * @param length the amount of data to read
	 * @throws RadiusException when length is less than 2
	 */
	public void readAttribute(byte[] data, int offset, int length) throws RadiusException {
		if (length < 2)
			throw new RadiusException("attribute length too small: " + length);
		int attrType = data[offset] & 0x0ff;
		int attrLen = data[offset + 1] & 0x0ff;
		byte[] attrData = new byte[attrLen - 2];
		System.arraycopy(data, offset + 2, attrData, 0, attrLen - 2);
		setAttributeType(attrType);
		setAttributeData(attrData);
	}

	public void readAttribute(byte[] data, int offset, int attrType, int attrLen, int attrTypeSize, int attrLenSize) throws RadiusException {
		if (attrLen < 2)
			throw new RadiusException("attribute length too small: " + attrLen);
		byte[] attrData = new byte[attrLen - attrTypeSize - attrLenSize];
		System.arraycopy(data, offset + attrTypeSize + attrLenSize, attrData, 0, attrLen - attrTypeSize - attrLenSize);
		setAttributeType(attrType);
		setAttributeData(attrData);
	}

	/**
	 * String representation for debugging purposes.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String name;

		// determine attribute name
		AttributeType at = getAttributeTypeObject();
		if (at != null)
			name = at.getName();
		else if (getVendorId() != -1)
			name = "Unknown-Sub-Attribute-" + getAttributeType();
		else
			name = "Unknown-Attribute-" + getAttributeType();

		// indent sub attributes
		if (getVendorId() != -1)
			name = "  " + name;

		return name + ": " + getAttributeValue();
	}

	/**
	 * Retrieves an AttributeType object for this attribute.
	 * 
	 * @return AttributeType object for (sub-)attribute or null
	 */
	public AttributeType getAttributeTypeObject() {
		if (getVendorId() != -1) {
			return dictionary.getAttributeTypeByCode(getVendorId(), getAttributeType());
		}
		return dictionary.getAttributeTypeByCode(getAttributeType());
	}

	/**
	 * Creates a RadiusAttribute object of the appropriate type.
	 * 
	 * @param dictionary
	 *            Dictionary to use
	 * @param vendorId
	 *            vendor ID or -1
	 * @param attributeType
	 *            attribute type
	 * @return RadiusAttribute object
	 */
	public static RadiusAttribute createRadiusAttribute(Dictionary dictionary, int vendorId, int attributeType) {
		RadiusAttribute attribute = new RadiusAttribute();

		AttributeType at = dictionary.getAttributeTypeByCode(vendorId, attributeType);
		if (at != null && at.getAttributeClass() != null) {
			try {
				attribute = (RadiusAttribute) at.getAttributeClass().newInstance();
			}
			catch (Exception e) {
				// error instantiating class - should not occur
			}
		}

		attribute.setAttributeType(attributeType);
		attribute.setDictionary(dictionary);
		attribute.setVendorId(vendorId);
		return attribute;
	}

	/**
	 * Creates a Radius attribute, including vendor-specific
	 * attributes. The default dictionary is used.
	 * 
	 * @param vendorId
	 *            vendor ID or -1
	 * @param attributeType
	 *            attribute type
	 * @return RadiusAttribute instance
	 */
	public static RadiusAttribute createRadiusAttribute(int vendorId, int attributeType) {
		Dictionary dictionary = DefaultDictionary.getDefaultDictionary();
		return createRadiusAttribute(dictionary, vendorId, attributeType);
	}

	/**
	 * Creates a Radius attribute. The default dictionary is
	 * used.
	 * 
	 * @param attributeType
	 *            attribute type
	 * @return RadiusAttribute instance
	 */
	public static RadiusAttribute createRadiusAttribute(int attributeType) {
		Dictionary dictionary = DefaultDictionary.getDefaultDictionary();
		return createRadiusAttribute(dictionary, -1, attributeType);
	}

	private static byte[] toByteArray(int value, int len) {
		switch (len) {
			case 1:
				return new byte[] {(byte) value};
			case 2:
				return new byte[] {(byte) (value >> 8), (byte) value};
			case 4:
				return new byte[] {(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
			default:
				throw new IllegalArgumentException("integer can only be [1,2,4] bytes in size");
		}
	}

	/**
	 * Dictionary to look up attribute names.
	 */
	private Dictionary dictionary = DefaultDictionary.getDefaultDictionary();

	/**
	 * Attribute type
	 */
	private int attributeType = -1;

	/**
	 * Vendor ID, only for sub-attributes of Vendor-Specific attributes.
	 */
	private int vendorId = -1;

	/**
	 * Attribute data
	 */
	private byte[] attributeData = null;

}
