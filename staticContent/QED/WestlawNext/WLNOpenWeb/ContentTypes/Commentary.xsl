<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Commentary.xsl" forceDefaultProduct="true"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:call-template name="GetCommentaryDocumentEnhancementClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:apply-templates select="n-docbody/doc"/>
			<xsl:call-template name="EndOfDocument" />

		</div>
	</xsl:template>

	<!-- title only -->
	<xsl:template match="doc"  name="docBase">
		<div>
			<xsl:apply-templates select="content.metadata.block[1]" />
			<xsl:apply-templates select="prop.block[1]" mode="PropBlock">
				<xsl:with-param name="appendContent">
					<xsl:apply-templates select="article/article.front/author.line"/>
					<xsl:apply-templates select="newsletter.article/newsletter.article.front/author.line"/>
					<xsl:apply-templates select="journal.article/journal.article.front/author.line"/>
					<xsl:apply-templates select="section/section.front/author.line"/>
				</xsl:with-param>
			</xsl:apply-templates>
			
			<!-- 
				these are small documents, find title in MOST generic way even though it may be slower
				plus it's just for OpenWeb 
			-->			
			<xsl:apply-templates select="//doc.title"/>
			
			<!-- more specific, but more error prone (you may miss some titles doing it this way -->
			<!--<xsl:apply-templates select="*/section.front/doc.title"/>	
			<xsl:apply-templates select="article/article.front/doc.title"/>
			<xsl:apply-templates select="newsletter.article/newsletter.article.front/doc.title"/>
			<xsl:apply-templates select="journal.article/journal.article.front/doc.title"/>
			<xsl:apply-templates select="appendix/doc.title"/>
			<xsl:apply-templates select="grade.notes/doc.title"/>	
			<xsl:apply-templates select="correlation.tbl.block/doc.title"/>-->	

			<!-- again more specific, but you may miss some titles doing it this way -->
			<!--<xsl:apply-templates select="*/*/doc.title"/>	
			<xsl:apply-templates select="*/doc.title"/>-->
		
		</div>
	</xsl:template>

	<xsl:template match="footnote.reference" />
	
</xsl:stylesheet>