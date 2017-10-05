package com.thomsonreuters.uscl.ereader.gather.services;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;

import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * Factory to create the main Novus API object for communicating with Novus
 * to get document data.
 */
public interface NovusFactory {
    /**
     * Create the Novus system connection.
     * @param isFinalStage determines to retrieve content from Final or Review stage
     */
    Novus createNovus(boolean isFinalStage) throws NovusException;

    /**
     * Which environment "Client" | "Prod" are we working with.
     * @param env the typesafe environment name
     */
    void setNovusEnvironment(NovusEnvironment env);
}
