package com.thomsonreuters.uscl.ereader.sap.comparsion;

import java.util.Collection;
import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;

public class MaterialComponentComporator implements Comparator<MaterialComponent> {
    private final Collection<PrintComponent> printComponents;

    public MaterialComponentComporator(final Collection<PrintComponent> printComponents) {
        this.printComponents = printComponents;
    }

    @Override
    public int compare(
        final MaterialComponent firstMaterialComponent,
        final MaterialComponent secondMaterialComponent) {
        final Integer firstOrder = getCurrentOrder(firstMaterialComponent);
        final Integer secondOrder = getCurrentOrder(secondMaterialComponent);

        final Integer result;
        if (firstOrder == null && secondOrder == null) {
            result = 0;
        } else if (firstOrder == null && secondOrder != null) {
            result = 1;
        } else if (firstOrder != null && secondOrder == null) {
            result = -1;
        } else {
            result = firstOrder.compareTo(secondOrder);
        }
        return result;
    }

    private Integer getCurrentOrder(final MaterialComponent materialComponent) {
        Integer order = null;
        for (final PrintComponent printComponent : printComponents) {
            if (printComponent.getMaterialNumber().equals(materialComponent.getBomComponent())) {
                order = printComponent.getComponentOrder();
                break;
            }
        }
        return order;
    }
}
