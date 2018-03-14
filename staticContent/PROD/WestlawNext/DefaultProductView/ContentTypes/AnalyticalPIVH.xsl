<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SummaryToc.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Document.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CustomFunctions.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType" select="'&contentTypeAnalyticalPIVHClass;'"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="n-docbody" priority="1">
		<xsl:apply-templates />
		<xsl:call-template name="RenderFootnoteSection">
			<xsl:with-param name="renderHorizontalRule" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="doc.title" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="prelim/citation">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="prelim/product">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&headtextClass; &centerClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="prelim/product.title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&headtextClass; &centerClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="thead | tbody">
		<xsl:param name="columnInfo" />
		<xsl:param name="proportionalTotal"/>
		
		<xsl:variable name="totalColWidth" select="sum($columnInfo/@colwidth)" />
		<colgroup>
			<xsl:for-each select="$columnInfo">
				<col>
					<xsl:call-template name="renderCol">
						<xsl:with-param name="colWidth" select="round(@colwidth div $totalColWidth * 100)" />
					</xsl:call-template>
				</col>
			</xsl:for-each>
		</colgroup>
		
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
	
	<xsl:template name="renderCol">
		<xsl:param name="colWidth" />

		<xsl:attribute name="style">
			<xsl:value-of select="concat('width:', $colWidth, '%;')"/>
		</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="row/entry/bold[2]">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<strong>
			<xsl:apply-templates select="@*|node()" />
		</strong>
	</xsl:template>
	
	<xsl:template match="row">
		<xsl:param name="columnInfo" />
		<xsl:param name="header"/>
		<xsl:param name="proportionalTotal"/>

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
			<xsl:choose>
				<xsl:when test="not(normalize-space(.))">
					<td>&nbsp;</td>
				</xsl:when>
				<xsl:when test="$columnInfo">
					<xsl:variable name="row" select="." />
					<xsl:variable name="entries" select="entry" />
					<xsl:variable name="colspans" select="entry[@namest]" />
					<xsl:for-each select="$columnInfo">
						<xsl:variable name="colname" select="@colname" />
						<xsl:variable name="colnumber" select="@colname" />
						<xsl:variable name="colposition" select="position()" />
						<xsl:variable name="colalign" select="@align" />
						<xsl:variable name="colwidth">
							<xsl:choose>
								<xsl:when test="$proportionalTotal &gt; 0">
									<xsl:value-of select="concat(round(substring-before(@colwidth, '*') div $proportionalTotal * 100), '%')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@colwidth"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="render">
							<!-- Will stay empty if the column can render -->
							<xsl:choose>
								<xsl:when test="$colspans">
									<xsl:for-each select="$colspans">
										<xsl:variable name="startCol">
											<xsl:call-template name="GetColNumber">
												<xsl:with-param name="columnInfo" select="$columnInfo" />
												<xsl:with-param name="text" select="@namest" />
											</xsl:call-template>
										</xsl:variable>
										<xsl:variable name="endCol">
											<xsl:call-template name="GetColNumber">
												<xsl:with-param name="columnInfo" select="$columnInfo" />
												<xsl:with-param name="text" select="@nameend" />
											</xsl:call-template>
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
								<xsl:when test="$entries[1][@namest>1] and $entries[1][@namest=$colposition]">
									<xsl:variable name="thisEntry" select="$entries[1]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$colspans and count($entries) &lt; count($columnInfo) and not($entries[@colname] or $entries[@colnum]) and not($colposition=1 and $entries[1][@namest>1])">
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
								<xsl:when test="$entries[@namest=$colposition] and not($entries[position()=$colposition]/@colname) and not($entries[position()=$colposition]/@colnum)">
									<xsl:variable name="thisEntry" select="$entries[@namest=$colposition]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$entries[position()=$colposition] and not($entries[position()=$colposition]/@colname) and not($entries[position()=$colposition]/@colnum) and not($colposition=1 and $entries[1][@namest>1])">
									<xsl:variable name="thisEntry" select="$entries[position()=$colposition]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:otherwise>
									<!-- this is an empty cell -->
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
	</xsl:template>
	
	<xsl:template match="text()" priority="1">
		<xsl:param name="textToTranslate" select="." />
		
		<!-- Replace a double-encoded non-breaking space with the correct output. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;amp;#160; for an invalid nbsp -->
		<xsl:variable name="textWithDoubleEncodedNonBreakingSpaceDecoded">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textToTranslate" />
				<xsl:with-param name="pattern" select="'&amp;amp;#160;'" />
				<xsl:with-param name="replacement" select="'&nbsp;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace another double-encoded non-breaking space with the correct output. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;#160; for an invalid nbsp -->
		<xsl:variable name="textWithDoubleEncodedNonBreakingSpaceDecoded2">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithDoubleEncodedNonBreakingSpaceDecoded" />
				<xsl:with-param name="pattern" select="'&amp;#160;'" />
				<xsl:with-param name="replacement" select="'&nbsp;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid ellipse entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &#8230; for an invalid ellipse -->
		<xsl:variable name="textWithEllipseEntityFixed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithDoubleEncodedNonBreakingSpaceDecoded2" />
				<xsl:with-param name="pattern" select="'&amp;#8230;'" />
				<xsl:with-param name="replacement" select="'&#8230;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid en dash entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;#8211; for an invalid en dash -->
		<xsl:variable name="textWithEnDashEntityFixed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithEllipseEntityFixed" />
				<xsl:with-param name="pattern" select="'&amp;amp;#8211;'" />
				<xsl:with-param name="replacement" select="'&#8211;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid em dash entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;#8212; for an invalid em dash -->
		<xsl:variable name="textWithEmDashEntityFixed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithEnDashEntityFixed" />
				<xsl:with-param name="pattern" select="'&amp;#8212;'" />
				<xsl:with-param name="replacement" select="'&#8212;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid less than sign entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;#60; for an invalid less than sign -->
		<xsl:variable name="textWithLessThanSignEntityFixed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithEmDashEntityFixed" />
				<xsl:with-param name="pattern" select="'&amp;amp;#60;'" />
				<xsl:with-param name="replacement" select="'&#60;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid right single quote mark entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;#8217; for an invalid right single quote mark -->
		<xsl:variable name="textWithRightSingleQuoteEntityFixed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithLessThanSignEntityFixed" />
				<xsl:with-param name="pattern" select="'&amp;#8217;'" />
				<xsl:with-param name="replacement" select="'&#8217;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace another invalid right single quote mark entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;amp;#8217; for an invalid right single quote mark -->
		<xsl:variable name="textWithRightSingleQuoteEntityFixed2">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithRightSingleQuoteEntityFixed" />
				<xsl:with-param name="pattern" select="'&amp;amp;#8217;'" />
				<xsl:with-param name="replacement" select="'&#8217;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid registered trademark sign entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;#174; for an invalid registered trademark sign -->
		<xsl:variable name="textWithRegisteredTrademarkEntityFixed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithRightSingleQuoteEntityFixed2" />
				<xsl:with-param name="pattern" select="'&amp;#174;'" />
				<xsl:with-param name="replacement" select="'&#174;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an invalid ampersand entity with a valid one. I5beabaa40fb211e18b05fdf15589d8e8 contains &amp;amp; for an invalid ampersand -->
		<xsl:call-template name="replace">
			<xsl:with-param name="string" select="$textWithRegisteredTrademarkEntityFixed" />
			<xsl:with-param name="pattern" select="'&amp;amp;'" />
			<xsl:with-param name="replacement" select="'&#38;'"/>
		</xsl:call-template>
		
	</xsl:template>

	<xsl:template match="study//node()[@ID and @refid and not(self::toc.entry)]">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" >
			<xsl:with-param name="id">
				<xsl:if test="@id or @ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id | @ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
  </xsl:template>

	<xsl:template match="case">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="location">
		<xsl:variable name="label" select="label"/>
		<xsl:variable name="state" select="state"/>
		<xsl:variable name="county" select="county"/>
		<div>
			<strong>
				<xsl:value-of select="$label"/>
			</strong>
			<xsl:value-of select="concat($state, '&#47;', $county)"/>
		</div>
	</xsl:template>
	
	
	<xsl:template match="case.information">
		<table>
			<xsl:for-each select="child::*">
				<tr>
					<td style="width:229px;">
						<strong>
							<xsl:value-of select="label" />
						</strong>
					</td>
					<td>
						<xsl:value-of select="*[not(self::label)]/text()" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template match="case.reference" >
		<table>
				<tr>
					<td style="width:229px;">
						<strong>
							<xsl:value-of select="label" />
						</strong>
					</td>
					<td>
						<xsl:call-template name="citeQuery">
							<xsl:with-param name="citeQueryElement" select="reference/cite.query"/>
						</xsl:call-template>
					</td>
				</tr>
		</table>
	</xsl:template>
	
	<xsl:template match="study//head[not(ancestor::prelim) and not(ancestor::doc.toc) and not(ancestor::award.section) and not(ancestor::settlement.section)]/headtext">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&headtextClass; &centerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="contact.note/note">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="contact.note/web.address">
			<div class="&centerClass;">
				<em>
					<xsl:value-of select="." />
				</em>
			</div>
	</xsl:template>
	
	<xsl:template match="verdict">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

</xsl:stylesheet>
