package org.tinyradius.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VendorSpecificAttributeTest {

	@Test
	public void test() {
		final String vsaName = "WISPr-Location-Name";
		final String vsaValue = "somewhere";
		VendorSpecificAttribute vsa = new VendorSpecificAttribute(14122);
		vsa.addSubAttribute(vsaName, vsaValue);
		//System.err.println(vsa);
		Assertions.assertEquals(VendorSpecificAttribute.class, vsa.getAttributeTypeObject().getAttributeClass());
		Assertions.assertEquals("Vendor-Specific", vsa.getAttributeTypeObject().getName());
		Assertions.assertEquals(vsaValue, vsa.getSubAttributeValue(vsaName));
	}

}
