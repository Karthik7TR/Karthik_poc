package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.sap.comparsion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;
import org.junit.Before;
import org.junit.Test;

public final class MaterialComponentComporatorTest {
    private MaterialComponentComporator materialComponentComporator;

    @Before
    public void onTestSetUp() {
        //given
        materialComponentComporator = new MaterialComponentComporator(getPrintComponents());
    }

    private List<PrintComponent> getPrintComponents() {
        final List<PrintComponent> printComponents = new ArrayList<>();

        PrintComponent printComponent = new PrintComponent();
        printComponent.setComponentOrder(1);
        printComponent.setMaterialNumber("1");
        printComponents.add(printComponent);

        printComponent = new PrintComponent();
        printComponent.setComponentOrder(2);
        printComponent.setMaterialNumber("2");
        printComponents.add(printComponent);

        return printComponents;
    }

    @Test
    public void shouldReturnZeroValue() {
        //when
        final int value = materialComponentComporator.compare(getMaterialComponent("3"), getMaterialComponent("4"));
        //then
        assertThat(value, equalTo(0));
    }

    @Test
    public void shouldReturnNegativeValue() {
        //when
        int value = materialComponentComporator.compare(getMaterialComponent("1"), getMaterialComponent("3"));
        //then
        assertThat(value, lessThan(0));

        //when
        value = materialComponentComporator.compare(getMaterialComponent("1"), getMaterialComponent("2"));
        //then
        assertThat(value, lessThan(0));
    }

    @Test
    public void shouldReturnPositiveValue() {
        //when
        final int value = materialComponentComporator.compare(getMaterialComponent("4"), getMaterialComponent("2"));
        //then
        assertThat(value, greaterThan(0));
    }

    private MaterialComponent getMaterialComponent(final String materialNumber) {
        final MaterialComponent materialComponent = new MaterialComponent();
        materialComponent.setBomComponent(materialNumber);
        return materialComponent;
    }
}
