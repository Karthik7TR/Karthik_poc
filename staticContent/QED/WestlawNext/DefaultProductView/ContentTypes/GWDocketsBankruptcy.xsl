<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="DocketsBankruptcy.xsl"/>
  <xsl:include href="SharedGWDockets.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template name="ToCreditor">
    <xsl:variable name="url">
      <xsl:call-template name="createGatewayDocketsCreditorMashupLink" />
    </xsl:variable> 

    <h2 class="&docketsHeading;">
      <xsl:text>&docketsToCreditor;</xsl:text>
    </h2>
    <table>
      <tr class="&docketsRowClass;">
        <td class="&docketsRowLabelClass;">
          <xsl:if test="string-length($url) &gt; 0">
            <a>
              <xsl:attribute name="href">
                <xsl:call-template name="replace">
                  <xsl:with-param name="string" select="$url" />
                  <xsl:with-param name="pattern" select="'/Docket'" />
                  <xsl:with-param name="replacement" select="'/Gateway/Docket'"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="id">&docketsCreditorLinkId;</xsl:attribute>
              <xsl:text>&docketsCreditorLinkText;</xsl:text>
            </a>
          </xsl:if>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="party.phone.block"/>
  
</xsl:stylesheet>
