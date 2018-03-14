<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<!--Real Property Records - property transaction-->
	
	<xsl:template match="legacy.id|legacyId|col.key|p|pc|c|pre|key|map"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyTransactionClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_realPropertyTransactionRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="currentThroughDateLabel" select="'&pr_filingsCurrentThrough;'"/>
			<xsl:with-param name="databaseLastUpdatedLabel" select="'&pr_countyLastUpdated;'"/>
			<xsl:with-param name="updateFrequencyLabel" select="'&pr_frequencyOfUpdate;'"/>
		</xsl:apply-templates>
		<!-- Render the "Owner Information" section -->
		<xsl:apply-templates select ="own.na1.b"/>
		<!-- Render the "Property Information" section -->
		<xsl:apply-templates select ="prp.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!-- Render the "Transaction Information" section -->
		<xsl:apply-templates select ="trn.info.b"/>
		<!-- Render the "Link" section -->
		<xsl:apply-templates select="rpts"/>
		<!-- Render the "Note" section -->
	</xsl:template>

	<xsl:template name="PublicRecordsEpilog"> 
		<xsl:call-template name="RPAssessorSearch">
			<xsl:with-param name="novusQuery" select="rpts/link[@key='RPTaxAssessor']/@retrv.qry"/>
			<xsl:with-param name="linkContents" select="'TAX ASSESSOR RECORD may be available for this property. The record contains information from the office of the local real property tax assessor office. In addition to identifying the current owner, the record may include tax assessment information, the legal description, and property characteristics. Additional charges may apply.'"/>
		</xsl:call-template>

		<xsl:call-template name="RPTransactionSearch">
			<xsl:with-param name="novusQuery" select="rpts/link[@key='RPHistory']/@retrv.qry"/>
			<xsl:with-param name="linkContents" select="'TRANSACTION HISTORY REPORT may be available for this property. The report contains details about all available transactions associated with this property. The report may include information about sales, ownership transfers, refinances, construction loans, 2nd mortgages, or equity loans based on recorded deeds. Additional charges may apply.'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--**********************************************************************
	*********************  (B)OWNER INFORMATION  *********************
	**********************************************************************-->
	<xsl:template match="own.na1.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_ownerInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select ="*[name()!= 'de.own.na2.b']"/>
			<xsl:if test="not(prp.add.b) and not(mail.add.b) and ../de.own.na2.b/co.na">
				<tr>
					<th>
					</th>
					<td>
						<xsl:text>&pr_careOf;<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="../de.own.na2.b/co.na" />
					</td>
				</tr>
			</xsl:if>
		</table>
	</xsl:template>

	<xsl:template match="de.own.na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_owners;'"/>
			<xsl:with-param name="firstName" select="buy.fst.na"/>
			<xsl:with-param name="middleName" select="buy.mid"/>
			<xsl:with-param name="lastName" select="buy.lst.na"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="de.own.rltn.b | own.rlt1.b | own.rlt2.b | own.rlt3.b | own.rlt4.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownerRelationship;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.rt.cd.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownershipRights;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.rt.cd1.b | own.rt.cd2.b | own.rt.cd3.b | own.rt.cd4.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownerRights;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.cd.ind.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_corporateOwner;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="abst.ind.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_absenteeOwner;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="part.int.ind.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_partialInterest;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prp.add.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_propertyAddress;'"/>
			<xsl:with-param name="streetNum" select="de.prp.hs.num"/>
			<xsl:with-param name="street" select="de.prp.str.na"/>
			<xsl:with-param name="streetUnitNumber" select="de.prp.apt.unt.num"/>
			<xsl:with-param name="streetSuffix" select="de.prp.md"/>
			<xsl:with-param name="city" select="de.prp.cty"/>
			<xsl:with-param name="stateOrProvince" select="de.prp.st"/>
			<xsl:with-param name="zip" select="de.prp.zip"/>
		</xsl:call-template>
		<xsl:if test="not(following-sibling::mail.add.b or preceding-sibling::mail.add.b) and preceding-sibling::de.own.na2.b/co.na" >
			<tr>
				<th>
				</th>
				<td>
					<xsl:text>&pr_careOf;<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="../de.own.na2.b/co.na" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--Mailing Address-->
	<xsl:template match="mail.add.b">
		<tr>
			<th>
				<xsl:choose>
					<xsl:when test="l">
						<xsl:value-of select="normalize-space(l)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&pr_mailingAddress;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<xsl:if test="preceding-sibling::de.own.na2.b/co.na">
					<xsl:text>&pr_careOf;<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="preceding-sibling::de.own.na2.b/co.na"/>
					<br/>
				</xsl:if>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="streetNum" select="de.mail.hs.num"/>
					<xsl:with-param name="street" select="de.mail.str.na"/>
					<xsl:with-param name="streetUnitNumber" select="de.mail.apt.unt.num"/>
					<xsl:with-param name="streetSuffix" select="de.mail.md"/>
					<xsl:with-param name="city" select="de.mail.cty"/>
					<xsl:with-param name="stateOrProvince" select="de.mail.st"/>
					<xsl:with-param name="zip" select="de.mail.zip"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<!--Give the appropriate label to additional owners-->
	<!-- TODO Can we consolidate the next four templates. -->
	<xsl:template match="buy.na1.b" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalOwner1;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="buy.na2.b" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalOwner2;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="buy.na3.b" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalOwner3;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="buy.na4.b" >
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalOwner4;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Only display the street direction once (ie N, S, NW...)
	TO DO: needs more testing to see how exactly to handle when the direction is given by
	de.prp.dir and de.prp.qud-->
	<xsl:template match="de.prp.dir">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:choose>
			<xsl:when test="following-sibling::*[name() = 'de.prp.qud']/descendant-or-self::*/text()"/>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--**********************************************************************
	*********************  (C)PROPERTY INFORMATION  *********************
	**********************************************************************-->
	<xsl:template match="prp.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_propertyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="fip.cd.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mun.na.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_municipality;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prp.ind.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propertyType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.use.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_landUse;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bldg.sq.ft.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_buildingSquareFeet;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fmt.apn.b" >
		<xsl:choose>
			<xsl:when test="normalize-space(de.acct.num)">
				<xsl:apply-templates select="de.acct.num"/>
			</xsl:when>
			<xsl:when test="normalize-space(de.fmt.apn)">
				<xsl:apply-templates select="de.fmt.apn"/>
			</xsl:when>
			<xsl:when test="normalize-space(de.orig.apn)">
				<xsl:apply-templates select="de.origin.apn"/>
			</xsl:when>
			<xsl:when test="normalize-space(de.unft.apn)">
				<xsl:apply-templates select="de.unft.apn"/>
			</xsl:when>
			<xsl:when test="normalize-space(iris.apn)">
				<xsl:apply-templates select="iris.apn"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="de.acct.num | de.fmt.apn | de.orig.apn | de.unft.apn | iris.apn">
		<xsl:variable name="key">
			<xsl:text>&pr_assessorsParcelNumber;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$key"/>
		</xsl:call-template>
	</xsl:template>

	<!--*******************************************************************************
	*********************  (D)TRANSACTION ASSESSMENT INFORMATION  *********************
	********************************************************************************-->
	<xsl:template match="trn.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_transactionInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="sl.d.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_transactionDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sell.na.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sellerName;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sl.prc.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salePrice;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cons.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_consideration;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.trn.per.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_percentTransferred;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sls.d.cat.t.b">
		<xsl:apply-templates select="doc.t | ddoc.t"/>
	</xsl:template>
	
	<xsl:template match="doc.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ddoc.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trn.t.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfTransaction;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.amt.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageAmount;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.ln.t.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.trm.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageTerm;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.de.t.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageDeedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="de.mort1.d.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="de.mort1.du.d.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageDueDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="de.mort1.assm.amt.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageAssumptionAmount;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort.int.rate.t.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_interestRate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lend1.na.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lenderName;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lend.add.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="'&pr_lenderAddress;'"/>
			<xsl:with-param name="streetNum" select="lend.num"/>
			<xsl:with-param name="street" select="lend.na"/>
			<xsl:with-param name="streetSuffix" select="lend.md"/>
			<xsl:with-param name="city" select="lend.cty"/>
			<xsl:with-param name="stateOrProvince" select="lend.st"/>
			<xsl:with-param name="zip" select="lend.zip"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="r.d.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordingDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="doc.num.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bk.pg.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordingBookOrPage;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ti.co.na.b">
    <xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

  <xsl:template match="de.ti.co.na">
    <xsl:call-template name="FormatCompany">
      <xsl:with-param name="companyName" select="."/>
    </xsl:call-template>
  </xsl:template>

	<xsl:template match="sel.cry.back.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sellerCarryback;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pri.pty.lend.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_privatePartyLender;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="const.ln.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_constructionLoan;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="resl.new.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_constructionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inter.fam.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_interFamilyTransaction;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cash.mort.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_purchasePayment;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="frcles.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_foreclosureSale;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="refi.fg.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_refinanceLoan;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="equity.fg.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_equityLoan;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="de.mlt.apn.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_multipleParcelSale;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mlt.apn.cnt.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfParcels;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort2.amt.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondMortgageAmount;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort2.ln.t.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondMortgageType;'"/>
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="mort2.de.t.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondMortgageDeedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ***************************Common formatting templates****************************************-->

	<!-- Since cities have a comma inserted after them, don't let the text node
				 get processed and simply take the text. -->
	<xsl:template match="de.prp.cty | de.mail.cty | lend.cty">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="textToTranslate" select="normalize-space(text())"/>
		</xsl:call-template>
	</xsl:template>

	<!-- For this content type, always append a space after a text node if it
			 has other siblings after it that contain text. -->
	<xsl:template match="text()[../following-sibling::*/text()]" priority="1">
		<xsl:if test="normalize-space(.)">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="normalize-space(.)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Format book-->
	<xsl:template match="de.bk" >
		<xsl:text>&pr_book;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Format Page-->
	<xsl:template match="de.pg" >
		<xsl:if test="name(preceding-sibling::*[1])= 'de.bk'">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:text>&pr_page;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Format Dollar Amounts-->
	<xsl:template match="assd.tot.val | tax.amt | imp.val.cal | de.mort1.amt | de.mort2.amt | de.sl.prc | de.mort1.assm.amt">
		<xsl:choose>
			<xsl:when test='string(number(.))="NaN"'>
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCurrency" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>