package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

@Getter
@Setter
@NoArgsConstructor
public class ProviewTitleForm implements Serializable {
    public static final long serialVersionUID = 3423742385720087L;
    public static final String FORM_NAME = "proviewTitleInfoForm";

    public enum Command {
        REMOVE,
        DELETE,
        PROMOTE
    }

    private String titleId;
    private String version;
    private String status;
    private String lastUpdate;
    private String comments;
    private Command command;

    public ProviewAudit createAudit(final String titleId) {
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

    public ProviewTitleForm(final String titleId, final String version, final String status, final String lastUpdate) {
        super();
        this.titleId = titleId;
        this.version = version;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    private Date parseDate(final String dateString) {
        Date date = null;
        try {
            if (StringUtils.isNotBlank(dateString)) {
                final String[] parsePatterns = {"yyyyMMdd"};
                date = DateUtils.parseDate(dateString, parsePatterns);
            }
        } catch (final ParseException e) {
            //Intentionally left blank
        }
        return date;
    }
}
