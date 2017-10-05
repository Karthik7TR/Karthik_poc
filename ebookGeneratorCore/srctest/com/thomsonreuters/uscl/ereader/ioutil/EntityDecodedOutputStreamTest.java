package com.thomsonreuters.uscl.ereader.ioutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public final class EntityDecodedOutputStreamTest {
    @Test
    public void testPi0() throws Exception {
        final String input3 = "?PI $#saveMe;a&lt;b ?>$#arf;cd$#lt;";
        final String input2 = "#lt;<";
        final String input1 = "ab$#arf;cd$";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(input1.getBytes());
        decode.write(input2.getBytes());
        decode.write(input3.getBytes());
        decode.flush();
        decode.close();
        final String actual = out.toString();
        final String expected = "ab&arf;cd&lt;<?PI $#saveMe;a&lt;b ?>&arf;cd&lt;";

        assertEquals('\n' + expected, '\n' + actual);
    }

    @Test
    public void testPi1() throws Exception {
        final String output = "ab$#arf;cd$#lt;<?PI $#saveMe;a&lt;b ?>$#arf;cd$#lt;";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();
        final String actual = out.toString();
        final String expected = "ab&arf;cd&lt;<?PI $#saveMe;a&lt;b ?>&arf;cd&lt;";

        assertEquals('\n' + expected, '\n' + actual);
    }

    @Test
    public void testPi2() throws Exception {
        final String output = "$$ab$#arf;cd$#lt;<?PI $#saveMe;a$b &hi; ?>$$";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();

        final String actual = out.toString();
        final String expected = "$ab&arf;cd&lt;<?PI $#saveMe;a$b &hi; ?>$";

        assertEquals('\n' + expected, '\n' + actual);
    }

    @Test
    public void testDecodeEntities() throws Exception {
        final String output = "<element>apples $#amp; oranges cost $$12.00.</element>";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();

        final String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testDecodeEntitiesByteAtATime() throws Exception {
        final String output = "<element>apples $#amp; oranges cost $$12.00.</element>";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);
        final byte[] bytes = output.getBytes();
        for (int i = 0; i < bytes.length; ++i) {
            decode.write(bytes[i]);
        }
        decode.flush();
        decode.close();

        final String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testDecodeNonEncodedEntitiesIfAskedNicely() throws Exception {
        final String input = "<element>apples $#nbsp; &amp;.</element>";
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();

        final String expected = "<element>apples &nbsp; &amp;.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testDecodeNonEncodedDollarSign() throws Exception {
        final String input = "<element>oranges cost $12.00.</element>";

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(input.getBytes());
            decode.flush();
            decode.close();

            fail("expected exception");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("Input does not appear to be entity encoded") != -1);
        }
    }

    @Test
    public void testDecodeNonEncodedDollarSignAtEnd() throws Exception {
        final String input = "<element>oranges cost 12.00.</element>$";

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(input.getBytes());
            decode.flush();
            decode.close();

            fail("expected exception");
        } catch (final IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(
                e.getMessage().indexOf(
                    "at close an extra $ was left in the buffer! input does not appear to be entity encoded") != -1);
        }
    }

    @Test
    public void testDontFailOnNonEncodedDollarSignWhenAllowUnencodedEntitiesIsTrue() throws Exception {
        final String input = "<element>oranges cost $12.00.</element>";
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();
        decode.close();
        assertEquals(input, out.toString());
    }

    @Test
    public void testDontFailOnNonEncodedDollarSignWhenAllowUnencodedEntitiesIsTrueAtEnd() throws Exception {
        final String input = "<element>oranges cost 12.00.</element>$";
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();
        decode.close();
        assertEquals(input, out.toString());
    }

    @Test
    public void testDontFailOnNonEncodedDollarSignWhenAllowUnencodedEntitiesIsTrueButDoubleDollarsDecoded()
        throws Exception {
        final String input = "<element>Apples cost $$ but oranges cost $12.00.</element>";
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out, true);

        decode.write(input.getBytes());
        decode.flush();
        decode.close();
        final String expected = "<element>Apples cost $ but oranges cost $12.00.</element>";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testDecodeNonEncodedEntities() {
        final String input = "<element>apples &amp;.</element>";

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(input.getBytes());
            decode.flush();
            decode.close();

            fail("expected exception");
        } catch (final Exception e) {
            assertTrue(
                e.getMessage().indexOf("Detected entity in the input when no unencoded entitites are allowed") != -1);
        }
    }

    @Test
    public void testDollarAtEnd() throws Exception {
        try {
            final String outputOne = "This is a line of text with $$ at the end $";
            final String outputTwo = "doesn't handle error.";
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

            decode.write(outputOne.getBytes());
            decode.write(outputTwo.getBytes());
            decode.flush();
            decode.close();

            fail("Fail to throw exception.");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("input does not appear to be entity encoded") != -1);
        }
    }

    @Test
    public void testDollarSignAcrossWrites() throws Exception {
        final String outputOne = "<element>apples $#amp; oranges cost $";
        final String outputTwo = "$12.00.</element>";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        final String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testEncodedEntityAcrossWrites() throws Exception {
        final String outputOne = "<element>apples $";
        final String outputTwo = "#amp; oranges cost $$12.00.</element>";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        final String expected = "<element>apples &amp; oranges cost $12.00.</element>";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testMultipleDollarSigns() throws Exception {
        final String output = "$$$$";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(output.getBytes());
        decode.flush();
        decode.close();

        final String expected = "$$";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testMultipleDollarSignsAcrossWrite() throws Exception {
        final String outputOne = "$$";
        final String outputTwo = "$$";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        final String expected = "$$";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testUnevenMultipleDollarSignsAcrossWrite() throws Exception {
        final String outputOne = "$";
        final String outputTwo = "$$$";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        decode.write(outputOne.getBytes());
        decode.write(outputTwo.getBytes());
        decode.flush();
        decode.close();

        final String expected = "$$";

        assertEquals('\n' + expected, '\n' + out.toString());
    }

    @Test
    public void testNoEntitiesAllowedMessageFail() throws Exception {
        final String output =
            "<nod.body><analysis ID=\"I99697C30DB9811DAAEE200123F44C07C\"><analysis.entry ID=\"ID4592BF0EAA811DA8EEAE79DD304BC7A\"><metadata.block owner=\"ID4592BF0EAA811DA8EEAE79DD304BC7A\"><md.mnem>nal</md.mnem><md.pub.tag.info><md.pub.tag>WL</md.pub.tag></md.pub.tag.info><md.source.tag>03-A1</md.source.tag></metadata.block><analysis.line><analysis.text>Gummy&mdash;bears</analysis.text>$#emsp;<label.designator>5</label.designator></analysis.line></analysis.entry></analysis></nod.body>";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decode = new EntityDecodedOutputStream(out);

        try {
            decode.write(output.getBytes());
            fail("Should not get here");
            decode.flush();
            decode.close();
        } catch (final IllegalArgumentException e) {
            // expected
            assertTrue("message too long", e.getMessage().length() < 256);
        }
    }
}
