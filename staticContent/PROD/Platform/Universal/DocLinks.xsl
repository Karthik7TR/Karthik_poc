<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="SearchTerms.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="specialVersionParamVariable">
		<xsl:if test="$DeliveryMode">
			<xsl:choose>
				<xsl:when test="string-length($SpecialVersionParam) &gt; 0">
					<xsl:value-of select="concat('&specialVersionParamName;=', $SpecialVersionParam)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&specialVersionParamName;=', '&versionForRequestDirector;')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:variable>

	<xsl:variable name="specialRequestSourceParamVariable">
		<xsl:if test="$DeliveryMode">
			<xsl:choose>
				<xsl:when test="string-length($SpecialRequestSourceParam) &gt; 0">
					<xsl:value-of select="concat('&requestSourceUrlParamName;=',$SpecialRequestSourceParam)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&requestSourceUrlParamName;=', '&requestSourceForRequestDirector;')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:variable>

	<xsl:template name="createDocketBlobLink">
		<!-- ==> this is where we create local PDF link -->
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
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('courtNorm=', $courtNorm), concat('id=', translate(substring-before($id, ';'), '|', ',')), concat('filename=', $filename), concat('courtnumber=', $courtNumber), concat('casenumber=', $casenumber), concat('originationContext=', $originationContext), concat('extension=', $extension), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&docketLocalImageGuid;=', $guid))"/>
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
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('courtNorm=', $courtNorm), concat('id=', translate(substring-before($id, ';'), '|', ',')), concat('filename=', $filename), concat('courtnumber=', $courtNumber), concat('casenumber=', $casenumber), concat('extension=', $extension), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', 'contextData=(sc.Default)', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
	</xsl:template>

	<xsl:template name="createBlobLink">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="mimeType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="maxWidth" />
		<xsl:param name="height" />
		<xsl:param name="width" />
		<xsl:param name="forImgTag" />
		<xsl:param name="originationContext" />
		<xsl:param name="prettyName" />
		<xsl:param name="hash" />
		<xsl:param name="docGuid" />

		<xsl:variable name="highResolutionSegment">
			<xsl:if test="$highResolution">
				<xsl:text>highResolution/</xsl:text>
			</xsl:if>
		</xsl:variable>

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
				<xsl:when test="$mimeType = 'image/x-png'">
					<xsl:text>png</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/x-wgsl'">
					<xsl:text>amz</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/rtf' or $mimeType = 'text/plain' and $targetType = 'PLC-multimedia'">
					<xsl:text>rtf</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/msword'">
					<xsl:text>doc</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'">
					<xsl:text>docx</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'text/plain'">
					<xsl:text>txt</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/vnd.ms-excel'">
					<xsl:text>xls</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/vnd.ms-powerpoint'">
					<xsl:text>ppt</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'">
					<xsl:text>xlsx</xsl:text>
				</xsl:when>
				<xsl:when test="$mimeType = 'application/zip'">
					<xsl:text>zip</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$DeliveryMode and $forImgTag">
				<xsl:if test="string-length($ImageHost) &gt; 0 and string-length($guid) &gt; 0 and string-length($extension) &gt; 0">
					<xsl:value-of select="concat($ImageHost, '/Blob/V1/')" />
					<xsl:if test="$highResolution">
						<xsl:text>HighResolution/</xsl:text>
					</xsl:if>
					<xsl:value-of select="concat($guid, '.', $extension)" />
					<xsl:variable name="queryString">
						<xsl:if test="string-length($maxHeight) &gt; 0">
							<xsl:value-of select="concat('maxHeight=', $maxHeight)"/>
						</xsl:if>
						<xsl:if test="string-length($maxWidth) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0">
								<xsl:text>&amp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="concat('maxWidth=', $maxWidth)"/>
						</xsl:if>
						<xsl:if test="string-length($height) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0 or string-length($maxWidth) &gt; 0">
								<xsl:text>&amp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="concat('height=', $height)"/>
						</xsl:if>
						<xsl:if test="string-length($width) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0 or string-length($maxWidth) &gt; 0 or string-length($height) &gt; 0">
								<xsl:text>&amp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="concat('width=', $width)"/>
						</xsl:if>
						<xsl:if test="string-length($targetType) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0 or string-length($maxWidth) &gt; 0 or string-length($height) &gt; 0 or string-length($width) &gt; 0">
								<xsl:text>&amp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="concat('targetType=', $targetType)"/>
						</xsl:if>
						<xsl:if test="string-length($originationContext) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0 or string-length($maxWidth) &gt; 0 or string-length($height) &gt; 0 or string-length($width) &gt; 0 or string-length($targetType) &gt; 0">
								<xsl:text>&amp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="concat('originationContext=', $originationContext)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:if test="string-length($queryString) &gt; 0">
						<xsl:value-of select="concat('?', $queryString)"/>
					</xsl:if>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="CreateBlobLinkUrl">
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
	</xsl:template>

	<xsl:template name="CreateBlobLinkUrlPlatform">
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
			<xsl:when test="$highResolution">
				<xsl:choose>
					<xsl:when test="$UseRelativePathForImages">
						<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), concat('originatingDocGuid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), concat('originatingDocGuid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length($prettyName) &gt; 0">
				<xsl:choose>
					<xsl:when test="$UseBlobRoyaltyId">
						<xsl:variable name="originatingDocGuid" select="$Guid" />
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$UseBlobRoyaltyId">
						<xsl:variable name="originatingDocGuid" select="$Guid" />
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="md.toggle.links">
		<div>
			<xsl:apply-templates select ="md.toggle.link[1]" />
		</div>
	</xsl:template>

	<xsl:template match="keyCiteFlagLink.Url" name="keyCiteFlag">
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.KeyCiteFlagHistoryByLookup', concat('docGuid=', @docGuid), 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<xsl:template name="categoryPageLink">
		<xsl:param name="id" />
		<xsl:param name="databaseId" />
		<xsl:param name="linkContents" />

		<xsl:variable name="persistentUrl">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.GetCategoryInformationBySignOn', concat('databaseId=', $databaseId), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
				<a>
					<xsl:attribute name="id">
						<xsl:text>&linkIdPrefix;</xsl:text>
						<xsl:value-of select="$id"/>
					</xsl:attribute>
					<xsl:attribute name="class">
						<xsl:text>&linkClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$persistentUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="createHeadnotesCitedCaseRefLink">
		<xsl:param name="guid" />
		<xsl:param name="headnoteId" />
		<xsl:param name="originationContext" />
		<xsl:param name="docSource" select="''" />
		<xsl:param name="rank" select="''" />

		<xsl:if test="string-length($headnoteId) &gt; 0">
			<xsl:choose>
				<xsl:when test="string-length($docSource) &gt; 0">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocHeadnoteCitingReferencesByLookup', concat('docGuid=', $guid), concat('headnoteId=', $headnoteId), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('docSource=', $docSource), concat('rank=', $rank), '&transitionTypeParamName;=&transitionTypeCitingReferences;')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocHeadnoteCitingReferencesByLookup', concat('docGuid=', $guid), concat('headnoteId=', $headnoteId), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeCitingReferences;')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="createHeadnotesTopicLink">
		<xsl:param name="citeQueryElement"/>
		<xsl:variable name="sourceCite">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite[not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite)]" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$citeQueryElement">
			<xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentItem;')"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="markupSourceSerialSearchTerm">
		<xsl:param name="linkContents"/>
		<xsl:call-template name="nHit">
			<xsl:with-param name="contents" select="$linkContents"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="md.toggle.link" name="toggleLink">
		<xsl:call-template name="citeQuery">
			<xsl:with-param name="transitionType" select="'&transitionTypeDocumentToggle;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="a[//cite[@data-tr-normalized-cite]]" priority="9">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cite[@data-tr-normalized-cite]" priority="9">

		<xsl:variable name="citation">
			<xsl:value-of select="@data-tr-normalized-cite"/>
		</xsl:variable>

		<xsl:variable name="citeValue">
			<xsl:value-of select="'normalizedCite='"/>
			<xsl:value-of select="$citation"/>
		</xsl:variable>

		<xsl:variable name="persistentUrl">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', 'viewType=FullText', $citeValue, 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
		</xsl:variable>

		<!--xsl:variable name="persistentDeliveryUrl">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', 'viewType=FullText', $citeValue, 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
		</xsl:variable-->

		<xsl:variable name="group">
			<xsl:value-of select="@citationGroupId"/>
		</xsl:variable>

		<!-- We may need some CSS classes on this stuff for handling link highlights and image size etc -->
		<xsl:choose>
			<xsl:when test="CitationLinkExtension:HasFlagUrls($citation, 'WestlawNext', $DeliveryMode) = 'true'">
				<xsl:if test="(not(preceding::cite[@citationGroupId=$group]) or ancestor::SummaryOfCitationsMetadata)">
					<xsl:for-each select="CitationLinkExtension:CreateFlagCitations($citation, 'WestlawNext', $DeliveryMode, 'false')//Citation">
						<xsl:if test="(string-length(FlagColor) &gt; 0)">
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="FlagCitationUrl"/>
								</xsl:attribute>
								<xsl:text>&zeroWidthSpace;</xsl:text>
								<img>
									<xsl:attribute name="class">
										<xsl:text>&citationFlagImage;</xsl:text>
									</xsl:attribute>
									<xsl:attribute name="src">
										<xsl:value-of select="FlagImageUrl"/>
									</xsl:attribute>
									<xsl:attribute name="alt">
										<xsl:value-of select="FlagColor"/>
									</xsl:attribute>
								</img>
							</a>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="string-length($persistentUrl) &gt; 0">
						<a>
							<xsl:attribute name="class">
								<xsl:text>&linkClass;</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="href">
								<xsl:value-of select="$persistentUrl"/>
							</xsl:attribute>
							<xsl:attribute name="id">
								<xsl:text>CiteId</xsl:text>
								<xsl:value-of select="@citationId"/>
							</xsl:attribute>
							<xsl:value-of select="text()"/>
						</a>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="text()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="cite.query" name="citeQuery">
		<xsl:param name="citeQueryElement" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$citeQueryElement/node()[not(self::starpage.anchor)]" />
		</xsl:param>
		<xsl:param name="transitionType" select="'&transitionTypeDocumentItem;'" />
		<xsl:param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		<xsl:param name ="originationPubNum"/>
		<xsl:variable name="fullLinkContents">
			<xsl:choose>
				<xsl:when test="string-length($SourceSerial) &gt; 0 and ($citeQueryElement/@w-serial-number = $SourceSerial or $citeQueryElement/@w-normalized-cite = $SourceSerial)">
					<xsl:call-template name="markupSourceSerialSearchTerm">
						<xsl:with-param name="linkContents" select="$linkContents"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="isDraggable">
			<xsl:choose>
				<xsl:when test="$citeQueryElement/@w-ref-type = 'KD' or $citeQueryElement/@w-ref-type = 'KW'">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:when test="not($AllowLinkDragAndDrop)">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>true</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:apply-templates select="$citeQueryElement/starpage.anchor"/>

		<xsl:variable name="sourceCite">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite[not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite)]" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="string-length($fullLinkContents) &gt; 0">

			<xsl:variable name="infoType" select="/Document/n-metadata/metadata.block/md.infotype"/>

			<xsl:variable name="persistentUrl">
				<xsl:choose>
					<xsl:when test="$citeQueryElement/@w-ref-type = 'WK'">
						<xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', concat('typecode=', $infoType/@typecode), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', $transitionType))"/>
					</xsl:when>
					<xsl:when test="$citeQueryElement/@w-ref-type = 'EDGAR'">
						<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=', $citeQueryElement/@ID), 'viewType=FullText', concat('originationContext=', $originationContext), '&transitionTypeParamName;=&transitionTypeDocumentItem;' )"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', $transitionType), concat('pubNum=', $originationPubNum))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:call-template name ="displayCiteQueryLink">
				<xsl:with-param name="persistentUrl" select="$persistentUrl"/>
				<xsl:with-param name="citeQueryElement" select="$citeQueryElement"/>
				<xsl:with-param name="fullLinkContents" select="$fullLinkContents"/>
				<xsl:with-param name="isDraggable" select="$isDraggable"/>
			</xsl:call-template>
		</xsl:if>

		<!-- Add a space if the following sibling is a cite.query -->
		<xsl:if test="$citeQueryElement/following-sibling::node()[1]/self::cite.query">
			<!-- We have a named template here to allow products to override the display if needed. For example: Commentary content wants a '|' character between the navigation links so that product can override this function to display the separation as they wish. -->
			<xsl:call-template name="renderSpaceBetweenCiteQueries" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderSpaceBetweenCiteQueries">
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template name="displayCiteQueryLink">
		<xsl:param name ="persistentUrl" />
		<xsl:param name="citeQueryElement" />
		<xsl:param name="fullLinkContents" />
		<xsl:param name="isDraggable" />

		<xsl:choose>
			<xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
				<a>
					<xsl:attribute name="id">
						<xsl:text>&linkIdPrefix;</xsl:text>
						<xsl:choose>
							<xsl:when test="string-length($citeQueryElement/@ID) &gt; 0">
								<xsl:value-of select="$citeQueryElement/@ID"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="generate-id($citeQueryElement)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:attribute name="class">
						<xsl:text>&linkClass;</xsl:text>
						<xsl:if test="$isDraggable = 'true'">
							<xsl:text><![CDATA[ ]]>&linkDraggableClass;</xsl:text>
						</xsl:if>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$persistentUrl"/>
					</xsl:attribute>
					<xsl:if test="$citeQueryElement/@w-ref-type = 'WM'">
						<xsl:attribute name="target">
							<xsl:text>_blank</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:copy-of select="$fullLinkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$fullLinkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="cite.query[cite.query]" priority="1">
		<xsl:apply-templates mode="nestedCiteQueryRef" />
	</xsl:template>

	<xsl:template match="cite.query/node()" mode="nestedCiteQueryRef">
		<xsl:choose>
			<xsl:when test="self::cite.query">
				<xsl:apply-templates select="." />
			</xsl:when>
			<xsl:when test="self::starpage.anchor">
				<xsl:apply-templates select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="citeQuery">
					<xsl:with-param name="linkContents" select="." />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="createCiteQueryLinkByParameters">
		<xsl:param name="linkContents" />
		<xsl:param name="findType" />
		<xsl:param name="serNum" />
		<xsl:param name="pubNum" />
		<xsl:param name="cite" />

		<xsl:variable name="persistentUrl" select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', concat('findType=', $findType), concat('serNum=', $serNum), concat('pubNum=', $pubNum), concat('cite=', $cite), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentItem;' )"/>
		<xsl:choose>
			<xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
				<a>
					<xsl:attribute name="class">
						<xsl:text>&linkClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$persistentUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="renderNotesOfDecisionsLink">
		<xsl:param name="count"/>
		<xsl:param name="docGuid"/>
		<xsl:param name="originationContext" />
		<xsl:param name="text"/>
		<xsl:variable name="renderNotesOfDecisionsUrl" select="UrlBuilder:CreatePersistentUrl('Page.NotesofDecisionsByLookup', concat('docGuid=', $docGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeNotesOfDecision;' )" />
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

	<!-- Don't create links in text-only scenarios (e.g. Composite Header title, title metadata) -->
	<xsl:template match="Title//cite.query" priority="2">
		<xsl:param name="linkContents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:copy-of select="$linkContents"/>
	</xsl:template>

	<!-- We need to suppress any CM cite.query elements in Refs & Annos documents because they will not resolve -->
	<xsl:template match="refs.annos//cite.query[@w-ref-type='CM']" priority="2" />

	<xsl:template name="createDocketsUpdateJson">
		<xsl:variable name="docketDocGuid" select="$Guid" />

		<xsl:variable name="docketCaseNumber">
			<xsl:call-template name="getDocketCaseNumber" />
		</xsl:variable>

		<xsl:variable name="docketCaseTitle">
			<xsl:call-template name="json-encode">
				<xsl:with-param name="str" select="/Document/n-metadata/metadata.block/md.descriptions/md.title" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="docketCourtNorm">
			<xsl:call-template name="getDocketCourtNorm" />
		</xsl:variable>

		<xsl:variable name="additionalParmsJsonObject">
			<xsl:call-template name="getAdditionalParmsJsonObject" />
		</xsl:variable>

		<xsl:variable name="jsonObject">
			<xsl:text>{</xsl:text>
			<xsl:value-of select="concat('&quot;', '&docketJsonDocGuid;', '&quot;:&quot;', $docketDocGuid, '&quot;')" />
			<xsl:value-of select="concat(',&quot;', '&docketJsonSignon;', '&quot;:&quot;', $docketCourtNorm, '&quot;')" />
			<xsl:value-of select="concat(',&quot;', '&docketJsonCaseNumber;', '&quot;:&quot;', $docketCaseNumber, '&quot;')" />
			<xsl:value-of select="concat(',&quot;', '&docketJsonCaseTitle;', '&quot;:&quot;', $docketCaseTitle, '&quot;')" />
			<xsl:value-of select="concat(',&quot;', '&docketJsonSlowCourt;', '&quot;:', $DocketIsSlowCourt)" />
			<xsl:if test="string-length($additionalParmsJsonObject) &gt; 0">
				<xsl:value-of select="concat(',&quot;', '&docketJsonAdditionalParms;', '&quot;:{', $additionalParmsJsonObject, '}')" />
			</xsl:if>
			<xsl:text>}</xsl:text>
		</xsl:variable>

		<xsl:value-of select="$jsonObject"/>
	</xsl:template>

	<xsl:template name="createDocketsUpdateLink">
		<xsl:choose>
			<xsl:when test="not($IsMobile)">
				<xsl:text></xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="docketDocGuid" select="$Guid" />

				<xsl:variable name="docketCaseNumber">
					<xsl:call-template name="getDocketCaseNumber" />
				</xsl:variable>

				<xsl:variable name="docketCaseTitle">
					<xsl:value-of select="/Document/n-metadata/metadata.block/md.descriptions/md.title" />
				</xsl:variable>

				<xsl:variable name="docketCourtNorm">
					<xsl:call-template name="getDocketCourtNorm" />
				</xsl:variable>

				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketUpdateFull', concat('guid=', $docketDocGuid), concat('caseNumber=', $docketCaseNumber), concat('caseTitle=', $docketCaseTitle), concat('signon=', $docketCourtNorm), concat('slowCourt=', $DocketIsSlowCourt) )" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="createDocketsMashupLink">
		<xsl:param name="viewType" />

		<xsl:variable name="guid" select="$Guid" />
		<xsl:variable name="signon">
			<xsl:call-template name="getDocketSignon" />
		</xsl:variable>
		<xsl:variable name="docketCaseNumber">
			<xsl:call-template name="getDocketCaseNumber" />
		</xsl:variable>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Docket', concat('guid=', $guid), concat('viewType=', $viewType), concat('signon=', $signon), concat('docNum=', $docketCaseNumber), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocument;' )" />
	</xsl:template>

	<xsl:template name="createDocketsTrackMashupLink">
		<xsl:variable name="guid" select="$Guid" />
		<xsl:variable name="docketCaseNumber">
			<xsl:call-template name="getDocketCaseNumber"/>
		</xsl:variable>
		<xsl:variable name="courtType">
			<xsl:choose>
				<xsl:when test="//alert.info">
					<xsl:value-of select="//alert.info"/>
				</xsl:when>
				<xsl:when test="//md.sortvalues/md.case.type">
					<xsl:value-of select="//md.sortvalues/md.case.type"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&ctypeCivil;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="jurisdiction">
			<xsl:choose>
				<xsl:when test="//md.jurisdiction/md.jurisabbrev">
					<xsl:value-of select="concat('&trackJurisdictionPrefix;', //md.jurisdiction/md.jurisabbrev)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketTrack', concat('&guidParamName;=', $guid), concat('&dnumParamName;=', $docketCaseNumber), concat('&actionParamName;=', '&actionCreate;'), concat('&ctypeParamName;=', $courtType), concat('&jurisParamName;=', $jurisdiction), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocument;' )" />
	</xsl:template>

	<xsl:template name="createDocketsCreditorMashupLink">
		<xsl:variable name="docGuid" select="$Guid" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketCreditorNew', concat('docguid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocument;' )" />
	</xsl:template>

	<xsl:template name="createDocketsClaimRegisterMashupLink">
		<xsl:variable name="docGuid" select="$Guid" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketClaimRegister', concat('docguid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocument;' )" />
	</xsl:template>

	<xsl:template name="createDocketsCalenderingLink">
		<xsl:variable name="docGuid" select="$Guid" />
		<xsl:variable name="county">
			<xsl:choose>
				<xsl:when test="/Document/n-docbody/r/update.link.block/link.parameter[parameter.name='COUNTY']/parameter.value">
					<xsl:value-of select="/Document/n-docbody/r/update.link.block/link.parameter[parameter.name='COUNTY']/parameter.value" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketCalendarNew', concat('docguid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('county=', $county), '&transitionTypeParamName;=&transitionTypeDocument;' )" />
	</xsl:template>

	<xsl:template name="getNormalizedDbId">
		<xsl:choose>
			<xsl:when test="//md.jurisdiction/md.jurisabbrev">
				<xsl:value-of select="//md.jurisdiction/md.jurisabbrev"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getDocketSignon">
		<xsl:choose>
			<xsl:when test="/Document/n-docbody/r/cluster.name">
				<xsl:value-of select="/Document/n-docbody/r/cluster.name" />
			</xsl:when>
			<xsl:when test="/Document/n-docbody/r/col.key">
				<xsl:value-of select="/Document/n-docbody/r/col.key" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getDocketCaseNumber">
		<xsl:choose>
			<xsl:when test="//update.link.block/link.parameter[parameter.name='CN']/parameter.value">
				<xsl:value-of select="//update.link.block/link.parameter[parameter.name='CN']/parameter.value"/>
			</xsl:when>
			<xsl:when test="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum">
				<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="//docket.block/docket.number.dc" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getDocketCourtNorm">
		<xsl:value-of select="/Document/n-docbody/r/case.information.block/court.block/court.norm | /Document/n-docbody/r/c/court.norm | /Document/n-docbody/r/court.block/court.norm | /Document/n-docbody/r/claims.register.block/case.info.block/court.block/court.norm" />
	</xsl:template>

	<xsl:template name="getAdditionalParmsJsonObject">
		<xsl:for-each select="/Document//r/update.link.block/link.parameter">
			<xsl:if test="parameter.name != 'CN'">
				<xsl:value-of select="concat('&quot;', translate(parameter.name,':',''), '&quot;:&quot;', parameter.value, '&quot;')" />
				<xsl:if test="position() != last() and following-sibling::link.parameter/parameter.name != 'CN'">
					<xsl:text>,</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="xrefLink">
		<xsl:param name="xrefElement" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$xrefElement/node()[not(self::starpage.anchor)]" />
		</xsl:param>
		<xsl:param name="transitionType" select="'&transitionTypeDocumentItem;'" />

		<xsl:variable name="fullLinkContents">
			<xsl:choose>
				<xsl:when test="string-length($SourceSerial) &gt; 0 and ($xrefElement/@wlserial = $SourceSerial)">
					<xsl:call-template name="markupSourceSerialSearchTerm">
						<xsl:with-param name="linkContents" select="$linkContents"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="isDraggable">
			<xsl:choose>
				<xsl:when test="not($AllowLinkDragAndDrop)">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>true</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($fullLinkContents) &gt; 0">
			<xsl:variable name="persistentUrl">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', 'viewType=FullText', 'findType=Y', concat('pubNum=',@pubid), concat('serNum=',@wlserial),'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="string-length($persistentUrl) &gt; 0">
					<a>
						<xsl:attribute name="id">
							<xsl:text>&linkIdPrefix;</xsl:text>
							<xsl:choose>
								<xsl:when test="string-length(@idref) &gt; 0">
									<xsl:value-of select="@idref"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="generate-id($xrefElement)"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>&linkClass;</xsl:text>
							<xsl:if test="$isDraggable = 'true'">
								<xsl:text><![CDATA[ ]]>&linkDraggableClass;</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:attribute name="href">
							<xsl:copy-of select="$persistentUrl"/>
						</xsl:attribute>
						<xsl:copy-of select="$fullLinkContents"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$fullLinkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

		<!-- Add a space if the following sibling is a cite.query -->
		<xsl:if test="$xrefElement/following-sibling::node()[1]/self::cite.query">
			<!-- We have a named template here to allow products to override the display if needed. For example: Commentary content wants a '|' character between the navigation links so that product can override this function to display the separation as they wish. -->
			<xsl:call-template name="renderSpaceBetweenCiteQueries" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="SendRunnerHref">
		<xsl:param name="documentGuid" />
		<xsl:param name="orderedIndex" />
		<xsl:param name="checkSum" />
		<xsl:param name="docPersistId" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentOrderReview', concat('documentGuid=', $documentGuid), concat('orderedIndex=', $orderedIndex), concat('checkSum=', $checkSum), concat('docPersistId=', $docPersistId), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;',$specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<xsl:template name="BatchPdfHref">
		<xsl:param name="documentGuid" />
		<xsl:param name="pdfIndex" />
		<xsl:param name="checkSum" />
		<xsl:param name="docPersistId" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketsPdfBatchDownload', concat('documentGuid=', $documentGuid), concat('pdfIndex=', $pdfIndex), concat('checkSum=', $checkSum), concat('docPersistId=', $docPersistId), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;',$specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<xsl:template name="GetOtherExpertsResponses">
		<xsl:param name="citeQuery" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$citeQuery/node()[not(self::starpage.anchor)]" />
		</xsl:param>
		<xsl:param name="transitionType" select="'&transitionTypeDocumentItem;'" />

		<xsl:variable name="isDraggable">
			<xsl:choose>
				<xsl:when test="not($AllowLinkDragAndDrop)">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>true</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="$citeQuery and string-length($citeQuery/@w-normalized-cite) &gt; 0">
			<xsl:variable name="query" select="concat('strict: question.id(', $citeQuery/@w-normalized-cite, ')')" />
			<xsl:variable name="persistentUrl">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.RedirectToExpertQA', concat('query=',$query), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="string-length($persistentUrl) &gt; 0">
					<a>
						<xsl:attribute name="id">
							<xsl:text>&linkIdPrefix;</xsl:text>
							<xsl:value-of select="$citeQuery/@w-normalized-cite"/>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>&linkClass;</xsl:text>
							<xsl:if test="$isDraggable = 'true'">
								<xsl:text><![CDATA[ ]]>&linkDraggableClass;</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:attribute name="href">
							<xsl:copy-of select="$persistentUrl"/>
						</xsl:attribute>
						<xsl:copy-of select="$linkContents"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="GlossaryTerm">
		<span class="&glossaryTerm;">
			<input type="hidden">
				<xsl:attribute name="value">
					<xsl:value-of select="@guid" />
				</xsl:attribute>
			</input>
			<a class="&linkClass; &linkDraggableClass;">
				<xsl:attribute name="guid">
					<xsl:value-of select="@guid" />
				</xsl:attribute>
				<xsl:attribute name="href">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="@guid" />
					</xsl:call-template>
				</xsl:attribute>
				<xsl:apply-templates />
			</a>
		</span>
	</xsl:template>			

	<xsl:template name="GetDocumentUrl">
		<xsl:param name ="documentGuid" />
		<xsl:param name ="navigationPath" />
		<xsl:param name ="list" />
		<xsl:param name ="listSource" />
		<xsl:param name ="rank" />
		<xsl:param name ="transitionType" select="'&transitionTypeDocumentItem;'"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$documentGuid), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', concat('&transitionTypeParamName;=', $transitionType), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&navigationPathParamName;=', $navigationPath), concat('&listParamName;=', $list), concat('&listSourceParamName;=', $listSource), concat('&rankParamName;=', $rank))"/>
	</xsl:template>

	<xsl:template name="GetSearchResultUrl">
		<xsl:param name ="query" />
		<xsl:param name ="categoryPageUrl" />
		<xsl:param name ="jurisdiction" />
		<xsl:param name ="simpleSearch" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.SearchResults', concat('categoryPageUrl=',$categoryPageUrl), concat('query=',$query), concat('jurisdiction=',$jurisdiction), concat('simpleSearch=',$simpleSearch), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
</xsl:stylesheet>
