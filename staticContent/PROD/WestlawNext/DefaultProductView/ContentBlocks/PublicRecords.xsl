<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="CoverageInformation.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>
	<xsl:include href="PublicRecordsHelpers.xsl"/>
	<xsl:include href="PublicRecordsStates.xsl"/>

	<!-- PublicRecordsFullText - If this is true, we show entire PRS document otherwise we show the Summary. -->
	<xsl:param name="PublicRecordsFullText" select ="true()" />

	<!-- ShowSensitivePublicRecordsData - If this is true, we show entire PRS document OTHERWISE we mask SSN and DOB. -->
	<xsl:param name="ShowSensitivePublicRecordsData" select ="false()" />

	<!-- PRYear - This is the current year. -->
	<xsl:param name="PRYear" select="0000"/>
	
	<!-- PermissibleUse -->
	<xsl:param name="PermissibleUse" select ="badPU" />

	<!-- FCRA Legal Disclaimer -->
	<xsl:param name="FCRADisclaimerText" />

    <!-- Do not render these nodes for ANY PublicRecords template. -->
    <xsl:template match="ShowSearchLink"/>

    <xsl:template name="wrapPublicRecordsSection">
		<xsl:param name="class"/>
		<xsl:param name="contents"/>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsContent">
		<xsl:param name="container"/>
		<xsl:param name="dualColumn" select="true()"/>
		<div id="&documentId;" class="&documentClass;">
			<div>
				<xsl:attribute name="class">
					<xsl:value-of select="$container"/>
				</xsl:attribute>

				<xsl:call-template name="PublicRecordsHeader"/>
				<xsl:variable name="class">
					<xsl:choose>
						<xsl:when test="$dualColumn=false()">
							<xsl:text>&pr_singleColumn;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_dualColumn;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<table>
					<xsl:attribute name="class">
						<xsl:value-of select="$class"/>
					</xsl:attribute>
					<tr>
						<xsl:choose>
							<xsl:when test="$dualColumn=true()">
								<xsl:choose>
									<xsl:when test="$PublicRecordsFullText=true()">
										<td class="&pr_leftSection;">
											<xsl:call-template name="PublicRecordsLeftColumn"/>
										</td>
										<td class="&pr_rightSection;">
											<xsl:call-template name="PublicRecordsRightColumn"/>
										</td>
									</xsl:when>
									<xsl:otherwise>
										<td class="&pr_leftSection;">
											<xsl:call-template name="PublicRecordsLeftColumn"/>
										</td>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<td class="&pr_singleColumn;">
									<xsl:call-template name="PublicRecordsMainColumn"/>
								</td>
							</xsl:otherwise>
						</xsl:choose>
					</tr>
				</table>
				<xsl:call-template name="PublicRecordsEpilog"/>
				<xsl:if test="$FCRADisclaimerText != ''">
					<xsl:call-template name="PublicRecordsFCRADisclaimer"/>
				</xsl:if>
				<xsl:call-template name="EndOfDocument" />
			</div>
		</div>
	</xsl:template>

	<xsl:template name="outputOrderDocumentsSection">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_orderDocuments;'" />
		</xsl:call-template>

		<table class="&pr_table;">
			<xsl:call-template name="FormatOrderDocs"/>
		</table>
	</xsl:template>

	<xsl:template name="FormatOrderDocs">
		<tr>
			<td>
				<xsl:text>Call Westlaw CourtExpress at 1-877-DOC-RETR (1-877-362-7387) for on-site manual retrieval of documents related to this or other matters.	Additional charges apply.</xsl:text>
			</td>
		</tr>
	</xsl:template>

	
	<!-- Callable template for minor parts of content blocks -->
	<xsl:template name="wrapWithPublicRecordsSectionMultiContent">
		<xsl:param name="class"/>
		<xsl:param name="contents"/>
		<xsl:param name="contents2"/>

		<div>
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>

					<xsl:if test="string-length($contents2) &gt; 0">
						<xsl:copy-of select="$contents2"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	
	<xsl:template name="wrapPublicRecordsDisclaimers">
		<xsl:param name="displayWarning" select="false()"/>
		<xsl:param name="disclaimer1"/>
		<xsl:param name="disclaimer2"/>
		<xsl:param name="disclaimer3"/>
		<xsl:param name="disclaimer4"/>
		<xsl:if test="normalize-space($disclaimer1) or normalize-space($disclaimer2) or normalize-space($disclaimer3) or normalize-space($disclaimer4)">
			<table class="&pr_table;">
				<xsl:if test="normalize-space($disclaimer1)">
					<tr class="&pr_item; &pr_paddingTop;">
						<td>
							<xsl:if test="string($displayWarning)='true'">
								<div>WARNING:</div>
							</xsl:if>
							<xsl:value-of select="normalize-space($disclaimer1)"/>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="normalize-space($disclaimer2)">
					<tr class="&pr_item; &pr_paddingTop;">
						<td>
							<xsl:value-of select="normalize-space($disclaimer2)"/>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="normalize-space($disclaimer3)">
					<tr class="&pr_item; &pr_paddingTop;">
						<td>
							<xsl:value-of select="normalize-space($disclaimer3)"/>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="normalize-space($disclaimer4)">
					<tr class="&pr_item; &pr_paddingTop;">
						<td>
							<xsl:value-of select="normalize-space($disclaimer4)"/>
						</td>
					</tr>
				</xsl:if>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- NOTE: All XSLT files including this template need to define a template named "PublicRecordsHeader". -->

	<!-- NOTE: The templates PublicRecordsMainColumn, PublicRecordsLeftColumn, and PublicRecordsRightColumn need
	     to be implemented by the individual content type XSLTs, as necessary.  They are defined here since content
			 types either use PublicRecordsMainColumn (for single column) or both PublicRecordsLeftColumn and
			 PublicRecordsRightColumn (for dual column).  Thus, these need to be defined as empty templates so that the
			 individual content types can implement the desired behavior and not have undefined template issues. -->
	<xsl:template name="PublicRecordsMainColumn">
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
	</xsl:template>

	<xsl:template name="PublicRecordsEpilog">
	</xsl:template>

  <xsl:template name="PublicRecordsFCRADisclaimer">
    <br/>
    <table class="&pr_table;">
      <tr class="&pr_item; &pr_paddingTop;">
        <td class="&pr_fcraDisclaimer;">
          <xsl:value-of select="$FCRADisclaimerText"/>
        </td>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>
