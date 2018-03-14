<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Address.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Mark Nordstrom - Bug #735738 - 2/13/2015 - sample document DRUGDEX-EV 2433 (Guid=I38cf2e2290af11dba168de33dfaf5627) -->
	<!-- The source XML has whitespace in the footnotes, online display is fine; in delivery the xhtml2fo.xsl creates a -->
	<!-- column for this whitespace node and footnotes do not delivery correctly. So, I am stripping the whitespace here. -->
	<xsl:strip-space elements="footnote"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:variable name="contentTypeClass">
						<xsl:text>&contentTypeMedLitMicromedexClass;</xsl:text>
						<xsl:if test="n-docbody/drug.nonprescrip.reference">
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&contentTypeMedLitMicromedexNonPrescripDrugsClass;</xsl:text>
						</xsl:if>
						<xsl:if test="n-docbody/drug.prescrip.reference">
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&contentTypeMedLitMicromedexPrescripDrugsClass;</xsl:text>
						</xsl:if>
						<xsl:if test="n-docbody/drug.evaluation | n-docbody/drug.consult">
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&contentTypeMedLitMicromedexDrugDexEvalsClass;</xsl:text>
						</xsl:if>
						<xsl:if test="n-docbody/drug.summary">
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&contentTypeMedLitMicromedexDrugPointClass;</xsl:text>
						</xsl:if>
					</xsl:variable>
					<xsl:value-of select="$contentTypeClass"/>
				</xsl:with-param>
			</xsl:call-template>
		
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="list.item/unit/head" priority="1">
		<xsl:call-template name="head" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="list.item/label.designator[following-sibling::para]" />
	<xsl:template match="para[parent::list.item and preceding-sibling::label.designator]" priority="2">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="preceding-sibling::label.designator" mode="labelDesignatorInList" />
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="label.designator" mode="labelDesignatorInList">
		<span class="&labelClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="list[ancestor::list]/list.item/para | list[ancestor::list]/list.item/para/paratext | list/list.item[not(child::list)]/para | list/list.item[not(child::list)]/para/paratext" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="footnote.block | endnote.block" priority="1">
		<xsl:call-template name="footnoteBlock">
			<xsl:with-param name="suppressHeading" select="true()"/>
			<xsl:with-param name="id">
				<xsl:if test="@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="message.block/include.copyright" priority="1">
		<div class="&copyrightClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="drug.summary//list/list.item[para/paratext/bold]" priority="1">
		<li class="&listHeadClass;">
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[not(self::text())][1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[not(self::text())][1]" />
			</xsl:if>
		</li>
	</xsl:template>
	
	<!-- DrugDex Evals need special rules to display correctly -->

	<xsl:template match="drug.evaluation//overview.body//para | drug.evaluation//section//para" priority="1">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="drug.evaluation//overview.body//para/head | drug.evaluation//section//para/head" priority="1">
		<strong>
			<xsl:apply-templates />
			<xsl:text>)<![CDATA[ ]]></xsl:text>
		</strong>
	</xsl:template>
	
	<xsl:template match="drug.evaluation//overview.body//paratext | drug.evaluation//section//paratext" priority="1">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="para/label.designator | para/head/label.designator | para/label.name | para/head/label.name" priority="1">
		<xsl:apply-templates />		
	</xsl:template>

	<xsl:template match="para/head"/>

	<xsl:template match="title.block" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="trademark" priority="1">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="contents">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Suppress elements that were not present in Westlaw Classic and were bleeding through into WLN. -->
	<xsl:template match="tcc.block/label | tcc.block/tcc.code | gcc.block/label | gcc.block/gcc.code | gfc.block/label | gfc.block/gfc.code | pkg.desc.block" />

	<xsl:template match="exceptional.drug.block/label">
		<strong>
			<xsl:call-template name="wrapWithSpan" />
		</strong>
	</xsl:template>

</xsl:stylesheet>
