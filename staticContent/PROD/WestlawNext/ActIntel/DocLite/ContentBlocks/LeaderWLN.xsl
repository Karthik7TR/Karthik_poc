<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="LeaderWLN.xsl" forceDefaultProduct="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overriding to suppress leaderContent -->
	<xsl:template match="signature.line/signature">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="signature.line//leader">
		<xsl:apply-templates />
		<xsl:text>&#160;</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
