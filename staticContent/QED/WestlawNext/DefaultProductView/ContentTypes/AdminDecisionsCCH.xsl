<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AdminDecisionCommentary.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:value-of select="'&contentTypeAdminDecisionClass;'"/>
					<xsl:call-template name="GetCommentaryDocumentClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
					<xsl:call-template name="GetCommentaryDocumentEnhancementClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="not($DeliveryMode)">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&CommerceClearingHouse;'" />
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates />
			<xsl:call-template name="RenderFootnote" />
			<xsl:apply-templates select="n-docbody/header/prelim/copyright"/>
			<xsl:call-template name="EndOfDocument" />
			<xsl:if test="not($DeliveryMode)">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&CommerceClearingHouse;'" />
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>
	
	<!-- BUG 403005 - Suppress Star Paging -->
	<xsl:template match="starpage.anchor" priority="2"/>

	<!-- Bug 405057 - headnote element is like a para element -->
	<xsl:template match="headnote" priority="2">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Bug 405057 - headnote.text element is like a paratext element -->
	<xsl:template match="headnote.text" priority="2">
		<xsl:call-template name="renderParagraphTextDiv" />
	</xsl:template>	

	<xsl:template match="front.matter[title.block]" name="frontMatterWithTitleBlockOrig">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:if test="not(../front.matter/date.block)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>
	
	<!-- Bug 403358 - byline is not centered as it states in the survey. -->
	<xsl:template match="author.byline" priority="2">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>	

	<xsl:template match="front.matter[date.block]" name="frontMatterWithDateBlockOrig">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Bug 407955 - links are not all linked up like in web2...also new lines are not added for enline elements 
			updates will also have to be made to UrlBuilder to enable the w-ref-type of "LC".  -->
	<xsl:template match="endline">
		<br />
	</xsl:template>

	<!-- Fixed defect 377423, The template is copied from Web2 XSLT for Intendation-->
	<xsl:template match="para" priority="1">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<!-- Call template from Para.xsl -->
				<xsl:call-template name="para">
					<xsl:with-param name="className">
						<xsl:value-of select="'&paraMainClass;'"/>
						<xsl:if test="parent::para">
							<xsl:value-of select="' &paraIndentLeftClass;'"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!--Renders original non enhanced markup. -->
				<xsl:choose>
					<xsl:when test="parent::para">
						<div class="&paraIndentLeftClass;">
							<xsl:apply-templates />
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="&paratextMainClass;">
							<xsl:apply-templates />
						</div>
						<xsl:text>&#160;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteNumberMarkupTable" priority="1">
		<xsl:param name="contents"/>
		<td class="&footnoteNumberLargeClass;">
			<xsl:copy-of select="$contents"/>
		</td>
	</xsl:template>

	<xsl:template name="RenderFootnoteBodyMarkupTable" priority="1">
		<xsl:param name="contents"/>
		<td class="&footnoteBodySmallClass;">
			<xsl:copy-of select="$contents"/>
		</td>
	</xsl:template>

	<xsl:template match="prelim.synopsis.body/para" priority="2" />

	<xsl:template match="grade.head">
		<xsl:variable name="dateline" select="//front.matter/date.block/date.line/text()" />
		<xsl:variable name="month" select="substring-before($dateline, ' ')"/>
		<xsl:choose>
			<xsl:when test="$dateline and string-length(text()) &lt;= string-length($dateline) and starts-with(text(), $month)" />
			<xsl:otherwise>
				<xsl:call-template name="wrapContentBlockWithGenericClass" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Bug 451306 - Center prelim.synopsis/prelim.synopsis.body/doc.type.head text.
			There are other prelim synopsis elements that should not be centered 
			(e.g. /Document/n-docbody/decision/prelim.synopsis/address.block)-->
	<xsl:template match="prelim.synopsis/prelim.synopsis.body/doc.type.head[not(ancestor::prelim.block)]">
		<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
			<xsl:with-param name="additionalClass" select="'&centerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Commentary enhancements overrides BEGIN-->

	<xsl:template match="front.matter[title.block]" priority="1">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:call-template name="frontMatterBase">
					<xsl:with-param name="suppressHeadComment" select="../front.matter/date.block"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!--Renders original non enhanced markup. -->
				<xsl:call-template name="frontMatterWithTitleBlockOrig"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Commentary enhancements overrides END-->

</xsl:stylesheet>
