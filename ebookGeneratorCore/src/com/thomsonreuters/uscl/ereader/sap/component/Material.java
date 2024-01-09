package com.thomsonreuters.uscl.ereader.sap.component;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Material {
    @JsonProperty("material_no")
    private String materialNumber;
    @JsonProperty("component")
    private List<MaterialComponent> components;

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(final String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public List<MaterialComponent> getComponents() {
        return components;
    }

    public void setComponents(final List<MaterialComponent> components) {
        this.components = components;
    }
}
