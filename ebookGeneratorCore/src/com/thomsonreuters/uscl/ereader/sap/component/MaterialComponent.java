package com.thomsonreuters.uscl.ereader.sap.component;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thomsonreuters.uscl.ereader.sap.deserialize.DateNullHandleConverter;

public class MaterialComponent
{
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
    private String materialDesc;
    @JsonProperty("material_type")
    private String materialType;
    @JsonProperty("mediahl_rule")
    private String mediahlRule;
    @JsonProperty("mediall_rule")
    private String mediallRule;
    @JsonProperty("dchain_status")
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

    public String getBomComponent()
    {
        return bomComponent;
    }
    public void setBomComponent(final String bomComponent)
    {
        this.bomComponent = bomComponent;
    }

    public Date getEffectiveDate()
    {
        return effectiveDate;
    }
    public void setEffectiveDate(final Date effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }

    public Date getPlanOrderDate()
    {
        return planOrderDate;
    }
    public void setPlanOrderDate(final Date planOrderDate)
    {
        this.planOrderDate = planOrderDate;
    }

    public String getMaterialDesc()
    {
        return materialDesc;
    }
    public void setMaterialDesc(final String materialDesc)
    {
        this.materialDesc = materialDesc;
    }

    public String getMaterialType()
    {
        return materialType;
    }
    public void setMaterialType(final String materialType)
    {
        this.materialType = materialType;
    }

    public String getMediahlRule()
    {
        return mediahlRule;
    }
    public void setMediahlRule(final String mediahlRule)
    {
        this.mediahlRule = mediahlRule;
    }

    public String getMediallRule()
    {
        return mediallRule;
    }
    public void setMediallRule(final String mediallRule)
    {
        this.mediallRule = mediallRule;
    }

    public String getDchainStatus()
    {
        return dchainStatus;
    }
    public void setDchainStatus(final String dchainStatus)
    {
        this.dchainStatus = dchainStatus;
    }

    public Date getDchainEffectiveDate()
    {
        return dchainEffectiveDate;
    }
    public void setDchainEffectiveDate(final Date dchainEffectiveDate)
    {
        this.dchainEffectiveDate = dchainEffectiveDate;
    }

    public Date getProdDate()
    {
        return prodDate;
    }
    public void setProdDate(final Date prodDate)
    {
        this.prodDate = prodDate;
    }

    public Date getShipDate()
    {
        return shipDate;
    }
    public void setShipDate(final Date shipDate)
    {
        this.shipDate = shipDate;
    }
}
