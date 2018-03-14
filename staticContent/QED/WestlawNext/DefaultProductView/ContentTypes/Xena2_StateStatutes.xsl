<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="md.cites" priority="3">
    <div class="&citesClass;">
			<div>
      <xsl:choose>
	      <xsl:when test="md.second.line.cite">
		      <xsl:apply-templates select="md.second.line.cite"/>
			  </xsl:when>
				<xsl:when test="md.third.line.cite">
          <xsl:apply-templates select="md.third.line.cite"/>
	      </xsl:when>
		    <xsl:when test="md.first.line.cite">
			    <xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
				<xsl:when test="md.primarycite/md.primarycite.info/md.display.primarycite">
			    <xsl:apply-templates select="md.primarycite/md.primarycite.info/md.display.primarycite"/>
				</xsl:when>
			</xsl:choose>				
			</div>
			<div>
				<xsl:apply-templates select="/Document/n-docbody/doc/ce[1]//d7/text()"/>
			</div>
		</div>
  </xsl:template>

	<!--Supress the copyright message-->
	<xsl:template match="copmx"/>

</xsl:stylesheet>
