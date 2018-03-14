<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Fullpath to Coverage information -->
	<xsl:variable name="fullpath-node-r" select="/Document/n-docbody/r" />
	<xsl:variable name="SourceID" select="$fullpath-node-r/pre/s.b/s/@srce.id" />
	<xsl:variable name="SourceData" select="$fullpath-node-r/pre/s.b/s" />
	<xsl:variable name="colKey" select="$fullpath-node-r/col.key"/>

	<!-- Do not display. -->
	<xsl:template match="p | pre | bop | bos | legacy.id | col.key | darc.status | lic.t.msg
					| lic.nbr.msg" priority="1"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsProfessionalLicensesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:variable name="header">
			<xsl:choose>
				<xsl:when test="$SourceID='US_DEAREG1'">
					<xsl:text>&pr_drugEnforcementAdminRecord;</xsl:text>
				</xsl:when>
				<xsl:when test="$SourceID='US_FAAAIR1'">
					<xsl:text>&pr_federalAviationAdminRecord;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_professionalLicenseRecord;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="$header" />
		</xsl:call-template>
	</xsl:template>


	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block"/>
		<xsl:apply-templates select="na.prof.info.b"/>
		<xsl:apply-templates select="emp.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="historical.b"/>
		<xsl:apply-templates select="licensing.info.b"/>
		<xsl:apply-templates select="ed.info.b"/>
		<xsl:apply-templates select="med.prof.info.b"/>
		<xsl:apply-templates select="atty.prof.info.b"/>
		<xsl:apply-templates select="insurance.prof.info.b"/>
		<xsl:apply-templates select="real.estate.prof.info"/>
		<xsl:apply-templates select="further.info"/>
	</xsl:template>

	<xsl:template match="CoverageMeta" priority="1">
		<xsl:apply-templates select="CurrenthThroughDate">
			<xsl:with-param name="Label" select="'&pr_agencyInformationCurrentThrough;'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="DatabaseLastUpdated"/>
		<xsl:apply-templates select="UpdateFrequency"/>
		<xsl:apply-templates select="CurrentDate"/>
		<xsl:apply-templates select="$SourceData"/>
		<xsl:call-template name="CurrencyStatus"/>
	</xsl:template>

	<xsl:template match="s">
		<tr>
			<th>&pr_source;</th>
			<td>
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="CurrencyStatus">
		<xsl:if test="darc.status='R'">
			<tr class="&pr_item;">
				<th>&pr_currencyStatus;</th>
				<td>&pr_archiveRecord;</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
	<!--************************************************************************************-->
	<!--** This is the template that really drives this stylesheet. We look at each child **-->
	<!--** node, if that node has a child node of label we display the label and data. If **-->
	<!--** there is no label, we apply-templates to keep processing all the nodes.        **-->
	<!--************************************************************************************-->
	<xsl:template match="*[ancestor::na.prof.info.b] | *[ancestor::emp.info.b] | *[ancestor::historical.b]
								|	*[ancestor::licensing.info.b] | *[ancestor::ed.info.b] | *[ancestor::med.prof.info.b]
								| *[ancestor::atty.prof.info.b] | *[ancestor::insurance.prof.info.b] | *[ancestor::real.estate.prof.info]
								| *[ancestor::further.info]">
		<xsl:choose>
			<xsl:when test="label">
				<xsl:call-template name="wrapProfessionalLicenseItem"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="wrapProfessionalLicenseItem">
    <xsl:variable name="nodeName">
      <xsl:value-of select ="local-name()"/>
    </xsl:variable>

    <tr class="&pr_item;">
			<th>
				<xsl:apply-templates select="label"/>
			</th>
			<td>
       
        <xsl:choose>
          <xsl:when test="$nodeName ='trade.na.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="trade.na"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$nodeName ='emp.na.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="emp"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$nodeName ='emp.dba.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="emp.dba"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$nodeName ='prev.emp.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="prev.emp"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$nodeName ='sch.att.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="sch.att"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$nodeName ='hosp.priv.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="hosp.priv"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$nodeName ='outlet.na.b'">
            <xsl:call-template name="FormatCompany">
              <xsl:with-param name="companyName" select="outlet.na"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="*[not(self::label)]"/>
          </xsl:otherwise>
        </xsl:choose>
			
			</td>
		</tr>
	</xsl:template>

	<!-- *********************************************************************
	*******************  Name & Professional Information  ********************
	***********************************************************************-->
	<xsl:template match="na.prof.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_nameAndProfessionalInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="na.b" priority="1">
		<xsl:choose>
			<xsl:when test="full.na">
				<xsl:apply-templates select="full.na"/>
			</xsl:when>
			<xsl:when test="label">
				<xsl:call-template name="wrapPublicRecordsItem"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="full.na" priority="1">
		<!-- We will display the <full.na> if we have it, if not display <na>. -->
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="../label">
					<xsl:value-of select="../label"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_name;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="prefix" select="na.prefix"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mna"/>
			<xsl:with-param name="lastName" select="lna"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Birth -->
	<xsl:template match="birth.d.b" priority="2">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
			<xsl:with-param name="selectNodes" select="birth.d"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="pers.addr.info | emp.addr.b | prev.addr.b | supv.md.addr.b | outlet.addr.b" priority="1">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="label">
					<xsl:value-of select="label"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_address;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="count(addr | prev.addr | emp.addr | outlet.addr) > 1">
			<xsl:for-each select="addr[position() != last()] | prev.addr[position() != last()] | emp.addr[position() != last()] | outlet.addr[position() != last()]">
				<xsl:choose>
					<xsl:when test="position()=1">
						<xsl:call-template name="wrapPublicRecordsItem">
							<xsl:with-param name="defaultLabel" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="wrapPublicRecordsItem"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:if>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label">
				<xsl:if test="count(addr | prev.addr | emp.addr | outlet.addr) = 1">
					<xsl:value-of select="$label"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="street" select="addr[position()=last()] | emp.addr[position()=last()] | prev.addr[position()=last()] | supv.md.addr | outlet.addr[position()=last()]"/>
			<xsl:with-param name="city" select="cty | emp.cty | prev.cty | supv.md.cty | outlet.cty"/>
			<xsl:with-param name="stateOrProvince" select="st | emp.st | prev.st | prev.prov | supv.md.st | outlet.st | prov | emp.prov
											| prev.prov	| supv.md.prov | outlet.prov"/>
			<xsl:with-param name="zip" select="zip | emp.zip | prev.zip | supv.md.zip | outlet.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone -->
	<xsl:template match="phn.b | toll.free.phn.b | fax.b | prev.phone.b | emp.phn.b | emp.fax.b | outlet.phn.b" priority="1">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="label">
					<xsl:value-of select="label"/>
				</xsl:when>
				<xsl:when test="name()='fax.b' or name()='emp.fax.b'">
					<xsl:text>&pr_faxNumber;</xsl:text>
				</xsl:when>
				<xsl:when test="name()='prev.phone.b'">
					<xsl:text>&pr_previousPhone;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_phone;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="selectNodes" select="*[not(self::label)]"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ssn.b" priority="1">
		<xsl:apply-templates select="ssn"/>
		<xsl:apply-templates select="ssn.frag">
			<xsl:with-param name="isPrivacyProtected" select="$colKey='MN'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="ssn.frag" priority="1">
		<xsl:param name="isPrivacyProtected" select="false()"/>
		<xsl:param name="searchableLink" select="'false'"/>

		<xsl:if test="not(./optout.encrypted)">
			<xsl:if test="not(preceding-sibling::ssn)">
				<tr>
					<th>
						&pr_ssn;
					</th>
					<td>
						<xsl:call-template name="FormatSSN">
							<xsl:with-param name="nodevalue">
								<xsl:value-of select="concat('XXX-XX-',normalize-space(encrypted))"/>
							</xsl:with-param>
							<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
							<xsl:with-param name="searchableLink" select="$searchableLink"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ssn" priority="1">
		<xsl:variable name="isPrivacyProtected">
			<xsl:choose>
				<xsl:when test="$colKey='MN'">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="preceding-sibling::label | following-sibling::label"/>
			<xsl:with-param name="selectNodes" select="encrypted"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
			<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
		</xsl:call-template>
	</xsl:template>

	<!-- *********************************************************************
	*************************  Employer Information  *************************
	***********************************************************************-->
	<xsl:template match="emp.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_employerInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="emp.b"/>
			<xsl:apply-templates select="hire.d.b"/>
			<xsl:apply-templates select="release.d.b"/>
			<xsl:apply-templates select="supv.b"/>
		</table>
	</xsl:template>

	<xsl:template match="emp.b" priority="1">
		<!-- Specifically call the fields to change the order the data is displayed in -->
		<xsl:apply-templates select="emp.dba.b"/>
		<xsl:apply-templates select="emp.na.b"/>
		<xsl:apply-templates select="emp.addr.b"/>
		<xsl:apply-templates select="emp.mail.dist.b"/>
		<xsl:apply-templates select="emp.phn.b"/>
		<xsl:apply-templates select="emp.fax.b"/>
		<xsl:apply-templates select="emp.cnty.b"/>
		<xsl:apply-templates select="emp.pos.t.b"/>
		<xsl:apply-templates select="emp.stat.b"/>
		<xsl:apply-templates select="emp.fld.b" />
		<xsl:apply-templates select="emp.lic.t.b" />
		<xsl:apply-templates select="emp.lic.nbr.b"/>
		<xsl:apply-templates select="emp.stat.det.b"/>
		<xsl:apply-templates select="org.ind.b"/>
	</xsl:template>

	<xsl:template match="supv.b" priority="1">
		<xsl:apply-templates select="supv.na.b"/>
		<xsl:apply-templates select="supv.id.b"/>
		<xsl:apply-templates select="supv.lic.t.b"/>
		<xsl:apply-templates select="supv.lic.nbr.b"/>
	</xsl:template>

	<!-- *********************************************************************
	************************  Historical Information  ************************
	***********************************************************************-->
	<xsl:template match="historical.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_historicalInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- *********************************************************************
	************************  Licensing Information  *************************
	***********************************************************************-->
	<xsl:template match="licensing.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_licensingInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- *********************************************************************
	************************  Education Information  *************************
	***********************************************************************-->
	<xsl:template match="ed.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_educationInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- *********************************************************************
	*******************  Medical Professional Information  *******************
	***********************************************************************-->
	<xsl:template match="med.prof.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_medicalProfessionalInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- *********************************************************************
	******************  Attorney Professional Information  *******************
	***********************************************************************-->
	<xsl:template match="atty.prof.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_attorneyProfessionalInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- *********************************************************************
	******************  Insurance Professional Information  ******************
	***********************************************************************-->
	<xsl:template match="insurance.prof.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_insuranceProfessionalInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="atty.addr.b" priority="1">
		<!-- This is an odd use of wrapPublicRecordsAddress, but it should work based on how the data is formatted. -->
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="label"/>
			<xsl:with-param name="street" select="atty.addr"/>
			<xsl:with-param name="city" select="atty.cty.st.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- The atty.addr node seems to be duplicated quite often, this will suppress the display of any atty.addr node that is not the first one. -->
	<xsl:template match="atty.addr[position()>1]"/>

	<!-- *********************************************************************
	*****************  Real Estate Professional Information  *****************
	***********************************************************************-->
	<xsl:template match="real.estate.prof.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_RealEstateProfessionalInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- *********************************************************************
	*************************  Further Information  **************************
	***********************************************************************-->
	<xsl:template match="further.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_furtherInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="mail.addr[not(position()=1)]" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&pr_marginTop;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Match all nodes that are in a date block.  Assuming all nodes that end in ".d.b" are date blocks. -->
	<xsl:template match="*[substring(name(), string-length(name()) - string-length('.d.b') + 1) = '.d.b' and name() != 'med.sch.att.d.b']" priority="1">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="nodeType" select="$DATE"/>
			<xsl:with-param name="selectNodes" select="*[not(self::label)]"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sch.entry.d.b" priority="2">
		<xsl:call-template name="wrapProfessionalLicenseItem"/>
	</xsl:template>

	<xsl:template match="sch.entry.d" priority="1">
		<xsl:call-template name="FormatNonSensitiveDate">
			<xsl:with-param name="isoFormatted" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="eml" priority ="1">
		<xsl:call-template name="FormatEmailAddress">
			<xsl:with-param name="email" select="."/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
