<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="TopicKeyCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:template match="headnote.block | keysummary.block" />
	<xsl:template match="headnote.block/expanded.headnote | keysummary.block/expanded.keysummary" />
	<xsl:template name="RenderHeadnoteAsTable"/>
	<xsl:template name="HeadNoteTopics"/>
	<xsl:template match="headnote | keysummary"/>
	<xsl:template match="expanded.classification" mode="enhancedKeyTopic"/>
	<xsl:template name="findLastUnprohibitedKeyText"/>
	<xsl:template match="topic.key | topic.key.ref" mode="findLastUnprohibitedKey"/>
	<xsl:template name="getHeadnoteTopicUrl"/>
	<xsl:template match="expanded.classification"/>
	<xsl:template match="expanded.classification/classification"/>
	<xsl:template match="headnote.block.head | keysummary.block.head"/>
	<xsl:template match="expanded.classification" mode="topicKey"/>
	<xsl:template match="expanded.classification/prior.classification"/>
	<xsl:template match="topic.line"/>
	<xsl:template match="headnote.reference"/>
	<xsl:template match="/*[self::summary or self::summaries]//headnote.reference" priority="2"/>
	<xsl:template match="headnote.case.title"/>
	<xsl:template match="headnote.reference//text()"/>
	<xsl:template match="/*[self::summary or self::summaries]//headnote.number/internal.reference | /*[self::summary or self::summaries]//keysummary/internal.reference" priority="2"/>
	<xsl:template match="headnote.block/expanded.headnote/headnote.number | keysummary.block/expanded.keysummary/keysummary.number"/>
	<xsl:template match="headnote.number/internal.reference | keysummary.number/internal.reference" priority="1"/>
	<xsl:template match="headnote.number/cite.query | keysummary.number/cite.query" priority="1"/>
	<xsl:template name="getHeadnoteNumber"/>
	<xsl:template match="headnote.number | keysummary.number"/>
	<xsl:template match="topic.key.hierarchy"/>
	<xsl:template name="RenderKeyIconImage"/>
	<xsl:template name="RenderTopicKeyHierarchy"/>
	<xsl:template match="topic.key"/>
	<xsl:template match="topic.key.ref"/>
	<xsl:template match="key"/>
	<xsl:template match="keytext//text()"/>
	<xsl:template match="headnote.courtyear"/>
	<xsl:template match="/Document/document-data/title//keytext" priority="1"/>
	<xsl:template match="court.headnote.block"/>
	<xsl:template match="court.headnote.block/headnote"/>
	<xsl:template match="library.reference"/>
	<xsl:template match="para[ancestor::headnote.body or ancestor::keysummary.body]" priority="1"/>
	<xsl:template name="wrapWithTableCellIfDeliveryMode"/>
</xsl:stylesheet>
