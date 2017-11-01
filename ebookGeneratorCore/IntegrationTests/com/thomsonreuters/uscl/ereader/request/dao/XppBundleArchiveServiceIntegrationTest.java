package com.thomsonreuters.uscl.ereader.request.dao;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = XppBundleArchiveServiceIntegrationTestConf.class)
@Transactional
@ActiveProfiles("IntegrationTests")
public class XppBundleArchiveServiceIntegrationTest {
    @Autowired
    private XppBundleArchiveService service;
    @Autowired
    private DataSource dataSource;

    public static final String SAME_MATERIAL_NUMBER = "15065010";

    @Before
    public void setUp() {
        final DbSetup dbSetup = new DbSetup(
            new DataSourceDestination(dataSource),
            sequenceOf(
                Operations.deleteAllFrom("XPP_BUNDLE_ARCHIVE"),
                insertInto("XPP_BUNDLE_ARCHIVE")
                    .columns(
                        "XPP_BUNDLE_ARCHIVE_ID",
                        "DATE_TIME",
                        "MESSAGE_ID",
                        "MESSAGE_REQUEST",
                        "MATERIAL_NUMBER",
                        "DELETED",
                        "ARCHIVE_LOCATION",
                        "RESURRECT_COUNT")
                    .values(
                        "666666",
                        new DateTime(2017, 2, 14, 17, 3, 0).toDate(),
                        "I9dac72105cc011e789a4fa23e76ecab4",
                        "message1",
                        SAME_MATERIAL_NUMBER,
                        "N",
                        "/apps/eBookBuilder/cicontent/xpp/archive/2017/06/test_bundle.zip",
                        0)
                    .values(
                        "777777",
                        new DateTime(2017, 2, 16, 17, 3, 0).toDate(),
                        "I9dac72105cc011e789a4fa23e76ecab5",
                        "message2",
                        SAME_MATERIAL_NUMBER,
                        "N",
                        "/apps/eBookBuilder/cicontent/xpp/archive/2017/06/test_bundle.zip",
                        0)
                    .values(
                        "888888",
                        new DateTime(2017, 2, 18, 17, 3, 0).toDate(),
                        "I9dac72105cc011e789a4fa23e76ecab6",
                        "message3",
                        SAME_MATERIAL_NUMBER,
                        "N",
                        "/apps/eBookBuilder/cicontent/xpp/archive/2017/06/test_bundle.zip",
                        0)
                    .build()));
        dbSetup.launch();
    }

    @Test
    public void shouldReturnXppBundleArchive() {
        // given
        final XppBundleArchive expectedBundleArchive = new XppBundleArchive();
        expectedBundleArchive.setXppBundleArchiveId(888888L);
        expectedBundleArchive.setDateTime(new DateTime(2017, 2, 18, 17, 3, 0).toDate());
        expectedBundleArchive.setMessageId("I9dac72105cc011e789a4fa23e76ecab6");
        expectedBundleArchive.setMessageRequest("message3");
        expectedBundleArchive.setMaterialNumber(SAME_MATERIAL_NUMBER);
        expectedBundleArchive.setIsDeleted(false);
        expectedBundleArchive.setEBookSrcPath("/apps/eBookBuilder/cicontent/xpp/archive/2017/06/test_bundle.zip");
        expectedBundleArchive.setResurrectCount(0);
        // when
        final XppBundleArchive xppBundleArchive = service.findByMaterialNumber(SAME_MATERIAL_NUMBER);
        // then
        assertEquals("", expectedBundleArchive, xppBundleArchive);
    }
}
