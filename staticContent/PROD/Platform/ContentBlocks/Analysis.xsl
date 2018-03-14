<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="analysis" name="analysis">
		<xsl:call-template name="Toc">
			<xsl:with-param name="rootClass" select="'&analysisClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="analysis.entry">
		<xsl:choose>
			<xsl:when test="(ancestor::analysis.division or (parent::analysis and (preceding-sibling::analysis.division or following-sibling::analysis.division))) or (ancestor::division or (parent::analysis and (preceding-sibling::division or following-sibling::division)))">
				<li class="&tocHeading;">
          <div>
            <xsl:if test="@id or @ID">
              <xsl:attribute name="id">
                <xsl:value-of select="concat('&internalLinkIdPrefix;',string(@id | @ID))" />
              </xsl:attribute>
            </xsl:if>            
            <xsl:choose>
              <xsl:when test="internal.reference">
                <xsl:apply-templates select="internal.reference" />
              </xsl:when>
              <xsl:when test="head | text() | bold | b | ital | N-HIT">
                <xsl:call-template name="internalReference">
                  <xsl:with-param name="contents">
                    <xsl:apply-templates select="head | text() | bold | b | ital | N-HIT"/>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="node()[not(self::analysis.entry)]"/>
              </xsl:otherwise>
            </xsl:choose>
          </div>
					<xsl:if test="analysis.entry">
						<ol class="&tocMainClass;">
							<xsl:apply-templates select="analysis.entry"/>
						</ol>
					</xsl:if>
				</li>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="TocEntry"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="analysis.division">
		<li class="&tocHeading;">
			<xsl:call-template name="Toc">
				<xsl:with-param name="rootClass" select="'&analysisClassDivision;'"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="analysis.entry[@refid]/head[headtext]" priority="2">
		<xsl:apply-templates select="node()[not(self::label.name) and not(self::label.designator)]"/>
	</xsl:template>

	<xsl:template match="analysis.entry[@refid]/head[not(headtext)]" priority="2">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="analysis.entry[@refid]/head/headtext" priority="2">
		<xsl:if test="preceding-sibling::label.name">
			<xsl:apply-templates select="preceding-sibling::label.name"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::label.designator">
			<xsl:apply-templates select="preceding-sibling::label.designator"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="analysis.ref">
		<xsl:call-template name="internalReference"/>
	</xsl:template>

	<xsl:template match="analysis.ref[internal.reference]">
		<xsl:apply-templates mode="analysisRef" />
	</xsl:template>

	<xsl:template match="analysis.ref/node()" mode="analysisRef">
		<xsl:choose>
			<xsl:when test="self::internal.reference">
				<xsl:apply-templates select="." />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="internalReference">
					<xsl:with-param name="refid" select="../@refid" />
					<xsl:with-param name="contents">
						<xsl:apply-templates select="." />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>