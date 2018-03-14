<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsObituaryClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageState" select="false()"/>
      <xsl:with-param name="displayUpdateDate" select="false()"/>
      <xsl:with-param name="displayDateAcquired" select="false()"/>
      <xsl:with-param name="displayCoverageBeginDate" select="false()"/>
      <xsl:with-param name="displayReportAuthority" select="false()"/>
    </xsl:apply-templates>
		<xsl:call-template name="DeceasedName"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:call-template name="InformationRegardingDeceased"/>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_obituary;'" />
		</xsl:call-template>
	</xsl:template>

		<xsl:template name="DeceasedName">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_identifyingInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="wrapPublicRecordsName">
				<xsl:with-param name="label" select="'&pr_name;'"/>
				<xsl:with-param name="prefixName" select="na.b/na.prefix"/>
				<xsl:with-param name="firstName" select="na.b/first"/>
				<xsl:with-param name="middleName" select="na.b/na.mid"/>
				<xsl:with-param name="lastName" select="na.b/last"/>
				<xsl:with-param name="suffixName" select="na.b/na.suf"/>
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="InformationRegardingDeceased">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_informationRegardingDeceased;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="death.d"/>
			<xsl:apply-templates select="age.death.d"/>
			<xsl:apply-templates select="addr.b/lst.res"/>
			<xsl:apply-templates select="addr.b/ctry"/>			
			<xsl:apply-templates select="addr.b/cty"/>
			<xsl:apply-templates select="birth.d"/>
			<xsl:call-template name="ConcatenatedBirthYears"/>		
		</table>
	</xsl:template>

	<xsl:template match="death.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfDeath;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="age.death.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ageAtDeath;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.b/ctry">
	</xsl:template>

	<xsl:template match="addr.b/lst.res">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_state;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.b/cty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_city;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="birth.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="ConcatenatedBirthYears">

		<!-- there should be only zero, one or two possible birth years -->
		<xsl:variable name="concatenatedString">
			<xsl:if test="count(birth.year) = 1">
				<xsl:value-of select="substring((//birth.year)[1],1,4)"/>
			</xsl:if>
			<xsl:if test="count(birth.year) = 2">		
				<xsl:value-of select="concat(substring((//birth.year)[1],1,4), ', ' ,substring((//birth.year)[2],1,4))"/>
			</xsl:if>
		</xsl:variable>
			
		<xsl:call-template name="displayLabelValue">
			<xsl:with-param name="label" select="'&pr_possibleYearsOfBirth;'"/>
			<xsl:with-param name="value" select="$concatenatedString"/>
		</xsl:call-template>
		
	</xsl:template>

	<xsl:template name="displayLabelValue">
		<xsl:param name="label" select="'label'"/>
		<xsl:param name="value" select="'value'"/>
		<xsl:if test="$value != ''">
			<tr class="&pr_item;">
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="$label"/>
				</xsl:call-template>
				<td>
					<xsl:value-of select="$value"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
