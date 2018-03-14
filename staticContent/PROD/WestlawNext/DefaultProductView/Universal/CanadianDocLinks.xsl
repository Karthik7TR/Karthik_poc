<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<!-- Include the platform FO transform -->
	<xsl:include href="DocLinks.xsl"/>
	<xsl:include href="WrappingUtilities.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="link" name="DocumentLink">
		<xsl:param name="documentGuid" select="@tuuid"/>
		<xsl:param name="browsePageUrl"/>
		<xsl:param name="isSearchLink" select="false()"/>
		<xsl:param name ="isSingleSearchDocument" select="false()"/>
		<xsl:param name="linkElement" select="."/>
		<xsl:param name="linkContents">
		<xsl:apply-templates select="$linkElement/node()" />
		</xsl:param>
    
		<xsl:variable name="fullLinkContents">
			<xsl:choose>
				<xsl:when test="string-length($SourceSerial) &gt; 0 and ($linkElement/@wlserial = $SourceSerial)">
					<xsl:call-template name="markupSourceSerialSearchTerm">
						<xsl:with-param name="linkContents" select="$linkContents"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="@tuuid='' and $documentGuid=''">
			<xsl:copy-of select="$fullLinkContents"/>
			</xsl:when>			
			<xsl:otherwise>
				<xsl:variable name="isDraggable">
					<xsl:call-template name="IsDraggableLink">
						<xsl:with-param name="documentGuid" select="$documentGuid"></xsl:with-param>
						<xsl:with-param name="browsePageUrl" select="$browsePageUrl"></xsl:with-param>
					</xsl:call-template>
				</xsl:variable>

				<xsl:variable name="persistentUrl">
					<xsl:choose>
						<!-- guid-based appliation feature links take precedence -->
						<!-- Term highlighting across links. Ex. Legal Memo Summary -> Legal Memo Full -->
						<xsl:when test="$isSingleSearchDocument = 'true' and string-length(concat($SearchQuery,$SearchWithinQuery)) &gt; 0">
							<xsl:call-template name="SingleSearchDocumentLink">
								<xsl:with-param name="documentGuid" select="$documentGuid"></xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Links to Abridgment Digest TOC within CED documents -->
						<xsl:when test="starts-with($documentGuid,'CANABR:')">
							<xsl:call-template name="AbridgmentTOCLink">
								<xsl:with-param name="topic" select="substring($documentGuid,8)"></xsl:with-param>
								<xsl:with-param name="isSearchLink" select="$isSearchLink"></xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Links to Abridgment Digest TOC within Caselaw/Case Views -->
						<xsl:when test="$browsePageUrl = '&crswAbridgmentPageUrl;'">
							<xsl:call-template name="AbridgmentTOCLink">
								<xsl:with-param name="topic" select="$documentGuid"></xsl:with-param>
								<xsl:with-param name="isSearchLink" select="$isSearchLink"></xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Links from Legal Memo documents back to Legal Memo TOC -->
						<xsl:when test="$browsePageUrl = '&crswLegalMemoPageUrl;'">
							<xsl:call-template name="TOCLink">
								<xsl:with-param name="topic" select="$documentGuid"></xsl:with-param>
								<xsl:with-param name="browsePageUrl" select="$browsePageUrl"></xsl:with-param>
								<xsl:with-param name="isSearchLink" select="$isSearchLink"></xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- If the <link> element has a wlserial attribute build WestMate style find link to match the way legacy Westlaw Canada builds the links and
						     to give links to International documents the best chance of resolving if CRC directs them back to legacy Westlaw Canada -->
						<xsl:when test="string-length(@wlserial) &gt; 0">
							<xsl:call-template name="GetDocumentFindUrl">
								<xsl:with-param name="pubid" select="@pubid"></xsl:with-param>
								<xsl:with-param name="wlserial" select="@wlserial"></xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name="documentGuid" select="$documentGuid"></xsl:with-param>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:call-template name="GetAnchorElmentForLink">
					<xsl:with-param name="persistentUrl" select="$persistentUrl"></xsl:with-param>
					<xsl:with-param name="isDraggable" select="$isDraggable"></xsl:with-param>
					<xsl:with-param name="fullLinkContents" select="$fullLinkContents"></xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="IsDraggableLink">
		<xsl:param name="documentGuid"/>
		<xsl:param name="browsePageUrl"/>
		<xsl:choose>
			<xsl:when test="not($AllowLinkDragAndDrop) or browsePageUrl = '&crswAbridgmentPageUrl;' or browsePageUrl = '&crswLegalMemoPageUrl;' or starts-with($documentGuid,'CANABR:')">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>true</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Build Anchor tag for <link> elements. -->
	<xsl:template name="GetAnchorElmentForLink">
		<xsl:param name="persistentUrl"/>
		<xsl:param name="isDraggable" select="false()"/>
		<xsl:param name ="fullLinkContents"/>
		<xsl:choose>
			<xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
				<xsl:choose>
					<xsl:when test="ancestor::cite.query | node()[self::link]">
						<span>
						<xsl:attribute name="id">
							<xsl:text>&linkIdPrefix;</xsl:text>
							<xsl:choose>
								<xsl:when test="string-length(@tuuid) &gt; 0">
									<xsl:value-of select="translate(@tuuid, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
								</xsl:when>
								<xsl:when test="string-length(@normalizedcite) &gt; 0">
									<xsl:value-of select="@normalizedcite"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="generate-id(.)"/>
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
						</span>
					</xsl:when>
					<xsl:otherwise>
						<a>
						<xsl:attribute name="id">
							<xsl:text>&linkIdPrefix;</xsl:text>
							<xsl:choose>
								<xsl:when test="string-length(@tuuid) &gt; 0">
									<xsl:value-of select="translate(@tuuid, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
								</xsl:when>
								<xsl:when test="string-length(@normalizedcite) &gt; 0">
									<xsl:value-of select="@normalizedcite"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="generate-id(.)"/>
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
							<xsl:choose>
								<!-- Links from Phase 2 Content Index need their anchors added if they exists -->
								<xsl:when test="string-length(@tanchor) &gt; 0">
									<xsl:copy-of select="concat($persistentUrl, '#', '&internalLinkIdPrefix;', @tanchor)"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$persistentUrl"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:copy-of select="$fullLinkContents"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$fullLinkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Check the language of the document -->
	<xsl:template match="md.references/md.toggle.links/md.toggle.link">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="'&crswLanguageInfo;'"/>
			<xsl:with-param name="class" select="'&hideStateClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;'"/>
		</xsl:call-template>

		<xsl:variable name="documentGuid" select="@tuuid"/>
		<xsl:variable name="documentUrl">
			<xsl:call-template name="GetDocumentUrlToggle">
				<xsl:with-param name="documentGuid" select="$documentGuid"></xsl:with-param>
				<xsl:with-param name="transitionType" select="'&transitionTypeDocumentToggle;'" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="'&crswOtherLanguageDocUrl;'"/>
			<xsl:with-param name="class" select="'&hideStateClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;'"/>
			<xsl:with-param name="contents" select="$documentUrl"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="GetDocumentFindUrl">
		<xsl:param name="pubid" select="@pubid"/>
		<xsl:param name="wlserial" select="@wlserial"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', 'viewType=FullText', 'findType=Y', concat('pubNum=',$pubid), concat('serNum=',$wlserial),'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<xsl:template name="AbridgmentTOCLink">
		<xsl:param name ="topic" /> 
		<xsl:param name ="originalDocumentGuid" select="$Guid" />
		<xsl:param name="isSearchLink" select="false()"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.BrowseAbridgmentTOC', concat('topic=',$topic), concat('docGuid=',$originalDocumentGuid), concat('searchResult=',$isSearchLink), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
	
	<xsl:template name="TOCLink">
		<xsl:param name ="browsePageUrl" /> 
		<xsl:param name ="topic" /> 
		<xsl:param name ="originalDocumentGuid" select="$Guid" />
		<xsl:param name="isSearchLink" select="false()"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.BrowseTOCByTopic',concat('pageUrl=',$browsePageUrl), concat('topic=',$topic), concat('docGuid=',$originalDocumentGuid), concat('searchResult=',$isSearchLink), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
		
	<xsl:template name="SingleSearchDocumentLink">
		<xsl:param name="searchQuery" select="$SearchQuery" /> 
		<xsl:param name="searchWithinQuery" select="$SearchWithinQuery" /> 
		<xsl:param name="documentGuid"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentSearch',concat('documentGuid=',$documentGuid), concat('query=',$searchQuery,' ', $searchWithinQuery), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
	
	<xsl:template name="GetDocumentUrlToggle">
		<xsl:param name ="documentGuid" /> 
		<xsl:param name ="navigationPath" /> 
		<xsl:param name ="list" /> 
		<xsl:param name ="listSource" /> 
		<xsl:param name ="rank" /> 
		<xsl:param name ="transitionType" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$documentGuid), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', concat('&transitionTypeParamName;=', $transitionType), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&navigationPathParamName;=', $navigationPath), concat('&listParamName;=', $list), concat('&listSourceParamName;=', $listSource), concat('&rankParamName;=', $rank))"/>
	</xsl:template>
</xsl:stylesheet>
