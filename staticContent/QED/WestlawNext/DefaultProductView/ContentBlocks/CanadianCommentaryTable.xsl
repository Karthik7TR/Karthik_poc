<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CommentaryTable.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="tbl" priority="1">
		<xsl:if test=".//text()">
			<div>
				<xsl:if test="@id or @ID">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', @id | @ID)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$IsCommentaryEnhancementMode and (@id or @ID) and .//*[self::thead or @rowsep = '1' or @colsep = '1' or (string-length(@frame) > 0 and not(@frame = 'none'))]">
					<xsl:attribute name="class">
						<xsl:text>&fullscreenTableClass;</xsl:text>
					</xsl:attribute>
					<xsl:if test="not(ancestor::classification.block)">
						<xsl:attribute name="data-link-text">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&tableFullscreenModeLabelKey;', '&tableFullscreenModeLabel;')"/>
						</xsl:attribute>
						<xsl:attribute name="data-link-href">
							<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentTable', concat('guid=', $Guid), concat('tableId=', @id | @ID), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)')"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="tbl[@id or @ID]/table[descendant-or-self::*[self::thead or @rowsep = '1' or @colsep = '1' or (string-length(@frame) > 0 and not(@frame = 'none'))]]/tgroup" priority="1" >
		<xsl:call-template name="TGroupTemplate">
			<xsl:with-param name="additionalClass">
				<xsl:if test="($IsCommentaryEnhancementMode)">
					<xsl:value-of select="'&borderedTableClass;'"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
