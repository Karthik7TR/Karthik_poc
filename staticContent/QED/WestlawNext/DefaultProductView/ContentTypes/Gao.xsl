<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl" />
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeGaoClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Custom cite handling to match Web2 -->
	<xsl:template match="cmd.cites">
		<div class="&citesClass;">
			<xsl:apply-templates select=".//cmd.third.line.cite"/>
			<xsl:text>&#160;&#160;</xsl:text>
			<xsl:apply-templates select=".//cmd.second.line.cite"/>
		</div>
	</xsl:template>

	<xsl:template match="descriptive.shell">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cong.mat.head">
		<div class="&congMatHeadClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="hearing.head">
		<div class="&hearingHeadClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="pres.doc.head">
		<div class="&presDocInfoClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cong.mat.info//label.content">
		<xsl:call-template name="createPdfCites"/>
		<xsl:text>&#160;</xsl:text>
		<xsl:choose>
			<xsl:when test="cite.segments/com.print.block">
				<xsl:apply-templates select="cite.segments/com.print.block/com.print.date" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="pdfdate" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="pdfpage" />
	</xsl:template>

	<xsl:template match="hearings.info//label.content">
		<xsl:apply-templates select="cite.segments/hearing.block/hearing.cite" />
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/hearing.block/hearing.title" />
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/hearing.block/hearing.date" />
		<xsl:apply-templates select="pdfpage" />
	</xsl:template>

	<xsl:template match="pres.doc.info//label.content[.//pres.block]">
		<xsl:apply-templates select="cite.segments/pres.block/pres.source" />
		<xsl:text>,&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/pres.block/doc.title" />
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/pres.block/pres.date" />
		<xsl:apply-templates select="pdfpage" />
	</xsl:template>

	<xsl:template match="pres.doc.info//label.content[.//exec.order.block]">
		<xsl:apply-templates select="cite.segments/exec.order.block/exec.order.type" />
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/exec.order.block/exec.order.number" />
		<xsl:text>,&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/exec.order.block/exec.order.title" />
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates select="cite.segments/exec.order.block/exec.order.date" />
		<xsl:apply-templates select="pdfpage" />
	</xsl:template>

	<xsl:template match="cite.segments/pres.block/doc.title">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="createPdfCites">
		<xsl:variable name="pdfCites" select=".//pdfcite"/>
		<xsl:for-each select="$pdfCites">
			<xsl:apply-templates select="."/>
			<xsl:if test="position() != last()">
				<xsl:text>,&nbsp;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="prelim" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>


	<xsl:template match="no.image.block" >
		<xsl:apply-templates select="description.text"/>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="pdfdate"/>
		<br />
	</xsl:template>
	
	<!-- suppressed elements -->
	<xsl:template match="nondisplayable.text" />
	<xsl:template match="document.image.link/document.type" />
	<xsl:template match="prelim/citedate" />

	<!-- suppress these metadata elements -->
	<xsl:template match="md.cites | cmd.first.line.cite | cmd.batch.id | md.related.reference" />

	<!-- suppress these cite.segments element descendants -->
	<xsl:template match="cite.segments/bill.block" />
	<xsl:template match="cite.segments/calendar.block" />
	<xsl:template match="cite.segments/com.print.block" />
	<xsl:template match="cite.segments/cong.rec.block" />
	<xsl:template match="cite.segments/cong.session.info" />
	<xsl:template match="cite.segments/congressional.session" />
	<xsl:template match="cite.segments/hearing.block" />
	<xsl:template match="cite.segments/misc.block" />
	<xsl:template match="cite.segments/plcite" />
	<xsl:template match="cite.segments/pres.block" />
	<xsl:template match="cite.segments/rpt.block" />
</xsl:stylesheet>
