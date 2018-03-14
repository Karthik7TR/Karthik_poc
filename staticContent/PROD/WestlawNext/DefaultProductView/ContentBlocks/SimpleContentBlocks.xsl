<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- xsl:template name="additionalResourcesHeader">
		<h2 class="&relevantAdditionalResourcesTitleClass; &printHeadingClass;">&relevantAdditionalResourcesHeadingText;</h2>
		<p class="&coFontSize12;">&relevantAdditionalResourcesExplaination;</p>
	</xsl:template -->

	<!-- xsl:template match="annotations/reference.block[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
		<xsl:if test="not(../descendant::hist.note.block[.//N-HIT or .//N-LOCATE or .//N-WITHIN])
				and descendant::node()/*[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
			<xsl:call-template name="additionalResourcesHeader" />
		</xsl:if>
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template -->

	<!-- procedural posture block -->
	<xsl:template match="procedural.posture.block">
		<xsl:if test="$IAC-INDIGO-PPT and $IndigoDisplay and $ProceduralPostureFilter">
			<xsl:call-template name="wrapContentBlockWithCobaltClass" >
				<xsl:with-param name="contents">
					<strong>
						<xsl:text>&postureTitle;</xsl:text>
					</strong>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:call-template name="proceduralPosture" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="proceduralPosture">
		<xsl:for-each select="procedural.posture/procedural.posture.item">
			<xsl:if test="$IAC-INDIGO-PPT and $IndigoDisplay and $ProceduralPostureFilter">
				<xsl:if test="@level!='1' or not(../procedural.posture.item[@level='2'])">
					<xsl:apply-templates />
					<xsl:if test="position() != last()">&postureDelimiter;</xsl:if>
					<xsl:if test="position() = last()">&postureEndSymbol;</xsl:if>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="remarks.block">
		<xsl:variable name="wrapperId">
			<xsl:choose>
				<!--This will add id for remarks.block if not provided-->
				<xsl:when test="./ancestor::*[local-name() = 'trial.court.order'] and local-name() = 'remarks.block' and head and not(@id)">
					<xsl:value-of select="'co_anchor_custom_toc_Id'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id" select="$wrapperId" />
		</xsl:call-template>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

</xsl:stylesheet>
