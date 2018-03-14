<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CodesFederalRegister.xsl" forceDefaultProduct="true"/>
	<!-- Including Added/Deleted Material after so that it overrides the hiding of it -->
	<xsl:include href="AddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overridden with suppression of KeyCite and StarPageMetadata -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesFederalRegisterClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates/>
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Overriding special handling of star page and instead suppressing it -->
	<xsl:template match="starpage.anchor" priority="3" />

</xsl:stylesheet>
