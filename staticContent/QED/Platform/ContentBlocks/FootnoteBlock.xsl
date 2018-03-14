<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Footnotes.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="n-docbody" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="footnote.block" name="footnoteBlock">
		<xsl:param name="suppressHeading" select="false()" />
		<xsl:param name="id" />
		<xsl:call-template name="RenderFootnoteBlockMarkup">
			<xsl:with-param name="id">
				<xsl:choose>
					<xsl:when test="$id">
						<xsl:value-of select="$id" />
					</xsl:when>
					<xsl:when test="@id | @ID">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', (@id | @ID))" />
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="suppressHeading" select="boolean(preceding-sibling::footnote.block or $suppressHeading)" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderFootnoteBlockMarkup">
		<xsl:param name="suppressHeading" select="false()" />
		<xsl:param name="id"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteBlockMarkupTable">
					<xsl:with-param name="suppressHeading" select="$suppressHeading" />
					<xsl:with-param name="id" select="$id"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteBlockMarkupDiv">
					<xsl:with-param name="suppressHeading" select="$suppressHeading" />
					<xsl:with-param name="id" select="$id"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteBlockMarkupTable">
		<xsl:param name="suppressHeading" select="false()" />
		<xsl:param name="id"/>
		<table class="&footnoteSectionClass;">
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="not($suppressHeading)">
				<tr>
					<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
            <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
          </td>
				</tr>
			</xsl:if>
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template name="RenderFootnoteBlockMarkupDiv">
		<xsl:param name="suppressHeading" select="false()" />
		<xsl:param name="id"/>
		<div class="&footnoteSectionClass;">
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="not($suppressHeading)">
				<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
        </h2>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="endnote.block[ancestor::section and preceding-sibling::head] |
								endnote[ancestor::endnote.block and preceding-sibling::head]" priority="2">
		<xsl:call-template name="footnoteBlock">
			<xsl:with-param name="suppressHeading" select="true()"/>
			<xsl:with-param name="id">
				<xsl:if test="@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="footnote | form.footnote | endnote | form.endnote" priority="1">
		<xsl:apply-templates select="." mode="footnote"/>
	</xsl:template>

	<xsl:template match="footnote[not(ancestor::footnote.block or ancestor::endnote.block)]
										 | form.footnote[not(ancestor::footnote.block or ancestor::endnote.block)]
										 | endnote[not(ancestor::footnote.block or ancestor::endnote.block)]
										 | form.endnote[not(ancestor::footnote.block or ancestor::endnote.block)]" priority="2">
		<xsl:call-template name="RenderFootnoteWithoutBlockAncestorMarkup"/>
	</xsl:template>

	<xsl:template name="RenderFootnoteWithoutBlockAncestorMarkup">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteWithoutBlockAncestorMarkupTable"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteWithoutBlockAncestorMarkupDiv"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteWithoutBlockAncestorMarkupTable">
		<xsl:variable name="footnoteContent">
			<xsl:apply-templates select="." mode="footnote"/>
		</xsl:variable>
		<xsl:if test="string-length($footnoteContent) &gt; 0">
			<table class="&footnoteSectionClass;">
				<xsl:copy-of select="$footnoteContent"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderFootnoteWithoutBlockAncestorMarkupDiv">
		<xsl:variable name="footnoteContent">
			<xsl:apply-templates select="." mode="footnote"/>
		</xsl:variable>
		<xsl:if test="string-length($footnoteContent) &gt; 0">
			<div class="&footnoteSectionClass;">
				<xsl:copy-of select="$footnoteContent"/>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
