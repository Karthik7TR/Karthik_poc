<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CommonInlineTemplates.xsl"/>
	<xsl:include href="CharFill.xsl"/>
	<xsl:include href="Fraction.xsl"/>
	<xsl:include href="NPrivateChar.xsl"/>
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="Suppressed.xsl"/>
	<xsl:include href="FootnoteReferenceCleaner.xsl"/>
	<xsl:include href="TopicKeyCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Title//label.designator | Title//label.name">
			<xsl:apply-templates />
			<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<!-- Suppress footnote references from composite header -->
	<xsl:template match="Title//footnote.reference | Title//table.footnote.reference | Title//endnote.reference" priority="2" />

	<!-- Capture rogue "super" elements that are not contained within "table.footnote.reference"/"footnote.reference"/endnote.reference" elements -->
	<xsl:template match="Title//super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]" priority="2" />

	<!-- Suppress starpages from composite header -->
	<xsl:template match="Title//starpage.anchor" priority="2" />

	<!-- Special rules for titles -->
	<xsl:template match="Title//keytext" priority="1">
		<xsl:call-template name="getKeyTextForTitles" />
	</xsl:template>

	<xsl:template match="Title//findorig" />

	<xsl:template name="Title-n-private-char">
		<xsl:choose>
			<xsl:when test="@charName = 'TLRkey'">
				<xsl:text>k</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="nonMetadataNPrivateChars" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


</xsl:stylesheet>