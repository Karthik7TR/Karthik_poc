package com.thomsonreuters.uscl.ereader.format.text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

/**
 * This class serves as an adapter to ensure that any calls to DocumentExtension, during the
 * xslt transformation process, return a String.  This class is a Java port of the
 * .NET DocumentXslExtension.cs object and performs the same logical operations.
 *
 */
@Slf4j
public class DocumentExtensionAdapter {

    public DocumentExtensionAdapter() {
    }

    /**
     * Returns the default text.
     * @param context
     * @param key
     * @param defaultText
     * @return
     */
    public String RetrieveContextValue(final String context, final String key, final String defaultText) {
        return defaultText;
    }

    /**
     * Returns the default text.
     * @param context
     * @param key
     * @param defaultText
     * @return
     */
    public String RetrieveLocaleValue(
        final String language,
        final String context,
        final String key,
        final String defaultText) {
        return defaultText;
    }

    /**
     *  Generates hash for Sponsor ID.
     * @param sponsorId
     * @param documentGuid
     * @return Hash for use with sponsor.
     */
    public String GenerateSponsorHash(final String sponsorId, final String documentGuid) {
        if (StringUtils.isEmpty(sponsorId) || StringUtils.isEmpty(documentGuid)) {
            return null;
        }

        String str = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(sponsorId.getBytes("UTF-8"), 0, sponsorId.length());
            final byte[] hashedSponsor = md.digest();

            final SecretKeySpec localMac = new SecretKeySpec(hashedSponsor, "HmacSHA256");
            final Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(localMac);
            final byte[] hmac = hmacSha256.doFinal(documentGuid.getBytes("UTF-8"));
            str = DatatypeConverter.printHexBinary(hmac);
        } catch (final NoSuchAlgorithmException e) {
            log.debug(e.getMessage());
        } catch (final UnsupportedEncodingException e) {
            log.debug(e.getMessage());
        } catch (final InvalidKeyException e) {
            log.debug(e.getMessage());
        }

