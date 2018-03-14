<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<!-- includes -->
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="Date.xsl"/>

	<!-- variables -->
	<xsl:variable name="doNotDisplayValue" select="'~DoNotDisplay~'" />
	<xsl:variable name="na-answer" select="'Not Available'" />
	<xsl:variable name="nd-answer" select="'ND'" />
	<xsl:variable name="xpath-root" select="//n-docbody/PrivateEquityFundsAndFirms" />
	<xsl:variable name="pe-ff-summary" select="$xpath-root/PEFFsummary"/>
	<xsl:variable name="pe-funds" select="$xpath-root/PEfunds"/>
	<xsl:variable name="pe-firms" select="$xpath-root/PEfirms"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name ="contentType" select="'&contentTypePrivateEquityFunds;'"/>
			</xsl:call-template>
			<xsl:call-template name="Content"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="Content">
		<div class="&peFundsParentClass;">

			<!--Summary-->
			<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='TRS']">
				<xsl:call-template name="TOC">
					<xsl:with-param name="cd" select="'TRS'"/>
				</xsl:call-template>
				<xsl:call-template name="Summary" />
			</xsl:if>

			<!--Fund Information-->
			<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='FND']">
				<xsl:call-template name="TOC">
					<xsl:with-param name="cd" select="'FND'"/>
				</xsl:call-template>
				<xsl:call-template name="Fund" />
				<xsl:call-template name="Fundraising" />
				<xsl:call-template name="LimitedPartnershipInvestors" />
				<xsl:call-template name="LimitedPartnershipPools" />
				<xsl:call-template name="FundInvestmentStrategy" />
				<xsl:call-template name="RealEstateFund" />
			</xsl:if>

			<!--Investment-->
			<xsl:if test="$xpath-root/PEfunds/*[starts-with(name(), 'section.')][@cd='INV']">
				<xsl:call-template name="TOC">
					<xsl:with-param name="cd" select="'INV'"/>
				</xsl:call-template>
				<xsl:call-template name="Investment" />
			</xsl:if>

			<!--Firm Information-->
			<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='FRM']">
				<xsl:call-template name="TOC">
					<xsl:with-param name="cd" select="'FRM'"/>
				</xsl:call-template>
				<xsl:call-template name="Firm" />
				<xsl:call-template name="FirmInvestmentStrategy" />
				<xsl:call-template name="RealEstateSection" />
				<xsl:call-template name="Executives" />
				<xsl:call-template name="BranchOffices" />
			</xsl:if>

		</div>
	</xsl:template>

	<!--Display Label-->
	<xsl:template name="DisplayLabel">
		<xsl:param name="text"/>
		<strong>
			<xsl:value-of select="$text"/>
			<xsl:text>&nbsp;&nbsp;</xsl:text>
		</strong>
	</xsl:template>

	<!--************** START: TOC *****************-->

	<xsl:template name="TOC">
		<xsl:param name="cd" />
		<xsl:element name="span">
			<xsl:attribute name="id">
				<xsl:text>co_internalToc_</xsl:text>
				<xsl:copy-of select="$cd"/>
			</xsl:attribute>
			<xsl:attribute name="class">co_internalTocMarker</xsl:attribute>
			<xsl:text>&nbsp;</xsl:text>
		</xsl:element>
	</xsl:template>

	<!--************** END: TOC *****************-->

	<xsl:template name="DisplayStringValuesInOneColumn">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="truncate" />
		<xsl:param name="indentLeft" />
		<xsl:param name="displayZero" select="false()" />
		<xsl:param name="NAtoZero" select="false()"/>

		<div>
			<xsl:if test="$truncate=string(true())">
				<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
			</xsl:if>
			<xsl:if test="$indentLeft">
				<xsl:attribute name="class">&indentLeft2Class;</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$stringValue1" />
			</xsl:call-template>
			<xsl:call-template name="VerifyStringValue">
				<xsl:with-param name="stringValue" select="$stringValue2" />
				<xsl:with-param name="displayZero" select="$displayZero" />
				<xsl:with-param name="NAtoZero" select="$NAtoZero" />
			</xsl:call-template>
		</div>

		<xsl:if test="$truncate=string(true())">
			<xsl:call-template name="MoreLink">
				<xsl:with-param name="count" select="string-length($stringValue2)" />
				<xsl:with-param name="threshold" select="1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--************** START: Summary *****************-->

	<xsl:template name="Summary">

		<div id="co_pefunds_summary">
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Summary</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Fund Year -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Fund Vintage:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/fundVintageYear" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Fund Location -->
							<xsl:variable name="summaryFundLocationText">
								<xsl:call-template name="TwoDelimiterSeperatedStringValues">
									<xsl:with-param name="value1">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1">
												<xsl:apply-templates select="$pe-ff-summary/fundLocBlock/fundCity" />
											</xsl:with-param>
											<xsl:with-param name="value2">
												<xsl:variable name="fundTRCSState" select="$pe-ff-summary/fundLocBlock/fundTRCSstateBlock/fundTRCSstate" />
												<xsl:choose>
													<xsl:when test="string-length($fundTRCSState) &gt; 0">
														<xsl:apply-templates select="$fundTRCSState" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:apply-templates select="$pe-ff-summary/fundLocBlock/fundLocalStateBlock/fundLocalState" />
													</xsl:otherwise>
												</xsl:choose>
											</xsl:with-param>
											<xsl:with-param name="delimiter" select="', '"/>
										</xsl:call-template>
									</xsl:with-param>
									<xsl:with-param name="value2">
										<xsl:apply-templates select="$pe-ff-summary/fundLocBlock/fundCountryBlock/fundCountry" />
									</xsl:with-param>
									<xsl:with-param name="delimiter" select="', '"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Fund Location:'" />
								<xsl:with-param name="stringValue2" select="$summaryFundLocationText" />
							</xsl:call-template>

							<!-- Fund Type -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Fund Type:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/fundTypeBlock/fundType" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Fundraising Status -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Fundraising Status:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/fundraisingStatusBlock/fundraisingStatus" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Fund Size (USD) -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Fund Size (USD):'" />
								<xsl:with-param name="stringValue2">
									<xsl:choose>
										<xsl:when test="$pe-ff-summary/fundSizeBlock/fundSizeConfidential = 'confidential' or $pe-ff-summary/fundSizeBlock/fundSizeConfidential = 'Confidential'">
											<xsl:value-of select="'Confidential'" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="FormatCurrancy">
												<xsl:with-param name="value" select="$pe-ff-summary/fundSizeBlock/fundSize"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
							</xsl:call-template>

							<!-- Investment Status -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Investment Status:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/fundISinvestStatusBlock/fundISinvestStatus" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Stage Focus -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Stage Focus:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/fundISstageFocusBlock/fundISstageFocus" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Number of Investments -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Number of Investments:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/investTotal" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Number of Real Estate Investments -->
							<xsl:variable name="summaryNumberOfRealEstateInvestmentsText" select="$pe-ff-summary/realEstateInvestTotal" />
							<xsl:if test="(string-length($summaryNumberOfRealEstateInvestmentsText) &gt; 0) and ($summaryNumberOfRealEstateInvestmentsText &gt; 0)">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Number of Real Estate Investments:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$summaryNumberOfRealEstateInvestmentsText" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Type of Investments -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Type of Investments:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$pe-ff-summary/fundISinvestTypeBlock/fundISinvestType" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Tot. Non-Confid. Equity Invested (USD) -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Tot. Non-Confid. Equity Invested (USD):'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="FormatCurrancy">
										<xsl:with-param name="value" select="$pe-ff-summary/fundInvestEquity"/>
										<xsl:with-param name="displayZero" select="true()"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="displayZero" select="true()"/>
							</xsl:call-template>

							<!-- Tot. Disclos. Debt Invested (USD) -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Tot. Disclos. Debt Invested (USD):'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="FormatCurrancy">
										<xsl:with-param name="value" select="$pe-ff-summary/fundInvestDebt"/>
										<xsl:with-param name="displayZero" select="true()"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="displayZero" select="true()"/>
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>

		</div>

	</xsl:template>

	<!--************** END: Summary *******************-->



	<!--************** START: Fund *****************-->

	<xsl:template name="Fund">

		<div id="co_pefunds_fund">
			<table class="&layout_table; &blcPortfolioTable;">
				<tr class="&blcBorderTop;">
					<td class="&blcWidth25; &blcSectionHeading;">
						<h3>Fund Information</h3>
					</td>
					<td class="&blcWidth75;">

						<xsl:variable name="fundraisingBlock" select="$pe-funds/fundraisingBlock" />

						<!-- Name -->
						<xsl:variable name="fundUnspecified" select="$pe-funds/fundNameBlock/fundUnspecified" />
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Name:'" />
							<xsl:with-param name="stringValue2">
								<xsl:choose>
									<xsl:when test="$fundUnspecified = 'Y'">
										<xsl:value-of select="'Unspecified'" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:apply-templates select="$pe-funds/fundNameBlock/fundName" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>

						<!-- Alias -->
						<xsl:variable name="fundAliasName" select="$pe-funds/fundAliasBlock/fundAliasName" />
						<xsl:if test="string-length($fundAliasName) &gt; 0">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Alias:'" />
								<xsl:with-param name="stringValue2">
									<xsl:for-each select="$pe-funds/fundAliasBlock">
										<xsl:apply-templates select="./fundAliasName" /> (<xsl:apply-templates select="./fundAliasType" />)<br />
									</xsl:for-each>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>

						<!-- Formerly Known As -->
						<xsl:variable name="fundAFKname" select="$pe-funds/fundAFKblock/fundAFKname" />
						<xsl:if test="string-length($fundAFKname) &gt; 0">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Formerly Known As:'" />
								<xsl:with-param name="stringValue2">
									<xsl:for-each select="$pe-funds/fundAFKblock">
										<xsl:apply-templates select="./fundAFKname" /> (<xsl:apply-templates select="./fundAFKtype" />)<br />
									</xsl:for-each>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>

						<!-- Vintage -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Vintage:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$pe-funds/fundVintageYear" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Location -->
						<xsl:variable name="fundLocationText">
							<xsl:call-template name="TwoDelimiterSeperatedStringValues">
								<xsl:with-param name="value1">
									<xsl:call-template name="TwoDelimiterSeperatedStringValues">
										<xsl:with-param name="value1">
											<xsl:apply-templates select="$pe-funds/fundLocBlock/fundCity" />
										</xsl:with-param>
										<xsl:with-param name="value2">
											<xsl:variable name="fundTRCSState" select="$pe-funds/fundLocBlock/fundTRCSstateBlock/fundTRCSstate" />
											<xsl:choose>
												<xsl:when test="string-length($fundTRCSState) &gt; 0">
													<xsl:apply-templates select="$fundTRCSState" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:apply-templates select="$pe-funds/fundLocBlock/fundLocalStateBlock/fundLocalState" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:with-param>
										<xsl:with-param name="delimiter" select="', '"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="value2">
									<xsl:apply-templates select="$pe-funds/fundLocBlock/fundCountryBlock/fundCountry" />
								</xsl:with-param>
								<xsl:with-param name="delimiter" select="', '"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Fund Location:'" />
							<xsl:with-param name="stringValue2" select="$fundLocationText" />
						</xsl:call-template>

						<!-- Size (USD) -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Size (USD):'" />
							<xsl:with-param name="stringValue2">
								<xsl:choose>
									<xsl:when test="$pe-funds/fundSizeBlock/fundraisingSizeConfidential = 'confidential' or $pe-funds/fundSizeBlock/fundraisingSizeConfidential = 'Confidential'">
										<xsl:value-of select="'Confidential'" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="Format">
											<xsl:with-param name="value" select="$pe-funds/fundSizeBlock/fundSize"/>
											<xsl:with-param name="format" select="'#,###'"/>
											<xsl:with-param name="currancy" select="' (USD)'"/>

											<xsl:with-param name ="postfix">
												<xsl:if test="$fundraisingBlock/fundraisingInfoBlock/fundraisingCloseDate">
													<xsl:text> as of </xsl:text>
													<xsl:call-template name="parseYearMonthDayDateFormat">
														<xsl:with-param name="displayDay" select="1" />
														<xsl:with-param name="date">
															<xsl:apply-templates select="$fundraisingBlock/fundraisingInfoBlock/fundraisingCloseDate" />
														</xsl:with-param>
													</xsl:call-template>
												</xsl:if>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>

						<!-- Target Size (USD) -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Target Size (USD):'" />
							<xsl:with-param name="stringValue2">
								<xsl:call-template name="FormatCurrancy">
									<xsl:with-param name="value" select="$pe-funds/fundTargetSize"/>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>

						<!-- Group Type -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Group Type:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$pe-funds/fundGroupTypeBlock/fundGroupType" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Type -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Type:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$pe-funds/fundTypeBlock/fundType" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Main or Side Fund -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Main or Side Fund:'" />
							<xsl:with-param name="stringValue2">
								<xsl:choose>
									<xsl:when test="$pe-funds/fundMainOrSide = 'No'">
										<xsl:text>Main Fund</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>Side Fund</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>

					</td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!--************** END: Fund *******************-->

	<!--************** START: Fundraising *******************-->

	<xsl:template name="Fundraising">

		<div id="co_pefunds_fundraising">
			<table class="&layout_table; &blcPortfolioTable;">
				<tr>
					<td class="&blcWidth25;"></td>
					<td class="&blcWidth75;">

						<h4 class="&blcSectionSubheading;">Fundraising</h4>

						<xsl:variable name="fundraisingBlock" select="$pe-funds/fundraisingBlock" />
						<xsl:variable name="fundraisingInfoConfidentialText" select="$fundraisingBlock/fundraisingInfoBlock/fundraisingInfoConfidential" />

						<!-- Fundraising Status -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Fundraising Status:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$fundraisingBlock/fundraisingStatusBlock/fundraisingStatus" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Fundraising Close Date -->
						<xsl:if test="string-length($fundraisingBlock/fundraisingInfoBlock/fundraisingCloseDate) &gt; 0">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Fundraising Close Date:'" />
								<xsl:with-param name="stringValue2">
									<xsl:choose>
										<xsl:when test="$fundraisingInfoConfidentialText = 'confidential' or $fundraisingInfoConfidentialText = 'Confidential'">
											<xsl:value-of select="'Confidential'" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="parseYearMonthDayDateFormat">
												<xsl:with-param name="displayDay" select="1" />
												<xsl:with-param name="date">
													<xsl:apply-templates select="$fundraisingBlock/fundraisingInfoBlock/fundraisingCloseDate" />
												</xsl:with-param>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>

						<!-- Total LP Commitments (USD) to date -->
						<xsl:if test="string-length($fundraisingBlock/fundraisingInfoBlock/fundraisingTotalLPcommitments) &gt; 0">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Total LP Commitments (USD) to Date:'" />
								<xsl:with-param name="stringValue2">
									<xsl:choose>
										<xsl:when test="$fundraisingInfoConfidentialText = 'confidential' or $fundraisingInfoConfidentialText = 'Confidential'">
											<xsl:value-of select="'Confidential'" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="FormatCurrancy">
												<xsl:with-param name="value" select="$fundraisingBlock/fundraisingInfoBlock/fundraisingTotalLPcommitments"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>

						<!-- Amounts Raised (USD) by Quarter -->
						<xsl:choose>
							<xsl:when test="$fundraisingInfoConfidentialText = 'confidential' or $fundraisingInfoConfidentialText = 'Confidential'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Amounts Raised (USD) by Quarter:'" />
									<xsl:with-param name="stringValue2" select="'Confidential'" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="$fundraisingBlock/fundraisingInfoBlock[string-length(fundraisingAmtRaisedLastQtr) > 0 and number(fundraisingAmtRaisedLastQtr) != 'NaN' and fundraisingAmtRaisedLastQtr != '0']">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Amounts Raised (USD) by Quarter:'" />
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="Format">
												<xsl:with-param name="value" select="$fundraisingBlock/fundraisingInfoBlock/fundraisingAmtRaisedLastQtr"/>
												<xsl:with-param name="format" select="'#,###'"/>
												<xsl:with-param name="currancy" select="' (USD)'"/>
												<xsl:with-param name="postfix">
													<xsl:if test="$fundraisingBlock/fundraisingInfoBlock/fundraisingAmtRaisedLastQtrDate">
														<xsl:text> qtr ending </xsl:text>
														<xsl:call-template name="parseYearMonthDayDateFormat">
															<xsl:with-param name="displayDay" select="1" />
															<xsl:with-param name="date">
																<xsl:apply-templates select="$fundraisingBlock/fundraisingInfoBlock/fundraisingAmtRaisedLastQtrDate" />
															</xsl:with-param>
														</xsl:call-template>
													</xsl:if>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>
								<xsl:for-each select="$fundraisingBlock/fundraisingInfoBlock/fundraisingPriorQtrBlock/fundraisingPriorQtr[string-length(fundraisingAmtRaised) > 0 and number(fundraisingAmtRaised) != 'NaN' and fundraisingAmtRaised != '0']">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1">
											<xsl:choose>
												<xsl:when test="($fundraisingBlock/fundraisingInfoBlock[string-length(fundraisingAmtRaisedLastQtr) > 0 and number(fundraisingAmtRaisedLastQtr) != 'NaN' and fundraisingAmtRaisedLastQtr != '0'])
												  or position() != 1">
													<xsl:value-of select="''"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'Amounts Raised (USD) by Quarter:'"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:with-param>
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="Format">
												<xsl:with-param name="value" select="./fundraisingAmtRaised"/>
												<xsl:with-param name="format" select="'#,###'"/>
												<xsl:with-param name="currancy" select="' (USD)'"/>
												<xsl:with-param name="postfix">
													<xsl:if test="./fundraisingEndDate">
														<xsl:text> qtr ending </xsl:text>
														<xsl:call-template name="parseYearMonthDayDateFormat">
															<xsl:with-param name="displayDay" select="1" />
															<xsl:with-param name="date">
																<xsl:apply-templates select="./fundraisingEndDate" />
															</xsl:with-param>
														</xsl:call-template>
													</xsl:if>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>

					</td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!--************** END: Fundraising *********************-->



	<!--************** START: Limited Partnership Investors *****************-->

	<xsl:template name="LimitedPartnershipInvestors">
		<xsl:if test="$pe-funds/fundLPItypeBlock/fundLPItype">
			<div id="co_pefunds_limitedPartnershipInvestors" class="&indentLeft8Class;">
				<div class="&layoutHeaderRow; &headText;">
					<i>
						<xsl:text>Limited Partnership Investors</xsl:text>
					</i>
				</div>

				<table class="&layout_table;">

					<!-- Limited Partnership Investors table headers -->
					<xsl:call-template name="DisplayValuesInTableCols">
						<xsl:with-param name="headers" select="1" />
						<xsl:with-param name="value1" select="'Type'" />
						<xsl:with-param name="value2" select="'Commitment (USD)'" />
					</xsl:call-template>

					<!-- Limited Partnership Investors table data -->
					<xsl:for-each select="$pe-funds/fundLPItypeBlock/fundLPItype">
						<xsl:call-template name="DisplayValuesInTableCols">
							<xsl:with-param name="headers" select="0" />
							<xsl:with-param name="value1" select="./fundLPItypeName" />
							<xsl:with-param name="value2">
								<xsl:call-template name="FormatCurrancy">
									<xsl:with-param name="value" select="./fundLPIcommitment"/>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>

				</table>
			</div>
		</xsl:if>
	</xsl:template>


	<!--************** END: Limited Partnership Investors *******************-->



	<!--************** START: Limited Partnership Pools *****************-->

	<xsl:template name="LimitedPartnershipPools">
		<xsl:if test="$pe-funds/fundLimitedPartnershipPoolBlock/*">
			<div id="co_pefunds_limitedPartnershipPools">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25;">
							</td>
							<td class="&blcWidth75;">
								<h4 class="&blcSectionSubheading;">Limited Partnership Pools</h4>


								<xsl:variable name="empty-row">
									<xsl:text>&nbsp;</xsl:text>
								</xsl:variable>

								<xsl:for-each select="$pe-funds/fundLimitedPartnershipPoolBlock/fundLimitedPartnershipPool">
									<xsl:if test="position() != 1">
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="stringValue1" select="$empty-row" />
											<xsl:with-param name="stringValue2" select="$empty-row" />
										</xsl:call-template>
									</xsl:if>

									<!-- Name -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Name:'" />
										<xsl:with-param name="stringValue2">
											<xsl:apply-templates select="./fundLPPnameBlock/fundLPPname" />
										</xsl:with-param>
									</xsl:call-template>

									<!-- Location -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Location:'" />
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="TwoDelimiterSeperatedStringValues">
												<xsl:with-param name="value1">
													<xsl:call-template name="TwoDelimiterSeperatedStringValues">
														<xsl:with-param name="value1">
															<xsl:apply-templates select="./fundLPPlocBlock/fundLPPcity" />
														</xsl:with-param>
														<xsl:with-param name="value2">
															<xsl:variable name="fundLPPTRCSstate" select="./fundLPPlocBlock/fundLPPTRCSstateBlock/fundLPPTRCSstate" />
															<xsl:choose>
																<xsl:when test="string-length($fundLPPTRCSstate) &gt; 0">
																	<xsl:apply-templates select="$fundLPPTRCSstate" />
																</xsl:when>
																<xsl:otherwise>
																	<xsl:apply-templates select="./fundLPPlocBlock/fundLPPlocalStateBlock/fundLPPlocalState" />
																</xsl:otherwise>
															</xsl:choose>
														</xsl:with-param>
														<xsl:with-param name="delimiter" select="', '"/>
													</xsl:call-template>
												</xsl:with-param>
												<xsl:with-param name="value2">
													<xsl:apply-templates select="./fundLPPlocBlock/fundLPPcountryBlock/fundLPPcountry" />
												</xsl:with-param>
												<xsl:with-param name="delimiter" select="', '"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>

									<!-- Investor Type -->
									<xsl:if test="string-length(./fundLPPinvestorTypeBlock/fundLPPinvestorType) > 0 and not(./fundLPPinvestorTypeBlock/fundLPPinvestorType) = '0'">
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="stringValue1" select="'Investor Type:'" />
											<xsl:with-param name="stringValue2">
												<xsl:apply-templates select="./fundLPPinvestorTypeBlock/fundLPPinvestorType" />
											</xsl:with-param>
										</xsl:call-template>
									</xsl:if>

									<!-- Organization Type -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Organization Type:'" />
										<xsl:with-param name="stringValue2">
											<xsl:apply-templates select="./fundLPPorgTypeBlock/fundLPPorgType" />
										</xsl:with-param>
									</xsl:call-template>

									<!-- Total Assets Under Management (USD) -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Total Assets Under Management (USD):'" />
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="FormatCurrancy">
												<xsl:with-param name="value" select="./fundLPPtotalAssetsUnderMgmt"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>

									<!-- Investment (as % of LP Commitment) -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Investment (as % of LP Commitment):'" />
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="Format">
												<xsl:with-param name="value" select="./fundLPPpct"/>
												<xsl:with-param name ="format" select="'#,##0.0'"/>
												<xsl:with-param name ="postfix" select="'%'"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>

									<div class="&indentLeft2Class;">
										<!-- Managing Limited Partner Investor -->
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="stringValue1" select="'Managing Limited Partner Investor:'" />
											<xsl:with-param name="stringValue2">
												<xsl:apply-templates select="./fundLPPmanagingLPinvestorBlock/fundLPPmanagingLPinvestor" />
											</xsl:with-param>
										</xsl:call-template>

										<!-- LP Investor Type -->
										<xsl:if test="string-length(./fundLPPmanagingLPinvestorBlock/fundLPPmanagingLPinvestorTypeBlock/fundLPPmanagingLPinvestorType) > 0">
											<xsl:call-template name="DisplayStringValuesInOneColumn">
												<xsl:with-param name="stringValue1" select="'LP Investor Type:'" />
												<xsl:with-param name="stringValue2">
													<xsl:apply-templates select="./fundLPPmanagingLPinvestorBlock/fundLPPmanagingLPinvestorTypeBlock/fundLPPmanagingLPinvestorType" />
												</xsl:with-param>
											</xsl:call-template>
										</xsl:if>

										<!-- Total Assets Under Management (USD) -->
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="stringValue1" select="'Total Assets Under Management (USD):'" />
											<xsl:with-param name="stringValue2">
												<xsl:call-template name="FormatCurrancy">
													<xsl:with-param name="value" select="./fundLPPmanagingLPinvestorBlock/fundLPPmanagingLPtotalAssetsUnderMgmt"/>
												</xsl:call-template>
											</xsl:with-param>
										</xsl:call-template>
									</div>
								</xsl:for-each>
							</td>
						</tr>
					</tbody>
				</table>

			</div>
		</xsl:if>
	</xsl:template>

	<!--************** END: Limited Partnership Pools *******************-->



	<!--************** START: Fund Investment Strategy *****************-->

	<xsl:template name="FundInvestmentStrategy">
		<div id="co_pefunds_fundInvestmentStrategy">
			<table class="&layout_table; &blcPortfolioTable;">
				<tr>
					<td class="&blcWidth25;"></td>
					<td class="&blcWidth75;">
						<h4 class="&blcSectionSubheading;">Investment Strategy</h4>
						<xsl:variable name="fundInvestStrategyBlock" select="$pe-funds/fundInvestStrategyBlock" />

						<!-- Investment Status -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Investment Status:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$fundInvestStrategyBlock/fundISinvestStatusBlock/fundISinvestStatus" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Type of Investments -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Type of Investments:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$fundInvestStrategyBlock/fundISinvestTypeBlock/fundISinvestType" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Stage Focus -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Stage Focus:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$fundInvestStrategyBlock/fundISstageFocusBlock/fundISstageFocus" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Stage Preference -->
						<xsl:call-template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
							<xsl:with-param name="name" select="'Stage Preference:'" />
							<xsl:with-param name="nodesset" select="$fundInvestStrategyBlock/fundISprefStageBlock/fundISprefStage" />
							<xsl:with-param name="truncate" select="not($DeliveryMode)" />
						</xsl:call-template>

						<!-- Sequence -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Sequence:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$fundInvestStrategyBlock/fundISsequenceTypeBlock/fundISsequenceType" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Industry Focus (>60% investments) -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Industry Focus (>60% investments):'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$fundInvestStrategyBlock/fundISindFocusBlock/fundISindFocus" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Industry Preference -->
						<xsl:call-template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
							<xsl:with-param name="name" select="'Industry Preference:'" />
							<xsl:with-param name="nodesset" select="$fundInvestStrategyBlock/fundISprefIndBlock/fundISprefInd" />
							<xsl:with-param name="truncate" select="not($DeliveryMode)" />
						</xsl:call-template>


						<!-- Geography Preference -->
						<xsl:call-template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
							<xsl:with-param name="name" select="'Geography Preference:'" />
							<xsl:with-param name="nodesset" select="$fundInvestStrategyBlock/fundISprefGeoBlock/fundISprefGeo" />
							<xsl:with-param name="truncate" select="not($DeliveryMode)" />
						</xsl:call-template>
					</td>
				</tr>
			</table>
		</div>

	</xsl:template>

	<!--************** END: Fund Investment Strategy *******************-->



	<!--************** START: Real Estate Investments *****************-->

	<xsl:template name="RealEstateFund">
		<xsl:variable name="fundRealEstateFundBlock" select="$pe-funds/fundRealEstateFundBlock" />
		<xsl:if test="$fundRealEstateFundBlock/*">
			<div id="co_pefunds_realEstateFund">
				<div class="&layoutHeaderRow; &headText;">
					<i>
						<xsl:text>Real Estate Investments</xsl:text>
					</i>
				</div>

				<table class="&layout_table; &layout_2Columns;">

					<!-- Loan to Value (%) -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Loan to Value (%):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$fundRealEstateFundBlock/fundREFloanToValuePct"/>
								<xsl:with-param name="format" select="'#,##0.0'"/>
								<xsl:with-param name="postfix" select="'%'"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>

					<!-- Target Net IRR -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Target Net IRR (%):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$fundRealEstateFundBlock/fundREFtargetNetIRR"/>
								<xsl:with-param name="format" select="'#,##0.0'"/>
								<xsl:with-param name="postfix" select="'%'"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>

					<!-- Target Exposure to Land (%) -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Target Exposure to Land (%):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$fundRealEstateFundBlock/fundREFtargetExposureToLandPct"/>
								<xsl:with-param name="format" select="'#,##0.0'"/>
								<xsl:with-param name="postfix" select="'%'"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>

					<!-- Catch Up (%) -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Catch Up (%):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$fundRealEstateFundBlock/fundREFcatchUpPct"/>
								<xsl:with-param name="format" select="'#,##0.0'"/>
								<xsl:with-param name="postfix" select="'%'"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>

					<!-- Incentive Fee (%) -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Incentive Fee (%):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$fundRealEstateFundBlock/fundREFincentiveFeePct"/>
								<xsl:with-param name="format" select="'#,##0.0'"/>
								<xsl:with-param name="postfix" select="'%'"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>

				</table>

			</div>

		</xsl:if>

	</xsl:template>

	<!--************** END: Real Estate Investments *******************-->



	<!--************** START: Investment *****************-->

	<xsl:template name="Investment">
		<div id="co_pefunds_investment">
			<table class="&layout_table; &blcPortfolioTable;">
				<tr class="&blcBorderTop;">
					<td class="&blcSectionHeading;">
						<h3>Investments</h3>
					</td>
				</tr>
				<tr>
					<td colspan="2">

						<table class="&layout_table; &layout_8Columns; &extraPaddingClass;">

							<!-- Investment table headers -->
							<xsl:call-template name="DisplayValuesInTableCols">
								<xsl:with-param name="headers" select="1" />
								<xsl:with-param name="value1" select="'Investee'" />
								<xsl:with-param name="value2" select="'Rnd'" />
								<xsl:with-param name="value3" select="'Rnd Date'" />
								<xsl:with-param name="value4" select="'Equity (USD)'" />
								<xsl:with-param name="value5" select="'Debt (USD)'" />
								<xsl:with-param name="value6" select="'Security'" />
								<xsl:with-param name="value7" select="'Still Invested'" />
								<xsl:with-param name="value7C" select="$pe-funds/fundInvestBlock/fundInvest[fundInvestStillInvestedBlock/fundInvestStillInvested]" />
								<xsl:with-param name="value8" select="'Exit'" />
								<xsl:with-param name="value8C" select="$pe-funds/fundInvestBlock/fundInvest[fundInvestExitBlock/fundInvestExitTypeBlock/fundInvestExitType]" />
								<xsl:with-param name="col1Style" select="'width: 25%;'"/>
								<xsl:with-param name="col2Style" select="'width: 5%;'"/>
								<xsl:with-param name="col3Style" select="'width: 13%;'"/>
								<xsl:with-param name="col4Style" select="'width: 10%;'"/>
								<xsl:with-param name="col5Style" select="'width: 10%;'"/>
								<xsl:with-param name="col6Style" select="'width: 20%;'"/>
								<xsl:with-param name="col7Style" select="'width: 15%;'"/>
							</xsl:call-template>

							<!-- Investment table data -->
							<xsl:for-each select="$pe-funds/fundInvestBlock/fundInvest[fundInvestRoundDate and string-length(fundInvestRoundDate) > 0]">
								<xsl:sort select="./fundInvestRoundDate" order="descending"/>
								<xsl:call-template name="InvestmentRow"/>
							</xsl:for-each>
							<xsl:for-each select="$pe-funds/fundInvestBlock/fundInvest[not(fundInvestRoundDate and string-length(fundInvestRoundDate) > 0)]">
								<xsl:sort select="./fundInvestPortfolioCoNameBlock/fundInvestPortfolioCoName" order="ascending"/>
								<xsl:call-template name="InvestmentRow"/>
							</xsl:for-each>

						</table>

					</td>
				</tr>
			</table>
		</div>

	</xsl:template>

	<xsl:template name="InvestmentRow">
		<xsl:call-template name="DisplayValuesInTableCols">
			<xsl:with-param name="headers" select="0" />
			<xsl:with-param name="value1">
				<xsl:apply-templates select="./fundInvestPortfolioCoNameBlock/fundInvestPortfolioCoName" />
			</xsl:with-param>
			<xsl:with-param name="value2">
				<xsl:apply-templates select="./fundInvestRoundParticipated" />
			</xsl:with-param>
			<xsl:with-param name="value3">
				<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
					<xsl:with-param name="displayDay" select="1" />
					<xsl:with-param name="date">
						<xsl:apply-templates select="./fundInvestRoundDate" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="value4">
				<xsl:choose>
					<xsl:when test="./fundInvestEquityTotalConfidential = 'Confidential' or ./fundInvestEquityTotalConfidential = 'confidential'">
						<xsl:value-of select="'Confidential'"/>
					</xsl:when>
					<xsl:when test="(not(./fundInvestDebt) or ./fundInvestDebt = '0') and (not(./fundInvestEquity) or ./fundInvestEquity = '0')">
						<xsl:value-of select="$nd-answer"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatCurrancy">
							<xsl:with-param name="currancy" select="''"/>
							<xsl:with-param name="displayZero" select="true()"/>
							<xsl:with-param name="value" select="./fundInvestEquity"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="value5">
				<xsl:choose>
					<xsl:when test="(not(./fundInvestDebt) or ./fundInvestDebt = '0') and (not(./fundInvestEquity) or ./fundInvestEquity = '0')">
						<xsl:value-of select="$nd-answer"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatCurrancy">
							<xsl:with-param name="currancy" select="''"/>
							<xsl:with-param name="displayZero" select="true()"/>
							<xsl:with-param name="value" select="./fundInvestDebt"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>

			</xsl:with-param>
			<xsl:with-param name="value6">
				<xsl:for-each select="./fundInvestSecurityBlock/fundInvestSecurity">
					<xsl:apply-templates select="./fundInvestSecurityName" />
					<br />
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="value7">
				<xsl:apply-templates select="./fundInvestStillInvestedBlock/fundInvestStillInvested" />
			</xsl:with-param>
			<xsl:with-param name="value7C" select="$pe-funds/fundInvestBlock/fundInvest[fundInvestStillInvestedBlock/fundInvestStillInvested]" />
			<xsl:with-param name="value8">
				<xsl:for-each select="./fundInvestExitBlock/fundInvestExitTypeBlock">
					<xsl:apply-templates select="./fundInvestExitType" />
					<xsl:if test="./fundInvestExitType and ./fundInvestExitDate">
						<xsl:value-of select="' - '"/>
						<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
							<xsl:with-param name="displayDay" select="1" />
							<xsl:with-param name="date">
								<xsl:apply-templates select="./fundInvestExitDate" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="position() &lt; last()">
						<xsl:value-of select="' | '"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="value8C" select="$pe-funds/fundInvestBlock/fundInvest[fundInvestExitBlock/fundInvestExitTypeBlock/fundInvestExitType]" />
			<xsl:with-param name="col1Style" select="'width: 25%;'"/>
			<xsl:with-param name="col2Style" select="'width: 5%;'"/>
			<xsl:with-param name="col3Style" select="'width: 13%;'"/>
			<xsl:with-param name="col4Style" select="'width: 10%; text-align: right;'"/>
			<xsl:with-param name="col5Style" select="'width: 10%; text-align: right;'"/>
			<xsl:with-param name="col6Style" select="'width: 20%;'"/>
			<xsl:with-param name="col7Style" select="'width: 15%;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--************** END: Investment *******************-->



	<!--************** START: Firm *****************-->

	<xsl:template name="Firm">
		<div id="co_pefunds_firm">
			<table class="&layout_table; &blcPortfolioTable;">
				<tr class="&blcBorderTop;">
					<td class="&blcWidth25; &blcSectionHeading;">
						<h3>Firm Information</h3>
					</td>
					<td class="&blcWidth75;">
						<!-- Name -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Name:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$pe-firms/firmNameBlock/firmName" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Ticker/Exchange -->
						<xsl:variable name="firmTicker" select="$pe-firms/firmTicker" />
						<xsl:variable name="firmExch" select="$pe-firms/firmExch" />
						<xsl:if test="string-length($firmTicker) &gt; 0 or string-length($firmExch) &gt; 0">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Ticker/Exchange:'" />
								<xsl:with-param name="stringValue2">
									<xsl:if test="string-length($firmTicker) &gt; 0">
										<xsl:apply-templates select="$firmTicker" />
										<xsl:if test="string-length($firmExch) &gt; 0">
											<xsl:text>&#32;/&#32;</xsl:text>
										</xsl:if>
									</xsl:if>
									<xsl:apply-templates select="$firmExch" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>

						<!-- Founded Date -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Founded Date:'" />
							<xsl:with-param name="stringValue2">
								<xsl:call-template name="parseYearMonthDayDateFormat">
									<xsl:with-param name="displayDay" select="1" />
									<xsl:with-param name="date">
										<xsl:apply-templates select="$pe-firms/firmFoundingDate" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>

						<!-- Status -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Status:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$pe-firms/firmStatusBlock/firmStatus" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Location -->
						<xsl:variable name="firmLocationText">
							<xsl:call-template name="TwoDelimiterSeperatedStringValues">
								<xsl:with-param name="value1">
									<xsl:call-template name="TwoDelimiterSeperatedStringValues">
										<xsl:with-param name="value1">
											<xsl:apply-templates select="$pe-firms/firmLocBlock/firmCity" />
										</xsl:with-param>
										<xsl:with-param name="value2">
											<xsl:variable name="firmTRCSState" select="$pe-firms/firmLocBlock/firmTRCSstateBlock/firmTRCSstate" />
											<xsl:choose>
												<xsl:when test="string-length($firmTRCSState) &gt; 0">
													<xsl:apply-templates select="$firmTRCSState" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:apply-templates select="$pe-firms/firmLocBlock/firmLocalStateBlock/firmLocalState" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:with-param>
										<xsl:with-param name="delimiter" select="', '"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="value2">
									<xsl:apply-templates select="$pe-firms/firmLocBlock/firmCountryBlock/firmCountry" />
								</xsl:with-param>
								<xsl:with-param name="delimiter" select="', '"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Location:'" />
							<xsl:with-param name="stringValue2" select="$firmLocationText" />
						</xsl:call-template>

						<!-- Type -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Type:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$pe-firms/firmTypeBlock/firmType" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Venture Association Membership -->
						<xsl:if test="$pe-firms/firmVentureAssocMemberBlock/firmVentureAssocMember">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Venture Association Membership:'" />
								<xsl:with-param name="stringValue2">
									<xsl:for-each select="$pe-firms/firmVentureAssocMemberBlock">
										<xsl:apply-templates select="./firmVentureAssocMember" />
										<br />
									</xsl:for-each>
								</xsl:with-param>
								<xsl:with-param name="truncate" select="not($DeliveryMode)" />
							</xsl:call-template>
						</xsl:if>

						<!-- Description -->
						<xsl:variable name="firmOAshortDesc" select="$pe-firms/firmOAshortDesc" />
						<xsl:if test="$firmOAshortDesc">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Description:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$firmOAshortDesc" />
								</xsl:with-param>
								<xsl:with-param name="truncate" select="not($DeliveryMode)" />
							</xsl:call-template>
						</xsl:if>
					</td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!--************** END: Firm *******************-->



	<!--************** START: Executives *****************-->

	<xsl:template name="Executives">
		<xsl:if test="$pe-firms/firmExecBlock/*">
			<div id="co_pefunds_executives">
				<table class="&layout_table; &blcPortfolioTable;">
					<tr>
						<td class="&blcWidth25;"></td>
						<td class="&blcWidth75;">
							<h4 class="&blcSectionSubheading;">Executives</h4>

							<table>

								<xsl:choose>
									<xsl:when test="not($DeliveryMode)">
										<xsl:attribute name="class">&layout_table; &layout_4Columns; &morelessTextStyle; &ellipsisClass; &extraPaddingClass;</xsl:attribute>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="class">&layout_table; &layout_4Columns;</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>

								<!-- Executives table headers -->
								<xsl:call-template name="DisplayValuesInTableCols">
									<xsl:with-param name="headers" select="1" />
									<xsl:with-param name="value1" select="'Name'" />
									<xsl:with-param name="value2" select="'Title'" />
									<xsl:with-param name="value2C" select="$pe-firms/firmExecBlock/firmExec[firmExecTitleBlock/firmExecTitle/firmExecTitleName]" />
									<xsl:with-param name="value3" select="'Previous Title(s)'"/>
									<xsl:with-param name="value3C" select="$pe-firms/firmExecBlock/firmExec[firmExecPrevTitleBlock/firmExecPrevTitle/firmExecPrevTitleName]"/>
									<xsl:with-param name="value4" select="'Outsider'" />
									<xsl:with-param name="col1Style" select="'width: 30%'"/>
									<xsl:with-param name="col2Style" select="'width: 30%'"/>
									<xsl:with-param name="col3Style" select="'width: 30%'"/>
									<xsl:with-param name="col4Style" select="'width: 10%'"/>
								</xsl:call-template>

								<!-- Executives table data -->
								<xsl:for-each select="$pe-firms/firmExecBlock/firmExec">
									<xsl:call-template name="DisplayValuesInTableCols">
										<xsl:with-param name="headers" select="0" />
										<xsl:with-param name="value1">
											<xsl:apply-templates select="./firmExecNameBlock/firmExecName"/>
										</xsl:with-param>
										<xsl:with-param name="value2">
											<xsl:for-each select="./firmExecTitleBlock/firmExecTitle">
												<xsl:if test="position() != 1">
													<xsl:element name="br"/>
												</xsl:if>
												<xsl:apply-templates select="./firmExecTitleName" />
												<xsl:if test="./firmExecTitleStartDate">
													<xsl:text> (from </xsl:text>
													<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
														<xsl:with-param name="displayDay" select="1" />
														<xsl:with-param name="date" select="./firmExecTitleStartDate" />
													</xsl:call-template>
													<xsl:text>)</xsl:text>
												</xsl:if>
											</xsl:for-each>
										</xsl:with-param>
										<xsl:with-param name="value2C" select="$pe-firms/firmExecBlock/firmExec[firmExecTitleBlock/firmExecTitle/firmExecTitleName]" />
										<xsl:with-param name="value3">
											<xsl:for-each select="./firmExecPrevTitleBlock/firmExecPrevTitle">
												<xsl:if test="position() != 1">
													<xsl:element name="br"/>
												</xsl:if>
												<xsl:apply-templates select="./firmExecPrevTitleName" />
												<xsl:if test="./firmExecPrevTitleStartDate or ./firmExecPrevTitleEndDate">
													<xsl:text> (</xsl:text>

													<xsl:if test="./firmExecPrevTitleStartDate">
														<xsl:text>from </xsl:text>
														<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
															<xsl:with-param name="displayDay" select="1" />
															<xsl:with-param name="date" select="./firmExecPrevTitleStartDate" />
														</xsl:call-template>
													</xsl:if>

													<xsl:if test="./firmExecPrevTitleEndDate">
														<xsl:if test="./firmExecPrevTitleStartDate">
															<xsl:text> to </xsl:text>
														</xsl:if>
														<xsl:if test="not(./firmExecPrevTitleStartDate)">
															<xsl:text>to </xsl:text>
														</xsl:if>
														<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
															<xsl:with-param name="displayDay" select="1" />
															<xsl:with-param name="date" select="./firmExecPrevTitleEndDate" />
														</xsl:call-template>
													</xsl:if>

													<xsl:text>)</xsl:text>
												</xsl:if>
											</xsl:for-each>
										</xsl:with-param>
										<xsl:with-param name="value3C" select="$pe-firms/firmExecBlock/firmExec[firmExecPrevTitleBlock/firmExecPrevTitle/firmExecPrevTitleName]"/>
										<xsl:with-param name="value4">
											<xsl:choose>
												<xsl:when test="./firmExecOutsiderFlag = 'y'">
													<xsl:value-of select="'Yes'" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'No'" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:with-param>
										<xsl:with-param name="col1Style" select="'width: 30%'"/>
										<xsl:with-param name="col2Style" select="'width: 30%'"/>
										<xsl:with-param name="col3Style" select="'width: 30%'"/>
										<xsl:with-param name="col4Style" select="'width: 10%'"/>
									</xsl:call-template>
								</xsl:for-each>
							</table>
							<xsl:if test="not($DeliveryMode)">
								<xsl:call-template name="MoreLink">
									<xsl:with-param name="count" select="count($pe-firms/firmExecBlock/firmExec)" />
									<xsl:with-param name="threshold" select="1" />
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</table>
			</div>

		</xsl:if>
	</xsl:template>

	<!--************** END: Executives *******************-->



	<!--************** START: Firm Investment Strategy *****************-->

	<xsl:template name="FirmInvestmentStrategy">

		<div id="co_pefunds_firmInvestmentStrategy">
			<table class="&layout_table; &blcPortfolioTable;">
				<tr>
					<td class="&blcWidth25;"></td>
					<td class="&blcWidth75;">
						<h4 class="&blcSectionSubheading;">Investment Strategy</h4>

						<xsl:variable name="firmInvestStrategyBlock" select="$pe-firms/firmInvestStrategyBlock" />

						<!-- Capital Under Management (USD) -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Capital Under Management (USD):'" />
							<xsl:with-param name="stringValue2">
								<xsl:choose>
									<xsl:when test="$firmInvestStrategyBlock/firmIScapUnderMgmtBlock/firmIScapUnderMgmtConfidential = 'Y' or not($firmInvestStrategyBlock/firmIScapUnderMgmtBlock/firmIScapUnderMgmt)">
										<xsl:value-of select="'Confidential'" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="Format">
											<xsl:with-param name="value" select="$firmInvestStrategyBlock/firmIScapUnderMgmtBlock/firmIScapUnderMgmt"/>
											<xsl:with-param name="format" select="'#,###'"/>
											<xsl:with-param name="currancy" select="' (USD)'"/>
											<xsl:with-param name="postfix">
												<xsl:if test="$firmInvestStrategyBlock/firmIScapUnderMgmtBlock/firmIScapUnderMgmtDate">
													<xsl:text> as of </xsl:text>
													<xsl:call-template name="parseYearMonthDayDateFormat">
														<xsl:with-param name="displayDay" select="1" />
														<xsl:with-param name="date" select="$firmInvestStrategyBlock/firmIScapUnderMgmtBlock/firmIScapUnderMgmtDate" />
													</xsl:call-template>
												</xsl:if>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>

						<!-- Usual Investment Type -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Usual Investment Type:'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$firmInvestStrategyBlock/firmISusualInvestTypeBlock/firmISusualInvestType" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Industry Focus (>60% Investments) -->
						<xsl:call-template name="DisplayStringValuesInOneColumn">
							<xsl:with-param name="stringValue1" select="'Industry Focus (>60% Investments):'" />
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="$firmInvestStrategyBlock/firmISindFocusBlock/firmISindFocus" />
							</xsl:with-param>
						</xsl:call-template>

						<!-- Industry Preference -->
						<xsl:call-template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
							<xsl:with-param name="name" select="'Industry Preference:'" />
							<xsl:with-param name="nodesset" select="$firmInvestStrategyBlock/firmISprefIndBlock/firmISprefInd" />
							<xsl:with-param name="truncate" select="not($DeliveryMode)" />
						</xsl:call-template>


						<!-- Geography Preference -->
						<xsl:call-template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
							<xsl:with-param name="name" select="'Geography Preference:'" />
							<xsl:with-param name="nodesset" select="$firmInvestStrategyBlock/firmISprefGeoBlock/firmISprefGeo" />
							<xsl:with-param name="truncate" select="not($DeliveryMode)" />
						</xsl:call-template>

						<!-- Role Preference -->
						<xsl:call-template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
							<xsl:with-param name="name" select="'Role Preference:'" />
							<xsl:with-param name="nodesset" select="$firmInvestStrategyBlock/firmISprefRoleBlock/firmISprefRole" />
						</xsl:call-template>

					</td>
				</tr>
			</table>
		</div>

	</xsl:template>

	<!--************** END: Firm Investment Strategy *******************-->



	<!--************** START: Real Estate Section *****************-->

	<xsl:template name="RealEstateSection">
		<xsl:if test="$pe-firms/firmRealEstateBlock/*">
			<div id="co_pefunds_realEstateSection">
				<div class="&layoutHeaderRow; &headText;">
					<i>
						<xsl:text>Real Estate Section</xsl:text>
					</i>
				</div>

				<table class="&layout_table; &layout_2Columns;">

					<!-- Founding Year -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Founding Year:'" />
						<xsl:with-param name="stringValue2">
							<xsl:apply-templates select="$pe-firms/firmRealEstateBlock/firmREfoundingYear" />
						</xsl:with-param>
					</xsl:call-template>

					<!-- Number of Employees -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Number of Employees:'" />
						<xsl:with-param name="stringValue2">
							<xsl:apply-templates select="$pe-firms/firmRealEstateBlock/firmREemployeeCount" />
						</xsl:with-param>
					</xsl:call-template>

					<!-- Number of Value Added RE Funds Managed -->
					<xsl:call-template name="DisplayStringValueIn2Columns">
						<xsl:with-param name="stringValue1" select="'Number of Value Added RE Funds Managed:'" />
						<xsl:with-param name="stringValue2">
							<xsl:apply-templates select="$pe-firms/firmRealEstateBlock/firmREvalueAddedFundsCount" />
						</xsl:with-param>
					</xsl:call-template>

				</table>

			</div>
		</xsl:if>
	</xsl:template>

	<!--************** END: Real Estate Section *******************-->



	<!--************** START: Branch Offices *****************-->

	<xsl:template name="BranchOffices">
		<xsl:if test="$pe-firms/firmBranchOfficeBlock/*">

			<div id="co_pefunds_branchOffices">
				<table class="&layout_table; &blcPortfolioTable;">
					<tr>
						<td class="&blcWidth25;"></td>
						<td class="&blcWidth75;">
							<h4 class="&blcSectionSubheading;">Branch Offices</h4>

							<table class="&layout_table; &layout_2Columns;">
								<xsl:choose>
									<xsl:when test="not($DeliveryMode)">
										<xsl:attribute name="class">&layout_table; &layout_4Columns; &morelessTextStyle; &ellipsisClass; &extraPaddingClass;</xsl:attribute>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="class">&layout_table; &layout_4Columns;</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
								<!-- Branch Offices table headers -->
								<xsl:call-template name="DisplayValuesInTableCols">
									<xsl:with-param name="headers" select="1" />
									<xsl:with-param name="value1" select="'Name'" />
									<xsl:with-param name="value2" select="'Location'" />
								</xsl:call-template>

								<!-- Branch Offices table data -->
								<xsl:for-each select="$pe-firms/firmBranchOfficeBlock/firmBranchOffice">
									<xsl:call-template name="DisplayValuesInTableCols">
										<xsl:with-param name="headers" select="0" />
										<xsl:with-param name="value1">
											<xsl:apply-templates select="./firmBOnameBlock/firmBOname" />
										</xsl:with-param>
										<!--<xsl:with-param name="value1" select="./firmBOnameBlock/firmBOname" />-->
										<xsl:with-param name="value2">
											<xsl:call-template name="TwoDelimiterSeperatedStringValues">
												<xsl:with-param name="value1">
													<xsl:call-template name="TwoDelimiterSeperatedStringValues">
														<xsl:with-param name="value1">
															<xsl:apply-templates select="./firmBOlocBlock/firmBOcity" />
														</xsl:with-param>
														<xsl:with-param name="value2">
															<xsl:variable name="firmBOTRCSstate" select="./firmBOlocBlock/firmBOTRCSstateBlock/firmBOTRCSstate" />
															<xsl:choose>
																<xsl:when test="string-length($firmBOTRCSstate) &gt; 0">
																	<xsl:apply-templates select="$firmBOTRCSstate" />
																</xsl:when>
																<xsl:otherwise>
																	<xsl:apply-templates select="./firmBOlocBlock/firmBOlocalStateBlock/firmBOlocalState" />
																</xsl:otherwise>
															</xsl:choose>
														</xsl:with-param>
														<xsl:with-param name="delimiter" select="', '"/>
													</xsl:call-template>
												</xsl:with-param>
												<xsl:with-param name="value2">
													<xsl:apply-templates select="./firmBOlocBlock/firmBOcountryBlock/firmBOcountry" />
												</xsl:with-param>
												<xsl:with-param name="delimiter" select="', '"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:for-each>
							</table>
							<xsl:if test="not($DeliveryMode)">
								<xsl:call-template name="MoreLink">
									<xsl:with-param name="count" select="count($pe-firms/firmBranchOfficeBlock/firmBranchOffice)" />
									<xsl:with-param name="threshold" select="1" />
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!--************** END: Branch Offices *******************-->



	<!--********** START: Utility templates **********-->

	<!--VerifyStringValue-->
	<xsl:template name="VerifyStringValue">
		<xsl:param name="stringValue"/>
		<xsl:param name="displayZero" select="false()" />
		<xsl:param name="NAtoZero" select="false()" />

		<xsl:choose>
			<xsl:when test="contains($stringValue, $doNotDisplayValue)">
				<xsl:value-of select="''" />
			</xsl:when>
			<xsl:when test="$stringValue = 'Y'">
				<xsl:value-of select="'Yes'" />
			</xsl:when>
			<xsl:when test="$stringValue = 'N'">
				<xsl:value-of select="'No'" />
			</xsl:when>
			<xsl:when test="string-length($stringValue) > 0 and not($stringValue='NA') and not($stringValue='0')">
				<xsl:copy-of select="$stringValue" />
			</xsl:when>
			<xsl:when test="($stringValue = '0' and $displayZero) or (($stringValue='NA' or string-length($stringValue) = 0) and $NAtoZero=true())">
				<xsl:value-of select="'0'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- TwoDelimiterSeperatedStringValues -->
	<xsl:template name="TwoDelimiterSeperatedStringValues">
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>
		<xsl:param name="delimiter"/>

		<xsl:choose>
			<xsl:when test="(string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) != 0 and $value2 != $na-answer)">
				<xsl:copy-of select="$value2"/>
			</xsl:when>
			<xsl:when test="(string-length($value2) = 0 or $value2 = $na-answer) and (string-length($value1) != 0 and $value1 != $na-answer)">
				<xsl:copy-of select="$value1"/>
			</xsl:when>
			<xsl:when test="(string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) = 0 or $value2 = $na-answer)">
				<xsl:value-of select="$na-answer"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$value1"/>
				<xsl:value-of select="$delimiter"/>
				<xsl:copy-of select="$value2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--DisplayValuesInTableCols-->
	<xsl:template name="DisplayValuesInTableCols">
		<xsl:param name="headers"/>
		<xsl:param name="value1" />
		<xsl:param name="col1Style"/>
		<xsl:param name="value2"/>
		<xsl:param name="col2Style"/>
		<xsl:param name="value2C" select="true()"/>
		<xsl:param name="value3"/>
		<xsl:param name="col3Style"/>
		<xsl:param name="value3C" select="true()"/>
		<xsl:param name="value4"/>
		<xsl:param name="col4Style"/>
		<xsl:param name="value5"/>
		<xsl:param name="col5Style"/>
		<xsl:param name="value6"/>
		<xsl:param name="col6Style"/>
		<xsl:param name="value7"/>
		<xsl:param name="col7Style"/>
		<xsl:param name="value7C" select="true()"/>
		<xsl:param name="value8"/>
		<xsl:param name="value8C" select="true()"/>

		<tr>
			<xsl:choose>
				<xsl:when test="$headers">
					<xsl:if test="$value1">
						<td class="&layout_col1;">
							<xsl:if test="$col1Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col1Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value1" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value2 and $value2C">
						<td class="&layout_col2;">
							<xsl:if test="$col2Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col2Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value2" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value3 and $value3C">
						<td class="&layout_col3;">
							<xsl:if test="$col3Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col3Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value3" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value4">
						<td class="&layout_col4;">
							<xsl:if test="$col4Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col4Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value4" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value5">
						<td class="&layout_col5;">
							<xsl:if test="$col5Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col5Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value5" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value6">
						<td class="&layout_col6;">
							<xsl:if test="$col6Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col6Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value6" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value7 and $value7C">
						<td class="&layout_col7;">
							<xsl:if test="$col7Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col7Style"/>
								</xsl:attribute>
							</xsl:if>
							<b>
								<xsl:copy-of select="$value7" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value8 and $value8C">
						<td class="&layout_col8;">
							<b>
								<xsl:copy-of select="$value8" />
							</b>
						</td>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="$value1">
						<td class="&layout_col1;">
							<xsl:if test="$col1Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col1Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value1" />
						</td>
					</xsl:if>
					<xsl:if test="$value2 and $value2C">
						<td class="&layout_col2;">
							<xsl:if test="$col2Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col2Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value2" />
						</td>
					</xsl:if>
					<xsl:if test="$value3 and $value3C">
						<td class="&layout_col3;">
							<xsl:if test="$col3Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col3Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value3" />
						</td>
					</xsl:if>
					<xsl:if test="$value4">
						<td class="&layout_col4;">
							<xsl:if test="$col4Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col4Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value4" />
						</td>
					</xsl:if>
					<xsl:if test="$value5">
						<td class="&layout_col5;">
							<xsl:if test="$col5Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col5Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value5" />
						</td>
					</xsl:if>
					<xsl:if test="$value6">
						<td class="&layout_col6;">
							<xsl:if test="$col6Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col6Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value6" />
						</td>
					</xsl:if>
					<xsl:if test="$value7 and $value7C">
						<td class="&layout_col7;">
							<xsl:if test="$col7Style">
								<xsl:attribute name="style">
									<xsl:value-of select="$col7Style"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$value7" />
						</td>
					</xsl:if>
					<xsl:if test="$value8 and $value8C">
						<td class="&layout_col8;">
							<xsl:copy-of select="$value8" />
						</td>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</tr>
	</xsl:template>

	<!-- MoreLink -->
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

	<!-- parseAbbrevYearMonthDayDateFormat -->
	<xsl:template name="parseAbbrevYearMonthDayDateFormat">
		<xsl:param name="date" select="."/>
		<xsl:param name="displayDay" />
		<xsl:param name="displayTime" />
		<xsl:if test="string-length($date) &gt; 7 and number($date) != 'NaN'">
			<xsl:variable name="year" select="substring($date,1,4)"/>
			<xsl:variable name="month" select="substring($date,5,2)"/>
			<xsl:variable name="day" select="substring($date,7,2)"/>
			<xsl:choose>
				<xsl:when test="$month = 01">Jan.</xsl:when>
				<xsl:when test="$month = 02">Feb.</xsl:when>
				<xsl:when test="$month = 03">Mar.</xsl:when>
				<xsl:when test="$month = 04">Apr.</xsl:when>
				<xsl:when test="$month = 05">May</xsl:when>
				<xsl:when test="$month = 06">Jun.</xsl:when>
				<xsl:when test="$month = 07">Jul.</xsl:when>
				<xsl:when test="$month = 08">Aug.</xsl:when>
				<xsl:when test="$month = 09">Sep.</xsl:when>
				<xsl:when test="$month = 10">Oct.</xsl:when>
				<xsl:when test="$month = 11">Nov.</xsl:when>
				<xsl:when test="$month = 12">Dec.</xsl:when>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:if test="$displayDay">
				<xsl:value-of select="$day"/>
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:value-of select="$year"/>
		</xsl:if>
	</xsl:template>

	<!--DisplayStringValueIn2Columns-->
	<xsl:template name="DisplayStringValueIn2Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="truncate"/>
		<xsl:param name="indentLeft"/>
		<xsl:param name="displayZero" select="false()" />
		<xsl:param name="NAtoZero" select="false()"/>

		<tr>
			<xsl:choose>
				<xsl:when test="$indentLeft">
					<td class="&layout_col1;">
						<div class="&indentLeft2Class;">
							<xsl:copy-of select="$stringValue1"/>
						</div>
					</td>
				</xsl:when>
				<xsl:otherwise>
					<td class="&layout_col1;">
						<div>
							<xsl:copy-of select="$stringValue1"/>
						</div>
					</td>
				</xsl:otherwise>
			</xsl:choose>

			<td class="&layout_col2;">
				<div>
					<xsl:if test="$truncate=string(true())">
						<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
					</xsl:if>
					<xsl:if test="$indentLeft">
						<xsl:attribute name="class">&indentLeft2Class;</xsl:attribute>
					</xsl:if>

					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="stringValue" select="$stringValue2" />
						<xsl:with-param name="displayZero" select="$displayZero" />
						<xsl:with-param name="NAtoZero" select="$NAtoZero" />
					</xsl:call-template>

				</div>

				<xsl:if test="$truncate=string(true())">
					<xsl:call-template name="MoreLink">
						<xsl:with-param name="count" select="string-length($stringValue2)" />
						<xsl:with-param name="threshold" select="1" />
					</xsl:call-template>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!-- SplitDelimitedStringIntoNewLines -->
	<xsl:template name="SplitDelimitedStringIntoNewLines">
		<xsl:param name="stringValue" />
		<xsl:param name="delimiter" />
		<xsl:param name="textToInsertAfter" />
		<xsl:if test="string-length($stringValue) &gt; 0">
			<xsl:choose>
				<xsl:when test="contains($stringValue, $delimiter)">
					<xsl:value-of select="substring-before($stringValue, $delimiter)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$stringValue"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="$textToInsertAfter">
				<xsl:value-of select="$textToInsertAfter"/>
			</xsl:if>
			<br />
			<xsl:call-template name="SplitDelimitedStringIntoNewLines">
				<xsl:with-param name="stringValue" select="substring-after($stringValue, $delimiter)"/>
				<xsl:with-param name="delimiter" select="$delimiter" />
				<xsl:with-param name="textToInsertAfter" select="$textToInsertAfter" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--********** END: Utility templates ************-->
	<xsl:template name="FormatCurrancy">
		<xsl:param name="value" />
		<xsl:param name="currancy" select="' (USD)'" />
		<xsl:param name="displayZero" select="false()" />

		<xsl:call-template name="Format">
			<xsl:with-param name="value" select="$value" />
			<xsl:with-param name="format" select="'#,###'" />
			<xsl:with-param name ="currancy" select="$currancy"/>
			<xsl:with-param name="displayZero" select="$displayZero" />
		</xsl:call-template>
	</xsl:template>

	<!-- Format -->
	<xsl:template name="Format">
		<xsl:param name="value" />
		<xsl:param name="format" select="'#'"/>
		<xsl:param name="currancy" select="''"/>
		<xsl:param name ="postfix" select ="''"/>
		<xsl:param name="displayZero" select="false()" />

		<xsl:choose>
			<xsl:when test="(string-length($value) > 0 and number($value) != 'NaN' and $value != '0')">
				<xsl:value-of select="concat(format-number($value, $format), $currancy, $postfix)" />
			</xsl:when>
			<xsl:when test="$displayZero = true()">
				<xsl:value-of select="concat(0, $postfix)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- DisplayNodesSetValuesOnDifferentLinesOrNA -->
	<xsl:template name="DisplayNodesSetValuesOnDifferentLinesOrNA">
		<xsl:param name="nodesset"/>
		<xsl:param name="name"/>
		<xsl:param name="truncate"/>
		<xsl:choose>
			<xsl:when test="$nodesset">
				<table class="&blcNestedTable;">
					<tr>
						<td>
							<xsl:call-template name="DisplayLabel">
								<xsl:with-param name="text" select="$name" />
							</xsl:call-template>
						</td>
						<td class="&blcNestedTableCell;">
							<xsl:for-each select="$nodesset">
								<div>
									<xsl:if test="position() > 5">
										<xsl:if test="$truncate = true()">
											<xsl:attribute name="class">&morelessItemStyle; &hideStateClass;</xsl:attribute>
										</xsl:if>
									</xsl:if>
									<xsl:apply-templates/>
								</div>
							</xsl:for-each>
						</td>
					</tr>
				</table>
				<xsl:if test="$truncate = true()">
					<xsl:call-template name="MoreLink">
						<xsl:with-param name="count" select="count($nodesset)" />
						<xsl:with-param name="threshold" select="5" />
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="$name" />
					<xsl:with-param name="stringValue2" select="''"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
