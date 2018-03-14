<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsRealProperty.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="/">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyLegalAndVestingClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!-- Renders the report/document header-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_legalAndVestingHeader;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- renders the main column using the Public Records styles-->
	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="//_PROPERTY_INFORMATION"/>
	</xsl:template>

	<xsl:template match="_PROPERTY_INFORMATION">
		<xsl:param name="reportType" select="normalize-space(@_ReportType)" />
		<xsl:if test="$reportType = 'LegalAndVestingReport'">
			<xsl:apply-templates select="PROPERTY" mode="LegalAndVesting"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="PROPERTY" mode="LegalAndVesting">
		<xsl:call-template name="Legal"/>
		<xsl:call-template name="Location"/>
		<xsl:apply-templates select="_PROPERTY_TAX" mode="LegalAndVesting"/>
		<xsl:apply-templates select="_PROPERTY_HISTORY" mode="LegalAndVesting"/>
	</xsl:template>

	<xsl:template name="Legal">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_legalDescriptionHeader;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_County" />
			<xsl:apply-templates select="@_State" mode="Descriptive" />
			<xsl:apply-templates select="PROPERTY_OWNER" mode="Legal" />
			<xsl:apply-templates select="_LEGAL_DESCRIPTION/@_LegalAndVestingTextDescription"/>
		</table>
	</xsl:template>

	<xsl:template name="Location">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_propertyInformation;'" />
		</xsl:call-template>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_locationInformation;'" />
		</xsl:call-template>
		
		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_SubdivisionIdentifier"/>
			<xsl:apply-templates select="@_BlockIdentifier"/>
			<xsl:apply-templates select="@_LegalBookAndPageIdentifier"/>
			<xsl:apply-templates select="@_LotIdentifier"/>
			<xsl:apply-templates select="@_AssessorsParcelIdentifier"/>
		</table>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_addressHeading;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<tr>
				<xsl:attribute name="class">&pr_item;</xsl:attribute>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="'&pr_propertyAddress;'"/>
				</xsl:call-template>
				
				<xsl:variable name="propertyAddress">
					<xsl:apply-templates select="@_StreetAddress" />
					<xsl:apply-templates select="@_City" />
					<xsl:apply-templates select="@_State" >
						<xsl:with-param name="cityNode" select="@_City" />
					</xsl:apply-templates>
					<xsl:apply-templates select="@_PostalCode" />
					<xsl:apply-templates select="@_PlusFourPostalCode" />
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$propertyAddress"/>
				</xsl:call-template>
			</tr>

			<tr>
				<xsl:attribute name="class">&pr_item;</xsl:attribute>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="'&pr_mailingAddress;'"/>
				</xsl:call-template>

				<xsl:variable name="mailingAddress">
					<xsl:apply-templates select="PROPERTY_OWNER/@_MailingAddress"/>
					<xsl:apply-templates select="PROPERTY_OWNER/@_MailingCityAndState"/>
					<xsl:apply-templates select="PROPERTY_OWNER/@_MailingPostalCode"/>
					<xsl:apply-templates select="PROPERTY_OWNER/@_MailingCarrierRouteIdentifier"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$mailingAddress"/>
				</xsl:call-template>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="_PROPERTY_TAX" mode="LegalAndVesting">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_taxInformation;'" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_TaxYear"/>
			<xsl:apply-templates select="@_RealEstateTotalTaxAmount"/>
			<xsl:apply-templates select="@_AssessmentYear"/>
			<xsl:apply-templates select="@_TotalAssessedValueAmount"/>
			<xsl:apply-templates select="@_RateAreaIdentifier"/>
			<xsl:apply-templates select="@_LandValueAmount"/>
			<xsl:apply-templates select="@_DelinquentYear"/>
			<xsl:apply-templates select="@_ImprovementValueAmount"/>
			<xsl:apply-templates select="@_ExemptionTypeDescription"/>
		</table>
	</xsl:template>

	<xsl:template match="_PROPERTY_HISTORY" mode="LegalAndVesting">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_transactionHistory;'" />
		</xsl:call-template>

		<xsl:apply-templates select="_TRANSACTION_HISTORY" mode="LegalAndVesting"	/>
	</xsl:template>

	<xsl:template match="_TRANSACTION_HISTORY" mode="LegalAndVesting">
		<xsl:variable name="historyHeader">
			&pr_historyRecordNumber; <xsl:value-of select="@_TransactionNumber"/>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="normalize-space($historyHeader)" />
		</xsl:call-template>

		<table>
			<xsl:attribute name="class">&pr_table;</xsl:attribute>
			<xsl:apply-templates select="@_SalesPriceAmount"/>			
			<xsl:apply-templates select="@_SaleRecordingDate"/>
			<xsl:apply-templates select="@_SaleDate"/>
			<xsl:apply-templates select="@_OneSaleTypeDescription"/>
			<xsl:apply-templates select="@_SaleDocumentNumberIdentifier"/>
			<xsl:apply-templates select="@_SaleDeedTypeDescription"/>
			<xsl:choose>
				<xsl:when test="@_FinanceTitleCompanyName != ''">
					<xsl:apply-templates select="@_FinanceTitleCompanyName" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="@_SaleTitleCompanyName" />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="@_SaleSellerName" />
			<xsl:apply-templates select="@_SaleBuyerName" />
			<xsl:apply-templates select="@_FinanceRecordingDate" />
			<xsl:apply-templates select="@_FinanceDocumentNumberIdentifier"/>
			<xsl:apply-templates select="@_FinanceLenderName" />
			<xsl:apply-templates select="@_FinanceLoanAmount" />
			<xsl:apply-templates select="@_FinanceCashDownAmount"/>
			<xsl:apply-templates select="@_FinanceMortgageTermTypeDescription"/>			
			<xsl:apply-templates select="@_FinanceInterestRateTypeDescription" />			
			<xsl:apply-templates select="@_FinanceMortgageTermNumber"/>			
			<xsl:apply-templates select="@_FinanceInterestRatePercent"/>
			
		</table>
	</xsl:template>

	<xsl:template match="@_FinanceInterestRatePercent">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mortgageRate;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_FinanceMortgageTermNumber">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mortgageTerm;'"/>
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="$nodevalue" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_FinanceInterestRateTypeDescription">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mortgageRateType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>
	
	<xsl:template match="@_FinanceMortgageTermTypeDescription">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mortgageLoanType;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>
	
	<xsl:template match="@_FinanceCashDownAmount">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_downPayment;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_FinanceLoanAmount">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_loanAmount;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_FinanceLenderName">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_lender;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_FinanceDocumentNumberIdentifier">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mortgageDocumentNumber;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>
	
	<xsl:template match="@_FinanceRecordingDate">
		<xsl:variable name="recordingDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_mortgageRecordingDate;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$recordingDate" />
			</xsl:call-template>
		</tr>
	</xsl:template>
	
	<xsl:template match="@_SaleBuyerName">
		<xsl:param name="nodevalue"	select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_grantee;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SaleSellerName">
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_grantor;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>
	
	<xsl:template match="@_FinanceTitleCompanyName|@_SaleTitleCompanyName" >
		<xsl:param name="nodevalue" select="normalize-space(.)" />

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_titleCompany;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SalesPriceAmount">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_salePrice;'" />
			</xsl:call-template>

			<xsl:if test="$nodevalue != '' and $nodevalue != '0.0'">
				<xsl:variable name="result">
					$<xsl:value-of select="$nodevalue"/>
				</xsl:variable>

				<xsl:call-template name="wrapWithTableValue">
					<xsl:with-param name="contents" select="normalize-space($result)" />
				</xsl:call-template>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="@_SaleDeedTypeDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_salePriceType;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SaleRecordingDate">
		<xsl:variable name="recordingDate">
			<xsl:call-template name="standard_date">
				<xsl:with-param name="date" select="."/>
			</xsl:call-template>
		</xsl:variable>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_saleRecordingDate;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$recordingDate" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SaleDate">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_saleDate;'" />
			</xsl:call-template>

			<xsl:variable name="dateValue">
				<xsl:call-template name="standard_date">
					<xsl:with-param name="date" select="$nodevalue" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$dateValue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SaleDocumentNumberIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_RecordDocNo;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_MultiOrSplitSaleIdentifier">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_multiSplitSale;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_SaleDeedTypeDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_documentType;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="_LEGAL_DESCRIPTION/@_LegalAndVestingTextDescription" >
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_legalDescription;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="PROPERTY_OWNER" mode="Legal">
		<xsl:apply-templates select="@_VestingName" />
		<xsl:apply-templates select="@_VestingDescription" />
	</xsl:template>

	<xsl:template match="@_VestingName">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_assessedOwner;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>
	
	<xsl:template match="@_VestingDescription">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_assessedOwnerDescription;'"/>
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue"/>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="@_State" mode="Descriptive">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>

		<tr>
			<xsl:attribute name="class">&pr_item;</xsl:attribute>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_state;'" />
			</xsl:call-template>

			<xsl:call-template name="wrapWithTableValue">
				<xsl:with-param name="contents" select="$nodevalue" />
			</xsl:call-template>
		</tr>
	</xsl:template>
</xsl:stylesheet>