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


	<!--<xsl:variable name ="mergers.metadata" select="//entry[key='novus']/value/n-metadata/md.mergers.metadata.block" />-->
	<xsl:variable name ="mergers.metadata" select="/Document/n-metadata/md.mergers.metadata.block" />
	<xsl:variable name ="mergers" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers" />

	<xsl:variable name ="merger" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger" />
	<xsl:variable name ="events" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.events" />
	<xsl:variable name ="parties" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties" />
	<xsl:variable name ="party" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.merger.party" />

	<!--law firms-->
	<xsl:variable name ="t-counsel" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.related.merger.parties/md.target.counsel" />
	<xsl:variable name ="a-counsel" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.related.merger.parties/md.acquirer.counsel" />

	<!--financial counsels-->
	<xsl:variable name ="tfa" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.related.merger.parties/md.target.financial.advisor" />
	<xsl:variable name ="afa" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.related.merger.parties/md.acquirer.financial.advisor" />

	<!-- accountants -->
	<xsl:variable name ="t-accountant" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.related.merger.parties/md.target.accountant" />
	<xsl:variable name ="a-accountant" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.merger.parties/md.related.merger.parties/md.acquirer.accountant" />

	<!--considerations-->
	<xsl:variable name ="consideration" select="/Document/n-metadata/md.mergers.metadata.block/md.mergers/md.linkid.block/md.linkid/md.consideration" />


	<!-- main template match -->
	<xsl:template match="Document">
		<div id="&documentId;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&contentTypeMandATransactions;'"/>
      </xsl:call-template>
			<xsl:call-template name="Content"/>
		</div>
	</xsl:template>


	<!--
		**************************************************************************************
		*
		*		Render the XML for content.  Call templates to render each of the sections.      *
		**************************************************************************************
	-->
	<xsl:template name="Content">

		<xsl:call-template name="DealSection"/>
		<xsl:call-template name="TargetSection"/>
		<xsl:call-template name="AcquirerSection"/>
		<xsl:call-template name="ConsiderationSummarySection"/>
		<xsl:call-template name="RelatedPartiesSection"/>

	</xsl:template>



	<!--
	**************************************************************************************
	*		Deal Summary Section                                                              *
	**************************************************************************************
	-->
	<xsl:template name="DealSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h3>
					<xsl:text>Deal Summary</xsl:text>
				</h3>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table>

					<!--Announcement Date-->
					<xsl:call-template name="VerifyDate">
						<xsl:with-param name="param1" select="'Announcement Date:'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.announcedate) &gt; 7	and number($merger/md.announcedate) != 'NaN'"/>
						<xsl:with-param name="param3" select="$merger/md.announcedate/text()"/>
						<xsl:with-param name="yearFirst" select="1"/>
					</xsl:call-template>

					<!--Commencement Date-->
					<xsl:call-template name="VerifyDate">
						<xsl:with-param name="param1" select="'Commencement Date:'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.MA.filedate) &gt; 7 and number($merger/md.MA.filedate) != 'NaN'"/>
						<xsl:with-param name="param3" select="$merger/md.MA.filedate/text()"/>
						<xsl:with-param name="yearFirst" select="1"/>
					</xsl:call-template>

					<!--End Date-->
					<xsl:call-template name="VerifyDate">
						<xsl:with-param name="param1" select="'End Date:'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.completiondate) &gt; 7	and number($merger/md.completiondate) != 'NaN'"/>
						<xsl:with-param name="param3" select="$merger/md.completiondate/text()"/>
						<xsl:with-param name="yearFirst" select="1"/>
					</xsl:call-template>

					<!--Status-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Status:'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.MA.status) &gt; 0"/>
						<xsl:with-param name="param3" select="$merger/md.MA.status"/>
					</xsl:call-template>

					<!--Transaction Value-->
					<xsl:call-template name="VerifyNumber">
						<xsl:with-param name="param1" select="'Transaction Value (USD):'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.totalvalue) = 0"/>
						<xsl:with-param name="param3" select="$merger/md.totalvalue=0"/>
						<xsl:with-param name="param4" select="string(number($merger/md.totalvalue)) != 'NaN'"/>
						<xsl:with-param name="param5" select="format-number($merger/md.totalvalue, '#,###')"/>
					</xsl:call-template>

					<!--Termination Fee-->
					<xsl:call-template name="VerifyNumber">
						<xsl:with-param name="param1" select="'Termination Fee (USD):'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.terminationfee) = 0"/>
						<xsl:with-param name="param3" select="$merger/md.terminationfee=0"/>
						<xsl:with-param name="param4" select="string(number($merger/md.terminationfee)) != 'NaN'"/>
						<xsl:with-param name="param5" select="format-number($merger/md.terminationfee, '#,###')"/>
					</xsl:call-template>

					<!--Multiple Descriptions-->
					<xsl:call-template name="VerifyMultipleSingleElements">
						<xsl:with-param name="param1" select="'Deal Description:'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.mergerdescrip/md.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$merger/md.mergerdescrip/md.description"/>
					</xsl:call-template>

					<!--Target Response-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Target Response:'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.targetresponse/md.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$merger/md.targetresponse/md.description"/>
					</xsl:call-template>

					<!--Condition(s)-->
					<xsl:call-template name="VerifyMultipleSingleElements">
						<xsl:with-param name="param1" select="'Condition(s):'"/>
						<xsl:with-param name="param2" select="string-length($merger/md.transactionconditions/md.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$merger/md.transactionconditions/md.description"/>
					</xsl:call-template>

				</table>

			</div>
		</div>
	</xsl:template>

	<!--
	**************************************************************************************
	*		Target Section                                                              *
	**************************************************************************************
	-->
	<xsl:template name="TargetSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h3>
					<xsl:text>Target</xsl:text>
				</h3>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table>

					<xsl:choose>
						<!--Test for 1st structure-->
						<xsl:when test="count($parties/md.merger.party) &gt; 0">
							<!--Structure 1-->
							<!--Loop all potential nodes-->
							<xsl:for-each select="$parties/md.merger.party">
								<xsl:choose>
									<!--Choose only Acquirer type nodes-->
									<xsl:when test="./md.merger.party.type = 'T'">
										<xsl:call-template name="MergerPartyInfo" />
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
						<!--Test for 1st structure-->
						<xsl:when test="count($parties/md.target.party) &gt; 0">
							<!--Structure 2-->
							<!--Loop all potential nodes-->
							<xsl:for-each select="$parties/md.target.party">
								<xsl:choose>
									<!--Choose only Acquirer type nodes-->
									<xsl:when test="./md.merger.party.type = 'T'">
										<xsl:call-template name="MergerPartyInfo" />
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
					</xsl:choose>

				</table>

			</div>
		</div>
	</xsl:template>

	<!--
	**************************************************************************************
	*		Acquirer Section                                                              *
	**************************************************************************************
	-->
	<xsl:template name="AcquirerSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h3>
					<xsl:text>Acquirer</xsl:text>
				</h3>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table>

					<xsl:choose>
						<!--Test for 1st structure-->
						<xsl:when test="count($parties/md.merger.party) &gt; 0">
							<!--Structure 1-->
							<!--Loop all potential nodes-->
							<xsl:for-each select="$parties/md.merger.party">
								<xsl:choose>
									<!--Choose only Acquirer type nodes-->
									<xsl:when test="./md.merger.party.type = 'A'">
										<xsl:call-template name="MergerPartyInfo" />
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
						<!--Test for 2nd structure-->
						<xsl:when test="count($parties/md.acquirer.party) &gt; 0">
							<!--Structure 2-->
							<!--Loop all potential nodes-->
							<xsl:for-each select="$parties/md.acquirer.party">
								<xsl:choose>
									<!--Choose only Acquirer type nodes-->
									<xsl:when test="./md.merger.party.type = 'A'">
										<xsl:call-template name="MergerPartyInfo" />
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
					</xsl:choose>

				</table>

			</div>
		</div>
	</xsl:template>

	<xsl:template name="MergerPartyInfo">
		<!--Name-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'Name:'"/>
			<xsl:with-param name="param2" select="string-length(./md.companyname) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.companyname"/>
		</xsl:call-template>

		<!--Entity Type-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'Entity Type:'"/>
			<xsl:with-param name="param2" select="string-length(./md.companytype/md.companydescription) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.companytype/md.companydescription"/>
		</xsl:call-template>

		<!--Ticker symbol-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'Ticker Symbol(s):'"/>
			<xsl:with-param name="param2" select="string-length(./md.ticker.info.block/md.ticker) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.ticker.info.block/md.ticker"/>
		</xsl:call-template>

		<!--Exchange-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'Exchange(s):'"/>
			<xsl:with-param name="param2" select="string-length(./md.ticker.info.block/md.MA.stock.exchange) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.ticker.info.block/md.MA.stock.exchange"/>
		</xsl:call-template>

		<!--SIC Code-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'SIC Code(s) and Description(s):'"/>
			<xsl:with-param name="param2" select="string-length(./md.sic.block/md.sic) &gt; 0 and string-length(./md.sic.block/md.sicdescription) &gt; 0"/>
			<xsl:with-param name="param3" select="concat(./md.sic.block/md.sic, ' - ', ./md.sic.block/md.sicdescription)"/>
		</xsl:call-template>

		<!--LOH Code-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'Location of Headquarters:'"/>
			<xsl:with-param name="param2" select="string-length(./md.business.address/md.state) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.business.address/md.state/text()"/>
		</xsl:call-template>

		<!--LOI Code-->
		<xsl:call-template name="VerifyString">
			<xsl:with-param name="param1" select="'Location of Incorporation:'"/>
			<xsl:with-param name="param2" select="string-length(./md.MA.state.of.incorporation) &gt; 0"/>
			<xsl:with-param name="param3" select="./md.MA.state.of.incorporation/text()"/>
		</xsl:call-template>
	</xsl:template>

	<!--
	**************************************************************************************
	*		Deal Details Section                                                              *
	**************************************************************************************
	-->
	<xsl:template name="ConsiderationSummarySection">
		<xsl:for-each select="$consideration">
			<div class="&layout_TransactionAbstractDocumentSection;">
				<div class="&layoutHeaderRow;">
					<h3>
						<xsl:text>Deal Details</xsl:text>
					</h3>
				</div>

				<div class="&layout_Row_MarginBottom;">

					<table>

						<!--Announcement Date-->
						<xsl:call-template name="VerifyDate">
							<xsl:with-param name="param1" select="'Consideration Date:'"/>
							<xsl:with-param name="param2" select="string-length($consideration/md.considerationdate) &gt; 7	and number($consideration/md.considerationdate) != 'NaN'"/>
							<xsl:with-param name="param3" select="./md.considerationdate/text()"/>
							<xsl:with-param name="yearFirst" select="1"/>
						</xsl:call-template>

						<!--Consideration-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Consideration:'"/>
							<xsl:with-param name="param2" select="string-length(./md.considerationdescrip) &gt; 0"/>
							<xsl:with-param name="param3" select="./md.considerationdescrip"/>
						</xsl:call-template>

						<!--Consideration Description-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Consideration Description:'"/>
							<xsl:with-param name="param2" select="string-length(./md.considerationdescription) &gt; 0"/>
							<xsl:with-param name="param3" select="./md.considerationdescription"/>
						</xsl:call-template>

						<!--Acquirer Consideration Offered-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Acquirer Consideration Offered:'"/>
							<xsl:with-param name="param2" select="string-length(./md.offered.description) &gt; 0"/>
							<xsl:with-param name="param3" select="./md.offered.description"/>
						</xsl:call-template>

						<!--For Target Consideration-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'For Target Consideration:'"/>
							<xsl:with-param name="param2" select="string-length(./md.targetsecuritydescrip) &gt; 0"/>
							<xsl:with-param name="param3" select="./md.targetsecuritydescrip"/>
						</xsl:call-template>

						<!--Target Market Cap-->
						<xsl:call-template name="VerifyNumber">
							<xsl:with-param name="param1" select="'Target Market Cap (USD):'"/>
							<xsl:with-param name="param2" select="string-length(./md.MA.market.cap) = 0"/>
							<xsl:with-param name="param3" select="./md.MA.market.cap=0"/>
							<xsl:with-param name="param4" select="string(number(./md.MA.market.cap)) != 'NaN'"/>
							<xsl:with-param name="param5" select="format-number(./md.MA.market.cap, '#,###')"/>
						</xsl:call-template>

						<!--Exchange Ratio-->
						<xsl:call-template name="VerifyNumber">
							<xsl:with-param name="param1" select="'Exchange Ratio:'"/>
							<xsl:with-param name="param2" select="string-length(./md.exchangeratio) = 0"/>
							<xsl:with-param name="param3" select="string(./md.exchangeratio) = '-1.#IND00'"/>
							<xsl:with-param name="param4" select="string(number(./md.exchangeratio=0)) != 'NaN'"/>
							<xsl:with-param name="param5" select="format-number(./md.exchangeratio, '#,##0.0000')"/>
						</xsl:call-template>

						<!--Equity Cash Equivalent-->
						<xsl:call-template name="VerifyNumber">
							<xsl:with-param name="param1" select="'Equity Cash Equivalent:'"/>
							<xsl:with-param name="param2" select="string-length(./md.equitycashequivalent) = 0"/>
							<xsl:with-param name="param3" select="./md.equitycashequivalent=0"/>
							<xsl:with-param name="param4" select="string(number(./md.equitycashequivalent=0)) != 'NaN'"/>
							<xsl:with-param name="param5" select="format-number(./md.equitycashequivalent, '#,##0.0000')"/>
						</xsl:call-template>

						<!--Premium Discount Percent-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Premium/Discount (%):'"/>
							<xsl:with-param name="param2" select="string-length(./md.percentdiscount) &gt; 0"/>
							<xsl:with-param name="param3" select="concat(format-number(./md.percentdiscount, '#,##0.0000'), ' %')"/>
						</xsl:call-template>

						<!--Premium Discount Percent-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Maximum Shares Accepted (%):'"/>
							<xsl:with-param name="param2" select="string-length(./md.percentmaxtender) &gt; 0"/>
							<xsl:with-param name="param3" select="concat(format-number(./md.percentmaxtender, '#,##0.0000'), ' %')"/>
						</xsl:call-template>

						<!--Maximum Value-->
						<xsl:call-template name="VerifyNumber">
							<xsl:with-param name="param1" select="'Max. Consideration Value (USD):'"/>
							<xsl:with-param name="param2" select="string-length(./md.maxconsidvalue) = 0"/>
							<xsl:with-param name="param3" select="./md.maxconsidvalue=0"/>
							<xsl:with-param name="param4" select="string(number(./md.maxconsidvalue)) != 'NaN'"/>
							<xsl:with-param name="param5" select="format-number(./md.maxconsidvalue, '#,###')"/>
						</xsl:call-template>

						<!--Amount Tendered-->
						<!--<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Amount Tendered (%):'"/>
							<xsl:with-param name="param2" select="string-length(./md.percenttendered) &gt; 0"/>
							<xsl:with-param name="param3" select="concat(format-number(./md.percenttendered, '#,##0.0000'), ' %')"/>
						</xsl:call-template>-->

						<!--Outstanding-->
						<!--<xsl:call-template name="Outstanding"/>-->

						<!--Diluted Outstanding-->
						<!--<xsl:call-template name="DilutedOutstanding"/>-->

						<!--Pre-Announce Benefit-->
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="'Pre-Announce Ben. Own.- All Acquirers (%):'"/>
							<xsl:with-param name="param2" select="string-length(./md.percentown) &gt; 0"/>
							<xsl:with-param name="param3" select="concat(format-number(./md.percentown, '#,##0.0000'), ' %')"/>
						</xsl:call-template>

						<!--Pre-Announce Price-->
						<xsl:call-template name="VerifyNumber">
							<xsl:with-param name="param1" select="'Target Pre-Announce Share/Unit Price (USD):'"/>
							<xsl:with-param name="param2" select="string-length(./md.target.preannouncestockprice) = 0"/>
							<xsl:with-param name="param3" select="./md.target.preannouncestockprice=0"/>
							<xsl:with-param name="param4" select="string(number(./md.target.preannouncestockprice)) != 'NaN'"/>
							<xsl:with-param name="param5" select="format-number(./md.target.preannouncestockprice, '#,##0.00')"/>
						</xsl:call-template>

						<!--Acquirer Pre-Announce Share/Unit Price (USD)-->
						<xsl:call-template name="VerifyNumber">
							<xsl:with-param name="param1" select="'Acquirer Pre-Announce Share/Unit Price (USD):'"/>
							<xsl:with-param name="param2" select="string-length(./md.acquirer.preannouncestockprice) = 0"/>
							<xsl:with-param name="param3" select="./md.acquirer.preannouncestockprice=0"/>
							<xsl:with-param name="param4" select="string(number(./md.acquirer.preannouncestockprice)) != 'NaN'"/>
							<xsl:with-param name="param5" select="format-number(./md.acquirer.preannouncestockprice, '#,###.##')"/>
						</xsl:call-template>

					</table>

				</div>
			</div>
		</xsl:for-each>
	</xsl:template>

	<!-- Diluted Outstanding -->
	<xsl:template name="DilutedOutstanding">
		<xsl:variable name="total">
			<xsl:choose>
				<xsl:when test="(string(number(./md.outstandingsecurities)) != 'NaN') and (string(number(./md.not.outstandingsecurities)) != 'NaN')">
					<xsl:value-of select="number(./md.not.outstandingsecurities) + number(./md.outstandingsecurities)"/>
				</xsl:when>
				<xsl:when test="string(number(./md.not.outstandingsecurities)) != 'NaN'">
					<xsl:value-of select="./md.not.outstandingsecurities"/>
				</xsl:when>
				<xsl:when test="string(number(./md.outstandingsecurities)) != 'NaN'">
					<xsl:value-of select="./md.outstandingsecurities"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'NaN'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<tr>
			<td class="&layout_TopAlignment;">
				<div>
					<h4>
						<xsl:text>Diluted Outstanding (USD):</xsl:text>
					</h4>
				</div>
			</td>

			<td class="&layout_TopAlignment;">

				<xsl:choose>
					<xsl:when test="string(number($total)) = 'NaN'">
						<div>
							<xsl:value-of select="$na-answer"/>
						</div>
					</xsl:when>
					<xsl:when test="number($total) = 0">
						<div>
							<xsl:value-of select="$na-answer"/>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:value-of select="format-number(number($total),'#,###')"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>

			</td>
		</tr>

	</xsl:template>

	<!--Outstanding-->
	<xsl:template name="Outstanding">
		<xsl:variable name="total">
			<xsl:choose>
				<xsl:when test="string-length(./md.outstandingsecurities) = 0">
					<xsl:value-of select="0"/>
				</xsl:when>
				<xsl:when test="./md.outstandingsecurities=0">
					<xsl:value-of select="0"/>
				</xsl:when>
				<xsl:when test="string(number(./md.outstandingsecurities)) != 'NaN'">
					<xsl:value-of select="number(./md.outstandingsecurities)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'NaN'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<tr>
			<td class="&layout_TopAlignment;">
				<div>
					<h4>
						<xsl:text>Outstanding (USD):</xsl:text>
					</h4>
				</div>
			</td>

			<td class="&layout_TopAlignment;">

				<xsl:choose>
					<xsl:when test="string(number($total)) = 'NaN'">
						<div>
							<xsl:value-of select="$na-answer"/>
						</div>
					</xsl:when>
					<xsl:when test="number($total) = 0">
						<div>
							<xsl:value-of select="$na-answer"/>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:value-of select="format-number(number($total),'#,###')"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>

			</td>
		</tr>

	</xsl:template>

	<!--
	**************************************************************************************
	*		Related Parties & Fees Section                                                   *
	**************************************************************************************
	-->
	<xsl:template name="RelatedPartiesSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h3>
					<xsl:text>Related Parties &amp; Fees (USD)</xsl:text>
				</h3>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table class="&layout_table; &layout_3Columns;">

					<!--Target Law Firm-->
					<xsl:call-template name="VerifyMultipleSingleElements">
						<xsl:with-param name="param1" select="'Target Law Firm(s):'"/>
						<xsl:with-param name="param2" select="string-length($t-counsel/md.MA.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$t-counsel/md.MA.description"/>
					</xsl:call-template>

					<!--Acquirer Law Firm-->
					<xsl:call-template name="VerifyMultipleSingleElements">
						<xsl:with-param name="param1" select="'Acquirer Law Firm(s):'"/>
						<xsl:with-param name="param2" select="string-length($a-counsel/md.MA.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$a-counsel/md.MA.description"/>
					</xsl:call-template>

					<!--Target Accountant-->
					<xsl:call-template name="VerifyMultipleSingleElements">
						<xsl:with-param name="param1" select="'Target Accountant(s):'"/>
						<xsl:with-param name="param2" select="string-length($t-accountant/md.MA.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$t-accountant/md.MA.description"/>
					</xsl:call-template>

					<!--Acquirer Accountant-->
					<xsl:call-template name="VerifyMultipleSingleElements">
						<xsl:with-param name="param1" select="'Acquirer Accountant(s):'"/>
						<xsl:with-param name="param2" select="string-length($a-accountant/md.MA.description) &gt; 0"/>
						<xsl:with-param name="param3" select="$a-accountant/md.MA.description"/>
					</xsl:call-template>

					<!-- Target Advisor -->
					<xsl:call-template name="TargetAdvisor"/>

					<!-- Acquirer Advisor -->
					<xsl:call-template name="AcquirerAdvisor"/>

				</table>

			</div>
		</div>
	</xsl:template>

	<!-- TargetAdvisor template applicable only to this content type-->
	<xsl:template name="TargetAdvisor">
		<xsl:choose>
			<xsl:when test="$tfa">
				<xsl:for-each select="$tfa">
					<xsl:variable name="label">
						<xsl:choose>
							<xsl:when test="position() = 1">
								<xsl:value-of select="'Target Advisor(s) &amp; Fee(s):'" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="''" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="DisplayValuesIn3Cols">
						<xsl:with-param name="label" select="$label"/>
						<xsl:with-param name="value1" select="md.MA.description" />
						<xsl:with-param name="value2" select="format-number(md.advisorfee, '#,###')"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayValuesIn3Cols">
					<xsl:with-param name="label" select="'Target Advisor(s) &amp; Fee(s):'"/>
					<xsl:with-param name="value1" select="$na-answer" />
					<xsl:with-param name="value2" select="$na-answer"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- AcquirerAdvisor template applicable only to this content type-->
	<xsl:template name="AcquirerAdvisor">
		<xsl:choose>
			<xsl:when test="$afa">
				<xsl:for-each select="$afa">
					<xsl:variable name="label">
						<xsl:choose>
							<xsl:when test="position() = 1">
								<xsl:value-of select="'Acquirer Advisor(s) &amp; Fee(s):'" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="''" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="DisplayValuesIn3Cols">
						<xsl:with-param name="label" select="$label"/>
						<xsl:with-param name="value1" select="md.MA.description" />
						<xsl:with-param name="value2" select="format-number(md.advisorfee, '#,###')"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayValuesIn3Cols">
					<xsl:with-param name="label" select="'Acquirer Advisor(s) &amp; Fee(s):'"/>
					<xsl:with-param name="value1" select="$na-answer" />
					<xsl:with-param name="value2" select="$na-answer"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
