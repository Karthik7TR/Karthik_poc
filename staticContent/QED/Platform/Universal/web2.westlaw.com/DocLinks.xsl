<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="SearchTerms.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="specialVersionParamVariable">
		<xsl:value-of select="concat('&specialVersionParamName;=', $SpecialVersionParam)"/>
	</xsl:variable>

	<xsl:variable name="specialRequestSourceParamVariable">
		<xsl:value-of select="concat('&requestSourceUrlParamName;=',$SpecialRequestSourceParam)"/>
	</xsl:variable>

	<xsl:template name="createBlobLink">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="mimeType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="forImgTag" />
		<xsl:param name="originationContext" />

		<xsl:choose>
			<xsl:when test="$mimeType = 'application/pdf' and string-length($targetType) &gt; 0">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageFileName=', $guid), 'imcnt=NRS-IMAGE', concat('it=', $targetType), 'mt=Westlaw', 'uw=1', concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageFileName=', $guid), 'mt=Westlaw', 'uw=1', concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="cite.query | md.toggle.link" name="citeQuery">
		<xsl:param name="citeQueryElement" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$citeQueryElement/node()[not(self::starpage.anchor)]" />
		</xsl:param>

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
			<xsl:variable name="persistentUrl" select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, 'mt=Westlaw', 'uw=1', 'originationContext=&docDisplayOriginationContext;',  $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
			<xsl:choose>
				<xsl:when test="string-length($persistentUrl) &gt; 0">
					<a target="{$Target}">
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
						<xsl:if test="$isDraggable = 'true'">
							<xsl:attribute name="class">
								<xsl:text>&linkClass; &linkDraggableClass;</xsl:text>
							</xsl:attribute>
						</xsl:if>
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
		<xsl:if test="$citeQueryElement/following-sibling::node()[1]/self::cite.query">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>