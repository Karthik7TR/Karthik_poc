<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
  <xsl:include href="BusinessInvestigatorName.xsl"/>

  <!-- Database Signon: STOCK -->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- don't render these elements-->
	<xsl:template match ="p|pc|legacy.id|role.class|trans.cd|seq.nbr|nbr.shares.adj|nbr.deriv.acq"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsStockClass;'"/>
			<xsl:with-param name="dualColumn" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_insiderStockTransactionRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<!-- Display coverage information -->
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<!-- (A)Insider Information and (B)Issuing Company Information-->
		<xsl:apply-templates select ="descendant::ins.info.b|descendant::iss.info.b"/>
		<!-- (C) Proposed Sale Information -->
		<xsl:apply-templates select="descendant::prop.sal.info.b"/>
		<!-- (D) Non-Derivative Securities Acquired, Disposed Of, Or Beneficially Owned-->
		<xsl:apply-templates select="descendant::nonderiv.info.b"/>
		<!-- (E) Derivative Or Non-Derivative Securities Acquired, Disposed Of, Or Beneficially Owned-->
		<xsl:apply-templates select="descendant::deriv.info.b"/>
	</xsl:template>

	<!-- ********************************************************************** 
	************************  (A) Insider Information  ************************
	************************************************************************-->
	<xsl:template match="ins.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_insiderInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Insider Name-->
	<xsl:template match="insider.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_insiderName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address-->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_addressReported;'"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="country" select="cntry"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="str">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<!-- Insider Phone-->
	<xsl:template match="phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_insiderPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Insider Role-->
	<xsl:template match="roles.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_insiderRole;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Role-->
	<xsl:template match="role">
		<xsl:if test="preceding-sibling::role">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Form Type-->
	<xsl:template match="form.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_formType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- SEC Receipt Date-->
	<xsl:template match="date">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secReceiptDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	********************  (B) Issuing Company Information  ********************
	************************************************************************-->
	<xsl:template match="iss.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_issuingCompanyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Company Name-->
	<xsl:template match ="iss.nm">
      <xsl:call-template name="wrapBusinessInvestigatorName">
        <xsl:with-param name="label" select="'&pr_companyName;'"/>
        <xsl:with-param name="companyName" select="."/>
      </xsl:call-template>
	</xsl:template>

	<!-- Ticker Symbol-->
	<xsl:template match ="tic.sym">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_tickerSymbol;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--CUSIP -->
	<xsl:template match ="cusip">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cusip;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  (C) Proposed Sale Information **********************
	************************************************************************-->
	<xsl:template match ="prop.sal.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_proposedSaleInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match ="prop.sal.b">
		<xsl:apply-templates select ="brok.nm" />
		<xsl:apply-templates select ="nbr.shares" />
		<xsl:apply-templates select ="mkt.val" />
		<xsl:apply-templates select ="prop.sal.d" />
		<xsl:apply-templates select ="natur.acq" />
		<xsl:apply-templates select ="nbr.shares.adj" />
	</xsl:template>

	<!--Broker Name -->
	<xsl:template match ="brok.nm">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_brokerName;'"/>
			<xsl:with-param name="indexNode" select="preceding-sibling::seq.nbr"/>
      <xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Proposed Number of Shares-->
	<xsl:template match ="nbr.shares">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_proposedNumberOfShares;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Transaction Market Value -->
	<xsl:template match ="mkt.val">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_transactionMarketValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Proposed Sale Date -->
	<xsl:template match ="prop.sal.d">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_proposedSaleDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Nature of Acquisition -->
	<xsl:template match ="natur.acq">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_natureOfAcquisition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Number of Shares Adjusted -->
	<xsl:template match ="nbr.shares.adj">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_proposedSharesAdjusted;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	(D) Non-Derivative Securities Acquired, Disposed Of, Or Beneficially Owned
	************************************************************************-->
	<xsl:template match ="nonderiv.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_nonDerivativeSecuritiesAcquiredDisposedOfOrBeneficiallyOwned;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Security Title -->
	<xsl:template match ="scy.typ">
		<xsl:variable name="countNonDerivs" select="count(preceding::nonderiv.b)+1"/>
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securityTitle;'"/>
			<xsl:with-param name="indexNumber" select="$countNonDerivs"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Transaction Date-->
	<xsl:template match ="trans.d">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_transactionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Number of Shares in Transaction -->
	<xsl:template match ="nbr.shares.trans">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfShares;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Transaction Type -->
	<xsl:template match ="trans.typ">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_transactionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Resulting Shares Held -->
	<xsl:template match ="shares.hld[parent::nonderiv.b]">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_resultingSharesHeld;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Shares Acquired -->
	<xsl:template match ="nbr.shares.acq">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sharesAcquired;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Shares Sold-->
	<xsl:template match ="nbr.shares.sold">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sharesSold;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Share Price -->
	<xsl:template match ="trans.pri">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sharePrice;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Ownership Form-->
	<xsl:template match ="own.typ">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownershipForm;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	(E) Derivative Or Non-Derivative Securities Acquired, Disposed Of, Or Beneficially Owned
	************************************************************************-->
	<xsl:template match="deriv.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_derivativeSecuritiesAcquiredDisposedOfOrBeneficiallyOwned;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Derivative Title -->
	<xsl:template match ="deriv.typ">
		<xsl:variable name="countDerivs" select="count(preceding::deriv.b)+1"/>
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_derivativeTitle;'"/>
			<xsl:with-param name="indexNumber" select="$countDerivs"/>
		</xsl:call-template>
	</xsl:template>

	<!--Number of Derivatives Acqd -->
	<xsl:template match ="nbr.deriv.trans">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_derivativesAcquired;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Exercise Price-->
	<xsl:template match ="cnvr.pri">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_exercisePrice;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Derivatives Sold-->
	<xsl:template match ="nbr.deriv.sold">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_derivativesSold;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Exercisable -->
	<xsl:template match ="exer.d">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateExercisable;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Expiration Date-->
	<xsl:template match ="exp.d">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_expirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Underlying Security-->
	<xsl:template match ="und.scy.typ">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_underlyingSecurity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Underlying Shares -->
	<xsl:template match ="nbr.shares.und">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_underlyingShares;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Underlying Market Price-->
	<xsl:template match ="mkt.pri">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_underlyingMarketPrice;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Resulting Derivatives Held-->
	<xsl:template match ="shares.hld[parent::deriv.b]">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_resultingDerivativesHeld;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Options Sold -->
	<xsl:template match ="sell.opt">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_optionsSold;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>