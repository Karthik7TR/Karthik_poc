<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/*">
		<xsl:call-template name="renderDocumentHeader"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="contents">
				<xsl:apply-templates select="*[not(self::prelim) and not(self::content.metadata.block) and not(self::title) and not(self::image.block)]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="renderDocumentHeader">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="image.block"/>
			<xsl:apply-templates select="//cmd.cites"/>
			<xsl:apply-templates select="prelim"/>
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates select="prelim/currency"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="prelim" priority="1">
		<xsl:variable name="database">
			<xsl:apply-templates select="database.name"/>
		</xsl:variable>
		<xsl:variable name="topic">
			<xsl:apply-templates select="topic"/>
		</xsl:variable>
		<xsl:variable name="subtopic">
			<xsl:apply-templates select="subtopic"/>
		</xsl:variable>
		<div class="&simpleContentBlockClass; &prelimClass;">
			<div class="&centerClass;">
				<xsl:if test="string-length($database) &gt; 0">
					<xsl:copy-of select="$database"/>
				</xsl:if>
				<xsl:if test="string-length($topic) &gt; 0">
					<xsl:if test="string-length($database) &gt; 0">
						<xsl:text>: </xsl:text>
					</xsl:if>
					<xsl:copy-of select="$topic"/>
				</xsl:if>
				<xsl:if test="string-length($subtopic) &gt; 0">
					<xsl:if test="string-length($database) &gt; 0 or string-length($topic) &gt; 0">
						<xsl:text>: </xsl:text>
					</xsl:if>
					<xsl:copy-of select="$subtopic"/>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="database.name | topic | subtopic">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="currency">
		<div class="&dateClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="summary">
		<div class="&simpleContentBlockClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="summary/para/list" priority="1">
		<ul class="&bullListClass;">
			<xsl:apply-templates select="list.item" />
		</ul>
	</xsl:template>

	<xsl:template match="list/list.item" priority="1">
		<li>
			<xsl:apply-templates select="paratext"/>
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="summary//paratext" priority="1">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="suppressLabel" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="summary/para/paratext[following-sibling::node()[1]/self::list]" priority="1">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="division">
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class" />
		</xsl:variable>
		<xsl:if test="jurisdiction or para">
			<div class="&simpleContentBlockClass; {$xmlBasedClassName}">
				<xsl:if test="@id | @ID">
					<xsl:attribute name="id">
						&internalLinkIdPrefix;<xsl:value-of select="@id | @ID"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="jurisdiction"/>
				<ul>
					<xsl:apply-templates select="para"/>
				</ul>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="division/jurisdiction">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="division/para">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

</xsl:stylesheet>
