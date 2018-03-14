<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited  -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="GlobalParams.xsl"/>
  <xsl:include href="Footnotes.xsl" />
  <xsl:include href="Transactions.xsl"/>
  
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

  <xsl:variable name ="metadatablock" select="Document/n-metadata/md.sedi.metadata.block" />
  <xsl:variable name ="na-answer" select="'N/A'" />

  <xsl:template match="Document">
    <div id="&documentId;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&contentTypeSEDI;'"/>
      </xsl:call-template>
      <xsl:comment>&EndOfDocumentHead;</xsl:comment>
      <xsl:call-template name="Content"/>
      <xsl:call-template name="RenderFootnoteSection"/>
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <!--
		**************************************************************************************
		*
		*		Render the XML for content.  Call templates to render each of the sections.      *
		**************************************************************************************
	-->
  <xsl:template name="Content">

    <xsl:call-template name="InsiderSection"/>
    <xsl:call-template name="IssuerSection"/>
    <xsl:call-template name="TransactionSection"/>
    
  </xsl:template>

  <!--
	**************************************************************************************
	*		Insider Section                                                              *
	**************************************************************************************
	-->
  <xsl:template name="InsiderSection">
    <div class="&layout_TransactionAbstractDocumentSection;">
      <div class="&layoutHeaderRow;">
        <h3>
          <xsl:text>Insider</xsl:text>
        </h3>
      </div>
    </div>

    <div class="&layout_Row_MarginBottom;">
      <table class="&layout_table; &layout_2Columns;">

        <!--Name-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Name:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.insider.name) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.insider.name"/>
        </xsl:call-template>

        <!--Number-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Number:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.insider.nbr) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.insider.nbr"/>
        </xsl:call-template>

        <!--Relationship to Issuer-->
        <xsl:for-each select="$metadatablock/md.insider.relationships/md.insider.relationship">
          <xsl:variable name="label">
            <xsl:choose>
              <xsl:when test="position() = 1"><xsl:value-of select="'Relationship to Issuer:'"/></xsl:when>
              <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:call-template name="VerifyString">
            <xsl:with-param name="param1" select="$label"/>
            <xsl:with-param name="param2" select="string-length(md.relationship) &gt; 0"/>
            <xsl:with-param name="param3" select="md.relationship"/>
          </xsl:call-template>
        </xsl:for-each>

        <!--Date Became Insider of Issuer-->
        <xsl:call-template name="VerifyDate">
          <xsl:with-param name="param1" select="'Date Became Insider of Issuer:'"/>
          <xsl:with-param name="param2" select="$metadatablock/md.insider.begin.date"/>
          <xsl:with-param name="yearFirst" select="1"/>
        </xsl:call-template>
        
        <!--Date Ceased to be an Insider of Issuer-->
        <xsl:call-template name="VerifyDate">
          <xsl:with-param name="param1" select="'Date Ceased to be an Insider of Issuer:'"/>
          <xsl:with-param name="param2" select="$metadatablock/md.insider.end.date"/>
          <xsl:with-param name="yearFirst" select="1"/>
        </xsl:call-template>
        
      </table>
    </div>
    
  </xsl:template>

  <!--
	**************************************************************************************
	*		Issuer Section                                                              *
	**************************************************************************************
	-->
  <xsl:template name="IssuerSection">
    <div class="&layout_TransactionAbstractDocumentSection;">
      <div class="&layoutHeaderRow;">
        <h3>
          <xsl:text>Issuer</xsl:text>
        </h3>
      </div>
    </div>

    <div class="&layout_Row_MarginBottom;">
      <table class="&layout_table; &layout_2Columns;">

        <!--Name-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Name:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.issuer.name) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.issuer.name"/>
        </xsl:call-template>

        <!--Ticker Symbol-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Ticker Symbol:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.stock.symbol) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.stock.symbol"/>
        </xsl:call-template>

      </table>
    </div>
    
  </xsl:template>

  <!--
	**************************************************************************************
	*		Transaction Section                                                              *
	**************************************************************************************
	-->  
  <xsl:template name="TransactionSection">
    <div class="&layout_TransactionAbstractDocumentSection;">
      <div class="&layoutHeaderRow;">
        <h3>
          <xsl:text>Transaction</xsl:text>
        </h3>
      </div>
    </div>

    <div class="&layout_Row_MarginBottom;">
      <table class="&layout_table; &layout_2Columns;">

        <!--Transaction Date-->
        <xsl:call-template name="VerifyDate">
          <xsl:with-param name="param1" select="'Transaction Date:'"/>
          <xsl:with-param name="param2" select="$metadatablock/md.transaction.date"/>
          <xsl:with-param name="yearFirst" select="1"/>
        </xsl:call-template>
        
        <!--Closing Date-->
        <xsl:call-template name="VerifyDate">
          <xsl:with-param name="param1" select="'Closing Date:'"/>
          <xsl:with-param name="param2" select="$metadatablock/md.closing.date"/>
          <xsl:with-param name="yearFirst" select="1"/>
        </xsl:call-template>

        <!--Filing Date-->
        <xsl:call-template name="VerifyDate">
          <xsl:with-param name="param1" select="'Filing Date:'"/>
          <xsl:with-param name="param2" select="$metadatablock/md.filing.date"/>
          <xsl:with-param name="yearFirst" select="1"/>
        </xsl:call-template>

        <!--Transaction ID-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Transaction ID:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.transaction.id) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.transaction.id"/>
        </xsl:call-template>

        <!--Transaction State-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Transaction State:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.status/text()) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.status/text()"/>
        </xsl:call-template>

        <!--Nature of Transaction-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Nature of Transaction:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.transaction.type/md.type) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.transaction.type/md.type"/>
        </xsl:call-template>
        
        <!--Registered Holder-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Registered Holder:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.registered.holder) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.registered.holder"/>
        </xsl:call-template>

        <!--Ownership Type-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Ownership Type:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.ownership.type/md.type) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.ownership.type/md.type"/>
        </xsl:call-template>

        <!--Security Designation-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Security Designation:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.security.designation/md.security) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.security.designation/md.security"/>
        </xsl:call-template>

        <!--Underlying Security Designation-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Underlying Security Designation:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.underlying.security/md.designation) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.underlying.security/md.designation"/>
        </xsl:call-template>

        <!--Date of Expiry or Maturity-->
        <xsl:call-template name="VerifyDate">
          <xsl:with-param name="param1" select="'Date of Expiry or Maturity:'"/>
          <xsl:with-param name="param2" select="$metadatablock/md.expire.date"/>
          <xsl:with-param name="yearFirst" select="1"/>
        </xsl:call-template>

        <!--Unit Price or Exercise Price-->
        <xsl:variable name="unitPrice">
          <xsl:call-template name="VerifyNumberValue">
            <xsl:with-param name="xpath" select="$metadatablock/md.unit.price/md.amount" />
            <xsl:with-param name="value" select="format-number($metadatablock/md.unit.price/md.amount, '#,###0.0000')" />
          </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="unitPriceDisplayValue">
          <xsl:choose>
            <xsl:when test="$unitPrice = $na-answer">
              <xsl:value-of select="''"/>
            </xsl:when>
            <xsl:when test="string-length($metadatablock/md.unit.price/md.currency/md.code) &gt; 0">
              <xsl:value-of select="concat('(', $metadatablock/md.unit.price/md.currency/md.code, ') ', $unitPrice)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$unitPrice"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Unit Price or Exercise Price:'"/>
          <xsl:with-param name="param2" select="string-length($unitPriceDisplayValue) &gt; 0"/>
          <xsl:with-param name="param3" select="$unitPriceDisplayValue"/>
        </xsl:call-template>
        
        <!--Conversion or Exercise Price-->
        <xsl:variable name="convertedPrice">
          <xsl:call-template name="VerifyNumberValue">
            <xsl:with-param name="xpath" select="$metadatablock/md.converted.price/md.amount" />
            <xsl:with-param name="value" select="format-number($metadatablock/md.converted.price/md.amount, '#,###0.0000')" />
          </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="convertedPriceDisplayValue">
          <xsl:choose>
            <xsl:when test="$convertedPrice = 'N/A'">
              <xsl:value-of select="''"/>
            </xsl:when>
            <xsl:when test="string-length($metadatablock/md.converted.price/md.currency/md.code) &gt; 0">
              <xsl:value-of select="concat('(', $metadatablock/md.converted.price/md.currency/md.code, ') ', $convertedPrice)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$convertedPrice"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Conversion or Exercise Price:'"/>
          <xsl:with-param name="param2" select="string-length($convertedPriceDisplayValue) &gt; 0"/>
          <xsl:with-param name="param3" select="$convertedPriceDisplayValue"/>
        </xsl:call-template>

        <!--Number or Value Acquired or Disposed of-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Number or Value Acquired or Disposed of:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.value) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.value"/>
        </xsl:call-template>

        <!--Equivalent Number of Underlying Securities Acquired or Disposed of-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Equivalent Underlying Securities Acquired or Disposed of:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.underlying.security/md.equivalent.nbr) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.underlying.security/md.equivalent.nbr"/>
        </xsl:call-template>
        
        <!--Balance of Securities Held as of Transaction Date-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Balance of Securities Held as of Transaction Date:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.security.balance) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.security.balance"/>
        </xsl:call-template>

        <!--Closing Balance of Equivalent Number or Value of Underlying Securities-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Closing Balance of Equivalent Underlying Securities:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.underlying.security/md.close.balance) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.underlying.security/md.close.balance"/>
        </xsl:call-template>

        <!--Insider Disagreed with System Calculated Balance-->
        <xsl:call-template name="VerifyString">
          <xsl:with-param name="param1" select="'Insider Disagreed with System Calculated Balance:'"/>
          <xsl:with-param name="param2" select="string-length($metadatablock/md.balance.disagree.flag) &gt; 0"/>
          <xsl:with-param name="param3" select="$metadatablock/md.balance.disagree.flag"/>
        </xsl:call-template>

      </table>
    </div>
    
  </xsl:template>

  <!--Override from Transactions.xsl. This abstracts some parameter logic which need not be repeated while calling it. --> 
  <!--NOTE: The template VerifyDate in Transactions.xsl could be modified as this one but there is a dependency as lots of xsls call it. -->
  <!--VerifyDate-->
  <xsl:template name="VerifyDate">
    <xsl:param name="param1"/>
    <xsl:param name="param2"/>
    <xsl:param name="yearFirst"/>
    <tr>
      <td class="&layout_col1;">
        <div>
          <h4>
            <xsl:value-of select="$param1"/>
          </h4>
        </div>
      </td>

      <td class="&layout_col2;">
        <div>
          <xsl:call-template name="FormatDateValue">
            <xsl:with-param name="ifValidDate" select="string-length($param2) &gt; 7	and number($param2) != 'NaN'"/>
            <xsl:with-param name="dateValue" select="$param2"/>
            <xsl:with-param name="yearFirst" select="$yearFirst"/>
          </xsl:call-template>
        </div>
      </td>
    </tr>
  </xsl:template>

  <!--Override from Transactions.xsl. This abstracts some parameter logic which need not be repeated while calling it.-->
  <!--NOTE: The template VerifyDate in Transactions.xsl could be modified as this one but there is a dependency as lots of xsls call it. -->
  <!--VerifyNumberValue-->
  <xsl:template name="VerifyNumberValue">
    <xsl:param name="xpath"/>
    <xsl:param name="value"/>

    <xsl:choose>
      <xsl:when test="$xpath = 0 or string-length($xpath) = 0">
        <xsl:value-of select="$na-answer"/>
      </xsl:when>
      <xsl:when test="string(number($xpath)) != 'NaN'">
        <xsl:value-of select="$value" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$na-answer"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
