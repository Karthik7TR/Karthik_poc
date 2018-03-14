<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="index" name="index">
		<div class="&indexClass;">
			<xsl:if test="@ID|@id">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()[not(self::index.entry)]"/>
			<xsl:if test="index.entry">
				<ol class="&tocMainClass;">
					<xsl:apply-templates select="index.entry"/>
				</ol>
			</xsl:if>
		</div>
	</xsl:template>
	
	<xsl:template match="index.item">
			<xsl:if test="@ID|@id">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:attribute>
			</xsl:if>
			<div>
				<xsl:apply-templates select="node()[not(self::index.item)]" />
			</div>
			<xsl:if test="index.item">
				<ol class="&tocMainClass;">
					<xsl:apply-templates select="index.item"/>
				</ol>
			</xsl:if>
	</xsl:template>
	
	<xsl:template match="index.entry">
		<li class="&tocCellWithoutLeadersClass;">
			<xsl:if test="@ID|@id">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:attribute>
			</xsl:if>
			<div>
				<xsl:apply-templates select="node()[not(self::index.entry)]" />
			</div>
			<xsl:if test="index.entry">
				<ol class="&tocMainClass;">
					<xsl:apply-templates select="index.entry"/>
				</ol>
			</xsl:if>
		</li>
	</xsl:template>

</xsl:stylesheet>