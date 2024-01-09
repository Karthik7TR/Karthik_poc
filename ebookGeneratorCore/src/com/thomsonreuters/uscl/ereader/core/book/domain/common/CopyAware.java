package com.thomsonreuters.uscl.ereader.core.book.domain.common;

public interface CopyAware<D extends CopyAware<D>> {
    void copy(D other);
}
