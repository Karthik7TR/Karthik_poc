package com.thomsonreuters.uscl.ereader.deliver.rest;

/**
 * A factory to serve up instances of ProviewRequestCallback objects.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class ProviewRequestCallbackFactory {
    public ProviewRequestCallback getStreamRequestCallback() {
        return new ProviewRequestCallback();
    }

    public ProviewXMLRequestCallback getXMLRequestCallback() {
        return new ProviewXMLRequestCallback();
    }
}
