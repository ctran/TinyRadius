package org.tinyradius.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IpAttributeTest {

	@Test
	public void test() {
		final IpAttribute ipAttr = new IpAttribute(8,"192.168.1.2");
		//System.err.println(ipAttr.getAttributeTypeObject().getName());

		Assertions.assertEquals(IpAttribute.class, ipAttr.getAttributeTypeObject().getAttributeClass());
		Assertions.assertEquals("Framed-IP-Address", ipAttr.getAttributeTypeObject().getName());
		Assertions.assertEquals(3232235778L, ipAttr.getIpAsLong());
	}

}
