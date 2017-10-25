package com.thomsonreuters.uscl.ereader.sap.component;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thomsonreuters.uscl.ereader.sap.deserialize.DateNullHandleConverter;
import com.thomsonreuters.uscl.ereader.sap.deserialize.StringNullHandleConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MaterialComponent {
    @JsonProperty("bom_component")
    private String bomComponent;
    @JsonProperty("eff_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CTZ")
    @JsonDeserialize(converter = DateNullHandleConverter.class)
    private Date effectiveDate;
    @JsonProperty("plan_order_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CTZ")
    @JsonDeserialize(converter = DateNullHandleConverter.class)
    private Date planOrderDate;
    @JsonProperty("material_desc")
    @JsonDeserialize(converter = StringNullHandleConverter.class)
    private String materialDesc;
    @JsonProperty("material_type")
    private String materialType;
    @JsonProperty("mediahl_rule")
    @JsonDeserialize(converter = StringNullHandleConverter.class)
    private String mediahlRule;
    @JsonProperty("mediall_rule")
    private String mediallRule;
    @JsonProperty("dchain_status")
    @JsonDeserialize(converter = StringNullHandleConverter.class)
    private String dchainStatus;
    @JsonProperty("dchain_eff_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CTZ")
    @JsonDeserialize(converter = DateNullHandleConverter.class)
    private Date dchainEffectiveDate;
    @JsonProperty("prod_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CTZ")
    @JsonDeserialize(converter = DateNullHandleConverter.class)
    private Date prodDate;
    @JsonProperty("ship_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CTZ")
    @JsonDeserialize(converter = DateNullHandleConverter.class)
    private Date shipDate;

    public String getBomComponent() {
        return bomComponent;
    }

    public void setBomComponent(final String bomComponent) {
        this.bomComponent = bomComponent;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getPlanOrderDate() {
        return planOrderDate;
    }

    public void setPlanOrderDate(final Date planOrderDate) {
        this.planOrderDate = planOrderDate;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(final String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(final String materialType) {
        this.materialType = materialType;
    }

    public String getMediahlRule() {
        return mediahlRule;
    }

    public void setMediahlRule(final String mediahlRule) {
        this.mediahlRule = mediahlRule;
    }

    public String getMediallRule() {
        return mediallRule;
    }

    public void setMediallRule(final String mediallRule) {
        this.mediallRule = mediallRule;
    }

    public String getDchainStatus() {
        return dchainStatus;
    }

    public void setDchainStatus(final String dchainStatus) {
        this.dchainStatus = dchainStatus;
    }

    public Date getDchainEffectiveDate() {
        return dchainEffectiveDate;
    }

    public void setDchainEffectiveDate(final Date dchainEffectiveDate) {
        this.dchainEffectiveDate = dchainEffectiveDate;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public void setProdDate(final Date prodDate) {
        this.prodDate = prodDate;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(final Date shipDate) {
        this.shipDate = shipDate;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(bomComponent)
            .append(materialDesc)
            .append(materialType)
            .append(mediahlRule)
            .append(mediallRule)
            .append(dchainStatus)
            .append(effectiveDate)
            .append(planOrderDate)
            .append(dchainEffectiveDate)
            .append(prodDate)
            .append(shipDate)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof MaterialComponent)) {
            return false;
        }

        final MaterialComponent materialComponent = (MaterialComponent) other;
        return new EqualsBuilder().append(bomComponent, materialComponent.bomComponent)
                            .append(materialDesc, materialComponent.materialDesc)
                            .append(materialType, materialComponent.materialType)
                            .append(mediahlRule, materialComponent.mediahlRule)
                            .append(mediallRule, materialComponent.mediallRule)
                            .append(dchainStatus, materialComponent.dchainStatus)
                            .append(effectiveDate, materialComponent.effectiveDate)
                            .append(planOrderDate, materialComponent.planOrderDate)
                            .append(dchainEffectiveDate, materialComponent.dchainEffectiveDate)
                            .append(prodDate, materialComponent.prodDate)
                            .append(shipDate, materialComponent.shipDate)
                            .isEquals();
    }
}
