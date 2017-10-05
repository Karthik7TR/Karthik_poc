package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AutoPopulatingList;

public class EditGroupDefinitionForm {
    //private static final Logger log = LogManager.getLogger(EditGroupDefinitionForm.class);
    public static final String FORM_NAME = "editGroupDefinitionForm";

    public enum Version {
        NONE,
        MAJOR,
        OVERWRITE
    };

    private Long bookDefinitionId;
    private String fullyQualifiedTitleId;
    private String groupId;
    private String groupType;
    private Version versionType;
    private Boolean hasSplitTitles;
    private Boolean includeSubgroup;
    private Boolean includePilotBook;
    private String groupName;
    private Subgroup notGrouped;
    private List<Subgroup> subgroups;
    private Subgroup pilotBooks;
    private String comment;

    public EditGroupDefinitionForm() {
        super();
        notGrouped = new Subgroup();
        pilotBooks = new Subgroup();
        subgroups = new AutoPopulatingList<>(Subgroup.class);
        hasSplitTitles = false;
        includeSubgroup = false;
    }

    public GroupDefinition createGroupDefinition(final Collection<ProviewTitleInfo> proviewTitleInfos) {
        final GroupDefinition groupDefinition = new GroupDefinition();
        groupDefinition.setGroupId(groupId);
        groupDefinition.setName(groupName);
        groupDefinition.setType(groupType);

        if (includeSubgroup) {
            final List<SubGroupInfo> subgroupInfos = new ArrayList<>();
            for (final Subgroup subgroup : subgroups) {
                final SubGroupInfo subgroupInfo = new SubGroupInfo();
                subgroupInfo.setHeading(subgroup.getHeading());

                for (final Title titleInfo : subgroup.getTitles()) {
                    final String titleId = titleInfo.getTitleId() + "/v" + titleInfo.getVersion();
                    // Set first book as head title
                    if (StringUtils.isBlank(groupDefinition.getHeadTitle())) {
                        groupDefinition.setHeadTitle(titleId);
                    }
                    subgroupInfo.addTitle(titleId);
                }
                subgroupInfos.add(subgroupInfo);
            }
            groupDefinition.setSubGroupInfoList(subgroupInfos);
        } else {
            // No subgroups. Only single titles
            groupDefinition.setHeadTitle(fullyQualifiedTitleId);

            // Get unique fully qualified title ids.
            final Set<String> proViewTitles = new LinkedHashSet<>();
            for (final ProviewTitleInfo info : proviewTitleInfos) {
                proViewTitles.add(info.getTitleId());
            }

            final SubGroupInfo subgroupInfo = new SubGroupInfo();
            subgroupInfo.setTitles(new ArrayList<>(proViewTitles));
            groupDefinition.addSubGroupInfo(subgroupInfo);
        }

        return groupDefinition;
    }

    public void initialize(
        final BookDefinition book,
        final Map<String, ProviewTitleInfo> proviewTitleMap,
        final Map<String, ProviewTitleInfo> pilotBookMap,
        final GroupDefinition group) {
        includeSubgroup = false;
        includePilotBook = false;
        if (book != null) {
            Boolean containsSplitTitle = false;
            for (final ProviewTitleInfo titleInfo : proviewTitleMap.values()) {
                if (titleInfo.getTitleId().matches("^.+\\_pt\\d+$")) {
                    containsSplitTitle = true;
                    break;
                }
            }
            setHasSplitTitles(containsSplitTitle);
        }

        if (group != null) {
            setGroupName(group.getName());

            if (group.subgroupExists()) {
                includeSubgroup = true;

                // Create current ProView group in form
                final List<SubGroupInfo> subgroupInfos = group.getSubGroupInfoList();
                for (final SubGroupInfo subgroupInfo : subgroupInfos) {
                    final Subgroup subgroup = new Subgroup();
                    subgroup.setHeading(subgroupInfo.getHeading());

                    final List<String> titleStrs = subgroupInfo.getTitles();
                    for (final String titleStr : titleStrs) {
                        final ProviewTitleInfo info = proviewTitleMap.remove(titleStr);

                        if (info != null) {
                            final Title title = new Title();
                            title.setProviewName(info.getTitle());
                            title.setVersion(info.getMajorVersion());
                            title.setTitleId(info.getTitleId());
                            subgroup.addTitle(title);
                        }
                    }
                    subgroups.add(subgroup);
                }
            }
        }

        pilotBooks = new Subgroup();
        for (final ProviewTitleInfo titleInfo : pilotBookMap.values()) {
            includePilotBook = true;
            final Title title = new Title();
            title.setVersion(titleInfo.getMajorVersion());
            title.setTitleId(titleInfo.getTitleId());
            pilotBooks.addTitle(title);
        }

        // Add titles that are not subgrouped
        final Subgroup subgroup = new Subgroup();
        for (final ProviewTitleInfo titleInfo : proviewTitleMap.values()) {
            final Title title = new Title();
            title.setProviewName(titleInfo.getTitle());
            title.setVersion(titleInfo.getMajorVersion());
            title.setTitleId(titleInfo.getTitleId());
            subgroup.addTitle(title);
        }
        notGrouped = subgroup;
    }

    public Long getBookDefinitionId() {
        return bookDefinitionId;
    }

    public void setBookDefinitionId(final Long bookDefinitionId) {
        this.bookDefinitionId = bookDefinitionId;
    }

    public String getFullyQualifiedTitleId() {
        return fullyQualifiedTitleId;
    }

    public void setFullyQualifiedTitleId(final String fullyQualifiedTitleId) {
        this.fullyQualifiedTitleId = fullyQualifiedTitleId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(final String groupType) {
        this.groupType = groupType;
    }

    public Version getVersionType() {
        return versionType;
    }

    public void setVersionType(final Version versionType) {
        this.versionType = versionType;
    }

    public Boolean getHasSplitTitles() {
        return hasSplitTitles;
    }

    public void setHasSplitTitles(final Boolean hasSplitTitles) {
        this.hasSplitTitles = hasSplitTitles;
    }

    public Boolean getIncludeSubgroup() {
        return includeSubgroup;
    }

    public void setIncludeSubgroup(final Boolean includeSubgroup) {
        this.includeSubgroup = includeSubgroup;
    }

    public Boolean getIncludePilotBook() {
        return includePilotBook;
    }

    public void setIncludePilotBook(final Boolean includePilotBook) {
        this.includePilotBook = includePilotBook;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public Subgroup getNotGrouped() {
        return notGrouped;
    }

    public void setNotGrouped(final Subgroup notGrouped) {
        this.notGrouped = notGrouped;
    }

    public Subgroup getPilotBooks() {
        return pilotBooks;
    }

    public void setPilotBooks(final Subgroup pilotBooks) {
        this.pilotBooks = pilotBooks;
    }

    public List<Subgroup> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(final List<Subgroup> subgroups) {
        this.subgroups = subgroups;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
