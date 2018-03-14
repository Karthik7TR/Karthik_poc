<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Database Signon: FEIN-ALL -->

	<!-- Do not render these nodes -->
	<!-- For some reason, the 'p' node is rendering unless I put a priority below. -->
	<xsl:template match="map|p|pc|col.key|legacyId|so.duns|conf.cd|so.ind|fein.ind" priority="1"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsFederalEmployeeIdentificationNumberClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_federalEmployeeIdentificationNumberRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- ********************************************************************** 
	******************* (B)"BUSINESS INFORMATION" section *********************
	************************************************************************-->
	<xsl:template match="bus.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="co.name"/>
			<xsl:apply-templates select="addr.b"/>
			<xsl:apply-templates select="fein"/>
			<xsl:apply-templates select="tax.id.b"/>
			<xsl:apply-templates select="so.name"/>
			<xsl:apply-templates select="dnb.co.name"/>
			<xsl:apply-templates select="duns"/>
			<xsl:apply-templates select="sic.b"/>
		</table>
	</xsl:template>

	<!-- Company Name -->
	<xsl:template match="co.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_companyName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- FEIN Number block for Experian provided FEIN-->
	<xsl:template match="tax.id.b[position() = 1]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_federalEmployeeIdentificationNumber;'"/>
			<xsl:with-param name="selectNodes" select="fein.b/fein"/>
			<xsl:with-param name="nodeType" select="$FEIN"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- FEIN Number block for Experian provided FEIN-->
	<xsl:template match="tax.id.b[position() != 1]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_emptyString;'"/>
			<xsl:with-param name="selectNodes" select="fein.b/fein"/>
			<xsl:with-param name="nodeType" select="$FEIN"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- FEIN Number -->
	<xsl:template match="fein">
		<xsl:if test="not(parent::fein.b)">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_federalEmployeeIdentificationNumber;'"/>
				<xsl:with-param name="nodeType" select="$FEIN"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- FEIN Source -->
	<xsl:template match="so.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_feinSource;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- D&B Company Name -->
	<xsl:template match="dnb.co.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_dAndBCompanyName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- D&B Company Name -->
	<xsl:template match="duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dunsNumber;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- SIC -->
	<xsl:template match="sic.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sic;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sic.desc">
		<xsl:if test="preceding-sibling::sic.nbr">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- ********************************************************************** 
	******************  (C)"EXECUTIVE INFORMATION" section  *******************
	************************************************************************-->
	<xsl:template match="exec.info.b">
		<xsl:if test="contact.name">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Executive Name -->
	<xsl:template match="contact.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_executiveName;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Executive Title -->
	<xsl:template match="contact.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>