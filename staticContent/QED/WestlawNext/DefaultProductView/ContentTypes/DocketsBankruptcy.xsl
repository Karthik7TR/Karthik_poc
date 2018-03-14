<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="n-docbody">
		<xsl:if test="$PreviewMode">
			<h2 class="&docketsHeading;">
				<xsl:text>&docketsCaseInformation;</xsl:text>
			</h2>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="summary" priority="1">
		<table>
			<xsl:apply-templates />
		</table>
		<!--<xsl:if test="preceding-sibling::court.block and preceding-sibling::title.block and not(preceding-sibling::case.information.block) and child::case.number.block">-->
		<xsl:if test="not(following-sibling::claims.info.block) and $IsSuppressDocketTracksCalendarPdfAndCreditorLinks = 'false'">
			<xsl:call-template name="ToCreditor" />
		</xsl:if>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<!--</xsl:if>-->
	</xsl:template>

	<xsl:template name="ToCreditor">
		<xsl:call-template name="ToCreditorAsTable" />
	</xsl:template>

	<xsl:template name="ToCreditorAsTable">
		<h2 class="&docketsHeading;">
			<xsl:text>&docketsToCreditor;</xsl:text>
		</h2>
		<xsl:variable name="url">
			<xsl:call-template name="createDocketsCreditorMashupLink" />
		</xsl:variable>
		<table>
			<tr class="&docketsRowClass;">
				<td class="&docketsRowLabelClass;">
					<xsl:if test="string-length($url) &gt; 0">
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$url"/>
							</xsl:attribute>
							<xsl:attribute name="id">&docketsCreditorLinkId;</xsl:attribute>
							<xsl:text>&docketsCreditorLinkText;</xsl:text>
						</a>
					</xsl:if>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="renderBeforeEndOfDocument">
		<xsl:if test="$IAC-DTP-BANKRUPTCY-CLAIMS and not(descendant::claims.register.block) and (//summary and not(//summary/following-sibling::claims.info.block))">
			<xsl:call-template name="ToClaimsRegister" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="ToClaimsRegister">
		<xsl:call-template name="ToClaimsRegisterAsTable" />
	</xsl:template>

	<xsl:template name="ToClaimsRegisterAsTable">
		<h2 class="&docketsHeading;">
			<xsl:text>&docketsToClaimsRegister;</xsl:text>
		</h2>
		<xsl:variable name="url">
			<xsl:call-template name="createDocketsClaimRegisterMashupLink" />
		</xsl:variable>
		<table>
			<tr class="&docketsRowClass;">
				<td class="&docketsRowLabelClass;">
					<xsl:if test="string-length($url) &gt; 0">
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$url"/>
							</xsl:attribute>
							<xsl:attribute name="id">&docketsClaimRegisterLinkId;</xsl:attribute>
							<xsl:text>&docketsClaimsRegisterLinkText;</xsl:text>
						</a>
					</xsl:if>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="r">
		<xsl:if test="not(child::claims.register.block) or child::claims.register.block/case.info.block">
			<h2 id="&docketsCaseInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsCaseInformation;</xsl:text>
			</h2>
			<table>
				<xsl:call-template name="CaseInformationSection"></xsl:call-template>
			</table>
		</xsl:if>
		<xsl:apply-templates select="*[not(self::court.block or self::title.block or self::docket.block or self::panel.block or self::filing.date.block or discharge.date.block or self::terminated.date.block or self::final.decree.date.block or self::discharge.date.block or self::office.block or self::case.type.block or self::case.details.block or self::reopened.date.block or self::status.block or self::closed.date.block or self::convert.date.block or self::other.dockets.block or self::case.status.flag.block)]" />
	</xsl:template>

	<xsl:template name="CaseInformationSection">
		<xsl:choose>
			<xsl:when test="claims.register.block/case.info.block">
				<xsl:apply-templates select="claims.register.block/case.info.block"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="court.block"/>
				<xsl:apply-templates select="title.block"/>
				<xsl:apply-templates select="docket.block"/>
				<xsl:apply-templates select="panel.block"/>
				<xsl:apply-templates select="filing.date.block"/>
				<xsl:apply-templates select="reopened.date.block"/>
				<xsl:apply-templates select="discharge.date.block"/>
				<xsl:apply-templates select="terminated.date.block"/>
				<xsl:apply-templates select="final.decree.date.block"/>
				<xsl:apply-templates select="office.block"/>
				<xsl:apply-templates select="case.type.block"/>
				<xsl:apply-templates select="case.details.block"/>
				<xsl:apply-templates select="status.block"/>
				<xsl:apply-templates select="closed.date.block"/>
				<xsl:apply-templates select="convert.date.blcok"/>
				<xsl:apply-templates select="other.dockets.blcok"/>
				<xsl:apply-templates select="case.status.flag.block"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="final.decree.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="final.decree.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="discharge.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="discharge.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="terminated.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="terminated.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="office.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="office" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.details.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="asset.case" />
				<xsl:if test="voluntary">
					<xsl:text>, </xsl:text>
					<xsl:apply-templates select="voluntary" />
				</xsl:if>
				<xsl:if test="fee">
					<xsl:text>, </xsl:text>
					<xsl:apply-templates select="fee" />
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reopened.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="reopened.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:if test="status.description">
					<div>
						<xsl:apply-templates select="status.description"/>
					</div>
				</xsl:if>
				<xsl:if test="status.date">
					<div>
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="status.date" />
						</xsl:call-template>
					</div>
				</xsl:if>
				<xsl:if test="status.time">
					<div>
						<xsl:apply-templates select="status.time"/>
					</div>
				</xsl:if>
				<xsl:if test="status.set.by">
					<div>
						<xsl:apply-templates select="status.set.by"/>
					</div>
				</xsl:if>
				<xsl:if test="previous.chapter.block">
					<div>
						<xsl:apply-templates select="previous.chapter.block" />
					</div>
				</xsl:if>
				<xsl:if test="status.set.by.block">
					<div>
						<xsl:apply-templates select="status.set.by.block" />
					</div>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="status.set.by.block">
		<xsl:variable name="text" select="label"/>
		<xsl:value-of select="normalize-space(substring-before($text, ':'))" />
		<xsl:if test="status.set.by">
			<xsl:text>: </xsl:text>
			<xsl:apply-templates select="status.set.by"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="previous.chapter.block">
		<xsl:variable name="text" select="label"/>
		<xsl:value-of select="normalize-space(substring-before($text, ':'))" />
		<xsl:if test="previous.chapter">
			<xsl:text>: </xsl:text>
			<xsl:apply-templates select="previous.chapter"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="case.status.flag.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.status.flag" mode="displayStatusFlag"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.status.flag" mode="displayStatusFlag">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="trustee.info.block" name="generalTrusteeInfoBlock">
		<div class="&docketsSection;">
			<h2 id="&docketsTrusteeInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsTrusteeInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:for-each select="trustee">
				<div class="&docketsSubSection;">
					<xsl:apply-templates select="." />
				</div>
			</xsl:for-each>
		</div>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="trustee.info.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<xsl:call-template name="generalTrusteeInfoBlock"/>
	</xsl:template>

	<xsl:template match="other.info.block" name="generalOtherInfoBlock">
		<div class="&docketsSection;">
			<h2 id="&docketsOtherPartiesId;" class="&docketsHeading;">
				<xsl:text>&docketsOtherParties;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:for-each select="other">
				<div class="&docketsSubSection;">
					<xsl:apply-templates select="." />
				</div>
			</xsl:for-each>
		</div>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="other.info.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<xsl:call-template name="generalOtherInfoBlock"/>
	</xsl:template>

	<xsl:template match="debtor | trustee | other | creditor">
		<xsl:apply-templates select="party.name.block" />
		<xsl:if test="party.address.block or party.ssn.block or party.phone.block or added.date.block or firm.name or party.tax.id.block or party.attorney.block or firm.address.block">
			<table>
				<xsl:call-template name="PartyOrDateOrFirmInfo"></xsl:call-template>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PartyOrDateOrFirmInfo">
		<xsl:apply-templates select="party.address.block" />
		<xsl:apply-templates select="party.ssn.block" />
		<xsl:apply-templates select="party.phone.block" />
		<xsl:apply-templates select="added.date.block" />
		<xsl:apply-templates select="firm.name" />
		<xsl:apply-templates select="party.tax.id.block" />
		<xsl:apply-templates select="party.attorney.block" />
		<xsl:apply-templates select="firm.address.block" />
		<xsl:apply-templates select="firm.phone" />
	</xsl:template>

	<xsl:template match="party.ssn.block">
		<xsl:apply-templates select="ssn"/>
		<xsl:apply-templates select="ssn.frag"/>
	</xsl:template>

	<!-- Social security template -->
	<xsl:template match="ssn[normalize-space(.)]">
		<xsl:if test="$IsPublicRecords = true()">
			<xsl:call-template name="wrapDocketBankruptcySSN">
				<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
				<xsl:with-param name="nodeType" select="$SSN"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Display partial ssn only if full ssn is not there -->
	<xsl:template match="ssn.frag[not(preceding-sibling::ssn or following-sibling::ssn)]" priority="1">
		<xsl:if test="$IsPublicRecords = true()">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
				<xsl:with-param name="nodeType" select="$SSN"/>
				<xsl:with-param name="selectNodes" select="concat('XXX-XX-',normalize-space(.))"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Empty Template to make PublicRecords.xsl happy. -->
	<xsl:template name="PublicRecordsHeader"/>

	<xsl:template match="party.tax.id.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.tax.id" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="schedule.341.block">
		<xsl:call-template name="Schedule341SectionAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="Schedule341SectionAsTable">
		<h2 id="&docketsSchedule341Id;" class="&docketsHeading;">
			<xsl:text>&docketsSchedule341;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="schedule.341.entry"/>
			</xsl:call-template>
		</h2>
		<table class="&docketsTable;">
			<xsl:call-template name="Schedule341SectionTableHeaderRow"/>
			<xsl:call-template name="Schedule341SectionTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="Schedule341SectionTableHeaderRow">
		<tr>
			<xsl:for-each select="label">
				<th>
					<xsl:apply-templates />
				</th>
			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template name="Schedule341SectionTableRows">
		<xsl:for-each select="schedule.341.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:if test="schedule.341.date">
							<xsl:choose>
								<xsl:when test="string-length(schedule.341.date) = 8">
									<xsl:call-template name="DocketsDate">
										<xsl:with-param name="date" select="schedule.341.date" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="schedule.341.date" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="schedule.341.time" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="schedule.341.hearing.location" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="schedule.341.description" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="calendar.entries.block">
		<xsl:call-template name="CalendarSectionAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="CalendarSectionAsTable">
		<h2 id="&docketsCalendarId;" class="&docketsHeading;">
			<xsl:text>&docketsCalendar;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="calendar.entry"/>
			</xsl:call-template>
		</h2>
		<table class="&docketsTable;">
			<xsl:call-template name="CalendarSectionTableHeaderRow"/>
			<xsl:call-template name="CalendarSectionTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="CalendarSectionTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsTime;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsRoom;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJudge;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsCalendarEntry;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="CalendarSectionTableRows">
		<xsl:for-each select="calendar.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="time" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="room" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="judge" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="description" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<!--Start Claims Register section-->

	<!--Start Case Information section-->

	<xsl:template match="case.info.block">
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="title.block"/>
		<xsl:apply-templates select="docket.block"/>
		<xsl:apply-templates select="panel.block"/>
		<xsl:apply-templates select="filing.date.block"/>
		<xsl:apply-templates select="reopened.date.block"/>
		<xsl:apply-templates select="discharge.date.block"/>
		<xsl:apply-templates select="terminated.date.block"/>
		<xsl:call-template name="reterminatedDateBlockDocketsRow"/>
		<xsl:apply-templates select="final.decree.date.block"/>
		<xsl:apply-templates select="office.block"/>
		<xsl:apply-templates select="case.type.block"/>
		<xsl:apply-templates select="case.details.block"/>
		<xsl:apply-templates select="status.block"/>
		<xsl:apply-templates select="closed.date.block"/>
		<xsl:apply-templates select="convert.date.blcok"/>
		<xsl:apply-templates select="other.dockets.blcok"/>
		<xsl:apply-templates select="case.status.flag.block"/>
		<xsl:call-template name="caseNumberBlockDocketsRow"/>
		<xsl:call-template name="claimCountBlockDocketsRow"/>
		<xsl:call-template name="claimTotalBlockDocketsRow"/>
		<xsl:call-template name="claimTotalAllowedBlockDocketsRow"/>
		<xsl:call-template name="lastFilingDateClaimBlockDocketsRow"/>
		<xsl:call-template name="lastFilingDateGovBlockDocketsRow"/>
		<xsl:call-template name="trusteeDocketsRows"/>
	</xsl:template>

	<xsl:template name="lastFilingDateClaimBlockDocketsRow">
		<xsl:if test="last.filing.date.claim.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="last.filing.date.claim.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="last.filing.date.claim.block/last.filing.date.claim" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="lastFilingDateGovBlockDocketsRow">
		<xsl:if test="last.filing.date.gov.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="last.filing.date.gov.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="last.filing.date.gov.block/last.filing.date.gov" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="reterminatedDateBlockDocketsRow">
		<xsl:if test="reterminated.date.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="reterminated.date.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="reterminated.date.block/reterminated.date" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="caseNumberBlockDocketsRow">
		<xsl:if test="case.number.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="case.number.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="case.number.block/case.number" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimCountBlockDocketsRow">
		<xsl:if test="claim.count.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.count.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.count.block/claim.count" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimTotalBlockDocketsRow">
		<xsl:if test="claim.total.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.total.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.total.block/claim.total" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimTotalAllowedBlockDocketsRow">
		<xsl:if test="claim.total.allowed.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.total.allowed.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.total.allowed.block/claim.total.allowed" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="trusteeDocketsRows">
		<xsl:if test="Trustee">
			<xsl:call-template name="claimPartyNameBlockDocketsRow"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimPartyNameBlockDocketsRow">
		<xsl:if test="Trustee/party.name.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="Trustee/party.name.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="Trustee/party.name.block/party.name" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--End Case Information section-->

	<xsl:template match="claims.register.block">
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:call-template name="ClaimsRegisterSectionAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="ClaimsRegisterSectionAsTable">
		<div class="&docketsSection;">
			<h2 id="&docketsClaimsRegisterInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsClaimsRegisterInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="claimsEntryRender" />
		</div>
	</xsl:template>

	<xsl:template name="claimsEntryRender">
		<xsl:choose>
			<xsl:when test="not(claim.entry)">
				<table class="&docketsTable;">
					<tr>
						<th>
							<xsl:text>&docketsNoClaimsRegister;</xsl:text>
						</th>
					</tr>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="claim.entry">
					<div class="&docketsSubSection;">
						<xsl:call-template name="claimNumberBlockDocketsHeaderRow"/>
						<table class="&docketsTable;">
							<xsl:call-template name="claimCreditorInfoBlockDocketsRow"/>
							<xsl:call-template name="claimDateBlockDocketsRow"/>
							<xsl:call-template name="claimEnteredDateBlockDocketsRow"/>
							<xsl:call-template name="claimLastAmendmentFiledBlockDocketsRow"/>
							<xsl:call-template name="claimLastAmendmentEnteredBlockDocketsRow"/>
							<xsl:call-template name="claimStatusBlockDocketsRow"/>
							<xsl:call-template name="claimFiledByBlockDocketsRow"/>
							<xsl:call-template name="claimEnteredByBlockDocketsRow"/>
							<xsl:call-template name="claimModifiedByBlockDocketsRow"/>
							<xsl:call-template name="claimCostBlocksDocketsRow"/>
							<xsl:call-template name="claimFilingStatusBlockDocketsRow"/>
							<xsl:call-template name="claimDocketStatusBlockDocketsRow"/>
							<xsl:call-template name="claimLateBlockDocketsRow"/>
							<xsl:call-template name="claimHistoryBlockDocketsRow"/>
							<xsl:call-template name="claimDescriptionBlockDocketsRow"/>
							<xsl:call-template name="claimNotesBlockDocketsRow"/>
						</table>
						<xsl:processing-instruction name="chunkMarker"/>
					</div>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="claimNumberBlockDocketsHeaderRow">
		<xsl:if test="claim.number.block">
			<xsl:call-template name="DocketsHeaderRow">
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.number.block/label" />
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="claim.number.block/claim.number" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimCreditorInfoBlockDocketsRow">
		<xsl:call-template name="creditorNmaeBlockDocketsRow"/>
		<xsl:apply-templates select="claim.creditor.info.block/creditor.block/creditor.address.block"/>
	</xsl:template>

	<xsl:template name="creditorNmaeBlockDocketsRow">
		<xsl:if test="claim.creditor.info.block/creditor.block/creditor.name.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.creditor.info.block/creditor.block/creditor.name.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.creditor.info.block/creditor.block/creditor.name.block/creditor.name" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimDateBlockDocketsRow">
		<xsl:if test="claim.date.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.date.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="claim.date.block/claim.date" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimEnteredDateBlockDocketsRow">
		<xsl:if test="claim.entered.date.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.entered.date.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="claim.entered.date.block/claim.entered.date" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimLastAmendmentFiledBlockDocketsRow">
		<xsl:if test="claim.last.amendment.filed.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.last.amendment.filed.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="claim.last.amendment.filed.block/claim.last.amendment.filed" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimLastAmendmentEnteredBlockDocketsRow">
		<xsl:if test="claim.last.amendment.entered.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.last.amendment.entered.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="claim.last.amendment.entered.block/claim.last.amendment.entered" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimStatusBlockDocketsRow">
		<xsl:if test="claim.status.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.status.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.status.block/claim.status" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimFiledByBlockDocketsRow">
		<xsl:if test="claim.filed.by.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.filed.by.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.filed.by.block/claim.filed.by" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimEnteredByBlockDocketsRow">
		<xsl:if test="claim.entered.by.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.entered.by.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.entered.by.block/claim.entered.by" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimModifiedByBlockDocketsRow">
		<xsl:if test="claim.modified.by.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.modified.by.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.modified.by.block/claim.modified.by" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimCostBlocksDocketsRow">
		<xsl:if test="claim.cost.block">
			<xsl:for-each select="claim.cost.block">
				<xsl:call-template name="DocketsRow">
					<xsl:with-param name="label">
						<xsl:apply-templates select="label" />
					</xsl:with-param>
					<xsl:with-param name="text">
						<xsl:apply-templates select="claim.cost" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimFilingStatusBlockDocketsRow">
		<xsl:if test="claim.filing.status.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.filing.status.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.filing.status.block/claim.filing.status" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimDocketStatusBlockDocketsRow">
		<xsl:if test="claim.docket.status.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.docket.status.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.docket.status.block/claim.docket.status" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimLateBlockDocketsRow">
		<xsl:if test="claim.late.block ">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.docket.status.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.docket.status.block/claim.late" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimHistoryBlockDocketsRow">
		<xsl:if test="claim.History.block">
			<xsl:for-each select="claim.History.block">
				<xsl:call-template name="DocketsRow">
					<xsl:with-param name="label">
						<xsl:apply-templates select="label" />
					</xsl:with-param>
					<xsl:with-param name="text">
						<xsl:if test="image.block/image.gateway.link">
							<xsl:call-template name="wrapWithSpan">
								<xsl:with-param name="contents">
									<xsl:call-template name="claimImageGatewayLink">
										<xsl:with-param name="link" select="image.block/image.gateway.link" />
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:value-of select="claim.history" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimImageGatewayLink">
		<xsl:param name="link" />

		<xsl:if test="$link">
			<xsl:variable name="court">
				<xsl:call-template name="getCourtNumber">
					<xsl:with-param name="courtNumber" select="$link/@court" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="casenumber" select="$link/@casenumber" />
			<xsl:variable name="id" select="$link/@image.ID | $link/@image.id" />
			<xsl:variable name="platform" select="$link/@platform" />
			<xsl:variable name="mimeType">
				<xsl:value-of select="'&pdfMimeType;'" />
			</xsl:variable>
			<xsl:variable name="localImageGuid">
				<xsl:call-template name="ParseImageGuid">
					<xsl:with-param name="imageGuid" select="substring-before($id,';')" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="filename">
				<xsl:call-template name="createPdfFilename">
					<xsl:with-param name="cite" select="$Cite" />
					<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'" />
					<xsl:with-param name="date" select="$link/ancestor::claim.entry/claim.date.block/claim.date" />
					<xsl:with-param name="number" select="concat($link/ancestor::claim.entry/claim.number.block/claim.number, '-', $link/text())" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="displayToolTip">
				<xsl:if test="string-length($localImageGuid) &gt; 0 and $HasPassThruPdfsAccess">
					<xsl:value-of select="'&docketExhibitLocalToolTip;'" />
				</xsl:if>
			</xsl:variable>

			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:call-template name="createDocumentGatewayBlobLink">
				<xsl:with-param name="court" select="$court" />
				<xsl:with-param name="courtNumber" select="$JurisdictionNumber "/>
				<xsl:with-param name="casenumber" select="$casenumber" />
				<xsl:with-param name="id" select="$id" />
				<xsl:with-param name="platform" select="$platform" />
				<xsl:with-param name="mimeType" select="$mimeType" />
				<xsl:with-param name="contents" select="$link/text()" />
				<xsl:with-param name="className" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'" />
				<xsl:with-param name="filename" select="$filename" />
				<xsl:with-param name="displayToolTip" select="$displayToolTip" />
				<xsl:with-param name="localImageGuid" select="$localImageGuid" />
			</xsl:call-template>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimDescriptionBlockDocketsRow">
		<xsl:if test="claim.description.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.description.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.description.block/claim.description" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="claimNotesBlockDocketsRow">
		<xsl:if test="claim.notes.block">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label">
					<xsl:apply-templates select="claim.notes.block/label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="claim.notes.block/claim.notes" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--End Claims Register Section-->

	<xsl:template match="claims.info.block[not(descendant::claim.creditor.info.block)]">
		<xsl:call-template name="ClaimsInformationSectionAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="ClaimsInformationSectionAsTable">
		<h2 id="&docketsClaimsInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsClaimsInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="claim.entry"/>
			</xsl:call-template>
		</h2>
		<table class="&docketsTable;">
			<xsl:call-template name="ClaimsInformationSectionTableHeaderRow" />
			<xsl:call-template name="ClaimsInformationSectionTableRows" />
		</table>
	</xsl:template>

	<xsl:template name="ClaimsInformationSectionTableHeaderRow">
		<tr>
			<xsl:for-each select="label">
				<th>
					<xsl:apply-templates />
				</th>
			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template name="ClaimsInformationSectionTableRows">
		<xsl:for-each select="claim.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:if test="claim.date">
							<xsl:call-template name="DocketsDate">
								<xsl:with-param name="date" select="claim.date" />
							</xsl:call-template>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="claim.number" />
					</xsl:with-param>
				</xsl:call-template>
				<!-- Creditor Information only shows up when creditor link is clicked see matches for claim.creditor.info.block -->
				<td>
					<xsl:choose>
						<xsl:when test="not($IsIpad)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text><![CDATA[-]]></xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="claim.info.block" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<!-- START claim.info.block children -->

	<xsl:template match="claim.notes.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="claim.notes" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="claim.cost.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="claim.cost" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="claim.total.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="claim.total" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- END claim.info.block children -->

	<!-- START special creditor matches -->

	<xsl:template match="claims.info.block[descendant::claim.creditor.info.block]">
		<xsl:if test="not(preceding-sibling::summary)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsCreditorInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsCreditorInformation;</xsl:text>
				<xsl:call-template name="DisplayCount">
					<xsl:with-param name="nodes" select="claim.entry/claim.creditor.info.block"/>
				</xsl:call-template>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:apply-templates select="claim.entry[claim.creditor.info.block]" />
		</div>
	</xsl:template>

	<xsl:template match="claim.creditor.info.block">
		<xsl:for-each select="creditor.block">
			<div class="&docketsSubSection;">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="creditor.block">
		<xsl:apply-templates select="creditor.name.block" />
		<xsl:if test="creditor.address.block or creditor.phone.block">
			<table>
				<xsl:apply-templates select="creditor.address.block" />
				<xsl:apply-templates select="creditor.phone.block" />
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="creditor.name.block">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="creditor.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="creditor.address.block">
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

	<xsl:template match="creditor.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="creditor.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- END special creditor matches -->

	<xsl:template match="creditorlist" />

</xsl:stylesheet>
