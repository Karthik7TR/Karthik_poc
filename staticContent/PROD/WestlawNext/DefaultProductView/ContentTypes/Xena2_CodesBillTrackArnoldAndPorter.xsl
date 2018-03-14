<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<div class="&citesClass;">
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
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="cr">
		<xsl:if test="not(//md.cites)">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="ti | ti2 | til | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | snl | srnl | hc2 | sy |dl | pd">
		<xsl:apply-templates />
		<xsl:if test="not(following-sibling::ti or following-sibling::ti2 or following-sibling::til or following-sibling::hg0 or following-sibling::hg1 or following-sibling::hg2 or following-sibling::hg3 or following-sibling::hg4 or following-sibling::hg5 or following-sibling::hg6 or following-sibling::hg7 or following-sibling::hg8 or following-sibling::hg9 or following-sibling::hg10 or following-sibling::hg11 or following-sibling::hg12 or following-sibling::hg13 or following-sibling::hg14 or following-sibling::hg15 or following-sibling::hg16 or following-sibling::hg17 or following-sibling::hg18 or following-sibling::hg19 or following-sibling::snl or following-sibling::srnl or following-sibling::hc2 or following-sibling::sy or following-sibling::dl  or following-sibling::pd)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>