<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsNewMoversClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_newMoversRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Render the "Coverage" section -->
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<!-- Render the "Head of Household" section -->
		<xsl:apply-templates select="member"/>
	</xsl:template>
	<xsl:template name="PublicRecordsRightColumn">
		<!-- Render the "Household Info" section -->
		<xsl:apply-templates select="addr.info"/>
	</xsl:template>

	<xsl:template match="member">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_headOfHouseholdInfo;'" />
		</xsl:call-template>

		<table class="&pr_table;">
			<!-- Displayed field: Name -->
			<xsl:apply-templates select="name.b"/>
			<!-- Displayed fields: Demographic information -->
			<xsl:apply-templates select="demo.b"/>
		</table>
	</xsl:template>

	<!-- *************************  Name Display ************************* -->
	<xsl:template match="name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="na.t"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mid"/>
			<xsl:with-param name="lastName" select="lna"/>
			<xsl:with-param name="suffixName" select="suf|na.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!-- *************************  Demographic Display ************************* -->
	<xsl:template match="demo.b[normalize-space(.)]">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_ageRange;'" />
			</xsl:call-template>
			<td>
				<xsl:value-of select='age.range' />
			</td>
		</tr>
	</xsl:template>


	<!-- ********************************************************************** 
*************************  Household Information  *************************
************************************************************************-->
	<xsl:template match="addr.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_householdInfo;'" />
		</xsl:call-template>

		<table class="&pr_table;">
			<!-- Displayed field: Address -->
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="label" select="'&pr_address;'"/>
				<xsl:with-param name="street" select="addr.b/str"/>
				<xsl:with-param name="city" select="addr.b/cty"/>
				<xsl:with-param name="stateOrProvince" select="addr.b/st"/>
				<xsl:with-param name="zip" select="addr.b/zip"/>
				<xsl:with-param name="zipExt" select="addr.b/zip.ext"/>
			</xsl:call-template>
			<!-- Displayed field: County -->
			<xsl:apply-templates select="cnty"/>
			<!-- Displayed field: Dwelling -->
			<xsl:apply-templates select="dwel"/>
			<!-- Displayed field: Telephone -->
			<xsl:apply-templates select="phn.b/phn"/>
			<!-- Displayed field: Residence Type -->
			<xsl:apply-templates select="res.typ"/>
			<xsl:apply-templates select="../member/demo.b/pub.d"/>
		</table>
	</xsl:template>

	<!-- County -->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Residence Type -->
	<xsl:template match="res.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Telephone -->
	<xsl:template match="phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_telephoneNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Dwellings -->
	<xsl:template match="dwel">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceType;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Date -->
	<xsl:template match="pub.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_date;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
