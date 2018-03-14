<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="CanadianUniversal.xsl"/>
  <xsl:include href="CanadianCites.xsl"/>
  <xsl:include href="CanadianLinkedToc.xsl"/>  
  <xsl:include href="CanadianFootnotes.xsl"/> 	
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!-- Do not render -->
  <xsl:template match="message.block.carswell" />

  <xsl:template match="Document">
    <div id="&documentClass;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&crswLegislation;'"/>
      </xsl:call-template>
    
      <xsl:apply-templates select="n-metadata/metadata.block/md.references/md.toggle.links/md.toggle.link"/>
      <xsl:call-template name="StarPageMetadata" />

      <div class="&documentHeadClass;">
        <xsl:if test="not($PreviewMode)">
          <xsl:apply-templates select="n-docbody/legis/doc_heading/toc_headings"/>
        </xsl:if>        
        <div class="&headnotesClass; &centerClass;">				
          <xsl:apply-templates select="n-docbody/legis/doc_heading/doc_citation | n-docbody/legstub/stub_heading/stub_citation"/>
          <xsl:apply-templates select="n-docbody/legstub/stub_heading/stub_head"/>
          <!-- For Legislation Stub documents only -->

          <xsl:choose>
            <xsl:when test="descendant::formti">
              <xsl:apply-templates select="(n-docbody//formti)[1]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="n-docbody/legis/doc_heading/doc_title"/>
            </xsl:otherwise>
          </xsl:choose>

          <xsl:apply-templates select="n-docbody/legis/doc_heading/include.currency.block" mode="currencyLink"/>
          <xsl:apply-templates select="n-docbody/legis/doc_heading/img/image.block"/>
        </div>
      </div>
      <xsl:comment>&EndOfDocumentHead;</xsl:comment>

      <xsl:apply-templates select="n-docbody/legis/node()[not(self::message.block.carswell or self::legisrm or self::ul[preceding-sibling::p//sup/a[starts-with(@name, 'f')]]) and not(descendant::formti) or self::proposed] | n-docbody/legstub/node()[not(self::legisrm or self::message.block.carswell)]"/>

      <xsl:apply-templates select="n-docbody/legis/doc_heading/include.currency.block"/>

      <xsl:apply-templates select="n-docbody/legis/legisrm"/>

      <xsl:call-template name="RenderFootnoteSection"/>
      
			<xsl:call-template name="EndOfDocument"/>
				
    </div>
  </xsl:template>

  <xsl:template match="cmd.mostrecent.case">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswMostRecentlyConsidered;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="doc_title | formti" priority="1">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass;'"/>			
    </xsl:call-template>
  </xsl:template>

  <!-- For Legislation Stub documents only -->
  <xsl:template match="stub_heading/stub_head">
    <xsl:for-each select="*">
      <xsl:call-template name="wrapWithDiv"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="include.currency.block" mode="currencyLink">
    <xsl:if test="/Document/document-data/versioned = 'False' or
								 (/Document/document-data/versioned = 'True' and 
									/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.starteffective and 
									/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)">
      <xsl:variable name="id" select="include.currency/@n-include_guid"/>
      <div class="&currentnessClass; &paraMainClass;">
        <a>
          <xsl:attribute name="class">
            <xsl:text>&internalLinkClass;</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:copy-of select="concat(concat('#','&internalLinkIdPrefix;'),include.currency/@n-include_guid)"/>
          </xsl:attribute>
          <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswCurrencyKey;', '&crswCurrency;')"/>					
        </a>
      </div>			
    </xsl:if>
  </xsl:template>

  <xsl:template match="include.currency.block">
    <xsl:if test="/Document/document-data/versioned = 'False' or
								 (/Document/document-data/versioned = 'True' and 
									/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.starteffective and 
									/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)">
      <xsl:variable name="content">
        <strong><xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswCurrencyKey;', '&crswCurrency;')"/></strong>
        <br/>
        <xsl:apply-templates />
      </xsl:variable>
      <xsl:call-template name="wrapContentBlockWithCobaltClass">
        <xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',include.currency/@n-include_guid)"/>
        <xsl:with-param name="contents" select="$content"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section" priority="1">
    <xsl:apply-templates />
    <xsl:choose>
      <xsl:when test="../following-sibling::b[1][title]">
        <xsl:text><![CDATA[   ]]></xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <br/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="subsec" priority="1">
    <xsl:apply-templates />
    <xsl:text><![CDATA[   ]]></xsl:text>
  </xsl:template>

  <xsl:template match="n-docbody/legis/doc_heading" priority="1"></xsl:template>
  <xsl:template match="n-docbody/legstub/stub_heading" priority="1"></xsl:template>
  <xsl:template match="n-docbody/legis/content.metadata.block" priority="1"></xsl:template>
  <xsl:template match="n-docbody/legstub/content.metadata.block" priority="1"></xsl:template>

  <xsl:template match="n-docbody/legis/proposed">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswLegislation; &proposedBlock; &paraMainClass;'"/>			
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="title">
    <xsl:apply-templates />
    <br/>
  </xsl:template>

  <xsl:template match="br">
    <br/>
  </xsl:template>

  <xsl:template match="n-docbody/legis//p[@align='right']" priority="1">
    <xsl:choose>
      <xsl:when test="(local-name(parent::*) = 'legis') and not(following-sibling::p) and statref">
        <!-- Display Amendment History -->
        <div class="&paraMainClass;">
          <strong>
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswAmendmentHistoryKey;', '&crswAmendmentHistory;')"/>
          </strong>
          <br/>
          <xsl:for-each select="statref">
            <xsl:apply-templates />
          </xsl:for-each>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="wrapWithDiv">
          <xsl:with-param name="class" select="'&alignHorizontalRightClass;'"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>  

  <!--Ignore paragraphs with footnote links-->
  <xsl:template match="p[sup/a[starts-with(@name, 'f')]]" priority="1"/>

</xsl:stylesheet>
