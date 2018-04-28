package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service for PublisherCode
 */
public interface PublisherCodeService {
    /**
     * Get all the Publisher codes from the PUBLISHER_CODES table
     * @return a list of Publisher objects
     */
    @NotNull
    List<PublisherCode> getAllPublisherCodes();

    /**
     * Get a Publisher Code from the PUBLISHER_CODES table that match PUBLISHER_CODES_ID
     * @param publisherCodeId
     * @return
     */
    @Nullable
    PublisherCode getPublisherCodeById(@NotNull Long publisherCodeId);

    /**
     * Create or Update a Publisher Code to the PUBLISHER_CODES table
     * @param publisherCode
     * @return
     */
    void savePublisherCode(@NotNull PublisherCode publisherCode);

    /**
     * Delete a Publisher Code in the PUBLISHER_CODES table
     * @param publisherCode
     * @return
     */
    void deletePublisherCode(@NotNull PublisherCode publisherCode);
}
