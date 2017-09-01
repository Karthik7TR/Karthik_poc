package com.thomsonreuters.uscl.ereader.sap.comparsion;

import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;

public interface MaterialComponentComparatorProvider
{
    Comparator<MaterialComponent> getComparator(String bookTitleId);
}
