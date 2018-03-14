<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="renderDocumentTopBlock">
		<xsl:call-template name="renderDocumentTopBlockWithDisclaimer"/>
	</xsl:template>
	
	<xsl:template name="renderDocumentTopBlockWithDisclaimer">
		<div class="&twoColumnClass;">
			<div class="&coColumnClass; &co60PercentClass;">
				<xsl:call-template name="renderDocketOrdersAccessBlock"/>
				<xsl:call-template name="renderDocumentTopBodyBlock"/>
				<xsl:call-template name="renderDocketDateBlock"/>
			</div>
			<div class="&coColumnClass; &co40PercentClass;">
				<xsl:call-template name="renderDisclaimerBlock"/>
			</div>
		</div>
		<xsl:call-template name="renderDisclaimerClearBlock"/>
	</xsl:template>

	<xsl:template name="renderDisclaimerBlock">

		<xsl:variable name="disclaimerHeaderText">
			<xsl:value-of disable-output-escaping="yes" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&docketsDisclaimerHeader;', '&docketsDisclaimerHeaderDefaultText;')"/>
		</xsl:variable>
		
		<xsl:variable name="disclaimerUrl">
			<xsl:value-of disable-output-escaping="yes" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&docketsSupplierAdditionalTermsUrl;', '&docketsSupplierAdditionalTermsDefaultUrl;')"/>
		</xsl:variable>

		<xsl:variable name="disclaimerTitleText">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&docketsSupplierAdditionalTermsLinkTitleText;', '&docketsSupplierAdditionalTermsDefaultLinkTitleText;')"/>
		</xsl:variable>

		<xsl:variable name="disclaimerText">
			<xsl:value-of disable-output-escaping="yes" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&docketsDisclaimer;', '&docketsDisclaimerDefaultText;')"/>
			<xsl:element name="a">
				<xsl:choose>
					<xsl:when test="$DeliveryMode or $IsIpad or $IsMobile or $IsIphone">
						<xsl:attribute name="href">
							<xsl:value-of select="$disclaimerUrl"/>
						</xsl:attribute>
						<xsl:attribute name="target">
							<xsl:value-of select="'_blank'"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="href">
							<xsl:text>javascript:void(0);</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>&linkoutShowLightboxOnClickClass;</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="data-external-url">
							<xsl:value-of select="$disclaimerUrl"/>
						</xsl:attribute>
						<xsl:attribute name="data-external-title">
							<xsl:value-of select="$disclaimerTitleText"/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:attribute name="title">
					<xsl:value-of select="$disclaimerTitleText"/>
				</xsl:attribute>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&docketsSupplierAdditionalTermsLinkText;', '&docketsSupplierAdditionalTermsDefaultLinkText;')"/>
			</xsl:element>
		</xsl:variable>

		<div id="&docketsDisclaimerBlockId;">
			<h3 class="&sectionHeaderClass;">
				<xsl:value-of select="$disclaimerHeaderText"/>
			</h3>
			<div class="&headnotePublicationBlockContainer;">
				<xsl:copy-of select="$disclaimerText"/>
		</div>
		</div>
	</xsl:template>

	<xsl:template name="renderDisclaimerClearBlock">
		<div class="&footnoteClass;"></div>
	</xsl:template>
	
</xsl:stylesheet>
