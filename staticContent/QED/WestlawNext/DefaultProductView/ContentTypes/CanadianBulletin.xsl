<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="CanadianUniversal.xsl"/>
  <xsl:include href="CanadianOutline.xsl"/>
  <xsl:include href="CanadianFootnotes.xsl"/>
  <xsl:include href="CanadianCites.xsl"/>
  <xsl:include href="CanadianDate.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
    <div id="&documentClass;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&crswRegulatoryMaterialsClass;'"/>
      </xsl:call-template>
      
      <xsl:call-template name="StarPageMetadata" />
      <div class="&citesClass;">
        <xsl:if test="//n-docbody/comment">
          <xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites"/>
        </xsl:if>
        <xsl:apply-templates select="//n-docbody//doc_heading" />
      </div>

      <xsl:comment>&EndOfDocumentHead;</xsl:comment>


      <xsl:choose>
        <!--Render Bulletin-->
        <xsl:when test="//n-docbody/bulletin">
          <xsl:apply-templates select="//n-docbody/bulletin/node()[not(self::doc_heading | self::p/sup/a[starts-with(@href, 'r')])]"/>
        </xsl:when>
        <!--Render Comment-->
        <xsl:when test="//n-docbody/comment">
          <xsl:apply-templates select="//n-docbody/comment/node()[not(self::doc_heading | self::p/sup/a[starts-with(@href, 'r')])]" />
        </xsl:when>
      </xsl:choose>

      <xsl:call-template name="RenderFootnoteSection"/>
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <xsl:template  match="message.block.carswell/include.copyright  | content.metadata.block"/>

  <xsl:template match="prelims">
    <div>
      <xsl:attribute name="class">
        <xsl:text>&titleClass;</xsl:text>
      </xsl:attribute>
      <xsl:for-each select="*">
        <xsl:call-template name="wrapWithDiv">
          <xsl:with-param name="class" select="&titleLineClass;"/>
        </xsl:call-template>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template match="toc_headings">
    <div>
      <xsl:attribute name="class">
        <xsl:text>&titleLineClass;</xsl:text>
      </xsl:attribute>
      <xsl:for-each select="*">
        <xsl:call-template name="wrapWithDiv">
          <xsl:with-param name="class" select="&titleLineClass;"/>
        </xsl:call-template>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template match="doc_title">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass;'" />
    </xsl:call-template>
  </xsl:template>


  <xsl:template match="bulletin//a[@name]">
    <xsl:if test="not(@href)">
      <a>
        <xsl:attribute name="id">
          <xsl:value-of select="translate(@name, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
        </xsl:attribute>
      </a>
    </xsl:if>
  </xsl:template>

  <xsl:template match="bulletin//a[@href]">
    <xsl:variable name="reference" select="@href"/>
    <xsl:choose>
      <xsl:when test="/Document/n-docbody/bulletin//a[@name = $reference]">
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="concat('#', translate($reference, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
          </xsl:attribute>
          <xsl:attribute name="class">
            <xsl:text>&linkUnderlineClass;</xsl:text>
          </xsl:attribute>
          <xsl:apply-templates/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="@href"/>
          </xsl:attribute>
          <xsl:attribute name="target">
            <xsl:text>_blank</xsl:text>
          </xsl:attribute>
          <xsl:apply-templates/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Added this template to remove $ character from the ID otherwise document fails the HTML validation -->
  <xsl:template match="tbl" priority="1">
    <xsl:if test=".//text()">
      <div>
        <xsl:if test="@id or @ID">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('&internalLinkIdPrefix;', translate(@id | @ID, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:apply-templates />
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
