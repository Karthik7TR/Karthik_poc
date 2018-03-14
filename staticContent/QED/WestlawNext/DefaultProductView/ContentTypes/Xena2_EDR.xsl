<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select="cr | so[1] | so1 | hcb | hcb1 | hcb2 | hcb3 | hcb4 | ti | mx | cy.gen"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="*[not(self::cr or self::so[1] or self::so1 or self::hcb or self::hcb1 or self::hcb2 or self::hcb3 or self::hcb4 or self::ti or self::mx or self::cy.gen)] | so[position() > 1]" />
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
    <div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="md.primarycite">
					<xsl:apply-templates select="md.primarycite/md.primarycite.info/md.display.primarycite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
			</xsl:choose>				
		</div>
  </xsl:template>
	
	<xsl:template match="cr" priority="2">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="smp53/d6">
		<xsl:choose>
			<xsl:when test="contains(text(), '&#8195;')">
				<div>&nbsp;</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Overrode these to avoid duplicate DHE -->
	<xsl:template match="ti | ti2 | til | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | snl | srnl | hc2" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="socl" />

</xsl:stylesheet>
