package com.thomsonreuters.uscl.ereader.quality.service;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.quality.helper.FtpManager;
import com.thomsonreuters.uscl.ereader.quality.helper.QualityUtil;
import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ReportServiceImplTest {
    @InjectMocks
    private ReportServiceImpl sut;
    @Mock
    private FtpManager ftpManager;

    @Before
    public void setUp() {
        Whitebox.setInternalState(sut, "qualityUtil", new QualityUtil("some\\ftp\\path"));
    }

    @Test
    public void shouldGetReports() {
        //given
        when(ftpManager.downloadFile(anyString(),
                anyString()))
                .thenReturn(new File("."));
        final JsonResponse response = loadResponse();
        //when
        final List<File> files = sut.getReports(response, "reports/dir");
        //then
        assertEquals(files.size(), response.getResponses().length);
        files.forEach(file -> assertEquals(".", file.getPath()));
    }

    @SneakyThrows
    private JsonResponse loadResponse() {
        final File responseFile = new File(ReportServiceImplTest.class.getResource("response.json").toURI());
        final String responseString = readFileToString(responseFile);
        return new ObjectMapper().readValue(responseString, JsonResponse.class);
    }
}
