<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">	
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="Copyright.xsl"/>	
	<xsl:include href="Publisher.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!--<xsl:template match="dl/d3 | so/d4 | so1/d3 | crdms/d1 | md.display.primarycite | md.title" priority="5" />-->

	<xsl:template match="md.display.primarycite | md.title" priority="5" />

	<!--<xsl:template match="ti/d9" priority="5" >
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>-->

	<xsl:template match="starpage.anchor" priority="2" />

	<xsl:template match="dpa1" priority="5" >
		<div class="&paraMainClass;">&#160;</div>
		<xsl:apply-templates/>
		
	</xsl:template>
	
</xsl:stylesheet>
