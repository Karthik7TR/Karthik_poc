<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<!--- Provisions table -->
	<xsl:template match="provisions-table">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="pgroup">
		<!-- First render the title of the group -->
		<div class="&paraMainClass;">
			<xsl:if test="pgroup-number or pgroup-title">
				<xsl:choose>
					<xsl:when test="count(ancestor::pgroup) >= 2">
						<div class="&paraMainClass; &indentLeft1Class;">
							<h4 class="&printHeadingClass;">
								<xsl:apply-templates select="pgroup-number|pgroup-title" />
							</h4>
						</div>
					</xsl:when>
					<xsl:when test="count(ancestor::pgroup) = 1">
						<div class="&paraMainClass;">
							<h3 class="&printHeadingClass;">
								<xsl:apply-templates select="pgroup-number|pgroup-title" />
							</h3>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="&paraMainClass;">
							<h2 class="&printHeadingClass;">
								<xsl:apply-templates select="pgroup-number|pgroup-title" />
							</h2>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:apply-templates select="pgroup|pgroup-entry" />
		</div>
	</xsl:template>

	<xsl:template match="pgroup-number">
		<xsl:choose>
			<xsl:when test="parent::pgroup">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pgroup-title">
		<xsl:if test="preceding-sibling::pgroup-number">
			&#160;
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="pgroup-entry">
		<div class="&paraMainClass; &indentLeft3Class;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>