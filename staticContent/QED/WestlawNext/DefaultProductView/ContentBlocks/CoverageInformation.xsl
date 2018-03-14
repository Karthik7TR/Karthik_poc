<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<!-- Fullpath to Coverage information -->
	<xsl:variable name="coverage-block" select="/Document/n-docbody/CoverageData" />

	<!-- ********************************************************************** 
	*************************  (A)"Coverage" section  *************************
	************************************************************************-->
	<xsl:template match="CoverageData">
		<xsl:param name="displayCoverageState" select="false()"/>
		<xsl:param name="coverageStateValue" select="/Document/n-docbody/CoverageData/MetaData/State"/>
		<xsl:param name="contentTypeValue" select="/Document/n-docbody/r/p"/>
		<xsl:param name="displayUpdateDate" select="true()"/>
		<xsl:param name="updateDateLabel"/>
		<xsl:param name="displayCoverageBeginDate" select="true()"/>
		<xsl:param name="coverageBeginDateLabel"/>
		<xsl:param name="displayCurrentThroughDate" select="true()"/>
		<xsl:param name="currentThroughDateLabel"/>
		<xsl:param name="displayDatabaseLastUpdated" select="true()"/>
		<xsl:param name="databaseLastUpdatedLabel"/>
		<xsl:param name="displayUpdateFrequency" select="true()"/>
		<xsl:param name="updateFrequencyLabel"/>
		<xsl:param name="updateFrequencyValue"/>
		<xsl:param name="displayCurrentDate" select="true()"/>
		<xsl:param name="currentDateLabel"/>
		<xsl:param name="displayDateAcquired" select="true()"/>
		<xsl:param name="dateAcquiredLabel"/>
		<xsl:param name="displaySource" select="true()"/>
		<xsl:param name="sourceLabel"/>
		<xsl:param name="sourceNodes" select="/.."/>
		<xsl:param name="displayReportAuthority" select="true()"/>
		<xsl:param name="reportAuthorityLabel"/>
		<xsl:param name="reportAuthorityNodes" select="/.."/>

		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_sourceInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:if test="$displayCoverageState = true()">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel"  select="'&pr_state;'"/>
					<xsl:with-param name="selectNodes" select="$coverageStateValue"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$displayUpdateDate = true()">
				<xsl:apply-templates select="UpdateDate">
					<xsl:with-param name="Label" select="$updateDateLabel"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayDateAcquired = true()">
				<xsl:apply-templates select="DateAcquired">
					<xsl:with-param name="Label" select="$dateAcquiredLabel"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayCoverageBeginDate = true()">
				<xsl:apply-templates select="CoverageBeginDate">
					<xsl:with-param name="Label" select="$coverageBeginDateLabel"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayCurrentThroughDate = true()">
				<xsl:apply-templates select="CurrentThroughDate">
					<xsl:with-param name="Label" select="$currentThroughDateLabel"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayDatabaseLastUpdated = true()">
				<xsl:apply-templates select="DatabaseLastUpdated">
					<xsl:with-param name="Label" select="$databaseLastUpdatedLabel"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayUpdateFrequency = true()">
				<xsl:apply-templates select="UpdateFrequency">
					<xsl:with-param name="Label" select="$updateFrequencyLabel"/>
					<xsl:with-param name="Value" select="$updateFrequencyValue"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayCurrentDate = true()">
				<xsl:apply-templates select="CurrentDate">
					<xsl:with-param name="Label" select="$currentDateLabel"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displaySource = true()">
				<xsl:apply-templates select="Source">
					<xsl:with-param name="Label" select="$sourceLabel"/>
					<xsl:with-param name="sourceNodes" select="$sourceNodes"/>
					<xsl:with-param name="contentTypeValue" select="$contentTypeValue"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$displayReportAuthority = true()">
				<xsl:apply-templates select="ReportAuthority">
					<xsl:with-param name="Label" select="$reportAuthorityLabel"/>
					<xsl:with-param name="sourceNodes" select="$reportAuthorityNodes"/>
				</xsl:apply-templates>
			</xsl:if>

		</table>
	</xsl:template>

	<!--Update Date -->
	<xsl:template match ="UpdateDate">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel"  select="'&pr_dAndBCompletedAnalysis;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Acquired-->
	<xsl:template match ="DateAcquired">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel"  select="'&pr_dateAcquired;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Coverage Begin Date -->
	<xsl:template match="CoverageBeginDate">
		<xsl:param name="Label"/>

		<xsl:call-template name="CoverageDate">
			<xsl:with-param name="labelToUse" select="$Label"/>
			<xsl:with-param name="defaultLabel" select="'&pr_coverageBeginDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Information Current Through-->
	<xsl:template match="CurrentThroughDate">
		<xsl:param name="Label"/>
		<xsl:call-template name="CoverageDate">
			<xsl:with-param name="labelToUse" select="$Label"/>
			<xsl:with-param name="defaultLabel" select="'&pr_informationCurrentThrough;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Database Updated-->
	<xsl:template match="DatabaseLastUpdated">
		<xsl:param name="Label"/>

		<xsl:call-template name="CoverageDate">
			<xsl:with-param name="labelToUse" select="$Label"/>
			<xsl:with-param name="defaultLabel" select="'&pr_databaseLastUpdated;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Updated Frequency-->
	<xsl:template match="UpdateFrequency">
		<xsl:param name="Label"/>
		<xsl:param name="Value"/>

		<xsl:if test="normalize-space(.) != ''">
			<tr>
				<!--Label-->
				<th>
					<xsl:choose>
						<xsl:when test="$Label">
							<xsl:value-of select="$Label"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_updateFrequency;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</th>
				<!--Data-->
				<td>
					<xsl:choose>
						<xsl:when test="$Value">
							<xsl:value-of select="$Value"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--Current Date-->
	<xsl:template match="CurrentDate">
		<xsl:param name="Label"/>
		<xsl:call-template name="CoverageDate">
			<xsl:with-param name="labelToUse" select="$Label"/>
			<xsl:with-param name="defaultLabel" select="'&pr_currentDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Source-->
	<xsl:template match="Source | ReportAuthority">
		<xsl:param name="Label"/>
		<xsl:param name="sourceNodes" select="/.."/>
		<xsl:param name="contentTypeValue" select="/.."/>

		<xsl:if test="normalize-space(.)">
			<tr>
				<!--Label-->
				<th>
					<xsl:choose>
						<xsl:when test="$Label">
							<xsl:value-of select="$Label"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_source;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</th>
				<!--Data-->
				<td>
					<xsl:variable name="src">
						<xsl:choose>
							<xsl:when test="$sourceNodes">
								<xsl:value-of select="$sourceNodes"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="."/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="contains($src,'&amp;#169;')">
							<xsl:value-of select="substring-before($src, '&amp;#169;')" />
							<xsl:text>&#169;</xsl:text>
							<xsl:value-of select="substring-after($src, '&amp;#169;')" />
						</xsl:when>
						<xsl:when test="contains($src,'??')">
							<xsl:value-of select="substring-before($src, '??')" />
							<xsl:text>&#169;</xsl:text>
							<xsl:value-of select="substring-after($src, '??')" />
						</xsl:when>
						<xsl:when test="contains($src,'INFO GROUP') or contains($src,'INFOGROUP') ">
							<xsl:text>Data by Infogroup, Copyright © </xsl:text>
							<xsl:value-of select="$PRYear"/>
							<xsl:text>, All Rights Reserved.</xsl:text>
						</xsl:when>
						<xsl:when test="contains($src,'DUN &amp; BRADSTREET') or contains($src,'Dun &amp; Bradstreet') or contains($src,'DUN&amp;BRADSTREET') or contains($src,'Dun&amp;Bradstreet') or contains($src,'D&amp;B') or contains($src,'D &amp; B')">
							<xsl:choose>
								<xsl:when test="contains($contentTypeValue,'&pr_corporateRecordsAndBusinessRegistrationsPxmlElement;')">
									<xsl:text>&pr_corporateRecordsAndBusinessRegistrationsSourceText;</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>Copyright © </xsl:text>
									<xsl:value-of select="$PRYear"/>
									<xsl:text> by Dun &amp; Bradstreet, Inc.</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$src" />
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CoverageDate">
		<xsl:param name="labelToUse"/>
		<xsl:param name="defaultLabel"/>

		<xsl:if test="./text() != '00-00-0000' ">
			<xsl:if test="normalize-space(.)">

				<tr>
					<!--Label-->
					<th>
						<xsl:choose>
							<xsl:when test="$labelToUse">
								<xsl:value-of select="$labelToUse"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$defaultLabel"/>
							</xsl:otherwise>
						</xsl:choose>
					</th>
					<!--Data-->
					<td>
						<xsl:call-template name="ConvertMMDDYYYY">
							<xsl:with-param name="tempDate" select="."/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--****************************Date Formatter**************************-->
	<!-- Convert MM-DD-YYYY format to MM/DD/YYYY format.
	     ASSUMPTION: YYYY will always be present. -->
	<xsl:template name="ConvertMMDDYYYY">
		<xsl:param name="tempDate"/>
		<xsl:if test ="$tempDate">
			<xsl:choose>
				<xsl:when test="string-length($tempDate)!=10">
					<xsl:value-of select="$tempDate"/>
				</xsl:when>
				<!-- Next when is for no month. -->
				<xsl:when test="substring($tempDate, 1, 2)='00'">
					<xsl:value-of select="substring($tempDate, 7, 4)"/>
				</xsl:when>
				<!-- Next when is for no day. -->
				<xsl:when test="substring($tempDate, 4, 2)='00'">
					<xsl:choose>
						<xsl:when test="substring($tempDate, 1, 2)='00'">
							<xsl:value-of select="substring($tempDate, 7, 4)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="substring($tempDate, 1, 2)"/>
							<xsl:text>/</xsl:text>
							<xsl:value-of select="substring($tempDate, 7, 4)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<!-- Will only fall to the following otherwise if both month and year are not '00'.
				     Since we are assuming there will always be a year, just output the date. -->
				<xsl:otherwise>
					<xsl:value-of select="translate($tempDate,'-','/')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
