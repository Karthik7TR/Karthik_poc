package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Log4j
@Getter @Setter
@ToString(of = {"groupId", "groupStatus", "groupName", "groupVersion", "proviewName"})
@EqualsAndHashCode(of = {"groupName", "proviewName", "groupVersion", "groupId", "groupStatus"})
public class ProviewGroup implements Serializable, Comparable<ProviewGroup> {
    private static final long serialVersionUID = -4229230493652304110L;

    private static final String TITLE_WITH_VERSION_PATTERN = "%s/%s";

    private String proviewName;
    private String groupName;
    private String groupVersion;
    private String groupId;
    private String groupIdByVersion;
    private Integer totalNumberOfVersions;
    private String headTitle;
    //subgroup information parsed from proview xml
    private List<SubgroupInfo> subgroupInfoList;
    //For third screen
    private List<GroupDetails> groupDetailList;

    private String groupStatus;
    private String latestUpdateDate;

    public Integer getVersion() {
        return Optional.of(groupVersion)
            .map(version -> StringUtils.substringAfter(version, "v"))
            .filter(StringUtils::isNotBlank)
            .filter(NumberUtils::isParsable)
            .map(Integer::valueOf)
            .orElse(null);
    }

    @Override
    public int compareTo(final ProviewGroup info) {
        final int version = info.getVersion().compareTo(getVersion());
        return version == 0 ? groupId.compareToIgnoreCase(info.getGroupId()) : version;
    }

    @Getter @Setter
    public static class SubgroupInfo implements Serializable {
        private static final long serialVersionUID = -4229230493652422923L;
        private List<String> titleIdList;
        private String subGroupName;
    }

    @EqualsAndHashCode(of = {"bookVersion", "isPilotBook", "proviewDisplayName", "titleId", "id"})
    public static class GroupDetails implements Serializable, Comparable<GroupDetails> {
        private static final long serialVersionUID = -4229230493652304110L;
        private static final String REVIEW_STATE = "review";

        @Getter
        private List<ProviewTitleInfo> titleInfoList;
        @Getter @Setter
        private String bookStatus;
        @Getter @Setter
        private String subGroupName;
        @Getter @Setter
        private String bookVersion;
        @Getter @Setter
        private String lastupdate;
        @Getter @Setter
        private String id;
        @Getter @Setter
        private String proviewDisplayName;
        @Getter @Setter
        private boolean isPilotBook;

        //These are for titles with no subgroups
        @Getter @Setter
        private String[] titleIdWithVersionArray;
        @Getter @Setter
        private String titleId;

        public List<String> getTitleIdList() {
            return titleInfoList.stream()
                .map(ProviewTitleInfo::getTitleId)
                .collect(Collectors.toCollection(ArrayList::new));
        }

        public List<String> getTitleIdListWithVersion() {
            return titleInfoList.stream()
                .map(title -> String.format(TITLE_WITH_VERSION_PATTERN, title.getTitleId(), title.getVersion()))
                .collect(Collectors.toCollection(ArrayList::new));
        }

        public String getIdWithVersion() {
            return String.format(TITLE_WITH_VERSION_PATTERN, id, bookVersion);
        }

        public Integer getMajorVersion() {
            Integer majorVersion = null;
            final String version = StringUtils.substringAfter(bookVersion, "v");
            final String number = StringUtils.substringBefore(version, ".");
            try {
                if (StringUtils.isNotBlank(number)) {
                    majorVersion = Integer.valueOf(number);
                }
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
            }
            return majorVersion;
        }

        public void setTitleIdList(final List<ProviewTitleInfo> titleInfoList) {
            this.titleInfoList = titleInfoList;
            titleInfoList.stream()
                .filter(this::updateStatus)
                .map(ProviewTitleInfo::getStatus)
                .forEach(status -> bookStatus = status);
        }

        public void addTitleInfo(final ProviewTitleInfo titleInfo) {
            if (updateStatus(titleInfo)) {
                bookStatus = titleInfo.getStatus();
            }
            Optional.ofNullable(titleInfoList)
                .orElseGet(() -> titleInfoList = new ArrayList<>())
                .add(titleInfo);
        }

        private boolean updateStatus(final ProviewTitleInfo titleInfo) {
            return REVIEW_STATE.equalsIgnoreCase(bookStatus) ? false : REVIEW_STATE.equalsIgnoreCase(titleInfo.getStatus());
        }

        @Override
        public int compareTo(final GroupDetails other) {
            try {
                return new Version(other.bookVersion).compareTo(new Version(bookVersion));
            } catch (final Exception e) {
                log.error("Failed to parse version: ", e);
                return 0;
            }
        }
    }
}
