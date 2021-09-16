package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

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
    @Setter(AccessLevel.NONE)
    private List<SubgroupInfo> subgroupInfoList;
    //For third screen
    @Setter(AccessLevel.NONE)
    private List<GroupDetails> groupDetailList;

    private String groupStatus;
    private String latestUpdateDate;

    public void setSubgroupInfoList(@NotNull final List<SubgroupInfo> list) {
        this.subgroupInfoList = new CopyOnWriteArrayList<>(list);
    }

    public void setGroupDetailList(@NotNull final List<GroupDetails> list) {
        this.groupDetailList = new CopyOnWriteArrayList<>(list);
    }

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
        @Setter(AccessLevel.NONE)
        private List<String> titleIdList;
        private String subGroupName;

        public void setTitleIdList(@NotNull final List<String> list) {
            this.titleIdList = new CopyOnWriteArrayList<>(list);
        }
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
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        }

        public List<String> getTitleIdListWithVersion() {
            return titleInfoList.stream()
                .map(title -> String.format(TITLE_WITH_VERSION_PATTERN, title.getTitleId(), title.getVersion()))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
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
            this.titleInfoList = new CopyOnWriteArrayList<>(titleInfoList);
            this.titleInfoList.stream()
                .filter(this::updateStatus)
                .map(ProviewTitleInfo::getStatus)
                .forEach(status -> bookStatus = status);
        }

        public void addTitleInfo(final ProviewTitleInfo titleInfo) {
            if (updateStatus(titleInfo)) {
                bookStatus = titleInfo.getStatus();
            }
            Optional.ofNullable(titleInfoList)
                .orElseGet(() -> titleInfoList = new CopyOnWriteArrayList<>())
                .add(titleInfo);
        }

        private boolean updateStatus(final ProviewTitleInfo titleInfo) {
            return REVIEW_STATE.equalsIgnoreCase(bookStatus) ? false : REVIEW_STATE.equalsIgnoreCase(titleInfo.getStatus());
        }

        @Override
        public int compareTo(@NotNull final GroupDetails other) {
            try {
                return new Version(other.bookVersion).compareTo(new Version(bookVersion));
            } catch (final Exception e) {
                log.error("Failed to parse version: ", e);
                return 0;
            }
        }
    }
}
