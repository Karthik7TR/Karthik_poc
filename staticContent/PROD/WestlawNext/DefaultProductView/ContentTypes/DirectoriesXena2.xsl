<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Xena2Shared.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
    <div id="&documentClass;">
      <xsl:call-template name="AddDocumentClasses"/>
      <xsl:call-template name="headerCitation"/>
      <xsl:call-template name="headerDocument"/>
      <xsl:comment>&EndOfDocumentHead;</xsl:comment>
      <xsl:call-template name="StarPageMetadata" />
      <xsl:apply-templates select="n-docbody"/>
      <xsl:call-template name="FooterCitation" />
      <xsl:call-template name="EndOfDocument" />      
    </div>
  </xsl:template> 

  <xsl:template match="doc">    
      <xsl:apply-templates select="*[not(self::hg0 or self::hg1 or self::hg2 or self::hg3 or self::hg4 or self::dl or self::ti or self::ti2 or self::dj or self::dj2 or self::cr or self::gh1 or self::gh2 or self::gh3 or self::gh4 or self::gh5 or self::hcb)]" />      
  </xsl:template>  

  <xsl:template match="centv | so | so1 | typ"/>

  <xsl:template name="headerCitation">
    <xsl:if test="/Document/document-data/collection = 'w_3rd_abadir'">
      <xsl:variable name="firstcitation" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info" />
      <xsl:variable name="secondcitation" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info"/>
      <xsl:variable name="copyright" select="/Document/n-docbody//so1"/>
      <div class="&citationClass;">
        <xsl:if test="string-length($firstcitation) &gt; 0">
          <div>
            <xsl:value-of	select="$firstcitation"	/>
          </div>
        </xsl:if>
        <xsl:if test="string-length($secondcitation) &gt; 0">
          <div>
            <xsl:value-of	select="$secondcitation"	/>
          </div>
        </xsl:if>
        <xsl:if test="string-length($copyright) &gt; 0">
          <div>
            <xsl:value-of	select="$copyright"	/>
          </div>
        </xsl:if>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="headerDocument">    
    <xsl:if test="/Document/document-data/collection = 'w_3rd_abadir'">
      <xsl:variable name="el1" select="/Document/n-docbody//so/d4" />
      <xsl:variable name="el2" select="/Document/n-docbody//d5"/>
      <xsl:variable name="el3" select="/Document/n-docbody//ticl/d1"/>
      
        <div class="&centerClass;">
          <xsl:if test="string-length($el1) &gt; 0">
            <div>
              <xsl:value-of	select="$el1"	/>
            </div>
          </xsl:if>
          <xsl:if test="string-length($el2) &gt; 0">
            <div>
              <xsl:value-of	select="$el2"	/>
            </div>
          </xsl:if>
          <xsl:if test="string-length($el3) &gt; 0">
            <div>
              <xsl:value-of	select="$el3"	/>
            </div>
          </xsl:if>
        </div>      
    </xsl:if>    
  </xsl:template>  

  <xsl:template name="FooterCitation">
    <xsl:variable name="citation">
      <xsl:choose>
        <xsl:when test="/Document/document-data/collection = 'w_3rd_abadir'">
          <xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="string-length($citation) &gt; 0">
      <div class="&citationClass;">
        <xsl:value-of	select="$citation"	/>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>