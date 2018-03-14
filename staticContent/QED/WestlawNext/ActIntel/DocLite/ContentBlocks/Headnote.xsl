<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	
	<!-- Suppress all headnote elements -->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="headnote.block | keysummary.block" />

	<xsl:template name="RenderHeadnoteAsTable" />

	<xsl:template name="HeadNoteTopics" />

	<xsl:template match="headnote | keysummary" />
	
	<xsl:template name="getHeadnoteTopicUrl" />

	<xsl:template match="headnote.reference" />

	<xsl:template match="/*[self::summary or self::summaries]//headnote.reference" priority="2" />

	<xsl:template match="headnote.case.title" />

	<xsl:template match="/*[self::summary or self::summaries]//headnote.number/internal.reference | /*[self::summary or self::summaries]//keysummary/internal.reference" priority="2" />

	<xsl:template name="getHeadnoteNumber" />

	<xsl:template match="headnote.number | keysummary.number" />

	<xsl:template match="headnote.courtyear" />

	<xsl:template match="court.headnote.block" />

	<xsl:template match="para[ancestor::headnote.body or ancestor::keysummary.body]" priority="1" />

	<xsl:template name="headnotePublicationBlock" />

  <xsl:template name="renderKeyHierarchy" />
	
</xsl:stylesheet>
