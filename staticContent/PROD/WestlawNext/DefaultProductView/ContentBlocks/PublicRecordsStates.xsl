<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="GetFullStateName">
		<xsl:param name="state" select="."/>
		<xsl:choose>
			<xsl:when test="string-length(normalize-space($state))!=2">
				<xsl:value-of select="normalize-space($state)"/>
			</xsl:when>
			<xsl:when test="normalize-space($state)='AL'">
				<xsl:text>ALABAMA</xsl:text>
			</xsl:when>
			<xsl:when test="normalize-space($state)='AK'">
				<xsl:text>ALASKA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='AZ')">
				<xsl:text>ARIZONA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='AR')">
				<xsl:text>ARKANSAS</xsl:text>
			</xsl:when>
			<xsl:when test="normalize-space($state)='CA'">
				<xsl:text>CALIFORNIA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='CO')">
				<xsl:text>COLORADO</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='CT')">
				<xsl:text>CONNECTICUT</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='DC')">
				<xsl:text>WASHINGTON D.C.</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='FL')">
				<xsl:text>FLORIDA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='GA')">
				<xsl:text>GEORGIA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='HI')">
				<xsl:text>HAWAII</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='ID')">
				<xsl:text>IDAHO</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='IL')">
				<xsl:text>ILLINOIS</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='IN')">
				<xsl:text>INDIANA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='IA')">
				<xsl:text>IOWA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='KS')">
				<xsl:text>KANSAS</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='KY')">
				<xsl:text>KENTUCKY</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='LA')">
				<xsl:text>LOUISIANA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='ME')">
				<xsl:text>MAINE</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MD')">
				<xsl:text>MARYLAND</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MA')">
				<xsl:text>MASSACHUSETTS</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MI')">
				<xsl:text>MICHIGAN</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MN')">
				<xsl:text>MINNESOTA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MS')">
				<xsl:text>MISSISSIPPI</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MO')">
				<xsl:text>MISSOURI</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='MT')">
				<xsl:text>MONTANA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NE')">
				<xsl:text>NEBRASKA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NV')">
				<xsl:text>NEVADA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NH')">
				<xsl:text>NEW HAMPSHIRE</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NJ')">
				<xsl:text>NEW JERSEY</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NM')">
				<xsl:text>NEW MEXICO</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NY')">
				<xsl:text>NEW YORK</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='NC')">
				<xsl:text>NORTH CAROLINA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='ND')">
				<xsl:text>NORTH DAKOTA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='OH')">
				<xsl:text>OHIO</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='OK')">
				<xsl:text>OKLAHOMA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='OR')">
				<xsl:text>OREGON</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='PA')">
				<xsl:text>PENNSYLVANIA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='PR')">
				<xsl:text>PUERTO RICO</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='RI')">
				<xsl:text>RHODE ISLAND</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='SC')">
				<xsl:text>SOUTH CAROLINA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='SD')">
				<xsl:text>SOUTH DAKOTA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='TN')">
				<xsl:text>TENNESSEE</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='TX')">
				<xsl:text>TEXAS</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='UT')">
				<xsl:text>UTAH</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='VT')">
				<xsl:text>VERMONT</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='VA')">
				<xsl:text>VIRGINIA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='VI')">
				<xsl:text>VIRGIN ISLANDS</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='WA')">
				<xsl:text>WASHINGTON</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='WV')">
				<xsl:text>WEST VIRGINIA</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='WI')">
				<xsl:text>WISCONSIN</xsl:text>
			</xsl:when>
			<xsl:when test="(normalize-space($state)='WY')">
				<xsl:text>WYOMING</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="normalize-space($state)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
