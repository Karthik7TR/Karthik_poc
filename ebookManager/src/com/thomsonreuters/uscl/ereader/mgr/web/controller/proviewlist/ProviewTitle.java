package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfoDecorator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ProviewTitle extends TitleInfoDecorator {

    private boolean canPromote;
    private boolean canRemove;
    private boolean canDelete;

    public ProviewTitle(@NotNull final TitleInfo titleInfo, final boolean canPromote, final boolean canRemove, boolean canDelete) {
        super(titleInfo);
        this.canPromote = canPromote;
        this.canRemove = canRemove;
        this.canDelete = canDelete;
    }

    public ProviewTitle(@NotNull final TitleInfo titleInfo) {
        super(titleInfo);
    }

}
