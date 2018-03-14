<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
  
	<xsl:template name="renderDocumentTopBlock">
		<xsl:call-template name="renderDocumentTopBlockWithoutDisclaimer"/>
	</xsl:template>
		
  <xsl:template name="RenderDocketImage">
		<xsl:param name="imageBlock" />
		<xsl:param name="index" />
		<xsl:apply-templates select="$imageBlock" />
		<xsl:variable name="batchPdfHref">
			<xsl:call-template name="GatewayBatchPdfHref">
				<xsl:with-param name="documentGuid" select="$Guid" />
				<xsl:with-param name="pdfIndex" select="$index" />
        <xsl:with-param name="linkParams" select="$DocketGatewayLinkParams" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="string-length($batchPdfHref) &gt; 0">
			<a href="{$batchPdfHref}" class="&docketProceedingsButtonClass;">
				<xsl:text>&docketBatchDownloadText;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>
  
  <xsl:template name="RenderSendRunnerLink">
		<xsl:param name="index" />
		<xsl:variable name="sendRunnerHref">
			<xsl:call-template name="GatewaySendRunnerHref">
				<xsl:with-param name="documentGuid" select="$Guid" />
				<xsl:with-param name="orderedIndex" select="$index" />
        <xsl:with-param name="linkParams" select="$DocketGatewayLinkParams" />
			</xsl:call-template>
		</xsl:variable>
    
		<xsl:if test="string-length($sendRunnerHref) &gt; 0">
			<a id="&docketSendRunnerLinkId;{$index}" class="&docketProceedingsButtonClass; &docketSendRunnerLinkClass;" href="{$sendRunnerHref}">
				<i></i>
				<xsl:text>&docketSendRunnerText;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>
  
  <xsl:template match="image.gateway.link">
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
		<xsl:param name="localImageGuid"/>
		<xsl:variable name="court">
			<xsl:call-template name="getCourtNumber">
				<xsl:with-param name="courtNumber" select="@court" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="casenumber" select="@casenumber" />
		<xsl:variable name="id" select="@image.ID" />
		<xsl:variable name="platform" select="@platform" />
		<xsl:variable name="mimeType">
			<xsl:value-of select="'&pdfMimeType;'" />
		</xsl:variable>

		<xsl:variable name="hasAttachments">
			<xsl:choose>
				<xsl:when test="ancestor::number.block">
					<xsl:variable name="docketEntry" select="ancestor::docket.entry" />
					<xsl:choose>
						<xsl:when test="$docketEntry/descendant::docket.description/descendant::image.gateway.link[@item.type='ATTACHMENT']">
							<xsl:value-of select="true()"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="false()"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="fromPDFBatchDownload">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="createDocketGatewayBlobLink">
			<xsl:with-param name="court" select="$court"/>
			<xsl:with-param name="courtNumber" select="$JurisdictionNumber"/>
			<xsl:with-param name="casenumber" select="$casenumber"/>
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="platform" select="$platform"/>
			<xsl:with-param name="mimeType" select="$mimeType"/>
			<xsl:with-param name="contents" select="$text"/>
			<xsl:with-param name="className" select="$className" />
			<xsl:with-param name="displayIcon" select="$displayIcon"/>
			<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
			<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
			<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			<xsl:with-param name="localImageGuid" select="$localImageGuid"/>
			<xsl:with-param name="hasAttachments" select="$hasAttachments"/>
			<xsl:with-param name="fromPDFBatchDownload" select="$fromPDFBatchDownload" />

			<xsl:with-param name="filename">
				<xsl:call-template name="createPdfFilename">
					<xsl:with-param name="cite" select="$Cite"/>
					<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
					<xsl:with-param name="date" select="ancestor::docket.entry/date"/>
					<xsl:with-param name="number" select="ancestor::docket.entry/number.block/number"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
  
  <xsl:template name="createDocketGatewayBlobLink">
		<xsl:param name="contents"/>
		<xsl:param name="court"/>
		<xsl:param name="courtNumber" />
		<xsl:param name="casenumber"/>
		<xsl:param name="id"/>
		<xsl:param name="platform"/>
		<xsl:param name="mimeType" />
		<xsl:param name="className" />
		<xsl:param name="displayIcon" />
		<xsl:param name="displayIconClassName" />
		<xsl:param name="displayToolTip"/>
		<xsl:param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
		<xsl:param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		<xsl:param name="filename" />
		<xsl:param name="localImageGuid" />
		<xsl:param name="hasAttachments" />
		<xsl:param name="fromPDFBatchDownload"/>
    <xsl:variable name="courtNorm">
      <xsl:value-of select="/Document/n-docbody/r/c/court.norm | /Document/n-docbody/r/case.information.block/court.block/court.norm"/>
		</xsl:variable>
		<xsl:variable name="blobHref">
			<xsl:choose>
				<xsl:when test="string-length($localImageGuid) &gt; 0 and string-length($contents) &gt; 0">
					<xsl:call-template name="createDocketBlobLinkWithCourtNorm">
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
						<xsl:with-param name="casenumber" select="$casenumber"/>
						<xsl:with-param name="courtNorm" select="$courtNorm"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="filename" select="$filename"/>
						<xsl:with-param name="platform" select="$platform"/>
						<xsl:with-param name="mimeType" select="$mimeType"/>
						<xsl:with-param name="originationContext" select="$originationContext"/>
						<xsl:with-param name="guid" select="$localImageGuid"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createGatewayBlobHrefWithCourtNorm">
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
						<xsl:with-param name="courtNorm" select="$courtNorm"/>
						<xsl:with-param name="casenumber" select="$casenumber"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="filename" select="$filename"/>
						<xsl:with-param name="platform" select="$platform"/>
						<xsl:with-param name="mimeType" select="$mimeType"/>
						<xsl:with-param name="originationContext" select="$originationContext"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="imageLinkUrl">
			<xsl:call-template name="GetOnclickForImageLink">
				<xsl:with-param name="blobHref" select="$blobHref"/>
				<xsl:with-param name="hasAttachments" select="$hasAttachments"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="string-length($contents) &gt; 0">
			<a>
				<xsl:choose>
					<xsl:when test="$DeliveryMode">
						<xsl:attribute name="href">
							<xsl:value-of select="$blobHref"/>
							<xsl:if test="string-length($blobHref) &gt; 0">
								<xsl:text>&amp;attachments=</xsl:text>
								<xsl:value-of select="$hasAttachments"/>
								<xsl:if test="$fromPDFBatchDownload">
									<xsl:text>&amp;isFromBatchDownload=</xsl:text>
									<xsl:value-of select="$fromPDFBatchDownload"/>
								</xsl:if>
							</xsl:if>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="href">
							<xsl:text>javascript:void();</xsl:text>
						</xsl:attribute>
						<xsl:if test="string-length($imageLinkUrl) &gt; 0">
							<xsl:attribute name="data-pdf-link">
								<xsl:value-of select="$imageLinkUrl"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:attribute name="class">
					<xsl:text>&blobLinkClass;</xsl:text>
					<xsl:if test="$className">
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:value-of select="$className"/>
					</xsl:if>
					<xsl:if test="string-length($imageLinkUrl) &gt; 0">
						<xsl:text> &showMultiPartPdfClickClass;</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<xsl:if test="string-length($mimeType) &gt; 0">
					<xsl:attribute name="type">
						<xsl:value-of select="$mimeType"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="string-length($displayIcon) &gt; 0">
					<img>
						<xsl:attribute name="src">
							<xsl:value-of select="$Images"/>
							<xsl:value-of select="$displayIcon"/>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:value-of select="$displayIconClassName"/>
						</xsl:attribute>
						<xsl:attribute name="alt">
							<xsl:value-of select="$displayIconAltText"/>
						</xsl:attribute>
					</img>
					<!-- HACK to make string-length evaluate to greater than 0 -->
					<xsl:text>&#x200B;</xsl:text>
				</xsl:if>
				<xsl:if test="string-length($displayToolTip) &gt; 0">
					<xsl:attribute name="title">
						<xsl:value-of select="$displayToolTip"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:copy-of select="$contents"/>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="GetOnclickForImageLink">
		<xsl:param name="blobHref"/>
		<xsl:param name="hasAttachments"/>
		<xsl:value-of select="$blobHref"/>
		<xsl:if test="string-length($blobHref) &gt; 0">
			<xsl:text>&amp;attachments=</xsl:text>
			<xsl:value-of select="$hasAttachments"/>
		</xsl:if>
	</xsl:template>
  
</xsl:stylesheet>
