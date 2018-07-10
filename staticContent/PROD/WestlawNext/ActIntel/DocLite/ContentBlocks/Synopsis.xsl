<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!-- I18n Completed As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="synopsis[.//synopsis.background and not(ancestor::summary)]" priority="1"/>
  <xsl:template match="synopsis.holding//headnote.reference"/>
</xsl:stylesheet>