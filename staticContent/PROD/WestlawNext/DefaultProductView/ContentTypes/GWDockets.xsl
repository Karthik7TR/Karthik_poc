<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Dockets.xsl"/>
  <xsl:include href="SharedGWDockets.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template name="renderDocumentTopBlock">
    <xsl:call-template name="renderDocumentTopBlockWithDisclaimer"/>
  </xsl:template>
  
  <xsl:template match="calendar.block">
    <h2 id="&docketsCalendarInformationId;" class="&docketsHeading;">
      <xsl:text>&docketsCalendarInformation;</xsl:text>
      <xsl:call-template name="DisplayCount">
        <xsl:with-param name="nodes" select="calendar.entry"/>
      </xsl:call-template>
    </h2>
    <table class="&docketsTable;">
      <tr>
        <th>
          <xsl:text>&docketsDateTime;</xsl:text>
        </th>
        <th>
          <xsl:text>&docketsDescription;</xsl:text>
        </th>
        <th>
          <xsl:text>&docketsLocation;</xsl:text>
        </th>
        <th>
          <xsl:text>&docketsJudge;</xsl:text>
        </th>
      </tr>
      <xsl:for-each select="calendar.entry">
        <tr>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:if test="event/date">
                <div>
                  <xsl:apply-templates select="event/date" />
                </div>
              </xsl:if>
              <xsl:if test="event/time">
                <div>
                  <xsl:apply-templates select="event/time" />
                </div>
              </xsl:if>
              <xsl:if test="event/continued.block">
                <div>
                  <xsl:apply-templates select="event/continued.block" />
                </div>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:apply-templates select="calendar.description" />
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:for-each select="location.info.block/node()">
                <xsl:apply-templates select="." />
                <xsl:if test="position() != last()">
                  <xsl:text><![CDATA[ ]]></xsl:text>
                </xsl:if>
              </xsl:for-each>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="DocketsTableCell">
            <xsl:with-param name="text">
              <xsl:apply-templates select="judge" />
            </xsl:with-param>
          </xsl:call-template>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
  
  <xsl:template match="event/continued.block">
    <xsl:call-template name="DocketsTableData">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates select="continued" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="arrest.disposition.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates select="arrest.disposition" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  

    <xsl:template match="matched.block" name="generalMatchedBlock" mode="render">
			<xsl:apply-templates select="matched.party.block"/>
    </xsl:template>

    <xsl:template match="matched.party.block">
			<xsl:variable name="current" select="."/>
			<div class="&docketsSubSection;">
				<xsl:apply-templates select="matched.party.name.block | matched.plaintiff.party.block/matched.party.name.block | matched.defendant.party.block/matched.party.name.block" mode="matchedPartyNameBlock"/>
				<table>
					<xsl:apply-templates/>
					<xsl:apply-templates select="following-sibling::*[not(self::matched.party.block)][preceding-sibling::matched.party.block[1] = $current]"/>
				</table>
			</div>
			<xsl:processing-instruction name="chunkMarker"/>
    </xsl:template>
	
		<xsl:template match="matched.email.block" >
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="matched.email.address" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:template>

	<xsl:template match="assignment.date.block">
		<xsl:if test="assignment.date">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="assignment.date" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

  <xsl:template match="judgment.party.block">
    <xsl:call-template name="DocketsTableData">
      <xsl:with-param name="label" select="judgment.party.type.block/label" />
      <xsl:with-param name="text">
        <xsl:apply-templates select="judgment.party.type.block/judgment.party.type" />
        <xsl:text>  </xsl:text>
        <xsl:apply-templates select="judgment.party.name" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
