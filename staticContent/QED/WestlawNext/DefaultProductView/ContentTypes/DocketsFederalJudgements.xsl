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
			<h2 class="&docketsHeading;">
				<xsl:text>&docketsFederalJudgmentIndexRecords;</xsl:text>
			</h2>
			<xsl:apply-templates />
			<xsl:call-template name="ToOrderBottom" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="court.info.block">
		<h2 id="&docketsCourtInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsCourtInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="judgment.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="judgment.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.info.block">
		<h2 id="&docketsCaseInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsCaseInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="debtor.info.block" priority="2">
		<h2 id="&docketsDebtorInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsDebtorInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="debtor.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="debtor.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="creditor.info.block" priority="2">
		<h2 id="&docketsCreditorInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsCreditorInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="creditor.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="creditor.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.descrip.block">
		<h2 id="&docketsJudgmentDescriptionId;" class="&docketsHeading;">
			<xsl:text>&docketsJudgmentDescription;</xsl:text>
		</h2>
		<table class="&docketsJudgmentDescriptionTableClass;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="judgment.document.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:if test="$IsPublicRecords = false() and not($IsIpad) and not($IsIphone)">
					<xsl:apply-templates select="judgment.document/image.block" />
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="award.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:text>$</xsl:text>
				<xsl:apply-templates select="award" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="interest.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="interest" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="court.cost.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="court.cost" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="judgment.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
