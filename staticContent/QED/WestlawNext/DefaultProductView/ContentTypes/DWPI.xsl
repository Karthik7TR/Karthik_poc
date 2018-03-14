<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Patents.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeDWPIClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Derwent Info -->
	<xsl:template match="derwent.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&patentInfoClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="derwent.info/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Derwent Info - Derwent Title Terms Block -->
	<xsl:template match="derwent.info/derwent.title.terms.b">
		<div class="&panelBlockClass; &indentLeft1Class;">
			<xsl:apply-templates select="label" />
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="title.term" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Derwent Info - Related Numbers -->
	<xsl:template match="derwent.info/derwent.rel.no.b">
		<div class="&panelBlockClass;">
			<xsl:apply-templates select="label" />
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="derwent.rel.no" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Derwent Info - Derwent Other Number Block -->
	<xsl:template match="derwent.info/derwent.other.no.b">
		<!-- Hide this block -->
	</xsl:template>

	<!-- Derwent Info - Derwent Inventors -->
	<xsl:template match="inventor.name">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Derwent Info - Derwent Assignees -->
	<xsl:template match="assignee.b">
		<div>
			<xsl:apply-templates select="assignee.name"/>
			<xsl:text>&nbsp;&nbsp;(</xsl:text>
			<xsl:apply-templates select="sublabel"/>
			<xsl:text>&nbsp;</xsl:text>
			<xsl:apply-templates select="assignee.code"/>
			<xsl:text>)</xsl:text>
		</div>
	</xsl:template>

	<!-- Derwent Info - Derwent Priorities -->
	<xsl:template match="priority.app.b">
		<div class="&indentLeft1Class;">
			<xsl:apply-templates select="priority.app.no"/>
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="priority.app.date"/>
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="priority.type"/>
		</div>
	</xsl:template>

	<xsl:template match="earliest.priority.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass; &indentLeft1Class;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Derwent Info - Derwent Related Docs -->
	<xsl:template match="related.doc.b">
		<div class="&indentLeft1Class;">
			<xsl:apply-templates select="related.doc.text" />
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="related.doc.no" />
		</div>
	</xsl:template>

	<!-- Derwent Info - Number of Patents -->
	<xsl:template match="derwent.info/num.patents.b">
		<!-- Don't use the panelBlockClass here. -->
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Derwent Info - Derwent Dates Block -->
	<xsl:template match="first.derwent.release.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="latest.derwent.release.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Derwent Info - Derwent Classification Info -->
	<xsl:template match="intl.class.b | us.class.b | euro.class.b | euro.ico.class.b | jp.class.b | derwent.class.b | derwent.manual.codes.b | cpc.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass; &indentLeft1Class;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.list | us.class.list | euro.class.list | euro.ico.class.list | jp.class.list | cpc.list">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="intl.class.main | intl.class | us.class.main | us.class | euro.class.main | euro.class | euro.ico.class.main | euro.ico.class | jp.class.main | jp.class | cpc.main | cpc" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="intl.class.list[label] | us.class.list[label] | euro.class.list[label] | euro.ico.class.list[label] | jp.class.list[label] | cpc.list[label]">
		<div>
			<xsl:apply-templates select="label" />
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="intl.class.main | intl.class | us.class.main | us.class | euro.class.main | euro.class | euro.ico.class.main | euro.ico.class | jp.class.main | jp.class | cpc.main | cpc" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="chem.class.b | eng.class.b | eng.codes.b | elec.class.b | chem.codes.b | elec.codes.b">
		<div class="&indentLeft2Class;">
			<xsl:apply-templates select="category" />
			<xsl:text>&nbsp;</xsl:text>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="chem.class | eng.class | eng.code | elec.class | chem.code | elec.code" />
			</xsl:call-template>
		</div>
	</xsl:template>
	
	<!-- Derwent Info - Derwent Abstract -->
	<xsl:template match="derwent.info/derwent.abstract">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="derwent.abstract/*[preceding-sibling::head[1]]">
		<div class="&simpleContentBlockClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="derwent.info/derwent.abstract/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="derwent.abstract//para">
		<!-- Display paragraphs as sentences in the abstract block. -->
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="derwent.abstract//para/paratext">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
	<!-- Derwent Info - Derwent Reference Block -->
	<xsl:template match="patent.refs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.ref.b">
		<div>
			<xsl:apply-templates select="patent.no" />
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="patent.date" />
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

	<xsl:template match="citing.patents">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="citing.patent.b">
		<div>
			<xsl:apply-templates select="citing.pat.no" />
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="citing.pat.date" />
		</div>
	</xsl:template>

	<!-- Derwent Patent Family -->
	<xsl:template match="family.members.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&coFamilyMembersClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="family.member">
		<xsl:choose>
			<xsl:when test="position() != last()">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="family.member/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Derwent Patent Family - Patent Info -->
	<xsl:template match="family.member/patent.info/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="family.member//patent.title.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="family.member//filing.app.no.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="family.member//filing.app.date.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="family.member//inventor.b">
		<div class="&indentLeft1Class;">
			<xsl:apply-templates select="inventor.info"/>
		</div>
	</xsl:template>

	<xsl:template match="family.member//assignee.b">
		<div class="&indentLeft1Class;">
			<xsl:apply-templates select="assignee.info"/>
			<xsl:if test="assignee.code">
				<xsl:text>&nbsp;(</xsl:text>
				<xsl:apply-templates select="assignee.code"/>
				<xsl:text>)</xsl:text>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="family.member//agent.b">
		<div class="&indentLeft1Class;">
			<xsl:apply-templates select="agent.info"/>
		</div>
	</xsl:template>

	<!-- Derwent Patent Family - Classification Info -->
	<xsl:template match="family.member//intl.class.b | family.member//intl.class.orig.b | family.member//us.class.b | family.member//us.class.orig.b | family.member//euro.class.b | family.member//euro.ico.class.b | family.member//jp.class.b | family.member//cpc.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&indentLeft1Class;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="family.member//intl.class.list[sublabel] | family.member//intl.class.orig.list[sublabel] | family.member//us.class.list[sublabel] | family.member//us.class.orig.list[sublabel] | family.member//euro.class.list[sublabel] | family.member//euro.ico.class.list[sublabel] | family.member//jp.class.list[sublabel] | family.member//cpc.list[sublabel]">
		<div>
			<xsl:apply-templates select="sublabel"/>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="intl.class.main | intl.class | intl.class.orig.main | intl.orig.class | us.class.main | us.class | us.class.orig.main | us.orig.class | euro.class.main | euro.class | euro.ico.class.main | euro.ico.class | jp.class.main | jp.class | cpc.main | cpc" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="family.member//intl.class.list[not(sublabel)] | family.member//intl.class.orig.list[not(sublabel)] | family.member//us.class.list[not(sublabel)] | family.member//us.class.orig.list[not(sublabel)] | family.member//euro.class.list[not(sublabel)] | family.member//euro.ico.class.list[not(sublabel)] | family.member//jp.class.list[not(sublabel)] | family.member//cpc.list[not(sublabel)]">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="intl.class.main | intl.class | intl.class.orig.main | intl.orig.class | us.class.main | us.class | us.class.orig.main | us.orig.class | euro.class.main | euro.class | euro.ico.class.main | euro.ico.class | jp.class.main | jp.class | cpc.main | cpc" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="family.member//sublabel">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&labelClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Derwent Patent Family - Claims Block -->
	<xsl:template match="family.member/claims.block//claim.para">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="family.member/claims.block//paratext">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="family.member/claims.block//lang">
		<xsl:text>(</xsl:text>
		<xsl:apply-templates />
		<xsl:text>)&nbsp;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
