<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="ValuePoint4SubHeader">
		<div>
			<xsl:attribute name="class">co_hAlign2</xsl:attribute>
			<xsl:value-of select="normalize-space('&pr_valuePoint4Copyright;')"/>
		</div>		
	</xsl:template>
	
	<xsl:template match="@_TaxYear">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_taxYear;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_RateAreaIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_taxRateArea;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ExemptionTypeDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_exemption;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_DelinquentYear">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_deilnquentTaxYear;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LegalBookAndPageIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_legalBookPage;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_BlockIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_legalBlockBuilding;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LotIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_legalLotUnit;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ImprovementValueAmount" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_improvementValue;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="result">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($result)"/>
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_LandValueAmount" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_landValue;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="result">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($result)"/>
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorFirstMortgageAmount" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorFirstMortgageAmount;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="value">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($value)"/>
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_SecondMortgageAmount" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_secondMortgageAmount;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="value">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($value)"/>
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_OneSaleTypeDescription" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_saleType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_AbsenteeIndicator">
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_absenteeOwner;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_Municipality">
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_townshipName;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_ValuationCommentIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_comment;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_RunDate">
		<xsl:variable name="runDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="PrintSaleDate">
			<xsl:with-param name="name" select="'&pr_processedDate;'" />
			<xsl:with-param name="value" select="$runDate" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@_ValuationDate">
		<xsl:variable name="vaulationDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="PrintSaleDate">
			<xsl:with-param name="name" select="'&pr_valueAsOf;'" />
			<xsl:with-param name="value" select="$vaulationDate" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@_IndicatedValueAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_estimatedValue;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="estimatedValue">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($estimatedValue)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_RealEstateTotalTaxAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_propertyTax;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="propertyTax">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($propertyTax)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<!--gets the distance in miles from the subject property-->
	<xsl:template match="@_DistanceFromSubjectNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:variable name="distance">
			<xsl:value-of select="$nodevalue"/>
			&#13;miles
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

	<xsl:template match="@_AssessmentYear">
		<xsl:param name="assessedYear" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_assessmentYear;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$assessedYear" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_TotalAssessedValueAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_totalAssessmentValue;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="assessedValue">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($assessedValue)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="PROPERTY_OWNER">
		<xsl:apply-templates select="@_OwnerName"/>
	</xsl:template>

	<xsl:template match="@_OwnerName">
		<xsl:param name="name" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>

			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_owner;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$name"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_YearBuiltDateIdentifier">
		<xsl:param name="yearBuilt" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_yearBuildEff;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$yearBuilt" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LotAreaAcresNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_acres;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_TotalStoriesNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_numberOfStories;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_CentralizedIndicator">
		<xsl:param name="indicator" select="normalize-space(.)"/>
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_centralizedIndicator;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$indicator" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LotAreaSquareFeetNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_lotArea;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="squareFeet">
					<xsl:value-of select="$nodevalue"/>
					&#13;sq.ft.
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($squareFeet)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="_IMPROVEMENTS/_FEATURES/_FIREPLACES">
		<xsl:variable name="hasFeatureIndicator" select="normalize-space(@_HasFeatureIndicator)"/>
		<xsl:variable name="countNumber" select="normalize-space(@_CountNumber)"/>

		<xsl:variable name="fireplaceCount">
			<xsl:value-of select="$hasFeatureIndicator"/>
			<xsl:if test="$hasFeatureIndicator != '' and $countNumber != ''">
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$countNumber"/>
			</xsl:if>
		</xsl:variable>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_fireplace;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$fireplaceCount" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_TotalLivingAreaSquareFeetNumber">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_livingSquareFeet;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="squareFeet">
					<xsl:value-of select="$nodevalue"/>
					&#13;sq.ft.
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($squareFeet)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_TotalRoomCount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_totalNumberOfRooms;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="_IMPROVEMENTS/_FEATURES/_POOL/@_HasFeatureIndicator">
		<xsl:param name="pool" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_pool;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$pool" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_TotalBathsCount|@_TotalFullBathsCount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_totalBaths;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_RoofSurfaceDescription">
		<xsl:param name="roofSurface" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_roofMaterial;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$roofSurface" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_TotalBedroomsCount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_numberOfBedrooms;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue !='' and $nodevalue !='0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_MaterialDescription">
		<xsl:param name="material" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_foundationType;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$material" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_AreaSquareFeet">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_basementSquareFeet;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_GarageTotalCarCount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_garageCapacity;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@NFIPMapIdentifier">
		<xsl:param name="identifier" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_floodPanel;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$identifier" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@NFIPMapPanelDate">
		<xsl:variable name="date">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_floodPanelDate;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$date" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@FloodZoneIdentifier">
		<xsl:param name="identifier" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_floodZone;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$identifier" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LandUseDescription">
		<xsl:param name="landUseDescription" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_landUse;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$landUseDescription" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_AssessorsParcelIdentifier">
		<xsl:param name="parcelIdentifier" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:variable name="parcelNumberLabel">
				<xsl:text>&pr_assessorsParcelNumber;</xsl:text>
			</xsl:variable>

			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="$parcelNumberLabel" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$parcelIdentifier" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="_PARSED_STREET_ADDRESS">
		<xsl:apply-templates select="@_HouseNumber"/>
		<xsl:apply-templates select="@_DirectionPrefix"/>
		<xsl:apply-templates select="@_StreetName"/>
		<xsl:apply-templates select="@_StreetSuffix"/>
		<xsl:apply-templates select="@_DirectionSuffix"/>
		<xsl:apply-templates select="@_ApartmentOrUnit"/>
	</xsl:template>

	<xsl:template match="@_StreetAddress|@_PostalCode|@_MailingAddress|@_MailingPostalCode|@_HouseNumber| @_DirectionPrefix| @_StreetName| @_StreetSuffix| @_DirectionSuffix| 	@_ApartmentOrUnit">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="@_City|@_MailingCityAndState">
		<br/>
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="@_State">
		<xsl:param name="cityNode"/>
		<xsl:if test="$cityNode=''">
			<br/>
		</xsl:if>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="@_PlusFourPostalCode|@_MailingCarrierRouteIdentifier">
		<xsl:text>-</xsl:text>
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="@_SellerName">
		<xsl:param name="name" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>

			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_sellerName;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$name"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_County">
		<xsl:param name="county" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_county;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$county"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SubdivisionIdentifier">
		<xsl:param name="subdivision" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_subdivision;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$subdivision"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@CensusTractIdentifier">
		<xsl:param name="censusTract" select="normalize-space(.)" />
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_censusTract;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$censusTract"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LastSalesPriceAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_salePrice;'"  />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="salePrice">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($salePrice)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_PriorSalePriceAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorSalePrice;'"  />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="priorSalePrice">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($priorSalePrice)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_LastSalesDate">
		<xsl:variable name="saleDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="PrintSaleDate">
			<xsl:with-param name="name" select="'&pr_saleDate;'" />
			<xsl:with-param name="value" select="$saleDate" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@_PriorSaleDate">
		<xsl:variable name="saleDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="PrintSaleDate">
			<xsl:with-param name="name" select="'&pr_priorSaleDate;'" />
			<xsl:with-param name="value" select="$saleDate" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PrintSaleDate">
		<xsl:param name="name" />
		<xsl:param name="value"/>
		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="$name" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$value" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_LastRecordingDate">
		<xsl:variable name="recordingDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="PrintSaleDate">
			<xsl:with-param name="name" select="'&pr_recordingDate;'" />
			<xsl:with-param name="value" select="$recordingDate" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@_PriorDeedTypeDescription">
		<xsl:param name="typeDescription" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_priorDeedType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$typeDescription" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_DocumentNumberIdentifier">
		<xsl:param name="documentNumber" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_documentNumber;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$documentNumber" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_CashDownAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_cashDown;'"/>
			</xsl:call-template>
			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_FirstMortgageAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_firstMortgageAmount;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="mortgageAmount">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($mortgageAmount)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_LenderName">
		<xsl:param name="lender" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_firstMortgageLender;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$lender" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<!-- the following templates should be placed in a base file-->
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

</xsl:stylesheet>