/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.links;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trgr.cobalt.util.urlbuilder.Container;
import com.trgr.cobalt.util.urlbuilder.ContainerAwareUrlBuilderFactoryBean;
import com.trgr.cobalt.util.urlbuilder.Parameter;
import com.trgr.cobalt.util.urlbuilder.UrlBuilder;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderException;


/**
 * Instances of this class are invoked by Xalan as a result of executing the WLN stylesheets.<p>Calls
 * to instances of this class will delegate to a pre-configured Cobalt URLBuilder.</p>
 *  <p>This class is a direct port of the .NET implementation of UrlBuilderExtension.cs with
 * minor tweaks (the Cobalt URLBuilder is a class-scoped field in our implementation).</p>
 *  <p><em>Methods implemented in this class match, exactly, the methods as declared in the WLN
 * XSL Stylesheets.  We <strong>deliberately</strong> broke from Java method naming conventions
 * due to the requirement to be compatible with the WLN XSL Stylesheets (capitalized public method
 * names are a good example).</em></p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class UrlBuilderAdapter
{
    private static Logger LOG = Logger.getLogger(UrlBuilderAdapter.class);
    private static UrlBuilder URL_BUILDER;
    private static String HOSTNAME;
    private static String MUD_PARAMETERS_RS;
    private static String MUD_PARAMETERS_VR;
    private UrlBuilder urlBuilder;

    public UrlBuilderAdapter() throws Exception
    {
        this.urlBuilder = new ContainerAwareUrlBuilderFactoryBean().getObject();
    }

    public String CreatePersistentUrl(String input, String param1)
    {
        return this.persistentIntermediary(input, param1);
    }

    public String CreatePersistentUrl(String input, String param1, String param2)
    {
        return this.persistentIntermediary(input, param1, param2);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3)
    {
        return this.persistentIntermediary(input, param1, param2, param3);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7)
    {
        return this.persistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8)
    {
        return this.persistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9)
    {
        return this.persistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9, String param10)
    {
        return this.persistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9, String param10, String param11)
    {
        return this.persistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10,
            param11);
    }

    public String CreatePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9, String param10, String param11,
        String param12)
    {
        return this.persistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10,
            param11, param12);
    }

    public String CreateRelativePersistentUrl(String input, String param1)
    {
        return this.relativePersistentIntermediary(input, param1);
    }

    public String CreateRelativePersistentUrl(String input, String param1, String param2)
    {
        return this.relativePersistentIntermediary(input, param1, param2);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3)
    {
        return this.relativePersistentIntermediary(input, param1, param2, param3);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4)
    {
        return this.relativePersistentIntermediary(input, param1, param2, param3, param4);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5)
    {
        return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9, String param10)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9, String param10, String param11)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10,
            param11);
    }

    public String CreateRelativePersistentUrl(
        String input, String param1, String param2, String param3, String param4, String param5,
        String param6, String param7, String param8, String param9, String param10, String param11,
        String param12)
    {
        return this.relativePersistentIntermediary(
            input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10,
            param11, param12);
    }

    public void setHostname(final String hostname)
    {
        UrlBuilderAdapter.HOSTNAME = hostname;
    }

    public void setRs(final String rs)
    {
        UrlBuilderAdapter.MUD_PARAMETERS_RS = rs;
    }

    /**
     * Sets the static UrlBuilder instance to be shared by all UrlBuilderAdapters.
     *
     * @param urlBuilder the UrlBuilder to set.
     */
    public void setUrlBuilder(UrlBuilder urlBuilder)
    {
        UrlBuilderAdapter.URL_BUILDER = urlBuilder;
    }

    public void setVr(final String vr)
    {
        UrlBuilderAdapter.MUD_PARAMETERS_VR = vr;
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

    private List<Parameter> createParameters(String... parameters)
    {
        List<Parameter> paramList = new ArrayList<Parameter>();

        for (String nameValuePair : parameters)
        {
            String[] nameAndValue = nameValuePair.split("=");

            String name = nameAndValue[0];
            String value = "";

            if (nameAndValue.length > 1)
            {
                value = nameAndValue[1];
            }

            paramList.add(new Parameter(name, value, true));
        }

        return paramList;
    }

    private String persistentIntermediary(String templateName, String... parameters)
    {
        String response = null;

        try
        {
            List<Parameter> paramList = createParameters(parameters);
            response = HOSTNAME
                + this.urlBuilder.createUrl(Container.COBALT.name(), templateName, paramList);
            response = addExtraParameters(response);
        }
        catch (UrlBuilderException e)
        {
            response = "";
        }

        return response;
    }

    private String relativePersistentIntermediary(String templateName, String... parameters)
    {
        String response = null;

        try
        {
            List<Parameter> paramList = createParameters(parameters);
            response = HOSTNAME
                + this.urlBuilder.createUrl(Container.COBALT.name(), templateName, paramList);
            response = addExtraParameters(response);
        }
        catch (UrlBuilderException e)
        {
            response = "";
        }

        return response;
    }
}
