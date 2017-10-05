package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfoDecorator;
import org.jetbrains.annotations.NotNull;

public class ProviewTitle extends TitleInfoDecorator {
    private static final String STATUS_REVIEW = "Review";

    private boolean canRemove;
    private boolean canPromoteBook;

    public ProviewTitle(@NotNull final TitleInfo titleInfo, final boolean canRemove, final boolean canPromoteBook) {
        super(titleInfo);
        this.canRemove = canRemove;
        this.canPromoteBook = canPromoteBook;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public boolean isCanPromote() {
        return canPromoteBook && STATUS_REVIEW.equals(getStatus());
    }
}
