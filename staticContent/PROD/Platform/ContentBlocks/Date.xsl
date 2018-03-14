<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Date.Block -->
	<xsl:template name="dateBlock" match="date.block | date | prop.block/content.metadata.block/cmd.dates | pub.date[not(parent::date.block or parent::date or parent::prop.block/content.metadata.block/cmd.dates)]">
		<xsl:param name="extraClasses" />

		<xsl:if test=".//text()">
			<xsl:variable name="classes">
				<xsl:text>&dateClass;</xsl:text>
				<xsl:if test="$extraClasses">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="$extraClasses"/>
				</xsl:if>
			</xsl:variable>

			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="$classes" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Date.Line -->
	<xsl:template match="date.line | pub.date">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div>
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dateLine" match="date.line[date][not(ancestor::date.block)]">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&dateClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match ="date.line/date">
		<xsl:choose>
			<xsl:when test="string-length(.) = 8 and not(number(.) = NaN)">
				<xsl:call-template name="parseYearMonthDayDateFormat"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="md.endeffective | md.starteffective">
		<xsl:choose>
			<xsl:when test="string-length(.) &gt; 13 and number(.) != 'NaN'">
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="displayDay" select="'true'" />
					<xsl:with-param name="displayTime" select="'true'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="string-length(.) &gt; 7 and number(.) != 'NaN'">
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="displayDay" select="'true'" />
					<xsl:with-param name="displayTime" select="'false'" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="parseYearMonthDayDateFormat">
		<xsl:param name="date" select="."/>
		<xsl:param name="displayDay" />
		<xsl:param name="displayDayFormat"/>
		<xsl:param name="displayTime" />
		<xsl:if test="string-length($date) &gt; 7 and number($date) != 'NaN'">
			<xsl:variable name ="year" select ="substring($date,1,4)"/>
			<xsl:variable name ="month" select ="substring($date,5,2)"/>
			<xsl:variable name ="day" select ="substring($date,7,2)"/>
			<xsl:choose>
				<xsl:when test ="$month = 01">January</xsl:when>
				<xsl:when test ="$month = 02">February</xsl:when>
				<xsl:when test ="$month = 03">March</xsl:when>
				<xsl:when test ="$month = 04">April</xsl:when>
				<xsl:when test ="$month = 05">May</xsl:when>
				<xsl:when test ="$month = 06">June</xsl:when>
				<xsl:when test ="$month = 07">July</xsl:when>
				<xsl:when test ="$month = 08">August</xsl:when>
				<xsl:when test ="$month = 09">September</xsl:when>
				<xsl:when test ="$month = 10">October</xsl:when>
				<xsl:when test ="$month = 11">November</xsl:when>
				<xsl:when test ="$month = 12">December</xsl:when>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:if test="$displayDay">
				<xsl:choose>
					<xsl:when test="$displayDayFormat">
						<xsl:value-of select="format-number($day, $displayDayFormat)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select ="$day"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>

			<xsl:value-of select ="$year"/>

			<xsl:if test="$displayTime">
				<xsl:if test="string-length($date) &gt; 13">
					<xsl:variable name ="hour" select ="substring($date,9,2)"/>
					<xsl:variable name ="minute" select ="substring($date,11,2)"/>
					<xsl:variable name ="second" select ="substring($date,13,2)"/>

					<xsl:text><![CDATA[ ]]></xsl:text>


					<xsl:choose>
						<xsl:when test="number($hour) = 00">12</xsl:when>
						<xsl:when test="number($hour) &gt; 12">
							<xsl:value-of select="number($hour) - 12"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number($hour)"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select ="concat(':', $minute, ':', $second)"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:choose>
						<xsl:when test="$hour &gt; 11">PM</xsl:when>
						<xsl:otherwise>AM</xsl:otherwise>
					</xsl:choose>
				</xsl:if>

			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="parseMonthDayYearDateFormat">
		<xsl:param name="date" select="."/>
		<xsl:if test="string-length($date) &gt; 7 and number($date) != 'NaN'">
			<xsl:variable name ="month" select ="substring($date,1,2)"/>
			<xsl:variable name ="day" select ="substring($date,3,2)"/>
			<xsl:variable name ="year" select ="substring($date,5,4)"/>
			<xsl:choose>
				<xsl:when test ="$month = 01">January</xsl:when>
				<xsl:when test ="$month = 02">February</xsl:when>
				<xsl:when test ="$month = 03">March</xsl:when>
				<xsl:when test ="$month = 04">April</xsl:when>
				<xsl:when test ="$month = 05">May</xsl:when>
				<xsl:when test ="$month = 06">June</xsl:when>
				<xsl:when test ="$month = 07">July</xsl:when>
				<xsl:when test ="$month = 08">August</xsl:when>
				<xsl:when test ="$month = 09">September</xsl:when>
				<xsl:when test ="$month = 10">October</xsl:when>
				<xsl:when test ="$month = 11">November</xsl:when>
				<xsl:when test ="$month = 12">December</xsl:when>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:value-of select ="$day"/>
			<xsl:text>,<![CDATA[ ]]></xsl:text>
			<xsl:value-of select ="$year"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="added.date.block | changed.date.block | deactivation.date.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="added.date.block/label | changed.date.block/label | deactivation.date.block/label">
		<xsl:call-template name="wrapWithSpan" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	<xsl:template match="added.date.block/added.date | changed.date.block/changed.date | deactivation.date.block/deactivation.date">
		<xsl:call-template name="parseMonthDayYearDateFormat"/>
	</xsl:template>

</xsl:stylesheet>
