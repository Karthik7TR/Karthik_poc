<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Footnotes.xsl" forcePlatform="true" />
	<xsl:include href="FootnoteReferenceCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<xsl:key name="distinctFootnoteIds" match="footnote | form.footnote | endnote | form.endnote" use="@ID | @id" />
	<xsl:key name="distinctAlternateFootnoteIds" match="footnote//anchor" use="@ID | @id"/>
	<xsl:key name="distinctFootnoteReferenceRefIds" match="footnote.reference | table.footnote.reference | endnote.reference" use="@refid" />
	<xsl:key name="distinctFootnoteAnchorReferenceRefIds" match="internal.reference"  use="@refid" />

	<xsl:template name="RenderFootnoteSection"/>

	<xsl:template match="body.footnote.block | footnote | form.footnote | footnote.body" priority="1"/>

	<!-- The link down to the footnote -->
	<xsl:template match="footnote.reference | table.footnote.reference | endnote.reference">
		<xsl:variable name="fromBadFootnote">
			<xsl:call-template name="isFirstChildFromBadFootnote" />
		</xsl:variable>

		<xsl:if test="$fromBadFootnote = 'false'">
			<xsl:variable name="refNumberOutputText">
				<xsl:call-template name="footnoteCleanup">
					<xsl:with-param name="refNumberTextParam" select="." />
				</xsl:call-template>
			</xsl:variable>

			<xsl:call-template name="generateFootnoteNumberText">
				<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Capture rogue "super" elements that are not contained within "table.footnote.reference"/"footnote.reference"/endnote.reference" elements -->
	<xsl:template match="super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]">
		<xsl:variable name="refNumberOutputText">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="." />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="generateFootnoteNumberText">
			<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="generateFootnoteNumberText">
		<xsl:param name="refNumberText" select="''" />

		<xsl:if test="not($EasyEditMode)">
			<xsl:if test="string-length($refNumberText) &gt; 0">
				<sup>
					<xsl:value-of select="$refNumberText"/>
				</sup>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Suppress footnote references from title metadata -->
	<xsl:template match="/Document/document-data/title//footnote.reference | /Document/document-data/title//table.footnote.reference | /Document/document-data/title//endnote.reference" priority="2" />

	<!-- Capture rogue "super" elements that are not contained within "table.footnote.reference"/"footnote.reference"/endnote.reference" elements -->
	<xsl:template match="/Document/document-data/title//super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]" priority="2" />

</xsl:stylesheet>
