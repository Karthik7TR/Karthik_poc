<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AdminDecision.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overridden with suppression of KeyCite and StarPageMetadata -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:choose>
				<!-- In order to make the font uniform all over the document-->
				<xsl:when test="/Document/document-data/collection = 'w_3rd_arbbio'">
					<xsl:call-template name="AddDocumentClasses"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeAdminDecisionClass;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
			<xsl:call-template name="RenderFootnote" />
			<xsl:call-template name="FooterCitation" />
			<xsl:apply-templates select="n-docbody/header/prelim/copyright"/>
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="'&nonUSGovernmentCopyrightText;'" />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>
