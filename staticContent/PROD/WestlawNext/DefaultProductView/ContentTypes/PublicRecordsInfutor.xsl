<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match="ph.conf.score|f.nm|l.nm|mi.nm"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsInfutorClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:choose>
			<xsl:when test="phone.info/rec.type = 'BUSINESS'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_header;'" />
					<xsl:with-param name="contents" select="'&pr_companyPhoneRecord;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_header;'" />
					<xsl:with-param name="contents" select="'&pr_personPhoneRecord;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
		<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		<xsl:with-param name="updateFrequencyValue" select="'&pr_monthly;'"/>
	</xsl:apply-templates>
		<xsl:apply-templates select="phone.info"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="address.info"/>
	</xsl:template>


	<!-- *************************************
	*************PHONE INFO ******************
	******************************************-->

	<xsl:template match ="phone.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_phoneInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phoneNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="phn.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phoneType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dir.dial[contains(., 'Y')]">
		<tr class="&pr_item;">
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_directInwardDialNumber;'"/>
			</xsl:call-template>
			<td>
				<xsl:text>YES</xsl:text>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="rec.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="first.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_firstReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="last.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="phn.carrier">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalServiceProvider;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bus.nm">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_businessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="name.b/full.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dir.asst">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_listedInDirectoryAssistance;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ph.conf.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_telephoneConfidenceDescription;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- *************************************
	*************ADDRESS INFO ****************
	******************************************-->
	<xsl:template match ="address.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_addressInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="street" select="addr.str"/>
			<xsl:with-param name="streetUnitType" select="unit.type"/>
			<xsl:with-param name="streetUnitNumber" select="unit.nbr"/>
			<xsl:with-param name="city" select="addr.cty"/>
			<xsl:with-param name="stateOrProvince" select="addr.st"/>
			<xsl:with-param name="zip" select="addr.zip.b/addr.zip"/>
			<xsl:with-param name="zipExt" select="addr.zip.b/addr.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mail.delivery">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mailDeliverable;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.valid.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressValidationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>