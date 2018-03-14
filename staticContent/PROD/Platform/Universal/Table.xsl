<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Leader.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>



	<xsl:template match="tgroup" name="TGroupTemplate">
		<!--if checkNoColWidthExists is true, check if the colWidth attribute is not provided, then assume a 100% width. This is a bandaide to make sure other tests are not effected.-->
		<xsl:param name="checkNoColWidthExists"/>
		<xsl:param name="additionalClass"/>
		<xsl:choose>
			<xsl:when test="normalize-space(.) != '' or count(tbody/row/entry) != 1 or count(tbody/row/entry//image.block) != 1">
				<table>
					<xsl:variable name="class">
						<xsl:choose>
							<xsl:when test="parent::table/@frame='top' and not(preceding-sibling::tgroup)">
								<!-- if there are multiple tgroups in a table we only want to put a border on the top one -->
								<xsl:text>&borderTopClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:when>
							<xsl:when test="parent::table/@frame='bottom' and not(following-sibling::tgroup)">
								<!-- if there are multiple tgroups in a table we only want to put a border on the bottom one -->
								<xsl:text>&borderBottomClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:when>
							<xsl:when test="parent::table/@frame='topbot'">
								<xsl:if test="not(preceding-sibling::tgroup)">
									<!-- if there are multiple tgroups in a table we only want to put a border on the top one -->
									<xsl:text>&borderTopClass;</xsl:text>
									<xsl:text><![CDATA[ ]]></xsl:text>
								</xsl:if>
								<xsl:if test="not(following-sibling::tgroup)">
									<!-- if there are multiple tgroups in a table we only want to put a border on the bottom one -->
									<xsl:text>&borderBottomClass;</xsl:text>
									<xsl:text><![CDATA[ ]]></xsl:text>
								</xsl:if>
							</xsl:when>
							<xsl:when test="parent::table/@frame='all'">
								<xsl:if test="not(preceding-sibling::tgroup)">
									<!-- if there are multiple tgroups in a table we only want to put a border on the top one -->
									<xsl:text>&borderTopClass;</xsl:text>
									<xsl:text><![CDATA[ ]]></xsl:text>
								</xsl:if>
								<xsl:if test="not(following-sibling::tgroup)">
									<!-- if there are multiple tgroups in a table we only want to put a border on the bottom one -->
									<xsl:text>&borderBottomClass;</xsl:text>
									<xsl:text><![CDATA[ ]]></xsl:text>
								</xsl:if>
								<xsl:text>&borderLeftClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
								<xsl:text>&borderRightClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:when>
							<xsl:when test="parent::table/@frame='sides'">
								<xsl:text>&borderLeftClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
								<xsl:text>&borderRightClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:when>
						</xsl:choose>
						<xsl:if test="tbody/processing-instruction('ctbl')[contains(., 'ampex.horiz.rule')]">
							<xsl:text>&extraPaddingClass; &ampexHorizRule;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:if test="string-length($additionalClass) &gt; 0">
							<xsl:value-of select="$additionalClass"/>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:variable>

					<xsl:if test="string-length($class) &gt; 0">
						<xsl:attribute name="class">
							<xsl:value-of select="$class"/>
						</xsl:attribute>
					</xsl:if>

					<!-- table width -->
					<xsl:call-template name="RenderTableWidthStyles">
						<xsl:with-param name="checkNoColWidthExists" select="$checkNoColWidthExists"/>
					</xsl:call-template>

					<xsl:variable name="colspecCount" select="count(colspec)" />
					
					<xsl:variable name="moreColumnsThanDefined" select="node()[self::thead or self::tbody]/row[count(entry) &gt; $colspecCount][1]" />
					
					<xsl:variable name="columnInfo" select="colspec[not($moreColumnsThanDefined)]" />
					
					<xsl:variable name="proportionalTotal">
						<xsl:choose>
							<xsl:when test="contains($columnInfo/@colwidth, '*')">
								<xsl:call-template name="sumProportionalWidths">
									<xsl:with-param name="nodes" select="$columnInfo/@colwidth"/>
									<xsl:with-param name="total" select="0"/>
									<xsl:with-param name="index" select="1"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>0</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:apply-templates>
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
					</xsl:apply-templates>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<!--Allow standard image processing to take place on images in a table with no text-->
				<xsl:apply-templates select="tbody/row/entry/*" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tbl">
		<xsl:if test=".//text()">
			<div>
				<xsl:if test="@id or @ID">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', @id | @ID)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates />				
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="thead | tbody">
		<xsl:param name="columnInfo" />
		<xsl:param name="proportionalTotal"/>
			
		<xsl:apply-templates>
			<xsl:with-param name="columnInfo" select="$columnInfo"/>
			<xsl:with-param name="header">
				<xsl:if test="self::thead">
					<xsl:value-of select="true()"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--The row template below was named. priority="-1" is necessary to not break existing overriding code. -->
	<xsl:template match="row" name="rowCore" priority="-1">
		<xsl:param name="columnInfo" />
		<xsl:param name="header"/>
		<xsl:param name="proportionalTotal"/>
		<xsl:variable  select ="ancestor::tbl/@row-shade" name="alternate"/>
		<xsl:call-template name="BeforeRenderTableRow" />    
		<xsl:choose>
			<!-- To avoid chunking until after a header row -->
			<xsl:when test="parent::thead and count(preceding-sibling::row) = 0">   
				<xsl:call-template name="startUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking after the first row if there isn't a header row -->
			<xsl:when test="not(ancestor::tgroup[1]/thead) and parent::tbody and count(preceding-sibling::row) = 0">        
				<xsl:call-template name="startUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking between the last table row and the end of the table -->
			<xsl:when test="parent::tbody and count(following-sibling::row) = 1">
				<xsl:call-template name="startUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_trailer')" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		
		<tr>
			<xsl:if test ="$alternate = 'alt'">
				<xsl:attribute name="class">
					<xsl:value-of select="concat('co_stripeRow',(position() mod 2)+1)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$columnInfo">
					<xsl:variable name="row" select="." />
					<xsl:variable name="entries" select="entry" />
					<xsl:variable name="colspans" select="entry[@namest | @spanname]" />
					<xsl:for-each select="$columnInfo">
						<xsl:variable name="colname" select="@colname" />
						<xsl:variable name="colnumber" select="@colnum" />
						<xsl:variable name="rowspanOffset">
							<xsl:call-template name="GetRowspanOffset">
								<xsl:with-param name="row" select="$row" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:variable name="colposition" select="position() - $rowspanOffset" />
						<xsl:variable name="colalign" select="@align" />
						<xsl:variable name="colwidth">
							<xsl:call-template name="calcColWidth">
								<xsl:with-param name="columnInfo" select="$columnInfo"/>
								<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:variable name="render">
							<!-- Will stay empty if the column can render -->
							<xsl:choose>
								<xsl:when test="$colspans">
									<xsl:for-each select="$colspans">
										<xsl:variable name="startColInit">
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
										<xsl:variable name="endColInit">
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
										<xsl:variable name="startCol">
											<xsl:choose>
												<xsl:when test="string(number($startColInit)) != 'NaN'">
													<xsl:value-of select="$startColInit"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="0"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:variable>
										<xsl:variable name="endCol">
											<xsl:choose>
												<xsl:when test="string(number($endColInit)) != 'NaN'">
													<xsl:value-of select="$endColInit"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="0"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="number($colposition) &lt; number($startCol) or number($colposition) = number($startCol)" />
											<xsl:when test="number($colposition) &gt; number($endCol)" />
											<xsl:otherwise>
												<xsl:value-of select="false()"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:for-each>
								</xsl:when>
								<!-- determine if the cell should be skipped because it is in a rowspan area -->
								<xsl:when test="position() &lt;= $rowspanOffset">
									<xsl:value-of select="false()"/>
								</xsl:when>
							</xsl:choose>
						</xsl:variable>
						<xsl:if test="string-length($render) = 0">
							<xsl:choose>
								<xsl:when test="$colname and $entries[(@colname=$colname and not(@namest)) or (@colname and @namest=$colname)]">
									<xsl:variable name="thisEntry" select="$entries[(@colname=$colname and not(@namest)) or (@colname and @namest=$colname)]"/>
										<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$colnumber and $entries[@colnum=$colnumber]">
									<xsl:variable name="thisEntry" select="$entries[@colnum=$colnumber]"/>
										<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$colspans and count($entries) &lt; count($columnInfo) and not($entries[@colname] or $entries[@colnum])">
										<xsl:call-template name="findEntryAndRender">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="entries" select="$entries"/>
										<xsl:with-param name="index" select="1"/>
										<xsl:with-param name="colposition" select="$colposition"/>
										<xsl:with-param name="total" select="1"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="$entries[position()=$colposition] and 
											not($entries[position()=$colposition]/@colname) and 
											not($entries[position()=$colposition]/@colnum) and 
											not($entries[position()=$colposition]/@spanname) and 
											not($entries[position()=$colposition]/@namest) and 
											not($entries[position()=$colposition]/@nameend)">
									<xsl:variable name="thisEntry" select="$entries[position()=$colposition]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$entries[position()=$colposition] and 
											count($entries) = count($columnInfo) and
											(($entries[position()=$colposition]/@namest and 
											$entries[position()=$colposition]/@nameend) or
											$entries[position()=$colposition]/@spanname)">
									<xsl:variable name="thisEntry" select="$entries[position()=$colposition]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$entries[position()=$colposition] and 
											not($entries[position()=$colposition]/@colname != '') and 
											not($entries[position()=$colposition]/@colnum != '') and 
											not($entries[position()=$colposition]/@namest != '') and 
											not($entries[position()=$colposition]/@nameend != '')">
									<xsl:variable name="thisEntry" select="$entries[position()=$colposition]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$entries[@namest = $colname or @namest = $colnumber]">
									<xsl:variable name="thisEntry" select="$entries[@namest = $colname or @namest = $colnumber]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$entries[(@colname=$colname and string-length(@namest) =0 and string-length(@nameend) =0 )]">
									<xsl:variable name="thisEntry" select="$entries[(@colname=$colname)]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:otherwise>
									<!--this is an empty cell-->
									<xsl:call-template name="RenderEmptyCell">
										<xsl:with-param name="row" select="$row"/>
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates>
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="header" select="$header" />
						<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</tr>

		<xsl:choose>
			<!-- To avoid chunking until after a header row -->
			<xsl:when test="parent::thead and count(preceding-sibling::row) = 0">
				<xsl:call-template name="endUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking immediately after a header row (when combined with ancestoral <xsl:if test="parent::tbody">) -->
			<xsl:when test="parent::tbody and count(preceding-sibling::row) = 0">
				<xsl:call-template name="endUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking between the last table row and the end of the table (see template match for "row" to see the corresponding startUnchunkableBlock) -->
			<xsl:when test="parent::tbody and count(following-sibling::row) = 0">
				<xsl:call-template name="endUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_trailer')" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<xsl:call-template name="AfterRenderTableRow" />
	</xsl:template>

	<xsl:template name="RenderEmptyCell">
		<xsl:param name="row"/>
		<xsl:param name="columnInfo"/>
		<xsl:param name="colalign"/>
		<xsl:param name="colposition"/>
		<xsl:param name="colwidth"/>
		<xsl:choose>
			<xsl:when test="$row/parent::thead">
				<th>
					<xsl:call-template name="RenderTableCell">
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="colalign" select="$colalign" />
						<xsl:with-param name="colposition" select="$colposition" />
						<xsl:with-param name="colwidth" select="$colwidth" />
						<xsl:with-param name="row" select="$row" />
					</xsl:call-template>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</th>
			</xsl:when>
			<xsl:otherwise>
				<td>
					<xsl:call-template name="RenderTableCell">
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="colalign" select="$colalign" />
						<xsl:with-param name="colposition" select="$colposition" />
						<xsl:with-param name="colwidth" select="$colwidth" />
						<xsl:with-param name="row" select="$row" />
					</xsl:call-template>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tbody/row/entry">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		
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

	<xsl:template match="thead/row/entry | tgroup/row/entry">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<th>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
			</xsl:call-template>
		</th>
	</xsl:template>

	<xsl:template name="BeforeRenderTableRow" />

	<xsl:template name="AfterRenderTableRow" />

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
				<xsl:otherwise>
					<xsl:apply-templates/>
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
		<xsl:if test="not(string-length($colspan) &gt; 0) and $colwidth and string-length($colwidth) &gt; 0">
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

	<xsl:template name="GetRowSpan">
		<xsl:param name="entry" />
		<xsl:variable name="collection" select="/Document/document-data/collection" />
		<xsl:choose>
			<xsl:when test="string(number($entry/@morerows)) != 'NaN' and $collection = 'w_codesadcnyonvdp'">
				<xsl:value-of select="number($entry/@morerows) + 1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="wip.flag" />

	<xsl:template name="sumProportionalWidths">
		<xsl:param name="nodes" />
		<xsl:param name="total" />
		<xsl:param name="index" />
		<xsl:choose>
			<xsl:when test="$index &lt;= count($nodes)">
				<xsl:call-template name="sumProportionalWidths">
					<xsl:with-param name="nodes" select="$nodes"/>
					<xsl:with-param name="total" select="$total + number(substring-before($nodes[$index], '*'))"/>
					<xsl:with-param name="index" select="$index + 1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$total"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="calcColWidth">
		<xsl:param name="columnInfo"/>
		<xsl:param name="proportionalTotal"/>
		<xsl:choose>
			<xsl:when test="$proportionalTotal &gt; 0">
				<xsl:value-of select="concat(round(substring-before(@colwidth, '*') div $proportionalTotal * 100), '%')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@colwidth"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="findEntryAndRender">
		<xsl:param name="columnInfo" />
		<xsl:param name="entries" />
		<xsl:param name="index" />
		<xsl:param name="colposition" />
		<xsl:param name="total" />
		<xsl:param name="colalign" />
		<xsl:param name="colwidth" />
		<xsl:choose>
			<xsl:when test="$colposition = $total">
				<xsl:variable name="thisEntry" select="$entries[$index]"/>
				<xsl:apply-templates select="$thisEntry">
					<xsl:with-param name="columnInfo" select="$columnInfo"/>
					<xsl:with-param name="colalign" select="$colalign" />
					<xsl:with-param name="colposition" select="$colposition" />
					<xsl:with-param name="colwidth" select="$colwidth" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$total > count($columnInfo)" />
			<xsl:otherwise>
				<xsl:call-template name="findEntryAndRender">
					<xsl:with-param name="columnInfo" select="$columnInfo"/>
					<xsl:with-param name="entries" select="$entries" />
					<xsl:with-param name="index" select="$index + 1"/>
					<xsl:with-param name="colposition" select="$colposition"/>
					<xsl:with-param name="total">
						<xsl:choose>
							<xsl:when test="$entries[$index]/@namest | $entries[$index]/@spanname">
								<xsl:variable name="entry" select="$entries[$index]" />
								<xsl:variable name="startCol">
									<xsl:call-template name="GetColNumber">
										<xsl:with-param name="columnInfo" select="$columnInfo" />
										<xsl:with-param name="text">
											<xsl:choose>
												<xsl:when test="$entry/@namest">
													<xsl:value-of select="$entry/@namest"/>
												</xsl:when>
												<xsl:when test="$entry/@spanname">
													<xsl:variable name="currentSpanName" select="$entry/@spanname" />
													<xsl:value-of select ="ancestor::tgroup/spanspec[@spanname=$currentSpanName]/@namest"/>
												</xsl:when>
											</xsl:choose>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:variable>
								<xsl:variable name="endCol">
									<xsl:call-template name="GetColNumber">
										<xsl:with-param name="columnInfo" select="$columnInfo" />
										<xsl:with-param name="text">
											<xsl:choose>
												<xsl:when test="$entry/@nameend">
													<xsl:value-of select="$entry/@nameend"/>
												</xsl:when>
												<xsl:when test="$entry/@spanname">
													<xsl:variable name="currentSpanName" select="$entry/@spanname" />
													<xsl:value-of select ="ancestor::tgroup/spanspec[@spanname=$currentSpanName]/@nameend"/>
												</xsl:when>
											</xsl:choose>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:variable>
								<xsl:variable name="colDiff" select="number($endCol) - number($startCol)" />
								<xsl:choose>
									<xsl:when test="string(number($colDiff)) = 'NaN'">
										<xsl:value-of select="$total + 1" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$total + $colDiff + 1" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$total + 1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="colalign" select="$colalign" />
					<xsl:with-param name="colwidth" select="$colwidth" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GetColNumber">
		<xsl:param name="columnInfo" />
		<xsl:param name="colposition" select="'1'" />
		<xsl:param name="text" />

		<xsl:choose>
			<xsl:when test="$colposition &lt; (count($columnInfo) + 1)">
				<xsl:choose>
					<xsl:when test="$columnInfo[number($colposition)]/@colname = $text">
						<xsl:value-of select="number($colposition)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="GetColNumber">
							<xsl:with-param name="columnInfo" select="$columnInfo" />
							<xsl:with-param name="colposition" select="number($colposition) + 1" />
							<xsl:with-param name="text" select="$text" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains($text, 'col')">
						<xsl:value-of select="substring-after($text, 'col')"/>
					</xsl:when>
					<xsl:when test="number($text)">
						<xsl:value-of select="$text"/>
					</xsl:when>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- gets the number of columns that have cells from previous rows that extend into this row, does not account for specific columns 
			 can be overridden for content that supports table cell row spanning -->
	<xsl:template name="GetRowspanOffset">
		<xsl:param name="row" />
		<xsl:value-of select="0" />
	</xsl:template>
	
	<xsl:template name="RenderTableWidthStyles">
		<xsl:param name="checkNoColWidthExists" select="false()"/>
		<xsl:choose>
			<xsl:when test="$checkNoColWidthExists  and descendant::colspec[not(@colwidth)]">
				<xsl:attribute name="style">
					<xsl:text>width:100%;</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="descendant::colspec/@colwidth[contains(.,'%')] ">
				<xsl:attribute name="style">
					<xsl:text>width:100%;</xsl:text>
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
