package com.thomsonreuters.uscl.ereader.gather.services;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import org.springframework.beans.factory.annotation.Required;

import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * Factory to create the main Novus API object for communicating with Novus
 * to get document data.
 */
public class NovusFactoryImpl implements NovusFactory
{
    private NovusEnvironment novusEnvironment;
    private String productName;
    private String businessUnit;

    @Override
    public Novus createNovus(final boolean isFinalStage) throws NovusException
    {
        final Novus novus = new Novus();
        novus.setQueueCriteria(null, novusEnvironment.toString());
        novus.setResponseTimeout(30000);

        if (isFinalStage)
        {
            novus.useLatestPit();
        }
        else
        {
            novus.createRPit();
        }
        novus.setProductName(productName);
        novus.setBusinessUnit(businessUnit);
        return novus;
    }

    @Override
    @Required
    public void setNovusEnvironment(final NovusEnvironment env)
    {
        novusEnvironment = env;
    }

    @Required
    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    @Required
    public void setBusinessUnit(final String businessUnit)
    {
        this.businessUnit = businessUnit;
    }
}
