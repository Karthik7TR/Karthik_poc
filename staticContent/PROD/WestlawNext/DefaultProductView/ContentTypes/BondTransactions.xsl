<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!--
	This stylesheet transforms Filings & Disclosure documents from Novus XML
	into HTML for UI display.
-->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="Transactions.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<xsl:variable name ="issuer" select="Document/n-docbody/bondsDocument/issuer" />
	<xsl:variable name ="issuance" select="Document/n-docbody/bondsDocument/issuance" />
	<xsl:variable name ="principalOrCoupon" select="Document/n-docbody/bondsDocument/principalOrCoupon" />
	<xsl:variable name ="relatedParties" select="Document/n-docbody/bondsDocument/relatedParties" />
	<xsl:variable name ="prospectus" select="Document/n-docbody/bondsDocument/prospectus" />
	<xsl:variable name ="prospectusTermsAndConditions" select="Document/n-docbody/bondsDocument/prospectusTermsAndConditions" />
	<xsl:variable name="doNotDisplayValue" select="'~DoNotDisplay~'" />
	<xsl:variable name ="na-answer" select="'N/A'" />

	<xsl:template match="Document">
		<!-- put doc content on display -->
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeBondTransactions;'"/>
			</xsl:call-template>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="Content"/>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!--
		**************************************************************************************
		*
		*		Render the XML for content.  Call templates to render each of the sections.      *
		**************************************************************************************
	-->
	<xsl:template name="Content">
		<xsl:call-template name="IssuerInformationSection"/>
		<xsl:call-template name="IssuanceSection"/>
		<xsl:call-template name="PrincipalCouponSection"/>
		<xsl:call-template name="RelatedPartiesSection"/>
		<xsl:call-template name="ProspectusSection"/>
		<xsl:call-template name="ProspectusTermsAndConditionsSection" />
	</xsl:template>

	<!-- ISSUER INFORMATION -->
	<xsl:template name="IssuerInformationSection">

		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Issuer</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Name-->
							<xsl:call-template name="DisplayOneValueOrTheOther">
								<xsl:with-param name="test" select="string-length($issuer/WCAName) != 0" />
								<xsl:with-param name="label" select="'Name:'" />
								<xsl:with-param name="value1" select="$issuer/WCAName" />
								<xsl:with-param name="value2" select="$issuer/name" />
							</xsl:call-template>

							<!--Ticker Symbol-->
							<xsl:call-template name="DisplayOneValueOrTheOther">
								<xsl:with-param name="test" select="string-length($issuer/WCATickerSymbolBlock/tickerSymbol) != 0" />
								<xsl:with-param name="label" select="'Ticker Symbol:'" />
								<xsl:with-param name="value1" select="$issuer/WCATickerSymbolBlock/tickerSymbol" />
								<xsl:with-param name="value2" select="$issuer/tickerSymbol" />
							</xsl:call-template>

							<!--Location of Headquarters-->
							<xsl:variable name="location">
								<xsl:call-template name="TwoDelimiterSeperatedStringValues">
									<xsl:with-param name="value1" select="$issuer/locationHeadquartersCity" />
									<xsl:with-param name="value2" select="$issuer/locationHeadquartersCountry" />
									<xsl:with-param name="delimiter" select="', '" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Location of Headquarters:'" />
								<xsl:with-param name="param2" select="string-length($location) &gt; 0" />
								<xsl:with-param name="param3" select="$location" />
							</xsl:call-template>

							<!--Location of Incorporation-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Location of Incorporation:'" />
								<xsl:with-param name="param2" select="string-length($issuer/locationIncorporation) &gt; 0" />
								<xsl:with-param name="param3" select="$issuer/locationIncorporation" />
							</xsl:call-template>

							<!--Organization Type-->
							<xsl:call-template name="DisplayOneValueOrTheOther">
								<xsl:with-param name="test" select="string-length($issuer/WCAOrganizationType) != 0" />
								<xsl:with-param name="label" select="'Organization Type:'" />
								<xsl:with-param name="value1" select="$issuer/WCAOrganizationType" />
								<xsl:with-param name="value2" select="$issuer/organizationType" />
							</xsl:call-template>

							<!--Parent Company-->
							<xsl:call-template name="DisplayOneValueOrTheOther">
								<xsl:with-param name="test" select="string-length($issuer/WCAParentCompany) != 0" />
								<xsl:with-param name="label" select="'Parent Company:'" />
								<xsl:with-param name="value1" select="$issuer/WCAParentCompany" />
								<xsl:with-param name="value2" select="$issuer/parentCompany" />
							</xsl:call-template>

							<!--SIC Code & Description-->
							<table class="&blcNestedTable;">
								<tr>
									<td>
										<xsl:call-template name="DisplayLabel">
											<xsl:with-param name="text" select="'SIC Code &amp; Description:'" />
										</xsl:call-template>
									</td>
									<td class="&blcNestedTableCell;">
										<xsl:for-each select="$issuer/SICCodeBlock">
											<div>
												<xsl:if test="position() > $extraThreshold">
													<xsl:if test="not($DeliveryMode)">
														<xsl:attribute name="class">
															<xsl:value-of select="$extraItem"/>
														</xsl:attribute>
													</xsl:if>
												</xsl:if>

												<xsl:call-template name="VerifyStringValue">
													<xsl:with-param name="ifValidString" select="string-length(SICDescription) &gt; 0"/>
													<xsl:with-param name="stingValue" select="concat(SICCode, ' - ', SICDescription)"/>
												</xsl:call-template>
											</div>
										</xsl:for-each>
									</td>
								</tr>
							</table>
							<xsl:if test="not($DeliveryMode)">
								<xsl:call-template name="MoreLink">
									<xsl:with-param name="count" select="count($issuer/SICCodeBlock)" />
									<xsl:with-param name="threshold" select="$extraThreshold" />
								</xsl:call-template>
							</xsl:if>

							<!--Business Description-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Business Description:'"/>
								<xsl:with-param name="param2" select="string-length($issuer/businessDescription) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuer/businessDescription"/>
								<xsl:with-param name="truncate" select="not($DeliveryMode)" />
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!-- ISSUANCE-->
	<xsl:template name="IssuanceSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Issuance</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Issue Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Issue Date:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/issueDate) &gt; 7	and number($issuance/issueDate) != 'NaN'"/>
								<xsl:with-param name="param3" select="$issuance/issueDate"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Maturity Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Maturity Date:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/maturityDate) &gt; 7	and number($issuance/maturityDate) != 'NaN'"/>
								<xsl:with-param name="param3" select="$issuance/maturityDate"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Status-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Status:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/currentStatusBlock/status) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/currentStatusBlock/status"/>
							</xsl:call-template>

							<!-- Secured-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Secured:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/securedFlag) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/securedFlag"/>
							</xsl:call-template>

							<!--Offering Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Offering Type:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/offeringTypeBlock/offeringType) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/offeringTypeBlock/offeringType"/>
							</xsl:call-template>

							<!--Instrument Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Instrument Type:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/instrumentTypeBlock/instrumentType) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/instrumentTypeBlock/instrumentType"/>
							</xsl:call-template>

							<!--Seniority-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Seniority:'" />
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/seniorityBlock/seniority) &gt; 0" />
								<xsl:with-param name="param3" select="$principalOrCoupon/seniorityBlock/seniority" />
							</xsl:call-template>

							<!--Program-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Program:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/programBlock/program) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/programBlock/program"/>
							</xsl:call-template>

							<!--Target Market-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Target Market:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/targetMarket) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/targetMarket"/>
							</xsl:call-template>

							<!--Grade-->
							<table class="&blcNestedTable;">
								<tr>
									<td>
										<xsl:call-template name="DisplayLabel">
											<xsl:with-param name="text" select="'Characteristics:'" />
										</xsl:call-template>
									</td>
									<td class="&blcNestedTableCell;">
										<xsl:for-each select="$issuance/bondGradeRCSBlock">
											<div>
												<xsl:call-template name="VerifyStringValue">
													<xsl:with-param name="ifValidString" select="string-length(bondRCSGrade) &gt; 0"/>
													<xsl:with-param name="stingValue" select="bondRCSGrade"/>
												</xsl:call-template>
											</div>
										</xsl:for-each>
									</td>
								</tr>
							</table>

							<!--Ownership Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Ownership Type:'"/>
								<xsl:with-param name="param2" select="string-length($issuance/ownershipTypeBlock/ownershipType) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuance/ownershipTypeBlock/ownershipType"/>
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!-- PRINCIPAL / COUPON -->
	<xsl:template name="PrincipalCouponSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Principal / Coupon</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Total Amount Issued-->
							<xsl:variable name="currencyCodeTAI" select="$principalOrCoupon/currencyBlock/currencyCode" />
							<xsl:variable name="totalAmountIssuedValue">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($principalOrCoupon/totalAmountIssuedBlock/totalAmountIssued) = 0" />
									<xsl:with-param name="doesNotExist" select="$principalOrCoupon/totalAmountIssuedBlock/totalAmountIssued=0" />
									<xsl:with-param name="isNumber" select="string(number($principalOrCoupon/totalAmountIssuedBlock/totalAmountIssued)) != 'NaN'" />
									<xsl:with-param name="value" select="concat(format-number($principalOrCoupon/totalAmountIssuedBlock/totalAmountIssued, '#,###'), ' ', $currencyCodeTAI)" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Amount Issued:'"/>
								<xsl:with-param name="param2" select="string-length($totalAmountIssuedValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$totalAmountIssuedValue"/>
							</xsl:call-template>

							<!--Total Amount Issued (USD)-->
							<xsl:variable name="valueText">
								<xsl:choose>
									<xsl:when test="$principalOrCoupon/currencyBlock/currencyCode != 'USD'">
										<xsl:variable name="exchangeRateDateTAI">
											<xsl:call-template name="FormatDateValue">
												<xsl:with-param name="ifValidDate" select="string-length($principalOrCoupon/currentAmountOutstandingBlock/exchangeRateDate) &gt; 7	and number($principalOrCoupon/currentAmountOutstandingBlock/exchangeRateDate) != 'NaN'"/>
												<xsl:with-param name="dateValue" select="$principalOrCoupon/currentAmountOutstandingBlock/exchangeRateDate"/>
												<xsl:with-param name="yearFirst" select="1"/>
											</xsl:call-template>
										</xsl:variable>
										<xsl:variable name="exchangeRateTAI">
											<xsl:call-template name="VerifyStringValue">
												<xsl:with-param name="ifValidString" select="string-length($principalOrCoupon/currentAmountOutstandingBlock/exchangeRate) &gt; 0"/>
												<xsl:with-param name="stingValue" select="$principalOrCoupon/currentAmountOutstandingBlock/exchangeRate "/>
											</xsl:call-template>
										</xsl:variable>
										<xsl:value-of select="concat(' USD based on exchange rate of ', $exchangeRateTAI, ' on ', $exchangeRateDateTAI)"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="' USD'"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:variable name="totalAmountIssuedUSValue">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($principalOrCoupon/totalAmountIssuedBlock/totalAmountIssuedUS) = 0"/>
									<xsl:with-param name="doesNotExist" select="$principalOrCoupon/totalAmountIssuedBlock/totalAmountIssuedUS=0"/>
									<xsl:with-param name="isNumber" select="string(number($principalOrCoupon/totalAmountIssuedBlock/totalAmountIssuedUS)) != 'NaN'"/>
									<xsl:with-param name="value" select="concat(format-number($principalOrCoupon/totalAmountIssuedBlock/totalAmountIssuedUS, '#,###'), $valueText)"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Amount Issued (USD):'"/>
								<xsl:with-param name="param2" select="string-length($totalAmountIssuedUSValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$totalAmountIssuedUSValue"/>
							</xsl:call-template>

							<!--Currency-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Currency:'"/>
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/currencyBlock/currency) &gt; 0"/>
								<xsl:with-param name="param3" select="$principalOrCoupon/currencyBlock/currency"/>
							</xsl:call-template>

							<!-- Overallotment-->
							<xsl:variable name="OverallotmentValue">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($issuance/overallotmentAmt) = 0"/>
									<xsl:with-param name="doesNotExist" select="$issuance/overallotmentAmt=0"/>
									<xsl:with-param name="isNumber" select="string(number($issuance/overallotmentAmt)) != 'NaN'"/>
									<xsl:with-param name="value" select="format-number($issuance/overallotmentAmt, '#,###')"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Overallotment:'"/>
								<xsl:with-param name="param2" select="string-length($OverallotmentValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$OverallotmentValue"/>
							</xsl:call-template>

							<!--Gross Spread % of Principal-->
							<xsl:variable name="GrossSpreadPrincipalValue">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($principalOrCoupon/grossSpreadPctOfPrincipal) = 0"/>
									<xsl:with-param name="doesNotExist" select="$principalOrCoupon/grossSpreadPctOfPrincipal=0"/>
									<xsl:with-param name="isNumber" select="string(number($principalOrCoupon/grossSpreadPctOfPrincipal)) != 'NaN'"/>
									<xsl:with-param name="value" select="concat(format-number($principalOrCoupon/grossSpreadPctOfPrincipal, '#,##0.0000'), ' %')"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Gross Spread % of Principal:'"/>
								<xsl:with-param name="param2" select="string-length($GrossSpreadPrincipalValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$GrossSpreadPrincipalValue"/>
							</xsl:call-template>

							<!--Amount Outstanding-->
							<xsl:variable name="currencyCodeAO" select="$principalOrCoupon/currencyBlock/currencyCode" />
							<xsl:variable name="AmountOutstandingValue">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($principalOrCoupon/currentAmountOutstandingBlock/amountOutstanding) = 0"/>
									<xsl:with-param name="doesNotExist" select="$principalOrCoupon/currentAmountOutstandingBlock/amountOutstanding=0"/>
									<xsl:with-param name="isNumber" select="string(number($principalOrCoupon/currentAmountOutstandingBlock/amountOutstanding)) != 'NaN'"/>
									<xsl:with-param name="value" select="concat(format-number($principalOrCoupon/currentAmountOutstandingBlock/amountOutstanding, '#,###'), ' ', $currencyCodeAO)"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Amount Outstanding:'"/>
								<xsl:with-param name="param2" select="string-length($AmountOutstandingValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$AmountOutstandingValue"/>
							</xsl:call-template>

							<!--Amount Outstanding (USD)-->
							<xsl:variable name="exchangeRateDateAOUS">
								<xsl:call-template name="FormatDateValue">
									<xsl:with-param name="ifValidDate" select="string-length($principalOrCoupon/currentAmountOutstandingBlock/exchangeRateDate) &gt; 7	and number($principalOrCoupon/currentAmountOutstandingBlock/exchangeRateDate) != 'NaN'"/>
									<xsl:with-param name="dateValue" select="$principalOrCoupon/currentAmountOutstandingBlock/exchangeRateDate"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:variable name="exchangeRateAOUS">
								<xsl:call-template name="VerifyStringValue">
									<xsl:with-param name="ifValidString" select="string-length($principalOrCoupon/currentAmountOutstandingBlock/exchangeRate) &gt; 0"/>
									<xsl:with-param name="stingValue" select="$principalOrCoupon/currentAmountOutstandingBlock/exchangeRate "/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:variable name="AmountOutstandingUSValue">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($principalOrCoupon/currentAmountOutstandingBlock/amountOutstandingUS) = 0"/>
									<xsl:with-param name="doesNotExist" select="$principalOrCoupon/currentAmountOutstandingBlock/amountOutstandingUS=0"/>
									<xsl:with-param name="isNumber" select="string(number($principalOrCoupon/currentAmountOutstandingBlock/amountOutstandingUS)) != 'NaN'"/>
									<xsl:with-param name="value" select="concat(format-number($principalOrCoupon/currentAmountOutstandingBlock/amountOutstandingUS, '#,###'), ' USD based on exchange rate of ', $exchangeRateAOUS, ' on ', $exchangeRateDateAOUS)"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Amount Outstanding (USD):'"/>
								<xsl:with-param name="param2" select="string-length($AmountOutstandingUSValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$AmountOutstandingUSValue"/>
							</xsl:call-template>

							<!--Percent Outstanding-->
							<xsl:variable name="PercentOutstandingValue">
								<xsl:choose>
									<xsl:when test="$AmountOutstandingValue =  $na-answer or $totalAmountIssuedValue = $na-answer">
										<xsl:value-of select="$na-answer"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$principalOrCoupon/currentAmountOutstandingBlock/amountOutstanding div $principalOrCoupon/totalAmountIssuedBlock/totalAmountIssued"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:variable name="PercentOutstandingDisplayValue">
								<xsl:choose>
									<xsl:when test="$PercentOutstandingValue = $na-answer">
										<xsl:value-of select="$PercentOutstandingValue"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="concat(format-number($PercentOutstandingValue * 100, '#,##0.0000'), ' %')"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Percent Outstanding:'"/>
								<xsl:with-param name="param2" select="string-length($PercentOutstandingDisplayValue) &gt; 0"/>
								<xsl:with-param name="param3" select="$PercentOutstandingDisplayValue"/>
							</xsl:call-template>

							<!--Coupon Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Coupon Type:'"/>
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/couponTypeBlock/couponType) &gt; 0"/>
								<xsl:with-param name="param3" select="$principalOrCoupon/couponTypeBlock/couponType"/>
							</xsl:call-template>

							<!--Current Coupon Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Current Coupon Type:'"/>
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/currentCouponBlock/currentCouponType) &gt; 0"/>
								<xsl:with-param name="param3" select="$principalOrCoupon/currentCouponBlock/currentCouponType"/>
							</xsl:call-template>

							<!--Float-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Float:'"/>
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/couponTypeBlock/floatFlag) &gt; 0"/>
								<xsl:with-param name="param3" select="$principalOrCoupon/couponTypeBlock/floatFlag"/>
							</xsl:call-template>

							<!--Current Coupon Rate-->
							<xsl:variable name="rateEffectiveDate">
								<xsl:call-template name="FormatDateValue">
									<xsl:with-param name="ifValidDate" select="string-length($principalOrCoupon/currentCouponRateBlock/rateEffectiveDate) &gt; 7	and number($principalOrCoupon/currentCouponRateBlock/rateEffectiveDate) != 'NaN'"/>
									<xsl:with-param name="dateValue" select="$principalOrCoupon/currentCouponRateBlock/rateEffectiveDate"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:variable name="couponRate">
								<xsl:call-template name="VerifyNumberValue">
									<xsl:with-param name="isBlank" select="string-length($principalOrCoupon/currentCouponRateBlock/currentCouponRate) = 0"/>
									<xsl:with-param name="doesNotExist" select="$principalOrCoupon/currentCouponRateBlock/currentCouponRate=0"/>
									<xsl:with-param name="isNumber" select="string(number($principalOrCoupon/currentCouponRateBlock/currentCouponRate)) != 'NaN'"/>
									<xsl:with-param name="value" select="format-number($principalOrCoupon/currentCouponRateBlock/currentCouponRate, '#,##0.0000')"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:variable name="currentCouponRate">
								<xsl:call-template name="TwoDelimiterSeperatedStringValues">
									<xsl:with-param name="value1" select="$couponRate"/>
									<xsl:with-param name="value1DisplayZero" select="$principalOrCoupon/currentCouponRateBlock/currentCouponRate=0"/>
									<xsl:with-param name="value2" select ="$rateEffectiveDate"/>
									<xsl:with-param name="delimiter" select="' as of '"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Current Coupon Rate:'"/>
								<xsl:with-param name="param2" select="string-length($currentCouponRate) &gt; 0"/>
								<xsl:with-param name="param3" select="$currentCouponRate"/>
							</xsl:call-template>

							<!--First Coupon Date-->
							<xsl:variable name="FirstCouponDate">
								<xsl:call-template name="FormatDateValue">
									<xsl:with-param name="ifValidDate" select="string-length($principalOrCoupon/firstCouponDate) &gt; 7	and number($principalOrCoupon/firstCouponDate) != 'NaN'"/>
									<xsl:with-param name="dateValue" select="$principalOrCoupon/firstCouponDate/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'First Coupon Date:'"/>
								<xsl:with-param name="param2" select="string-length($FirstCouponDate) &gt; 0"/>
								<xsl:with-param name="param3" select="$FirstCouponDate"/>
							</xsl:call-template>

							<!--Last Coupon Date-->
							<xsl:variable name="LastCouponDate">
								<xsl:call-template name="FormatDateValue">
									<xsl:with-param name="ifValidDate" select="string-length($principalOrCoupon/lastCouponDate) &gt; 7	and number($principalOrCoupon/lastCouponDate) != 'NaN'"/>
									<xsl:with-param name="dateValue" select="$principalOrCoupon/lastCouponDate/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Last Coupon Date:'"/>
								<xsl:with-param name="param2" select="string-length($LastCouponDate) &gt; 0"/>
								<xsl:with-param name="param3" select="$LastCouponDate"/>
							</xsl:call-template>

							<!--Coupon Frequency-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Coupon Frequency:'"/>
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/currentCouponBlock/couponFreq) &gt; 0"/>
								<xsl:with-param name="param3" select="$principalOrCoupon/currentCouponBlock/couponFreq"/>
							</xsl:call-template>

							<!--Use of Proceeds-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Use of Proceeds:'"/>
								<xsl:with-param name="param2" select="string-length($principalOrCoupon/useOfProceedsBlock/useOfProceeds) &gt; 0"/>
								<xsl:with-param name="param3" select="$principalOrCoupon/useOfProceedsBlock/useOfProceeds"/>
							</xsl:call-template>

							<!--Notes-->
							<table class="&layout_table; &blcPortfolioTable; &extraPaddingClass;">
								<xsl:for-each select="$principalOrCoupon/noteBlock/note">
									<xsl:sort select="normalize-space(substring(noteDate,0,4))" order="descending" />
									<xsl:sort select="normalize-space(substring(noteDate,5,2))" order="descending" />
									<xsl:sort select="normalize-space(substring(noteDate,7,2))" order="descending"/>
									<xsl:variable name="label">
										<xsl:choose>
											<xsl:when test="position() = 1">
												<xsl:value-of select="'Notes:'" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="''" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<xsl:variable name="noteDate">
										<xsl:call-template name="FormatDateValue">
											<xsl:with-param name="ifValidDate" select="string-length(noteDate) &gt; 7	and number(noteDate) != 'NaN'"/>
											<xsl:with-param name="dateValue" select="noteDate"/>
											<xsl:with-param name="yearFirst" select="1"/>
										</xsl:call-template>
									</xsl:variable>
									<tr>
										<td>
											<strong>
												<xsl:value-of select="$label"/>
											</strong>
										</td>
										<td>
											<xsl:value-of select="$noteDate"/>
										</td>
										<td>
											<xsl:value-of select="noteType"/>
										</td>
										<td>
											<div>
												<xsl:if test="not($DeliveryMode)">
													<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
												</xsl:if>
												<xsl:value-of select="noteDetail"/>
											</div>
											<xsl:if test="not($DeliveryMode)">
												<xsl:call-template name="MoreLink">
													<xsl:with-param name="count" select="1" />
													<xsl:with-param name="threshold" select="0" />
												</xsl:call-template>
											</xsl:if>
										</td>
									</tr>
								</xsl:for-each>
							</table>
							<xsl:if test="not($DeliveryMode)">
								<xsl:call-template name="MoreLink">
									<xsl:with-param name="count" select="string-length(noteDetail)" />
									<xsl:with-param name="threshold" select="1" />
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!-- RELATED PARTIES INFORMATION -->
	<xsl:template name="RelatedPartiesSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Related Parties</h3>
						</td>
						<td class="&blcWidth75;">

							<table class="&layout_table; &layout_3Columns; &extraPaddingClass;">

								<!--Law Firm-->
								<xsl:call-template name="LawFirm"/>

								<!--Guarantor-->
								<xsl:variable name="guarantorType">
									<xsl:choose>
										<xsl:when test="string-length($relatedParties/guarantorBlock/guarantorTypeBlock/guarantorType) &gt; 0">
											<xsl:value-of select="$relatedParties/guarantorBlock/guarantorTypeBlock/guarantorType"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$doNotDisplayValue"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<xsl:call-template name="DisplayThreeString">
									<xsl:with-param name="label" select="'Guarantor:'"/>
									<xsl:with-param name="value1Xpath" select="$guarantorType"/>
									<xsl:with-param name="value2Xpath" select="$relatedParties/guarantorBlock/GuarantorName"/>
								</xsl:call-template>

								<!--Escrow Account-->
								<xsl:call-template name="DisplayThreeString">
									<xsl:with-param name="label" select="'Escrow Account:'"/>
									<xsl:with-param name="value1Xpath" select="$doNotDisplayValue"/>
									<xsl:with-param name="value2Xpath" select="$relatedParties/guarantorBlock/escrowAccountFlag"/>
								</xsl:call-template>

								<!--Underwriter-->
								<xsl:call-template name="Underwriter"/>

							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!-- PROSPECTUS INFORMATION -->
	<xsl:template name="ProspectusSection">

		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Prospectus</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Filing Status-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Filing Status:'"/>
								<xsl:with-param name="param2" select="string-length($prospectus/filingStatusBlock/filingStatus) &gt; 0"/>
								<xsl:with-param name="param3" select="$prospectus/filingStatusBlock/filingStatus"/>
							</xsl:call-template>

							<!--Approval Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Approval Date:'"/>
								<xsl:with-param name="param2" select="string-length($prospectus/approvalDate) &gt; 7	and number($prospectus/approvalDate) != 'NaN'"/>
								<xsl:with-param name="param3" select="$prospectus/approvalDate/text()"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Stickered Supplement-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Stickered Supplement:'"/>
								<xsl:with-param name="param2" select="string-length($prospectus/stickeredSupplementFlag) &gt; 0"/>
								<xsl:with-param name="param3" select="$prospectus/stickeredSupplementFlag"/>
							</xsl:call-template>

							<!--Supplement Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Supplement Date:'"/>
								<xsl:with-param name="param2" select="string-length($prospectus/supplementDate) &gt; 7	and number($prospectus/supplementDate) != 'NaN'"/>
								<xsl:with-param name="param3" select="$prospectus/supplementDate/text()"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Supplement Number-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Supplement Number:'"/>
								<xsl:with-param name="param2" select="string-length($prospectus/supplementaNumber) = 0"/>
								<xsl:with-param name="param3" select="$prospectus/supplementaNumber=0"/>
								<xsl:with-param name="param4" select="string(number($prospectus/supplementaNumber)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($prospectus/supplementaNumber, '#,###.#####')"/>
							</xsl:call-template>

							<!--Shelf Prospectus Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Shelf Prospectus Date:'"/>
								<xsl:with-param name="param2" select="string-length($prospectus/shelfProspectusDate) &gt; 7	and number($prospectus/shelfProspectusDate) != 'NaN'"/>
								<xsl:with-param name="param3" select="$prospectus/shelfProspectusDate/text()"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>

	<!-- PROSPECTUS TERMS & CONDITIONS SECTION -->
	<xsl:template name="ProspectusTermsAndConditionsSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Prospectus Terms &amp; Conditions</h3>
						</td>
						<td class="&blcWidth75;">

							<h4 class="&blcSectionSubheading;">European Markets</h4>

							<div class="&layout_indentHeadings;">

								<!--Subject to EU Savings Directive Tax-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Subject to EU Savings Directive Tax:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/europeanMarketsBlock/subjectToEUSavingsTaxFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/europeanMarketsBlock/subjectToEUSavingsTaxFlag"/>
								</xsl:call-template>

								<!--Tapped (After February 29, 2002)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Tapped (After February 29, 2002):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/europeanMarketsBlock/tappedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/europeanMarketsBlock/tappedFlag"/>
								</xsl:call-template>
							</div>

							<h4 class="&blcSectionSubheading;">Interest</h4>

							<div class="&layout_indentHeadings;">

								<!--Compound or Simple-->
								<xsl:variable name="InterestValue">
									<xsl:choose>
										<xsl:when test="$prospectusTermsAndConditions/interestBlock/interestTypeFlag = 'Yes'">
											<xsl:value-of select="'Simple'"/>
										</xsl:when>
										<xsl:when test="$prospectusTermsAndConditions/interestBlock/interestTypeFlag = 'No'">
											<xsl:value-of select="'Compound'"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$na-answer"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Compound or Simple'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/interestBlock/interestTypeFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$InterestValue"/>
								</xsl:call-template>

								<!--Priced Including Accrued Interest (Secondary Market)-->
								<xsl:variable name="priceAccruedInterestValue">
									<xsl:choose>
										<xsl:when test="$prospectusTermsAndConditions/interestBlock/priceTypeFlag = 'Yes'">
											<xsl:value-of select="'Include (Dirty)'"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="'Exclude (Clean)'"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Priced Including Accrued Interest (Secondary Market):'"/>
									<xsl:with-param name="param2" select="true()"/>
									<xsl:with-param name="param3" select="$priceAccruedInterestValue"/>
								</xsl:call-template>
							</div>

							<h4 class="&blcSectionSubheading;">Maturity Date</h4>
							<div class="&layout_indentHeadings;">

								<!--Extendible-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Extendible:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/extendibleFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/extendibleFlag"/>
								</xsl:call-template>

								<!--Extendible (Holder Only)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Extendible (Holder Only):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/extendibleHolderFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/extendibleHolderFlag"/>
								</xsl:call-template>

								<!--Obligation to Redeem Prior to Maturity-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Obligation to Redeem Prior to Maturity:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/obligationToRedeemFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/obligationToRedeemFlag"/>
								</xsl:call-template>

								<!--Perpetual-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Perpetual:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/perpetualFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/perpetualFlag"/>
								</xsl:call-template>

								<!--Putable (Non-specific)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Putable (Non-specific):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/putableFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/putableFlag"/>
								</xsl:call-template>

								<!--Putable (Poison Pill)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Putable (Poison Pill):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/putablePoisionPillFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/putablePoisionPillFlag"/>
								</xsl:call-template>

								<!--Putable (Special, Incl. Survivor or Asset Sales)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Putable (Special, Incl. Survivor or Asset Sales):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/maturityDateBlock/putableSpecialFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/maturityDateBlock/putableSpecialFlag"/>
								</xsl:call-template>
							</div>

							<h4 class="&blcSectionSubheading;">Payment</h4>
							<div class="&layout_indentHeadings;">

								<!--Pay in Part (Installments)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Pay in Part (Installments):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/paymentBlock/inPartFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/paymentBlock/inPartFlag"/>
								</xsl:call-template>

								<!--Pay in Kind-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Pay in Kind:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/paymentBlock/inKindFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/paymentBlock/inKindFlag"/>
								</xsl:call-template>
							</div>

							<h4 class="&blcSectionSubheading;">General</h4>
							<div class="&layout_indentHeadings;">

								<!--Callable-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Callable:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/callableFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/callableFlag"/>
								</xsl:call-template>

								<!--Callable (Tax Changes)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Callable (Tax Changes):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/callableTaxChangesFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/callableTaxChangesFlag"/>
								</xsl:call-template>

								<!--Capitalized-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Capitalized:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/capitalizedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/capitalizedFlag"/>
								</xsl:call-template>

								<!--Change of Control Provision-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Change of Control Provision:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/changeOfControlFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/changeOfControlFlag"/>
								</xsl:call-template>

								<!--Convertible-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Convertible:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/convertibleFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/convertibleFlag"/>
								</xsl:call-template>

								<!--Coupon May Be Stripped-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Coupon May Be Stripped:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/couponStrippedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/couponStrippedFlag"/>
								</xsl:call-template>

								<!--Credit Rating Sensitive-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Credit Rating Sensitive:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/creditRatingFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/creditRatingFlag"/>
								</xsl:call-template>

								<!--Defeasible-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Defeasible:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/defeasibleFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/defeasibleFlag"/>
								</xsl:call-template>

								<!--Dual Currency-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Dual Currency:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/dualCurrencyFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/dualCurrencyFlag"/>
								</xsl:call-template>

								<!--Equity Clawback-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Equity Clawback:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/clawbackFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/clawbackFlag"/>
								</xsl:call-template>

								<!--Exchange Listed-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Exchange Listed:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/exchangeListedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/exchangeListedFlag"/>
								</xsl:call-template>

								<!--Fungible-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Fungible:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/fungibleFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/fungibleFlag"/>
								</xsl:call-template>

								<!--Green-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Green:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/greenFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/greenFlag"/>
								</xsl:call-template>

								<!--Guaranteed-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Guaranteed:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/guaranteedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/guaranteedFlag"/>
								</xsl:call-template>

								<!--Inflation Index Linked-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Inflation Index Linked:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/inflationIndexFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/inflationIndexFlag"/>
								</xsl:call-template>

								<!--Issue Price Not Fixed Prior to Issue-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Issue Price Not Fixed Prior to Issue:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/issuePriceFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/issuePriceFlag"/>
								</xsl:call-template>

								<!--Medium Term Note-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Medium Term Note:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/mediumTermFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/mediumTermFlag"/>
								</xsl:call-template>

								<!--Moody's Backed-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Moodys Backed:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/moodyBackedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/moodyBackedFlag"/>
								</xsl:call-template>

								<!--Original Issue Discount-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Original Issue Discount:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/issueDiscountFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/issueDiscountFlag"/>
								</xsl:call-template>

								<!--Private Placement-->
								<xsl:variable name="PrivatePlacementValue">
									<xsl:choose>
										<xsl:when test="$prospectusTermsAndConditions/generalBlock/privatePlacement = 'Yes'">
											<xsl:value-of select="'Private'"/>
										</xsl:when>
										<xsl:when test="$prospectusTermsAndConditions/generalBlock/privatePlacement = 'No'">
											<xsl:value-of select="'Public'"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$na-answer"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Private Placement:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/privatePlacement) &gt; 0"/>
									<xsl:with-param name="param3" select="$PrivatePlacementValue"/>
								</xsl:call-template>

								<!--Remarketing Eligible-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Remarketing Eligible:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/remarketingEligibleFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/remarketingEligibleFlag"/>
								</xsl:call-template>

								<!--Tappable-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Tappable:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/tappableFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/tappableFlag"/>
								</xsl:call-template>

								<!--Not Underwritten (All or Portion)-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Not Underwritten (All or Portion):'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/underwrittenFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/underwrittenFlag"/>
								</xsl:call-template>

								<!--Warrants Included at Issue-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Warrants Included at Issue:'"/>
									<xsl:with-param name="param2" select="string-length($prospectusTermsAndConditions/generalBlock/warrantsIncludedFlag) &gt; 0"/>
									<xsl:with-param name="param3" select="$prospectusTermsAndConditions/generalBlock/warrantsIncludedFlag"/>
								</xsl:call-template>
							</div>

						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>

	<!-- LawFirm template applicable only to this content type-->
	<xsl:template name="LawFirm">
		<xsl:choose>
			<xsl:when test="$relatedParties/lawFirmBlock">
				<xsl:for-each select="$relatedParties/lawFirmBlock">
					<xsl:variable name="label">
						<xsl:choose>
							<xsl:when test="position() = 1">
								<xsl:value-of select="'Law Firm:'" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="''" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="DisplayValuesIn3Cols">
						<xsl:with-param name="label" select="$label"/>
						<xsl:with-param name="value1" select="$doNotDisplayValue" />
						<xsl:with-param name="value2" select="lawFirm"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayValuesIn3Cols">
					<xsl:with-param name="label" select="'Law Firm:'"/>
					<xsl:with-param name="value1" select="$doNotDisplayValue" />
					<xsl:with-param name="value2" select="$na-answer"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Underwriter template applicable only to this content type-->
	<xsl:template name="Underwriter">
		<xsl:choose>
			<xsl:when test="$relatedParties/underwriterBlock">
				<xsl:for-each select="$relatedParties/underwriterBlock">
					<xsl:variable name="label">
						<xsl:choose>
							<xsl:when test="position() = 1">
								<xsl:value-of select="'Underwriter:'" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="''" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="DisplayValuesIn3Cols">
						<xsl:with-param name="label" select="$label"/>
						<xsl:with-param name="value1" select="underwriterRole" />
						<xsl:with-param name="value2" select="underwriter"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayValuesIn3Cols">
					<xsl:with-param name="label" select="'Underwriter:'"/>
					<xsl:with-param name="value1" select="$doNotDisplayValue" />
					<xsl:with-param name="value2" select="$na-answer"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- TwoDelimiterSeperatedStringValues -->
	<xsl:template name="TwoDelimiterSeperatedStringValues">
		<xsl:param name="value1"/>
		<xsl:param name="value1DisplayZero" select="false()"/>
		<xsl:param name="value2"/>
		<xsl:param name="delimiter"/>

		<xsl:choose>
			<xsl:when test="$value1DisplayZero and (string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) != 0 and $value2 != $na-answer)">
				<xsl:value-of select="concat('0', $delimiter, $value2)"/>
			</xsl:when>
			<xsl:when test="(string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) != 0 and $value2 != $na-answer)">
				<xsl:value-of select="$value2"/>
			</xsl:when>
			<xsl:when test="$value1DisplayZero and (string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) = 0 or $value2 = $na-answer)">
				<xsl:value-of select="'0'"/>
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

</xsl:stylesheet>
