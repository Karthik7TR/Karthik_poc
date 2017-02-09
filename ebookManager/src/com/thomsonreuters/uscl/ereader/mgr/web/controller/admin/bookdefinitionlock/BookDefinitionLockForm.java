package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookdefinitionlock;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;

public class BookDefinitionLockForm
{
    //private static final Logger log = LogManager.getLogger(BookDefinitionLockForm.class);
    public static final String FORM_NAME = "bookDefinitionLockForm";

    private Long bookDefinitionLockId;
    private Long bookDefinitionId;
    private String username;
    private String fullName;
    private Date checkoutTimestamp;

    public BookDefinitionLockForm()
    {
        super();
    }

    public void initialize(final BookDefinitionLock lock)
    {
        bookDefinitionLockId = lock.getEbookDefinitionLockId();
        bookDefinitionId = lock.getEbookDefinition().getEbookDefinitionId();
        username = lock.getUsername();
        fullName = lock.getFullName();
        checkoutTimestamp = lock.getCheckoutTimestamp();
    }

    public Long getBookDefinitionLockId()
    {
        return bookDefinitionLockId;
    }

    public void setBookDefinitionLockId(final Long bookDefinitionLockId)
    {
        this.bookDefinitionLockId = bookDefinitionLockId;
    }

    public Long getBookDefinitionId()
    {
        return bookDefinitionId;
    }

    public void setBookDefinitionId(final Long bookDefinitionId)
    {
        this.bookDefinitionId = bookDefinitionId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(final String fullName)
    {
        this.fullName = fullName;
    }

    public Date getCheckoutTimestamp()
    {
        return checkoutTimestamp;
    }

    public void setCheckoutTimestamp(final Date checkoutTimestamp)
    {
        this.checkoutTimestamp = checkoutTimestamp;
    }
}
