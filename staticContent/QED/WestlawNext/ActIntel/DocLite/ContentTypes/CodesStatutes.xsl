<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CodesStatutes.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overriding to suppress StarPageMetadata and IsRuleBookMode logic -->
	<xsl:template match="Document" priority="2">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStatutesClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates/>
			<xsl:apply-templates select="n-docbody//content.metadata.block" mode="footerCustomCitation" />
			<!--Adding a Div so the a separate Fo block is made for the currencyblock Fix for Bug303578-->
			<div>
				<xsl:apply-templates select="n-docbody//include.currency.block/include.currency" mode="currency"/>
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Overriding to remove styling -->
	<xsl:template match="subsection//headtext">
		<div>
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<!-- Suppressing actual signatures -->
	<xsl:template match="signature.block/signature.line">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Overriding to suppress links -->
	<xsl:template match="abbreviations.reference | abbreviations" priority="2">
		<xsl:variable name="refid" select="translate(@refid, '?', 'Þ')" />
		<xsl:variable name="id" select="translate(@ID, '?', 'Þ')" />
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<div>
			<xsl:if test="string-length($contents) &gt; 0 or string-length($id) &gt; 0">
				<xsl:copy-of select="$contents"/>
			</xsl:if>
		</div>
	</xsl:template>

	<!-- TODO: move to Universal.xsl override -->
	<!-- Suppress propagated block -->
	<xsl:template match="propagated.block" />

	<!-- Overriding special annotations case to suppress annotations -->
	<xsl:template match="annotations[/Document/document-data/collection = 'w_codesstailnvdp']" />

	<!-- Overriding to suppress links and ignore rule book mode -->
	<xsl:template match="subsection.hovertext">
		<xsl:apply-templates />
	</xsl:template>
	
</xsl:stylesheet>