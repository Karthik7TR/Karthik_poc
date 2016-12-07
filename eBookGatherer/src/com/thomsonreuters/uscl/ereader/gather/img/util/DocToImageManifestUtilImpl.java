/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class DocToImageManifestUtilImpl implements DocToImageManifestUtil {

	@Override
	@NotNull
	public Map<String, List<String>> getDocsWithImages(@NotNull File docToImageManifestFile) {
		Assert.notNull(docToImageManifestFile);
		Assert.isTrue(docToImageManifestFile.exists(), "doc-to-image-manifest.txt not exist");
		
		Map<String, List<String>> imgDocGuidMap = new HashMap<>();
		try (FileReader fileReader = new FileReader(docToImageManifestFile);
				BufferedReader reader = new BufferedReader(fileReader)) {
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					String[] ids = textLine.split("\\|");
					if (ids.length > 1) {
						String docId = ids[0].trim();
						String imageIdsStr = ids[1].trim();
						List<String> imageIds = getImageIds(imageIdsStr);
						imgDocGuidMap.put(docId, imageIds);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read doc-to-image-manifest.txt", e);
		}
		return imgDocGuidMap;
	}

	private List<String> getImageIds(String imageIdsStr) {
		String[] imageIds = imageIdsStr.split(",");
		ArrayList<String> list = new ArrayList<>();
		for (String id : imageIds) {
			list.add(id);
		}
		return list;
	}

}
