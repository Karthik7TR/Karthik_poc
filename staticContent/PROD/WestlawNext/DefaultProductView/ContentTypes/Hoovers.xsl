<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Universal.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />
	
		<!--Variables-->
	<xsl:variable name="pvalue" select="/Document/n-docbody/r/p"/>
	<xsl:variable name="pcvalue" select="/Document/n-docbody/r/pc"/>
	<xsl:variable name="stateId" select="/Document/n-docbody/r/col.key"/>

	<!-- Parameter(s) -->
	<xsl:param name="View" select="'content'"/>

	<!-- Element(s) not displayed -->
	<xsl:template match="map|sort-pub-date|rank-date|ch.indus.cd|par.indus.cd|key.cmpt.cd" />
	
	<xsl:template match="/">
		<div id="&documentClass;" class="&documentClass;">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<div class="&hp_HooversProfile;">
				<xsl:call-template name="Content"/>
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<!-- Render the CONTENT view. -->
	<xsl:template name="Content">
		<!-- 
			This content is comprised of five major sections:
			- (A) Document Header
			- (B) Coverage Information:obsoleted
			- (C) Company Information
		-->
		<div class="&hp_hooversReportDetails;">
			<!--(A) Document Header-->
			<xsl:call-template name="Header"/>

			<!-- (B) Coverage Information -->
			<xsl:call-template name ="CoverageBlock"/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<!--************************************************************************
	******************************  (A)HEADER  *********************************
	*************************************************************************-->
	<xsl:template name="Header">
			<div class="&hp_reportMainHeader;">
				<xsl:choose>
					<xsl:when test ="$pcvalue = 'HCR'">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversHeaderHCRKey;', '&hooversHeaderHCR;')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversHeaderKey;', '&hooversHeader;')"/>
					</xsl:otherwise>
				</xsl:choose>
			</div>
	</xsl:template>
	
	<!-- ********************************************************************** 
	*************************  (B)"Coverage" section  *************************
	************************************************************************-->
	<xsl:template name ="CoverageBlock">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversSourceInformationKey;', '&hooversSourceInformation;')"/>
		</div>
		<table class="&hp_reportContentTable;">
			<xsl:call-template name ="DatabaseLastUpdated"/>
			<xsl:call-template name ="UpdateFrequency"/>
			<xsl:call-template name ="CurrentDate"/>
			<xsl:call-template name ="Source"/>
		</table>
	</xsl:template>
	
	<!--Database Last Updated-->
	<xsl:template name ="DatabaseLastUpdated">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversDatabaseLastUpdatedKey;', '&hooversDatabaseLastUpdated;')"/>
			</td>
			<td class="&hp_reportDetails;">				
				<xsl:variable name ="date">
					<xsl:value-of select="/Document/n-docbody/r/pub.d"/>
				</xsl:variable>

				<xsl:choose>
					<!-- Check if it is already a formatted date. -->
					<xsl:when test="(substring($date, 3, 1)='/') or (substring($date, 6, 1)='/')">
						<xsl:value-of select="$date"/>
					</xsl:when>
					<xsl:otherwise>
						<!-- Format as MM/dd/yyyy. -->
						<!-- Month -->
						<xsl:if test="string-length($date) &gt; 5">
							<xsl:if test="not(substring($date, 5, 2)='00')">
								<xsl:value-of select="substring($date, 5, 2)"/>
								<xsl:text>/</xsl:text>
							</xsl:if>
						</xsl:if>

						<!-- Day -->
						<xsl:if test="string-length($date) &gt; 7">
							<xsl:if test="not(substring($date, 7, 2)='00' or substring($date, 5, 2)='00')">
								<xsl:value-of select="substring($date, 7, 2)"/>
								<xsl:text>/</xsl:text>
							</xsl:if>
						</xsl:if>

						<!-- Year -->
						<xsl:value-of select="substring($date, 1, 4)"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
	
	<!--Update Frequency-->
	<xsl:template name ="UpdateFrequency">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversUpdateFrequencyKey;', '&hooversUpdateFrequency;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:value-of select ="'Weekly'"/>
			</td>
		</tr>
	</xsl:template>
	
	<!--Current Date-->
	<xsl:template name ="CurrentDate">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCurrentDateKey;', '&hooversCurrentDate;')"/>
			</td>
			<td class="&hp_reportDetails;">
				 <xsl:variable name="date">
						 <xsl:value-of select="$currentDate" />
				 </xsl:variable>

				 <xsl:variable name="mm">			 
						<xsl:value-of select="substring-before($date, '/')"/>
				 </xsl:variable>
				
				 <xsl:variable name="ddyyyy">
					 <xsl:value-of select="substring-after($date, '/')"/>
				 </xsl:variable>
 
				 <xsl:variable name="dd">
					 <xsl:value-of select="substring-before($ddyyyy, '/')"/>
				 </xsl:variable>
 
				 <xsl:variable name="yyyy">
						<xsl:value-of select="substring-after($ddyyyy, '/')"/>
				 </xsl:variable>
 
				 <xsl:value-of select="concat(format-number($mm, '00'),'/', format-number($dd, '00'), '/', $yyyy)" />
			</td>
		</tr>
	</xsl:template>

	<!--Source-->
	<xsl:template name ="Source">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversSourceKey;', '&hooversSource;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCopyrightKey;', '&hooversCopyright;')"/>
			</td>
		</tr>
	</xsl:template>
	
		<!-- **********************************************************************
	**********************  Rendering the Document body section.  *************
	************************************************************************-->
	<xsl:template match ="r">
		<xsl:apply-templates select="co.info.b"/>
		<xsl:apply-templates select="synop"/>
		<xsl:apply-templates select ="key.nbr.b"/>
		<xsl:apply-templates select="top.off.b"/>
		<xsl:apply-templates select="full.off.list.b"/>
		<xsl:apply-templates select="dir.list.b"/>
		<xsl:apply-templates select ="overvw"/>
		<xsl:apply-templates select ="hstry"/>
		<xsl:call-template name ="ProductionSection"/>
		<xsl:call-template name ="KeyCompetitors"/>
		<xsl:call-template name ="IndustrySection"/>
	</xsl:template>

	<!-- **********************************************************************
	************************  Company Information  ************************
	************************************************************************-->
	<!--Company Address-->
	<xsl:template match ="co.info.b">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyInformationKey;', '&hooversCompanyInformation;')"/>
		</div>
		<table class="&hp_reportContentTable;">
			<xsl:if test ="string-length(co.name) > 0">
				<xsl:apply-templates select ="co.name"/>
			</xsl:if>
			<xsl:if test ="bus.addr.b">
				<xsl:apply-templates select ="bus.addr.b"/>
			</xsl:if>
			<xsl:if test ="string-length(cntry) > 0">
				<xsl:apply-templates select="cntry"/>
			</xsl:if>
			<xsl:if test ="string-length(phn) > 0">
				<xsl:call-template name ="Telephone"/>
			</xsl:if>
			<xsl:if test ="string-length(fax) > 0">
				<xsl:call-template name ="Fax"/>
			</xsl:if>
			<xsl:if test ="string-length(toll.free.phn) > 0">
				<xsl:apply-templates select ="toll.free.phn"/>
			</xsl:if>
			<xsl:if test ="string-length(url) > 0">
				<xsl:apply-templates select ="url"/>
			</xsl:if>
			<xsl:if test ="string-length(co.typ) > 0">
				<xsl:apply-templates select ="co.typ"/>
			</xsl:if>
			<xsl:if test ="string-length(us.tic) > 0">
				<xsl:apply-templates select="us.tic"/>
			</xsl:if>
			<xsl:if test ="string-length(us.exc) > 0">
				<xsl:apply-templates select ="us.exc"/>
			</xsl:if>
			<xsl:if test ="string-length(frgn.tic) > 0">
				<xsl:apply-templates select="frgn.tic"/>
			</xsl:if>
			<xsl:if test ="string-length(frgn.exc) > 0">
				<xsl:apply-templates select="frgn.exc"/>
			</xsl:if>
			<xsl:if test ="rank.b">
				<xsl:call-template name ="CompanyRanking"/>
			</xsl:if>
			<xsl:if test ="string-length(audit.yr) > 0">
				<xsl:apply-templates select ="audit.yr"/>
			</xsl:if>
			<xsl:if test ="string-length(audit.nm) > 0">
				<xsl:apply-templates select ="audit.nm"/>
			</xsl:if>
		</table>
	</xsl:template>

	<!-- Company Name -->
	<xsl:template match="co.name">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyNameKey;', '&hooversCompanyName;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Address Information -->
	<xsl:template match="bus.addr.b">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversAddressKey;', '&hooversAddress;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<div>
					<xsl:apply-templates select="bus.addr"/>
				</div>
				<div>
					<xsl:apply-templates select="bus.cty"/>
					<xsl:text>, </xsl:text>
					<xsl:apply-templates select="bus.st"/>
					<xsl:text> </xsl:text>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="bus.zip"/>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!-- Country Information -->
	<xsl:template match="cntry">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCountryKey;', '&hooversCountry;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Telephone Information -->
	<xsl:template name="Telephone">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTelephoneKey;', '&hooversTelephone;')"/>
			</td>
			<td class="&hp_reportDetails;">
					<xsl:for-each select ="phn">
						<div>
							<xsl:apply-templates/>
						</div>
					</xsl:for-each>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match ="phn">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Fax Information -->
	<xsl:template name="Fax">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversFaxKey;', '&hooversFax;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:for-each select ="fax">
					<div>
						<xsl:apply-templates/>
					</div>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match ="fax">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Toll Free Information -->
	<xsl:template match="toll.free.phn">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTollFreeKey;', '&hooversTollFree;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Website Information -->
	<xsl:template match="url">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversWebsiteKey;', '&hooversWebsite;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Company Type Information -->
	<xsl:template match="co.typ">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyTypeKey;', '&hooversCompanyType;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- U.S. Ticker Information -->
	<xsl:template match="us.tic">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversUSTickerKey;', '&hooversUSTicker;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- U.S. Exchange Information -->
	<xsl:template match="us.exc">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversUSExchangeKey;', '&hooversUSExchange;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Company Rankings -->
	<xsl:template name ="CompanyRanking">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyRankingsKey;', '&hooversCompanyRankings;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:for-each select ="rank.b">
					<div>
						<xsl:apply-templates select="rank.typ"/>
						<xsl:apply-templates select="rank"/>
					</div>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>
	
	<!--template rank.typ-->
	<xsl:template match ="rank.typ">
		<xsl:apply-templates/>
	</xsl:template>
	
	<!--template rank-->
	<xsl:template match ="rank">
		<xsl:text>: </xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--(Year) Auditor Name -->
	<xsl:template match="audit.nm">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select ="/Document/n-docbody/r/co.info.b/audit.yr"/>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversAuditorKey;', '&hooversAuditor;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match ="audit.yr"/>

	<!-- Foreign Ticker Information -->
	<xsl:template match="frgn.tic">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversForeignTickerKey;', '&hooversForeignTicker;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Foreign Exchange Information -->
	<xsl:template match="frgn.exc">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversForeignExchangeKey;', '&hooversForeignExchange;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- **********************************************************************
	************************  Key Numbers Information  ************************
	************************************************************************-->
	<!--Key Numbers-->
	<xsl:template match ="key.nbr.b">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversKeyNumbersKey;', '&hooversKeyNumbers;')"/>
		</div>
		<table class="&hp_reportContentTable;">
			<xsl:apply-templates select ="fye"/>
			<xsl:if test ="sales.yr or sales.amt">
				<xsl:call-template name ="SalesYear">
					<xsl:with-param name ="salesyr" select ="sales.yr"/>
					<xsl:with-param name="salesamt" select ="sales.amt"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select ="sales.growth"/>
			<xsl:if test ="net.inc.yr or net.inc.amt">
				<xsl:call-template name ="NetIncome">
					<xsl:with-param name ="netincyr" select ="net.inc.yr"/>
					<xsl:with-param name ="netincamt" select ="net.inc.amt"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select="inc.growth"/>
			<xsl:if test ="nbr.emp.yr or nbr.emp">
				<xsl:call-template name ="NumberEmployee">
					<xsl:with-param name ="nbrempyr" select ="nbr.emp.yr"/>
					<xsl:with-param name ="nbremp" select ="nbr.emp"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select ="emp.growth"/>
		</table>
	</xsl:template>

	<!-- Fiscal Year End -->
	<xsl:template match="fye">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversFiscalYearEndKey;', '&hooversFiscalYearEnd;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Sales year -->
	<xsl:template name ="SalesYear">
		<xsl:param name ="salesyr"/>
		<xsl:param name ="salesamt"/>
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select ="$salesyr"/>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversSalesKey;', '&hooversSales;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:value-of select ="$salesamt"/>
			</td>
		</tr>
	</xsl:template>
	<!-- One Year Sales Growth-->
	<xsl:template match="sales.growth">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversOneYearSalesGrowthKey;', '&hooversOneYearSalesGrowth;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- (Year) Net Income (US $ millions) -->
	<xsl:template name ="NetIncome">
		<xsl:param name ="netincyr"/>
		<xsl:param name ="netincamt"/>
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select ="$netincyr"/>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversNetIncomeKey;', '&hooversNetIncome;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:value-of select ="$netincamt"/>
			</td>
		</tr>
	</xsl:template>
	
	<!--One Year Income Growth-->
	<xsl:template match ="inc.growth">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversOneYearIncomeGrowthKey;', '&hooversOneYearIncomeGrowth;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Number Employee -->
	<xsl:template name ="NumberEmployee">
		<xsl:param name ="nbrempyr"/>
		<xsl:param name ="nbremp"/>
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select ="$nbrempyr"/>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversEmployeesKey;', '&hooversEmployees;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:value-of select ="$nbremp"/>
			</td>
		</tr>
	</xsl:template>

	<!-- One Year Employee Growth-->
	<xsl:template match="emp.growth">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversOneYearEmployeeGrowthKey;', '&hooversOneYearEmployeeGrowth;')"/>
			</td>
			<td class="&hp_reportDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- **********************************************************************
	************************  Top Officers Information  ************************
	************************************************************************-->
	<xsl:template match ="top.off.b">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTopOfficersKey;', '&hooversTopOfficers;')"/>
		</div>
		<xsl:call-template name ="TopOfficersTable"/>
	</xsl:template>
	<!--Top Officers table-->
	<xsl:template name ="TopOfficersTable">
		<xsl:variable name ="col1Width" select ="translate(tbl/table/tgroup/colspec[@colname='col1']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col2Width" select ="translate(tbl/table/tgroup/colspec[@colname='col2']/@colwidth, '*', '%')"/>
		<table class="&hp_reportDataTable;">
			<tr class="&hp_reportDataHeader;">
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col1Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTitleKey;', '&hooversTitle;')"/>
				</xsl:element>
				<xsl:element name ="td">			
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col2Width"/>
					</xsl:attribute>
					
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversNameKey;', '&hooversName;')"/>
				</xsl:element>
			</tr>
			<xsl:for-each select ="tbl/table/tgroup/tbody/row">
				<tr>
					<td>
						<xsl:value-of select ="entry/off.title"/>
					</td>
					<td>
						<xsl:value-of select ="entry/off.name"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!-- **********************************************************************
	************************  Officers Information  ************************
	************************************************************************-->
	<!--Officers table-->
	<xsl:template match ="full.off.list.b">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversOfficersKey;', '&hooversOfficers;')"/>
		</div>
		<xsl:call-template name="OfficersTable"/>
	</xsl:template>

	<!--Officers Table-->
	<xsl:template name ="OfficersTable">
		<!--Define style for each column-->
		<xsl:variable name ="col1Width" select ="translate(tbl/table/tgroup/colspec[@colname='col1']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col2Width" select ="translate(tbl/table/tgroup/colspec[@colname='col2']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col3Width" select ="translate(tbl/table/tgroup/colspec[@colname='col3']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col4Width" select ="translate(tbl/table/tgroup/colspec[@colname='col4']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col5Width" select ="translate(tbl/table/tgroup/colspec[@colname='col5']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col6Width" select ="translate(tbl/table/tgroup/colspec[@colname='col6']/@colwidth, '*', '%')"/>
		<table class="&hp_reportDataTable;">
			<tr class="&hp_reportDataHeader;">
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col1Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTitleKey;', '&hooversTitle;')"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col2Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversNameKey;', '&hooversName;')"/>
				</xsl:element>
				<xsl:element name ="td">					
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col3Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversAgeKey;', '&hooversAge;')"/>
				</xsl:element>
				<xsl:element name ="td">					
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col4Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversSalaryKey;', '&hooversSalary;')"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col5Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversBonusKey;', '&hooversBonus;')"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col6Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTotalCompensationKey;', '&hooversTotalCompensation;')"/>
				</xsl:element>
			</tr>
			<xsl:for-each select="tbl/table/tgroup/tbody/row">
				<tr>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/off.title) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/off.title"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/off.name) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/off.name"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/off.age) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/off.age"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/off.slry) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/off.slry"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/off.bonus) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/off.bonus"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/off.compen) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/off.compen"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!-- **********************************************************************
	************************  Directors Information  ************************
	************************************************************************-->
	<!--Directors table-->
	<xsl:template match ="dir.list.b">
		<xsl:if test ="/Document/n-docbody/r/dir.list.b">
			<div class="&hp_reportSubHeader;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversDirectorsKey;', '&hooversDirectors;')"/>
			</div>
			<xsl:call-template name="DirectorTable"/>
		</xsl:if>
	</xsl:template>

	<!--Director table-->
	<xsl:template name ="DirectorTable">
		<!--Define style for each column-->
		<xsl:variable name ="col1Width" select ="translate(tbl/table/tgroup/colspec[@colname='col1']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col2Width" select ="translate(tbl/table/tgroup/colspec[@colname='col2']/@colwidth, '*', '%')"/>
		<xsl:variable name ="col3Width" select ="translate(tbl/table/tgroup/colspec[@colname='col3']/@colwidth, '*', '%')"/>

		<table class="&hp_reportDataTable;">
			<tr class="&hp_reportDataHeader;">
				<xsl:element name ="td">					
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col1Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversTitleKey;', '&hooversTitle;')"/>
				</xsl:element>
				<xsl:element name ="td">					
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col2Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversNameKey;', '&hooversName;')"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:</xsl:text><xsl:value-of select ="$col3Width"/>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversAgeKey;', '&hooversAge;')"/>
				</xsl:element>
			</tr>
			<xsl:for-each select ="tbl/table/tgroup/tbody/row">
				<tr>
					<td>
						<xsl:value-of select ="entry/dir.title"/>
					</td>
					<td>
						<xsl:value-of select ="entry/dir.name"/>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test ="string-length(entry/dir.age) = 0">
								<xsl:text>-</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select ="entry/dir.age"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!-- **********************************************************************
	************************  Key Competitors Information  ************************
	************************************************************************-->

	<!--Template Name KeyCompetitors-->
	<xsl:template name ="KeyCompetitors">
		<xsl:if test ="/Document/n-docbody/r/key.cmpt.info.b">
			<div class="&hp_reportSubHeader;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversKeyCompetitorsKey;', '&hooversKeyCompetitors;')"/>
			</div>
			<table class="&hp_reportContentTable;">
				<tr>
					<td class="&hp_reportLabel;">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversKeyCompetitorsColonKey;', '&hooversKeyCompetitorsColon;')"/>
					</td>
					<td class="&hp_reportDetails;">
						<xsl:for-each select ="/Document/n-docbody/r/key.cmpt.info.b">
							<xsl:apply-templates/>
						</xsl:for-each>
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match ="key.cmpt">
		<xsl:apply-templates/>
		<br/>
	</xsl:template>

	<!-- **********************************************************************
	************************  Product Information  ************************
	************************************************************************-->
	<!--Production Section-->
	<xsl:template name ="ProductionSection">
		<xsl:if test ="/Document/n-docbody/r/prod.ops.b">
			<div class="&hp_reportSubHeader;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversProductionandOperationsKey;', '&hooversProductionandOperations;')"/>
			</div>
			<xsl:apply-templates select ="/Document/n-docbody/r/prod.ops.b/prod.tbl"/>
			<xsl:apply-templates select ="/Document/n-docbody/r/prod.ops.b/ops.tbl/tbl"/>
			<xsl:call-template name ="MainEntry"/>
			<br/>
		</xsl:if>
	</xsl:template>
	
	<!--ops.tbl template-->
	<xsl:template match ="/Document/n-docbody/r/prod.ops.b/ops.tbl/tbl">
		<table class="&hp_reportDataTable;">
			<tr class="&hp_reportDataHeader;">
				<xsl:element name ="td">					
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="table/tgroup/thead/row/entry[@colname='col1']/cell"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="table/tgroup/thead/row/entry[@colname='col2']/cell"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="table/tgroup/thead/row/entry[@colname='col3']/cell"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="table/tgroup/thead/row/entry[@colname='col4']/cell"/>
				</xsl:element>
			</tr>
			<xsl:for-each select ="table/tgroup/tbody/row">
				<tr>
					<td>
						<xsl:value-of select ="entry[@colname='col1']/cell"/>
					</td>
					<td>
						<xsl:value-of select ="entry[@colname='col2']/cell"/>
					</td>
					<td>
						<xsl:value-of select ="entry[@colname='col3']/cell"/>
					</td>
					<td>
						<xsl:value-of select ="entry[@colname='col4']/cell"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!--prod.tbl template-->
	<xsl:template match ="/Document/n-docbody/r/prod.ops.b/prod.tbl">
		<table class="&hp_reportDataTable;">
			<tr class="&hp_reportDataHeader;">
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="tbl/table/tgroup/thead/row/entry[@colname='col1']/cell"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="tbl/table/tgroup/thead/row/entry[@colname='col2']/cell"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="tbl/table/tgroup/thead/row/entry[@colname='col3']/cell"/>
				</xsl:element>
				<xsl:element name ="td">
					<xsl:attribute name="style">
						<xsl:text>width:25%</xsl:text>
					</xsl:attribute>
					<xsl:value-of select ="tbl/table/tgroup/thead/row/entry[@colname='col4']/cell"/>
				</xsl:element>
			</tr>
			<xsl:for-each select ="tbl/table/tgroup/tbody/row">
				<tr>
					<td>
						<xsl:value-of select ="entry[@colname='col1']/cell"/>
					</td>
					<td>
						<xsl:value-of select ="entry[@colname='col2']/cell"/>
					</td>
					<td>
						<xsl:value-of select ="entry[@colname='col3']/cell"/>
					</td>
					<td>
						<xsl:value-of select ="entry[@colname='col4']/cell"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!--Main entry-->
	<xsl:template name ="MainEntry">
		<xsl:if test ="/Document/n-docbody/r/prod.ops.b/ops.serv.b">
			<xsl:for-each select ="/Document/n-docbody/r/prod.ops.b/ops.serv.b/main.entry">
				<xsl:apply-templates/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template match ="entry.txt">
		<div>
			<xsl:apply-templates/>
			<br/>
			<br/>
		</div>
	</xsl:template>
	<xsl:template match ="secd.entry/entry.txt">
		<div class="&hp_reportTextIndentSmall;">
			<xsl:apply-templates/>
			<br/>
			<br/>
		</div>
	</xsl:template>

	<xsl:template match ="secd.entry/secd.entry/entry.txt">
		<div class="&hp_reportTextIndentMedium;">
			<xsl:apply-templates/>
			<br/>
			<br/>
		</div>
	</xsl:template>

	<!-- **********************************************************************
	************************  Company Description Information  ***************
	************************************************************************-->
	<!--Business Description-->
	<xsl:template match ="synop">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyDescriptionKey;', '&hooversCompanyDescription;')"/>
		</div>
		<table class="&hp_reportContentTable;">
			<tr>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<!-- **********************************************************************
	************************  Overview Information  ***************
	************************************************************************-->
	<xsl:template match ="overvw">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyOverviewKey;', '&hooversCompanyOverview;')"/>
		</div>
		<table class="&hp_reportContentTable;">
			<tr>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<!-- **********************************************************************
	************************  History Information  ***************
	************************************************************************-->
	<xsl:template match ="hstry">
		<div class="&hp_reportSubHeader;">
			<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversCompanyHistoryKey;', '&hooversCompanyHistory;')"/>
		</div>
		<table class="&hp_reportContentTable;">
			<tr>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<!--Para template-->
	<xsl:template match ="para">
		<xsl:apply-templates />
	</xsl:template>

	<!--paratext template-->
	<xsl:template match="paratext">
		<xsl:apply-templates/>
		<br/>
		<br/>
	</xsl:template>

	<!-- **********************************************************************
	************************  Industry Information  ***************
	************************************************************************-->
	<xsl:template name ="IndustrySection">
		<xsl:if test ="/Document/n-docbody/r/indus.info.b">
			<div class="&hp_reportSubHeader;">
				<xsl:choose>
					<xsl:when test ="$pcvalue = 'HCR'">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversIndustryDescriptionsKey;', '&hooversIndustryDescriptions;')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversIndustryDescriptionsAndCodesKey;', '&hooversIndustryDescriptionsAndCodesKey;')"/>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<table class="&hp_reportContentTable;">
				<xsl:choose>
					<xsl:when test="$pcvalue='HCR'">
						<xsl:apply-templates select ="/Document/n-docbody/r/indus.info.b/indus.b"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select ="/Document/n-docbody/r/indus.info.b/indus.b"/>
						<xsl:call-template name ="SICCodes"/>
						<xsl:call-template name ="NAICSCodes"/>
					</xsl:otherwise>
				</xsl:choose>
			</table>
		</xsl:if>
	</xsl:template>

	<!--Industry-->
	<xsl:template match ="indus.b">
		<tr>
			<td class="&hp_reportLabel;">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversIndustryKey;', '&hooversIndustry;')"/>
			</td>
			<td class="&hp_reportLabelDetails;">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match ="parent.b">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match ="par.indus.name">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match ="child.b">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match ="ch.indus.name">
		<div class="&hp_reportTextIndentSmall;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match ="child.b/child.b/ch.indus.name">
		<div class="&hp_reportTextIndentMedium;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--SIC Code-->
	<xsl:template name ="SICCodes">
		<xsl:if test ="/Document/n-docbody/r/indus.info.b/sic.b">
			<tr>
				<td class="&hp_reportLabel;">
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversSICCodesKey;', '&hooversSICCodes;')"/>
				</td>
				<td class="&hp_reportDetails;">
					<xsl:for-each select ="/Document/n-docbody/r/indus.info.b/sic.b/sic.cd">
						<xsl:apply-templates/>
						<br/>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--NAICS Codes-->
	<xsl:template name ="NAICSCodes">
		<xsl:if test ="/Document/n-docbody/r/indus.info.b/naics.b">
			<tr>
				<td class="&hp_reportLabel;">
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&hooversNAICSCodesKey;', '&hooversNAICSCodes;')"/>
				</td>
				<td class="&hp_reportDetails;">
					<xsl:for-each select ="/Document/n-docbody/r/indus.info.b/naics.b/naics.cd">
						<xsl:apply-templates/>
						<br/>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
