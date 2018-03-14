<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl" />
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="n-metadata/metadata.block/md.references"  mode="HeaderCustomization"/>
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="n-metadata/metadata.block/md.references">
		<xsl:apply-templates select="node()[not(self :: md.print.rendition.id)]"/> 
	</xsl:template>
	
	<xsl:template match="n-metadata/metadata.block/md.references" mode="HeaderCustomization">
		<xsl:apply-templates select="md.print.rendition.id"/>
	</xsl:template>

	<xsl:template match="publication.year" priority="5">
		<div>
			<xsl:text>&roalPublished;</xsl:text>
			<xsl:apply-templates/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>
	
	<xsl:template match="md.cites" priority="5">
		<xsl:call-template name="getCitation" />
	</xsl:template>
	
	<xsl:template match="message.block/include.copyright" priority="1">
		<div class="&copyrightClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="preformatted.text.block">
		<div class="&simpleContentBlockClass; &courierClass;">
			<xsl:apply-templates />
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="preformatted.text.block/preformatted.text.line">
		<xsl:variable name="contents">
			<xsl:call-template name="PreformattedTextCleaner"/>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:copy-of select="$contents"/>
		</xsl:if>
	<xsl:text>&nbsp;</xsl:text>
	</xsl:template>


</xsl:stylesheet>
