<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">
  <xsl:include href="Para.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="para[(@ampexmnem = 'dpa2' or @ampexmnem = 'dpa3' or @ampexmnem = 'dpa4')]/paratext | para//para/paratext[count(begin.quote) = 1 and count(end.quote) = 1 and not(begin.quote/preceding-sibling::node()[not(self::starpage.anchor or self::footnote.reference or self::endnote.reference or self::table.footnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN')] or self::eos or self::bos or self::eop or self::bop)]) and not(end.quote/following-sibling::node()[not(self::starpage.anchor or self::footnote.reference or self::endnote.reference or self::table.footnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN')] or self::eos or self::bos or self::eop or self::bop)])]" priority="2">
    <xsl:call-template name="renderParagraphTextDiv">
      <xsl:with-param name="contents">
        <blockquote>
          <div>
            <xsl:apply-templates/>
          </div>
        </blockquote>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
