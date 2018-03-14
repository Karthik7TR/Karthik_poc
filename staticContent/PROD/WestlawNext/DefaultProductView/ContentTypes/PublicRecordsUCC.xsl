<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!-- Do not render these nodes -->
	<xsl:template match="map|legacy.id|col.key|p|pc|c|v|pre|prism-clipdate|restrict|upd.d|db.d.b|filg.pgs"/>

	<!--Elements with no match in legacy stylesheet -->
	<xsl:template match="filg.act|filg.mthd|orig.filg.nbr|orig.filg.nbr.full|filg.nbr.full|col.cnty|col.attch|dbtr.ctry"/>

	<xsl:variable name="fullpath-node-r" select="/Document/n-docbody/r" />

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsUccClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:variable name="uccHeaderText">
			<xsl:choose>
				<xsl:when test="col.key ='NYC'">
					<xsl:text>&pr_newYorkCityUniformCommercialCodeAndFederalLienRecords;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_uniformCommercialCodeReport;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="$uccHeaderText" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:call-template name="MiscInfo"/>

		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>

		<xsl:choose>
			<!-- UCC WA doc-->
			<xsl:when test="single.filg.b">
				<xsl:apply-templates select="single.filg.b[not(preceding-sibling::single.filg.b)]"/>
			</xsl:when>

			<!-- UCC NYC doc -->
			<xsl:when test="filg.lien.b">
				<xsl:apply-templates select="filg.lien.b"/>
				<xsl:apply-templates select="party.b/debt.info.b" />
				<xsl:apply-templates select="party.b/sec.info.b" />
			</xsl:when>

			<!-- UCC doc-->
			<xsl:otherwise>
				<xsl:apply-templates select="filg.stmt.b"/>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:if test="count(single.filg.b)=1">
			<xsl:call-template name="UCCDocFooter"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:choose>
			<!-- UCC WA doc-->
			<xsl:when test="single.filg.b">
				<xsl:apply-templates select="single.filg.b[preceding-sibling::single.filg.b]"/>
			</xsl:when>

			<!-- UCC NYC doc-->
			<xsl:when test="filg.lien.b">
				<xsl:apply-templates select="party.b/other.info.b" />
				<xsl:apply-templates select="parcel.info.b"/>
				<xsl:apply-templates select="col.info.b"/>
				<xsl:apply-templates select="cross.ref.b"/>
				<xsl:apply-templates select="remarks.b"/>
			</xsl:when>

			<!-- UCC doc-->
			<xsl:otherwise>
				<xsl:apply-templates select="dbtr.b"/>
				<xsl:apply-templates select="scrd.b"/>
				<xsl:apply-templates select="asgn.b"/>
				<xsl:apply-templates select="col.b"/>
				<xsl:apply-templates select="rltd.filg.b"/>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:if test="not(count(single.filg.b)=1)">
			<xsl:call-template name="UCCDocFooter"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="MiscInfo">
		<xsl:if test="col.key='PA' or col.key='NC' or col.key='CA'">
			<xsl:variable name="disclaimerText">
				<xsl:choose>
					<xsl:when test ="col.key='PA'">
						<xsl:text>The following data is for informational purposes only and is not an official record. Certified copies may be obtained from the Pennsylvania Department of State.</xsl:text>
					</xsl:when>
					<xsl:when test ="col.key='NC'">
						<xsl:text>This data is for information purposes only. Certification can only be obtained through the Department of the North Carolina Secretary of State.</xsl:text>
					</xsl:when>
					<xsl:when test ="col.key='CA'">
						<xsl:text>THIS DATA IS FOR INFORMATION PURPOSES ONLY.  CERTIFICATION CAN ONLY BE OBTAINED THROUGH THE SACRAMENTO OFFICE OF THE CALIFORNIA SECRETARY OF STATE.</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="wrapPublicRecordsDisclaimers">
				<xsl:with-param name="disclaimer1" select="$disclaimerText"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="UCCDocFooter">
		<xsl:choose>
			<xsl:when test="col.key='CA'">
				<xsl:call-template name="wrapPublicRecordsDisclaimers">
					<xsl:with-param name="disclaimer1">
						<xsl:text>The public record items reported above may have been paid, terminated, vacated or released prior to today's date.</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsDisclaimers">
					<xsl:with-param name="disclaimer1">
						<xsl:text>The preceding public record data is for information purposes only and is not the official record. Certified copies can only be obtained from the official source.</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="disclaimer2">
						<xsl:text>The public record items reported above may have been paid, terminated, vacated or released prior to today's date.</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_orderDocuments;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="FormatOrderDocs"/>
		</table>
	</xsl:template>


	<!-- **********************************************************************
	********************** Original Filing section ********************
	************************************************************************-->

	<!--renders WA docs having original filing-->
	<xsl:template match="single.filg.b[not(preceding-sibling::single.filg.b)]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'"/>
			<xsl:with-param name="contents" select="'&pr_originalFiling;'"/>
		</xsl:call-template>

		<xsl:apply-templates select="filg.stmt.b"/>
		<xsl:apply-templates select="dbtr.b"/>
		<xsl:apply-templates select="scrd.b"/>
		<xsl:apply-templates select="asgn.b"/>
		<xsl:apply-templates select="real.filg.b"/>
		<xsl:apply-templates select="col.b"/>
		<xsl:apply-templates select="rltd.filg.b | filg.off.stmt.b"/>
		<xsl:apply-templates select="asgnor.b"/>
	</xsl:template>

	<!--renders WA docs having original filing and subsequent filing-->
	<xsl:template  match="single.filg.b[preceding-sibling::single.filg.b]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'"/>
			<xsl:with-param name="contents" select="'&pr_subsequentFiling;'"/>
		</xsl:call-template>
		<xsl:apply-templates select="filg.stmt.b"/>
		<xsl:apply-templates select="rltd.filg.b | filg.off.stmt.b"/>
		<xsl:apply-templates select="dbtr.b"/>
		<xsl:apply-templates select="scrd.b"/>
		<xsl:apply-templates select="asgn.b"/>
		<xsl:apply-templates select="real.filg.b"/>
		<xsl:apply-templates select="col.b"/>
		<xsl:apply-templates select="asgnor.b"/>
	</xsl:template>

	<!--FILING INFORMATION-->
	<xsl:template match="filg.stmt.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'filg.stmt.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_filingInfo;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<xsl:apply-templates select="filg.nbr.b/filg.nbr"/>
			<xsl:apply-templates select="filg.d"/>
			<xsl:apply-templates select="filg.tme"/>
			<xsl:apply-templates select="exp.d"/>
			<xsl:apply-templates select="pg.cnt"/>
			<xsl:apply-templates select="film.nbr"/>
			<xsl:apply-templates select="vol.nbr"/>
			<xsl:apply-templates select="pg.nbr"/>
			<xsl:apply-templates select="xref.nbr"/>
			<xsl:apply-templates select="filg.typ"/>
			<xsl:apply-templates select="filg.act"/>
			<xsl:apply-templates select="stat"/>
			<xsl:apply-templates select="term.d"/>
			<xsl:apply-templates select="ref.cd"/>
			<xsl:apply-templates select="cont.typ"/>
			<xsl:apply-templates select="src.name"/>
			<xsl:apply-templates select="cmnts.b/cmnt"/>
			<xsl:apply-templates select="cmnts.b/cmnt.txt"/>
			<xsl:apply-templates select="cmnts.b/cmnt.d"/>
			<xsl:apply-templates select="cmnts.b/cmnt.src.b"/>
			<xsl:apply-templates select="filg.off.b"/>
			<xsl:apply-templates select="filg.off.b/filg.off.cnty"/>
			<xsl:apply-templates select="orig.filg.nbr.b/orig.filg.nbr"/>
		</table>
	</xsl:template>

	<!-- Filing Number -->
	<xsl:template match="filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Time-->
	<xsl:template match="filg.tme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingTime;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Expiration Date-->
	<xsl:template match="exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_expirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Total Number of Pages	-->
	<xsl:template match="pg.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalNumberOfPages;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Film Number-->
	<xsl:template match="film.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filmNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Volume-->
	<xsl:template match="vol.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_volume;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Page Number -->
	<xsl:template match="pg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_pageNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Related Filing Number-->
	<xsl:template match="xref.nbr">
		<xsl:choose>
			<xsl:when test="ancestor::filg.off.stmt.b">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_relatedFilingNumber;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Filing Type -->
	<xsl:template match="filg.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Action -->
	<xsl:template match="filg.act">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingAction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Status-->
	<xsl:template match="stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Termination-->
	<xsl:template match="term.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingTermination;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Initial Filing-->
	<xsl:template match="ref.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfInitialFiling;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Contract Type-->
	<xsl:template match="cont.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_contractType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Date of Related Filing-->
	<xsl:template match="xref.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDateOfRelatedFiling;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Reported Comment-->
	<xsl:template match="cmnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedComment;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cmnt.txt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentText;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Comment-->
	<xsl:template match="cmnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfComment;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cmnt.src.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSource;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="src.ti">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="src.bus">
		<div>
			<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="."/>
				</xsl:call-template>
		</div>
	</xsl:template>

	<!--Source title 2 or 3-->
	<xsl:template match="src.ti2|src.ti3">
		<xsl:if test="preceding-sibling::*[name() = 'src.ti' or name()='src.ti2']">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Source title description-->
	<xsl:template match="src.ti.desc">
		<xsl:if test="not(preceding-sibling::*[name() = 'src.ti' or name()='src.ti2' or name()='src.ti3'])">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!--Filing Office-->
	<xsl:template match="filg.off.b">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_filingOffice;'"/>
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="filg.off.nme"/>
				<xsl:apply-templates select="filg.off.loc"/>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="street" select="filg.off.str"/>
					<xsl:with-param name="city" select="filg.off.cty"/>
					<xsl:with-param name="stateOrProvince" select="filg.off.st"/>
					<xsl:with-param name="zip" select="filg.off.zip"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="filg.off.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingOfficeCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- keep filing name, location, and office on seperate lines-->
	<xsl:template match="filg.off.nme | filg.off.loc">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Original Filing Number block-->
	<xsl:template match="orig.filg.nbr.b">
		<xsl:apply-templates select="orig.filg.nbr"/>
	</xsl:template>

	<!--Original Filing Number-->
	<xsl:template match="orig.filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalFilingNumber;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- **********************************************************************
	********************** Debtor Information section ********************
	************************************************************************-->

	<!--DEBTOR INFORMATION-->
	<xsl:template match="dbtr.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'dbtr.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_debtorInformationSubheader;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<xsl:call-template name="debtorInfo"/>
		</table>
	</xsl:template>

	<xsl:template name="debtorInfo">
		<xsl:variable name="trClass">
			<xsl:if test="preceding-sibling::dbtr.b">
			</xsl:if>
		</xsl:variable>
		<tr>
			<xsl:attribute name="class">
				<xsl:text>&pr_item;</xsl:text>
				<xsl:if test="preceding-sibling::dbtr.b">
					<xsl:text><![CDATA[ ]]>&pr_paddingTop;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<th>
				<xsl:choose>
					<xsl:when test="preceding-sibling::dbtr.b">
						<xsl:if test="preceding-sibling::dbtr.b/@authorized != @authorized">
							<xsl:if test="@authorized='Y'">
								<xsl:text>&pr_authorized;<![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:if>
						<xsl:text>&pr_debtors;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="@authorized='Y'">
							<xsl:text>&pr_authorized;<![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:text>&pr_debtors;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<xsl:apply-templates select="dbtr.nme.b"/>
				<xsl:apply-templates select="dbtr.addr.b"/>
			</td>
		</tr>
		<xsl:apply-templates select="ssn.fein"/>
		<xsl:apply-templates select="bus.duns.b"/>
		<xsl:apply-templates select="hq.duns.b"/>
		<xsl:apply-templates select="sgnr.b"/>
	</xsl:template>

	<xsl:template match="dbtr.nme.b">
		<xsl:choose>
			<xsl:when test="dbtr.nme | dbtr.bus.nme">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="dbtr.name.b/dbtr.fst.nme"/>
					<xsl:with-param name="middleName" select="dbtr.name.b/dbtr.mid"/>
					<xsl:with-param name="lastName" select="dbtr.name.b/dbtr.last.nme"/>
					<xsl:with-param name="suffixName" select="dbtr.name.b/dbtr.suf"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="dbtr.nme">
		<xsl:call-template name="FormatBusinessName">
			<xsl:with-param name="cite" select="cite.query"/>
			<xsl:with-param name="text" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dbtr.bus.nme">
		<xsl:call-template name="FormatBusinessName">
			<xsl:with-param name="cite" select="cite.query"/>
			<xsl:with-param name="text" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Put Debtor address on next line under name if there is a name-->
	<xsl:template match="dbtr.addr.b">
		<xsl:variable name="debtorZipcode" select ="dbtr.zip.b/dbtr.zip | dbtr.zip" />
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="dbtr.str"/>
				<xsl:with-param name="streetLineTwo" select="dbtr.str2"/>
				<xsl:with-param name="city" select="dbtr.cty | dbtr.frgn"/>
				<xsl:with-param name="stateOrProvince" select="dbtr.st"/>
				<xsl:with-param name="zip" select="$debtorZipcode"/>
				<xsl:with-param name="zipExt" select="dbtr.zip.b/dbtr.zip.ext"/>
				<xsl:with-param name="country" select="dbtr.ctry | dbtr.cntry"/>
			</xsl:call-template>
		</div>
	</xsl:template>


	<!--SSN/FEIN-->
	<xsl:template match="ssn.fein">
		<!-- Do not display SSN/FEIN (changes for no PRACCESS) -->
	</xsl:template>


	<!--D&B DUNS Number-->
	<xsl:template match="bus.duns.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dAndBDuns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!--HDQ D&B DUNS Number-->
	<xsl:template match="hq.duns.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hdqDAndBDuns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!--Signatory:-->
	<xsl:template match="sgnr.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_signatory;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Signatory title 2 & 3-->
	<xsl:template match="sgnr.ti2|sgnr.ti3">
		<xsl:if test="preceding-sibling::*[name() = 'sgnr.ti' or name()='sgnr.ti2']">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Signatory description-->
	<xsl:template match="sgnr.ti.desc">
		<xsl:if test="not(preceding-sibling::*[name() = 'sgnr.ti' or name()='sgnr.ti2' or name()='sgnr.ti3'])">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>


	<!-- **********************************************************************
	********************** Secured Party or Creditor section ********************
	************************************************************************-->

	<!--SECURED PARTY OR CREDITOR INFORMATION-->
	<xsl:template match="scrd.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'scrd.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_securedPartyOrCreditorInformation;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<xsl:call-template name="securedInfo"/>
		</table>
	 </xsl:template>

	 <xsl:template name="securedInfo">      
			<tr>
			<xsl:if test="preceding-sibling::scrd.b">
				<xsl:attribute name="class">
					<xsl:value-of select="'&pr_marginTop;'"/>
				</xsl:attribute>
			</xsl:if>
				<th>
					<xsl:choose>
						<xsl:when test="preceding-sibling::scrd.b">
							<xsl:if test="preceding-sibling::scrd.b/@authorized != @authorized">
								<xsl:if test="@authorized='Y'">
									<xsl:text>&pr_authorized;<![CDATA[ ]]></xsl:text>
								</xsl:if>
							</xsl:if>
				<xsl:text>&pr_securedParties;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="@authorized='Y'">
								<xsl:text>&pr_authorized;<![CDATA[ ]]></xsl:text>
							</xsl:if>
							<xsl:text>&pr_securedParties;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</th>
				<td>
					<xsl:apply-templates select="scrd.nme.b"/>
					<xsl:apply-templates select="scrd.nme"/>
					<xsl:apply-templates select="scrd.addr.b"/>
				</td>
			</tr>
			<xsl:apply-templates select="ssn.fein"/>
			<xsl:apply-templates select="bus.duns.b"/>
			<xsl:apply-templates select="hq.duns.b"/>
	</xsl:template>

	
	<xsl:template match="scrd.bus.nme">
				<xsl:variable name="DisplayBusinessName">
			<xsl:value-of select="cite.query"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$DisplayBusinessName!= ''">
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="$DisplayBusinessName"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="scrd.nme.b">
		<xsl:choose>
			<xsl:when test="scrd.nme | scrd.bus.nme">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="scrd.name.b/scrd.fst.nme"/>
					<xsl:with-param name="middleName" select="scrd.name.b/scrd.mid"/>
					<xsl:with-param name="lastName" select="scrd.name.b/scrd.last.nme"/>
					<xsl:with-param name="suffixName" select="scrd.name.b/scrd.suf"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="scrd.nme">
				<xsl:variable name="DisplayBusinessName">
			<xsl:value-of select="cite.query"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$DisplayBusinessName!= ''">
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="$DisplayBusinessName"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Put secured party address on next line-->
	<xsl:template match="scrd.addr.b">
		<xsl:variable name="partyZipcode" select ="scrd.zip.b/scrd.zip | scrd.zip" />
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="scrd.str"/>
				<xsl:with-param name="streetLineTwo" select="scrd.str2"/>
				<xsl:with-param name="city" select="scrd.cty"/>
				<xsl:with-param name="stateOrProvince" select="scrd.st"/>
				<xsl:with-param name="zip" select="$partyZipcode"/>
				<xsl:with-param name="zipExt" select="scrd.zip.b/scrd.zip.ext"/>
				<xsl:with-param name="country" select="scrd.cntry"/>
			</xsl:call-template>
		</div>
	</xsl:template>



	<!-- **********************************************************************
	********************** Assignee section ********************
	************************************************************************-->

	<!--ASSIGNEE INFORMATION-->
	<xsl:template match="asgn.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'asgn.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_assigneeInformation;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<tr class="&pr_item;">
				<th>
					<xsl:if test="position()=1">
						<xsl:text>&pr_assignees;</xsl:text>
					</xsl:if>
				</th>
				<td>
					<xsl:apply-templates select="asgn.nme"/>
					<xsl:apply-templates select="asgn.addr.b"/>
				</td>
			</tr>
			<xsl:apply-templates select="ssn.fein"/>
			<xsl:apply-templates select="bus.duns.b"/>
			<xsl:apply-templates select="hq.duns.b"/>
		</table>
	</xsl:template>
	
	<xsl:template match="asgn.nme">
		<xsl:variable name="DisplayBusinessName">
			<xsl:value-of select="cite.query"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$DisplayBusinessName!= ''">
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="$DisplayBusinessName"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Put assignee address on next line under name if there is a name-->
	<xsl:template match="asgn.addr.b">
		<xsl:variable name="assigneeZipcode" select ="asgn.zip.b/asgn.zip | asgn.zip" />
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="asgn.str"/>
				<xsl:with-param name="streetLineTwo" select="asgn.str2"/>
				<xsl:with-param name="city" select="asgn.cty"/>
				<xsl:with-param name="stateOrProvince" select="asgn.st"/>
				<xsl:with-param name="zip" select="$assigneeZipcode"/>
				<xsl:with-param name="zipExt" select="asgn.zip.b/asgn.zip.ext"/>
				<xsl:with-param name="country" select="asgn.cntry"/>
			</xsl:call-template>
		</div>
	</xsl:template>


	<!-- **********************************************************************
	********************** Real Estate Filing Information section ********************
	************************************************************************-->

	<!--REAL ESTATE FILING INFORMATION FOR WA DOCUMENTS-->
	<xsl:template match="real.filg.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'real.filg.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_realEstateFilingInformation;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Real Estate Designation-->
	<xsl:template match="real.desig">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_realEstateDesignation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Real Estate Description-->
	<xsl:template match="real.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_realEstateDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Real Estate Owner(s)-->
	<xsl:template match="real.nme.b/real.name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_realEstateOwners;'"/>
			<xsl:with-param name="firstName" select="real.fst.nme"/>
			<xsl:with-param name="middleName" select="real.mid"/>
			<xsl:with-param name="lastName" select="real.last.nme"/>
			<xsl:with-param name="suffixName" select="real.suf"/>
			<xsl:with-param name="lastNameFirst" select="real.bus.nme"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="real.nme.b/real.bus.nme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_realEstateOwners;'"/>
			<xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Real estate address-->
	<xsl:template match="real.addr.b">
		<tr>
			<td> </td>
			<td>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="street" select="real.str"/>
					<xsl:with-param name="city" select="real.cty"/>
					<xsl:with-param name="stateOrProvince" select="real.st"/>
					<xsl:with-param name="zip" select="real.zip"/>
					<xsl:with-param name="country" select="real.cntry"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>
	
	<!-- **********************************************************************
	********************** Collateral Information section ********************
	************************************************************************-->

	<!--COLLATERAL INFORMATION-->
	<xsl:template match="col.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'col.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_collateralInformation;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<tr>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="class" select="'&pr_itemKey;'"/>
					<xsl:with-param name="contents" select="'&pr_collateralType;'"/>
				</xsl:call-template>
				<td>
					<xsl:apply-templates select="col.typ.b"/>
					<xsl:apply-templates select="col.desc"/>
				</td>
			</tr>
			<xsl:apply-templates select="col.item.b"/>
		</table>
	</xsl:template>

	<!--Space seperate colateral type, description-->
	<xsl:template match="col.typ.b/col.typ|col.typ.b/col.desc|col.typ.b/col.prqual|col.typ.b/col.poqual">
		<xsl:if test="preceding-sibling::*">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Machine Type-->
	<xsl:template match="mach.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_machineType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Secondary Machine Type-->
	<xsl:template match="mach2.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondaryMachineType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Manufacturer-->
	<xsl:template match="mfr.b">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_manufacturer;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
					<xsl:when test="mfr">
						<xsl:call-template name="FormatCompany">
							<xsl:with-param name="companyName" select="mfr"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatCompany">
							<xsl:with-param name="companyName" select="mfr.nme"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!--Year Manufactured-->
	<xsl:template match="mfd.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_yearManufactured;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Model-->
	<xsl:template match="mdl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_model;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Model Year-->
	<xsl:template match="mdl.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_modelYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Model Description-->
	<xsl:template match="mdl.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_modelDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Model Status-->
	<xsl:template match="new.used">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_modelStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Quantity-->
	<xsl:template match="qty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_quantity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Serial Number-->
	<xsl:template match="ser.nbr|ftl.ser.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_serialNumber;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- **********************************************************************
	********************** Related Filing Information section ********************
	************************************************************************-->
	<xsl:template match="rltd.filg.b | filg.off.stmt.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = 'rltd.filg.b']"/>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_relatedFilingInformation;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<table class="&pr_table;">
			<xsl:choose>
				<xsl:when test="$fullpath-node-r/col.key = 'CA'">
					<xsl:apply-templates select="ucc3.doc.nbr"/>
				</xsl:when>
				<xsl:when test="name()='filg.off.stmt.b'">
					<xsl:apply-templates select="xref.nbr.b/xref.nbr" />
					<xsl:apply-templates select="inacc.stmt" />
					<xsl:apply-templates select="act.stmt" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ucc3.filg.nbr"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="ucc3.filg.d"/>
			<xsl:apply-templates select="ucc3.filg.tme"/>
			<xsl:apply-templates select="ucc3.filg.typ"/>
		</table>
	</xsl:template>

	<!--Related Filing Number-->
	<xsl:template match="ucc3.filg.nbr|ucc3.doc.nbr">
		<xsl:choose>
			<xsl:when test="$fullpath-node-r/col.key = 'TX'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_relatedFilingNumber;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Related Filing Date-->
	<xsl:template match="ucc3.filg.d">
		<xsl:choose>
			<xsl:when test="$fullpath-node-r/col.key = 'TX'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
					<xsl:with-param name="nodeType" select="$DATE"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_relatedFilingDate;'"/>
					<xsl:with-param name="nodeType" select="$DATE"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Related Filing Time-->
	<xsl:template match="ucc3.filg.tme">
		<xsl:choose>
			<xsl:when test="$fullpath-node-r/col.key = 'TX'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_filingTime;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_relatedFilingTime;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Related Filing Type-->
	<xsl:template match="ucc3.filg.typ">
		<xsl:choose>
			<xsl:when test="$fullpath-node-r/col.key = 'TX'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_relatedFilingType;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Total Number of Pages-->
	<xsl:template match="ucc3.pg.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalNumberOfPages;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inacc.stmt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_inaccuracy;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="act.stmt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actions;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- **********************************************************************
	********************** Assignor Information section ********************
	************************************************************************-->

	<!--ASSIGNOR INFORMATION -->
	<xsl:template match="asgnor.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_assignorInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<tr>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="class" select="'&pr_itemKey;'"/>
					<xsl:with-param name="contents" select="'&pr_assignor;'"/>
				</xsl:call-template>
				<td>
					<xsl:apply-templates select="asgnor.nme.b/asgnor.name.b"/>
					<xsl:apply-templates select="asgnor.nme.b/asgnor.bus.nme"/>
					<xsl:apply-templates select="asgnor.addr.b"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<!--Assignor(s)-->
	<xsl:template match="asgnor.name.b">
		<xsl:call-template name="FormatName">
			<xsl:with-param name="firstName" select="asgnor.fst.nme"/>
			<xsl:with-param name="middleName" select="asgnor.mid"/>
			<xsl:with-param name="lastName" select="asgnor.last.nme"/>
			<xsl:with-param name="suffixName" select="asgnor.suf"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="asgnor.bus.nme">
		 <xsl:variable name="DisplayBusinessName">
			<xsl:value-of select="cite.query"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$DisplayBusinessName!= ''">
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="$DisplayBusinessName"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCompany">
					<xsl:with-param name="companyName" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Assignor address-->
	<xsl:template match="asgnor.addr.b">
		<div>
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="street" select="asgnor.str"/>
			<xsl:with-param name="streetLineTwo" select="asgnor.str2"/>
			<xsl:with-param name="city" select="asgnor.cty"/>
			<xsl:with-param name="stateOrProvince" select="asgnor.st"/>
			<xsl:with-param name="zip" select="asgnor.zip"/>
			<xsl:with-param name="country" select="asgnor.cntry"/>
		</xsl:call-template>
		</div>
	</xsl:template>


	<!-- **********************************************************************
	********************** UCC NYC section ********************
	************************************************************************-->

	<!--FILING/LIEN INFORMATION-->
	<xsl:template match='filg.lien.b'>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingLienInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Document Type -->
	<xsl:template match="filg.lien.b/filg.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Document ID -->
	<xsl:template match="filg.lien.b/doc.id">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentId;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--City Register File Number-->
	<xsl:template match="filg.lien.b/crfn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cityRegisterFileNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- File Number -->
	<xsl:template match="filg.lien.b/filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fileNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Reel Number/Page block-->
	<xsl:template match="filg.lien.b/reel.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reelNumberPage;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="filg.lien.b/reel.b/reel.pg">
		<xsl:if test="name(preceding-sibling::*[1]) = 'reel.nbr'">		
			<xsl:text>/</xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Reel Year-->
	<xsl:template match="filg.lien.b/reel.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reelYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.lien.b/filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Borough-->
	<xsl:template match="filg.lien.b/rec.borough">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingBorough;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Federal Tax Serial Number-->
	<xsl:template match="filg.lien.b/ftl.ser.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_federalTaxSerialNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Federal Tax Assessment Date-->
	<xsl:template match="filg.lien.b/ftl.ass.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_federalTaxAssessmentDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Real Property Transfer Tax #-->
	<xsl:template match="filg.lien.b/rpttl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_realPropertyTransferTaxNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Document Amount-->
	<xsl:template match="filg.lien.b/amt.due">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Time-->
	<xsl:template match="filg.lien.b/filg.tme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingTime;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--DEBTOR INFORMATION-->
	<xsl:template match="debt.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_debtorInformationSubheader;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--SECURED PARTY INFORMATION-->
	<xsl:template match="sec.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_securedPartyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--ASSIGNEE INFORMATION-->
	<xsl:template match="other.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_assigneeInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Debtor-->
	<xsl:template match="debt.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtors;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="debt.b[preceding-sibling::debt.b]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtors;'"/>
			<xsl:with-param name="divClass" select="'&pr_paddingTop;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Secured Party-->
	<xsl:template match="sec.party.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securedParties;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sec.party.b[preceding-sibling::sec.party.b]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securedParties;'"/>
			<xsl:with-param name="divClass" select="'&pr_paddingTop;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Assignee-->
	<xsl:template match="other.party.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assignee;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.party.b[preceding-sibling::other.party.b]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assignee;'"/>
			<xsl:with-param name="divClass" select="'&pr_paddingTop;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.nme">
		<xsl:call-template name="FormatBusinessName">
			<xsl:with-param name="cite" select="cite.query"/>
			<xsl:with-param name="text" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Don't render party type for debtors, secured parties, or assignees-->
	<xsl:template match="party.typ"/>

	<!-- Address -->
	<xsl:template match="party.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="party.str1"/>
				<xsl:with-param name="streetLineTwo" select="party.str2"/>
				<xsl:with-param name="city" select="party.cty"/>
				<xsl:with-param name="stateOrProvince" select="party.st"/>
				<xsl:with-param name="zip" select="party.zip.b/party.zip"/>
				<xsl:with-param name="zipExt" select="party.zip.b/party.zip.ext"/>
				<xsl:with-param name="country" select="party.cntry"/>
			</xsl:call-template>
		</div>
	</xsl:template>


	<!--PARCEL INFORMATION-->
	<xsl:template match="parcel.b">
		<xsl:variable name="parcelSubheader">
			<xsl:choose>
				<xsl:when test="parent::node()/@cnt='1'">
					<xsl:text>&pr_parcelInformation;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_parcel;<![CDATA[ ]]></xsl:text>
					<xsl:number format="1"/>
					<xsl:text><![CDATA[ ]]>&pr_information;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="$parcelSubheader"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Street Number & Name-->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_streetNumberAndName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Space separate street number and name-->
	<xsl:template match="str.nme">
		<xsl:if test="name(preceding-sibling::*[1]) = 'str.nbr'">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Unit Number-->
	<xsl:template match="addr.unit">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_unitNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Borough-->
	<xsl:template match="lot.borough">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_borough;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--BlockNumber-->
	<xsl:template match="block.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_blockNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Lot Number-->
	<xsl:template match="lot.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lotNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Easement-->
	<xsl:template match="easement">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_easement;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Partial/Entire-->
	<xsl:template match="partial.lot">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_partialEntire;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Air Rights-->
	<xsl:template match="air.rights">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_airRights;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Subterranean Rights-->
	<xsl:template match="sub.rights">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_subterraneanRights;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Property Type-->
	<xsl:template match="prop.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propertyType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--COLLATERAL INFORMATION-->
	<xsl:template match="col.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_collateralInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Collateral Type-->
	<xsl:template match="col.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_collateralType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--CROSS REFERENCES-->	
	<xsl:template match="cross.ref.b">
	<xsl:call-template name="wrapPublicRecordsSection">
		<xsl:with-param name="class" select="'&pr_subheader;'"/>
		<xsl:with-param name="contents" select="'&pr_crossReferences;'"/>
	</xsl:call-template>
	<table class="&pr_table;">
		<xsl:apply-templates/>
	</table>
	</xsl:template>

	<xsl:template match="cross.ref.b[preceding-sibling::cross.ref.b]">
	<table class="&pr_table;">
		<xsl:apply-templates/>
	</table>
	</xsl:template>

	<!--City Register File Number-->
	<xsl:template match="ref.crfn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cityRegisterFileNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Document ID-->
	<xsl:template match="ref.doc.id">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentId;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--File Number-->
	<xsl:template match="ref.filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fileNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--COMMENTS-->
	<xsl:template match="remarks.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_comments;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
		<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--remark-->
	<xsl:template match="remark">
		<tr>
			<td>
				 <xsl:apply-templates/>
				</td>
			</tr>
	</xsl:template>

</xsl:stylesheet>