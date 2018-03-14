<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="popular.name.doc.title"/>
	<xsl:template match="propagated.block"/>
	<xsl:template match="credit"/>
	<xsl:template match="para[ancestor::credit]/paratext"/>
	<xsl:template match="annotations"/>
	<xsl:template match="include.currency"/>
	<xsl:template match="authority"/>
	<xsl:template match="authority.note.block"/>
	<xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgp']" priority="1"/>
	<xsl:template match="nod.block" name="renderNotesOfDecisionsStatutes"/>
	<xsl:template match="para[parent::section]/paratext"/>
	<xsl:template match="toc-data" />

</xsl:stylesheet>