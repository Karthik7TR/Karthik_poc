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
			<xsl:call-template name="DisplayDocketSummariesHeader"/>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="DisplayDocketSummariesHeader">
		<h2 id="&docketsIPDocketSummariesId;" class="&docketsHeading;">
			<xsl:text>&docketsIPDocketSummaries;</xsl:text>
		</h2>
	</xsl:template>

	<xsl:template match="r">

		<table>
			<xsl:call-template name="IpDocketSummariesSection" />
		</table>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>

		<xsl:if test="party.block">
			<h2 id="&docketsParticipantInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsParticipantInformation;</xsl:text>
			</h2>
			<table>
				<xsl:apply-templates select="party.block"/>
			</table>
		</xsl:if>
		<xsl:apply-templates select="*[not(self::court.block or self::title.block or self::docket.block or self::filing.date.block or self::cause.block or self::other.dockets.block or self::currency.date.block or self::party.block)]" />

	</xsl:template>

	<xsl:template name="IpDocketSummariesSection">
		<xsl:apply-templates select="title.block"/>
		<xsl:apply-templates select="docket.block"/>
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="filing.date.block"/>
		<xsl:apply-templates select="cause.block" />
		<xsl:apply-templates select="other.dockets.block"/>
		<xsl:apply-templates select="currency.date.block"/>
	</xsl:template>

	<xsl:template match="party.block" name="partyBlock">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="party.type" />
				<xsl:text>:</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.names.block/party.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.block/party.type" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="currency.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="currency.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.information.block">
		<div class="&docketsSection;">
			<h2 id="&docketsPatentInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsPatentInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="patent.block">
		<div class="&docketsSubSection;">
			<xsl:call-template name="patentInformationRowHeader" />
			<table>
				<xsl:apply-templates />
			</table>
		</div>
	</xsl:template>

	<xsl:template name="patentInformationRowHeader">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="text">
				<xsl:apply-templates select="patent.number.block/patent.number" />
				<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
				<xsl:apply-templates select="patent.title.block/patent.title" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="patent.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.app.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="patent.app.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.title.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:text>Title</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="patent.title" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="abstract">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:text>Abstract</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="national.class.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:for-each select="national.class.line">
					<xsl:apply-templates select="national.class" />
					<xsl:if test="national.subclass">
						<xsl:text>/</xsl:text>
						<xsl:apply-templates select="national.subclass" />
					</xsl:if>
					<xsl:if test="position() != last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="international.class.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="international.class" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inventors.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:for-each select="inventor">
					<div>
						<xsl:apply-templates select="name" />
						<xsl:text> (</xsl:text>
						<xsl:apply-templates select="city" />
						<xsl:text>, </xsl:text>
						<xsl:apply-templates select="state" />
						<xsl:text>)</xsl:text>
					</div>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assignees.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:for-each select="assignee">
					<div>
						<xsl:apply-templates select="name" />
						<xsl:text> (</xsl:text>
						<xsl:apply-templates select="city" />
						<xsl:text>, </xsl:text>
						<xsl:apply-templates select="state" />
						<xsl:text>)</xsl:text>
					</div>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="patent.block/currency.date.block | trademark.block/currency.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="currency.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trademark.information.block">
		<div class="&docketsSection;">
			<h2 id="&docketsTrademarkInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsTrademarkInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="trademark.block">
		<div class="&docketsSubSection;">
			<xsl:call-template name="trademarkInformationRowHeader" />
			<table>
				<xsl:apply-templates />
			</table>
		</div>
	</xsl:template>
	
	<xsl:template name="trademarkInformationRowHeader">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="text">
				<xsl:apply-templates select="mark.block/mark" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mark.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="mark" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trademark.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="trademark.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="drawing.code.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="drawing.code" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trademark.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="trademark.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="serial.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="serial.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="registration.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="registration.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="registration.number">
		<xsl:variable name="databaseIdentifier">
			<xsl:text>cb_w_3rd_alltm</xsl:text>
		</xsl:variable>
		<xsl:variable name="documentName">
			<xsl:value-of select="concat('FEDTM ', .)" />
		</xsl:variable>
		<xsl:call-template name="createCiteQueryLinkByParameters">
			<xsl:with-param name="linkContents">
				<xsl:apply-templates />
			</xsl:with-param>
			<xsl:with-param name="findType" select="'Y'" />
			<xsl:with-param name="pubNum" select="$databaseIdentifier" />
			<xsl:with-param name="cite" select="$documentName" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="goods.services.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="goods.services.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="owner.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<div>
					<xsl:apply-templates select="name" />
					<xsl:text> (</xsl:text>
					<xsl:apply-templates select="city" />
					<xsl:text>, </xsl:text>
					<xsl:apply-templates select="state" />
					<xsl:text>)</xsl:text>
					<xsl:if test="owner.type">
						<xsl:text> - </xsl:text>
						<xsl:apply-templates select="owner.type"/>
					</xsl:if>
				</div>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.ip.information.block">
		<h2 id="&docketsOtherPropertiesId;" class="&docketsHeading;">
			<xsl:text>&docketsOtherProperties;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="ip.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="ip.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ip.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="ip.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ip.registration.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="ip.registration.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.number">
		<xsl:variable name="dataSourceType" select="/Document/n-docbody/r/data.source.type" />
		<xsl:variable name="documentName">
			<xsl:value-of select="concat('CASE-NUMBER' , '(', ., ')' , ' &amp;', ' CRTN', '(', $dataSourceType , ')')"/>
		</xsl:variable>
		<xsl:call-template name="createCiteQueryLinkByParameters">
			<xsl:with-param name="linkContents">
				<xsl:apply-templates />
			</xsl:with-param>
			<xsl:with-param name="findType" select="'l'" />
			<!-- Lowercase 'L'-->
			<xsl:with-param name="pubNum" select="'DOCK-IP-F'" />
			<xsl:with-param name="cite" select="$documentName" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>