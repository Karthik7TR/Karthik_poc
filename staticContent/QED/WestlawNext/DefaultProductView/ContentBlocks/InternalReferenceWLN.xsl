<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="yes"/>
<!-- To Fix the Bug 474396 -->
	<xsl:template match="internal.reference" priority="1">
		<xsl:variable name="refid"  select="translate(@refid, '?', 'Þ')" />
		<xsl:variable name="id"  select="translate(@ID, '?', 'Þ')" />
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0 or string-length($id) &gt; 0">
			<xsl:choose>
				<xsl:when test="key('allElementIds', $refid)">
					<span>
						<xsl:if test="string-length($id) &gt; 0">
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&internalLinkIdPrefix;', $id)"/>
							</xsl:attribute>
						</xsl:if>
						<a href="{concat('#&internalLinkIdPrefix;', $refid)}" class="&internalLinkClass;">
							<xsl:copy-of select="$contents"/>
							<xsl:comment>anchor</xsl:comment>
						</a>
					</span>
				</xsl:when>
				<xsl:when test="string-length($id) &gt; 0">
					<a id="{concat('&internalLinkIdPrefix;', $id)}">
						<xsl:comment>anchor</xsl:comment>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$contents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>