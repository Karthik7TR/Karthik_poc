<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<xsl:variable name="fileacquireddate1" select="/Document/map/entry[key='novus']/value/n-metadata/metadata.block/md.dates/md.createddatetime" />
	<xsl:variable name="fileacquireddate2" select="/Document/map/entry[key='novus']/value/n-metadata/prism-clipdate" />
	<xsl:variable name="pcVal" select ="/Document/n-docbody/r/pc"/>
	<xsl:variable name="fullpath-node-r" select="/Document/n-docbody/r" />

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsPeopleEmail;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_peopleEmailRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:call-template name="IndividualInfo"/>
	</xsl:template>



	<!-- **********************************************************************
	********************** (B)Individual Information section ********************
	************************************************************************-->

	<!-- Individual Information -->
	<xsl:template name="IndividualInfo">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
		</xsl:call-template>

		<table class="&pr_table;">
			<xsl:apply-templates select="name.b"/>
			<xsl:apply-templates select="email.b"/>
			<xsl:apply-templates select="ip.addr"/>
			<xsl:choose>
				<xsl:when test="$pcVal='WAF' or $pcVal='SUB'">
					<xsl:apply-templates select="home.addr.b"/>
					<xsl:apply-templates select="home.phn"/>
					<xsl:apply-templates select="cell.phn"/>
					<xsl:apply-templates select="bus.phn"/>
					<xsl:choose>
						<xsl:when test="$fileacquireddate1">
							<xsl:call-template name="wrapPublicRecordsItem">
								<xsl:with-param name="defaultLabel" select="'&pr_fileAcquiredDate;'"/>
								<xsl:with-param name="selectNodes" select="$fileacquireddate1"/>
								<xsl:with-param name="nodeType" select="$DATE"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="$fileacquireddate2">
							<xsl:call-template name="wrapPublicRecordsItem">
								<xsl:with-param name="defaultLabel" select="'&pr_fileAcquiredDate;'"/>
								<xsl:with-param name="selectNodes" select="$fileacquireddate2"/>
								<xsl:with-param name="nodeType" select="$DATE"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="addr.b"/>
					<xsl:apply-templates select="opt.in.d|ent.db.d"/>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<!--Name  -->
	<xsl:template match="name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="f.nm"/>
			<xsl:with-param name="middleName" select="m.nm"/>
			<xsl:with-param name="lastName" select="l.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!--Email  -->
	<xsl:template match="email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="email" select="email"/>
			<xsl:with-param name="user" select="user.nm"/>
			<xsl:with-param name="domain" select="domain.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!--IP Address  -->
	<xsl:template match="ip.addr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ipAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address  -->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="addr.str"/>
			<xsl:with-param name="city" select="addr.cty"/>
			<xsl:with-param name="stateOrProvince" select="addr.st"/>
			<xsl:with-param name="zip" select="addr.zip.b/addr.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address  -->
	<xsl:template match="home.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="home.str"/>
			<xsl:with-param name="city" select="home.cty"/>
			<xsl:with-param name="stateOrProvince" select="home.st"/>
			<xsl:with-param name="zip" select="home.zip.b/home.zip"/>
			<xsl:with-param name="zipExt" select="home.zip.b/home.zip.ext"/>
		</xsl:call-template>
	</xsl:template>


	<!-- Phones -->
	<xsl:template match="home.phn | cell.phn | bus.phn">
		<xsl:variable name="phoneLabel">
			<xsl:text>&pr_phonePrefix;</xsl:text>
			<xsl:value-of select="position()" />
			<xsl:text>:</xsl:text>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$phoneLabel"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!--Reported Date  -->
	<xsl:template match="opt.in.d|ent.db.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>