<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:include href="PublicRecordsMaskedData.xsl"/>

	<!-- ASSUMPTION #1: Formats always have at least the year.
	     ASSUMPTION #2: Formats that are 6 characters are of the format yyyyMM.
			 ASSUMPTION #3: Formats that are 8 charachters are of the format yyyyMMdd.
			ASSUMPTION #4: We will never get a date where the day is valued, but the month is not such as 20010011 -->
	<xsl:template name="parseYearMonthDayDateFormat">
		<xsl:param name="date" select="."/>

		<xsl:choose>
			<!-- Check if it is already a formatted date. -->
			<xsl:when test="(substring($date, 3, 1)='/') or (substring($date, 6, 1)='/')">
				<xsl:value-of select="$date"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- Format as MM/dd/yyyy. -->
				<!-- Month -->
				<xsl:if test="string-length($date) &gt; 5">
					<xsl:if test="not(substring($date, 5, 2)='00')">
						<xsl:value-of select="substring($date, 5, 2)"/>	
					</xsl:if>
				</xsl:if>

				<!-- Day -->
				<xsl:if test="string-length($date) &gt; 7">
					<xsl:if test="not(substring($date, 7, 2)='00')">
						<xsl:text>/</xsl:text>
						<xsl:value-of select="substring($date, 7, 2)"/>
					</xsl:if>
				</xsl:if>

				<!-- Year -->
				<xsl:if test="not(substring($date, 1, 4)='0000')">
					<xsl:if test="string-length($date) &gt; 5">
						<xsl:if test="not(substring($date, 5, 2)='00')">
							<xsl:text>/</xsl:text>	
						</xsl:if>
					</xsl:if>
					<xsl:value-of select="substring($date, 1, 4)"/>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ASSUMPTION #1: Formats always have at least the year.
	     ASSUMPTION #2: Formats that are 6 characters are of the format MMyyyy.
			 ASSUMPTION #3: Formats that are 8 charachters are of the format MMddyyyy.
			 In cases where there is no month of day, this method inserts 00 (e.g. 01/00/2013 if day is not specified). -->
	<xsl:template name="parseMonthDayYearDateFormat">
		<xsl:param name="date" select="."/>

		<xsl:choose>
			<!-- Check if it is already a formatted date. -->
			<xsl:when test="(substring($date, 3, 1)='/') or (substring($date, 6, 1)='/')">
				<xsl:value-of select="$date"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- Format as MM/dd/yyyy. -->
				<!-- Month -->
				<xsl:if test="string-length($date) &gt; 1 and not(substring($date, 1, 2)='00')">
					<xsl:value-of select="substring($date, 1, 2)"/>
					<xsl:text>/</xsl:text>
				</xsl:if>

				<!-- Day -->
				<xsl:if test="string-length($date) &gt; 3 and not(substring($date, 1, 2)='00' or substring($date, 3, 2)='00')">
					<xsl:value-of select="substring($date, 3, 2)"/>
					<xsl:text>/</xsl:text>
				</xsl:if>

				<!-- Year -->
				<xsl:value-of select="substring($date, 5, 4)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatNonSensitiveDate">
		<xsl:param name="dateNode" select="."/>
		<xsl:param name="isoFormatted" select="true()"/>
		<xsl:call-template name="FormatDate">
			<xsl:with-param name="displaySensitive" select="true()"/>
			<xsl:with-param name="dateNode" select="$dateNode"/>
			<xsl:with-param name="isoFormatted" select="$isoFormatted"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="FormatDate">
		<xsl:param name="displaySensitive" select="false()"/>
		<xsl:param name="dateNode" select="."/>
		<xsl:param name="isoFormatted" select="true()"/>
		<xsl:variable name="formattedDate">
			<xsl:choose>
				<xsl:when test="$isoFormatted">
					<xsl:call-template name="parseYearMonthDayDateFormat">
						<xsl:with-param name="date" select="$dateNode"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="parseMonthDayYearDateFormat">
						<xsl:with-param name="date" select="$dateNode"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- We don't have a full date, so simply spit it out. -->
		<xsl:value-of select="$formattedDate"/>
	</xsl:template>
</xsl:stylesheet>
