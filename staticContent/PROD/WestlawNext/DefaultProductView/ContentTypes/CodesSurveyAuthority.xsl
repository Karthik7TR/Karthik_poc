<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="super.survey">
		<xsl:call-template name="renderDocumentHeader"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="renderDocumentHeader">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="content.metadata.block" mode="docHeader"/>
			<xsl:apply-templates select="prelim" mode="docHeader"/>
			<xsl:apply-templates select="title" mode="docHeader"/>
			<xsl:apply-templates select="prelim/currency" mode="docHeader"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
    
    <xsl:template match="content.metadata.block"/>
    <xsl:template match="prelim"/>
    <xsl:template match="title" priority="1"/>
    <xsl:template match="prelim/currency"/>

    <xsl:template match="content.metadata.block" mode="docHeader">
        <xsl:apply-templates select=".//cmd.cites"/>
    </xsl:template>
        
	<xsl:template match="prelim" mode="docHeader">
		<xsl:variable name="database">
			<xsl:apply-templates select="database.name"/>
		</xsl:variable>
		<xsl:variable name="topic">
			<xsl:apply-templates select="topic"/>
		</xsl:variable>
		<xsl:variable name="subtopic">
			<xsl:apply-templates select="subtopic"/>
		</xsl:variable>
		<div class="&simpleContentBlockClass; &prelimClass;">
			<div class="&centerClass;">
				<xsl:if test="string-length($database) &gt; 0">
					<xsl:copy-of select="$database"/>
				</xsl:if>
				<xsl:if test="string-length($topic) &gt; 0">
					<xsl:if test="string-length($database) &gt; 0">
						<xsl:text>: </xsl:text>
					</xsl:if>
					<xsl:copy-of select="$topic"/>
				</xsl:if>
				<xsl:if test="string-length($subtopic) &gt; 0">
					<xsl:if test="string-length($database) &gt; 0 or string-length($topic) &gt; 0">
						<xsl:text>: </xsl:text>
					</xsl:if>
					<xsl:copy-of select="$subtopic"/>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

    <xsl:template match="title" mode="docHeader">
        <xsl:call-template name="titleBlock"/>
    </xsl:template>
         
	<xsl:template match="prelim/currency" mode="docHeader">
		<div class="&dateClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
    
        
	<xsl:template match="database.name | topic | subtopic">
		<xsl:apply-templates />
	</xsl:template>
   

	<xsl:template match="summary">
		<div class="&simpleContentBlockClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="summary//paratext" priority="1">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="suppressLabel" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="summary/para/paratext[following-sibling::node()[1]/self::list]" priority="1">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="jurisdiction">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="key" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="division/topic.key.hierachy/topic.key/topic.key/topic.key.ref/key/n-private-char" priority="1">
		<xsl:choose>
			<xsl:when test="@charName = 'TLRkey'">
				<xsl:text> k </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="nonMetadataNPrivateChars" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="analysis" priority="1">
		<xsl:call-template name="Toc">
			<xsl:with-param name="rootClass" select="'&analysisClass;'"/>
		</xsl:call-template>
		<ol class="&tocMainClass;">
			<xsl:apply-templates select="../division" mode="listItem"/>
		</ol>
	</xsl:template>

	<xsl:template match="division" mode="listItem">
		<li class="&tocHeading;">
			<xsl:variable name="xmlBasedClassName">
				<xsl:call-template name="escape-to-class" />
			</xsl:variable>
			<div class="&simpleContentBlockClass; {$xmlBasedClassName}">
				<xsl:if test="@id | @ID">
					<xsl:attribute name="id">&internalLinkIdPrefix;<xsl:value-of select="@id | @ID"/></xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="division">
						<xsl:apply-templates select="head"/>
						<xsl:apply-templates select="para"/>
						<ol class="&tocMainClass;">
							<xsl:apply-templates select="division" mode="listItem"/>
						</ol>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</li>
	</xsl:template>
	
	<!-- Avoid duplicate display -->
	<xsl:template match="division[@level = 1]" />

</xsl:stylesheet>
