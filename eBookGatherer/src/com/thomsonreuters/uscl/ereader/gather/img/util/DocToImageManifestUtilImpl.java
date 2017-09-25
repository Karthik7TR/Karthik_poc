package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("docToImageManifestUtil")
public class DocToImageManifestUtilImpl implements DocToImageManifestUtil
{
    @Override
    @NotNull
    public Map<String, List<String>> getDocsWithImages(@NotNull final File docToImageManifestFile)
    {
        Assert.notNull(docToImageManifestFile);
        Assert.isTrue(docToImageManifestFile.exists(), "doc-to-image-manifest.txt not exist");

        final Map<String, List<String>> imgDocGuidMap = new HashMap<>();
        try (FileReader fileReader = new FileReader(docToImageManifestFile);
            BufferedReader reader = new BufferedReader(fileReader))
        {
            String textLine;
            while ((textLine = reader.readLine()) != null)
            {
                if (StringUtils.isNotBlank(textLine))
                {
                    final String[] ids = textLine.split("\\|");
                    if (ids.length > 1)
                    {
                        final String docId = ids[0].trim();
                        final String imageIdsStr = ids[1].trim();
                        final List<String> imageIds = getImageIds(imageIdsStr);
                        imgDocGuidMap.put(docId, imageIds);
                    }
                }
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Cannot read doc-to-image-manifest.txt", e);
        }
        return imgDocGuidMap;
    }

    private List<String> getImageIds(final String imageIdsStr)
    {
        final String[] imageIds = imageIdsStr.split(",");
        final List<String> list = new ArrayList<>();
        for (final String id : imageIds)
        {
            list.add(id);
        }
        return list;
    }
}
