<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Brief.xsl" forceDefaultProduct="true"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeBriefClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
				<xsl:apply-templates select="n-docbody/brief/content.block/court.block"/>
				<xsl:apply-templates select="n-docbody/brief/content.block/title.block"/>
				<xsl:apply-templates select="n-docbody/brief/content.block/docket.block"/>
				<xsl:apply-templates select="n-docbody/brief/content.block/date.block"/>
				<xsl:apply-templates select="n-docbody/brief/content.block/action.block"/>
				<xsl:apply-templates select="n-docbody/brief/content.block/document.head.block"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<!-- limit output to certain number of words -->
			<xsl:variable name="Contents">
				<div>
					<xsl:choose>
						<xsl:when test="n-docbody/brief/content.block/synopsis">
							<xsl:apply-templates select="n-docbody/brief/content.block/synopsis[1]" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="n-docbody/brief/content.block/node()[not(self::court.block or self::title.block or self::docket.block or self::date.block or self::action.block or self::document.head.block)]"/>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:variable>
			<xsl:copy-of select="DocumentExtension:LimitTextWordCount($Contents, &LimitTextTo250Words;)"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Suppress -->
	<!-- See suppression of remarks.block in WLNOpenWebDtPComponent.cs -->
	<xsl:template match="headnote.block" priority="1"/>
	<xsl:template match="analysis" priority="1"/>
	<xsl:template match="reference.block" priority="1"/>
	<xsl:template match="message.block" priority="1"/>

	<xsl:template match="cite.query" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="generateLinkToFootnote" priority="1">
		<xsl:param name="refNumberText" select="''" />
		<sup>
			<xsl:value-of select="$refNumberText"/>
		</sup>
	</xsl:template>

</xsl:stylesheet>
