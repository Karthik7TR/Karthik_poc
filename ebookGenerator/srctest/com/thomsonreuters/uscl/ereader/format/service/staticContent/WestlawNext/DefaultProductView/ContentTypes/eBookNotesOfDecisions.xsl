<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="nod.block" name="renderNotesOfDecisionsStatutes">
		<xsl:choose>

			<xsl:when test="count(../nod.block//nod.note | ../nod.block//nod.ref) = 0" />
			<xsl:otherwise>
				eBookNotesOfDecisionsOutput
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
