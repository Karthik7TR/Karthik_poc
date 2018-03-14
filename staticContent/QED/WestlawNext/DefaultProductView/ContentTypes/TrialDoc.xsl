<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalysisTable.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="StarPagesWithoutRules.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="date.block" priority="1">
		<xsl:call-template name="dateBlock">
			<xsl:with-param name="extraClasses" select="'&centerClass;'" />
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- suppress media for now-->
	<xsl:template match="md.media.block | media.anchor"/>

	<xsl:template match="analytical.reference.block | reference.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;', @ID)" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.block">
		<div class="&centerClass; &simpleContentBlockClass; &docketBlockClass;">
			<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		</div>
	</xsl:template>
	
	<!-- suppress the arbitration.award.range-->
	<xsl:template match="arbitration.award.range"/>

	
 <xsl:template match="arbitration.block/arbitration.case.type |arbitration.block/arbitration.award.amount |arbitration.block/arbitration.award.date">
		<div class="&alignHorizontalLeftClass;">
			<b>
	     <xsl:apply-templates/>
	   </b>	
	  </div>
	</xsl:template>

	<xsl:template match="attorney.block[/Document/document-data/collection = 'w_lt_td_aa']">
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates select="plaintiff.attorney.line"/>
			</b>
		</div>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates select="defendant.attorney.line"/>
			</b>
		</div>
		<xsl:if test="attorney.line">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select="attorney.line"/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:if>
	</xsl:template>
	
<xsl:template match="arbitration.block/arbitrator.name">
	<b>
		<xsl:apply-templates/>
	</b>
	</xsl:template>
	
	<!-- Profession heading before profession -->
	<xsl:template match="profession.block">
		<xsl:if test="normalize-space(.)">
			<div class="&headtextClass;">
				<strong><xsl:text>&tdProfession;</xsl:text></strong>
			</div>
			<div class="&paratextMainClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>
	

</xsl:stylesheet>
