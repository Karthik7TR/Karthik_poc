<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKSecondarySources.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UKSecondarySourcesToc.xsl" />
	<xsl:include href="PrevNextNavigation.xsl"/>
	<xsl:include href="ProvisionsTree.xsl"/>


	<!--hmrc-->
	<xsl:param name="PrevProvisionTitle" />
	<xsl:param name="NextProvisionTitle" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name ="contentType" select ="'&ukWestlawContent;'"/>
	<xsl:variable name="documentType">
		<xsl:choose>
			<xsl:when test="$infoType='&legisAOAInfoType;'">
				<xsl:text>&HRMCArrangementType;</xsl:text>
			</xsl:when>
			<xsl:when test="$infoType='&HRMCManualInfoType;'">
				<xsl:text>&HRMCManualType;</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name ="showLoading" select="true()"/>

	<!-- ** Document structure ** -->
	<xsl:template name="BuildSpecificDocument">
		<xsl:choose>
			<xsl:when test="$documentType='&HRMCArrangementType;'">
				<xsl:call-template name="OneColumnDocument" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="GeneralDocument" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ** Document header ** -->
	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<xsl:value-of select="$documentType"/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="BuildHRMCManualHeader">
		<xsl:variable name="headers" select="//n-docbody/document/headers/*" />
		<xsl:variable name="arrangementTitle" select="($headers)[1]"/>
		<xsl:variable name="arrangementGuid" select="//n-docbody/document/fulltext_metadata/aoctop/link/@tuuid"/>
		<xsl:variable name="headersCount" select="count($headers)" />
		<xsl:variable name="documentTitle" select="($headers)[number($headersCount)-1]"/>
		<xsl:variable name="documentNumber" select="($headers)[number($headersCount)]"/>
		<xsl:variable name="hoverOver">
			<xsl:call-template name="CreateHoverOver">
				<xsl:with-param name="headers" select="$headers" />
				<xsl:with-param name="currentHeaderPosition" select="number($headersCount)-1"/>
			</xsl:call-template>
		</xsl:variable>

		<h1 class="&titleClass;">
			<a>
				<xsl:attribute name="title">
					<xsl:value-of select="$hoverOver"/>
				</xsl:attribute>
				<xsl:attribute name="href">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name="documentGuid" select="$arrangementGuid"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:value-of select="$arrangementTitle"/>
			</a>
			<br/>
			<xsl:value-of select="$documentNumber"/>
			<xsl:text>&nbsp;&mdash;&nbsp;</xsl:text>
			<xsl:value-of select="$documentTitle"/>
		</h1>
	</xsl:template>

	<!--Create hover over message for header-->
	<xsl:template name="CreateHoverOver">
		<xsl:param name="headers"/>
		<xsl:param name="currentHeaderPosition"/>
		<xsl:for-each select="$headers">
			<xsl:if test="(position() != 2) and (position() != 3) and (position() &lt; $currentHeaderPosition)">
				<xsl:if test="position() &gt; 1">
					<xsl:text> &gt; </xsl:text>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="BuildHRMCArrangementHeader">
		<h1 class="&titleClass;">
			<xsl:value-of select="//pgroup-title[1]"/>
		</h1>
	</xsl:template>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:choose>
			<xsl:when test="$documentType='&HRMCArrangementType;'" >
				<xsl:call-template name="BuildHRMCArrangementHeader" />
			</xsl:when>
			<xsl:when test="$documentType='&HRMCManualType;'" >
				<xsl:call-template name="BuildHRMCManualHeader" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent">

	</xsl:template>
	<!-- ** end of document header ** -->

	<!-- ** Document body ** -->
	<xsl:template name="BuildHRMCArrangementBody">
		<xsl:call-template name="BuildPrevNextNavigation">
			<xsl:with-param name="prevProvisionTitle" select="$PrevProvisionTitle"/>
			<xsl:with-param name="nextProvisionTitle" select="$NextProvisionTitle"/>
		</xsl:call-template>
		<xsl:call-template name="BuildProvisionsTree" />
		<xsl:call-template name="HMRCArrangementSubjects" />
		<xsl:call-template name="HMRCArrangementKeywords" />
	</xsl:template>

	<xsl:template name="BuildHRMCManualBody">
		<xsl:call-template name="BuildPrevNextNavigation">
			<xsl:with-param name="prevProvisionTitle" select="$PrevProvisionTitle"/>
			<xsl:with-param name="nextProvisionTitle" select="$NextProvisionTitle"/>
		</xsl:call-template>
		<xsl:apply-templates select="//n-docbody/document/fulltext" />
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<xsl:choose>
			<xsl:when test="$documentType='&HRMCArrangementType;'" >
				<xsl:call-template name="BuildHRMCArrangementBody" />
			</xsl:when>
			<xsl:when test="$documentType='&HRMCManualType;'" >
				<xsl:call-template name="BuildHRMCManualBody" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="HMRCArrangementSubjects">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="captionOnNewLine" select="true()"/>
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukSubjects;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody//md.keyphrases/md.keyphrase">
					<xsl:sort select="."/>
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="HMRCArrangementKeywords">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="captionOnNewLine" select="true()"/>
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&euKeywords;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody//md.keywords/md.keyword">
					<xsl:sort select="."/>
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='unordered']">
		<div>
			<xsl:element name="ul">
				<xsl:attribute name="class">
					<xsl:text>&docUnorderedList;</xsl:text>
					<xsl:if test="$DeliveryMode">
						<xsl:text> &coAssetList;</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<xsl:call-template name="listItemsDisplay"/>
			</xsl:element>
		</div>
	</xsl:template>

	<xsl:template match="fulltext//url[@href]">
		<xsl:call-template name="LinkOpensInNewTab">
			<xsl:with-param name="href" select="./@href"/>
			<xsl:with-param name="title" select="'&externalLinkHoverOverText;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fulltext//title">
		<h3 class="&headtextClass;">
			<xsl:apply-templates />
		</h3>
	</xsl:template>

	<xsl:template match="para//p">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="sub">
		<sub>
			<xsl:apply-templates />
		</sub>
	</xsl:template>

	<xsl:template match="sup">
		<sup>
			<xsl:apply-templates />
		</sup>
	</xsl:template>
	
	
	<!-- ** end of document body ** -->


	<!-- ** Document metadata ** -->
	<xsl:template name="BuildMetaInfoColumnContent">
		<xsl:choose>
			<xsl:when test="$documentType='&HRMCManualType;'" >
				<xsl:call-template name="HMRCManualMetaPublished" />
				<xsl:call-template name="HMRCManualMetaUpdated" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="HMRCManualMetaPublished">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'" />
			<xsl:with-param name="fieldCaption" select="'&publishedText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:value-of select="DateTimeExtension:Format(substring-after(//document/headers/head-2,': '), 'dd MMMM yyyy','d MMMM yyyy')"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="HMRCManualMetaUpdated">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'" />
			<xsl:with-param name="fieldCaption" select="'&updatedText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:value-of select="DateTimeExtension:Format(substring-after(//document/headers/head-3,': '), 'dd MMMM yyyy','d MMMM yyyy')"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ** end of document metadata ** -->
</xsl:stylesheet>


