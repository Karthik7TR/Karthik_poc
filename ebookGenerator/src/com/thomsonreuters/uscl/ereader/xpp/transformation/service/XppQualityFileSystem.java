package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("xppQualityFileSystem")
@NoArgsConstructor
public class XppQualityFileSystem implements QualityFileSystem {
    private XppFormatFileSystem fileSystem;

    @Autowired
    public XppQualityFileSystem(final XppFormatFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public MultiKeyMap<String, Collection<File>> getHtmlFileMap(final BookStep step) {
        return fileSystem.getFiles(step, XppFormatFileSystemDir.UNESCAPE_DIR)
                .entrySet()
                .stream()
                .collect(MULTI_KEY_MAP_COLLECTOR);
    }
}
