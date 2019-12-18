<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />
	<xsl:import href="insertISBN.xsl" />
	<xsl:import href="../transform-utils.xsl" />
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	
	<xsl:param name="materialNumber" select="materialNumber" />
	<xsl:param name="isMultiVolume" select="isMultiVolume" />
	<xsl:param name="isbn" select="isbn" />
    <xsl:param name="fileNameBasedVolumeNumber" />
    
    <xsl:variable name="materialNumberVolume" select="x:addVolumeNumberIfPresent($materialNumber)"/>
    
	<xsl:variable name="front_matter_uuid" select="concat($materialNumberVolume,'.','FrontMatter')" />

	<xsl:template match="x:root">
		<root>
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid"
					select="concat($materialNumberVolume,'.fm.title.page')" />
			</xsl:call-template>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:variable name="pageNumber" select="@serial-num" />
		<xsl:apply-templates select="(following::x:fm.highlights|following::x:fm.dedication|
									  following::x:fm.acknowledgment|following::x:fm.foreword|
									  following::x:fm.online.research.guide|following::x:fm.proview|
									  following::x:fm.related.products|following::x:fm.preface|
									  following::x:fm.about.the.author|following::x:fm.other.structure|
									  following::x:fm.copyright.page)[1]" mode="secBreak">
			<xsl:with-param name="pageNum" select="@serial-num" />
		</xsl:apply-templates>
		<xsl:copy-of select="." />
	</xsl:template>
	
	<xsl:template match="x:fm.highlights|x:fm.dedication|x:fm.acknowledgment|x:fm.foreword|x:fm.online.research.guide|x:fm.proview|x:fm.related.products|x:fm.preface" mode="secBreak">
		<xsl:param name="pageNum" />
		<xsl:if test="preceding::x:pagebreak[1]/@serial-num = $pageNum and not(preceding::x:t[preceding::x:pagebreak[1]/@serial-num = $pageNum])">
			<xsl:variable name="common_tags_uuid" select="concat($materialNumberVolume,'.', name(), x:getIdSuffix(current()))" />
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$common_tags_uuid" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="x:fm.about.the.author|x:fm.other.structure" mode="secBreak">
		<xsl:param name="pageNum" />
		<xsl:if test="preceding::x:pagebreak[1]/@serial-num = $pageNum and not(preceding::x:t[preceding::x:pagebreak[1]/@serial-num = $pageNum])">
			<xsl:variable name="other_label" select="string-join(./x:head[1]/x:name.block[1]/x:name[1]//x:t, ' ')" />
			<xsl:variable name="other_uuid" select="concat($materialNumberVolume,'.',name(), x:getIdSuffix(current()))" />
			<xsl:if test= "$other_label">
				<xsl:call-template name="placeSectionbreak">
					<xsl:with-param name="sectionuuid" select="$other_uuid" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="x:fm.copyright.page" mode="secBreak">
		<xsl:param name="pageNum" />
		<xsl:if test="preceding::x:pagebreak[1]/@serial-num = $pageNum">
			<xsl:variable name="uuid" select="concat($materialNumberVolume,'.',name())" />
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$uuid" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
    
	<xsl:template match="x:fm.title.page">
		<xsl:variable name="uuid" select="concat($materialNumberVolume,'.',name())" />
		<xsl:variable name="volNumber"
			select="substring($materialNumberVolume, string-length($materialNumberVolume), 1)" />

		<xsl:if test="$isMultiVolume">
			<xsl:element name="XPPHier">
				<xsl:attribute name="uuid" select="$materialNumberVolume" />
				<xsl:attribute name="name"
					select="concat($volNamePlaceholder, $volNumber)" />
				<xsl:attribute name="parent_uuid" select="$materialNumberVolume" />
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
    
	<xsl:template match="x:fm.isbn.issn.number"/>
	
	<xsl:template match="x:fm.copyright.page">
		<xsl:variable name="uuid" select="concat($materialNumberVolume,'.',name())" />
		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="'Copyright Page'" />
			<xsl:with-param name="parent_uuid" select="$uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid" />
		</xsl:call-template>
		
		<xsl:call-template name="insertISBNTemplate">
			<xsl:with-param name="isbnNumber" select="$isbn" />
		</xsl:call-template>
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>

		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$front_matter_uuid" />
			<xsl:with-param name="name" select="'Front matter'" />
			<xsl:with-param name="parent_uuid" select="$front_matter_uuid" />
		</xsl:call-template>
		
	</xsl:template>

	<xsl:template
		match="x:fm.highlights|x:fm.dedication|x:fm.acknowledgment|x:fm.foreword|x:fm.online.research.guide|x:fm.proview|x:fm.related.products|x:fm.preface">
		<xsl:variable name="common_tags_uuid" select="concat($materialNumberVolume,'.', name(), x:getIdSuffix(current()))" />
		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$common_tags_uuid" />
			<xsl:with-param name="name">
				<xsl:call-template name="defineTagTitle">
					<xsl:with-param name="tag_name" select="name()" />
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="parent_uuid" select="$front_matter_uuid" />
			<xsl:with-param name="doc_family_uuid" select="$common_tags_uuid" />
		</xsl:call-template>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:fm.about.the.author|x:fm.other.structure">
		<xsl:variable name="other_label"
			select="string-join(./x:head[1]/x:name.block[1]/x:name[1]//x:t, ' ')" />
		<xsl:variable name="other_uuid"
			select="concat($materialNumberVolume,'.',name(), x:getIdSuffix(current()))" />
		<xsl:if test= "$other_label">
			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$other_uuid" />
				<xsl:with-param name="name" select="$other_label" />
				<xsl:with-param name="parent_uuid" select="$front_matter_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$other_uuid" />
			</xsl:call-template>
		</xsl:if>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template name="defineTagTitle">
		<xsl:param name="tag_name" />
		<xsl:choose>
			<xsl:when test="$tag_name = 'fm.highlights'">
				<xsl:value-of select="'Introduction to Edition'" />
			</xsl:when>
			<xsl:when test="$tag_name = 'fm.dedication'">
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
			<xsl:when test="$tag_name = 'fm.preface'">
				<xsl:value-of select="'Preface'" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

    <xsl:function name="x:getIdSuffix">
        <xsl:param name="currentNode" as="node()"/>
        <xsl:variable name="tagPosition" select="count($currentNode/preceding-sibling::*[name() = $currentNode/name()])" />
        <xsl:choose>
            <xsl:when test="$tagPosition=0">
                <xsl:value-of select="''" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('.', $tagPosition)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="x:addVolumeNumberIfPresent">
        <xsl:param name="baseId"/>
        <xsl:value-of select="string-join(($baseId, $fileNameBasedVolumeNumber)[. != ''],'.vol')"/>
    </xsl:function>
</xsl:stylesheet>