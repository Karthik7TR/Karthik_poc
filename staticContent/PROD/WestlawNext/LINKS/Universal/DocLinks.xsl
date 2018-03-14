<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.LINKS.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forceDefaultProduct="true"/>
	<xsl:include href="BlobLink.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--
		NPD's requirements for DLA Piper is to allow Update, View, and Batch Download button are accessible within Links.
	-->
	
	<!-- The two templates below are for setting the View button's host to be Links. -->
	<xsl:template name="createDocketBlobLink">
		<xsl:param name="court" />
		<xsl:param name="courtNumber" />
		<xsl:param name="casenumber" />
		<xsl:param name="id" />
		<xsl:param name="filename" />
		<xsl:param name="platform" />
		<xsl:param name="mimeType" />
		<xsl:param name="originationContext" />
		<xsl:param name="guid" />
		<xsl:variable name="extension">
			<xsl:choose>
				<xsl:when test="$mimeType = 'application/pdf'">
					<xsl:text>pdf</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="courtNorm">
			<xsl:call-template name="getDocketCourtNorm" />
		</xsl:variable>
		<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('courtNorm=', $courtNorm), concat('id=', translate(substring-before($id, ';'), '|', ',')), concat('filename=', $filename), concat('courtnumber=', $courtNumber), concat('casenumber=', $casenumber), concat('originationContext=', $originationContext), concat('extension=', $extension), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&docketLocalImageGuid;=', $guid))"/>
	</xsl:template>

	<xsl:template name="createGatewayBlobHref">
		<xsl:param name="court" />
		<xsl:param name="courtNumber" />
		<xsl:param name="casenumber" />
		<xsl:param name="id" />
		<xsl:param name="filename" />
		<xsl:param name="platform" />
		<xsl:param name="mimeType" />
		<xsl:param name="originationContext" />
		<xsl:variable name="extension">
			<xsl:choose>
				<xsl:when test="$mimeType = 'application/pdf'">
					<xsl:text>pdf</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'image/jpeg'">
					<xsl:text>jpg</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'image/gif'">
					<xsl:text>gif</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/x-wgsl'">
					<xsl:text>amz</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="courtNorm">
			<xsl:call-template name="getDocketCourtNorm" />
		</xsl:variable>		
		<!-- need to translate pipes to commas in the id -->
		<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('courtNorm=', $courtNorm), concat('id=', translate(substring-before($id, ';'), '|', ',')), concat('filename=', $filename), concat('courtnumber=', $courtNumber), concat('casenumber=', $casenumber), concat('extension=', $extension), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', 'contextData=(sc.Default)', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
	</xsl:template>


	<!-- This part below is for allowing normal images to be displayed within Links. -->
	<xsl:template name="CreateBlobLinkUrl">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="originationContext" />
		<xsl:param name="prettyName" />
		<xsl:param name="hash" />
		<xsl:param name="docGuid" />
		<xsl:param name="extension" />

		<xsl:choose>
			<xsl:when test="$extension = 'jpg' or $extension = 'gif' or $extension = 'png'">
				<xsl:call-template name="CreateBlobLinkUrlLINKS">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="highResolution" select="$highResolution"/>
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="maxHeight" select="$maxHeight"/>
					<xsl:with-param name="originationContext" select="$originationContext"/>
					<xsl:with-param name="prettyName" select="$prettyName"/>
					<xsl:with-param name="hash" select="$hash"/>
					<xsl:with-param name="docGuid" select="$docGuid"/>
					<xsl:with-param name="extension" select="$extension"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="CreateBlobLinkUrlPlatform">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="highResolution" select="$highResolution"/>
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="maxHeight" select="$maxHeight"/>
					<xsl:with-param name="originationContext" select="$originationContext"/>
					<xsl:with-param name="prettyName" select="$prettyName"/>
					<xsl:with-param name="hash" select="$hash"/>
					<xsl:with-param name="docGuid" select="$docGuid"/>
					<xsl:with-param name="extension" select="$extension"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- The two templates below are for setting the Batch Download button's host to be Links. -->
	<xsl:template name="BatchPdfHref">
		<xsl:param name="documentGuid" />
		<xsl:param name="pdfIndex" />
		<xsl:param name="checkSum" />
		<xsl:param name="docPersistId" />
		<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrl('Page.DocketsPdfBatchDownload', concat('documentGuid=', $documentGuid), concat('pdfIndex=', $pdfIndex), concat('checkSum=', $checkSum), concat('docPersistId=', $docPersistId), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;',$specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<xsl:template name="BatchPdfHrefWithCaseNumber">
		<xsl:param name="pdfIndex" />
		<xsl:param name="court" />
		<xsl:param name="caseNumber" />
		<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrl('Page.DocketsPdfBatchDownload', concat('pdfIndex=', $pdfIndex), concat('court=', $court), concat('caseNumber=', $caseNumber), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
	
	<xsl:template name="renderNotesOfDecisionsLink">
		<xsl:param name="count"/>
		<xsl:param name="docGuid"/>
		<xsl:param name="originationContext" />
		<xsl:param name="text"/>
		<xsl:variable name="renderNotesOfDecisionsUrl" select="UrlBuilder:CreatePersistentLINKSUrlIgnoreBlock('Page.NotesofDecisionsByLookup', concat('docGuid=', $docGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeNotesOfDecision;' )" />
		<a class="&notesOfDecisionsLink;">
			<xsl:if test="string-length($renderNotesOfDecisionsUrl) &gt; 0">			
				<xsl:attribute name="href">
					<xsl:value-of select="$renderNotesOfDecisionsUrl"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$text and string-length($text) &gt; 0">
					<xsl:value-of select="concat($text, ' (', $count, ')')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&notesOfDecisionsViewAll;', $count)"/>
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>

</xsl:stylesheet>
