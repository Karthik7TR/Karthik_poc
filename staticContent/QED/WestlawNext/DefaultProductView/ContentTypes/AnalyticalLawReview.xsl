<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AmpexIndentation.xsl" />
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="AppendixToc.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Overiding the key distinctEligibleStarPagesForDisplay for display of star page anchors in the title block also -->
	<xsl:key name="distinctEligibleStarPagesForDisplay" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[(/Document/document-data/collection = 'w_3rd_lawrevintl' or /Document/document-data/collection = 'w_3rd_lawrevab') and @pageset and not(ancestor::docket.block or ancestor::date.block or ancestor::court.block or ancestor::message.block or ancestor::headnote.block or ancestor::court.headnote.block or ancestor::synopsis or ancestor::archive.headnote.block or ancestor::trial.type.block or ancestor::headnote.publication.block or ancestor::layout.control.block or ancestor::content.layout.block or ancestor::error.block or ancestor::withdrawn.block or ancestor::archive.brief.reference.block)]"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:call-template name="GetCommentaryDocumentEnhancementClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="PublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:apply-templates select="n-docbody/doc/content.metadata.block/cmd.identifiers/cmd.cites" mode="footerCustomCitation" />
			<xsl:call-template name="EndOfDocument"/>
			<xsl:call-template name="PublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="doc">
		<div>
			<xsl:apply-templates select="content.metadata.block[1]" />
			<xsl:apply-templates select="front.matter" />
		</div>
		<xsl:apply-templates select="front.matter/title.block" />
		<xsl:apply-templates select="node()[(not(self::content.metadata.block) or preceding-sibling::content.metadata.block) and not(self::front.matter)]" />
	</xsl:template>

	<xsl:template match="title.block" priority="1">
		<xsl:choose>
			<xsl:when test="'$IsCommentaryEnhancementMode'">
				<!--Render horizontal divider -->
				<div class="&dividerClass;"></div>
				<xsl:call-template name="displayHeadlineHeading">
					<xsl:with-param name="headingSize" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">
						<xsl:text>&simpleContentBlockClass;</xsl:text>
					</xsl:attribute>
					<xsl:call-template name="displayHeadlineHeading">
						<xsl:with-param name="headingSize" select="1"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="displayHeadlineHeading">
		<xsl:param name="headingSize" />
		<xsl:element name="h{$headingSize}">
			<xsl:attribute name="class">
				<xsl:text>&documentHeader; &indentBottomClass;</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="front.matter">
		<xsl:variable name="contents">
			<xsl:apply-templates select="node()[not(self::title.block)]"/>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="contents" select="$contents"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template name="PublisherLogo">
		<xsl:choose>
			<xsl:when test="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.pubtype.name">
				<xsl:call-template name="DisplayPublisherLogo" />
			</xsl:when>
			<xsl:when test="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.view[last()-2] = '&PublisherAsp;'">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&PublisherAsp;'" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
			<!--******************Fix Table Spacing************************-->	
<xsl:template match="appendix[tbl][1]//tbody/row/entry[/Document/document-data/collection = 'w_3rd_lrevintl']" priority="3">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:variable name="textNodes">
			<xsl:apply-templates select="preceding-sibling::entry" mode="appendixToc"/>
			<xsl:if test="./following-sibling::entry">
				<xsl:apply-templates/>
			</xsl:if>
			<xsl:apply-templates select="following-sibling::entry[following-sibling::entry]" mode="appendixToc"/>
		</xsl:variable>
		<td>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
				<xsl:with-param name="contents">
					<xsl:variable name="idRef" >
						<xsl:variable name="headtextId" select="generate-id(/Document/n-docbody/doc/introduction.section/section/appendix[preceding::appendix]/head/headtext[contains(translate(., '&alphabetUppercase; .', '&alphabetLowercase;'), translate($textNodes, '&alphabetUppercase; .', '&alphabetLowercase;'))][1])"/>
						<xsl:choose>
							<xsl:when test="string-length($headtextId) &gt; 0">
								<xsl:value-of select="$headtextId"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="generate-id(/Document/n-docbody/doc/introduction.section/section/appendix[preceding::appendix]/para/paratext[contains(translate(., '&alphabetUppercase; .', '&alphabetLowercase;'), translate($textNodes, '&alphabetUppercase; .', '&alphabetLowercase;'))][1])"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<!-- Apply templates inside a variable so that we don't get links with no text. These links
							 with no text cause delivered word documents to display the link's href as text. Bug 260273 -->
					<xsl:variable name="contents">
						<xsl:apply-templates/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="string-length($idRef) &gt; 0 and string-length($contents) &gt; 0">
							<xsl:text>&#160;&#160;</xsl:text>
							<a class="&internalLinkClass;" href="#co_g_{$idRef}">
								<xsl:apply-templates/>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&#160;&#160;</xsl:text>
							<xsl:apply-templates/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</td>
	</xsl:template>	
	
	<!-- Section: Footer Citation -->
	<xsl:template match="n-docbody/doc/content.metadata.block/cmd.identifiers/cmd.cites" mode="footerCustomCitation">
		<div class="&citesClass;">
			<xsl:apply-templates select ="cmd.first.line.cite"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
