package org.tinyradius.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class IntegerAttributeTest {

	@Test
	public void test() {
		final IntegerAttribute intAttr = new IntegerAttribute(27, 0);
		final long bigValue = 0xffffffffL; // big value with the highest bit set
		final String bigValueSt = Long.toString(bigValue);
		intAttr.setAttributeValue(bigValueSt);
		//System.err.println(intAttr.getAttributeTypeObject().getName());
		Assertions.assertEquals(IntegerAttribute.class, intAttr.getAttributeTypeObject().getAttributeClass());
		Assertions.assertEquals("Session-Timeout", intAttr.getAttributeTypeObject().getName());
		Assertions.assertEquals(bigValueSt, intAttr.getAttributeValue());
	}

}
