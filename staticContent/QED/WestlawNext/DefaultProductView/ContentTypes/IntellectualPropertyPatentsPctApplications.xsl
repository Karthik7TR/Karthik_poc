<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="PatentsTablesInternational.xsl" />
	<xsl:include href="IntellectualProperty.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsPctApplicationsClass;'"/>
			</xsl:call-template>
			<div class="&sazanamiMinchoClass; &contentTypeIPDocumentClass;">
				<!--<xsl:apply-templates select="//md.gateway.image.link" />
				<xsl:apply-templates select="//cmd.first.line.cite" />-->
				<xsl:call-template name="displayInternationalPatentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="cmd.first.line.cite">
		<div class="&citesClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Hide the platform citation display since it will display the second line cite instead of first.line.cite which is what we want for this content. -->
	<xsl:template match="cmd.second.line.cite" />

	<!-- Patent Info -->
	<xsl:template match="patent.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&patentInfoClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/gateway.doc.image.link">
		<!-- Hide this node. -->
	</xsl:template>

	<xsl:template match="pub.app.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="filing.app.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="patent.info/inventors">
		<div class="&panelBlockClass;">
			<xsl:apply-templates select="label" />
			<xsl:apply-templates select="inventor.b" />
		</div>
	</xsl:template>

	<xsl:template match="inventor.b">
		<xsl:choose>
			<xsl:when test="position() = '1' or not(inventor.limit.b | inventor.residence.b | inventor.nationality.b)">
				<xsl:call-template name="wrapWithDiv" />
			</xsl:when>
			<xsl:otherwise>
				<!-- Only add the top margin (panelBlockClass) when there are multiple rows for each inventor. Never add a top margin to the first inventor. -->
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="inventor.name | inventor.addr.b">
		<!-- Hide these nodes. -->
	</xsl:template>

	<xsl:template match="inventor.limit.b | inventor.residence.b | inventor.nationality.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="inventor.b//label">
		<!-- Prevent these labels from being marked strong. -->
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&labelClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/assignees">
		<div class="&panelBlockClass;">
			<xsl:apply-templates select="label" />
			<xsl:apply-templates select="assignee.b" />
		</div>
	</xsl:template>

	<xsl:template match="assignee.b">
		<xsl:choose>
			<xsl:when test="position() = '1' or not(assignee.limit.b | assignee.residence.b | assignee.nationality.b)">
				<xsl:call-template name="wrapWithDiv" />
			</xsl:when>
			<xsl:otherwise>
				<!-- Only add the top margin (panelBlockClass) when there are multiple rows for each assignee. Never add a top margin to the first assignee. -->
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="assignee.name | assignee.addr.b">
		<!-- Hide these nodes. -->
	</xsl:template>

	<xsl:template match="assignee.limit.b | assignee.residence.b | assignee.nationality.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="assignee.b//label">
		<!-- Prevent these labels from being marked strong. -->
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&labelClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Other Parties -->
	<xsl:template match="other.parties">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_otherParties &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="agents">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="agent.b">
		<div>
			<xsl:apply-templates select="agent.info" />
		</div>
	</xsl:template>

	<xsl:template match="priorities">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">
				<xsl:text>&panelBlockClass;</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="priority.app.b">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="priority.app.no | priority.app.date" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="earliest.priority.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Classification Info -->
	<xsl:template match="class.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_classificationInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.list">
		<div>
			<xsl:apply-templates select="label" />
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="intl.class.main | intl.class" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Reference Block -->
	<xsl:template match="reference.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&referenceBlockClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.ref.b">
		<div>
			<xsl:apply-templates select="patent.no" />
		</div>
	</xsl:template>

	<xsl:template match="other.patent.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lit.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lit.ref">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Designated States -->
	<xsl:template match="designated.states">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_designatedStates &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Search Info -->
	<xsl:template match="search.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_searchInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="search.authority.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_searchInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="search.type.list">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_searchInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="search.type.b">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="*" />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>
