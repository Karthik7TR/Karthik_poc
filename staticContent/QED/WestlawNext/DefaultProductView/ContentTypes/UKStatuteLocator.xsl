<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="primarycite" select="Document/n-docbody/document/metadata.block/md.identifiers/md.cites/md.primarycite" />
	<!-- Don't render these elements -->
	<xsl:template match="map|short-headings|copyright-message" />

	<!-- Document Display -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&ukStatLocClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<!-- Render the Content - This consists of:
							A) Top Citation & Headings
							B) In-document links to further down
							C) Document Sections -->
			<xsl:apply-templates select="n-docbody"/>

			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/copyright-message" mode="endOfDocumentCopyright"/>


			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="$primarycite" />
			</div>

			<!--		D) End of Document Text -->
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<!-- **********************************************************************************************
	     * Section (A) - Top Document Citation and Heading                                            *
		 ********************************************************************************************** -->

	<!-- Document Citation at top of document -->
	<xsl:template match="metadata.block">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates select="descendant::md.parallelcite" />
		</div>
	</xsl:template>

	<!-- Centered Headings -->
	<xsl:template match="headings">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates />
			<xsl:apply-templates select="ancestor::n-docbody/copyright-message" mode="header" />
		</div>

		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- From Web2 - bnordland bu #28573 - place line breaks between headings -->
	<xsl:template match="heading">
		<xsl:if test="not(@rank = '1')">
			<br/>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="copyright-message" mode="header">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- **********************************************************************************************
	     * Section (B) - In Document Links to Further Down                                            *
		 ********************************************************************************************** -->

	<xsl:template match="contents">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="content">
		<!-- From Web2 - We only want to render content items that have a link directly in them -->
		<xsl:apply-templates select="link" />
	</xsl:template>

	<!-- Links in the table of contents-->
	<xsl:template match="link[parent::content]">
		<div>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:text>#</xsl:text>
					<xsl:value-of select="@tanchor"/>
				</xsl:attribute>
				<xsl:apply-templates />
			</xsl:element>
		</div>
	</xsl:template>


	<!-- **********************************************************************************************
	     * Section (C) - Sections                                                                     *
		 ********************************************************************************************** -->

	<xsl:template match="subsection">
		<xsl:apply-templates select="title" />
		<table width="100%">
			<xsl:apply-templates select="entry|case_entry" />
		</table>
	</xsl:template>

	<xsl:template match="title">
		<div class="&headtextClass;">
			<xsl:element name="div">
				<xsl:if test="@anchor">
					<!-- Render the anchor reference -->
					<xsl:attribute name="id">
						<xsl:value-of select="@anchor"/>
					</xsl:attribute>
				</xsl:if>
				<!-- Center subsection titles to differentiate them from section titles -->
				<xsl:if test="parent::subsection">
					<xsl:attribute name="class">
						<xsl:text>&alignHorizontalCenterClass;</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<!--<xsl:if test="not(parent::subsection) or not(name(parent::subsection/preceding-sibling::*[1]) = 'title')">
					<br/>
					<br/>
				</xsl:if>-->
				<xsl:apply-templates />
			</xsl:element>
		</div>
	</xsl:template>

	<xsl:template match="entry[parent::subsection]|case_entry[parent::subsection]">
		<xsl:choose>
			<xsl:when test="definition">
				<tr class="&alignVerticalTopClass;">
					<td>
						<xsl:apply-templates select="definition" />
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<!--<xsl:if test="not(preceding-sibling::entry) and child::number">
					<tr>
						<td/>
					</tr>
				</xsl:if>-->
				<tr class="&alignVerticalTopClass;">
					<td class="&ukStatLocLeftColClass;">
						<xsl:apply-templates select="number" />
					</td>
					<td class="&ukStatLocRightColClass;">
						<xsl:apply-templates select="citation|reference" />
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="definition">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="citation">
		<xsl:if test="preceding-sibling::citation">
			<br/>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- <notes> and <reference> elements are used to provide additional information to blocks of text. Place on the next line of text. -->
	<xsl:template match="notes">
		<br/>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="reference">
		<br />
		<xsl:apply-templates />
	</xsl:template>

	<!-- START <definitions> handling -->
	<xsl:template match="definitions">
		<xsl:apply-templates/>
	</xsl:template>
	<!-- END <definitions> handling -->

	<!-- START <versiontable> handling -->
	<!-- This tends to be a Table of Amendments -->
	<xsl:template match="versiontable">
		<table class="&detailsTable; &extraPaddingClass; &ukStatLoc_amendmentsTable;">
			<thead>
				<tr>
					<th class="&ukStatLoc_version;">Version</th>
					<th class="&ukStatLoc_provision;">Provision</th>
					<th class="&ukStatLoc_amendmentNotes;">Amendment Notes</th>
					<th class="&ukStatLoc_effectiveDate;">Effective Date</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="versionentry" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="versionentry">
		<xsl:apply-templates select ="descendant::an-entry"/>
	</xsl:template>

	<!-- From Web2 - Bug 28574 - Add a line space if there are multiple an-entry elements-->
	<xsl:template match ="an-entry">
		<tr>
			<xsl:choose>
				<xsl:when test ="not(preceding-sibling::an-entry)">
					<td class="&ukStatLoc_version;">
						<xsl:apply-templates select="ancestor::versionentry/ve-no" />
					</td>
				</xsl:when>
				<xsl:otherwise>
					<td/>
				</xsl:otherwise>
			</xsl:choose>
			<td class="&ukStatLoc_provision;">
				<xsl:apply-templates select="number" />
			</td>
			<td class="&ukStatLoc_amendmentNotes;">
				<xsl:apply-templates select="citation" />
			</td>
			<td class="&ukStatLoc_effectiveDate;">
				<xsl:apply-templates select="effective-date" />
			</td>
		</tr>
	</xsl:template>

	<!-- END <versiontable> handling -->

	<!-- START <prospective-table> handling -->

	<xsl:template match="prospective-table">
		<table class="&detailsTable; &extraPaddingClass; &ukStatLoc_prospectiveLawTable;">
			<thead>
				<tr>
					<th class="&ukStatLoc_provision;">Provision</th>
					<th class="&ukStatLoc_amendmentNotes;">Amendment Notes</th>
					<th class="&ukStatLoc_effectiveDate;">Effective Date</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="prospective-entry" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="prospective-entry">
		<tr>
			<td class="&ukStatLoc_provision;">
				<xsl:apply-templates select="prospective-provision" />
			</td>
			<td class="&ukStatLoc_amendmentNotes;">
				<xsl:apply-templates select="prospective-amendnotes" />
			</td>
			<td class="&ukStatLoc_effectiveDate;">
				<xsl:apply-templates select="prospective-inforcedate" />
			</td>
		</tr>
	</xsl:template>

	<!-- END <prospective-table> handling -->

	<xsl:template match="emphasis[@type='strong']">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="copyright-message" mode="endOfDocumentCopyright">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>
