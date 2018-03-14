<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&docketsClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="ToOrderTop" />
			<xsl:apply-templates />
			<xsl:call-template name="ToOrderBottom" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="r">
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:call-template name="CaseInformation" />
		<xsl:call-template name="ParticipantInformation" />
	</xsl:template>


	<xsl:template name="CaseInformation">
		<h2 class="&docketSubHeading;">
			<xsl:text>&docketsCaseInformation;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="CaseInformationSection" />
		</table>
	</xsl:template>

	<xsl:template name="CaseInformationSection">
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="court.b/court" />
			<xsl:with-param name="labelText" select="'&docketsCourt;'" />
		</xsl:call-template>
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="court.b/court2" />
			<xsl:with-param name="labelText" select="'&docketsDivision;'" />
		</xsl:call-template>
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="cs.nbr.b/cs.nbr" />
			<xsl:with-param name="labelText" select="'&docketsCaseNumber;'" />
		</xsl:call-template>
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="cs.typ.b/cs.typ" />
			<xsl:with-param name="labelText" select="'&docketsCaseType;'" />
		</xsl:call-template>
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="filg.d" />
			<xsl:with-param name="labelText" select="'&docketsFilingDate;'" />
		</xsl:call-template>		
	</xsl:template>

	<xsl:template name="ParticipantInformation">
		<h2 id="&docketsParticipantInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsParticipantInformation;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="ParticipantInformationSection" />
		</table>
	</xsl:template>

	<xsl:template name="ParticipantInformationSection">
		<xsl:call-template name="Plaintiff" />
		<xsl:call-template name="Defendant" />
		<xsl:call-template name="Other" />		
	</xsl:template>
	
	<xsl:template name="Plaintiff">
		<xsl:for-each select="nm.info.b/pltf.info.b">
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="pltf.nm.b/pltf.nm" />
				<xsl:with-param name="labelText" select="'&docketsName;'" />
			</xsl:call-template>
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsType;'" />
				<xsl:with-param name="text" select="'&docketsPlaintiff;'" />
			</xsl:call-template>			
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="Defendant">
		<xsl:for-each select="nm.info.b/def.info.b">
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="def.nm.b/def.nm" />
				<xsl:with-param name="labelText" select="'&docketsName;'" />
			</xsl:call-template>
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsType;'" />
				<xsl:with-param name="text" select="'&docketsDefendant;'" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="Other">
		<xsl:for-each select="nm.info.b/oth.nm.prty.b">
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="oth.nm.b/oth.nm" />
				<xsl:with-param name="labelText" select="'&docketsName;'" />
			</xsl:call-template>
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsType;'" />
				<xsl:with-param name="text" select="'&docketsOther;'" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
