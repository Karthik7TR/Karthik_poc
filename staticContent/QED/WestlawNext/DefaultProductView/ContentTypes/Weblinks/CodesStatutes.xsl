<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Cites.xsl"/>
  <xsl:include href="Title.xsl"/>
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="NotesOfDecisions.xsl"/>
  <xsl:include href="Credit.xsl"/>
  <xsl:include href="ContextAndAnalysis.xsl"/>
  <xsl:include href="Footnotes.xsl"/>
  <xsl:include href="LinkedToc.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:include href="Annotations.xsl"/>
  <xsl:include href="HistoryNotes.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document"  priority="1">
    <div id="&documentId;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&contentTypeCodesStatutesClass;'"/>
      </xsl:call-template>
      <xsl:call-template name="StarPageMetadata"/>
      <xsl:apply-templates/>
      <xsl:apply-templates select="n-docbody//content.metadata.block" mode="footerCustomCitation"  />
      <!--Adding a Div so the a separate Fo block is made for the currencyblock  Fix for Bug303578-->
      <div>
        <xsl:apply-templates select="n-docbody//include.currency.block/include.currency" mode="currency"/>

      </div>
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <!--Supress this-->
  <xsl:template match="include.currency"/>
 
  <xsl:template match="content.metadata.block" mode="footerCustomCitation">
    <div class="&alignHorizontalLeftClass;">
      <xsl:apply-templates select ="cmd.identifiers/cmd.cites/cmd.expandedcite"/>
    </div>
  </xsl:template>

  <xsl:template match="subsection//headtext">
    <div>
      <strong>
        <xsl:apply-templates />
      </strong>
    </div>
  </xsl:template>

  <xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgp']" priority="1">
    <xsl:call-template name="copyrightBlock">
      <xsl:with-param name="copyrightNode" select="." />
    </xsl:call-template>
  </xsl:template>


  <!-- Suppress these two elements since they look weird. -->
  <xsl:template match="md.secondary.cites | popular.name.doc.title" />

  <xsl:template match="include.head.block">
    <div class="&headtextClass;">
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <!-- Message.Block/Message -->
  <xsl:template match="message.block">
    <div class="&centerClass;">
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="message.block/message">
    <div>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="signature.block/signature.line">
    <div>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="body.footnote.block" />

  <xsl:template match="abbreviations.reference | abbreviations">
    <xsl:variable name="refid"  select="translate(@refid, '?', 'Þ')" />
    <xsl:variable name="id"  select="translate(@ID, '?', 'Þ')" />
    <xsl:variable name="contents">
      <xsl:apply-templates />
    </xsl:variable>
    <div>
      <xsl:if test="string-length($contents) &gt; 0 or string-length($id) &gt; 0">
        <xsl:choose>
          <xsl:when test="key('allElementIds', $refid)">
            <a href="{concat('#&internalLinkIdPrefix;', $refid)}" class="&internalLinkClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
              <xsl:if test="string-length($id) &gt; 0">
                <xsl:attribute name="id">
                  <xsl:value-of select="concat('&internalLinkIdPrefix;', $id)"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:copy-of select="$contents"/>
              <xsl:comment>anchor</xsl:comment>
            </a>
          </xsl:when>
          <xsl:when test="string-length($id) &gt; 0">
            <a id="{concat('#&internalLinkIdPrefix;', $id)}">
              <xsl:comment>anchor</xsl:comment>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="$contents"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="centdol"/>

  <!--Fix for the missing content in WLN(Collection : w_codesstailnvdp)-->
  <xsl:template match="ed.note.grade">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="propagated.block" />
  
  <xsl:template match="annotations[/Document/document-data/collection = 'w_codesstailnvdp']" />
        
  <!--Supress tgroup tags that have warning attribute. bug 339115)-->
  <xsl:template match="tgroup[@warning]" priority="2"/>

  <!--call tgroup template passing in the column width check parameter.-->
  <xsl:template match="tgroup" priority="1">
    <xsl:call-template name ="TGroupTemplate">
      <xsl:with-param name ="checkNoColWidthExists" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="tbody/row/entry" priority="1">
    <xsl:param name="columnInfo" />
    <xsl:param name="colalign" />
    <xsl:param name="colposition" />
    <xsl:param name="colwidth" />
    <xsl:if test="not($colposition &gt; count($columnInfo))">
      <td>
        <xsl:call-template name="RenderTableCell">
          <xsl:with-param name="columnInfo" select="$columnInfo"/>
          <xsl:with-param name="colalign" select="$colalign" />
          <xsl:with-param name="colposition" select="$colposition" />
          <xsl:with-param name="colwidth" select="$colwidth" />
        </xsl:call-template>
      </td>
    </xsl:if>
  </xsl:template>

 </xsl:stylesheet>