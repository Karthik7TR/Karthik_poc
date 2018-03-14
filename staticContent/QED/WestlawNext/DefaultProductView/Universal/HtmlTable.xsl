<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Leader.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--*******************************************************************************************
		We only want to match table elements that are not in a tgroup (so that we don't
	      conflict with calls to the table template). This entire section is to handle HTML Markup 
		  for Tables in the document xml.  So far this was only pointed out for UK Journals
		  (Bug # 504260)
		*******************************************************************************************
	-->
	<xsl:template match="table[not(child::tgroup)]" name="HtmlTable">
		<xsl:apply-templates select="caption" mode="moveCaption" />
		<table>
			<xsl:attribute name="style">
				<xsl:text>width:100%;</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates />
		</table>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="tr">
		<tr valign="top">
			<xsl:apply-templates />
		</tr>
	</xsl:template>

	<xsl:template match="td|th">
		<!-- Create a copy of the td or th element and support the align, colspan, and rowspan attributes -->
		<xsl:element name="{name()}">
			<xsl:if test="@colspan and not(@colspan='NaN')">
				<xsl:attribute name="colspan">
					<xsl:value-of select="@colspan"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@rowspan">
				<xsl:attribute name="rowspan">
					<xsl:value-of select="@rowspan"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@width[contains(.,'%')]"> <!--  and name() = 'th' -->
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:', @width)" />
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="RenderHtmlTableCell" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderHtmlTableCell">
		<xsl:attribute name="class">
			<xsl:if test=".//leader">
				<xsl:text>&leaderTableCellClass;</xsl:text>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>

			<xsl:choose>
				<xsl:when test="@align = 'right'">
					<xsl:text>&alignHorizontalRightClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@align = 'left'">
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@align = 'center'">
					<xsl:text>&alignHorizontalCenterClass;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="name() = 'th'">
							<xsl:text>&alignHorizontalCenterClass;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&alignHorizontalLeftClass;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:choose>
				<xsl:when test="@valign = 'bottom'">
					<xsl:text>&alignVerticalBottomClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@valign = 'top'">
					<xsl:text>&alignVerticalTopClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@valign">
					<xsl:value-of select="concat('vAlignError_', @valign)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&alignVerticalTopClass;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="table/caption" mode="moveCaption">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="table/caption" />

	<!--*******************************************************************************************
		End of HTML templates for UK Journals
		*******************************************************************************************
	-->


</xsl:stylesheet>