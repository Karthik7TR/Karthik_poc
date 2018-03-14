<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
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

	<xsl:variable name="IsAdminAnalyticalOther" select="contains('|w_3rd_fhahb|w_3rd_fhahl|w_3rd_fhfaag|w_3rd_fhfaar|w_3rd_fhfabem|w_3rd_metarsmn|w_3rd_enfman|w_3rd_nhtsa|w_3rd_oshafom|w_3rd_ssahndbk|w_3rd_ssapomsk|w_3rd_faman|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="IsNASDAQAlert" select="contains('|w_3rd_nasdaqal|', concat('|', /Document/document-data/collection, '|'))"/>
  <xsl:variable name="IsICC" select="contains('|w_3rd_iccmbc|', concat('|', /Document/document-data/collection, '|'))"/>
	
	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType" select="'&contentTypeAnalyticalEaganProductClass;'"/>
			<xsl:with-param name="displayPublisherLogo" select="true()"/>
			<xsl:with-param name="citationText">
				<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite"/>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="md.cites" priority="2">
		<xsl:call-template name="getCitation" />
	</xsl:template>

	<xsl:template match="doc.title[last()]" priority="1">
		<xsl:choose>
			<xsl:when test="/Document/document-data/collection = 'w_3rd_enfman'">				
					<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
		<xsl:call-template name="titleBlock"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="section.body//headtext[contains('|w_3rd_fhfabem|', concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">				
			<xsl:apply-templates/>
			<br/>
			<br/>
		</div>		
	</xsl:template>

	<xsl:template match="doc.title[last()]/head/headtext[contains('|w_3rd_enfman|', concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="prop.head[not(following::doc.title)][last()]" priority="1">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- For these collections, set indentation by counting paragraph nesting level. -->
	<xsl:template match="para[contains('|w_3rd_naicf|w_3rd_naicmrkt|w_3rd_naicstlw|w_3rd_naicjir|w_3rd_naicmodk|w_3rd_naicmod2|w_3rd_naicm|w_3rd_thomps2k|w_3rd_fitaxic|w_3rd_thomps17|w_3rd_cjerdis|w_3rd_fhahb|w_3rd_fhahl|w_3rd_fhfaag|w_3rd_fhfaar|w_3rd_fhfabem|w_3rd_metarsmn|w_3rd_enfman|w_3rd_nhtsa|w_3rd_oshafom|w_3rd_ssahndbk|w_3rd_ssapomsk|w_3rd_faman|w_3rd_iccmbc|w_3rd_imjbtext|w_3rd_ajvct1|', concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<xsl:call-template name="nestedParas"/>
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

	<xsl:template match="date" priority="1">
		<xsl:choose>
			<xsl:when test="name(following-sibling::date[1]) = 'date'">
				<xsl:call-template name="dateBlock">
					<xsl:with-param name="extraClasses" select="'&centerClass;'" />
				</xsl:call-template>
				<br />
			</xsl:when>
			<xsl:when test="/Document/document-data/collection = 'w_3rd_nasdaq' and not(ancestor::section)">
				<div>
					<xsl:text>&#160;</xsl:text>
				</div>
				<div>
					<xsl:apply-templates/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="dateBlock" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Supress starPage metadata if the content is Admin analytical other -->
	<xsl:template name="StarPageMetadata" match="testStarpageMetadata" priority="3">
		<xsl:if test="not($IsAdminAnalyticalOther = true() or $IsNASDAQAlert = true() or $IsICC = true())">
			<xsl:variable name="jsonObject">
				<xsl:text>{ "&citationMapJsonPropertyName;": { </xsl:text>
				<xsl:for-each select="$displayableCitesForStarPaging">
					<xsl:value-of select="concat('&quot;', @ID, '&quot;:&quot;')" />
					<!-- Apply templates to the md.primarycite|md.parallelcite grandparent (rather than doing a
				     value-of on ".") to account for adjusted cites, jurisdictions, labels, etc. -->
					<xsl:apply-templates select="parent::node()/parent::node()" />
					<xsl:text>"</xsl:text>
					<xsl:if test="position() != last()">
						<xsl:text>, </xsl:text>
					</xsl:if>
				</xsl:for-each>
				<xsl:text> }</xsl:text>
				<xsl:if test="$displayWestlawCiteOnly = true()">
					<xsl:text>, &westlawCiteOnlyJsonProperty;</xsl:text>
				</xsl:if>
				<xsl:text> }</xsl:text>
			</xsl:variable>
			<xsl:if test="$IncludeCopyWithRefLinks = true()">
				<xsl:call-template name="generateCopyWithReferenceLink" />
			</xsl:if>
			<input type="hidden" id="&starPageMetadataId;" value="{$jsonObject}" alt="&metadataAltText;" />
		</xsl:if>
	</xsl:template>

	<!-- Suppress the normal "*.cites" elements -->
	<xsl:template match="cmd.cites" priority ="1"/>	

	<xsl:template match="toc.headings.block | starpage.anchor" priority="1"/>

</xsl:stylesheet>