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
	<xsl:include href="PublicRecordsRealPropertyReportDetail.xsl"/>
	<xsl:include href="PublicRecordsRealPropertyReportComparable.xsl"/>
	<xsl:include href="PublicRecordsRealPropertyReportLegalVesting.xsl"/>
	<xsl:include href="PublicRecordsRealPropertyReportValuePoint4.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="/">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyComprehensiveClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!-- Renders the report/document header-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_comprehensiveReport;'" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- renders the main column using the Public Records styles-->
	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="//_PROPERTY_INFORMATION" />
	</xsl:template>

	<xsl:template match="_PROPERTY_INFORMATION">
		<xsl:param name="reportType" select="normalize-space(@_ReportType)" />
		<xsl:choose>
			<xsl:when test="$reportType = 'DetailedSubjectReport'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_comprehensiveHeader;'" />
					<xsl:with-param name="contents" select="'&pr_propertyDetailReportRecord;'" />
				</xsl:call-template>
				<xsl:apply-templates select="PROPERTY" mode="DetailLeft" />
				<xsl:apply-templates select="PROPERTY" mode="DetailRight" />
			</xsl:when>
			<xsl:when test="($reportType = 'DetailedComparableReport') or ($reportType = 'StandardComparableReport')">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_comprehensiveHeader;'" />
					<xsl:with-param name="contents" select="'&pr_comparableSalesHeader;'" />
				</xsl:call-template>
				<xsl:apply-templates select="_DATA_PROVIDER_COMPARABLE_SALES" mode="CompSales" />
			</xsl:when>
			<xsl:when test="$reportType = 'LegalAndVestingReport'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_comprehensiveHeader;'" />
					<xsl:with-param name="contents" select="'&pr_legalAndVestingHeader;'" />					
				</xsl:call-template>
				<xsl:apply-templates select="PROPERTY" mode="LegalAndVesting" />
			</xsl:when>
			<xsl:when test="$reportType = 'ValuePoint4'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_comprehensiveHeader;'" />
					<xsl:with-param name="contents" select="'&pr_valuePoint4Header;'" />
				</xsl:call-template>
				<xsl:call-template name="ValuePoint4SubHeader" />
				<xsl:apply-templates select="../_AUTOMATED_VALUATION" mode="ValuePoint4" />
				<xsl:apply-templates select="PROPERTY" mode="ValuePoint4" />				
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_areaMarketSales;'" />
				</xsl:call-template>
				<xsl:apply-templates select="_DATA_PROVIDER_COMPARABLE_SALES" mode="ValuePoint4"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>