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
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyValuePoint4Class;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!-- Renders the report/document header-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_valuePoint4Header;'" />
		</xsl:call-template>
		<xsl:call-template name="ValuePoint4SubHeader" />
	</xsl:template>

	<!-- renders the main column using the Public Records styles-->
	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="//_AUTOMATED_VALUATION" mode="ValuePoint4"/>
		<xsl:apply-templates select="//_PROPERTY_INFORMATION" mode="ValuePoint4"/>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_areaMarketSales;'" />
		</xsl:call-template>
		<xsl:apply-templates select="//_DATA_PROVIDER_COMPARABLE_SALES" mode="ValuePoint4"/>
	</xsl:template>

	<!-- Builds all the subsections for each comparable. -->
	<xsl:template match="_PROPERTY_INFORMATION" mode="ValuePoint4">
		<xsl:param name="reportType" select="normalize-space(@_ReportType)" />

		<xsl:if test="$reportType = 'ValuePoint4'">
			<xsl:apply-templates select="PROPERTY" mode="ValuePoint4" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="PROPERTY" mode="ValuePoint4">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_subjectPropertyInfoHeader;'" />
		</xsl:call-template>

		<xsl:call-template name="VPLocation"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY" mode="ValuePoint4"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS"/>
		<xsl:apply-templates select="_PROPERTY_TAX" mode="ValuePoint4"/>
	</xsl:template>

	<!-- Valuation template for ValuePoint4 Report-->
	<xsl:template match="_AUTOMATED_VALUATION" mode="ValuePoint4">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_valuationHeader;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_IndicatedValueAmount"/>
			<xsl:call-template name="valueRangeAmount">
				<xsl:with-param name="lowRange" select="@_LowValueRangeAmount"/>
				<xsl:with-param name="highRange" select="@_HighValueRangeAmount" />
			</xsl:call-template>
			<xsl:apply-templates select="@_ValuationScoreIdentifier" />
			<xsl:apply-templates select="@_ValuationDate" />
			<xsl:apply-templates select="@_RunDate" />
			<xsl:apply-templates select="@_ValuationCommentIdentifier" />
		</table>
	</xsl:template>

	<xsl:template name="VPLocation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_locationInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="PROPERTY_OWNER/@_OwnerName" />
			<xsl:call-template name="PropertyAddress"/>
			<xsl:apply-templates select="@_City" mode="ValuePoint4"/>
			<xsl:apply-templates select="@_State" mode="ValuePoint4"/>
			<xsl:call-template name="FullPostalCode">
				<xsl:with-param name="postalCode" select="@_PostalCode" />
				<xsl:with-param name="plustFourPostalCode" select="@_PlusFourPostalCode" />
			</xsl:call-template>
			<xsl:apply-templates select="@_County"/>
			<xsl:apply-templates select="@_AssessorsParcelIdentifier"/>
			<xsl:apply-templates select="@CensusTractIdentifier"/>
			<xsl:apply-templates select="@_Municipality"/>
			<xsl:apply-templates select="PROPERTY_OWNER/@_AbsenteeIndicator"/>
		</table>
	</xsl:template>

	<xsl:template match="_PROPERTY_HISTORY" mode="ValuePoint4">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_lastMarketSaleInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastSalesPriceAmount"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_LastSalesDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_OneSaleTypeDescription"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_SellerName"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_FirstMortgageAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_LoanCodeIdentifier"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_SecondMortgageAmount"/>
		</table>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_priorSaleInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSalePriceAmount"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSaleDate"/>
			<xsl:apply-templates select="_SALES_HISTORY/@_PriorSellerName"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_PriorFirstMortgageAmount"/>
			<xsl:apply-templates select="_MORTGAGE_HISTORY/@_FirstMortgageTypeDescription"/>
		</table>
	</xsl:template>

	<xsl:template match="_PROPERTY_CHARACTERISTICS">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_propertyInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalLivingAreaSquareFeetNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalRoomCount"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalBedroomsCount"/>
			<xsl:choose>
				<xsl:when test="_IMPROVEMENTS/_ROOM_COUNT/@_TotalFullBathsCount !='' and _IMPROVEMENTS/_ROOM_COUNT/@_TotalHalfBathsCount != ''">
					<xsl:call-template name="FullHalfBathCount">
						<xsl:with-param name="fullBathCount" select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalFullBathsCount" />
						<xsl:with-param name="halfBathCount" select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalHalfBathsCount" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="_IMPROVEMENTS/_ROOM_COUNT/@_TotalBathsCount"/>
				</xsl:otherwise>
			</xsl:choose>			
			<xsl:apply-templates select="_IMPROVEMENTS/_FEATURES/_POOL/@_HasFeatureIndicator" />
			<xsl:apply-templates select="_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_TotalStoriesNumber" />			
			<xsl:apply-templates select="_SITE/_CHARACTERISTICS/@_LandUseDescription"/>
			<xsl:apply-templates select="_SITE/_DIMENSIONS/@_LotAreaSquareFeetNumber"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_YearBuiltDateIdentifier"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_COOLING/@_CentralizedIndicator"/>
			<xsl:apply-templates select="_IMPROVEMENTS/_FEATURES/_FIREPLACES" />			
			<xsl:apply-templates select="_IMPROVEMENTS/_PARKING" mode="ValuePoint4" />			
		</table>
	</xsl:template>

	<xsl:template match="_PROPERTY_TAX" mode="ValuePoint4">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_taxInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_TotalAssessedValueAmount"/>
			<xsl:apply-templates select="@_AssessmentYear"/>

			<xsl:apply-templates select="@_LandValueAmount"/>
			<xsl:apply-templates select="@_ImprovementValueAmount"/>
		</table>
	</xsl:template>

	<xsl:template match="_DATA_PROVIDER_COMPARABLE_SALES" mode="ValuePoint4">
		<xsl:variable name="title">
			&pr_comparableSubHeader; <xsl:value-of select="@_ComparableNumber"/>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="normalize-space($title)" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_DistanceFromSubjectNumber"/>
			<xsl:apply-templates select="PROPERTY" mode="ValuePoint4Comparable" />
		</table>
	</xsl:template>

	<xsl:template match="PROPERTY" mode="ValuePoint4Comparable">
		<xsl:apply-templates select="@_StreetAddress" mode="ValuePoint4Comparable"/>
		<xsl:apply-templates select="PROPERTY_OWNER/@_OwnerName"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_SALES_HISTORY/@_SellerName"/>
		<xsl:apply-templates select="@_AssessorsParcelIdentifier"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_SITE/_CHARACTERISTICS/@_LandUseDescription"/>
		<xsl:apply-templates select="@_County"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_YearBuiltDateIdentifier"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_SALES_HISTORY/@_LastSalesPriceAmount"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_SALES_HISTORY/@_LastSalesDate"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_SALES_HISTORY/@_PriorSalePriceAmount"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_SALES_HISTORY/@_PriorSaleDate"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY/_MORTGAGE_HISTORY/@_FirstMortgageAmount"/>
		<xsl:apply-templates select="_PROPERTY_TAX/@_TotalAssessedValueAmount"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalLivingAreaSquareFeetNumber"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_COOLING/@_CentralizedIndicator"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalRoomCount"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_FEATURES/_FIREPLACES"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalBedroomsCount"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_FEATURES/_POOL/@_HasFeatureIndicator"/>
		<xsl:choose>
			<xsl:when test="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalFullBathsCount !='' and _PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalHalfBathsCount != ''">
				<xsl:call-template name="FullHalfBathCount">
					<xsl:with-param name="fullBathCount" select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalFullBathsCount" />
					<xsl:with-param name="halfBathCount" select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalHalfBathsCount" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalFullBathsCount !=''">
				<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalFullBathsCount"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_ROOM_COUNT/@_TotalBathsCount"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_IMPROVEMENTS/_GENERAL_DESCRIPTION/@_TotalStoriesNumber"/>
		<xsl:apply-templates select="_PROPERTY_CHARACTERISTICS/_SITE/_DIMENSIONS/@_LotAreaSquareFeetNumber"/>
	</xsl:template>

	<xsl:template match="@_StreetAddress" mode="ValuePoint4Comparable">
		<xsl:param name="street" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_address;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$street"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ComparableNumber" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_comparableSubHeader;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_FirstMortgageTypeDescription" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorFirstMortgageType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorSellerName" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorSellerName;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LoanCodeIdentifier" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_firstMortgageType;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="value">
					<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($value)"/>
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_City" mode="ValuePoint4">
		<xsl:param name="nodevalue" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>

			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_city;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_State" mode="ValuePoint4">
		<xsl:param name="nodevalue" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_state;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template name="FullPostalCode">
		<xsl:param name="postalCode"/>
		<xsl:param name="plusFourPostalCode" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>

			<xsl:call-template name="wrapWithTableHeader"	>
				<xsl:with-param name="contents" select="'&pr_zip;'" />
			</xsl:call-template>

			<xsl:variable name="fullZip">
				<xsl:value-of select="$postalCode"/>-<xsl:value-of select="$plusFourPostalCode"/>
			</xsl:variable>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="normalize-space($fullZip)" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template name="PropertyAddress">
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>

			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_propertyAddress;'"  />
			</xsl:call-template>

			<xsl:variable name="fullAddress">
				<xsl:apply-templates select="_PARSED_STREET_ADDRESS" />
			</xsl:variable>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$fullAddress" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ValuationScoreIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_score;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template name="valueRangeAmount">
		<xsl:param name="lowRange" />
		<xsl:param name="highRange" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_estimatedValueRange;'" />
			</xsl:call-template>

			<xsl:if test="$lowRange !='' and $lowRange != '0.0' and $highRange != '' and $highRange != '0.0'">
				<xsl:variable name="rangeValue">
					$<xsl:value-of select="$lowRange"/> - $<xsl:value-of select="$highRange"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($rangeValue)"/>
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template name="FullHalfBathCount">
		<xsl:param name="fullBathCount" select="'0'"/>
		<xsl:param name="halfBathCount" select="'0'" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_bathHalfFull;'"/>
			</xsl:call-template>

			<xsl:variable name="nodevalue">
				<xsl:value-of select="$fullBathCount"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$halfBathCount"/>
			</xsl:variable>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="_IMPROVEMENTS/_PARKING" mode="ValuePoint4">
		<xsl:variable name="parkingSpaces" select="normalize-space(@_SpacesCount)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_parkingSpaces;'" />
			</xsl:call-template>

			<xsl:if test="$parkingSpaces != '' and $parkingSpaces != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$parkingSpaces" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>
</xsl:stylesheet>