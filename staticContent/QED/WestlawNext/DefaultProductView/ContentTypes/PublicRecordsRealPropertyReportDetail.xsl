<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:msxsl="urn:schemas-microsoft-com:xslt" xmlns:settings="urn:XslSettings" extension-element-prefixes="settings" exclude-result-prefixes="msxsl">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsRealProperty.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Response data from First American -->
	<xsl:template match="//RESPONSE_DATA/PROPERTY_INFORMATION_RESPONSE">
		<xsl:if test="STATUS/@_Code = 0400">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="_PROPERTY_INFORMATION">
		<xsl:call-template name="PublicRecordsContent" >
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyDetailClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Render the CONTENT view. -->
	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:param name="reportType" select="normalize-space(@_ReportType)"/>
		<xsl:apply-templates select="PROPERTY" mode="DetailLeft"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:param name="reportType" select="normalize-space(@_ReportType)"/>
		<xsl:apply-templates select="PROPERTY" mode="DetailRight"/>
	</xsl:template>

	<!-- PROPERTY Template for PropertyDetail Report -->
	<xsl:template match="PROPERTY" mode="DetailLeft">
		<xsl:apply-templates select="PROPERTY_OWNER" mode="Detail"/>
		<xsl:call-template name="DetailLocation"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY" mode="Detail"/>
	</xsl:template>

	<!-- PROPERTY Template for PropertyDetail Report -->
	<xsl:template match="PROPERTY" mode="DetailRight">
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS" mode="Detail"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_SITE" mode="Detail"/>
		<xsl:apply-templates select="_PROPERTY_TAX" mode="Detail"/>
	</xsl:template>

	<!-- Property location information for PropertyDetail Report -->
	<xsl:template name="DetailLocation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_locationInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_LEGAL_DESCRIPTION/@_TextDescription"/>
			<xsl:apply-templates select="@_County"/>
			<xsl:apply-templates select="@CensusTractIdentifier"/>
			<xsl:apply-templates select="@_LotIdentifier"/>
			<xsl:apply-templates select="@_BlockIdentifier"/>
			<xsl:apply-templates select="@_LegalBookAndPageIdentifier"/>
			<xsl:apply-templates select="@_AssessorsParcelIdentifier"/>
			<xsl:apply-templates select="@_SubdivisionIdentifier"/>
			<xsl:apply-templates select="_NEIGHBORHOOD_AREA_INFORMATION/@_SchoolDistrictName"/>
			<xsl:apply-templates select="@_Municipality"/>
		</table>
	</xsl:template>

	<xsl:template match="PROPERTY_OWNER" mode="Detail">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_ownerInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_OwnerName"/>
			<xsl:apply-templates select="@_PhoneNumber"/>
			<xsl:call-template name="MailingAddress" />
		</table>
	</xsl:template>

	<!-- Property history information for PropertyDetail Report -->
	<xsl:template match="_PROPERTY_HISTORY" mode="Detail">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_lastMarketSaleInformation;'" />
		</xsl:call-template>
		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastSalesPriceAmount"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastRecordingDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastSalesDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_OneSaleTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_DocumentNumberIdentifier"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_DeedTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_TitleCompanyName"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_SellerName"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_FirstMortgageAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_FirstMortgageInterestRatePercent"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_FirstMortgageInterestRateTypeDescription"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_SecondMortgageAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_SecondMortgageInterestRatePercent"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_SecondMortgageInterestRateTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PricePerSquareFootAmount"/>
		</table>
		<!-- <xsl:call-template name="toplink"/> -->
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_priorSaleInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSalePriceAmount"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSaleDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSaleTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorRecordingDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorDocumentNumberIdentifier"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorDeedTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorLenderName"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorStampsAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_PriorFirstMortgageAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_PriorFirstMortgageInterestRatePercent"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_PriorFirstMortgageInterestRateTypeDescription"/>
		</table>
	</xsl:template>

	<!-- Property characteristics information for PropertyDetail Report-->
	<xsl:template match="_PROPERTY_CHARACTERISTICS" mode="Detail">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_propertyCharacteristics;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalLivingAreaSquareFeetNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalRoomCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalBedroomsCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalBathsCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_YearBuiltDateIdentifier"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_FEATURES/_POOL/@_HasFeatureIndicator"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_COOLING/@_CentralizedIndicator"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_FEATURES/_FIREPLACES"/>
			<xsl:apply-templates select="following-sibling::PROPERTY_ANALYSIS/_IMPROVEMENT_ANALYSIS/@_OtherPropertyImprovementsDescription"/>			
			<xsl:apply-templates select="_IMPROVEMENTS/_PARKING/@_TypeDescription"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_PARKING/@_GarageSquareFeetNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_PARKING/@_GarageTotalCarCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_TotalStoriesNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_BASEMENT/@_AreaSquareFeet"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_EXTERIOR_DESCRIPTION/@_RoofTypeDescription"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_EXTERIOR_DESCRIPTION/@_RoofSurfaceDescription"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_FOUNDATION/@_MaterialDescription"/>
			<xsl:apply-templates select="following-sibling::PROPERTY_ANALYSIS/_IMPROVEMENT_ANALYSIS/_IMPROVEMENT_RATINGS/@_ConstructionQualityTypeDescription"/>
			<xsl:apply-templates select="following-sibling::PROPERTY_ANALYSIS/_IMPROVEMENT_ANALYSIS/@_ConditionsDescription"/>
		</table>
	</xsl:template>

	<!-- Property site characteristics information for PropertyDetail Report-->
	<xsl:template match="_PROPERTY_CHARACTERISTICS/_SITE" mode="Detail">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_siteInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_CHARACTERISTICS/@_LandUseDescription"/>
			<xsl:apply-templates select="_FLOOD_ZONE/@FloodZoneIdentifier"/>
			<xsl:apply-templates select="_FLOOD_ZONE/@NFIPMapIdentifier"/>
			<xsl:apply-templates select="_FLOOD_ZONE/@NFIPMapPanelDate"/>
			<xsl:apply-templates select="_DIMENSIONS/@_LotAreaAcresNumber"/>
			<xsl:apply-templates select="_DIMENSIONS/@_LotAreaSquareFeetNumber"/>

			<tr>
				<xsl:attribute name="class">&pr_item;</xsl:attribute>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="'&pr_lotWidthDepth;'"/>
				</xsl:call-template>
				<td >
					<xsl:apply-templates select="_DIMENSIONS/@_LotWidthNumber"/>
					<xsl:apply-templates select="_DIMENSIONS/@_LotDepthNumber"/>
				</td>
			</tr>
			<xsl:apply-templates select="_ZONING/@_ClassificationIdentifier"/>
		</table>
	</xsl:template>

	<!-- Property Tax information for PropertyDetail Report-->
	<xsl:template match="_PROPERTY_TAX" mode="Detail">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_taxInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_TotalAssessedValueAmount"/>
			<xsl:apply-templates select="@_LandValueAmount"/>
			<xsl:apply-templates select="@_ImprovementValueAmount"/>
			<xsl:apply-templates select="@_AssessmentYear"/>
			<xsl:apply-templates select="@_DelinquentYear"/>
			<xsl:apply-templates select="@_ExemptionTypeDescription"/>
			<xsl:apply-templates select="@_RealEstateTotalTaxAmount"/>
			<xsl:apply-templates select="@_RateAreaIdentifier"/>
			<xsl:apply-templates select="@_TaxYear"/>
		</table>
	</xsl:template>

	<xsl:template match="_PARSED_STREET_ADDRESS">
		<xsl:apply-templates select="@_HouseNumber"/>
		<xsl:apply-templates select="@_DirectionPrefix"/>
		<xsl:apply-templates select="@_StreetName"/>
		<xsl:apply-templates select="@_StreetSuffix"/>
		<xsl:apply-templates select="@_DirectionSuffix"/>
		<xsl:apply-templates select="@_ApartmentOrUnit"/>
	</xsl:template>

	<!-- Format the standard date from string -->
	<xsl:template name="standard_date">
		<xsl:param name="date"/>
		<xsl:if test="$date != '' and $date != 0">
			<!-- Month -->
			<xsl:value-of select="substring($date, 5, 2)"/>
			<xsl:text>/</xsl:text>
			<!-- Day -->
			<xsl:value-of select="substring($date, 7, 2)"/>
			<xsl:text>/</xsl:text>
			<!-- Year -->
			<xsl:value-of select="substring($date, 1, 4)"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_propertyDetailReportRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@_LotWidthNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
			<xsl:value-of select="$nodevalue"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@_LotDepthNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
			<xsl:text>x</xsl:text>
			<xsl:value-of select="$nodevalue"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@_TextDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_legalDescription;'"/>
			</xsl:call-template>
			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_PhoneNumber">
		<xsl:param name="phone" select="normalize-space(.)"/>
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_ownerPhoneNumber;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$phone" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SchoolDistrictName">
		<xsl:param name="districtName" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_schoolDistrict;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$districtName" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SecondMortgageInterestRateTypeDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_secondMortgageInterestRateType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_PhoneNumber">
		<xsl:param name="phone" select="normalize-space(.)"/>
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_ownerPhoneNumber;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$phone" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_DeedTypeDescription">
		<xsl:param name="deedType" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_deedType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$deedType" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_TitleCompanyName">
		<xsl:param name="companyName" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_titleCompany;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$companyName" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_FirstMortgageInterestRatePercent">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_firstMortgageInterestRate;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_FirstMortgageInterestRateTypeDescription">
		<xsl:param name="description" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_firstMortgageInterestRateType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$description" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SecondMortgageInterestRatePercent">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_secondMortgageInterestRate;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_PricePerSquareFootAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_pricePerSquareFoot;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:variable name="price">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($price)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorSaleTypeDescription">
		<xsl:param name="description" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorSaleType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$description" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorRecordingDate">
		<xsl:variable name="recordingDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="." />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="PrintSaleDate">
			<xsl:with-param name="name" select="'&pr_priorRecordingDate;'" />
			<xsl:with-param name="value" select="$recordingDate" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@_PriorDocumentNumberIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorDocNumber;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorLenderName">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorLender;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorStampsAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorStampAmount;'"/>
			</xsl:call-template>
			<xsl:if test="$nodevalue !=''">
				<xsl:variable name="value">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($value)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorFirstMortgageInterestRatePercent">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorFirstMortgageInterestRate;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorFirstMortgageInterestRateTypeDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorFirstMortgageInterestRateType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_OtherPropertyImprovementsDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_otherImprovements;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ConditionsDescription">
		<xsl:param name="description" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_condition;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$description"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_TypeDescription">
		<xsl:param name="parkingType" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_parkingType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$parkingType" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_GarageSquareFeetNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_garageArea;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_RoofTypeDescription">
		<xsl:param name="description" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_roofType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$description"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ConstructionQualityTypeDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_quality;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ClassificationIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_zoning;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template name="MailingAddress">
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mailingAddress;'" />
			</xsl:call-template>

			<xsl:variable name="address">
				<xsl:apply-templates select="@_MailingAddress"/>
				<xsl:apply-templates select="@_MailingCityAndState" mode="detail"/>
				<xsl:apply-templates select="@_MailingPostalCode"/>
			</xsl:variable>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="normalize-space($address)" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_MailingCityAndState" mode="detail">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>
