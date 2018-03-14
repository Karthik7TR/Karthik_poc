<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="corpDECompanyDetails">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsDelawareCorporateRecordsClass;'"/>
			<xsl:with-param name="dualColumn" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<!--************************************************
      This content is comprised of seven major sections:
      (A)CompanyInformationSection
      (B)AgentInformationSection
      (C)FilingInformationSection
      (D)StockInformationSection
      (E)TaxInformationSection
      (F)FileHistorySection
      (G)CurrencyInformationSection
      ************************************************* -->
		<xsl:call-template name="CurrencyInformationSection"/>
		<xsl:apply-templates select="name"/>
		<xsl:apply-templates select="agent"/>
		<xsl:apply-templates select="filingInformation"/>
		<xsl:apply-templates select="stockInformation"/>
		<xsl:apply-templates select="taxInformation"/>
		<xsl:apply-templates select="filingInformation/filingHistoryRecords"/>
		<xsl:call-template name="outputOrderDocumentsSection"/>
	</xsl:template>

	<!-- ********************************************************************* 
  *********************  (A)"COMPANY INFORMATION" section  ********************
  ************************************************************************-->
	<xsl:template match="agent">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_agentInformationCaps;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="name"/>
			<xsl:apply-templates select="number"/>
			<xsl:apply-templates select="Phone"/>
			<xsl:apply-templates select="Fax"/>
			<xsl:apply-templates select="address"/>
		</table>
	</xsl:template>

	<!-- Company Name -->
	<xsl:template match="name[not(name(..)='agent')]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_companyInformationCaps;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="displayItem">
				<xsl:with-param name="label" select="'&pr_name;'"/>
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="displayItem">
		<xsl:param name="label" select="/.."/>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="displayTextOrNotAvailable"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="displayTextOrNotAvailable">
		<xsl:choose>
			<xsl:when test="child::text()">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Not Available</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  <xsl:template name="displayValueWithLabel">
    <xsl:param name="label" select="/.."/>
    <xsl:param name="value" select="/.."/>
    <tr class="&pr_item;">
      <th>
        <xsl:value-of select="$label"/>
      </th>
      <td>
        <xsl:value-of select="$value"/>
      </td>
    </tr>
  </xsl:template>

  <!-- ********************************************************************* 
  *********************  (B)"AGENT INFORMATION" section  ********************
  ************************************************************************-->
	<!--Agent Name-->
	<xsl:template match="name[name(..)='agent']">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_agentName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Agent ID-->
	<xsl:template match="number">
    <xsl:variable name="agentName" select="../name"/>
    <xsl:choose>
      <xsl:when test="$agentName='Unassigned Agent'">
        <xsl:call-template name="displayValueWithLabel">
          <xsl:with-param name="label" select="'&pr_agentIdNumber;'"/>
          <xsl:with-param name="value" select="'0000000'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="displayItem">
          <xsl:with-param name="label" select="'&pr_agentIdNumber;'"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

	<!--Agent Phone-->
	<xsl:template match="Phone">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_agentPhone;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Agent Fax-->
	<xsl:template match="Fax">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_agentFax;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="address">
		<xsl:variable name="stateText" select="./state"/>
		<xsl:choose>
			<xsl:when test="$stateText!='NULL_VALUE'">
				<xsl:call-template name="wrapPublicRecordsAddress">
					<xsl:with-param name="fullStreet" select="mailingAddress1"/>
					<xsl:with-param name="streetLineTwo" select="mailingAddress2"/>
					<xsl:with-param name="city" select="city"/>
					<xsl:with-param name="stateOrProvince" select="state | province"/>
					<xsl:with-param name="zip" select="postalCode"/>
				</xsl:call-template>
			</xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="wrapPublicRecordsAddress" />
      </xsl:otherwise>
    </xsl:choose>
	</xsl:template>

	<!-- ********************************************************************* 
  *********************  (C)"FILING INFORMATION" section  ********************
  ************************************************************************-->
	<xsl:template match="filingInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingInformationCaps;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="residency"/>
			<xsl:apply-templates select="entityKind"/>
			<xsl:apply-templates select="entityType"/>
			<xsl:call-template name="StateOfIncorporation"/>
			<xsl:apply-templates select="incorporationDate"/>
			<xsl:apply-templates select="entityStatus"/>
			<xsl:apply-templates select="expirationDate"/>
			<xsl:apply-templates select="../fileNumber"/>
		</table>
	</xsl:template>

	<!--Residency-->
	<xsl:template match="residency">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_residency;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Entity Kind-->
	<xsl:template match="entityKind">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_entityKind;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-->Entity Type-->
	<xsl:template match="entityType">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_entityType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State of Incorporation-->
	<xsl:template name="StateOfIncorporation">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_stateOfIncorporation;</xsl:text>
			</th>
			<td>
				<xsl:text>Delaware Company</xsl:text>
			</td>
		</tr>
	</xsl:template>

	<!--Date of Incorporation-->
	<xsl:template match="incorporationDate">
		<xsl:call-template name="displayDate">
			<xsl:with-param name="label" select="'&pr_dateOfIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="displayDate">
		<xsl:param name="label" select="/.."/>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="formatDate"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="formatDate">
		<xsl:param name="node" select="."/>
		<xsl:variable name="year" select="substring-before($node, '-')"/>
		<xsl:variable name="month" select="substring-before(substring-after($node, '-'), '-')"/>
		<xsl:variable name="day" select="substring-before(substring-after(substring-after($node, '-'), '-'), 'T')"/>
		<xsl:value-of select="concat($month, '-', $day, '-', $year)"/>
	</xsl:template>

	<xsl:template name="formatTime">
		<xsl:param name="node" select="."/>
		<xsl:variable name="hour" select="substring-after(substring-before($node, ':'), 'T')"/>
		<xsl:variable name="minute" select="substring-before(substring-after(substring-after($node, 'T'), ':'), ':')"/>
		<xsl:value-of select="concat($hour, ':', $minute)"/>
	</xsl:template>

	<!--Status-->
	<xsl:template match="entityStatus">
		<tr class="&pr_item;">
			<th>&pr_status;</th>
			<td>
				<xsl:apply-templates/>
				<xsl:if test="following-sibling::statusDate">
					<xsl:text>, </xsl:text>
					<xsl:call-template name="formatDate">
						<xsl:with-param name="node" select="following-sibling::statusDate"/>
					</xsl:call-template>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!--Expiration Date-->
	<xsl:template match="expirationDate">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_expirationDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State ID-->
	<xsl:template match="fileNumber">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_stateIdNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************* 
  *********************  (D)"STOCK INFORMATION" section  ********************
  ************************************************************************-->
	<xsl:template match="stockInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_stockInformationCaps;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="displayItemOrNotAvailable">
				<xsl:with-param name="label" select="'&pr_totalAuthorizedShares;'"/>
				<xsl:with-param name="node" select="totalAuthorizedShares"/>
			</xsl:call-template>
			<xsl:call-template name="displayItemOrNotAvailable">
				<xsl:with-param name="label" select="'&pr_totalValue;'"/>
				<xsl:with-param name="node" select="totalValue"/>
			</xsl:call-template>
			<xsl:call-template name="displayItemOrNotAvailable">
				<xsl:with-param name="label" select="'&pr_stockEffectiveDate;'"/>
				<xsl:with-param name="node" select="stockBeginDate"/>
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="displayItemOrNotAvailable">
		<xsl:param name="label" select="/.."/>
		<xsl:param name="node" select="/.."/>

		<xsl:choose>
			<xsl:when test="$node">
				<xsl:apply-templates select="$node"/>
			</xsl:when>
			<xsl:otherwise>
				<tr class="&pr_item;">
					<th>
						<xsl:value-of select="$label"/>
					</th>
					<td>
						<xsl:text>Not Available</xsl:text>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="displayNumber">
		<xsl:param name="label" select="/.."/>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="FormatNumber"/>
			</td>
		</tr>
	</xsl:template>

	<!--Total Authorized Shares-->
	<xsl:template match="totalAuthorizedShares">
		<xsl:call-template name="displayNumber">
			<xsl:with-param name="label" select="'&pr_totalAuthorizedShares;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="displayCurrency">
		<xsl:param name="label" select="/.."/>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="FormatCurrency"/>
			</td>
		</tr>
	</xsl:template>

	<!--Total Value-->
	<xsl:template match="totalValue">
		<xsl:call-template name="displayCurrency">
			<xsl:with-param name="label" select="'&pr_totalValue;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Stock Effective Date-->
	<xsl:template match="stockBeginDate">
		<xsl:call-template name="displayDate">
			<xsl:with-param name="label" select="'&pr_stockEffectiveDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************* 
  *********************  (E)"TAX INFORMATION" section  ********************
  ************************************************************************-->
	<xsl:template match="taxInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_taxInformationCaps;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="taxRecords/taxRecord[1]"/>
			<xsl:apply-templates select="taxType"/>
		</table>
	</xsl:template>

	<!--Tax Information block-->
	<xsl:template match="taxRecord">
		<xsl:apply-templates select="taxYear"/>
		<xsl:apply-templates select="totalTaxes"/>
		<xsl:apply-templates select="balance"/>
	</xsl:template>

	<!--Tax Year-->
	<xsl:template match="taxYear">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_lastAnnualReportFiled;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Total Taxes-->
	<xsl:template match="totalTaxes">
		<xsl:call-template name="displayCurrency">
			<xsl:with-param name="label" select="'&pr_annualTaxAssessment;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="displayCurrencyOrNotAvailable">
		<xsl:choose>
			<xsl:when test="child::text()">
				<xsl:call-template name="FormatCurrency"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Not Available</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Total Balance-->
	<xsl:template match="balance">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_currentTaxEstimateAsOf;</xsl:text>
				<div>
					<xsl:value-of select="$currentDate"/>
				</div>
			</th>
			<td>
				<xsl:call-template name="displayCurrencyOrNotAvailable"/>
			</td>
		</tr>
	</xsl:template>

	<!--Tax Type-->
	<xsl:template match="taxType">
		<xsl:call-template name="displayItem">
			<xsl:with-param name="label" select="'&pr_taxAreaAndCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************* 
  *********************  (F)"FILE HISTORY INFORMATION" section  ********************
  ************************************************************************-->
	<xsl:template match="filingHistoryRecords">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingHistoryLastFiveFilings;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<tr>
				<th>&pr_descriptionLabel;</th>
				<th>&pr_pages;</th>
				<th>&pr_filingDateLabel;</th>
				<th>&pr_filingTimeLabel;</th>
				<th>&pr_effectiveDate;</th>
			</tr>
			<xsl:apply-templates select="filingHistoryRecord"/>
		</table>
	</xsl:template>

	<!--Filing History-->
	<xsl:template match="filingHistoryRecord">
		<tr class="&pr_item;">
			<td>
				<xsl:apply-templates select="documentCode"/>
			</td>
			<td>
				<xsl:apply-templates select="docPages"/>
			</td>
			<td>
				<xsl:call-template name="formatDate">
					<xsl:with-param name="node" select="filingDateTime"/>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="formatTime">
					<xsl:with-param name="node" select="filingDateTime"/>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="formatDate">
					<xsl:with-param name="node" select="effectiveDate"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<!-- ********************************************************************* 
  *********************  (A)"CURRENCY INFORMATION" section  ********************
  ************************************************************************-->
	<xsl:template name="CurrencyInformationSection">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_currencyCaps;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="CurrentDate"/>
			<xsl:call-template name="UpdateFrequency"/>
			<xsl:call-template name="Source"/>
		</table>
	</xsl:template>

	<xsl:template name="CurrentDate">
		<tr class="&pr_item;">
			<th>&pr_informationCurrentThrough;</th>
			<td>
				<xsl:value-of select="$currentDate" />
			</td>
		</tr>
	</xsl:template>

	<!--State of UpdateFrequency-->
	<xsl:template name="UpdateFrequency">
		<tr class="&pr_item;">
			<th>&pr_updateFrequency;</th>
			<td>&pr_asCurrentAsSecretaryOfState;</td>
		</tr>
	</xsl:template>

	<!--Source-->
	<xsl:template name="Source">
		<tr class="&pr_item;">
			<th>&pr_source;</th>
			<td>&pr_delawareSecretaryOfState;</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>