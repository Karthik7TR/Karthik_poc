<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
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
	<xsl:include href="Transactions.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<xsl:variable name ="financial.info" select="/Document/n-metadata/md.financial.info.metadata.block" />
	<xsl:variable name ="filing.metadata" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block" />
	<xsl:variable name ="filing.company" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.company.info" />

	<xsl:variable name ="registration" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block" />
	<xsl:variable name ="issuer" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block/md.issuer" />
	<xsl:variable name ="filing.value" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.filing/md.filing.values/md.filing.value" />
	<xsl:variable name ="natures" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block/md.natures" />

	<xsl:variable name ="prospectuses" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block/md.prospectuses" />
	<xsl:variable name ="related-parties" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block/md.RP.related.parties" />

	<xsl:variable name ="nature" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block/md.natures/md.nature" />
	<xsl:variable name ="security" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.registrations.metadata.block/md.securities/md.security" />

	<xsl:variable name ="company-data" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.filing/md.filing.values/md.filing.value/md.company.data" />


	<!-- main template match -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeRandPTransactions;'"/>
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
		<div id="&documentLinksContainer;"></div>
		<xsl:call-template name="IssuerInformationSection"/>
		<xsl:call-template name="DealInformationSection"/>
		<xsl:call-template name="ProspectusInformationSection"/>
		<xsl:call-template name="RelatedPartiesInformationSection"/>
		<xsl:call-template name="FeesInformationSection"/>

	</xsl:template>



	<!--
	**************************************************************************************
	*		Issuer Section                                                              *
	**************************************************************************************
	-->

	<!-- ISSUER INFORMATION -->
	<xsl:template name="IssuerInformationSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr>
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Issuer Information</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Issuer-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Company Name:'"/>
								<xsl:with-param name="param2" select="string-length($issuer/md.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuer/md.name"/>
							</xsl:call-template>

							<!--Exchange-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Exchange:'"/>
								<xsl:with-param name="param2" select="string-length($company-data/md.stock.exchange) &gt; 0"/>
								<xsl:with-param name="param3" select="$company-data/md.stock.exchange"/>
							</xsl:call-template>

							<!--SIC Code-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'SIC Code:'"/>
								<xsl:with-param name="param2" select="string-length($issuer/md.sic.code) &gt; 0 or string-length($issuer/md.sic.code.description) &gt; 0"/>
								<xsl:with-param name="param3" select="concat($issuer/md.sic.code, ' - ', $issuer/md.sic.code.description)"/>
							</xsl:call-template>

							<!--LOI-->
							<xsl:choose>
								<xsl:when test="string-length($issuer/md.RP.state.of.incorporation/text()) &gt; 0">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Location of Incorporation:'"/>
										<xsl:with-param name="param2" select="string-length($issuer/md.RP.state.of.incorporation/text()) &gt; 0"/>
										<xsl:with-param name="param3" select="$issuer/md.RP.state.of.incorporation/text()"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="string-length($issuer/md.country.of.incorporation/text()) &gt; 0">
									<xsl:call-template name="DisplayStringValuesInOneColumn">
										<xsl:with-param name="param1" select="'Location of Incorporation:'"/>
										<xsl:with-param name="param2" select="string-length($issuer/md.country.of.incorporation/text()) &gt; 0"/>
										<xsl:with-param name="param3" select="$issuer/md.country.of.incorporation/text()"/>
									</xsl:call-template>
								</xsl:when>
							</xsl:choose>

							<!--LOH-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Location of Headquarters:'"/>
								<xsl:with-param name="param2" select="string-length($issuer/md.address/md.state/text()) &gt; 0"/>
								<xsl:with-param name="param3" select="$issuer/md.address/md.state/text()"/>
							</xsl:call-template>

							<!--Market Cap-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Market Cap (USD):'"/>
								<xsl:with-param name="param2" select="string-length($company-data/md.financial.block/md.market.cap) = 0"/>
								<xsl:with-param name="param3" select="$company-data/md.financial.block/md.market.cap=0"/>
								<xsl:with-param name="param4" select="string(number($company-data/md.financial.block/md.market.cap)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($company-data/md.financial.block/md.market.cap, '#,###')"/>
							</xsl:call-template>

							<!--Net Income-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Net Income (USD):'"/>
								<xsl:with-param name="param2" select="string-length($company-data/md.financial.block/md.net.income) = 0"/>
								<xsl:with-param name="param3" select="$company-data/md.financial.block/md.net.income=0"/>
								<xsl:with-param name="param4" select="string(number($company-data/md.financial.block/md.net.income)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($company-data/md.financial.block/md.net.income, '#,###')"/>
							</xsl:call-template>

							<!--Revenue-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Revenue (USD):'"/>
								<xsl:with-param name="param2" select="string-length($company-data/md.financial.block/md.revenue) = 0"/>
								<xsl:with-param name="param3" select="$company-data/md.financial.block/md.revenue=0"/>
								<xsl:with-param name="param4" select="string(number($company-data/md.financial.block/md.revenue)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($company-data/md.financial.block/md.revenue, '#,###')"/>
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>



	<!--
	**************************************************************************************
	*		Deal Section                                                              *
	**************************************************************************************
	-->

	<!-- DEAL INFORMATION -->
	<xsl:template name="DealInformationSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Deal Information</h3>
						</td>
						<td class="&blcWidth75;">

							<!--SEC File Number-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'SEC File Number:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.filenum) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.filenum"/>
							</xsl:call-template>

							<!--Status-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Status:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.RP.status.description) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.RP.status.description"/>
							</xsl:call-template>

							<!--Registration Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Registration Date:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.RP.filedate) &gt; 7	and number($registration/md.RP.filedate) != 'NaN'"/>
								<xsl:with-param name="param3" select="$registration/md.RP.filedate/text()"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Effective Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Effective Date:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.effective.date) &gt; 7	and number($registration/md.effective.date) != 'NaN'"/>
								<xsl:with-param name="param3" select="$registration/md.effective.date/text()"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Form Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Form Type:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.RP.formtype) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.RP.formtype"/>
							</xsl:call-template>

							<!--Shelf Registration-->
							<xsl:call-template name="DisplayBooleanValuesInOneColumn">
								<xsl:with-param name="param1" select="'Shelf Registration:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.shelf.flag) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.shelf.flag = 'T'"/>
								<xsl:with-param name="param4" select="$registration/md.shelf.flag = 'F'"/>
							</xsl:call-template>

							<!--Initial Public Offering-->
							<xsl:call-template name="DisplayBooleanValuesInOneColumn">
								<xsl:with-param name="param1" select="'Initial Public Offering:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.ipo.flag) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.ipo.flag = 'T'"/>
								<xsl:with-param name="param4" select="$registration/md.ipo.flag = 'F'"/>
							</xsl:call-template>

							<!--Multiple Descriptions-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Deal Description:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.natures/md.nature/md.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.natures/md.nature/md.name"/>
							</xsl:call-template>

							<!--Offering Type-->
							<xsl:call-template name="DisplayStringValuesInOneColumn">
								<xsl:with-param name="param1" select="'Offering Type:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.offertype) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.offertype"/>
							</xsl:call-template>

							<!--Multiple Securities-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Security:'"/>
								<xsl:with-param name="param2" select="string-length($security/md.security.description) &gt; 0"/>
								<xsl:with-param name="param3" select="$security/md.security.description"/>
							</xsl:call-template>

							<!--Maximum Offering Price-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Maximum Offering Price (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.max.offering.price) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.max.offering.price=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.max.offering.price)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.max.offering.price, '#,###')"/>
							</xsl:call-template>

							<!--Total Takedown-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Takedown (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.total.takedowns) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.total.takedowns=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.total.takedowns)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.total.takedowns, '#,###')"/>
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>



	<!--
	**************************************************************************************
	*		Prospectus Section                                                              *
	**************************************************************************************
	-->

	<!-- PROSPECTUS SECTION -->
	<xsl:template name="ProspectusInformationSection">
		<xsl:choose>
			<!--Test for prospectus nodes-->
			<xsl:when test="count($prospectuses/md.prospectus) &gt; 0">
				<xsl:call-template name="ProspectusCounter">
					<xsl:with-param name="counter" select="0" />
					<xsl:with-param name="self" select="$prospectuses/md.prospectus[1]" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="ProspectusCounter">
		<xsl:param name="counter" />
		<xsl:param name="self" />

		<xsl:variable name="selfCounter">
			<xsl:choose>
				<xsl:when test="count($self/md.tranches) &gt; 0">
					<xsl:value-of select="$counter+1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$counter"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="count($self/md.tranches) &gt; 0">
			<xsl:call-template name="ProspectusInformation">
				<xsl:with-param name="prospectusNumber" select="$selfCounter"/>
				<xsl:with-param name="currentProspectus" select="$self" />
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="$self/following-sibling::md.prospectus[1]">
			<xsl:call-template name="ProspectusCounter">
				<xsl:with-param name="counter" select="$selfCounter" />
				<xsl:with-param name="self" select="$self/following-sibling::md.prospectus[1]" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- PROSPECTUS INFORMATION -->
	<xsl:template name="ProspectusInformation">
		<xsl:param name="prospectusNumber" />
		<xsl:param name="currentProspectus" />

		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>
								<xsl:text>Prospectus</xsl:text>
								<xsl:text>&nbsp;</xsl:text>
								<xsl:value-of select="$prospectusNumber"/>
							</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Announcement Date-->
							<xsl:call-template name="DisplayDateValuesInOneColumn">
								<xsl:with-param name="param1" select="'Prospectus Date:'"/>
								<xsl:with-param name="param2" select="string-length($currentProspectus/md.prospectus.date) &gt; 7	and number($currentProspectus/md.prospectus.date) != 'NaN'"/>
								<xsl:with-param name="param3" select="$currentProspectus/md.prospectus.date/text()"/>
								<xsl:with-param name="yearFirst" select="1"/>
							</xsl:call-template>

							<!--Total Debt Offerred-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Debt Offerred (USD):'"/>
								<xsl:with-param name="param2" select="string-length($currentProspectus/md.total.debt.offered) = 0"/>
								<xsl:with-param name="param3" select="$currentProspectus/md.total.debt.offered=0"/>
								<xsl:with-param name="param4" select="string(number($currentProspectus/md.total.debt.offered)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($currentProspectus/md.total.debt.offered, '#,###')"/>
							</xsl:call-template>

							<!--Equity Overallotment-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Equity Overallotment:'"/>
								<xsl:with-param name="param2" select="string-length($currentProspectus/md.equity.overallotment) = 0"/>
								<xsl:with-param name="param3" select="$currentProspectus/md.equity.overallotment=0"/>
								<xsl:with-param name="param4" select="string(number($currentProspectus/md.equity.overallotment)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($currentProspectus/md.equity.overallotment, '#,###')"/>
							</xsl:call-template>

							<!--Debt Overallotment-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Debt Overallotment:'"/>
								<xsl:with-param name="param2" select="string-length($currentProspectus/md.debt.overallotment) = 0"/>
								<xsl:with-param name="param3" select="$currentProspectus/md.debt.overallotment=0"/>
								<xsl:with-param name="param4" select="string(number($currentProspectus/md.debt.overallotment)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($currentProspectus/md.debt.overallotment, '#,###')"/>
							</xsl:call-template>

							<!--Loop all tranches in the prospectus-->
							<xsl:choose>
								<!--Test for tranche nodes-->
								<xsl:when test="count($currentProspectus/md.tranches/md.tranche) &gt; 0">
									<!--Loop all potential tranche nodes-->
									<xsl:for-each select="$currentProspectus/md.tranches/md.tranche">
										<xsl:call-template name="TrancheInformation"/>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>

						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>


	<!--
	**************************************************************************************
	*		Tranche Information Section                                                              *
	**************************************************************************************
	-->

	<!-- TRANCHE INFORMATION -->
	<xsl:template name="TrancheInformation">
		<h4 class="&blcSectionSubheading;">Tranche</h4>

		<!--Security-->
		<xsl:call-template name="DisplayStringValuesInOneColumn">
			<xsl:with-param name="param1" select="'Security Type'"/>
			<xsl:with-param name="param2" select="string-length(./md.security.type.description/text()) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.security.type.description/text()"/>
		</xsl:call-template>

		<!--Number of Shares Issued-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Number of Shares Issued:'"/>
			<xsl:with-param name="param2" select="string-length(./md.number.shares.issued) = 0"/>
			<xsl:with-param name="param3" select="./md.number.shares.issued=0"/>
			<xsl:with-param name="param4" select="string(number(./md.number.shares.issued)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.number.shares.issued, '#,###')"/>
		</xsl:call-template>

		<!--Price per Share-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Price per Share (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.price.per.share) = 0"/>
			<xsl:with-param name="param3" select="./md.price.per.share=0"/>
			<xsl:with-param name="param4" select="string(number(./md.price.per.share)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.price.per.share, '#,###0.00')"/>
		</xsl:call-template>

		<!--Underwriter Discount per Share-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Underwriter Discount per Share (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.uw.disc.per.share) = 0"/>
			<xsl:with-param name="param3" select="./md.uw.disc.per.share=0"/>
			<xsl:with-param name="param4" select="string(number(./md.uw.disc.per.share)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.uw.disc.per.share, '#,##0.00')"/>
		</xsl:call-template>

		<!--Investor Price-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Investor Price (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.investor.price) = 0"/>
			<xsl:with-param name="param3" select="./md.investor.price=0"/>
			<xsl:with-param name="param4" select="string(number(./md.investor.price)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.investor.price, '#,##0.00')"/>
		</xsl:call-template>

		<!--Total Price-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Total Price (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.total.price) = 0"/>
			<xsl:with-param name="param3" select="./md.total.price=0"/>
			<xsl:with-param name="param4" select="string(number(./md.total.price)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.total.price, '#,###')"/>
		</xsl:call-template>

		<!--Total Underwriter Discount-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Total Underwriter Discount (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.total.uw.disc) = 0"/>
			<xsl:with-param name="param3" select="./md.total.uw.disc=0"/>
			<xsl:with-param name="param4" select="string(number(./md.total.uw.disc)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.total.uw.disc, '#,###')"/>
		</xsl:call-template>

		<!--Total Proceeds - Company-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Total Proceeds - Company (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.total.proceeds.company) = 0"/>
			<xsl:with-param name="param3" select="./md.total.proceeds.company=0"/>
			<xsl:with-param name="param4" select="string(number(./md.total.proceeds.company)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.total.proceeds.company, '#,###')"/>
		</xsl:call-template>

		<!--Total Proceeds - Seller-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Total Proceeds - Seller (USD):'"/>
			<xsl:with-param name="param2" select="string-length(./md.total.proceeds.seller) = 0"/>
			<xsl:with-param name="param3" select="./md.total.proceeds.seller=0"/>
			<xsl:with-param name="param4" select="string(number(./md.total.proceeds.seller)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.total.proceeds.seller, '#,###')"/>
		</xsl:call-template>

		<!--Coupon Rate-->
		<xsl:call-template name="DisplayNumberValuesInOneColumn">
			<xsl:with-param name="param1" select="'Coupon Rate (%):'"/>
			<xsl:with-param name="param2" select="string-length(./md.coupon.rate) = 0"/>
			<xsl:with-param name="param3" select="./md.coupon.rate=0"/>
			<xsl:with-param name="param4" select="string(number(./md.coupon.rate)) != 'NaN'"/>
			<xsl:with-param name="param5" select="format-number(./md.coupon.rate, '#,###0.0000')"/>
		</xsl:call-template>

		<!--Maturity Date-->
		<xsl:call-template name="DisplayDateValuesInOneColumn">
			<xsl:with-param name="param1" select="'Maturity Date:'"/>
			<xsl:with-param name="param2" select="string-length(./md.maturity.date) &gt; 7	and number(./md.maturity.date) != 'NaN'"/>
			<xsl:with-param name="param3" select="./md.maturity.date/text()"/>
			<xsl:with-param name="yearFirst" select="1"/>
		</xsl:call-template>
	</xsl:template>




	<!--
	**************************************************************************************
	*		Related Parties Section                                                              *
	**************************************************************************************
	-->

	<!-- RELATED PARTIES INFORMATION -->
	<xsl:template name="RelatedPartiesInformationSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Related Parties</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Underwriter-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Underwriter:'"/>
								<xsl:with-param name="param2" select="string-length($related-parties/md.RP.uwriter.related.party/md.RP.firm.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$related-parties/md.RP.uwriter.related.party/md.RP.firm.name"/>
							</xsl:call-template>

							<!--Accountant/Auditor-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Accountant/Auditor:'"/>
								<xsl:with-param name="param2" select="string-length($related-parties/md.RP.acct.related.party/md.RP.firm.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$related-parties/md.RP.acct.related.party/md.RP.firm.name"/>
							</xsl:call-template>

							<!--Issuer Law Firm-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Issuer Law Firm:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.issuercounsel/md.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.issuercounsel/md.name"/>
							</xsl:call-template>

							<!--Underwriter Law Firm-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Underwriter Law Firm:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.underwritercounsel/md.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.underwritercounsel/md.name"/>
							</xsl:call-template>

							<!--Other Law Firm-->
							<xsl:call-template name="DisplayMultipleSingleElementsInOneColumn">
								<xsl:with-param name="param1" select="'Other Law Firm:'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.othercounsel/md.name) &gt; 0"/>
								<xsl:with-param name="param3" select="$registration/md.othercounsel/md.name"/>
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>






	<!--
	**************************************************************************************
	*		Fees Section                                                              *
	**************************************************************************************
	-->

	<!-- FEES INFORMATION -->
	<xsl:template name="FeesInformationSection">
		<div>
			<table class="&layout_table; &blcPortfolioTable;">
				<tbody>
					<tr class="&blcBorderTop;">
						<td class="&blcWidth25; &blcSectionHeading;">
							<h3>Fees</h3>
						</td>
						<td class="&blcWidth75;">

							<!--Legal Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Legal Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.legal.fee) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.legal.fee=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.legal.fee)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.legal.fee, '#,###')"/>
							</xsl:call-template>

							<!--Accounting Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Accounting Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.auditor.fee) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.auditor.fee=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.auditor.fee)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.auditor.fee, '#,###')"/>
							</xsl:call-template>

							<!--Printing Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Printing Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.printing.fee) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.printing.fee=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.printing.fee)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.printing.fee, '#,###')"/>
							</xsl:call-template>

							<!--Blue Sky Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Blue Sky Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.bluesky.fee) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.bluesky.fee=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.bluesky.fee)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.bluesky.fee, '#,###')"/>
							</xsl:call-template>

							<!--Rating Agency Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Rating Agency Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.rating.fee) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.rating.fee=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.rating.fee)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.rating.fee, '#,###')"/>
							</xsl:call-template>

							<!--SEC Filing Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'SEC Filing Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.sec.filing.fee) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.sec.filing.fee=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.sec.filing.fee)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.sec.filing.fee, '#,###')"/>
							</xsl:call-template>

							<!--Total Fees-->
							<xsl:call-template name="DisplayNumberValuesInOneColumn">
								<xsl:with-param name="param1" select="'Total Fees (USD):'"/>
								<xsl:with-param name="param2" select="string-length($registration/md.total.fee.parts) = 0"/>
								<xsl:with-param name="param3" select="$registration/md.total.fee.parts=0"/>
								<xsl:with-param name="param4" select="string(number($registration/md.total.fee.parts)) != 'NaN'"/>
								<xsl:with-param name="param5" select="format-number($registration/md.total.fee.parts, '#,###')"/>
							</xsl:call-template>

						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</xsl:template>


</xsl:stylesheet>
