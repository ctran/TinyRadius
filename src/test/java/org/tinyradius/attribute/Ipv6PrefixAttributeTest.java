package org.tinyradius.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ipv6PrefixAttributeTest {

	@Test
	public void test() {
		final String testIpv6Prefix = "2001:db8:85a3::/56";
		final Ipv6PrefixAttribute ipv6PrefixAttr = new Ipv6PrefixAttribute(123, testIpv6Prefix);
		//System.err.println(ipv6Prefix);
		Assertions.assertEquals(Ipv6PrefixAttribute.class, ipv6PrefixAttr.getAttributeTypeObject().getAttributeClass());
		Assertions.assertEquals("Delegated-IPv6-Prefix", ipv6PrefixAttr.getAttributeTypeObject().getName());
		Assertions.assertEquals(testIpv6Prefix, ipv6PrefixAttr.getAttributeValue());
	}

}
