<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Footnotes.xsl" />
  <xsl:include href="PreformattedText.xsl"/>
  <xsl:include href="GlobalParams.xsl"/>
  
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:variable name="textSpace" select="Document/n-docbody/filing/filing.body/text/@xml:space"/>
  <xsl:variable name="isPreFormattedText">
    <xsl:choose>
      <xsl:when test="$textSpace='preserve' or Document/n-docbody//preformatted">
        <xsl:value-of select="true()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="preformatDeliveryStyles">
    <xsl:text>font-family: 'Courier New', monospace; font-size: 7pt;</xsl:text>
  </xsl:variable>

	<xsl:template match="Document">
		<div id="&documentId;">
      <xsl:choose>
        <xsl:when test="$isPreFormattedText=string(true())">
          <xsl:if test="$DeliveryMode=string(true())">
            <xsl:attribute name="style">
              <xsl:value-of select="$preformatDeliveryStyles"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:call-template name="AddDocumentClasses">
            <xsl:with-param name="contentType" select="'&contentTypeSECAdminLetters; &preformattedDocument;'"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="AddDocumentClasses">
            <xsl:with-param name="contentType" select="'&contentTypeSECAdminLetters;'"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="n-metadata/metadata.block/md.references" />
      <!--<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />-->
      <xsl:comment>&EndOfDocumentHead;</xsl:comment>
      <xsl:choose>
        <xsl:when test="$isPreFormattedText=string(false())">
          <xsl:apply-templates select="n-docbody/filing/filing.body/text/node()"/>
        </xsl:when>
        <xsl:otherwise>
          <pre class="&layoutPreSpacedContent;">
            <xsl:apply-templates select="n-docbody/filing/filing.body/text/node()"/>
          </pre>
        </xsl:otherwise>
      </xsl:choose>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

  <xsl:template match="display.name">
    <div class="&simpleContentBlockClass; &preformattedTextClass;">
      <xsl:call-template name="PreformattedTextCleaner" />
    </div>
    <br/>
  </xsl:template>

  <xsl:template match="text()" priority="1">
    <!-- if the text contains just spaces, do not display it -->
    <xsl:if test="string-length(translate(., ' ', '')) > 0">
      <span>
        <xsl:value-of select="."/>
      </span>
    </xsl:if>
  </xsl:template>
  
  <!--Task to remove the Global Securities message - task 374287-->
  <xsl:template match="message.block"/>

  <xsl:template match="gsi.text.head "/>

  <xsl:template match="gsi.prelim.head">
    <xsl:call-template name="wrapContentBlockWithCobaltClass">
      <xsl:with-param name="contents" select="'&fdTitleText;'" />
    </xsl:call-template>
    <!--<xsl:comment>&EndOfDocumentHead;</xsl:comment>-->
  </xsl:template>

	<xsl:template match=" gsi.company.block | gsi.type.block | gsi.accession.block | gsi.attorney.block | gsi.text.block | gsi.line.space | gsi.source">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="gsi.company.block/node() | gsi.type.block/node() | gsi.accession.block/node() | gsi.attorney.block/node() | gsi.text.block/node()[not(self::gsi.text.head | self::gsi.line.space)]">
    <!-- Fix to add a line break for an empty gsi.text.block -->
    <xsl:if test="child::node()[2]='&#x2001;'">
      <br/>
    </xsl:if>
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

  <!-- Fix for adding a line break before and after the accountant block -->
  <xsl:template match="gsi.accountant.block">
    <br/>
    <xsl:apply-templates/>
    <br/>
  </xsl:template>

</xsl:stylesheet>
