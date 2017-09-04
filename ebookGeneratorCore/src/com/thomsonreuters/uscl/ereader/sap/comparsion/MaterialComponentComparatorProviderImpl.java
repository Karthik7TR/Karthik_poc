package com.thomsonreuters.uscl.ereader.sap.comparsion;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;
import org.apache.commons.lang3.StringUtils;

public class MaterialComponentComparatorProviderImpl implements MaterialComponentComparatorProvider
{
    private final BookDefinitionService bookDefinitionService;

    public MaterialComponentComparatorProviderImpl(final BookDefinitionService bookDefinitionService)
    {
        this.bookDefinitionService = bookDefinitionService;
    }

    @Override
    public Comparator<MaterialComponent> getComparator(final String bookTitleId)
    {
        final Collection<PrintComponent> printComponents;
        if (StringUtils.isNotBlank(bookTitleId))
        {
            final BookDefinition definition = bookDefinitionService.findBookDefinitionByTitle(bookTitleId);
            final Set<PrintComponent> components = definition == null
                ? Collections.<PrintComponent>emptySet() : definition.getPrintComponents();
            printComponents = components == null ? Collections.<PrintComponent>emptySet() : components;
        }
        else
        {
            printComponents = Collections.emptySet();
        }
        return new MaterialComponentComporator(printComponents);
    }
}
