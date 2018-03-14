<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="IntellectualProperty.xsl"/>
	<xsl:include href="PatentsTables.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsGrantedClass;'"/>
			</xsl:call-template>
			<div class="&contentTypeIPDocumentClass;">
				<xsl:call-template name="displayPatentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Currently inline images are not available for Granted Patents. -->
	<xsl:template match="image.link" />

	<xsl:template match="maths">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<!-- Patent Info -->
	<xsl:template match="patent.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_patentInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.info/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="estimated.expiration.date.b" priority="1">
	</xsl:template>

	<xsl:template match="patent.info/notice.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&indentLeft1Class;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.type">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="granted.pat.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="granted.pat.no">
		<xsl:apply-templates /> 
	</xsl:template>

	<xsl:template match="granted.pat.date">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="pub.app.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="filing.app.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="filing.app.no">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="filing.app.date">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="inventor.b">
		<div>
			<xsl:call-template name="join" >
				<xsl:with-param name="nodes" select="inventor.name | inventor.addr.b/inventor.addr.line" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="assignee.b">
		<div>
			<xsl:call-template name="join" >
				<xsl:with-param name="nodes" select="assignee.name | assignee.addr.b/assignee.addr.line" />
			</xsl:call-template>
		</div>
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
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="examiners">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="examiner.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Reissue Info -->
	<xsl:template match="reissue.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_reissueInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="orig.patent.no.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="orig.patent.date.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="orig.filing.no.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="orig.filing.date.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Priority Info -->
	<xsl:template match="priority.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_priorityInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pct.docs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pct.app.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="pct.app.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="related.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="priority.docs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="priority.app.b">
		<div>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="priority.app.no | priority.app.date | priority.app.jur.text" />
				<xsl:with-param name="separator" select="'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="earliest.priority.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="earliest.priority.date">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Classification Info -->
	<xsl:template match="class.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_classificationInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ipc.edition.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.list">
		<xsl:call-template name="join" >
			<xsl:with-param name="nodes" select="*" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cpc.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nat.class.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nat.class.list">
		<xsl:call-template name="join" >
			<xsl:with-param name="nodes" select="*" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lang">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="fos.class.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fos.class.list">
		<xsl:call-template name="join" >
			<xsl:with-param name="nodes" select="*" />
		</xsl:call-template>
	</xsl:template>

	<!-- Reference Block -->
	<xsl:template match="reference.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&referenceBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.ref.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="us.patent.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="us.patent.ref.b">
		<div>
			<xsl:call-template name="join" >
				<xsl:with-param name="nodes" select="patent.ref | pub.date | inventor.name | nat.class" />
				<xsl:with-param name="separator" select="'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="other.patent.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.patent.ref.b">
		<div>
			<xsl:call-template name="join" >
				<xsl:with-param name="nodes" select="patent.ref | pub.date | country.text" />
				<xsl:with-param name="separator" select="'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="lit.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lit.ref">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Unprocessed Bucket -->
	<xsl:template match="unprocessed.bucket">
		<!-- Hide this -->
	</xsl:template>

</xsl:stylesheet>
