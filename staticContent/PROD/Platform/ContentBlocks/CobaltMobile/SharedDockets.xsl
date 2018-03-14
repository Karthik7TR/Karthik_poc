<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SharedDockets.xsl" forcePlatform="true"/>
  <xsl:template name="PerformDocketEntriesBlockMatch">
    <xsl:param name="currentNode" />

    <xsl:call-template name="renderCalendaringSection"/>
    <h2 id="&docketsDocketProceedingsId;" class="&docketsHeading;">
      <xsl:text>&docketsDocketProceedings;</xsl:text>
      <xsl:call-template name="DisplayCount">
        <xsl:with-param name="nodes" select="$currentNode/docket.entry"/>
      </xsl:call-template>
    </h2>
    <table class="&docketsTable;">
      <tr>
        <th>
          <xsl:text>&docketsEntry;</xsl:text>
        </th>
        <th>
          <xsl:text>&docketsDate;</xsl:text>
        </th>
        <th>
          <xsl:text>&docketsDescription;</xsl:text>
        </th>
        <xsl:if test="$IsPublicRecords = false()">
          <th></th>
        </xsl:if>
      </tr>
      <xsl:for-each select="$currentNode/docket.entry">
        <tr>
          <xsl:variable name="imageOrLink">
            <xsl:if test="$IsPublicRecords = false()">
              <xsl:variable name="index">
                <xsl:value-of select="position()"/>
              </xsl:variable>
              <xsl:if test="number.block/image.block">
                <xsl:call-template name="RenderDocketImage">
                  <xsl:with-param name="imageBlock" select="number.block/image.block" />
                  <xsl:with-param name="index" select="$index" />
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:variable>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:apply-templates select="number.block/number" />
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:apply-templates select="date" />
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:apply-templates select="docket.description" />
            </xsl:with-param>
          </xsl:call-template>
          <xsl:if test="$IsPublicRecords = false()">
            <xsl:call-template name="DocketsTableCell">
              <xsl:with-param name="text">
                <xsl:copy-of select="$imageOrLink"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="PerformNumberBlockImageBlockMatch">
    <xsl:param name="currentNode" />
    <xsl:if test="$IsPublicRecords = false()">
      <xsl:variable name="imageId" select="$currentNode/image.gateway.link/@image.ID" />
      <xsl:variable name="localImage" >
        <xsl:call-template name="ParseImageGuid">
          <xsl:with-param name="imageGuid" select="substring-before($imageId,';')" />
        </xsl:call-template>
      </xsl:variable>

      <xsl:apply-templates select="$currentNode/image.gateway.link[@item.type='main']">
        <xsl:with-param name="text">
          <i></i>
          <xsl:text>&docketViewPDFText;</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="displayIcon" select="'&pdfIconPath;'" />
        <xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'" />
        <xsl:with-param name="displayIconAltText" select="'&pdfAltText;'" />
        <xsl:with-param name="localImageGuid" select="$localImage"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
