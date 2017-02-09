package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProviewGroupListFilterForm
{
    public static final String FORM_NAME = "proviewGroupListFilterForm";

    public enum FilterCommand
    {
        SEARCH,
        RESET
    };

    public enum GroupCmd
    {
        REMOVE,
        DELETE,
        PROMOTE
    };

    private String groupName;
    private FilterCommand filterCommand;

    private GroupCmd groupCmd;
    private String proviewGroupID;
    private String groupVersion;
    private String groupStatus;
    private List<String> groupIds;
    private List<String> groupMembers;
    private List<GroupDetails> groupDetails;
    private Long bookDefinitionId;
    private String comments;
    private boolean groupOperation;

    public boolean isGroupOperation()
    {
        return groupOperation;
    }

    public void setGroupOperation(final boolean groupOperation)
    {
        this.groupOperation = groupOperation;
    }

    private String groupIdByVersion;

    public String getGroupIdByVersion()
    {
        return groupIdByVersion;
    }

    public void setGroupIdByVersion(final String groupIdByVersion)
    {
        this.groupIdByVersion = groupIdByVersion;
    }

    public ProviewGroupListFilterForm()
    {
        //Intentionally left blank
    }

    public List<String> getGroupIds()
    {
        return groupIds;
    }

    public void setGroupIds(final List<String> groupIds)
    {
        this.groupIds = groupIds;
    }

    public List<String> getGroupMembers()
    {
        return groupMembers;
    }

    public void setGroupMembers(final List<String> groupMembers)
    {
        this.groupMembers = groupMembers;
    }

    public List<GroupDetails> getGroupDetails()
    {
        return groupDetails;
    }

    public void setGroupDetails(final List<GroupDetails> groupDetails)
    {
        this.groupDetails = groupDetails;
    }

    public String getProviewGroupID()
    {
        return proviewGroupID;
    }

    public Long getBookDefinitionId()
    {
        return bookDefinitionId;
    }

    public void setBookDefinitionId(final Long bookDefinitionId)
    {
        this.bookDefinitionId = bookDefinitionId;
    }

    public void setProviewGroupID(final String groupID)
    {
        proviewGroupID = groupID;
    }

    public String getGroupVersion()
    {
        return groupVersion;
    }

    public String getGroupStatus()
    {
        return groupStatus;
    }

    public void setGroupStatus(final String groupStatus)
    {
        this.groupStatus = groupStatus;
    }

    public void setGroupVersion(final String groupVersion)
    {
        this.groupVersion = groupVersion;
    }

    public GroupCmd getGroupCmd()
    {
        return groupCmd;
    }

    public void setGroupCmd(final GroupCmd groupCmd)
    {
        this.groupCmd = groupCmd;
    }

    public void initNull()
    {
        init(null, null);
    }

    private void init(final String proviewGroupID, final String groupName)
    {
        this.proviewGroupID = proviewGroupID;
        this.groupName = groupName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(final String groupName)
    {
        this.groupName = groupName;
    }

    public FilterCommand getFilterCommand()
    {
        return filterCommand;
    }

    public void setFilterCommand(final FilterCommand filterCommand)
    {
        this.filterCommand = filterCommand;
    }

    public ProviewAudit createAudit(
        final String titleId,
        final String bookVersion,
        final Date lastUpdate,
        final String command,
        final String comments)
    {
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

    public String getComments()
    {
        return comments;
    }

    public void setComments(final String comments)
    {
        this.comments = comments;
    }

    public ProviewGroupListFilterForm(
        final String groupName,
        final Long bookId,
        final List<String> groupIds,
        final String groupID,
        final String groupVersion,
        final String groupByVersion,
        final boolean groupOperation)
    {
        this.groupName = groupName;
        bookDefinitionId = bookId;
        this.groupIds = groupIds;
        bookDefinitionId = bookId;
        proviewGroupID = groupID;
        this.groupVersion = groupVersion;
        this.groupOperation = groupOperation;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
