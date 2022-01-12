package com.thomsonreuters.uscl.ereader.format.text;

import lombok.extern.slf4j.Slf4j;

/// <summary>
/// The citation link extension class will extend the xslt process to support generating a url or flag color text for a given citation/entity.
/// </summary>
@Slf4j
public class CitationLinkExtensionAdapter {

    public CitationLinkExtensionAdapter() {
    }

    /// <summary>
    /// Checks if there are flag urls with a given citation
    /// </summary>
    /// <param name="citation">A citation within the document</param>
    /// <returns>true if flag urls exist, false otherwise</returns>
    public boolean HasFlagUrls(final String citation, final String host, final Boolean createPersistentUrls) {
        log.error("Inside HasFlagUrls");
        return false;
    }

    /// <summary>
    /// Builds persistent or relative urls for the requested citation based in the document cache key.
    /// </summary>
    /// <param name="citation">The citation within the document that we want to fetch and build a url for.</param>
    /// <param name="host">The host.</param>
    /// <param name="createPersistentUrls">Create persistent urls.</param>
    /// <param name="allowSsl">Allow ssl for flags</param>
    /// <returns>
    /// A XPathNavigator containing relative URLs, persistent urls, or an empty string if no citation was found.
    /// </returns>
    public String CreateFlagCitations(
        final String citation,
        final String host,
        final Boolean createPersistentUrls,
        final Boolean allowSsl) {
        return "";
    }
}
