<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="PatentsTablesInternational.xsl"/>
	<xsl:include href="IntellectualProperty.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsEuropeClass;'"/>
			</xsl:call-template>
			<div class="&sazanamiMinchoClass; &contentTypeIPDocumentClass;">
				<!--<xsl:apply-templates select="//md.gateway.image.link" />-->
				<xsl:call-template name="displayInternationalPatentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- hide these nodes -->
	<xsl:template match="prelim/doc.subtype | related.priority.code | prelim/doc.type"/>

	<!-- Patent Info -->
	<xsl:template match="patent.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&patentInfoClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="granted.pat.b/*">
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

	<xsl:template match="patent.info/applicants">
		<div class="&panelBlockClass;">
			<xsl:apply-templates select="label" />
			<xsl:apply-templates select="applicant.b" />
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

	<xsl:template match="applicant.b">
		<xsl:choose>
			<xsl:when test="position() = '1' or not(applicant.limit.b | applicant.residence.b | applicant.nationality.b)">
				<xsl:call-template name="wrapWithDiv" />
			</xsl:when>
			<xsl:otherwise>
				<!-- Only add the top margin (panelBlockClass) when there are multiple rows for each applicant. Never add a top margin to the first applicant. -->
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="inventor.addr.b | applicant.b/applicant.name | applicant.b/applicant.addr.b">
		<!-- Hide these nodes. -->
	</xsl:template>

	<xsl:template match="inventor.b//label | applicant.b//label">
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

	<xsl:template match="assignee.addr.b">
		<!-- Hide these nodes. -->
	</xsl:template>

	<xsl:template match="assignee.b//label">
		<!-- Prevent these labels from being marked strong. -->
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&labelClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/pub.app.b | patent.info/filing.app.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pub.app.b/pub.app.no.b | pub.app.b/pub.app.date.b">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<!-- Other Parties -->
	<xsl:template match="other.parties">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_otherParties &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="agents">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="agent.b">
		<div>
			<xsl:apply-templates select="agent.info" />
		</div>
	</xsl:template>

	<!-- Priority Info -->
	<xsl:template match="priority.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="priority.app.b">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="priority.app.no | priority.app.date" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="pct.filing.app.b">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="pct.filing.app.no | pct.filing.app.date" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addl.app.b">
		<div>
			<xsl:if test="preceding-sibling::*[1][self::head]">
				<xsl:attribute name="class">
					<xsl:value-of select="'&panelBlockClass;'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="addl.app.no | addl.app.date" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="pct.pub.app.b">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="pct.pub.app.no | pct.pub.app.date" />
		</xsl:call-template>
	</xsl:template>

	<!-- Classification Info -->
	<xsl:template match="class.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_classificationInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.b | cpc.b | nat.class.b">
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

	<xsl:template match="nat.class.list">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="nat.class.main | nat.class" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="cpc.list">
		<div>
			<xsl:apply-templates select="label" />
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="cpc.main | cpc" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Reference Block -->
	<xsl:template match="reference.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&referenceBlockClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reference.block/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lit.ref">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&patentLitRefClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.ref.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="other.patent.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="history.info | designated.states">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="history.info/hist.event.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="hist.event.date">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Designated States -->
	<xsl:template match="designated.states">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_designatedStates &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="search.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="desig.states.ext.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="related.doc.b">
		<div>
			<xsl:if test="preceding-sibling::*[1][self::head]">
				<xsl:attribute name="class">
					<xsl:value-of select="'&panelBlockClass;'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="related.doc.text"/>
			<xsl:text>&#160;</xsl:text>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="related.doc.no | related.doc.date"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="pct.pub.apps">
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::*[1][self::pct.filing.apps])">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="priority.info/pct.apps | priority.info/pct.filing.apps | priority.info/priorities | priority.info/related.docs | earliest.priority.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="definition">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&indentLeft1Class;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.term">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="patent.date">
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="division//headtext">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="division//paratext//text()">
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="hist.opponent.b | hist.agent.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&indentLeft1Class;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.opponent.b/* | hist.agent.b/*" />

	<xsl:template match="hist.opponent.b/label | hist.agent.b/label">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="hist.opponent.b/label | hist.agent.b/label">
		<xsl:apply-templates />
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="hist.opponent.b/opponent.info | hist.agent.b/agent.info">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="search.type.b">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="search.type | search.text" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="hist.des.states">
		<xsl:text>,&#160;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
