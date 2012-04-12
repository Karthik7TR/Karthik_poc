/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.links;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.trgr.cobalt.util.urlbuilder.CiteQuery;
import com.trgr.cobalt.util.urlbuilder.Container;
import com.trgr.cobalt.util.urlbuilder.ContainerAwareUrlBuilderFactoryBean;
import com.trgr.cobalt.util.urlbuilder.UrlBuilder;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderException;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderInput;


/**
 * This class serves as an adapter to ensure that any calls to GetCiteQueryLink, during the
 * xslt transformation process, return a Persistent MUD URL.  This class is a Java port of the
 * .NET CiteQueryExtension.cs object and performs the same logical operations.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class CiteQueryAdapter
{
    private static final Logger LOG = Logger.getLogger(CiteQueryAdapter.class);
    private static String HOSTNAME;
    private static String MUD_PARAMETERS_RS;
    private static String MUD_PARAMETERS_VR;
    CiteQuery citeQuery = null;
    UrlBuilder urlBuilder = null;

    public CiteQueryAdapter() throws Exception
    {
        this.citeQuery = new CiteQuery(Container.COBALT.name());
        this.urlBuilder = new ContainerAwareUrlBuilderFactoryBean().getObject();
    }

    public String GetCiteQueryLink(
        String linkElement, String originatingDoc, String keyText, String sourceCite)
    {
        return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite);
    }

    public String GetCiteQueryLink(
        String linkElement, String originatingDoc, String keyText, String sourceCite, String param1)
    {
        return this.getCiteQueryLinkIntermediary(
            linkElement, originatingDoc, keyText, sourceCite, param1);
    }

    public String GetCiteQueryLink(
        String linkElement, String originatingDoc, String keyText, String sourceCite, String param1,
        String param2)
    {
        return this.getCiteQueryLinkIntermediary(
            linkElement, originatingDoc, keyText, sourceCite, param1, param2);
    }

    public String GetCiteQueryLink(
        String linkElement, String originatingDoc, String keyText, String sourceCite, String param1,
        String param2, String param3)
    {
        return this.getCiteQueryLinkIntermediary(
            linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3);
    }

    public String GetCiteQueryLink(
        String linkElement, String originatingDoc, String keyText, String sourceCite, String param1,
        String param2, String param3, String param4)
    {
        return this.getCiteQueryLinkIntermediary(
            linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);
    }

    public String GetCiteQueryLink(
        Node linkElement, String originatingDoc, String keyText, String sourceCite, String param1,
        String param2, String param3, String param4)
    {
        return this.getCiteQueryLinkIntermediary(
            linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);
    }

    public String GetCiteQueryLink(
        String linkElement, String originatingDoc, String keyText, String sourceCite, String param1,
        String param2, String param3, String param4, String param5)
    {
        return this.getCiteQueryLinkIntermediary(
            linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4, param5);
    }

    public void setHostname(final String hostname)
    {
        CiteQueryAdapter.HOSTNAME = hostname;
    }

    public void setRs(final String rs)
    {
        CiteQueryAdapter.MUD_PARAMETERS_RS = rs;
    }

    public void setVr(final String vr)
    {
        CiteQueryAdapter.MUD_PARAMETERS_VR = vr;
    }

    /**
     * 
     * @param url
     *
     * @return Updated Url (with extra parameters)
     */
    private String addExtraParameters(final String url)
    {
        StringBuilder strBuilder = new StringBuilder();
        String extraParameters = "&RS=" + MUD_PARAMETERS_RS + "&vr=" + MUD_PARAMETERS_VR;

        if (url.contains("#"))
        {
            String[] strSpliter = url.split("#");

            strBuilder.append(strSpliter[0]);
            strBuilder.append(extraParameters);

            if (strSpliter.length > 1)
            {
                if (strSpliter[1].contains("&"))
                {
                    String[] newStrSpliter = strSpliter[1].split("&");
                    int i = 1;

                    for (; i < newStrSpliter.length; i++)
                    {
                        strBuilder.append('&');
                        strBuilder.append(newStrSpliter[i]);
                    }

                    strBuilder.append("#" + newStrSpliter[0]);
                }
                else
                {
                    strBuilder.append("#" + strSpliter[1]);
                }
            }
        }
        else
        {
            return url + extraParameters;
        }

        return strBuilder.toString();
    }

    private String getCiteQueryLinkIntermediary(
        Node linkElement, String originatingDoc, String keyText, String sourceCite,
        String... parameters)
    {
        try
        {
            UrlBuilderInput input =
                this.citeQuery.getCiteQueryLink(
                    linkElement, originatingDoc, keyText, sourceCite, "ebook");

            if (input == null)
            {
                return "";
            }

            String response = null;

            try
            {
                response = HOSTNAME
                    + this.urlBuilder.createUrl(
                        Container.COBALT.name(), input.getUrlTemplateName(), input.getParameters());
                response = addExtraParameters(response);
            }
            catch (UrlBuilderException e)
            {
                response = "";

                LOG.debug("UrlBuilderException is " + e.getMessage());
            }

            return response;
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /// <summary>
    /// Returns a String of the UrlBuilderInput object that contains the appropriate template Name and paramters for the given input.
    /// </summary>
    /// <param Name="linkElement">The String of the XML cite.query element</param>
    /// <param Name="originatingDoc">The guid of the doc hosting the cite.query element</param>
    /// <param Name="keyText">The keytext used for key number links</param>
    /// <param name="sourceCite">The sourceCite of the document, used for CM ref type.</param>
    /// <returns>UrlBuilderInput to pass to UrlBuilder</returns>
    private String getCiteQueryLinkIntermediary(
        String linkElement, String originatingDoc, String keyText, String sourceCite,
        String... parameters)
    {
        try
        {
            UrlBuilderInput input =
                this.citeQuery.getCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite);

            if (input == null)
            {
                return "";
            }

            String response = null;

            try
            {
                response = HOSTNAME
                    + this.urlBuilder.createUrl(
                        Container.COBALT.name(), input.getUrlTemplateName(), input.getParameters());
                response = addExtraParameters(response);
            }
            catch (UrlBuilderException e)
            {
                response = "";

                LOG.debug("UrlBuilderException is " + e.getMessage());
            }

            return response;
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
