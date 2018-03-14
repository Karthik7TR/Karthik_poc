<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2_CodesBulletin.xsl" forceDefaultProduct="true"/>
	<!-- Including Added/Deleted Material after so that it overrides the hiding of it -->
	<xsl:include href="AddedDeletedMaterial.xsl"/>
	<!-- Explicitly including InternalReferences again so that it overrides the templates in InternalReferenceWLN -->
	<xsl:include href="InternalReferences.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overridden with suppression of StarPageMetadata -->
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:apply-templates select="//md.cites" />
			<xsl:apply-templates select="n-docbody" />
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo" />
		</div>
	</xsl:template>

</xsl:stylesheet>