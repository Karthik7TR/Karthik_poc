<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="d1" name="d1" />

	<xsl:template match="d2" name="d2">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="'&xenaD2;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d3" name="d3">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="'&xenaD3;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d4" name="d4">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="'&xenaD4;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d5" name="d5">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />
		<xsl:param name="className" select="'&xenaD5;'" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d6" name="d6">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />
		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />			
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d7" name="d7">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="'&xenaD7;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d8" name="d8">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="'&xenaD8;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="d9" name="d9">
		<xsl:param name="lm" select="@lm" />
		<xsl:param name="line" select="@line" />

		<xsl:call-template name="dtagDivWithMarginsAndIndenting">
			<xsl:with-param name="lm" select="$lm" />
			<xsl:with-param name="line" select="$line" />
			<xsl:with-param name="className" select="'&xenaD9;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="dtagDivWithMarginsAndIndenting">
		<xsl:param name="lm" />
		<xsl:param name="line" />
		<xsl:param name="className" />
		<div>
			<xsl:if test="$className">
				<xsl:attribute name="class">
					<xsl:value-of select="$className"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($lm) &gt; 0 or string-length($line) &gt; 0">
				<xsl:attribute name="style">
					<xsl:if test="string-length($lm) &gt; 0">
						<xsl:value-of select="concat('&xenaLeftMargin;', $lm * .0625, '&xenaIn;')"/>
					</xsl:if>
					<xsl:if test="string-length($line) &gt; 0">
						<xsl:value-of select="concat('&xenaTextIndent;', $line * .0625, '&xenaIn;')"/>
					</xsl:if>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>

</xsl:stylesheet>
