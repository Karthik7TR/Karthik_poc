<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="ProvisionsTable.xsl" />
	<xsl:include href="Date.xsl"/>
	<xsl:include href="FootnotesCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="ContentsForFootnotesSection" select="//footnote" />

	<xsl:template name="headings">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="/Document/n-docbody/document/fulltext_metadata/headings/heading[@rank = 1]"/>

			<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.dates/md.publisheddate[//provisions-table]" />
			<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.dates/md.starteffective[//provisions-table]" />

			<xsl:if test="/Document/n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.document.status">
				<div class="&centeredClass;">
					&pr_status;
					<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.document.status"/>
				</div>
			</xsl:if>

			<xsl:apply-templates select="/Document/n-docbody/document/fulltext_metadata/headings/heading[@rank > 1]"/>

			<xsl:if test="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisstate">
				<div class="&centeredClass;">
					<span class="&uBoldClass;">&statesText;</span>
					<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisstate"/>
				</div>
			</xsl:if>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="heading">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="number">
		<xsl:param name="suppressNumber" select="true()" />

		<xsl:if test="not($suppressNumber)">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="title[preceding-sibling::*[1][self::number]]">
		<div class="&sectionTitleClass; &uBoldClass;">
			<span class="&uBoldClass;">
				<xsl:apply-templates select="preceding-sibling::number[1]">
					<xsl:with-param name="suppressNumber" select="false()" />
				</xsl:apply-templates>
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:apply-templates/>
			</span>
		</div>
	</xsl:template>

	<xsl:template name="dateHeader">
		<xsl:param name="header" />
		<xsl:if test="number(.) = .">
			<xsl:variable name="formattedDate">
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="displayDay" select="'true'" />
					<xsl:with-param name="displayTime" select="'false'" />
				</xsl:call-template>
			</xsl:variable>
			<div class="&centeredClass;">
				<xsl:value-of select="concat($header,$formattedDate)"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.publisheddate">
		<xsl:call-template name="dateHeader">
			<xsl:with-param name="header" select="'&headerPublishedDate;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="md.starteffective">
		<xsl:call-template name="dateHeader">
			<xsl:with-param name="header" select="'&headerEffectiveDate;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Links definition -->
	<xsl:template match="link[@tuuid]">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="parent::xref[@behaviour = 'pdf']">
				<xsl:call-template name="CreatePDFLink">
					<xsl:with-param name="content" select="$content" />
					<xsl:with-param name="guid" select="@tuuid" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="GetDocumentUrl">
							<xsl:with-param name="documentGuid" select="@tuuid"/>
						</xsl:call-template>
					</xsl:attribute>
					<xsl:copy-of select="$content" />
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="heading/link[@tuuid]">
		<!-- Suppress links in header -->
		<xsl:apply-templates />
	</xsl:template>

	<!-- Enable tables -->
	<xsl:template match="table">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&tableGroupClass;</xsl:text>
				<xsl:value-of select="' '" />
				<xsl:text>&tableGroupBorderClass;</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para-text">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="footnote">
		<xsl:variable name="footnotePosition" select="count(preceding::footnote) + 1"/>
		<xsl:variable name="foonoteReference">
			<xsl:text>co_footnoteReference_</xsl:text>
			<xsl:value-of select="$footnotePosition"/>
		</xsl:variable>
		<sup>
			<xsl:attribute name="id">
				<xsl:value-of select="$foonoteReference"/>
			</xsl:attribute>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:text>&#35;FN</xsl:text>
					<xsl:value-of select="$footnotePosition"/>
				</xsl:attribute>
				<xsl:attribute name="class">&footnoteReferenceClass;</xsl:attribute>
				<xsl:value-of select="$footnotePosition"/>
			</xsl:element>
		</sup>
	</xsl:template>

	<!-- Entry point -->
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&indiaDocumentClass;'"/>
			</xsl:call-template>

			<xsl:call-template name="headings" />
			<xsl:apply-templates select="/Document/n-docbody/document/fulltext | /Document/n-docbody/document/provisions-table"/>

			<xsl:call-template name="RenderFootnotes">
				<xsl:with-param name="footNoteTitle" select="'&nbsp;'" />
			</xsl:call-template>

			<!-- Copyright block -->
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="normalize-space(string(/Document/n-docbody/copyright-message))" />
				<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="'true'" />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>
