<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Document.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType" select="'&contentTypeAnalyticalEaganProductClass;'"/>
			<xsl:with-param name="displayPublisherLogo" select="true()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<xsl:call-template name="getCitation" />
	</xsl:template>

	<xsl:template match="doc.title[last()]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="prop.head[not(following::doc.title)][last()]" priority="1">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="para[contains('|w_3rd_naicf|w_3rd_naicmrkt|w_3rd_naicstlw|w_3rd_naicjir|w_3rd_naicmodk|w_3rd_naicmod2|w_3rd_naicm|' , concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<xsl:choose>
			<xsl:when test="parent::para">
				<xsl:variable name="indent" select="count(ancestor::para)"/>
				<div>
					<xsl:attribute name="style">
						<xsl:text>padding-left:</xsl:text>
						<xsl:value-of select="$indent" />
						<xsl:text>em;</xsl:text>
					</xsl:attribute>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;" >
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<xsl:template match="title.block[not(//doc.title or //prop.head)]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="include.copyright" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	
	<!--Tables with large number of columns and long headings in NAIC 
	TO DO: Find a better way to fix this -->
	<xsl:template match="table[/Document/document-data/collection = 'w_3rd_naicf']" priority="2">
		<xsl:attribute name="style">
			<xsl:text>width:105%;</xsl:text>
		</xsl:attribute>
		<xsl:apply-templates/>
	</xsl:template>
	
	<!--Fix table spacing issue in NAIC  -->
	<xsl:template match="tgroup[contains('|w_3rd_naicf|w_3rd_naicmrkt|w_3rd_naicstlw|w_3rd_naicjir|w_3rd_naicmodk|w_3rd_naicmod2|w_3rd_naicm|' , concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<xsl:call-template name ="TGroupTemplate">
			<xsl:with-param name ="checkNoColWidthExists" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Modified footnote processing for proper display and linking-->
	<xsl:template match="footnote/para/paratext[ancestor::footnote.block and /Document/document-data/collection = 'w_3rd_gmarbrl']" priority="3">
		<xsl:call-template name="footnotenumberextractionandlink">
			<xsl:with-param name="footnotenumber">
				<xsl:value-of select="label.designator"/>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="RenderFootnoteBodyMarkup">
			<xsl:with-param name="contents">
				<xsl:apply-templates select="text()[not(parent::label.designator)]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="footnotenumberextractionandlink">
		<xsl:param name="footnotenumber"/>
		<xsl:variable name="refNumberOutputText">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="$footnotenumber" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="generateLinkBackToFootnoteReference">
			<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
			<xsl:with-param name="footnoteId" select="../../@ID | ../../@id" />
		</xsl:call-template>
	</xsl:template>

	<!-- Suppress the normal "*.cites" elements -->
	<xsl:template match="cmd.cites" priority ="1"/>	

	<xsl:template match="toc.headings.block | starpage.anchor" priority="1"/>	

</xsl:stylesheet>