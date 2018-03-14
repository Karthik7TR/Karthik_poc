<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CiteLinesInMetaData.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:include href="Copyright.xsl"/>
  <xsl:include href="CommentaryTable.xsl"/>
  <xsl:include href="CommentaryCommon.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType">
          <xsl:call-template name="GetCommentaryDocumentClasses"/>
          <xsl:value-of select="' &contentTypeJuryInstructionsClass;'"/>
        </xsl:with-param>
      </xsl:call-template>
			<xsl:choose>
				<xsl:when test="$EasyEditMode">
					<xsl:apply-templates select="node()" mode="EasyEdit"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="//md.form.flag">
						<xsl:call-template name="EasyEditFlag"/>
					</xsl:if>
					<xsl:call-template name="StarPageMetadata" />
					<xsl:call-template name="documentHead"/>
					<xsl:apply-templates select="n-docbody"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="documentHead">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&documentHeadClass;'" />
			<xsl:with-param name="contents">
				<xsl:apply-templates select="//md.cites | //prop.block"/>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Bug 191321- The copy rightblock(2012) was showing after the line in word and Pdf-->
	
		<xsl:template match="cmd.copyright">
		<div class="&copyrightClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>	
	
	<xsl:template match="doc">
		<xsl:apply-templates select="*[not(self::prop.block)]" />
	</xsl:template>
</xsl:stylesheet>
