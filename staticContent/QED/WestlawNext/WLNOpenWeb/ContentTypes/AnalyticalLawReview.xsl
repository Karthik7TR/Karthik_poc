<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalyticalLawReview.xsl" forceDefaultProduct="true"/>


	<!-- OPEN WEB - Limit text displayed -->
	<xsl:template match="section">
		<xsl:variable name="Contents">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:variable>
		<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents,&LimitTextByCharacterCountValue;)"/>
	</xsl:template>

	<!--Supress cite at bottom of page-->
	<xsl:template match="cmd.first.line.cite" />
  
	<!--Supress the Foot notes block-->
	<xsl:template match="footnote.block" priority="1"/>

	<!-- Remove the underlying link for the foot note reference overriding the platform changes-->
	<xsl:template name="generateLinkToFootnote" priority="1">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteRef" select="." />

		<xsl:if test="not($EasyEditMode)">
			<xsl:if test="string-length($refNumberText) &gt; 0">
				<xsl:choose>
					<xsl:when test="not(/*[1]/self::summary or /*[1]/self::summaries) and string-length($footnoteRef/@refid) &gt; 0 and (string-length(key('distinctFootnoteIds', $footnoteRef/@refid)) &gt; 0 or count(key('distinctAlternateFootnoteIds', $footnoteRef/@refid)) &gt; 0)">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', $footnoteRef/@refid, '_', generate-id($footnoteRef))"/>
							</xsl:attribute>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:when>
					<xsl:when test="(following-sibling::internal.reference[1]/@refid)">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', following-sibling::internal.reference/@refid)"/>
							</xsl:attribute>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:when>
					<xsl:when test="starts-with(normalize-space(descendant-or-self::text()),'[FN') and /Document//footnote[contains(normalize-space(descendant-or-self::text()),concat('FN',$refNumberText))]">
						<xsl:variable name="tableIdPrefix" select="ancestor::tbl/@ID"></xsl:variable>
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('co_tablefootnote_', concat($tableIdPrefix, translate($refNumberText,'*','s')) )"/>
							</xsl:attribute>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:when>
					<xsl:otherwise>
						<sup>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
