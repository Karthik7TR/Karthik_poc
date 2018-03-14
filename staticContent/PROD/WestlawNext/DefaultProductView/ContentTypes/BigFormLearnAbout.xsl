<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		 <div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeBigFormLearnAboutClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument"/>			
		</div>
	</xsl:template>
	
	<xsl:template match="prelim">
		<div class="&simpleContentBlockClass; &prelimClass;">
			<div class="&centerClass;">
				<xsl:apply-templates />
			</div>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
		
	<xsl:template match="form.finder.link"/>

	<xsl:template match="prelim/database.name | prelim/topic | prelim/subtopic">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<!--
	==================================================================
	Special handling for text containing a cite.query with DB ref type
	==================================================================
	For example:
	<paratext><bos />Arbitrators - Court Cases<cite.query w-ref-type="DB" ID="I13afb4a61d9c11df9b8c850332338889" w-normalized-cite="ARB-CS">ARB-CS</cite.query> - Federal court decisions related to the arbitration of human resources issues for federal government employees. <eos /><bos />Selected coverage begins with 1978.<eos /></paratext>
	would be rendered as:
	<a id="co_link_I13afb4a61d9c11df9b8c850332338889" class="co_link co_drag ui-draggable" href="http://www.next.ci.westlaw.com/Link/Database/SignOn/ARB-CS?originationContext=document&amp;transitionType=DocumentItem">Arbitrators - Court Cases</a> - Federal court decisions related to the arbitration of human resources issues for federal government employees. Selected coverage begins with 1978.
	-->
	<xsl:template match="paratext[(ancestor::prac.guides or ancestor::rel.wl.databases) and child::cite.query[@w-ref-type='DB']]">
		<xsl:variable name="linkContent">
			<xsl:choose>
				<xsl:when test="string-length(normalize-space(text()[1])) &gt; 0">
					<xsl:apply-templates select="text()[1]"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="*[following-sibling::cite.query[@w-ref-type='DB'] and not(self::cite.query)]"/>					
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<div class="&paratextMainClass;">
			<xsl:call-template name="citeQuery">
				<xsl:with-param name="citeQueryElement" select="cite.query[1]"/>
				<xsl:with-param name="linkContents" select="$linkContent"/>
			</xsl:call-template>
			<xsl:apply-templates select="text()[position() &gt; 1]"/>
		</div>
	</xsl:template>

	<xsl:template match="underscore[following-sibling::cite.query[@w-ref-type='DB']]">
		<xsl:apply-templates select="text()"/>
	</xsl:template>
</xsl:stylesheet>
