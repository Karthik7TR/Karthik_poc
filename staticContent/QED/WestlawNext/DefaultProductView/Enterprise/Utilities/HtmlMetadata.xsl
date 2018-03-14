<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="htmlMetadata">
		<head>
			<title>
				<xsl:value-of select="n-metadata/metadata.block/md.descriptions/md.title"/>
			</title>

			<xsl:call-template name="metaTag">
				<xsl:with-param name="type" select="string('PLCReference')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.plc.reference" />
			</xsl:call-template>
			<xsl:call-template name="metaTag">
				<xsl:with-param name="type" select="string('Description')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.abstract" />
			</xsl:call-template>
			<xsl:call-template name="metaTag">
				<xsl:with-param name="type" select="string('MAINTAINED')" />
				<xsl:with-param name="content">
					<xsl:choose>
						<xsl:when test="n-docbody/*/prelim/currency/currency.status = '&MaintainedText;' ">
							<xsl:value-of select="true()"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="false()"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="metaTag">
				<xsl:with-param name="type" select="string('Author')" />
				<xsl:with-param name="content" select="n-metadata/metadata.block/md.contributors/md.author" />
			</xsl:call-template>
			<xsl:call-template name="metaTag">
				<xsl:with-param name="type" select="string('publication_date')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.lawdate" />
			</xsl:call-template>
			<xsl:call-template name="metaTag">
				<xsl:with-param name="type">IXR</xsl:with-param>
				<xsl:with-param name="content">1</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="metaList">
				<xsl:with-param name="csv" select="string('TITLE, META_TITLE')" />
				<xsl:with-param name="content" select="n-metadata/metadata.block/md.descriptions/md.title" />
			</xsl:call-template>
			<xsl:call-template name="metaList">
				<xsl:with-param name="csv" select="string('RT, RESOURCE_TYPE, RTF, RESOURCETYPEFOLDER')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.resource.type/plcmd.name" />
			</xsl:call-template>
			<xsl:call-template name="metaList">
				<xsl:with-param name="csv" select="string('RT, RESOURCE_TYPE, RTF, RESOURCETYPEFOLDER')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.resource.type/plcmd.plc.reference" />
			</xsl:call-template>
			<xsl:call-template name="metaList">
				<xsl:with-param name="csv" select="string('JF, JURISDICTIONFOLDER')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.jurisdictions/plcmd.jurisdiction/plcmd.plc.reference" />
			</xsl:call-template>
			<xsl:call-template name="metaList">
				<xsl:with-param name="csv" select="string('JF, JURISDICTIONFOLDER')" />
				<xsl:with-param name="content" select="n-metadata/plc.metadata.block/plcmd.jurisdictions/plcmd.jurisdiction/plcmd.name" />
			</xsl:call-template>

			<xsl:for-each select="n-metadata/plc.metadata.block/md.practice.areas/md.practice.area">
				<xsl:call-template name="metaList">
					<xsl:with-param name="content" select="md.plc.reference" />
					<xsl:with-param name="csv" select="string('PF, PRODUCTFOLDER')" />
				</xsl:call-template>
				<xsl:call-template name="metaList">
					<xsl:with-param name="content" select="md.practice.area.name" />
					<xsl:with-param name="csv" select="string('PF, PRODUCTFOLDER')" />
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="n-metadata/plc.metadata.block/md.topics/md.topic">
				<xsl:if test="md.topic.name != ''">
					<xsl:call-template name="metaList">
						<xsl:with-param name="content" select="md.topic.name" />
						<xsl:with-param name="csv" select="string('PAF, PRACTICEAREAFOLDER')" />
					</xsl:call-template>
					<xsl:call-template name="metaList">
						<xsl:with-param name="content" select="md.plc.reference" />
						<xsl:with-param name="csv" select="string('PAF, PRACTICEAREAFOLDER')" />
					</xsl:call-template>
				</xsl:if>
			</xsl:for-each>
		</head>
	</xsl:template>

	<xsl:template name="metaTag">
		<xsl:param name="content" />
		<xsl:param name="type" />
		<meta content="{$content}" name="{$type}" />
	</xsl:template>

	<!--Tokenizes a comma seperated string-->
	<xsl:template name="metaList">
		<xsl:param name="csv" />
		<xsl:param name="content" />

		<!-- Create tokens from the csv string-->
		<xsl:variable name="first-item" select="normalize-space( 
	  substring-before( concat( $csv, ','), ','))" />

		<xsl:if test="$first-item">
			<xsl:call-template name="metaTag">
				<xsl:with-param name="content" select="$content" />
				<xsl:with-param name="type" select="$first-item" />
			</xsl:call-template>

			<xsl:call-template name="metaList">
				<xsl:with-param name="csv" select="substring-after($csv,',')" />
				<xsl:with-param name="content" select="$content" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
