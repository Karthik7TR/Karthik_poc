<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.LINKS.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Generate url with Links as host -->
	<!-- Currently used by Links's FormsPDFLinks.xsl, DocLinks.xsl and Links's Gao.xsl -->
	<xsl:template name="CreateBlobLinkUrlLINKS">
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
						<xsl:value-of select="UrlBuilder:CreateRelativePersistentLINKSUrlIgnoreBlock('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), concat('originatingDocGuid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrlIgnoreBlock('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), concat('originatingDocGuid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length($prettyName) &gt; 0">
				<xsl:choose>
					<xsl:when test="$UseBlobRoyaltyId">
						<xsl:variable name="originatingDocGuid" select="$Guid" />
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentLINKSUrlIgnoreBlock('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrlIgnoreBlock('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentLINKSUrlIgnoreBlock('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrlIgnoreBlock('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
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
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentLINKSUrlIgnoreBlock('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrlIgnoreBlock('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentLINKSUrlIgnoreBlock('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrlIgnoreBlock('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
