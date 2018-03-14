<?xml version="1.0" encoding="utf-8"?>
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Footnotes.xsl" forceDefaultProduct="true"/>

	<xsl:template name="generateLinkBackToFootnoteReference">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteId" select="''" />
		<xsl:param name="pertinentFootnote" select="ancestor-or-self::node()[self::footnote or self::form.footnote or self::endnote or self::form.endnote][1]" />

		<xsl:if test="string-length($refNumberText) &gt; 0">
			<xsl:variable name="contents">
				<!-- Make a special call to insert calculated page numbers to handle the normal displacement of footnotes -->
				<xsl:apply-templates select="$pertinentFootnote" mode="starPageCalculation" />

				<span>
					<xsl:value-of select="$refNumberText"/>
				</span>
			</xsl:variable>
			<xsl:call-template name="RenderFootnoteNumberMarkup">
				<xsl:with-param name="contents" select="$contents"/>
				<xsl:with-param name="refNumberText" select="$refNumberText"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="generateLinkToFootnote">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteRef" select="." />

		<xsl:if test="string-length($refNumberText) &gt; 0">
			<sup>
				<xsl:value-of select="$refNumberText"/>
			</sup>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
