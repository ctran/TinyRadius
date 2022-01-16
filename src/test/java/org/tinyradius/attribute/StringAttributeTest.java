package org.tinyradius.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringAttributeTest {

	@Test
	public void test() {
		final String username = "jdoe@example.com";
		final StringAttribute strAttr = new StringAttribute(1, username);
		//System.err.println(strAttr);
		Assertions.assertEquals(StringAttribute.class, strAttr.getAttributeTypeObject().getAttributeClass());
		Assertions.assertEquals("User-Name", strAttr.getAttributeTypeObject().getName());
		Assertions.assertEquals(username, strAttr.getAttributeValue());
	}

}
