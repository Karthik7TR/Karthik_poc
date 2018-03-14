<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="cite.query" name="citeQuery" priority="2">
		<xsl:param name="citeQueryElement" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$citeQueryElement/node()[not(self::starpage.anchor)]" />
		</xsl:param>
		<xsl:if test="string-length($linkContents) &gt; 0">
			<xsl:copy-of select="$linkContents"/>
		</xsl:if>

		<!-- Add a space if the following sibling is a cite.query -->
		<xsl:if test="$citeQueryElement/following-sibling::node()[1]/self::cite.query">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="pinpoint.anchor" priority="2"/>
	
	<xsl:template match="entity.link" name="medicalEntityLinkOut">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="md.toggle.links" priority="2"/>

	<xsl:template match="urllink" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="xrefLink">
		<xsl:param name="xrefElement" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$xrefElement/node()[not(self::starpage.anchor)]" />
		</xsl:param>

		<xsl:variable name="fullLinkContents">
			<xsl:choose>
				<xsl:when test="string-length($SourceSerial) &gt; 0 and ($xrefElement/@wlserial = $SourceSerial)">
					<xsl:call-template name="markupSourceSerialSearchTerm">
						<xsl:with-param name="linkContents" select="$linkContents"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($fullLinkContents) &gt; 0">
			<xsl:copy-of select="$fullLinkContents"/>
		</xsl:if>

		<!-- Add a space if the following sibling is a cite.query -->
		<xsl:if test="$xrefElement/following-sibling::node()[1]/self::cite.query">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
