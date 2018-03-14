<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="IntellectualPropertyPatentsEurope.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:variable name="jurisdictionValue" select="//md.ip.juris" />
	<xsl:variable name="fullTextAvailable" select="DocumentExtension:JurisdictionFullTextAvailable('INPADOC', $jurisdictionValue)"></xsl:variable>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsEuropeClass;'"/>
			</xsl:call-template>
			<div class="&sazanamiMinchoClass; &contentTypeIPDocumentClass;">
				<xsl:call-template name="displayInternationalPatentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Suppress the Family ID from being displayed naturally so 
			we can force it to display below the title. -->
	<xsl:template match="doc.family.no.b" />

	<xsl:template match="md.gateway.image.link" priority="1">
		<xsl:if test="$fullTextAvailable">
			<xsl:call-template name="createIPImageLink" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="doc.family.no.b" mode="inpadocDisplay">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Force the Family ID to be displayed after the title element here. -->
	<xsl:template match="patent.info/patent.title.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
		<xsl:apply-templates select="//intl.patent/doc.family.no.b" mode="inpadocDisplay" />
	</xsl:template>

	<xsl:template match="patent.info/pub.app.b | patent.info/filing.app.b | patent.info/application.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pub.app.b/pub.app.no.b | pub.app.b/pub.app.date.b | application.b/app.no.b | application.b/app.date.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paratextMainClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="euro.class.b | nat.class.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass; &indentLeft1Class;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="euro.class.b/euro.class.list | nat.class.b/nat.class.list">
		<xsl:call-template name="join">
			<xsl:with-param name="nodes" select="./*" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="publication.b/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="patent.ref.b">
		<div>
			<xsl:apply-templates select="patent.no" />
			<xsl:if	test="patent.date or applicant.name">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:apply-templates select="patent.date" />
			<xsl:if	test="applicant.name">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:apply-templates select="applicant.name" />
		</div>
	</xsl:template>

	<xsl:template match="cite.query[@w-ref-type='PA'] | cite.query[@w-ref-type='PH'] | cite.query[@w-ref-type='PW'] | cite.query[@w-ref-type='PG'] | cite.query[@w-ref-type='EQ']">
			<xsl:value-of select="." />
	</xsl:template>

</xsl:stylesheet>
