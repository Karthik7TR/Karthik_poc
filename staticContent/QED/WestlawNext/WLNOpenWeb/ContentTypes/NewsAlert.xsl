<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="NewsAlert.xsl" forceDefaultProduct="true"/>
	
	<!-- OPEN WEB - Limit text displayed -->
	<xsl:template match="article">
		<xsl:apply-templates select="article.title" />
		<xsl:variable name="Contents">
			<div class="&paraMainClass;">
				<xsl:apply-templates select="*[local-name() != 'article.title']" />
			</div>
		</xsl:variable>
		<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents,&LimitTextByCharacterCountValue;)"/>
	</xsl:template>
	
	<xsl:template match="related.article/para">
		<xsl:variable name="Contents">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:variable>
		<div>
			<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents,&LimitTextByCharacterCountValue;)"/>
		</div>
	</xsl:template>

	<!--Supress Topics Section of right side of page-->
	<xsl:template match="article.detail.block" />

	<!--Additional copyright message for Congressional Quarterly documents-->
	<xsl:template name="displayCopyright">
		<xsl:variable name="copyright_node" select="concat('&copy; ', $currentYear, ' &cqCopyright;') "/>
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="$copyright_node"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
