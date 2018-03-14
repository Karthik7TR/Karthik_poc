<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Process current SSN value to determine how to format it -->
	<xsl:template name="SSNProcess">
		<xsl:param name="ssnvalue" select="normalize-space(.)"/>
		<xsl:param name="isPrivacyProtected" select="false()"/>
		<xsl:param name="searchableLink" select="'false'"/>
		<xsl:choose>
			<!--When in privacy encryption and length not equal to 9 -->
			<xsl:when test="parent::ssn.b and string-length($ssnvalue) != 9">
				<xsl:call-template name="incompleteSSN">
					<xsl:with-param name="incompletessn" select="$ssnvalue"/>
					<xsl:with-param name="lengthremaining" select="9 - string-length($ssnvalue)"/>
					<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
				</xsl:call-template>
			</xsl:when>
			<!--Current un-encrypted data -->
			<xsl:when test="contains($ssnvalue, '-')">
				<xsl:call-template name="FormatSSN">
					<xsl:with-param name="nodevalue" select="$ssnvalue"/>
					<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
					<xsl:with-param name="searchableLink" select="$searchableLink"/>
				</xsl:call-template>
			</xsl:when>
			<!--Encrypted data and needs hashes to be inserted -->
			<xsl:otherwise>
				<xsl:call-template name="FormatSSN">
					<xsl:with-param name="nodevalue">
						<xsl:value-of select="concat(substring($ssnvalue, 1, 3), '-', substring($ssnvalue, 4, 2), '-', substring($ssnvalue, 6, 4))"/>
					</xsl:with-param>
					<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
					<xsl:with-param name="searchableLink" select="$searchableLink"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Generic template to format SSN for privacy encryption project -->
	<xsl:template name="FormatSSN">
		<xsl:param name="nodevalue" select="normalize-space(.)"/>
		<xsl:param name="incomplete" select="false()"/>
		<xsl:param name="isPrivacyProtected" select="false()"/>
        <xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>

		<xsl:variable name="linkContents">
			<xsl:choose>
				<!-- When incomplete SSN, then no hyphens should be there -->
				<xsl:when test="string($incomplete)='true'">
					<!-- Removes all the X's -->
					<xsl:variable name="incompleteSsn" select="translate($nodevalue,'X','')" />
					<xsl:variable name="incompleteSsnLength" select="string-length($incompleteSsn)" />

					
						<!-- Displays everything for government user -->
							<xsl:value-of select="$nodevalue"/>
					<xsl:text> Invalid SSN contains less than 9 digits.</xsl:text>
				</xsl:when>
				<!--Complete but without term highlighting -->
				<xsl:otherwise>
					<xsl:value-of select="$nodevalue"/>
				</xsl:otherwise>
				<!-- TODO:SE FIX THIS FOR SEARCH TERM IN DOCUMENTS-->
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=SSN', concat('ssn=', $linkContents), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--Display partial ssn only if full ssn is not there -->
	<xsl:template match="ssn.frag">
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
								<xsl:value-of select="concat('XXX-XX-',normalize-space(.))"/>
							</xsl:with-param>
							<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
							<xsl:with-param name="searchableLink" select="$searchableLink"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--If SSN is incomplete, then pad with appropriate number of Xs before formatting -->
	<xsl:template name="incompleteSSN">
		<xsl:param name="incompletessn"/>
		<xsl:param name="lengthremaining"/>
		<xsl:param name="isPrivacyProtected" select="false()"/>
        <xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
		
		<xsl:variable name="modifiedssn">
			<xsl:if test="$lengthremaining &gt; 0">
				<xsl:value-of select="concat($incompletessn, 'X')"/>
			</xsl:if>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$lengthremaining &gt; 0">
				<xsl:call-template name="incompleteSSN">
					<xsl:with-param name="incompletessn" select="$modifiedssn"/>
					<xsl:with-param name="lengthremaining" select="$lengthremaining - 1"/>
					<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatSSN">
					<xsl:with-param name="nodevalue" select="$incompletessn"/>
					<xsl:with-param name="incomplete" select="true()"/>
					<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
					<xsl:with-param name="searchableLink" select="$searchableLink"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Template to highlight the ssn and date values -->
	<xsl:template name="highlight">
		<xsl:param name="termposition"/>
		<xsl:param name="termvalue"/>
	</xsl:template>
</xsl:stylesheet>
