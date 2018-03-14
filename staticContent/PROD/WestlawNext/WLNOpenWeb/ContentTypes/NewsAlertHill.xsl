<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="NewsAlertHill.xsl" forceDefaultProduct="true"/>


	<!-- OPEN WEB - Limit text displayed -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeNewsAlertHillClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite"/>
			<xsl:call-template name="displayCopyright"/>
			<xsl:apply-templates select="n-metadata/metadata.block/md.publications/md.publication/md.pubname"/>
			<xsl:apply-templates select="n-docbody/news.alert.document/article/article.title"/>
			<xsl:apply-templates select="n-docbody/news.alert.document/article/article.date"/>
			<xsl:apply-templates select="n-docbody/news.alert.document/article/author.byline"/>
			<xsl:variable name="Contents">
				<div>
					<xsl:apply-templates select="n-docbody/news.alert.document/article/lead.para"/>
					<xsl:apply-templates select="n-docbody/news.alert.document/article/para"/>
				</div>
			</xsl:variable>
			<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents,&LimitTextByCharacterCountValue;)"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

</xsl:stylesheet>