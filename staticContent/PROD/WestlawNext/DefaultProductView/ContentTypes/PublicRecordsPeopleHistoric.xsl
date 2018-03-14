<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--  AKA Merlin People -->

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsPeopleHistoricClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_peopleHistorical;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_individualInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.info.b/name.info"/>
			<xsl:apply-templates select="individ.info.b/ssn.b/ssn"/>
			<xsl:apply-templates select="individ.info.b/dob"/>
			<xsl:apply-templates select="individ.info.b/filesince.d"/>
			<xsl:apply-templates select="individ.info.b/phone.b/phone.nbr"/>
		</table>

		<xsl:if test="descendant::addl.individ.info.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_additionalInformation;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="addl.individ.info.b/name.info"/>
				<xsl:apply-templates select="addl.individ.info.b/ssn.b/ssn"/>
				<xsl:apply-templates select="addl.individ.info.b/dob"/>
				<xsl:apply-templates select="addl.individ.info.b/filesince.d"/>
				<xsl:apply-templates select="addl.individ.info.b/phone.b/phone.nbr"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!-- Historic Address Information Section -->
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_historicAddressInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="hist.addr.b/addr.b"/>
			<xsl:apply-templates select="death.info.b/notes"/>
			<xsl:apply-templates select="death.info.b/deceased.d"/>
		</table>
	</xsl:template>

	<!-- ***************************************************************************
	*************************  PERSON-FIND VIEW  ***********************************
	********************************************************************************-->
	<xsl:template match="name.info">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="prefix"/>
			<xsl:with-param name="firstName" select="fname"/>
			<xsl:with-param name="middleName" select="mname"/>
			<xsl:with-param name="lastName" select="lname"/>
			<xsl:with-param name="suffixName" select="suffix"/>
			<xsl:with-param name="professionalSuffixName" select="na.prof.suf"/>
			<xsl:with-param name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ssn[normalize-space(.) and not(./optout.encrypted)]">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="filesince.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_onFileSince;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="phone.nbr">
		<xsl:variable name="label">
			<xsl:text>&pr_phoneNumberLabelPrefix;<![CDATA[ ]]></xsl:text>
			<xsl:value-of select="position()"/>
			<xsl:text>:</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.addr.b/addr.b">
		<xsl:variable name="tableRowClass">
			<xsl:if test="preceding-sibling::addr.b">
				<xsl:text>&pr_paddingTop;</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="label">
			<xsl:text>&pr_addressLabelPrefix;<![CDATA[ ]]></xsl:text>
			<xsl:value-of select="position()"/>
			<xsl:text>:</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="fullStreet" select="hist.full.str"/>
			<xsl:with-param name="streetNum" select="str.nbr"/>
			<xsl:with-param name="streetDirection" select="str.pre.dir"/>
			<xsl:with-param name="street" select="str.name"/>
			<xsl:with-param name="streetSuffix" select="str.type"/>
			<xsl:with-param name="streetDirectionSuffix" select="str.post.dir"/>
			<xsl:with-param name="streetUnitNumber" select="unit.type"/>
			<xsl:with-param name="streetUnit" select="unit.nbr"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
			<xsl:with-param name="divClass" select="$tableRowClass"/>
		</xsl:call-template>
		<xsl:apply-templates select="addr.rpt.d"/>
		<xsl:apply-templates select="addr.type"/>

	</xsl:template>

	<xsl:template match="addr.rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressLastReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="notes">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_notes;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="deceased.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfDeath;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>

