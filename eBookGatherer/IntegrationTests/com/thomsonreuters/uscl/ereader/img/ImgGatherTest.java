package com.thomsonreuters.uscl.ereader.img;

import static com.thomsonreuters.uscl.ereader.img.FileEmptyMatcher.isEmptyFile;
import static com.thomsonreuters.uscl.ereader.img.FileNotEmptyMatcher.isNotEmptyFile;
import static com.thomsonreuters.uscl.ereader.img.GathererResponseMatcher.isGathererResponse;
import static com.thomsonreuters.uscl.ereader.img.ImageMetadataMatcher.imgMetadata;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.img.controller.ImgController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public final class ImgGatherTest {
    private static final String IMG = "/img";

    @Autowired
    private ImgController controller;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private MockMvc mockMvc;

    private File emptyDocToImageManifestFile;
    private File pdfDocToImageManifestFile;
    private File pngDocToImageManifestFile;
    private File incorrectDocToImageManifestFile;

    @Before
    public void setUp() throws URISyntaxException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        emptyDocToImageManifestFile = new File(ImgGatherTest.class.getResource("emptyDocToImg.txt").toURI());
        pdfDocToImageManifestFile = new File(ImgGatherTest.class.getResource("docToImgPdf.txt").toURI());
        pngDocToImageManifestFile = new File(ImgGatherTest.class.getResource("docToImgPng.txt").toURI());
        incorrectDocToImageManifestFile = new File(ImgGatherTest.class.getResource("docToImgIncorrect.txt").toURI());
    }

    @Test
    public void shouldDoNothingIfNoImages() throws Exception {
        // given
        final File dynamicImageDirectory = createDynamicImageDirectory();
        final String body = getRequestBody(dynamicImageDirectory, emptyDocToImageManifestFile);
        // when - then
        mockMvc.perform(post(IMG).contentType(MediaType.APPLICATION_XML).content(body))
            .andExpect(status().isOk())
            .andExpect(model().attribute(EBConstants.GATHER_RESPONSE_OBJECT, isGathererResponse(empty(), 0)));
    }

    @Test
    public void shouldReturnEmptyResponseIfExceptionThrown() throws Exception {
        // given
        final File dynamicImageDirectory = createDynamicImageDirectory();
        final String body = getRequestBody(dynamicImageDirectory, null);
        // when - then
        mockMvc.perform(post(IMG).contentType(MediaType.APPLICATION_XML).content(body))
            .andExpect(status().isOk())
            .andExpect(model().attribute(EBConstants.GATHER_RESPONSE_OBJECT, new GatherResponse()));
    }

    @Test
    public void shouldGetPdfImagesFromNovusAndNotTransform() throws Exception {
        // given
        final File dynamicImageDirectory = createDynamicImageDirectory();
        final String body = getRequestBody(dynamicImageDirectory, pdfDocToImageManifestFile);
        final File imgFile = new File(dynamicImageDirectory, "I80d4da10cfbb11dd89a8baa496db0c9f.pdf");
        final File missingImageGuidsFile = getMissingImagesGuidsFile(dynamicImageDirectory);
        // when - then
        mockMvc.perform(post(IMG).contentType(MediaType.APPLICATION_XML).content(body))
            .andExpect(status().isOk())
            .andExpect(
                model().attribute(
                    EBConstants.GATHER_RESPONSE_OBJECT,
                    isGathererResponse(
                        hasItem(imgMetadata("I412055ebb67411d9947c9ea867b7826a", "I80d4da10cfbb11dd89a8baa496db0c9f")),
                        0)));
        assertThat(imgFile, isNotEmptyFile());
        assertThat(missingImageGuidsFile, isEmptyFile());
    }

    @Test
    public void shouldGetTiffImagesFromNovusAndTransformToPng() throws Exception {
        // given
        final File dynamicImageDirectory = createDynamicImageDirectory();
        final String body = getRequestBody(dynamicImageDirectory, pngDocToImageManifestFile);
        final File imgFile = new File(dynamicImageDirectory, "Idbf7d5404c9311df951cd512f4918fbc.png");
        final File missingImageGuidsFile = getMissingImagesGuidsFile(dynamicImageDirectory);
        // when - then
        mockMvc.perform(post(IMG).contentType(MediaType.APPLICATION_XML).content(body))
            .andExpect(status().isOk())
            .andExpect(
                model().attribute(
                    EBConstants.GATHER_RESPONSE_OBJECT,
                    isGathererResponse(
                        hasItem(imgMetadata("I9597a08b52d811df9beda127186d21b0", "Idbf7d5404c9311df951cd512f4918fbc")),
                        0)));
        assertThat(imgFile, isNotEmptyFile());
        assertThat(missingImageGuidsFile, isEmptyFile());
    }

    @Test
    public void shouldReturnMissingCountIfImagesNotFound() throws Exception {
        // given
        final File dynamicImageDirectory = createDynamicImageDirectory();
        final String body = getRequestBody(dynamicImageDirectory, incorrectDocToImageManifestFile);
        final File missingImageGuidsFile = getMissingImagesGuidsFile(dynamicImageDirectory);
        // when - then
        mockMvc.perform(post(IMG).contentType(MediaType.APPLICATION_XML).content(body))
            .andExpect(status().isOk())
            .andExpect(model().attribute(EBConstants.GATHER_RESPONSE_OBJECT, isGathererResponse(empty(), 1)));
        assertThat(missingImageGuidsFile, isNotEmptyFile());
    }

    @Test
    public void shouldWorkInConcurrent() throws InterruptedException, ExecutionException {
        // given
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ExecutorService executorService = Executors.newCachedThreadPool();
        final List<Future<Boolean>> results = new ArrayList<>();
        // when
        results.add(executorService.submit(new ImageGathererRequest(countDownLatch) {
            @Override
            protected void doRequest() throws Exception {
                shouldGetPdfImagesFromNovusAndNotTransform();
            }
        }));
        results.add(executorService.submit(new ImageGathererRequest(countDownLatch) {
            @Override
            protected void doRequest() throws Exception {
                shouldGetTiffImagesFromNovusAndTransformToPng();
            }
        }));
        results.add(executorService.submit(new ImageGathererRequest(countDownLatch) {
            @Override
            protected void doRequest() throws Exception {
                shouldReturnMissingCountIfImagesNotFound();
            }
        }));
        countDownLatch.countDown();
        executorService.shutdown();
        // then
        for (final Future<Boolean> future : results) {
            assertTrue(future.get());
        }
    }

    private File createDynamicImageDirectory() {
        // Create deep dynamicImageDirectory to have separate missing_image_guids.txt for every test method
        final File parent = new File(temporaryFolder.getRoot(), UUID.randomUUID().toString());
        parent.mkdir();
        final File dynamicImageDirectory = new File(parent, UUID.randomUUID().toString());
        dynamicImageDirectory.mkdir();
        return dynamicImageDirectory;
    }

    private File getMissingImagesGuidsFile(final File dynamicImageDirectory) {
        return new File(dynamicImageDirectory.getParentFile(), "missing_image_guids.txt");
    }

    private String getRequestBody(final File dynamicImageDirectory, final File docToImageManifestFile)
        throws JAXBException {
        final GatherImgRequest gatherImgRequest = new GatherImgRequest();
        gatherImgRequest.setDynamicImageDirectory(dynamicImageDirectory);
        gatherImgRequest.setImgToDocManifestFile(docToImageManifestFile);
        gatherImgRequest.setFinalStage(true);

        final JAXBContext jaxbContext = JAXBContext.newInstance(GatherImgRequest.class);
        final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        final StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(gatherImgRequest, sw);
        return sw.toString();
    }
}
