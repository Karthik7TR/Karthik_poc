<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<!-- 
	 This stylesheet is re-written from scratch. This version of Mergers and Acquisition supports search terms/search within for most of the fields.
	 All utility templates are rewritten here and not used from Transaction.xsl. Templates in Transaction.xsl does not support searchterms/searchwithin and
	 those templates could not be modified as they are being used by other xsls.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<!-- includes -->
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="Date.xsl"/>

	<xsl:variable name="doNotDisplayValue" select="'~DoNotDisplay~'" />
	<xsl:variable name ="na-answer" select="'Not Available'" />
	<xsl:variable name="target" select="/Document/n-docbody/MergAndAcqDocument/MasterIndex/MasterContent/mergers.block/parties/t.party"/>
	<xsl:variable name="acquiror" select="/Document/n-docbody/MergAndAcqDocument/MasterIndex/MasterContent/mergers.block/parties/a.party"/>
	<xsl:variable name="merger-block" select="/Document/n-docbody/MergAndAcqDocument/MasterIndex/MasterContent/mergers.block"/>
	<xsl:variable name="relatedParties" select="/Document/n-docbody/MergAndAcqDocument/MasterIndex/MasterContent/mergers.block/related.parties"/>
	<xsl:variable name="tsTransactionCurrency">
		<xsl:call-template name="VerifyStringValue">
			<xsl:with-param name="stringValue">
				<xsl:apply-templates select="$merger-block/trans.curr.desc" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="apos" select='"&apos;"' />
	<xsl:variable name="HQStatesToBeHidden" select="'FF,PR,UN,MR,AS,VI,GU,MZ'" />
	<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
	<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

	<xsl:variable name="extraThreshold">5</xsl:variable>
	<xsl:variable name="extraItem">
		&morelessItemStyle; &hideStateClass;
	</xsl:variable>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="Content"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="Content">
		<xsl:call-template name="TransactionSummary" />
		<xsl:call-template name="Target" />
		<xsl:call-template name="Acquiror" />
		<xsl:call-template name="Investor" />
		<xsl:call-template name="Terms" />
		<xsl:call-template name="TransactionValue" />
		<xsl:call-template name="TransactionFinancing" />
		<xsl:call-template name="Participation" />
		<xsl:call-template name="RelatedParties" />
		<xsl:call-template name="GovernmentRegulations" />
	</xsl:template>

	<!--************** START: Transaction Summary *****************-->

	<xsl:template name="TransactionSummary">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Transaction Summary</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Synopsis -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Synopsis:'" />
								<xsl:with-param name="stringValue2">
									<xsl:for-each select="$merger-block/synopsis.text/st">
										<xsl:apply-templates select="." />
										<xsl:text>&nbsp;</xsl:text>
									</xsl:for-each>
								</xsl:with-param>
								<xsl:with-param name="truncate" select="not($DeliveryMode)" />
							</xsl:call-template>

							<!-- General Notes -->
							<xsl:if test="string-length($merger-block/general.notes) > 0">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'General Notes:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/general.notes" />
									</xsl:with-param>
									<xsl:with-param name="truncate" select="not($DeliveryMode)" />
								</xsl:call-template>
							</xsl:if>

							<!-- Status -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Status:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/trstatus.desc" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Announcement Date -->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Announcement Date:'" />
								<xsl:with-param name="dateValue">
									<xsl:apply-templates select="$merger-block/ann.date" />
								</xsl:with-param>
								<xsl:with-param name="parseYearFirst" select="true()" />
							</xsl:call-template>

							<!-- Announcement Date Estimated -->
							<xsl:if test="string-length($merger-block/ann.date.est.fl) > 0 and not($merger-block/ann.date.est.fl = 'NA') and not($merger-block/ann.date.est.fl='N')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Announcement Date Estimated:'" />
									<xsl:with-param name="stringValue2" select="$merger-block/ann.date.est.fl" />
								</xsl:call-template>
							</xsl:if>

							<!-- Expected Effective Date -->
							<xsl:if test="not($merger-block/eff.date and string-length($merger-block/eff.date) > 0)">
								<xsl:call-template name="DisplayDateValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Expected Effective Date:'" />
									<xsl:with-param name="dateValue" select="$merger-block/expected.eff.date" />
									<xsl:with-param name="parseYearFirst" select="true()" />
								</xsl:call-template>
							</xsl:if>

							<!-- Effective Date -->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Effective Date:'" />
								<xsl:with-param name="dateValue" select="$merger-block/eff.date" />
								<xsl:with-param name="parseYearFirst" select="true()" />
							</xsl:call-template>

							<!-- Effective Date Estimated -->
							<xsl:if test="string-length($merger-block/eff.date.est.fl) > 0 and not($merger-block/eff.date.est.fl = 'NA') and not($merger-block/eff.date.est.fl='N')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Effective Date Estimated:'" />
									<xsl:with-param name="stringValue2" select="$merger-block/eff.date.est.fl" />
								</xsl:call-template>
							</xsl:if>

							<!-- Transaction Value (Host Currency) -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Transaction Value (Host Currency):'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="TwoDelimiterSeperatedStringValues">
										<xsl:with-param name="value1">
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="value" select="$merger-block/transval.host" />
											</xsl:call-template>
										</xsl:with-param>
										<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
										<xsl:with-param name="delimiter" select="' ('"/>
										<xsl:with-param name="suffix" select="')'"/>
										<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>

							<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/transval.usd) > 0 and not($merger-block/transval.usd='NA') and not($merger-block/transval.usd='0')">
								<!-- Transaction Value (USD) -->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Transaction Value (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/transval.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>

								<!-- Exchange Rate -->
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Exchange Rate:'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1" select="format-number($merger-block/exch.rate, '0.####')" />
											<xsl:with-param name="value2">
												<xsl:call-template name="FormatStringAsDate">
													<xsl:with-param name="dateString" select="$merger-block/exch.rate.date" />
													<xsl:with-param name="parseYearFirst" select="true()" />
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="delimiter" select="' as of '"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Transaction Type -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Transaction Type:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/forms/form/fd.desc" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Acquisition Technique -->
							<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
								<xsl:with-param name="label" select="'Acquisition Technique:'" />
								<xsl:with-param name="values" select="$merger-block/acqtechs/acqtech/ad.desc" />
							</xsl:call-template>

							<!-- Challenged/Hostile Deal Outcome -->
							<xsl:if test="string-length($merger-block/outcome.desc) > 0">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Challenged/Hostile Deal Outcome:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/outcome.desc" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/asset.swap.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Asset Swap:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/asset.swap.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/auction.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Auction:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/auction.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/bankruptcy.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Bankruptcy:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/bankruptcy.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/block.purchases.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Block Repurchase or Private Purchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/block.purchases.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/concession.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Concession:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/concession.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/creeping.purchase.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Creeping Purchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/creeping.purchase.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/cross.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Cross Border:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/cross.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/debt.restructuring.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Debt Restructuring:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/debt.restructuring.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/divestiture.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Divestiture:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/divestiture.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/dutch.auction.tender.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Dutch Auction Tender:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/dutch.auction.tender.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/emp.participation.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Employee Participation:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/emp.participation.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/equity.carveout.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Equity Carveout:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/equity.carveout.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/esop.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'ESOP Participation:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/esop.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/exch.offer.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Exchange Offer (Equity/Equity-Convert):'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/exch.offer.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/goshop.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Go Shop:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/goshop.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/going.private.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Going Private:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/going.private.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/ interregional.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Interregional:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/ interregional.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/joint.venture.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Joint Venture Formed:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/joint.venture.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/lever.buyout.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Leveraged Buyout:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/lever.buyout.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/limited.partnership.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Limited Partnership Formed:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/limited.partnership.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/liquidation.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Liquidation Plan:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/liquidation.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/litigation.delay.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Litigation Delay:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/litigation.delay.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/litigation.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Litigation Resulted:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/litigation.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/loan.modify.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Loan Modification:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/loan.modify.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/mand.offer.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Legally Mandatory Offering (non-US):'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/mand.offer.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/merger.of.equals.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Merger of Equals:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/merger.of.equals.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/fin.entity.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Non-Financial Target/Financial Purpose:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/fin.entity.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/open.market.purchase.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Open Market Stock Purchase/Repurchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/open.market.purchase.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/portfolio.comp.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Portfolio Company Involvement:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/portfolio.comp.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/private.tender.offer.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Private Tender Offer:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/private.tender.offer.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/pvt.negot.purch.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Privately Negotiated Stake Purchase/Repurchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/pvt.negot.purch.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/privatization.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Privatization (Govt or Govt-Controlled Entity):'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/privatization.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/proxy.fight.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Proxy Fight:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/proxy.fight.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/recap.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Recapitalization:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/recap.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/repurchase.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Repurchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/repurchase.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/restructuring.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Restructuring:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/restructuring.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/reverse.lbo.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Reverse Leveraged Buyout:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/reverse.lbo.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/reverse.takeover.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Reverse Takeover:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/reverse.takeover.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/sellers.minority.interest.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Sale of Minority Interest:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/sellers.minority.interest.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/sale.leaseback.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Sale Leaseback:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/sale.leaseback.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/sec.buyout.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Secondary Buyout:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/sec.buyout.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/splitoff.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Splitoff:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/splitoff.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/stake.purchase.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Stake Purchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/stake.purchase.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/stock.swap.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Stock Swap:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/stock.swap.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/sweeping.purchase.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Sweeping Purchase:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/sweeping.purchase.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/alliance.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Strategic Alliance Formed:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/alliance.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/termin.fee.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Termination Fee:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/termin.fee.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/challenged.bid.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Topping (Challenging) Bid:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/challenged.bid.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/top.fee.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Topping Fee:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/top.fee.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:if test="$merger-block/two.tier.fl='Y'">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Two-Tier Transaction:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/two.tier.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Target's Initial Reception -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="concat('Target', $apos, 's Initial Reception:')" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/init.recept.desc" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Ultimate Board Recommendation -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Ultimate Board Recommendation:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/att.desc" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Purpose (General) -->
							<xsl:variable name="purpose">
								<xsl:for-each select="$merger-block/purposes/purpose">
									<xsl:if test="pur.desc and string-length(normalize-space(pur.desc)) > 0">
										<div>
											<xsl:apply-templates select="pur.desc"/>
										</div>
									</xsl:if>
								</xsl:for-each>
							</xsl:variable>

							<xsl:if test="string-length(normalize-space($purpose)) > 0">
								<table class="&blcNestedTable;">
									<tr>
										<td>
											<xsl:call-template name="DisplayLabel">
												<xsl:with-param name="text" select="'Purpose (General):'" />
											</xsl:call-template>
										</td>
										<td class="&blcNestedTableCell;">
											<xsl:copy-of select="$purpose"/>
										</td>
									</tr>
								</table>
							</xsl:if>

							<!-- Purpose (Specific) -->
							<xsl:if test="$merger-block/purpose.text and string-length($merger-block/purpose.text) > 0" >
								<xsl:call-template name="DisplayStringValuesInOneColumn" >
									<xsl:with-param name="stringValue1" select="'Purpose (Specific):'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/purpose.text" />
									</xsl:with-param>
									<xsl:with-param name="truncate" select="not($DeliveryMode)" />
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!--************** END: Transaction Summary *****************-->

	<!--Display Label-->
	<xsl:template name="DisplayLabel">
		<xsl:param name="text"/>
		<strong>
			<xsl:value-of select="$text"/>
			<xsl:text>&nbsp;&nbsp;</xsl:text>
		</strong>
	</xsl:template>

	<!--************** START: Target *****************-->

	<xsl:template name="Target">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Target</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Name-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Name:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$target/t.confname" />
								</xsl:with-param>
							</xsl:call-template>

							<!--Tickers /Exchanges-->
							<xsl:if test="$target/t.tic.exc.block">
								<table class="&blcNestedTable;">
									<tr>
										<td>
											<xsl:call-template name="DisplayLabel">
												<xsl:with-param name="text" select="'Tickers/Exchanges:'" />
											</xsl:call-template>
										</td>
										<td class="&blcNestedTableCell;">
											<xsl:for-each select="$target/t.tic.exc.block/t.tic.exc">
												<div>
													<xsl:apply-templates select="./t.tic" />
													<xsl:if test="string-length(./t.exc.desc) > 0 and not(./t.exc.desc='NA') and not(./t.exc.desc='0')">
														<xsl:value-of select="' ('"/>
														<xsl:apply-templates select="./t.exc.desc"/>
														<xsl:value-of select="')'"/>
													</xsl:if>
												</div>
											</xsl:for-each>
										</td>
									</tr>
								</table>
							</xsl:if>

							<!--Sic code & description-->
							<xsl:choose>
								<xsl:when test="count($target/t.sic.block) = 1">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'SIC Code &amp; Description:'" />
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="TwoDelimiterSeperatedStringValues">
												<xsl:with-param name="value1">
													<xsl:apply-templates select="$target/t.sic.block[1]/t.sic.code" />
												</xsl:with-param>
												<xsl:with-param name="value2">
													<xsl:apply-templates select="$target/t.sic.block[1]/t.sic.description" />
												</xsl:with-param>
												<xsl:with-param name="delimiter" select="' - '"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<table class="&blcNestedTable;">
										<tr>
											<td>
												<xsl:call-template name="DisplayLabel">
													<xsl:with-param name="text" select="'SIC Code &amp; Description:'" />
												</xsl:call-template>
											</td>
											<td class="&blcNestedTableCell;">
												<xsl:for-each select="$target/t.sic.block">
													<div>
														<xsl:call-template name="TwoDelimiterSeperatedStringValues">
															<xsl:with-param name="value1">
																<xsl:apply-templates select="t.sic.code" />
															</xsl:with-param>
															<xsl:with-param name="value2">
																<xsl:apply-templates select="t.sic.description" />
															</xsl:with-param>
															<xsl:with-param name="delimiter" select="' - '"/>
														</xsl:call-template>
													</div>
												</xsl:for-each>
											</td>
										</tr>
									</table>
								</xsl:otherwise>
							</xsl:choose>

							<!--Location of Headquarters-->
							<xsl:variable name="targetCityState">
								<xsl:choose>
									<xsl:when test="not(contains($HQStatesToBeHidden, translate($target/t.address.block/t.state, $smallcase, $uppercase)))">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1">
												<xsl:apply-templates select="$target/t.address.block/t.city" />
											</xsl:with-param>
											<xsl:with-param name="value2">
												<xsl:apply-templates select="$target/t.address.block/t.state" />
											</xsl:with-param>
											<xsl:with-param name="delimiter" select="', '"/>
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<xsl:apply-templates select="$target/t.address.block/t.city"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:variable name="targetLocationOfHeadquartersText">
								<xsl:call-template name="TwoDelimiterSeperatedStringValues">
									<xsl:with-param name="value1">
										<xsl:copy-of select="$targetCityState"/>
									</xsl:with-param>
									<xsl:with-param name="value2">
										<xsl:apply-templates select="$target/t.address.block/t.countrynm" />
									</xsl:with-param>
									<xsl:with-param name="delimiter" select="', '"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Location of Headquarters:'" />
								<xsl:with-param name="stringValue2" select="$targetLocationOfHeadquartersText" />
							</xsl:call-template>

							<!--Location of Incorporation-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Location of Incorporation:'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="join">
										<xsl:with-param name="nodes" select="$target/t.address.block/t.st_incnm | $target/t.address.block/t.country_inc_nm"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>

							<!--Ownership-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Ownership:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$target/t.companytype/t.status.desc" />
								</xsl:with-param>
							</xsl:call-template>

							<!--Parent-->
							<!--If Target name is same as Parent name, hide Parent name-->
							<xsl:variable name="parent.company" select="$target/tp.parent.company" />
							<xsl:if test="translate($target/t.confname, $smallcase, $uppercase) != translate($parent.company/tp.confname, $smallcase, $uppercase)">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Parent:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$parent.company/tp.confname" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!--Parent Ticker/Exchange-->
							<!--"Parent Ticker" only display the ticker if the Parent Name is displayed-->
							<xsl:if test="$parent.company/tp.tic.exc.block and translate($target/t.confname, $smallcase, $uppercase) != translate($parent.company/tp.confname, $smallcase, $uppercase)">
								<table class="&blcNestedTable;">
									<tr>
										<td>
											<xsl:call-template name="DisplayLabel">
												<xsl:with-param name="text" select="'Parent Ticker/Exchange:'" />
											</xsl:call-template>
										</td>
										<td class="&blcNestedTableCell;">
											<xsl:for-each select="$parent.company/tp.tic.exc.block/tp.tic.exc">
												<div>
													<xsl:apply-templates select="./tp.tic" />
													<xsl:if test="string-length(./tp.exc.desc) > 0 and not(./tp.exc.desc='NA') and not(./tp.exc.desc='0')">
														<xsl:value-of select="' ('"/>
														<xsl:apply-templates select="./tp.exc.desc"/>
														<xsl:value-of select="')'"/>
													</xsl:if>
												</div>
											</xsl:for-each>
										</td>
									</tr>
								</table>
							</xsl:if>

							<!--Financial Sponsor-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Financial Sponsor:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$target/t.financial.sponsor.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!--Employees (Prior to Announcement)-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Employees (Prior to Announcement):'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="value" select="$target/../../tar.emp.nos" />
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>

							<!--Value on Effective Date (USD)-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Value on Effective Date (USD):'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="value" select="$merger-block/val.eff.date.usd" />
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!--************** END: Target *****************-->


	<!--************** START: Acquiror *****************-->

	<xsl:template name="Acquiror">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Acquiror</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Name: -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Name:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$acquiror/a.confname" />
								</xsl:with-param>
							</xsl:call-template>

							<!--Tickers /Exchanges-->
							<xsl:if test="$acquiror/a.tic.exc.block">
								<table class="&blcNestedTable;">
									<tr>
										<td>
											<xsl:call-template name="DisplayLabel">
												<xsl:with-param name="text" select="'Tickers/Exchanges:'" />
											</xsl:call-template>
										</td>
										<td class="&blcNestedTableCell;">
											<xsl:for-each select="$acquiror/a.tic.exc.block/a.tic.exc">
												<div>
													<xsl:apply-templates select="./a.tic" />
													<xsl:if test="string-length(./a.exc.desc) > 0 and not(./a.exc.desc='NA') and not(./a.exc.desc='0')">
														<xsl:value-of select="' ('"/>
														<xsl:apply-templates select="./a.exc.desc"/>
														<xsl:value-of select="')'"/>
													</xsl:if>
												</div>
											</xsl:for-each>
										</td>
									</tr>
								</table>
							</xsl:if>

							<!-- SIC Code & Description -->
							<xsl:choose>
								<xsl:when test="count($acquiror/a.sic.block) = 1">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'SIC Code &amp; Description:'" />
										<xsl:with-param name="stringValue2">
											<xsl:call-template name="TwoDelimiterSeperatedStringValues">
												<xsl:with-param name="value1">
													<xsl:apply-templates select="$acquiror/a.sic.block[1]/a.sic.code" />
												</xsl:with-param>
												<xsl:with-param name="value2">
													<xsl:apply-templates select="$acquiror/a.sic.block[1]/a.sic.description" />
												</xsl:with-param>
												<xsl:with-param name="delimiter" select="' - '"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<table class="&blcNestedTable;">
										<tr>
											<td>
												<xsl:call-template name="DisplayLabel">
													<xsl:with-param name="text" select="'SIC Code &amp; Description:'" />
												</xsl:call-template>
											</td>
											<td class="&blcNestedTableCell;">
												<xsl:for-each select="$acquiror/a.sic.block">
													<div>
														<xsl:call-template name="TwoDelimiterSeperatedStringValues">
															<xsl:with-param name="value1">
																<xsl:apply-templates select="a.sic.code" />
															</xsl:with-param>
															<xsl:with-param name="value2">
																<xsl:apply-templates select="a.sic.description" />
															</xsl:with-param>
															<xsl:with-param name="delimiter" select="' - '"/>
														</xsl:call-template>
													</div>
												</xsl:for-each>
											</td>
										</tr>
									</table>
								</xsl:otherwise>
							</xsl:choose>

							<!-- Location of Headquarters -->
							<xsl:variable name="acquirorCityState">
								<xsl:choose>
									<xsl:when test="not(contains($HQStatesToBeHidden, translate($acquiror/a.address.block/a.state, $smallcase, $uppercase)))">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1">
												<xsl:apply-templates select="$acquiror/a.address.block/a.city" />
											</xsl:with-param>
											<xsl:with-param name="value2">
												<xsl:apply-templates select="$acquiror/a.address.block/a.state" />
											</xsl:with-param>
											<xsl:with-param name="delimiter" select="', '"/>
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<xsl:apply-templates select="$acquiror/a.address.block/a.city"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:variable name="acquirorLocationOfHeadquartersText">
								<xsl:call-template name="TwoDelimiterSeperatedStringValues">
									<xsl:with-param name="value1">
										<xsl:copy-of select="$acquirorCityState"/>
									</xsl:with-param>
									<xsl:with-param name="value2">
										<xsl:apply-templates select="$acquiror/a.address.block/a.countrynm" />
									</xsl:with-param>
									<xsl:with-param name="delimiter" select="', '"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Location of Headquarters:'" />
								<xsl:with-param name="stringValue2" select="$acquirorLocationOfHeadquartersText" />
							</xsl:call-template>

							<!-- Location of Incorporation -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Location of Incorporation:'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="join">
										<xsl:with-param name="nodes" select="$acquiror/a.address.block/a.st_incnm | $acquiror/a.address.block/a.country_inc_nm"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>

							<!-- Ownership -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Ownership:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$acquiror/a.companytype/a.status.desc" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Parent -->
							<!-- If Acquiror name is same as Parent name, hide Parent name -->
							<xsl:variable name="parent.company" select="$acquiror/ap.parent.company" />
							<xsl:if test="translate($acquiror/a.confname, $smallcase, $uppercase) != translate($parent.company/ap.confname, $smallcase, $uppercase)">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Parent:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$parent.company/ap.confname" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!--Parent Ticker/Exchange-->
							<!--"Parent Ticker" only display the ticker if the Parent Name is displayed-->
							<xsl:if test="$parent.company/ap.tic.exc.block and translate($acquiror/a.confname, $smallcase, $uppercase) != translate($parent.company/ap.confname, $smallcase, $uppercase)">
								<table class="&blcNestedTable;">
									<tr>
										<td>
											<xsl:call-template name="DisplayLabel">
												<xsl:with-param name="text" select="'Parent Ticker/Exchange:'" />
											</xsl:call-template>
										</td>
										<td class="&blcNestedTableCell;">
											<xsl:for-each select="$parent.company/ap.tic.exc.block/ap.tic.exc">
												<div>
													<xsl:apply-templates select="./ap.tic" />
													<xsl:if test="string-length(./ap.exc.desc) > 0 and not(./ap.exc.desc='NA') and not(./ap.exc.desc='0')">
														<xsl:value-of select="' ('"/>
														<xsl:apply-templates select="./ap.exc.desc"/>
														<xsl:value-of select="')'"/>
													</xsl:if>
												</div>
											</xsl:for-each>
										</td>
									</tr>
								</table>
							</xsl:if>

							<!-- Financial Sponsor -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Financial Sponsor:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$acquiror/a.financial.sponsor.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Investor Group -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Investor Group:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/investor.group.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- LBO Firm  -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'LBO Firm:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/lever.acq.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- White Knight  -->
							<!--Hide field if value is NO -->
							<xsl:if test="not($merger-block/acq.white.knight.fl='N')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'White Knight:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/acq.white.knight.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>

	<!--************** END: Acquiror *****************-->


	<!--************** START: Investors *****************-->

	<xsl:template name="Investor">
		<xsl:if test="count($merger-block/investor.block/investor) != 0">
			<div>
				<table class="&layout_table; &blcPortfolioTable;">
					<tr class="&blcBorderTop;">
						<td class="&blcSectionHeading;">
							<h3>Investors</h3>
						</td>
					</tr>
					<tr>
						<td colspan="2">

							<div class="&layout_Row_MarginBottom;">
								<table class="&layout_table; &layout_3Columns; &extraPaddingClass;">

									<!--Headers-->
									<xsl:call-template name="DisplayStringValueIn3Columns">
										<xsl:with-param name="stringValue1" select="'Investor'" />
										<xsl:with-param name="stringValue2" select="$doNotDisplayValue" />
										<xsl:with-param name="stringValue3" select="'Equity Purchased (%)'" />
										<xsl:with-param name="columnHeader" select="true()" />
									</xsl:call-template>

									<!--Data-->
									<xsl:for-each select="$merger-block/investor.block/investor">
										<xsl:variable name="equityPurchased">
											<xsl:apply-templates select="invpercent" />
										</xsl:variable>
										<xsl:call-template name="DisplayStringValueIn3Columns">
											<xsl:with-param name="stringValue1">
												<xsl:apply-templates select="iname" />
											</xsl:with-param>
											<xsl:with-param name="stringValue2" select="$doNotDisplayValue" />
											<xsl:with-param name="stringValue3">
												<xsl:choose>
													<xsl:when test="string-length($equityPurchased) != 0 and $equityPurchased != $na-answer">
														<xsl:value-of select="concat(format-number($equityPurchased, '0.####'), '%')" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="$na-answer" />
													</xsl:otherwise>
												</xsl:choose>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:for-each>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!--************** END: Investors *****************-->


	<!--************** START: Applicable Dates*****************-->

	<xsl:template name="Terms">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Events</h3>
						</td>
						<td class="&blcWidth75;">
							<xsl:call-template name="Events" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Deal Terms</h3>
						</td>
						<td class="&blcWidth75;">
							<xsl:call-template name="DealTerms" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="DefensiveStrategies" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="FairnessOpinion" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="LockupAgreement" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="TenderOffer" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="Spinoff" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="Options" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="Collars" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="Termination" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="Events">
		<table class="&layout_table; &layout_2Columns; &extraPaddingClass;">

			<tr>
				<td class="&layout_col1;">
					<div>
						<xsl:call-template name="displayAsLabelOrValue">
							<xsl:with-param name="columnHeader" select="true()" />
							<xsl:with-param name="stringValue" select="'Date'" />
						</xsl:call-template>
					</div>
				</td>

				<td class="&layout_col2;">
					<div>
						<xsl:call-template name="displayAsLabelOrValue">
							<xsl:with-param name="columnHeader" select="true()" />
							<xsl:with-param name="stringValue" select="'Description'" />
						</xsl:call-template>
					</div>
				</td>
			</tr>

			<xsl:choose>
				<xsl:when test="$merger-block/events/event">
					<xsl:for-each select="$merger-block/events/event">
						<tr>
							<td class="&layout_col1;">
								<div>
									<xsl:call-template name="displayAsLabelOrValue">
										<xsl:with-param name="columnHeader" select="false()" />
										<xsl:with-param name="stringValue">
											<xsl:call-template name="FormatStringAsDate">
												<xsl:with-param name="dateString" select="event.date" />
												<xsl:with-param name="parseYearFirst" select="true()" />
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</div>
							</td>

							<td class="&layout_col2;">
								<div>
									<xsl:if test="not($DeliveryMode)">
										<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
									</xsl:if>

									<xsl:call-template name="displayAsLabelOrValue">
										<xsl:with-param name="columnHeader" select="false()" />
										<xsl:with-param name="stringValue">
											<xsl:apply-templates select="event.desc" />
										</xsl:with-param>
									</xsl:call-template>
								</div>

								<xsl:if test="not($DeliveryMode)">
									<xsl:call-template name="MoreLink">
										<xsl:with-param name="count" select="string-length(event.desc)" />
										<xsl:with-param name="threshold" select="1" />
									</xsl:call-template>
								</xsl:if>
							</td>
						</tr>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<td class="&layout_col1;">
							<div>
								<xsl:value-of select="$na-answer"/>
							</div>
						</td>
						<td class="&layout_col2;">
							<div>
								<xsl:value-of select="$na-answer"/>
							</div>
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>

		</table>
	</xsl:template>

	<xsl:template name="DealTerms">

		<!--Defense Used-->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Defense Used:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/def.tactics.fl" />
			</xsl:with-param>
		</xsl:call-template>

		<!--Not using template to display value because 0 should be displayed when not available instead of N/A 
				 Fairness Opinions-->
		<div>
			<strong>
				<xsl:value-of select="'Fairness Opinions:'"/>
			</strong>
			<xsl:choose>
				<xsl:when test="$merger-block/fair.opin.count and string-length($merger-block/fair.opin.count) > 0">
					<xsl:value-of select="$merger-block/fair.opin.count" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>0</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</div>

		<!--Lockup Agreement-->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Lockup Agreement:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/lockup.fl" />
			</xsl:with-param>
		</xsl:call-template>

		<!--Tender Offer-->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Tender Offer:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/tender.offer.fl" />
			</xsl:with-param>
		</xsl:call-template>

		<!-- Collar Agreement -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Collar Agreement:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/collar.fl" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="DefensiveStrategies">
		<xsl:if test="$merger-block/def.tactics.fl='Y'">
			<h4 class="&blcSectionSubheading;">Defensive Strategies</h4>

			<!-- Defense Used -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Defense Used:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/def.tactics.fl" />
				</xsl:with-param>
			</xsl:call-template>

			<!-- Defensive Recapitalization Plan Proposed -->
			<xsl:if test="$merger-block/def.recap.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Defensive Recapitalization Plan Proposed:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/def.recap.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Backend Poison Pill  -->
			<xsl:if test="$merger-block/backend.poison.pill.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Backend Poison Pill:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/backend.poison.pill.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Flipover Poison Pill -->
			<xsl:if test="$merger-block/flipover.poison.pill.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Flipover Poison Pill:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/flipover.poison.pill.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Terminated Due to Poison Pill  -->
			<xsl:if test="$merger-block/poision.pill.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Terminated Due to Poison Pill:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/poision.pill.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Vote Plan -->
			<xsl:if test="$merger-block/voting.plan.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Vote Plan:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/voting.plan.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Defensive Repurchase -->
			<xsl:if test="$merger-block/def.repurchase.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Defensive Repurchase:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/def.repurchase.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Defensive Self Tender -->
			<xsl:if test="$merger-block/def.self.tender.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Defensive Self Tender:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/def.self.tender.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Greenmail -->
			<xsl:if test="$merger-block/greenmail.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Greenmail:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/greenmail.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- PacMan Defense -->
			<xsl:if test="$merger-block/pacman.defense.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'PacMan Defense:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/pacman.defense.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Scorched Earth  -->
			<xsl:if test="$merger-block/scorched.earth.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Scorched Earth:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/scorched.earth.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- White Knight -->
			<xsl:if test="$merger-block/def.white.knight.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'White Knight:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/def.white.knight.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- White Squire -->
			<xsl:if test="$merger-block/squire.fl = 'Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'White Squire:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/squire.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Successful Defensive Technique -->
			<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
				<xsl:with-param name="label" select="'Successful Defensive Technique:'" />
				<xsl:with-param name="values" select="$merger-block/deftechs/deftech/dd.desc" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="FairnessOpinion">
		<xsl:if test="$merger-block/fair.opin.count and $merger-block/fair.opin.count > 0">
			<h4 class="&blcSectionSubheading;">Fairness Opinion</h4>
			<!-- Not using template to display value because 0 should be displayed when not available instead of N/A -->
			<!-- Number of Opinions -->
			<div>
				<xsl:call-template name="DisplayLabel">
					<xsl:with-param name="text" select="'Number of Opinions:'" />
				</xsl:call-template>
				<xsl:choose>
					<xsl:when test="$merger-block/fair.opin.count and string-length($merger-block/fair.opin.count) > 0">
						<xsl:value-of select="$merger-block/fair.opin.count" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>0</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</div>

			<xsl:if test="$merger-block/fair.opin.count and $merger-block/fair.opin.count > '0'">
				<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
				<xsl:call-template name="Opinions" />
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="Opinions">
		<h4 class="&blcSectionSubheading;">Opinions</h4>
		<table class="&layout_table; &layout_3Columns; &extraPaddingClass;">
			<!--Headers-->
			<xsl:call-template name="DisplayStringValueIn3Columns">
				<xsl:with-param name="stringValue1" select="'Date'" />
				<xsl:with-param name="stringValue2" select="'Source Type'" />
				<xsl:with-param name="stringValue3" select="'Source'" />
				<xsl:with-param name="columnHeader" select="true()" />
			</xsl:call-template>

			<!--Data-->
			<xsl:choose>
				<xsl:when test="count($merger-block/fair.opin/fo) != 0">
					<xsl:for-each select="$merger-block/fair.opin/fo">
						<xsl:call-template name="DisplayStringValueIn3Columns">
							<xsl:with-param name="stringValue1">
								<xsl:call-template name="FormatStringAsDate">
									<xsl:with-param name="dateString" select="fodate" />
									<xsl:with-param name="parseYearFirst" select="true()" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="stringValue2">
								<xsl:apply-templates select="fostypedesc" />
							</xsl:with-param>
							<xsl:with-param name="stringValue3">
								<xsl:apply-templates select="foauthn" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="DisplayStringValueIn3Columns">
						<xsl:with-param name="stringValue1" select="$na-answer" />
						<xsl:with-param name="stringValue2" select="$na-answer" />
						<xsl:with-param name="stringValue3" select="$na-answer" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</table>
		<!--</xsl:for-each>-->
	</xsl:template>

	<xsl:template name="LockupAgreement">
		<xsl:if test="$merger-block/lockup.fl='Y'">
			<h4 class="&blcSectionSubheading;">Lockup Agreement</h4>

			<!-- Lockup Agreement -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Lockup Agreement:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/lockup.fl" />
				</xsl:with-param>
			</xsl:call-template>

			<xsl:if test="$merger-block/lockup.fl = 'Y'">
				<!-- Asset Lockup -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Asset Lockup:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/asset.lockup.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Stock Lockup -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Stock Lockup:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/stock.lockup.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Acquiror Lockup (%) -->
				<xsl:variable name="acquirorLockup">
					<xsl:apply-templates select="$merger-block/acq.lockup.perc" />
				</xsl:variable>
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Acquiror Lockup (%):'" />
					<xsl:with-param name="stringValue2">
						<xsl:choose>
							<xsl:when test="string-length($acquirorLockup) != 0 and $acquirorLockup != $na-answer">
								<xsl:value-of select="concat(format-number($acquirorLockup, '0.####'), '%')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$na-answer" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Acquiror Lockup Price (Host Curr.) -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Acquiror Lockup Price (Host Currency):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="Format">
									<xsl:with-param name="value" select="$merger-block/acq.lockup.price.host" />
									<xsl:with-param name="format" select="'#,##0.0000'" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Acquiror Lockup Price (USD) -->
				<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/acq.lockup.price.usd) > 0 and not($merger-block/acq.lockup.price.usd='NA') and not($merger-block/acq.lockup.price.usd='0')">
					<xsl:call-template name="DisplayStringValuesInOneColumn">
						<xsl:with-param name="stringValue1" select="'Acquiror Lockup Price (USD):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$merger-block/acq.lockup.price.usd" />
								<xsl:with-param name="format" select="'#,##0.0000'" />
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>

				<!-- Target Lockup Price (Host Curr.)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Target Lockup Price (Host Currency):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="Format">
									<xsl:with-param name="value" select="$merger-block/tar.lockup.price.host" />
									<xsl:with-param name="format" select="'#,##0.0000'" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Target Lockup Price (USD)  -->
				<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/tar.lockup.price.usd) > 0 and not($merger-block/tar.lockup.price.usd='NA') and not($merger-block/tar.lockup.price.usd='0')">
					<xsl:call-template name="DisplayStringValuesInOneColumn">
						<xsl:with-param name="stringValue1" select="'Target Lockup Price (USD):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$merger-block/tar.lockup.price.usd" />
								<xsl:with-param name="format" select="'#,##0.0000'" />
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>

				<!-- Description  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Description:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/lockup.text" />
					</xsl:with-param>
					<xsl:with-param name="truncate" select="not($DeliveryMode)" />
				</xsl:call-template>
			</xsl:if>

		</xsl:if>
	</xsl:template>

	<xsl:template name="TenderOffer">
		<xsl:if test="$merger-block/tender.offer.fl = 'Y'">
			<h4 class="&blcSectionSubheading;">Tender Offer</h4>

			<!-- Tender Offer -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Tender Offer:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/tender.offer.fl" />
				</xsl:with-param>
			</xsl:call-template>

			<xsl:if test="$merger-block/tender.offer.fl = 'Y'">

				<!-- Private Tender Offer -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Private Tender Offer:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/private.tender.offer.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Self-Tender -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Self-Tender:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/self.tender.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Tender Offer Followed by Merger for Remaining Shares -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Tender Offer Followed by Merger for Remaining Shares:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/tender.merger.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Start Date -->
				<xsl:call-template name="DisplayDateValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Start Date:'" />
					<xsl:with-param name="dateValue" select="$merger-block/tender.offer.date" />
					<xsl:with-param name="parseYearFirst" select="true()" />
				</xsl:call-template>

				<!-- Original Expiration Date -->
				<xsl:call-template name="DisplayDateValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Original Expiration Date:'" />
					<xsl:with-param name="dateValue" select="$merger-block/orig.tender.offer.expire.date" />
					<xsl:with-param name="parseYearFirst" select="true()" />
				</xsl:call-template>

				<!-- Expiration Date -->
				<xsl:call-template name="DisplayDateValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Expiration Date:'" />
					<xsl:with-param name="dateValue" select="$merger-block/tender.offer.expire.date" />
					<xsl:with-param name="parseYearFirst" select="true()" />
				</xsl:call-template>

				<!-- Tender Offer Extensions -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Tender Offer Extensions:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/num.tender.extns" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Common Shares Sought -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Common Shares Sought:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/num.com.shrs.sought.tender.offer" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Common Shares Tendered -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Common Shares Tendered:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/num.shrs.tendered" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Common Shares Accepted -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Common Shares Accepted:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/num.shrs.accepted" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Preferred Shares Tendered -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Preferred Shares Tendered:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/num.pref.shrs.tendered" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Preferred Shares Accepted -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Preferred Shares Accepted:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/num.pref.shrs.accepted" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Minimum Tender Condition (Common Shares)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Minimum Tender Condition (Common Shares):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/min.shrs.tender.cond" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Value Shares Tendered (USD) -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Value Shares Tendered (USD):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/val.shrs.tender.usd" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>

			</xsl:if>

		</xsl:if>
	</xsl:template>

	<xsl:template name="Collars">
		<xsl:if test="string-length($merger-block/collar.fl) > 0 and not($merger-block/collar.fl='NA') and not($merger-block/collar.fl='N')">
			<h4 class="&blcSectionSubheading;">Collar</h4>

			<!-- Collar Agreement -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Collar Agreement:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/collar.fl" />
				</xsl:with-param>
			</xsl:call-template>

			<xsl:if test="$merger-block/collar.fl = 'Y'">

				<!-- Collar Status -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Collar Information - Editorial Status:'" />
					<xsl:with-param name="stringValue2">
						<xsl:choose>
							<xsl:when test="$merger-block/collar.status='I'">
								<xsl:value-of select="'Incomplete'"/>
							</xsl:when>
							<xsl:when test="$merger-block/collar.status='C'">
								<xsl:value-of select="'Complete'"/>
							</xsl:when>
							<xsl:when test="$merger-block/collar.status='L'">
								<xsl:value-of select="'Limited'"/>
							</xsl:when>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Collar Agreement Breached -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Collar Agreement Breached:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/collar.breached.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Symmetric Collar -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Symmetric Collar:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/symmetric.collar.fl" />
					</xsl:with-param>
				</xsl:call-template>


				<!-- Percent Down (1 Day Prior to Ann.)  -->
				<xsl:variable name="collarsPercentDown">
					<xsl:apply-templates select="$merger-block/collar.perc.down" />
				</xsl:variable>
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Percent Down (1 Day Prior to Ann.):'" />
					<xsl:with-param name="stringValue2">
						<xsl:choose>
							<xsl:when test="string-length($collarsPercentDown) != 0 and $collarsPercentDown != $na-answer">
								<xsl:value-of select="concat(format-number($collarsPercentDown, '0.####'), '%')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$na-answer" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Percent Up (1 Day Prior to Ann.)  -->
				<xsl:variable name="collarsPercentUp">
					<xsl:apply-templates select="$merger-block/collar.perc.up" />
				</xsl:variable>
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Percent Up (1 Day Prior to Ann.):'" />
					<xsl:with-param name="stringValue2">
						<xsl:choose>
							<xsl:when test="string-length($collarsPercentUp) != 0 and $collarsPercentUp != $na-answer">
								<xsl:value-of select="concat(format-number($collarsPercentUp, '0.####'), '%')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$na-answer" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Collar Price High (Host)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Collar Price High (Host Currency):'" />
					<xsl:with-param name="stringValue2">
						<xsl:variable name="collarHighPriceHost">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$merger-block/collar.price.high.host" />
								<xsl:with-param name="format" select="'#,##0.00'" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:value-of select="concat($collarHighPriceHost, ' ' , $tsTransactionCurrency)"/>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Collar Price High (USD)  -->
				<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/collar.price.high.usd) > 0 and not($merger-block/collar.price.high.usd='NA') and not($merger-block/collar.price.high.usd='0')">
					<xsl:call-template name="DisplayStringValuesInOneColumn">
						<xsl:with-param name="stringValue1" select="'Collar Price High (USD):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$merger-block/collar.price.high.usd" />
								<xsl:with-param name="format" select="'#,##0.00'" />
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>

				<!-- Collar Price Low (Host)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Collar Price Low (Host Currency):'" />
					<xsl:with-param name="stringValue2">
						<xsl:variable name="collarLowPriceHost">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$merger-block/collar.price.low.host" />
								<xsl:with-param name="format" select="'#,##0.00'" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:value-of select="concat($collarLowPriceHost, ' ' , $tsTransactionCurrency)"/>
					</xsl:with-param>
				</xsl:call-template>

				<!-- Collar Price Low (USD)  -->
				<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/collar.price.low.usd) > 0 and not($merger-block/collar.price.low.usd='NA') and not($merger-block/collar.price.low.usd='0')">
					<xsl:call-template name="DisplayStringValuesInOneColumn">
						<xsl:with-param name="stringValue1" select="'Collar Price Low (USD):'" />
						<xsl:with-param name="stringValue2">
							<xsl:call-template name="Format">
								<xsl:with-param name="value" select="$merger-block/collar.price.low.usd" />
								<xsl:with-param name="format" select="'#,##0.00'" />
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>

				<!-- Acquiror Shares Fixed Ratio (unless Collar Broken)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Acquiror Shares Fixed Ratio (unless Collar Broken):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/collar.exch.ratio.fixed.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Acquiror Shares Floating Ratio (unless Collar Broken)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Acquiror Shares Floating Ratio (unless Collar Broken):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/collar.exch.ratio.float.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Max. Exchange Ratio (if Collar Broken)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Max. Exchange Ratio (if Collar Broken):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/collar.ratio.high" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Min. Exchange Ratio (if Collar Broken)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Min. Exchange Ratio (if Collar Broken):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/collar.ratio.low" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Target High Share Price (USD) (if Collar Broken)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Target High Share Price (USD) (if Collar Broken):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/tar.price.high.usd" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Target Low Share Price (USD) (if Collar Broken)  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Target Low Share Price (USD) (if Collar Broken):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/tar.price.low.usd" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Walkaway Price Available  -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Walkaway Price Available:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/walkaway.fl" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Acquiror Walkaway Price (USD) -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Acquiror Walkaway Price (USD):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/acq.walk.away.price.usd" />
					</xsl:with-param>
				</xsl:call-template>

				<!-- Target Walkaway Price (USD) -->
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Target Walkaway Price (USD):'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/tar.walk.away.price.usd" />
					</xsl:with-param>
				</xsl:call-template>

			</xsl:if>

			<!-- Description of Collar Agreement  -->
			<xsl:if test="$merger-block/collar.text and string-length($merger-block/collar.text)">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Description of Collar Agreement:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/collar.text" />
					</xsl:with-param>
					<xsl:with-param name="truncate" select="not($DeliveryMode)" />
				</xsl:call-template>
			</xsl:if>

		</xsl:if>
	</xsl:template>

	<xsl:template name="Spinoff">
		<xsl:if test="$merger-block/spinoff.fl = 'Y'">
			<h4 class="&blcSectionSubheading;">Spinoff</h4>

			<!-- Spinoff -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Spinoff:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/spinoff.fl" />
				</xsl:with-param>
			</xsl:call-template>

			<!-- Two-Step Spinoff -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Two-Step Spinoff:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/two.step.spin.fl" />
				</xsl:with-param>
			</xsl:call-template>

			<!-- Estimated Spinoff Price per Share (Host Currency) -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Estimated Spinoff Price per Share (Host Currency):'" />
				<xsl:with-param name="stringValue2">
					<xsl:call-template name="TwoDelimiterSeperatedStringValues">
						<xsl:with-param name="value1">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="value" select="$merger-block/est.spinoff.price.per.shr.host" />
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
						<xsl:with-param name="delimiter" select="' ('"/>
						<xsl:with-param name="suffix" select="')'"/>
						<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>

			<!-- Estimated Spinoff Price per Share (USD) -->
			<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/est.spinoff.price.per.shr.usd) > 0 and not($merger-block/est.spinoff.price.per.shr.usd='NA') and not($merger-block/est.spinoff.price.per.shr.usd='0')">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Estimated Spinoff Price per Share (USD):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/est.spinoff.price.per.shr.usd" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Estimated Spinoff Value (Host Currency) -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Estimated Spinoff Value (Host Currency):'" />
				<xsl:with-param name="stringValue2">
					<xsl:call-template name="TwoDelimiterSeperatedStringValues">
						<xsl:with-param name="value1">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="value" select="$merger-block/est.spinoff.val.host" />
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
						<xsl:with-param name="delimiter" select="' ('"/>
						<xsl:with-param name="suffix" select="')'"/>
						<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>

			<!-- Estimated Spinoff Value (USD) -->
			<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/est.spinoff.val.usd) > 0 and not($merger-block/est.spinoff.val.usd='NA') and not($merger-block/est.spinoff.val.usd='0')">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Estimated Spinoff Value (USD):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="value" select="$merger-block/est.spinoff.val.usd" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

		</xsl:if>
	</xsl:template>

	<xsl:template name="Options">
		<xsl:if test="string-length($merger-block/options.text) > 0 and not($merger-block/options.text='NA') and not($merger-block/options.text='0')">
			<h4 class="&blcSectionSubheading;">Options</h4>

			<!-- Summary -->
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Summary:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/options.text" />
				</xsl:with-param>
				<xsl:with-param name="truncate" select="not($DeliveryMode)" />
			</xsl:call-template>

		</xsl:if>
	</xsl:template>

	<xsl:template name="Termination">
		<h4 class="&blcSectionSubheading;">Termination</h4>

		<!-- Termination Date -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Termination Date:'" />
			<xsl:with-param name="dateValue" select="$merger-block/withdrawn.date" />
			<xsl:with-param name="parseYearFirst" select="true()" />
		</xsl:call-template>

		<!-- Termination Fee (Acquiror) -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Termination Fee (Acquiror):'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/acq.termin.fee.fl" />
			</xsl:with-param>
		</xsl:call-template>

		<!-- Termination Fee (Target) -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Termination Fee (Target):'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/tar.termin.fee.fl" />
			</xsl:with-param>
		</xsl:call-template>

		<!-- Acquiror Termination Fee (Host)  -->
		<xsl:if test="string-length($merger-block/acq.termin.fee.host) > 0 and not($merger-block/acq.termin.fee.host='NA') and not($merger-block/acq.termin.fee.host='0')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Acquiror Termination Fee (Host Currency):'" />
				<xsl:with-param name="stringValue2">
					<xsl:call-template name="TwoDelimiterSeperatedStringValues">
						<xsl:with-param name="value1">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="value" select="$merger-block/acq.termin.fee.host" />
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
						<xsl:with-param name="delimiter" select="' ('"/>
						<xsl:with-param name="suffix" select="')'"/>
						<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Acquiror Termination Fee (USD)  -->
		<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/acq.termin.fee.usd) > 0 and not($merger-block/acq.termin.fee.usd='NA') and not($merger-block/acq.termin.fee.usd='0')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Acquiror Termination Fee (USD):'" />
				<xsl:with-param name="stringValue2">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="value" select="$merger-block/acq.termin.fee.usd" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Target Termination Fee (Host Currency)  -->
		<xsl:if test="string-length($merger-block/tar.termin.fee.host) > 0 and not($merger-block/tar.termin.fee.host='NA') and not($merger-block/tar.termin.fee.host='0')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Target Termination Fee (Host Currency):'" />
				<xsl:with-param name="stringValue2">
					<xsl:call-template name="TwoDelimiterSeperatedStringValues">
						<xsl:with-param name="value1">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="value" select="$merger-block/tar.termin.fee.host" />
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
						<xsl:with-param name="delimiter" select="' ('"/>
						<xsl:with-param name="suffix" select="')'"/>
						<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Target Termination Fee (USD)  -->
		<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/tar.termin.fee.usd) > 0 and not($merger-block/tar.termin.fee.usd='NA') and not($merger-block/tar.termin.fee.usd='0')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Target Termination Fee (USD):'" />
				<xsl:with-param name="stringValue2">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="value" select="$merger-block/tar.termin.fee.usd" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Highest Term. Fee as % of Transaction Value -->
		<xsl:variable name="termFeePercentTransactionValue">
			<xsl:apply-templates select="$merger-block/termin.fee.perc.of.val" />
		</xsl:variable>
		<xsl:if test="string-length($termFeePercentTransactionValue) != 0 and $termFeePercentTransactionValue != $na-answer">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Highest Term. Fee as % of Transaction Value:'" />
				<xsl:with-param name="stringValue2">
					<xsl:value-of select="concat(format-number($termFeePercentTransactionValue, '0.####'), '%')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

	</xsl:template>

	<!--************** END: Deal Terms *****************-->

	<!--************** START: Transaction Value *****************-->

	<xsl:template name="TransactionValue">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Transaction Value</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Transaction Value (Host Currency) -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Transaction Value (Host Currency):'" />
								<xsl:with-param name="stringValue2">
									<xsl:call-template name="TwoDelimiterSeperatedStringValues">
										<xsl:with-param name="value1">
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="value" select="$merger-block/transval.host" />
											</xsl:call-template>
										</xsl:with-param>
										<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
										<xsl:with-param name="delimiter" select="' ('"/>
										<xsl:with-param name="suffix" select="')'"/>
										<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>

							<!-- Transaction Value (USD) -->
							<xsl:if test="$tsTransactionCurrency != 'U.S. Dollar' and string-length($merger-block/transval.usd) > 0 and not($merger-block/transval.usd='NA') and not($merger-block/transval.usd='0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Transaction Value (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/transval.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Amended Value -->
							<xsl:if test="$merger-block/amend.val.indicator and $merger-block/amend.val.indicator != 'NA'">
								<xsl:variable name="amendValueIndicator">
									<xsl:choose>
										<xsl:when test="$merger-block/amend.val.indicator = 'D'">
											<xsl:value-of select="'Decreased'"/>
										</xsl:when>
										<xsl:when test="$merger-block/amend.val.indicator = 'I'">
											<xsl:value-of select="'Increased'"/>
										</xsl:when>
									</xsl:choose>
								</xsl:variable>
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Amended Value:'" />
									<xsl:with-param name="stringValue2" select="$amendValueIndicator" />
								</xsl:call-template>
							</xsl:if>

							<!-- Estimated Value -->
							<xsl:if test="$merger-block/estimated.value.fl and not($merger-block/estimated.value.fl = 'N')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Estimated Value:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/estimated.value.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Analyst Estimated Value (USD)  -->
							<xsl:if test="$merger-block/anal.estim.val.usd and not($merger-block/anal.estim.val.usd = 'NA') and not($merger-block/anal.estim.val.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Analyst Estimated Value (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/anal.estim.val.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Valuation Includes Options -->
							<xsl:if test="$merger-block/options.in.val.fl">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Valuation Includes Options:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/options.in.val.fl" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Value at Effective Date (Host Currency) -->
							<xsl:if test="$merger-block/val.eff.date.host and not($merger-block/val.eff.date.host = 'NA') and not($merger-block/val.eff.date.host = '0') and $tsTransactionCurrency">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Value at Effective Date (Host Currency):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="TwoDelimiterSeperatedStringValues">
											<xsl:with-param name="value1">
												<xsl:call-template name="FormatNumber">
													<xsl:with-param name="value" select="$merger-block/val.eff.date.host" />
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
											<xsl:with-param name="delimiter" select="' ('"/>
											<xsl:with-param name="suffix" select="')'"/>
											<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Value Excluding Capital Infusion (USD)  -->
							<xsl:if test="$merger-block/val.deal.excl.cap.inf.usd and not($merger-block/val.deal.excl.cap.inf.usd = 'NA') and not($merger-block/val.deal.excl.cap.inf.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Value Excluding Capital Infusion (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/val.deal.excl.cap.inf.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Value of Capital Infusion (USD)  -->
							<xsl:if test="$merger-block/val.cap.inf.usd and not($merger-block/val.cap.inf.usd = 'NA') and not($merger-block/val.cap.inf.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Value of Capital Infusion (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/val.cap.inf.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Acquiror's Lowest Price Paid per Share (USD) -->
							<xsl:if test="$merger-block/low.price.per.shr.usd and not($merger-block/low.price.per.shr.usd = 'NA') and not($merger-block/low.price.per.shr.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="concat('Acquiror', $apos, 's Lowest Price Paid per Share (USD):')" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/low.price.per.shr.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Cost to Acquire Common Shares (USD)  -->
							<xsl:if test="$merger-block/cost.to.acquire.com.shrs.usd and not($merger-block/cost.to.acquire.com.shrs.usd = 'NA') and not($merger-block/cost.to.acquire.com.shrs.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Cost to Acquire Common Shares (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/cost.to.acquire.com.shrs.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Cost to Acquire Convertible Securities (USD) -->
							<xsl:if test="$merger-block/cost.to.acquire.conv.sec.usd and not($merger-block/cost.to.acquire.conv.sec.usd = 'NA') and not($merger-block/cost.to.acquire.conv.sec.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Cost to Acquire Convertible Securities (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/cost.to.acquire.conv.sec.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Cost to Acquire Flat Convertibles (excl. Options) (USD) -->
							<xsl:if test="$merger-block/cost.to.acquire.flat.conv.excl.options.usd and not($merger-block/cost.to.acquire.flat.conv.excl.options.usd = 'NA') and not($merger-block/cost.to.acquire.flat.conv.excl.options.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Cost to Acquire Flat Convertibles (excl. Options) (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/cost.to.acquire.flat.conv.excl.options.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<!-- Cost to Acquire Preferred Stock (USD) -->
							<xsl:if test="$merger-block/cost.to.acquire.pref.stk.usd and not($merger-block/cost.to.acquire.pref.stk.usd = 'NA') and not($merger-block/cost.to.acquire.pref.stk.usd = '0')">
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Cost to Acquire Preferred Stock (USD):'" />
									<xsl:with-param name="stringValue2">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="value" select="$merger-block/cost.to.acquire.pref.stk.usd" />
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>

							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="Consideration" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="ValueByConsideration" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="Consideration">
		<h4 class="&blcSectionSubheading;">Consideration</h4>

		<!-- Consideration Structure -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Consideration Structure:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/consid.struct.desc" />
			</xsl:with-param>
		</xsl:call-template>

		<!-- Consideration Offered -->
		<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
			<xsl:with-param name="label" select="'Consideration Offered:'" />
			<xsl:with-param name="values" select="$merger-block/consid.items/consid.offer.items/consid.offer/co.desc" />
			<xsl:with-param name="distinct" select="true()" />
		</xsl:call-template>

		<!-- Choice of Consideration Offered -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Choice of Consideration Offered:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/choice.fl" />
			</xsl:with-param>
		</xsl:call-template>

		<!-- Target Asset/Security -->
		<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
			<xsl:with-param name="label" select="'Target Asset/Security:'" />
			<xsl:with-param name="values" select="$merger-block/consid.items/consid.sought.items/consid.sought/cs.desc" />
			<xsl:with-param name="distinct" select="true()" />
		</xsl:call-template>

		<!-- Paid in Cash (%)  -->
		<xsl:variable name="paidInCash">
			<xsl:apply-templates select="$merger-block/perc.of.consid.cash" />
		</xsl:variable>
		<xsl:if test="string-length($paidInCash) != 0 and $paidInCash != $na-answer">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Paid in Cash (%):'" />
				<xsl:with-param name="stringValue2">
					<xsl:value-of select="concat(format-number($paidInCash, '0.####'), '%')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Paid in Stock (%)  -->
		<xsl:variable name="paidInStock">
			<xsl:apply-templates select="$merger-block/perc.of.consid.stk" />
		</xsl:variable>
		<xsl:if test="string-length($paidInStock) != 0 and $paidInStock != $na-answer">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Paid in Stock (%):'" />
				<xsl:with-param name="stringValue2">
					<xsl:value-of select="concat(format-number($paidInStock, '0.####'), '%')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Paid in Unknown (%)  -->
		<xsl:variable name="paidInUnknown">
			<xsl:apply-templates select="$merger-block/perc.of.consid.unknown" />
		</xsl:variable>
		<xsl:if test="string-length($paidInUnknown) != 0 and $paidInUnknown != $na-answer">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Paid in Unknown (%):'" />
				<xsl:with-param name="stringValue2">
					<xsl:value-of select="concat(format-number($paidInUnknown, '0.####'), '%')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Paid in Other (%)  -->
		<xsl:variable name="paidInOther">
			<xsl:apply-templates select="$merger-block/perc.of.consid.other" />
		</xsl:variable>
		<xsl:if test="string-length($paidInOther) != 0 and $paidInOther != $na-answer">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Paid in Other (%):'" />
				<xsl:with-param name="stringValue2">
					<xsl:value-of select="concat(format-number($paidInOther, '0.####'), '%')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Other Consideration -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Other Consideration:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/other.consid" />
			</xsl:with-param>
			<xsl:with-param name="truncate" select="not($DeliveryMode)" />
		</xsl:call-template>

		<!-- Comments  -->
		<xsl:if test="string-length($merger-block/consid.items/consid.text) > 0 and not($merger-block/consid.items/consid.text='NA')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Comments:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/consid.items/consid.text" />
				</xsl:with-param>
				<xsl:with-param name="truncate" select="not($DeliveryMode)" />
			</xsl:call-template>
		</xsl:if>

	</xsl:template>

	<xsl:template name="ValueByConsideration">
		<xsl:if test="$merger-block/value.estimated.fl='Y'
					or ($merger-block/*[not(contains('NA 0', self::val.alt.bid.usd)) or not(contains('NA 0', self::val.cap.inf.host))
						or not(contains('NA 0', self::val.cash.usd)) or not(contains('NA 0', self::val.com.shrs.eff.dt.usd)) or not(contains('NA 0', self::val.com.shrs.usd)) 
						or not(contains('NA 0', self::val.conv.debt.usd)) or not(contains('NA 0', self::val.conv.pref.usd)) or not(contains('NA 0', self::val.debt.usd))
						or not(contains('NA 0', self::val.earnout.usd)) or not(contains('NA 0', self::val.liab.assumed.usd)) or not(contains('NA 0', self::val.pref.equity.usd))
						or not(contains('NA 0', self::val.stake.purch.usd)) or not(contains('NA 0', self::val.tender.offer.host)) or not(contains('NA 0', self::val.war.usd)) 
						or not(contains('NA 0', self::val.undiscl.consid.usd)) or not(contains('NA 0', self::val.other.consid.usd))]
						and $tsTransactionCurrency)">
			<h4 class="&blcSectionSubheading;">Value By Consideration (Host Currency)</h4>

			<!-- Consideration Values Estimated -->
			<xsl:if test="$merger-block/value.estimated.fl='Y'">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Consideration Values Estimated:'" />
					<xsl:with-param name="stringValue2">
						<xsl:apply-templates select="$merger-block/value.estimated.fl" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Alternative Bid -->
			<xsl:if test="$merger-block/val.alt.bid.usd and not($merger-block/val.alt.bid.usd = 'NA') and not($merger-block/val.alt.bid.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Alternative Bid:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.alt.bid.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Capital Infusion -->
			<xsl:if test="$merger-block/val.cap.inf.host and not($merger-block/val.cap.inf.host = 'NA') and not($merger-block/val.cap.inf.host = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Capital Infusion:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.cap.inf.host" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Cash -->
			<xsl:if test="$merger-block/val.cash.usd and not($merger-block/val.cash.usd = 'NA') and not($merger-block/val.cash.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Cash:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.cash.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Common Stock (Effective Date)  -->
			<xsl:if test="$merger-block/val.com.shrs.eff.dt.usd and not($merger-block/val.com.shrs.eff.dt.usd = 'NA') and not($merger-block/val.com.shrs.eff.dt.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Common Stock (Effective Date):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.com.shrs.eff.dt.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Common Stock (Day Prior to Announce of Stock Swap Final Terms) -->
			<xsl:if test="$merger-block/val.com.shrs.usd and not($merger-block/val.com.shrs.usd = 'NA') and not($merger-block/val.com.shrs.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Common Stock (Day Prior to Ann of Stock Swap Final Terms):'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.com.shrs.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Convertible Debt -->
			<xsl:if test="$merger-block/val.conv.debt.usd and not($merger-block/val.conv.debt.usd = 'NA') and not($merger-block/val.conv.debt.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Convertible Debt:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.conv.debt.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Convertible Preferred Stock -->
			<xsl:if test="$merger-block/val.conv.pref.usd and not($merger-block/val.conv.pref.usd = 'NA') and not($merger-block/val.conv.pref.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Convertible Preferred Stock:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.conv.pref.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Debt -->
			<xsl:if test="$merger-block/val.debt.usd and not($merger-block/val.debt.usd = 'NA') and not($merger-block/val.debt.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Debt:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.debt.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Earnout -->
			<xsl:if test="$merger-block/val.earnout.usd and not($merger-block/val.earnout.usd = 'NA') and not($merger-block/val.earnout.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Earnout:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.earnout.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Liabilities Assumed -->
			<xsl:if test="$merger-block/val.liab.assumed.usd and not($merger-block/val.liab.assumed.usd = 'NA') and not($merger-block/val.liab.assumed.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Liabilities Assumed:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.liab.assumed.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Preferred Equity -->
			<xsl:if test="$merger-block/val.pref.equity.usd and not($merger-block/val.pref.equity.usd = 'NA') and not($merger-block/val.pref.equity.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Preferred Equity:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.pref.equity.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Stake Puchase -->
			<xsl:if test="$merger-block/val.stake.purch.usd and not($merger-block/val.stake.purch.usd = 'NA') and not($merger-block/val.stake.purch.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Stake Puchase:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.stake.purch.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Tender Offer -->
			<xsl:if test="$merger-block/val.tender.offer.host and not($merger-block/val.tender.offer.host = 'NA') and not($merger-block/val.tender.offer.host = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Tender Offer:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.tender.offer.host" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Warrants -->
			<xsl:if test="$merger-block/val.war.usd and not($merger-block/val.war.usd = 'NA') and not($merger-block/val.war.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Warrants:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.war.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Undisclosed -->
			<xsl:if test="$merger-block/val.undiscl.consid.usd and not($merger-block/val.undiscl.consid.usd = 'NA') and not($merger-block/val.undiscl.consid.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Undisclosed:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.undiscl.consid.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<!-- Other Consideration -->
			<xsl:if test="$merger-block/val.other.consid.usd and not($merger-block/val.other.consid.usd = 'NA') and not($merger-block/val.other.consid.usd = '0') and $tsTransactionCurrency">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="'Other Consideration:'" />
					<xsl:with-param name="stringValue2">
						<xsl:call-template name="TwoDelimiterSeperatedStringValues">
							<xsl:with-param name="value1">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="value" select="$merger-block/val.other.consid.usd" />
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value2" select="$tsTransactionCurrency"/>
							<xsl:with-param name="delimiter" select="' ('"/>
							<xsl:with-param name="suffix" select="')'"/>
							<xsl:with-param name="hideAllIfOneValueUnavailable" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

		</xsl:if>
	</xsl:template>

	<!--************** END: Transaction Value *****************-->

	<!--************** START: Transaction Financing *****************-->

	<xsl:template name="TransactionFinancing">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Transaction Financing</h3>
						</td>
						<td class="&blcWidth75;">
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="AcquirorFinancing" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="AcquirorFinancing">
		<h4 class="&blcSectionSubheading;">Acquiror Financing</h4>

		<!-- Source -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Source:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/source.of.funds/source.fund/sf.desc" />
			</xsl:with-param>
		</xsl:call-template>

		<!-- Description -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Description:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="$merger-block/source.of.funds.text" />
			</xsl:with-param>
			<xsl:with-param name="truncate" select="not($DeliveryMode)" />
		</xsl:call-template>

		<!-- Bridge Loan -->
		<xsl:if test="not($merger-block/bridge.loans.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Bridge Loan:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/bridge.loans.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Borrowings -->
		<xsl:if test="not($merger-block/borrowings.fl)">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Borrowings:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/borrowings.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Common Stock Offering -->
		<xsl:if test="not($merger-block/com.stk.offer.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Common Stock Offering:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/com.stk.offer.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Debt Securities -->
		<xsl:if test="not($merger-block/debt.securities.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Debt Securities:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/debt.securities.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Internal Corporate Bonds -->
		<xsl:if test="not($merger-block/internal.corporate.funds.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Internal Corporate Bonds:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/internal.corporate.funds.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Junk Bonds -->
		<xsl:if test="not($merger-block/junk.bonds.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Junk Bonds:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/junk.bonds.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Line of Credit  -->
		<xsl:if test="not($merger-block/line.of.credit.fl='N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Line of Credit:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/line.of.credit.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Mezzanine Financing -->
		<xsl:if test="not($merger-block/mezz.fin.fl='N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Mezzanine Financing:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/mezz.fin.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Preferred Stock Offering -->
		<xsl:if test="not($merger-block/pref.stk.offer.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Preferred Stock Offering:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/pref.stk.offer.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Rights Issue Financing -->
		<xsl:if test="not($merger-block/rights.issue.fin.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Rights Issue Financing:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/rights.issue.fin.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Staple Financing -->
		<xsl:if test="not($merger-block/staple.fin.fl = 'N')">
			<xsl:call-template name="DisplayStringValuesInOneColumn">
				<xsl:with-param name="stringValue1" select="'Staple Financing:'" />
				<xsl:with-param name="stringValue2">
					<xsl:apply-templates select="$merger-block/staple.fin.fl" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

	</xsl:template>

	<!--************** END: Transaction Financing *****************-->


	<!--************** START: Participation *****************-->

	<xsl:template name="Participation">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Participation</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Buyout Technique and/or Financial Sponsor Activity -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Buyout Technique and/or Financial Sponsor Activity:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/buyouts.fin.spons.involv.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Financial Sponsor (Buyside or Sellside) -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Financial Sponsor (Buyside or Sellside):'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/fin.spons.involv.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Buyside Financial Sponsor -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Buyside Financial Sponsor:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/buyside.fin.spons.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Buyside Financial Sponsor Consortium Or Club -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Buyside Financial Sponsor Consortium Or Club:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/buyside.spons.club.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Sellside Financial Sponsor -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Sellside Financial Sponsor:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/sellside.fin.spons.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Acquiror Financing from Foreign Lender -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Acquiror Financing from Foreign Lender:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/foreign.lender.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Majority/Non-Majority Owned Portfolio Co.  -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Majority/Non-Majority Owned Portfolio Co.:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/major.own.portfolio.comp.fl" />
								</xsl:with-param>
							</xsl:call-template>

							<!-- Target Management -->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1" select="'Target Management:'" />
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="$merger-block/mgmt.particpation.fl" />
								</xsl:with-param>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<!--************** END: Participation *****************-->


	<!--************** START: Related Parties *****************-->

	<xsl:template name="RelatedParties">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Related Parties</h3>
						</td>
						<td class="&blcWidth75;">

							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="AcquirorLawFirm" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="TargetLawFirm" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="AcquirorFinancialAdvisor" />
							<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
							<xsl:call-template name="TargetFinancialAdvisor" />

							<xsl:if test="string-length($merger-block/fees.text) > 0">
								<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
								<xsl:call-template name="DisplayStringValuesInOneColumn">
									<xsl:with-param name="stringValue1" select="'Fee Comments:'" />
									<xsl:with-param name="stringValue2">
										<xsl:apply-templates select="$merger-block/fees.text" />
									</xsl:with-param>
									<xsl:with-param name="truncate" select="not($DeliveryMode)" />
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="AcquirorLawFirm">
		<h4 class="&blcSectionSubheading;">Acquiror Law Firm</h4>

		<xsl:choose>
			<xsl:when test="$relatedParties/ac.party">
				<xsl:for-each select="$relatedParties/ac.party">

					<xsl:variable name="currentLawFirm" select="position()"/>

					<!-- Law Firm -->
					<xsl:call-template name="DisplayStringValuesInOneColumn">
						<xsl:with-param name="stringValue1" select="'Law Firm:'" />
						<xsl:with-param name="stringValue2">
							<xsl:apply-templates select="$relatedParties/ac.party[$currentLawFirm]/ac.desc" />
						</xsl:with-param>
					</xsl:call-template>

					<!-- Attorney(s) -->
					<xsl:if test="count($relatedParties/ac.party[$currentLawFirm]/ac.lawyers.block/ac.lawyer) != 0">
						<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
							<xsl:with-param name="label" select="'Attorney(s):'" />
							<xsl:with-param name="values" select="$relatedParties/ac.party[$currentLawFirm]/ac.lawyers.block/ac.lawyer/ac.lawyer.name" />
						</xsl:call-template>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="TargetLawFirm">
		<h4 class="&blcSectionSubheading;">Target Law Firm</h4>
		<xsl:choose>
			<xsl:when test="$relatedParties/tc.party">
				<xsl:for-each select="$relatedParties/tc.party">

					<xsl:variable name="currentLawFirm" select="position()"/>

					<!-- Law Firm -->
					<xsl:call-template name="DisplayStringValuesInOneColumn">
						<xsl:with-param name="stringValue1" select="'Law Firm:'" />
						<xsl:with-param name="stringValue2">
							<xsl:apply-templates select="$relatedParties/tc.party[$currentLawFirm]/tc.desc" />
						</xsl:with-param>
					</xsl:call-template>

					<!-- Attorney(s) -->
					<xsl:if test="count($relatedParties/tc.party[$currentLawFirm]/tc.lawyers.block/tc.lawyer) != 0">
						<xsl:for-each select="$relatedParties/tc.party[$currentLawFirm]/tc.lawyers.block/tc.lawyer">
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="stringValue1">
									<xsl:choose>
										<xsl:when test="position() = 1">
											<xsl:value-of select="'Attorney(s):'"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$doNotDisplayValue"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="stringValue2">
									<xsl:apply-templates select="tc.lawyer.name" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="AcquirorFinancialAdvisor">
		<h4 class="&blcSectionSubheading;">Target Law Firm</h4>

		<table class="&layout_table; &layout_4Columns; &extraPaddingClass;">

			<!-- Headers -->
			<xsl:call-template name="DisplayStringValueIn4Columns">
				<xsl:with-param name="stringValue1" select="'Name'" />
				<xsl:with-param name="stringValue2" select="'Assignment'" />
				<xsl:with-param name="stringValue3" select="'Fee Description'" />
				<xsl:with-param name="stringValue4" select="'Fee (USD)'" />
				<xsl:with-param name="column1Header" select="true()" />
				<xsl:with-param name="column2Header" select="true()" />
				<xsl:with-param name="column3Header" select="true()" />
				<xsl:with-param name="column4Header" select="true()" />
			</xsl:call-template>

			<!-- Data -->
			<xsl:choose>
				<xsl:when test="count($relatedParties/av.party) != 0">
					<xsl:for-each select="$relatedParties/av.party">
						<xsl:variable name="acquirorAdvisorName">
							<xsl:apply-templates select="av.desc" />
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="count(av.adv.fee.block/av.adv.fee) != 0">
								<xsl:for-each select="av.adv.fee.block/av.adv.fee">
									<xsl:variable name="acquirorAdvisorFeeItem" select="position()" />
									<xsl:call-template name="DisplayStringValueIn4Columns">
										<xsl:with-param name="stringValue1" select="$acquirorAdvisorName" />
										<xsl:with-param name="stringValue2">
											<xsl:apply-templates select="../../av.adv.assign.block/av.adv.assign[$acquirorAdvisorFeeItem]/av.assign.desc" />
										</xsl:with-param>
										<xsl:with-param name="stringValue3">
											<xsl:apply-templates select="av.fee.desc" />
										</xsl:with-param>
										<xsl:with-param name="stringValue4">
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="value" select="av.fee.amt.usd" />
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:for-each>
							</xsl:when>
							<xsl:when test="count(av.adv.assign.block/av.adv.assign) != 0">
								<xsl:for-each select="av.adv.assign.block/av.adv.assign">
									<xsl:call-template name="DisplayStringValueIn4Columns">
										<xsl:with-param name="stringValue1" select="$acquirorAdvisorName" />
										<xsl:with-param name="stringValue2">
											<xsl:apply-templates select="av.assign.desc" />
										</xsl:with-param>
										<xsl:with-param name="stringValue3" select="$na-answer" />
										<xsl:with-param name="stringValue4" select="$na-answer" />
									</xsl:call-template>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="DisplayStringValueIn4Columns">
									<xsl:with-param name="stringValue1" select="$acquirorAdvisorName" />
									<xsl:with-param name="stringValue2" select="$doNotDisplayValue" />
									<xsl:with-param name="stringValue3" select="$doNotDisplayValue" />
									<xsl:with-param name="stringValue4" select="$doNotDisplayValue" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="DisplayStringValueIn4Columns">
						<xsl:with-param name="stringValue1" select="$na-answer" />
						<xsl:with-param name="stringValue2" select="$doNotDisplayValue" />
						<xsl:with-param name="stringValue3" select="$doNotDisplayValue" />
						<xsl:with-param name="stringValue4" select="$doNotDisplayValue" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<xsl:template name="TargetFinancialAdvisor">
		<h4 class="&blcSectionSubheading;">Target Financial Advisor</h4>

		<table class="&layout_table; &layout_4Columns; &extraPaddingClass;">

			<!-- Headers -->
			<xsl:call-template name="DisplayStringValueIn4Columns">
				<xsl:with-param name="stringValue1" select="'Name'" />
				<xsl:with-param name="stringValue2" select="'Assignment'" />
				<xsl:with-param name="stringValue3" select="'Fee Description'" />
				<xsl:with-param name="stringValue4" select="'Fee (USD)'" />
				<xsl:with-param name="column1Header" select="true()" />
				<xsl:with-param name="column2Header" select="true()" />
				<xsl:with-param name="column3Header" select="true()" />
				<xsl:with-param name="column4Header" select="true()" />
			</xsl:call-template>

			<!-- Data -->
			<xsl:choose>
				<xsl:when test="count($relatedParties/tv.party) != 0">
					<xsl:for-each select="$relatedParties/tv.party">
						<xsl:variable name="targetAdvisorName">
							<xsl:apply-templates select="tv.desc" />
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="count(tv.adv.fee.block/tv.adv.fee) != 0">
								<xsl:for-each select="tv.adv.fee.block/tv.adv.fee">
									<xsl:variable name="targetAdvisorFeeItem" select="position()" />
									<xsl:call-template name="DisplayStringValueIn4Columns">
										<xsl:with-param name="stringValue1" select="$targetAdvisorName" />
										<xsl:with-param name="stringValue2">
											<xsl:apply-templates select="../../tv.adv.assign.block/tv.adv.assign[$targetAdvisorFeeItem]/tv.assign.desc" />
										</xsl:with-param>
										<xsl:with-param name="stringValue3">
											<xsl:apply-templates select="tv.fee.desc" />
										</xsl:with-param>
										<xsl:with-param name="stringValue4">
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="value" select="tv.fee.amt.usd" />
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:for-each>
							</xsl:when>
							<xsl:when test="count(tv.adv.assign.block/tv.adv.assign) != 0">
								<xsl:for-each select="tv.adv.assign.block/tv.adv.assign">
									<xsl:call-template name="DisplayStringValueIn4Columns">
										<xsl:with-param name="stringValue1" select="$targetAdvisorName" />
										<xsl:with-param name="stringValue2">
											<xsl:apply-templates select="tv.assign.desc" />
										</xsl:with-param>
										<xsl:with-param name="stringValue3" select="$na-answer" />
										<xsl:with-param name="stringValue4" select="$na-answer" />
									</xsl:call-template>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="DisplayStringValueIn4Columns">
									<xsl:with-param name="stringValue1" select="$targetAdvisorName" />
									<xsl:with-param name="stringValue2" select="$doNotDisplayValue" />
									<xsl:with-param name="stringValue3" select="$doNotDisplayValue" />
									<xsl:with-param name="stringValue4" select="$doNotDisplayValue" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="DisplayStringValueIn4Columns">
						<xsl:with-param name="stringValue1" select="$na-answer" />
						<xsl:with-param name="stringValue2" select="$doNotDisplayValue" />
						<xsl:with-param name="stringValue3" select="$doNotDisplayValue" />
						<xsl:with-param name="stringValue4" select="$doNotDisplayValue" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>

		</table>
	</xsl:template>

	<!--************** END: Related Parties *****************-->


	<!--************** START: Government Regulations *****************-->

	<xsl:template name="GovernmentRegulations">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Government Regulation</h3>
						</td>
						<td class="&blcWidth75;">

							<!-- Regulatory Agency -->
							<xsl:choose>
								<xsl:when test="$merger-block/reg.agencies/reg.agency">
									<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
										<xsl:with-param name="label" select="'Regulatory Agency:'" />
										<xsl:with-param name="values" select="$merger-block/reg.agencies/reg.agency/ra.desc" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="stringValue1" select="'Regulatory Agency:'" />
										<xsl:with-param name="stringValue2" select="$na-answer" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>

							<!-- US State Law(s) -->
							<xsl:if test="$merger-block/laws.states/laws.state">
								<xsl:call-template name="DisplayMultipleStringValuesInOneColumn">
									<xsl:with-param name="label" select="'US State Law(s):'" />
									<xsl:with-param name="values" select="$merger-block/laws.states/laws.state/ls.name" />
								</xsl:call-template>
							</xsl:if>

							<xsl:call-template name="DealSummary" />

						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>

	<!--************** END: Government Regulations *****************-->

	<xsl:template name="DealSummary">
		<xsl:text>&nbsp;&#x0D;&#x0A;</xsl:text>
		<!-- Thomson Reuters Deal Number -->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="stringValue1" select="'Thomson Reuters Deal Number:'" />
			<xsl:with-param name="stringValue2">
				<xsl:apply-templates select="/Document/n-docbody/MergAndAcqDocument/MasterIndex/MasterIndexRecord/deal.no" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>


	<!-- Template that matches all string values -->
	<xsl:template match="text()" priority="1">
		<xsl:call-template name="VerifyStringValue">
			<xsl:with-param name="stringValue" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--VerifyStringValue-->
	<xsl:template name="VerifyStringValue">
		<xsl:param name="stringValue"/>

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
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--DisplayStringValuesInOneColumn-->
	<xsl:template name="DisplayStringValuesInOneColumn">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="truncate" />
		<div>
			<xsl:if test="$truncate=string(true())">
				<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="displayAsLabelOrValue">
				<xsl:with-param name="columnHeader" select="true()" />
				<xsl:with-param name="stringValue" select="$stringValue1" />
			</xsl:call-template>
			<xsl:call-template name="displayAsLabelOrValue">
				<xsl:with-param name="columnHeader" select="false()" />
				<xsl:with-param name="stringValue" select="$stringValue2" />
			</xsl:call-template>
		</div>

		<xsl:if test="$truncate=string(true())">
			<xsl:call-template name="MoreLink">
				<xsl:with-param name="count" select="string-length($stringValue2)" />
				<xsl:with-param name="threshold" select="1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--DisplayMultipleStringValuesInOneColumn-->
	<xsl:template name="DisplayMultipleStringValuesInOneColumn">
		<xsl:param name="label"/>
		<xsl:param name="values"/>
		<xsl:param name="distinct" select="false()"/>

		<xsl:choose>
			<xsl:when test="count($values) = 1">
				<xsl:call-template name="DisplayStringValuesInOneColumn">
					<xsl:with-param name="stringValue1" select="$label" />
					<xsl:with-param name="stringValue2" select="$values[1]"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<table class="&blcNestedTable;">
					<tr>
						<td>
							<xsl:call-template name="DisplayLabel">
								<xsl:with-param name="text" select="$label" />
							</xsl:call-template>
						</td>
						<td class="&blcNestedTableCell;">
							<xsl:choose>
								<xsl:when test="$distinct=false()">
									<xsl:for-each select="$values">
										<div>
											<xsl:value-of select="text()"/>
										</div>
									</xsl:for-each>
								</xsl:when>
								<xsl:otherwise>
									<xsl:for-each select="$values">
										<xsl:if test="generate-id() = generate-id($values[. = current()][1])">
											<div>
												<xsl:value-of select="text()"/>
											</div>
										</xsl:if>
									</xsl:for-each>
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
				</table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--DisplayStringValueIn2Columns-->
	<xsl:template name="DisplayStringValueIn2Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="truncate" />

		<tr>
			<td class="&layout_col1;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="true()" />
						<xsl:with-param name="stringValue" select="$stringValue1" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:if test="$truncate=string(true())">
						<xsl:attribute name="class">&morelessTextStyle; &ellipsisClass;</xsl:attribute>
					</xsl:if>

					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="false()" />
						<xsl:with-param name="stringValue" select="$stringValue2" />
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

	<!--DisplayStringValueIn3Columns-->
	<xsl:template name="DisplayStringValueIn3Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="stringValue3"/>
		<xsl:param name="columnHeader"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue1" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue2" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue3" />
					</xsl:call-template>
				</div>
			</td>
		</tr>

		<!-- Add a blank line after headers -->
		<xsl:if test="$columnHeader=string(true())">
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--DisplayStringValueIn4Columns-->
	<xsl:template name="DisplayStringValueIn4Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="stringValue3"/>
		<xsl:param name="stringValue4"/>
		<xsl:param name="column1Header"/>
		<xsl:param name="column2Header"/>
		<xsl:param name="column3Header"/>
		<xsl:param name="column4Header"/>

		<tr>
			<td class="&layout_col1;" style="width: 30%;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$column1Header" />
						<xsl:with-param name="stringValue" select="$stringValue1" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col2;" style="width: 20%;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$column2Header" />
						<xsl:with-param name="stringValue" select="$stringValue2" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col3;" style="width: 20%;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$column3Header" />
						<xsl:with-param name="stringValue" select="$stringValue3" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col4;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$column4Header" />
						<xsl:with-param name="stringValue" select="$stringValue4" />
					</xsl:call-template>
				</div>
			</td>
		</tr>

		<!-- Add a blank line after headers -->
		<xsl:if test="$column1Header=string(true()) and $column2Header=string(true()) and $column3Header=string(true()) and $column4Header=string(true())">
			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--DisplayStringValueIn5Columns-->
	<xsl:template name="DisplayStringValueIn5Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="stringValue3"/>
		<xsl:param name="stringValue4"/>
		<xsl:param name="stringValue5"/>
		<xsl:param name="columnHeader"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue1" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue2" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue3" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col4;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue4" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col5;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue5" />
					</xsl:call-template>
				</div>
			</td>
		</tr>

		<!-- Add a blank line after headers -->
		<xsl:if test="$columnHeader=string(true())">
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--DisplayStringValueIn6Columns-->
	<xsl:template name="DisplayStringValueIn6Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="stringValue2"/>
		<xsl:param name="stringValue3"/>
		<xsl:param name="stringValue4"/>
		<xsl:param name="stringValue5"/>
		<xsl:param name="stringValue6"/>
		<xsl:param name="columnHeader"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue1" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue2" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col3;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue3" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col4;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue4" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col5;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue5" />
					</xsl:call-template>
				</div>
			</td>

			<td class="&layout_col6;">
				<div>
					<xsl:call-template name="displayAsLabelOrValue">
						<xsl:with-param name="columnHeader" select="$columnHeader" />
						<xsl:with-param name="stringValue" select="$stringValue6" />
					</xsl:call-template>
				</div>
			</td>
		</tr>

		<!-- Add a blank line after headers -->
		<xsl:if test="$columnHeader=string(true())">
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- displayAsLabelOrValue -->
	<xsl:template name="displayAsLabelOrValue">
		<xsl:param name="columnHeader" />
		<xsl:param name="stringValue" />

		<xsl:choose>
			<xsl:when test="$columnHeader=string(true())">
				<strong>
					<xsl:call-template name="VerifyStringValue">
						<xsl:with-param name="stringValue" select="$stringValue" />
					</xsl:call-template>
					<xsl:text>&nbsp;&nbsp;</xsl:text>
				</strong>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="VerifyStringValue">
					<xsl:with-param name="stringValue" select="$stringValue" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--DisplayDateValuesInOneColumn-->
	<xsl:template name="DisplayDateValuesInOneColumn">
		<xsl:param name="stringValue1"/>
		<xsl:param name="dateValue"/>
		<xsl:param name="parseYearFirst"/>
		<div>
			<xsl:call-template name="DisplayLabel">
				<xsl:with-param name="text" select="$stringValue1" />
			</xsl:call-template>
			<xsl:call-template name="FormatStringAsDate">
				<xsl:with-param name="dateString" select="$dateValue" />
				<xsl:with-param name="parseYearFirst" select="$parseYearFirst" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--DisplayDateValueIn2Columns-->
	<xsl:template name="DisplayDateValueIn2Columns">
		<xsl:param name="stringValue1"/>
		<xsl:param name="dateValue"/>
		<xsl:param name="parseYearFirst"/>

		<tr>
			<td class="&layout_col1;">
				<div>
					<h4>
						<xsl:value-of select="$stringValue1"/>
					</h4>
				</div>
			</td>

			<td class="&layout_col2;">
				<div>
					<xsl:call-template name="FormatStringAsDate">
						<xsl:with-param name="dateString" select="$dateValue" />
						<xsl:with-param name="parseYearFirst" select="$parseYearFirst" />
					</xsl:call-template>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!-- FormatStringAsDate -->
	<xsl:template name="FormatStringAsDate">
		<xsl:param name="dateString" />
		<xsl:param name="parseYearFirst"/>

		<xsl:choose>
			<xsl:when test="string-length($dateString) &gt; 7	and number($dateString) != 'NaN'">
				<xsl:choose>
					<xsl:when test="$parseYearFirst">
						<xsl:call-template name="parseYearMonthDayDateFormat">
							<xsl:with-param name="date" select="$dateString"/>
							<xsl:with-param name="displayDay" select="1"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="parseMonthDayYearDateFormat">
							<xsl:with-param name="date" select="$dateString"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
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
		<xsl:param name="suffix"/>
		<xsl:param name="hideAllIfOneValueUnavailable"/>

		<xsl:choose>
			<xsl:when test="($hideAllIfOneValueUnavailable=false()) and (string-length($value1) = 0 or $value1 = $na-answer) and (string-length($value2) != 0 and $value2 != $na-answer)">
				<xsl:copy-of select="$value2"/>
			</xsl:when>
			<xsl:when test="($hideAllIfOneValueUnavailable=false()) and (string-length($value2) = 0 or $value2 = $na-answer) and (string-length($value1) != 0 and $value1 != $na-answer)">
				<xsl:copy-of select="$value1"/>
			</xsl:when>
			<xsl:when test="string-length($value1) != 0 and $value1 != $na-answer and string-length($value2) != 0 and $value2 != $na-answer">
				<xsl:copy-of select="$value1"/>
				<xsl:value-of select="$delimiter"/>
				<xsl:copy-of select="$value2"/>
				<xsl:value-of select="$suffix"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
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

	<!-- FormatNumber -->
	<xsl:template name="FormatNumber">
		<xsl:param name="value" />

		<xsl:call-template name="Format">
			<xsl:with-param name="value" select="$value" />
			<xsl:with-param name="format" select="'#,###'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Format -->
	<xsl:template name="Format">
		<xsl:param name="value" />
		<xsl:param name="format" />

		<xsl:choose>
			<xsl:when test="string-length($value) > 0 and number($value) != 'NaN' and $value != '0'">
				<xsl:value-of select="format-number($value, $format)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$na-answer"/>
			</xsl:otherwise>
		</xsl:choose>
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

	<!--********** END: Utility templates **********-->

</xsl:stylesheet>