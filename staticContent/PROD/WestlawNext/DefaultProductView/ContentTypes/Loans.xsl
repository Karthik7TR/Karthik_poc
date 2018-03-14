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
	<xsl:variable name="loanDocBlock" select="Document/n-docbody/loanDocument" />
	<xsl:variable name ="institutionBlock" select="Document/n-docbody/loanDocument/institutionBlock" />
	<xsl:variable name ="issuance" select="Document/n-docbody/bondsDocument/issuance" />
	<xsl:variable name ="principalOrCoupon" select="Document/n-docbody/bondsDocument/principalOrCoupon" />
	<xsl:variable name ="relatedParties" select="Document/n-docbody/bondsDocument/relatedParties" />
	<xsl:variable name ="prospectus" select="Document/n-docbody/bondsDocument/prospectus" />
	<xsl:variable name ="prospectusTermsAndConditions" select="Document/n-docbody/bondsDocument/prospectusTermsAndConditions" />
	<xsl:variable name ="covenantBlock" select="Document/n-docbody/loanDocument/masterLoanBlock/dealAmendBlock/dealAmendItem[activeFlag='y']/loanTermsBlock/covenantBlock" />
	<xsl:variable name ="doNotDisplayValue" select="'~DoNotDisplay~'" />
	<xsl:variable name ="masterLoanBlock" select="Document/n-docbody/loanDocument/masterLoanBlock" />
	<xsl:variable name ="dealAmendBlock" select="Document/n-docbody/loanDocument/masterLoanBlock/dealAmendBlock" />
	<xsl:variable name ="lastCommitItem" select="$dealAmendBlock/dealAmendItem[last()]/loanTermsBlock" />
	<xsl:variable name ="na-answer" select="'N/A'" />
	<xsl:variable name ="amendmentBlock" select="amendmentBlock" />
	<xsl:variable name ="trancheTermsBlock" select="$amendmentBlock/amendmentItem[amendEffectBlock/amendEffect='Origination']/trancheTermsBlock" />
	<xsl:variable name="LawFirmLabel" select="'Law Firm(s):'" />
	<xsl:variable name="GuarantorLabel" select="'Guarantor(s):'" />
	<xsl:variable name="SponsorLabel" select="'Sponsor(s):'" />

	<xsl:template name="VerifyStringStackValues">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="param4"/>

		<table class="&blcNestedTable;">
			<tr>
				<td>
					<xsl:call-template name="DisplayLabel">
						<xsl:with-param name="text" select="$param1" />
					</xsl:call-template>
				</td>
				<td class="&truncatedVertical; &blcNestedTableCell;">
					<xsl:for-each select="$param3">
						<div>
							<xsl:call-template name="VerifyStringValue">
								<xsl:with-param name="ifValidString" select="string-length(text() > 0)" />
								<xsl:with-param name="stingValue" select="text()" />
							</xsl:call-template>
						</div>
					</xsl:for-each>
					<xsl:if test="string-length($param3) = 0">
						<div>N/A</div>
					</xsl:if>
				</td>

				<xsl:if test="not($DeliveryMode)">
					<xsl:call-template name="MoreLink">
						<xsl:with-param name="count" select="$param4" />
						<xsl:with-param name="threshold" select="$extraThreshold" />
					</xsl:call-template>
				</xsl:if>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="VerifyStringWithHtml">
		<xsl:param name="param1"/>
		<xsl:param name="param2"/>
		<xsl:param name="param3"/>
		<xsl:param name="param4"/>
		<xsl:param name="isTable" select="false()"/>

		<table>
			<xsl:if test="$isTable=true()">
				<xsl:attribute name="class">&blcNestedTable;</xsl:attribute>
			</xsl:if>
			<tr>
				<td>
					<xsl:call-template name="DisplayLabel">
						<xsl:with-param name="text" select="$param1" />
					</xsl:call-template>
				</td>
				<td class="&truncatedVertical;">
					<xsl:if test="$isTable=true()">
						<xsl:attribute name="class">&blcNestedTableCell;</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$param3" disable-output-escaping="yes" />
				</td>
			</tr>
		</table>

		<xsl:if test="not($DeliveryMode)">
			<xsl:call-template name="MoreLink">
				<xsl:with-param name="count" select="$param4" />
				<xsl:with-param name="threshold" select="$extraThreshold" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ConvertLetterToYesOrNo">
		<xsl:param name="param1"/>
		<xsl:choose>
			<xsl:when test="$param1='y' or $param1='Y'">
				<xsl:value-of select="'Yes'"/>
			</xsl:when>
			<xsl:when test="$param1='n' or $param1='N'">
				<xsl:value-of select="'No'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="DisplayEightCols">
		<xsl:param name="value1Xpath"/>
		<xsl:param name="value2Xpath"/>
		<xsl:param name="value3Xpath"/>
		<xsl:param name="value4Xpath"/>
		<xsl:param name="value5Xpath"/>
		<xsl:param name="value6Xpath"/>
		<xsl:param name="value7Xpath"/>
		<xsl:param name="rowCssClass"/>


		<tr>
			<xsl:if test="not($DeliveryMode)">
				<xsl:attribute name="class">
					<xsl:value-of select="$rowCssClass"/>
				</xsl:attribute>
			</xsl:if>

			<td class="&layout_col1;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value1Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value1Xpath" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value2Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value2Xpath" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value3Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value3Xpath" />
					</xsl:call-template>
				</div>
			</td>
			<td class="&layout_col4;">
				<div>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value4Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value4Xpath" />
					</xsl:call-template>
				</div>
			</td>
		</tr>

	</xsl:template>

	<xsl:template name="DisplayEightColsHeadings">
		<xsl:param name="value1Xpath"/>
		<xsl:param name="value2Xpath"/>
		<xsl:param name="value3Xpath"/>
		<xsl:param name="value4Xpath"/>
		<xsl:param name="value5Xpath"/>
		<xsl:param name="value6Xpath"/>
		<xsl:param name="value7Xpath"/>



		<tr>
			<th class="&layout_col1;">
				<div>
					<h4>
						<xsl:call-template name="VerifyStringValue">
							<xsl:with-param name="ifValidString" select="string-length($value1Xpath) &gt; 0" />
							<xsl:with-param name="stingValue" select="$value1Xpath" />
						</xsl:call-template>
					</h4>
				</div>
			</th>
			<th class="&layout_col2;">
				<div>
					<h4>
						<xsl:call-template name="VerifyStringValue">
							<xsl:with-param name="ifValidString" select="string-length($value2Xpath) &gt; 0" />
							<xsl:with-param name="stingValue" select="$value2Xpath" />
						</xsl:call-template>
					</h4>
				</div>
			</th>
			<th class="&layout_col3;">
				<div>
					<h4>
						<xsl:call-template name="VerifyStringValue">
							<xsl:with-param name="ifValidString" select="string-length($value3Xpath) &gt; 0" />
							<xsl:with-param name="stingValue" select="$value3Xpath" />
						</xsl:call-template>
					</h4>
				</div>
			</th>
			<th class="&layout_col4;">
				<div>
					<h4>
						<xsl:call-template name="VerifyStringValue">
							<xsl:with-param name="ifValidString" select="string-length($value4Xpath) &gt; 0" />
							<xsl:with-param name="stingValue" select="$value4Xpath" />
						</xsl:call-template>
					</h4>
				</div>
			</th>
		</tr>

	</xsl:template>

	<xsl:template name="DisplayFourString">
		<xsl:param name="label"/>
		<xsl:param name="value1Xpath"/>
		<xsl:param name="value2Xpath"/>
		<xsl:param name="value3Xpath"/>

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
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="ifValidString" select="string-length($value3Xpath) &gt; 0" />
						<xsl:with-param name="stingValue" select="$value3Xpath" />
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="Document">
		<!-- put doc content on display -->
		<div  id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeLoans;'"/>
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
		<xsl:call-template name="InstitutionBlock"/>
		<xsl:call-template name="LoansBlock"/>
		<xsl:call-template name="TrancheBlock"/>
	</xsl:template>

	<!-- Institution Block-->
	<xsl:template name="InstitutionBlock">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Borrower</h3>
						</td>
						<td class="&blcWidth75;">

							<xsl:for-each select="$institutionBlock">
								<div class="&layout_Row_MarginBottom;">
									<!--Name-->
									<xsl:variable name="alternateName">
										<xsl:choose>
											<xsl:when test="string-length(nameBlock/WCAName) > 0">
												<xsl:value-of select="nameBlock/WCAName"/>
											</xsl:when>
											<xsl:when test="string-length(nameBlock/abbrevName) > 0">
												<xsl:value-of select="nameBlock/abbrevName"/>
											</xsl:when>
											<xsl:when test="string-length(nameBlock/nativeLangName) > 0">
												<xsl:value-of select="nameBlock/nativeLangName"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$na-answer"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Name:'"/>
										<xsl:with-param name="param2" select="true()"/>
										<xsl:with-param name="param3" select="$alternateName"/>
									</xsl:call-template>

									<!--Location of Headquarters-->
									<xsl:variable name="cityState">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1" select="locationOfHeadquartersBlock/addressBlock/city" />
											<xsl:with-param name="value2" select="locationOfHeadquartersBlock/addressBlock/stateBlock/state" />
											<xsl:with-param name="delimiter" select="', '"/>
										</xsl:call-template>
									</xsl:variable>
									<xsl:variable name="cityStateCountry">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1" select="$cityState" />
											<xsl:with-param name="value2" select="locationOfHeadquartersBlock/addressBlock/countryBlock/country" />
											<xsl:with-param name="delimiter" select="', '"/>
										</xsl:call-template>
									</xsl:variable>
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Location of Headquarters:'"/>
										<xsl:with-param name="param2" select="true()"/>
										<xsl:with-param name="param3" select="$cityStateCountry"/>
									</xsl:call-template>

									<!--Institution Type-->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Institution Type - Subtype:'"/>
										<xsl:with-param name="param2" select="string-length(institutionTypeBlock/institutionType) > 0"/>
										<xsl:with-param name="param3">
											<xsl:value-of select="institutionTypeBlock/institutionType" />
											<xsl:if test="string-length(institutionSubtypeBlock/institutionSubtype) > 0">
												<xsl:text> - </xsl:text>
												<xsl:value-of select="institutionSubtypeBlock/institutionSubtype" />
											</xsl:if>
										</xsl:with-param>
									</xsl:call-template>

									<!--Private/Public-->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Private/Public:'"/>
										<xsl:with-param name="param2" select="string-length(publicPrivateBlock/publicPrivate) > 0"/>
										<xsl:with-param name="param3" select="publicPrivateBlock/publicPrivate"/>
									</xsl:call-template>

									<!-- SIC Codes and Description -->
									<xsl:call-template name="VerifyStringWithHtml">
										<xsl:with-param name="param1" select="'SIC Codes &amp; Descriptions:'"/>
										<xsl:with-param name="param2" select="string-length(primarySICCodeBlock/SICCode) > 0"/>
										<xsl:with-param name="param3">
											<xsl:text>&lt;div&gt;</xsl:text>
											<xsl:if test="string-length(primarySICCodeBlock/SICCode) > 0">
												<xsl:value-of select="primarySICCodeBlock/SICCode" />
												<xsl:text> - </xsl:text>
												<xsl:value-of select="primarySICCodeBlock/SICDescription" />
											</xsl:if>
											<xsl:if test="string-length(primarySICCodeBlock/SICCode) = 0">
												<xsl:text>N/A</xsl:text>
											</xsl:if>
											<xsl:text>&lt;/div&gt;</xsl:text>
											<xsl:for-each select="SICCodeBlock">
												<xsl:choose>
													<xsl:when test="position() > $extraThreshold -1">
														<xsl:text>&lt;div class=&apos;</xsl:text>
														<xsl:value-of select="$extraItem"/>
														<xsl:text>&apos;&gt;</xsl:text>
													</xsl:when>
													<xsl:otherwise>
														<xsl:text>&lt;div&gt;</xsl:text>
													</xsl:otherwise>
												</xsl:choose>

												<xsl:if test="string-length(SICCode) > 0">
													<xsl:value-of select="SICCode" />
													<xsl:text> - </xsl:text>
													<xsl:value-of select="SICDescription" />
												</xsl:if>

												<xsl:text>&lt;/div&gt;</xsl:text>
											</xsl:for-each>
										</xsl:with-param>
										<xsl:with-param name="param4" select="count(SICCodeBlock) + 1" />
										<xsl:with-param name="isTable" select="true()" />
									</xsl:call-template>

									<!--Parent Company-->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Parent Company:'"/>
										<xsl:with-param name="param2" select="string-length(WCAParent) > 0"/>
										<xsl:with-param name="param3" select="WCAParent"/>
									</xsl:call-template>

									<!--Borrower Type-->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Borrower Type:'"/>
										<xsl:with-param name="param2" select="string-length(borrowerTypeBlock/borrowerType) > 0"/>
										<xsl:with-param name="param3" select="borrowerTypeBlock/borrowerType"/>
									</xsl:call-template>

									<!--Description-->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Description:'"/>
										<xsl:with-param name="param2" select="string-length(description) > 0"/>
										<xsl:with-param name="param3" select="description"/>
										<xsl:with-param name="truncate" select="not($DeliveryMode)"/>
									</xsl:call-template>
								</div>
							</xsl:for-each>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!-- START: Loans Block -->
	<xsl:template name="LoansBlock">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Loan</h3>
						</td>
						<td class="&blcWidth75;">
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<xsl:call-template name="LoansSummary" />
		<xsl:call-template name="LoansOriginationSection" />
		<xsl:call-template name="LoansAmendmentSection" />
	</xsl:template>

	<xsl:template name="LoansSummary">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Loan Summary</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Active -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Active:'"/>
								<xsl:with-param name="param2" select="string-length($masterLoanBlock/masterLoanActiveFlag) > 0"/>
								<xsl:with-param name="param3">
									<xsl:call-template name="ConvertLetterToYesOrNo">
										<xsl:with-param name="param1" select="$masterLoanBlock/masterLoanActiveFlag"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>

							<!-- Syndication Status -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Syndication Status:'"/>
								<xsl:with-param name="param2" select="string-length($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/syndicationStatusBlock/syndicationStatus) > 0"/>
								<xsl:with-param name="param3">
									<xsl:value-of select="$masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/syndicationStatusBlock/syndicationStatus"/>
									<xsl:if test="string-length($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/syndicationStatusBlock/effectiveDate) > 0">
										<xsl:text> effective as of </xsl:text>
										<xsl:call-template name="FormatDateValue">
											<xsl:with-param name="ifValidDate" select="string-length($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/syndicationStatusBlock/effectiveDate) &gt; 7	and number($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/syndicationStatusBlock/effectiveDate) != 'NaN'"/>
											<xsl:with-param name="dateValue" select="$masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/syndicationStatusBlock/effectiveDate" />
											<xsl:with-param name="yearFirst" select="true()"/>
										</xsl:call-template>
									</xsl:if>
								</xsl:with-param>
							</xsl:call-template>

							<!-- Currency -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Currency:'" />
								<xsl:with-param name="param2" select="string-length($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/currencyBlock/currency) > 0" />
								<xsl:with-param name="param3" select="$masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/currencyBlock/currency" />
							</xsl:call-template>

							<!-- Total Commitment (Host) -->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Commitment (Host):'" />
								<xsl:with-param name="param2" select="string-length($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentHost) = 0" />
								<xsl:with-param name="param3" select="$masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentHost=0" />
								<xsl:with-param name="param4" select="string(number($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentHost)) != 'NaN'" />
								<xsl:with-param name="param5" select="concat(format-number($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentHost, '#,###'),' ','(',$masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/currencyBlock/currencyCode, ')')" />
							</xsl:call-template>

							<!-- Total Commitment (USD) -->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Commitment (USD):'" />
								<xsl:with-param name="param2" select="string-length($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentUS) = 0" />
								<xsl:with-param name="param3" select="$masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentUS=0" />
								<xsl:with-param name="param4" select="string(number($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentUS)) != 'NaN'" />
								<xsl:with-param name="param5" select="concat(format-number($masterLoanBlock/dealAmendBlock/dealAmendItem[@mostRecentAmendFlag='true']/loanTermsBlock/commitmentBlock/totalCommitmentUS, '#,###'),' ','(USD)')" />
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>

	<xsl:template name="LoansOriginationSection">

		<xsl:for-each select="$dealAmendBlock/dealAmendItem[number='0']">
			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>Loan Origination</h3>
							</td>
							<td class="&blcWidth75;">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Active:'"/>
									<xsl:with-param name="param2" select="string-length(activeFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="activeFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="DisplayDateValuesInOneColumn">
									<xsl:with-param name="param1" select="'Signed Date:'"/>
									<xsl:with-param name="param2" select="string-length(signedDate) &gt; 7	and number(signedDate) != 'NaN'"/>
									<xsl:with-param name="param3" select="signedDate/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>

								<xsl:call-template name="DisplayDateValuesInOneColumn">
									<xsl:with-param name="param1" select="'Effective End Date:'"/>
									<xsl:with-param name="param2" select="string-length(effectiveDate) &gt; 7	and number(effectiveDate) != 'NaN'"/>
									<xsl:with-param name="param3" select="effectiveDate/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Amendment Status:'"/>
									<xsl:with-param name="param2" select="string-length(statusHistoryBlock/status) > 0"/>
									<xsl:with-param name="param3">
										<xsl:value-of select="statusHistoryBlock/status"/>
										<xsl:text> effective as of </xsl:text>
										<xsl:call-template name="FormatDateValue">
											<xsl:with-param name="ifValidDate" select="string-length(statusHistoryBlock/statusEffectveDate) &gt; 7	and number(statusHistoryBlock/statusEffectveDate) != 'NaN'"/>
											<xsl:with-param name="dateValue" select="statusHistoryBlock/statusEffectveDate" />
											<xsl:with-param name="yearFirst" select="true()"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Syndication Status:'"/>
									<xsl:with-param name="param2" select="string-length(syndicationStatusBlock/syndicationStatus) > 0"/>
									<xsl:with-param name="param3">
										<xsl:value-of select="syndicationStatusBlock/syndicationStatus"/>
										<xsl:text> effective as of </xsl:text>
										<xsl:call-template name="FormatDateValue">
											<xsl:with-param name="ifValidDate" select="string-length(syndicationStatusBlock/effectiveDate) &gt; 7	and number(syndicationStatusBlock/effectiveDate) != 'NaN'"/>
											<xsl:with-param name="dateValue" select="syndicationStatusBlock/effectiveDate" />
											<xsl:with-param name="yearFirst" select="true()"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="LoansDealTitleSection" />
								<xsl:call-template name="LoansPurpose" />
								<xsl:call-template name="LoansFinancials"  />

							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<xsl:call-template name="LoansCommentsSection"/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="LoansAmendmentSection">
		<xsl:if test="string-length($dealAmendBlock/dealAmendItem[number!='0']) > 0">
			<xsl:for-each select="$dealAmendBlock/dealAmendItem[number!='0']">
				<div>
					<table class="&layout_table; &blcPortfolioTable;">
						<tbody>
							<tr class="&coLayoutRowTopDashBorder;">
								<td class="&blcWidth25; &blcSectionHeading;">
									<h3>
										<xsl:text>Loan Amendment </xsl:text>
										<xsl:value-of select="number"/>
									</h3>
								</td>
								<td class="&blcWidth75;">

									<!-- Active -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Active:'"/>
										<xsl:with-param name="param2" select="string-length(activeFlag) > 0"/>
										<xsl:with-param name="param3">
											<xsl:call-template name="ConvertLetterToYesOrNo">
												<xsl:with-param name="param1" select="activeFlag"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>

									<!-- Signed Date -->
									<xsl:call-template name="DisplayDateValuesInOneColumn">
										<xsl:with-param name="param1" select="'Signed Date:'"/>
										<xsl:with-param name="param2" select="string-length(signedDate) &gt; 7	and number(signedDate) != 'NaN'"/>
										<xsl:with-param name="param3" select="signedDate/text()"/>
										<xsl:with-param name="yearFirst" select="1"/>
									</xsl:call-template>

									<!-- Effective End Date -->
									<xsl:call-template name="DisplayDateValuesInOneColumn">
										<xsl:with-param name="param1" select="'Effective End Date:'"/>
										<xsl:with-param name="param2" select="string-length(effectiveDate) &gt; 7	and number(effectiveDate) != 'NaN'"/>
										<xsl:with-param name="param3" select="effectiveDate/text()"/>
										<xsl:with-param name="yearFirst" select="1"/>
									</xsl:call-template>

									<!-- Amendment Status -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Amendment Status:'"/>
										<xsl:with-param name="param2" select="string-length(statusHistoryBlock/status) > 0"/>
										<xsl:with-param name="param3">
											<xsl:value-of select="statusHistoryBlock/status"/>
											<xsl:text> effective as of </xsl:text>
											<xsl:call-template name="FormatDateValue">
												<xsl:with-param name="ifValidDate" select="string-length(statusHistoryBlock/statusEffectveDate) &gt; 7	and number(statusHistoryBlock/statusEffectveDate) != 'NaN'"/>
												<xsl:with-param name="dateValue" select="statusHistoryBlock/statusEffectveDate" />
												<xsl:with-param name="yearFirst" select="true()"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>

									<!-- Syndication Status -->
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Syndication Status :'"/>
										<xsl:with-param name="param2" select="string-length(syndicationStatusBlock/syndicationStatus) > 0"/>
										<xsl:with-param name="param3">
											<xsl:value-of select="syndicationStatusBlock/syndicationStatus"/>
											<xsl:text> effective as of </xsl:text>
											<xsl:call-template name="FormatDateValue">
												<xsl:with-param name="ifValidDate" select="string-length(syndicationStatusBlock/effectiveDate) &gt; 7	and number(syndicationStatusBlock/effectiveDate) != 'NaN'"/>
												<xsl:with-param name="dateValue" select="syndicationStatusBlock/effectiveDate" />
												<xsl:with-param name="yearFirst" select="true()"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>

									<xsl:call-template name="LoansDealTitleSection" />
									<xsl:call-template name="LoansPurpose" />
									<xsl:call-template name="LoansFinancials"  />
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				<xsl:call-template name="LoansCommentsSection"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template name="LoansDealTitleSection">

		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Total Commitment (Host):'" />
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/commitmentBlock/totalCommitmentHost) = 0" />
			<xsl:with-param name="param3" select="loanTermsBlock/commitmentBlock/totalCommitmentHost=0" />
			<xsl:with-param name="param4" select="string(number(loanTermsBlock/commitmentBlock/totalCommitmentHost)) != 'NaN'" />
			<xsl:with-param name="param5" select="concat(format-number(loanTermsBlock/commitmentBlock/totalCommitmentHost, '#,###'),' ','(',loanTermsBlock/commitmentBlock/currencyBlock/currencyCode, ')')" />
		</xsl:call-template>

		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Total Commitment (USD):'" />
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/commitmentBlock/totalCommitmentUS) = 0" />
			<xsl:with-param name="param3" select="loanTermsBlock/commitmentBlock/totalCommitmentUS=0" />
			<xsl:with-param name="param4" select="string(number(loanTermsBlock/commitmentBlock/totalCommitmentUS)) != 'NaN'" />
			<xsl:with-param name="param5" select="concat(format-number(loanTermsBlock/commitmentBlock/totalCommitmentUS, '#,###'),' ','(USD)')" />
		</xsl:call-template>

		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Exchange Rate:'" />
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/exchangeRateBlock/exchangeRate) = 0" />
			<xsl:with-param name="param3" select="loanTermsBlock/exchangeRateBlock/exchangeRate=0" />
			<xsl:with-param name="param4" select="string(number(loanTermsBlock/exchangeRateBlock/exchangeRate)) != 'NaN'" />
			<xsl:with-param name="param5" select="format-number(loanTermsBlock/exchangeRateBlock/exchangeRate, '#,##0.00000')" />
		</xsl:call-template>

		<xsl:call-template name="DisplayDateValuesInOneColumn">
			<xsl:with-param name="param1" select="'Exchange Rate Date:'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/exchangeRateBlock/exchangeRateDate) &gt; 7	and number(loanTermsBlock/exchangeRateBlock/exchangeRateDate) != 'NaN'"/>
			<xsl:with-param name="param3" select="loanTermsBlock/exchangeRateBlock/exchangeRateDate/text()"/>
			<xsl:with-param name="yearFirst" select="1"/>
		</xsl:call-template>

		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Min. Commitments Received:'" />
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/commitmentBlock/minimumCommitmentsRecieved) = 0" />
			<xsl:with-param name="param3" select="loanTermsBlock/commitmentBlock/minimumCommitmentsRecieved=0" />
			<xsl:with-param name="param4" select="string(number(loanTermsBlock/commitmentBlock/minimumCommitmentsRecieved)) != 'NaN'" />
			<xsl:with-param name="param5" select="concat(format-number(loanTermsBlock/commitmentBlock/minimumCommitmentsRecieved, '#,###'),' ','(',loanTermsBlock/commitmentBlock/currencyBlock/currencyCode, ')')" />
		</xsl:call-template>

		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="param1" select="'Default Interest Rate:'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/defaultInterestRateBlock/defaultInterestRate) > 0"/>
			<xsl:with-param name="param3" select="loanTermsBlock/defaultInterestRateBlock/defaultInterestRate"/>
		</xsl:call-template>

		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="param1" select="'Default Interest Rate Spread (BPS):'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/defaultInterestRateBlock/defaultInterestRateSpread) > 0"/>
			<xsl:with-param name="param3" select="loanTermsBlock/defaultInterestRateBlock/defaultInterestRateSpread"/>
		</xsl:call-template>

	</xsl:template>

	<xsl:template name="LoansPurpose">

		<xsl:call-template name="VerifyStringWithHtml">
			<xsl:with-param name="param1" select="'Use of Proceeds:'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/useOfProceedsBlock/useOfProceeds) > 0"/>
			<xsl:with-param name="param3">
				<xsl:for-each select="loanTermsBlock/useOfProceedsBlock/useOfProceeds">
					&lt;div&gt;
					<xsl:value-of select="text()"/>
					&lt;/div&gt;
				</xsl:for-each>
				<xsl:for-each select="loanTermsBlock/projectFinanceBlock/projectFinance">
					&lt;div&gt;
					<xsl:value-of select="concat('Project Finance - ',text())"/>
					&lt;/div&gt;
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="isTable" select="true()"/>
		</xsl:call-template>

		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="param1" select="'Refinancing:'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/refinancingFlag) > 0"/>
			<xsl:with-param name="param3">
				<xsl:call-template name="ConvertLetterToYesOrNo">
					<xsl:with-param name="param1" select="loanTermsBlock/refinancingFlag"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="LoansFinancials">

		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="param1" select="'Dividend Payment Restrictions:'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/financialTermsBlock/paymentRestrictionsFlag) > 0"/>
			<xsl:with-param name="param3">
				<xsl:call-template name="ConvertLetterToYesOrNo">
					<xsl:with-param name="param1" select="loanTermsBlock/financialTermsBlock/paymentRestrictionsFlag"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>

		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="param1" select="'Financial Covenants:'"/>
			<xsl:with-param name="param2" select="string-length(loanTermsBlock/financialTermsBlock/financialCovenantsFlag) > 0"/>
			<xsl:with-param name="param3">
				<xsl:call-template name="ConvertLetterToYesOrNo">
					<xsl:with-param name="param1" select="loanTermsBlock/financialTermsBlock/financialCovenantsFlag"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>

		<xsl:for-each select="loanTermsBlock/lenderVotingRightsBlock/covenantBlock/lenderVotingRightsBlock">

			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="param1" select="'Amendments/Waivers Req. Lender Approval:'"/>
				<xsl:with-param name="param2" select="string-length(requiresLenderApproval) > 0"/>
				<xsl:with-param name="param3" select="requiresLenderApproval"/>
			</xsl:call-template>

			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="param1" select="'Minimum Lender Commitment to Retain Voting Rights (%):'"/>
				<xsl:with-param name="param2" select="string-length(commitRetainVotingRights) > 0"/>
				<xsl:with-param name="param3" select="commitRetainVotingRights"/>
			</xsl:call-template>

		</xsl:for-each>

	</xsl:template>

	<xsl:template name="LoansCommentsSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Comments</h3>
						</td>
						<td class="&blcWidth75;">
							<xsl:variable name="LoansComments">
								<xsl:for-each select="loanTermsBlock/commentBlock/commentItem">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="typeBlock/type"/>
										<xsl:with-param name="param2" select="string-length(comment) &gt; 0"/>
										<xsl:with-param name="param3" select="comment" />
										<xsl:with-param name="truncate" select="not($DeliveryMode)" />
									</xsl:call-template>
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="''"/>
										<xsl:with-param name="param2" select="1 &gt; 0"/>
										<xsl:with-param name="param3" select="''" />
									</xsl:call-template>
								</xsl:for-each>
								<xsl:if test="string-length(comment) > 0">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Amendment:'"/>
										<xsl:with-param name="param2" select="string-length(comment) > 0"/>
										<xsl:with-param name="param3" select="comment"/>
									</xsl:call-template>
								</xsl:if>
							</xsl:variable>

							<xsl:choose>
								<xsl:when test="string-length($LoansComments) > 0">
									<xsl:copy-of select="$LoansComments"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$na-answer"/>
									<!--<xsl:call-template name="VerifyString">
                <xsl:with-param name="param1" select="''"/>
                <xsl:with-param name="param2" select="true()"/>
                <xsl:with-param name="param3" select="$na-answer"/>
              </xsl:call-template>-->
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>
	<!-- END: Loans Block -->

	<!-- START: Tranche Block -->
	<xsl:template name="TrancheBlock">
		<xsl:for-each select="$loanDocBlock/trancheBlock/masterTrancheBlock">
			<xsl:call-template name="TrancheSummary">
				<xsl:with-param name="trancheNumber" select="tranNumber" />
			</xsl:call-template>
			<xsl:call-template name="TrancheOriginSection">
				<xsl:with-param name="trancheNumber" select="tranNumber" />
			</xsl:call-template>
			<xsl:call-template name="TrancheAmendmentSection">
				<xsl:with-param name="trancheNumber" select="tranNumber" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="TrancheSummary">
		<xsl:param name="trancheNumber" />
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>
								<xsl:text>Tranche </xsl:text>
								<xsl:value-of select="$trancheNumber"/>
								<xsl:text> Summary</xsl:text>
							</h3>
						</td>
						<td class="&blcWidth75;">

							<table class="&layout_table; &layout_3Columns; &extraPaddingClass;">
								<!-- Active -->
								<xsl:call-template name="VerifyString">
									<xsl:with-param name="param1" select="'Active:'"/>
									<xsl:with-param name="param2" select="string-length(activeFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="activeFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<!-- Date -->
								<tr>
									<td colspan="3">
										<strong>
											<xsl:text>Dates:</xsl:text>
										</strong>
									</td>
								</tr>
								<xsl:for-each select="amendmentBlock/amendmentItem/trancheTermsBlock/dateBlock/dateItem">
									<xsl:call-template name="DisplayThreeString">
										<xsl:with-param name="label" select="''"/>
										<xsl:with-param name="value1Xpath" select="dateType"/>
										<xsl:with-param name="value2Xpath">
											<xsl:call-template name="FormatDateValue">
												<xsl:with-param name="ifValidDate" select="string-length(date) &gt; 7 and number(date) != 'NaN'"/>
												<xsl:with-param name="dateValue" select="date/text()"/>
												<xsl:with-param name="yearFirst" select="1"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:for-each>

								<!-- Maturity Date -->
								<xsl:call-template name="DisplayThreeString">
									<xsl:with-param name="label" select="''"/>
									<xsl:with-param name="value1Xpath" select="'Maturity Date:'"/>
									<xsl:with-param name="value2Xpath">
										<xsl:call-template name="FormatDateValue">
											<xsl:with-param name="ifValidDate" select="string-length(amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/maturityBlock/date) &gt; 7	and number(amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/maturityBlock/date) != 'NaN'"/>
											<xsl:with-param name="dateValue" select="amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/maturityBlock/date/text()"/>
											<xsl:with-param name="yearFirst" select="1"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<!-- Maturity (Months) -->
								<xsl:call-template name="VerifyString">
									<xsl:with-param name="param1" select="'Maturity (Months):'"/>
									<xsl:with-param name="param2" select="string-length(amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/maturityBlock/monthsToMaturity) > 0"/>
									<xsl:with-param name="param3" select="amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/maturityBlock/monthsToMaturity" />
								</xsl:call-template>

								<!-- Extension Option -->
								<xsl:call-template name="VerifyString">
									<xsl:with-param name="param1" select="'Extension Option:'"/>
									<xsl:with-param name="param2" select="string-length(amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/extensionOptionBlock/extensionOptionFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="amendmentBlock/amendmentItem[@mostRecentTrancheAmendFlag='true']/trancheTermsBlock/extensionOptionBlock/extensionOptionFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="TrancheOriginSection">
		<xsl:param name="trancheNumber" />

		<xsl:variable name="TrancheOriginBaseXpath" select="amendmentBlock/amendmentItem[amendEffectBlock/amendEffect='Origination']" />
		<xsl:if test="string-length($TrancheOriginBaseXpath) > 0">

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Tranche </xsl:text>
									<xsl:value-of select="$trancheNumber"/>
									<xsl:text> Origination</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Active:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/activeFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="$TrancheOriginBaseXpath/activeFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="DisplayDateValuesInOneColumn">
									<xsl:with-param name="param1" select="'Maturity Date:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/maturityBlock/date) &gt; 7	and number($TrancheOriginBaseXpath/trancheTermsBlock/maturityBlock/date) != 'NaN'"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/maturityBlock/date/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Loan Type:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/loanTypeBlock/type) > 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/loanTypeBlock/type" />
								</xsl:call-template>

								<!-- Repayment Section-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Repayment Type:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/repaymentBlock/typeBlock/type) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/repaymentBlock/typeBlock/type" />
								</xsl:call-template>

								<xsl:call-template name="VerifyStringStackValues">
									<xsl:with-param name="param1" select="'Characteristics:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/characteristicBlock/characteristic) > 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/characteristicBlock/characteristic" />
								</xsl:call-template>

								<xsl:call-template name="VerifyStringStackValues">
									<xsl:with-param name="param1" select="'Market Segment:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/marketSegmentBlock/marketSegment) > 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/marketSegmentBlock/marketSegment" />
								</xsl:call-template>

								<xsl:call-template name="VerifyStringWithHtml">
									<xsl:with-param name="param1" select="'Use of Proceeds:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/useOfProceedsBlock/useOfProceeds) > 0"/>
									<xsl:with-param name="param3">
										<xsl:for-each select="$TrancheOriginBaseXpath/trancheTermsBlock/useOfProceedsBlock/useOfProceeds">
											&lt;div&gt;
											<xsl:value-of select="text()"/>
											&lt;/div&gt;
										</xsl:for-each>
										<xsl:for-each select="$TrancheOriginBaseXpath/trancheTermsBlock/projectFinanceBlock/projectFinance">
											&lt;div&gt;
											<xsl:value-of select="concat('Project Finance - ',text())"/>
											&lt;/div&gt;
										</xsl:for-each>
									</xsl:with-param>
									<xsl:with-param name="isTable" select="true()"/>
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Seniority:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/seniorityBlock/seniority) > 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/seniorityBlock/seniority" />
								</xsl:call-template>

								<!--Minimum Commitment (Host Currency)-->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'Minimum Commitment (Host):'" />
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/minCommitment) = 0" />
									<xsl:with-param name="param3" select="minCommitment=0" />
									<xsl:with-param name="param4" select="string(number($TrancheOriginBaseXpath/trancheTermsBlock/minCommitment)) != 'NaN'" />
									<xsl:with-param name="param5" select="concat(format-number($TrancheOriginBaseXpath/trancheTermsBlock/minCommitment, '#,###'), ' (', $TrancheOriginBaseXpath/trancheTermsBlock/currencyBlock/currencyCode,')')" />
								</xsl:call-template>

								<!--Commitment-->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'Commitment (Host):'" />
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/commitment) = 0" />
									<xsl:with-param name="param3" select="commitment=0" />
									<xsl:with-param name="param4" select="string(number($TrancheOriginBaseXpath/trancheTermsBlock/commitment)) != 'NaN'" />
									<xsl:with-param name="param5" select="concat(format-number($TrancheOriginBaseXpath/trancheTermsBlock/commitment, '#,###'), ' (', $TrancheOriginBaseXpath/trancheTermsBlock/currencyBlock/currencyCode,')')" />
								</xsl:call-template>

								<!--Exchange Rate-->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'Exchange Rate:'" />
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRate) = 0" />
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRate=0" />
									<xsl:with-param name="param4" select="string(number($TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRate)) != 'NaN'" />
									<xsl:with-param name="param5" select="concat(format-number($TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRate, '#,###.##') * 100, '%')" />
								</xsl:call-template>

								<xsl:call-template name="DisplayDateValuesInOneColumn">
									<xsl:with-param name="param1" select="'Exchange Rate Date:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRateDate) &gt; 7	and number($TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRateDate) != 'NaN'"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/exchangeRateBlock/exchangeRateDate/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>

								<!--Currencies Drawn/Repaid -->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Currencies Drawn/Repaid:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/termsBlock/currenciesDrawnRepaidBlock/currencyBlock/currency) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/termsBlock/currenciesDrawnRepaidBlock/currencyBlock/currency" />
								</xsl:call-template>

								<!-- BasisPointsSection-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'BPS Above Base Rate:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/BPSBlock/BPSAboveBaseRate) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/BPSBlock/BPSAboveBaseRate" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Base Rate Type:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/BPSBlock/BPSRateTypeBlock/BPSRateType) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/BPSBlock/BPSRateTypeBlock/BPSRateType" />
								</xsl:call-template>

								<!-- AIS Section -->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'AIS Drawn (BPS):'" />
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISDrawn) = 0" />
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISDrawn=0" />
									<xsl:with-param name="param4" select="string(number($TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISDrawn)) != 'NaN'" />
									<xsl:with-param name="param5" select="format-number($TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISDrawn, '#,###')" />
								</xsl:call-template>

								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'AIS Undrawn (BPS):'" />
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISUndrawn) = 0" />
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISUndrawn=0" />
									<xsl:with-param name="param4" select="string(number($TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISUndrawn)) != 'NaN'" />
									<xsl:with-param name="param5" select="format-number($TrancheOriginBaseXpath/trancheTermsBlock/AISBlock/AISUndrawn, '#,###')" />
								</xsl:call-template>

								<!-- SYNDICATION Section-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Initial Borrower Fees (BPS):'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/initialBorrowerFeeBlock/BPSFees) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/initialBorrowerFeeBlock/BPSFees" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Initial Borrower Fee Type:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/initialBorrowerFeeBlock/typeBlock/type) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/initialBorrowerFeeBlock/typeBlock/type" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Syndication Type:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/syndicationBlock/typeBlock/type) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/syndicationBlock/typeBlock/type" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Primary Syndication Country:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/syndicationBlock/primarySyndication) &gt; 0"/>
									<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/syndicationBlock/primarySyndication" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Banker’s Acceptance Option:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/termsBlock/bankersAcceptOptionFlag) &gt; 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="$TrancheOriginBaseXpath/trancheTermsBlock/termsBlock/bankersAcceptOptionFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Competitive Bid Option/Rate Option:'"/>
									<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/termsBlock/competitiveBidOptionFlag) &gt; 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="$TrancheOriginBaseXpath/trancheTermsBlock/termsBlock/competitiveBidOptionFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Comments</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">

								<xsl:variable name="TrancheOriginationComments">
									<xsl:for-each select="$TrancheOriginBaseXpath/trancheTermsBlock/commentBlock/commentItem">
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="param1" select="typeBlock/type"/>
											<xsl:with-param name="param2" select="string-length(comment) &gt; 0"/>
											<xsl:with-param name="param3" select="comment" />
											<xsl:with-param name="truncate" select="not($DeliveryMode)" />
										</xsl:call-template>
									</xsl:for-each>
									<xsl:if test="string-length($TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/comment) > 0">
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="param1" select="'Initial Interest'"/>
											<xsl:with-param name="param2" select="string-length($TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/comment) &gt; 0"/>
											<xsl:with-param name="param3" select="$TrancheOriginBaseXpath/trancheTermsBlock/initialInterestBlock/comment" />
											<xsl:with-param name="truncate" select="not($DeliveryMode)" />
										</xsl:call-template>
									</xsl:if>
								</xsl:variable>

								<xsl:choose>
									<xsl:when test="string-length($TrancheOriginationComments) > 0">
										<xsl:copy-of select="$TrancheOriginationComments"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="param1" select="''"/>
											<xsl:with-param name="param2" select="true()"/>
											<xsl:with-param name="param3" select="$na-answer"/>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Lenders</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">

								<table class="&layout_table; &layout_8Columns; &extraPaddingClass;">
									<xsl:call-template name="DisplayEightColsHeadings">
										<xsl:with-param name="value1Xpath" select="'Name'" />
										<xsl:with-param name="value2Xpath" select="'Role'" />
										<xsl:with-param name="value3Xpath" select="'Primary'" />
										<xsl:with-param name="value4Xpath" select="'Commitment (%)'" />
										<xsl:with-param name="value5Xpath" select="'Commitment (US%)'" />
										<xsl:with-param name="value6Xpath" select="'Commitment Minimum (US%)'" />
										<xsl:with-param name="value7Xpath" select="'Underwritten Amount (US%)'" />
									</xsl:call-template>
									<xsl:for-each select="$TrancheOriginBaseXpath/trancheTermsBlock/lenderBlock/lenderItem">
										<xsl:sort select="name"/>
										<xsl:call-template name="DisplayEightCols">
											<xsl:with-param name="value1Xpath" select="name" />
											<xsl:with-param name="value2Xpath" select="roleBlock/role" />
											<xsl:with-param name="value3Xpath">
												<xsl:call-template name="ConvertLetterToYesOrNo">
													<xsl:with-param name="param1" select="primaryFlag"/>
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="value4Xpath">
												<xsl:choose>
													<xsl:when test="string-length(commitmentPercent) > 0">
														<xsl:value-of select="format-number(commitmentPercent, '#,##0.00000')" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="$na-answer"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:with-param>
											<xsl:with-param name="value5Xpath" select="commitmentUS" />
											<xsl:with-param name="value6Xpath" select="commitmentMinimumUS" />
											<xsl:with-param name="value7Xpath" select="underwrittenAmountUS" />
											<xsl:with-param name="rowCssClass">
												<xsl:choose>
													<xsl:when test="position() > $extraThreshold">
														<xsl:value-of select="$extraItem"/>
													</xsl:when>
													<xsl:otherwise></xsl:otherwise>
												</xsl:choose>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:for-each>
								</table>

								<xsl:if test="not($DeliveryMode)">
									<xsl:call-template name="MoreLink">
										<xsl:with-param name="count" select="count($TrancheOriginBaseXpath/trancheTermsBlock/lenderBlock/lenderItem)" />
										<xsl:with-param name="threshold" select="$extraThreshold" />
									</xsl:call-template>
								</xsl:if>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Related Parties</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">

								<table class="&layout_table; &layout_3Columns; &extraPaddingClass;">

									<xsl:variable name="TrancheOriginRelatedPartiesBlock" select="$TrancheOriginBaseXpath/trancheTermsBlock/relatedPartiesBlock" />

									<!--Law Firms-->
									<xsl:choose>
										<xsl:when test="$TrancheOriginRelatedPartiesBlock/lawFirmBlock/lawFirmItem">
											<xsl:for-each select="$TrancheOriginRelatedPartiesBlock/lawFirmBlock/lawFirmItem">
												<xsl:call-template name="DisplayValuesIn3Cols">
													<xsl:with-param name="label">
														<xsl:if test="position() = 1">
															<xsl:value-of select="$LawFirmLabel" />
														</xsl:if>
													</xsl:with-param>
													<xsl:with-param name="value1" select="name" />
													<xsl:with-param name="value2" select="typeBlock/type"/>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="DisplayValuesIn3Cols">
												<xsl:with-param name="label" select="$LawFirmLabel"/>
												<xsl:with-param name="value1" select="$doNotDisplayValue" />
												<xsl:with-param name="value2" select="$na-answer"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

									<!--Guarantors-->
									<xsl:choose>
										<xsl:when test="$TrancheOriginRelatedPartiesBlock/guarantorBlock/guarantorItem">
											<xsl:for-each select="$TrancheOriginRelatedPartiesBlock/guarantorBlock/guarantorItem">
												<xsl:call-template name="DisplayValuesIn3Cols">
													<xsl:with-param name="label">
														<xsl:if test="position() = 1">
															<xsl:value-of select="$GuarantorLabel" />
														</xsl:if>
													</xsl:with-param>
													<xsl:with-param name="value1" select="name" />
													<xsl:with-param name="value2" select="typeBlock/type"/>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="DisplayValuesIn3Cols">
												<xsl:with-param name="label" select="$GuarantorLabel"/>
												<xsl:with-param name="value1" select="$doNotDisplayValue" />
												<xsl:with-param name="value2" select="$na-answer"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

									<!--Sponsors-->
									<xsl:choose>
										<xsl:when test="$TrancheOriginRelatedPartiesBlock/sponsorBlock/sponsorItem">
											<xsl:for-each select="$TrancheOriginRelatedPartiesBlock/sponsorBlock/sponsorItem">
												<xsl:call-template name="DisplayValuesIn3Cols">
													<xsl:with-param name="label">
														<xsl:if test="position() = 1">
															<xsl:value-of select="$SponsorLabel" />
														</xsl:if>
													</xsl:with-param>
													<xsl:with-param name="value1" select="$doNotDisplayValue" />
													<xsl:with-param name="value2" select="name"/>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="DisplayValuesIn3Cols">
												<xsl:with-param name="label" select="$SponsorLabel"/>
												<xsl:with-param name="value1" select="$doNotDisplayValue" />
												<xsl:with-param name="value2" select="$na-answer"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

		</xsl:if>
	</xsl:template>

	<xsl:template name="TrancheAmendmentSection">
		<xsl:param name="trancheNumber" />
		<xsl:for-each select="amendmentBlock/amendmentItem[amendEffectBlock/amendEffect!='Origination' and amendEffectBlock/amendEffectCode!='FUTURE']">
			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr class="&coLayoutRowTopDashBorder;">
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Tranche </xsl:text>
									<xsl:value-of select="$trancheNumber"/>
									<xsl:text> Amendment </xsl:text>
									<xsl:value-of select="number"/>
								</h3>
							</td>
							<td class="&blcWidth75;">

								<!--Active-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Active:'"/>
									<xsl:with-param name="param2" select="string-length(activeFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="activeFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<!--Effect-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Effect:'"/>
									<xsl:with-param name="param2" select="string-length(amendEffectBlock/amendEffect) > 0"/>
									<xsl:with-param name="param3" select="amendEffectBlock/amendEffect"/>
								</xsl:call-template>

								<!--Amend &amp; Extent Transaction-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Amend &amp; Extend Transaction:'"/>
									<xsl:with-param name="param2" select="string-length(amendExtendTranFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="amendExtendTranFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<!--100% Lender Approval-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'100% Lender Approval:'"/>
									<xsl:with-param name="param2" select="string-length(approvalFlag) > 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="approvalFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<!--Type-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Loan Type:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/loanTypeBlock/type) > 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/loanTypeBlock/type"/>
								</xsl:call-template>

								<!-- Repayment Section-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Repayment Type:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/repaymentBlock/typeBlock/type) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/repaymentBlock/typeBlock/type" />
								</xsl:call-template>

								<!--Characteristics-->
								<xsl:call-template name="VerifyStringStackValues">
									<xsl:with-param name="param1" select="'Characteristics:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/characteristicBlock/characteristic) > 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/characteristicBlock/characteristic" />
								</xsl:call-template>

								<!--Market Segment-->
								<xsl:call-template name="VerifyStringStackValues">
									<xsl:with-param name="param1" select="'Market Segment:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/marketSegmentBlock/marketSegment) > 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/marketSegmentBlock/marketSegment" />
								</xsl:call-template>

								<!--Use of Proceeds-->
								<xsl:call-template name="VerifyStringWithHtml">
									<xsl:with-param name="param1" select="'Use of Proceeds:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/useOfProceedsBlock/useOfProceeds) > 0"/>
									<xsl:with-param name="param3">
										<xsl:for-each select="trancheTermsBlock/useOfProceedsBlock/useOfProceeds">
											&lt;div&gt;
											<xsl:value-of select="text()"/>
											&lt;/div&gt;
										</xsl:for-each>
										<xsl:for-each select="trancheTermsBlock/projectFinanceBlock/projectFinance">
											&lt;div&gt;
											<xsl:value-of select="concat('Project Finance - ',text())"/>
											&lt;/div&gt;
										</xsl:for-each>
									</xsl:with-param>
									<xsl:with-param name="isTable" select="true()"/>
								</xsl:call-template>

								<!--Seniority:-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Seniority:'"/>
									<xsl:with-param name="param2" select="string-length($trancheTermsBlock/seniorityBlock/seniority) > 0"/>
									<xsl:with-param name="param3" select="$trancheTermsBlock/seniorityBlock/seniority"/>
								</xsl:call-template>

								<!--Commitment Section-->
								<!--Minimum Commitment (Host Currency)-->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'Minimum Commitment (Host):'" />
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/minCommitment) = 0" />
									<xsl:with-param name="param3" select="minCommitment=0" />
									<xsl:with-param name="param4" select="string(number(trancheTermsBlock/minCommitment)) != 'NaN'" />
									<xsl:with-param name="param5" select="concat(format-number(trancheTermsBlock/minCommitment, '#,###.#####'), ' (', trancheTermsBlock/currencyBlock/currencyCode,')')" />
								</xsl:call-template>

								<!--Commitment-->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'Commitment (Host):'" />
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/commitment) = 0" />
									<xsl:with-param name="param3" select="commitment=0" />
									<xsl:with-param name="param4" select="string(number(trancheTermsBlock/commitment)) != 'NaN'" />
									<xsl:with-param name="param5" select="concat(format-number(trancheTermsBlock/commitment, '#,###.#####'), ' (', trancheTermsBlock/currencyBlock/currencyCode, ')')" />
								</xsl:call-template>

								<!--Exchange Rate-->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'Exchange Rate:'" />
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/exchangeRateBlock/exchangeRate) = 0" />
									<xsl:with-param name="param3" select="trancheTermsBlock/exchangeRateBlock/exchangeRate=0" />
									<xsl:with-param name="param4" select="string(number(trancheTermsBlock/exchangeRateBlock/exchangeRate)) != 'NaN'" />
									<xsl:with-param name="param5" select="concat(format-number(trancheTermsBlock/exchangeRateBlock/exchangeRate, '#,###.##') * 100, '%')" />
								</xsl:call-template>

								<xsl:call-template name="DisplayDateValuesInOneColumn">
									<xsl:with-param name="param1" select="'Exchange Rate Date:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/exchangeRateBlock/exchangeRateDate) &gt; 7	and number(trancheTermsBlock/exchangeRateBlock/exchangeRateDate) != 'NaN'"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/exchangeRateBlock/exchangeRateDate/text()"/>
									<xsl:with-param name="yearFirst" select="1"/>
								</xsl:call-template>

								<!--Currencies Drawn/Repaid -->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Currencies Drawn/Repaid:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/termsBlock/currenciesDrawnRepaidBlock/currencyBlock/currency) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/termsBlock/currenciesDrawnRepaidBlock/currencyBlock/currency" />
								</xsl:call-template>

								<!-- BasisPointsSection-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'BPS Above Base Rate:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/initialInterestBlock/BPSBlock/BPSAboveBaseRate) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/initialInterestBlock/BPSBlock/BPSAboveBaseRate" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Base Rate Type:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/initialInterestBlock/BPSBlock/BPSRateTypeBlock/BPSRateType) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/initialInterestBlock/BPSBlock/BPSRateTypeBlock/BPSRateType" />
								</xsl:call-template>

								<!-- AIS Section -->
								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'AIS Drawn (BPS):'" />
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/AISBlock/AISDrawn) = 0" />
									<xsl:with-param name="param3" select="trancheTermsBlock/AISBlock/AISDrawn=0" />
									<xsl:with-param name="param4" select="string(number(trancheTermsBlock/AISBlock/AISDrawn)) != 'NaN'" />
									<xsl:with-param name="param5" select="format-number(trancheTermsBlock/AISBlock/AISDrawn, '#,###')" />
								</xsl:call-template>

								<xsl:call-template name="DisplayNumberValuesInOneColumn">
									<xsl:with-param name="param1" select="'AIS Undrawn (BPS):'" />
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/AISBlock/AISUndrawn) = 0" />
									<xsl:with-param name="param3" select="trancheTermsBlock/AISBlock/AISUndrawn=0" />
									<xsl:with-param name="param4" select="string(number(trancheTermsBlock/AISBlock/AISUndrawn)) != 'NaN'" />
									<xsl:with-param name="param5" select="format-number(trancheTermsBlock/AISBlock/AISUndrawn, '#,###')" />
								</xsl:call-template>

								<!-- SYNDICATION Section-->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Initial Borrower Fees (BPS):'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/initialBorrowerFeeBlock/BPSFees) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/initialBorrowerFeeBlock/BPSFees" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Initial Borrower Fee Type:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/initialBorrowerFeeBlock/typeBlock/type) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/initialBorrowerFeeBlock/typeBlock/type" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Syndication Type:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/syndicationBlock/typeBlock/type) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/syndicationBlock/typeBlock/type" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Primary Syndication Country:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/syndicationBlock/primarySyndication) &gt; 0"/>
									<xsl:with-param name="param3" select="trancheTermsBlock/syndicationBlock/primarySyndication" />
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Banker’s Acceptance Option:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/bankersAcceptOptionFlag) &gt; 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="trancheTermsBlock/bankersAcceptOptionFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="param1" select="'Competitive Bid Option/Rate Option:'"/>
									<xsl:with-param name="param2" select="string-length(trancheTermsBlock/competitiveBidOptionFlag) &gt; 0"/>
									<xsl:with-param name="param3">
										<xsl:call-template name="ConvertLetterToYesOrNo">
											<xsl:with-param name="param1" select="trancheTermsBlock/competitiveBidOptionFlag"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</td>
						</tr>
					</tbody>

				</table>
			</div>

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Comments</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">
								<xsl:variable name="TrancheAmendmentsComments">
									<xsl:for-each select="trancheTermsBlock/commentBlock/commentItem">
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="param1" select="typeBlock/type"/>
											<xsl:with-param name="param2" select="string-length(comment) &gt; 0"/>
											<xsl:with-param name="param3" select="comment" />
											<xsl:with-param name="truncate" select="not($DeliveryMode)" />
										</xsl:call-template>
									</xsl:for-each>
									<xsl:if test="string-length(trancheTermsBlock/initialInterestBlock/comment) > 0">
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="param1" select="'Initial Interest'"/>
											<xsl:with-param name="param2" select="string-length(trancheTermsBlock/initialInterestBlock/comment) &gt; 0"/>
											<xsl:with-param name="param3" select="trancheTermsBlock/initialInterestBlock/comment" />
											<xsl:with-param name="truncate" select="not($DeliveryMode)" />
										</xsl:call-template>
									</xsl:if>
								</xsl:variable>

								<xsl:choose>
									<xsl:when test="string-length($TrancheAmendmentsComments) > 0">
										<xsl:copy-of select="$TrancheAmendmentsComments"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="DisplayStringValuesInOneColumn">
											<xsl:with-param name="param1" select="''"/>
											<xsl:with-param name="param2" select="true()"/>
											<xsl:with-param name="param3" select="$na-answer"/>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Lenders</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">
								<table class="&layout_table; &layout_8Columns; &extraPaddingClass;">
									<xsl:call-template name="DisplayEightColsHeadings">
										<xsl:with-param name="value1Xpath" select="'Name'" />
										<xsl:with-param name="value2Xpath" select="'Role'" />
										<xsl:with-param name="value3Xpath" select="'Primary'" />
										<xsl:with-param name="value4Xpath" select="'Commitment (%)'" />
										<xsl:with-param name="value5Xpath" select="'Commitment (US%)'" />
										<xsl:with-param name="value6Xpath" select="'Commitment Minimum (US%)'" />
										<xsl:with-param name="value7Xpath" select="'Underwritten Amount (US%)'" />
									</xsl:call-template>
									<xsl:for-each select="trancheTermsBlock/lenderBlock/lenderItem">
										<xsl:sort select="name"/>
										<xsl:call-template name="DisplayEightCols">
											<xsl:with-param name="value1Xpath" select="name" />
											<xsl:with-param name="value2Xpath" select="roleBlock/role" />
											<xsl:with-param name="value3Xpath">
												<xsl:call-template name="ConvertLetterToYesOrNo">
													<xsl:with-param name="param1" select="primaryFlag"/>
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="value4Xpath">
												<xsl:choose>
													<xsl:when test="string-length(commitmentPercent) > 0">
														<xsl:value-of select="format-number(commitmentPercent, '#,##0.00000')" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="$na-answer"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:with-param>
											<xsl:with-param name="value5Xpath" select="commitmentUS" />
											<xsl:with-param name="value6Xpath" select="commitmentMinimumUS" />
											<xsl:with-param name="value7Xpath" select="underwrittenAmountUS" />
										</xsl:call-template>
									</xsl:for-each>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tbody>
						<tr>
							<td class="&blcWidth25; &blcSectionHeading;">
								<h3>
									<xsl:text>Related Parties</xsl:text>
								</h3>
							</td>
							<td class="&blcWidth75;">
								<table class="&layout_table; &layout_3Columns; &extraPaddingClass;">

									<xsl:variable name="TrancheAmendmentRelatedPartiesBlock" select="trancheTermsBlock/relatedPartiesBlock" />

									<!--Law Firms-->
									<xsl:choose>
										<xsl:when test="$TrancheAmendmentRelatedPartiesBlock/lawFirmBlock/lawFirmItem">
											<xsl:for-each select="$TrancheAmendmentRelatedPartiesBlock/lawFirmBlock/lawFirmItem">
												<xsl:call-template name="DisplayValuesIn3Cols">
													<xsl:with-param name="label">
														<xsl:if test="position() = 1">
															<xsl:value-of select="$LawFirmLabel" />
														</xsl:if>
													</xsl:with-param>
													<xsl:with-param name="value1" select="name" />
													<xsl:with-param name="value2" select="typeBlock/type"/>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="DisplayValuesIn3Cols">
												<xsl:with-param name="label" select="$LawFirmLabel"/>
												<xsl:with-param name="value1" select="$doNotDisplayValue" />
												<xsl:with-param name="value2" select="$na-answer"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

									<!--Guarantors-->
									<xsl:choose>
										<xsl:when test="$TrancheAmendmentRelatedPartiesBlock/guarantorBlock/guarantorItem">
											<xsl:for-each select="$TrancheAmendmentRelatedPartiesBlock/guarantorBlock/guarantorItem">
												<xsl:call-template name="DisplayValuesIn3Cols">
													<xsl:with-param name="label">
														<xsl:if test="position() = 1">
															<xsl:value-of select="$GuarantorLabel" />
														</xsl:if>
													</xsl:with-param>
													<xsl:with-param name="value1" select="name" />
													<xsl:with-param name="value2" select="typeBlock/type"/>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="DisplayValuesIn3Cols">
												<xsl:with-param name="label" select="$GuarantorLabel"/>
												<xsl:with-param name="value1" select="$doNotDisplayValue" />
												<xsl:with-param name="value2" select="$na-answer"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

									<!--Sponsors-->
									<xsl:choose>
										<xsl:when test="$TrancheAmendmentRelatedPartiesBlock/sponsorBlock/sponsorItem">
											<xsl:for-each select="$TrancheAmendmentRelatedPartiesBlock/sponsorBlock/sponsorItem">
												<xsl:call-template name="DisplayValuesIn3Cols">
													<xsl:with-param name="label">
														<xsl:if test="position() = 1">
															<xsl:value-of select="$SponsorLabel" />
														</xsl:if>
													</xsl:with-param>
													<xsl:with-param name="value1" select="$doNotDisplayValue" />
													<xsl:with-param name="value2" select="name"/>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="DisplayValuesIn3Cols">
												<xsl:with-param name="label" select="$SponsorLabel"/>
												<xsl:with-param name="value1" select="$doNotDisplayValue" />
												<xsl:with-param name="value2" select="$na-answer"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</xsl:for-each>
	</xsl:template>
	<!-- END: Tranche Block -->

</xsl:stylesheet>
