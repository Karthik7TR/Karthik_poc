<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CodesStateAdminCodes.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Document override with no KeyCite or StarPage -->
	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStateAdminCodesClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates/>
			<xsl:call-template name="FooterCitation"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- TODO: put in a specific Footnotes override -->
	<!-- Table Footnote override with no links -->
	<xsl:template match="table.footnote.reference[ancestor::table]" priority="2">
		<xsl:variable name="tblId" select="ancestor::tbl/@ID" />
		<xsl:variable name="refNumberTextParam" select="." />
		<xsl:variable name="refNumberText" select="substring-after(substring-before($refNumberTextParam, ']'),'[FN')" />
		<xsl:variable name="refNumber" select="translate($refNumberText,'*','s')" />
		<xsl:if test="string-length($refNumber) &gt; 0">
			<sup id="co_table_footnote_reference_{$tblId}_{$refNumber}">
				<xsl:value-of select="$refNumberText"/>
			</sup>
		</xsl:if>
	</xsl:template>

	<xsl:template match="table.footnote.reference[ancestor::footnote]" priority="2">
		<xsl:variable name="tblId" select="ancestor::tbl/@ID" />
		<xsl:variable name="refNumberTextParam" select="." />
		<xsl:variable name="refNumberText" select="substring-after(substring-before($refNumberTextParam, ']'),'[FN')" />
		<xsl:variable name="refNumber" select="translate($refNumberText,'*','s')" />
		<span id="co_table_footnote_{$tblId}_{$refNumber}">
			<xsl:value-of select="$refNumberText"/>
		</span>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- overriding to suppress links -->
	<xsl:template match="internet.url">
		<xsl:copy-of select="normalize-space(.)"/>
	</xsl:template>

</xsl:stylesheet>
