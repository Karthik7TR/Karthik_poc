package com.thomsonreuters.uscl.ereader.quality.service;

import static java.util.Collections.singletonList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.thomsonreuters.uscl.ereader.quality.helper.FtpManager;
import com.thomsonreuters.uscl.ereader.quality.helper.QualityUtil;
import com.thomsonreuters.uscl.ereader.quality.domain.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.domain.request.JsonRequest;
import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public final class ComparisonServiceImplTest {
    @InjectMocks
    private ComparisonServiceImpl sut;
    @Mock
    private FtpManager ftpManager;
    @Mock
    private QualityUtil qualityUtil;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JsonRequest jsonRequest;
    private CompareUnit compareUnit = new CompareUnit("source", "targer");
    private List<CompareUnit> compareUnits = singletonList(compareUnit);
    @Mock
    private JsonResponse expectedResponse;

    @Before
    public void setUp() {
        doNothing().when(ftpManager).uploadFile(any());
        when(qualityUtil.createJsonRequest(compareUnits))
                .thenReturn(jsonRequest);
        when(restTemplate.exchange(anyString(),
                eq(HttpMethod.POST),
                eq(new HttpEntity<>(jsonRequest)),
                eq(JsonResponse.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));
    }

    @Test
    public void shouldCompare() {
        final JsonResponse actualResponse = sut.compare(compareUnits);
        assertEquals(expectedResponse, actualResponse);
        verify(ftpManager).uploadFile(eq(compareUnit.getSource()));
        verify(ftpManager).uploadFile(eq(compareUnit.getTarget()));
    }
}
