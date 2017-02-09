package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.text.ParseException;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class ProviewTitleForm
{
    public static final String FORM_NAME = "proviewTitleInfoForm";

    public enum Command
    {
        REMOVE,
        DELETE,
        PROMOTE,
        REFRESH,
        PAGESIZE
    };

    private String titleId;
    private String version;
    private String status;
    private String lastUpdate;
    private String comments;
    private Command command;
    private String objectsPerPage;

    public ProviewTitleForm()
    {
        super();
    }

    public ProviewAudit createAudit()
    {
        final ProviewAudit audit = new ProviewAudit();
        audit.setAuditNote(comments);
        audit.setBookLastUpdated(parseDate(lastUpdate));
        audit.setBookVersion(version);
        audit.setProviewRequest(command.toString());
        audit.setRequestDate(new Date());
        audit.setTitleId(titleId);
        audit.setUsername(UserUtils.getAuthenticatedUserName());

        return audit;
    }

    public ProviewTitleForm(final String titleId, final String version, final String status, final String lastUpdate)
    {
        super();
        this.titleId = titleId;
        this.version = version;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    public String getObjectsPerPage()
    {
        return objectsPerPage;
    }

    public void setObjectsPerPage(final String objectsPerPage)
    {
        this.objectsPerPage = objectsPerPage;
    }

    public String getTitleId()
    {
        return titleId;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = titleId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(final String version)
    {
        this.version = version;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(final String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(final String comments)
    {
        this.comments = comments;
    }

    public Command getCommand()
    {
        return command;
    }

    public void setCommand(final Command cmd)
    {
        command = cmd;
    }

    private Date parseDate(final String dateString)
    {
        Date date = null;
        try
        {
            if (StringUtils.isNotBlank(dateString))
            {
                final String[] parsePatterns = {"yyyyMMdd"};
                date = DateUtils.parseDate(dateString, parsePatterns);
            }
        }
        catch (final ParseException e)
        {
            //Intentionally left blank
        }
        return date;
    }
}
