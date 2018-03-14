<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="credit">
		<xsl:variable name="contents">
      <xsl:apply-templates />
		</xsl:variable>
    <xsl:if test="string-length($contents) &gt; 0">
			<xsl:if test="not(preceding::credit)">
				<div class="&printHeadingClass;">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', '&credits;')" />
					</xsl:attribute>
					<h2>
              <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&creditsKey;', '&credits;')"/>
          </h2>
				</div>
			</xsl:if>
      <xsl:apply-templates />
    </xsl:if>		
	</xsl:template>

	<!-- Suppressed -->
	<xsl:template match="credit/head/headtext"/>
	
</xsl:stylesheet>
