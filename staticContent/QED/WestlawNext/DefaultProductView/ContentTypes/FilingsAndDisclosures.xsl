<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!--
	This stylesheet transforms Filings & Disclosure documents from Novus XML
	into HTML for UI display.
-->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Filings.xsl"/>
	<xsl:include href="Transactions.xsl"/>
	<xsl:include href="QuickTable.xsl"/>

	<xsl:variable name="IsIP144ADoc" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.filing/md.filing.values/md.filing.value/md.formtype = '144A'" />
	<xsl:variable name="financial.info" select="/Document/n-metadata/md.financial.info.metadata.block" />
	<xsl:variable name="company.data" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.filing/md.filing.values/md.filing.value/md.company.data" />
	<xsl:variable name="metadata.144A" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.144A.metadata.block" />
	<xsl:variable name="metadata.filingmetadablock" select="/Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block" />
	<xsl:variable name="frenchToggleLink" select="/Document/n-metadata/metadata.block/md.references/md.toggle.links/md.toggle.link" />

	<!-- the main match -->
	<xsl:template match="Document">
		<!-- put doc content on display -->
		<div  id="&documentId;">
			<!-- Need document css classes added for correct alignment -->
			<xsl:choose>
				<xsl:when test="$isPreFormattedText=string(true())">
					<xsl:if test="$DeliveryMode=string(true())">
						<xsl:attribute name="style">
							<xsl:value-of select="$preformatDeliveryStyles"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeFilingsAndDisclosures; &preformattedDocument;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeFilingsAndDisclosures;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:call-template name="EndOfDocumentHeader" />
			<xsl:if test="$IsIP144ADoc">
				<xsl:call-template name="IP144AAbstract"/>
			</xsl:if>
			<xsl:call-template name="DisplayFrenchToggleLink" />
			<xsl:call-template name="Content">
				<xsl:with-param name="isScrollable" select="true()" />
			</xsl:call-template>
			<!--<xsl:call-template name="RenderFootnoteSection"/>-->
      <xsl:call-template name="EndOfDocument">
        <xsl:with-param name="endOfDocumentCopyrightText"></xsl:with-param>
        <xsl:with-param name="endOfDocumentCopyrightTextVerbatim">true()</xsl:with-param>
      </xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template name="DisplayFrenchToggleLink">
		<xsl:if test ="string-length($frenchToggleLink) > 0">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="concat('/Document/',$frenchToggleLink/@ID,'/View/FullText.html?transitionType=Default&amp;contextData=(sc.Default)')"/>
				</xsl:attribute>
				<xsl:value-of select="$frenchToggleLink" />
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/Document/n-metadata/metadata.block/md.references/md.toggle.links/md.toggle.link" />

	<!-- *************START: IP144A************* -->

	<!--DOCUMENT  ABSTRACT-->
	<xsl:template name="IP144AAbstract">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h4>
					<xsl:text>Document Abstract</xsl:text>
					<xsl:if test="not($DeliveryMode)">
						<a href="#co_documentLinksContainer">
							<xsl:text>Go to Full Text Document</xsl:text>
						</a>
					</xsl:if>
				</h4>
			</div>
		</div>
		<xsl:call-template name="IssuerInformationSection"/>
		<xsl:call-template name="DealInformationSection"/>
		<xsl:call-template name="RelatedPartiesSection"/>
		<xsl:call-template name="CopyrightSection"/>
		<xsl:call-template name="FullTextSeperator"/>
		<br/>
	</xsl:template>

	<!--ISSUER INFORMATION-->
	<xsl:template name="IssuerInformationSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h4>

					<xsl:text>Issuer Information</xsl:text>

				</h4>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table>

					<!--Issuer-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Issuer:'"/>
						<xsl:with-param name="param2" select="string-length($financial.info/md.filing.company.info/md.filing.company.name) &gt; 0"/>
						<xsl:with-param name="param3" select="$financial.info/md.filing.company.info/md.filing.company.name"/>
					</xsl:call-template>

					<!--Exchange-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Exchange:'"/>
						<xsl:with-param name="param2" select="string-length($company.data/md.stock.exchange) &gt; 0"/>
						<xsl:with-param name="param3" select="$company.data/md.stock.exchange"/>
					</xsl:call-template>

					<!--LOI-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Location of Incorporation:'"/>
						<xsl:with-param name="param2" select="string-length($company.data/md.state.of.incorporation/text()) &gt; 0"/>
						<xsl:with-param name="param3" select="$company.data/md.state.of.incorporation/text()"/>
					</xsl:call-template>

					<!--LOH-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Location of Headquarters:'"/>
						<xsl:with-param name="param2" select="string-length($company.data/md.state.of.headquarters/text()) &gt; 0"/>
						<xsl:with-param name="param3" select="$company.data/md.state.of.headquarters/text()"/>
					</xsl:call-template>

					<!--SIC Code-->
					<xsl:for-each select="$metadata.144A/md.144a.sic.code">
						<xsl:variable name="label">
							<xsl:choose>
								<xsl:when test="position() = 1">
									<xsl:value-of select="'SIC Code &amp; Description:'"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="''"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="$label"/>
							<xsl:with-param name="param2" select="string-length($metadata.144A/md.144a.sic.code) &gt; 0 or string-length($metadata.144A/md.144a.sic.desc) &gt; 0"/>
							<xsl:with-param name="param3" select="concat($metadata.144A/md.144a.sic.code, ' - ', $metadata.144A/md.144a.sic.desc)"/>
						</xsl:call-template>
					</xsl:for-each>

				</table>

			</div>
		</div>
	</xsl:template>

	<!--DEAL INFORMATION-->
	<xsl:template name="DealInformationSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h4>
					<xsl:text>Deal Information</xsl:text>

				</h4>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table>

					<!--Issued Date-->
					<xsl:call-template name="VerifyDate">
						<xsl:with-param name="param1" select="'Issued Date:'"/>
						<xsl:with-param name="param2" select="string-length($metadata.filingmetadablock/md.filing/md.filedatetime) &gt; 7	and number($metadata.filingmetadablock/md.filing/md.filedatetime) != 'NaN'"/>
						<xsl:with-param name="param3" select="$metadata.filingmetadablock/md.filing/md.filedatetime/text()"/>
						<xsl:with-param name="yearFirst" select="1"/>
					</xsl:call-template>

					<!--Nature of Offering-->
					<xsl:for-each select="$metadata.144A/md.144a.natures/md.144a.nature">
						<xsl:sort select="md.144a.nature.desc" data-type="text" order="ascending" />
						<xsl:variable name="label">
							<xsl:choose>
								<xsl:when test="position() = 1">
									<xsl:value-of select="'Nature of Offering:'"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="''"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="$label"/>
							<xsl:with-param name="param2" select="string-length(md.144a.nature.desc) &gt; 0"/>
							<xsl:with-param name="param3" select="md.144a.nature.desc"/>
						</xsl:call-template>
					</xsl:for-each>

					<!-- Keywords -->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Keywords:'"/>
						<xsl:with-param name="param2" select="string-length($metadata.144A/md.144a.keywords) &gt; 0"/>
						<xsl:with-param name="param3" select="$metadata.144A/md.144a.keywords" />
					</xsl:call-template>

					<!-- Language -->
					<xsl:for-each select="$metadata.144A/md.144a.languages/md.144a.language">
						<xsl:variable name="label">
							<xsl:choose>
								<xsl:when test="position() = 1">
									<xsl:value-of select="'Language:'"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="''"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:call-template name="VerifyString">
							<xsl:with-param name="param1" select="$label"/>
							<xsl:with-param name="param2" select="string-length(text()) &gt; 0"/>
							<xsl:with-param name="param3" select="text()"/>
						</xsl:call-template>
					</xsl:for-each>

				</table>
			</div>
		</div>

		<!--Loop all tranches in the prospectus-->
		<xsl:choose>
			<!--Test for tranche nodes-->
			<xsl:when test="count($metadata.144A/md.144a.tranches/md.144a.tranche) &gt; 0">
				<!--Loop all potential tranche nodes-->
				<xsl:for-each select="$metadata.144A/md.144a.tranches/md.144a.tranche">
					<xsl:call-template name="TrancheInformation">
						<xsl:with-param name="number" select="position()"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
		</xsl:choose>

	</xsl:template>

	<!-- TRANCHE INFORMATION -->
	<xsl:template name="TrancheInformation">
		<xsl:param name="number" />

		<div class="&layout_TranchesDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h4>
					<xsl:value-of select="concat('Tranche ', $number)"/>

				</h4>
			</div>

			<div class="&layout_Row_MarginBottom;">

				<table>

					<!--Security-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Security Type'"/>
						<xsl:with-param name="param2" select="string-length(./md.144a.security.type/md.144a.security.desc/text()) &gt; 0"/>
						<xsl:with-param name="param3" select="./md.144a.security.type/md.144a.security.desc/text()"/>
					</xsl:call-template>

					<!--Currency, Country of-->
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="'Currency, Country of:'"/>
						<xsl:with-param name="param2" select="string-length(./md.144a.currency/md.144a.currency.desc/text()) &gt; 0"/>
						<xsl:with-param name="param3" select="./md.144a.currency/md.144a.currency.desc/text()"/>
					</xsl:call-template>

					<!--Security Amount (USD)-->
					<xsl:call-template name="VerifyNumber">
						<xsl:with-param name="param1" select="'Security Amount:'"/>
						<xsl:with-param name="param2" select="string-length(./md.144a.security.amount) = 0"/>
						<xsl:with-param name="param3" select="./md.144a.security.amount=0"/>
						<xsl:with-param name="param4" select="string(number(./md.144a.security.amount)) != 'NaN'"/>
						<xsl:with-param name="param5" select="format-number(./md.144a.security.amount, '#,###')"/>
					</xsl:call-template>

					<!--Price-->
					<xsl:call-template name="VerifyNumber">
						<xsl:with-param name="param1" select="'Price:'"/>
						<xsl:with-param name="param2" select="string-length(./md.144a.security.price) = 0"/>
						<xsl:with-param name="param3" select="./md.144a.security.price=0"/>
						<xsl:with-param name="param4" select="string(number(./md.144a.security.price)) != 'NaN'"/>
						<xsl:with-param name="param5" select="format-number(./md.144a.security.price, '#,##0.0000')"/>
					</xsl:call-template>

					<!--Coupon Rate-->
					<xsl:call-template name="VerifyNumber">
						<xsl:with-param name="param1" select="'Coupon Rate (%):'"/>
						<xsl:with-param name="param2" select="string-length(./md.144a.coupon.pct) = 0"/>
						<xsl:with-param name="param3" select="./md.144a.coupon.pct=0"/>
						<xsl:with-param name="param4" select="string(number(./md.144a.coupon.pct)) != 'NaN'"/>
						<xsl:with-param name="param5" select="format-number(./md.144a.coupon.pct, '#,###0.0000')"/>
					</xsl:call-template>

					<!--Maturity Date-->
					<xsl:call-template name="FormatDateToYearFirst">
						<xsl:with-param name="param1" select="'Maturity Date:'"/>
						<xsl:with-param name="param2" select="string-length(./md.144a.maturity.date) &gt; 7	and number(./md.144a.maturity.date) != 'NaN'"/>
						<xsl:with-param name="param3" select="./md.144a.maturity.date/text()"/>
					</xsl:call-template>


				</table>

			</div>
		</div>
	</xsl:template>

	<!-- RELATED PARTIES -->
	<xsl:template name="RelatedPartiesSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<div class="&layoutHeaderRow;">
				<h4>
					<xsl:text>Related Parties Information</xsl:text>
				</h4>
			</div>

			<div class="&layout_Row_MarginBottom;">
				<table>

					<!--Issuer Law Firm-->
					<xsl:call-template name="RelatedParties">
						<xsl:with-param name="partySection" select="$metadata.144A/md.144a.related.parties/md.144a.ic.related.party" />
						<xsl:with-param name="sectionLabel" select="''" />
					</xsl:call-template>

					<!--Underwriter Law Firm-->
					<xsl:call-template name="RelatedParties">
						<xsl:with-param name="partySection" select="$metadata.144A/md.144a.related.parties/md.144a.uc.related.party" />
						<xsl:with-param name="sectionLabel" select="''" />
					</xsl:call-template>

					<!--Other Law Firm-->
					<xsl:call-template name="RelatedParties">
						<xsl:with-param name="partySection" select="$metadata.144A/md.144a.related.parties/md.144a.oc.related.party" />
						<xsl:with-param name="sectionLabel" select="''" />
					</xsl:call-template>

					<!--Underwriter-->
					<xsl:call-template name="RelatedParties">
						<xsl:with-param name="partySection" select="$metadata.144A/md.144a.related.parties/md.144a.br.related.party" />
						<xsl:with-param name="sectionLabel" select="''" />
					</xsl:call-template>

				</table>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="RelatedParties">
		<xsl:param name="partySection" />
		<xsl:param name="sectionLabel" />

		<xsl:choose>
			<xsl:when test="$partySection">
				<xsl:for-each select="$partySection">
					<xsl:variable name="label">
						<xsl:choose>
							<xsl:when test="position() = 1">
								<xsl:call-template name="VerifyString">
									<xsl:with-param name="param1" select="$sectionLabel"/>
									<xsl:with-param name="param2" select="string-length(md.144a.rp.role/text()) &gt; 0"/>
									<xsl:with-param name="param3" select="md.144a.rp.role/text()"/>
								</xsl:call-template>
								<xsl:text>:</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="''"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="VerifyString">
						<xsl:with-param name="param1" select="$label"/>
						<xsl:with-param name="param2" select="string-length(md.144a.rp.name/text()) &gt; 0"/>
						<xsl:with-param name="param3" select="md.144a.rp.name/text()"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="VerifyString">
					<xsl:with-param name="param1" select="$sectionLabel"/>
					<xsl:with-param name="param2" select="'true'" />
					<xsl:with-param name="param3" select="$na-answer"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- COPYRIGHT -->
	<xsl:template name="CopyrightSection">
		<div class="&layout_TransactionAbstractDocumentSection;">
			<xsl:if test="$DeliveryMode">
				<xsl:text>&nbsp;</xsl:text>
			</xsl:if>
			<br />
			<div class="&endOfDocumentCopyrightClass;">
				&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/>
			</div>
			<xsl:if test="$DeliveryMode">
				<xsl:text>&nbsp;</xsl:text>
			</xsl:if>
			<br/>
			<div class="&endOfDocumentCopyrightClass;">
				&fdIP144CopyrightText;
			</div>
			<xsl:if test="$DeliveryMode">
				<xsl:text>&nbsp;</xsl:text>
			</xsl:if>
			<br/>
		</div>
	</xsl:template>

	<!-- SEPERATOR -->
	<xsl:template name="FullTextSeperator">
		<div class="&layout_TransactionAbstractDocumentSection;"></div>
	</xsl:template>

	<!-- *************END: IP144A************* -->

	<!-- ************* START: Faster ************* -->
	<!-- Removes text formating -->
	<!-- WITH: 38seconds - 69Chunks -->
	<!-- WOUT: 49seconds - 199Chunks -->
	<!--<xsl:template match="para">
		<p>
			<xsl:call-template name="SpecialCharacterTranslator" />
		</p>
	</xsl:template>

	<xsl:template match="bold">
		<b>
			<xsl:call-template name="SpecialCharacterTranslator" />
		</b>
	</xsl:template>

	<xsl:template match="typo.format">
		<span>
			<xsl:call-template name="SpecialCharacterTranslator" />
		</span>
	</xsl:template>-->
	<!-- ************* END: Faster ************* -->

	<xsl:template match="*[starts-with(name(), 'section.')][@field.name]">
		<xsl:element name="span">
			<xsl:attribute name="id">co_internalToc_<xsl:value-of select="@field.name"/></xsl:attribute>
			<xsl:attribute name="class">co_internalTocMarker</xsl:attribute>
			<xsl:text>&nbsp;</xsl:text>
		</xsl:element>

		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="document[@ID][display.name]">
		<xsl:element name="span">
			<xsl:attribute name="id">co_internalToc_doc<xsl:value-of select="@ID"/></xsl:attribute>
			<xsl:attribute name="class">co_internalTocMarker</xsl:attribute>
			<xsl:text>&nbsp;</xsl:text>
		</xsl:element>

		<xsl:apply-templates/>
	</xsl:template>
	
</xsl:stylesheet>
