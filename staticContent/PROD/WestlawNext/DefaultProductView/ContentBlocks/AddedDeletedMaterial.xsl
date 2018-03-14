<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="added.material | centa" priority="1">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:variable name="DisplayRedlineMarkup">
				<xsl:call-template name="DetermineRedlineMarkup"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$DisplayRedlineMarkup = 'true'">
					<ins class="&ruleBookRedlineClass;">
						<xsl:copy-of select="$contents"/>
					</ins>
				</xsl:when>
				<xsl:otherwise>
					<ins style="background-color:white" class="&ruleBookRedlineClass;">
						<xsl:copy-of select="$contents"/>
					</ins>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="deleted.material | centd" priority="1">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:variable name="DisplayRedlineMarkup">
				<xsl:call-template name="DetermineRedlineMarkup"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$DisplayRedlineMarkup = 'true'">
					<del class="&ruleBookRedlineClass;">
						<xsl:copy-of select="$contents"/>
					</del>
				</xsl:when>
				<xsl:otherwise>
					<del class="&hideState; &ruleBookRedlineClass;">
						<xsl:copy-of select="$contents"/>
					</del>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="DetermineRedlineMarkup">
		<xsl:choose>
			<!-- If redlining is displayed also show in delivered document. -->
			<!-- RedLineToggle is WestlawNext specific - It is true if the toolbar widget is active ($('#co_redlineToggleContainer').hasClass('co_redlineIsActive')). -->
			<!-- The toggle will only be displayed if the feature access ADCVERREDLININGTOGGLE=true -->
			<!-- IsRuleBookMode and redline are specific to Practice Point. -->
			<xsl:when test="$DeliveryMode and $AdcVersionRedliningToggle and (($IsRuleBookMode and $redline) or $RedLineToggle)">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<!-- For delivery, if the document is a reg change or prior version display redlining. -->
			<!-- This case is for delivery from a list (the toolbar widget does not exist). Bug #797392 -->
			<xsl:when test="$DeliveryMode and $AdcVersionRedliningToggle and (/Document/n-metadata[n-view = 'CHG'] or (/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.endeffective))">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:when test="$StatutesCompareRedlining">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>