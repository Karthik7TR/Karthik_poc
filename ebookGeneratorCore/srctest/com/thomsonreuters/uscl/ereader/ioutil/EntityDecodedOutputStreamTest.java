/*
* EntityDecodedOutputStreamTest
* 
* Created on: Nov 2, 2010 By: u0009398
* 
* Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved.
*
* Proprietary and Confidential information of TRGR. 
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;

public class EntityDecodedOutputStreamTest extends TestCase
{
    public EntityDecodedOutputStreamTest(String s)
    {
        super(s);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(EntityDecodedOutputStreamTest.class);
    }

    public void testPi0() throws Exception
    {
        String input3 = "?PI $#saveMe;a&lt;b ?>$#arf;cd$#lt;";
        String input2 = "#lt;<";
        String input1 = "ab$#arf;cd$";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(input1.getBytes());
        decode.write(input2.getBytes());
        decode.write(input3.getBytes());
        decode.flush();
        decode.close();
        String actual = out.toString();
        String expected = "ab&arf;cd&lt;<?PI $#saveMe;a&lt;b ?>&arf;cd&lt;";

        assertEquals('\n' + expected, '\n' + actual);
    }

    public void testPi1() throws Exception
    {
        String output = "ab$#arf;cd$#lt;<?PI $#saveMe;a&lt;b ?>$#arf;cd$#lt;";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();
        String actual = out.toString();
        String expected = "ab&arf;cd&lt;<?PI $#saveMe;a&lt;b ?>&arf;cd&lt;";

        assertEquals('\n' + expected, '\n' + actual);
    }

    public void testPi2() throws Exception
    {
        String output = "$$ab$#arf;cd$#lt;<?PI $#saveMe;a$b &hi; ?>$$";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();

        String actual = out.toString();
        String expected = "$ab&arf;cd&lt;<?PI $#saveMe;a$b &hi; ?>$";

        assertEquals('\n' + expected, '\n' + actual);
    }

    public void testDecodeEntities() throws Exception
    {
        String output = "<element>apples $#amp; oranges cost $$12.00.</element>";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();

        String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testDecodeEntitiesByteAtATime() throws Exception
    {
        String output = "<element>apples $#amp; oranges cost $$12.00.</element>";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);
        byte[] bytes = output.getBytes();
        for (int i = 0; i < bytes.length; ++i)
        {
            decode.write(bytes[i]);
        }
        decode.flush();
        decode.close();

        String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testDecodeNonEncodedEntitiesIfAskedNicely() throws Exception
    {
        String input = "<element>apples $#nbsp; &amp;.</element>";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();

        String expected = "<element>apples &nbsp; &amp;.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testDecodeNonEncodedDollarSign() throws Exception
    {
        String input = "<element>oranges cost $12.00.</element>";

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(input.getBytes());
            decode.flush();
            decode.close();

            fail("expected exception");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().indexOf("Input does not appear to be entity encoded") != -1);
        }
    }

    public void testDecodeNonEncodedDollarSignAtEnd() throws Exception
    {
        String input = "<element>oranges cost 12.00.</element>$";

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(input.getBytes());
            decode.flush();
            decode.close();

            fail("expected exception");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().indexOf(
                "at close an extra $ was left in the buffer! input does not appear to be entity encoded") !=
                -1);
        }
    }

    public void testDontFailOnNonEncodedDollarSignWhenAllowUnencodedEntitiesIsTrue()
        throws Exception
    {
        String input = "<element>oranges cost $12.00.</element>";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();
        decode.close();
        assertEquals(input, out.toString());
    }

    public void testDontFailOnNonEncodedDollarSignWhenAllowUnencodedEntitiesIsTrueAtEnd()
        throws Exception
    {
        String input = "<element>oranges cost 12.00.</element>$";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();
        decode.close();
        assertEquals(input, out.toString());
    }

    public void testDontFailOnNonEncodedDollarSignWhenAllowUnencodedEntitiesIsTrueButDoubleDollarsDecoded()
        throws Exception
    {
        String input = "<element>Apples cost $$ but oranges cost $12.00.</element>";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();
        decode.close();
        String expected = "<element>Apples cost $ but oranges cost $12.00.</element>";
        assertEquals(expected, out.toString());
    }

    public void testDecodeNonEncodedEntities() throws Exception
    {
        String input = "<element>apples &amp;.</element>";

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(input.getBytes());
            decode.flush();
            decode.close();

            fail("expected exception");
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage()
                .indexOf("Detected entity in the input when no unencoded entitites are allowed") !=
                -1);
        }
    }


    public void testDollarAtEnd() throws Exception
    {
        try
        {
            String outputOne = "This is a line of text with $$ at the end $";
            String outputTwo = "doesn't handle error.";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(outputOne.getBytes());
            decode.write(outputTwo.getBytes());
            decode.flush();
            decode.close();

            fail("Fail to throw exception.");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().indexOf("input does not appear to be entity encoded") != -1);
        }
    }

    public void testDollarSignAcrossWrites() throws Exception
    {
        String outputOne = "<element>apples $#amp; oranges cost $";
        String outputTwo = "$12.00.</element>";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testEncodedEntityAcrossWrites() throws Exception
    {
        String outputOne = "<element>apples $";
        String outputTwo = "#amp; oranges cost $$12.00.</element>";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testMultipleDollarSigns() throws Exception
    {
        String output = "$$$$";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();

        String expected = "$$";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testMultipleDollarSignsAcrossWrite() throws Exception
    {
        String outputOne = "$$";
        String outputTwo = "$$";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        String expected = "$$";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    public void testUnevenMultipleDollarSignsAcrossWrite()
        throws Exception
    {
        String outputOne = "$";
        String outputTwo = "$$$";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        String expected = "$$";

        assertEquals('\n' + expected, '\n' + out.toString());
    }


    public void testNoEntitiesAllowedMessageFail() throws Exception
    {
        String output =
            "<nod.body><analysis ID=\"I99697C30DB9811DAAEE200123F44C07C\"><analysis.entry ID=\"ID4592BF0EAA811DA8EEAE79DD304BC7A\"><metadata.block owner=\"ID4592BF0EAA811DA8EEAE79DD304BC7A\"><md.mnem>nal</md.mnem><md.pub.tag.info><md.pub.tag>WL</md.pub.tag></md.pub.tag.info><md.source.tag>03-A1</md.source.tag></metadata.block><analysis.line><analysis.text>Gummy&mdash;bears</analysis.text>$#emsp;<label.designator>5</label.designator></analysis.line></analysis.entry></analysis></nod.body>";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        try
        {
            decode.write(output.getBytes());
            fail("Should not get here");
            decode.flush();
            decode.close();
        }
        catch (IllegalArgumentException e)
        {
            // expected
            assertTrue("message too long", e.getMessage().length() < 256);
        }
    }
}

