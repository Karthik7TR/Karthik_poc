<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="GlobalParams.xsl"/>

	<xsl:template match="begin.quote" name="beginQuote">
		<xsl:param name="additionalContent" />
		<xsl:if test="string-length(@ID) &gt; 0">
			<xsl:variable name="delimitedId" select="concat(';', concat(@ID, ';'))" />
			<xsl:if test="contains($Quotes, $delimitedId)">
				<xsl:copy-of select="$additionalContent"/>
				<xsl:processing-instruction name="start-highlighting">
					<xsl:value-of select="concat('co_searchTerm_', generate-id(.))"/>
				</xsl:processing-instruction>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="end.quote">
		<xsl:if test="string-length(@refid) &gt; 0">
			<xsl:variable name="delimitedRefId" select="concat(';', concat(@refid, ';'))" />
			<xsl:if test="contains($Quotes, $delimitedRefId)">
				<xsl:processing-instruction name="end-highlighting"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="EndOfDocument">
		<xsl:choose>
			<xsl:when test="$PreviewMode = 'True'">
				<xsl:call-template name="AdditionalContent" />
			</xsl:when>
			<xsl:otherwise>
				<div id="&endOfDocumentId;">
					<div>&endOfDocumentText;</div>
					<xsl:if test="$ClarivateAnalyricsCopyright">
						<div class="&endOfDocumentCopyrightClass;"><xsl:copy-of select="$ClarivateAnalyricsCopyright"/></div>
					</xsl:if>
					<div class="&endOfDocumentCopyrightClass;">&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/></div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
