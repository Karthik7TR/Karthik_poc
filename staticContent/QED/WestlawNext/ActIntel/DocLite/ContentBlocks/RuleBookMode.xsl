<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="RuleBookMode.xsl" forceDefaultProduct="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overriding to suppress RuleBookMode logic -->
	<xsl:template match="node()[prelim.block or content.metadata.block or doc.title][1]" priority="1" name="rulebookHeaderRenderer">
		<xsl:call-template name="renderCodeStatuteHeader"/>
	</xsl:template>

</xsl:stylesheet>
