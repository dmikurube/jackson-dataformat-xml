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

    public static class Mutable {
        @JsonCreator
        public Mutable(@JacksonXmlProperty(localName = "text")
                       String textInternal) {
            System.out.println("?");
            System.out.println(textInternal);
            System.out.println("?");
            this.textInternal = textInternal;
        }

        @JacksonXmlElementWrapper(localName = "mutableLabelsWrapper")
        @JacksonXmlProperty(localName = "mutableLabelSingle")
        public ArrayList<String> getMutableLabelsMethod() { return mutableLabelsInternal; }
        public void setMutableLabelsMethod(ArrayList<String> newone) {
            mutableLabelsInternal = newone;
        }
        @JacksonXmlProperty(localName = "text")
        public String getTextMethod() { return textInternal; }

        private ArrayList<String> mutableLabelsInternal;
        private final String textInternal;
    }

    static class Immutable {
        @JsonCreator
        public Immutable(@JacksonXmlProperty(localName = "text")
                         String textInternal,
                         @JacksonXmlElementWrapper(localName = "labelsWrapper")
                         @JacksonXmlProperty(localName = "labelSingle")
                         String[] labelsInternal) {
            System.out.println("!");
            System.out.println(textInternal);
            System.out.println(labelsInternal);
            System.out.println("!");
            this.textInternal = textInternal;
            this.labelsInternal = labelsInternal;
        }

        @JacksonXmlProperty(localName = "text")
        public String getTextMethod() { return textInternal; }
        @JacksonXmlElementWrapper(localName = "labelsWrapper")
        @JacksonXmlProperty(localName = "labelSingle")
        public String[] getLabelsMethod() { return labelsInternal; }

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
    /*
    public void testRoundTripWithJacksonExample() throws Exception
    {
        FiveMinuteUser user = new FiveMinuteUser("Joe", "Sixpack",
                true, FiveMinuteUser.Gender.MALE, new byte[] { 1, 2, 3 , 4, 5 });
        String xml = MAPPER.writeValueAsString(user);
        FiveMinuteUser result = MAPPER.readValue(xml, FiveMinuteUser.class);
        assertEquals(user, result);
    }
    */

    public void testMutableEmpty() throws Exception
    {
        System.out.println("+++ testMutableEmpty");
        Mutable im = MAPPER.readValue("<Mutable><text>fuga</text><mutableLabelsWrapper><mutableLabelSingle>baz</mutableLabelSingle></mutableLabelsWrapper></Mutable>",
                                        Mutable.class);
        assertEquals("fuga", im.getTextMethod());
        assertNotNull(im.getMutableLabelsMethod());
        assertEquals(1, im.getMutableLabelsMethod().size());
        System.out.println("+++ testMutableEmpty end");
    }

    public void testImmutableEmpty() throws Exception
    {
        System.out.println("+++ testImmutableEmpty");
        Immutable im = MAPPER.readValue("<Immutable><text>hoge</text><labelsWrapper></labelsWrapper></Immutable>",
                                        Immutable.class);
        assertEquals("hoge", im.getTextMethod());
        // assertNotNull(im.getLabelsMethod());
        // assertEquals(0, im.getLabelsMethod().length);
        System.out.println("+++ testImmutableEmpty end");
    }

    /*
    public void testImmutable2() throws Exception
    {
        System.out.println("+++ testImmutable");
        Immutable im = MAPPER.readValue(
            "<Immutable><text>hoge</text><labelsWrapper><labelSingle>foo</labelSingle></labelsWrapper></Immutable>",
            Immutable.class);
        assertEquals("hoge", im.getTextMethod());
        System.out.println("+++ testImmutable end");
        throw new RuntimeException();
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
    */
}
