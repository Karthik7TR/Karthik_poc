<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Don't render the follow data fields -->
	<xsl:template match="col.key | legacyId | p | pc | pub.date | metadata" />

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExperianCreditHeaderClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_creditHeader;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:call-template name="individualInformation"/>
		<xsl:call-template name="lastKnownAddress"/>
		<xsl:call-template name="previousAddress"/>
	</xsl:template>

	<!-- Individual Information Template -->
	<xsl:template name="individualInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.b/na.b/primary.na.b" />
			<xsl:apply-templates select="individ.b/na.b/aka.na.b" />
			<xsl:apply-templates select="individ.b/ssn.b/ssn" />
			<xsl:apply-templates select="individ.b/ssn.b/ssn.confirmed" />
			<xsl:apply-templates select="individ.b/birth.d.display" />
		</table>
	</xsl:template>
	
	<!-- Last Known Address Template -->
	<xsl:template name="lastKnownAddress">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_lastKnownAddressInformationSubheader;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.b/addr.b/curr.addr.b" />
			<xsl:apply-templates select="individ.b/addr.b/curr.addr.b/addr.rpt.d" />
		</table>
	</xsl:template>

	<!-- Previous Address Template -->
	<xsl:template name="previousAddress">
		<xsl:if test="individ.b/addr.b/prev.addr.b[normalize-space(.) and not(optout.encrypted)]">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_otherAddressInfo;'" />
			</xsl:call-template>
			<xsl:for-each select="individ.b/addr.b/prev.addr.b">
				<table class="&pr_table;">
					<xsl:apply-templates select="." />
					<xsl:apply-templates select="addr.rpt.d" />
				</table>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<!-- Primary Name -->
	<xsl:template match="primary.na.b[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="na.mid"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
			<xsl:with-param  name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Also Known As (AKA) First with label -->
	<xsl:template match="aka.na.b[not(optout.encrypted)]">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="position() = 1">
					<xsl:text><![CDATA[ ]]>&pr_alsoKnownAs;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]>&pr_emptyString;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="na.mid"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
			<xsl:with-param  name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- SSN -->
	<xsl:template match="ssn[normalize-space(.) and not(optout.encrypted) and normalize-space(.) != '000000000']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!-- SSN Confirmed -->
	<xsl:template match="ssn.confirmed">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssnStatus;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Birthday -->
	<xsl:template match="birth.d.display[normalize-space(.) and not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_birthday;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Current Address -->
	<xsl:template match="curr.addr.b[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_currentAddress;'"/>
			<xsl:with-param name="street" select="full.addr"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address Reported Date -->
	<xsl:template match="addr.rpt.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressOnFileSince;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Previous Address -->
	<xsl:template match="prev.addr.b[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_previousAddress;'"/>
			<xsl:with-param name="street" select="full.addr"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>