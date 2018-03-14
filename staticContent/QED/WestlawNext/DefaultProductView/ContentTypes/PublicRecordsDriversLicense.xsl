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

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsDriversLicensesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_driversLicenseRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="lic.hldr.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="lic.b" />
	</xsl:template>

	<!--Licensee Information-->
	<xsl:template match="lic.hldr.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_licenseeInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="na.b"/>
			<xsl:apply-templates select="res.addr.b"/>
			<xsl:apply-templates select="cnty"/>
			<xsl:apply-templates select="birth.d"/>
			<xsl:apply-templates select="gen"/>
			<xsl:apply-templates select="mail.addr.b"/>
			<xsl:apply-templates select="race"/>
			<xsl:apply-templates select="ht"/>
			<xsl:apply-templates select="wt"/>
			<xsl:apply-templates select="eyes"/>
			<xsl:apply-templates select="hair"/>
			<xsl:apply-templates select="rpt.d"/>
		</table>
	</xsl:template>

	<xsl:template match="na.b[normalize-space(.)]">
		<xsl:choose>
			<xsl:when test="full.na" >
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_name;'"/>
					<xsl:with-param name="firstName" select="full.na"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_name;'"/>
					<xsl:with-param name="firstName" select="fna"/>
					<xsl:with-param name="middleName" select="mna"/>
					<xsl:with-param name="lastName" select="lna"/>
					<xsl:with-param name="suffixName" select="na.suf"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Primary address information-->
	<xsl:template match="res.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_residenceAddress;'"/>
			<xsl:with-param name="street" select="str1"/>
			<xsl:with-param name="streetSuffix" select="str2"/>
			<xsl:with-param name="streetLineTwo" select="po.box"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--County-->
	<xsl:template match="cnty[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Birth-->
	<xsl:template match="birth.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Gender-->
	<xsl:template match="gen[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Mailing Address-->
	<xsl:template match="mail.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
			<xsl:with-param name="street" select="str1"/>
			<xsl:with-param name="streetSuffix" select="str2"/>
			<xsl:with-param name="streetLineTwo" select="po.box"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--Race-->
	<xsl:template match="race[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_race;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Height-->
	<xsl:template match="ht[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_height;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Weight-->
	<xsl:template match="wt[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_weight;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Eye Color-->
	<xsl:template match="eyes[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_eyeColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Hair Color-->
	<xsl:template match="hair[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hairColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--File Acquired Date-->
	<xsl:template match="rpt.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fileAcquiredDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
			<xsl:with-param name="selectNodes" select="rpt.d"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Information-->
	<xsl:template match="lic.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_licenseInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="iss.st"/>
			<xsl:for-each select="lic.type.b">
				<xsl:apply-templates select="lic.type"/>
				<xsl:apply-templates select="endrmt.b"/>
				<xsl:apply-templates select="iss.d"/>
				<xsl:apply-templates select="exp.d"/>
				<xsl:apply-templates select="cdl.ind"/>
				<xsl:apply-templates select="cdl.sts"/>
				<xsl:apply-templates select="orig.iss.d"/>
				<xsl:apply-templates select="noncdl.sts"/>
				<xsl:apply-templates select="status"/>
			</xsl:for-each>
		</table>

		<xsl:apply-templates select="." mode="OtherInformation" />
	</xsl:template>

	<!--Issuing State-->
	<xsl:template match="iss.st[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_issuingState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Type-->
	<xsl:template match="lic.type[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseType;'"/>
			<xsl:with-param name="selectNodes" select="lic.type"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Endorsements-->
	<xsl:template match="endrmt.b[normalize-space(.)]" >
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_licenseEndorsement;'" />
			</xsl:call-template>
			<td>
				<xsl:for-each select="endrmt">
					<div>
						<xsl:value-of select="node()" />
					</div>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>

	<!--License Issue Date-->
	<xsl:template match="iss.d[normalize-space(.)]" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_issueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
			<xsl:with-param name="selectNodes" select="iss.d"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Expiration Date-->
	<xsl:template match="exp.d[normalize-space(.)]" >
		<!-- this tries to format license type node -->
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseExpirationDate;'"/>
			<xsl:with-param name="nodeType" select="$LICENSE_EXPIRATION_DATE"/>
			<xsl:with-param name="selectNodes" select="exp.d"/>
		</xsl:call-template>
	</xsl:template>

	<!--Commercial Driver-->
	<xsl:template match="cdl.ind[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commercialDriver;'"/>
			<xsl:with-param name="selectNodes" select="cdl.ind"/>
		</xsl:call-template>
	</xsl:template>

	<!--Commercial Status-->
	<xsl:template match="cdl.sts[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commercialDriverStatus;'"/>
			<xsl:with-param name="selectNodes" select="cdl.sts"/>
		</xsl:call-template>
	</xsl:template>

	<!--Original Issue Date-->
	<xsl:template match="orig.iss.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalIssueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
			<xsl:with-param name="selectNodes" select="orig.iss.d"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Status-->
	<xsl:template match="noncdl.sts[normalize-space(.)]" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseStatus;'"/>
			<xsl:with-param name="selectNodes" select="noncdl.sts"/>
		</xsl:call-template>
	</xsl:template>

	<!--Status-->
	<xsl:template match="status[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
			<xsl:with-param name="selectNodes" select="status"/>
		</xsl:call-template>
	</xsl:template>

	<!--Other Information-->
	<xsl:template match="lic.b" mode="OtherInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_otherInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="lic.no"/>
			<xsl:apply-templates select="prev.lic.no"/>
			<xsl:apply-templates select="pi.no"/>
			<xsl:apply-templates select="oos.prev.b"/>
		</table>
	</xsl:template>

	<!--License Number-->
	<xsl:template match="lic.no[normalize-space(.)]" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_driversLicenseNumber;'"/>
			<xsl:with-param name="selectNodes" select="lic.no"/>
			<xsl:with-param name="nodeType" select="$LICENSE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Previous License Number-->
	<xsl:template match="prev.lic.no[normalize-space(.)]" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousLicenseNumber;'"/>
			<xsl:with-param name="selectNodes" select="prev.lic.no"/>
			<xsl:with-param name="nodeType" select="$LICENSE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Personal ID Number-->
	<xsl:template match="pi.no[normalize-space(.)]" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_personalIdNumber;'"/>
			<xsl:with-param name="selectNodes" select="pi.no"/>
		</xsl:call-template>
	</xsl:template>

	<!--Previous License Number-->
	<xsl:template match="oos.prev.lic.no[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousLicenseNumber;'"/>
			<xsl:with-param name="selectNodes" select="oos.prev.lic.no"/>
			<xsl:with-param name="nodeType" select="$LICENSE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Previous Issue State-->
	<xsl:template match="oos.prev.lic.st[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousLicenseState;'"/>
			<xsl:with-param name="selectNodes" select="oos.prev.lic.st"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>