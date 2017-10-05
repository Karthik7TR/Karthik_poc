/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;

@SuppressWarnings("null")
public class TitleManifestMatchers {

    public static List<Doc> docsList(String... ids) {
        List<Doc> docs = new ArrayList<>();
        for (String id : ids) {
            docs.add(new Doc(id, id, 0, null));
        }
        return docs;
    }

    @SuppressWarnings("unchecked")
    public static Matcher<Doc>[] docsWithIds(String... ids) {
        List<Matcher<Doc>> matchers = new ArrayList<>();
        for (String id : ids) {
            matchers.add(docWithId(id));
        }
        return (Matcher<Doc>[]) matchers.toArray(new Matcher[0]);
    }

    public static Matcher<Doc> docWithId(String id) {
        return hasProperty("id", is(id));
    }

    public static Feature feature(String name, String value) {
        return new Feature(name, value);
    }

    public static Feature feature(String name) {
        return new Feature(name);
    }
}
