<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />
	<xsl:import href="../transform-utils.xsl" />
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	
	<xsl:param name="volumeName" select="volumeName" />
	<xsl:param name="isMultiVolume" select="isMultiVolume" />
	<xsl:variable name="front_matter_uuid" select="concat($volumeName,'.','FrontMatter')" />

	<xsl:template match="x:root">
		<root>
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid"
					select="concat($volumeName,'.fm.title.page')" />
			</xsl:call-template>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:fm.title.page">
		<xsl:variable name="uuid" select="concat($volumeName,'.',name())" />
		<xsl:variable name="volNumber"
			select="substring($volumeName, string-length($volumeName), 1)" />

		<xsl:if test="$isMultiVolume">
			<xsl:element name="XPPHier">
				<xsl:attribute name="uuid" select="$volumeName" />
				<xsl:attribute name="name"
					select="concat($volNamePlaceholder, $volNumber)" />
				<xsl:attribute name="parent_uuid" select="$volumeName" />
				<xsl:attribute name="md.doc_family_uuid" select="$uuid" />
				<xsl:value-of select="$volNamePlaceholder" />
			</xsl:element>
		</xsl:if>

		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="'Title page'" />
			<xsl:with-param name="parent_uuid" select="$uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid" />
		</xsl:call-template>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:fm.copyright.page">
		<xsl:variable name="uuid" select="concat($volumeName,'.',name())" />
		<xsl:call-template name="createMetadataAndHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="parent_uuid" select="$uuid" />
			<xsl:with-param name="hierName" select="'Copyright Page'" />
		</xsl:call-template>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>

		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$front_matter_uuid" />
			<xsl:with-param name="name" select="'Front matter'" />
			<xsl:with-param name="parent_uuid" select="$front_matter_uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template
		match="x:fm.highlights|x:fm.dedicate|x:fm.acknowledgment|x:fm.foreword|x:fm.online.research.guide|x:fm.proview|x:fm.related.products">
		<xsl:variable name="common_tags_uuid" select="concat($volumeName,'.', name())" />
		<xsl:call-template name="createMetadataAndHier">
			<xsl:with-param name="uuid" select="$common_tags_uuid" />
			<xsl:with-param name="parent_uuid" select="$front_matter_uuid" />
			<xsl:with-param name="hierName">
				<xsl:call-template name="defineTagTitle">
					<xsl:with-param name="tag_name" select="name()" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:fm.about.the.author|x:fm.other.structure">
		<xsl:variable name="other_label"
			select="x:head[1]/x:name.block[1]/x:name[1]/x:t[1]/text()" />
		<xsl:variable name="other_uuid"
			select="concat($volumeName,'.',name(),'.',position())" />

		<xsl:call-template name="createMetadataAndHier">
			<xsl:with-param name="uuid" select="$other_uuid" />
			<xsl:with-param name="parent_uuid" select="$front_matter_uuid" />
			<xsl:with-param name="hierName" select="$other_label" />
		</xsl:call-template>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template name="createMetadataAndHier">
		<xsl:param name="uuid" />
		<xsl:param name="parent_uuid" />
		<xsl:param name="hierName" />

		<xsl:call-template name="placeSectionbreak">
			<xsl:with-param name="sectionuuid" select="$uuid" />
		</xsl:call-template>
		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="$hierName" />
			<xsl:with-param name="parent_uuid" select="$parent_uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="defineTagTitle">
		<xsl:param name="tag_name" />
		<xsl:choose>
			<xsl:when test="$tag_name = 'fm.highlights'">
				<xsl:value-of select="'Introduction to Edition'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.dedicate'">
				<xsl:value-of select="'Dedication'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.acknowledgment'">
				<xsl:value-of select="'Acknowledgment'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.foreword'">
				<xsl:value-of select="'Foreword'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.online.research.guide'">
				<xsl:value-of select="'Westlaw Online Research Guide'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.proview'">
				<xsl:value-of select="'ProView page'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.related.products'">
				<xsl:value-of select="'Related products'" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>