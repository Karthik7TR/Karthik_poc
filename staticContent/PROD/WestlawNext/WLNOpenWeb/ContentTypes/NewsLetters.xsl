<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Newsletters.xsl" forceDefaultProduct="true"/>


	<!-- OPEN WEB - Limit text displayed -->
	<xsl:template match="section">
		<xsl:variable name="Contents">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:variable>
		<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents, &LimitTextByCharacterCountValue;)" />
	</xsl:template>

	<xsl:template match="section[preceding::section]" />

	<!-- Supress footnotes, links to footenotes, and citations from OW-->
	<xsl:template match="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation" />
	<xsl:template match="cmd.cites/cmd.first.line.cite" />

	<xsl:template match="footnote.block" name="footnoteBlock" />
	<xsl:template name="generateLinkToFootnote" />
	
	<!-- Bug #890980. Example Guid: I09f8e9310fbf11dcae3697ced7e0d51e -->
	<xsl:template match="reference.block" />

	<!-- Bug #890980. Example Guid: I998fee810fbb11dcb5b2be4ca939c7c3 -->
	<xsl:template match="content.metadata.block[preceding::content.metadata.block]" />

	<!-- Bug #890980. Example Guid: I3e496ad13f9811dc8607dd2e129a83f2 -->
	<!-- Bug #890135. Example Guid: I45a426b3d2b611de9b8c850332338889 -->
	<xsl:template match="note.block" />

</xsl:stylesheet>