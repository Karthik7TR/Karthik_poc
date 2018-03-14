<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<!-- Standard copyright element inherited from WLN.
	endOfDocument text may be suppressed by setting endOfDocument to false -->
	<xsl:template name="DPACopyright">
		<xsl:param name="endOfDocument" select="true()" />
		<xsl:param name="endOfDocumentCopyrightText" />
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" />
		<xsl:param name="currentYear" />
		
		<xsl:if test="$endOfDocument = true()">
			<td>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&endOfDocumentTextKey;', '&endOfDocumentText;')" />
			</td>
		</xsl:if>
		<td>
			<xsl:attribute name="class">&endOfDocumentCopyrightClass;</xsl:attribute>
			<xsl:if test="not($endOfDocument)">
				<!-- TD containing end of document removed -> keep TD integrity by setting colspan -->
				<xsl:attribute name="colspan">2</xsl:attribute>
				<xsl:attribute name="class">&endOfDocumentCopyrightClass; &textAlignLeftClass;</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$endOfDocumentCopyrightTextVerbatim">
					<xsl:copy-of select="$endOfDocumentCopyrightText"/>
				</xsl:when>
				<xsl:otherwise>
					&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>
	
	<!-- DPA EndOfDocumnet override - removes EndOfDocument text for non-public records documents -->
	<xsl:template name="EndOfDocument">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" select="false()"/>
		<xsl:choose>
			<xsl:when test="$PreviewMode = 'True'">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode = 'True' ">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table>
					<xsl:choose>
						<!-- Cannot use id for public records documents because we render and print multiple documents -->
						<xsl:when test="$IsPublicRecords = true()">
							<xsl:attribute name="class">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="id">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<tr>
						<!-- End of document text is removed for non-public records -->
						<xsl:call-template name="DPACopyright">
							<xsl:with-param name="endOfDocument" select="$IsPublicRecords = true()" />
							<xsl:with-param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText" />
							<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="$endOfDocumentCopyrightTextVerbatim" />
							<xsl:with-param name="currentYear" select="$currentYear" />
						</xsl:call-template>
					</tr>
					
					<!-- Testing code inherited from WLN -->
					<!-- #region Business Story 808223.This code will be removed as soon as LEO team finish the testing of the new content for California Code of Regulations on CI and DEMO environments.
					// !ATTENTION! No code except the one related to Business Story 808223 should be based on the logic below. It is implemented solely for the purpose of testing of the new content.-->
					<xsl:if test="DocumentExtension:ShouldDisplayEffectiveDates(//md.doctype.name)">
						<xsl:variable name="startEffectiveDate">
							<xsl:value-of select="//md.starteffective" />
						</xsl:variable>
						<xsl:variable name="endEffectiveDate">
							<xsl:value-of select="//md.endeffective" />
						</xsl:variable>
						<xsl:if test="$startEffectiveDate">
							<tr>
								<td>
									<strong>Start Effective Date:</strong>
								</td>
								<td>
									<xsl:value-of select="DocumentExtension:ReformatDate($startEffectiveDate, 'd', '', 'yyyyMMddHHmmss')"/>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$endEffectiveDate">
							<tr>
								<td>
									<strong>End Effective Date:</strong>
								</td>
								<td>
									<xsl:value-of select="DocumentExtension:ReformatDate($endEffectiveDate, 'd', '', 'yyyyMMddHHmmss')"/>
								</td>
							</tr>
						</xsl:if>
					</xsl:if>
					<!-- #endregion-->
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="CreatePDFLink">
		<xsl:param name="content" />
		<xsl:param name="guid" />
		<xsl:param name="targetType" select="'&inlineTargetType;'" />
		<xsl:param name="mimeType" select="'&pdfMimeType;'" />
		<xsl:param name="originationContext" select="'&docDisplayOriginationContext;'" />
		<xsl:param name="iconClass" select="'&platformPdfIconClass;'" />
		<xsl:param name="iconText" select="'&pdfAltText;'" />

		<xsl:if test="string-length($guid) &gt; 0">
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="createBlobLink">
						<xsl:with-param name="guid" select="$guid" />
						<xsl:with-param name="targetType" select="$targetType" />
						<xsl:with-param name="mimeType" select="$mimeType" />
						<xsl:with-param name="originationContext" select="$originationContext" />
					</xsl:call-template>
				</xsl:attribute>
				<xsl:copy-of select="$content" />
				<i>
					<xsl:attribute name="class">
						<xsl:value-of select="$iconClass" />
					</xsl:attribute>
					<xsl:copy-of select="$iconText" />
				</i>
			</a>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
