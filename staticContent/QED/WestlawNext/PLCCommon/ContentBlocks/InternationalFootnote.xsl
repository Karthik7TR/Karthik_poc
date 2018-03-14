<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="InternationalFootnote.xsl" forceDefaultProduct="true"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>


	<!--Footnote processing - This does not support the "Inline" option of document delivery.  This was not deemed
		as required at the time of integrating the International content. -->

	<!-- Process the superscript notation for linking to the bottom of the document to the footnote. -->
	<xsl:template match="footnote-text[not(parent::footnote) and not(parent::author_footnote)] | footnote | fn/fnn">
		<xsl:variable name="footnoteNo">
			<xsl:call-template name="FootnoteNumberReference" />
		</xsl:variable>
		<xsl:variable name="footnoteDislay">
			<xsl:call-template name="FootnoteNumberDisplay" />
		</xsl:variable>
		<xsl:call-template name="displayFootnote">
			<xsl:with-param name="footnoteNo" select="$footnoteNo"/>
			<xsl:with-param name="footnoteDisplay" select="$footnoteDislay"/>
		</xsl:call-template>
	</xsl:template>

	<!-- process the footnotes section -->
	<xsl:template name="internationalFootnote">
		<xsl:if test="descendant::footnote | descendant::footnote-text[not(parent::footnote) and not(parent::author_footnote)] | descendant::fn/fnt">
			<!-- Display footnotes at bottom of page -->
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<tr>
							<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
							</td>
						</tr>
						<xsl:apply-templates select="descendant::footnote | descendant::footnote-text[not(parent::footnote) and not(parent::author_footnote)] | descendant::fn/fnt" mode="intFootnote"/>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
						</h2>
						<xsl:apply-templates select="descendant::footnote | descendant::footnote-text[not(parent::footnote) and not(parent::author_footnote)] | descendant::fn/fnt" mode="intFootnote"/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- process individual footnote entries in the footnote section -->
	<xsl:template match="footnote | footnote-text[not(parent::footnote) and not(parent::author_footnote)] | fn/fnt | author_footnote" mode="intFootnote">
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
									<xsl:call-template name="displayFootnoteWithinBody">
										<xsl:with-param name="footnoteNo" select="$footnoteNo"/>
										<xsl:with-param name="footnoteDisplay" select="$footnoteDislay"/>
									</xsl:call-template>
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
								<xsl:call-template name="displayFootnoteWithinBody">
									<xsl:with-param name="footnoteNo" select="$footnoteNo"/>
									<xsl:with-param name="footnoteDisplay" select="$footnoteDislay"/>
								</xsl:call-template>
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
									<xsl:call-template name="displayFootnoteWithinBody">
										<xsl:with-param name="footnoteNo" select="$footnoteNo"/>
										<xsl:with-param name="footnoteDisplay" select="$footnoteDislay"/>
									</xsl:call-template>
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
								<xsl:call-template name="displayFootnoteWithinBody">
									<xsl:with-param name="footnoteNo" select="$footnoteNo"/>
									<xsl:with-param name="footnoteDisplay" select="$footnoteDislay"/>
								</xsl:call-template>
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
			<xsl:when test="../descendant::footnote-text[not(parent::footnote) and not(parent::author_footnote)]">
				<xsl:value-of select="count(preceding::footnote-text[ancestor::n-docbody]) + 1" />
			</xsl:when>
			<xsl:when test="../descendant::fnt">
				<xsl:value-of select="count(preceding::fn/fnt[ancestor::n-docbody]) + 1" />
			</xsl:when>
			<xsl:when test="../descendant::author_footnote">
				<xsl:variable name="count" select="count(./preceding-sibling::*)+1"/>
				<xsl:value-of select="concat('asterisk',$count)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

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
			<xsl:when test="self::author_footnote">
				<xsl:variable name="count" select="count(./preceding-sibling::*)+1"/>
				<xsl:call-template name="asterisk">
					<xsl:with-param name="input" select="'*'"/>
					<xsl:with-param name="current-number" select="1"/>
					<xsl:with-param name="max-number" select="$count"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FootnoteNumberReference" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="asterisk">
		<xsl:param name="input"/>
		<xsl:param name="current-number"/>
		<xsl:param name="max-number"/>
		<xsl:choose >
			<xsl:when test="$current-number &lt; $max-number">
				<xsl:call-template name="asterisk">
					<xsl:with-param name="input" select="concat($input,'*')"/>
					<xsl:with-param name="current-number" select="$current-number + 1"/>
					<xsl:with-param name="max-number" select="$max-number"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$input"/>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="displayFootnote">
		<xsl:param name="footnoteNo"/>
		<xsl:param name="footnoteDisplay"/>
		<sup id="&footnoteReferenceIdPrefix;{$footnoteNo}" >
			<a href="#&footnoteIdPrefix;{$footnoteNo}" class="&footnoteReferenceClass;">
				<xsl:value-of select="$footnoteDisplay"/>
			</a>
		</sup>
	</xsl:template>

	<xsl:template name="displayFootnoteWithinBody">
		<xsl:param name="footnoteNo"/>
		<xsl:param name="footnoteDisplay"/>
		<span id="&footnoteIdPrefix;{$footnoteNo}">
			<a href="#&footnoteReferenceIdPrefix;{$footnoteNo}">
				<xsl:value-of select="$footnoteDisplay"/>
			</a>
		</span>
	</xsl:template>

</xsl:stylesheet>
