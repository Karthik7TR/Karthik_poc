<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n Completed As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
	<xsl:include href="CaselawNRS.xsl"/>

	<xsl:template match="Document" priority="1">
		<xsl:variable name="content">
			<xsl:if test="/Document/n-docbody/decision/content.block/synopsis[.//synopsis.background]">
				<div>
					<a href="#&synopsisId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&synopsisHeadingKey;', '&synopsisHeading;')"/>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/decision/content.block/headnote.block and not($HeadnoteDisplayOption = '&noKeyNumbers;')">
				<div>
					<a href="#&headnoteHeaderId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&westHeadnotesTextKey;', '&westHeadnotesText;')"/>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/decision/content.block/keysummary.block and not($HeadnoteDisplayOption = '&noKeyNumbers;')">
				<div>
					<a href="#&headnoteHeaderId;">
						<xsl:text>&westKeySummaryText;</xsl:text>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="not($IsIpad = 'true')">
				<xsl:if test="/Document/n-docbody/decision/content.block/construed.terms.block">
					<div>
						<a href="#&construedTermsId;">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&construedTermsLabelKey;', '&construedTermsBlockLabel;')"/>
						</a>
					</div>
				</xsl:if>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/decision/content.block/attorney.block">
				<div>
					<a href="#&attorneysAndLawFirmsId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&attorneyBlockLabelKey;', '&attorneyBlockLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<xsl:if test="/Document/n-docbody/decision/content.block/opinion.block and $IsNonPrecedentialCaseOfPennsylvania = 'false'">
				<div>
					<a href="#&opinionId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&opinionLabelKey;', '&opinionLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<xsl:if test="/Document/n-docbody/decision/content.block/opinion.block/opinion.block.body/opinion.concurrance">
				<div>
					<a href="#&concurranceOpinionID;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&concurranceOpinionLabelKey;', '&concurranceOpinionLabel;')"/>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/decision/content.block/opinion.block/opinion.block.body/opinion.dissent">
				<div>
					<a href="#&dissentOpinionID;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&dissentOpinionLabelKey;', '&dissentOpinionLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<xsl:if test="/Document/n-docbody/decision/content.block/opinion.block/opinion.block.body/opinion.cipdip">
				<div>
					<a href="#&cipdipOpinionID;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&cipdipOpinionLabelKey;', '&cipdipOpinionLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<xsl:call-template name="CitationsOutlineItem"/>

		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:copy-of select="$content"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CitationsOutlineItem">
		<xsl:if test="string-length($parallelCites) &gt; 0">
			<div>
				<a href="#&parallelCitationsId;">
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&parallelCitationsHeaderKey;', '&parallelCitationsHeader;')"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>