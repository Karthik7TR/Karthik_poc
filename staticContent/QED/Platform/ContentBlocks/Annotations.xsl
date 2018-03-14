<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="ProcessAnnotations">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="contents">
				<xsl:variable name="editorsNotes">
					<xsl:apply-templates select="node()[not(self::nod.block)]" />
				</xsl:variable>
				<xsl:if test="string-length($editorsNotes) &gt; 0">
					<xsl:if test="not(substring-before($editorsNotes, ' ') = 'LIBRARY')">
						<div eBookEditorNotes="true" class="&printHeadingClass;">
							<h2>&editorsNotes;</h2>
						</div>
					</xsl:if>
					<xsl:copy-of select="$editorsNotes"/>
				</xsl:if>
				<xsl:apply-templates select="nod.block[1]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="annotations">
		<xsl:call-template name="ProcessAnnotations" />
	</xsl:template>

	<xsl:template match="reference.block/ed.note.reference.block">
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
