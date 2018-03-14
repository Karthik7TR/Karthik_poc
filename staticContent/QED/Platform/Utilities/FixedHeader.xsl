<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="CiteLinesInMetaData.xsl"/>
	<xsl:include href="TitleText.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="FootnoteReferenceCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="FixedHeader">
		<xsl:choose>
			<xsl:when test="count(*) &gt; 0">
				<xsl:for-each select="*">
					<xsl:call-template name="GetHeader">
						<xsl:with-param name="data">
							<xsl:apply-templates select="."/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="GetHeader">
					<xsl:with-param name="data">
						<xsl:apply-templates select="text()"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GetHeader">
		<xsl:param name="data"/>
		<xsl:variable name="contentsWithFootnoteRemoved">
			<xsl:choose>
				<xsl:when test="contains($data, '[FN') or contains($data, '[fn') or contains($data, '(FN') or contains($data, '(fn')">
					<xsl:call-template name="removeFootnoteMarkup">
						<xsl:with-param name="textWithFootnoteParam" select="$data"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$data"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="InsertSpaceAfterComma">
			<xsl:with-param name="contentData" select="$contentsWithFootnoteRemoved"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="//md.contributors">
		<xsl:apply-templates select="md.author"/>
	</xsl:template>

	<xsl:template match="md.author">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:copy-of select="$contents"/>
			<xsl:if test="contains($contents, ',')">
				<xsl:variable name="afterCommaString" select="substring-after($contents, ', ')"/>
				<xsl:if test="string-length($afterCommaString) = 1">
					<xsl:text>.</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:variable name="endsWithComma">
				<xsl:call-template name="ends-with">
					<xsl:with-param name="string1" select="$contents" />
					<xsl:with-param name="string2" select="','" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$endsWithComma = 'false'">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.author[position() = last()]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="//title.block">
		<xsl:apply-templates select="fixed.title"/>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="title"/>
	</xsl:template>

	<xsl:template match="fixed.title | title">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="head | headtext" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="InsertSpaceAfterComma">
		<xsl:param name="contentData"/>
		<xsl:if test="string-length($contentData) &gt; 0">
			<xsl:choose>
				<xsl:when test="contains($contentData, ',')">
					<xsl:variable name="beforeCommaString" select="substring-before($contentData, ',')"/>
					<xsl:variable name="afterCommaString" select="substring-after($contentData, ',')"/>
					<xsl:copy-of select="$beforeCommaString"/>
					<xsl:choose>
						<xsl:when test="string-length($afterCommaString) = 0">
							<!-- Doing nothing here should remove the comma -->
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>,</xsl:text>
							<xsl:if test="string-length(normalize-space(substring($afterCommaString,1,1))) = 1">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
							<xsl:if test="string-length($afterCommaString) &gt; 0">
								<xsl:call-template name="InsertSpaceAfterComma">
									<xsl:with-param name="contentData" select="$afterCommaString"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$contentData"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="justified.line" priority="1">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<!-- Suppress internal.reference elements in the Fixed Header -->
	<xsl:template match="internal.reference" priority="1"/>

	<xsl:template match="cite.query" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Remove search term markup -->
	<xsl:template match="N-HIT | N-LOCATE | N-WITHIN" priority="1">
		<xsl:if test="preceding-sibling::node()[1][self::ital] | preceding-sibling::node()[1][self::N-HIT] | preceding-sibling::node()[1][self::N-LOCATE] | preceding-sibling::node()[1][self::N-WITHIN]">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="bold | ital | underscore">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="centv"  />

	<xsl:template match="prelim.block/prelim.head[position()>1]" priority="2">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>
	

</xsl:stylesheet>
