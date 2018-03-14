<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Annotations.xsl"/>
	<xsl:include href="HistoryNotes.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="annotations/hist.note.block">
		<xsl:if test="hist.note.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
			<xsl:call-template name="additionalResourcesHeader" />
		</xsl:if>
		<div class="&historyNotesClass; &disableHighlightFeaturesClass;" id="&historyNotesId;">
			<xsl:apply-templates select="head/head.info/headtext"/>
			<xsl:apply-templates select="hist.note.body" />
		</div>
	</xsl:template>

</xsl:stylesheet>
