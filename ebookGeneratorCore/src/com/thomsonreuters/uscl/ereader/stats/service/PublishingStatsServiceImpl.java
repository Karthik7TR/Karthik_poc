package com.thomsonreuters.uscl.ereader.stats.service;

import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import lombok.SneakyThrows;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("publishingStatsService")
public class PublishingStatsServiceImpl implements PublishingStatsService {
    public static final String AUTOMATIC_UPDATE = "AUTOMATIC UPDATE";
    public static final String DOES_NOT_EXIST_ON_PROVIEW = "does not exist";

    private static final Logger LOG = LogManager.getLogger(PublishingStatsServiceImpl.class);

    private final EBookAuditService eBookAuditService;
    private final BookDefinitionService bookDefinitionService;
    private final PublishingStatsDao publishingStatsDAO;
    private final PublishingStatsUtil publishingStatsUtil;
    private final ProviewHandler proviewHandler;

    @Autowired
    public PublishingStatsServiceImpl(final PublishingStatsDao publishingStatsDAO, final PublishingStatsUtil publishingStatsUtil,
        final EBookAuditService eBookAuditService, final BookDefinitionService bookDefinitionService,
        final ProviewHandler proviewHandler) {
        this.publishingStatsDAO = publishingStatsDAO;
        this.publishingStatsUtil = publishingStatsUtil;
        this.eBookAuditService = eBookAuditService;
        this.bookDefinitionService = bookDefinitionService;
        this.proviewHandler = proviewHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public Date getSysDate() {
        return publishingStatsDAO.getSysDate();
    }

    @Override
    @Transactional(readOnly = true)
    public PublishingStats findPublishingStatsByJobId(final Long JobId) {
        return publishingStatsDAO.findJobStatsByJobId(JobId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStatsByEbookDef(final Long EbookDefId) {
        return publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStats(
        final PublishingStatsFilter filter,
        final PublishingStatsSort sort) {
        return publishingStatsDAO.findPublishingStats(filter, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStats(final PublishingStatsFilter filter) {
        return publishingStatsDAO.findPublishingStats(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public int numberOfPublishingStats(final PublishingStatsFilter filter) {
        return publishingStatsDAO.numberOfPublishingStats(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findAuditInfoByJobId(final Long jobId) {
        return publishingStatsDAO.findAuditInfoByJobId(jobId);
    }

    @Override
    @Transactional
    public void savePublishingStats(final PublishingStats jobstats) {
        publishingStatsDAO.saveJobStats(jobstats);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updatePublishingStats(final PublishingStats newstats, final StatsUpdateTypeEnum updateType) {
        PublishingStats stats = publishingStatsDAO.findJobStatsByJobId(newstats.getJobInstanceId());
        final Date rightNow = publishingStatsDAO.getSysDate();
        if (stats == null) {
            stats = newstats;
        } else {
            switch (updateType) {
            case GATHERTOC:
            case GENERATETOC:
                stats.setGatherTocNodeCount(newstats.getGatherTocNodeCount());
                stats.setGatherTocDocCount(newstats.getGatherTocDocCount());
                stats.setGatherTocRetryCount(newstats.getGatherTocRetryCount());
                stats.setGatherTocSkippedCount(newstats.getGatherTocSkippedCount());
                break;
            case GATHERDOC:
            case GENERATEDOC:
                stats.setGatherDocExpectedCount(newstats.getGatherDocExpectedCount());
                stats.setGatherDocRetrievedCount(newstats.getGatherDocRetrievedCount());
                stats.setGatherDocRetryCount(newstats.getGatherDocRetryCount());
                stats.setGatherMetaExpectedCount(newstats.getGatherMetaExpectedCount());
                stats.setGatherMetaRetrievedCount(newstats.getGatherMetaRetrievedCount());
                stats.setGatherMetaRetryCount(newstats.getGatherMetaRetryCount());
                break;
            case GATHERIMAGE:
                stats.setGatherImageExpectedCount(newstats.getGatherImageExpectedCount());
                stats.setGatherImageRetrievedCount(newstats.getGatherImageRetrievedCount());
                stats.setGatherImageRetryCount(newstats.getGatherImageRetryCount());
                break;
            case TITLEDOC:
                stats.setTitleDocCount(newstats.getTitleDocCount());
                break;
            case TITLEDUPDOCCOUNT:
                stats.setTitleDupDocCount(newstats.getTitleDupDocCount());
                break;
            case FORMATDOC:
                stats.setFormatDocCount(newstats.getFormatDocCount());
                break;
            case ASSEMBLEDOC:
                stats.setBookSize(newstats.getBookSize());
                stats.setLargestDocSize(newstats.getLargestDocSize());
                stats.setLargestImageSize(newstats.getLargestImageSize());
                stats.setLargestPdfSize(newstats.getLargestPdfSize());
                stats.setAssembleDocCount(newstats.getAssembleDocCount());
                break;
            case FINALPUBLISH:
                stats.setPublishEndTimestamp(rightNow);
                break;
            case GENERAL:
                break;
            case GROUPEBOOK:
                stats.setGroupVersion(newstats.getGroupVersion());
                break;
            default:
                LOG.error("Unknown StatsUpdateTypeEnum");
                // TODO: failure logic
            }
            stats.setPublishStatus(newstats.getPublishStatus());
        }
        stats.setLastUpdated(rightNow);
        publishingStatsDAO.saveJobStats(stats);
    }

    @Override
    @Transactional
    public void deleteJobStats(final PublishingStats jobStats) {
        publishingStatsDAO.deleteJobStats(jobStats);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasIsbnBeenPublished(final String isbn) {
        final String digitalIsbn = isbn.replace("-", "");

        final Set<String> publishedIsbns = publishingStatsDAO.findSuccessfullyPublishedIsbns();

        return publishedIsbns.stream()
            .filter(Objects::nonNull)
            .map(publishedIsbn -> publishedIsbn.replace("-", ""))
            .anyMatch(digitalIsbn::equalsIgnoreCase);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasBeenGrouped(final Long ebookDefId) {
        final Long previousGroupBook = publishingStatsDAO.findSuccessfullyPublishedGroupBook(ebookDefId);
        return previousGroupBook != null;
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(final Long EbookDefId) {
        EbookAudit lastAuditSuccessful = null;

        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null && !publishingStats.isEmpty()) {
            lastAuditSuccessful = publishingStats.stream()
                    .filter(stats -> publishingStatsUtil.isPublishedSuccessfully(stats.getPublishStatus()))
                    .max(comparingLong(PublishingStats::getJobInstanceId))
                    .map(PublishingStats::getAudit)
                    .orElse(null);
        }
        return lastAuditSuccessful;
    }

    @Override
    @Transactional(readOnly = true)
    public Date findLastPublishDateForBook(final Long EbookDefId) {
        Date lastPublishDate = null;
        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null) {
            lastPublishDate = publishingStats.stream()
                .map(PublishingStats::getPublishEndTimestamp)
                .filter(Objects::nonNull)
                .min(Comparator.reverseOrder())
                .orElse(null);
        }

        return lastPublishDate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findAllPublishingStats() {
        return publishingStatsDAO.findAllPublishingStats();
    }

    @Transactional(readOnly = true)
    @Override
    public PublishingStats getPreviousPublishingStatsForSameBook(final long jobInstanceId) {
        return publishingStatsDAO.getPreviousPublishingStatsForSameBook(jobInstanceId);
    }

    @Transactional(readOnly = true)
    @Override
    public void deleteIsbn(final String titleId, final String version) {
        final String isbn = getIsbnByTitleAndVersion(titleId, version);
        if (canBeDeleted(isbn) && isLastVersionWithIsbn(titleId, isbn)) {
            final Optional<EbookAudit> auditUpdated = eBookAuditService.modifyIsbn(titleId, isbn, EbookAuditDao.DELETE_ISBN_TEXT);
            if (auditUpdated.isPresent()) {
                final Long bookDefinitionId = auditUpdated.get().getEbookDefinitionId();
                final BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
                eBookAuditService.restoreIsbn(book, AUTOMATIC_UPDATE);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String getIsbnByTitleAndVersion(final String title, final String fullVersion) {
        final String version = new Version(fullVersion).getVersionWithoutPrefix();
        return publishingStatsDAO.findSuccessfullyPublishedIsbnByTitleIdAndVersion(title, version);
    }

    private boolean canBeDeleted(String isbn) {
        return isbn != null && !isbn.contains(EbookAuditDao.DELETE_ISBN_TEXT)
            && !isbn.contains(EbookAuditDao.MODIFY_ISBN_TEXT);
    }

    @SneakyThrows
    private boolean isLastVersionWithIsbn(final String titleId, final String isbnToFind) {
        try {
            return isContainsIsbn(proviewHandler.getProviewTitleContainer(titleId), titleId, isbnToFind);
        } catch (ProviewException e) {
            if (e.getMessage().contains(DOES_NOT_EXIST_ON_PROVIEW)) {
                return true;
            } else {
                throw e;
            }
        }
    }

    private boolean isContainsIsbn(ProviewTitleContainer titleContainer, final String titleId, final String isbn) {
        return titleContainer.getProviewTitleInfos().stream()
            .map(titleInfo -> getIsbnByTitleAndVersion(titleId, titleInfo.getVersion()))
            .noneMatch(isbn::equalsIgnoreCase);
    }
}
