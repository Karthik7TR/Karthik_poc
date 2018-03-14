<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="FixedHeader.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!--START: For M & A Transactions content type. Concatenates target and acquirer party names with a dash separator. Also concatenates descriptions with comma separator. -->
	<xsl:template match="md.description">
    <xsl:variable name="contents" select=".">
    </xsl:variable>
    <xsl:if test="string-length($contents) &gt; 0">
      <xsl:copy-of select="$contents"/>
      <xsl:if test="contains($contents, ',')">
        <xsl:variable name="afterCommaString" select="substring-after($contents, ', ')"/>
        <xsl:if test="string-length($afterCommaString) = 1">
          <xsl:text>.</xsl:text>
        </xsl:if>
      </xsl:if>
      <xsl:variable name="endsWithComma">
        <xsl:call-template name="ends-with">
          <xsl:with-param name="string1" select="$contents" />
          <xsl:with-param name="string2" select="','" />
        </xsl:call-template>
      </xsl:variable>
      <xsl:if test="$endsWithComma = 'false'">
        <xsl:text>, </xsl:text>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="md.description[position() = last()]">
    <xsl:apply-templates />
  </xsl:template>
  <xsl:template match="md.code" />
  
  <xsl:template match="md.merger.parties">
    <xsl:apply-templates select="md.target.party" />
    <xsl:apply-templates select="md.acquirer.party" />
    <xsl:apply-templates select="md.merger.party/md.merger.party.type[.='T']" />
    <xsl:apply-templates select="md.merger.party/md.merger.party.type[.='A']" />
  </xsl:template>
  
  <xsl:template match="md.target.party | md.acquirer.party" priority="1">
    <xsl:value-of select="md.companyname"/>
    <xsl:if test="md.merger.party.type = 'T'"> | </xsl:if>
  </xsl:template>

  <xsl:template match="md.merger.party.type">
    <xsl:value-of select="../md.companyname"/>
    <xsl:if test=".='T'"> | </xsl:if>
  </xsl:template>

  <xsl:template match="//md.related.merger.parties" />
  <xsl:template match="//md.merger" />
  <xsl:template match="//md.linkid.block" />
  <xsl:template match="//md.events" />
  <xsl:template match="//md.mergerfilings" />
  <!--END: For M & A Transactions content type. Concatenates target and acquirer party names with a dash separator -->

  <xsl:template match="md.max.offering.price">
    <xsl:choose>
      <xsl:when test=".='0'">
        <xsl:text></xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--START: For Bond Transaction content type. Formats currency values and adds currency type in brackets for international currencies. -->
  <xsl:template match="FixedHeader/totalAmountIssuedBlock">
    <xsl:choose>
      <xsl:when test="totalAmountIssued=''">
        <xsl:value-of select="''"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="currencyType and currencyType != ''">
            <xsl:value-of select="concat(format-number(totalAmountIssued, '#,###'), ' (', currencyType, ')')" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number(totalAmountIssued, '#,###')" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="FixedHeader/totalAmountIssuedBlock/totalAmountIssuedUS" />
  <!--END: For Bond Transaction content type. Formats currency values and adds currency type in brackets for international currencies. -->

	<!-- Strip out super tags (don't change into sup), since fixed headers escape all tags 
				and they are rendered as user-visible text -->
	<xsl:template match="FixedHeader//super">
		<xsl:apply-templates />
	</xsl:template>
  
  <!--START: For Bond Transaction content type. Formats currency values and adds currency type in brackets for international currencies. -->
  <xsl:template match="FixedHeader/commitmentBlock">
    <xsl:choose>
      <xsl:when test="totalCommitmentHost=''">
        <xsl:value-of select="''"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="currencyBlock/currency and currencyBlock/currency != ''">
            <xsl:value-of select="concat(format-number(totalCommitmentHost, '#,###'), ' (', currencyBlock/currency, ')')" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number(totalCommitmentHost, '#,###')" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <!--END: For Bond Transaction content type. Formats currency values and adds currency type in brackets for international currencies. -->

	<xsl:template match="FixedHeader/PROPERTY">
		<xsl:value-of select="@_StreetAddress"/>&#160;
		<xsl:value-of select="@_City"/>,
		<xsl:value-of select="@_State"/>&#160;
		<xsl:value-of select="@_PostalCode"/>-
		<xsl:value-of select="@_PlusFourPostalCode"/>
	</xsl:template>
	
</xsl:stylesheet>
