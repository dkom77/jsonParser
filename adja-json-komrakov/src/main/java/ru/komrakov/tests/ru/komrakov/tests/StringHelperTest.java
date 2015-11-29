package ru.komrakov.tests;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import ru.komrakov.jsonParser.StringHelper;

public class StringHelperTest extends TestCase {

    @Test
    public void testGet() throws Exception {
        String test = "  \" ddd \"  ";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("string should be unchanged", "  \" ddd \"  ", helper.get());
    }

    @Test
    public void testRemoveQuotes() throws Exception {
        String test = "  \" ddd \"  ";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("quotes should remains", "\" ddd \"", helper.removeInsignificantSymbols().get());
        Assert.assertEquals("quotes removed, spaces should stay", " ddd ", helper.removeQuotes().get());
    }

    @Test
    public void testIsValueInQuotes() throws Exception {
        String test = "\" ddd \"";
        StringHelper helper = new StringHelper(test);
        Assert.assertTrue("value in quotes", helper.isValueInQuotes());
    }

    @Test
    public void testIsValueInQuotes1() throws Exception {
        String test = "\" ddd";
        StringHelper helper = new StringHelper(test);
        Assert.assertFalse("value not in quotes", helper.isValueInQuotes());
    }

    @Test
    public void testIsValueInQuotes2() throws Exception {
        String test = "ddd \"";
        StringHelper helper = new StringHelper(test);
        Assert.assertFalse("value not in quotes", helper.isValueInQuotes());
    }

    @Test
    public void testIsValueInQuotes3() throws Exception {
        String test = "\" ddd\"zzz";
        StringHelper helper = new StringHelper(test);
        Assert.assertFalse("value not in quotes", helper.isValueInQuotes());
    }

    @Test
    public void testIsValueContainNotClosedQuotes() throws Exception {
        String test = "\"ddd";
        StringHelper helper = new StringHelper(test);
        Assert.assertTrue("value contain not closed in quotes", helper.isValueContainNotClosedQuotes());
    }

    @Test
    public void testIsValueContainNotClosedQuotes2() throws Exception {
        String test = "ddd\"";
        StringHelper helper = new StringHelper(test);
        Assert.assertTrue("value contain not closed quotes", helper.isValueContainNotClosedQuotes());
    }

    @Test
    public void testIsValueContainNotClosedQuotes3() throws Exception {
        String test = "ddd\"ffff";
        StringHelper helper = new StringHelper(test);
        Assert.assertTrue("value contain not closed quotes", helper.isValueContainNotClosedQuotes());
    }

    @Test
    public void testIsValueContainNotClosedQuotes4() throws Exception {
        //no quotes should be allowed in the middle of value
        String test = "ddd\"ff\"ff";
        StringHelper helper = new StringHelper(test);
        Assert.assertTrue("value contain not closed quotes", helper.isValueContainNotClosedQuotes());
    }

    @Test
    public void testRemoveEscapeChar() throws Exception {
        String test = "\\\" ddd\"zzz";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("escape symbol should stay in place", "\\\" ddd\"zzz\"", helper.removeInsignificantSymbols().get());
        Assert.assertEquals("escape symbol should be removed", "\" ddd\"zzz\"", helper.removeEscapeChar().get());
    }

    @Test
    public void testRemoveEscapeChar2() throws Exception {
        String test = "\\\" ddd\"\"zz  z\"";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("escape symbol should stay in place", "\\\" ddd\"\"zz  z\"", helper.removeInsignificantSymbols().get());
        Assert.assertEquals("escape symbol should be removed", "\" ddd\"zzz\"", helper.removeEscapeChar().get());
    }

    @Test
    public void testRemoveInsignificantSymbols1() throws Exception {
        String test = "   ddd   ";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("spaces should be removed", "ddd", helper.removeInsignificantSymbols().get());
    }

    @Test
    public void testRemoveInsignificantSymbols2() throws Exception {
        String test = "  \" ddd \"  ";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("spaces in quotes should not be removed", "\" ddd \"", helper.removeInsignificantSymbols().get());
    }

    @Test
    public void testRemoveInsignificantSymbols3() throws Exception {
        String test = "  \" ddd \" p \tpp \n ";
        StringHelper helper = new StringHelper(test);
        Assert.assertEquals("spaces in quotes should not be removed", "\" ddd \"ppp", helper.removeInsignificantSymbols().get());
    }
}