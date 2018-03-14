<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="correlation.tbl">
		<div>
			<xsl:apply-templates select="node()[not(self::revised.col.head or self::correlation)]"/>
		</div>
	</xsl:template>

	<xsl:template match="former.col.head">
		<table>
			<tr>
				<th>
					<xsl:variable name="contents">
						<xsl:apply-templates />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="string-length($contents) &gt; 0">
							<xsl:copy-of select="$contents"/>
						</xsl:when>
						<xsl:otherwise>
							<![CDATA[ ]]>
						</xsl:otherwise>
					</xsl:choose>
				</th>
				<th>
					<xsl:variable name="contents">
						<xsl:apply-templates select="following-sibling::revised.col.head"/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="string-length($contents) &gt; 0">
							<xsl:copy-of select="$contents"/>
						</xsl:when>
						<xsl:otherwise>
							<![CDATA[ ]]>
						</xsl:otherwise>
					</xsl:choose>
				</th>
			</tr>
			<xsl:apply-templates select="following-sibling::correlation"/>
		</table>
	</xsl:template>

	<xsl:template match="correlation">
		<tr>
			<xsl:apply-templates/>
		</tr>
	</xsl:template>

	<xsl:template match="former | revised">
		<td>
			<xsl:variable name="contents">
				<xsl:apply-templates />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<![CDATA[ ]]>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>
</xsl:stylesheet>
