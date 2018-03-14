<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Image.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:variable name="primaryCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info" />
	<xsl:variable name="firstParallelCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite[1]/md.parallelcite.info/md.display.parallelcite"/>
	<xsl:variable name="regionalCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@type = 'West_court_regional']
																						| /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[@type = 'West_court_regional']"/>
	
	<xsl:template match="md.print.rendition.id">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and not($DisplayOriginalImageLink)">
				<!-- Do nothing -->
			</xsl:when>
			<xsl:when test="not($DisplayOriginalImageLink)">
				<!-- Do nothing -->
			</xsl:when>			
			<xsl:otherwise>
				<!-- We need to wrap the link in Div in order to properly display in delivered RTF document -->
				<div>
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="."/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
						<xsl:with-param name="contents">
							<xsl:text>&originalDocumentLinkText; </xsl:text>
							<xsl:choose>
								<xsl:when test="$regionalCite">
									<xsl:apply-templates select="$regionalCite" />
								</xsl:when>
								<xsl:when test="$primaryCite/md.display.primarycite[@status = 'nr' or @status = 'slip' or @status = 'dash']">
									<xsl:apply-templates select="$firstParallelCite"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="$Cite" />
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text> &pdfLabel;</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
						<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
						<xsl:with-param name="prettyName" select="translate($Cite//text(),'&space;', '&lowline;')" />
					</xsl:call-template>
				</div>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
