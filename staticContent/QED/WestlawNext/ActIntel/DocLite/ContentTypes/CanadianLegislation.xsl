<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianLegislation.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overriding to suppress StarPageMetadata and currencyLink -->
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswLegislation;'"/>
			</xsl:call-template>
		
			<div class="&documentHeadClass;">
				<xsl:if test="not($PreviewMode)">
					<xsl:apply-templates select="n-docbody/legis/doc_heading/toc_headings"/>
				</xsl:if>        
				<div class="&headnotesClass; &centerClass;">				
					<xsl:apply-templates select="n-docbody/legis/doc_heading/doc_citation | n-docbody/legstub/stub_heading/stub_citation"/>
					<xsl:apply-templates select="n-docbody/legstub/stub_heading/stub_head"/>
					<!-- For Legislation Stub documents only -->

					<xsl:choose>
						<xsl:when test="descendant::formti">
							<xsl:apply-templates select="(n-docbody//formti)[1]"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="n-docbody/legis/doc_heading/doc_title"/>
						</xsl:otherwise>
					</xsl:choose>

					<xsl:apply-templates select="n-docbody/legis/doc_heading/include.currency.block" mode="currencyLink"/>
					<xsl:apply-templates select="n-docbody/legis/doc_heading/img/image.block"/>
				</div>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<xsl:apply-templates select="n-docbody/legis/node()[not(self::message.block.carswell or self::legisrm or self::ul[preceding-sibling::p//sup/a[starts-with(@name, 'f')]]) and not(descendant::formti) or self::proposed] | n-docbody/legstub/node()[not(self::legisrm or self::message.block.carswell)]"/>

			<xsl:apply-templates select="n-docbody/legis/doc_heading/include.currency.block"/>

			<xsl:apply-templates select="n-docbody/legis/legisrm"/>

			<xsl:call-template name="RenderFootnoteSection"/>
			
			<xsl:call-template name="EndOfDocument"/>
			
		</div>
	</xsl:template>

	<xsl:template match="include.currency.block" mode="currencyLink" />

</xsl:stylesheet>