package com.thomsonreuters.uscl.ereader.img;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class GathererResponseMatcher extends BaseMatcher<Object> {
    private int missingImgCount;
    private Matcher<? extends Object> metadataMatcher;

    public GathererResponseMatcher(final Matcher<? extends Object> metadataMatcher, final int missingImgCount) {
        this.metadataMatcher = metadataMatcher;
        this.missingImgCount = missingImgCount;
    }

    public static Matcher<Object> isGathererResponse(
        final Matcher<? extends Object> metadataMatcher,
        final int missingImgCount) {
        return new GathererResponseMatcher(metadataMatcher, missingImgCount);
    }

    @Override
    public boolean matches(final Object item) {
        final GatherResponse response = (GatherResponse) item;
        return metadataMatcher.matches(response.getImageMetadataList())
            && missingImgCount == response.getMissingImgCount();
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("image metadata list should be ").appendDescriptionOf(metadataMatcher);
        description.appendText("; missingImgCount should be ").appendValue(missingImgCount);
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        final GatherResponse response = (GatherResponse) item;
        description.appendText("image metadata list was ").appendText(response.getImageMetadataList().toString());
        description.appendText("; missingImgCount was ").appendValue(response.getMissingImgCount());
    }
}
