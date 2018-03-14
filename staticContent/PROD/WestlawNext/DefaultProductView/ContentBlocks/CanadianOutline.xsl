<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 4/18/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianGlobalParams.xsl"/>
	
	<xsl:output method="text" indent="no" omit-xml-declaration="yes"/>

  <xsl:template name="CreateGoToLinks">
    <xsl:variable name="content">
      <xsl:choose>
        <xsl:when test="/Document/n-docbody/decision">
          <xsl:call-template name="caselawOutline"/>
        </xsl:when>
        <xsl:when test="/Document/n-docbody/index">
          <xsl:call-template name="indexOutline"/>
        </xsl:when>
      </xsl:choose>
     </xsl:variable>
    
    <xsl:if test="string-length($content) &gt; 0">
      <div id="&nrsOutlineId;" class="&hideStateClass; &excludeFromAnnotationsClass;">
        <div>
          <xsl:copy-of select="$content"/>
        </div>
      </div>
    </xsl:if>      
  </xsl:template>

  <xsl:template name="caselawOutline">
    <!-- Document Sections (Counsel|Abridgment Classification|Headnote|Annotation|Table of Authorities|Opinion|Disposition -->
      <!-- Counsel -->
      <xsl:if test="/Document/n-docbody/decision/content.block/attorney.block">
        <div>
          <a href="#&crswCounselId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswCounselLabelKey;', '&crswCounselLabel;')"/>
          </a>
        </div>
      </xsl:if>

      <!-- Abridgment Classification -->
      <xsl:if test="/Document/n-docbody/decision/content.block/headnote.block/digest.wrapper">
        <div>
          <a href="#&crswAbridgmentHeaderId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswAbridgmentClassificationLabelKey;', '&crswAbridgmentClassificationLabel;')"/>
          </a>
        </div>
      </xsl:if>
      
      <!-- Headnote -->
      <xsl:if test="/Document/n-docbody/decision/content.block/headnote.block/headnote.wrapper">
        <div>
          <a href="#&crswHeadnoteId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswHeadnoteLabelKey;', '&crswHeadnoteLabel;')"/>
          </a>
        </div>
      </xsl:if>

      <!-- Annotation -->
      <xsl:if test="/Document/n-docbody/decision/content.block/editorial.note.block.wrapper">
        <div>
          <a href="#&crswAnnotationHeaderId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswAnnotationLabelKey;', '&crswAnnotationLabel;')"/>
          </a>
        </div>
      </xsl:if>
      
      <!-- Table of Authorities (AKA ref list)??? -->
      <xsl:if test="/Document/n-docbody/decision/content.block/reflists.wrapper/table.of.cases.block or /Document/n-docbody/decision/content.block/reflists.wrapper//code.reference.block">
        <div>
          <a href="#&crswTableOfAuthoritiesId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswAuthoritiesLabelKey;', '&crswAuthoritiesLabel;')"/>
          </a>
        </div>
      </xsl:if>

      <!-- Opinion -->
      <xsl:if test="/Document/n-docbody/decision/content.block/opinion.block">
        <div>
          <a href="#&opinionId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswOpinionLabelKey;', '&crswOpinionLabel;')"/>
          </a>
        </div>
      </xsl:if>

      <!-- Disposition (STATEDIS tag) -->
      <xsl:if test="/Document/n-docbody/decision/content.block/order.block">
        <div>
          <a href="#&crswDispositionId;">
            <xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswDispositionLabelKey;', '&crswDispositionLabel;')"/>
          </a>
        </div>
      </xsl:if>    
  </xsl:template>


  <!-- This key is used to group letters together for the GoTo widget so it doesn't have both capitalized & Lowercase letters
       It uses the Muenchian grouping technique to key a map and then call elements that are keyed -->
  <xsl:key name="block0-first-letters" match="/Document/n-docbody/index/block0" use="translate(substring(blkti/text(), 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
  
  <!-- This template is used to render the GoTo for the Index documents, the links and everything. 
       Instead of using for-each which is somewhat resource-intensive, we use the apply-templates with mode "trick". -->
  <xsl:template name="indexOutline">
    <!-- Finding out whether a block0 is first in the list returned by the key block0-first-letters involves 
         comparing the block0 node with the node that is first in the list returned by the key block0-first-letters. -->
    <xsl:apply-templates mode="indexOutlineLoop" select="/Document/n-docbody/index/block0[generate-id() = generate-id(key('block0-first-letters', translate(substring(blkti/text(), 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'))[1])]" />
  </xsl:template>

  <xsl:template match="/Document/n-docbody/index/block0" mode="indexOutlineLoop">
    <xsl:variable name="firstLetter" select="translate(substring(blkti/text(), 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    <xsl:variable name="anchorid" select="anchor[DocumentExtension:IsMatch(@ID, '^[A-Z][a-fA-F0-9]{32}$')]/@ID" />
    <div>
      <a href="#&internalLinkIdPrefix;{$anchorid}">
        <xsl:copy-of select="$firstLetter"/>
      </a>
    </div>    
  </xsl:template>
  
  
</xsl:stylesheet>