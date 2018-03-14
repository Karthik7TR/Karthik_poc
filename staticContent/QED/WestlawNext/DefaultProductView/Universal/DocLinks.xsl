<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <!-- Include the platform FO transform -->
  <xsl:import href="DocLinks.xsl" forcePlatform="true"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>


  <xsl:template match="md.toggle.link" name="wlnToggleLink">
    <xsl:choose>
      <xsl:when test="$OutOfPlanInlinePreviewMode and $PreviewMode">
        <span>
          <xsl:attribute name="class">
            <xsl:text>co_disabled</xsl:text>
          </xsl:attribute>
          <xsl:value-of select="text()"/>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="toggleLink" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="citataion.Metadata" name="citeMetadata">
    <xsl:choose>
      <xsl:when test="$ShowStateSurveyInlineKCFlag">
        <a>
          <xsl:choose>
            <xsl:when test="@flagColor = 'R'">
              <xsl:attribute name="class">co_rFlagSm</xsl:attribute>
            </xsl:when>
            <xsl:when test="@flagColor = 'Y'">
              <xsl:attribute name="class">co_yFlagSm</xsl:attribute>
            </xsl:when>
          </xsl:choose>
          <xsl:attribute name="href">
            <xsl:value-of select="@targetDocumentUrl"/>
          </xsl:attribute>
          <img>
            <xsl:attribute name="src">
              <xsl:value-of select="concat($Images, @flagImageUrl)" />
            </xsl:attribute>
          </img>
        </a>
        <xsl:text><![CDATA[ ]]></xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

	<xsl:template match="entity.link" name="medicalEntityLinkOut">
		<xsl:variable name="entityLinkElementId" select="@entity.id" />
		<xsl:variable name="entityLinkElementType" select="@etype" />
		<xsl:variable name="entityLinkHref" select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', 'viewType=FullText', concat('entityType=',$entityLinkElementType), concat('entityId=', $entityLinkElementId), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', 'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)" />
		<a>
			<xsl:if test="string-length($entityLinkHref) &gt; 0">
				<xsl:attribute name="href">
					<xsl:value-of select="$entityLinkHref"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="class">co_link co_drag ui-draggable</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('co_link_', @entity.id)"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="inlineKeyCiteFlag" name="inlineKeyCiteFlag">
		<xsl:variable name="citeQueryElement" select="./following-sibling::cite.query[1]" />
		<xsl:if test="@flagImageUrl">
			<span>
				<xsl:attribute name="class">
					<xsl:text>&inlineKeyCiteFlagClass;</xsl:text>
				</xsl:attribute>

				<xsl:if test="@guid">
					<xsl:attribute name="guid">
						<xsl:value-of select="@guid"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="@citeText">
					<xsl:attribute name="data-cite-text">
						<xsl:value-of select="@citeText"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="@cite">
					<xsl:attribute name="data-cite">
						<xsl:value-of select="@cite"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="@suppressFlagLink">
					<xsl:attribute name="data-suppress-flag-link">
						<xsl:value-of select="@suppressFlagLink"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="@pubNum">
					<xsl:attribute name="data-pub-num">
						<xsl:value-of select="@pubNum"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="@serialNum">
					<xsl:attribute name="data-serial-num">
						<xsl:value-of select="@serialNum"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="@isIllegalLink">
					<xsl:attribute name="data-is-illegal-link">
						<xsl:value-of select="@isIllegalLink"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:attribute name="data-is-unique">
					<xsl:value-of select="@isUnique"/>
				</xsl:attribute>

				<xsl:choose>
					<xsl:when test="@isUnique='True'">
						<xsl:choose>
							<xsl:when test="@suppressFlagLink='True'">
								<img>
									<xsl:attribute name="src">
										<xsl:value-of select="concat($Images, @flagImageUrl)" />
									</xsl:attribute>
								</img>
							</xsl:when>
							<xsl:otherwise>
								<a>
									<xsl:attribute name="class">
										<xsl:text>&inlineKeyCiteFlagLinkClass;</xsl:text>
									</xsl:attribute>

									<xsl:if test="@targetDocumentUrl">
										<xsl:attribute name="href">
											<xsl:value-of select="@targetDocumentUrl"/>
										</xsl:attribute>
									</xsl:if>
										
									<img>
										<xsl:attribute name="src">
											<xsl:value-of select="concat($Images, @flagImageUrl)" />
										</xsl:attribute>
									</img>
								</a>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="sourceCite">
							<xsl:call-template name="SpecialCharacterTranslator">
								<xsl:with-param name="textToTranslate" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite[not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite)]" />
							</xsl:call-template>
						</xsl:variable>
						
						<a>
							<xsl:attribute name="class">
								<xsl:text>&inlineKeyCiteFlagLinkClass;</xsl:text>
							</xsl:attribute>

							<xsl:attribute name="href">
								<xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentItem;')"/>
							</xsl:attribute>

							<img>
								<xsl:attribute name="src">
									<xsl:value-of select="concat($Images, @flagImageUrl)" />
								</xsl:attribute>
							</img>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</span>
		</xsl:if>
		<xsl:if test="@isIllegalLink='True'">
			<span>
				<xsl:attribute name="class">
					<xsl:text>&inlineKeyCiteFlagIllegalLinkRemovedClass;</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&inlineKeyCiteFlagIllegalLinkRemovedId;', ./following-sibling::cite.query[1]/@ID)"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="./following-sibling::cite.query[1][charfill]">
						<xsl:apply-templates select="./following-sibling::cite.query[1]/charfill/preceding-sibling::node()" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="./following-sibling::cite.query[1]/node()" />
					</xsl:otherwise>
				</xsl:choose>
			</span>
		</xsl:if>
	</xsl:template>
  
	<xsl:template name="BatchPdfHrefWithCaseNumber">
		<xsl:param name="pdfIndex" />
    <xsl:param name="court" />
    <xsl:param name="caseNumber" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketsPdfBatchDownload', concat('pdfIndex=', $pdfIndex), concat('court=', $court), concat('caseNumber=', $caseNumber), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
	
	<xsl:template name="SendRunnerHrefWithCaseNumber">
		<xsl:param name="orderedIndex" />
    <xsl:param name="court" />
    <xsl:param name="caseNumber" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentOrderReview', concat('orderedIndex=', $orderedIndex), concat('court=', $court), concat('caseNumber=', $caseNumber), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
  
	<xsl:template name="createGatewayDocketsCreditorMashupLink">
		<xsl:variable name="docGuid" select="$Guid" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketCreditorNew', concat('docguid=', $docGuid), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocument;' )" />
	</xsl:template>

  <xsl:template name="createDocketsTrackMashupLinkWLN">
		<xsl:variable name="guid" select="$Guid" />
		<xsl:variable name="docketCaseNumber">
			<xsl:call-template name="getDocketCaseNumberWLN"/>
		</xsl:variable>
		<xsl:variable name="docketCounty">
			<xsl:call-template name="getDocketCountyWLN"/>
		</xsl:variable>
    <xsl:variable name="docketCourtNorm">
      <xsl:call-template name="getDocketCourtNorm" />
    </xsl:variable>
		<xsl:variable name="courtType">
			<xsl:choose>
        <xsl:when test="$DocketGatewayAlertInfo">
          <xsl:value-of select="$DocketGatewayAlertInfo"/>
				</xsl:when>
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
        <xsl:when test="$DocketGatewaySignon and $DocketGatewayAlertInfo">
					<xsl:value-of select="$DocketGatewaySignon"/>
				</xsl:when>
        <xsl:when test="$docketCourtNorm">
          <xsl:value-of select="concat('&trackJurisdictionPrefix;', $docketCourtNorm)"/>
        </xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="alertInfo">
			<xsl:value-of select="//alert.info"/>
		</xsl:variable>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketTrack', concat('&guidParamName;=', $guid), concat('&dnumParamName;=', $docketCaseNumber), concat('&actionParamName;=', '&actionCreate;'), concat('&ctypeParamName;=', $courtType), concat('&docketGatewayCaseType;=', $DocketGatewayCaseType), concat('&jurisParamName;=', $jurisdiction), concat('&countyParamName;=', $docketCounty), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocument;', concat('&alertInfoParamName;=', $alertInfo) )" />
	</xsl:template>
	
	<xsl:template name="getDocketCaseNumberWLN">
		<xsl:variable name="docketCounty">
			<xsl:call-template name="getDocketCountyWLN"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="//state.postal='MO' or //md.jurisstate='MO'">
				<xsl:value-of select="//docket.block/docket.number"/>
			</xsl:when>
			<xsl:when test="(//update.link.block/link.parameter[parameter.name='CN']/parameter.value) and starts-with(//update.link.block/link.parameter[parameter.name='CN']/parameter.value,$docketCounty)">
				<xsl:value-of select="//update.link.block/link.parameter[parameter.name='CN']/parameter.value"/>
			</xsl:when>
			<xsl:when test="(/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum) and starts-with(/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum,$docketCounty)">
				<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum"/>
			</xsl:when>
			<xsl:when test="(//docket.block/docket.number.dc) and starts-with(//docket.block/docket.number.dc,$docketCounty)">
				<xsl:value-of select="//docket.block/docket.number.dc"/>
			</xsl:when>
			<xsl:when test="//update.link.block/link.parameter[parameter.name='CN']/parameter.value">
				<xsl:value-of select="//update.link.block/link.parameter[parameter.name='CN']/parameter.value"/>
			</xsl:when>
			<xsl:when test="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum">
				<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.docketnum"/>
			</xsl:when>
			<xsl:when test="//docket.block/docket.number.dc">
				<xsl:value-of select="//docket.block/docket.number.dc"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="//docket.block/docket.number" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getDocketCountyWLN">
		<xsl:choose>
			<xsl:when test="//update.link.block/link.parameter[parameter.name='CNTY']/parameter.value">
				<xsl:value-of select="//update.link.block/link.parameter[parameter.name='CNTY']/parameter.value"/>
			</xsl:when>
      <xsl:when test="//court.block/filing.county">
        <xsl:value-of select="//court.block/filing.county"/>
      </xsl:when>
		</xsl:choose>
	</xsl:template>
	
  <xsl:template name="createDocketBlobLinkWithCourtNorm">
		<xsl:param name="court" />
		<xsl:param name="courtNumber" />
		<xsl:param name="casenumber" />
		<xsl:param name="courtNorm" />
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
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('id=', translate(substring-before($id, ';'), '|', ',')), concat('filename=', $filename), concat('courtnumber=', $courtNumber), concat('courtNorm=', $courtNorm), concat('casenumber=', $casenumber), concat('originationContext=', $originationContext), concat('extension=', $extension), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&docketLocalImageGuid;=', $guid))"/>
	</xsl:template>

	<xsl:template name="createGatewayBlobHrefWithCourtNorm">
		<xsl:param name="court" />
		<xsl:param name="courtNumber" />
		<xsl:param name="courtNorm" />
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
		<!-- need to translate pipes to commas in the id -->
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('id=', translate(substring-before($id, ';'), '|', ',')), concat('filename=', $filename), concat('courtnumber=', $courtNumber), concat('courtNorm=', $courtNorm), concat('casenumber=', $casenumber), concat('extension=', $extension), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', 'contextData=(sc.Default)', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
	</xsl:template>
  
  <xsl:template name="createPreDocketComplaintLink">
    <xsl:param name="guid"/>
    <xsl:param name="text"/>
    <xsl:param name="className" />
    <xsl:param name="displayIcon"/>
    <xsl:param name="displayIconClassName"/>
    <xsl:param name="displayIconAltText"/>
    <xsl:param name="targetType" />
    <xsl:param name="countImage" />
    <xsl:variable name="imageText">
      <xsl:choose>
        <xsl:when test="$text">
          <xsl:value-of select="$text"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'Original Image of this Document (PDF)'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:call-template name="createPredocketDocumentBlobLink">
      <xsl:with-param name="guid" select="$guid"/>
      <xsl:with-param name="className" select="$className" />
      <xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
      <xsl:with-param name="targetType" select="$targetType" />
      <xsl:with-param name="contents" select="$imageText"/>
      <xsl:with-param name="displayIcon" select="$displayIcon"/>
      <xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
      <xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
      <xsl:with-param name="prettyName" select="'&predocketComplaintFile;'"/> <!--Base on the pretty name to show a different error message when document is not available -->
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="createPredocketDocumentBlobLink">
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
			<a href="{$blobHref}">
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

  <xsl:template name="GatewayBatchPdfHref">
    <xsl:param name="documentGuid" />
		<xsl:param name="pdfIndex" />
    <xsl:param name ="linkParams" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.GatewayDocketsPdfBatchDownload', 
                  concat('documentGuid=', $documentGuid), concat('pdfIndex=', $pdfIndex), concat('linkParams=', $linkParams), 
                  'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 
                  'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
	
	<xsl:template name="GatewaySendRunnerHref">
    <xsl:param name="documentGuid" />
		<xsl:param name="orderedIndex" />
    <xsl:param name ="linkParams" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.GatewayDocumentOrderReview', 
                  concat('documentGuid=', $documentGuid), concat('orderedIndex=', $orderedIndex), concat('linkParams=', $linkParams),
                  'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 
                  'contextData=(sc.Default)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>
  
	<xsl:template name="createGatewayBlobHref">
		<!-- ==> this is where we create gateway PDF link -->
		<xsl:param name="county" />
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
		
		<!-- Replaces %'s with !JPML for jpml docket content -->
		<xsl:variable name="newId">
			<xsl:choose>
				<xsl:when test="contains($court, 'JPML')">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="$id" />
						<xsl:with-param name="pattern" select="'%'" />
						<xsl:with-param name="replacement" select="'!JPML'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$id" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="url">
			<xsl:call-template name="getGatewayBlobUrl">
				<xsl:with-param name="county" select="$county" />
				<xsl:with-param name="court" select="$court" />
				<xsl:with-param name="courtNumber" select="$courtNumber" />
				<xsl:with-param name="casenumber" select="$casenumber" />
				<xsl:with-param name="id" select="$id" />
				<xsl:with-param name="filename" select="$filename" />
				<xsl:with-param name="platform" select="$platform" />
				<xsl:with-param name="mimeType" select="$mimeType" />
				<xsl:with-param name="originationContext" select="$originationContext" />
				<xsl:with-param name="extension" select="$extension" />
				<xsl:with-param name="newId" select="$newId" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="$url"/>
	</xsl:template>

	<xsl:template name="getGatewayBlobUrl">
		<xsl:param name="county" />
		<xsl:param name="court" />
		<xsl:param name="courtNumber" />
		<xsl:param name="casenumber" />
		<xsl:param name="id" />
		<xsl:param name="filename" />
		<xsl:param name="platform" />
		<xsl:param name="mimeType" />
		<xsl:param name="originationContext" />
		<xsl:param name="extension" />
		<xsl:param name="newId" />
		<xsl:variable name="courtNorm">
			<xsl:call-template name="getDocketCourtNorm" />
		</xsl:variable>
		<!-- need to translate pipes to commas in the id -->
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('courtNorm=', $courtNorm), concat('id=', translate(substring-before($newId, ';'), '|', ',')), concat('filename=', $filename), concat('county=', $county), concat('courtnumber=', $courtNumber), concat('casenumber=', $casenumber), concat('extension=', $extension), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', 'contextData=(sc.Default)', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
	</xsl:template>

	<xsl:template name="GetSearchResultUrlByDatabaseID">
		<xsl:param name ="query" />
		<xsl:param name ="databaseID" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.SearchResults', concat('query=',$query), concat('databaseID=',$databaseID), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocumentItem;')"/>
	</xsl:template>

	<xsl:template match="search.link">
		<xsl:choose>
			<xsl:when test="@search-text and @db-signon">
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="GetSearchResultUrlByDatabaseID">
							<xsl:with-param name="query" select="@search-text" />
							<xsl:with-param name="databaseID" select="@db-signon" />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:apply-templates />
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="displayCiteQueryLink">
		<xsl:param name ="persistentUrl" />
		<xsl:param name="citeQueryElement" />
		<xsl:param name="fullLinkContents" />
		<xsl:param name="isDraggable" />

		<xsl:choose>
			<xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
				<xsl:call-template name ="buildCiteQueryLink">
					<xsl:with-param name="persistentUrl" select="$persistentUrl"/>
					<xsl:with-param name="citeQueryElement" select="$citeQueryElement"/>
					<xsl:with-param name="fullLinkContents" select="$fullLinkContents"/>
					<xsl:with-param name="isDraggable" select="$isDraggable"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$fullLinkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="buildCiteQueryLink">
		<xsl:param name ="persistentUrl" />
		<xsl:param name="citeQueryElement" />
		<xsl:param name="fullLinkContents" />
		<xsl:param name="isDraggable" />

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
	</xsl:template>

</xsl:stylesheet>
