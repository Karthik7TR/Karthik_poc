package com.thomsonreuters.uscl.ereader.img;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ImageMetadataMatcher extends BaseMatcher<Object> {
    private String docId;
    private String imgId;

    public ImageMetadataMatcher(final String docId, final String imgId) {
        this.docId = docId;
        this.imgId = imgId;
    }

    public static Matcher<Object> imgMetadata(final String docId, final String imgId) {
        return new ImageMetadataMatcher(docId, imgId);
    }

    @Override
    public boolean matches(final Object item) {
        final ImgMetadataInfo metadata = (ImgMetadataInfo) item;
        return metadata.getDocGuid().equals(docId) && metadata.getImgGuid().equals(imgId);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("docId should be ").appendValue(docId);
        description.appendText("; imgId should be ").appendValue(imgId);
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        final ImgMetadataInfo metadata = (ImgMetadataInfo) item;
        description.appendText("docId was ").appendValue(metadata.getDocGuid());
        description.appendText("; imgId was ").appendValue(metadata.getImgGuid());
    }
}
