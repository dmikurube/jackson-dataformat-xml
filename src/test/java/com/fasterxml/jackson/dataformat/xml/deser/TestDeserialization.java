package com.fasterxml.jackson.dataformat.xml.deser;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlTestBase;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class TestDeserialization extends XmlTestBase
{
    static class AttributeBean
    {
        @JacksonXmlProperty(isAttribute=true, localName="attr")
        public String text = "?";
    }

    static class Optional {
        @JacksonXmlText
        public String number = "NOT SET";
        public String type = "NOT SET";
    }

    static class Immutable {
        @JsonCreator
        public Immutable(@JacksonXmlProperty(localName = "text")
                         String textInternal,
                         @JacksonXmlElementWrapper(localName = "labelss")
                         @JacksonXmlProperty(localName = "labels")
                         String[] labelsInternal) {
            System.out.println("!");
            System.out.println(textInternal);
            System.out.println(labelsInternal);
            System.out.println("!");
            this.textInternal = textInternal;
            this.labelsInternal = labelsInternal;
        }

        @JacksonXmlProperty(localName = "text")
        public String getText() { return textInternal; }
        @JacksonXmlProperty(localName = "labels")
        public String[] getLabels() { return labelsInternal; }

        private final String textInternal;
        private final String[] labelsInternal;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final XmlMapper MAPPER = new XmlMapper();

    /**
     * Unit test to ensure that we can successfully also round trip
     * example Bean used in Jackson tutorial
     */
    public void testRoundTripWithJacksonExample() throws Exception
    {
        FiveMinuteUser user = new FiveMinuteUser("Joe", "Sixpack",
                true, FiveMinuteUser.Gender.MALE, new byte[] { 1, 2, 3 , 4, 5 });
        String xml = MAPPER.writeValueAsString(user);
        FiveMinuteUser result = MAPPER.readValue(xml, FiveMinuteUser.class);
        assertEquals(user, result);
    }

    public void testImmutableEmpty() throws Exception
    {
        Immutable im = MAPPER.readValue("<Immutable><text>hoge</text><labels></labels></Immutable>",
                                        Immutable.class);
        assertEquals("hoge", im.getText());
        assertNotNull(im.getLabels());
        assertEquals(0, im.getLabels().length);
    }

    public void testImmutable2() throws Exception
    {
        Immutable im = MAPPER.readValue(
            "<Immutable><text>hoge</text><labels><labels>foo<labels></labels></Immutable>",
            Immutable.class);
        assertEquals("hoge", im.getText());
    }

    public void testFromAttribute() throws Exception
    {
        AttributeBean bean = MAPPER.readValue("<AttributeBean attr=\"abc\"></AttributeBean>", AttributeBean.class);
        assertNotNull(bean);
        assertEquals("abc", bean.text);
    }

    // [Issue#14]
    public void testMapWithAttr() throws Exception
    {
        final String xml = "<order><person lang='en'>John Smith</person></order>";
        Map<?,?> map = MAPPER.readValue(xml, Map.class);

    	// Will result in equivalent of:
    	// { "person" : {
    	//     "lang" : "en",
    	//     "" : "John Smith"
    	//   }
    	// }
    	//
    	// which may or may not be what we want. Without attribute
    	// we would just have '{ "person" : "John Smith" }'

    	    assertNotNull(map);
    }

    // // Tests for [Issue#64]

    public void testOptionalAttr() throws Exception
    {
        Optional ob = MAPPER.readValue("<Optional type='work'>123-456-7890</Optional>",
                Optional.class);
        assertNotNull(ob);
        assertEquals("123-456-7890", ob.number);
        assertEquals("work", ob.type);
    }

    public void testMissingOptionalAttr() throws Exception
    {
        Optional ob = MAPPER.readValue("<Optional>123-456-7890</Optional>",
                Optional.class);
        assertNotNull(ob);
        assertEquals("123-456-7890", ob.number);
        assertEquals("NOT SET", ob.type);
    }
}
