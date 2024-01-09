package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.MediaType;

public final class ImageServiceTest {
    //private static final Logger log = LogManager.getLogger(ImageServiceTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private static final Long JOB_INSTANCE_ID = 1965L;
    private static final String GUID = "junitBogusGuid";
    private static final String DOC_GUID = "dummyDocGuid";
    private static final ImageMetadataEntityKey METADATA_PK =
        new ImageMetadataEntityKey(JOB_INSTANCE_ID, GUID, DOC_GUID);
    private ImageDao mockImageDao;
    private ImageServiceImpl imageService;

    @Before
    public void setUp() {
        mockImageDao = EasyMock.createMock(ImageDao.class);

        imageService = new ImageServiceImpl();
        imageService.setImageDao(mockImageDao);
    }

    protected ImgMetadataInfo getImgMetadata() {
        final ImgMetadataInfo imgMetadataInfo = new ImgMetadataInfo();
        final Long longValue = Long.valueOf(10);
        final String stringValue = "10";
        imgMetadataInfo.setDimUnit(stringValue);
        imgMetadataInfo.setDocGuid(DOC_GUID);
        imgMetadataInfo.setDpi(longValue);
        imgMetadataInfo.setImgGuid(stringValue);
        imgMetadataInfo.setHeight(longValue);
        imgMetadataInfo.setSize(longValue);
        imgMetadataInfo.setMimeType("img");
        imgMetadataInfo.setWidth(longValue);

        return imgMetadataInfo;
    }

    @Test
    public void testSaveImageMetadataEntity() {
        final ImageMetadataEntity entity =
            new ImageMetadataEntity(METADATA_PK, "titleId", 100L, 200L, 41234L, 1111L, "px", MediaType.IMAGE_PNG);
        EasyMock.expect(mockImageDao.saveImageMetadata(entity)).andReturn(METADATA_PK);
        EasyMock.replay(mockImageDao);

        final ImageMetadataEntityKey pk = imageService.saveImageMetadata(entity);
        Assert.assertNotNull(pk);
        Assert.assertEquals(METADATA_PK, pk);

        EasyMock.verify(mockImageDao);
    }

    @Test
    public void testFindImageMetadata() {
        final long jobInstanceId = System.currentTimeMillis();
        EasyMock.expect(mockImageDao.findImageMetadata(jobInstanceId)).andReturn(Collections.EMPTY_LIST);
        EasyMock.replay(mockImageDao);

        final List<ImageMetadataEntity> list = imageService.findImageMetadata(jobInstanceId);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        EasyMock.verify(mockImageDao);
    }
}
