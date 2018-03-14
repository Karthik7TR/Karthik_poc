<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Image.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="ImageMetaData" select="//ImageMetadata/n-metadata"/>

	<xsl:variable name="documentType" select="/Document/document-data/doc-type"/>

	<xsl:template match="image.block" priority="1">
		<div class="&paraMainClass;">
			<xsl:variable name="imgMimeType">
				<xsl:variable name="imgGuid" select="document.image.link/@tuuid"/>

				<xsl:choose>
					<xsl:when test="$ImageMetaData[@guid = $imgGuid]//md.image.format/text() = '&crswMSWordMimeType;'">
						<xsl:value-of select="'&crswRtfMimeType;'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$ImageMetaData[@guid = $imgGuid]//md.image.format/text()"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="isRtfForm" select="$imgMimeType = '&crswRtfMimeType;'"/>

			<xsl:variable name="imgText">
				<xsl:choose>
					<xsl:when test="contains(image.keywords, 'Original')">
						<xsl:value-of select="'&originalDocumentLinkText;'"/>
						<xsl:text><![CDATA[  ]]></xsl:text>
						<xsl:value-of select="$Cite"/>
						<xsl:text><![CDATA[  ]]></xsl:text>
						<xsl:value-of select="'&pdfLabel;'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="$DeliveryMode">
							<xsl:text><![CDATA[  ]]> </xsl:text>
						</xsl:if>
						<xsl:value-of select="image.text"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:choose>
				<xsl:when test="document.image.link">
					<xsl:apply-templates select="document.image.link">
						<xsl:with-param name="displayIcon">
							<xsl:call-template name="GetDocumentIcon">
								<xsl:with-param select="$isRtfForm" name="IsRtf"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="text" select="$imgText"/>
						<xsl:with-param name="mimeType" select="$imgMimeType"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="image.block" priority="1" mode="ToolBarButton">

		<xsl:variable name="imgMimeType">
			<xsl:variable name="imgGuid" select="document.image.link/@tuuid"/>
			<xsl:choose>
				<xsl:when test="$ImageMetaData[@guid = $imgGuid]//md.image.format/text() = '&crswMSWordMimeType;'">
					<xsl:value-of select="'&crswRtfMimeType;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$ImageMetaData[@guid = $imgGuid]//md.image.format/text()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="isRtfForm" select="$imgMimeType = '&crswRtfMimeType;'"/>

		<div>
			<xsl:attribute name="id">
				<xsl:text>&crswOriginalPdfDownloadInfo;</xsl:text>
			</xsl:attribute>
			<xsl:if test="not($DeliveryMode)">
				<xsl:attribute name="class">
					<xsl:text>&hideStateClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;</xsl:text>
				</xsl:attribute>
			</xsl:if>

			<!-- For Caselaw & Case view documents, get the text of the image -->
			<xsl:variable name="imageText" select="image.text/text()"/>

			<xsl:apply-templates select="document.image.link" mode="ToolBarButton">
				<xsl:with-param name="buttonText">
					<xsl:call-template name="ToolbarButtonText">
						<xsl:with-param name="keyword" select="image.keywords"/>
					</xsl:call-template>
				</xsl:with-param>

				<xsl:with-param name="text">
					<xsl:call-template name="ToolbarButtonHoverText" >
						<xsl:with-param name="keyword" select="image.keywords"/>
					</xsl:call-template>
				</xsl:with-param>

				<xsl:with-param name="displayIcon">
					<xsl:call-template name="GetDocumentIcon">
						<xsl:with-param name="IsRtf" select="$isRtfForm"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<!-- This element seems to only be in GAO -->
	<xsl:template match="document.image.link">
		<xsl:param name="text"/>

		<xsl:variable name="guid" select="@tuuid" />
		<xsl:variable name="imageMetaData" select="/*/ImageMetadata/n-metadata[@guid=$guid]" />

		<div class="&imageBlockClass;">
			<xsl:choose>
				<xsl:when test="$imageMetaData/md.image.renderType = 'BlobLink'">
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="@tuuid"/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="$imageMetaData/md.block/md.image.format"/>
						<xsl:with-param name="contents">
							<xsl:value-of select="$text"/>
						</xsl:with-param>
						 <!--These are generic, might be better to create mimeType specific icons/classes/alt text--> 
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
							<xsl:value-of select="$text"/>
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
							<xsl:value-of select="$text"/>
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

	<!--Carswell PDF link to orginal document-->
	<xsl:template match="document.image.link" priority="1" mode="ToolBarButton">
		<xsl:param name="buttonText"/>
		<xsl:param name="text"/>
		<xsl:param name="displayIcon"/>

		<xsl:variable name="guid">
			<xsl:choose>
				<xsl:when test="string-length(@tuuid) &gt; 30">
					<xsl:value-of select="@tuuid" />
				</xsl:when>
				<xsl:when test="string-length(@target) &gt; 30">
					<xsl:value-of select="@target" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>NotValidImage</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($guid) &gt; 0">
			<xsl:if test="/Document/ImageMetadata/n-metadata/@guid = $guid">
				<xsl:apply-templates select="/Document/ImageMetadata/n-metadata[@guid = $guid]" mode="MakeToolbarPdfDocumentLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="buttonText" select="$buttonText"/>
					<xsl:with-param name="text" select="$text"/>
					<xsl:with-param name="displayIcon" select="$displayIcon"/>
				</xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/Document/ImageMetadata/n-metadata" mode="MakeToolbarPdfDocumentLink">
		<xsl:param name="guid" />
		<xsl:param name="buttonText"/>
		<xsl:param name="text"/>
		<xsl:param name="displayIcon"/>

		<xsl:variable name="targetType" select="@ttype" />
		<xsl:variable name="imageFormat" select=".//md.image.format | .//obj.mimetype" />
		<xsl:variable name="mimeType">
			<xsl:choose>
				<xsl:when test="$targetType = 'NRS' or $targetType = 'Briefs' or $targetType = 'TrialDocs'">
					<xsl:value-of select="'&pdfMimeType;'" />
				</xsl:when>
				<xsl:when test="$imageFormat = '&pdfMimeType;' or $imageFormat = '&smartLabelMimeType;'">
					<xsl:value-of select="$imageFormat" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&jpgMimeType;'" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="blobHref">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="$guid"/>
				<xsl:with-param name="targetType" select="$targetType"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			</xsl:call-template>
		</xsl:variable>

		<a target="_blank" class="&imageLinkClass;" href="{$blobHref}" type="{$mimeType}" title="{$text}">
			<img src="{$Images}{$displayIcon}" height="16" width="16"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:value-of select="$buttonText"/>
		</a>
	</xsl:template>

	<xsl:template name="ToolbarButtonText">
		<xsl:param name="keyword"/>

		<xsl:choose>
			<!--Law Report -->
			<xsl:when test="$keyword = '&crswLawReportPdf;'">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswLawReportPdfKey;', '&crswLawReportPdf;')"/>
			</xsl:when>

			<!--Original -->
			<xsl:otherwise>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswOriginalPdfKey;', '&crswOriginalPdf;')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="ToolbarButtonHoverText">
		<xsl:param name="keyword"/>

		<xsl:choose>
			<!--Law Report -->
			<xsl:when test="$keyword = '&crswLawReportPdf;'">
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswLawReportPdfHoverTextKey;', '&crswLawReportPdfHoverText;')"/>
			</xsl:when>
			<!--Original -->
			<xsl:otherwise>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswOriginalPdfHoverTextKey;', '&crswOriginalPdfHoverText;')"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<!-- handle inline keycite flag images -->
  <xsl:template name="CreateKeyCiteInlineImage">
    <xsl:param name="icon"/>
    <xsl:param name="class"/>
    <xsl:param name="altText"/>
    
    <xsl:call-template name="buildBlobImageElement">
      <xsl:with-param name="displayIcon" select="$icon"/>
      <xsl:with-param name="displayIconAltText" select="$altText"/>
      <xsl:with-param name="displayIconClassName" select="$class"/>
    </xsl:call-template>
  </xsl:template>
	
	<xsl:template name="GetDocumentIcon">
		<xsl:param name="IsRtf"/>
		<xsl:choose>
			<xsl:when test="$IsRtf">
				<xsl:value-of select="'&documentIconPath;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&pdfIconPath;'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
