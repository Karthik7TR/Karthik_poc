<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="isNasdman" select="contains('|w_3rd_nasdman|', concat('|', /Document/document-data/collection, '|'))"/>
	
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="doc">
		<xsl:apply-templates select="/Document/n-docbody/doc/rcorig/d6[$isNasdman]"/>
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select="hg0 | hg1 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9"/>
		</div>
		<xsl:apply-templates select="*[not(self::hg0 or self::hg1 or self::hg2 or self::hg3 or self::hg4 or self::hg5 or self::hg6 or self::hg7 or self::hg8 or self::hg9 or self::hg10 or self::hg11 or self::hg12 or self::hg13 or self::hg14 or self::hg15 or self::hg16 or self::hg17 or self::hg18 or self::hg19 or self::cpmxa or self::mxda or self::rcorig/d6[$isNasdman])]" />
		<xsl:apply-templates select="mxda | cpmxa" />
	</xsl:template>
	
	<xsl:template match="md.cites" priority="2">
		<div class="&citesClass;">
			<div>
				<xsl:choose>
					<xsl:when test="md.second.line.cite">
						<xsl:apply-templates select="md.second.line.cite"/>
					</xsl:when>
					<xsl:when test="md.third.line.cite">
						<xsl:apply-templates select="md.third.line.cite"/>
					</xsl:when>
					<xsl:when test="md.first.line.cite">
						<xsl:apply-templates select="md.first.line.cite"/>
					</xsl:when>
					<!-- Documents in w_3rd_taxma and w_3rd_taxmi have no md.*.line.cite (as above) but do have md.primarycite (as below). -->
					<xsl:when test="md.primarycite">
						<xsl:apply-templates select="md.primarycite"/>
						<div>&nbsp;</div>
					</xsl:when>
				</xsl:choose>				
			</div>
			<div>
				<xsl:apply-templates select="/Document/n-docbody/doc/ce[1]//d7/text()"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="ce" />

	<xsl:template match="tblbg">
		<xsl:if test="following-sibling::tblbb">
			<table>
				<xsl:apply-templates select="following-sibling::tblbb[following-sibling::tbled[preceding-sibling::tblbb]]" mode="tableCell" />
			<tr>
				<td>&nbsp;
				</td>
			</tr>
		</table>
		</xsl:if>
	</xsl:template>
		
	<xsl:template match="tblbb//d6" mode="tableCell">
		<xsl:variable name="rowContent">
			<xsl:value-of select="normalize-space(text())"/>
		</xsl:variable>
		<xsl:if test="string-length($rowContent) &gt; 0">
			<tr>
				<td>
					<xsl:value-of select="$rowContent"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="tblbb | tbled"/>

</xsl:stylesheet>
