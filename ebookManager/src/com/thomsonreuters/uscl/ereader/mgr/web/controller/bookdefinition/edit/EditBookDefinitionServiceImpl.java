package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
import com.thomsonreuters.uscl.ereader.sap.comparsion.MaterialComponentComparatorProvider;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponentsResponse;
import com.thomsonreuters.uscl.ereader.sap.service.SapService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class EditBookDefinitionServiceImpl implements EditBookDefinitionService {
    private static final Logger LOG = LogManager.getLogger(EditBookDefinitionServiceImpl.class);
    private static final String SUB_NUMBER_MESSAGE = "Print Set/Sub Number: %s";
    private static final String SAP_COMPONENTS_NOT_FOUND_MESSAGE =
        "Material components not found, for " + SUB_NUMBER_MESSAGE;
    private static final String INVALID_SUB_NUMBER_MESSAGE = SUB_NUMBER_MESSAGE + " is invalid";

    private CodeService codeService;
    private StateCodeService stateCodeService;
    private File rootCodesWorkbenchLandingStrip;
    private List<String> frontMatterThemes;
    private SapService sapService;
    private MaterialComponentComparatorProvider materialComponentComparatorProvider;

    @Override
    public List<DocumentTypeCode> getDocumentTypes() {
        return codeService.getAllDocumentTypeCodes();
    }

    @Override
    public Map<String, String> getStates() {
        final List<StateCode> codes = stateCodeService.getAllStateCodes();
        final Map<String, String> states = new LinkedHashMap<>();

        for (final StateCode code : codes) {
            states.put(code.getName().toLowerCase(), code.getName());
        }

        return states;
    }

    @Override
    public Map<String, String> getJurisdictions() {
        final List<JurisTypeCode> codes = codeService.getAllJurisTypeCodes();
        final Map<String, String> jurisdictions = new LinkedHashMap<>();

        for (final JurisTypeCode code : codes) {
            jurisdictions.put(code.getName().toLowerCase(), code.getName());
        }

        return jurisdictions;
    }

    @Override
    public List<String> getFrontMatterThemes() {
        return frontMatterThemes;
    }

    public void setFrontMatterThemes(final List<String> frontMatterThemes) {
        this.frontMatterThemes = frontMatterThemes;
    }

    @Override
    public Map<String, String> getPubTypes() {
        final List<PubTypeCode> codes = codeService.getAllPubTypeCodes();
        final Map<String, String> pubTypes = new LinkedHashMap<>();

        for (final PubTypeCode code : codes) {
            pubTypes.put(code.getName().toLowerCase(), code.getName());
        }

        return pubTypes;
    }

    @Override
    public Map<String, String> getPublishers() {
        final List<PublisherCode> codes = codeService.getAllPublisherCodes();
        final Map<String, String> publishers = new LinkedHashMap<>();

        for (final PublisherCode code : codes) {
            publishers.put(code.getName().toLowerCase(), code.getName());
        }

        return publishers;
    }

    @Override
    public List<KeywordTypeCode> getKeywordCodes() {
        return codeService.getAllKeywordTypeCodes();
    }

    @Override
    public DocumentTypeCode getContentTypeById(final Long id) {
        return codeService.getDocumentTypeCodeById(id);
    }

    @Override
    public List<String> getCodesWorkbenchDirectory(final String folder) {
        if (StringUtils.isNotBlank(folder)) {
            final File dir = new File(rootCodesWorkbenchLandingStrip, folder);
            if (dir.exists()) {
                final List<String> files = Arrays.asList(dir.list());
                Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
                return files;
            } else {
                return null;
            }
        } else {
            final List<String> files = Arrays.asList(rootCodesWorkbenchLandingStrip.list());
            Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
            return files;
        }
    }

    @NotNull
    @Override
    public MaterialComponentsResponse getMaterialBySubNumber(
        @NotNull final String subNumber,
        @Nullable final String titleId) {
        final List<MaterialComponent> filteredComponents = new ArrayList<>();
        String responseMessage;

        try {
            final List<MaterialComponent> components = sapService.getMaterialByNumber(subNumber).getComponents();
            for (final MaterialComponent component : components) {
                if ("print".equalsIgnoreCase(component.getMediahlRule())
                    && StringUtils.isBlank(component.getDchainStatus())
                    && new Date(DateTime.now().withZone(DateTimeZone.forID("US/Central")).getMillis())
                        .compareTo(component.getEffectiveDate()) > 0) {
                    filteredComponents.add(component);
                }
            }
            responseMessage = filteredComponents.isEmpty() ? SAP_COMPONENTS_NOT_FOUND_MESSAGE : HttpStatus.OK.name();
        } catch (final HttpStatusCodeException e) {
            LOG.error(e.getMessage(), e);
            responseMessage = getMessageByError(e.getResponseBodyAsString());
        }

        Collections.sort(filteredComponents, materialComponentComparatorProvider.getComparator(titleId));
        return new MaterialComponentsResponse(String.format(responseMessage, subNumber), filteredComponents);
    }

    private String getMessageByError(final String errorText) {
        final String message;
        if (errorText.toLowerCase().contains("not found")) {
            message = SAP_COMPONENTS_NOT_FOUND_MESSAGE;
        } else if (errorText.toLowerCase().contains("enter a valid")) {
            message = INVALID_SUB_NUMBER_MESSAGE;
        } else {
            message = errorText + StringUtils.SPACE + SUB_NUMBER_MESSAGE;
        }
        return message;
    }

    @Required
    public void setCodeService(final CodeService service) {
        codeService = service;
    }

    @Required
    public void setStateCodeService(final StateCodeService service) {
        stateCodeService = service;
    }

    @Required
    public void setRootCodesWorkbenchLandingStrip(final File rootDir) {
        rootCodesWorkbenchLandingStrip = rootDir;
    }

    @Required
    public void setSapService(final SapService sapService) {
        this.sapService = sapService;
    }

    public void setMaterialComponentComparatorProvider(
        final MaterialComponentComparatorProvider materialComponentComparatorProvider) {
        this.materialComponentComparatorProvider = materialComponentComparatorProvider;
    }
}
