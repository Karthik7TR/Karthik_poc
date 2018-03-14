<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 4/18/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
	<xsl:include href="CanadianCaselaw.xsl"/>

	<xsl:template match="Document" priority="1">
		<xsl:variable name="content">

			<!-- Document Sections (Counsel|Abridgment Classification|Headnote|Annotation|Table of Authorities|Opinion|Disposition -->
			<!-- Counsel -->
			<xsl:if test="/Document/n-docbody/decision/content.block/attorney.block">
				<div>
					<a href="#&crswCounselId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswCounselLabelKey;', '&crswCounselLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<!-- Abridgment Classification -->
			<xsl:if test="/Document/n-docbody/decision/content.block/headnote.block/digest.wrapper and $IncludeAbridgmentClassification">
				<div>
					<a href="#&crswAbridgmentHeaderId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswAbridgmentClassificationLabelKey;', '&crswAbridgmentClassificationLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<!-- Headnote -->
			<xsl:if test="/Document/n-docbody/decision/content.block/headnote.block/headnote.wrapper and $IncludeNonWestHeadnotes">
				<div>
					<a href="#&crswHeadnoteHeaderId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswHeadnotesTextKey;', '&crswHeadnotesText;')"/>
					</a>
				</div>
			</xsl:if>

			<!-- Annotation -->
			<xsl:if test="/Document/n-docbody/decision/content.block/editorial.note.block.wrapper and $IncludeCaseAnnotation">
				<div>
					<a href="#&crswAnnotationHeaderId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswAnnotationLabelKey;', '&crswAnnotationLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<!-- Table of Authorities -->
			<xsl:if test="/Document/n-docbody/decision/content.block/reflists.wrapper/table.of.cases.block or /Document/n-docbody/decision/content.block/reflists.wrapper//code.reference.block">
				<div>
					<a href="#&crswTableOfAuthoritiesHeaderId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswAuthoritiesLabelKey;', '&crswAuthoritiesLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<!-- Opinion -->
			<xsl:if test="/Document/n-docbody/decision/content.block/opinion.block">
				<div>
					<a href="#&crswOpinionId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswOpinionLabelKey;', '&crswOpinionLabel;')"/>
					</a>
				</div>
			</xsl:if>

			<!-- Disposition -->
			<xsl:if test="/Document/n-docbody/decision/content.block/order.block">
				<div>
					<a href="#&crswDispositionId;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswDispositionLabelKey;', '&crswDispositionLabel;')"/>
					</a>
				</div>
			</xsl:if>

		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:copy-of select="$content"/>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>