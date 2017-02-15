package com.thomsonreuters.uscl.ereader.format.text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class serves as an adapter to ensure that any calls to DocumentExtension, during the
 * xslt transformation process, return a String.  This class is a Java port of the
 * .NET DocumentXslExtension.cs object and performs the same logical operations.
 *
 */
public class DocumentExtensionAdapter
{
    private static final Logger LOG = LogManager.getLogger(DocumentExtensionAdapter.class);

    public DocumentExtensionAdapter()
    {
    }

    /**
     * Returns the default text.
     * @param context
     * @param key
     * @param defaultText
     * @return
     */
    public String RetrieveContextValue(final String context, final String key, final String defaultText)
    {
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
        final String defaultText)
    {
        return defaultText;
    }

    /**
     *  Generates hash for Sponsor ID.
     * @param sponsorId
     * @param documentGuid
     * @return Hash for use with sponsor.
     */
    public String GenerateSponsorHash(final String sponsorId, final String documentGuid)
    {
        if (StringUtils.isEmpty(sponsorId) || StringUtils.isEmpty(documentGuid))
        {
            return null;
        }

        String str = null;
        try
        {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(sponsorId.getBytes("UTF-8"), 0, sponsorId.length());
            final byte[] hashedSponsor = md.digest();

            final SecretKeySpec localMac = new SecretKeySpec(hashedSponsor, "HmacSHA256");
            final Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(localMac);
            final byte[] hmac = hmacSha256.doFinal(documentGuid.getBytes("UTF-8"));
            str = DatatypeConverter.printHexBinary(hmac);
        }
        catch (final NoSuchAlgorithmException e)
        {
            LOG.debug(e.getMessage());
        }
        catch (final UnsupportedEncodingException e)
        {
            LOG.debug(e.getMessage());
        }
        catch (final InvalidKeyException e)
        {
            LOG.debug(e.getMessage());
        }

        return str;
    }

    public boolean ShouldDisplayEffectiveDates(final String documentType)
    {
        return false;
    }

    /**
     *
     * @param inputText - The text that we want to check for encoding issues
     * @return The encoded text.
     */
    public String ToXmlEncodedString(final String inputText)
    {
        if (inputText == null)
        {
            return null;
        }

        final StringBuilder outputText = new StringBuilder();
        char c = ' ';
        for (int i = 0; i < inputText.length(); i++)
        {
            c = inputText.charAt(i);
            if ((c == '\u00C2') || (c == '\u00E2') || (c == '\u20AC'))
            {
                outputText.append("");
            }
            else if (c == '\u201A')
            {
                outputText.append(" ");
            }
            else if (c == '\u2002')
            {
                outputText.append(" ");
            }
            else
            {
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
    public boolean IsMatch(final String input, final String pattern)
    {
        Validate.notEmpty(pattern);

        // Must support null/empty inputs, but they don't count as a match
        if (StringUtils.isEmpty(input))
        {
            return false;
        }

        return input.matches(pattern);
    }

    /// <summary>
    /// Applies TOC capitalization rules to the <paramref name="tocNodeName"/> string
    /// </summary>
    /// <param name="tocNodeName">A string to apply capitalization rules to</param>
    /// <returns><paramref name="tocNodeName"/> after applying capitalization rules</returns>
    public String TocToTitleCase(final String tocNodeName)
    {
        return WordUtils.capitalize(tocNodeName);
    }
}
