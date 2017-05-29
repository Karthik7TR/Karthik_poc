package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for XPP gather steps
 */
public interface XppGatherFileSystem extends GatherFileSystem
{
    /**
     * Returns XPP gather root directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Bundles}
     */
    @NotNull
    File getXppBundlesDirectory(@NotNull BookStep step);

    /**
     * Returns XPP bundles directories:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Bundles}{@code /[bundle material number]/[bundle name]}
     */
    @NotNull
    Collection<File> getXppBundleContentDirectories(@NotNull BookStep step);

    /**
     * Returns XPP unpacked images directories:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getXppBundlesDirectory Bundles}{@code /[bundle material number]/[bundle name]}{@code /assets}
     */
    @NotNull
    Collection<String> getXppAssetsDirectories(@NotNull BookStep step);

    /**
     * Returns XPP directories for all bundles:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getXppBundlesDirectory Bundles}{@code /[bundle material number]/[bundle name]}{@code /XPP}
     */
    @NotNull
    Collection<File> getXppSourceXmlDirectories(@NotNull BookStep step);

    /**
     * Returns source XPP XMLs for all bundles as plain list:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getXppBundlesDirectory Bundles}{@code /[bundle material number]/[bundle name]}{@code /XPP}{@code /*.xml}
     */
    @NotNull
    Map<String, Collection<File>> getXppSourceXmls(@NotNull BookStep step);
}
