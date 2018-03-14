<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Entry point-->
	<xsl:template match="Document">

		<div id="&documentClass;">

			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswProgressOfBills;'"/>
			</xsl:call-template>

			<!-- Document prelim -->
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&documentHeadClass;'"/>
				<xsl:with-param name="contents">
					<xsl:apply-templates select="n-docbody/progressofbills/bill_info/bill_citation" />
					<xsl:apply-templates select="n-docbody/progressofbills/bill_info/bill_headings/bill_head1" />
					<xsl:apply-templates select="n-docbody/progressofbills/bill_info/bill_headings/bill_head2" />
					<xsl:apply-templates select="n-docbody/progressofbills/bill_info/bill_headings/bill_head3" />
					<xsl:apply-templates select="n-docbody/progressofbills/document_block/currency" mode="currencyLink"/>
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&paraMainClass;'"  />
				<xsl:with-param name="contents">
					<xsl:apply-templates select="n-docbody/progressofbills/includeinfo_block" />
					<xsl:apply-templates select="n-docbody/progressofbills/document_block" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="EndOfDocument" />

		</div>
	</xsl:template>

	<xsl:template match ="bill_citation">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &crswTopMargin; &alignHorizontalCenterClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- bill_headings/-->
	<xsl:template match="bill_headings/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &crswTopMargin; &alignHorizontalCenterClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Document title -->
	<xsl:template match="bill_headings/bill_head3">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &titleClass; &crswTopMargin; &alignHorizontalCenterClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="currency" mode="currencyLink">
		<xsl:if test="/Document/document-data/versioned = 'False' or
								 (/Document/document-data/versioned = 'True' and 
									/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.starteffective and 
									/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)">
			<div class="&currentnessClass; &paraMainClass;">
				<a>
					<xsl:attribute name="class">
						<xsl:text>&internalLinkClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="concat('#','&internalLinkIdPrefix;')"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswCurrencyKey;', '&crswCurrency;')"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="currency">
		<xsl:if test="/Document/document-data/versioned = 'False' or
								 (/Document/document-data/versioned = 'True' and 
									/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.starteffective and 
									/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)">
			<xsl:variable name="content">
				<xsl:apply-templates />
			</xsl:variable>
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="id" select="'&internalLinkIdPrefix;'"/>
				<xsl:with-param name="class" select="'&paraMainClass; &crswTopMargin;'" />
				<xsl:with-param name="contents" select="$content"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
