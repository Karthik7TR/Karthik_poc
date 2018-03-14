<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Analysis.xsl"/>
  <xsl:include href="Cites.xsl"/>
  <xsl:include href="Date.xsl"/>
  <xsl:include href="Footnotes.xsl"/>
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Title.xsl"/>
  <xsl:include href="PreformattedText.xsl"/>
  <xsl:include href="Document.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
    <div id="&documentClass;">
       <xsl:call-template name="headerCitation" />
       <xsl:apply-templates/>       
       <xsl:call-template name="FooterCitation" />
       <xsl:call-template name="EndOfDocument" />
     </div>
  </xsl:template>

  <xsl:template match="court[preceding-sibling::court]">
    <br/>
    <xsl:apply-templates />
  </xsl:template>  

  <xsl:template match="include.copyright" priority="1">
    <xsl:apply-templates />
  </xsl:template> 

  <!-- Suppress the normal "*.cites" elements -->
  <xsl:template match="cmd.cites | md.cites" />

  <xsl:template match="doc.title[last()]" priority="1">
    <xsl:call-template name="titleBlock"/>
    <xsl:comment>&EndOfDocumentHead;</xsl:comment>
  </xsl:template>

  <xsl:template match="prop.head[not(following::doc.title)][last()]" priority="1">
    <xsl:apply-templates />
    <xsl:comment>&EndOfDocumentHead;</xsl:comment>
  </xsl:template>

  <xsl:template match="title.block[not(//doc.title or //prop.head)]" priority="1">
    <xsl:call-template name="titleBlock"/>
    <xsl:comment>&EndOfDocumentHead;</xsl:comment>
  </xsl:template>

  <xsl:template name="FooterCitation">
    <xsl:variable name="citation">
      <xsl:choose>
        <xsl:when test="/Document/document-data/collection = 'w_3rd_phafj'">
          <xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.display.primarycite"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="string-length($citation) &gt; 0">
      <div class="&citationClass;">
        <xsl:value-of	select="$citation"	/>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="headerCitation">
    <xsl:variable name="firstcitation">
      <xsl:choose>
        <xsl:when test="/Document/document-data/collection = 'w_3rd_phafj'">
          <xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.first.line.cite"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="string-length($firstcitation) &gt; 0">
      <div class="&citationClass;">
        <xsl:value-of	select="$firstcitation"	/>
      </div>
    </xsl:if>

    <xsl:variable name="secondcitation">
      <xsl:choose>
        <xsl:when test="/Document/document-data/collection = 'w_3rd_phafj'">
          <xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.second.line.cite"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="string-length($secondcitation) &gt; 0">
      <div class="&citationClass;">
        <xsl:value-of	select="$secondcitation"	/>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
