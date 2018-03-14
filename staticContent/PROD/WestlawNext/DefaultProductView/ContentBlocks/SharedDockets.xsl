<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsStatePreDocket" select="contains('|N_DSTATEPRE|N_DSTATEPRE_QA1|N_DSTATEPRE_QA2|N_DSTATEPRE_QA3|N_DSTATEPRE_QB1|N_DSTATEPRE_QB2|N_DSTATEPRE_QB3|', concat('|',  /Document/document-data/collection , '|'))"/>
	<xsl:variable name="IsFederalPreDocket" select="contains('|N_DFEDPRE|N_DFEDPRE_QA1|N_DFEDPRE_QA2|N_DFEDPRE_QA3|N_DFEDPRE_QB1|N_DFEDPRE_QB2|N_DFEDPRE_QB3|', concat('|',  /Document/document-data/collection , '|'))"/>
	<xsl:variable name="IsPreDocket" select="$IsFederalPreDocket or $IsStatePreDocket"/>

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
													$court = 'U.S. BANKRUPTCY COURT, CENTRAL DISTRICT OF CALIFORNIA (LOS ANGELES)' and number($closedate) &lt; 20010201"/>
	</xsl:variable>

	<xsl:variable name="IsSuppressDocketTrackLinks">
		<xsl:variable name="jurisabbrev" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisabbrev"/>
		<xsl:variable name="filedate" select="/Document/n-metadata/metadata.block/md.dates/md.filedate" />
		<xsl:variable name="court" select="/Document/n-docbody/r/court.block/court" />

		<xsl:value-of select="$jurisabbrev = 'USITC'  and number($filedate) &lt; 20081112"/>
	</xsl:variable>

	<xsl:template match="docket.entry/docket.description" priority="1">
		<xsl:apply-templates  />
	</xsl:template>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&docketsClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderDocumentTopBlock" />
			<xsl:apply-templates />

			<xsl:if test="not($IsPreDocket = true()) and not(/Document/n-docbody/r/docket.proceedings.block | /Document/n-docbody/r/docket.entries.block)">
					<xsl:call-template name="renderCalendaringSection"/>
			</xsl:if>

			<xsl:call-template name="renderBeforeEndOfDocument" />
			
			<xsl:if test="not($PreviewMode) and $HasDocketOrdersAccess">
				<xsl:call-template name="ToOrderBottom" />
				<xsl:if test="descendant-or-self::color or descendant-or-self::strikeout">
					<xsl:call-template name="AdditionsAreIndicated" />
				</xsl:if>
			</xsl:if>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="renderBeforeEndOfDocument" />

	<xsl:template name="NotIsPreDocketCurrentDateAndSource">
		<div>
			<xsl:call-template name="CurrentDate"/>
			<xsl:call-template name="Source"/>
		</div>
	</xsl:template>

	<xsl:template name="NotIsFederalPreDocketCurrentDate">
		<div>
			<xsl:call-template name="CurrentDate"/>
		</div>
	</xsl:template>
	
	<xsl:template name="ManuallyRetrievedBlock">
		<xsl:variable name="ScrapeDate" select="/Document/n-docbody/r//scrape.date" />
			<xsl:if test="$ScrapeDate">
				
					<div class="&docketsToOrderClass;">
						<div>
							<strong>
								<xsl:text>&docketsManuallyRetrieved;</xsl:text>
							</strong>
						</div>
						<div>
							<strong>
								<xsl:call-template name="DocketsDate">
									<xsl:with-param name="date" select="$ScrapeDate" />
								</xsl:call-template>
								<xsl:choose>
									<xsl:when test="$IsStatePreDocket">
										<xsl:text>&docketsMayNotBeElectronicallyAvailable;</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>&docketsMayNotBeAvailableOnPacer;</xsl:text>		
									</xsl:otherwise>
								</xsl:choose>
							</strong>
						</div>
					</div>
				
			</xsl:if>
	</xsl:template>	
	
	<xsl:template name="CurrentBlock" priority="1">
		<xsl:variable name="ScrapeDate" select="/Document/n-docbody/r/scrape.date" />
		<xsl:if test="$ScrapeDate or $DisplayDocketUpdateLink = true()">
			<div class="&docketsCurrentBlockClass;">
				<xsl:call-template name="CurrentBlockProductView"></xsl:call-template>
				<xsl:apply-templates select="$ScrapeDate" mode="render" />
				<!--Remove UPDATE button and a track link (Docket Track Alert bell) from Family Court dockets for Bucks County, PA-->
				<!--Remove UPDATE button and a track link (Docket Track Alert bell) from USITC dockets filed prior to 11/12/2008 (Bug 796818) -->
				<xsl:variable name="jurisAbbrev" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisabbrev" />
				<xsl:variable name="dateFiled" select="/Document/n-metadata/metadata.block/md.dates/md.filedate"/>
				<xsl:variable name="knosCodesCount" select="count(/Document/n-docbody/r/case.information.block/key.nature.of.suit.block/knos.code[starts-with(., '160')])"/>
				
				<xsl:if test="not($knosCodesCount > 0 and $jurisAbbrev = 'PA-BUCKS') and $IsSuppressDocketTrackLinks = 'false'">
					<xsl:call-template name="ToUpdate" />
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CurrentBlockProductView">
		<!-- nothing for Next to render -->
	</xsl:template>
	
	<xsl:template name="ToUpdate">
		<xsl:if test="$DisplayDocketUpdateLink = true() and $IsPublicRecords = false()">
			<xsl:variable name="updateLink">
				<xsl:call-template name="getDocketsUpdateLink" />
			</xsl:variable>
			<xsl:if test="string-length($updateLink) &gt; 0">
				<div class="&docketsToUpdateClass;">
					<xsl:call-template name="ToUpdateProductView"></xsl:call-template>
					<xsl:call-template name="renderDocketsUpdateLink">
						<xsl:with-param name="updateLink" select="$updateLink"/>
					</xsl:call-template>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ToUpdateProductView">
		<xsl:text>&docketsToUpdate;</xsl:text>
	</xsl:template>

	<xsl:template match="strikeout">
		<strike>
			<xsl:apply-templates />
		</strike>
	</xsl:template>
	
	<xsl:template name="RenderDocketImage">
		<xsl:param name="imageBlock" />
		<xsl:param name="index" />

		<!-- Suppression of PDF links except when we have a local  See bug 677473 for details -->
		<xsl:variable name="imageId" select="$imageBlock/image.gateway.link/@image.id|$imageBlock/image.gateway.link/@image.ID"/>
		<xsl:variable name="localImageGuid" >
			<xsl:call-template name="ParseImageGuid">
				<xsl:with-param name="imageGuid" select="substring-before($imageId,';')" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($localImageGuid) &gt; 0 or $IsSuppressDocketTracksCalendarPdfAndCreditorLinks = 'false'">
				<xsl:apply-templates select="$imageBlock">
					<xsl:with-param name="suppressPDF">
						<xsl:copy-of select="$IsSuppressDocketTracksCalendarPdfAndCreditorLinks"/>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderSendRunnerLink">
					<xsl:with-param name="index" select="$index"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:variable name="checkSum" select="/Document/n-docbody/persist.info/checksum" />	
		<xsl:variable name="docPersistId" select="/Document/n-docbody/persist.info/id" />
		<xsl:variable name="caseNumber" select="/Document/n-docbody/r/docket.block/docket.number | /Document/n-docbody/r/case.information.block/docket.block/docket.number" />		
		<xsl:variable name="court" select="/Document/n-docbody/r/c/court.norm" />
		<xsl:variable name="batchPdfHref">
			<xsl:choose>
				<xsl:when test="$Guid or $docPersistId">
					<xsl:call-template name="BatchPdfHref">
						<xsl:with-param name="documentGuid" select="$Guid" />
						<xsl:with-param name="pdfIndex" select="$index" />
						<xsl:with-param name="checkSum" select="$checkSum" />
						<xsl:with-param name="docPersistId" select="$docPersistId" />
					</xsl:call-template>
				 </xsl:when>
				<xsl:otherwise>
					 <xsl:call-template name="BatchPdfHrefWithCaseNumber">
						<xsl:with-param name="pdfIndex" select="$index" />
						<xsl:with-param name="caseNumber" select="$caseNumber" />
						<xsl:with-param name="court" select="$court" />
					</xsl:call-template>
				</xsl:otherwise>
			 </xsl:choose>
		</xsl:variable>
		
		<!-- Bug #677473 - Add suppression check to conditions for showing the button -->
		<xsl:if test="(string-length($batchPdfHref) &gt; 0 and $IsPublicRecords = false()) and 
		(($IsSuppressDocketTracksCalendarPdfAndCreditorLinks = 'false' or string-length($localImageGuid) &gt; 0) and 
		($IAC-PDF-MULTIPART-CHECK = true() or $imageBlock/image.gateway.link[@court!='N_DCAORANGE']))">
			<a href="{$batchPdfHref}" class="&docketProceedingsButtonClass;">
				<xsl:text>&docketBatchDownloadText;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="RenderSendRunnerLink">
		<xsl:param name="index" />
		<xsl:variable name="checkSum" select="/Document/n-docbody/persist.info/checksum" />	
		<xsl:variable name="docPersistId" select="/Document/n-docbody/persist.info/id" />
		<xsl:variable name="caseNumber" select="/Document/n-docbody/r/docket.block/docket.number | /Document/n-docbody/r/case.information.block/docket.block/docket.number" />		
		<xsl:variable name="court" select="/Document/n-docbody/r/c/court.norm" />
		<xsl:variable name="sendRunnerHref">
			<xsl:choose>
				<xsl:when test="$Guid or $docPersistId">
					<xsl:call-template name="SendRunnerHref">
						<xsl:with-param name="documentGuid" select="$Guid" />
						<xsl:with-param name="orderedIndex" select="$index" />
						<xsl:with-param name="checkSum" select="$checkSum" />
						<xsl:with-param name="docPersistId" select="$docPersistId" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="SendRunnerHrefWithCaseNumber">
						<xsl:with-param name="orderedIndex" select="$index" />
						<xsl:with-param name="caseNumber" select="$caseNumber" />
						<xsl:with-param name="court" select="$court" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($sendRunnerHref) &gt; 0 and $IsPublicRecords = false()">
			<a id="&docketSendRunnerLinkId;{$index}" class="&docketProceedingsButtonClass; &docketSendRunnerLinkClass;" href="{$sendRunnerHref}">
				<i></i>
				<xsl:text>&docketSendRunnerText;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>
 
	<xsl:template match="image.link">
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
		<xsl:variable name="countImage" select="count(preceding::image.link) + 1"/>
		<xsl:variable name="guid">
			<xsl:choose>
				<xsl:when test="string-length(@target) &gt; 30">
					<xsl:value-of select="@target" />
				</xsl:when>
				<xsl:when test="string-length(@tuuid) &gt; 30">
					<xsl:value-of select="@tuuid" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>NotValidImage</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($guid) &gt; 0">
			<xsl:choose>
				<xsl:when test="/*/ImageMetadata/n-metadata[md.blobref.guid = $guid]">
					<xsl:apply-templates select="/*/ImageMetadata/n-metadata[md.blobref.guid = $guid]" mode="MakeImageLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="imageNumInDoc" select="$countImage"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="/*/ImageMetadata/n-metadata/@guid = $guid">
					<xsl:apply-templates select="/*/ImageMetadata/n-metadata[@guid = $guid]" mode="MakeImageLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="text" select="$text"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="displayIcon" select="$displayIcon"/>
						<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
						<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
						<xsl:with-param name="imageNumInDoc" select="$countImage"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$IsStatePreDocket or $IsFederalPreDocket or $IsPreDocket">
					<xsl:call-template name="createPreDocketComplaintLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="text" select="$text"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="displayIcon" select="$displayIcon"/>
						<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
						<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
						<xsl:with-param name="countImage" select="$countImage"/>
						<xsl:with-param name="targetType" select="@ttype"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<span class="&imageNonDisplayableClass;">&tableOrGraphicNotDisplayableText;</span>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderDocketsTrackHiddenLinkWLN">
		<xsl:variable name="url">
			<xsl:call-template name="createDocketsTrackMashupLinkWLN" />
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
	
	<xsl:template match="color">
		<span style="font-weight: bold; background-color:aqua;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template name="AdditionsAreIndicated">
		<div class="&docketsToOrderClass;">
			<div>
				ADDITIONS ARE INDICATED BY <span style="font-weight: bold; background-color:aqua;">TEXT</span>; DELETIONS BY <strike>TEXT</strike>.
			</div>
		</div>
	</xsl:template>
	
	<xsl:template name="renderDocumentTopBlock">
		<xsl:call-template name="renderDocumentTopBlockWithoutDisclaimer"/>
	</xsl:template>
	
	<xsl:template name="renderDocumentTopBlockWithoutDisclaimer">
		<xsl:call-template name="renderDocketOrdersAccessBlock"/>
		<xsl:call-template name="renderDocumentTopBodyBlock"/>
		<xsl:call-template name="renderDocketDateBlock"/>
	</xsl:template>

	<xsl:template name="renderDocketOrdersAccessBlock">
		<xsl:if test="$HasDocketOrdersAccess">
				<xsl:call-template name="ToOrderTop" />
				<xsl:if test="descendant-or-self::color or descendant-or-self::strikeout">
					<xsl:call-template name="AdditionsAreIndicated" />
				</xsl:if>
			</xsl:if>
	</xsl:template>
	
	<xsl:template name="renderDocumentTopBodyBlock">
			<xsl:choose>
				<xsl:when test="$IsPreDocket = true()">
					<xsl:call-template name="ManuallyRetrievedBlock"/>		
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="CurrentBlock"/>
					<xsl:if test="$DisplayDocketTrackLink = true() and $IsSuppressDocketTracksCalendarPdfAndCreditorLinks = 'false' and $IsSuppressDocketTrackLinks = 'false'">
						<xsl:call-template name="renderDocketsTrackHiddenLinkWLN" />
					</xsl:if>
					<xsl:if test="$DisplayDocketUpdateLink = true()">
						<xsl:call-template name="renderDocketsUpdateHiddenJsonObject" />
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>

	<xsl:template name="renderDocketDateBlock">
		<xsl:choose>
			<xsl:when test="not($IsPreDocket = true())">
				<xsl:call-template name="NotIsPreDocketCurrentDateAndSource"></xsl:call-template>
			</xsl:when>
			<xsl:when test="not($IsFederalPreDocket = true())">
				<xsl:call-template name="NotIsFederalPreDocketCurrentDate"></xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
