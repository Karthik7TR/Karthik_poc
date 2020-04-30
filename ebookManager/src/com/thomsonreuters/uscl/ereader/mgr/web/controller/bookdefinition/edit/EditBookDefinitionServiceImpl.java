package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.JurisTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.book.service.PublisherCodeService;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.Bucket;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.sap.comparsion.MaterialComponentComparatorProvider;
import com.thomsonreuters.uscl.ereader.sap.component.Material;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponentsResponse;
import com.thomsonreuters.uscl.ereader.sap.component.MediaLowLevelRule;
import com.thomsonreuters.uscl.ereader.sap.service.SapService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Service("editBookDefinitionService")
public class EditBookDefinitionServiceImpl implements EditBookDefinitionService {
    private static final Logger LOG = LogManager.getLogger(EditBookDefinitionServiceImpl.class);
    private static final String SUB_NUMBER_MESSAGE = "Print Set/Sub Number: %s";
    private static final String SAP_COMPONENTS_NOT_FOUND_MESSAGE =
        "Material components not found, for " + SUB_NUMBER_MESSAGE;
    private static final String INVALID_SUB_NUMBER_MESSAGE = "Invalid Set/Sub Number: %s";

    private final CodeService codeService;
    private final KeywordTypeCodeSevice keywordTypeCodeSevice;
    private final PublisherCodeService publisherCodeService;
    private final DocumentTypeCodeService documentTypeCodeService;
    private final JurisTypeCodeService jurisTypeCodeService;
    private final StateCodeService stateCodeService;
    private final File rootCodesWorkbenchLandingStrip;
    private final List<String> frontMatterThemes;
    private final SapService sapService;
    private final MaterialComponentComparatorProvider materialComponentComparatorProvider;

    @Autowired
    public EditBookDefinitionServiceImpl(
        final CodeService codeService,
        final KeywordTypeCodeSevice keywordTypeCodeSevice,
        final PublisherCodeService publisherCodeService,
        final DocumentTypeCodeService documentTypeCodeService,
        final JurisTypeCodeService jurisTypeCodeService,
        final StateCodeService stateCodeService,
        @Value("${codes.workbench.root.dir}") final File rootCodesWorkbenchLandingStrip,
        final SapService sapService,
        final MaterialComponentComparatorProvider materialComponentComparatorProvider) {
        this.codeService = codeService;
        this.keywordTypeCodeSevice = keywordTypeCodeSevice;
        this.publisherCodeService = publisherCodeService;
        this.documentTypeCodeService = documentTypeCodeService;
        this.jurisTypeCodeService = jurisTypeCodeService;
        this.stateCodeService = stateCodeService;
        this.rootCodesWorkbenchLandingStrip = rootCodesWorkbenchLandingStrip;
        frontMatterThemes = Arrays.asList("WestLaw Next", "AAJ Press");
        this.sapService = sapService;
        this.materialComponentComparatorProvider = materialComponentComparatorProvider;
    }

    @Override
    public List<DocumentTypeCode> getDocumentTypes() {
        return documentTypeCodeService.getAllDocumentTypeCodes();
    }

    @Override
    public Map<String, List<DocumentTypeCode>> getDocumentTypesByPublishers() {
        return publisherCodeService.getAllPublisherCodes().stream()
                .collect(Collectors.toMap(PublisherCode::getName,
                        PublisherCode::getDocumentTypeCodes));
    }

    @Override
    public Map<String, String> getStates() {
        return buildNamesMap(stateCodeService::getAllStateCodes, StateCode::getName);
    }

    @Override
    public Map<String, String> getJurisdictions() {
        return buildNamesMap(jurisTypeCodeService::getAllJurisTypeCodes, JurisTypeCode::getName);
    }

    @Override
    public List<String> getFrontMatterThemes() {
        return frontMatterThemes;
    }

    @Override
    public Map<String, String> getPubTypes() {
        return buildNamesMap(codeService::getAllPubTypeCodes, PubTypeCode::getName);
    }

    @Override
    public Map<String, String> getPublishers() {
        return buildNamesMap(publisherCodeService::getAllPublisherCodes, PublisherCode::getName);
    }

    private <T> Map<String, String> buildNamesMap(
        final Supplier<Collection<T>> dataSupplier,
        final Function<T, String> getNameFunction) {
        return dataSupplier.get().stream().map(getNameFunction).collect(
            Collectors.toMap(String::toLowerCase, Function.identity(), (oldVal, newVal) -> newVal, LinkedHashMap::new));
    }

