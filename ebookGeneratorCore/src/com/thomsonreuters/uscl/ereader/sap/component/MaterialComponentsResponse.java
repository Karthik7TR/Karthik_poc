package com.thomsonreuters.uscl.ereader.sap.component;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class MaterialComponentsResponse {
    private final String message;
    private final List<MaterialComponent> materialComponents;

    public MaterialComponentsResponse(
        @NotNull final String message,
        @NotNull final List<MaterialComponent> materialComponents) {
        this.message = message;
        this.materialComponents = Collections.unmodifiableList(materialComponents);
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public List<MaterialComponent> getMaterialComponents() {
        return materialComponents;
    }
}
