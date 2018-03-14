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
	<xsl:output method="text" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Cite//n-private-char" priority="1">
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
