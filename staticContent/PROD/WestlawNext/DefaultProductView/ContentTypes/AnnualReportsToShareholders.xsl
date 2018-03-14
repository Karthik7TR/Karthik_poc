<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!--
	This stylesheet transforms Annual Reports to Shareholders documents from Novus XML
	into HTML for UI display.
-->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:include href="Filings.xsl"/>
	<xsl:include href="Footnotes.xsl"/>

	<!-- the main match -->
	<xsl:template match="Document">
		<!-- put doc content on display -->
		<div  id="&documentId;">
			<!-- Need document css classes added for correct alignment -->
			<xsl:choose>
				<xsl:when test="$isPreFormattedText=string(true())">
					<xsl:if test="$DeliveryMode=string(true())">
						<xsl:attribute name="style">
							<xsl:value-of select="$preformatDeliveryStyles"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeFilingsAndDisclosures; &preformattedDocument;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeFilingsAndDisclosures;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:call-template name="EndOfDocumentHeader" />
			<xsl:call-template name="Content">
				<xsl:with-param name="isScrollable" select="true()" />
			</xsl:call-template>
			<xsl:call-template name="RenderFootnoteSection"/>
      <xsl:call-template name="EndOfDocument">
        <xsl:with-param name="endOfDocumentCopyrightText"></xsl:with-param>
        <xsl:with-param name="endOfDocumentCopyrightTextVerbatim">true()</xsl:with-param>
      </xsl:call-template>
		</div>
	</xsl:template>
</xsl:stylesheet>