<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="PreformattedTextCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- xsl:template match="Document" -->
	<xsl:template match="n-docbody">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeComplianceAdvisorOdenSummaryClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="summary/division">
		<hr />
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class" />
		</xsl:variable>
			<div class="&simpleContentBlockClass; {$xmlBasedClassName}">
				<xsl:if test="string-length(@id | @ID) &gt; 0">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates />
			</div>
	</xsl:template>

	<xsl:template match="division/division">
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class" />
		</xsl:variable>
		<div class="&simpleContentBlockClass; {$xmlBasedClassName}">
			<xsl:if test="@id | @ID">
				<xsl:attribute name="id">&internalLinkIdPrefix;<xsl:value-of select="@id | @ID"/></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="paratext" priority="1">
		<div class="&simpleContentBlockClass; &preformattedTextClass;">
			<xsl:call-template name="PreformattedTextCleaner" />
		</div>
	</xsl:template>
	
	<!-- Inversion of control from PreformattedTextCleaner -->
	<xsl:template match="paratext//text()" priority="1">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="notPreformatted" select="false()" />
		</xsl:call-template>
	</xsl:template>
	
  <xsl:template match="cmd.cites" priority="1">
    <div class="&citesClass;">
     <xsl:if test="cmd.first.line.cite">
			 <xsl:apply-templates select="cmd.first.line.cite"/>
     </xsl:if>
    <xsl:if test="cmd.second.line.cite">
			 <xsl:apply-templates select="cmd.second.line.cite"/>
     </xsl:if>
     <xsl:if test="cmd.third.line.cite">
			 <xsl:apply-templates select="cmd.third.line.cite"/>
     </xsl:if>
    </div>
  </xsl:template>

	<xsl:template match="cmd.first.line.cite | cmd.second.line.cite | cmd.third.line.cite" priority="1">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="pub.date" priority="1">
		<xsl:choose>
			<xsl:when test="string-length(.) &gt; 13 and number(.) != 'NaN'">				
				<xsl:text>&publicationDate;</xsl:text>
				<xsl:apply-templates select="//md.starteffective"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Suppress these specific elements. -->
	<xsl:template match="doc.number" priority="1" />
	<xsl:template match="topic.code" priority="1" />
	<xsl:template match="md.doc.family.uuid" priority="1" />

	<xsl:template match="title" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

</xsl:stylesheet>