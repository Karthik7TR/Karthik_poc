<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format"
				xmlns:html="http://www.w3.org/1999/xhtml"
				xmlns:pcl="http://xmlgraphics.apache.org/fop/extensions/pcl"
				exclude-result-prefixes="html">
	<!-- Import the platform FO transform so we can override named template-->
	<xsl:import href="xhtml2fo.xsl" forcePlatform="true" />

	<xsl:output method="xml"
				version="1.0"
				encoding="UTF-8"
				indent="no"
				omit-xml-declaration="yes" />

	<xsl:param name="isInlineHtml" select="false()" />
	<xsl:param name="IAC-INLINEHTMLDELIVERY-DOCKETTRACKALERT" select="false()" />
	<xsl:param name="la-label-1" select="'a.'" />

	<xsl:template match="html:table[@id='co_endOfDocument']">
		<fo:block>
			<fo:table table-layout="fixed" border-top="0.5pt solid #777777" id="co_endOfDocument_1" margin-top="1.0em"
					  space-before="1.0em" padding-top="1.0em" font-weight="bold" color="#777777" clear="both"
					  margin-right="0px" margin-bottom="0px" margin-left="0px" border-collapse="collapse" font-size="1.0em"
					  border-spacing="0">
				<fo:table-column column-width="216px" />
				<fo:table-column column-width="288px" />
				<fo:table-body start-indent="0pt" end-indent="0pt" text-indent="0pt" last-line-end-indent="0pt" text-align="start">
					<xsl:apply-templates />
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>

	<xsl:template name="process-advanced-id">
		<xsl:param name="contextNode" select="." />
		<xsl:param name="idPostfix" select="''" />

		<xsl:variable name="rawId">
			<xsl:choose>
				<xsl:when test="$contextNode/@id">
					<xsl:value-of select="$contextNode/@id" />
				</xsl:when>
				<xsl:when test="$contextNode/self::html:a/@name">
					<xsl:value-of select="$contextNode/@name" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="exactId" select="concat('#', $rawId)" />
		<xsl:variable name="isReferenced" select="count(//html:a[@href = $exactId]) > 0" />

		<xsl:variable name="isNote"
					  select="contains($rawId, 'headnotesTable') or contains($rawId, 'co_inlineFootnote') or contains($rawId, 'co_note_')" />
		<xsl:variable name="isQuestion" select="contains($contextNode/parent::html:div/@class, 'kh_question')" />
		<xsl:variable name="isExceptional" select="$isNote or $isQuestion" />

		<xsl:if test="normalize-space($rawId) != ''">
			<xsl:if test="$isReferenced or $isExceptional">
				<xsl:attribute name="id">
					<xsl:value-of select="$rawId" />
					<xsl:copy-of select="$idPostfix" />
				</xsl:attribute>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Override is required to fix an issue with empty lines in the beginning of the document for PLCAU -->
	<xsl:template name="process-id">
		<xsl:param name="contextNode" select="." />
		<xsl:param name="idPostfix" select="''" />

		<xsl:choose>
			<xsl:when test="$productView = '&productViewNameAU;' or $productView = '&productViewNameUK;'">
				<xsl:call-template name="process-advanced-id">
					<xsl:with-param name="contextNode" select="$contextNode"></xsl:with-param>
					<xsl:with-param name="idPostfix" select="$idPostfix"></xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="process-common-id">
					<xsl:with-param name="contextNode" select="$contextNode"></xsl:with-param>
					<xsl:with-param name="idPostfix" select="$idPostfix"></xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- WLN template for matching WLN specific elements that get their own page sequence.  Add any elements
	to be transformed into a page to the match here that are specific to WLN.  e.g. Matter Benchmark reports
	Also, <page-sequences> can't have inner page sequences nor can they have content elements outside of them.  
	Make sure this template matches the outer most content element of the document.
	-->
	<xsl:template match="html:div[starts-with(@id, 'co_matterBenchmarkingReport')]" priority="2">
		<xsl:call-template name="buildPageSequence" />
	</xsl:template>

	<!-- Put feature specific FO templates here-->

	<!-- Start Trillium Commentary feature templates -->

	<!-- Fix right sided bullets to left sided for Research References block -->
	<!-- Bug 974933:US - Delivery has style discrepancies -->
	<xsl:template
		match="html:div[contains(@class, '&commentaryDocumentClass;')]//html:div[contains(@class, '&researchReferenceBlockClass;')]//html:ul/html:li"
		priority="2">
		<fo:list-item>
			<xsl:call-template name="process-common-attributes" />
			<fo:list-item-label text-align="start" wrap-option="no-wrap" end-indent="label-end()">
				<fo:block>
					<xsl:variable name="listItemType">
						<xsl:variable name="nodeListItemType">
							<xsl:call-template name="getValueOfStyleFromStyleAttribute">
								<xsl:with-param name="styleAttribute" select="@style" />
								<xsl:with-param name="styleName" select="'list-style-type'" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="string-length($nodeListItemType) &gt; 0">
								<xsl:value-of select="$nodeListItemType" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="getValueOfStyleFromStyleAttribute">
									<xsl:with-param name="styleAttribute" select="ancestor::html:ul[1]/@style" />
									<xsl:with-param name="styleName" select="'list-style-type'" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$listItemType = 'none'">
							<fo:inline>&#160;</fo:inline>
						</xsl:when>
						<xsl:when test="$listItemType = 'disc'">
							<fo:inline>&#8226;</fo:inline>
						</xsl:when>
						<xsl:when test="$listItemType = 'circle'">
							<fo:inline>&#x006F;</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline>
								<xsl:value-of select="$ul-label-1" />
							</fo:inline>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
					<xsl:apply-templates />
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>

	<!-- End Trillium Commentary feature templates-->

	<!-- Start Napa feature templates-->

	<!-- Napa LI elements.  Changes from platform include left sided bullets and margin spacing. -->
	<xsl:template name="getUlList"
				  match="html:div[@id='co_article' or contains(@class, 'co_medLitMicromedex')]//html:ul/html:li | html:li[ancestor::html:ul[contains(@class, '&khList;')]] | html:div[contains(@class,'co_noteText')]//html:ul/html:li | html:div[contains(@class,'co_noteText')]//html:ol/html:li"
				  priority="2">
		<fo:list-item>
			<xsl:variable name="isOrderedList" select="name(..)='ol'" />
			<xsl:call-template name="process-common-attributes" />
			<fo:list-item-label text-align="start" wrap-option="no-wrap" end-indent="label-end()">
				<fo:block>
					<xsl:variable name="listItemType">
						<xsl:variable name="nodeListItemType">
							<xsl:call-template name="getValueOfStyleFromStyleAttribute">
								<xsl:with-param name="styleAttribute" select="@style" />
								<xsl:with-param name="styleName" select="'list-style-type'" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="string-length($nodeListItemType) &gt; 0">
								<xsl:value-of select="$nodeListItemType" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$isOrderedList">
										<xsl:call-template name="getValueOfStyleFromStyleAttribute">
											<xsl:with-param name="styleAttribute" select="ancestor::html:ol[1]/@style" />
											<xsl:with-param name="styleName" select="'list-style-type'" />
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="getValueOfStyleFromStyleAttribute">
											<xsl:with-param name="styleAttribute" select="ancestor::html:ul[1]/@style" />
											<xsl:with-param name="styleName" select="'list-style-type'" />
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$listItemType = 'none'">
							<fo:inline>&#160;</fo:inline>
						</xsl:when>
						<xsl:when test="$listItemType = 'disc'">
							<fo:inline>&#8226;</fo:inline>
						</xsl:when>
						<xsl:when test="$listItemType = 'circle'">
							<fo:inline>&#x006F;</fo:inline>
						</xsl:when>
						<xsl:when test="$listItemType = 'upper-alpha'">
							<xsl:number format="{$ua-label-1}" />
						</xsl:when>
						<xsl:when test="$listItemType = 'lower-alpha'">
							<xsl:number format="{$la-label-1}" />
						</xsl:when>
						<xsl:otherwise>
							<fo:inline>
								<xsl:choose>
									<xsl:when test="$isOrderedList">
										<xsl:number format="{$ol-label-1}" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$ul-label-1" />
									</xsl:otherwise>
								</xsl:choose>
							</fo:inline>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<xsl:call-template name="BuildListItemBodyContents" />
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>

	<xsl:template name="BuildListItemBodyContents">
		<fo:block>
			<xsl:apply-templates />
		</fo:block>
	</xsl:template>

	<xsl:template name="indent-list-block">
		<xsl:attribute name="provisional-distance-between-starts">0.6cm</xsl:attribute>
		<xsl:attribute name="provisional-label-separation">0.15cm</xsl:attribute>
	</xsl:template>

	<!-- Including KnowHow as platform list have RIGHT aligned bullets. -->
	<xsl:template match="html:ul[contains(@class, '&khList;')] | html:ol[contains(@class, '&khList;')]" priority="1">
		<xsl:choose>
			<xsl:when test="($isRTF or $isMSWord) and name() = 'ol'">
				<fo:list-block>
					<xsl:if test="$productView = '&productViewNameAU;'">
						<xsl:call-template name="indent-list-block" />
					</xsl:if>
					<xsl:for-each select="./node()[name() = 'li']">
						<xsl:call-template name="getUlList"/>
					</xsl:for-each>
				</fo:list-block>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="./node()[name() = 'li']">
					<fo:list-block>
						<xsl:if test="$productView = '&productViewNameAU;'">
							<xsl:call-template name="indent-list-block" />
						</xsl:if>
						<xsl:call-template name="getUlList"/>
					</fo:list-block>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Render Napa Attorney Bio -->
	<xsl:template match="html:ul[@class='&inlineList;']" priority="1">
		<fo:block>
			<xsl:call-template name="process-common-attributes">
				<xsl:with-param name="contextNode" select="."/>
			</xsl:call-template>
			Updated:
			<xsl:variable name="updatedDate" select=".//html:li[@class='&updated;']" />
			<xsl:value-of select="substring-after($updatedDate, 'UPDATED:')" />
		</fo:block>
	</xsl:template>

	<xsl:template match="html:div[@id='co_author']" priority="1">
		<fo:block>
			<xsl:variable name ="authorImageURL">
				<xsl:value-of select=".//html:div[@class='&authorThumb;']/html:a/html:img/@src"/>
			</xsl:variable>

			<xsl:variable name ="firmImageURL">
				<xsl:value-of select=".//html:div[@class='&authorLogo;']/html:a/html:img/@src"/>
			</xsl:variable>

			<fo:table>
				<fo:table-column column-width="60px" />
				<fo:table-column column-width="250px" />
				<fo:table-column column-width="150px" />
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell padding-top="19px">
							<fo:block>
								<fo:external-graphic src="url('{$authorImageURL}')" content-width="scale-to-fit" content-height="100%" width="100%" scaling="uniform"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="19px" padding-left="11px">
							<fo:block>
								<fo:block>
									<xsl:call-template name="process-common-attributes">
										<xsl:with-param name="contextNode" select=".//html:ul/html:li[@class='&byLine;']"/>
									</xsl:call-template>
									by
									<xsl:apply-templates select=".//html:ul/html:li[@class='&byLine;']/html:a" /><xsl:text><![CDATA[ ]]></xsl:text>
									<fo:block></fo:block>
									<xsl:value-of select=".//html:ul/html:li[@class='&byLine;']/html:span[@id='co_authorTitle']" />
									<xsl:if test=".//html:ul/html:li[@class='&byLine;']/html:span[@id='co_firmName'] != ''">
										,
										<xsl:value-of select=".//html:ul/html:li[@class='&byLine;']/html:span[@id='co_firmName']" />
									</xsl:if>
								</fo:block>
								<xsl:if test=".//html:ul//html:span[@id='co_authorPhoneNumber'] != ''">
									<fo:block>
										<xsl:value-of select=".//html:ul//html:span[@id='co_authorPhoneNumber']"/>
									</fo:block>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="19px">
							<fo:block text-align="right">
								<fo:external-graphic src="url('{$firmImageURL}')" content-width="scale-to-fit" content-height="100%" width="100%" scaling="uniform"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>

	<!-- Remove the unnecessary articleHead container element.  Instead just
	process the children. It's adding too much space to the top of the document -->
	<xsl:template match="html:div[@id='co_articleHead']">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="html:div[@class='&paraMainClass;']">
		<fo:block>
			<xsl:call-template name="process-id" />
			<xsl:apply-templates/>
		</fo:block>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- Don't render <li> element not intended for delivery-->
	<xsl:template match="html:li[@noDeliver='true']" priority="1" />

	<!-- Don't render <div> element not intended for delivery-->
	<xsl:template match="html:div[@noDeliver='true']" priority="1" />

	<!-- End Napa feature templates-->

	<!-- Start Arbitration 2 Feature templates -->

	<!-- Remove chart selector. This does not apply to delivery -->
	<xsl:template match="html:div[@id='arbChartSelectorWrapper']" priority="1" />

	<!-- Hide charts not intended for Delivery -->
	<xsl:template match="html:div[starts-with(@id, 'co_arbChartContainer') and not(@name)]" priority="1" />

	<!-- Build chart image Url for charts to deliver -->
	<xsl:template match="html:div[starts-with(@id, 'co_arbChartContainer') and @name]" priority="1">
		<xsl:variable name="DocumentGuid" select="../html:input[@id='documentGuid']/@value" />
		<xsl:variable name="WebsiteHost" select="../html:input[@id='websiteHost']/@value" />
		<xsl:variable name="chartType" select="self::node()/@name" />

		<fo:block>
			<xsl:call-template name="addDualColumnInstructions"/>
			<xsl:call-template name="process-common-attributes"/>

			<xsl:apply-templates select="html:h2"/>

			<fo:external-graphic>
				<xsl:attribute name="content-width">
					<xsl:text>scale-to-fit</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="width">
					<xsl:text>440</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="src">
					<xsl:text>url('</xsl:text>
					<xsl:value-of select="concat($WebsiteHost, '/arbitration/v1/chart/', $DocumentGuid, '/', $chartType, '/Large')" />
					<xsl:text>')</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="role">
					<xsl:text>Chart Image</xsl:text>
				</xsl:attribute>
			</fo:external-graphic>

			<xsl:apply-templates select="*[not(self::html:h2)]"/>
		</fo:block>
	</xsl:template>

	<!-- End Arbitration 2 Feature templates -->

	<!-- Enhancement 446607 : Add ALJ Name to header section in admin decisions content type -->
	<xsl:template match="html:div[@class='&adminLawJudgeClass;']">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Fix for text overrun in the delivery pdf for contents that use h3 & h4 blocks. Changed keep-with-next.within-column from "always" to "auto" -->
	<xsl:attribute-set name="h3">
		<xsl:attribute name="font-size">
			<xsl:text>1.17em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>auto</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!-- Fix for text overrun in the delivery pdf for contents that use h3 & h4 blocks. Changed keep-with-next.within-column from "always" to "auto" -->
	<xsl:attribute-set name="h4">
		<xsl:attribute name="font-size">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>auto</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:template name="getTableWidths">
		<xsl:param name="table" />
		<xsl:param name="columnData" />

		<xsl:variable name="pixelWidth">
			<xsl:variable name="parentTable" select="$table/ancestor::html:table[1]" />

			<xsl:choose>
				<xsl:when test="$parentTable">
					<!-- If there is a parent table, first we need to get the column widths for it -->
					<xsl:variable name="parentWidths">
						<xsl:call-template name="getTableWidths">
							<xsl:with-param name="table" select="$parentTable" />
							<xsl:with-param name="columnData">
								<xsl:call-template name="getColumnData">
									<xsl:with-param name="table" select="$parentTable" />
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<!-- Get the index for the parent column -->
					<xsl:variable name="parentCellIndex">
						<xsl:call-template name="findParentCellIndex">
							<xsl:with-param name="tr" select="$table/ancestor::html:tr[1]"/>
							<xsl:with-param name="cell" select="$table/ancestor::node()[self::html:td or self::html:th][1]" />
							<xsl:with-param name="currentCell" select="1" />
							<xsl:with-param name="currentColumn" select="1" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="number($parentCellIndex) = 0">
							<!-- Couldn't find a parent index, default width -->
							<xsl:value-of select="number($usable-page-width-px)"/>
						</xsl:when>
						<xsl:otherwise>
							<!-- Find the width for the column from the list using the index -->
							<xsl:call-template name="findParentCellWidthFromIndex">
								<xsl:with-param name="parentWidths" select="$parentWidths" />
								<xsl:with-param name="index" select="$parentCellIndex" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<!-- Adding the following adjustment to the table width if the table is inside a blockquote
				Each blockquote adds start and end indent of 24pt making it 48pt. 48pt=64px so subtracting 64px
				from the table width in case of pdf documents, this problem is not for word/rtf Bug# 532462 -->
				<xsl:when test="$table/ancestor::html:blockquote and not($isRTF or $isMSWord)">
					<xsl:variable name="decreaseindent" select="count($table/ancestor::html:blockquote)"/>
					<xsl:value-of select="number($usable-page-width-px) - (number($decreaseindent) * 64)"/>
				</xsl:when>
				<!-- know how adjust width for table within box -->
				<xsl:when test="$table/ancestor::html:div[@class='kh_division kh_box']">
					<xsl:value-of select="number($usable-page-width-px - 60)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number($usable-page-width-px)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="goodWidths" select="not(starts-with($columnData, '|') or substring($columnData, string-length($columnData)) = '|' or contains($columnData, '||') or string-length($columnData) = 0)" />

		<xsl:variable name="totalWidth">

			<xsl:choose>
				<xsl:when test="$goodWidths">
					<xsl:call-template name="addColumnWidthsInColumnData">
						<xsl:with-param name="columnData" select="$columnData" />
						<xsl:with-param name="total" select="0" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- If we can't add up the widths we need to count columns -->
					<xsl:call-template name="countColumnsInColumnData">
						<xsl:with-param name="columnData" select="$columnData" />
						<xsl:with-param name="total" select="0" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="contains($table/@class, 'co_detailsTable')">
				<xsl:value-of select="$columnData"/>
			</xsl:when>
			<xsl:when test="contains($table/@id, 'headnotesTable') or contains($table/@id, 'co_inlineFootnote')">
				<xsl:call-template name="determineColumnWidthsFromTotalsForHeadnotes">
					<xsl:with-param name="columnData" select="$columnData" />
					<xsl:with-param name="totalWidth" select="number($totalWidth)" />
					<xsl:with-param name="pixelWidth">
						<xsl:choose>
							<xsl:when test="contains($page-master-reference, 'DualColumn')">
								<xsl:value-of select="number(($pixelWidth) div 2) - number($dualColumnGutter)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="number($pixelWidth)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="goodWidths" select="$goodWidths" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!-- The first pass calculates the columns as normal, but ensures a minimum column size of 25px.
				However, that means this first pass is able to get wider than $pixelWidth (which is wider than the parent container's width).
				The second pass renormalizes the columns down to fit within $pixelWidth. The columns can/will be smaller than 25px at this point.
				-->
				<xsl:variable name="columnWidthsFirstPass">
					<xsl:call-template name="determineColumnWidthsFromTotals">
						<xsl:with-param name="columnData" select="$columnData" />
						<xsl:with-param name="totalWidth" select="number($totalWidth)" />
						<xsl:with-param name="pixelWidth" select="number($pixelWidth)" />
						<xsl:with-param name="goodWidths" select="$goodWidths" />
						<xsl:with-param name="minWidth" select="'25'" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="determineColumnWidthsFromTotals">
					<xsl:with-param name="columnData" select="$columnWidthsFirstPass"/>
					<xsl:with-param name="totalWidth">
						<xsl:call-template name="addColumnWidthsInColumnData">
							<xsl:with-param name="columnData" select="$columnWidthsFirstPass" />
							<xsl:with-param name="total" select="0" />
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="pixelWidth" select="number($pixelWidth)" />
					<xsl:with-param name="goodWidths" select="true()" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-table">
		<xsl:if test="@width">
			<xsl:attribute name="inline-progression-dimension">
				<xsl:choose>
					<xsl:when test="contains(@width, '%')">
						<xsl:value-of select="@width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(@width, 'px')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@border or @frame">
			<xsl:choose>
				<xsl:when test="@border &gt; 0">
					<xsl:attribute name="border">
						<xsl:value-of select="concat(@border, 'px')"/>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="@border = '0' or @frame = 'void'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'above'">
					<xsl:attribute name="border-style">
						<xsl:text>outset hidden hidden hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'below'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden hidden outset hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'hsides'">
					<xsl:attribute name="border-style">
						<xsl:text>outset hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'vsides'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden outset</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'lhs'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden hidden hidden outset</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'rhs'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden outset hidden hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="border-style">
						<xsl:text>outset</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="@cellspacing">
			<xsl:attribute name="border-spacing">
				<xsl:value-of select="concat(@cellspacing, 'px')"/>
			</xsl:attribute>
			<xsl:attribute name="border-collapse">
				<xsl:text>separate</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@rules
				 and (@rules = 'groups'
				   or @rules = 'rows'
				   or @rules = 'cols'
				   or @rules = 'all'
				   and (not(@border or @frame) or @border = '0' or @frame and not(@frame = 'box' or @frame = 'border')))">
			<xsl:attribute name="border-collapse">
				<xsl:text>collapse</xsl:text>
			</xsl:attribute>
			<xsl:if test="not(@border or @frame)">
				<xsl:attribute name="border-style">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:if>

		<xsl:variable name="columnData">
			<xsl:call-template name="getColumnData">
				<xsl:with-param name="table" select="." />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="process-common-attributes"/>

		<!-- test if number of columns is greater than 8, update font-size -->
		<xsl:variable name="numberOfColumns">
			<xsl:call-template name="countColumnsInColumnData">
				<xsl:with-param name="columnData" select="$columnData" />
				<xsl:with-param name="total" select="0" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$numberOfColumns &gt; 12">
				<xsl:attribute name="font-size">
					<xsl:text>55%</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$numberOfColumns &gt; 8">
				<xsl:attribute name="font-size">
					<xsl:text>65%</xsl:text>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$numberOfColumns &gt; 4">
				<xsl:attribute name="font-size">
					<xsl:text>75%</xsl:text>
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>

		<xsl:variable name="tableWidths">
			<xsl:call-template name="getTableWidths">
				<xsl:with-param name="table" select="." />
				<xsl:with-param name="columnData" select="$columnData" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="getColGroupElements">
			<xsl:with-param name="tableWidths" select="$tableWidths" />
		</xsl:call-template>
		<xsl:apply-templates select="html:thead"/>
		<xsl:choose>
			<xsl:when test="html:tbody">
				<xsl:apply-templates select="html:tbody"/>
			</xsl:when>
			<xsl:otherwise>
				<fo:table-body xsl:use-attribute-sets="tbody">
					<xsl:apply-templates select="html:tr"/>
				</fo:table-body>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="html:tfoot"/>
	</xsl:template>

	<xsl:template name="process-table-cell">
		<xsl:if test="@colspan">
			<xsl:attribute name="number-columns-spanned">
				<xsl:value-of select="@colspan"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-rowspan-attribute"/>
		<xsl:for-each select="ancestor::html:table[1]">
			<xsl:if test="(@border or @rules) and (@rules = 'all' or not(@rules) and not(@border = '0'))">
				<xsl:attribute name="border-style">
					<xsl:text>inset</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@cellpadding">
				<xsl:attribute name="padding">
					<xsl:choose>
						<xsl:when test="contains(@cellpadding, '%')">
							<xsl:value-of select="@cellpadding"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat(@cellpadding, 'px')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="not(@align or ../@align or ../parent::*[self::html:thead or self::html:tfoot or self::html:tbody]/@align)
				and ancestor::html:table[1]/*[self::html:col or self::html:colgroup]/descendant-or-self::*/@align">
			<xsl:attribute name="text-align">
				<xsl:text>from-table-column()</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="not(@valign or ../@valign or ../parent::*[self::html:thead or self::html:tfoot or self::html:tbody]/@valign)
				and ancestor::html:table[1]/*[self::html:col or self::html:colgroup]/descendant-or-self::*/@valign">
			<xsl:attribute name="display-align">
				<xsl:text>from-table-column()</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="relative-align">
				<xsl:text>from-table-column()</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes"/>
		<xsl:choose>
			<xsl:when test="$IAC-INLINEHTMLDELIVERY-DOCKETTRACKALERT">
				<xsl:choose>
					<xsl:when test="not($isWordPerfect) and not($isInlineHtml) and ancestor::html:table[1]">
						<fo:block-container>
							<fo:block>
								<xsl:call-template name="process-cell"/>
							</fo:block>
						</fo:block-container>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<xsl:apply-templates/>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="not($isWordPerfect) and ancestor::html:table[1]">
						<fo:block-container>
							<fo:block>
								<xsl:call-template name="process-cell"/>
							</fo:block>
						</fo:block-container>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<xsl:apply-templates/>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-rowspan-attribute">
		<xsl:if test="@rowspan and contains(ancestor::html:table[1]/@id, 'co_doc_coverpageTable')">
			<xsl:attribute name="number-rows-spanned">
				<xsl:value-of select="@rowspan"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="determineColumnWidthsFromTotals">
		<xsl:param name="columnData" />
		<xsl:param name="totalWidth" />
		<xsl:param name="pixelWidth" />
		<xsl:param name="goodWidths" />
		<xsl:param name="minWidth" select="0" />

		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="$goodWidths">
					<xsl:variable name="tempWidth" select="substring-before($columnData, '|')" />
					<xsl:choose>
						<xsl:when test="string-length($tempWidth) &gt; 0">
							<xsl:value-of select="number(translate($tempWidth, 'pxPXemsEMSptPT%', ''))"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number(translate($columnData, 'pxPXemsEMSptPT%', ''))" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>1</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="percentage" select="number($width) div number($totalWidth)" />
		<xsl:variable name="columnWidth" select="round($pixelWidth * $percentage)" />

		<xsl:choose>
			<xsl:when test="number($columnWidth) &lt; number($minWidth)">
				<xsl:value-of select="number($minWidth)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number($columnWidth)"/>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:text>px</xsl:text>

		<xsl:if test="contains($columnData, '|')">
			<xsl:text>|</xsl:text>
			<xsl:call-template name="determineColumnWidthsFromTotals">
				<xsl:with-param name="columnData" select="substring-after($columnData, '|')" />
				<xsl:with-param name="totalWidth" select="number($totalWidth)" />
				<xsl:with-param name="pixelWidth" select="number($pixelWidth)" />
				<xsl:with-param name="goodWidths" select="$goodWidths" />
				<xsl:with-param name="minWidth" select="$minWidth" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:div[starts-with(@id, 'co_bankruptcyExplorerReport')]">
		<xsl:apply-templates />
	</xsl:template>

	<!-- START: PLC -->
	<!-- Tim S. 1/9/13 - According to Document Core and architect is is correct for products to add feature logic is XSL FO rendering. -->

	<!-- START: PLC QuickDraft Delivery - (RTF Only) -->
	<xsl:template match="html:table[contains(@class,'kh_quickDraftToc')]">
		<fo:table table-layout="fixed" font-size="100%">
			<fo:table-column column-width="16cm"/>
			<fo:table-column column-width="2cm"/>
			<fo:table-body start-indent="0pt" end-indent="0pt" text-indent="0pt" last-line-end-indent="0pt" text-align="start">
				<xsl:apply-templates />
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template match="html:td[ancestor::html:table[contains(@class,'kh_quickDraftToc')]]">
		<fo:table-cell border="none">
			<xsl:call-template name="process-common-attributes"/>
			<xsl:if test="@colspan">
				<xsl:attribute name="number-columns-spanned">
					<xsl:value-of select="@colspan"></xsl:value-of>
				</xsl:attribute>
			</xsl:if>

			<fo:block>
				<xsl:apply-templates />
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="html:td[contains(@class,'kh_quickDraftTocIdentifier')]">
		<xsl:variable name="refId" select="substring-after(parent::html:tr/@id,'toc_')" />
		<fo:table-cell border="none">
			<fo:block>
				<fo:basic-link internal-destination="{$refId}">
					<xsl:apply-templates />
				</fo:basic-link>
				<xsl:text>&nbsp;</xsl:text>
				<!-- This will purposely overflow the table cell, however RTF will hide the overflow (which is the only delivery method for quick draft.
				It also adds an extra line below the text, which we are using as spacing instead of padding-top/bottom. -->
				<!-- The below code is used by the template and right now we are not showing the page numbers and hence commented out.  -->
				<!--<fo:leader leader-pattern="dots" leader-pattern-width="3pt" leader-length="20cm" />-->
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="html:td[contains(@class,'kh_quickDraftTocPageRef')]">
		<xsl:variable name="refId" select="substring-after(parent::html:tr/@id,'toc_')" />
		<fo:table-cell border="none">
			<fo:block>
				<xsl:text>&nbsp;</xsl:text>
				<fo:page-number-citation ref-id="{$refId}"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>
	<!-- END: QuickDraft Delivery - (RTF Only) -->

	<xsl:template match="html:td[contains(@class,'co_internalTocRowSpacing')]">
		<fo:table-cell margin="0pt">
			<xsl:call-template name="process-common-attributes"/>
			<fo:block>
				<xsl:apply-templates />
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="html:hr[ancestor::html:div[contains(@class, '&khContent;')]]" name="renderHrLine">
		<fo:leader leader-pattern="rule" rule-style="solid" rule-thickness="0.25pt" leader-length="18cm"  background-color="#CCCCCC" color="#CCCCCC" />
	</xsl:template>

	<xsl:template match="html:a[(string-length(@href) &gt; 0) and ancestor::html:div[contains(@class, '&khContent;')]]" priority="3">
		<xsl:choose>
			<xsl:when test="starts-with(@href,'#')">
				<xsl:call-template name="process-a-link"/>
			</xsl:when>
			<xsl:when test="$hrefLimit and string-length(@href) &gt; number($hrefLimit)">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="process-a-link"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- END: PLC -->

	<xsl:template match="html:a[starts-with(@href, '#co_markmanAnchor')]" priority="3">
		<xsl:call-template name="process-a-link" />
	</xsl:template>

	<!-- MedLit content needs an additional fo:block around the fo:list-block as that alone does not create a new line in the word delivered file. -->
	<xsl:template match="html:div[contains(@class, 'co_medLitLippincott')]//html:ol">
		<xsl:variable name="content">
			<fo:block>
				<fo:list-block>
					<xsl:if test="$productView = '&productViewNameAU;'">
						<xsl:call-template name="indent-list-block" />
					</xsl:if>
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:list-block>
			</fo:block>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0 ">
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>

	<!-- PLC lists in box, convert to table in rtf output only as workaround to Apache FOP bug-->

	<xsl:template match="html:div[@class='kh_division kh_box'][ancestor::html:div/@class='kh_division kh_box']" priority="5">
		<xsl:call-template name="process-khBox">
			<xsl:with-param name="columnWidth" select='"160mm"'/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="html:div[@class='kh_division kh_box'][not(ancestor::html:div/@class='kh_division kh_box')]" priority="5">
		<xsl:call-template name="process-khBox">
			<xsl:with-param name="columnWidth" select='"178mm"'/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="html:div[@class='kh_box'][ancestor::html:div[@class ='kh_summary']]" priority="5">
		<xsl:call-template name="process-khBox">
			<xsl:with-param name="columnWidth" select='"178mm"'/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="html:div[@class='&coNotescontentClass; &khBox;']" priority="5">
		<fo:block>
			<xsl:text>&nbsp;</xsl:text>
		</fo:block>
		<fo:block>
			<xsl:call-template name="process-khBox-table">
				<xsl:with-param name="padding" select='"5mm"'/>
				<xsl:with-param name="borderStyle" select='"dashed"'/>
				<xsl:with-param name="borderColor" select='"#CCCCCC"'/>
				<xsl:with-param name="backgroundColor" select='"#F7F7F7"'/>
				<xsl:with-param name="contentColor" select='""'/>
			</xsl:call-template>
		</fo:block>
	</xsl:template>

	<xsl:template name="process-khBox">
		<xsl:param name="columnWidth"/>
		<xsl:choose>
			<xsl:when test="$isRTF or $isMSWord">
				<xsl:call-template name="process-khBox-table">
					<xsl:with-param name="columnWidth" select="$columnWidth"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<fo:block>
					<xsl:call-template name="process-common-attributes"/>
					<xsl:apply-templates/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-khBox-table">
		<xsl:param name="columnWidth" select='"178mm"'/>
		<xsl:param name="padding" select='"30pt"'/>
		<xsl:param name="borderStyle" select='"solid"'/>
		<xsl:param name="borderColor" select='"#000000"'/>
		<xsl:param name="backgroundColor" select='"#F6F6F6"'/>
		<xsl:param name="contentColor" select='"#505050"'/>
		<fo:table>
			<fo:table-column column-width="{$columnWidth}"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell padding="{$padding}" border-style="{$borderStyle}" border-width="1pt" border-color="{$borderColor}" background-color="{$backgroundColor}" background-image="none" background-repeat="repeat" background-position-vertical="top" background-position-horizontal="left" background-attachment="scroll" >
						<fo:block position="static"  color="{$contentColor}"  font-size="100%">
							<xsl:apply-templates/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template match="html:ul[contains(@class, '&khList;')][ancestor::html:div/@class='kh_division kh_box']" priority="5">
		<!--standard list handling-->
		<xsl:for-each select="./node()[name() = 'li']">
			<fo:list-block>
				<xsl:if test="$productView = '&productViewNameAU;'">
					<xsl:call-template name="indent-list-block" />
				</xsl:if>
				<xsl:call-template name="getUlList"/>
			</fo:list-block>
		</xsl:for-each>
	</xsl:template>

	<!-- extra whitespace is causing a problem in lists in word output hence override here -->
	<xsl:template match="html:div[@class='&paraMainClass;'][contains(ancestor::html:ul/@class,'&khList;')]">
		<fo:block>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<!-- Fix for 920656: Images should fit column width -->
	<xsl:template name="addDualColumnInstructions"/>

	<xsl:template name="dual-column-img-width">
		<xsl:param name="width"/>
		<xsl:attribute name="content-width">
			<xsl:choose>
				<xsl:when test="number(@width) &gt; number($usable-dualColumn-width-px)">
					<xsl:value-of select="$usable-dualColumn-width-px"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$width"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="dual-column-img-height">
		<xsl:attribute name="content-height">
			<xsl:text>scale-to-fit</xsl:text>
		</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>
