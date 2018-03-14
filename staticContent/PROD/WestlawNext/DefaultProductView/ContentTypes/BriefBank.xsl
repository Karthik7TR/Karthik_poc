<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="KnowHow.xsl"/>
	<xsl:include href="KnowHowOperatives.xsl"/>

	<!--This changes the text inside the link anchor for PDF, removes the (PDF) from the end-->
	<xsl:template name="getContentText">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="party">
		<table style="margin-top:0; padding-top:0; padding-bottom:0;">
			<tbody>
				<tr style="">
					<xsl:for-each select="*">
						<td style="padding-top:0; padding-bottom:0;">
							<xsl:apply-templates />
						</td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>
	</xsl:template>
	
</xsl:stylesheet>
