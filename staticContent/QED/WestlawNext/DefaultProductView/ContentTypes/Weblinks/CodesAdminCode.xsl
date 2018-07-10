<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="CitesFromContentMetadata.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="ContextAndAnalysis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<xsl:include href="NotesOfDecisions.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="node()[prelim.block or content.metadata.block or doc.title][1]" priority="1">
		<xsl:call-template name="renderCodeStatuteHeader"/>
	</xsl:template>

	<xsl:template match="n-metadata | popular.name.doc.title | hide.historical.version"/>

	<xsl:template name="email-link" match="paratext/web.address">
		<xsl:variable name="emailid">
			<xsl:value-of select="node()"/>
		</xsl:variable>
		<a>
			<xsl:attribute name="class">
				<xsl:text>&linkClass;</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="href">
				<xsl:value-of select="concat('mailto:', $emailid)"/>
			</xsl:attribute>
			<xsl:copy-of select="$emailid"/>
		</a>
	</xsl:template>
	<!--below code made it separate for table footnotes from regular footnotes. Table footnote reference doesnt have Id, so included text in the Id's-->
	<xsl:template match="table.footnote.reference[ancestor::table]" priority="1">
		<xsl:variable name="fromBadFootnote">
			<xsl:call-template name="isFirstChildFromBadFootnote" />
		</xsl:variable>
		<xsl:if test="$fromBadFootnote = 'false'">
			<xsl:variable name="refNumberOutputText">
				<xsl:call-template name="footnoteCleanup">
					<xsl:with-param name="refNumberTextParam" select="." />
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="generateLinkToTableFootnote">
				<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
				<xsl:with-param name="footnoteRef" select="." />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="footnote[parent::tbl]" mode="footnote">
		<xsl:variable name="footnoteContent">
			<xsl:variable name="firstTextNode" select="descendant::text()[1]" />
			<xsl:variable name="firstTextNodeHasSuperParent" select="string-length($firstTextNode/parent::super) &gt; 0" />
			<xsl:variable name="firstTextNodeIsInternalReference" select="string-length($firstTextNode/parent::internal.reference) &gt; 0" />
			<xsl:variable name="transformedText">
				<xsl:call-template name="trim-start">
					<xsl:with-param name="string">
						<xsl:call-template name="SpecialCharacterTranslator">
							<xsl:with-param name="textToTranslate" select="$firstTextNode" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="endsWithPeriod">
				<xsl:call-template name="ends-with">
					<xsl:with-param name="string1" select="$transformedText" />
					<xsl:with-param name="string2" select="'.'" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="normalizedTextBeforeFirstPeriodPlusSpace" select="normalize-space(substring-before($transformedText, '. '))" />
			<xsl:variable name="normalizedTextBeforeFirstPeriodPlusSpaceWithAlphaCharsRemoved" select="translate($normalizedTextBeforeFirstPeriodPlusSpace, '&englishAlphabetUppercaseAndLowercase;', '')" />

			<xsl:variable name="refNumberTextParam">
				<xsl:choose>
					<xsl:when test="((starts-with($transformedText, 'FN') or starts-with($transformedText, '[FN') or starts-with($transformedText, '(FN') or starts-with($transformedText, 'fn') or starts-with($transformedText, '[fn') or starts-with($transformedText, '(fn')) and contains($transformedText, ' '))">
						<xsl:value-of select="substring-before($transformedText, ' ')" />
					</xsl:when>
					<!-- This "when" clause is a content fix for Codes - State Reg Text -->
					<xsl:when test="(starts-with($transformedText, 'FN') or starts-with($transformedText, '[FN') or starts-with($transformedText, '(FN') or starts-with($transformedText, 'fn') or starts-with($transformedText, '[fn') or starts-with($transformedText, '(fn')) and $firstTextNodeIsInternalReference = true()">
						<xsl:value-of select="$transformedText" />
					</xsl:when>
					<!-- This "when" clause is a content fix for Caselaw RIA Tax Cases (Bug #17137) -->
					<xsl:when test="(starts-with($transformedText, 'FN') or starts-with($transformedText, 'fn')) and $endsWithPeriod = 'true' and not(contains($transformedText, ' '))">
						<xsl:value-of select="$transformedText" />
					</xsl:when>
					<!-- This "when" clause is a content fix for Caselaw WPADC -->
					<xsl:when test="(string(number($normalizedTextBeforeFirstPeriodPlusSpace)) != 'NaN' and number($normalizedTextBeforeFirstPeriodPlusSpace) &gt; 0) or (not(contains($normalizedTextBeforeFirstPeriodPlusSpace, ' ')) and string-length($normalizedTextBeforeFirstPeriodPlusSpace) &lt;= 2 and string-length($normalizedTextBeforeFirstPeriodPlusSpaceWithAlphaCharsRemoved) &gt; 0)">
						<xsl:value-of select="$normalizedTextBeforeFirstPeriodPlusSpace" />
					</xsl:when>
					<xsl:when test="$firstTextNodeHasSuperParent = true()">
						<xsl:value-of select="$transformedText" />
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="string-length($refNumberTextParam) &gt; 0">
					<xsl:variable name="refNumberOutputText">
						<xsl:call-template name="footnoteCleanup">
							<xsl:with-param name="refNumberTextParam" select="$refNumberTextParam" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:call-template name="generateLinkBackToTableFootnoteReference">
						<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
						<xsl:with-param name="footnoteId" select="$refNumberOutputText" />
						<xsl:with-param name="footnoteref" select="//table.footnote.reference[ancestor::row]" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="RenderFootnoteEmptyColumnMarkup"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="footnote.body or form.footnote.body or endnote.body or form.endnote.body">
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="contents">
						<xsl:apply-templates />
					</xsl:variable>
					<xsl:if test="string-length($contents) &gt; 0">
						<xsl:call-template name="RenderFootnoteBodyMarkup">
							<xsl:with-param name="contents" select="$contents"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="RenderFootnoteMarkup">
			<xsl:with-param name="contents" select="$footnoteContent"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="generateLinkBackToTableFootnoteReference">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteId" select="''" />
		<xsl:param name="footnoteref" select="''" />
		<xsl:param name="pertinentFootnote" select="ancestor-or-self::node()[self::footnote or self::form.footnote or self::endnote or self::form.endnote][1]" />
		<xsl:if test="string-length($refNumberText) &gt; 0">
			<xsl:variable name="contents">
				<xsl:apply-templates select="$pertinentFootnote" mode="starPageCalculation" />
				<span>
					<xsl:if test="$footnoteId">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&footnoteIdPrefix;', $footnoteId)"/>
						</xsl:attribute>
					</xsl:if>
					<a href="#co_footnoteReference_{$refNumberText}">
						<xsl:value-of select="$refNumberText"/>
					</a>
				</span>
			</xsl:variable>
			<xsl:call-template name="RenderFootnoteNumberMarkup">
				<xsl:with-param name="contents" select="$contents"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="generateLinkToTableFootnote">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteRef" select="." />
		<xsl:if test="string-length($refNumberText) &gt; 0">
			<sup>
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&footnoteReferenceIdPrefix;',$refNumberText)"/>
				</xsl:attribute>
				<a href="#co_footnote_{$refNumberText}" class="&footnoteReferenceClass;">
					<xsl:value-of select="$refNumberText"/>
				</a>
			</sup>
		</xsl:if>
	</xsl:template>

	<xsl:template match="include.currency.block">
		<xsl:if test="/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',currency.id/@ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!--Code included in order to fix the Bug 499904-->    
<xsl:template match="placeholder.text" />
	
	<!--Code included in order to fix the Bug 319747-->				
	<xsl:template match="n-docbody/refs.annos/grade.notes/ed.note.grade">
		<xsl:apply-templates/>
	</xsl:template>

  <xsl:template match="n-docbody/section/content.metadata.block/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="bottom">
    <xsl:call-template name="wrapContentBlockWithCobaltClass" />
  </xsl:template>
</xsl:stylesheet>