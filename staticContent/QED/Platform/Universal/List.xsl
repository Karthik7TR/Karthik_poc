<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="list" name="list" priority="-1">
		<xsl:param name="class"/>
		<xsl:if test="list.item">
			<ul class="{$class}">
				<xsl:apply-templates select="node()[not(self::list)]" />
			</ul>
		</xsl:if>
		<xsl:if test="li">
				<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="list">
		<xsl:param name="class" select="'&listClass;'"/>
		<xsl:call-template name="list">
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="list/list" name="listNested" priority="-1">
		<xsl:param name="class"/>
		<xsl:if test="list.item">
			<ul class="{$class}">
				<xsl:apply-templates select="node()[not(self::list)]" />
			</ul>
			<xsl:if test="following-sibling::node()[not(self::text())][1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[not(self::text())][1]" />
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list/list">
		<xsl:param name="class" select="'&listClass;'"/>
		<xsl:call-template name="listNested">
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="list/list.item | entity.link/list.item">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[not(self::text())][1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[not(self::text())][1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<!-- This is the special case for List/para, do not move this template to para.xsl-->
	<xsl:template match="para[parent::list]" priority="3">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[not(self::text())][1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[not(self::text())][1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="list/head | list/list.head" priority="2">
		<li class="&listHeadClass;">
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template match="list.item/label.designator">
		<span class="&labelClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<!--subsection code-->

	<xsl:template match="subsection">
		<xsl:choose>
			<xsl:when test="child::subsection">
				<ul class="&listClass;">
					<xsl:apply-templates select="node()[not(self::subsection)]" />
				</ul>
			</xsl:when>
			<xsl:when test="parent::subsection">
				<li>
					<xsl:apply-templates select="node()[not(self::subsection)]" />
				</li>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


</xsl:stylesheet>
