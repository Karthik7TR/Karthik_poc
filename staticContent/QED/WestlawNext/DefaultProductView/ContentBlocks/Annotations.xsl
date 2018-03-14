<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Annotations.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template name="additionalResourcesHeader">
		<h2 class="&relevantAdditionalResourcesTitleClass; &printHeadingClass;">&relevantAdditionalResourcesHeadingText;</h2>
		<p class="&coFontSize12;">&relevantAdditionalResourcesExplaination;</p>
	</xsl:template>
	
	<xsl:template match="annotations">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $StatutoryTextOnly" />
			<xsl:otherwise>
				<!--Call platform template-->
				<xsl:call-template name="ProcessAnnotations" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="annotations/reference.block[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
		<xsl:if test="not(../descendant::hist.note.block[.//N-HIT or .//N-LOCATE or .//N-WITHIN])
				and descendant::node()/*[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
			<xsl:call-template name="additionalResourcesHeader" />
		</xsl:if>
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

</xsl:stylesheet>
