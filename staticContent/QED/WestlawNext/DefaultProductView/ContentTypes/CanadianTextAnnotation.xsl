<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianTable.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render in document -->
	<xsl:template match="content.metadata.block | message.block.carswell" priority="1"/>


	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswTextAndAnnotation;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />

			<xsl:variable name="infotype">
				<xsl:value-of select="normalize-space(n-metadata/metadata.block/md.infotype)"/>
			</xsl:variable>

			<div class="&citesClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="TextAnnotations"/>
			</div>
			
			<xsl:choose>
				<!-- Family Commentary -->
				<xsl:when test="contains($infotype, 'treatise') or contains($infotype, 'form')">
					<div class="&headnotesClass; &centerClass;">
						<xsl:apply-templates select="n-docbody/comment/doc_heading/toc_headings"/>													
							<div class="&authorBylineClass;">
								<xsl:apply-templates select="n-docbody/comment/doc_heading/doc_authors"/>
							</div>
							<xsl:apply-templates select="n-docbody/comment/doc_heading/doc_title"/>						
					</div>
				</xsl:when>
				<!-- IP Commentary -->
				<xsl:when test="contains($infotype, 'crimdig')">
					<div class="&headnotesClass; &centerClass;">
						<xsl:apply-templates select="n-docbody/comment/doc_heading/toc_headings/toc_heading_0" />
						<xsl:apply-templates select="n-docbody/comment/doc_heading/toc_headings/toc_heading_1" />
						<div class="&authorBylineClass;">
							<xsl:apply-templates select="n-docbody/comment/doc_heading/doc_authors"/>
						</div>
						<xsl:apply-templates select="n-docbody/comment/doc_heading/toc_headings" mode="IPCommentary" />
						<xsl:apply-templates select="n-docbody/comment/doc_heading/doc_currency"/>
					</div>
				</xsl:when>				
				<xsl:otherwise>
					<xsl:text>&nbsp;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<!--</div>-->
			<xsl:if test="contains($infotype, 'crimdig')">
				<xsl:apply-templates select="n-docbody/comment/doc_heading/doc_section"/>
			</xsl:if >
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<xsl:apply-templates select="n-docbody/comment/node()[not(self::doc_heading | self::footnote.block | self::p//sup/a[starts-with(@name, 'f')] | self::ul[preceding-sibling::p//sup/a[starts-with(@name, 'f')]] | self::p[preceding-sibling::p//sup/a[starts-with(@name, 'f')]])]"/>

			<!--Render the footnotes-->
			<xsl:variable name="footnote_title">
				<xsl:value-of select="normalize-space(n-docbody/comment/footnote.block/title)"/>
			</xsl:variable>
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

			<xsl:if test="contains($infotype, 'crimdig')">
				<xsl:apply-templates select="n-docbody/comment/content.metadata.block/cmd.reldoc.indicators"/>
			</xsl:if >

			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="toc_heading_0">
		<!-- Make the first line bold (Publication Name) -->
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="toc_heading_1">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="toc_headings">
		<xsl:for-each select="*">
			<xsl:choose>
				<xsl:when test="position() = 1">
					<!-- Make the first line bold -->
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="'&titleClass;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="position() = last()">
					<!-- Check if the last toc_heading line contains info about authors -->
					<xsl:choose>
						<xsl:when test="/Document/n-docbody/comment/doc_heading/doc_authors">
							<!-- Authors node is there - display the whole toc_heading line -->
							<xsl:call-template name="wrapWithDiv"/>
						</xsl:when>
						<xsl:otherwise>
							<!-- Authors nodes are missing - check the last toc_heading -->
							<xsl:variable name="textWithAuthors">
								<xsl:copy-of select="."/>
							</xsl:variable>
							<!-- Show the heading without authors -->
							<xsl:call-template name="wrapWithDiv">
								<xsl:with-param name="contents" select="substring-before($textWithAuthors, ' &#160;')"/>
							</xsl:call-template>
							<!-- Show the Authors -->
							<xsl:if test="contains($textWithAuthors, '&#160;')">
								<xsl:call-template name="wrapWithDiv">
									<xsl:with-param name="class" select="'&authorBylineClass;'"/>
									<xsl:with-param name="contents" select="substring-after($textWithAuthors, ' &#160;')"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="wrapWithDiv"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="toc_headings" mode="IPCommentary">
		<xsl:for-each select="*">
			<xsl:choose>
				<xsl:when test="position() &gt; 2">
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="'&titleClass;'"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="doc_authors" priority="1">
		<xsl:variable name="authors">
			<xsl:variable name="authorsList" select="doc_author"/>
			<xsl:if test="string-length($authorsList) &gt; 0">
				<xsl:for-each select="$authorsList">
					<xsl:apply-templates select="."/>
					<xsl:if test="position() != last()">
						<xsl:text>,<![CDATA[ ]]></xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:variable>

		<xsl:if test="string-length($authors) &gt; 0">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&authorBylineClass;'"/>
				<xsl:with-param name="contents" select="$authors"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="doc_currency">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="doc_section">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&sectionFrontClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="doc_title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- LS Commentary -->
	<!-- The <blkti> element is generally a "short-title" element. -->
	<xsl:template match="blkti">
		<xsl:choose>
			<!-- This means that the blkti contains a footnote reference. Must add a specific class that will instruct the DeliveryInlineFootnoteComponentCRSW.cs to include the delivery of inline footnotes. -->
			<xsl:when test="./sup">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&titleClass;'"/>
				</xsl:call-template>
			</xsl:when>
			<!-- Don't add bottom margin for titles which don't have any text after them. -->
			<xsl:when test="self::node()[not(following-sibling::p or ../descendant-or-self::p) and preceding-sibling::anchor]">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&titleClass;'"/>
				</xsl:call-template>
			</xsl:when>
			<!-- General handling of the <blkti> element. -->
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&titleClass;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="footnote.body/p/title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="footnote.body/p/b" priority="1">
		<strong>
			<xsl:apply-templates/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</strong>
	</xsl:template>

	<!--Add indentation for quotations-->
	<xsl:template match="p/quote">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraIndentLeftClass;'"/>
			<xsl:with-param name="contents">
				<xsl:apply-templates/>
				<xsl:call-template name="ApplySpaceForDelivery"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!--Move block//n element inside the next paragraph-->
	<xsl:template match="block0//p[preceding-sibling::n]" priority="1">
		<div class="&paraMainClass;">
			<!-- Make sure that the preceding-sibling when adding the value of <n> is *not* <p>, because if there is a <p>
		   before this, then that would mean that this is a second paragraph within the <div>. The value of the <n>
		   should only be rendered right before the first paragraph in the <div>. -->
			<xsl:if test="not(preceding-sibling::p)">
				<strong>
					<xsl:value-of select="preceding-sibling::n"/>
				</strong>
				<xsl:text>&crswLabelDessignatorSpace;</xsl:text>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
		<xsl:if test="not(following-sibling::p/quote)">
			<br/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list/n" priority="1">
		<xsl:apply-templates/>
		<xsl:text>&#160;&#160;&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="entry/br">
		<br/>
	</xsl:template>

	<xsl:template match="cmd.reldoc.indicators">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
			<xsl:with-param name="contents">
				<xsl:call-template name="wrapWithSpan">
					<xsl:with-param name="class" select="'&titleClass;'"/>
					<xsl:with-param name="contents">
						<!--   Related Documents-->
						<xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;','&crswMartinsCriminalCodeRelatedDocumentsLabelKey;','&crswMartinsCriminalCodeRelatedDocumentsLabel;')"/>
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

	<!--Ignore following elements-->
	<xsl:template match="block0//n"/>
</xsl:stylesheet>

