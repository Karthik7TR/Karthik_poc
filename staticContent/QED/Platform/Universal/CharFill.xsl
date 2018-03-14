<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="NPrivateChar.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!-- CharFill -->
  <xsl:template match="charfill">
    <xsl:call-template name="repeat">
			<xsl:with-param name="contents">
				<xsl:apply-templates />
			</xsl:with-param>
      <xsl:with-param name="repetitions" select="@numchar" />
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
