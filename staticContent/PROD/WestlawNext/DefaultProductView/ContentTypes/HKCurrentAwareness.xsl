<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="primarycite" select="Document/n-docbody/header/identifier" />
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeBinClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:apply-templates select="n-docbody/header/created" />
			<xsl:apply-templates select="n-docbody/header/document_citation" />
			<div class="&binJournalClass; &centerClass;">
				<xsl:text>&hkLatestLegalRegulatoryDevelopments;</xsl:text>
			</div>
			<xsl:apply-templates select="n-docbody/data/entry_type" />
			<xsl:apply-templates select="n-docbody/data/title" />

			<xsl:choose>
				<xsl:when test="n-docbody/data/contributors/author" >
					<xsl:apply-templates select="n-docbody/data/contributors/author" />
				</xsl:when>
				<xsl:otherwise>
					<div class="&paratextMainClass;">&#160;</div>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:apply-templates select="n-docbody/data/subjects" />
			<xsl:apply-templates select="n-docbody/data/keywords" />

			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<xsl:apply-templates select="n-docbody/data/abstract" />

			<xsl:if test="normalize-space(n-docbody/data/url)">
				<xsl:apply-templates select="n-docbody/data/url" />
			</xsl:if>

			<xsl:if test="normalize-space(n-docbody/data/series)">
				<xsl:apply-templates select="n-docbody/data/series" />
			</xsl:if>

			<xsl:if test="normalize-space(n-docbody/data/publisher)">
				<xsl:apply-templates select="n-docbody/data/publisher" />
			</xsl:if>

			<xsl:if test="normalize-space(n-docbody/data/isbn)">
				<xsl:apply-templates select="n-docbody/data/isbn" />
			</xsl:if>

			<xsl:if test="normalize-space(n-docbody/data/price)">
				<xsl:apply-templates select="n-docbody/data/price" />
			</xsl:if>

			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/header/copyright" />

			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="$primarycite" />
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

		</div>
	</xsl:template>


	<xsl:template match="header/created">
		<div class="&citesClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="header/document_citation">
		<div class="&binJournalClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="data/entry_type">
		<div class="&binJournalClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="data/title">
		<div class="&titleClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="data/contributors/author">
		<div class="&centerClass; &binAuthorClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="data/subjects/subject_heading">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="data/subjects/main_subject">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="data/keywords">
		<div>
			<xsl:apply-templates/>
		</div>
		<div class="&paratextMainClass;">&#160;</div>
	</xsl:template>

	<xsl:template match="data/abstract">
		<div class="&textClass;">
			<div class="&paraMainClass;">
				<div class="&paratextMainClass;">
					<xsl:apply-templates/>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="data/url">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="copyright">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>