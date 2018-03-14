<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--show End Of Article item-->
	<xsl:template name="EndOfArticle">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" select="false()"/>
		<xsl:choose>
			<xsl:when test="$PreviewMode = 'True'">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode = 'True' ">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table>
					<xsl:choose>
						<!--Cannot use id for public records documents because we render and print multiple documents a-->
						<xsl:when test="$IsPublicRecords = true()">
							<xsl:attribute name="class">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="id">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<tr>
						<td>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&endOfArticleTextKey;', '&endOfArticleText;')"/>
						</td>
						<xsl:choose>
							<xsl:when test="$endOfDocumentCopyrightTextVerbatim">
								<td class="&endOfDocumentCopyrightClass;">
									<xsl:copy-of select="$endOfDocumentCopyrightText"/>
								</td>
							</xsl:when>
							<xsl:otherwise>
								<td class="&endOfDocumentCopyrightClass;">
									&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/>
								</td>
							</xsl:otherwise>
						</xsl:choose>

					</tr>
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!--show title with image and publication info-->
	<xsl:template name="mobile-pub-info">
		<xsl:param name="pubInfo"/>
		<xsl:variable name="imageSrc" select="$pubInfo/following-sibling::content/text[@type='links']/p/a[1]/@href"/>
		<xsl:variable name="imageAlt" select="$pubInfo/preceding::title-info"/>
		<div class="&titleClass; &textLeftClass;">
			<xsl:if test="string-length($imageSrc) &gt; 0">
				<img class="&leaderCommaClass;" width="80">
					<xsl:attribute name="src">
						<xsl:value-of select="$imageSrc" />
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="$imageAlt" />
					</xsl:attribute>
				</img>
			</xsl:if>
			<xsl:value-of select="$pubInfo/preceding::title-info"/>
		</div>
		<div class="&seeCitingRefsClass;">
			<span class="&docketBlockClass;">
				<xsl:value-of select="$pubInfo/pub-date/full-pub-date"/>&#160;
			</span>
			<span class="&docketBlockClass;">
				<xsl:if test="string-length($pubInfo/journal/sort-journal) &gt; 0">
					|&#160;<xsl:value-of select="$pubInfo/journal/sort-journal"/>&#160;
				</xsl:if>
			</span>
			<span class="&docketBlockClass;">
				<xsl:if test="string-length($pubInfo/preceding::author-info/author) &gt; 0">
					|&#160;By&#160;<xsl:value-of select="$pubInfo/preceding::author-info/author"/>&#160;
				</xsl:if>
			</span>
		</div>
	</xsl:template>

</xsl:stylesheet>