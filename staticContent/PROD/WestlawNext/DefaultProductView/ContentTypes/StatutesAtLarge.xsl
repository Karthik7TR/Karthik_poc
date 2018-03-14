<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeStatutesAtLargeClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />			
			<xsl:call-template name="renderHeader"/>
			<xsl:apply-templates select="n-docbody/document/body/*[not(self::cite.block or self::publication.name or self::congress.session.info or self::convening.date or self::approval.date or self::image.block)]"/>
			<br/>
			<xsl:apply-templates select="n-docbody/document/body/approval.date"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template name="renderHeader">
		<xsl:choose>
			<xsl:when test="$PreviewMode">
				<xsl:apply-templates select="n-docbody/*"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="n-docbody/document/body/image.block"/>
					<div class="&documentHeadClass;">
						<xsl:apply-templates select="n-docbody/document/body/cite.block/second.line.cite" mode="heading"/>
						<xsl:apply-templates select="n-docbody/document/body/publication.name" mode="title"/>
						<xsl:apply-templates select="n-docbody/document/body/congress.session.info"/>
						<xsl:apply-templates select="n-docbody/document/body/convening.date"/>
					</div>
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="publication.name">
		<xsl:if test="$PreviewMode">
			<xsl:apply-templates select="." mode="title"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="publication.name" mode="title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="second.line.cite">
		<xsl:if test="$PreviewMode">
			<xsl:apply-templates select="." mode="heading"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="second.line.cite" mode="heading">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="congress.session.info | convening.date | approval.date | qualifying.head | para | popular.name" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'" />
		</xsl:call-template>
		<xsl:if test="name(.) = 'qualifying.head'">
			<br/>
		</xsl:if>
	</xsl:template>

	<!-- Suppress -->
	<xsl:template match="primarycite | first.line.cite | stat.page" priority="1" />
</xsl:stylesheet>
