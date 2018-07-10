﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody" mode="header"/>
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="doc" mode="header">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select="//st | //rccl" mode="head"/>
		</div>
		<xsl:apply-templates select="*[not(self::cpmxa or self::mxda)]" />
		<xsl:apply-templates select="mxda | cpmxa" />
	</xsl:template>

	<xsl:template match="md.cites" priority="3">
		<div class="&citesClass;">
			<div>
				<xsl:choose>
					<xsl:when test="md.second.line.cite">
						<xsl:apply-templates select="md.second.line.cite"/>
					</xsl:when>
					<xsl:when test="md.third.line.cite">
						<xsl:apply-templates select="md.third.line.cite"/>
					</xsl:when>
					<xsl:when test="md.first.line.cite">
						<xsl:apply-templates select="md.first.line.cite"/>
					</xsl:when>
				</xsl:choose>
			</div>
			<div>
				<xsl:apply-templates select="/Document/n-docbody/doc/ce[1]//d7/text()"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="til | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | snl | hca | hcb | topic | agncy | catg | ht | voln | ht2" priority="1">
		<xsl:apply-templates />
		<xsl:if test="not(following-sibling::til or following-sibling::hg0 or following-sibling::hg1 or following-sibling::hg2 or following-sibling::hg3 or following-sibling::hg4 or following-sibling::hg5 or following-sibling::hg6 or following-sibling::hg7 or following-sibling::hg8 or following-sibling::hg9 or following-sibling::hg10 or following-sibling::hg11 or following-sibling::hg12 or following-sibling::hg13 or following-sibling::hg14 or following-sibling::hg15 or following-sibling::hg16 or following-sibling::hg17 or following-sibling::hg18 or following-sibling::hg19 or following-sibling::snl or following-sibling::hca or following-sibling::hcb or following-sibling::topic or following-sibling::agncy or following-sibling::catg or following-sibling::ht or following-sibling::voln or following-sibling::ht2)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!--Some of these documents have dockets contained in the doc element, so we need to differentiate between this template and the header match-->
	<xsl:template match="doc">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="smp[d6]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--Some documents have another node inside a sub element causing invalid XHTML to be produced-->
	<xsl:template match="sub[node()]">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="pcite[@pg]">
		<xsl:text>*</xsl:text>
		<xsl:value-of select="@pg"/>
		<xsl:text>&nbsp;</xsl:text>
	</xsl:template>

	<xsl:template match="rc | cr | docid.gen | crdms.gen | adt.gen | ca.stbl | cop"/>

	<xsl:template match="st | rccl"/>

	<xsl:template match="st | rccl" mode="head">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- footer citation -->
	<xsl:template name="FooterCitation">
		<div class="&citationClass;">
			<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.display.primarycite"/>
			<br/>
			<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.display.parallelcite"/>
		</div>
	</xsl:template>


	<!-- beginning of section / beginning of paragraph-->
	<xsl:template match="eop[not(preceding-sibling::hg1)]  | eop[preceding-sibling::tl]">
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- suppress starpage-->
	<xsl:template match="n-docbody//starpage.anchor" priority="4"/>


</xsl:stylesheet>