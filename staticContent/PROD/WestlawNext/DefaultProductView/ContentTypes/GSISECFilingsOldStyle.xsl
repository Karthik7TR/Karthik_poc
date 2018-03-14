<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="PreformattedTextCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="display.name | document | filing | filing.body	| section.acc	| section.all	| section.aud	| section.bal	| section.bus	| section.cmp	| section.cov	| section.ctl	| section.ctl	| section.ctl	| section.ctl	| section.exh	| section.exl	| section.exl	| section.exl	| section.fin	| section.fin	| section.flo	| section.inc	| section.leg	| section.mda	| section.mkt	| section.nts	| section.off	| section.oth	| section.own	| section.p1k	| section.p2k	| section.p3k	| section.p4k	| section.paf	| section.prp	| section.rel	| section.rfa	| section.rsk	| section.sel	| section.toc	| section.usc	| section.vot	| text">
		<div class="&simpleContentBlockClass; &preformattedTextClass;">
			<xsl:call-template name="PreformattedTextCleaner" />
		</div>
	</xsl:template>

	<!-- Inversion of control from PreformattedTextCleaner -->
	<xsl:template match="text()" priority="1">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="notPreformatted" select="false()" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