    @Override
    public List<String> getBuckets() {
        List<String> buckets = new LinkedList<>();
        for (Bucket bucket : Bucket.values()) {
            buckets.add(bucket.toString());
        }
        return buckets;
    }

    @Override
    public List<KeywordTypeCode> getKeywordCodes() {
        return keywordTypeCodeSevice.getAllKeywordTypeCodes();
    }

    @Override
    public DocumentTypeCode getContentTypeById(final Long id) {
        return documentTypeCodeService.getDocumentTypeCodeById(id);
    }

    @Override
    public List<String> getCodesWorkbenchDirectory(final String folder) {
        if (StringUtils.isNotBlank(folder)) {
            return Optional.of(new File(rootCodesWorkbenchLandingStrip, folder))
                .filter(File::exists)
                .map(dir -> collectFileNames(dir::list))
                .orElse(null);
        } else {
            return collectFileNames(rootCodesWorkbenchLandingStrip::list);
        }
    }

    private List<String> collectFileNames(final Supplier<String[]> filesArraySupplier) {
        return Stream.of(filesArraySupplier.get()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public MaterialComponentsResponse getMaterialBySubNumber(
        @NotNull final String subNumber,
        @Nullable final String setNumber,
        @Nullable final String titleId) {
        String responseMessage;
        List<MaterialComponent> components;

        try {
            components = getMaterialComponents(subNumber, setNumber).distinct()
                .parallel()
                .filter(this::isValidComponent)
                //Here we're merge elements with the same material numbers (bomComponent)
                .collect(
                    Collectors.toMap(MaterialComponent::getBomComponent, Function.identity(), this::mergeSapComponents))
                .values()
                .stream()
                .sorted(materialComponentComparatorProvider.getComparator(titleId))
                .collect(Collectors.toList());

            responseMessage = components.isEmpty() ? SAP_COMPONENTS_NOT_FOUND_MESSAGE : HttpStatus.OK.name();
        } catch (final HttpStatusCodeException e) {
            LOG.error(e.getMessage(), e);
            responseMessage = getMessageByError(e.getResponseBodyAsString());
            components = Collections.emptyList();
        }

        return new MaterialComponentsResponse(String.format(responseMessage, setNumber + "/" + subNumber), components);
    }

    private Stream<MaterialComponent> getMaterialComponents(
        @NotNull final String subNumber,
        @Nullable final String setNumber) {
        final Stream<MaterialComponent> subNumberComponents =
            sapService.getMaterialByNumber(subNumber).getComponents().stream();

        return Optional.ofNullable(setNumber)
            .filter(StringUtils::isNotBlank)
            .map(sapService::getMaterialByNumber)
            .map(Material::getComponents)
            .map(Collection::stream)
            .map(setNumberComponents -> Stream.concat(subNumberComponents, setNumberComponents))
            .orElse(subNumberComponents);
    }

    private boolean isValidComponent(final MaterialComponent component) {
        final boolean isNotFuture = new Date(DateTime.now().withZone(DateTimeZone.forID("US/Central")).getMillis())
            .compareTo(component.getEffectiveDate()) > 0;

        final MediaLowLevelRule lowLevelRule = MediaLowLevelRule.getByRuleValue(component.getMediallRule());
        final boolean isValidLowLevelRule = lowLevelRule != MediaLowLevelRule.UNSUPPORTED
            && (lowLevelRule != MediaLowLevelRule.LL_34 || !"ZFNV".equalsIgnoreCase(component.getMaterialType()));

        return isNotFuture
            && component.getProdDate() != null
            && "print".equalsIgnoreCase(component.getMediahlRule())
            && StringUtils.isBlank(component.getDchainStatus())
            && isValidLowLevelRule;
    }

    private MaterialComponent mergeSapComponents(
        final MaterialComponent existentComponent,
        final MaterialComponent newComponent) {
        final Instant existentEffDate = existentComponent.getEffectiveDate().toInstant();
        final Instant newEffDate = newComponent.getEffectiveDate().toInstant();
        return existentEffDate.isBefore(newEffDate) ? newComponent : existentComponent;
    }

    private String getMessageByError(final String errorText) {
        final String message;
        if (errorText.toLowerCase().contains("not found")) {
            message = SAP_COMPONENTS_NOT_FOUND_MESSAGE;
        } else if (errorText.toLowerCase().contains("enter a valid")) {
            message = INVALID_SUB_NUMBER_MESSAGE;
        } else {
            message = String.join(StringUtils.SPACE, errorText, SUB_NUMBER_MESSAGE);
        }
        return message;
    }
}
