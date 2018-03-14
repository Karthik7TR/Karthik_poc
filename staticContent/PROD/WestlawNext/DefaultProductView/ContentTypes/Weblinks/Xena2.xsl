<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="dpa0[d6] | dpa1[d6] | dpa2[d6]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="spa[not(name(preceding-sibling::node()[not(self::bop | self::eop)][1]) = 'spa')]">
			<div class="&paraMainClass;">
				<xsl:apply-templates />
				<br/>
			</div>
	</xsl:template>
	
	<xsl:template match="rc.gen">
		<xsl:value-of select="d6"/>	
	</xsl:template>

	<!-- restrict the solution of bug 360607 to this collection only to avoid unintented changes to other collections using the same stylesheet -->
	<xsl:template match="tgroup[/Document/document-data/collection = 'w_3rd_ftxuscold1']" priority="1">
		<xsl:call-template name ="TGroupTemplate">
			<xsl:with-param name ="checkNoColWidthExists" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="starpage.anchor[contains('|w_3rd_stfdch|w_3rd_stfdch2|w_3rd_stfdch5|w_codes_congalla|w_codes_congallb|w_codes_congallc|w_codes_congalld|w_codes_congalle|w_codes_congallf|w_3rd_nflxaga1|w_3rd_nflxmil1|w_codes_apbankr78|', concat('|', /Document/document-data/collection, '|'))]" priority="2" />

	<!-- for USCCAN Legislative History Citation should display (Leg-Hist), hence overriding the Citation -->
	<xsl:template match="md.cites[/Document/document-data/collection = 'w_codes_leghistory']" priority="3">
		<div class="&citesClass;">
			<xsl:apply-templates select ="md.expandedcite"/>
		</div>
	</xsl:template>

	<xsl:template match="ti" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
		<xsl:if test="not(following-sibling::ti or following-sibling::ti2 or following-sibling::til or following-sibling::hg0 or following-sibling::hg1 or following-sibling::hg2 or following-sibling::hg3 or following-sibling::hg4 or following-sibling::hg5 or following-sibling::hg6 or following-sibling::hg7 or following-sibling::hg8 or following-sibling::hg9 or following-sibling::hg10 or following-sibling::hg11 or following-sibling::hg12 or following-sibling::hg13 or following-sibling::hg14 or following-sibling::hg15 or following-sibling::hg16 or following-sibling::hg17 or following-sibling::hg18 or following-sibling::hg19 or following-sibling::snl or following-sibling::srnl or following-sibling::hc2)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!-- overriding global suppression on include.copyright -->
	<xsl:template match="include.copyright[@n-include_collection = 'w_wlnv_msg']" priority="1">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="." />
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
