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

	<!-- Database Signon: UTILITY, UTILITY-XX -->

	<xsl:variable name="fullpath-node-r" select="/Document/n-docbody/r" />

	<!--  Do not render these nodes -->
	<xsl:template match="map|p|pc|col.key|legacy.id"/>

	<xsl:template match="Document" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsUtilityClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_utilityRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Render the "Coverage" section -->
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<!-- Render the "Individual Information" section -->
		<xsl:call-template name="IndividualInfo"/>
	</xsl:template>
	<xsl:template name="PublicRecordsRightColumn">
		<!-- Render the "Address Information" section -->
		<xsl:call-template name="AddressInfo"/>
	</xsl:template>

	<!--*********************************************************************
	************************* (B) INDIVIDUAL INFORMATION ********************
	**********************************************************************-->
	<xsl:template name="IndividualInfo">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_individualInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="descendant::na.b"/>
			<xsl:apply-templates select="descendant::ssn.b"/>
			<xsl:apply-templates select="descendant::lic.no"/>
			<xsl:apply-templates select="descendant::lic.st"/>
			<xsl:apply-templates select="descendant::serv.typ.b"/>
			<xsl:apply-templates select="descendant::conn.d"/>
			<xsl:apply-templates select="descendant::add.d"/>
			<xsl:apply-templates select="descendant::work.ph"/>
			<xsl:apply-templates select="descendant::serv.ph"/>
		</table>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mna"/>
			<xsl:with-param name="lastName" select="lna"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!--Social security number-->
	<xsl:template match="ssn.b">
		<xsl:apply-templates select="ssn" />
	</xsl:template>

	<!-- Social security template -->
	<xsl:template match="ssn[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Drivers License Number -->
	<xsl:template match="lic.no">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_driversLicenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Drivers License Issue State -->
	<xsl:template match="lic.st[not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_driversLicenseIssueState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Service Type -->
	<xsl:template match="serv.typ[not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_serviceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Connect Date -->
	<xsl:template match="conn.d[not(./output.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_connectDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Reported Date -->
	<xsl:template match="add.d[not(./output.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Work Phone -->
	<xsl:template match="work.ph[not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_workPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Service Phone -->
	<xsl:template match="serv.ph[not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_contactPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="AddressInfo">
		<xsl:if test="$fullpath-node-r/serv.addr.b/*[not(child::optout.encrypted)] or $fullpath-node-r/bill.addr.b/*[not(child::optout.encrypted)]">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_addressInfo;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="descendant::serv.addr.b"/>
				<xsl:apply-templates select="descendant::bill.addr.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Service Address -->
	<xsl:template match="serv.addr.b[not(descendant::optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_serviceAddress;'"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="streetSuffix" select="str.typ"/>
			<xsl:with-param name="streetDirectionSuffix" select="str.dir"/>
			<xsl:with-param name="streetUnitNumber" select="apt.nbr"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="zipExt" select="zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Billing Address -->
	<xsl:template match="bill.addr.b[not(descendant::optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_billingAddress;'"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="streetSuffix" select="str.typ"/>
			<xsl:with-param name="streetDirectionSuffix" select="str.dir"/>
			<xsl:with-param name="streetUnitNumber" select="apt.nbr"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="zipExt" select="zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="apt.nbr">
		<xsl:text>#</xsl:text>
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
