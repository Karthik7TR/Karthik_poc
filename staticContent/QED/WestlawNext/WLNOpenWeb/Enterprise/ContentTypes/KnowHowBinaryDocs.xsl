<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<!--This DefaultProduct KnowHow.xsl refers to DefaultProductView/Enterprise/ContentBlocks/KnowHow.xsl-->
	<xsl:include href="KnowHow.xsl" forceDefaultProduct="true"/>

	<xsl:template match="n-docbody">
		<body>
			<xsl:apply-templates select="binary" />
		</body>
	</xsl:template>

	<xsl:template match="binary">
		<xsl:call-template name="TitleAndAbstract" />
		<div id="&wlnEnterprise_resource;">
			<div id="&wlnEnterprise_resource_abstract;">
				<xsl:apply-templates select="prelim/author/web.address" />
			</div>
		</div>
		<xsl:call-template name="RelatedTopicsSection" />
		<xsl:call-template name="RelatedResourcesSection" />
	</xsl:template>

</xsl:stylesheet>
