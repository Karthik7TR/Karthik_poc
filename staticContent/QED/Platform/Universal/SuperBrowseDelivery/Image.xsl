<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="DocLinks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:variable name="documentType" select="/Document/document-data/doc-type"/>

	<!-- This element seems to only be in GAO -->
	<xsl:template match="document.image.link">
		<xsl:variable name="guid" select="@tuuid" />

		<xsl:variable name="imageMetaData" select="/documents/*/ImageMetadata/n-metadata[@guid=$guid]" />

		<div class="&imageBlockClass;">
			<xsl:choose>
				<xsl:when test="$imageMetaData/md.image.renderType = 'BlobLink'">
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="@tuuid"/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="$imageMetaData/metadata.block/md.image.block/md.image.format"/>
						<xsl:with-param name="contents">
							<xsl:apply-templates />
						</xsl:with-param>
						<!-- These are generic, might be better to create mimeType specific icons/classes/alt text -->
						<xsl:with-param name="displayIcon" select="'&documentIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&blobIconClass;'"/>
						<xsl:with-param name="displayIconAltText" >
							<xsl:apply-templates />
						</xsl:with-param>

						<xsl:with-param name="className">
							<xsl:if test=".//N-HIT">
								<xsl:text>&searchTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-WITHIN">
								<xsl:text> &searchWithinTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-LOCATE">
								<xsl:text> &locateTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
								<xsl:text> &searchTermNoHighlightClass;</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$imageMetaData/md.image.renderType = 'XlsLink'">
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="@tuuid"/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&xlsMimeType;'"/>
						<xsl:with-param name="contents">
							<xsl:apply-templates />
							<xsl:text> (Excel)</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&documentIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&blobIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
						<xsl:with-param name="className">
							<xsl:if test=".//N-HIT">
								<xsl:text>&searchTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-WITHIN">
								<xsl:text> &searchWithinTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-LOCATE">
								<xsl:text> &locateTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
								<xsl:text> &searchTermNoHighlightClass;</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$imageMetaData/md.image.renderType = 'PptLink'">
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="@tuuid"/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pptMimeType;'"/>
						<xsl:with-param name="contents">
							<xsl:apply-templates />
							<!--<xsl:text> (PPT)</xsl:text>-->
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&documentIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&blobIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
						<xsl:with-param name="className">
							<xsl:if test=".//N-HIT">
								<xsl:text>&searchTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-WITHIN">
								<xsl:text> &searchWithinTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-LOCATE">
								<xsl:text> &locateTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
								<xsl:text> &searchTermNoHighlightClass;</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="@tuuid"/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
						<xsl:with-param name="contents">
							<xsl:call-template name="getContentText" />
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
						<xsl:with-param name="className">
							<xsl:if test=".//N-HIT">
								<xsl:text>&searchTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-WITHIN">
								<xsl:text> &searchWithinTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test=".//N-LOCATE">
								<xsl:text> &locateTermClass;</xsl:text>
							</xsl:if>
							<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
								<xsl:text> &searchTermNoHighlightClass;</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!-- This element is pervasive in almost every other content type -->
	<xsl:template match="image.block" name="imageBlock">
		<xsl:param name="suppressNoImageDisplay" select="false()"/>
		<xsl:choose>
			<!-- This is only being done to address bad content.  You can see an example in the guid I6626b5b2e6df11dbbe03ac4425687bc0.  Search for "− P" in the xhtml -->
			<xsl:when test="parent::csc">
				<span class="&imageBlockClass;">
					<xsl:apply-templates select="image.link"/>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapAndRenderImageBlock">
					<xsl:with-param name="suppressNoImageDisplay" select="$suppressNoImageDisplay"></xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- 
		factored out so productViews can override <div> wrapper if needed.  iPhone renders this inside a <p>, <div> is NOT
		allowed inside <p>.
	-->
	<xsl:template name="wrapAndRenderImageBlock">
		<xsl:param name="suppressNoImageDisplay" select="false()"/>
		<div>
			<xsl:call-template name="renderImageBlock">
				<xsl:with-param name="suppressNoImageDisplay" select="$suppressNoImageDisplay"></xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- the real guts of the imageBlock -->
	<xsl:template name="renderImageBlock">
		<xsl:param name="suppressNoImageDisplay" select="false()"/>
		<xsl:param name="imageClass" select="''" />
		<xsl:attribute name="class">
			<xsl:text>&imageBlockClass;</xsl:text>
			<xsl:if test="$imageClass and not($imageClass = '')">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:value-of select="$imageClass" />
			</xsl:if>
			<xsl:if test="$documentType = 'Analytical - EForms'">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&centerClass;</xsl:text>
			</xsl:if>
		</xsl:attribute>
		<xsl:choose>
			<xsl:when test="parent::docket.entry.description.block or parent::wcn.complaint.block or parent::complaint.block">
				<xsl:apply-templates select="image.link">
					<xsl:with-param name="text">
						<xsl:text>&originalDocketLinkText; &pdfLabel;</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="parent::number.block">
				<xsl:apply-templates select="image.gateway.link">
					<xsl:with-param name="text">
						<xsl:text>&docketProceedingPdfLinkText;</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&docketProceedingPdfAltText;'"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="parent::jv.research.guide">
				<xsl:apply-templates select="image.link">
					<xsl:with-param name="text">
						<xsl:choose>
							<xsl:when test=".//image.keywords">
								<xsl:value-of select=".//image.keywords"/>
							</xsl:when>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="displayIcon" select="'&documentIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="image.link">
					<xsl:with-param name="suppressNoImageDisplay" select="$suppressNoImageDisplay"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- PDF gateway links generally occuring in the docket.entry/number.block -->
	<xsl:template match="image.gateway.link">
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
		<xsl:param name="localImageGuid"/>
		<xsl:variable name="targetType" select="@ttype"/>
		<xsl:variable name="court">
			<xsl:call-template name="getCourtNumber">
				<xsl:with-param name="courtNumber" select="@court" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="casenumber" select="@casenumber" />
		<xsl:variable name="id" select="@image.ID" />

		<xsl:variable name="countyStart" select="substring-after($id, 'county=')"/>
		<xsl:variable name="county">
		<xsl:value-of select="substring-before($countyStart, ';')"/>
		</xsl:variable>

		<xsl:variable name="platform" select="@platform" />
		<xsl:variable name="mimeType">
			<xsl:value-of select="'&pdfMimeType;'" />
		</xsl:variable>

		<xsl:variable name="hasAttachments">
			<xsl:choose>
				<xsl:when test="ancestor::number.block">
					<xsl:variable name="docketEntry" select="ancestor::docket.entry or ancestor::available.image" />
					<xsl:choose>
						<xsl:when test="$docketEntry/descendant::docket.description/descendant::image.gateway.link[@item.type='ATTACHMENT'] or $docketEntry/descendant::docket.available.image/descendant::image.gateway.link[@item.type='ATTACHMENT']">
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

		<xsl:call-template name="createDocumentGatewayBlobLink">
			<xsl:with-param name="court" select="$court"/>
			<xsl:with-param name="courtNumber" select="$JurisdictionNumber"/>
			<xsl:with-param name="county" select="$county"/>
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

	<!-- PDF gateway links generally occuring in the docket.entry/docket description -->
	<xsl:template match="docket.description/image.gateway.link">
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:variable name="court">
				<xsl:call-template name="getCourtNumber">
					<xsl:with-param name="courtNumber" select="@court" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="casenumber" select="@casenumber" />
			<xsl:variable name="id" select="@image.id" />
			<xsl:variable name="platform" select="@platform" />
			<xsl:variable name="mimeType">
				<xsl:value-of select="'&pdfMimeType;'" />
			</xsl:variable>

			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:call-template name="createDocumentGatewayBlobLink">
				<xsl:with-param name="court" select="$court"/>
				<xsl:with-param name="courtNumber" select="$JurisdictionNumber"/>
				<xsl:with-param name="casenumber" select="$casenumber"/>
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="platform" select="$platform"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="contents" select="text()"/>
				<xsl:with-param name="className" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				<xsl:with-param name="filename">
					<xsl:call-template name="createPdfFilename">
						<xsl:with-param name="cite" select="$Cite"/>
						<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
						<xsl:with-param name="date" select="ancestor::docket.entry/date"/>
						<xsl:with-param name="number" select="concat(ancestor::docket.entry/number.block/number, '-', ./text())"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="hasAttachments" select="false()"/>
			</xsl:call-template>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="createPdfFilename">
		<xsl:param name="baseName" />
		<xsl:param name="cite" />
		<xsl:param name="date" />
		<xsl:param name="number" />
		<xsl:value-of select="translate(concat($cite, '_', $baseName, '_', $date, '_', $number), ' /:', '_--')"/>
	</xsl:template>

	<xsl:template match="image.link">
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
		<xsl:param name="suppressNoImageDisplay" select="false()"/>
		<xsl:variable name="countImage" select="count(preceding::image.link) + 1"/>
		<xsl:variable name="guid">
			<xsl:choose>
				<xsl:when test="string-length(@target) &gt; 30">
					<xsl:value-of select="@target" />
				</xsl:when>
				<xsl:when test="string-length(@tuuid) &gt; 30">
					<xsl:value-of select="@tuuid" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>NotValidImage</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($guid) &gt; 0">
			<xsl:choose>
				<xsl:when test="/documents/*/ImageMetadata/n-metadata[md.blobref.guid = $guid]">
					<xsl:apply-templates select="/documents/*/ImageMetadata/n-metadata[md.blobref.guid = $guid]" mode="MakeImageLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="imageNumInDoc" select="$countImage"/>
						<xsl:with-param name="suppressNoImageDisplay" select="$suppressNoImageDisplay"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="/documents/*/ImageMetadata/n-metadata/@guid = $guid">
					<xsl:apply-templates select="/documents/*/ImageMetadata/n-metadata[@guid = $guid]" mode="MakeImageLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="text" select="$text"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="displayIcon" select="$displayIcon"/>
						<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
						<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
						<xsl:with-param name="imageNumInDoc" select="$countImage"/>
						<xsl:with-param name="suppressNoImageDisplay" select="$suppressNoImageDisplay"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="string($suppressNoImageDisplay)='false'">
						<span class="&imageNonDisplayableClass;">&tableOrGraphicNotDisplayableText;</span>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Suppress this element in general -->
	<xsl:template match="ImageMetadata" />

	<xsl:template match="ImageMetadata/n-metadata" mode="MakeImageLink">
		<xsl:param name="guid" />
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
		<xsl:param name="imageNumInDoc" />
		<xsl:param name="suppressNoImageDisplay" select="false()"/>
		<xsl:variable name="targetType" select="@ttype" />
		<xsl:variable name="imageFormat">
			<xsl:choose>
				<xsl:when test="($documentType='UK - Cases' or $documentType='UK - Secondary Sources' or $documentType='UK - Journals') and metadata.block/md.references/md.print.rendition.id">
					<xsl:variable name="UKImageType" select="metadata.block/md.references/md.print.rendition.id/@ttype"/>
					<xsl:value-of select="concat('application/', $UKImageType)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select=".//md.image.format | .//obj.mimetype"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="bytes" select="number(translate(.//md.image.bytes | .//obj.size,',',''))" />
		<xsl:variable name="dpi" select=".//md.image.dpi" />
		<xsl:variable name="height">
			<xsl:choose>
				<xsl:when test="$DeliveryMode and $bytes &gt; number(100000)">
					<xsl:text>400</xsl:text>
				</xsl:when>
				<xsl:when test=".//md.image.height">
					<xsl:value-of select="number(translate(.//md.image.height,',',''))"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="$DeliveryMode and $bytes &gt; number(100000)">
					<xsl:text>400</xsl:text>
				</xsl:when>
				<xsl:when test=".//md.image.width">
					<xsl:value-of select="number(translate(.//md.image.width,',',''))"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="units" select=".//md.image.units" />
		<xsl:variable name="error" select=".//error" />
		<xsl:variable name="renderType" select="md.image.renderType" />
		<xsl:variable name="highResolution" select="$targetType = 'amaatlas-hr' or $targetType = 'blausenpr-hr' or $targetType = 'blausenst-hr'" />
		<xsl:variable name="medLit" select="$targetType = 'amaatlas' or $targetType = 'blausenpr' or $targetType = 'blausenst'"/>
		<xsl:variable name="mimeType">
			<xsl:choose>
				<xsl:when test="$targetType = 'NRS' or $targetType = 'Briefs' or $targetType = 'TrialDocs'">
					<xsl:value-of select="'&pdfMimeType;'" />
				</xsl:when>
				<xsl:when test="$imageFormat = '&pdfMimeType;' or $imageFormat = '&smartLabelMimeType;' or $imageFormat = '&xPngMimeType;'">
					<xsl:value-of select="$imageFormat" />
				</xsl:when>
				<xsl:when test="$imageFormat = '&pngMimeType;'">
					<xsl:value-of select="'&xPngMimeType;'"/>
				</xsl:when>
				<xsl:when test="$imageFormat = '&gifMimeType;'">
					<xsl:value-of select="'&gifMimeType;'" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&jpgMimeType;'" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="blobHref">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="$guid"/>
				<xsl:with-param name="highResolution" select="$highResolution"/>
				<xsl:with-param name="targetType" select="$targetType"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				<xsl:with-param name="docGuid" select="$Guid"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$renderType = 'BlobError'">
				<xsl:if test="string($suppressNoImageDisplay)='false'">
					<span class="&imageNonDisplayableClass;">&tableOrGraphicNotDisplayableText;</span>
				</xsl:if>
			</xsl:when>

			<xsl:when test="$highResolution and not($DeliveryMode)">
				<a>
					<xsl:if test="string-length($blobHref) &gt; 0">
						<xsl:attribute name="href">
							<xsl:value-of select="$blobHref"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="class">
						<xsl:text>&imageNoWatermarkLinkClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="target">
						<xsl:text>_blank</xsl:text>
					</xsl:attribute>					
					<xsl:text>&imageNoWatermarkLinkText;</xsl:text>
				</a>
				<div class="&imageCaptionClass;">
					<xsl:value-of select="concat('Image ', count(preceding-sibling::*) + 1, ' (')"/>
					<xsl:call-template name="CalcHeightWidthFromDPI">
						<xsl:with-param name="actualSize" select="$width"/>
						<xsl:with-param name="dpi" select="$dpi"/>
					</xsl:call-template>
					<xsl:text> &times; </xsl:text>
					<xsl:call-template name="CalcHeightWidthFromDPI">
						<xsl:with-param name="actualSize" select="$height"/>
						<xsl:with-param name="dpi" select="$dpi"/>
					</xsl:call-template>
					<xsl:text>) &notAvailableForOfflinePrintText;</xsl:text>
				</div>
			</xsl:when>

			<xsl:when test="($highResolution or $medLit) and $DeliveryMode">
				<xsl:call-template name="buildBlobImageElement">
					<xsl:with-param name="alt" select="concat('&blobImageTextBeforeCount;',$imageNumInDoc,'&blobImageTextAfterCount;',$text)"/>
					<xsl:with-param name="src">
						<xsl:call-template name="createBlobLink">
							<xsl:with-param name="guid" select="$guid"/>
							<xsl:with-param name="highResolution" select="$highResolution"/>
							<xsl:with-param name="targetType" select="$targetType"/>
							<xsl:with-param name="mimeType" select="$mimeType"/>
							<xsl:with-param name="forImgTag" select="'true'" />
							<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="height" select="$height"/>
					<xsl:with-param name="width" select="$width"/>
					<xsl:with-param name="class" select="'&medLitHiResImageClass;'"/>
					<xsl:with-param name="contents" select="$text"/>
					<xsl:with-param name="displayIcon" select="$displayIcon"/>
					<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
					<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
					<xsl:with-param name="compressionLevel">
						<xsl:if test="$highResolution">
							<xsl:value-of select="'0'"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
				
			<xsl:when test="$renderType = 'AmaAtlasSlLink'">
				<a class="&imageLinkClass;" type="{$mimeType}">
					<xsl:if test="string-length($blobHref) &gt; 0">
						<xsl:attribute name="href">
							<xsl:value-of select="$blobHref" />
						</xsl:attribute>
					</xsl:if>
					<img src="{$Images}&downloadSmartLabelsFileImage;" alt="&downloadSmartLabelsText;"/>
					<xsl:text>&#x200B;</xsl:text>
				</a>
			</xsl:when>

			<xsl:when test="$renderType = 'XlsLink' or ($renderType = 'Image' and $mimeType = '&xlsMimeType;')">
				<xsl:variable name="imageText">
					<xsl:choose>
						<xsl:when test="$text">
							<xsl:value-of select="$text"/>
						</xsl:when>
						<xsl:when test="//image.block/image.text[preceding-sibling::image.link[@tuuid = $guid]]">
							<xsl:value-of select="//image.block/image.text[preceding-sibling::image.link[@tuuid = $guid]]"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat('Image ', count(preceding-sibling::*) + 1, ' within document in XLS format.')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="iconToUse">
					<xsl:choose>
						<xsl:when test="string-length($displayIcon) &gt; 0">
							<xsl:value-of select="$displayIcon"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>noXlsIcon.gif</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="className" select="$className" />
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="mimeType" select="$mimeType"/>
					<xsl:with-param name="contents" select="$imageText"/>
					<xsl:with-param name="displayIcon" select="$iconToUse"/>
					<xsl:with-param name="displayIconClassName" />
					<xsl:with-param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="$renderType = 'PDFLink' or ($renderType = 'Image' and $mimeType = '&pdfMimeType;')">
				<xsl:variable name="imageText">
					<xsl:choose>
						<xsl:when test="$text">
							<xsl:value-of select="$text"/>
						</xsl:when>
						<xsl:when test="//image.block/image.text[preceding-sibling::image.link[@tuuid = $guid]]">
							<!--For the 50 State Surveys, use the text that comes with the image as the link name-->
							<xsl:value-of select="//image.block/image.text[preceding-sibling::image.link[@tuuid = $guid]]"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat('Image ', count(preceding-sibling::*) + 1, ' within document in PDF format.')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!-- If we have specified an displayIcon already, we should use that one. Most of the time, 
						 it will be the pdfIconPath anyways, but if not, we specified it for a reason. -->
				<xsl:variable name="iconToUse">
					<xsl:choose>
						<xsl:when test="string-length($displayIcon) &gt; 0">
							<xsl:value-of select="$displayIcon"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pdfIconPath;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:call-template name="renderPdfLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="className" select="$className" />
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="mimeType" select="$mimeType"/>
					<xsl:with-param name="contents" select="$imageText"/>
					<xsl:with-param name="displayIcon" select="$iconToUse"/>
				</xsl:call-template>
			</xsl:when>

			<xsl:otherwise>
				<xsl:variable name="imageHeightAfterScaling">
					<!-- 
						In some instance like trademarks, the image size is given in bytes. There is no width and no height.
						Need to shrink the image for delivery so that it fits in the page. 
						Since there is no image width and height provided, use the media page width and height instead. 
					-->
					<xsl:call-template name="CalculateMaxImageHeightOrWidth">
						<xsl:with-param name="imageWidth">
							<xsl:choose>
								<xsl:when test="string-length($width) = 0">
									<xsl:value-of select="$mediaPageWidth"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$width"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="imageHeight">
							<xsl:choose>
								<xsl:when test="string-length($height) = 0">
									<xsl:value-of select="$mediaPageHeight"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$height"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="dpi" select="$dpi"/>
						<xsl:with-param name="pageWidth" select="$mediaPageWidth"/>
						<xsl:with-param name="pageHeight" select="$mediaPageHeight"/>
						<xsl:with-param name="returnDimension" select="'height'" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="imageWidthAfterScaling">
					<xsl:choose>
						<xsl:when test="$DualColumnMode and $width &lt; number(number($mediaPageWidth div 2) - $dualColumnGutter)">
							<xsl:value-of select="$width"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="CalculateMaxImageHeightOrWidth">
								<xsl:with-param name="imageWidth">
									<xsl:choose>
										<xsl:when test="string-length($width) = 0">
											<xsl:value-of select="$mediaPageWidth"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$width"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="imageHeight">
									<xsl:choose>
										<xsl:when test="string-length($height) = 0">
											<xsl:value-of select="$mediaPageHeight"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$height"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="dpi" select="$dpi"/>
								<xsl:with-param name="pageWidth" select="$mediaPageWidth"/>
								<xsl:with-param name="pageHeight" select="$mediaPageHeight"/>
								<xsl:with-param name="returnDimension" select="'width'" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="titleText">
					<xsl:call-template name="trim">
						<xsl:with-param name="string">
							<xsl:apply-templates select="/Document/document-data/title" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="wasShrunk" select="number($imageHeightAfterScaling) &lt; number($height) or number($imageWidthAfterScaling) &lt; number($width)"/>
				<xsl:variable name="blobHrefWithResizing">
					<xsl:choose>
						<xsl:when test="$wasShrunk">
							<xsl:call-template name="createBlobLink">
								<xsl:with-param name="guid" select="$guid"/>
								<xsl:with-param name="targetType" select="$targetType"/>
								<xsl:with-param name="mimeType" select="$mimeType"/>
								<xsl:with-param name="maxHeight" select="$imageHeightAfterScaling"/>
								<xsl:with-param name="forImgTag" select="'true'" />
								<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="createBlobLink">
								<xsl:with-param name="maxWidth">
									<xsl:choose>
										<xsl:when test="$DeliveryMode and $AllowResizeImageOnDelivery and not(number($DeliveryPageWidth) = NaN)">
											<xsl:value-of select="$DeliveryPageWidth"/>
										</xsl:when>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="maxHeight">
									<xsl:choose>
										<xsl:when test="$DeliveryMode and $AllowResizeImageOnDelivery and not(number($DeliveryPageHeight) = NaN)">
											<xsl:value-of select="$DeliveryPageHeight"/>
										</xsl:when>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="guid" select="$guid"/>
								<xsl:with-param name="highResolution" select="$highResolution"/>
								<xsl:with-param name="targetType" select="$targetType"/>
								<xsl:with-param name="mimeType" select="$mimeType"/>
								<xsl:with-param name="forImgTag" select="'true'" />
								<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:variable name="imageClassName">
					<!-- If there is an image class that requires its own height or width specifications
							then add it as an entry in this list. -->
					<xsl:choose>
						<xsl:when test="$targetType = 'tms-inline-pdf'">
							<!-- We need to set our own max-height and max-width attributes for Trademark Images ex: FEDTM 77276752-->
							<xsl:text>&trademarkScanImageClass; &imageClass;</xsl:text>
						</xsl:when>
						<xsl:when test="$targetType = 'International_Patents'">
							<xsl:value-of select="$className"/>
							<xsl:text> &imageClass;</xsl:text>
						</xsl:when>
						<xsl:when test="$wasShrunk">
							<xsl:text>&imageShrunkClass;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&imageClass;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
			
				<a class="&imageLinkClass;" type="{$mimeType}">
					<xsl:if test="string-length($blobHref) &gt; 0">
						<xsl:attribute name="href">
							<xsl:value-of select="$blobHref"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="buildBlobImageElement">
						<xsl:with-param name="alt" select="concat('&blobImageTextBeforeCount;',$imageNumInDoc,'&blobImageTextAfterCount;',$titleText)"/>
						<xsl:with-param name="src" select="$blobHrefWithResizing"/>
						<xsl:with-param name="height" select="$imageHeightAfterScaling"/>
						<xsl:with-param name="width" select="$imageWidthAfterScaling"/>
						<xsl:with-param name="class" select="$imageClassName"/>
						<xsl:with-param name="contents" select="$text"/>
						<xsl:with-param name="displayIcon" select="$displayIcon"/>
						<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
						<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
					</xsl:call-template>
				</a>

				<xsl:if test="$medLit">
					<div class="&imageCaptionClass;">
						<xsl:value-of select="concat('Image ', count(preceding-sibling::*) + 1, ' (')"/>
						<xsl:call-template name="CalcHeightWidthFromDPI">
							<xsl:with-param name="actualSize" select="$width"/>
							<xsl:with-param name="dpi" select="$dpi"/>
						</xsl:call-template>
						<xsl:text> &times; </xsl:text>
						<xsl:call-template name="CalcHeightWidthFromDPI">
							<xsl:with-param name="actualSize" select="$height"/>
							<xsl:with-param name="dpi" select="$dpi"/>
						</xsl:call-template>
						<xsl:text>) &availableForOfflinePrintText;</xsl:text>
					</div>
				</xsl:if>	
		
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		named template so products can override this.
	-->
	<xsl:template name="renderPdfLink">
		<xsl:param name="guid"/>
		<xsl:param name="className"/>
		<xsl:param name="targetType"/>
		<xsl:param name="mimeType"/>
		<xsl:param name="contents"/>
		<xsl:param name="displayIcon"/>
		<xsl:call-template name="createDocumentBlobLink">
			<xsl:with-param name="guid" select="$guid"/>
			<xsl:with-param name="className" select="$className" />
			<xsl:with-param name="targetType" select="$targetType"/>
			<xsl:with-param name="mimeType" select="$mimeType"/>
			<xsl:with-param name="contents" select="$contents"/>
			<xsl:with-param name="displayIcon" select="$displayIcon"/>
			<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
			<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
		</xsl:call-template>	
	</xsl:template>
	
	<xsl:template name="CalculateMaxImageHeightOrWidth">
		<xsl:param name="imageHeight" />
		<xsl:param name="imageWidth" />
		<xsl:param name="dpi" />
		<xsl:param name="pageWidth" select="$mediaPageWidth"/>
		<xsl:param name="pageHeight" select="$mediaPageHeight"/>
		<xsl:param name="returnDimension" />
		
		<xsl:if test="$returnDimension = 'height' or $returnDimension = 'width'">
			<xsl:if test="not(number($imageHeight) = NaN or number($imageWidth) = NaN or number($dpi) = NaN)">
				<xsl:variable name="dpiScaleFactor" select="96 div number($dpi)"/>
				<xsl:variable name="imageHeightAfterDpiScaling">
					<xsl:choose>
						<xsl:when test="number($dpi) &gt; 96 and (number($imageWidth) &gt; number($pageWidth) or number($imageHeight) &gt; number($pageHeight))">
							<xsl:value-of select="round(number($imageHeight) * number($dpiScaleFactor))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="number($imageHeight)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="imageWidthAfterDpiScaling">
					<xsl:choose>
						<xsl:when test="number($dpi) &gt; 96 and (number($imageWidth) &gt; number($pageWidth) or number($imageHeight) &gt; number($pageHeight))">
							<xsl:value-of select="round(number($imageWidth) * number($dpiScaleFactor))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="number($imageWidth)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="widthBasedScaleFactor" select="number($pageWidth) div number($imageWidthAfterDpiScaling)"/>
				<xsl:variable name="imageHeightAfterWidthBasedScaling">
					<xsl:choose>
						<xsl:when test="number($imageWidthAfterDpiScaling) &gt; number($pageWidth)">
							<xsl:value-of select="round(number($imageHeightAfterDpiScaling) * number($widthBasedScaleFactor))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="number($imageHeightAfterDpiScaling)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="imageWidthAfterWidthBasedScaling">
					<xsl:choose>
						<xsl:when test="number($imageWidthAfterDpiScaling) &gt; number($pageWidth)">
							<xsl:value-of select="round(number($imageWidthAfterDpiScaling) * number($widthBasedScaleFactor))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="number($imageWidthAfterDpiScaling)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="heightBasedScaleFactor" select="number($pageHeight) div number($imageHeightAfterWidthBasedScaling)"/>
				<xsl:variable name="imageHeightAfterHeightBasedScaling">
					<xsl:choose>
						<xsl:when test="number($imageHeightAfterWidthBasedScaling) &gt; number($pageHeight)">
							<xsl:value-of select="round(number($imageHeightAfterWidthBasedScaling) * number($heightBasedScaleFactor))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="number($imageHeightAfterWidthBasedScaling)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="imageWidthAfterHeightBasedScaling">
					<xsl:choose>
						<xsl:when test="number($imageHeightAfterWidthBasedScaling) &gt; number($pageHeight)">
							<xsl:value-of select="round(number($imageWidthAfterWidthBasedScaling) * number($heightBasedScaleFactor))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="number($imageWidthAfterWidthBasedScaling)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="$returnDimension = 'height'">
						<xsl:value-of select="$imageHeightAfterHeightBasedScaling"/>
					</xsl:when>
					<xsl:when test="$returnDimension = 'width'">
						<xsl:value-of select="$imageWidthAfterHeightBasedScaling"/>
					</xsl:when>
				</xsl:choose>								
			</xsl:if>
		</xsl:if>			
	</xsl:template>

	<xsl:template name="CalcHeightWidthFromDPI">
		<xsl:param name="actualSize" />
		<xsl:param name="dpi" />
		<xsl:choose>
			<xsl:when test="not(number($actualSize) = NaN or number($dpi) = NaN)">
				<xsl:value-of select="round(number($actualSize) div number($dpi))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&valueCannotBeCalculatedText;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="createDocumentGatewayBlobLink">
		<xsl:param name="contents"/>
	<xsl:param name="county"/>
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

		<xsl:variable name="blobHref">
			<xsl:choose>
				<xsl:when test="string-length($localImageGuid) &gt; 0 and string-length($contents) &gt; 0">
					<xsl:call-template name="createDocketBlobLink">
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
						<xsl:with-param name="casenumber" select="$casenumber"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="filename" select="$filename"/>
						<xsl:with-param name="platform" select="$platform"/>
						<xsl:with-param name="mimeType" select="$mimeType"/>
						<xsl:with-param name="originationContext" select="$originationContext"/>
						<xsl:with-param name="guid" select="$localImageGuid"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createGatewayBlobHref">
			<xsl:with-param name="county" select="$county"/>
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
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
		<xsl:if test="string-length($blobHref) &gt; 0">
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
				<xsl:variable name="imageLinkHref">
				  <xsl:call-template name="GetHrefForImageLink">
					<xsl:with-param name="blobHref" select="$blobHref"/>
					<xsl:with-param name="hasAttachments" select="$hasAttachments"/>
				  </xsl:call-template>
				</xsl:variable>
				<xsl:if test="string-length($imageLinkHref) &gt; 0">
				  <xsl:attribute name="href">
					<xsl:value-of select="$imageLinkHref"></xsl:value-of>
				  </xsl:attribute>
				</xsl:if>
				
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
			  <xsl:if test="string-length($blobHref) = 0">
				<xsl:text> co_disabled</xsl:text>
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
				  <xsl:choose>
					<xsl:when test="contains($displayIcon,'permalink')">
					  <xsl:value-of select="$displayIcon"/>
					</xsl:when>
					<xsl:otherwise>
					  <xsl:value-of select="$Images"/>
					  <xsl:value-of select="$displayIcon"/>
					</xsl:otherwise>
				  </xsl:choose>
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
	  </xsl:if>
	</xsl:template>

	<xsl:template name="createDocumentBlobLink">
		<xsl:param name="contents"/>
		<xsl:param name="guid" select="."/>
		<xsl:param name="targetType" select="@ttype"/>
		<xsl:param name="mimeType" />
		<xsl:param name="className" />
		<xsl:param name="displayIcon" />
		<xsl:param name="displayIconClassName" />
		<xsl:param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
		<xsl:param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		<xsl:param name="prettyName" />
		<xsl:param name="hash" />

		<xsl:variable name="blobHref">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="$guid"/>
				<xsl:with-param name="targetType" select="$targetType"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="originationContext" select="$originationContext"/>
				<xsl:with-param name="prettyName" select="$prettyName" />
				<xsl:with-param name="hash" select="$hash" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="GetProductSpecificImageMarkup">
				<xsl:with-param name="blobHref" select="$blobHref"/>
			</xsl:call-template>
			
			<a>
				<xsl:if test="string-length($blobHref) &gt; 0">
					<xsl:attribute name="href">
						<xsl:value-of select="$blobHref"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="class">
					<xsl:text>&blobLinkClass;</xsl:text>
					<xsl:if test="$className">
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:value-of select="$className"/>
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
			  <xsl:choose>
				<xsl:when test="contains($displayIcon,'permalink')">
				  <xsl:value-of select="$displayIcon"/>
				</xsl:when>
				<xsl:otherwise>
				  <xsl:value-of select="$Images"/>
				  <xsl:value-of select="$displayIcon"/>
				</xsl:otherwise>
			  </xsl:choose>
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
				<xsl:copy-of select="$contents"/>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="buildBlobImageElement">
		<xsl:param name="contents"/>
		<xsl:param name="alt"/>
		<xsl:param name="src"/>
		<xsl:param name="height"/>
		<xsl:param name="width"/>
		<xsl:param name="class"/>
		<xsl:param name="displayIcon" />
		<xsl:param name="displayIconClassName" />
		<xsl:param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
		<xsl:param name="compressionLevel" />
		<xsl:choose>
			<xsl:when test="string-length($displayIcon) &gt; 0">
				<img>
					<xsl:attribute name="src">
			<xsl:choose>
			  <xsl:when test="contains($displayIcon,'permalink')">
				<xsl:value-of select="$displayIcon"/>
			  </xsl:when>
			  <xsl:otherwise>
				<xsl:value-of select="$Images"/>
				<xsl:value-of select="$displayIcon"/>
			  </xsl:otherwise>
			</xsl:choose>
					</xsl:attribute>
					<xsl:attribute name="class">
						<xsl:value-of select="$displayIconClassName"/>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="$displayIconAltText"/>
					</xsl:attribute>
				</img>
			</xsl:when>
			<xsl:otherwise>
				<img alt="{$alt}">
					<xsl:attribute name="src">
						<xsl:choose>
							<xsl:when test="string-length($compressionLevel) &gt; 0">
								<xsl:value-of select="concat($src, '&amp;compress=', $compressionLevel)" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$src" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="number($height)">
						<xsl:attribute name="height">
							<xsl:value-of select="$height"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="number($width)">
						<xsl:attribute name="width">
							<xsl:value-of select="$width"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="class">
						<xsl:value-of select="$class"/>
					</xsl:attribute>
				</img>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:call-template name="buildBlobImageElementContentsHack">
			<xsl:with-param name="contents" select="$contents" />	
		</xsl:call-template>
	</xsl:template>

	<!-- Some delivery formats show a blue box for the &#x200B; character, this allows override of the hack. -->
	<xsl:template name="buildBlobImageElementContentsHack">
		<xsl:param name="contents"/>
		
		<!-- HACK to make string-length evaluate to greater than 0 -->
		<xsl:text>&#x200B;</xsl:text>
		<xsl:copy-of select="$contents"/>
	</xsl:template>

	<xsl:template name="getCourtNumber">
		<xsl:param name="courtNumber"/>
		<xsl:choose>
			<xsl:when test="string-length($courtNumber) &gt; 0">
				<xsl:value-of select="$courtNumber" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$JurisdictionNumber" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getContentText">
		<xsl:apply-templates />
		<xsl:text> &pdfLabel;</xsl:text>
	</xsl:template>

	<!-- Blank template for product to override -->
	<xsl:template name="GetProductSpecificImageMarkup"/>

	<xsl:template name="GetHrefForImageLink">
	  <xsl:param name="blobHref"/>
	  <xsl:param name="hasAttachments"/>
			<xsl:text>javascript:void(0);</xsl:text>
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
