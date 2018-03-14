<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="prelim.head/head/head.info/headtext" priority="1">
		<xsl:param name="divId"/>
		<xsl:if test=".//text() or .//textrule">
			<xsl:choose>
				<xsl:when test="ancestor::Document/toc-data">
					<!-- Special processing used for delivery to map toc urls to prelim nodes -->
					<xsl:variable name="ancestorPrelimHeadCount" select="count(ancestor::prelim.head)" />
					<xsl:variable name="matchingTocAnchor" select="ancestor::Document/toc-data//ul[count(ancestor-or-self::ul) = $ancestorPrelimHeadCount]/li/a" />
					<xsl:choose>
						<xsl:when test="$matchingTocAnchor">
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="$matchingTocAnchor/@href"/>
								</xsl:attribute>
								<xsl:call-template name="renderStuffBeforeLink" />
							</a>
							<xsl:call-template name="renderStuffAfterLink" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="headtextApplyTemplatesLogic" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="headtextApplyTemplatesLogic" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="headtextApplyTemplatesLogic">
		<xsl:if test="preceding-sibling::label.name">
			<xsl:apply-templates select="preceding-sibling::label.name"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::label.designator">
			<xsl:apply-templates select="preceding-sibling::label.designator"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="renderStuffBeforeLink">
		<xsl:if test="preceding-sibling::label.name">
			<xsl:apply-templates select="preceding-sibling::label.name"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::label.designator">
			<xsl:apply-templates select="preceding-sibling::label.designator"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates select="node()[not(self::hide.historical.version |self::internal.reference|self::cite.query[@w-ref-type = 'CM'])]" />
	</xsl:template>

	<xsl:template name="renderStuffAfterLink">
		<xsl:apply-templates select="hide.historical.version |internal.reference|cite.query[@w-ref-type = 'CM']" />
	</xsl:template>

</xsl:stylesheet>
