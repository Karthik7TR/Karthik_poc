<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
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
			<xsl:apply-templates select="n-metadata/metadata.block/md.references"/>
			<br/>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:apply-templates select="n-docbody/resume/expert.name" mode="ER" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			<br/>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:apply-templates select="n-docbody/resume/filing.block/filed.in" mode="ER" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Suppress the expert.name text, survey from business wants it first in layout -->
	<xsl:template match="n-docbody/resume/expert.name" />
				  
	<!-- Suppress the copyright text in the document. -->
	<xsl:template match="n-docbody/doc/content.metadata.block/cmd.royalty"/>
	<xsl:template match="n-docbody/doc/content.metadata.block/cmd.identifiers"/>

	<!-- Suppress the filing.block as a whole and only render one piece of data per the survey from business -->
	<xsl:template match="n-docbody/resume/filing.block" />
	<xsl:template match="n-docbody/resume/filing.block/filed.in" mode="ER">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<!-- Match the entire expert.name field and only create a link for the cite.query element within expert.name -->
	<xsl:template match="expert.name" mode="ER">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Added nowrap to styling so that the date column does not wrap in the tables. -->
	<xsl:template match="tbody/row/entry" priority="2">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<td>
			<xsl:variable name="class">
				<xsl:if test="$colposition = '1'">
					<xsl:text>&noWrapClass;</xsl:text>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
				<xsl:with-param name="class" select="$class" />
			</xsl:call-template>
		</td>
	</xsl:template>

	<!-- Profession heading before profession -->
	<xsl:template match="profession.block">
		<xsl:if test="normalize-space(.)">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&tdProfession;</xsl:text>
				</strong>
			</div>
			<div class="&paratextMainClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Style the address block -->
	<xsl:template match="address.block">
		<xsl:call-template name="para" >
			<!-- &paraIndentLeftClass; -->
			<xsl:with-param name="className" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Expert Materials Paragraph -->
	<xsl:template match="para" priority="1">
		<xsl:choose>
			<xsl:when test="parent::profession.block">
				<xsl:call-template name="para" >
					<xsl:with-param name="className" select="'&paraIndentLeftClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="not(parent::para)">
						<xsl:call-template name="para">
							<xsl:with-param name="className" select="'&paraMainClass; &paraIndentLeftClass;'" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="para">
							<xsl:with-param name="className" select="'&paraMainClass;'" />
						</xsl:call-template>
						</xsl:otherwise>
				</xsl:choose>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="resume.block//justified.line" >
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&alignHorizontalLeftClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
