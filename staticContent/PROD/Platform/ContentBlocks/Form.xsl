<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Leader.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="form">
		<div class="&formClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="form.line">
		<div>
			<xsl:choose>
				<xsl:when test="@style='c'">
					<xsl:attribute name="class">&alignHorizontalCenterClass;</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="../node() = 'form.caption'">
						<xsl:attribute name="class">
							<xsl:text>&centerClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&formCaptionClass;</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="leader">
					<xsl:call-template name="leaderContent">
						<xsl:with-param name="parent" select="." />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="form.signature.line">
		<xsl:variable name="formSignatureLineContents">
			<xsl:for-each select="node()">
				<xsl:variable name="contents">
					<xsl:apply-templates select="."/>
					<!-- this dot is VERY IMPORTANT!  textrule wouldn't hit w/o it-->
				</xsl:variable>
				<xsl:if test="string-length($contents) &gt; 0">
						<xsl:copy-of select="$contents"/><xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($formSignatureLineContents) &gt; 0">
			<div>
				<xsl:copy-of select="$formSignatureLineContents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="form.notes">
		<xsl:if test="not($EasyEditMode)">
			<xsl:apply-templates 	/>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
