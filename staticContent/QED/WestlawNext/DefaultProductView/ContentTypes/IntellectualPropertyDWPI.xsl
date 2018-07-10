﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Patents.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeDWPIClass;'"/>
			</xsl:call-template>
			<div class="&contentTypeIPDocumentClass;">
				<xsl:call-template name="displayDerwentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="displayDerwentDocument">
		<xsl:call-template name="displayDerwentHeader" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<table class="&layout_table;">
			<xsl:call-template name="displayDerwentDocBody" />
		</table>
	</xsl:template>

	<xsl:template name="displayDerwentHeader">
		<table class="&layout_table; &layout_headerTable;">
			<tr>
				<td>
					<div>
						<h2>
							<xsl:value-of select="//md.ip.descriptions/md.ip.title" />
						</h2>
					</div>
					<div class="&panelBlockClass;">
						<xsl:apply-templates select="//derwent.prim.no.b" />
					</div>
				</td>
				<td align="right">
					<xsl:apply-templates select="//image.block" mode="dwpiHeaderImageBlock" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="displayDerwentDocBody">
		<xsl:call-template name="displayPatentInfoSection" />
		<xsl:apply-templates select="//derwent.info/derwent.class.info" />
		<xsl:apply-templates select="//derwent.info/derwent.abstract" />
		<xsl:apply-templates select="//derwent.info/reference.block" />
		<xsl:apply-templates select="//image.block" />
		<xsl:apply-templates select="//family.members.b" />
	</xsl:template>

	<xsl:template name="displayPatentInfoSection">
		<tr>
			<td class="&borderTopClass;">
				<xsl:variable name="nodeNameKey" select="'derwentpatentsection'" />
				<xsl:variable name="defaultText" select="'Patent'" />
				<strong>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
				</strong>
			</td>
			<td class="&borderTopClass;">
				<xsl:apply-templates select="//derwent.title.b" /> 
				<xsl:apply-templates select="//derwent.title.terms.b" /> 
				<xsl:apply-templates select="//derwent.prim.no.b" /> 
				<xsl:apply-templates select="//derwent.rel.no.b" />
				<xsl:apply-templates select="//derwent.inventors" />
				<xsl:apply-templates select="//derwent.assignees" />
				<xsl:apply-templates select="//derwent.priorities" /> 
				<xsl:apply-templates select="//derwent.related.docs" /> 
				<xsl:apply-templates select="//num.countries.b" /> 
				<xsl:apply-templates select="//num.patents.b" /> 
				<xsl:apply-templates select="//derwent.dates.b" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="derwent.abstract" priority="1">
		<tr>
			<td class="&borderTopClass;">
				<div>
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext" />
					<strong>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
					</strong>
				</div>
			</td>
			<td class="&borderTopClass;">
				<xsl:apply-templates select="./*[not(name() = 'head')]" />
			</td>
		</tr>
	</xsl:template>
	
	<!-- Derwent Info -->
	<xsl:template match="derwent.info/derwent.class.info | derwent.info/derwent.abstract | derwent.info/reference.block">
		<tr>
			<td class="&borderTopClass;">
				<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
				<xsl:variable name="defaultText" select="./head/headtext" />
				<strong>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
				</strong>
			</td>
			<td class="&borderTopClass;">
				<xsl:apply-templates select="./node()[not(name() = 'head')]" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="family.members.b">
		<tr>
			<td colspan="2">
				<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
				<xsl:variable name="defaultText" select="./head/headtext" />
				<h2 class="&sectionHeaderClass;">
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
				</h2>
				
			</td>
		</tr>
		<xsl:apply-templates select="family.member" />
	</xsl:template>
		
	<xsl:template match="family.members.b/family.member">
		<tr>
			<!--<td>
				<xsl:variable name="nodeNameKey" select="derwentfamilymembersection" />
				<xsl:variable name="defaultText" select="'Publication No. (Derwent)'" />
				<strong>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
				</strong>
			</td>-->
			<!-- Currently, the characters used for this text cause issues for the static text file.
				TODO: figure out if there is a way to retrieve this value from a static text file. -->
			<td>
				<xsl:if test="preceding-sibling::family.member">
					<xsl:attribute name="class">
						<xsl:value-of select="'&borderTopClass;'" />
					</xsl:attribute>
				</xsl:if>
				<strong>
					<xsl:text>Publication No. (Derwent)</xsl:text>
				</strong>
			</td>
			<td>
				<xsl:if test="preceding-sibling::family.member">
					<xsl:attribute name="class">
						<xsl:value-of select="'&borderTopClass;'" />
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="./node()" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="publication.no.b/label" priority="1" />

	<xsl:template match="derwent.prim.no.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Derwent Info - Derwent Title Terms Block -->
	<xsl:template match="derwent.info/derwent.title.terms.b">
		<div class="&panelBlockClass;">
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
	<xsl:template match="derwent.assignees">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
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
	<xsl:template match="derwent.priorities">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="priority.app.b">
		<div>
			<xsl:apply-templates select="priority.app.no"/>
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="priority.app.date"/>
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="priority.type"/>
		</div>
	</xsl:template>

	<xsl:template match="earliest.priority.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Derwent Info - Derwent Related Docs -->
	<xsl:template match="derwent.related.docs">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="related.doc.b">
		<div>
			<xsl:apply-templates select="related.doc.text" />
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="related.doc.no" />
		</div>
	</xsl:template>

	<!-- Derwent Info - Number of Patents -->
	<xsl:template match="derwent.info/num.patents.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="num.countries.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Derwent Info - Derwent Classification Info -->
	<xsl:template match="intl.class.b | us.class.b | euro.class.b | euro.ico.class.b | jp.class.b | derwent.class.b | derwent.manual.codes.b | cpc.b">
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::*[1]/self::head)">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
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
		<div>
			<xsl:apply-templates select="category" />
			<xsl:text>&nbsp;</xsl:text>
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select="chem.class | eng.class | eng.code | elec.class | chem.code | elec.code" />
			</xsl:call-template>
		</div>
	</xsl:template>
	
	<xsl:template match="derwent.info/derwent.abstract/* | reference.block/*">
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::*[1]/self::head)">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="derwent.abstract//para">
		<!-- Display paragraphs as sentences in the abstract block. -->
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="derwent.abstract//para/paratext">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
	<xsl:template match="patent.ref.b">
		<div>
			<xsl:apply-templates select="patent.no" />
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="patent.date" />
		</div>
	</xsl:template>

	<xsl:template match="lit.ref">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="lit.ref/text()">
		<xsl:choose>
			<xsl:when test="not($PreviewMode)">
				<xsl:value-of select="." disable-output-escaping="no" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="citing.patent.b">
		<div>
			<xsl:apply-templates select="citing.pat.no" />
			<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
			<xsl:apply-templates select="citing.pat.date" />
		</div>
	</xsl:template>

	<xsl:template match="family.member/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Derwent Patent Family - Patent Info -->
	<xsl:template match="family.member/patent.info/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="family.member//inventor.b">
		<div>
			<xsl:apply-templates select="inventor.info"/>
		</div>
	</xsl:template>

	<xsl:template match="family.member//assignee.b">
		<div>
			<xsl:apply-templates select="assignee.info"/>
			<xsl:if test="assignee.code">
				<xsl:text>&nbsp;(</xsl:text>
				<xsl:apply-templates select="assignee.code"/>
				<xsl:text>)</xsl:text>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="family.member//agent.b">
		<div>
			<xsl:apply-templates select="agent.info"/>
		</div>
	</xsl:template>

	<!-- Derwent Patent Family - Classification Info -->
	<xsl:template match="family.member//intl.class.b | family.member//intl.class.orig.b | family.member//us.class.b | family.member//us.class.orig.b | family.member//euro.class.b | family.member//euro.ico.class.b | family.member//jp.class.b | family.member//cpc.b">
		<xsl:call-template name="wrapWithDiv" />
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

	<xsl:template match="image.block" priority="1">
		<!-- In the document body we only want to display images if there is more than one.
				If there is only one image then it is already being displayed in the header. -->
		<xsl:if test="image.link[2]">
			<tr>
				<td>
				</td>
				<td>
					<xsl:apply-templates />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="family.member/patent.info/*[not(self::publication.no.b)] | family.member/class.info | family.member/claims.block/*[not(self::label)] | family.member/designated.states/* 
						| family.member/drawings.info | family.member/pages.info | family.member/related.docs | family.member/priorities | family.member/derwent.release.b | family.member/source.lang
						| patent.titles/patent.title.b | filing.app.b/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="derwent.dates.b/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="derwent.inventors">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="image.block" mode="dwpiHeaderImageBlock">
		<xsl:apply-templates select="image.link[1]">
			<xsl:with-param name="className" select="'&dwpiImageClass;'"/>
		</xsl:apply-templates>
	</xsl:template>

</xsl:stylesheet>