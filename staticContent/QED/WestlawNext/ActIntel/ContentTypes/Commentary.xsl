<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Commentary.xsl" forceDefaultProduct="true" />

	<!-- do not show reference block -->
	<xsl:template match="research.references/reference.block[(topic.key.ref or reference) 
						and not(ancestor::footnote) and head[@parent='reference.block']/
						headtext[normalize-space(.) = '&researchReferenceWestlawDB;' 
								or normalize-space(.) = '&researchReferencePeriodicals;']]">
	</xsl:template>

	<xsl:template match="headtext | form.headtext">
			<xsl:call-template name="renderHeadTextDiv">
				<xsl:with-param name="extraClass" select="'&headInlineClass;'"/>
			</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
