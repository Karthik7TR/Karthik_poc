package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProviewGroupListFilterForm {
    public static final String FORM_NAME = "proviewGroupListFilterForm";

    public enum GroupCmd {
        REMOVE,
        PROMOTE
    }

    private String groupName;
    private GroupCmd groupCmd;
    private String proviewGroupID;
    private String groupVersion;
    private String groupIdByVersion;
    private String groupStatus;
    private List<String> groupIds;
    private List<String> groupMembers;
    private List<GroupDetails> groupDetails;
    private Long bookDefinitionId;
    private String comments;
    private boolean groupOperation;

    public ProviewGroupListFilterForm(
        final String groupName,
        final Long bookId,
        final List<String> groupIds,
        final String groupID,
        final String groupVersion,
        final boolean groupOperation) {
        this.groupName = groupName;
        this.bookDefinitionId = bookId;
        this.groupIds = groupIds;
        this.proviewGroupID = groupID;
        this.groupVersion = groupVersion;
        this.groupOperation = groupOperation;
    }

    public ProviewAudit createAudit(
        final String titleId,
        final String bookVersion,
        final Date lastUpdate,
        final String command,
        final String comments) {
        final ProviewAudit audit = new ProviewAudit();
        audit.setAuditNote(comments);
        audit.setBookLastUpdated(lastUpdate);
        audit.setBookVersion(bookVersion);
        audit.setProviewRequest(command);
        audit.setRequestDate(new Date());
        audit.setTitleId(titleId);
        audit.setUsername(UserUtils.getAuthenticatedUserName());
        return audit;
    }
}
