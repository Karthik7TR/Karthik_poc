<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Date.Line -->
	<xsl:template match="date.line | docket.line">
		<xsl:if test="$DeliveryMode and (preceding-sibling::date.line or preceding-sibling::docket.line or (self::date.line and parent::date.block[preceding-sibling::docket.block])) ">
				<span class="&excludeFromAnnotationsClass;"><![CDATA[ ]]>|<![CDATA[ ]]></span>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="child::justified.line">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithSpan"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="date.line/justified.line | docket.line/justified.line">
		<xsl:call-template name="wrapWithSpan"/>
	</xsl:template>

</xsl:stylesheet>
