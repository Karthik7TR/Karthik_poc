<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="PreformattedTextCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="preformatted.text.block | p[@xml:space = 'preserve']">
		<div class="&simpleContentBlockClass; &preformattedTextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="preformatted.text.block/preformatted.text.line">
		<xsl:variable name="contents">
			<xsl:call-template name="PreformattedTextCleaner" />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div>
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Inversion of control from PreformattedTextCleaner -->
	<xsl:template match="preformatted.text.block/preformatted.text.line//text() | p[@xml:space = 'preserve']/text()" priority="1">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="notPreformatted" select="false()" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
