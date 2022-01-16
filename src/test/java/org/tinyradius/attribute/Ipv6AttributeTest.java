package org.tinyradius.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ipv6AttributeTest {

	@Test
	public void test() {
		final String testIpv6Addr = "2001:db8:85a3:0:0:0:0:1234";
		final Ipv6Attribute ipv6Attr = new Ipv6Attribute(168, testIpv6Addr);
		Assertions.assertEquals(Ipv6Attribute.class, ipv6Attr.getAttributeTypeObject().getAttributeClass());
		Assertions.assertEquals("Framed-IPv6-Address", ipv6Attr.getAttributeTypeObject().getName());
		Assertions.assertEquals(testIpv6Addr, ipv6Attr.getAttributeValue());
	}

}
