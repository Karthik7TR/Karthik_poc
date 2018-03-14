<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Prelim.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Prelim.head-->
	<xsl:template match="prelim.head" priority="2">
		<xsl:choose>
			<xsl:when test="not(prelim.head)">
				<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
					<xsl:with-param name="id" select="'&prelimGoldenLeafClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Overriding to suppress Currentness link text -->
	<xsl:template name="renderStuffAfterLink" />

</xsl:stylesheet>

