<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Commentary.xsl" forcePlatform="true" />
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianTable.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianCommentaryCommon.xsl"/>
	<xsl:include href="CanadianCommentaryTable.xsl"/>
  

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:value-of select="'&crswTextAndAnnotation;'"/>
					<xsl:call-template name="GetCommentaryDocumentClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
					<xsl:call-template name="GetCommentaryDocumentEnhancementClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:choose>
				<xsl:when test="$EasyEditMode">
					<xsl:apply-templates select="n-docbody" mode="EasyEdit"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="//md.form.flag">
						<xsl:call-template name="EasyEditFlag"/>
					</xsl:if>
					<xsl:call-template name="StarPageMetadata" />
					<xsl:apply-templates select="n-docbody | Section" />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:variable name="infotype">
				<xsl:value-of select="normalize-space(n-metadata/metadata.block/md.infotype)"/>
			</xsl:variable>
			<xsl:if test="contains($infotype, 'crimdig')">
				<xsl:apply-templates select="n-docbody/doc/content.metadata.block/cmd.reldoc.indicators"/>
			</xsl:if>
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo"/>
		</div>
	</xsl:template>

	<xsl:template match="n-docbody" priority="2">
		<xsl:apply-templates/>
		<!--Render the footnotes-->
		<xsl:variable name="footnote_title" select="normalize-space(.//footnote.block/title | .//footnote.block/head/headtext)"/>
		<xsl:variable name="lowercase_title">
			<xsl:call-template name="lower-case">
				<xsl:with-param name="string" select="$footnote_title" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<!--If footnotes have a meaningful title - use it-->
			<xsl:when test="string-length($footnote_title) &gt; 0 and not($lowercase_title = 'notes' or $lowercase_title = 'endnote')">
				<xsl:call-template name="RenderFootnoteSection">
					<xsl:with-param name="title" select="$footnote_title"/>
				</xsl:call-template>
			</xsl:when>
			<!--There is no meaningful title - render as usual-->
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteSection"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="doc" priority="1">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<div>
					<xsl:call-template name="doc"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
        <div class="&headnotesClass; &centerClass;">
          <xsl:call-template name="doc"/>
        </div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="node()[not(self::content.metadata.block) or preceding-sibling::content.metadata.block]" />
	</xsl:template>

	<xsl:template name="doc">
		<xsl:apply-templates select="content.metadata.block[1][cmd.identifiers/cmd.cites]" />
		<xsl:if test="not(content.metadata.block[1][cmd.identifiers/cmd.cites])">
			<xsl:call-template name="DisplayMetadataCites"/>
		</xsl:if>
		<xsl:apply-templates select="prop.block[1]" mode="PropBlock">
			<xsl:with-param name="appendContent">
				<!-- Display author name in prop.block container. -->
				<xsl:apply-templates select="grade.notes/author.line"/>
				<xsl:apply-templates select="section/section.front/author.line"/>
				<xsl:apply-templates select="article/article.front/author.line"/>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="prop.block[preceding-sibling::prop.block]" mode="PropBlock"/>
		<xsl:apply-templates select="article/article.front/doc.title"/>
		<xsl:apply-templates select="section[not(../grade.notes/doc.title)]/section.front/doc.title"/>
		<xsl:apply-templates select="grade.notes/doc.title"/>
	</xsl:template>

	<xsl:template match ="prop.block" mode="PropBlock" priority="2">
		<xsl:param name="appendContent"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents">
				<xsl:apply-templates/>
				<xsl:copy-of select="$appendContent"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prop.block[1]/prop.head">
		<xsl:param name="divId"/>
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:call-template name="head">
					<xsl:with-param name="divId" select="$divId"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="head-legacy">
					<xsl:with-param name="class">
						<xsl:choose>
							<xsl:when test="count(preceding-sibling::prop.head) = 1">
								<xsl:value-of select="false()"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'&titleClass;'"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="divId" select="$divId"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="head-legacy">
		<xsl:param name="class"/>
		<xsl:param name="divId"/>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="id" select="$divId"/>
			<xsl:with-param name="contents">
				<xsl:for-each select="headtext">
					<xsl:call-template name="render-sibling-name-designator"/>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="DisplayMetadataCites">
		<xsl:variable name="contents">
			<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.identifiers/md.cites" mode="TextAnnotations"/>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&citesClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="author.line | author.block" priority="1">
		<xsl:variable name="contents">
			<xsl:apply-templates />
			<xsl:for-each select="following-sibling::author.line | following-sibling::author.block">
				<xsl:text>,<![CDATA[ ]]></xsl:text>
				<xsl:apply-templates/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:call-template name="wrapContentBlockWithCobaltClass">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&authorBylineClass;'"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="author.line[preceding-sibling::author.line] | author.block[preceding-sibling::author.block]" priority="1"/>

	<!-- Prevent double wrapping with sup element. -->
	<xsl:template match="super[footnote.reference]">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="grade.notes">
		<xsl:variable name="contents">
			<!-- Display author name in prop.block and doc.title in doc container. -->
			<xsl:apply-templates select="node()[not(self::doc.title or self::author.line)]" />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="id">
					<xsl:if test="@ID|@id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID|@id)" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="contents" select="$contents"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="doc[grade.notes/doc.title]//section/section.front">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="doc.title" priority="2">
		<xsl:call-template name="docTitle">
			<xsl:with-param name="class">
				<xsl:choose>
					<xsl:when test="$IsCommentaryEnhancementMode">&titleClass;</xsl:when>
					<xsl:otherwise><!--&crswLongTitle;--></xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Consider grade.notes/doc.title as document title and the rest as paragraph titles. -->
	<xsl:template match="doc[grade.notes/doc.title]//section/section.front/doc.title" priority="3">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="doc[grade.notes/doc.title]//section/section.front/doc.title/head/headtext">
		<xsl:param name="divId"/>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">
				<xsl:choose>
					<xsl:when test="$IsCommentaryEnhancementMode">&headtextClass;</xsl:when>
					<xsl:otherwise>&titleClass; &crswTopMargin; &crswBottomMargin;</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="id" select="$divId"/>
			<xsl:with-param name="contents">
				<xsl:call-template name="render-sibling-name-designator"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!--Don't render chunkMarker for inner sections. -->
	<xsl:template match="section[ancestor::section or following-sibling::section]" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Replace core list classes to have margins between 2 sibling list element on UI.-->
	<xsl:template match="list" priority="2">
		<div class="&paraIndentLeftClass; &paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="list/list.item">
		<xsl:apply-templates/>
	</xsl:template>

	<!--Suppress label.designator if it is rendered in paratext-->
	<xsl:template match="label.designator[following-sibling::*[1][self::paratext or self::form.text or self::para/text.line][.//text() or .//leader]]">
		<xsl:if test="/Document/document-data/collection = 'w_an_ea_catexts'">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="cmd.reldoc.indicators">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
			<xsl:with-param name="contents">
				<xsl:call-template name="wrapWithSpan">
					<xsl:with-param name="class" select="'&titleClass;'"/>
					<xsl:with-param name="contents">
						<!--   Related Documents-->
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;','&crswMartinsCriminalCodeRelatedDocumentsLabelKey;','&crswMartinsCriminalCodeRelatedDocumentsLabel;')"/>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:apply-templates/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cmd.reldoc.indicator">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="classification.block">
		<xsl:if test="$IAC-MASTERTAX-DOC-TABLE">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para" priority="2">
		<xsl:call-template name="para"/>
	</xsl:template>

	<xsl:template match="para[string-length(paratext//text()) = 0 and string-length(label.designator/text()) &gt; 0]" priority="2">
		<xsl:call-template name="crswParaWithoutParatext">
			<xsl:with-param name="labelDesignator">
				<strong>
					<xsl:value-of select="normalize-space(label.designator/text())"/>
				</strong>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!--For Carswell we should have 5 spaces for label.designator and a bold label-->
	<xsl:template match="label.designator[not(ancestor::list)]" mode="para-label.designator" priority="2">
		<strong>
			<xsl:apply-templates />
		</strong>
		<xsl:text>&crswLabelDessignatorSpace;</xsl:text>
	</xsl:template>

	<!-- Don't render GoTo widget classes. -->
	<xsl:template match="paratext[preceding-sibling::label.designator]" priority="2">
		<xsl:call-template name="renderParagraphTextDiv" />
	</xsl:template>

	<xsl:template name="calcColWidth">
		<xsl:param name="columnInfo"/>
		<xsl:param name="proportionalTotal"/>
		<xsl:choose>
			<xsl:when test="$proportionalTotal &gt; 0">
				<xsl:value-of select="concat(round(substring-before(@colwidth, '*') div $proportionalTotal * 100), '%')"/>
			</xsl:when>
			<xsl:when test="@colwidth = '*'">
				<xsl:variable name="nonEmptyTotal">
					<xsl:call-template name="sumProportionalWidths">
						<xsl:with-param name="nodes" select="$columnInfo[not(@colwidth='*')]/@colwidth"/>
						<xsl:with-param name="total" select="0"/>
						<xsl:with-param name="index" select="1"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="emptyCount" select="count($columnInfo[@colwidth='*'])"/>
				<xsl:value-of select="concat(round((100 - $nonEmptyTotal) div $emptyCount), '%')"/>
			</xsl:when>
			<xsl:when test="contains(@colwidth, '*')">
				<xsl:value-of select="translate(@colwidth, '*', '%')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@colwidth"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>