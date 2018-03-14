﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 3/26/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="resume">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
			<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewResumeKey;', '&ewResume;')"/>
        </strong>
			</div>
			<xsl:apply-templates select="para"/>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
