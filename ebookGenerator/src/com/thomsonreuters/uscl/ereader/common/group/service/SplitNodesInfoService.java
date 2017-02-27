package com.thomsonreuters.uscl.ereader.common.group.service;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Service to work with splitNodesInfo file
 */
public interface SplitNodesInfoService
{
    @NotNull
    List<String> readSplitNodeInforFile(@NotNull File splitNodeInfoFile, @NotNull String fullyQualifiedTitleId);
}
