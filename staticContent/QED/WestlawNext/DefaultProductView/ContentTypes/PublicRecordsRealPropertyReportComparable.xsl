<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsRealProperty.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="/">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyComparableClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!-- Renders the report/document header-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_comparableSalesHeader;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- renders the main column using the Public Records styles-->
	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="//_DATA_PROVIDER_COMPARABLE_SALES" mode="CompSales"/>
	</xsl:template>

	<!-- Comparable properties for ComparableSales Report-->
	<xsl:template match="_DATA_PROVIDER_COMPARABLE_SALES" mode="CompSales">
		<xsl:apply-templates select="@_ComparableNumber" mode="CompSales"/>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_DistanceFromSubjectNumber"/>
		</table>
		<xsl:apply-templates select="PROPERTY" mode="CompSales"/>
	</xsl:template>

	<!--Builds each comparable sections to contain the comparable information -->
	<xsl:template match="@_ComparableNumber" mode="CompSales">
		<xsl:param name="count" select="normalize-space(.)"/>

		<xsl:variable name="subHeadingTitle">
			<xsl:text>&pr_comparableSubHeader;</xsl:text>
			<xsl:value-of select="$count"/>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="$subHeadingTitle" />
		</xsl:call-template>
	</xsl:template>

	<!--gets the distance in miles from the subject property-->
	<xsl:template match="@_DistanceFromSubjectNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:variable name="distance">
			<xsl:value-of select="$nodevalue"/> miles
		</xsl:variable>
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_distanceFromSubject;'"/>
			</xsl:call-template>
			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="normalize-space($distance)" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<!-- Builds all the subsections for each comparable. -->
	<xsl:template match="PROPERTY" mode="CompSales">
		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:call-template name="PropertyInformation"/>
		</table>
		<xsl:apply-templates select="_PROPERTY_HISTORY" mode="CompSales"/>
		<xsl:apply-templates select="_PROPERTY_TAX" mode="CompSales"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS" mode="CompSales"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_SITE/_FLOOD_ZONE"/>
	</xsl:template>

	<!--builds the property information section for each comparable. This
			includes information such as address, owner, county, subdivision
			and census tract.-->
	<xsl:template name="PropertyInformation">
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_address;'"  />
			</xsl:call-template>

			<xsl:variable name="fullAddress">
				<xsl:apply-templates select="_PARSED_STREET_ADDRESS" />
				<xsl:apply-templates select="@_City"/>
				<xsl:apply-templates select="@_State">
					<xsl:with-param name="cityNode" select="@_City"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="@_PostalCode"/>
				<xsl:apply-templates select="@_PlusFourPostalCode"/>
			</xsl:variable>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$fullAddress" />
			</xsl:call-template>
		</tr>

		<xsl:apply-templates select="PROPERTY_OWNER/@_OwnerName"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_SALES_HISTORY/@_SellerName"/>
		<xsl:apply-templates select="@_County"/>
		<xsl:apply-templates select="@_SubdivisionIdentifier"/>
		<xsl:apply-templates select="@CensusTractIdentifier"/>
	</xsl:template>

	<!--builds the mortgage and transaction information for each
			comparable property, with previous seller information and dates.-->
	<xsl:template match="_PROPERTY_HISTORY" mode="CompSales">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_transactionHeader;'"  />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastSalesPriceAmount"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastSalesDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSalePriceAmount"/>			
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSaleDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastRecordingDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorDeedTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_DocumentNumberIdentifier"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_CashDownAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_FirstMortgageAmount"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_LenderName"/>
		</table>
	</xsl:template>

	<!--builds the tax information for each comparable-->
	<xsl:template match="_PROPERTY_TAX" mode="CompSales">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_taxInformationCaps;'"  />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="ancestor::PROPERTY[1]/@_AssessorsParcelIdentifier"/>			
			<xsl:apply-templates select="@_TotalAssessedValueAmount"/>
			<xsl:apply-templates select="preceding-sibling::_PROPERTY_CHARACTERISTICS/_SITE/_CHARACTERISTICS/@_LandUseDescription"/>
			<xsl:apply-templates select="@_AssessmentYear"/>
			<xsl:apply-templates select="@_RealEstateTotalTaxAmount"/>
		</table>
	</xsl:template>

	<!--builds the property characteristics for each comparable which
			includes information such as year built, acres, number of stories,
			lot size, square footage, rooms, bedrooms, baths, etc.-->
	<xsl:template match="_PROPERTY_CHARACTERISTICS" mode="CompSales">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_propertyDescriptionHeader;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_YearBuiltDateIdentifier"/>
			<xsl:apply-templates select="_SITE/_DIMENSIONS/@_LotAreaAcresNumber"/>
			<xsl:apply-templates select="_SITE/_DIMENSIONS/@_LotAreaSquareFeetNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalLivingAreaSquareFeetNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalRoomCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalBedroomsCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalBathsCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_PARKING/@_GarageTotalCarCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_EXTERIOR_DESCRIPTION/@_RoofSurfaceDescription"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_FOUNDATION/@_MaterialDescription"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_BASEMENT/@_AreaSquareFeet"/>			
			<xsl:apply-templates select="_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_TotalStoriesNumber"/>			
			<xsl:apply-templates select="_IMPROVEMENTS/_COOLING/@_CentralizedIndicator"/>			
			<xsl:apply-templates select="_IMPROVEMENTS/_FEATURES/_FIREPLACES"/>			
			<xsl:apply-templates select="_IMPROVEMENTS/_FEATURES/_POOL/@_HasFeatureIndicator"/>
		</table>
	</xsl:template>

	<!--builds the flood zone information if available for each comparable-->
	<xsl:template match="_PROPERTY_CHARACTERISTICS/_SITE/_FLOOD_ZONE">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_floodPanelHeader;'"  />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@NFIPMapIdentifier"/>
			<xsl:apply-templates select="@NFIPMapPanelDate"/>
			<xsl:apply-templates select="@FloodZoneIdentifier"/>
		</table>
	</xsl:template>

</xsl:stylesheet>