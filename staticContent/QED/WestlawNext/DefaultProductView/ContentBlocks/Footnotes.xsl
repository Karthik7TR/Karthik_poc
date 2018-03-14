<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Footnotes.xsl" forcePlatform="true" />
  <xsl:include href="Universal.xsl"/>
  <xsl:include href="FootnoteReferenceCleaner.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
  <xsl:preserve-space elements="*"/>

  <xsl:key name="distinctFootnoteIds" match="footnote | form.footnote | endnote | form.endnote" use="@ID | @id" />
  <xsl:key name="distinctAlternateFootnoteIds" match="footnote//anchor" use="@ID | @id"/>
  <xsl:key name="distinctFootnoteReferenceRefIds" match="footnote.reference | table.footnote.reference | endnote.reference" use="@refid" />
  <xsl:key name="distinctFootnoteAnchorReferenceRefIds" match="internal.reference"  use="@refid" />

  <!-- Render footnotes at the bottom of the output document -->
  <xsl:template name="RenderFootnoteSectionMarkupDiv">
    <xsl:param name="contents"/>
    <div id="&footnoteSectionId;" class="&footnoteSectionClass; &briefItStateClass;">
      <h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
      </h2>
      <xsl:copy-of select="$contents"/>
    </div>
  </xsl:template>

  <xsl:template name="RenderFootnoteSectionMarkupTable">
    <xsl:param name="contents"/>
    <table id="&footnoteSectionId;" class="&footnoteSectionClass; &briefItStateClass;">
      <tr>
        <td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
        </td>
      </tr>
      <xsl:copy-of select="$contents"/>
    </table>
  </xsl:template>
  
  <!-- Suppress footnote references from title metadata -->
  <xsl:template match="/Document/document-data/title//footnote.reference | /Document/document-data/title//table.footnote.reference | /Document/document-data/title//endnote.reference" priority="2" />

  <!-- Capture rogue "super" elements that are not contained within "table.footnote.reference"/"footnote.reference"/endnote.reference" elements -->
  <xsl:template match="/Document/document-data/title//super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]" priority="2" />

</xsl:stylesheet>
