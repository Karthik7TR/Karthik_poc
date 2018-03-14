<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="IntellectualPropertyPatentsPctApplications.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsAsiaPacificClass;'"/>
			</xsl:call-template>
			<div class="&sazanamiMinchoClass; &contentTypeIPDocumentClass;">
				<xsl:call-template name="displayInternationalPatentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Hide these nodes. -->
	<xsl:template match="prelim/doc.subtype | prelim/doc.type" />

	<xsl:template match="other.parties/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="examiner.info">
		<xsl:if test="not(../examiner.name)">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- Prelim block -->
	<xsl:template match="prelim">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="patent.info/patent.title.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/granted.pat.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="granted.pat.b/granted.pat.no.b | granted.pat.b/granted.pat.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paratextMainClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/filing.app.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Priority Info -->

	<xsl:template match="priority.info">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="related.doc.b">
		<div>
			<xsl:if test="not(preceding-sibling::related.doc.b)">
				<xsl:attribute name="class">
					<xsl:value-of select="'&panelBlockClass;'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="related.doc.text"/>
			<xsl:text>&#160;</xsl:text>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="related.doc.no | related.doc.date"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="addl.app.b">
		<div class="&panelBlockClass;">
			<xsl:apply-templates select="addl.app.text"/>
			<xsl:text>&#160;</xsl:text>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="addl.app.no | addl.app.date"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="pct.filing.apps" priority="1">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[1]/self::head">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pct.pub.apps">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[head][1]">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pct.filing.app.b">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="./pct.filing.app.no | ./pct.filing.app.date"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pct.pub.app.b">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="./pct.pub.app.no | ./pct.pub.app.date"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Event History -->
	<xsl:template match="history.info/label">
		<xsl:variable name="divContents">
			<strong>
				<xsl:apply-templates/>
			</strong>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
			<xsl:with-param name="contents" select="$divContents"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.event.b">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="./hist.event.date | ./hist.event"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="abstract.head[1]/headtext" priority="1">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="abstract.head/headtext">
		<div class="&panelBlockClass;">
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="cpc.list">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

</xsl:stylesheet>