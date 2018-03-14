<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<!-- "Headnote.xsl" is included for its "topic.key.ref" template match -->
	<xsl:include href="Headnote.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="nod.block" priority="2"/>
		
	<xsl:template match="nod.block/nod.body/nod.body" />
	
	<xsl:template match="nod.block/nod.body" />
		
	<xsl:template match="nod.block/head/head.info/headtext" priority="2" />
		
	<xsl:template name="DisplayNODHeading" />
		
	<xsl:template name="DisplayNODWithZeroSearchResult" />
		
	<!-- Suppress -->
	<xsl:template match="nod.block/analysis" />
	<xsl:template match="nod.body/analysis" />
	<xsl:template match="nod.body/head/head.info/label.designator" />

</xsl:stylesheet>
