<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:include href="Date.xsl "/>

	<!-- variables -->
	<xsl:variable name="doNotDisplayValue" select="'~DoNotDisplay~'" />
	<xsl:variable name="na-answer" select="'Not Available'" />
	<xsl:variable name="nd-answer" select="'ND'" />
	<xsl:variable name="xpath-root" select="//n-docbody/PrivateEquityInvestments" />
	<xsl:variable name="trans-summary" select="$xpath-root/PEIsummary" />
	<xsl:variable name="pe-portfolio-company" select="$xpath-root/PEportfolioCompany" />
	<xsl:variable name="pe-real-estate-asset" select="$xpath-root/PErealEstateAsset" />
	<xsl:variable name="pe-exit" select="$xpath-root/PEexit" />
	<xsl:variable name="pe-portfolio-fund" select="$xpath-root/PEportfolioFund" />

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePrivateEquityDeals;'"/>
			</xsl:call-template>
			<xsl:call-template name="Content" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Content -->

	<xsl:template name ="Content">
		<xsl:call-template name="TransactionSummary" />
		<xsl:call-template name="PortfolioCompany" />
		<xsl:call-template name="RealEstateAsset" />
		<xsl:call-template name="Fund" />
		<xsl:call-template name="InvestmentRoundBlock" />
		<xsl:call-template name="Exit" />
	</xsl:template>

	<!-- Summary Section -->
	<xsl:template name="TransactionSummary">
		<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='TRS']">
			<xsl:call-template name="TOC">
				<xsl:with-param name="cd" select="'TRS'"/>
			</xsl:call-template>

			<div id="co_peDeals_summary">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Transaction Summary</h3>
							</td>
							<td class="&blcWidth75;">

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'PE Backed Status:'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template	name="stringWithDate">
											<xsl:with-param name="prefix" select ="$trans-summary/backedStatusBlock/backedStatus" />
											<xsl:with-param name="date" select ="$trans-summary/backedStatusBlock/backedStatusDate" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Latest Round Date:'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template name="parseYearMonthDayDateFormat">
											<xsl:with-param name="date" select="$trans-summary/lastRoundBlock/lastRoundDate" />
											<xsl:with-param name="displayDay" select="true()" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="$trans-summary/lastRoundBlock/lastRoundfinancingStage and string-length($trans-summary/lastRoundBlock/lastRoundfinancingStage) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Latest Round Financing Stage:'" />
										<xsl:with-param name="RowValue">
											<xsl:choose>
												<xsl:when test="$trans-summary/lastRoundBlock/lastRoundConfidential='confidential' or $trans-summary/lastRoundBlock/lastRoundConfidential='Confidential'">
													<xsl:value-of select="'Confidential'"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:apply-templates select="$trans-summary/lastRoundBlock/lastRoundfinancingStage"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Latest Round Total Financing:'" />
									<xsl:with-param name="RowValue">
										<xsl:choose>
											<xsl:when test="not($trans-summary/lastRoundBlock/lastRoundTotalFinancing) or $trans-summary/lastRoundBlock/lastRoundTotalFinancing = '0'">
												<xsl:value-of select="'Confidential'"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:call-template name="FormatCurrancy">
													<xsl:with-param name="value" select="$trans-summary/lastRoundBlock/lastRoundTotalFinancing" />
												</xsl:call-template>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="$trans-summary/numberOfFundingRoundsCompleted and string-length($trans-summary/numberOfFundingRoundsCompleted) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Number of Funding Rounds Completed:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$trans-summary/numberOfFundingRoundsCompleted" />
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Number of Funds Invested:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$trans-summary/numberOfFundsInvested"/>
									</xsl:with-param>
									<xsl:with-param name ="skipZero" select="false()"/>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Number of Firms Invested:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$trans-summary/numberOfFirmsInvested"/>
									</xsl:with-param>
									<xsl:with-param name ="skipZero" select="false()"/>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Total Non-Confidential Equity (USD):'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template name="FormatCurrancy">
											<xsl:with-param name="value" select="$trans-summary/totalEquityFinancing" />
											<xsl:with-param name="skipZero" select="false()"/>
										</xsl:call-template>
									</xsl:with-param>
									<xsl:with-param name="NAtoZero" select="true()" />
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Total Disclosed Debt (USD):'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template name="FormatCurrancy">
											<xsl:with-param name="value" select="$trans-summary/totalDebtFinancing" />
											<xsl:with-param name="skipZero" select="false()"/>
										</xsl:call-template>
									</xsl:with-param>
									<xsl:with-param name="NAtoZero" select="true()" />
								</xsl:call-template>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!--Display Label-->
	<xsl:template name="DisplayLabel">
		<xsl:param name="text"/>
		<strong>
			<xsl:value-of select="$text"/>
			<xsl:text>&nbsp;&nbsp;</xsl:text>
		</strong>
	</xsl:template>

	<!-- Portfolio Section -->

	<xsl:template name="PortfolioCompany">
		<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='PCI']">
			<xsl:call-template name="TOC">
				<xsl:with-param name="cd" select="'PCI'"/>
			</xsl:call-template>

			<div id="co_peDeals_portfolioCompany">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&blcBorderTop;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Portfolio Company</h3>
							</td>
							<td class="&blcWidth75;">

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Name:'" />
									<xsl:with-param name="RowValue" >
										<xsl:choose>
											<xsl:when test="$pe-portfolio-company/PCnameBlock/PCname and string-length($pe-portfolio-company/PCnameBlock/PCname) > 0">
												<xsl:apply-templates select="$pe-portfolio-company/PCnameBlock/PCname"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>Undisclosed</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="$pe-portfolio-company/PCvariantNameBlock">
									<xsl:for-each select="$pe-portfolio-company/PCvariantNameBlock">
										<xsl:call-template name="CreateTableRow" >
											<xsl:with-param name="RowLabel" select="concat(./PCvariantNameType,':')" />
											<xsl:with-param name="RowValue">
												<xsl:apply-templates select="./PCvariantName"/>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:for-each>
								</xsl:if>

								<!-- TODO: if more than one, stack values with primary ticker on top. -->
								<xsl:if test="$pe-portfolio-company/PCstockListingBlock/PCstockListingPrimary/PCSLprimaryTicker 
							and string-length($pe-portfolio-company/PCstockListingBlock/PCstockListingPrimary/PCSLprimaryTicker) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Ticker:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-company/PCstockListingBlock/PCstockListingPrimary/PCSLprimaryTicker"/>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<!-- TODO: if more than one, stack values with primary ticker on top. -->
								<xsl:if test="$pe-portfolio-company/PCstockListingBlock/PCstockListingPrimary/PCSLprimaryExchBlock/PCSLprimaryExch 
							and string-length($pe-portfolio-company/PCstockListingBlock/PCstockListingPrimary/PCSLprimaryExchBlock/PCSLprimaryExch) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Exchange:'" />
										<xsl:with-param name="RowValue" >
											<xsl:apply-templates select="$pe-portfolio-company/PCstockListingBlock/PCstockListingPrimary/PCSLprimaryExchBlock/PCSLprimaryExch"/>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Founded:'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template name="parseYearMonthDayDateFormat">
											<xsl:with-param name="date" select="$pe-portfolio-company/PCfoundedDate" />
											<xsl:with-param name="displayDay" select="true()" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Operational Stage:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$pe-portfolio-company/PCoperationStageBlock/PCoperationStage"/>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Status:'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template	name="stringWithDate">
											<xsl:with-param name="prefix" select="$pe-portfolio-company/PCstatusBlock/PCstatus" />
											<xsl:with-param name="date" select="$pe-portfolio-company/PCstatusBlock/PCstatusDate" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="$pe-portfolio-company/PClocationBlock/*">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Location:'" />
										<xsl:with-param name="RowValue">
											<xsl:if test="$pe-portfolio-company/PClocationBlock/PCcountryBlock/PCcountry">
												<xsl:call-template name="join">
													<xsl:with-param name="nodes" select="$pe-portfolio-company/PClocationBlock/PCcity 
									| $pe-portfolio-company/PClocationBlock/PClocalStateBlock/PClocalState 
									| $pe-portfolio-company/PClocationBlock/PCcountryBlock/PCcountry" />
												</xsl:call-template>
											</xsl:if>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PClocationOfIncorporationBlock/*">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Location of Incorporation:'" />
										<xsl:with-param name="RowValue">
											<xsl:call-template name="join">
												<xsl:with-param name="nodes" select="$pe-portfolio-company/PClocationOfIncorporationBlock/PClocOfIncorpStateBlock/PClocOfIncorpState 
														| $pe-portfolio-company/PClocationOfIncorporationBlock/PClocOfIncorpCountryBlock/PClocOfIncorpCountry" />
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Ownership:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$pe-portfolio-company/PCownershipBlock/PCownership" />
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="$pe-portfolio-company/PCindBlock/PCindEconBlock/*">
									<table class="&blcNestedTable;">
										<tr>
											<td>
												<xsl:call-template name="DisplayLabel">
													<xsl:with-param name="text" select="'Industry (Econ):'" />
												</xsl:call-template>
											</td>
											<td class="&blcNestedTableCell;">
												<xsl:for-each select="$pe-portfolio-company/PCindBlock/PCindEconBlock/PCindEcon">
													<div>
														<xsl:call-template name ="concat">
															<xsl:with-param name="value1" select="./PCindEconCode" />
															<xsl:with-param name="value2" select="./PCindEconType" />
														</xsl:call-template>
													</div>
												</xsl:for-each>
											</td>
										</tr>
									</table>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PCindBlock/PCindTechBlock/*">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Industry (Tech):'" />
										<xsl:with-param name="RowValue">
											<xsl:for-each select="$pe-portfolio-company/PCindBlock/PCindTechBlock/PCindTech">
												<xsl:if test="position() != 1">
													<br/>
												</xsl:if>
												<xsl:call-template name ="concat">
													<xsl:with-param name="value1" select="./PCindTechCode" />
													<xsl:with-param name="value2" select="./PCindTechType" />
												</xsl:call-template>
											</xsl:for-each>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PCprimaryCustomerBlock/PCprimaryCustomer and string-length($pe-portfolio-company/PCprimaryCustomerBlock/PCprimaryCustomer) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Primary Customer Type:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-company/PCprimaryCustomerBlock/PCprimaryCustomer" />
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PCcompanyDesc and string-length($pe-portfolio-company/PCcompanyDesc) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Company Description:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-company/PCcompanyDesc"/>
										</xsl:with-param>
										<xsl:with-param name="Truncate" select="not($DeliveryMode)" />
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PCcustomerDesc and string-length($pe-portfolio-company/PCcustomerDesc) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Customer Description:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-company/PCcustomerDesc"/>
										</xsl:with-param>
										<xsl:with-param name="Truncate" select="not($DeliveryMode)" />
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PCmarketDesc and string-length($pe-portfolio-company/PCmarketDesc) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Market Description:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-company/PCmarketDesc"/>
										</xsl:with-param>
										<xsl:with-param name="Truncate" select="not($DeliveryMode)" />
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-portfolio-company/PCcompetitorBlock/PCcompetitor and string-length($pe-portfolio-company/PCcompetitorBlock/PCcompetitor) > 0">
									<table class="&blcNestedTable;">
										<tr>
											<td>
												<xsl:call-template name="DisplayLabel">
													<xsl:with-param name="text" select="'Competitors:'" />
												</xsl:call-template>
											</td>
											<td class="&blcNestedTableCell;">
												<xsl:for-each select="$pe-portfolio-company/PCcompetitorBlock/PCcompetitor">
													<div>
														<xsl:if test="position() > 5">
															<xsl:if test="not($DeliveryMode)">
																<xsl:attribute name="class">&morelessItemStyle; &hideStateClass;</xsl:attribute>
															</xsl:if>
														</xsl:if>
														<xsl:apply-templates select="./PCcompetitorName"/>
													</div>
												</xsl:for-each>
											</td>
										</tr>
									</table>
									<xsl:if test="not($DeliveryMode)">
										<xsl:call-template name="MoreLink">
											<xsl:with-param name="count" select="count($pe-portfolio-company/PCcompetitorBlock/PCcompetitor)" />
											<xsl:with-param name="threshold" select="5" />
										</xsl:call-template>
									</xsl:if>
								</xsl:if>
							</td>
						</tr>
					</tbody>
				</table>
				<xsl:call-template name="Executives" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="Executives">
		<xsl:variable name="execBlock" select="$pe-portfolio-company/PEexecutiveBlock" />

		<xsl:if test="$execBlock/*">

			<div id="co_pedeals_executives">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&blcBorderTop;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Executives</h3>
							</td>
							<td class="&blcWidth75;">

								<table>
									<xsl:choose>
										<xsl:when test="not($DeliveryMode)">
											<xsl:attribute name="class">&layout_table; &layout_4Columns; &morelessTextStyle; &ellipsisClass; &extraPaddingClass;</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="class">&layout_table; &layout_4Columns; &extraPaddingClass;</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>

									<!-- Executives table headers -->
									<xsl:call-template name="DisplayValuesInTableCols">
										<xsl:with-param name="headers" select="1" />
										<xsl:with-param name="value1" select="'Name'" />
										<xsl:with-param name="value2" select="'Title'" />
										<xsl:with-param name="value2C" select="$execBlock/executive[execTitleBlock/execTitle/execTitleName]" />
										<xsl:with-param name="value3" select="'Previous Title(s)'"/>
										<xsl:with-param name="value3C" select="$execBlock/executive[execPrevTitleBlock/execPrevTitle/execPrevTitleName]"/>
										<xsl:with-param name="value4" select="'Outsider'" />
										<xsl:with-param name="col1Style" select="'width: 30%'"/>
										<xsl:with-param name="col2Style" select="'width: 30%'"/>
										<xsl:with-param name="col3Style" select="'width: 30%'"/>
										<xsl:with-param name="col4Style" select="'width: 10%'"/>
									</xsl:call-template>

									<!-- Executives table data -->
									<xsl:for-each select="$execBlock/executive">
										<xsl:call-template name="DisplayValuesInTableCols">
											<xsl:with-param name="headers" select="0" />
											<xsl:with-param name="value1">
												<xsl:apply-templates select="./execNameBlock/execName"/>
											</xsl:with-param>
											<xsl:with-param name="value2">
												<xsl:for-each select="./execTitleBlock/execTitle">
													<xsl:if test="position() != 1">
														<xsl:element name="br"/>
													</xsl:if>
													<xsl:apply-templates select="./execTitleName" />
													<xsl:if test="./execTitleStartDate">
														<xsl:text> (from </xsl:text>
														<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
															<xsl:with-param name="displayDay" select="1" />
															<xsl:with-param name="date" select="./execTitleStartDate" />
														</xsl:call-template>
														<xsl:text>)</xsl:text>
													</xsl:if>
												</xsl:for-each>
											</xsl:with-param>
											<xsl:with-param name="value2C" select="$execBlock/executive[execTitleBlock/execTitle/execTitleName]" />
											<xsl:with-param name="value3">
												<xsl:for-each select="./execPrevTitleBlock/execPrevTitle">
													<xsl:if test="position() != 1">
														<xsl:element name="br"/>
													</xsl:if>
													<xsl:apply-templates select="./execPrevTitleName" />
													<xsl:if test="./execPrevTitleStartDate or ./execPrevTitleEndDate">
														<xsl:text> (</xsl:text>

														<xsl:if test="./execPrevTitleStartDate">
															<xsl:text>from </xsl:text>
															<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
																<xsl:with-param name="displayDay" select="1" />
																<xsl:with-param name="date" select="./execPrevTitleStartDate" />
															</xsl:call-template>
														</xsl:if>

														<xsl:if test="./execPrevTitleEndDate">
															<xsl:if test="./execPrevTitleStartDate">
																<xsl:text> to </xsl:text>
															</xsl:if>
															<xsl:if test="not(./execPrevTitleStartDate)">
																<xsl:text>to </xsl:text>
															</xsl:if>
															<xsl:call-template name="parseAbbrevYearMonthDayDateFormat">
																<xsl:with-param name="displayDay" select="1" />
																<xsl:with-param name="date" select="./execPrevTitleEndDate" />
															</xsl:call-template>
														</xsl:if>

														<xsl:text>)</xsl:text>
													</xsl:if>
												</xsl:for-each>
											</xsl:with-param>
											<xsl:with-param name="value3C" select="$execBlock/executive[execPrevTitleBlock/execPrevTitle/execPrevTitleName]"/>
											<xsl:with-param name="value4">
												<xsl:choose>
													<xsl:when test="./execOutsiderFlag = 'y'">
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
										<xsl:with-param name="count" select="count($execBlock/executive)" />
										<xsl:with-param name="threshold" select="1" />
									</xsl:call-template>
								</xsl:if>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Real Estate Asset Section -->

	<xsl:template name="RealEstateAsset">
		<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='REA']">
			<xsl:call-template name="TOC">
				<xsl:with-param name="cd" select="'REA'"/>
			</xsl:call-template>

			<div id="co_peDeals_realEstateAsset">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&blcBorderTop;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Real Estate Asset</h3>
							</td>
							<td class="&blcWidth75;">

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Name:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$pe-real-estate-asset/REAnameBlock/REAname"/>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:for-each select="$pe-real-estate-asset/REAaliasBlock">
									<xsl:sort select="./REAaliasSeq" order="ascending"/>
									<xsl:if test="./REAaliasType and ./REAaliasName">
										<xsl:call-template name="CreateTableRow" >
											<xsl:with-param name="RowLabel" select="concat(./REAaliasType,':')" />
											<xsl:with-param name="RowValue">
												<xsl:apply-templates select="./REAaliasName"/>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:if>
								</xsl:for-each>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Address:'" />
									<xsl:with-param name="RowValue">
										<xsl:if test="$pe-real-estate-asset/REAlocationBlock/REAaddressStreetBlock/REAaddressStreet1">
											<xsl:apply-templates select="$pe-real-estate-asset/REAlocationBlock/REAaddressStreetBlock/REAaddressStreet1"/>
											<br />
										</xsl:if>
										<xsl:if test="$pe-real-estate-asset/REAlocationBlock/REAaddressStreetBlock/REAaddressStreet2">
											<xsl:apply-templates select="$pe-real-estate-asset/REAlocationBlock/REAaddressStreetBlock/REAaddressStreet2"/>
											<br />
										</xsl:if>
										<xsl:choose>
											<xsl:when test="$pe-real-estate-asset/REATRCSstateBlock/REATRCSstate">
												<xsl:call-template name="join">
													<xsl:with-param name="nodes" select="$pe-real-estate-asset/REAlocationBlock/REAcity 
						| $pe-real-estate-asset/REAlocationBlock/REATRCSstateBlock/REATRCSstate 
						| $pe-real-estate-asset/REAlocationBlock/REAcountryBlock/REAcountry 
						| $pe-real-estate-asset/REAlocationBlock/REApostalCode" />
												</xsl:call-template>
											</xsl:when>
											<xsl:otherwise>
												<xsl:call-template name="join">
													<xsl:with-param name="nodes" select="$pe-real-estate-asset/REAlocationBlock/REAcity 
						| $pe-real-estate-asset/REAlocationBlock/REAlocalStateBlock/REAlocalState 
						| $pe-real-estate-asset/REAlocationBlock/REAcountryBlock/REAcountry 
						| $pe-real-estate-asset/REAlocationBlock/REApostalCode" />
												</xsl:call-template>
											</xsl:otherwise>
										</xsl:choose>

									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Status:'" />
									<xsl:with-param name="RowValue">
										<xsl:call-template	name="stringWithDate">
											<xsl:with-param name="prefix" select="$pe-real-estate-asset/REAstatusBlock/REAstatus" />
											<xsl:with-param name="date" select="$pe-real-estate-asset/REAstatusBlock/REAstatusDate" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="$pe-real-estate-asset/REAsectorBlock/REAsector/REAsector">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Type:'" />
										<xsl:with-param name="RowValue">
											<xsl:for-each select="$pe-real-estate-asset/REAsectorBlock/REAsector">
												<xsl:sort select="./REAsectorSeq" order="ascending"/>
												<xsl:if test="position() != 1">
													<br/>
												</xsl:if>
												<xsl:apply-templates select="./REAsector" />
											</xsl:for-each>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:if test="$pe-real-estate-asset/REAindBlock/REAindVEIC6Block/REAindVEIC6/REAindVEIC6">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Industry:'" />
										<xsl:with-param name="RowValue">
											<xsl:for-each select="$pe-real-estate-asset/REAindBlock/REAindVEIC6Block/REAindVEIC6">
												<xsl:sort select="./REAindVEIC6Seq" order="ascending"/>
												<xsl:if test="position() != 1">
													<br/>
												</xsl:if>
												<xsl:apply-templates select="./REAindVEIC6" />
											</xsl:for-each>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Fund Section -->

	<xsl:template name="Fund">
		<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='FND']">
			<xsl:call-template name="TOC">
				<xsl:with-param name="cd" select="'FND'"/>
			</xsl:call-template>

			<div id="co_peDeals_fund">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&blcBorderTop;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Fund</h3>
							</td>
							<td class="&blcWidth75;">

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Name:'" />
									<xsl:with-param name="RowValue" >
										<xsl:choose>
											<xsl:when test="$pe-portfolio-fund/PFfundNameBlock/PFfundName and string-length($pe-portfolio-fund/PFfundNameBlock/PFfundName) > 0">
												<xsl:apply-templates select="$pe-portfolio-fund/PFfundNameBlock/PFfundName"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>Unspecified</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:for-each select="$pe-portfolio-fund/PFfundAliasBlock">
									<xsl:sort select="./PFfundAliasSequence" order="ascending"/>
									<xsl:if test="./PFfundAliasType and string-length(./PFfundAliasType) > 0">
										<xsl:call-template name="CreateTableRow" >
											<xsl:with-param name="RowLabel" select="concat(./PFfundAliasType,':')" />
											<xsl:with-param name="RowValue">
												<xsl:apply-templates select="./PFfundAliasName"/>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:if>
								</xsl:for-each>

								<xsl:if test="$pe-portfolio-fund/PFfundAFKblock/PFfundAFKtype and string-length($pe-portfolio-fund/PFfundAFKblock/PFfundAFKtype) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="concat($pe-portfolio-fund/PFfundAFKblock/PFfundAFKtype,':')" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-fund/PFfundAFKblock/PFfundAFKname"/>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Vintage Year:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$pe-portfolio-fund/PFfundVintageYear"/>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Location:'" />
									<xsl:with-param name="RowValue">
										<xsl:if test="//PFfundCountry">
											<xsl:call-template name="join">
												<xsl:with-param name="nodes" select="//PFfundCity | //PFfundLocalState  | //PFfundCountry" />
											</xsl:call-template>
										</xsl:if>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:if test="string-length($pe-portfolio-fund/PFfundOwnershipBlock/PFfundOwnership) > 0">
									<xsl:call-template name="CreateTableRow" >
										<xsl:with-param name="RowLabel" select="'Ownership:'" />
										<xsl:with-param name="RowValue">
											<xsl:apply-templates select="$pe-portfolio-fund/PFfundOwnershipBlock/PFfundOwnership"/>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:if>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Fund Group Type:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$pe-portfolio-fund/PFfundGroupTypeBlock/PFfundGroupType"/>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Fund Type:'" />
									<xsl:with-param name="RowValue">
										<xsl:apply-templates select="$pe-portfolio-fund/PFfundTypeBlock/PFfundType"/>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="CreateTableRow" >
									<xsl:with-param name="RowLabel" select="'Main or Side Fund:'" />
									<xsl:with-param name="RowValue">
										<xsl:choose>
											<xsl:when test="$pe-portfolio-fund/PFfundMainOrSide = 'Yes'">
												<xsl:value-of select="'Side'" />
											</xsl:when>
											<xsl:when test="$pe-portfolio-fund/PFfundMainOrSide = 'No'">
												<xsl:value-of select="'Main'" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="''" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
								</xsl:call-template>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Investment Round Block -->

	<xsl:template name="InvestmentRoundBlock">

		<div id="co_peDeals_investmentRounds">
			<xsl:if test="$xpath-root/*[starts-with(name(), 'section.')][@cd='IRS']">
				<xsl:call-template name="TOC">
					<xsl:with-param name="cd" select="'IRS'"/>
				</xsl:call-template>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&blcBorderTop;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Investment Rounds</h3>
							</td>
							<td class="&blcWidth75;"></td>
						</tr>
					</tbody>
				</table>
			</xsl:if>

			<xsl:for-each select="//investmentRound">
				<xsl:if test="./preceding::*[position() = 1][starts-with(name(), 'section.')]">
					<xsl:call-template name="TOC">
						<xsl:with-param name="cd" select="concat('IR',./IRround)"/>
					</xsl:call-template>
					<xsl:call-template name="investmentRound"/>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template name="investmentRound">
		<xsl:variable name="roundDate">
			<xsl:if test="./IRdate">
				<xsl:value-of select="' - '"/>
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="date" select="./IRdate" />
					<xsl:with-param name="displayDay" select="true()" />
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>
		<table class="&layout_table; &blcPortfolioTable;">
			<tbody>
				<tr>
					<td class="&blcWidth25; &blcSectionHeading;">
						<h3>
							<xsl:value-of select="concat('Round ', ./IRround, $roundDate)"/>
						</h3>
					</td>
					<td class="&blcWidth75;">
						<xsl:call-template name="createPortfolioCompanyTable" />
						<xsl:call-template name="createFinancingTable" />
						<xsl:call-template name="createFundsBlockTable" />
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<!-- Valuation-->

	<xsl:template name="createPortfolioCompanyTable">
		<h4 class="&blcSectionSubheading;">Valuation</h4>
		<xsl:call-template name="IRoperatingStageBlock" />
		<xsl:call-template name="IRageAtRound" />
		<xsl:call-template name="IRpreRoundValue" />
		<xsl:call-template name="IRpostRoundValueBlock" />
		<xsl:call-template name="IRpostRoundValuationSourceBlock" />
		<xsl:call-template name="IRprePostValueDifference" />
	</xsl:template>

	<xsl:template name="IRoperatingStageBlock">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Operating Stage:'" />
			<xsl:with-param name="RowValue">
				<xsl:call-template	name="stringWithDate">
					<xsl:with-param name="prefix" select="./IRoperatingStageBlock/IRoperatingStage" />
					<xsl:with-param name="date" select="./IRoperatingStageBlock/IRoperatingStageDate" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="IRageAtRound">
		<xsl:if test="./IRageAtRound">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Age:'" />
				<xsl:with-param name="RowValue">
					<xsl:choose>
						<xsl:when test="./IRageAtRound = 1">
							<xsl:value-of select="concat(./IRageAtRound, ' year')"/>
						</xsl:when>
						<xsl:when test="./IRageAtRound > 1 or ./IRageAtRound = 0">
							<xsl:value-of select="concat(./IRageAtRound, ' years')"/>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="IRpreRoundValue">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Pre-Round Value (USD):'" />
			<xsl:with-param name="RowValue">
				<xsl:call-template name="FormatCurrancy">
					<xsl:with-param name="value" select="./IRpreRoundValue" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="IRpostRoundValueBlock">
		<xsl:choose>
			<xsl:when test="./IRpostRoundValueBlock/IRpostRoundValueConfidential = 'Confidential' or ./IRpostRoundValueBlock/IRpostRoundValueConfidential = 'confidential'">
				<xsl:call-template name="CreateTableRow" >
					<xsl:with-param name="RowLabel" select="'Post-Round Value (USD):'" />
					<xsl:with-param name="RowValue" select="'Confidential'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="CreateTableRow" >
					<xsl:with-param name="RowLabel" select="'Post-Round Value (USD):'" />
					<xsl:with-param name="RowValue">
						<xsl:call-template name="FormatCurrancy">
							<xsl:with-param name="value" select="./IRpostRoundValueBlock/IRpostRoundValue" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="IRpostRoundValuationSourceBlock">
		<xsl:if test="string-length(./IRpostRoundValuationSourceBlock/IRpostRoundValuationSource) > 0">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Valuation Source:'" />
				<xsl:with-param name="RowValue">
					<xsl:apply-templates select="./IRpostRoundValuationSourceBlock/IRpostRoundValuationSource" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--TODO: should this be Confidential of the Post Round Value is Confidential?-->

	<xsl:template name="IRprePostValueDifference">
		<xsl:if test="./IRprePostValueDifference > 0">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Difference in Valuation(Pre/Post Round):'" />
				<xsl:with-param name="RowValue">
					<xsl:call-template name="FormatCurrancy">
						<xsl:with-param name="value" select="./IRprePostValueDifference" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--Financing Table-->

	<xsl:template name="createFinancingTable">
		<h4 class="&blcSectionSubheading;">Financing</h4>
		<xsl:call-template name="IRfinancingStageBlock" />
		<xsl:call-template name="IRnumberOfFunds" />
		<xsl:call-template name="IRtotalFinancing" />
		<xsl:call-template name="IRtotalEquityFinancing" />
		<xsl:call-template name="IRtotalDebtFinancing" />
		<xsl:call-template name="IRbuyoutValue" />
	</xsl:template>

	<xsl:template name="IRfinancingStageBlock">
		<xsl:if test="./IRfinancingStageBlock/IRfinancingStage and string-length(./IRfinancingStageBlock/IRfinancingStage) > 0">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Financing Stage:'" />
				<xsl:with-param name="RowValue">
					<xsl:apply-templates select="./IRfinancingStageBlock/IRfinancingStage"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="IRnumberOfFunds">
		<xsl:if test="./IRnumberOfFunds and string-length(./IRnumberOfFunds) > 0">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Number of Funds:'" />
				<xsl:with-param name="RowValue"  >
					<xsl:apply-templates select="./IRnumberOfFunds"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="IRtotalFinancing">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Total Financing (USD):'" />
			<xsl:with-param name="RowValue">
				<xsl:call-template name="FormatCurrancy">
					<xsl:with-param name="value" select="./IRtotalFinancing" />
					<xsl:with-param name="skipZero" select="false()"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="skipZero" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="IRtotalEquityFinancing">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Non-Confidential Equity (USD):'" />
			<xsl:with-param name="RowValue">
				<xsl:call-template name="FormatCurrancy">
					<xsl:with-param name="value" select="./IRtotalEquityFinancing" />
					<xsl:with-param name="skipZero" select="false()"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="NAtoZero" select="true()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="IRtotalDebtFinancing">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Disclosed Debt (USD):'" />
			<xsl:with-param name="RowValue">
				<xsl:call-template name="FormatCurrancy">
					<xsl:with-param name="value" select="./IRtotalDebtFinancing" />
					<xsl:with-param name="format" select="'###,##0'"/>
					<xsl:with-param name="skipZero" select="false()"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="NAtoZero" select="true()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="IRbuyoutValue">
		<xsl:if test="./IRbuyoutValue and not(./IRbuyoutValue = 0)">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Buyout (USD):'" />
				<xsl:with-param name="RowValue">
					<xsl:call-template name="FormatCurrancy">
						<xsl:with-param name="value" select="./IRbuyoutValue" />
						<xsl:with-param name="format" select="'###,##0'"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--Funds Block Table-->

	<xsl:template name="createFundsBlockTable" >
		<xsl:if test="./IRfundBlock/IRfund">
			<table class="&layout_table; &layout_6Columns; &extraPaddingClass;">
				<tr>
					<td class="&layout_col1;">
						<b>Fund Name</b>
					</td>
					<td class="&layout_col2;">
						<b>Firm Name</b>
					</td>
					<td class="&layout_col3;">
						<b>Equity (USD)</b>
					</td>
					<td class="&layout_col4;">
						<b>Debt (USD)</b>
					</td>
					<td class="&layout_col5;">
						<b>Security</b>
					</td>
					<td class="&layout_col6;" style="width: 100px;">
						<b>Fund Info.</b>
					</td>
				</tr>
				<xsl:variable name="roundPosition" select="position()"/>
				<xsl:for-each select="./IRfundBlock/IRfund">
					<xsl:call-template name="IRfund">
						<xsl:with-param name="roundPosition" select="$roundPosition"/>
					</xsl:call-template>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="IRfund">
		<xsl:param name="roundPosition"/>
		<tr>
			<xsl:call-template name="IRfundName" />
			<xsl:call-template name="IRfirmName" />
			<xsl:call-template name="IRequity">
				<xsl:with-param name="roundPosition" select="$roundPosition"/>
			</xsl:call-template>
			<xsl:call-template name="IRdebt" />
			<xsl:call-template name="IRsecurityBlock" />
			<xsl:call-template name="TypeAndStatusBlock" />
		</tr>
	</xsl:template>

	<xsl:template name="IRfundName">
		<td class="&layout_col1;">
			<xsl:if test="./IRfundNameBlock/IRfundName and string-length(./IRfundNameBlock/IRfundName) > 0">
				<xsl:call-template name ="VerifyStringValue">
					<xsl:with-param name="stringValue">
						<xsl:apply-templates select="./IRfundNameBlock/IRfundName"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</td>
	</xsl:template>

	<xsl:template name="IRfirmName">
		<td class="&layout_col2;">
			<xsl:if test="./IRfirmNameBlock/IRfirmName and string-length(./IRfirmNameBlock/IRfirmName) > 0">
				<xsl:call-template name ="VerifyStringValue">
					<xsl:with-param name="stringValue">
						<xsl:apply-templates select="./IRfirmNameBlock/IRfirmName"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</td>
	</xsl:template>


	<!--TODO: if 392, need to mark as estimated.  If what is 392?-->

	<xsl:template name="IRequity">
		<xsl:param name="roundPosition"/>
		<td class="&layout_col3; &textRightClass;">
			<xsl:call-template name ="VerifyStringValue">
				<xsl:with-param name="stringValue">
					<xsl:choose>
						<xsl:when test="//investmentRound[$roundPosition]/IRfundEquityTotalConfidential='Confidential' or //investmentRound[$roundPosition]/IRfundEquityTotalConfidential='confidential'">
							<xsl:value-of select="'Confidential'"/>
						</xsl:when>
						<xsl:when test="(./IRequity = '0' or not(string-length(./IRequity) > 0)) and (./IRdebt = '0' or not(string-length(./IRdebt) > 0))">
							<xsl:value-of select="$nd-answer"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatCurrancy">
								<xsl:with-param name="value" select="./IRequity" />
								<xsl:with-param name="currancy" select="''"/>
								<xsl:with-param name="format" select="'###,##0'"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="skipZero" select="false()" />
				<xsl:with-param name="NAtoZero" select="true()" />
			</xsl:call-template>
		</td>
	</xsl:template>

	<xsl:template name="IRdebt">
		<td class="&layout_col4; &textRightClass;">
			<xsl:call-template name ="VerifyStringValue">
				<xsl:with-param name="stringValue">
					<xsl:choose>
						<xsl:when test="(./IRequity = '0' or not(string-length(./IRequity) > 0)) and (./IRdebt = '0' or not(string-length(./IRdebt) > 0))">
							<xsl:value-of select="$nd-answer"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatCurrancy">
								<xsl:with-param name="value" select="./IRdebt" />
								<xsl:with-param name="currancy" select="''"/>
								<xsl:with-param name="format" select="'###,##0'"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="skipZero" select="false()" />
				<xsl:with-param name="NAtoZero" select="true()" />
			</xsl:call-template>
		</td>
	</xsl:template>


	<!--TODO: Are the codes needed?-->
	<!--TODO: Do we need to make sure each field exists before trying to place it?-->

	<xsl:template name="IRsecurityBlock">
		<td class="&layout_col5;">
			<xsl:if test="./IRsecurityBlock/IRprimarySecurityBlock/IRprimarySecurity 
				and string-length(./IRsecurityBlock/IRprimarySecurityBlock/IRprimarySecurity) > 0">
				<xsl:apply-templates select="./IRsecurityBlock/IRprimarySecurityBlock/IRprimarySecurity" />
				<xsl:value-of select="' - Primary'" />
				<br />
			</xsl:if>

			<xsl:if test="./IRsecurityBlock/IRsecondarySecurityBlock/IRsecondarySecurity
				and string-length(./IRsecurityBlock/IRsecondarySecurityBlock/IRsecondarySecurity) > 0">
				<xsl:apply-templates select="./IRsecurityBlock/IRsecondarySecurityBlock/IRsecondarySecurity" />
				<xsl:value-of select="' - Secondary'" />
				<br />
			</xsl:if>

			<xsl:if test="./IRsecurityBlock/IRtertiarySecurityBlock/IRtertiarySecurity 
				and string-length(./IRsecurityBlock/IRtertiarySecurityBlock/IRtertiarySecurity) > 0">
				<xsl:apply-templates  select="./IRsecurityBlock/IRtertiarySecurityBlock/IRtertiarySecurity" />
				<xsl:value-of  select="' - Tertiary'" />
			</xsl:if>
		</td>
	</xsl:template>

	<xsl:template name="TypeAndStatusBlock">
		<td class="&layout_col6;">
			<xsl:call-template name="IRleadInvestorFlag" />
			<xsl:call-template name="IRIsStdVCInvestor" />
			<xsl:call-template name="IRinPortfolioFlag" />
		</td>
	</xsl:template>

	<xsl:template name="IRleadInvestorFlag">
		<xsl:if test="translate(./IRleadInvestorFlag, 'Y', 'y') = 'y' or translate(./IRleadInvestorFlag, 'YES', 'yes') = 'yes'">
			<xsl:text>Lead Investor</xsl:text>
			<br />
		</xsl:if>
	</xsl:template>


	<!--TODO: make sure that if the value actually is "no", do not display-->

	<xsl:template name="IRIsStdVCInvestor" >
		<xsl:if test="./IRVCOrBuyoutBlock/IRIsStdVCInvestor">
			<xsl:apply-templates select="./IRVCOrBuyoutBlock/IRIsStdVCInvestor"/>
			<br />
		</xsl:if>
		<xsl:if test="./IRVCOrBuyoutBlock/IRIsStdBuyoutInvestor">
			<xsl:apply-templates select="./IRVCOrBuyoutBlock/IRIsStdBuyoutInvestor"/>
			<br />
		</xsl:if>
	</xsl:template>

	<xsl:template name="IRinPortfolioFlag">
		<xsl:call-template name="convertPortfolioFlag">
			<xsl:with-param name="value" select="./IRinPortfolioFlagBlock/IRinPortfolioFlag" />
		</xsl:call-template>
	</xsl:template>


	<!--Exit Block. Label depends on type of Deal-->

	<xsl:template name="Exit">
		<xsl:if test="//exit">
			<div id="co_peDeals_exit">
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&blcBorderTop;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:choose>
										<xsl:when test="count(//exit) > 1">
											<xsl:text>Exits</xsl:text>
										</xsl:when>
										<xsl:when test="count(//exit) = 1">
											<xsl:text>Exit</xsl:text>
										</xsl:when>
									</xsl:choose>
								</h3>
							</td>
							<td class="&blcWidth75;">
								<xsl:for-each select="//exit">
									<xsl:if test="./preceding::*[position() = 1][starts-with(name(), 'section.')]">
										<xsl:call-template name="TOC">
											<xsl:with-param name="cd" select="./preceding::*[position() = 1][starts-with(name(), 'section.')]/@cd" />
										</xsl:call-template>
										<xsl:call-template name="exitType" />
										<xsl:call-template name="exitDate" />
										<xsl:call-template name="exitAmtDivestedAtCost" />
										<xsl:call-template name="exitParticipantBlock" />
									</xsl:if>
								</xsl:for-each>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:if>
	</xsl:template>


	<!--TODO: possible to have more than one. display most recent second? are we assuming there are only 2 or can the order be: 1, 5, 2, 3, 4?-->

	<xsl:template name="exitType">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Type:'" />
			<xsl:with-param name="RowValue">
				<xsl:apply-templates select="./exitTypeBlock/exitType" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>


	<!--TODO: possible to have more than one. display most recent second? are we assuming there are only 2 or can the order be: 1, 5, 2, 3, 4?-->

	<xsl:template name="exitDate">
		<xsl:call-template name="CreateTableRow" >
			<xsl:with-param name="RowLabel" select="'Date:'" />
			<xsl:with-param name="RowValue">
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="date" select="./exitTypeBlock/exitDate" />
					<xsl:with-param name="displayDay" select="true()" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="exitAmtDivestedAtCost">
		<xsl:if test="./exitAmtDivestedAtCost">
			<xsl:call-template name="CreateTableRow" >
				<xsl:with-param name="RowLabel" select="'Amount Divested at Cost (USD):'" />
				<xsl:with-param name="RowValue">
					<xsl:call-template name="FormatCurrancy">
						<xsl:with-param name="value" select="./exitAmtDivestedAtCost" />
						<xsl:with-param name="format" select="'###,##0'"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="exitParticipantBlock">
		<xsl:if test="./exitParticipantBlock">
			<table class="&blcNestedTable;">
				<tr>
					<td>
						<xsl:call-template name="DisplayLabel">
							<xsl:with-param name="text" select="'Participants:'" />
						</xsl:call-template>
					</td>
					<td class="&blcNestedTableCell;">
						<xsl:for-each select="./exitParticipantBlock">
							<div>
								<xsl:if test="./exitFundParticipantBlock/exitFundParticipantName or ./exitFirmParticipantBlock/exitFirmParticipantName">
									<xsl:apply-templates select="./exitFundParticipantBlock/exitFundParticipantName"/>
									<xsl:if test="./exitFundParticipantBlock/exitFundParticipantName and ./exitFirmParticipantBlock/exitFirmParticipantName">
										<xsl:value-of select="' - '"/>
									</xsl:if>
									<xsl:apply-templates select="./exitFirmParticipantBlock/exitFirmParticipantName"/>
								</xsl:if>
							</div>
						</xsl:for-each>
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Add the Internal TOC Anchor links.  This is also needed in the delivered docs for N&H! -->
	<xsl:template name="TOC">
		<xsl:param name="cd" />
		<xsl:element name="span">
			<xsl:attribute name="id">
				<xsl:text>co_internalToc_</xsl:text>
				<xsl:value-of select="$cd"/>
			</xsl:attribute>
			<xsl:attribute name="class">co_internalTocMarker</xsl:attribute>
			<xsl:text>&nbsp;</xsl:text>
		</xsl:element>
	</xsl:template>

	<!-- Helper method section -->
	<xsl:template name="CreateTable">
		<xsl:param name="TableTitle" select="." />

		<div class="&layoutHeaderRow;">
			<h3>
				<xsl:value-of select="$TableTitle"/>
			</h3>
		</div>
		<table class="&layout_table; &layout_2Columns;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template name="CreateTableRow">
		<xsl:param name="RowLabel" />
		<xsl:param name="RowValue" select="." />
		<xsl:param name="Truncate" />
		<xsl:param name="skipZero" select="true()" />
		<xsl:param name="NAtoZero" select="false()" />

		<div>
			<xsl:if test="$Truncate=string(true())">
				<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$RowLabel" />
			</xsl:call-template>
			<xsl:call-template name ="VerifyStringValue">
				<xsl:with-param name="stringValue" select="$RowValue"/>
				<xsl:with-param name="skipZero" select="$skipZero"/>
				<xsl:with-param name="NAtoZero" select="$NAtoZero"/>
			</xsl:call-template>
		</div>

		<xsl:if test="$Truncate=string(true())">
			<xsl:call-template name="MoreLink">
				<xsl:with-param name="count" select="string-length($RowValue)" />
				<xsl:with-param name="threshold" select="1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--VerifyStringValue-->
	<xsl:template name="VerifyStringValue">
		<xsl:param name="stringValue"/>
		<xsl:param name="skipZero" select="true()" />
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
			<xsl:when test="string-length($stringValue) > 0 and not($stringValue='NA') and not($stringValue='NaN') and not($stringValue='0')">
				<xsl:copy-of select="$stringValue" />
			</xsl:when>
			<xsl:when test="($stringValue = '0' and not($skipZero)) or 
				(($stringValue='NA' or string-length($stringValue) = 0) and $NAtoZero=true())">
				<xsl:value-of select="'0'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="CreateExitTable">
		<xsl:param name="TableName"/>
		<xsl:param name="FundName" />
		<xsl:param name="FirmName" />

		<div class="&layoutHeaderRow;">
			<b>
				<xsl:value-of select="$TableName"/>
			</b>
		</div>
		<table class="&layout_table; &layout_2Columns;">
			<tr>
				<td class="&layout_col1;">
					<b>Fund</b>
				</td>
				<td class="&layout_col2;">
					<b>Firm</b>
				</td>
			</tr>
			<tr>
				<td class="&layout_col1;">
					<xsl:copy-of select="$FundName"/>
				</td>
				<td class="&layout_col2;">
					<xsl:copy-of select="$FirmName"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="join">
		<xsl:param name="nodes" />
		<xsl:param name="separator" select="', '" />

		<xsl:for-each select="$nodes">
			<xsl:apply-templates />
			<xsl:if test="position() &lt; last()">
				<xsl:value-of select="$separator"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="convertPortfolioFlag">
		<xsl:param name="value" select="." />

		<xsl:choose>
			<xsl:when test="translate($value, 'Y', 'y') = 'y' or translate($value, 'YES', 'yes') = 'yes'">
				<xsl:text>Still Invested</xsl:text>
			</xsl:when>
			<xsl:when test="translate($value, 'N', 'n') = 'n' or translate($value, 'NO', 'no') = 'no'">
				<xsl:text>No Longer Invested</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

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
		<xsl:param name="value6"/>
		<xsl:param name="value7"/>
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
							<b>
								<xsl:copy-of select="$value5" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value6">
						<td class="&layout_col6;">
							<b>
								<xsl:copy-of select="$value6" />
							</b>
						</td>
					</xsl:if>
					<xsl:if test="$value7 and $value7C">
						<td class="&layout_col7;">
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
							<xsl:copy-of select="$value5" />
						</td>
					</xsl:if>
					<xsl:if test="$value6">
						<td class="&layout_col6;">
							<xsl:copy-of select="$value6" />
						</td>
					</xsl:if>
					<xsl:if test="$value7 and $value7C">
						<td class="&layout_col7;">
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

	<xsl:template name="stringWithDate">
		<xsl:param name="prefix" />
		<xsl:param name="date" />

		<xsl:variable name="formattedDate">
			<xsl:call-template name="parseYearMonthDayDateFormat">
				<xsl:with-param name="date" select="$date" />
				<xsl:with-param name="displayDay" select="true()" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($prefix) > 0 and not($prefix='NA') and not($prefix='NaN') and not($prefix='0')">
				<xsl:apply-templates select="$prefix"/>
				<xsl:if test="string-length($date) > 0">
					<xsl:value-of select="' as of '"/>
					<xsl:value-of select="$formattedDate"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name ="concat">
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>
		<xsl:param name ="delimetr" select="' - '"/>

		<xsl:apply-templates select="$value1"/>
		<xsl:value-of select ="$delimetr"/>
		<xsl:apply-templates select="$value2"/>
	</xsl:template>

	<xsl:template name="FormatCurrancy">
		<xsl:param name="value" />
		<xsl:param name="format" select="'###,###'"/>
		<xsl:param name ="currancy" select="' (USD)'"/>
		<xsl:param name="skipZero" select="true()"/>

		<xsl:choose>
			<xsl:when test="string-length($value) > 0 and number($value) != 'NaN' and $value != '0'">
				<xsl:value-of select="concat(format-number($value, $format), $currancy)" />
			</xsl:when>
			<xsl:when test="$value = '0' and not($skipZero)">
				<xsl:value-of select="$value" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>