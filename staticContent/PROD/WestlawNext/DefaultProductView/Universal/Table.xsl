<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Table.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="tbody/row/entry">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />

		<xsl:if test="(@namest | @spanname) and not(@colnum) and not(@colname)">
			<xsl:variable name="previousColumnNum">
				<xsl:choose>					
					<xsl:when test="preceding-sibling::entry">
						<xsl:call-template name="GetColNumber">
							<xsl:with-param name="columnInfo" select="$columnInfo" />
							<xsl:with-param name="text">
								<xsl:choose>
									<xsl:when test="@namest">
										<xsl:value-of select="@namest"/>
									</xsl:when>
									<xsl:when test="@spanname">
										<xsl:variable name="currentSpanName" select="@spanname" />
										<xsl:value-of select ="ancestor::tgroup/spanspec[@spanname=$currentSpanName]/@namest"/>
									</xsl:when>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="startColNum">
				<xsl:call-template name="GetColNumber">
					<xsl:with-param name="columnInfo" select="$columnInfo" />
					<xsl:with-param name="text">
						<xsl:choose>
							<xsl:when test="@namest">
								<xsl:value-of select="@namest"/>
							</xsl:when>
							<xsl:when test="@spanname">
								<xsl:variable name="currentSpanName" select="@spanname" />
								<xsl:value-of select ="ancestor::tgroup/spanspec[@spanname=$currentSpanName]/@namest"/>
							</xsl:when>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="numberOfMissingColumns">
				<xsl:value-of select="number($startColNum) - number($previousColumnNum) - 1"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$numberOfMissingColumns &gt; 1">
					<td>
						<xsl:attribute name="colspan">
							<xsl:value-of select="$numberOfMissingColumns"/>
						</xsl:attribute>
					</td>
				</xsl:when>
				<xsl:when test="$numberOfMissingColumns &gt; 0">
					<td/>
				</xsl:when>
			</xsl:choose>			
		</xsl:if>

		<xsl:variable name="rowspan">
			<xsl:call-template name="GetRowSpan">
				<xsl:with-param name="entry" select="." />
			</xsl:call-template>
		</xsl:variable>

		<td>
			<xsl:if test="$rowspan and number($rowspan) > 1">
				<xsl:attribute name="rowspan">
					<xsl:value-of select="$rowspan" />
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
			</xsl:call-template>
		</td>
	</xsl:template>

	<xsl:template name="RenderTableCell">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:param name="row" />
		<xsl:param name="class" />
		<xsl:param name="contents">
			<xsl:choose>
				<xsl:when test="leader">
					<xsl:call-template name="leaderContent">
						<xsl:with-param name="parent" select="." />
					</xsl:call-template>
				</xsl:when>
				<!-- Documents coming from Novus can have actual 0x00A0 characters. 
						 Turn them into &#160; when within tables so browsers don't assume 
						 table cells are empty and therefore have no height. -->
				<xsl:when test="normalize-space(.) = '&#160;'">
					<xsl:text disable-output-escaping="yes"><![CDATA[&#160;]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="DefineContent"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
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
				<xsl:when test="$colalign = 'right'">
					<xsl:text>&alignHorizontalRightClass;</xsl:text>
				</xsl:when>
				<xsl:when test="$colalign = 'left'">
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				</xsl:when>
				<xsl:when test="$colalign = 'center'">
					<xsl:text>&alignHorizontalCenterClass;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
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
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:call-template name="AddTableCellBorderClasses">
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="row" select="$row" />
			</xsl:call-template>
			<xsl:value-of select="$class"/>
		</xsl:attribute>
		<xsl:variable name="colspan">
			<xsl:if test="@namest | @spanname">
				<xsl:variable name="startCol">
					<xsl:call-template name="GetColNumber">
						<xsl:with-param name="columnInfo" select="$columnInfo" />
						<xsl:with-param name="text">
							<xsl:choose>
								<xsl:when test="@namest">
									<xsl:value-of select="@namest"/>
								</xsl:when>
								<xsl:when test="@spanname">
									<xsl:variable name="currentSpanName" select="@spanname" />
									<xsl:value-of select ="ancestor::tgroup/spanspec[@spanname=$currentSpanName]/@namest"/>
								</xsl:when>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:if test="number($startCol) = $colposition">
					<xsl:variable name="endCol">
						<xsl:call-template name="GetColNumber">
							<xsl:with-param name="columnInfo" select="$columnInfo" />
							<xsl:with-param name="text">
								<xsl:choose>
									<xsl:when test="@nameend">
										<xsl:value-of select="@nameend"/>
									</xsl:when>
									<xsl:when test="@spanname">
										<xsl:variable name="currentSpanName" select="@spanname" />
										<xsl:value-of select ="ancestor::tgroup/spanspec[@spanname=$currentSpanName]/@nameend"/>
									</xsl:when>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<xsl:for-each select="$columnInfo">
						<xsl:if test="number($endCol) = position()">
							<xsl:value-of select="position() - $colposition + 1"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="string-length($colspan) &gt; 0">
			<xsl:attribute name="colspan">
				<xsl:value-of select="$colspan"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="not(number($colspan) &gt; 1) and $colwidth and string-length($colwidth) &gt; 0">
			<xsl:attribute name="style">
				<xsl:value-of select="concat('width:', $colwidth)"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:variable  select ="ancestor::tbl/@row-shade" name="alternateCheck"/>
		<xsl:choose>
			<xsl:when test ="$alternateCheck = 'alt'">
				<xsl:choose>
					<xsl:when test="normalize-space()">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<br />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$contents"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="AddTableCellBorderClasses">
		<xsl:param name="row" />
		<xsl:param name="colposition"/>
		<xsl:if test="@rowsep = '1' or parent::row/@rowsep = '1' or ($row and $row/@rowsep = '1') or ancestor::table[1]/@rowsep = '1'">
			<xsl:text>&borderBottomClass;</xsl:text>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="@colsep = '1'">
			<xsl:text>&borderRightClass;</xsl:text>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!--Creates cell items to be displayed-->
	<xsl:template name="DefineContent" >
		<xsl:apply-templates/>
	</xsl:template>

</xsl:stylesheet>
