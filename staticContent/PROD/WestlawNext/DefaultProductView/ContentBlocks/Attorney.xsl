<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Universal.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="attorney.block" priority="1">
    <xsl:if test="not(preceding-sibling::attorney.block)">
      <h2 class="&attorneyBlockLabelClass; &printHeadingClass; &briefItStateClass;" id="&attorneysAndLawFirmsId;">
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&attorneyBlockLabelKey;', '&attorneyBlockLabel;')"/>
      </h2>
    </xsl:if>
    <xsl:call-template name="wrapContentBlockWithCobaltClass" />
    <xsl:processing-instruction name="chunkMarker"/>
  </xsl:template>

</xsl:stylesheet>
