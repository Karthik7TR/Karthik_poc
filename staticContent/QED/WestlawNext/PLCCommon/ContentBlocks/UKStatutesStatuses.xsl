<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">


	<!--Status region-->
	<xsl:template name ="StatusIconClass">
		<xsl:param name="statusCode"/>
		<xsl:param name="isSmall" select="false()"/>

		<xsl:variable name="status">
			<xsl:choose>
				<xsl:when test="$statusCode = 'L'">
					<!--green-->
					<xsl:value-of select="'&positiveStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'LA'">
					<!--green/yellow with exclamation mark-->
					<xsl:value-of select="'&positiveCautionStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'P' or $statusCode = 'I'">
					<!--white/green-->
					<xsl:value-of select="'&partiallyInForceStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'PA' or $statusCode = 'IA'">
					<!--white/green with exclamation mark-->
					<xsl:value-of select="'&partiallyInForceCautionStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'A'">
					<!--yellow-->
					<xsl:value-of select="'&cautionStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'H' or $statusCode = 'R' or $statusCode = 'BS'">
					<!--red-->
					<xsl:value-of select="'&negativeStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'F' or $statusCode = 'N'">
					<!--blue-->
					<xsl:value-of select="'&prospectiveStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'FA' or $statusCode = 'NA'">
					<!--blue/yellow-->
					<xsl:value-of select="'&prospectiveCautionStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'BC' or $statusCode = 'SV' or $statusCode = 'BA'">
					<!--purple-->
					<xsl:value-of select="'&billStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = 'V'">
					<xsl:value-of select="'&unknownStatus;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&noneStatus;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="sizePostfix">
			<xsl:choose>
				<xsl:when test="$isSmall=true()">
					<xsl:value-of select="'&statusIconSmallPostfix;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&statusIconLargePostfix;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>&statusIconPrefix;</xsl:text>
		<xsl:value-of select="concat($status,$sizePostfix)"/>
	</xsl:template>

	<xsl:template name="StatusIconHoverOver">

		<xsl:param name="statusCode"/>
		<xsl:choose>
			<xsl:when test="$statusCode = 'H'">
				<xsl:value-of select="'&historicStatusHover;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'V'">
				<xsl:value-of select="'&asOriginallyEnactedStatusHover;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="StatusDisplayText">
					<xsl:with-param name="statusCode" select="$statusCode"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name ="StatusDisplayText">
		<xsl:param name="statusCode" />
		<xsl:choose>
			<xsl:when test="$statusCode = ''">
				<xsl:value-of select="'&ukStatusBillWithAmendmentsPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'A'">
				<xsl:value-of select="'&ukStatusAmendmentPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'BS' or $statusCode = 'H'">
				<xsl:value-of select="'&ukStatusHistoricLaw;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'BA'">
				<xsl:value-of select="'&ukStatusProposedBillInsertion;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'SV'">
				<xsl:value-of select="'&ukStatusBillAmendedText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'BC'">
				<xsl:value-of select="//fulltext_metadata//bill/type"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'F'">
				<xsl:value-of select="'&ukStatusProspectiveLaw;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'FA'">
				<xsl:value-of select="'&ukStatusProspectiveLaw; &withText; &ukStatusAmendmentPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'I'">
				<xsl:value-of select="'&ukStatusPartiallyRepealed;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'IA'">
				<xsl:value-of select="'&ukStatusPartiallyRepealed; &withText; &ukStatusAmendmentPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'L'">
				<xsl:value-of select="'&ukStatusLawInForce;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'LA'">
				<xsl:value-of select="'&ukStatusLawInForce; &withText; &ukStatusAmendmentPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'N'">
				<xsl:value-of select="'&ukStatusNotYetInForce;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'NA'">
				<xsl:value-of select="'&ukStatusNotYetInForce; &withText; &ukStatusAmendmentPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'P'">
				<xsl:value-of select="'&ukStatusPartiallyInForce;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'PA'">
				<xsl:value-of select="'&ukStatusPartiallyInForce; &withText; &ukStatusAmendmentPending;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'R'">
				<xsl:value-of select="'&ukStatusRepealed;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'V'">
				<xsl:value-of select="'&ukStatusAsOriginallyEnacted;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="StatusTextColor">
		<xsl:param name="statusCode" />
		<xsl:choose>
			<xsl:when test="$statusCode = 'I' or $statusCode = 'L' or $statusCode = 'P' or $statusCode = 'LA'">
				<xsl:value-of select="'&greenStatusText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'A' or $statusCode = 'IA' or $statusCode = 'PA'">
				<xsl:value-of select="'&orangeStatusText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'H' or $statusCode = 'R' or $statusCode = 'BS'">
				<xsl:value-of select="'&redStatusText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = 'F' or $statusCode = 'N' or $statusCode = 'FA' or $statusCode = 'NA'">
				<xsl:value-of select="'&blueStatusText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '' or $statusCode = 'V' or $statusCode = 'BA' or $statusCode = 'BC' or $statusCode = 'SV'">
				<xsl:value-of select="'&greyStatusText;'"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
