<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="Footnotes.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>


	<!--Footnote processing - This does not support the "Inline" option of document delivery.  This was not deemed
		as required at the time of integrating the International content. -->

	<!-- Process the superscript notation for linking to the bottom of the document to the footnote. -->
	<xsl:template match="footnote-text | footnote | fn/fnn">
		<xsl:variable name="footnoteNo">
			<xsl:call-template name="FootnoteNumberReference" />
		</xsl:variable>
		<xsl:variable name="footnoteDislay">
			<xsl:call-template name="FootnoteNumberDisplay" />
		</xsl:variable>
		<sup id="sourcefn{$footnoteNo}" >
			<a href="#targetfn{$footnoteNo}" class="&footnoteReferenceClass;">
				<xsl:value-of select="$footnoteDislay"/>
			</a>
		</sup>
	</xsl:template>

	<!-- process the footnotes section -->
	<xsl:template name="internationalFootnote">
		<xsl:if test="descendant::footnote | descendant::footnote-text | descendant::fn/fnt">
			<!-- Display footnotes at bottom of page -->
				<xsl:choose>
					<xsl:when test="$DeliveryMode">
						<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
							<tr>
								<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
                  <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
                </td>
							</tr>
							<xsl:apply-templates select="descendant::footnote | descendant::footnote-text[not(parent::footnote)] | descendant::fn/fnt" mode="intFootnote"/>
						</table>
					</xsl:when>
					<xsl:otherwise>
						<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
							<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
                <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
              </h2>
							<xsl:apply-templates select="descendant::footnote | descendant::footnote-text[not(parent::footnote)] | descendant::fn/fnt" mode="intFootnote"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- process individual footnote entries in the footnote section -->
	<xsl:template match="footnote | footnote-text | fn/fnt" mode="intFootnote">
		<xsl:variable name="footnoteNo">
			<xsl:call-template name="FootnoteNumberReference" />
		</xsl:variable>
		<xsl:variable name="footnoteDislay">
			<xsl:call-template name="FootnoteNumberDisplay" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:choose>
					<!-- If the footnote has label, we can test and avoid duplicate footnotes -->
					<xsl:when test="@label">
						<xsl:if test="count(preceding::footnote[@label = $footnoteDislay]) = 0">

							<tr>
								<td>
									<span id="targetfn{$footnoteNo}">

										<a href="#sourcefn{$footnoteNo}">
											<xsl:value-of select="$footnoteDislay"/>
										</a>

									</span>
								</td>
								<td>
									<xsl:call-template name="renderFootnoteBody" />
								</td>
							</tr>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td>
								<span id="targetfn{$footnoteNo}">

									<a href="#sourcefn{$footnoteNo}">
										<xsl:value-of select="$footnoteDislay"/>
									</a>

								</span>
							</td>
							<td>
								<xsl:call-template name="renderFootnoteBody" />
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<!-- If the footnote has label, we can test and avoid duplicate footnotes -->
					<xsl:when test="@label">
						<xsl:if test="count(preceding::footnote[@label = $footnoteDislay]) = 0">
							<div>
								<div class="&footnoteNumberClass;">
									<span id="targetfn{$footnoteNo}">
										<a href="#sourcefn{$footnoteNo}">
											<xsl:value-of select="$footnoteDislay"/>
										</a>
									</span>
								</div>
								<div class="&footnoteBodyClass;">
									<xsl:call-template name="renderFootnoteBody" />
								</div>
							</div>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<div class="&footnoteNumberClass;">
								<span id="targetfn{$footnoteNo}">
									<a href="#sourcefn{$footnoteNo}">
										<xsl:value-of select="$footnoteDislay"/>
									</a>
								</span>
							</div>
							<div class="&footnoteBodyClass;">
								<xsl:call-template name="renderFootnoteBody" />
							</div>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="renderFootnoteBody">
		<xsl:choose>
			<!-- This handles the case where we passed in a footnote element -->
			<xsl:when test="child::footnote-text">
				<xsl:apply-templates select="footnote-text" mode="renderFootnote" />
			</xsl:when>
			<xsl:otherwise>
				<!-- This handles all current nodes that are not 'footnote' elements -->
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Stlye each footnote-text element in the document -->
	<xsl:template match="footnote-text" mode="renderFootnote">
		<div>
			<xsl:choose>
				<xsl:when test="@indent = '1'">
					<xsl:attribute name="class">&indentLeft1Class;</xsl:attribute>
				</xsl:when>
				<xsl:when test="@indent = '2'">
					<xsl:attribute name="class">&indentLeft2Class;</xsl:attribute>
				</xsl:when>
				<xsl:when test="@indent = '3'">
					<xsl:attribute name="class">&indentLeft3Class;</xsl:attribute>
				</xsl:when>
				<xsl:when test="@indent = '4'">
					<xsl:attribute name="class">&indentLeft4Class;</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates />
		</div>
		
		<!-- Some content has multiple footnote-text elements in one footnote element.
			Adding a line break and some space between them. -->
		<xsl:if test="following-sibling::footnote-text">
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>
	
	<!-- This template simply counts the number of preceeding elements for footnotes to generate an id to be used for linking and 
			also possibly for display, if the document xml does not have footnote IDs in it. -->
	<xsl:template name="FootnoteNumberReference">
		<!--Since these nodes sometimes appears in the metadata we need to add a predicate
					(text within the square brackets []) so it is only counting the footnote-text nodes within 
					the report node.  Also the xslt count function is zero based, so we add 1.-->
		<xsl:choose>
			<xsl:when test="../descendant::footnote">
				<xsl:value-of select="count(preceding::footnote[ancestor::n-docbody]) + 1" />
			</xsl:when>
			<xsl:when test="../descendant::footnote-text">
				<xsl:value-of select="count(preceding::footnote-text[ancestor::n-docbody]) + 1" />
			</xsl:when>
			<xsl:when test="../descendant::fnt">
				<xsl:value-of select="count(preceding::fn/fnt[ancestor::n-docbody]) + 1" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- This template is used to maintain the display footnote identifier from the original text, if any.
			If there is not, then it simply calls to get the count of the current footnote within the document. -->
	<xsl:template name="FootnoteNumberDisplay">
		<xsl:choose>
			<xsl:when test="descendant-or-self::footnote-text[@footnote]">
				<xsl:value-of select="descendant-or-self::footnote-text/@footnote"/>
			</xsl:when>
			<xsl:when test="../fnn">
				<xsl:value-of select="../fnn"/>
			</xsl:when>
			<xsl:when test="descendant-or-self::footnote[@label]">
				<xsl:value-of select="descendant-or-self::footnote/@label"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FootnoteNumberReference" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- need to supress this element for the EU Legislation documents. 
		If not suppressed, the footnote text will show up right after the 
		<sup> footnote reference in the document as well as in the foontote
		section itself. -->
	<xsl:template match="fn/fnt" />
	
</xsl:stylesheet>
