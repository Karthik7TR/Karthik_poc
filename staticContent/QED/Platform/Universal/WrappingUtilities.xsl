<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Callable template for minor parts of content blocks -->
	<xsl:template name="wrapWithDiv">
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="contents"/>

		<div>
			<xsl:if test="$class and string-length($class) &gt; 0">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$id and string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template name="wrapWithSpan">
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="contents"/>
		<span>
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template name="wrapWithLi">
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="contents" />
		<li>
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</li>
	</xsl:template>

	<xsl:template name="wrapWithUl">
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="contents" />
		<ul>
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</ul>
	</xsl:template>

	<!-- Callable template for content blocks -->
	<xsl:template name="wrapContentBlockWithGenericClass">
		<xsl:param name="id"/>
		<xsl:param name="contents" />
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class" />
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="$id" />
			<xsl:with-param name="class">
				<xsl:value-of select="concat('&simpleContentBlockClass; ', $xmlBasedClassName)"/>
			</xsl:with-param>
			<xsl:with-param name="contents" select="$contents" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="wrapContentBlockWithCobaltClass">
		<xsl:param name="id"/>
		<xsl:param name="contents" />
		<xsl:variable name="classPrefix">
			<xsl:call-template name="getEscapeToClassPrefix"/>
		</xsl:variable>
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class">
				<xsl:with-param name="prefix" select="$classPrefix" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="$id" />
			<xsl:with-param name="class">
				<xsl:text>&simpleContentBlockClass; </xsl:text>
				<xsl:value-of select="$xmlBasedClassName"/>
			</xsl:with-param>
			<xsl:with-param name="contents" select="$contents" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="wrapContentBlockWithAdditionalCobaltClasses">
		<xsl:param name="id"/>
		<xsl:param name="additionalClass"/>
		<xsl:param name="contents" />
		<xsl:variable name="classPrefix">
			<xsl:call-template name="getEscapeToClassPrefix"/>
		</xsl:variable>
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class">
				<xsl:with-param name="prefix" select="$classPrefix"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="$id" />
			<xsl:with-param name="class">
				<xsl:text>&simpleContentBlockClass; </xsl:text>
				<xsl:value-of select="$xmlBasedClassName"/>
				<xsl:if test="string-length($additionalClass) &gt; 0">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="$additionalClass"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="contents" select="$contents" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="wrapContentBlockWithGenericClassAndAnchoredId">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID | @id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="wrapWithH">
		<xsl:param name="level" />
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="contents" />
		<xsl:element name="h{$level}">
			<xsl:if test="$class and string-length($class) &gt; 0">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$id and string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<!-- This template is added so that products can override the class prefix based on product business logic.-->
	<xsl:template name="getEscapeToClassPrefix">
		<xsl:text>&coPrefix;</xsl:text>
	</xsl:template>
</xsl:stylesheet>