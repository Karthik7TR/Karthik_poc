<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocketsFederalAppellate.xsl"/>
  <xsl:include href="SharedGWDockets.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="prior.cases.block">
		<table class="&docketsTable;">
			<tr>
				<xsl:for-each select="label">
					<th>
						<xsl:apply-templates />
					</th>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="prior.cases.entry">
				<tr>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="prior.docket.number.block/prior.docket.number" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="prior.cases.judge" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="prior.cases.date.filed" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="date.disposed" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="disposition.code" />
						</xsl:with-param>
					</xsl:call-template>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
</xsl:stylesheet>
