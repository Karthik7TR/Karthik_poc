<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!--People Finder Historic Tracker Record - PEOPLE-FIND-->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="isPrivacyProtected">
		<xsl:choose>
			<xsl:when test="/Document/n-docbody/r/@r10='MN'">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- Do not render these nodes -->
	<xsl:template match="p|pc|c|col.key|coll.nm|map|coverageData|s|ext.addr|del|r8|r9|r10"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsProfessionalLicensesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_peopleFinderHistoricTrackerRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- (A) Render the "Coverage" section -->
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<!-- Individual Information Section-->
		<xsl:apply-templates select="list.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="list.b/curr.addr.b/addr.b"/>
		<xsl:if test="not(list.b/curr.addr.b)">
			<xsl:apply-templates select="list.b/addr.b"/>
		</xsl:if>
		<xsl:if test="normalize-space(list.b/prev.addr.b) or normalize-space(list.b/dec)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_otherAddressInfo;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="list.b/prev.addr.b">
					<xsl:sort select="rpt.d/@iso.d" order="descending"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="list.b/dec"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- **********************************************************************
	******************** (B)"Individual Information" section ******************
	************************************************************************-->

	<!-- Name information -->
	<xsl:template match="list.b[name.b or ssn.b or birth.b or gndr or fil.d or phn.b or hist.phn]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_individualInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="name.b"/>
			<xsl:apply-templates select="name.b/aka.b"/>
			<xsl:apply-templates select="ssn.b"/>
			<xsl:apply-templates select="ssn[not(parent::ssn.b)]"/>
			<xsl:apply-templates select="birth.b"/>
			<xsl:apply-templates select="gndr"/>
			<xsl:apply-templates select="hist.phn" />
			<xsl:apply-templates select="fil.d"/>
			<xsl:apply-templates select="phn.b" />
		</table>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="name.b[not(./optout.encrypted) and (ln or fn or mid or suf)]">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="prefixName" select="pref"/>
			<xsl:with-param name="firstName" select="fn"/>
			<xsl:with-param name="middleName" select="mid"/>
			<xsl:with-param name="lastName" select="ln"/>
			<xsl:with-param name="suffixName" select="suf"/>
			<xsl:with-param name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Alias info -->
	<xsl:template match="name.b/aka.b[not(child::node()/optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_alsoKnownAs;'"/>
			<xsl:with-param name="selectNodes" select="aka"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aka">
		<div>
			<xsl:call-template name="FormatName">
				<xsl:with-param name="lastName" select="."/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Social security number -->
	<xsl:template match="ssn.b">
		<xsl:apply-templates select="ssn"/>
		
		<xsl:apply-templates select="ssn.frag">
			<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Social security template -->
	<xsl:template match="ssn[normalize-space(.) and not(./optout.encrypted)]">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
			<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="birth.ind"/>

	<!-- Date of Birth -->
	<xsl:template match="birth.d[normalize-space(.)]">
		<xsl:variable name="showPartialDate">
			<xsl:choose>
				<xsl:when test="preceding-sibling::birth.ind or following-sibling::birth.ind">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="string($showPartialDate)='true'">
					<xsl:text>&pr_estimatedDateOfBirth;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_dateOfBirth;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
			<xsl:with-param name="isPrivacyProtected" select="$showPartialDate"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Gender -->
	<xsl:template match="gndr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- On file since -->
	<xsl:template match="fil.d[normalize-space(.) and not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_onFileSince;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone number -->
	<xsl:template match="phn.b[not(child::node()/optout.encrypted)]">
		<xsl:apply-templates select="phn[normalize-space(.)] | telco.phn.1[normalize-space(.)] | telco.phn.2[normalize-space(.)]"/>
	</xsl:template>

	<xsl:template match="phn | telco.phn.1 | telco.phn.2">
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

	<!-- Hist Phone number -->
	<xsl:template match="hist.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_historicPhoneNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address First Reported date -->
	<xsl:template match="rpt.d[normalize-space(.) and not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressFirstReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	**************** (C)"Last Known Address Information" section **************
	************************************************************************-->
	<!-- Address template -->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_lastKnownAddressInformationSubheader;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="outputAddress"/>
			<xsl:apply-templates select="../phn.b"/>
			<xsl:apply-templates select="../rpt.d"/>
			<xsl:apply-templates select="cnty"/>
			<xsl:apply-templates select="dwel"/>
		</table>
	</xsl:template>

	<xsl:template match="pvaddr.b">
		<xsl:call-template name="outputAddress">
			<xsl:with-param name="label" select="'&pr_previousAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="outputAddress">
		<xsl:param name="label" select="'&pr_currentAddress;'"/>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="streetDirection" select="str.dir"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="streetSuffix" select="str.typ"/>
			<xsl:with-param name="streetUnitNumber" select="apt.nbr"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="zipExt" select="zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Res type -->
	<xsl:template match="dwel[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_type;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	****************** (D)"Other Address Information" section *****************
	************************************************************************-->
	<xsl:template match="dec[normalize-space(.) and not(./optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_notes;'"/>
		</xsl:call-template>
		<!-- The original style sheet had a section with the following text with a link.  Don't know if we need this.
			<xsl:variable name="SocSecAdmin">
				<xsl:text>Search for Social Security Administration death record</xsl:text>
			</xsl:variable>
			<xsl:call-template name="LinkedData">
				<xsl:with-param name="displayText" select="$SocSecAdmin"/>
			</xsl:call-template>-->
	</xsl:template>

	<xsl:template match="ital">
		<xsl:text> </xsl:text>
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

</xsl:stylesheet>
