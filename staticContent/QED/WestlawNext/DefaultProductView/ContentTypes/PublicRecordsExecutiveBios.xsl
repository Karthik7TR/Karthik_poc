<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match="map"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExecutiveBiographyClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_executiveBiographyRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="displayCurrentThroughDate" select="false()"/>
			<xsl:with-param name="displayUpdateFrequency" select="false()" />
		</xsl:apply-templates>
		<xsl:apply-templates select="co.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="exec.info.b"/>
		<xsl:apply-templates select="mgmt.resp.b"/>
		<xsl:apply-templates select="exec.bio"/>
	</xsl:template>


	<!-- ********************************************************************** 
	*************************  (A) Company Information  ***********************
	************************************************************************-->
	<xsl:template match="co.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_companyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--DUNS Number-->
	<xsl:template match="duns.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dunsNumber;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!--Company Name-->
	<xsl:template match="co.nm">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_companyName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Address-->
	<xsl:template match="addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address: Street-->
	<xsl:template match="str">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--County-->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Telephone-->
	<xsl:template match="phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_telephone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	************************  (B) Executive Information  **********************
	************************************************************************-->
	<xsl:template match="exec.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Executive Name-->
	<xsl:template match="na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="prefixName" select="na.pre"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mna"/>
			<xsl:with-param name="lastName" select="lna"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!--Executive Title-->
	<xsl:template match="full.exec.ttl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Other Role-->
	<xsl:template match="ceo.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_otherRole;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Year of Birth-->
	<xsl:template match="dob">
		<!-- Do not display Year of Birth (changes for no PRACCESS) -->
	</xsl:template>


	<!-- ********************************************************************** 
	*********************  (C) Management Responsibilities  *******************
	************************************************************************-->
	<xsl:template match="mgmt.resp.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_managementResponsibilities;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Description-->
	<xsl:template match="mrc.code.b">
		<tr>
			<td>
				<xsl:apply-templates select="mrc.desc"/>
			</td>
		</tr>
	</xsl:template>

	<!-- ********************************************************************** 
	******************************  (D) Biography  ****************************
	************************************************************************-->
	<xsl:template match="exec.bio">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_biographySubheader;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<tr>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>