<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>
	<xsl:include href="ProvisionsTable.xsl" />
	<xsl:include href="FootnotesCommon.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Preparing key for filtering content for footnotes section -->
	<xsl:key name="KeysForContentsForFootnotesSection" match="//updatenote[not(.=preceding::updatenote) and not(../ins/update/del and not(../ins/update/ins) and not(../ins/*[name()!='update']) and not(../ins/text())) and not(ancestor::snippet-text) and not(ancestor::del)]
				|//footnote-text[not(ancestor::del) and (not(.=preceding::footnote-text))]|//footnote[not(ancestor::del) and (not(.=preceding::footnote))]
				|//mnote[not(ancestor::del) and (not(.=preceding::mnote))]" use="translate(string(.), '&#xD;&#xA; ', '')" />

	<xsl:variable name="ContentsForFootnotesSectionWithDuplicates" select="//updatenote[not(.=preceding::updatenote) and not(../ins/update/del and not(../ins/update/ins) and not(../ins/*[name()!='update']) and not(../ins/text())) and not(ancestor::snippet-text) and not(ancestor::del)]
				|//footnote-text[not(ancestor::del) and (not(.=preceding::footnote-text))]|//footnote[not(ancestor::del) and (not(.=preceding::footnote))]
				|//mnote[not(ancestor::del) and (not(.=preceding::mnote))]" />

	<!-- Overriding ContentsForFootnotesSection variable using the MUENCHIAN METHOD for filtering duplicated elements -->
	<xsl:variable name="ContentsForFootnotesSection" select="$ContentsForFootnotesSectionWithDuplicates[count(. | key('KeysForContentsForFootnotesSection', translate(string(.), '&#xD;&#xA; ', ''))[1]) = 1]" />

	<xsl:variable name="statusCode" select="//n-metadata/metadata.block/md.history/md.keycite/md.flag.color.code" />

	<!-- Document -->
	<xsl:template match="Document">
		<div id="&documentClass;">

			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&malaysiaDocumentClass;'"/>
			</xsl:call-template>

			<!-- render the CONTENT -->
			<div class="&paraMainClass;">&#160;</div>
			<xsl:apply-templates select="*[not(self::footnote-text or self::updatenote)]"/>

			<xsl:call-template name="RenderFootnotes">
				<xsl:with-param name="footNoteTitle" select="'&footnotesTitle;'" />
			</xsl:call-template>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&malaysiaCopyrightText;</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Links definition -->
	<xsl:template match="link[@tuuid and not(parent::heading)]">
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="@tuuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="fulltext_metadata">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="headings" />
			<div class="&paraMainClass;">
				<xsl:apply-templates select="commencement" />
			</div>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Centered Headings -->
	<xsl:template match="headings">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="heading">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::heading">
			<br/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="commencement">
		<xsl:choose>
			<xsl:when test="start-date">
				<div class="&centerClass; &paraMainClass;">
					<xsl:choose>
						<xsl:when test ="$statusCode='R' and @type='sif'">
							<strong>
								<xsl:text>&ukRepealed;</xsl:text>
							</strong>
						</xsl:when>
						<xsl:when test ="$statusCode='R'">
							<xsl:text>&ukRepealedOn;</xsl:text>
						</xsl:when>
						<xsl:when test ="@type='sda'">
							<xsl:text>&ukThisVersionInForceOn;</xsl:text>
						</xsl:when>
						<xsl:when test ="@type='partial'">
							<xsl:text>&ukThisVersionPartiallyInForceFrom;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&ukVersionInForceFrom;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="@type='sif'">
							<strong>
								<xsl:text>&ukDateNotAvailable;</xsl:text>
							</strong>
							<xsl:if test ="end-date or (@type='lif' or @type='sif' or @type='sif_dateknown') and $statusCode != 'R'">
								<xsl:text>&ukTo;</xsl:text>
							</xsl:if>
						</xsl:when>
						<xsl:when test="start-date = '19910201'">
							<strong>
								<xsl:text>&ukDateNotAvailable;</xsl:text>
							</strong>
							<xsl:if test ="end-date or (@type='lif' or @type='sif' or @type='sif_dateknown') and $statusCode != 'R'">
								<xsl:text>&ukTo;</xsl:text>
							</xsl:if>
						</xsl:when>
						<!-- Render 'date to be appointed' when the start date is 20990101-->
						<xsl:when test ="start-date = '20990101'">
							<strong>
								<xsl:text>&ukDateToBeAppointed;</xsl:text>
							</strong>
						</xsl:when>
						<xsl:otherwise>
							<strong>
								<xsl:value-of select="start-date/@date"/>
							</strong>
							<xsl:if test ="end-date or (@type='lif' or @type='sif' or @type='sif_dateknown') and $statusCode != 'R'">
								<xsl:text>&ukTo;</xsl:text>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>

					<strong>
						<xsl:choose>
							<xsl:when test ="$statusCode = 'R'"/>
							<xsl:when test="end-date = '20990101'">
								<xsl:text>&ukDateToBeAppointed;</xsl:text>
							</xsl:when>
							<xsl:when test="end-date = '99991231'">
								<xsl:text>&ukPresent;</xsl:text>
							</xsl:when>
							<xsl:when test="(@type='basic' or (@type='sif' and end-date) or (@type='sif_dateknown' and end-date) or (@type='nyif' and end-date)) and start-date">
								<xsl:value-of select="end-date/@date"/>
							</xsl:when>
							<xsl:when test="@type='lif' or @type='sif' or @type='sif_dateknown'">
								<xsl:text>&ukPresent;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="end-date/@date"/>
							</xsl:otherwise>
						</xsl:choose>
					</strong>
				</div>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Dates -->
	<xsl:template match="dates">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="section">
		<xsl:call-template name="ProcessParagraphAlignAttribute" />
	</xsl:template>

	<xsl:template match="number">
		<xsl:if test="parent::item">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="title">
		<xsl:variable name="number" select="../number" />
		<xsl:variable name="text" select="." />
		<h2>
			<xsl:value-of select="$number" />
			<xsl:value-of select="' '" />
			<xsl:apply-templates />
		</h2>
	</xsl:template>

	<xsl:template match="para-text">
		<xsl:call-template name="ProcessParagraphAlignAttribute" />
	</xsl:template>

	<xsl:template match="sub1/para-text | sub2/para-text | sub3/para-text">
		<xsl:variable name="number" select="../number" />
		<xsl:choose>
			<xsl:when test="preceding-sibling::para-text[1]">
				<xsl:call-template name="ProcessParagraphAlignAttribute" />
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:value-of select="concat($number,' ')" />
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="paragraph/para-text[not(preceding-sibling::title)]">
		<xsl:variable name="number" select="../number" />
		<h2>
			<xsl:value-of select="$number"/>
		</h2>
		<xsl:call-template name="ProcessParagraphAlignAttribute" />
	</xsl:template>

	<xsl:template match="entry/para-text[ancestor::ins or not(preceding-sibling::para-text)] | updatenote/para-text">
		<xsl:call-template name="ProcessParagraphAlignAttribute">
			<xsl:with-param name="isParagraph" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="effective-date/para-text">
		(<xsl:apply-templates />)
	</xsl:template>

	<xsl:template name="ProcessParagraphAlignAttribute">
		<xsl:param name="isParagraph" select="true()" />
		<xsl:choose>
			<xsl:when test="not(./@align = 'center') and not($isParagraph)">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:attribute name="class">
						<xsl:if test="$isParagraph">
							<xsl:text>&paratextMainClass;</xsl:text>
						</xsl:if>
						<xsl:choose>
							<xsl:when test="./@align='center'">
								<xsl:value-of select="' '" />
								<xsl:text>&alignHorizontalCenterClass;</xsl:text>
							</xsl:when>
							<xsl:when test="./@align='right'">
								<xsl:value-of select="' '" />
								<xsl:text>&alignHorizontalRightClass;</xsl:text>
							</xsl:when>
							<xsl:when test="./@align='left'">
								<xsl:value-of select="' '" />
								<xsl:text>&alignHorizontalLeftClass;</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:attribute>
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- In forms we use space to make a cell large enough to a text to fit in -->
	<xsl:template match="bos[ancestor::entry]">
		<xsl:if test="string-length(normalize-space(string(ancestor::*[1]))) = 0">
			<xsl:value-of select="'&nbsp;'" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="list">
		<div class="&paraMainClass; &indentLeft2Class;">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<ul>
						<xsl:apply-templates />
					</ul>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="ins/item">
		<li>
			<xsl:text>[</xsl:text>
			<div class="&paraMainClass; &indentLeft1Class;">
				<xsl:apply-templates />
				<xsl:if test="following-sibling::node()[1][self::list]">
					<xsl:apply-templates select="following-sibling::node()[1]" />
				</xsl:if>
			</div>
			<xsl:text>]</xsl:text>
			<xsl:call-template name="RenderFootnoteSuperscript">
				<xsl:with-param name="currentFootnote">
					<xsl:apply-templates select="parent::*/preceding-sibling::updatenote/text()|parent::*/preceding-sibling::updatenote/child::*" />
				</xsl:with-param>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="item">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="sub1">
		<div class="&paraMainClass; &paraIndentLeftClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="sub2">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft2Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text() and not(following-sibling::sub2)">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="sub3">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft3Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text()">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="defnlist-item | enacting-text | act-begin/longtitle">
		<div class="&paraMainClass; &paraIndentLeftClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Enable borders for all tables, except for those having frame='none' attribute -->
	<xsl:template match="table">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&tableGroupClass;</xsl:text>
				<xsl:value-of select="' '" />
				<xsl:text>&tableGroupBorderClass;</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ins">
		<xsl:choose>
			<xsl:when test="parent::update/parent::thead or parent::update/parent::tbody">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="tbl//thead/row/entry">
				<xsl:if test="not(tbl//thead/row/preceding-sibling::tbl//thead/row) and not(tbl//thead/row/entry/preceding-sibling::tbl//thead/row/entry)">
					<xsl:text>[</xsl:text>
				</xsl:if>
				<xsl:apply-templates />
				<xsl:if test="not(tbl//thead/row/following-sibling::tbl//thead/row) and not(tbl//thead/row/entry/following-sibling::tbl//thead/row/entry)">
					<xsl:text>]</xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:when test="child::item">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>[</xsl:text>
				<xsl:apply-templates />
				<xsl:text>]</xsl:text>
				<xsl:call-template name="RenderFootnoteSuperscript">
					<xsl:with-param name="currentFootnote">
						<xsl:apply-templates select="preceding-sibling::updatenote/text()|preceding-sibling::updatenote/child::*" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- suppress these -->
	<xsl:template match="toch|tndx|crx|space|si-begin/subject|si-begin/title|si-numbers|act-number|shorttitle|department-code|schedule/title[not(ancestor::longquote)]" />
	<xsl:template match="md.previous.doc.in.sequence | md.next.doc.in.sequence | copyright-message | md.primarycite" />
	<xsl:template match="md.parallelcite[ancestor::n-metadata]"/>

</xsl:stylesheet>