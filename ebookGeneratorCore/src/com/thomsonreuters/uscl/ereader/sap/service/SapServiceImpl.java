package com.thomsonreuters.uscl.ereader.sap.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.sap.component.Material;
import com.thomsonreuters.uscl.ereader.sap.exception.SapRequestException;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of SAP service with REST API
 */
public class SapServiceImpl implements SapService {
    private static final Logger LOG = LogManager.getLogger(SapServiceImpl.class);
    private static final String CHECK_STATUS_SUB_NUMBER = "13517355";
    private static final String PLANT_PLACEHOLDER = "\\{plant\\}";
    private static final String SAP_CLIENT_PLACEHOLDER = "\\{sap-client\\}";
    private static final String MATERIAL = "material";
    private static final String BASIC_AUTH = "Basic";

    private final RestTemplate restTemplate;
    private final HttpEntity<String> requestEntity;
    private final String getComponentsByMaterialNumberUrl;

    public SapServiceImpl(
        final String sapLogin,
        final String sapPassword,
        final String getComponentsByMaterialNumberUrl,
        final String plant,
        final String sapClient) {
        final String authorization =
            new Base64Encoder().encode(StringUtils.join(sapLogin, ":", sapPassword).getBytes(StandardCharsets.UTF_8));
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, StringUtils.join(BASIC_AUTH, StringUtils.SPACE, authorization));

        requestEntity = new HttpEntity<>(headers);
        this.getComponentsByMaterialNumberUrl = getComponentsByMaterialNumberUrl.replaceAll(PLANT_PLACEHOLDER, plant)
            .replaceAll(SAP_CLIENT_PLACEHOLDER, sapClient);
        restTemplate = new RestTemplate();
    }

    @NotNull
    @Override
    public Material getMaterialByNumber(@NotNull final String materialNumber) {
        final ResponseEntity<Material> response = restTemplate.exchange(
            getComponentsByMaterialNumberUrl,
            HttpMethod.GET,
            requestEntity,
            Material.class,
            Collections.singletonMap(MATERIAL, materialNumber));

        final HttpStatus responseStatus = response.getStatusCode();
        if (HttpStatus.OK != responseStatus) {
            throw new SapRequestException(responseStatus, materialNumber);
        }
        return response.getBody();
    }

    @NotNull
    @Override
    public SmokeTest checkSapStatus() {
        final SmokeTest status = new SmokeTest();
        status.setName("SAP Service");
        status.setAddress(
            getComponentsByMaterialNumberUrl
                .replaceAll(StringUtils.join("\\{", MATERIAL, "\\}"), CHECK_STATUS_SUB_NUMBER));
        boolean isRunning;
        try {
            getMaterialByNumber(CHECK_STATUS_SUB_NUMBER);
            isRunning = true;
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            isRunning = false;
        }
        status.setIsRunning(isRunning);
        return status;
    }
}
