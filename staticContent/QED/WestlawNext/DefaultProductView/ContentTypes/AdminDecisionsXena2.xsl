<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="FootnoteBlock.xsl"/>

	<xsl:include href="Dtags.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Xena1.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select ="so"/>
			<xsl:apply-templates select ="ti"/>
			<xsl:apply-templates select ="dl"/>
			<xsl:apply-templates select ="tk1"/>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</div>
		<xsl:apply-templates select="*[not(self::so or self::ti or self::dl or self::tk1)]" />
	</xsl:template>

	<xsl:template match="ti" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="so" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tk1" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dl" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Since the d6 tag doesn't add a blank line by itself, force one in the following cases: -->
	<xsl:template match="dpa0[d6] | dpa1[d6] | dpa2[d6] | smp[d6]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="fnax" priority="5">
		<xsl:param name="suppressHeading" select="false()" />
		<xsl:param name="id" />
		<xsl:call-template name="RenderFootnoteBlockMarkup">
			<xsl:with-param name="id">
				<xsl:choose>
					<xsl:when test="$id">
						<xsl:value-of select="$id" />
					</xsl:when>
					<xsl:when test="@id | @ID">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', (@id | @ID))" />
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="suppressHeading" select="boolean(preceding-sibling::fnax or $suppressHeading)" />
		</xsl:call-template>
	</xsl:template>

		<xsl:template name="RenderFootnoteBlockMarkupTable">
		<xsl:param name="suppressHeading" select="false()" />
		<xsl:param name="id"/>
		<table class="&footnoteSectionClass;">
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="not($suppressHeading)">
				<tr>
					<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
            <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
          </td>
				</tr>
			</xsl:if>
			<tr>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
			
		</table>
	</xsl:template>
	
		<xsl:template match="internal.reference" priority="3">
		<xsl:variable name="refid"  select="translate(@refid, '?', 'Þ')" />
		<xsl:variable name="id"  select="translate(@ID, '?', 'Þ')" />
		<xsl:variable name="contents">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="." />
			</xsl:call-template>
			<!--<xsl:apply-templates />-->
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