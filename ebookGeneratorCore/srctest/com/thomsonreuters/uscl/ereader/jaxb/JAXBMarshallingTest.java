package com.thomsonreuters.uscl.ereader.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage.Operation;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class JAXBMarshallingTest {
    private String filePath = "/tmp/usr/ebook_test";
    private ByteArrayInputStream inStream;
    private ByteArrayOutputStream outStream;

    @Before
    public void setUp() {
        //Intentionally left blank
    }

    @After
    public void tearDown() {
        inStream = null;
        outStream = null;
    }

    @Test
    public void testMarshalEBookBundle() {
        try {
            final JAXBContext context = JAXBContext.newInstance(XppBundle.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final XppBundle expected = new XppBundle();
            expected.setProductTitle("1.0");
            expected.setProductType("ed4abfa40ee548388d39ecad55a0daaa");
            expected.setMaterialNumber("0x8d8d44aef6464c9aL");
            expected.setReleaseDate(new Date());
            expected.setReleaseNumber(5);
            expected.setVolumes("8");
            expected.setBundleRoot("/apps/eBookBuilder/prodcontent/xpp/tileName.gz");

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final XppBundle actual = (XppBundle) unmarshaller.unmarshal(inStream);

            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMarshalEBookRequest() {
        try {
            final JAXBContext context = JAXBContext.newInstance(XppBundleArchive.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final XppBundleArchive expected = new XppBundleArchive();
            expected.setVersion("1.0");
            expected.setMessageId("ed4abfa40ee548388d39ecad55a0daaa");
            expected.setBundleHash("8d8d44aef6464c9ab8326e08edbf96d6");
            expected.setDateTime(new Date());
            expected.setEBookSrcFile(new File("/apps/eBookBuilder/prodcontent/xpp/tileName.gz"));

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final XppBundleArchive actual = (XppBundleArchive) unmarshaller.unmarshal(inStream);

            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testNullFieldsEBookRequest() {
        try {
            final JAXBContext context = JAXBContext.newInstance(XppBundleArchive.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final XppBundleArchive expected = new XppBundleArchive();
            expected.setVersion("1.0");
            expected.setMessageId("ed4abfa40ee548388d39ecad55a0daaa");
            expected.setBundleHash("8d8d44aef6464c9ab8326e08edbf96d6");
            expected.setDateTime(new Date());
            expected.setEBookSrcFile(new File("/apps/eBookBuilder/prodcontent/xpp/tileName.gz"));

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final XppBundleArchive actual = (XppBundleArchive) unmarshaller.unmarshal(inStream);

            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGatherDocRequestMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(GatherDocRequest.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final String[] guidArray = {"a", "b", "c"};
            final Collection<String> guids = new ArrayList<>(Arrays.asList(guidArray));
            final String collectionName = "bogusCollname";
            final File contentDir = new File("/foo");
            final File metadataDir = new File("/bar");
            final GatherDocRequest expected =
                new GatherDocRequest(guids, collectionName, contentDir, metadataDir, true, true);

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final GatherDocRequest actual = (GatherDocRequest) unmarshaller.unmarshal(inStream);
            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGatherImgRequestMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(GatherImgRequest.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final File manifest = new File(filePath);
            final File dynamicImgDir = new File(filePath);
            final long id = 127;
            final boolean isFinal = true;

            final GatherImgRequest expected = new GatherImgRequest(manifest, dynamicImgDir, id, isFinal);
            expected.setXppSourceImageDirectory(Arrays.asList("path1", "path2"));

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final GatherImgRequest actual = (GatherImgRequest) unmarshaller.unmarshal(inStream);
            Assert.assertTrue(expected.equals(actual));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGatherNortRequestMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(GatherNortRequest.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final BookDefinition bookDef = createBookDefinition();
            final List<ExcludeDocument> excludeDocs = createExcludeDocuments(bookDef);
            bookDef.setExcludeDocuments(excludeDocs);
            final List<RenameTocEntry> tocEntries = createRenameTocEntries(bookDef);
            bookDef.setRenameTocEntries(tocEntries);
            final List<String> splitTocGuids = new ArrayList<>();
            splitTocGuids.add("hello");
            splitTocGuids.add("world");

            final GatherNortRequest expected = new GatherNortRequest(
                "domain",
                "filter",
                new File(filePath),
                new Date(),
                excludeDocs,
                tocEntries,
                true,
                true,
                splitTocGuids,
                127);
            TimeUnit.MILLISECONDS.sleep(3);
            // force unmarshalled date to be different from the marshalled date
            // if the default constructor is called again

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final GatherNortRequest actual = (GatherNortRequest) unmarshaller.unmarshal(inStream);
            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGatherResponseMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(GatherResponse.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final GatherResponse expected = new GatherResponse(999, "bogus error message");

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final GatherResponse actual = (GatherResponse) unmarshaller.unmarshal(inStream);
            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGatherTocRequestMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(GatherTocRequest.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final BookDefinition bookDef = createBookDefinition();
            final List<ExcludeDocument> excludeDocs = createExcludeDocuments(bookDef);
            bookDef.setExcludeDocuments(excludeDocs);
            final List<RenameTocEntry> tocEntries = createRenameTocEntries(bookDef);
            bookDef.setRenameTocEntries(tocEntries);
            final List<String> splitTocGuids = new ArrayList<>();
            splitTocGuids.add("hello");
            splitTocGuids.add("world");

            final GatherTocRequest expected = new GatherTocRequest(
                "someGuid",
                "TestCollectionName",
                new File(filePath),
                excludeDocs,
                tocEntries,
                true,
                splitTocGuids,
                127);

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final GatherTocRequest actual = (GatherTocRequest) unmarshaller.unmarshal(inStream);
            Assert.assertEquals(expected, actual);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testJobThrottleConfigMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(JobThrottleConfig.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final int coreThreadPoolSize = 1;
            final boolean stepThrottleEnabled = true;
            final String throttleStepName = "helloworld";
            final String throttleStepNameXppPathway = "helloworldXppPathway";
            final String throttleStepNameXppBundle = "helloworldXppBundle";
            final int throttleStepMaxJobs = 1;
            final JobThrottleConfig expected = new JobThrottleConfig(coreThreadPoolSize, stepThrottleEnabled, throttleStepName,
                                                                     throttleStepNameXppPathway, throttleStepNameXppBundle, throttleStepMaxJobs);

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final JobThrottleConfig actual = (JobThrottleConfig) unmarshaller.unmarshal(inStream);
            Assert.assertTrue(expected.equals(actual));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMiscConfigMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(MiscConfig.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final MiscConfig expected = new MiscConfig(Level.ALL, Level.TRACE, NovusEnvironment.Prod, "hostname", 10);

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final MiscConfig actual = (MiscConfig) unmarshaller.unmarshal(inStream);
            Assert.assertTrue(expected.equals(actual));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSimpleRestServiceResponseMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(SimpleRestServiceResponse.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final long id = 127;
            final SimpleRestServiceResponse expected = new SimpleRestServiceResponse(id, true, "message");

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final SimpleRestServiceResponse actual = (SimpleRestServiceResponse) unmarshaller.unmarshal(inStream);
            Assert.assertTrue(expected.equals(actual));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPlannedOutageMarshalling() {
        try {
            final JAXBContext context = JAXBContext.newInstance(PlannedOutage.class);
            final Marshaller marshaller = context.createMarshaller();
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            final PlannedOutage expected = createPlannedOutage();
            TimeUnit.MILLISECONDS.sleep(3);
            // force unmarshalled date to be different from the marshalled date
            // if the default constructor is called again

            outStream = new ByteArrayOutputStream();
            marshaller.marshal(expected, outStream);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            final PlannedOutage actual = (PlannedOutage) unmarshaller.unmarshal(inStream);
            Assert.assertTrue(outageEquals(expected, actual));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private BookDefinition createBookDefinition() {
        final BookDefinition bookDef = new BookDefinition();
        bookDef.setEbookDefinitionId(127L);

        return bookDef;
    }

    private List<ExcludeDocument> createExcludeDocuments(final BookDefinition bookDef) {
        final List<ExcludeDocument> excludeDocs = new ArrayList<>();
        final ExcludeDocument excludeDoc = new ExcludeDocument();
        excludeDocs.add(excludeDoc);
        excludeDoc.setBookDefinition(bookDef);
        excludeDoc.setDocumentGuid("NABCDEFGHIJKLMNOPQRSTUV");
        excludeDoc.setNote("Test Marshalling");
        excludeDoc.setLastUpdated(new Date());

        return excludeDocs;
    }

    private List<RenameTocEntry> createRenameTocEntries(final BookDefinition bookDef) {
        final List<RenameTocEntry> renameEntries = new ArrayList<>();
        final RenameTocEntry renameEntry = new RenameTocEntry();
        renameEntries.add(renameEntry);
        renameEntry.setBookDefinition(bookDef);
        renameEntry.setTocGuid("NABCDEFGHIJKLMNOPQRSTUV");
        renameEntry.setOldLabel("old");
        renameEntry.setNewLabel("new");
        renameEntry.setNote("Test Marshalling");
        renameEntry.setLastUpdated(new Date());

        return renameEntries;
    }

    private PlannedOutage createPlannedOutage() {
        final OutageType outageType = new OutageType();
        outageType.setId(127L);
        outageType.setSystem("test");
        outageType.setSubSystem("test");
        outageType.setLastUpdated(new Date());
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(127L);
        outage.setOutageType(outageType);
        outage.setStartTime(new Date());
        outage.setEndTime(new Date());
        outage.setReason("test");
        outage.setSystemImpactDescription("test");
        outage.setServersImpacted("a b c");
        outage.setNotificationEmailSent(true);
        outage.setAllClearEmailSent(true);
        outage.setUpdatedBy("test");
        outage.setLastUpdated(new Date());
        outage.setOperation(Operation.SAVE);

        return outage;
    }

    private boolean outageEquals(final PlannedOutage first, final PlannedOutage second) {
        if (first == second)
            return true;
        if (first == null || second == null)
            return false;

        if (first.getId() != second.getId())
            return false;

        if (first.getOutageType() == null) {
            if (second.getOutageType() != null)
                return false;
        } else {
            if (!first.getOutageType().equals(second.getOutageType()))
                return false;
            final OutageType firstOut = first.getOutageType();
            final OutageType secondOut = second.getOutageType();
            if (firstOut.getId() == null) {
                if (secondOut.getId() != null)
                    return false;
            } else if (!firstOut.getId().equals(secondOut.getId()))
                return false;
            if (firstOut.getSystem() == null) {
                if (secondOut.getSystem() != null)
                    return false;
            } else if (!firstOut.getSystem().equals(secondOut.getSystem()))
                return false;
            if (firstOut.getSubSystem() == null) {
                if (secondOut.getSubSystem() != null)
                    return false;
            } else if (!firstOut.getSubSystem().equals(secondOut.getSubSystem()))
                return false;
            if (firstOut.getLastUpdated() == null) {
                if (secondOut.getLastUpdated() != null)
                    return false;
            } else if (!firstOut.getLastUpdated().equals(secondOut.getLastUpdated()))
                return false;
        }

        if (first.getStartTime() == null) {
            if (second.getStartTime() != null)
                return false;
        } else if (!first.getStartTime().equals(second.getStartTime()))
            return false;

        if (first.getEndTime() == null) {
            if (second.getEndTime() != null)
                return false;
        } else if (!first.getEndTime().equals(second.getEndTime()))
            return false;

        if (first.getReason() == null) {
            if (second.getReason() != null)
                return false;
        } else if (!first.getReason().equals(second.getReason()))
            return false;

        if (first.getSystemImpactDescription() == null) {
            if (second.getSystemImpactDescription() != null)
                return false;
        } else if (!first.getSystemImpactDescription().equals(second.getSystemImpactDescription()))
            return false;

        if (first.getServersImpacted() == null) {
            if (second.getServersImpacted() != null)
                return false;
        } else if (!first.getServersImpacted().equals(second.getServersImpacted()))
            return false;

        if (!first.isNotificationEmailSent() == second.isNotificationEmailSent())
            return false;

        if (!first.isAllClearEmailSent() == second.isAllClearEmailSent())
            return false;

        if (first.getUpdatedBy() == null) {
            if (second.getUpdatedBy() != null)
                return false;
        } else if (!first.getUpdatedBy().equals(second.getUpdatedBy()))
            return false;

        if (first.getLastUpdated() == null) {
            if (second.getLastUpdated() != null)
                return false;
        } else if (!first.getLastUpdated().equals(second.getLastUpdated()))
            return false;

        if (first.getOperation() == null) {
            if (second.getOperation() != null)
                return false;
        } else if (!first.getOperation().equals(second.getOperation()))
            return false;

        return true;
    }
}
