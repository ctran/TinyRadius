package org.tinyradius.attribute;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tinyradius.dictionary.DefaultDictionary;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.MemoryDictionary;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class VariableDictionaryFormatTest {

    static MemoryDictionary defaultDictionary;

    @BeforeClass
    public static void prepareDictionary() {
        defaultDictionary = (MemoryDictionary) DefaultDictionary.getDefaultDictionary();
        ClassLoader classLoader = VariableDictionaryFormatTest.class.getClassLoader();

        try (InputStream in = classLoader.getResourceAsStream("org/tinyradius/dictionary/dictionary.starent");
             InputStream il = classLoader.getResourceAsStream("org/tinyradius/dictionary/dictionary.3gpp")) {
            DictionaryParser.parseDictionary(in, defaultDictionary);
            DictionaryParser.parseDictionary(il, defaultDictionary);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseDictionaryFormat() {
        assertEquals(2, defaultDictionary.getVendorLengthSize(8164));
        assertEquals(2, defaultDictionary.getVendorTypeSize(8164));
    }

    @Test
    public void parseDictionaryFormatDefault() {
        assertEquals(1, defaultDictionary.getVendorLengthSize(14122));
        assertEquals(1, defaultDictionary.getVendorTypeSize(14122));
    }

    @Test
    public void encodeDecode() throws IOException, RadiusException {
        String hexRadiusPacket = "04060217a72a40db2866fa5877f22a53154822e31f0e35323535353430303434393304060afffbfa2806000000011a1700001fe40120001132312e313920283738353431290606000000020706000000073d06000000121a0e00001fe4003e0008000000011a17000028af01113333343032303435373533363733301a0e000028af08083333343032301a09000028af0a03351a09000028af0c03301a0c000028af0206428b4cd91a27000028af052130352d313339323146373339364645464537343832464646463030353930301a0c000028af0d06304330301a0e000028af09083333343032301a09000028af1a03001a09000028af1503011a0a000028af17040a211a18000028af1412383636303138303530303333383932391a0c000028af0306000000002c12433835464130373734323842344344391a0c000028af0606c990c3601a0c000028af0706c85fa0771a0c00001fe40002000667691a0e00001fe4007b0008000000011a0c00001fe40068000667691a0e00001fe400920008000000011a1100001fe4012d000b64656661756c741a1500001fe400fa000f74656c63656c2d686f6d651a0e00001fe400690008000000023212433835464130373734323842344344390c06000005dc1a10000028af160a013304202f4c88921a0e000028af120833333430323037066173129508060aa175cc1a0e00001fe400180008000000040506002573b829060000000f";
        InputStream inputStream =  new ByteArrayInputStream(hexStringToByteArray(hexRadiusPacket));
        RadiusPacket radiusPacket = RadiusPacket.decodePacket(defaultDictionary, inputStream, "secret", null, RadiusPacket.UNDEFINED);

        System.out.println(radiusPacket);

        //validate String type
        assertEquals(radiusPacket.getAttribute(8164, 146).getAttributeValue(), "Enabled");
        assertEquals(radiusPacket.getAttribute(8164, 146).getAttributeTypeObject().getName(), "SN-Mediation-No-Interims");

        //validate String type
        assertEquals(radiusPacket.getAttribute(10415, 10).getAttributeValue(), "5");
        assertEquals(radiusPacket.getAttribute(10415, 10).getAttributeTypeObject().getName(), "3GPP-NSAPI");

        //validate ipaddr Type
        assertEquals(((IpAttribute)radiusPacket.getAttribute(10415, 6)).getAttributeValue(), "201.144.195.96");
        assertEquals(radiusPacket.getAttribute(10415, 6).getAttributeTypeObject().getName(), "3GPP-SGSN-Address");

        //validate integer Type
        assertEquals(((IntegerAttribute)radiusPacket.getAttribute(8164, 62)).getAttributeValue(), "GTP_VERSION_1");
        assertEquals(((IntegerAttribute)radiusPacket.getAttribute(8164, 62)).getAttributeValueInt(), 1);
        assertEquals(radiusPacket.getAttribute(8164, 62).getAttributeTypeObject().getName(), "SN-GTP-Version");

        //validate integer Type
        assertEquals(((IntegerAttribute)radiusPacket.getAttribute(10415, 2)).getAttributeValueInt(), 1116425433);
        assertEquals(radiusPacket.getAttribute(10415, 2).getAttributeTypeObject().getName(), "3GPP-Charging-ID");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        radiusPacket.encodeRequestPacket(outputStream, "secret");

        assertEquals(hexRadiusPacket, encodeHexString(outputStream.toByteArray()));
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }
}
