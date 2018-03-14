<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<!--Political Contributions-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:preserve-space elements="*"/>

	<!--
		Desired output view:
			content    - renders document (default)
	-->

	<!-- Used to tell if we have donors plus data -->
	<xsl:variable name="isDonorsPlus">
		<xsl:choose>
			<xsl:when test ="/Document/n-docbody/r/p = 'DONORS PLUS'">
				<xsl:value-of select ="true"/>
			</xsl:when>
			<xsl:otherwise >
				<xsl:value-of select ="false"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- Do not render these nodes -->
	<xsl:template match="legacy.id|legacyId|col.key|p|pc|key|range"/>

	<!-- Render the CONTENT view. -->
	<xsl:template match="Document">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsPoliticalDonorsClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_politicalDonorRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Render (A) Coverage (Source) Information section -->
		<xsl:apply-templates select="$coverage-block"/>

		<!-- Render (B) Name/Persional Information section -->
		<xsl:apply-templates select="descendant::person.b"/>

		<!-- Render (C) Employment Information Section for Donors Plus data-->
		<xsl:apply-templates select="descendant::employ.b" />
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!-- Render (D) Donation Information section -->
		<xsl:apply-templates select="descendant::contrib.b"/>

		<!-- Render (E) Contribution Totals section -->
		<xsl:call-template name="partyContributions"/>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (A)"Coverage" section  *************************
	************************************************************************-->

	<xsl:template match="CoverageMeta">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_sourceInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:if test="$isDonorsPlus">
				<xsl:apply-templates select="/descendant::prelim" />
			</xsl:if>
			<!-- Displayed field: Database Updated -->
			<xsl:apply-templates select="UpdateFrequency">
				<xsl:with-param name="Label" select="'&pr_updateFrequency;'"/>
			</xsl:apply-templates>
			<!-- Displayed field: Current Date -->
			<xsl:apply-templates select="CurrentDate"/>
		</table>
	</xsl:template>

	<xsl:template match="prelim">
		<xsl:apply-templates select="acq.d" />
	</xsl:template>

	<xsl:template match="acq.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateAcquired;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  (B)Name/Personal Information  **********************
	************************************************************************-->

	<xsl:template match="person.b">
		<xsl:if test="name.b or addr.b or (mail.addr.b and not($isDonorsPlus))">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_nameAndPersonalInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates/>
			</table>
		</xsl:if>
	</xsl:template>

	<!--Name-->
	<xsl:template match="name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="title"/>
			<xsl:with-param name="firstName" select="fname"/>
			<xsl:with-param name="middleName" select="mname"/>
			<xsl:with-param name="lastName" select="lname"/>
			<xsl:with-param name="suffixName" select="suf"/>
			<xsl:with-param name="professionalSuffixName" select="prof.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address-->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--Mailing Address-->
	<xsl:template match="mail.addr.b">
		<!-- Only render mailing address if it is not donors plus information -->
		<xsl:if test="$isDonorsPlus">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
				<xsl:with-param name="street" select="str"/>
				<xsl:with-param name="city" select="cty"/>
				<xsl:with-param name="stateOrProvince" select="st"/>
				<xsl:with-param name="zip" select="zip.b/zip"/>
				<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- ********************************************************************** 
	***********************  (C)Employment Information    *********************
	************************************************************************-->

	<xsl:template match="employ.b">
		<xsl:if test="$isDonorsPlus and (occ or emp)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_employmentInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<!-- Information does not come in order desired-->
				<xsl:apply-templates select="occ" />
				<xsl:apply-templates select="emp"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="occ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_occupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="emp">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_employer;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	***********************  (D)Donation Information  *************************
	************************************************************************-->

	<xsl:template match="contrib.b[not(count(child::*) = 1 and child::prty.contrib.b)]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_donationInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:choose>
				<xsl:when test="$isDonorsPlus">
					<xsl:apply-templates select="first.contrib.d" />
					<xsl:apply-templates select="first.amt" />
					<xsl:apply-templates select="last.contrib.d" />
					<xsl:apply-templates select="last.amt" />
					<xsl:apply-templates select="last.prty"/>
					<xsl:apply-templates select="ccpac" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="tot.contrib"/>
					<xsl:apply-templates select="contrib.d"/>
					<xsl:apply-templates select="last.off"/>
					<xsl:apply-templates select="last.prty"/>
					<xsl:apply-templates select="last.amt.b"/>
					<xsl:apply-templates select="tot.amt.b"/>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<!-- - - - - - - - - - - - - - - - - - - - - - -
			 | Donors-Plus Contribution Templates       |
		 - - - - - - - - - - - - - - - - - - - - - - -->

	<!-- First Contribution Date (Donors-Plus) -->
	<xsl:template match="first.contrib.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_firstContributionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- First Contribution Amount (Donors-Plus) -->
	<xsl:template match="first.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Contribution Date (Donors-Plus) -->
	<xsl:template match="last.contrib.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastContributionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Contribution Amount-->
	<xsl:template match="last.amt">
		<xsl:choose>
			<xsl:when test="$isDonorsPlus">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_amount;'"/>
					<xsl:with-param name="nodeType" select="$CURRENCY"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!-- Political-Donors - don't need the label -->
				<xsl:call-template name="FormatCurrency"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Candidate/Committee/PAC (Donors-Plus) -->
	<xsl:template match ="last.prty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_party;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="ccpac">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_candidateOrCommitteeOrPac;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- - - - - - - - - - - - - - - - - - - - - - -
			 | Political-Donors Contribution Templates |
		 - - - - - - - - - - - - - - - - - - - - - - -->

	<!-- Total Contributions -->
	<xsl:template match="tot.contrib">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalNumberOfContributions;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Last Contribution (Political-Donors) -->
	<xsl:template match="contrib.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfLastContribution;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Last Office Contributed to-->
	<xsl:template match="last.off">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastOfficeContributedTo;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Last Party Contributed to-->
	<!--Last Amount Contributed-->
	<xsl:template match="last.amt.b">
		<tr>
			<th>
				<xsl:text>&pr_lastAmountContributed;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="last.amt">
						<xsl:apply-templates select="last.amt"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="range"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!--Total Amount Contributed-->
	<xsl:template match="tot.amt.b">
		<tr>
			<th>
				<xsl:text>&pr_totalAmountContributed;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="tot.amt">
						<xsl:apply-templates select="tot.amt"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="range"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="range">
		<xsl:choose>
			<xsl:when test="string() = 'VAL1'">
				<xsl:text>$1.00 - $49.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL2'">
				<xsl:text>$50.00 - $99.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL3'">
				<xsl:text>$100.00 - $149.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL4'">
				<xsl:text>$150.00 - $199.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL5'">
				<xsl:text>$200.00 - $249.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL6'">
				<xsl:text>$250.00 - $299.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL7'">
				<xsl:text>$300.00 - $349.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL8'">
				<xsl:text>$350.00 - $399.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL9'">
				<xsl:text>$400.00 - $449.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL10'">
				<xsl:text>$450.00 - $499.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL11'">
				<xsl:text>$500.00 - $549.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL12'">
				<xsl:text>$550.00 - $599.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL13'">
				<xsl:text>$600.00 - $699.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL14'">
				<xsl:text>$700.00 - $799.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL15'">
				<xsl:text>$800.00 - $999.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL16'">
				<xsl:text>$1,000.00 - $1,499.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL17'">
				<xsl:text>$1,500.00 - $1,999.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL18'">
				<xsl:text>$2,000.00 - $2,999.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL19'">
				<xsl:text>$3,000.00 - $4,999.00</xsl:text>
			</xsl:when>
			<xsl:when test="string() = 'VAL20'">
				<xsl:text>$5,000 &amp; up</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- ********************************************************************** 
	************************  (E)Contribution Totals  *************************
	************************************************************************-->

	<xsl:template name="partyContributions">
		<xsl:if test="descendant::prty.contrib.b or descendant::total.contrib.b">
			<xsl:variable name="label">
				<xsl:choose>
					<xsl:when test="descendant::total.contrib.b">
						<!-- Donors Plus Label -->
						<xsl:text>&pr_contributionTotals;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&pr_contributionTotalsByParty;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="$label"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="descendant::prty.contrib.b"/>
				<xsl:apply-templates select="descendant::total.contrib.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Democrat Contribution Amount -->
	<xsl:template match="tot.dem.amt">
		<xsl:choose>
			<xsl:when test="$isDonorsPlus">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_democrats;'"/>
					<xsl:with-param name="nodeType" select="$CURRENCY"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCurrency"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Republican Contribution Amount -->
	<xsl:template match="tot.rep.amt">
		<xsl:choose>
			<xsl:when test="$isDonorsPlus">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_republicans;'"/>
					<xsl:with-param name="nodeType" select="$CURRENCY"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCurrency"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Total Contribution Amount -->
	<xsl:template match="tot.amt">
		<xsl:choose>
			<xsl:when test="$isDonorsPlus">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_total;'"/>
					<xsl:with-param name="nodeType" select="$CURRENCY"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatCurrency"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Contributions by Party-->
	<xsl:template match="prty.contrib.b">
		<xsl:choose>
			<xsl:when test="child::contrib.amt">
				<tr>
					<th>
						<!--Convert to Mixed-Case-->
						<xsl:value-of select="substring(prty.t, 1,1)"/>
						<xsl:value-of select="substring(translate(prty.t, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),2,100)"/>
						<xsl:text>:</xsl:text>
					</th>
					<td>
						<xsl:choose>
							<xsl:when test="contrib.amt">
								<xsl:apply-templates select="contrib.amt"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates select="range"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_lastPartyContributedTo;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!--Contribution Amount-->
	<xsl:template match="contrib.amt">
		<xsl:call-template name="FormatCurrency"/>
	</xsl:template>

</xsl:stylesheet>