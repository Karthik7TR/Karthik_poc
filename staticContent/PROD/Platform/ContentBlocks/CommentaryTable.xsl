<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:include href="Table.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:key name="Tables" match="tbl[(@id or @ID) and .//*[@rowsep = '1' or @colsep = '1' or (string-length(@frame) > 0 and not(@frame = 'none'))]]" use="@id | @ID"/>

	<xsl:template match="tbl">
		<xsl:if test=".//text()">
			<div>
				<xsl:if test="@id or @ID">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', @id | @ID)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="($IsCommentaryEnhancementMode) and key('Tables', @id | @ID)">
					<xsl:attribute name="class">
						<xsl:text>&fullscreenTableClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-link-text">
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&tableFullscreenModeLabelKey;', '&tableFullscreenModeLabel;')"/>
					</xsl:attribute>
					<xsl:attribute name="data-link-href">
						<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentTable', concat('guid=', $Guid), concat('tableId=', @id | @ID), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.Default)')"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="tbl[@id or @ID]/table[descendant-or-self::*[@rowsep = '1' or @colsep = '1' or (string-length(@frame) > 0 and not(@frame = 'none'))]]/tgroup" priority="1" >
		<xsl:call-template name="TGroupTemplate">
			<xsl:with-param name="additionalClass">
				<xsl:if test="($IsCommentaryEnhancementMode)">
					<xsl:value-of select="'&borderedTableClass;'"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="row">
		<xsl:param name="columnInfo" />
		<xsl:param name="header"/>
		<xsl:param name="proportionalTotal"/>
		<xsl:choose>
			<xsl:when test="($IsCommentaryEnhancementMode) and key('Tables', ancestor::tbl[1]/@id | ancestor::tbl[1]/@ID)">
				<xsl:if test="string-length(normalize-space(translate(., '&specialCharactersToBeReplaced;', '&blankSpaceCharacters;'))) &gt; 0">
					<xsl:call-template name="rowCore">
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="header" select="$header"/>
						<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="rowCore">
					<xsl:with-param name="columnInfo" select="$columnInfo"/>
					<xsl:with-param name="header" select="$header"/>
					<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="AddTableCellBorderClasses">
		<xsl:param name="row" />
		<xsl:param name="colposition"/>
		<!--Border between table rows-->
		<xsl:if test="@rowsep = '1' or parent::row/@rowsep = '1' or ($row and $row/@rowsep = '1') or ancestor::table[1]/@rowsep = '1'">
			<xsl:text>&borderBottomClass;</xsl:text>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<!--Border between table columns-->
		<xsl:if test="@colsep = '1' or parent::row/@colsep = '1' or ($row and $row/@colsep = '1') or ancestor::table[1]/@colsep = '1'">
			<xsl:text>&borderRightClass;</xsl:text>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BeforeRenderTableRow" priority="1">
		<xsl:if test="parent::thead">
			<xsl:processing-instruction name="startChunkCopyingBlock" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="AfterRenderTableRow" priority="1">
		<xsl:if test="parent::thead">
			<xsl:processing-instruction name="endChunkCopyingBlock" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
