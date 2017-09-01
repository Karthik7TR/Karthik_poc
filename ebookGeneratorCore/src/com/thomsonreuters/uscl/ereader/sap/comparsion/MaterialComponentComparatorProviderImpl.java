package com.thomsonreuters.uscl.ereader.sap.comparsion;

import java.util.Comparator;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;

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
        final Set<PrintComponent> currentPrintComponents = bookDefinitionService
            .findBookDefinitionByTitle(bookTitleId)
            .getPrintComponents();

        return new MaterialComponentComporator(currentPrintComponents);
    }
}
