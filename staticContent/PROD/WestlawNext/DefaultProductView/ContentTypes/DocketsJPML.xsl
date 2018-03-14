<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocketsFederal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="r" priority="1">
		<table>
			<xsl:call-template name="FirstSection"></xsl:call-template>
		</table>
		<xsl:apply-templates select="*[not(self::court.block or self::title.block or self::docket.block or self::panel.block or self::filing.date.block or self::case.status.flag or self::closed.date.block or self::other.dockets.block or self::published.opinion.block or self::mdl.jurisdiction.block or self::lead.case.block)]" />
	</xsl:template>

	<xsl:template name="FirstSection">
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="title.block"/>
		<xsl:apply-templates select="docket.block"/>
		<xsl:apply-templates select="panel.block"/>
		<xsl:apply-templates select="filing.date.block"/>
		<xsl:apply-templates select="published.opinion.block"/>
		<xsl:apply-templates select="lead.case.block"/>
		<xsl:apply-templates select="mdl.jurisdiction.block"/>
		<xsl:apply-templates select="other.dockets.block"/>
		<xsl:apply-templates select="closed.date.block"/>
		<xsl:apply-templates select="case.status.flag"/>		
	</xsl:template>

	<xsl:variable name="assoc.case">
		<xsl:value-of select="count(//assoc.case.block/*)" />
	</xsl:variable>
	
	<xsl:variable name="member.case">
		<xsl:value-of select="count(//member.case.block/*)" />
	</xsl:variable>
	
	<xsl:variable name="related.case">
		<xsl:value-of select="count(//related.case.block/*)" />
	</xsl:variable>
	
	<xsl:variable name="part.info">
		<xsl:value-of select="count(//plaintiff.party/*)" />
	</xsl:variable>

	<xsl:template match="jpml.assoc.case.block" priority="1">
		<xsl:call-template name="JpmlAssocCaseBlockAsTable"></xsl:call-template>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template name="JpmlAssocCaseBlockAsTable">
		<div class="&docketsSubSection;">
			<h2 class="&docketsHeading;">
				<xsl:text>&docketsJPMLAssociatedCases;</xsl:text>
				<xsl:if test="($assoc.case &lt; 500) and ($member.case &lt; 500) and ($related.case &lt; 500) and ($part.info &lt; 500)">
					<xsl:if test="not($IsMobile)">
						<a class="&widgetToggleIcon;">Toggle Section</a>
					</xsl:if>
				</xsl:if>
			</h2>
			<table class="&docketsTable;">
				<xsl:call-template name="JpmlAssocCaseBlockTableHeaderRow"/>
				<xsl:call-template name="JpmlAssocCaseBlockTableRows"/>
			</table>
		</div>
	</xsl:template>
	
	<xsl:template name="JpmlAssocCaseBlockTableHeaderRow">
		<tr class="&docketsRowClass;">
			<th>
				<xsl:text>&docketsJPMLDocketNumber;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJPMLCaseNameStatus;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJPMLStartDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJPMLEndDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJPMLOriginalDistrict;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJPMLOriginalDocketNumber;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="JpmlAssocCaseBlockTableRows">
		<xsl:for-each select="assoc.case.block/assoc.case.entry">
			<tr class="&docketsRowClass;">
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="docket.number" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="case.name" />
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
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="original.case.block/original.district" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="original.case.block/original.docket.number" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="mdl.member.case.block" priority="1">
		
		<xsl:if test="count(*[(self::member.case.block)]) > 0">
			<xsl:call-template name="MemberCaseBlockAsTable"></xsl:call-template>
		</xsl:if>

		<xsl:if test="count(*[(self::related.case.block)]) > 0">
			<xsl:call-template name="RelatedCaseBlockAsTable"></xsl:call-template>
		</xsl:if>
		
	</xsl:template>

	<xsl:template name="MemberCaseBlockAsTable">
		<div class="&docketsSubSection;">
			<h2 class="&docketsHeading;">
				<xsl:text>&docketMDLMemberCases;</xsl:text>
				<xsl:if test="($assoc.case &lt; 500) and ($member.case &lt; 500) and ($related.case &lt; 500) and ($part.info &lt; 500)">
					<xsl:if test="not($IsMobile)">
						<a class="&widgetToggleIcon;">Toggle Section</a>
					</xsl:if>
				</xsl:if>
			</h2>
			<table class="&docketsTable;">
				<xsl:call-template name="MemberCaseEntryTableHeaderRow">
					<xsl:with-param name="caseNameLabel" select="'&docketsMDLCaseName;'" />
				</xsl:call-template>
				<xsl:call-template name="MemberCaseEntryTableRows">
					<xsl:with-param name="templateMatch" select="member.case.block/member.case.entry" />
				</xsl:call-template>
			</table>
		</div>
	</xsl:template>
	
	<xsl:template name="MemberCaseEntryTableHeaderRow">
		<xsl:param name="caseNameLabel"/>
		<tr class="&docketsRowClass;">
			<th>
				<xsl:value-of select="$caseNameLabel" />
			</th>
			<th>
				<xsl:text>&docketsMDLStartDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsMDLEndDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsMDLDocketNumber;</xsl:text>
			</th>
		</tr>
	</xsl:template>
	
	<xsl:template name="MemberCaseEntryTableRows">
		<xsl:param name="templateMatch"/>
		<xsl:for-each select="$templateMatch">
			<tr class="&docketsRowClass;">
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="case.name" />
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
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="docket.number" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="RelatedCaseBlockAsTable">
		<div class="&docketsSubSection;">
			<h2 class="&docketsHeading;">
				<xsl:text>&docketMDLRelatedCases;</xsl:text>
				<xsl:if test="($assoc.case &lt; 500) and ($member.case &lt; 500) and ($related.case &lt; 500) and ($part.info &lt; 500)">
					<xsl:if test="not($IsMobile)">
						<a class="&widgetToggleIcon;">Toggle Section</a>
					</xsl:if>
				</xsl:if>
			</h2>
			<table class="&docketsTable;">
				<xsl:call-template name="MemberCaseEntryTableHeaderRow">
					<xsl:with-param name="caseNameLabel" select="'&docketsJPMLRelatedCases;'" />
				</xsl:call-template>
				<xsl:call-template name="MemberCaseEntryTableRows">
					<xsl:with-param name="templateMatch" select="related.case.block/member.case.entry" />
				</xsl:call-template>
			</table>
		</div>
	</xsl:template>
	
	<xsl:template match="party.block" priority="1">
		<div class="&docketsSubSection;">
			<h2 class="&docketsHeading;">
				<xsl:text>&docketsParticipantInformation;</xsl:text>
				<xsl:if test="($assoc.case &lt; 500) and ($member.case &lt; 500) and ($related.case &lt; 500) and ($part.info &lt; 500)">
					<xsl:if test="not($IsMobile)">
						<a class="&widgetToggleIcon;">Toggle Section</a>
					</xsl:if>
				</xsl:if>
			</h2>					
			<table>
				<xsl:for-each select="plaintiff.party | defendant.party">
					<xsl:apply-templates select="party.type" />
					<xsl:apply-templates select="party.attorney.block" />
				</xsl:for-each>
			</table>
		</div>
	</xsl:template>	
	

	<xsl:template match="published.opinion.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="published.opinion.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lead.case.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="lead.case.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="case.status.flag">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:text>Case Status: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mdl.jurisdiction.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="mdl.jurisdiction" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>

