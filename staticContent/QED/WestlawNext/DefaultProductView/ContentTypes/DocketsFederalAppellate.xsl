<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="r">
		<table>
			<xsl:call-template name="FirstSection"></xsl:call-template>
		</table>			
		<xsl:apply-templates select="*[not(self::court.block or self::title.block or self::appeal.from.block or self::panel.block or self::panel.date.block or self::filing.date.block)]" />
	</xsl:template>

	<xsl:template name="FirstSection">
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="title.block"/>
		<xsl:apply-templates select="appeal.from.block"/>
		<xsl:apply-templates select="panel.block"/>
		<xsl:apply-templates select="panel.date.block"/>
		<xsl:apply-templates select="filing.date.block"/>		
	</xsl:template>
	
	<!-- PDF gateway links generally occuring in the docket.entry/docket description -->
	<xsl:template match="docket.description/gateway.image.link"  name="gatewayimagelink">
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
	<xsl:param name="localImageGuid"/>
		<xsl:variable name="court">
			<xsl:call-template name="getCourtNumber">
				<xsl:with-param name="courtNumber" select="@court" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="casenumber" select="@casenumber" />
		<xsl:variable name="id" select="@key" />
		<xsl:variable name="platform" select="@ttype" />
		<xsl:variable name="mimeType">
			<xsl:value-of select="'&pdfMimeType;'" />
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
			<xsl:with-param name="localImageGuid" select="$localImageGuid"/>
			<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			<xsl:with-param name="filename">
				<xsl:call-template name="createPdfFilename">
					<xsl:with-param name="cite" select="$Cite"/>
					<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
					<xsl:with-param name="date" select="ancestor::docket.entry/date"/>
					<xsl:with-param name="number" select="concat(ancestor::docket.entry/number.block/number, '-', ./text())"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
	<xsl:template match="appeal.from.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="appeal.from" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fee.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="fee.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="district.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="district.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="district.docket.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:choose>
					<xsl:when test="district.docket.number.INF">
						<xsl:apply-templates select="district.docket.number.INF" />
					</xsl:when>
					<xsl:when test="district.docket.number">
						<xsl:apply-templates select="district.docket.number" />
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trial.judge.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="trial.judge" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="court.reporter.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="court.reporter" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="court.reporter">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="panel.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="panel.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.filed.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="date.filed" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.order.judgment.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="date.order.judgment" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.noa.filed.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="date.noa.filed" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.coa.received.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="date.coa.received" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.cases">
		<h2 id="&docketsOtherCasesId;" class="&docketsHeading;">
			<xsl:text>&docketsOtherCases;</xsl:text>
		</h2>
		<xsl:apply-templates />			
	</xsl:template>

	<xsl:template match="prior.cases.block">
		<xsl:call-template name="PriorCasesAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="PriorCasesAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="PriorCasesTableHeaderRow"/>
			<xsl:call-template name="PriorCasesTableRows"/>
		</table>		
	</xsl:template>
	
	<xsl:template name="PriorCasesTableHeaderRow">
		<tr>
			<xsl:for-each select="label">
				<th>
					<xsl:apply-templates />
				</th>
			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template name="PriorCasesTableRows">
		<xsl:for-each select="prior.cases.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="prior.docket.number.block/prior.docket.number.INF" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="prior.cases.judge" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="prior.cases.date.filed" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="date.disposed" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="disposition.code" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="PriorCasesForIphone">
		<div class="&docketsSubSection; &docketsNotExpandable; &docketsSectionCollapsed;">
			<div class="&docketsReportBody; &docketsHasPadding;">
				<xsl:for-each select="prior.cases.entry">
					<div>
						<h3>
							<xsl:apply-templates select="../label[1]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="prior.docket.number.block/prior.docket.number.INF" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[2]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="prior.cases.judge" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[3]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="prior.cases.date.filed" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[4]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="date.disposed" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[5]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="disposition.code" />
							</xsl:with-param>
						</xsl:call-template>
					</div>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="current.cases.block">
		<xsl:call-template name="CurrentCasesAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="CurrentCasesAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="CurrentCasesTableHeaderRow"/>
			<xsl:call-template name="CurrentCasesTableRows"/>
		</table>
	</xsl:template>
	
	<xsl:template name="CurrentCasesTableHeaderRow">
		<tr>
			<xsl:for-each select="label">
				<th>
					<xsl:apply-templates />
				</th>
			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template name="CurrentCasesTableRows">
		<xsl:for-each select="current.case.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="relationship" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="lead.docket.block/lead.docket.number.INF" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="member.docket.block/member.docket.number.INF" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="start.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="end.date" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="CurrentCasesForIphone">
		<div class="&docketsSubSection; &docketsNotExpandable; &docketsSectionCollapsed;">
			<div class="&docketsReportBody; &docketsHasPadding;">
				<xsl:for-each select="current.case.entry">
					<div>
						<h3>
							<xsl:apply-templates select="../label[1]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="relationship" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[2]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="lead.docket.block/lead.docket.number.INF" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[3]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="member.docket.block/member.docket.number.INF" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[4]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="start.date" />
							</xsl:with-param>
						</xsl:call-template>
						<h3>
							<xsl:apply-templates select="../label[5]"/>
						</h3>
						<xsl:call-template name="DocketsTableCell">
							<xsl:with-param name="text">
								<xsl:apply-templates select="end.date" />
							</xsl:with-param>
						</xsl:call-template>
					</div>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="party.block" name="generalPartyBlock">
		<xsl:apply-templates />
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="party.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 class="&docketsHeading;">
				<xsl:text>&docketsNames;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="generalPartyBlock"/>
		</div>
	</xsl:template>

	<xsl:template match="party">
		<div class="&docketsSubSection;">
			<xsl:apply-templates select="party.name.block" />
			<xsl:if test="party.type.block or party.aka.block or party.address.block or party.phone.block or party.fax.block or party.email.block or party.attorney.block">
				<table>
					<xsl:call-template name="PartySection"></xsl:call-template>
				</table>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="PartySection">
		<xsl:apply-templates select="party.type.block" />
		<xsl:apply-templates select="party.aka.block" />
		<xsl:apply-templates select="party.address.block" />
		<xsl:apply-templates select="party.phone.block" />
		<xsl:apply-templates select="party.fax.block" />
		<xsl:apply-templates select="party.email.block" />
		<xsl:apply-templates select="party.attorney.block" />	
	</xsl:template>

	<xsl:template match="party.fax.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.fax" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.email.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.email" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.aka.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.aka" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
