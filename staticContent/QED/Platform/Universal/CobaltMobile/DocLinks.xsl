<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl" xmlns:CiteQuery="urn:citeQuery" xmlns:UrlBuilder="urn:urlBuilder" extension-element-prefixes="CiteQuery UrlBuilder">
  <xsl:output method="xml" indent="yes"/>

	<xsl:template name="mobileDocumentSectionLink">
		<xsl:param name="sectionId" select="'0'"/>
		<xsl:param name="docGuid" select="//md.uuid"/>
		<xsl:param name="ListSource" select="''"/>
		<xsl:param name="NavigationPath" select="''"/>
		<xsl:param name="List" select="''"/>
		<xsl:param name="Rank" select="''"/>

		<xsl:choose>
			<xsl:when test="$ListSource = ''">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentSection', concat('guid=', $docGuid), concat('sectionId=', $sectionId), concat('&transitionTypeParamName;=', '&transitionTypeDocument;'))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentSection', concat('guid=', $docGuid), concat('sectionId=', $sectionId), concat('ListSource=', $ListSource), concat('NavigationPath=', $NavigationPath), concat('List=', $List), concat('Rank=', $Rank), concat('&transitionTypeParamName;=', '&transitionTypeDocument;'))"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<xsl:template name="createBlobLink">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="mimeType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="forImgTag" />
		<xsl:param name="originationContext" />

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
				<xsl:when test="$mimeType = 'text/plain'">
					<xsl:text>txt</xsl:text>
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
						<xsl:if test="string-length($targetType) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0">
								<xsl:text>&amp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="concat('targetType=', $targetType)"/>
						</xsl:if>
						<xsl:if test="string-length($originationContext) &gt; 0">
							<xsl:if test="string-length($maxHeight) &gt; 0 or string-length($targetType) &gt; 0">
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
				<xsl:choose>
					<xsl:when test="$highResolution">
						<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', '&transitionTypeDocumentImage;'), concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageFileName=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', '&transitionTypeDocumentImage;'), concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
