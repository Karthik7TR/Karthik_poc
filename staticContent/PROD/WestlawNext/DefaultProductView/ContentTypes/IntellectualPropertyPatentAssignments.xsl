<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Date.xsl"/>
	<xsl:include href="IntellectualProperty.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsAssignmentsClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="displayAssignmentHeader" />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="displayAssignmentBody" />
			<xsl:call-template name="displayAffectedPatentsSection" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="content.metadata.block | unprocessed.bucket | prelim" />
	
	<xsl:template name="displayAssignmentHeader">
		<div>
			<strong>
				<xsl:choose>
					<xsl:when test="//md.first.line.cite">
						<xsl:apply-templates select="//md.first.line.cite" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="//md.second.line.cite" />
					</xsl:otherwise>
				</xsl:choose>
			</strong>
		</div>
		<div>
			<xsl:apply-templates select="//recorded.date" mode="headerDisplay" />
		</div>
	</xsl:template>

	<!-- Don't display these within the main table.  They will get displayed in their own table.
			This will be done in a separate, named, template. -->
	<xsl:template match="patents.affected | patents.affected/patent.info | patents.affected/head/headtext" priority="1" />

	<xsl:template name="displayAssignmentBody">
		<table class="&patentAssignmentsBodyTable;">
			<tr>
				<td class="&borderTopClass; &verticalAlignTopClass; &horizontalRuleClass;">
					<xsl:variable name="assignmentKey" select="'pattentAssignmentsBodySectionLabel'" />
					<xsl:variable name="assignmentDefault" select="'Assignment'" />
					<div class="&panelBlockClass;">
						<strong>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $assignmentKey, $assignmentDefault)"/>
						</strong>
					</div>
				</td>
				<td class="&borderTopClass;">
					<xsl:apply-templates />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="displayAffectedPatentsSection">
		<div class="&panelBlockClass;">
			<xsl:variable name="patentsAffectedSectionKey" select="'patentsAffected'" />
			<xsl:variable name="patentsAffectedDefaultText" select="'Patents Affected'" />
			<strong>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $patentsAffectedSectionKey, $patentsAffectedDefaultText)" />
			</strong>
		</div>
		<table class="&patentAssignmentsAffectedTable; &detailsTable;">
			<thead>
				<tr>
					<th>
						<xsl:variable name="titleKey" select="'patentsAffectedTableHeaderTitle'" />
						<xsl:variable name="titleDefault" select="'Title'" />
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $titleKey, $titleDefault)"/>
					</th>
					<th>
						<xsl:variable name="patentKey" select="'patentsAffectedTableHeaderPatent'" />
						<xsl:variable name="patentDefault" select="'Patent'" />
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $patentKey, $patentDefault)"/>
					</th>
					<th>
						<xsl:variable name="publishedKey" select="'patentsAffectedTableHeaderPublished'" />
						<xsl:variable name="publishedDefault" select="'Published'" />
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $publishedKey, $publishedDefault)"/>
					</th>
					<th>
						<xsl:variable name="applicationKey" select="'patentsAffectedTableHeaderApplication'" />
						<xsl:variable name="applicationDefault" select="'Application'" />
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $applicationKey, $applicationDefault)"/>
					</th>
					<th>
						<xsl:variable name="regNumberKey" select="'patentsAffectedTableHeaderRegNumber'" />
						<xsl:variable name="regNumberDefault" select="'International Registration Number'" />
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $regNumberKey, $regNumberDefault)"/>
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="//patents.affected/patent.info" mode="displayPatentsAffectedTable" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="patents.affected/patent.info" mode="displayPatentsAffectedTable">
		<tr>
			<td class="&verticalAlignTopClass;">
				<xsl:apply-templates select="patent.title.b/patent.title" />
			</td>
			<td class="&verticalAlignTopClass;">
				<xsl:apply-templates select="granted.pat.b" />
			</td>
			<td class="&verticalAlignTopClass;">
				<xsl:apply-templates select="pub.app.b" />
			</td>
			<td class="&verticalAlignTopClass;">
				<xsl:apply-templates select="filing.app.b" />
			</td>
			<td class="&verticalAlignTopClass;">
				<xsl:apply-templates select="intl.reg.b" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="granted.pat.no.b/label | granted.pat.date.b/label | pub.app.no.b/label
							| pub.app.date.b/label | filing.app.no.b/label | filing.app.date.b/label | intl.reg.no.b/label | intl.reg.date.b/label" priority="1" />
	
	<xsl:template match="granted.pat.no | granted.pat.date | pub.app.no | pub.app.date | filing.app.no | filing.app.date | intl.reg.no | intl.reg.date">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="recorded.date" mode="headerDisplay" priority="1">
		<xsl:call-template name="parseYearMonthDayDateFormat">
			<xsl:with-param name="date" select="translate(., '-','')" />
			<xsl:with-param name="displayDay" select="1" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assignment/node()[not(self::content.metadata.block) and not(self::prelim) and not(self::unprocessed.bucket)]" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assignment//label">
		<strong>
			<xsl:apply-templates />
			<xsl:text>: </xsl:text>
		</strong>
	</xsl:template>

	<xsl:template match="assignee.b[preceding-sibling::assignee.b]" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="assignee.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
		
	<xsl:template match="assignor.b[preceding-sibling::assignor.b]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assignee.addr.b">
		<xsl:for-each select="assignee.addr.line">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:for-each>
		<div>
			<xsl:apply-templates select="assignee.city" />
			<xsl:if test="assignee.city and assignee.st">
				<xsl:text>,&#160;</xsl:text>
			</xsl:if>
			<xsl:apply-templates select="assignee.st" />
			<xsl:text>&#160;</xsl:text>
			<xsl:apply-templates select="assignee.post.cd" />
		</div>
		<div>
			<xsl:apply-templates select="assignee.country" />
		</div>
	</xsl:template>
	
	<xsl:template match="agent.b[preceding-sibling::assignor.b]" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="agent.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
	
	<xsl:template match="agent.addr.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="execution.date.b | acknowledge.date.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="source">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="assign.patent.info/* | assign.parties/* | summary/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="affected.patent/* | assignee/* | assignor.block/* | agent/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- If there is a content block that ends in a link here then Microsoft Word and RTF 
			documents will not use the correct styles for that block (the styles will get ignored).
			Adding a space after the link will cause RTF to deliver properly. -->
	<xsl:template match="patent.number" priority="1">
		<div>
			<xsl:apply-templates />
			<xsl:text>&#160;</xsl:text>
		</div>
	</xsl:template>
</xsl:stylesheet>