<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 3/26/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Universal.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="area.of.expertise.development">
	<xsl:if test="normalize-space(.)">
	  <div class="&headtextClass;">
		<strong>
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewInterestingDevelopmentInAreaOfExpertiseKey;', '&ewInterestingDevelopmentInAreaOfExpertise;')"/>
		</strong>
	  </div>
	  <div class="&paraMainClass;">
		<xsl:apply-templates/>
	  </div>
	</xsl:if>
  </xsl:template>

  <xsl:template match="area.of.expertise.conference">
	<xsl:if test="normalize-space(.)">
	  <div class="&headtextClass;">
		<strong>
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewPremierConferenceInAreaOfExpertiseKey;', '&ewPremierConferenceInAreaOfExpertise;')"/>
		</strong>
	  </div>
	  <div class="&paraMainClass;">
		<xsl:apply-templates/>
	  </div>
	</xsl:if>
  </xsl:template>

  <xsl:template match="area.of.expertise.rec.book">
	<xsl:if test="normalize-space(.)">
	  <div class="&headtextClass;">
		<strong>
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewRecommendedBookInAreaOfExpertiseKey;', '&ewRecommendedBookInAreaOfExpertise;')"/>
		</strong>
	  </div>
	  <div class="&paraMainClass;">
		<xsl:apply-templates/>
	  </div>
	</xsl:if>
  </xsl:template>

  <xsl:template match="area.of.expertise.rec.website">
	<xsl:if test="translate(normalize-space(.), '&#x0009;&#x000A;&#x000D;', ' ')">
	  <div class="&headtextClass;">
		<strong>
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewRecommendedWebsiteInAreaOfExpertiseKey;', '&ewRecommendedWebsiteInAreaOfExpertise;')"/>
		</strong>
	  </div>
	  <div class="&paraMainClass;">
		<!-- Data contains new lines,  New lines get converted to spaces (ex. guid:  ) -->
		<xsl:variable name="convertReturnsToSpace"  select="translate(normalize-space(.), '&#x0009;&#x000A;&#x000D;', ' ')"/>
		<xsl:call-template name="SpecialCharacterTranslator">
		  <xsl:with-param name="textToTranslate" select="$convertReturnsToSpace"></xsl:with-param>
		</xsl:call-template>
	  </div>
	</xsl:if>
  </xsl:template>

</xsl:stylesheet>

