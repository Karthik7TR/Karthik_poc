<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:html="http://www.w3.org/1999/xhtml">

  <xsl:output method="xml" indent="no" encoding="utf-8" omit-xml-declaration="yes" />

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="html:*[contains(@class,'co_briefItState') and not(contains(@class,'co_synopsis')) and not(contains(@class,'co_synopsisHolding')) and not(contains(@class,'co_courtBlock')) and not(contains(@class,'co_docketBlock'))]"></xsl:template>

</xsl:stylesheet>
