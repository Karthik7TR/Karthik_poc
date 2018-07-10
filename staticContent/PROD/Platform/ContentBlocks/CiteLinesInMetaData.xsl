<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="md.cites">
		<xsl:variable name="displayableCites" select="md.first.line.cite[text()] | md.second.line.cite[text()]" />
		<xsl:if test="string-length($displayableCites) &gt; 0">
			<div class="&citesClass;">
				<xsl:for-each select="$displayableCites">
					<xsl:if test="string-length() &gt; 0">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="cmd.cites" />

</xsl:stylesheet>