package com.thomsonreuters.uscl.ereader.deliver.rest;

/**
 * This class is responsible for serving up instances of ProviewResponseExtractor.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
public class ProviewResponseExtractorFactory
{
    public ProviewResponseExtractor getResponseExtractor()
    {
        return new ProviewResponseExtractor();
    }
}
