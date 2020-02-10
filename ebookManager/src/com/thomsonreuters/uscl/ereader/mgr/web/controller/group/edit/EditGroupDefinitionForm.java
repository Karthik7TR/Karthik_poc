package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AutoPopulatingList;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class EditGroupDefinitionForm {
    public static final String FORM_NAME = "editGroupDefinitionForm";
    private static final String SPLIT_PART_PATTERN = " (eBook";

    public enum VersionType {
        NONE,
        MAJOR,
        OVERWRITE
    };

    private Long bookDefinitionId;
    private String fullyQualifiedTitleId;
    private String groupId;
    private String groupType;
    private VersionType versionType;
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

    public GroupDefinition createGroupDefinition(final Collection<ProviewTitleInfo> proviewTitleInfos, final Map<String, List<String>> titleIdToPartsMap) {
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
                    titleIdToPartsMap.getOrDefault(titleId, Collections.singletonList(titleId)).forEach(subgroupInfo::addTitle);
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
                proViewTitles.add(new BookTitleId(info.getTitleId(), new Version(info.getVersion())).getTitleIdWithMajorVersion());
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
                for (final SubGroupInfo subgroupInfo : group.getSubGroupInfoList()) {
                    final Subgroup subgroup = new Subgroup();
                    subgroup.setHeading(subgroupInfo.getHeading());
                    Map<String, List<String>> titleIdToPartsMap = subgroupInfo.getTitles().stream()
                            .collect(Collectors.groupingBy(item -> new BookTitleId(item).getHeadTitleIdWithMajorVersion()));
                    for (final  Map.Entry<String, List<String>> titleIdToParts : titleIdToPartsMap.entrySet()) {
                        final ProviewTitleInfo info = proviewTitleMap.remove(titleIdToParts.getKey());
                        if (info != null) {
                            final Title title = new Title();
                            title.setProviewName(StringUtils.substringBeforeLast(info.getTitle(), SPLIT_PART_PATTERN));
                            title.setVersion(info.getMajorVersion());
                            title.setTitleId(info.getTitleId());
                            title.setNumberOfParts(titleIdToParts.getValue().size());
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
            title.setNumberOfParts(1);
            subgroup.addTitle(title);
        }
        notGrouped = subgroup;
    }
}
