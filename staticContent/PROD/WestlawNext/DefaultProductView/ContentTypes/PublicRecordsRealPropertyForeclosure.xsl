<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:settings="urn:XslSettings"
	extension-element-prefixes="settings">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>

	<!--Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->

	<!--Real Property Records - property foreclosure-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:preserve-space elements="*"/>

	<!--Desired output view: content - renders document (default)-->

	<!--Variables-->
	<!-- Fullpath to the value of md.document.guid -->
	<!--<xsl:variable name="md-document-guid-value" select="/Document/map/entry[key='md.document.guid']/value" />-->

	<!-- Do not render these nodes -->
	<!-- Tracker #134351: don't render doc.yr.b -->
	<xsl:template match="legacy.id|legacyId|col.key|p|pc|c|pre|key|fip.state.cd|r/fip.cnty.cd|pubdate|etal.ind|map|trust.sl.num.b/l|legl.disc.b/l|lis.pend.pltf.nm.b/l|doc.yr.b"/>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="Document" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyForeclosureClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_realPropertyForeclosureRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="currentThroughDateLabel" select="'&pr_filingsCollectedThrough;'"/>
			<xsl:with-param name="databaseLastUpdatedLabel" select="'&pr_countyLastUpdated;'"/>
			<xsl:with-param name="updateFrequencyLabel" select="'&pr_frequencyOfUpdate;'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="descendant::doc.info.b" />
		<xsl:apply-templates select="descendant::own.b" />
		<xsl:apply-templates select="descendant::dft.info.b" />
		<xsl:apply-templates select="descendant::lis.pend.info.b" />
		<xsl:apply-templates select="descendant::judg.b" />
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="descendant::prop.b" />
		<xsl:apply-templates select="descendant::lst.sl.info.b" />
		<xsl:apply-templates select="descendant::orig.ln.info.b" />
	</xsl:template>

	<!-- **********************************************************************
	**************************  Document Information  *************************
	************************************************************************-->
	<xsl:template match="doc.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_documentInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="doc.t.b">
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="l" />
			</th>
			<td>
				<!-- Tracker 138794 - Hard code values in when the data is HR or MR -->
				<xsl:choose>
					<xsl:when test="doc.t='HR'">
						<xsl:text>&pr_homeOwnsersAssociationRelease;</xsl:text>
					</xsl:when>
					<xsl:when test="doc.t='MR'">
						<xsl:text>&pr_releaseOfMechanicsLien;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="doc.t"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Tracker 133206: added template match for doc.t to have hard-coded values for HR and MR -->
	<xsl:template match="doc.t">
		<xsl:choose>
			<xsl:when test="(node() ='HR')">
				<xsl:text>&pr_homeOwnsersAssociationRelease;</xsl:text>
			</xsl:when>
			<xsl:when test="(node() ='MR')">
				<xsl:text>&pr_releaseOfMechanicsLien;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="r.dt.b">
		<tr class="&pr_item;">
			<th>
				<xsl:choose>
					<!-- Tracker #136642: show publication date label when is should show -->
					<xsl:when test='(preceding-sibling::doc.t.b/doc.t="PUBLIC NOTICE OF FORECLOSURE SALE") or (preceding-sibling::doc.t.b/doc.t="PUBLIC NOTICE OF SHERIFF&apos;S SALE") or (preceding-sibling::doc.t.b/doc.t="NS") or (preceding-sibling::doc.t.b/doc.t="NF")'>
						<xsl:text>&pr_publicationDate;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&pr_recordingDate;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<xsl:apply-templates select="r.dt"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="r.dt">
		<xsl:call-template name="FormatNonSensitiveDate"/>
	</xsl:template>

	<xsl:template match="doc.num.b | bkpg.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- **********************************************************************
	****************  Defendant/Borrower/Owner Information  *******************
	************************************************************************-->
	<xsl:template match="own.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_defendantOrBorrowerOrOwnerInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="own.nm.b[normalize-space(.)]">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="normalize-space(l)">
					<xsl:value-of select="l"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_names;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:apply-templates select="own.nm1.b"/>
				<xsl:apply-templates select="own.oth.nm.b"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="own.nm1.b | own.oth.nm.b">
		<div>
			<xsl:choose>
				<xsl:when test="normalize-space(lst.nm) or normalize-space(fst.nm)">
					<xsl:call-template name="FormatName">
						<xsl:with-param name="firstName" select="fst.nm"/>
						<xsl:with-param name="lastName" select="lst.nm"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="co.nm"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template  match="prp.add.b[normalize-space(.)] | mail.add.b[normalize-space(.)]">
		<tr>
			<th>
				<xsl:choose>
					<xsl:when test="l">
						<xsl:value-of select="l"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="name()=prp.add.b">
								<xsl:value-of select="'&pr_propertyAddress;'"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'&pr_mailingAddress;'"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="fullStreet" select="full.str.add"/>
					<xsl:with-param name="streetNum" select="hs.num"/>
					<xsl:with-param name="street" select="str.nm"/>
					<xsl:with-param name="city" select="own.cty"/>
					<xsl:with-param name="stateOrProvince" select="own.st"/>
					<xsl:with-param name="zip" select="own.zip"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<!-- **********************************************************************
	**************************  Notice Information  ***************************
	************************************************************************-->
	<xsl:template match="dft.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_noticeInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="dft.amt.b | dft.dt.b | dft.auction.dt.b | auction.tm.b | auction.loc.b | tot.amt.due.b | lend.phn.b | trust.phn.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="lend.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="selectNodes" select="*[not(self::l or self::lend.phn.b)]"/>
		</xsl:call-template>
		<xsl:apply-templates select="lend.phn.b" />
	</xsl:template>

	<xsl:template match="lend.nm.b">
		<xsl:apply-templates select="*"/>
		<xsl:if test="normalize-space(following-sibling::lend.add.b)">
			<!-- Add a comma and space before the address. -->
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="trust.b[normalize-space(.)]">
		<xsl:if test="normalize-space(trust.nm.b) or normalize-space(trust.add.b)">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="selectNodes" select="trust.nm.b | trust.add.b"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="trust.phn.b" />
		<xsl:apply-templates select="trust.sl.num.b" />
	</xsl:template>

	<xsl:template match="trust.sl.num.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_trusteeSaleNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	*********************  Lis Pendens Information  ***************************
	************************************************************************-->
	<xsl:template match="lis.pend.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_lisPendensInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="lis.pend.filg.dt.b | lis.pend.t.b | lis.pend.filg.num.b | lis.pend.atty.nm.b | lis.pend.atty.phn.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="lis.pend.pltf.nm.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_plaintiffs;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	****************** Foreclosure Judgment Information   *********************
	************************************************************************-->
	<xsl:template match="judg.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_foreclosureJudgmentInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="filg.dt.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="filg.num.b | atty.nm.b | atty.phn.b | judg.amt.b | auction.dt.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="pltf.nm.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_plaintiffs;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	*****************  Property & Improvement Information  ********************
	************************************************************************-->
	<xsl:template match="prop.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_propertyAndImprovementInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="cnty.b | prp.ind.b | land.use.b | zon.b | lvng.sq.ft.b | bed.num.b | tot.bath.b | grge.spc.b | lot.ar.b | map.bkpg.b | yr.blt.b
												| land.val.cal.b | imp.val.cal.b | sect.b | twnshp.b | rng.b | sdiv.nm.b | blk.num.b | lot.num.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="apn.b[normalize-space(.)]">
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="l"/>
			</th>
			<td>
				<xsl:apply-templates select="fmt.apn"/>
				<xsl:apply-templates select="unft.apn[not(following-sibling::fmt.apn or preceding-sibling::fmt.apn)]"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="legl.disc.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_abstractedLegalDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	************************  Last Full Sale Information  *********************
	************************************************************************-->
	<xsl:template match="lst.sl.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_lastFullSaleInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="lst.sl.dt.b | lst.sl.prc.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- **********************************************************************
	**************************  Original Information  *************************
	************************************************************************-->
	<xsl:template match="orig.ln.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_originalLoanInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="orig.ln.dt.b | orig.r.dt.b | orig.ln.amt.b | orig.doc.num.b | orig.bkpg.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="auction.loc.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="l"/>
			<xsl:with-param name="fullStreet" select="auction.str"/>
			<xsl:with-param name="city" select="auction.cty"/>
			<xsl:with-param name="stateOrProvince" select="auction.st"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ***************************Common formatting templates****************************************-->

	<!--Format Dollar Amounts-->
	<xsl:template  match="land.val.cal|imp.val.cal|orig.ln.amt|dft.amt|judg.amt|sl.prc">
		<xsl:choose>
			<xsl:when test='string(number(.))="NaN"'>
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<!-- Tracker 132696: changed this to use standard currency formatting -->
				<xsl:call-template name="FormatCurrency" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  <xsl:template match ="co.nm">
    <xsl:call-template name="FormatCompany">
      <xsl:with-param name="companyName" select="."/>
    </xsl:call-template>
  </xsl:template>

	<!--Lender or Trustee  Address-->
	<xsl:template  match="lend.add.b|trust.add.b">
		<xsl:if test="descendant-or-self::*/text()">
			<div>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="fullStreet" select="str.add"/>
					<xsl:with-param name="city" select="cty"/>
					<xsl:with-param name="stateOrProvince" select="st"/>
					<xsl:with-param name="zip" select="zip"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<!--Format book-->
	<xsl:template match="bk" >
		<xsl:text>&pr_book;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Format Page-->
	<xsl:template match="pg" >
		<xsl:if test="name(preceding-sibling::*[1])= 'bk'">
			<xsl:text>/</xsl:text>
		</xsl:if>
		<xsl:text>&pr_page;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Format Original Recording Page-->
	<xsl:template match="orig.pg" >
		<xsl:if test="name(preceding-sibling::*[1])= 'orig.bk'">
			<xsl:text>/</xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="map.pg">
		<xsl:if test="name(preceding-sibling::*[1])= 'map.bk'">
			<xsl:text>/</xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>


</xsl:stylesheet>
