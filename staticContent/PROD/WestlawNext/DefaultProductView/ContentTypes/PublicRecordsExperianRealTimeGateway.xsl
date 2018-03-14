<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="address/phones | normalizedaddress/phone"></xsl:template>


	<xsl:template match="Document" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExperianRealTimeCreditHeaderClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_creditHeaderRealTime;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/fraudservices"/>
		<xsl:call-template name="SourceInformation"></xsl:call-template>
	</xsl:template>
	<xsl:template name="PublicRecordsRightColumn">
		<xsl:call-template name="IndividualInformation"></xsl:call-template>
		<xsl:call-template name="BestAddressInformation"></xsl:call-template>
		<xsl:call-template name="OtherAddressInformation"></xsl:call-template>
	</xsl:template>

	<xsl:template name ="FraudAlertInformationTableHeader">
		<xsl:param name="heading"></xsl:param>
		<tr>
			<td>
				<h4>
					<strong>
						<xsl:value-of select="$heading"></xsl:value-of>
					</strong>
				</h4>
			</td>
			<td>
				<xsl:text disable-output-escaping="yes"><![CDATA[ ]]></xsl:text>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name ="FraudAlertInformationTableRow">
		<xsl:param name="heading"></xsl:param>
		<xsl:param name="code"></xsl:param>
		<tr>
			<td>
				<xsl:value-of select="$heading"/>
			</td>
			<td align="center">
				<xsl:choose>
					<xsl:when test="$heading = '&pr_otherSSN;'">
						<xsl:choose>
							<xsl:when test="count(//ssndata[not(ssn/text() = preceding::ssndata/ssn/text())]) &lt; 2">
								<xsl:value-of select="'&pr_no;'"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="//ssndata[not(ssn/text() = preceding::ssndata/ssn/text())]">
									<xsl:if test="position() != 1">
										<xsl:apply-templates select="." mode="otherSSN"/>
										<xsl:if test="position() != last()">
											<xsl:text>,<![CDATA[ ]]></xsl:text>
										</xsl:if>
									</xsl:if>
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$code = '26'">
								<xsl:choose>
									<xsl:when test="descendant::indicator[contains(., 'BEST ONFILE SSN NOT ISSUED AS OF')]">
											<xsl:variable name="ssnNotIssuedMsgText" select="descendant::indicator[contains(., 'BEST ONFILE SSN NOT ISSUED AS OF')]"/>
											<xsl:variable name="ssnTextLength" select="string-length($ssnNotIssuedMsgText)"/>
											<xsl:value-of select="substring($ssnNotIssuedMsgText, $ssnTextLength - 4)"/>
									</xsl:when>
									<xsl:when test="$code and descendant::code/number = $code">
										<xsl:value-of select="'&pr_yes;'"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="'&pr_no;'"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:when test="$code and descendant::code/number = $code">
								<xsl:value-of select="'&pr_yes;'"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'&pr_no;'"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>


	<!--
			Fraud Alert Indicators
			================
			01                            INQUIRY/ONFILE CURRENT ADDRESS CONFLICT
			02                            INQUIRY ADDRESS 1ST REPORTED < 90 DAYS
			03                            INQUIRY CURRENT ADDRESS NOT ONFILE
			04                            INQUIRY SSN HAS NOT BEEN ISSUED
			05                            INQUIRY SSN RECORDED AS DECEASED
			06                            INQUIRY AGE YOUNGER THAN SSN ISSUE DATE
			10                            INQUIRY ADDRESS: ALERT
			11                            INQUIRY ADDRESS: NON-RESIDENTIAL
			13                            HIGH PROBABILITY SSN BELONGS TO ANOTHER
			14                            INQUIRY SSN FORMAT IS INVALID
			15                            INQUIRY ADDRESS: CAUTIOUS
			16                            ONFILE ADDRESS: ALERT
			17                            ONFILE ADDRESS: NON-RESIDENTIAL
			18                            ONFILE ADDRESS: CAUTIOUS
			21                            TELEPHONE NUMBER INCONSISTENT W/ADDRESS
			25                            BEST ONFILE SSN RECORDED AS DECEASED
			26                            BEST ONFILE SSN NOT ISSUED AS OF MMYYY
			27                            SSN REPORTED MORE FREQUENTLY FOR ANOTHER
				(we do not display 28-30)
			28                            AUTH USER TRADE ADDED IN LAST 120 DAYS
			29                            RECENT AUTH USER ON ACCT OPEN OVER 5 YRS
			30                            MORE AUTH USER TRADES THAN OTHER TRADES
		-->

	<xsl:template match="fraudservice">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_fraudAlertInformation;'" />
		</xsl:call-template>
		<table class="&pr_table; &pr_fraudAlertTable;">
			<xsl:call-template name="FraudAlertInformationTableHeader">
				<xsl:with-param name="heading" select="'&pr_ssnAlert;'"/>
			</xsl:call-template>

			<!--<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_bestSsnMatch;'"/>
				<xsl:with-param name="hasAlert" select="true()"/>
				<xsl:with-param name="hasAlert" select="(descendant::SSN[not(ancestor::ConfirmedVerifiedSSN)])[1]/VariationIndicator = 'Same'"/>
			</xsl:call-template>-->

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquirySsnHasNotBeenIssued;'"/>
				<xsl:with-param name="code" select="'04'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquirySsnRecordedAsDeceased;'"/>
				<xsl:with-param name="code" select="'05'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryAgeYounger;'"/>
				<xsl:with-param name="code" select="'06'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_highProbabilitySSNBelongsToAnother;'"/>
				<xsl:with-param name="code" select="'13'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquirySsnFormatIsInvalid;'"/>
				<xsl:with-param name="code" select="'14'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_bestOnFileSsnIsDead;'"/>
				<xsl:with-param name="code" select="'25'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_bestOnFileSsnNotIssued;'"/>
				<xsl:with-param name="code" select="'26'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_ssnReportedForAnother;'"/>
				<xsl:with-param name="code" select="'27'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_otherSSN;'"/>
			</xsl:call-template>

			<xsl:call-template name="EmptyRow"></xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableHeader">
				<xsl:with-param name="heading" select="'&pr_addressAlerts;'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryOnlineCurrentAddressConflict;'"/>
				<xsl:with-param name="code" select="'01'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryAddressReported;'"/>
				<xsl:with-param name="code" select="'02'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryAddressNotOnfile;'"/>
				<xsl:with-param name="code" select="'03'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryAddressAlert;'"/>
				<xsl:with-param name="code" select="'10'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryAddressNonResidential;'"/>
				<xsl:with-param name="code" select="'11'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryAddressCautious;'"/>
				<xsl:with-param name="code" select="'15'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_bestAddressAlart;'"/>
				<xsl:with-param name="code" select="'16'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_bestAddressNonResidential;'"/>
				<xsl:with-param name="code" select="'17'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_bestAddressCautious;'"/>
				<xsl:with-param name="code" select="'18'"/>
			</xsl:call-template>

			<xsl:call-template name="FraudAlertInformationTableRow">
				<xsl:with-param name="heading" select="'&pr_inquiryTelephoneInconsistent;'"/>
				<xsl:with-param name="code" select="'21'"/>
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="SourceInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_sourceInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="Document/realtimeupdate/reportdate"/>
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_source;</xsl:text>
				</th>
				<td>
					<xsl:text>&pr_experianCreditHeader;</xsl:text>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="IndividualInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_individualInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/personnames/personname"/>
			<xsl:apply-templates select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/ssns/ssndata[position() = 1]"/>
			<xsl:apply-templates select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/dobs"/>
			<xsl:apply-templates select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/phones"/>
		</table>
	</xsl:template>

	<xsl:template name="BestAddressInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_bestAddressInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="ExperianAddress">
				<xsl:with-param name="isBestAddress" select="true()"></xsl:with-param>
				<xsl:with-param name="content" select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/addresses/address[position() = 1]"></xsl:with-param>
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="OtherAddressInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_otherAddressInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="Document/realtimeupdate/realtimeupdatepersons/realtimeupdateperson/addresses/address[position() != 1]"></xsl:apply-templates>
		</table>
	</xsl:template>

	<xsl:template match="reportdate">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_currentDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="personname">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="position() = 1">
					<xsl:value-of select="'&pr_bestName;'"/>
				</xsl:when>
				<xsl:when test="position() = 2">
					<xsl:value-of select="'&pr_otherName;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="' '"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="$label"></xsl:with-param>
			<xsl:with-param name="prefixName" select="prefix"></xsl:with-param>
			<xsl:with-param name="firstName" select="firstname"></xsl:with-param>
			<xsl:with-param name="middleName" select="middlename"></xsl:with-param>
			<xsl:with-param name="lastName" select="lastname"></xsl:with-param>
			<xsl:with-param name="suffixName" select="gen"></xsl:with-param>
		</xsl:call-template>

		<xsl:if test="position() = 1 or position() = last()">
			<xsl:call-template name="EmptyRow"></xsl:call-template>
		</xsl:if>
		
		<xsl:if test="position() = 1 and spousename">
				<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_spouseName;'"></xsl:with-param>
				<xsl:with-param name="selectNodes" select="spousename"/>
			</xsl:call-template>
			<xsl:call-template name="EmptyRow"></xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dobs">
		<xsl:variable name="bestDOB">
			<xsl:choose>
				<xsl:when test="dob[2]/fulldob and string-length(dob[2]/fulldob/text()) &gt; 4">
					<xsl:value-of select="dob[2]/fulldob"/>
				</xsl:when>
				<xsl:when test="dob[1]/fulldob">
					<xsl:value-of select="dob[1]/fulldob"/>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="$bestDOB">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"></xsl:with-param>
				<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
				<xsl:with-param name="selectNodes" select="$bestDOB"/>
			</xsl:call-template>
			<xsl:call-template name="EmptyRow"></xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ssndata" mode="otherSSN">
		<xsl:call-template name="SSNProcess">
			<xsl:with-param name="ssnvalue" select="ssn"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ssndata">
		<xsl:variable name="currentSSN" select="ssn"/>

		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bestSsn;'"></xsl:with-param>
			<xsl:with-param name="nodeType" select="$SSN"/>
			<xsl:with-param name="selectNodes" select="$currentSSN"/>
		</xsl:call-template>

		<xsl:variable name="times">
			<xsl:value-of select="@timesreportedforsamename"/>
		</xsl:variable>		

		<!-- If we can't get the times report from the attribute, fall back to counting matching nodes -->
		<xsl:variable name="fallBackCount">
			<xsl:value-of select="count(//ssn[text() = $currentSSN/text()])"/>
		</xsl:variable>		
		
		<!-- Taking a wild stab at this, as the documentation is non-existent, and we can't do live gateway calls from lower envs -->
		<xsl:choose>
			<xsl:when test="$times and not($times = '0')">
				<xsl:call-template name="displayLabelValue">
					<xsl:with-param name="label" select="'Times Reported:'"/>
					<xsl:with-param name="value" select="$times"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="displayLabelValue">
					<xsl:with-param name="label" select="'Times Reported:'"/>
					<xsl:with-param name="value" select="$fallBackCount"/>
				</xsl:call-template>				
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:call-template name="EmptyRow"></xsl:call-template>
	</xsl:template>
	
	<xsl:template name="displayLabelValue">
		<xsl:param name="label" select="'label'"/>
		<xsl:param name="value" select="'value'"/>

			<tr class="&pr_item;">
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="$label"/>
				</xsl:call-template>
				<td>
					<xsl:value-of select="$value"/>
				</td>
			</tr>

	</xsl:template>
		
	<xsl:template match="realtimeupdateperson/phones">
		<xsl:apply-templates select="phone"/>
	</xsl:template>

	<xsl:template match="phone">
		<tr class="&pr_item;">
			<th>
				<xsl:if test="position() = 1">
					<xsl:text>&pr_telephoneNumber;</xsl:text>
				</xsl:if>

			</th>
			<td>
				<xsl:apply-templates select="phonenumber"></xsl:apply-templates>
				<xsl:apply-templates select="phonetype"></xsl:apply-templates>
				<xsl:apply-templates select="source"></xsl:apply-templates>
			</td>
		</tr>
		<xsl:if test="position() != last()">
			<xsl:call-template name="EmptyRow"></xsl:call-template>
		</xsl:if>

	</xsl:template>

	<xsl:template match="phonenumber">
		<div>
			<xsl:call-template name="FormatPhone">
				<xsl:with-param name="phone" select="."/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="phonetype">
		<div>
			<xsl:text>&pr_type;<![CDATA[ ]]></xsl:text>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="source">
		<div>
			<xsl:text>&pr_source;<![CDATA[ ]]></xsl:text>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="address" name="ExperianAddress">
		<xsl:param name="isBestAddress" select="false()"></xsl:param>
		<xsl:param name="content" select="."></xsl:param>

		<xsl:variable name="addressLabel">
			<xsl:choose>
				<xsl:when test="$isBestAddress = true()">
					<xsl:value-of select="'&pr_bestAddress;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&pr_otherAddress;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$addressLabel"></xsl:with-param>
			<xsl:with-param name="fullStreet" select="$content/street"></xsl:with-param>
			<xsl:with-param name="stateOrProvince" select="$content/state"></xsl:with-param>
			<xsl:with-param name="city" select="$content/city"></xsl:with-param>
			<xsl:with-param name="zip" select="$content/zip"></xsl:with-param>
			<xsl:with-param name="zipext" select="$content/zipext"></xsl:with-param>
			<xsl:with-param name="country" select="$content/country"></xsl:with-param>
		</xsl:call-template>

		<xsl:if test="$content/timesreported">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="' '"></xsl:with-param>
				<xsl:with-param name="selectNodes" select="$content/timesreported"></xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<xsl:apply-templates select="$content/datasources/datasource/firstreported"/>
		<xsl:apply-templates select="$content/datasources/datasource/lastreported"/>

		<xsl:if test="position() != last()">
			<xsl:call-template name="EmptyRow"></xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="firstreported">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressFirstReported;'"></xsl:with-param>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lastreported">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressLastReported;'"></xsl:with-param>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="timesreportedforsamename | timesreported">
		<xsl:text>&pr_reported;<![CDATA[ ]]></xsl:text>
		<xsl:call-template name="RemoveLeadingZero"/>
		<xsl:text><![CDATA[ ]]>&pr_times;</xsl:text>
	</xsl:template>

	<xsl:template name="RemoveLeadingZero">
		<xsl:choose>
			<xsl:when test=". = '00'">
				<xsl:text>0</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring-after(., substring-before(., substring(translate(., '0', ''), 1, 1)))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="EmptyRow">
		<tr>
			<td>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</td>
			<td>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>

