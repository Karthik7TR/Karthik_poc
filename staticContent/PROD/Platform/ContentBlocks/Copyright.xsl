<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	
	<xsl:template name="copyrightBlock">
		<xsl:param name="copyrightNode" select="."/>

		<xsl:variable name="copyright">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="normalize-space($copyrightNode)" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="should-display">
			<xsl:call-template name="check-copyright-text">
				<xsl:with-param name="string">
					<xsl:call-template name="upper-case">
						<xsl:with-param name="string" select="translate($copyright, '/', '&space;')"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="contains($should-display, 'true')">
			<div class="&copyrightClass;">
				<xsl:value-of select="$copyright"/>
			</div>
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="check-copyright-text">
		<xsl:param name="string" select="."/>
		<xsl:param name="split" select="'&space;'"/>

		<xsl:choose>
			<xsl:when test="contains($string, $split)">
				<!--Remove commas and periods from the substring-->
				<xsl:variable name="substring" select="translate(substring-before($string, $split), '.,', '')"/>
			
				<xsl:if test="string-length($substring) &gt; 1">
					<!-- If the substring isn't in the list of generic copyright messages, and it isn't a number (year) we want to display the copyright -->
					<xsl:if test="not(contains('&genericCopyrightWords;', $substring)) and not(number($substring))">
						<xsl:text>true</xsl:text>
					</xsl:if>
				</xsl:if>
				
				<xsl:call-template name="check-copyright-text">
					<xsl:with-param name="string" select="substring-after($string, $split)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="substring" select="translate($string, '.,', '')"/>
				
				<xsl:if test="not(contains('&genericCopyrightWords;', $substring)) and not(number($substring))">
					<xsl:text>true</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	
	<xsl:template match="cmd.copyright">
		<div class="&copyrightClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>		


	<!--MATCH FOR TESTING PURPOSES-->
	<xsl:template match="copyright.test.data">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="."/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>