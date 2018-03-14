<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalyticalEaganProducts.xsl" forceDefaultProduct="true" />
	<xsl:include href="KeyNumberLinks.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template name="RenderFootnoteBodyMarkupDiv">
		<xsl:param name="contents" />
		<xsl:variable name="footnoteNumberPart">
			<xsl:call-template name="GetRefNumberFromFootnote">
				<xsl:with-param name="transformedText" select="descendant::text()[1]" />
			</xsl:call-template>
		</xsl:variable>
		<div class="&footnoteBodyClass;">
			<xsl:choose>
				<xsl:when test="starts-with($contents, $footnoteNumberPart)">
					<xsl:copy-of select="substring-after($contents, $footnoteNumberPart)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$contents" />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="Document">
		<xsl:param name="contentType" select="'&contentTypeAnalyticalEaganProductClass;'"/>
		<xsl:param name="displayPublisherLogo" select="true()"/>
		<xsl:param name="citationText">
			<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite"/>
		</xsl:param>
		<div id="&documentId;">
			<xsl:choose>
				<xsl:when test="string-length($contentType) &gt; 0">
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="$contentType"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses" />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="string-length($citationText) &gt; 0">
				<xsl:call-template name="DisplayCitation">
					<xsl:with-param name="citation" select="$citationText"/>
				</xsl:call-template>
			</xsl:if>

			<xsl:if test="$displayPublisherLogo">
				<xsl:call-template name="DisplayPublisherLogo"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$EasyEditMode">
					<xsl:apply-templates select="node()" mode="EasyEdit"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="//md.form.flag">
						<xsl:call-template name="EasyEditFlag"/>
					</xsl:if>
					<xsl:call-template name="StarPageMetadata" />
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="string-length($citationText) &gt; 0">
				<xsl:call-template name="DisplayCitation">
					<xsl:with-param name="citation" select="$citationText"/>
				</xsl:call-template>
			</xsl:if>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="'&nonUSGovernmentCopyrightText;'" />
			</xsl:call-template>
			<xsl:if test="$displayPublisherLogo">
				<xsl:call-template name="DisplayPublisherLogo"/>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>