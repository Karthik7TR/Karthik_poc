package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

public class EditBookDefinitionServiceImpl implements EditBookDefinitionService
{
    //private static final Logger log = LogManager.getLogger(EditBookDefinitionForm.class);
    private CodeService codeService;
    private File rootCodesWorkbenchLandingStrip;
    private List<String> frontMatterThemes;

    @Override
    public List<DocumentTypeCode> getDocumentTypes()
    {
        return codeService.getAllDocumentTypeCodes();
    }

    @Override
    public Map<String, String> getStates()
    {
        final List<StateCode> codes = codeService.getAllStateCodes();
        final Map<String, String> states = new LinkedHashMap<>();

        for (final StateCode code : codes)
        {
            states.put(code.getName().toLowerCase(), code.getName());
        }

        return states;
    }

    @Override
    public Map<String, String> getJurisdictions()
    {
        final List<JurisTypeCode> codes = codeService.getAllJurisTypeCodes();
        final Map<String, String> jurisdictions = new LinkedHashMap<>();

        for (final JurisTypeCode code : codes)
        {
            jurisdictions.put(code.getName().toLowerCase(), code.getName());
        }

        return jurisdictions;
    }

    @Override
    public List<String> getFrontMatterThemes()
    {
        return frontMatterThemes;
    }

    public void setFrontMatterThemes(final List<String> frontMatterThemes)
    {
        this.frontMatterThemes = frontMatterThemes;
    }

    @Override
    public Map<String, String> getPubTypes()
    {
        final List<PubTypeCode> codes = codeService.getAllPubTypeCodes();
        final Map<String, String> pubTypes = new LinkedHashMap<>();

        for (final PubTypeCode code : codes)
        {
            pubTypes.put(code.getName().toLowerCase(), code.getName());
        }

        return pubTypes;
    }

    @Override
    public Map<String, String> getPublishers()
    {
        final List<PublisherCode> codes = codeService.getAllPublisherCodes();
        final Map<String, String> publishers = new LinkedHashMap<>();

        for (final PublisherCode code : codes)
        {
            publishers.put(code.getName().toLowerCase(), code.getName());
        }

        return publishers;
    }

    @Override
    public List<KeywordTypeCode> getKeywordCodes()
    {
        return codeService.getAllKeywordTypeCodes();
    }

    @Override
    public DocumentTypeCode getContentTypeById(final Long id)
    {
        return codeService.getDocumentTypeCodeById(id);
    }

    @Override
    public List<String> getCodesWorkbenchDirectory(final String folder)
    {
        if (StringUtils.isNotBlank(folder))
        {
            final File dir = new File(rootCodesWorkbenchLandingStrip, folder);
            if (dir.exists())
            {
                final List<String> files = Arrays.asList(dir.list());
                Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
                return files;
            }
            else
            {
                return null;
            }
        }
        else
        {
            final List<String> files = Arrays.asList(rootCodesWorkbenchLandingStrip.list());
            Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
            return files;
        }
    }

    @Required
    public void setCodeService(final CodeService service)
    {
        codeService = service;
    }

    @Required
    public void setRootCodesWorkbenchLandingStrip(final File rootDir)
    {
        rootCodesWorkbenchLandingStrip = rootDir;
    }
}
