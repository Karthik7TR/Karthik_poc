package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.sap.comparsion;

import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;

public interface MaterialComponentComparatorProvider {
    Comparator<MaterialComponent> getComparator(String bookTitleId);
}
