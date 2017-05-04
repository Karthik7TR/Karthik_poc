package com.thomsonreuters.uscl.ereader.stats;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class StatsServiceIntegrationTest
{
    @Autowired
    private PublishingStatsService service;
    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp()
    {
        final DbSetup dbSetup = new DbSetup(
            new DataSourceDestination(dataSource),
            Operations.sequenceOf(
                Operations.deleteAllFrom(Arrays.asList(new String[] {"PUBLISHING_STATS", "EBOOK_AUDIT", "DUAL"})),
                Operations.insertInto("DUAL").row().column("PID", 1).end().build(),
                buildAuditEntry(1),
                buildAuditEntry(2),
                buildAuditEntry(3),
                buildAuditEntry(4),
                buildAuditEntry(5),
                buildStatsEntry(1),
                buildStatsEntry(2),
                buildStatsEntry(3)));
        dbSetup.launch();
    }

    @Test
    public void testfindLastPublishDateForBook()
    {
        Assert.assertNull(service.findLastPublishDateForBook(1L));

        Date expected = new DateTime(2017, 1, 1, 3, 3, 3).toDate();
        PublishingStats stats = initStats(4);
        stats.setEbookDefId(1L);
        stats.setPublishEndTimestamp(expected);
        stats.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.updatePublishingStats(stats, StatsUpdateTypeEnum.GENERAL);

        Assert.assertEquals(expected, service.findLastPublishDateForBook(1L));

        expected = new DateTime(2017, 2, 2, 3, 3, 3).toDate();
        stats = initStats(5);
        stats.setEbookDefId(1L);
        stats.setPublishEndTimestamp(expected);
        stats.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.updatePublishingStats(stats, StatsUpdateTypeEnum.GENERAL);

        Assert.assertEquals(expected, service.findLastPublishDateForBook(1L));
    }

    @Test
    public void testgetPreviousPublishingStatsForSameBook()
    {
        final PublishingStats expected = initStats(4);
        expected.setEbookDefId(1L);
        expected.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.savePublishingStats(expected);

        final PublishingStats stats = initStats(5);
        stats.setEbookDefId(1L);
        stats.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.savePublishingStats(stats);

        final PublishingStats actual = service.getPreviousPublishingStatsForSameBook(5L);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindAllPublishingStats()
    {
        final List<PublishingStats> stats = service.findAllPublishingStats();
        Assert.assertEquals(3, stats.size());

        PublishingStats expected = initStats(1);
        PublishingStats actual = stats.get(0);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);

        expected = initStats(2);
        actual = stats.get(1);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);

        expected = initStats(3);
        actual = stats.get(2);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindPublishingStatsByJobId()
    {
        // test object is found
        PublishingStats expected = initStats(1);
        PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);

        // test object was not removed
        actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);

        // test other objects are found
        expected = initStats(2);
        actual = service.findPublishingStatsByJobId(2L);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);

        expected = initStats(3);
        actual = service.findPublishingStatsByJobId(3L);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindPublishingStatsByEbookDef()
    {
        final List<PublishingStats> stats = service.findPublishingStatsByEbookDef(1L);
        Assert.assertEquals(1, stats.size());

        // test object is found
        final PublishingStats expected = initStats(1);
        PublishingStats actual = stats.get(0);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);

        // test object was not removed
        actual = stats.get(0);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testfindPublishingStats()
    {
        PublishingStatsFilter filter = new PublishingStatsFilter();
        List<PublishingStats> stats = service.findPublishingStats(filter);
        Assert.assertEquals(3, stats.size());

        // book ID
        filter = new PublishingStatsFilter(1L);
        stats = service.findPublishingStats(filter);
        Assert.assertEquals(1, stats.size());
        Assert.assertEquals(initStats(1), stats.get(0));

        // from date
        filter = new PublishingStatsFilter(new DateTime(2017, 2, 2, 0, 0, 0).toDate(), null, null, null, null);
        stats = service.findPublishingStats(filter);
        Assert.assertEquals(2, stats.size());

        // before date
        filter = new PublishingStatsFilter(null, new DateTime(2017, 2, 2, 0, 0, 0).toDate(), null, null, null);
        stats = service.findPublishingStats(filter);
        Assert.assertEquals(2, stats.size());

        // titleID
        filter = new PublishingStatsFilter(null, null, "titleid1", null, null);
        stats = service.findPublishingStats(filter);
        Assert.assertEquals(1, stats.size());
        Assert.assertEquals(initStats(1), stats.get(0));

        // bookName
        filter = new PublishingStatsFilter(null, null, null, "name2", null);
        stats = service.findPublishingStats(filter);
        Assert.assertEquals(1, stats.size());
        Assert.assertEquals(initStats(2), stats.get(0));

        // book Definition ID
        filter = new PublishingStatsFilter(null, null, null, null, 3L);
        stats = service.findPublishingStats(filter);
        Assert.assertEquals(1, stats.size());
        Assert.assertEquals(initStats(3), stats.get(0));
    }

    @Test
    public void testnumberOfPublishingStats()
    {
        PublishingStatsFilter filter = new PublishingStatsFilter();
        int actual = service.numberOfPublishingStats(filter);
        Assert.assertEquals(3, actual);

        // isbn
        filter = new PublishingStatsFilter(null, null, "3");
        actual = service.numberOfPublishingStats(filter);
        Assert.assertEquals(1, actual);
    }

    @Test
    public void testfindPublishingStatsSort()
    {
        final PublishingStatsFilter filter = new PublishingStatsFilter();
        PublishingStatsSort sort = new PublishingStatsSort(SortProperty.AUDIT_ID, false, 1, 20);
        List<PublishingStats> stats = service.findPublishingStats(filter, sort);
        Assert.assertEquals(3, stats.size());
        Assert.assertEquals(initStats(3), stats.get(0));
        Assert.assertEquals(initStats(2), stats.get(1));
        Assert.assertEquals(initStats(1), stats.get(2));

        sort = new PublishingStatsSort(SortProperty.AUDIT_ID, true, 1, 20);
        stats = service.findPublishingStats(filter, sort);
        Assert.assertEquals(3, stats.size());
        Assert.assertEquals(initStats(1), stats.get(0));
        Assert.assertEquals(initStats(2), stats.get(1));
        Assert.assertEquals(initStats(3), stats.get(2));
    }

    @Test
    public void testfindAuditInfoByJobId()
    {
        // test object is found
        final EbookAudit expected = initAudit(1);
        EbookAudit actual = service.findAuditInfoByJobId(1L);
        actual = resolveLazyProxy(actual);
        Assert.assertEquals(expected, actual);

        // test object was not removed
        actual = service.findAuditInfoByJobId(1L);
        actual = resolveLazyProxy(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testsavePublishingStats()
    {
        // verify object doesnt exist
        Assert.assertNull(service.findPublishingStatsByJobId(4L));

        // save object
        final PublishingStats expected = initStats(4);
        service.savePublishingStats(expected);

        // verify object exists
        final PublishingStats actual = service.findPublishingStatsByJobId(4L);
        resolveLazyProxies(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsNEW()
    {
        final PublishingStats expected = initStats(4);
        service.updatePublishingStats(expected, StatsUpdateTypeEnum.GENERAL);
        final PublishingStats actual = service.findPublishingStatsByJobId(4L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGATHERTOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setGatherTocNodeCount(4);
        expected.setGatherTocDocCount(4);
        expected.setGatherTocRetryCount(4);
        expected.setGatherTocSkippedCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GATHERTOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGENERATETOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setGatherTocNodeCount(4);
        expected.setGatherTocDocCount(4);
        expected.setGatherTocRetryCount(4);
        expected.setGatherTocSkippedCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GATHERTOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGATHERDOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setGatherDocExpectedCount(4);
        expected.setGatherDocRetrievedCount(4);
        expected.setGatherDocRetryCount(4);
        expected.setGatherMetaExpectedCount(4);
        expected.setGatherMetaRetrievedCount(4);
        expected.setGatherMetaRetryCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GATHERDOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGENERATEDOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setGatherDocExpectedCount(4);
        expected.setGatherDocRetrievedCount(4);
        expected.setGatherDocRetryCount(4);
        expected.setGatherMetaExpectedCount(4);
        expected.setGatherMetaRetrievedCount(4);
        expected.setGatherMetaRetryCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GENERATEDOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGATHERIMAGE()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setGatherImageExpectedCount(4);
        expected.setGatherImageRetrievedCount(4);
        expected.setGatherImageRetryCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GATHERIMAGE);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsTITLEDOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setTitleDocCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.TITLEDOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsTITLEDUPDOCCOUNT()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setTitleDupDocCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.TITLEDUPDOCCOUNT);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsFORMATDOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setFormatDocCount(4);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.FORMATDOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsASSEMBLEDOC()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setBookSize(4L);
        expected.setLargestDocSize(4L);
        expected.setLargestImageSize(4L);
        expected.setLargestPdfSize(4L);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.ASSEMBLEDOC);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsFINALPUBLISH()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.FINALPUBLISH);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertNotNull(actual.getPublishEndTimestamp());
        expected.setPublishEndTimestamp(actual.getPublishEndTimestamp());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGENERAL()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GENERAL);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testupdatePublishingStatsGROUPEBOOK()
    {
        final PublishingStats expected = initStats(1);
        expected.setPublishStatus("testing4");
        expected.setGroupVersion(4L);

        final PublishingStats newStats = initStats(4);
        newStats.setJobInstanceId(1L);

        service.updatePublishingStats(newStats, StatsUpdateTypeEnum.GROUPEBOOK);

        final PublishingStats actual = service.findPublishingStatsByJobId(1L);
        resolveLazyProxies(actual);
        expected.setLastUpdated(actual.getLastUpdated());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testdeleteJobStats()
    {
        PublishingStats stats = service.findPublishingStatsByJobId(1L);
        Assert.assertNotNull(stats);
        service.deleteJobStats(stats);
        stats = service.findPublishingStatsByJobId(1L);
        Assert.assertNull(stats);
    }

    @Test
    public void testhasIsbnBeenPublished()
    {
        Assert.assertFalse(service.hasIsbnBeenPublished("1", "titleid1"));
        final PublishingStats jobstats = initStats(1);
        jobstats.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        Assert.assertTrue(service.hasIsbnBeenPublished("1", "titleid1"));
    }

    @Test
    public void testhasBeenGrouped()
    {
        Assert.assertFalse(service.hasBeenGrouped(4L));
        final PublishingStats jobstats = initStats(4);
        jobstats.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.savePublishingStats(jobstats);
        Assert.assertTrue(service.hasBeenGrouped(4L));
    }

    @Test
    public void testfindLastSuccessfulJobStatsAuditByEbookDef()
    {
        Assert.assertNull(service.findLastSuccessfulJobStatsAuditByEbookDef(4L));

        final PublishingStats stats = initStats(4);
        stats.setPublishStatus(PublishingStats.SUCCESFULL_PUBLISH_STATUS);
        service.savePublishingStats(stats);

        final EbookAudit expected = initAudit(4);

        final EbookAudit actual = service.findLastSuccessfulJobStatsAuditByEbookDef(4L);
        Assert.assertEquals(expected, actual);
    }

    private void resolveLazyProxies(final PublishingStats stats)
    {
        stats.setAudit(resolveLazyProxy(stats.getAudit()));
    }

    private <T> T resolveLazyProxy(@NotNull final T entity)
    {
        if (entity == null)
        {
            throw new NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy)
        {
            return (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        else
        {
            return entity;
        }
    }

    private Insert buildAuditEntry(final int id)
    {
        return Operations.insertInto("EBOOK_AUDIT")
            .row()
            .column("AUDIT_ID", id)
            .column("EBOOK_DEFINITION_ID", id)
            .column("TITLE_ID", "titleid" + id)
            .column("PROVIEW_DISPLAY_NAME", "name" + id)
            .column("COPYRIGHT", "copyright" + id)
            .column("IS_TOC_FLAG", 'Y')
            .column("ENABLE_COPY_FEATURE_FLAG", 'Y')
            .column("IS_DELETED_FLAG", 'Y')
            .column("MATERIAL_ID", "materialId" + id)
            .column("ISBN", "" + id)
            .column("AUDIT_TYPE", "auditType" + id)
            .column("UPDATED_BY", "updatedby" + id)
            .column("LAST_UPDATED", new DateTime(2017, id, id, 0, 0, 0).toDate())
            .column("GROUP_NAME", "groupname" + id)
            .end()
            .build();
    }

    private EbookAudit initAudit(final int id)
    {
        final long l_id = Long.valueOf(id);
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(l_id);
        audit.setEbookDefinitionId(l_id);
        audit.setTitleId("titleid" + id);
        audit.setProviewDisplayName("name" + id);
        audit.setCopyright("copyright" + id);
        audit.setIsTocFlag(true);
        audit.setEnableCopyFeatureFlag(true);
        audit.setIsDeletedFlag(true);
        audit.setMaterialId("materialId" + id);
        audit.setIsbn("" + id);
        audit.setAuditType("auditType" + id);
        audit.setUpdatedBy("updatedby" + id);
        audit.setLastUpdated(new DateTime(2017, id, id, 0, 0, 0).toDate());
        audit.setGroupName("groupname" + id);
        return audit;
    }

    private Insert buildStatsEntry(final int id)
    {
        return Operations.insertInto("PUBLISHING_STATS")
            .row()
            .column("JOB_INSTANCE_ID", id)
            .column("AUDIT_ID", id)
            .column("EBOOK_DEFINITION_ID", id)
            .column("BOOK_VERSION_SUBMITTED", "v" + id + "." + id)
            .column("JOB_HOST_NAME", "hostname" + id)
            .column("JOB_SUBMITTER_NAME", "submittername" + id)
            .column("JOB_SUBMIT_TIMESTAMP", new DateTime(2017, id, id, 0, 0, 0).toDate())
            .column("PUBLISH_START_TIMESTAMP", new DateTime(2017, id, id, 1, 1, 1).toDate())
            .column("GATHER_TOC_NODE_COUNT", id)
            .column("GATHER_TOC_SKIPPED_COUNT", id)
            .column("GATHER_TOC_DOC_COUNT", id)
            .column("GATHER_TOC_RETRY_COUNT", id)
            .column("GATHER_DOC_EXPECTED_COUNT", id)
            .column("GATHER_DOC_RETRY_COUNT", id)
            .column("GATHER_DOC_RETRIEVED_COUNT", id)
            .column("GATHER_META_EXPECTED_COUNT", id)
            .column("GATHER_META_RETRIEVED_COUNT", id)
            .column("GATHER_META_RETRY_COUNT", id)
            .column("GATHER_IMAGE_EXPECTED_COUNT", id)
            .column("GATHER_IMAGE_RETRIEVED_COUNT", id)
            .column("GATHER_IMAGE_RETRY_COUNT", id)
            .column("FORMAT_DOC_COUNT", id)
            .column("ASSEMBLE_DOC_COUNT", id)
            .column("TITLE_DOC_COUNT", id)
            .column("TITLE_DUP_DOC_COUNT", id)
            .column("PUBLISH_STATUS", "testing" + id)
            .column("PUBLISH_END_TIMESTAMP", null)
            .column("LAST_UPDATED", new DateTime(2017, id, id, 3, 3, 3).toDate())
            .column("BOOK_SIZE", id)
            .column("LARGEST_DOC_SIZE", id)
            .column("LARGEST_IMAGE_SIZE", id)
            .column("LARGEST_PDF_SIZE", id)
            .column("GROUP_VERSION", id)
            .end()
            .build();
    }

    private PublishingStats initStats(final int id)
    {
        final long l_id = Long.valueOf(id);
        final PublishingStats expected = new PublishingStats();
        expected.setJobInstanceId(l_id);
        expected.setAudit(initAudit(id));
        expected.setEbookDefId(l_id);
        expected.setBookVersionSubmitted("v" + id + "." + id);
        expected.setJobHostName("hostname" + id);
        expected.setJobSubmitterName("submittername" + id);
        expected.setJobSubmitTimestamp(new DateTime(2017, id, id, 0, 0, 0).toDate());
        expected.setPublishStartTimestamp(new DateTime(2017, id, id, 1, 1, 1).toDate());
        expected.setGatherTocNodeCount(id);
        expected.setGatherTocSkippedCount(id);
        expected.setGatherTocDocCount(id);
        expected.setGatherTocRetryCount(id);
        expected.setGatherDocExpectedCount(id);
        expected.setGatherDocRetryCount(id);
        expected.setGatherDocRetrievedCount(id);
        expected.setGatherMetaExpectedCount(id);
        expected.setGatherMetaRetrievedCount(id);
        expected.setGatherMetaRetryCount(id);
        expected.setGatherImageExpectedCount(id);
        expected.setGatherImageRetrievedCount(id);
        expected.setGatherImageRetryCount(id);
        expected.setFormatDocCount(id);
        expected.setAssembleDocCount(id);
        expected.setAssembleDocCount(id);
        expected.setTitleDocCount(id);
        expected.setTitleDupDocCount(id);
        expected.setPublishStatus("testing" + id);
        expected.setPublishEndTimestamp(null);
        expected.setLastUpdated(new DateTime(2017, id, id, 3, 3, 3).toDate());
        expected.setBookSize(l_id);
        expected.setLargestDocSize(l_id);
        expected.setLargestImageSize(l_id);
        expected.setLargestPdfSize(l_id);
        expected.setGroupVersion(l_id);
        return expected;
    }
}
