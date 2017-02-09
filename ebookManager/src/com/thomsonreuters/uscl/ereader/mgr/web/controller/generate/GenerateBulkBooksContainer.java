package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

public class GenerateBulkBooksContainer
{
    private Long bookId;
    private String fullyQualifiedTitleId;
    private String proviewDisplayName;
    private boolean isDeleted;

    public String getProviewDisplayName()
    {
        return proviewDisplayName;
    }

    public void setProviewDisplayName(final String proviewDisplayName)
    {
        this.proviewDisplayName = proviewDisplayName;
    }

    public String getFullyQualifiedTitleId()
    {
        return fullyQualifiedTitleId;
    }

    public void setFullyQualifiedTitleId(final String fullyQualifiedTitleId)
    {
        this.fullyQualifiedTitleId = fullyQualifiedTitleId;
    }

    public Long getBookId()
    {
        return bookId;
    }

    public void setBookId(final Long bookId)
    {
        this.bookId = bookId;
    }

    public boolean isDeleted()
    {
        return isDeleted;
    }

    public void setDeleted(final boolean isDeleted)
    {
        this.isDeleted = isDeleted;
    }
}
