<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:param name="docUrl" />
	<xsl:include href="Image.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<xsl:variable name="guid" select="//attached.file/document.image.link/@tuuid" />
		<xsl:variable name="imageMetaData" select="/*/ImageMetadata/n-metadata[@guid=$guid]" />
		<xsl:variable name="mimeType" select="$imageMetaData/metadata.block/md.image.block/md.image.format"/>

    <xsl:variable name="documentType" select="/Document/document-data/collection"/>

    <!-- create the link only if the MimeType is Word Document and the document is a Standard document or UK standard document  -->
    <xsl:if test="$mimeType = 'application/msword' and ($documentType = 'w_plc_standdoc' or $documentType = 'w_plc_uk_standdoc')">
			<xsl:apply-templates select="//attached.file/document.image.link" />
		</xsl:if>
	</xsl:template>	
	
</xsl:stylesheet>