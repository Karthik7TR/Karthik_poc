<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:variable name="Relationships" select="DocumentExtension:GetRelatedProfileDataCollection($Guid, 'w_lt_pdf_refs')/relationships" />

	<!-- Suppress Docket Track Links, Docked Calendar Track links and Creditor lists for dockets the meet the following 
			 criteria See bug 677473 for details -->

	<xsl:variable name="IsSuppressDocketTracksCalendarPdfAndCreditorLinks">
		<xsl:variable name="jurisabbrev" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisabbrev"/>
		<xsl:variable name="filedate" select="/Document/n-metadata/metadata.block/md.dates/md.filedate" />
		<xsl:variable name="court" select="/Document/n-docbody/r/court.block/court" />


		<xsl:variable name="closedate">
			<xsl:value-of select="//terminated.date[1]"/>
		</xsl:variable>

		<xsl:value-of select="$jurisabbrev = 'CTA2'  and number($filedate) &lt; 20100101 or 
													$jurisabbrev = 'CTA7'  and number($filedate) &lt; 20080107 or
													$jurisabbrev = 'CTA11' and number($filedate) &lt; 20100101 or
													$jurisabbrev = 'CTAF'  and number($filedate) &lt; 20120301 or
													$court = 'U.S. BANKRUPTCY COURT, CENTRAL DISTRICT OF CALIFORNIA (LOS ANGELES)' and number($closedate) &lt; 20010201"  />
	</xsl:variable>
	<xsl:variable name="guid" select="//md.uuid" />
	<xsl:variable name="specialVersionParamVariable">
		<xsl:if test="$DeliveryMode">
			<xsl:choose>
				<xsl:when test="string-length($SpecialVersionParam) &gt; 0">
					<xsl:value-of select="concat('&specialVersionParamName;=', $SpecialVersionParam)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&specialVersionParamName;=', '&versionForRequestDirector;')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:variable>

	<xsl:variable name="specialRequestSourceParamVariable">
		<xsl:if test="$DeliveryMode">
			<xsl:choose>
				<xsl:when test="string-length($SpecialRequestSourceParam) &gt; 0">
					<xsl:value-of select="concat('&requestSourceUrlParamName;=',$SpecialRequestSourceParam)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&requestSourceUrlParamName;=', '&requestSourceForRequestDirector;')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:variable>
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&docketsClass;'"/>
			</xsl:call-template>
			<xsl:if test="$HasDocketOrdersAccess">
				<xsl:call-template name="ToOrderTop" />
			</xsl:if>
			<xsl:call-template name="CurrentBlock"/>
			<xsl:if test="$DisplayDocketTrackLink = true()">
				<xsl:call-template name="renderDocketsTrackHiddenLink" />
			</xsl:if>
			<xsl:if test="$DisplayDocketUpdateLink = true()">
				<xsl:call-template name="renderDocketsUpdateHiddenJsonObject" />
			</xsl:if>
			<xsl:call-template name="CurrentDate"/>
			<xsl:call-template name="Source"/>
			<xsl:apply-templates />
			<xsl:if test="not(/Document/n-docbody/r/docket.proceedings.block | /Document/n-docbody/r/docket.entries.block)">
				<xsl:call-template name="renderCalendaringSection"/>
			</xsl:if>
			<xsl:if test="not($PreviewMode) and $HasDocketOrdersAccess">
				<xsl:call-template name="ToOrderBottom" />
			</xsl:if>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="renderCalendaringSection">
		<xsl:if test="$HasCalenderingInformation and $IsPublicRecords = false() and $IsSuppressDocketTracksCalendarPdfAndCreditorLinks = 'false'">
			<xsl:call-template name="renderCalendaringSectionAsTable" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderCalendaringSectionAsTable">
		<h2 class="&docketsHeading;">
			<xsl:text>&docketsCalendarInformation;</xsl:text>
		</h2>
		<xsl:variable name="href">
			<xsl:call-template name="createDocketsCalenderingLink"/>
		</xsl:variable>
		<table>
			<tr class="&docketsRowClass;">
				<td class="&docketsRowLabelClass;">
					<xsl:choose>
						<xsl:when test="$IsIpad">
							<!-- NPD wants just text here rather than an <href> -->
							<xsl:text>&docketsCalenderInformationLinkText;</xsl:text>
						</xsl:when>
						<xsl:when test="not($IsMobile)">
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="$href"/>
								</xsl:attribute>
								<xsl:attribute name="id">&docketsCalendarLinkId;</xsl:attribute>
								<xsl:text>&docketsCalenderInformationLinkText;</xsl:text>
							</a>
						</xsl:when>
					</xsl:choose>
				</td>
			</tr>
		</table>
	</xsl:template>
	
	<xsl:template name="CurrentBlock">
		<xsl:variable name="ScrapeDate" select="/Document/n-docbody/r/scrape.date" />
		<xsl:if test="$ScrapeDate or $DisplayDocketUpdateLink = true()">
			<div class="&docketsCurrentBlockClass;">
				<!--<xsl:apply-templates select="/Document/n-docbody/r/scrape.date" mode="render" />-->
				<xsl:apply-templates select="$ScrapeDate" mode="render" />
				<xsl:call-template name="ToUpdate" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CurrentDate">
		<strong>Current Date: </strong>
		<xsl:text></xsl:text>
		<xsl:value-of select="$currentDate"/>
	</xsl:template>

	<xsl:template name="Source">
		<xsl:variable name="court">
			<xsl:call-template name="NodeText">
				<xsl:with-param name="parentNode" select="/Document/n-docbody/r/case.information.block/court.block/court | /Document/n-docbody/r/court.block/court | /Document/n-docbody/r/court.info.block/court.block/court | /Document/n-docbody/r/claims.register.block/case.info.block/court.block/court"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="county">
			<xsl:apply-templates select="/Document/n-docbody/r/case.information.block/court.block/filing.county"/>
		</xsl:variable>
		<xsl:variable name="state">
			<xsl:call-template name="NodeText">
				<xsl:with-param name="parentNode" select="/Document/n-docbody/r/full.state"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="sourceForDisplay">
			<xsl:copy-of select="$court"/>
			<xsl:if test="string-length($county) &gt; 0">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$county"/>
				<xsl:text>&docketsCounty;</xsl:text>
			</xsl:if>
			<xsl:if test="string-length($state) &gt; 0">
				<xsl:text>, </xsl:text>
				<xsl:copy-of select="$state"/>
			</xsl:if>
		</xsl:variable>
		<xsl:call-template name="RenderSource">
			<xsl:with-param name="sourceForDisplay" select="$sourceForDisplay"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="RenderSource">
		<xsl:param name="sourceForDisplay"></xsl:param>
		<br/>
		<strong>Source: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong>
		<xsl:value-of select="$sourceForDisplay"/>
	</xsl:template>
	
	<xsl:template name="NodeText">
		<xsl:param name="parentNode"/>
		<xsl:for-each select="$parentNode/node()">
			<xsl:apply-templates select="." />
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="scrape.date" mode="render">
		<div class="&docketsScrapeDateClass;">
			<xsl:text>&docketsScrape;</xsl:text>
			<xsl:call-template name="DocketsDate">
				<xsl:with-param name="date" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template name="ToOrderTop">
		<div class="&docketsToOrderClass;">
			<div>
				<xsl:text>&docketsToOrderPart1;</xsl:text>
			</div>
			<div>
				<xsl:text>&docketsToOrderPart2;</xsl:text>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="ToOrderBottom">
		<div class="&docketsToOrderClass;">
			<div>
				<xsl:text>&docketsToOrderPart3;</xsl:text>
			</div>
			<div>
				<xsl:text>&docketsToOrderPart2;</xsl:text>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="ToUpdate">
		<xsl:if test="$DisplayDocketUpdateLink = true() and $IsPublicRecords = false()">
			<xsl:variable name="updateLink">
				<xsl:call-template name="getDocketsUpdateLink" />
			</xsl:variable>
			<xsl:if test="string-length($updateLink) &gt; 0">
				<div class="&docketsToUpdateClass;">
					<xsl:text>&docketsToUpdate;</xsl:text>
					<xsl:call-template name="renderDocketsUpdateLink">
						<xsl:with-param name="updateLink" select="$updateLink"/>
					</xsl:call-template>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderDocketsTrackHiddenLink">
		<xsl:variable name="url">
			<xsl:call-template name="createDocketsTrackMashupLink" />
		</xsl:variable>
		<xsl:if test="string-length($url) &gt; 0">
			<input>
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="title">&docketsTrackDocket;</xsl:attribute>
				<xsl:attribute name="id">&docketsTrackLinkId;</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:value-of select="$url" />
				</xsl:attribute>
			</input>
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderDocketsUpdateHiddenJsonObject">
		<xsl:variable name="jsonObject">
			<xsl:call-template name="createDocketsUpdateJson" />
		</xsl:variable>
		<xsl:if test="string-length($jsonObject) &gt; 0">
			<input>
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="id">&docketsUpdateJsonId;</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:value-of select="$jsonObject" />
				</xsl:attribute>
			</input>
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderDocketsUpdateLink">
		<xsl:param name="updateLink"/>
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="$updateLink"/>
			</xsl:attribute>
			<xsl:attribute name="id">&docketsUpdateId;</xsl:attribute>
			<xsl:text>&docketsUpdate;</xsl:text>
		</a>
	</xsl:template>

	<xsl:template name="DocketsRow">
		<xsl:param name="label" />
		<xsl:param name="text" />
		<tr class="&docketsRowClass;">
			<td class="&docketsRowLabelClass;">
				<xsl:copy-of select="$label"/>
			</td>
			<td class="&docketsRowTextClass;">
				<xsl:choose>
					<xsl:when test="string-length($text) &gt; 0 or not($IsIpad)">
						<xsl:copy-of select="$text"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text><![CDATA[-]]></xsl:text>
					</xsl:otherwise>
				</xsl:choose>						
				<div class="&clearClass;"></div>
			</td>
		</tr>				
	</xsl:template>
	
	<xsl:template name="DocketsRowByTemplateMatch">
		<xsl:param name="templateMatch" />
		<xsl:param name="labelText" />
		<xsl:if test="$templateMatch">
			<tr class="&docketsRowClass;">
				<td class="&docketsRowLabelClass;">
					<xsl:value-of select="$labelText"/>
				</td>
				<td class="&docketsRowTextClass;">
					<xsl:apply-templates select="$templateMatch" />
					<div class="&clearClass;"></div>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="DocketsHeaderRow">
		<xsl:param name="label" />
		<xsl:param name="text" />
		<h3 class="&docketSubHeading;">
			<xsl:copy-of select="$text"/>
			<xsl:if test="not($IsMobile)">
				<a class="&widgetToggleIcon;">Toggle Section</a>
			</xsl:if>
		</h3>
	</xsl:template>

	<xsl:template name="RenderAddress">
		<xsl:param name="address" />
		<xsl:call-template name="RenderExplicitAddress">
			<xsl:with-param name="description" select="$address/address.description" />
			<xsl:with-param name="street" select="$address/street | $address/firm.street" />
			<xsl:with-param name="city" select="$address/city | $address/firm.city" />
			<xsl:with-param name="state" select="$address/state | $address/firm.state" />
			<xsl:with-param name="citystate" select="$address/firm.city.state" />
			<xsl:with-param name="zip" select="$address/zip | $address/firm.zip" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderExplicitAddress">
		<xsl:param name="description" />
		<xsl:param name="street" />
		<xsl:param name="city" />
		<xsl:param name="state" />
		<xsl:param name="citystate" />
		<xsl:param name="zip" />
		<xsl:if test="$description">
			<div>
				<xsl:apply-templates select="$description" />
			</div>
		</xsl:if>
		<xsl:if test="$street">
			<xsl:for-each select="$street">
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="$city or $state or $citystate or $zip">
			<div>
				<xsl:choose>
					<xsl:when test="$city and $state">
						<xsl:apply-templates select="$city" />
						<xsl:if test="string-length($city) &gt; 0 and string-length($state) &gt; 0">
							<xsl:text>, </xsl:text>
						</xsl:if>
						<xsl:apply-templates select="$state"/>
					</xsl:when>
					<xsl:when test="$state">
						<xsl:apply-templates select="$state" />
					</xsl:when>
					<xsl:when test="$citystate">
						<xsl:apply-templates select="$citystate"/>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="(string-length($city) &gt; 0 or string-length($state) &gt; 0 or string-length($citystate) &gt; 0) and string-length($zip) &gt; 0">
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:if>
				<xsl:apply-templates select="$zip" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="DocketsTableData">
		<xsl:param name="label" />
		<xsl:param name="text" />
		<strong>
			<xsl:copy-of select="$label"/>
		</strong>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:copy-of select="$text"/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template name="DocketsTableCell">
		<xsl:param name="text" />
		<td>
      <xsl:choose>
				<xsl:when test="string-length($text) &gt; 0">
					<xsl:copy-of select="$text"/>
				</xsl:when>
				<xsl:when test="$IsIpad">
					<xsl:text><![CDATA[-]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>

	<xsl:template name="DocketsDate">
		<xsl:param name="date" />
		<xsl:choose>
			<xsl:when test="$date/N-HIT">
				<xsl:call-template name="nHit">
					<xsl:with-param name="contents">
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="$date/N-HIT" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$date/N-LOCATE">
				<xsl:call-template name="nLocate">
					<xsl:with-param name="contents">
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="$date/N-LOCATE" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$date/N-WITHIN">
				<xsl:call-template name="nWithin">
					<xsl:with-param name="contents">
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="$date/N-WITHIN" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name ="year" select="substring($date,1,4)"/>
				<xsl:variable name ="month" select="substring($date,5,2)"/>
				<xsl:variable name ="day" select="substring($date,7,2)"/>
				<xsl:value-of select="$month"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$day"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$year"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="DisplayCount">
		<xsl:param name="nodes"/>
		<xsl:if test="$nodes">
			<xsl:text> (</xsl:text>
			<xsl:value-of select="count($nodes)"/>
			<xsl:text>)</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="debtor.info.block" name="generalDebtorInfoBlock">
		<xsl:choose>
			<xsl:when test="debtor">
				<xsl:for-each select="debtor">
					<div class="&docketsSubSection;">
						<xsl:apply-templates select="." />
					</div>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<div class="&docketsSubSection;">
					<table>
						<xsl:apply-templates />
					</table>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="debtor.info.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsDebtorInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsDebtorInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="generalDebtorInfoBlock"/>
		</div>
	</xsl:template>

	<xsl:template match="creditor.info.block" name="generalCreditorInfoBlock">
		<xsl:choose>
			<xsl:when test="creditor">
				<xsl:for-each select="creditor">
					<div class="&docketsSubSection;">
						<xsl:apply-templates select="." />
					</div>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<div class="&docketsSubSection;">
					<table>
						<xsl:apply-templates />
					</table>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="creditor.info.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsCreditorId;" class="&docketsHeading;">
				<xsl:text>&docketsCreditor;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="generalCreditorInfoBlock"/>
		</div>
	</xsl:template>

	<xsl:template match="case.information.block">
		<h2 id="&docketsCaseInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsCaseInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
		<!--<xsl:if test="(child::court.block and child::title.block) or (preceding-sibling::court.block and child::docket.number.block)">-->
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<!--</xsl:if>-->
	</xsl:template>

	<xsl:template match="summary" name="summary">
		<table>
			<xsl:apply-templates />
		</table>

		<!--<xsl:if test="preceding-sibling::court.block and preceding-sibling::title.block and not(preceding-sibling::case.information.block) and child::case.number.block">-->
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<!--</xsl:if>-->
	</xsl:template>

	<xsl:template match="synopsis.block">
		<h2 id="&docketsSynopsisInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsSynopsisInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="court.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="court" />
				<xsl:if test="filing.county">
					<xsl:text>, </xsl:text>
					<xsl:apply-templates select="filing.county" />
					<xsl:text>&docketsCounty;</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="title.block">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="DocketsRow">
					<xsl:with-param name="label">
						<xsl:apply-templates select="label" />
					</xsl:with-param>
					<xsl:with-param name="text">
						<xsl:call-template name="TitleWithLink" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DocketsRow">
					<xsl:with-param name="label">
						<xsl:apply-templates select="label" />
					</xsl:with-param>
					<xsl:with-param name="text">
						<xsl:apply-templates select="primary.title" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="TitleWithLink">
		<xsl:variable name="docketUrl">
			<xsl:choose>
				<xsl:when test="$guid != ''">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="$guid" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="$DocketGuidForLinkCreation" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="$docketUrl"/>
			</xsl:attribute>
			<xsl:apply-templates select="primary.title" />
		</a>
	</xsl:template>

	<xsl:template match="docket.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.type" />
				<xsl:if test="chapter">
					<xsl:text>: </xsl:text>
					<xsl:apply-templates select="chapter"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.type.block/case.type">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="case.subtype.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsRowCaseSubtypeBlockText"></xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- 
		factored out so productViews can override <div> wrapper if needed.  iPhone renders this inside a <p>, <div> is NOT
		allowed inside <p>.
	-->
	<xsl:template name="DocketsRowCaseSubtypeBlockText">
		<xsl:for-each select="case.subtype">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="facts.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="facts" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="damages.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="damages" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="panel.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:call-template name="CreateDocketsRowLabel"></xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsRowPanelBlockText"></xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- 
		factored out so productViews can override <div> wrapper if needed.  iPhone renders this inside a <p>, <div> is NOT
		allowed inside <p>.
	-->
	<xsl:template name="CreateDocketsRowLabel">
		<xsl:for-each select="label">
			<div>
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>		
	</xsl:template>

	<!-- 
		factored out so productViews can override <div> wrapper if needed.  iPhone renders this inside a <p>, <div> is NOT
		allowed inside <p>.
	-->
	<xsl:template name="DocketsRowPanelBlockText">
		<xsl:for-each select="judge | panel.judge | judge.block/judge">
			<div>
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="filing.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="filing.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.information.block/image.block">
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="image.gateway.link[@item.type='main']">
						<xsl:with-param name="text">
							<xsl:text>&docketsQuestionsPresented;</xsl:text>
						</xsl:with-param>
					</xsl:apply-templates>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="closed.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="closed.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.ref.to.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.ref.to" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.dockets.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="other.dockets" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nature.of.suit.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="nature.of.suit" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key.nature.of.suit.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="knos.level1.block/label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="knos.level1.block/knos.level1" />
				<xsl:if test="knos.level2.block">
					<xsl:text>; </xsl:text>
					<xsl:apply-templates select="knos.level2.block/knos.level2" />
				</xsl:if>
				<xsl:if test="knos.level3.block">
					<xsl:text>; </xsl:text>
					<xsl:apply-templates select="knos.level3.block/knos.level3" />
				</xsl:if>
				<xsl:if test="knos.code">
					<xsl:text> (</xsl:text>
					<xsl:apply-templates select="knos.code" />
					<xsl:text>)</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="jurisdiction.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="jurisdiction" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cause.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="cause" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.name.block">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.name" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:if test="party.aka.block">
			<xsl:apply-templates select="party.aka.block" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="party.aka.block">
		<table>
			<xsl:choose>
				<xsl:when test="party.aka.name.block">
					<xsl:apply-templates select="party.aka.name.block" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="DocketsRow">
						<xsl:with-param name="label">
							<xsl:apply-templates select="label" />
						</xsl:with-param>
						<xsl:with-param name="text">
							<xsl:apply-templates select="party.aka" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<xsl:template match="party.aka.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:value-of select="'&docketsPartyDescription;'" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="concat(party.aka.type, ' ', party.aka)" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.aka">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="party.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.type.block/party.type" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="party.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderAddress">
					<xsl:with-param name="address" select="." />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderAddress">
					<xsl:with-param name="address" select="." />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trustee/firm.phone">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:value-of select="'Phone:'" />
			</xsl:with-param>
			<xsl:with-param name="text" select="." />
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="added.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="added.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.terminated.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.terminated" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.attorney.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.name" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:if test="attorney.status">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsStatus;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="attorney.status" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="attorney.status.block" />
		<xsl:apply-templates select="attorney.terminated.block" />
		<xsl:apply-templates select="attorney.fts.block" />
		<xsl:apply-templates select="attorney.address.block" />
		<xsl:apply-templates select="attorney.phone.block" />
		<xsl:if test="attorney.phone">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsAttorneyPhone;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="attorney.phone" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="attorney.fax.block" />
		<xsl:if test="attorney.fax">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsAttorneyFax;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="attorney.fax" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="firm.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsFirmName;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="firm.name" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="firm.name | firm.name.block" />
		<xsl:if test="firm.address.combined">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="text">
					<xsl:apply-templates select="firm.address.combined" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="firm.address.block" />
		<xsl:apply-templates select="firm.phone.block" />
		<xsl:apply-templates select="firm.fax.block" />
		<xsl:apply-templates select="attorney.email.block" />
		<xsl:if test="attorney.email">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsEmailAddress;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="attorney.email" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="party.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.terminated.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.terminated" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.fts.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.fts" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:choose>
					<xsl:when test="label">
						<xsl:apply-templates select="label" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&docketsAttorneyAddress;'" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderAddress">
					<xsl:with-param name="address" select="." />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.fax.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.fax" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="firm.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.name">
		<xsl:choose>
			<xsl:when test="parent::firm.name.block">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DocketsRow">
					<xsl:with-param name="label" select="'&docketsFirmName;'"/>
					<xsl:with-param name="text">
						<xsl:apply-templates />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="firm.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderAddress">
					<xsl:with-param name="address" select="." />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="firm.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.fax.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="firm.fax" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.email.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.email" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.entries.block">
	  <xsl:call-template name="PerformDocketEntriesBlockMatch">
		<xsl:with-param name="currentNode" select="current()"/>
	  </xsl:call-template>
	</xsl:template>

  <xsl:template name="PerformDocketEntriesBlockMatch">
	<xsl:param name="currentNode" />
		<xsl:call-template name="renderCalendaringSection"/>
		<xsl:call-template name="DocketProceedingsSectionAsTable">
			<xsl:with-param name="currentNode" select="$currentNode" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="DocketProceedingsSectionAsTable">
		<xsl:param name="currentNode" />		
		<h2 id="&docketsDocketProceedingsId;" class="&docketsHeading;">
			<xsl:text>&docketsDocketProceedings;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="$currentNode/docket.entry"/>
			</xsl:call-template>
		</h2>
		<table class="&docketsTable;">
			<tr>
				<th>
					<xsl:text>&docketsEntry;</xsl:text>
				</th>
				<th>
					<xsl:text>&docketsDate;</xsl:text>
				</th>
				<th>
					<xsl:text>&docketsDescription;</xsl:text>
				</th>
				<xsl:if test="not($IsMobile) and $IsPublicRecords = false() and not($IsIpad)">
					<th></th>
				</xsl:if>
			</tr>
			<xsl:for-each select="docket.entry">
				<tr> 
					<xsl:variable name="imageOrLink">
						<xsl:if test="not($IsMobile) and $IsPublicRecords = false() and not($IsIpad)">
							<xsl:variable name="index">
								<xsl:choose>
									<xsl:when test="send.runner.link">
										<xsl:value-of select="send.runner.link/@indexvalue" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="position()"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="number.block/image.block">
                  <xsl:call-template name="RenderGreenTriangleIconIfPdfIsOnNovus" />
									<xsl:call-template name="RenderDocketImage">
										<xsl:with-param name="imageBlock" select="number.block/image.block" />
										<xsl:with-param name="index" select="$index" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="$index and $HasDocketOrdersAccess">
									<xsl:call-template name="RenderSendRunnerLink">
										<xsl:with-param name="index" select="$index" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text><![CDATA[ ]]></xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:variable>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="number.block/number" />
              <xsl:apply-templates select="pinpoint.anchor" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="date" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="docket.description" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="not($IsMobile) and $IsPublicRecords = false() and not($IsIpad)">
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:copy-of select="$imageOrLink"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</tr>
			</xsl:for-each>
		</table>		
	</xsl:template>

	<xsl:template match="calendar/event.block">
		<xsl:call-template name="calendarEventBlockAsTable" />
	</xsl:template>

	<xsl:template name="calendarEventBlockAsTable">
		<h2 class="&docketsHeading;">
			<xsl:text>&docketsCalendarInformation;</xsl:text>
		</h2>
		<table class="&docketsTable;">
			<tr>
				<th>&docketsDateTime;</th>
				<th>&docketsDescription;</th>
				<th>&docketsLocation;</th>
				<th>&docketsJudge;</th>
			</tr>
			<xsl:choose>
				<xsl:when test="not(child::event)">
					<tr>
						<td colspan="4">
							<xsl:text>&docketsNoCalenderEvents;</xsl:text>
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="event">
						<tr>
							<xsl:call-template name="DocketsTableCell">
								<xsl:with-param name="text">
									<xsl:apply-templates select="event.schedule/event.date/local" />
									<xsl:text><![CDATA[ ]]></xsl:text>
									<xsl:apply-templates select="event.schedule/event.time/local" />
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="DocketsTableCell">
								<xsl:with-param name="text">
									<xsl:apply-templates select="event.description" />
									<xsl:call-template name="RenderAddEventToOutlookLink">
										<xsl:with-param name="index" select="position()" />
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="DocketsTableCell">
								<xsl:with-param name="text">
									<xsl:apply-templates select="event.location" />
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="DocketsTableCell">
								<xsl:with-param name="text">
									<xsl:apply-templates select="event.judge" />
								</xsl:with-param>
							</xsl:call-template>
						</tr>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<xsl:template match="r[child::calendar]">
		<!-- this prevents an empty <table> when displaying calendaring. -->
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="r/persist.info">
		<xsl:variable name="persistInfo">
			<xsl:value-of select="id"/>
			<xsl:text>|</xsl:text>
			<xsl:value-of select="checksum"/>
			<xsl:text>|</xsl:text>
			<xsl:value-of select="type"/>
		</xsl:variable>
		<input type="hidden" id="&docketCalendaringPersistInfoId;" value="{$persistInfo}" />
	</xsl:template>

	<xsl:template match="calendar/case.information.block/primary.title">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="calendar/case.information.block/case.type">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="calendar/case.information.block/case.subtype">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="Document/n-docbody/persist.info" />

	<xsl:template name="RenderDocketImage">
		<xsl:param name="imageBlock" />
		<xsl:param name="index" />
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:apply-templates select="$imageBlock" />
			<xsl:variable name="checkSum" select="/Document/n-docbody/persist.info/checksum" />
			<xsl:variable name="docPersistId" select="/Document/n-docbody/persist.info/id" />
			<xsl:variable name="batchPdfHref">
				<xsl:call-template name="BatchPdfHref">
					<xsl:with-param name="documentGuid" select="$Guid" />
					<xsl:with-param name="pdfIndex" select="$index" />
					<xsl:with-param name="checkSum" select="$checkSum" />
					<xsl:with-param name="docPersistId" select="$docPersistId" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($batchPdfHref) &gt; 0">
				<a href="{$batchPdfHref}" class="&docketProceedingsButtonClass;">
					<xsl:text>&docketBatchDownloadText;</xsl:text>
				</a>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- adds CSS class that will render a 'green triangle' icon to UI display iff PDF is here on Novus -->
	<xsl:template name="RenderGreenTriangleIconIfPdfIsOnNovus">
		<xsl:if test="$HasPassThruPdfsAccess and $IsPublicRecords = false()">
			<xsl:variable name="imageId" select="*/image.block/image.gateway.link/@image.ID|*/image.block/image.gateway.link/@image.id|
			image.block/image.gateway.link/@image.ID"/>
			<xsl:variable name="alreadyVisited">
				<xsl:call-template name="ParseImageGuid">
					<xsl:with-param name="imageGuid" select="substring-before($imageId,';')" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($alreadyVisited) &gt; 0">
				<div class="&docketsRelativeClass;">
					<a title="&docketsStoredPdfDownloadText;" class="&docketsDocumentLinkPreviouslyViewed;">&docketsStatusText;</a>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ParseImageGuid">
		<xsl:param name="imageGuid" />
		<xsl:value-of select="$Relationships/relationship[user.download/guid = $imageGuid]/target"/>
	</xsl:template>

	<xsl:template match="docket.description/image.gateway.link">
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:variable name="court">
				<xsl:call-template name="getCourtNumber">
					<xsl:with-param name="courtNumber" select="@court" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="casenumber" select="@casenumber" />
			<xsl:variable name="id" select="@image.id|@image.ID" />
			<xsl:variable name="platform" select="@platform" />
			<xsl:variable name="mimeType">
				<xsl:value-of select="'&pdfMimeType;'" />
			</xsl:variable>
			<xsl:variable name="localImageGuid" >
				<xsl:call-template name="ParseImageGuid">
					<xsl:with-param name="imageGuid" select="substring-before($id,';')" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="filename">
				<xsl:call-template name="createPdfFilename">
					<xsl:with-param name="cite" select="$Cite"/>
					<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
					<xsl:with-param name="date" select="ancestor::docket.entry/date"/>
					<xsl:with-param name="number" select="concat(ancestor::docket.entry/number.block/number, '-', ./text())"/>
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="displayToolTip">
				<xsl:if test="string-length($localImageGuid) &gt; 0 and $HasPassThruPdfsAccess">
					<xsl:value-of select="'&docketExhibitLocalToolTip;'"/>
				</xsl:if>
			</xsl:variable>

			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:call-template name="createDocumentGatewayBlobLink">
				<xsl:with-param name="court" select="$court"/>
				<xsl:with-param name="courtNumber" select="$JurisdictionNumber"/>
				<xsl:with-param name="casenumber" select="$casenumber"/>
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="platform" select="$platform"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="contents" select="text()"/>
				<xsl:with-param name="className" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				<xsl:with-param name="filename" select="$filename" />
				<xsl:with-param name="displayToolTip" select="$displayToolTip"/>
				<xsl:with-param name="localImageGuid" select="$localImageGuid"/>
			</xsl:call-template>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="number.block/image.block|docket.entry.number.block/image.block|judgment.document/image.block|available.image/image.block" priority="1">
		<xsl:call-template name="PerformNumberBlockImageBlockMatch">
		<xsl:with-param name="currentNode" select="current()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PerformNumberBlockImageBlockMatch">
		<xsl:param name="currentNode" />
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:variable name="imageId" select="$currentNode/image.gateway.link/@image.ID|$currentNode/image.gateway.link/@image.id" />
			<xsl:variable name="localImage" >
				<xsl:call-template name="ParseImageGuid">
					<xsl:with-param name="imageGuid" select="substring-before($imageId,';')" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:apply-templates select="$currentNode/image.gateway.link[@item.type='main' and @court!='N_DCAORANGE']">
				<xsl:with-param name="text">
					<i></i>
					<xsl:text>&docketViewPDFText;</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="className" select="'&docketProceedingsButtonClass; &docketProceedingsPDFButtonClass;'"/>
				<xsl:with-param name="localImageGuid" select="$localImage"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderSendRunnerLink">
		<xsl:param name="index" />
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:variable name="checkSum" select="/Document/n-docbody/persist.info/checksum" />
			<xsl:variable name="docPersistId" select="/Document/n-docbody/persist.info/id" />
			<xsl:variable name="sendRunnerHref">
				<xsl:call-template name="SendRunnerHref">
					<xsl:with-param name="documentGuid" select="$Guid" />
					<xsl:with-param name="orderedIndex" select="$index" />
					<xsl:with-param name="checkSum" select="$checkSum" />
					<xsl:with-param name="docPersistId" select="$docPersistId" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($sendRunnerHref) &gt; 0">
				<a id="&docketSendRunnerLinkId;{$index}" class="&docketProceedingsButtonClass; &docketSendRunnerLinkClass;" href="{$sendRunnerHref}">
					<i></i>
					<xsl:text>&docketSendRunnerText;</xsl:text>
				</a>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderAddEventToOutlookLink">
		<xsl:param name="index" />
		<xsl:if test="$IsPublicRecords = false()">
			<a id="&docketAddEventToOutlookLinkId;{$index}" class="&docketProceedingsButtonClass; &docketEventLinkClass;" href="javascript:void(0);">
				<xsl:text>&docketAddEventToOutlookText;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template match="lower.court.block">
		<h2 id="&docketsLowerCourtInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsLowerCourtInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template name="getDocketsUpdateLink">
		<xsl:choose>
			<xsl:when test="$IsIpad">
				<xsl:text>http://ipad/updatedocket</xsl:text>
			</xsl:when>
			<xsl:when test="not($IsMobile)">
				<xsl:text>javascript:void(0);</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="docketsUpdateLinkHref">
					<xsl:call-template name="createDocketsUpdateLink" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="string-length($docketsUpdateLinkHref) &gt; 0">
						<xsl:value-of select="$docketsUpdateLinkHref"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>javascript:void(0);</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Suppressions -->
	<xsl:template match="acquire.date" />
	<xsl:template match="additional.records.INF" />
	<xsl:template match="alert.info" />
	<xsl:template match="c" />
	<xsl:template match="case.status.flag" />
	<xsl:template match="case.type" />
	<xsl:template match="charge.details.block" />
	<xsl:template match="cluster.name" />
	<xsl:template match="col.key" />
	<xsl:template match="convert.date" />
	<xsl:template match="convert.date.block" />
	<xsl:template match="data.source.type" />
	<xsl:template match="full.state" />
	<xsl:template match="higher.court.information" />
	<xsl:template match="index.scrape.date" />
	<xsl:template match="jurisdiction.number" />
	<xsl:template match="legacy.id" />
	<xsl:template match="link" />
	<xsl:template match="p" priority="1" />
	<xsl:template match="party.type" />
	<xsl:template match="pc" />
	<xsl:template match="platform" />
	<xsl:template match="publish.date" />
	<xsl:template match="scrape.date" />
	<xsl:template match="source" />
	<xsl:template match="state.postal" />
	<xsl:template match="state.source" />
	<xsl:template match="update.link.block" />
	<xsl:template match="update.type" />
	<xsl:template match="gateway.image.link" />

</xsl:stylesheet>
