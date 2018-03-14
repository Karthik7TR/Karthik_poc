<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<!-- includes -->
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl" />
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Title.xsl"/>

	<xsl:variable name="doNotDisplayValue" select="'~DoNotDisplay~'" />
	<xsl:variable name ="na-answer" select="'N/A'" />
	<xsl:variable name ="yes-answer" select="'Yes'" />
	<xsl:variable name ="no-answer" select="'No'" />
	<xsl:variable name="extraThreshold">5</xsl:variable>
	<xsl:variable name="extraItem">
		&morelessItemStyle; &hideStateClass;
	</xsl:variable>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<!--
	**************************************************************************************
	*		Re-usable Routines                                                              *
	**************************************************************************************
	-->

	<xsl:template name="MoreLink">
		<xsl:param name="count"/>
		<xsl:param name="threshold"/>

		<xsl:if test="$count > $threshold">
			<div>
				<a href="#" class="&coFloatRight; &coMoreLessLink;">
					<div class="&coFloatLeft; &morelessStyle;">More</div>
					<span class="&coDropdownArrowCollapsed;">&nbsp;</span>
				</a>
				<div class="&clear;"></div>
			</div>
		</xsl:if>
	</xsl:template>

	<!--DisplayStringValuesInOneColumn-->
	<xsl:template name="DisplayStringValuesInOneColumn">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="truncate"/>

		<div>
			<xsl:if test="$truncate">
				<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$param1" />
			</xsl:call-template>
			<xsl:call-template name="VerifyStringValue">
				<xsl:with-param name="ifValidString" select="$param2" />
				<xsl:with-param name="stingValue" select="$param3" />
			</xsl:call-template>
		</div>

		<xsl:if test="$truncate">
			<xsl:call-template name="MoreLink">
				<xsl:with-param name="count" select="string-length($param3)" />
				<xsl:with-param name="threshold" select="1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--DisplayNumberValuesInOneColumn-->
	<xsl:template name="DisplayNumberValuesInOneColumn">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="param4"/>
		<xsl:param name="param5"/>

		<div>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$param1" />
			</xsl:call-template>
			<xsl:call-template name="VerifyNumberValue">
				<xsl:with-param name="isBlank" select="$param2" />
				<xsl:with-param name="doesNotExist" select="$param3" />
				<xsl:with-param name="isNumber" select="$param4" />
				<xsl:with-param name="value" select="$param5" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--DisplayDateValuesInOneColumn-->
	<xsl:template name="DisplayDateValuesInOneColumn">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="yearFirst"/>

		<div>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$param1" />
			</xsl:call-template>
			<xsl:call-template name="FormatDateValue">
				<xsl:with-param name="ifValidDate" select="$param2"/>
				<xsl:with-param name="dateValue" select="$param3"/>
				<xsl:with-param name="yearFirst" select="$yearFirst"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--DisplayBooleanValuesInOneColumn-->
	<xsl:template name="DisplayBooleanValuesInOneColumn">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="param4"/>

		<div>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$param1" />
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="$param2">
					<xsl:choose>
						<xsl:when test="$param3">
							<xsl:value-of select="$yes-answer"/>
						</xsl:when>
						<xsl:when test="$param4">
							<xsl:value-of select="$no-answer"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$na-answer"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$na-answer"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!--DisplayMultipleSingleElementsInOneColumn-->
	<xsl:template name="DisplayMultipleSingleElementsInOneColumn">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>

		<table class="&blcNestedTable;">
			<tr>
				<td>
					<xsl:call-template name="DisplayLabel">
						<xsl:with-param name="text" select="$param1" />
					</xsl:call-template>
				</td>
				<td class="&blcNestedTableCell;">
					<xsl:choose>
						<xsl:when test="$param2">
							<xsl:for-each select="$param3">
								<xsl:choose>
									<xsl:when test="string-length(text()) &gt; 0">
										<div>
											<xsl:value-of select="text()"/>
										</div>
									</xsl:when>
									<xsl:otherwise>
										<div>
											<xsl:value-of select="$na-answer"/>
										</div>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<div>
								<xsl:value-of select="$na-answer"/>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</table>
	</xsl:template>

	<!--VerifyString-->
	<xsl:template name="VerifyString">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="truncate"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$param1"/>
					</h4>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:if test="$truncate">
						<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
					</xsl:if>

					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="$param2" />
						<xsl:with-param name="stingValue" select="$param3" />
					</xsl:call-template>
				</div>

				<xsl:if test="$truncate">
					<xsl:call-template name="MoreLink">
						<xsl:with-param name="count" select="string-length($param3)" />
						<xsl:with-param name="threshold" select="1" />
					</xsl:call-template>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!--VerifyStringValue-->
	<xsl:template name="VerifyStringValue">
		<xsl:param name="ifValidString"/>
		<xsl:param name="stingValue"/>

		<xsl:choose>
			<xsl:when test="contains($stingValue, $doNotDisplayValue)">
				<xsl:value-of select="''" />
			</xsl:when>
			<xsl:when test="$ifValidString and not($stingValue='NA')">
				<xsl:value-of select="$stingValue" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Display Label-->
	<xsl:template name="DisplayLabel">
		<xsl:param name="text"/>
		<strong>
			<xsl:value-of select="$text"/>
			<xsl:text>&nbsp;&nbsp;</xsl:text>
		</strong>
	</xsl:template>

	<!--DisplayTwoString-->
	<xsl:template name="DisplayTwoString">
		<xsl:param name="label"/>
		<xsl:param name="value1Xpath"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$label"/>
					</h4>
				</div>
			</td>
			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value1Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value1Xpath" />
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!--DisplayThreeString-->
	<xsl:template name="DisplayThreeString">
		<xsl:param name="label"/>
		<xsl:param name="value1Xpath"/>
		<xsl:param name="value2Xpath"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$label"/>
					</h4>
				</div>
			</td>
			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value1Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value1Xpath" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value2Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value2Xpath" />
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!--DisplayFourString-->
	<xsl:template name="DisplayFourString">
		<xsl:param name="label"/>
		<xsl:param name="value1Xpath"/>
		<xsl:param name="value2Xpath"/>
		<xsl:param name="value3Xpath"/>
		<xsl:param name="truncate" />
		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$label"/>
					</h4>
				</div>
			</td>
			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value1Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value1Xpath" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value2Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value2Xpath" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col4;">
				<div>
					<xsl:if test="$truncate">
						<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
					</xsl:if>

					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value3Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value3Xpath" />
					</xsl:call-template>
				</div>

				<xsl:if test="$truncate">
					<xsl:call-template name="MoreLink">
						<xsl:with-param name="count" select="1" />
						<xsl:with-param name="threshold" select="0" />
					</xsl:call-template>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!--DisplayValuesIn3Cols-->
	<xsl:template name="DisplayValuesIn3Cols">
		<xsl:param name="label"/>
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$label"/>
					</h4>
				</div>
			</td>
			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value1) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value1" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value2) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value2" />
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!--VerifyBoolean-->
	<xsl:template name="VerifyBoolean">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="param4"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$param1"/>
					</h4>
				</div>
			</td>

			<td class="&layout_col2;">

				<xsl:choose>
					<xsl:when test="$param2">
						<xsl:choose>
							<xsl:when test="$param3">
								<div>
									<xsl:value-of select="$yes-answer"/>
								</div>
							</xsl:when>
							<xsl:when test="$param4">
								<div>
									<xsl:value-of select="$no-answer"/>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<div>
									<xsl:value-of select="$na-answer"/>
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:value-of select="$na-answer"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>

			</td>
		</tr>
	</xsl:template>

	<!--VerifyNumber-->
	<xsl:template name="VerifyNumber">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="param4"/>
		<xsl:param name="param5"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$param1"/>
					</h4>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="VerifyNumberValue">
						<xsl:with-param name="isBlank" select="$param2" />
						<xsl:with-param name="doesNotExist" select="$param3" />
						<xsl:with-param name="isNumber" select="$param4" />
						<xsl:with-param name="value" select="$param5" />
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!--VerifyNumberValue-->
	<xsl:template name="VerifyNumberValue">
		<xsl:param name="isBlank"/>
		<xsl:param name="doesNotExist"/>
		<xsl:param name="isNumber"/>
		<xsl:param name="value"/>

		<xsl:choose>
			<xsl:when test="$isBlank">
				<xsl:value-of select="$na-answer"/>
			</xsl:when>
			<xsl:when test="$doesNotExist">
				<xsl:value-of select="$na-answer"/>
			</xsl:when>
			<xsl:when test="$isNumber">
				<xsl:value-of select="$value" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- VerifyDate -->
	<xsl:template name="VerifyDate">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="yearFirst"/>
		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$param1"/>
					</h4>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="FormatDateValue">
						<xsl:with-param name="ifValidDate" select="$param2"/>
						<xsl:with-param name="dateValue" select="$param3"/>
						<xsl:with-param name="yearFirst" select="$yearFirst"/>
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!-- FormatDateToYearFirst-->
	<xsl:template name="FormatDateToYearFirst">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$param1"/>
					</h4>
				</div>

			</td>
			<td class="&layout_col2;">
				<div>

					<xsl:variable name="newDate">
						<!--Year-->
						<xsl:text>20</xsl:text>
						<xsl:value-of select="substring($param3, 7, 2)"/>
						<!--Month-->
						<xsl:value-of select="substring($param3, 1, 2)"/>
						<!--Day-->
						<xsl:value-of select="substring($param3, 4, 2)"/>
					</xsl:variable>

					<xsl:call-template name="FormatDateValue">
						<xsl:with-param name="ifValidDate" select="$param2"/>
						<xsl:with-param name="dateValue" select="$newDate"/>
						<xsl:with-param name="yearFirst" select="1"/>
					</xsl:call-template>
				</div>
			</td>
		</tr>


	</xsl:template>


	<!--FormateDateValue-->
	<xsl:template name="FormatDateValue">
		<xsl:param name="ifValidDate"/>
		<xsl:param name="dateValue"/>
		<xsl:param name="yearFirst"/>

		<xsl:choose>
			<xsl:when test="$ifValidDate">
				<xsl:choose>
					<xsl:when test="$yearFirst">
						<xsl:call-template name="parseYearMonthDayDateFormat">
							<xsl:with-param name="date"	select="$dateValue"/>
							<xsl:with-param name="displayDay" select="1"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="parseMonthDayYearDateFormat">
							<xsl:with-param name="date"	select="$dateValue"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- VerifyMultipleSingleElements -->
	<xsl:template name="VerifyMultipleSingleElements">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$param1"/>
					</h4>
				</div>
			</td>

			<td class="&layout_col2;">
				<xsl:choose>
					<xsl:when test="$param2">
						<xsl:for-each select="$param3">
							<xsl:choose>
								<xsl:when test="string-length(text()) &gt; 0">
									<div>
										<xsl:value-of select="text()"/>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<div>
										<xsl:value-of select="$na-answer"/>
									</div>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:value-of select="$na-answer"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- TwoDelimiterSeperatedStringValues -->
	<xsl:template name="TwoDelimiterSeperatedStringValues">
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>
		<xsl:param name="delimiter"/>

		<xsl:choose>
			<xsl:when test="(string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) != 0 and $value2 != $na-answer)">
				<xsl:value-of select="$value2"/>
			</xsl:when>
			<xsl:when test="(string-length($value2) = 0 or $value2 = $na-answer) and (string-length($value1) != 0 and $value1 != $na-answer)">
				<xsl:value-of select="$value1"/>
			</xsl:when>
			<xsl:when test="(string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) = 0 or $value2 = $na-answer)">
				<xsl:value-of select="$na-answer"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($value1, $delimiter, $value2)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- DisplayOneValueOrTheOther -->
	<xsl:template name="DisplayOneValueOrTheOther">
		<xsl:param name="test"/>
		<xsl:param name="label"/>
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>

		<xsl:choose>
			<xsl:when test="$test" >
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="param1" select="$label"/>
					<xsl:with-param name="param2" select="string-length($value1) &gt; 0"/>
					<xsl:with-param name="param3" select="$value1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="param1" select="$label"/>
					<xsl:with-param name="param2" select="string-length($value2) &gt; 0"/>
					<xsl:with-param name="param3" select="$value2"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
