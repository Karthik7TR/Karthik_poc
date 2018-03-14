<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="NotesOfDecisions.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="ContextAndAnalysis.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Annotations.xsl"/>
	<xsl:include href="AddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStateAdminCodesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="FooterCitation"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="node()[prelim.block or content.metadata.block or doc.title][1]" priority="1">
		<xsl:call-template name="renderCodeStatuteHeader"/>
	</xsl:template>

	<xsl:template match="doc.title" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- footer Citation-->
	<xsl:template name="FooterCitation" priority="2">
		<xsl:choose>
			<xsl:when test="contains('|w_codesadccinvdp|', concat('|',  /Document/document-data/collection , '|'))">
				<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.expandedcite"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="n-docbody/section/content.metadata.block/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="bottom"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="n-docbody/section/content.metadata.block/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="bottom">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>


	<xsl:template match="ed.note.grade">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgp']" priority="1">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="popular.name.doc.title"/>



	<!-- Table Footnotes -->
	<xsl:template match="footnote[parent::tbl]" priority="2">
		<div class="&footnoteBodyClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="table.footnote.reference[ancestor::table]" priority="2">
		<xsl:variable name="tblId" select="ancestor::tbl/@ID" />
		<xsl:variable name="refNumberTextParam" select="." />
		<xsl:variable name="refNumberText" select="substring-after(substring-before($refNumberTextParam, ']'),'[FN')" />
		<xsl:variable name="refNumber" select="translate($refNumberText,'*','s')" />
		<xsl:if test="string-length($refNumber) &gt; 0">
			<sup id="co_table_footnote_reference_{$tblId}_{$refNumber}">
				<a href="#co_table_footnote_{$tblId}_{$refNumber}" class="&footnoteReferenceClass;">
					<xsl:value-of select="$refNumberText"/>
				</a>
			</sup>
		</xsl:if>
	</xsl:template>

	<xsl:template match="table.footnote.reference[ancestor::footnote]" priority="2">
		<xsl:variable name="tblId" select="ancestor::tbl/@ID" />
		<xsl:variable name="refNumberTextParam" select="." />
		<xsl:variable name="refNumberText" select="substring-after(substring-before($refNumberTextParam, ']'),'[FN')" />
		<xsl:variable name="refNumber" select="translate($refNumberText,'*','s')" />
		<span id="co_table_footnote_{$tblId}_{$refNumber}">
			<a href="#co_table_footnote_reference_{$tblId}_{$refNumber}" class="&footnoteReferenceClass;">
				<xsl:value-of select="$refNumberText"/>
			</a>
		</span>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- override the cite.query match for only cite.query elements that are "Refs and Annos" links (in the TOC). -->
	<xsl:template match ="cite.query[not(./@w-pub-number) and ./@w-ref-type = 'CM' and /Document/document-data/doc-type-id = '105' and contains(text(),'(Refs &amp; Annos)')]">
		<xsl:call-template name ="citeQuery">
			<xsl:with-param name="originationPubNum" select="/Document/n-metadata/metadata.block/md.publications/md.publication/md.pubid" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="internet.url">
		<a class="&pauseSessionOnClickClass;" target="_blank">
			<xsl:attribute name="href">
				<xsl:if test="not(starts-with(cite.query/@w-normalized-cite, 'http://')) and not(starts-with(cite.query/@w-normalized-cite, 'https://'))">
					<xsl:text>http://</xsl:text>
				</xsl:if>
				<xsl:apply-templates select="cite.query/@w-normalized-cite"/>
			</xsl:attribute>
			<xsl:value-of select="normalize-space(.)"/>
		</a>
	</xsl:template>

	<!-- Suppress <crcl> nodes (contains citation) - Mark Nordstrom - Bug #796058 - Sample Guid: IEB78D9D019E111E5BBFB8458372B6C54 -->
	<xsl:template match="crcl" />

</xsl:stylesheet>
