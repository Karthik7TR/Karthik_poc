<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Universal.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />
	
	<xsl:template match="/">
		<div id="&documentClass;" class="&documentClass;">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<div class="&ci_BoardOfDirector; &ci_Record;">
				<xsl:apply-templates select="Document/n-docbody/officer.profile" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="officer.profile"> 
		<!-- Positions -->
		<div class="&ci_Layout_CompensationAndOptionsTableContainer;">
			<table class="&ci_PositionTable;">
				<thead>
					<tr>
						<th scope="col">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerPositionTypeKey;', '&officerPositionType;')"/>
						</th>
						<th scope="col">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerTitleKey;', '&officerTitle;')"/>
						</th>
						<th scope="col">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerStartDateKey;', '&officerStartDate;')"/>
						</th>
						<th scope="col">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerDirectorSinceKey;', '&officerDirectorSince;')"/>
						</th>
						<th scope="col">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerOfficerSinceKey;', '&officerOfficerSince;')"/>
						</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<th>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerCurrentPositionKey;', '&officerCurrentPosition;')"/>
						</th>
						<td>
							<xsl:value-of select="current.position/title"/>
						</td>
						<td>
							<xsl:call-template name="parseYearMonthDayDateFormat">
								<xsl:with-param name="date" select="current.position/start.date" />
								<xsl:with-param name="displayDay" select="'true'" />
							</xsl:call-template>
						</td>
						<td>
							<xsl:if test="current.position">
								<xsl:call-template name="parseYearMonthDayDateFormat">
									<xsl:with-param name="date" select="director.start.date" />
									<xsl:with-param name="displayDay" select="'true'" />
								</xsl:call-template>
							</xsl:if>
						</td>
						<td>
							<xsl:if test="current.position">
								<xsl:call-template name="parseYearMonthDayDateFormat">
									<xsl:with-param name="date" select="officer.start.date" />
									<xsl:with-param name="displayDay" select="'true'" />
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
					<xsl:if test="previous.positions">
						<xsl:for-each select="previous.positions/previous.position">
							<xsl:if test="position() &lt; 11">
								<tr>
									<th>
										<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerPreviousPositionKey;', '&officerPreviousPosition;')"/>
									</th>
									<td>								
										<xsl:value-of select="title"/>
									</td>
									<td>
										<xsl:call-template name="parseYearMonthDayDateFormat">
											<xsl:with-param name="date" select="start.date" />
											<xsl:with-param name="displayDay" select="'true'" />
										</xsl:call-template>
									</td>
									<td>
										<xsl:if test="not(current.position)">
											<xsl:call-template name="parseYearMonthDayDateFormat">
												<xsl:with-param name="date" select="director.start.date" />
												<xsl:with-param name="displayDay" select="'true'" />
											</xsl:call-template>
										</xsl:if>
									</td>
									<td>
										<xsl:if test="not(current.position)">
											<xsl:call-template name="parseYearMonthDayDateFormat">
												<xsl:with-param name="date" select="officer.start.date" />
												<xsl:with-param name="displayDay" select="'true'" />
											</xsl:call-template>
										</xsl:if>
									</td>
								</tr>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
				</tbody>
			</table>
		</div>
	
		<!-- Biography, Education, Committee Memberships, Other Corporate Affiliations -->
		<div class="&ci_BoardOfDirectorSplitSection;">
			<!-- CHA: container for quirk mode -->
			<table class="&ci_Pr_DualColumn;">
				<tbody>
					<tr>
						<!-- left pane -->
						<td class="&ci_Pr_LeftSection;">
							<div class="&ci_Pr_Subheader;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerBiographyKey;', '&officerBiography;')"/>
							</div>
							<table class="&ci_Pr_Table;">
								<tr>
									<td>
										<xsl:value-of select="biography"/>
									</td>
								</tr>
							</table>
						</td>

						<!-- right pane -->
						<td class="&ci_Pr_RightSection;">
							<div class="&ci_Pr_Subheader;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerEducationKey;', '&officerEducation;')"/>
							</div>
							<table class="&ci_Pr_Table;">
								<tr>
									<td>
										<xsl:apply-templates select="education" />
									</td>
								</tr>
							</table>

							<div class="&ci_Pr_Subheader;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerCommitteeMembershipsKey;', '&officerCommitteeMemberships;')"/>
							</div>
							<table class="&ci_Pr_Table;">
								<tr>
									<td>
										<xsl:apply-templates select="committee.memberships" />
									</td>
								</tr>
							</table>

							<div class="&ci_Pr_Subheader;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerCurrentAffiliationsKey;', '&officerCurrentAffiliations;')"/>
							</div>
							<table class="&ci_Pr_Table;">
								<tr>
									<td>
										<xsl:apply-templates select="current.corp.affiliations" />
									</td>
								</tr>
							</table>
							<div class="&ci_Pr_Subheader;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerPreviousAffiliationsKey;', '&officerPreviousAffiliations;')"/>
							</div>
							<table class="&ci_Pr_Table;">
								<tr>
									<td>
										<xsl:apply-templates select="previous.corp.affiliations" />
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

			<xsl:if test="salary.information">
		<!-- Compensation & Options Table-->
		<div class="&ci_CompensationAndOptionsTableContainer;">
			<div class="&ci_Pm_FullTextSectionHeader;">
				<h4>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerCompensationSummaryKey;', '&officerCompensationSummary;')"/>
				</h4>
			</div>
				<table class="&ci_CompensationAndOptionsTable; &ci_Pr_Table;">
					<xsl:attribute name="summary">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerCompensationSummaryKey;', '&officerCompensationSummary;')"/>
					</xsl:attribute>
					<colgroup>
						<col />
					</colgroup>
					<thead>
						<tr>
							<th scope="row" class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerPeriodEndDateKey;', '&officerPeriodEndDate;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="periodEndDate"/>
						</tr>
					</thead>
					<tbody>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerSubmissionDateKey;', '&officerSubmissionDate;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="submissionDateParam" />
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerSubmissionTypeKey;', '&officerSubmissionType;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="submissionDate" />
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerCurrencyKey;', '&officerCurrency;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="currency" />
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerSalaryKey;', '&officerSalary;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="salary"/>
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerBonusKey;', '&officerBonus;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="bonus"/>
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerOtherAnnualCompKey;', '&officerOtherAnnualComp;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="otherAnnualComp"/>
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerTotalAnnualCompKey;', '&officerTotalAnnualComp;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="totalAnnualComp"/>
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerRestrictedStockAwardsKey;', '&officerRestrictedStockAwards;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="restrictedStockAwards"/>
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerFiscalYearTotalKey;', '&officerFiscalYearTotal;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="fiscalYearTotal"/>
						</tr>
						<tr>
							<th class="&ci_Layout_MajorRowTitleBox;">								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerOptionsKey;', '&officerOptions;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="options"/>
						</tr>
						<tr>
							<th>								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerExcercisedNumberKey;', '&officerExcercisedNumber;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="excercisedNumber"/>
						</tr>
						<tr>
							<th>								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerExcercisedValueKey;', '&officerExcercisedValue;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="excercisedValue"/>
						</tr>
						<tr>
							<th>								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerExercisableNumberKey;', '&officerExercisableNumber;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="exercisableNumber"/>
						</tr>
						<tr>
							<th>								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerExercisableValueKey;', '&officerExercisableValue;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="exercisableValue"/>
						</tr>
						<tr>
							<th>								
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerUnexercisableNumberKey;', '&officerUnexercisableNumber;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="unexercisableNumber"/>
						</tr>
						<tr>
							<th>
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerUnexercisableValueKey;', '&officerUnexercisableValue;')"/>
							</th>
							<xsl:apply-templates select="salary.information/compensation.period" mode="unexercisableValue"/>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="periodEndDate">
		<xsl:if test="position() &lt; 6">
			<th>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="end.year"/>
			</th>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="submissionDate">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="submission.type"/>
			</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="submissionDateParam">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="date" select="submission.date" />
					<xsl:with-param name="displayDay" select="'true'" />
				</xsl:call-template>
			</td>
		</xsl:if>
	</xsl:template>
	
		<xsl:template match="compensation.period" mode="currency">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:if test="string-length(currency/currency.type) &gt; 0" >
					<xsl:value-of select="currency/currency.type"/>
				</xsl:if>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="salary">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(currency/salary))!='NaN'">
						<xsl:value-of select="format-number(currency/salary, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="bonus">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(currency/bonus))!='NaN'">
						<xsl:value-of select="format-number(currency/bonus, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="otherAnnualComp">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(currency/other.comp))!='NaN'">
						<xsl:value-of select="format-number(currency/other.comp, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="totalAnnualComp">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(total.compensations/total.comp))!='NaN'">
						<xsl:value-of select="format-number(total.compensations/total.comp, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="restrictedStockAwards">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(total.compensations/restricted.stock))!='NaN'">
						<xsl:value-of select="format-number(total.compensations/restricted.stock, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="allOtherComp">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(total.compensations/total.other.comp))!='NaN'">
						<xsl:value-of select="format-number(total.compensations/total.other.comp, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="fiscalYearTotal">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/fiscal.year.total))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/fiscal.year.total, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
		<xsl:template match="compensation.period" mode="options">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:choose>
					<xsl:when test="position() = 1">
						<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">Options</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="excercisedNumber">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/options/exercised.option/option.number))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/options/exercised.option/option.number, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerNAKey;', '&officerNA;')"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="excercisedValue">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/options/exercised.option/option.value))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/options/exercised.option/option.value, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="exercisableNumber">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/options/exercisable.option/option.number))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/options/exercisable.option/option.number, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>n/a</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="compensation.period" mode="exercisableValue">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/options/exercisable.option/option.value))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/options/exercisable.option/option.value, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template match="compensation.period" mode="unexercisableNumber">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/options/unexercisable.option/option.number))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/options/unexercisable.option/option.number, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>n/a</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
		<xsl:template match="compensation.period" mode="unexercisableValue">
		<xsl:if test="position() &lt; 6">
			<td>
				<xsl:if test="position() = 1">
					<xsl:attribute name="class">Layout_MajorColumnTitleBoxDivider</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string(number(fiscal.year.totals/options/unexercisable.option/option.value))!='NaN'">
						<xsl:value-of select="format-number(fiscal.year.totals/options/unexercisable.option/option.value, '#,###.##')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-</xsl:text>
						<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:if>
	</xsl:template>
	
		<!-- Education -->
	<xsl:template match="education">
		<div class="&ci_Layout_MinorSection;">
			<xsl:apply-templates select="degree.info" />
		</div>
	</xsl:template>
	
		<!-- Degree Info -->
	<xsl:template match="degree.info">
		<div>
			<xsl:if test="position() mod 2=1">
				<xsl:attribute name="class">AlternateRow</xsl:attribute>
			</xsl:if>
			<span>
				<xsl:choose>
					<xsl:when test="string-length(college) &gt; 0">
						<xsl:value-of select="college"/>
						<xsl:choose>
							<xsl:when test="string-length(degree) &gt; 0">
								<xsl:text>, </xsl:text>
								<xsl:value-of select="degree"/>
								<xsl:if test="string-length(major) &gt; 0">
									<xsl:text> </xsl:text>
										in
									<xsl:text> </xsl:text>
									<xsl:value-of select="major"/>
								</xsl:if>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="string-length(major) &gt; 0">
									<xsl:text>, </xsl:text>
									<xsl:value-of select="major"/>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="string-length(degree) &gt; 0">
						<xsl:value-of select="degree"/>
						<xsl:if test="string-length(major) &gt; 0">
							<xsl:text> </xsl:text>
								in
							<xsl:text> </xsl:text>
							<xsl:value-of select="major"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="string-length(major) &gt; 0">
						<xsl:value-of select="major"/>
					</xsl:when>
				</xsl:choose>
			</span>
		</div>
	</xsl:template>
	
	<!-- Committee -->
	<xsl:template match="committee.memberships">
		<div class="&ci_Layout_MinorSection;">		
			<xsl:apply-templates select ="committee"/>
		</div>
	</xsl:template>

	<xsl:template match="committee">
		<div>
			<xsl:if test="position() mod 2=1">
				<xsl:attribute name="class">AlternateRow</xsl:attribute>
			</xsl:if>
			<span>
				<xsl:choose>
					<xsl:when test="string-length(committee.name) &gt; 0">
						<xsl:value-of select="committee.name"/>
						<xsl:if test="string-length(committee.title) &gt; 0" >
							<xsl:text>, </xsl:text>
							<xsl:value-of select="committee.title"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="string-length(committee.title) &gt; 0">
						<xsl:value-of select="committee.title"/>
					</xsl:when>
				</xsl:choose>
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:if test="string-length(start.date) &gt; 0">
						<xsl:variable name ="startDate">
							<xsl:value-of select="start.date"/>
						</xsl:variable>
						<xsl:variable name ="yyyy" select ="substring($startDate, 1, 4)"/>
						<xsl:variable name ="mm" select ="substring($startDate, 5, 2)"/>
					  <xsl:variable name ="dd" select ="substring($startDate, 7, 2)"/>
						<xsl:value-of select ="$mm"/>
						<xsl:text>/</xsl:text>
					<xsl:if test="string-length($dd) &gt; 0">
						<xsl:value-of select ="$dd"/>
						<xsl:text>/</xsl:text>
					</xsl:if>
						<xsl:value-of select ="$yyyy"/>
				</xsl:if>
			</span>
		</div>
	</xsl:template>
	
		<!-- Current Corporate Affiliations -->
	<xsl:template match="current.corp.affiliations">
		<div class="&ci_Layout_MinorSection;">
			<xsl:apply-templates select ="corp.affiliation" mode="currentCorpAffiliations" />
		</div>
	</xsl:template>
	
		<xsl:template match="corp.affiliation" mode="currentCorpAffiliations">
		<div>
			<xsl:if test="position() mod 2=1">
				<xsl:attribute name="class">AlternateRow</xsl:attribute>
			</xsl:if>
			<span>
				<xsl:if test="string-length(company.name) &gt; 0">
					<xsl:choose>
						<xsl:when test="company.name/cite.query/@w-ref-type='CO' or company.name/cite.query/@w-ref-type='WC'">
							<a>
								<xsl:attribute name="href">
									<xsl:call-template name="GetCISearchResultUrl">
										<xsl:with-param name="query" select="concat('advanced:WCAID(',company.name/cite.query/@w-normalized-cite,')')" />
										<xsl:with-param name="contentType">
											<xsl:text>BUSINESS-INVESTIGATOR</xsl:text>
										</xsl:with-param>
										<xsl:with-param name="categoryPageUrl">
											<xsl:text>Home/CompanyInvestigator</xsl:text>
										</xsl:with-param>
										<xsl:with-param name="simpleSearch" select="'true'" />
									</xsl:call-template>
								</xsl:attribute>
								<xsl:value-of select="company.name"/>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="company.name"/>
						</xsl:otherwise>		
					</xsl:choose>
				</xsl:if>
				<xsl:if test="string-length(title) &gt; 0">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="title"/>
				</xsl:if>
			</span>
		</div>
	</xsl:template>
	
		<!-- Previous Corporate Affiliations -->
	<xsl:template match="previous.corp.affiliations">
		<div class="&ci_Layout_MinorSection;">
			<xsl:apply-templates select ="corp.affiliation" mode="previousCorpAffiliations" />
		</div>
	</xsl:template>

	<xsl:template match="corp.affiliation" mode="previousCorpAffiliations">
		<div>
			<xsl:if test="position() mod 2=1">
				<xsl:attribute name="class">AlternateRow</xsl:attribute>
			</xsl:if>
			<span>
				<xsl:if test="string-length(company.name) &gt; 0">
					<xsl:choose>
						<xsl:when test="company.name/cite.query/@w-ref-type='CO' or company.name/cite.query/@w-ref-type='WC'">
							<a>
								<xsl:attribute name="href">
									<xsl:call-template name="GetCISearchResultUrl">
										<xsl:with-param name="query" select="concat('advanced:WCAID(',company.name/cite.query/@w-normalized-cite,')')" />
										<xsl:with-param name="contentType">
											<xsl:text>BUSINESS-INVESTIGATOR</xsl:text>
										</xsl:with-param>
										<xsl:with-param name="categoryPageUrl">
											<xsl:text>Home/CompanyInvestigator</xsl:text>
										</xsl:with-param>
										<xsl:with-param name="simpleSearch" select="'true'" />
									</xsl:call-template>
								</xsl:attribute>
								<xsl:value-of select="company.name"/>
							</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="company.name"/>
							</xsl:otherwise>	
						</xsl:choose>
					<xsl:if test="company.name/@active=0">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerInactiveKey;', '&officerInactive;')"/>
					</xsl:if>
				</xsl:if>
				<xsl:if test="string-length(title) &gt; 0">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="title"/>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&officerInactiveKey;', '&officerInactive;')"/>
				</xsl:if>
			</span>
		</div>
	</xsl:template>
	<xsl:template name="GetCISearchResultUrl">
		<xsl:param name ="query" />
		<xsl:param name ="contentType" />
		<xsl:param name ="categoryPageUrl" />
		<xsl:param name ="jurisdiction" />
		<xsl:param name ="simpleSearch" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.SearchResults', concat('categoryPageUrl=',$categoryPageUrl), concat('contentType=',$contentType), concat('query=',$query), concat('jurisdiction=',$jurisdiction), concat('simpleSearch=',$simpleSearch), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>	
</xsl:stylesheet>