        return str;
    }

    public boolean ShouldDisplayEffectiveDates(final String documentType) {
        return false;
    }

    /**
     *
     * @param inputText - The text that we want to check for encoding issues
     * @return The encoded text.
     */
    public String ToXmlEncodedString(final String inputText) {
        if (inputText == null) {
            return null;
        }

        final StringBuilder outputText = new StringBuilder();
        char c = ' ';
        for (int i = 0; i < inputText.length(); i++) {
            c = inputText.charAt(i);
            if ((c == '\u00C2') || (c == '\u00E2') || (c == '\u20AC')) {
                outputText.append("");
            } else if (c == '\u201A') {
                outputText.append(" ");
            } else if (c == '\u2002') {
                outputText.append(" ");
            } else {
                outputText.append(c);
            }
        }
        return outputText.toString();
    }

    /**
     * IsMatch - simple regular expression match
     * @param input string to search
     * @param pattern regex pattern to match
     * @return
     */
    public boolean IsMatch(final String input, final String pattern) {
        Validate.notEmpty(pattern);

        // Must support null/empty inputs, but they don't count as a match
        if (StringUtils.isEmpty(input)) {
            return false;
        }

        return input.matches(pattern);
    }

    public String RetrieveScopedPageUrlParameter() {
        final String ScopedPageUrlParamName = "scopedPageUrl";
        return this.RetrieveScopedSearchParameter(ScopedPageUrlParamName);
    }

    public String RetrieveScopedJurisdictionParameter() {
        final String ScopedJurisdictionParamName = "scopedJurisdiction";
        return this.RetrieveScopedSearchParameter(ScopedJurisdictionParamName);
    }

    private String RetrieveScopedSearchParameter(String scopedSearchParamName) {
        return StringUtils.EMPTY;
    }

    public String TocToTitleCase(final String tocNodeName) {
        return WordUtils.capitalize(tocNodeName);
    }

    public String ReplaceSpecialCharacters(String input, boolean notPreformatted) {
        Objects.requireNonNull(input);
        if (input.length() == 0) {
            return StringUtils.EMPTY;
        }

        StringBuilder result = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // Replace a 'Middle Dot' entity with a hyphen as done in Web2. IF18F5C706FAB11E0BF19F7BCE048A5D4 is an example
            if (c == '\uE6DD') {
                result.append('\u002D');
            }
            // Replace invalid double quote charater with a valid one.Id22bb2e0dabd11e398db8b09b4f043e0 contains &#x0093 and &#x0094
            else if (c == '\u0093' || c == '\u0094') {
                result.append('\u0022');
            }
            // InternalStatictext.dtd
            else if (c == '\u00A0' || c == '\u2000' || c == '\u2001' || c == '\u2002' || c == '\u2003' || c == '\u2004' || c == '\u2005' || c == '\u2006' || c == '\u2007' || c == '\u2008' || c == '\u2009' || c == '\u200A') {
                result.append('\u0020');
            } else if (c == '\u2605') {
                result.append('*');
            } else if (c == '\u2024') {
                result.append('.');
            } else if (c == '\uEE52' || c == '\u200B' || notPreformatted && (c == '\n' || c == '\r')) {
                continue;
            } else if (c == '&') {
                if (input.length() > i + 3) {
                    // Replace an &amp;amp; (&amp;AMP;) with a valid &amp;. Ifed69c2cf1d911e18b05fdf15589d8e8 and Ifed69c55f1d911e18b05fdf15589d8e8 contain &amp;amp; in the title line
                    if (input.length() > i + 4
                            && (input.charAt(i + 1) == 'a' || input.charAt(i + 1) == 'A')
                            && (input.charAt(i + 2) == 'm' || input.charAt(i + 2) == 'M')
                            && (input.charAt(i + 3) == 'p' || input.charAt(i + 3) == 'P')
                            && input.charAt(i + 4) == ';') {
                        i = i + 4;
                        result.append('&');
                    }
                    // Replace an &SECT; with a valid one. I0b1eeb53e39611e08b05fdf15589d8e8 contains &sect; in fixed header
                    else if (input.length() > i + 5
                            && (input.charAt(i + 1) == 's' || input.charAt(i + 1) == 'S')
                            && (input.charAt(i + 2) == 'e' || input.charAt(i + 2) == 'E')
                            && (input.charAt(i + 3) == 'c' || input.charAt(i + 3) == 'C')
                            && (input.charAt(i + 4) == 't' || input.charAt(i + 4) == 'T')
                            && input.charAt(i + 5) == ';') {
                        i = i + 5;
                        result.append('\u00A7');
                    }
                    // Replace an &emsp; with blank. Ifed69c20f1d911e18b05fdf15589d8e8 contains &mdash; in the title line
                    else if (input.length() > i + 6 && (input.charAt(i + 1) == 'm' && input.charAt(i + 2) == 'd' && input.charAt(i + 3) == 'a' && input.charAt(i + 4) == 's' && input.charAt(i + 5) == 'h' && input.charAt(i + 6) == ';')) {
                        i = i + 6;
                        result.append('\u2014');
                    }
                    // Replace an &emsp; with blank. I0b1eeb53e39611e08b05fdf15589d8e8 contains &emsp; in 2nd line cite
                    else if (input.length() > i + 5 && (input.charAt(i + 1) == 'e' && input.charAt(i + 2) == 'm' && input.charAt(i + 3) == 's' && input.charAt(i + 4) == 'p' && input.charAt(i + 5) == ';')) {
                        i = i + 5;
                        result.append(' ');
                    }
                    // Replace an invalid quote entity with a valid one. IF7B750FB629B11DA97FAF3F66E4B6844 contains &QUOT;
                    else if (input.length() > i + 5 && (input.charAt(i + 1) == 'Q' && input.charAt(i + 2) == 'U' && input.charAt(i + 3) == 'O' && input.charAt(i + 4) == 'T' && input.charAt(i + 5) == ';')) {
                        i = i + 5;
                        result.append('\"');
                    }
                    // Replace an invalid greater entity with a valid one. I221ADD3FFF1B11DFAA23BCCC834E9520 contains &GT;
                    else if (input.charAt(i + 1) == 'G' && input.charAt(i + 2) == 'T' && input.charAt(i + 3) == ';') {
                        i = i + 3;
                        result.append('>');
                    }
                    // Replace an invalid Lesser entity with a valid one. IF601331DF9B711E0A9E5BDC02EF2B18E, I81843dc07eea11e18b05fdf15589d8e8(PublicRecords Document) contains &LT;
                    else if (input.charAt(i + 1) == 'L' && input.charAt(i + 2) == 'T' && input.charAt(i + 3) == ';') {
                        i = i + 3;
                        result.append('<');
                    }
                    // Replace an invalid bullet entity with a valid one. I988b61b94ebd11de9b8c850332338889 contains &#159; for an invalid bullet
                    // Replace an invalid bullet entity with a valid one. I13afb6981d9c11df9b8c850332338889 contains &#183; for an invalid bullet
                    else if (input.length() > i + 5
                            && input.charAt(i + 1) == '#'
                            && ((input.charAt(i + 2) == '1' && input.charAt(i + 3) == '5' && input.charAt(i + 4) == '9')
                            || (input.charAt(i + 2) == '1' && input.charAt(i + 3) == '8' && input.charAt(i + 4) == '3'))
                            && input.charAt(i + 5) == ';') {
                        i = i + 5;
                        result.append('\u2022');
                    } else {
                        result.append('&');
                    }
                } else {
                    result.append('&');
                }
            } else {
                result.append(c);
            }
        }

        if (notPreformatted) {
            this.ReplaceWhitespacesToSingleSpace(result);
        }

        return result.toString();
    }

    private void ReplaceWhitespacesToSingleSpace(StringBuilder result) {
        int current = 0;
        for (int i = 0; i < result.length(); i++) {
            if (Character.isWhitespace(result.charAt(i))) {
                if (i == 0 || !Character.isWhitespace(result.charAt(i - 1))) {
                    result.setCharAt(current, ' ');
                    current++;
                }
                continue;
            }
            if (current != i) {
                result.setCharAt(current, result.charAt(i));
            }
            current++;
        }
        result.setLength(current);
    }

    public String RetrieveExternalImageUrl(String externalUrl) {
        return StringUtils.EMPTY;
    }
}
