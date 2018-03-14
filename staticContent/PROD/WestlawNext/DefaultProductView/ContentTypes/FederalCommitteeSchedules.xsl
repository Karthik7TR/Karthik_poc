<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:call-template name="HeaderCitation" />			
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="prelim.head[last()]">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="product.name | source | cong.session.no">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template name="HeaderCitation">
		<xsl:variable name="citation">		
			<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.first.line.cite"/>
		</xsl:variable>
		<xsl:if test="string-length($citation) &gt; 0">
			<div class="&centerClass;">
				<xsl:value-of	select="$citation"	/>
				<br/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="member">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="branch | committee | subcommittee | hearing.date | hearing.time | subcommittee.info | location | title | remarks | related.billtext">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="meeting.summary">
		<br />
		<div class="&paraMainClass;">
			<xsl:value-of select="'&meetingSummaryLabel;'"/>
			<br />
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="panels">
		<br />
		<div class="&paraMainClass;">
			<xsl:value-of select="'&panelLabel;'"/>
			<br />
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
