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
				<xsl:when test="$statusCode = '1' or $statusCode = '4' or $statusCode = '5'">
					<!--green-->
					<xsl:value-of select="'&positiveStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = '1A' or $statusCode = '4A' or $statusCode = '5A'">
					<!--green/yellow with letter A-->
					<xsl:value-of select="'&positiveAppealStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = '2'">
					<!--yellow-->
					<xsl:value-of select="'&cautionStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = '2A'">
					<!--yellow exclamation mark with letter A-->
					<xsl:value-of select="'&mixedAppealStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = '0A'">
					<!--yellow with letter A-->
					<xsl:value-of select="'&appealStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = '3'">
					<!--red-->
					<xsl:value-of select="'&negativeStatus;'"/>
				</xsl:when>
				<xsl:when test="$statusCode = '3A'">
					<!--red/yellow with letter A-->
					<xsl:value-of select="'&negativeAppealStatus;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&unknownStatus;'"/>
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
		<xsl:call-template name="StatusDisplayText">
			<xsl:with-param name="statusCode" select="$statusCode"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name ="StatusDisplayText">
		<xsl:param name="statusCode" />
		<xsl:choose>
			<xsl:when test="$statusCode = '1'">
				<xsl:value-of select="'&lbl_status_good_law;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '1A'">
				<xsl:value-of select="'&lbl_status_good_law; &withText; &lbl_status_appeal_outstanding;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '2'">
				<xsl:value-of select="'&lbl_status_caution;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '2A'">
				<xsl:value-of select="'&lbl_status_caution; &withText; &lbl_status_appeal_outstanding;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '3'">
				<xsl:value-of select="'&lbl_status_bad_law;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '3A'">
				<xsl:value-of select="'&lbl_status_bad_law; &withText; &lbl_status_appeal_outstanding;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '4'">
				<xsl:value-of select="'&lbl_status_good_law;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '4A'">
				<xsl:value-of select="'&lbl_status_good_law; &withText; &lbl_status_appeal_outstanding;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '5'">
				<xsl:value-of select="'&lbl_status_good_law;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '5A'">
				<xsl:value-of select="'&lbl_status_good_law; &withText; &lbl_status_appeal_outstanding;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '0A'">
				<xsl:value-of select="'&lbl_status_appeal_outstanding;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&lbl_status_unknown;'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="StatusTextColor">
		<xsl:param name="statusCode" />
		<xsl:choose>
			<xsl:when test="$statusCode = '1' or $statusCode = '4' or $statusCode = '5' or $statusCode = '1A' or $statusCode = '4A' or $statusCode = '5A'">
				<xsl:value-of select="'&greenStatusText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '2' or $statusCode = '2A' or $statusCode = '0A'">
				<xsl:value-of select="'&orangeStatusText;'"/>
			</xsl:when>
			<xsl:when test="$statusCode = '3' or $statusCode = '3A'">
				<xsl:value-of select="'&redStatusText;'"/>
			</xsl:when>
		<xsl:when test="$statusCode = '' or $statusCode = '0'">
				<xsl:value-of select="'&greyStatusText;'"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
