<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="CustomFunctions.xsl"/>
  <xsl:include href="Head.xsl"/>
  <xsl:include href="WrappingUtilities.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsCommentaryEnhancementMode" select="false()"/>
	
	<xsl:template match="pinpoint.anchor" name="pinpointAnchor" priority="1">
		<xsl:if test="@ID">
			<a id="&pinpointIdPrefix;sp_{@ID}">
				<xsl:comment>anchor</xsl:comment>
			</a>
		</xsl:if>
		<xsl:if test="@hashcode">
			<a id="&pinpointIdPrefix;{@hashcode}">
				<xsl:comment>anchor</xsl:comment>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="GetCommentaryDocumentClasses">
		<xsl:param name="prependSpace" select="false()"/>
		<xsl:if test="$prependSpace">
			<xsl:value-of select="' '"/>
		</xsl:if>
		<xsl:value-of select="'&commentaryDocumentClass;'" />
	</xsl:template>

	<xsl:template name="GetCommentaryDocumentEnhancementClasses">
		<xsl:param name="prependSpace" select="false()"/>
		<xsl:if test="$IsCommentaryEnhancementMode">
			<xsl:if test="$prependSpace">
				<xsl:value-of select="' '"/>
			</xsl:if>
			<xsl:value-of select="'&commentaryDocumentEnhancementClass;'"/>
		</xsl:if>
	</xsl:template>
  
  <!--Suppress the prop.block and display the prop.block on the top on the content. -->
  <xsl:template name ="commentary-prop-block">
    <xsl:call-template name="wrapContentBlockWithCobaltClass">
      <xsl:with-param name="contents">
        <xsl:choose>
          <xsl:when test="$IsCommentaryEnhancementMode and prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date]">
            <div class="&publicationLineClass;">
              <!--Renders first publication title-->
              <xsl:apply-templates select="prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date][1]" />
              <span class="&verticalDividerClass;">
                <xsl:text><![CDATA[ | ]]></xsl:text>
              </span>
              <!--Renders publication date-->
              <xsl:apply-templates select="content.metadata.block/cmd.dates | date"/>
            </div>
            <!--Renders other publication titles-->
            <xsl:for-each select="prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date]">
              <xsl:variable name="headPosition" select="position()"/>
              <xsl:if test="$headPosition &gt; 1">
                <xsl:call-template name="head" />
              </xsl:if>
            </xsl:for-each>
            <!--Render author info, prelim text etc.-->
            <xsl:apply-templates select="node()[not(self::prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date][1] or self::date or self::content.metadata.block[cmd.dates])]" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="second-line-cite">
    <div>
      <xsl:call-template name="wrapWithDiv">
        <xsl:with-param name="class" select="'&citesClass;'" />
        <xsl:with-param name="contents">
          <xsl:value-of select="md.second.line.cite"/>
        </xsl:with-param>
      </xsl:call-template>
    </div>
  </xsl:template>


</xsl:stylesheet>

