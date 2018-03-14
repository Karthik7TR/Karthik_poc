<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Cites.xsl"/>
  <xsl:include href="Title.xsl"/>
  <xsl:include href="Date.xsl"/>
  <xsl:include href="SimpleContentBlocks.xsl"/>  
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeMedLitSciSearchClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
      <xsl:apply-templates />
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <xsl:template match="head" priority="1">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="head/headtext" priority="1">
    <span class="&headtextClass; &titlePrefixClass;">
      <xsl:apply-templates select="text()" />
    </span>
  </xsl:template>

  <xsl:template match="author.block" priority="1">
		<div class="&authorSciSearchClass;">
			<xsl:apply-templates />
		</div>
  </xsl:template>

  <xsl:template match="author.line/author" priority="1">
    <span>
      <xsl:text><![CDATA[ ]]></xsl:text>      
			<xsl:apply-templates />
    </span>
  </xsl:template>

  <xsl:template match="subject.category.block" priority="1">
    <div>
      <xsl:attribute name="class">
        <xsl:text>&simpleContentBlockClass;</xsl:text>
      </xsl:attribute>
      <xsl:apply-templates select="head" />
      <span>
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="subject.category" />
      </span>
    </div>
  </xsl:template>

  <xsl:template match="ids.number.block" priority="1">
    <div class="&simpleContentBlockClass;">
      <xsl:apply-templates select="head" />
      <span>
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="ids.number" />
      </span>
    </div>
  </xsl:template>
  
  <xsl:template match="language.block" priority="1">
    <div class="&languageBlockClass;">
      <xsl:apply-templates select="head" />
      <span>
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="language.line"/>
      </span>
    </div>
  </xsl:template>
	
	<xsl:template match="language.block/language.line" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

  <xsl:template match="keywords.block" priority="1">
    <div class="&keywordsBlockClass;">
      <xsl:apply-templates select="head" />
      <xsl:apply-templates select="keyword.list/keyword" />
    </div>
  </xsl:template>

  <xsl:template match="keyword.list/keyword" priority="1">
    <div>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="abstract" priority="1">
    <span>
      <xsl:if test="@id">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('&internalLinkIdPrefix;', @id)"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:text><![CDATA[ ]]></xsl:text>
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="issn.block" priority="1">
    <div class="&simpleContentBlockClass;">
      <xsl:apply-templates select="head" />
      <xsl:apply-templates select="issn" />
    </div>
  </xsl:template>

  <xsl:template match="issn" priority="1">
    <span>
      <xsl:text><![CDATA[ ]]></xsl:text>
      <xsl:apply-templates />
    </span>
  </xsl:template>

	<xsl:template match="publication.block" priority="1">
    <div class="&simpleContentBlockClass;">
      <xsl:apply-templates select="head" />
      <span>
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="publication.type.line/publication.type" />
      </span>
    </div>
	</xsl:template>
  
  <xsl:template match="publisher.block" priority="1">
    <div class="&simpleContentBlockClass;">
      <xsl:apply-templates select="head" />
      <span>
				<xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="publisher/publisher.name" />
				<xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="publisher/publisher.address" />
      </span>
    </div>
  </xsl:template>

  <!-- START: source.block templates -->
  <xsl:template match="source.block" priority="1">
    <div class="&sourceBlockClass;">
      <xsl:apply-templates select="head" />
      <span>
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="source/source.title" />
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:apply-templates select="source/source.info" />
      </span>
    </div>
	</xsl:template>

  <xsl:template match="issue" priority="1">
    <xsl:text><![CDATA[ ]]></xsl:text>
    <xsl:apply-templates />
    <xsl:text><![CDATA[ ]]></xsl:text>
  </xsl:template>
  
  <xsl:template match="pubdate" priority="1">
    <xsl:text><![CDATA[ ]]></xsl:text>
    <xsl:apply-templates />
  </xsl:template>

  <!-- END: source.block templates -->

	<xsl:template match="pub.name" priority="1">
		<div class="&alignHorizontalCenterClass;">
			<xsl:copy-of select="text()" />
		</div>
	</xsl:template>

  <xsl:template match="date.line">
    <div class="&simpleContentBlockClass; &alignHorizontalCenterClass;">
      <xsl:apply-templates />
    </div>
  </xsl:template>

	<xsl:template match="title.block" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

  <!-- START: references.block templates -->
  
  <xsl:template match="references.block" priority="1">
    <div class="&referenceBlockClass;">
      <xsl:apply-templates select="head" />
      <xsl:text><![CDATA[ ]]></xsl:text>
      <span>
        <xsl:apply-templates select="num.references" />
        <xsl:apply-templates select="patent.reference" />
      </span>
      <xsl:apply-templates select="reference" />
    </div>
  </xsl:template>

	<xsl:template match="patent.reference/patent.holder | patent.reference/patent.info/patent.no | patent.reference/patent.info/patent.country | patent.reference/patent.info/patent.type | patent.reference/patent.info/patent.year" priority="1">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

  <xsl:template match="reference" priority="1">
    <xsl:if test="cited.author">
      <xsl:apply-templates select="cited.author" />
    </xsl:if>
    <xsl:if test="cited.title">
      <xsl:apply-templates select="cited.title" />
    </xsl:if>
    <xsl:if test="cited.publication">
      <xsl:for-each select="cited.publication/node()">
        <xsl:apply-templates select="." />
        <xsl:text><![CDATA[ ]]></xsl:text>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="cited.author" priority="1">
    <xsl:call-template name="wrapContentBlockWithGenericClass" />
  </xsl:template>
  
  <xsl:template match="cited.title" priority="1">
    <xsl:call-template name="wrapContentBlockWithGenericClass" />
  </xsl:template>

  <!-- END: references.block templates -->

  <!-- START: Simple Content Blocks -->
  
  <xsl:template match="abstract.block" priority="1">
    <xsl:call-template name="wrapContentBlockWithGenericClass" />
  </xsl:template>

  <xsl:template match="address.list" priority="1">
	  <xsl:call-template name="wrapContentBlockWithGenericClass" />
  </xsl:template>

  <xsl:template match="reprint.author" priority="1">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
    
  <xsl:template match="keyword.list" priority="1">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

  <!-- END: Simple Content Blocks -->

	<xsl:template match="message.block/include.copyright" priority="1">
		<div class="&copyrightClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

</xsl:stylesheet>
