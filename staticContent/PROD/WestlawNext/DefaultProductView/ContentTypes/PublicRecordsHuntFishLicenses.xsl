<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="md-document-guid-value" select="/Document/map/entry[key='md.document.guid']/value" />
	<xsl:variable name="state" select="/Document/n-docbody/r/s.st"/>
	<xsl:variable name="aquired" select="/Document/n-docbody/r/acq.d"/>

	<!-- PublicRecordsDocumentGuid -->

	<!-- Do not render these nodes -->
	<xsl:template match="map|legacy.id|col.key|p|pc|prior|oph.nbr"/>

	<xsl:template match="Document" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsUtilityClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_huntFishPermit;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageState" select="true()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="descendant::person.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="descendant::huntfish.permit.b"/>
	</xsl:template>

	<xsl:template match="CoverageMeta">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_sourceInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="$state"/>
			<xsl:apply-templates select="UpdateFrequency">
				<xsl:with-param name="BoldingLabel" select="true()"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="$aquired"/>
			<xsl:apply-templates select="CurrentDate">
				<xsl:with-param name="BoldingLabel" select="true()"/>
			</xsl:apply-templates>
		</table>
	</xsl:template>

	<xsl:template match="s.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_state;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="acq.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fileAcquired;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--NAME AND PERSONAL INFORMATION BLOCK-->
	<xsl:template match="person.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_personalInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="name.b | corp.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="fname"/>
			<xsl:with-param name="middleName" select="mname"/>
			<xsl:with-param name="lastName" select="lname"/>
			<xsl:with-param name="suffixName" select="suf"/>
		</xsl:call-template>
	</xsl:template>

	<!--Maiden/Prior Name(s)-->
	<!-- MSchroeder - fix for bug 44429 -->
	<xsl:template match="prior">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maidenNames;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="addr.b">
		<xsl:if test="count(node())!= 0">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
				<xsl:with-param name="street" select="str"/>
				<xsl:with-param name="city" select="cty"/>
				<xsl:with-param name="stateOrProvince" select="st"/>
				<xsl:with-param name="zip" select="zip.b/zip"/>
				<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Mailing address -->
	<xsl:template match="mail.addr.b">
		<xsl:if test="count(node())!= 0">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="defaultLabel" select="'&pr_mailingAddress;'"/>
				<xsl:with-param name="street" select="str"/>
				<xsl:with-param name="city" select="cty"/>
				<xsl:with-param name="stateOrProvince" select="st"/>
				<xsl:with-param name="zip" select="zip.b/zip"/>
				<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Phone block-->
	<xsl:template match ="ph.b">
		<xsl:if test="count(node())!= 0">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!-- Home Phone number -->
	<xsl:template match="hph.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Work Phone number -->
	<xsl:template match="wph.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_workPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--PERMIT INFORMATION BLOCK-->
	<xsl:template match="huntfish.permit.b">
		<xsl:if test="count(node())!= 0">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_permitInfo;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates/>
			</table>
		</xsl:if>
	</xsl:template>

	<!--Permit/License Number-->
	<xsl:template match="permit.lic.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_permitNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Permit block-->
	<xsl:template match="permit.b">
		<xsl:if test="count(node())!= 0">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!--Permit Class block-->
	<xsl:template match="permit.cl.b">
		<xsl:if test="count(node())!= 0">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!--Permit Type-->
	<xsl:template match="permit.t[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_permitType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Permit Class-->
	<xsl:template match="permit.cl[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_permitClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Permit Date-->
	<xsl:template match="permit.iss.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_permitDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Lifetime Permit-->
	<xsl:template match="life.permit[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_permitLifetime;'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>