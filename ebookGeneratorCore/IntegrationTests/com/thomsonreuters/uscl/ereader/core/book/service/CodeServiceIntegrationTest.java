package com.thomsonreuters.uscl.ereader.core.book.service;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodeServiceIntegrationTestConf.class)
@ActiveProfiles("IntegrationTests")
public class CodeServiceIntegrationTest {
    private static final Logger log = LogManager.getLogger(CodeServiceIntegrationTest.class);
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CodeService service;

    @Test
    public void testGetAllPubType() {
        final DbSetup dbSetup = new DbSetup(
            new DataSourceDestination(dataSource),
            sequenceOf(
                Operations.deleteAllFrom("PUB_TYPE_CODES"),
                insertInto("PUB_TYPE_CODES")
                    .columns("PUB_TYPE_CODES_ID", "PUB_TYPE_CODES_NAME", "LAST_UPDATED")
                    .values(1, "Local", new Date())
                    .values(2, "State", new Date())
                    .values(3, "Fed", new Date())
                    .values(4, "FedRule", new Date())
                    .values(5, "FedDist", new Date())
                    .values(6, "Bankr", new Date())
                    .build()));
        dbSetup.launch();

        final List<PubTypeCode> codes = service.getAllPubTypeCodes();
        log.debug(codes);
        Assert.assertEquals(6, codes.size());
    }

    @Test
    public void testPubTypeCodeCRUD() {
        // Create StateCode
        final PubTypeCode createCode = new PubTypeCode();
        createCode.setName("Test");
        service.savePubTypeCode(createCode);

        // Get
        PubTypeCode readCode = service.getPubTypeCodeById(createCode.getId());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());

        // Update
        readCode.setName("Test2");
        service.savePubTypeCode(readCode);

        // Get 2
        readCode = service.getPubTypeCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());

        // Delete
        service.deletePubTypeCode(readCode);
        readCode = service.getPubTypeCodeById(createCode.getId());
        Assert.assertEquals(null, readCode);
    }
}
