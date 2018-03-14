<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Image.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Suppress image links -->
	<xsl:template match="ImageMetadata/n-metadata" name="imageMetadata" mode="MakeImageLink">
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
				<xsl:when test=".//md.image.height">
					<xsl:value-of select="number(translate(.//md.image.height,',',''))"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="width">
			<xsl:choose>
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

			<xsl:when test="$renderType = 'AmaAtlasSlLink'" />

			<xsl:when test="$renderType = 'XlsLink' or ($renderType = 'Image' and $mimeType = '&xlsMimeType;')" />

			<xsl:when test="$renderType = 'PDFLink' or ($renderType = 'Image' and $mimeType = '&pdfMimeType;')" />

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

	<!-- suppress PDF/XSL links -->
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
		<xsl:param name="data-language" />
	</xsl:template>
	
</xsl:stylesheet>
