package com.thomsonreuters.uscl.ereader.deliver.service;

import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CAN_DELETE_STATUSES;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CAN_REMOVE_STATUSES;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.DIR_DATE_FORMAT;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.ERROR_SIGN_SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.ERROR_SIGN_SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT;

@Service
public class SplitPartsUniteServiceImpl implements SplitPartsUniteService {
    private static final String SPLIT_PART_NUMBER_GROUP = "splitPartNumber";
    private static final Pattern PATTERN = Pattern.compile(String.format(".+ \\(eBook \\d+ of (?<%s>\\d+)\\)", SPLIT_PART_NUMBER_GROUP));
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DIR_DATE_FORMAT);
    private static final String SPLIT_PARTS_PATTERN = " (eBook";

    @Override
    public Map<String, ProviewTitleContainer> getTitlesWithUnitedParts(final Map<String, ProviewTitleContainer> allProviewTitleInfo) {
        return allProviewTitleInfo.values().stream()
                .flatMap(item -> item.getProviewTitleInfos().stream())
                .collect(Collectors.groupingBy(e -> new BookTitleId(new TitleId(e.getTitleId()).getHeadTitleId(), new Version(e.getVersion())).getTitleIdWithVersion()))
                .values().stream()
                .map(this::getUnitedTitleInfo)
                .collect(Collectors.groupingBy(ProviewTitleInfo::getTitleId, Collectors.collectingAndThen(Collectors.toList(), ProviewTitleContainer::new)));
    }

    @NotNull
    private ProviewTitleInfo getUnitedTitleInfo(final List<ProviewTitleInfo> list) {
        final ProviewTitleInfo mainTitle = getMainTitle(list);
        String minStatus = getMinStatus(list);
        String maxLastUpdatedDate = Collections.max(list, Comparator.comparing(this::getParsedDate)).getLastupdate();
        mainTitle.setStatus(minStatus);
        mainTitle.setLastupdate(maxLastUpdatedDate);
        mainTitle.setSplitParts(list.size());
        mainTitle.setTitleId(new TitleId(mainTitle.getTitleId()).getHeadTitleId());
        mainTitle.setTitle(StringUtils.substringBeforeLast(mainTitle.getTitle(), SPLIT_PARTS_PATTERN));
        return mainTitle;
    }

    @NotNull
    private ProviewTitleInfo getMainTitle(final List<ProviewTitleInfo> list) {
        return list.stream()
                .filter(it -> it.getTitleId().equals(new TitleId(it.getTitleId()).getHeadTitleId()))
                .findAny()
                .orElse(list.get(0));
    }

    @NotNull
    private String getMinStatus(final List<ProviewTitleInfo> list) {
        String minStatus = Collections.min(list, Comparator.comparingInt(it -> ProviewStatus.valueOf(it.getStatus()).getPriority())).getStatus();
        final Set<String> statuses = list.stream().map(ProviewTitleInfo::getStatus).collect(Collectors.toSet());
        int splitPartNumber = getSplitPartNumber(list);
        if (splitPartNumber != list.size() || (statuses.size() > 1 && !Collections.disjoint(statuses, CAN_DELETE_STATUSES))) {
            minStatus += ERROR_SIGN_SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT;
        }
        if (splitPartNumber == list.size() && statuses.size() > 1 && statuses.equals(CAN_REMOVE_STATUSES)) {
            minStatus += ERROR_SIGN_SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE;
        }
        return minStatus;
    }

    @NotNull
    private Integer getSplitPartNumber(final List<ProviewTitleInfo> list) {
        return list.stream()
                .map(ProviewTitleInfo::getTitle)
                .filter(it -> it.contains(SPLIT_PARTS_PATTERN))
                .findAny()
                .map(PATTERN::matcher)
                .map(it -> {
                    it.matches();
                    return it.group(SPLIT_PART_NUMBER_GROUP);
                })
                .map(Integer::valueOf)
                .orElse(1);
    }

    @SneakyThrows
    private Date getParsedDate(final ProviewTitleInfo item) {
        return DATE_FORMAT.parse(item.getLastupdate());
    }

}
