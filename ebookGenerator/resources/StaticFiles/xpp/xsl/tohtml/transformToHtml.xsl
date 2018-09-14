<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="../transform-utils.xsl" />
    <xsl:include href="transformText.xsl" />
    <xsl:include href="transformFootnotes.xsl" />
    <xsl:include href="transformTlrKey.xsl" />
    <xsl:include href="transformImagesTags.xsl" />
    <xsl:include href="pocketPartLinksHtml.xsl" />
    
    <xsl:output method="html" indent="no" omit-xml-declaration="yes"/>
	<xsl:param name="fileBaseName" />
	<xsl:param name="divXmlName" />
	<xsl:param name="documentUidMapDoc" />
	<xsl:param name="summaryTocDocumentUidMapDoc" />
	<xsl:param name="isPocketPart" />
	<xsl:param name="entitiesDocType" />
	<xsl:param name="bundleFileType" />
	<xsl:param name="volumesMap" />
	<xsl:param name="currentPartTitle" />
	
	<xsl:variable name="documentUidMap" select="document($documentUidMapDoc)" />
	<xsl:variable name="summaryTocDocumentUidMap" select="document($summaryTocDocumentUidMapDoc)" />
	<xsl:variable name="volumesMapFile" select="document($volumesMap)" />
	<xsl:variable name="isMultiVolume" select="count(distinct-values(($volumesMapFile/x:VolumesMap/x:entry/text()))) > 1" />
	
	<xsl:template match="x:parts">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE root SYSTEM &#34;</xsl:text>
		<xsl:value-of select="$entitiesDocType" />
		<xsl:text disable-output-escaping="yes">&#34;&gt;</xsl:text>
		<html>
			<head>
      			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
      			<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"></meta>
      			<title>Thomson Reuters eBook</title>
      			<link rel="stylesheet" type="text/css" href="er:#document"></link>
      			<xsl:element name="link">
      				<xsl:attribute name="rel">stylesheet</xsl:attribute>
      				<xsl:attribute name="type">text/css</xsl:attribute>
      				<xsl:attribute name="href">er:#<xsl:value-of select="$divXmlName" /></xsl:attribute>
      			</xsl:element>
      			<link rel="stylesheet" type="text/css" href="er:#blackkey"></link>
			</head>
			<xsl:element name="body">
            	<xsl:attribute name="fileBaseName" select="$fileBaseName" />
				<xsl:apply-templates />
            </xsl:element>
		</html>
	</xsl:template>

	<xsl:template match="x:part.main">
		<section>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:call-template name="createPBPI" />
		
		<xsl:variable name="num" select="./@num" />
		<xsl:if test="preceding::x:XPPHier[1]/@no-page-content = 'true' and ancestor::x:part.main and preceding::x:XPPHier[1][following::x:pagebreak[1]/@num = $num]">
			<xsl:apply-templates select="preceding::x:XPPHier[1]" mode="place-anchor" />
		</xsl:if>
		<xsl:if test="preceding::x:XPPMetaData[1]/@no-page-content = 'true' and ancestor::x:part.main and preceding::x:XPPMetaData[1][following::x:pagebreak[1]/@num = $num]">
			<xsl:apply-templates select="preceding::x:XPPMetaData[1]" mode="place-anchor" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="createPBPI">
		<xsl:variable name="apostrophe">'</xsl:variable>
		<xsl:variable name="pagePrefix">
			<xsl:variable name="volumeNumber">
				<xsl:call-template name="getDocumentVolume" />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$isMultiVolume = true() and $isPocketPart = true()">
					<xsl:value-of select="concat('V', $volumeNumber, '-', 'PP', '-')" />
				</xsl:when>
				<xsl:when test="$isMultiVolume = true()">
					<xsl:value-of select="concat('V', $volumeNumber, '-')" />
				</xsl:when>
				<xsl:when test="$isPocketPart = true()">
					<xsl:value-of select="concat('PP', '-')" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="pageNum">
			<xsl:choose>
				<xsl:when test="$bundleFileType = 'TABLE_OF_LRRE' or $bundleFileType = 'TABLE_OF_CASES'">
					<xsl:value-of select="tokenize(./@num, '\s+')[last()]" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="./@num" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:processing-instruction name="pb" select="concat('label', '=', $apostrophe, $pagePrefix, $pageNum, $apostrophe, '?')" />
	</xsl:template>
	
	<xsl:template name="getDocumentVolume">
		<xsl:variable name="pageNum" select="./@num" />
		<xsl:variable name="closestUuid" select="(ancestor::x:parts//x:XPPHier)[last()]/@uuid" />
		<xsl:value-of select="$volumesMapFile/x:VolumesMap/x:entry[@uuid = $closestUuid][1]/text()" />
	</xsl:template>

	<xsl:template match="x:XPPHier | x:XPPMetaData">
		<xsl:if test="not(./@no-page-content)">
			<xsl:apply-templates select="self::node()" mode="place-anchor" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="x:XPPHier | x:XPPMetaData" mode="place-anchor">
		<xsl:variable name="uid">
			<xsl:choose>
				<xsl:when test="@guid">
					<xsl:value-of select="@guid" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@uuid" />
				</xsl:otherwise>
			</xsl:choose>	
		</xsl:variable>
		
		<xsl:element name="a">
			<xsl:attribute name="name" select="$uid" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="element()">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:value-of select="x:get-class-name(name(.))" />
					<xsl:if test=".[@align]">
						<xsl:value-of select="concat(' ', x:get-class-name(@align))" />
					</xsl:if>
					<xsl:if test="@style">
						<xsl:value-of select="concat(' font_', x:get-class-name(@style))" />
					</xsl:if>
					<xsl:if test="self::x:fm.image.wrap/@pre.leading or self::x:para/@pre.leading or self::x:text.line/@pre.leading">
						<xsl:value-of select="concat(' pre_leading_', x:get-class-name(@pre.leading))" />
					</xsl:if>
					<xsl:if test="self::x:para/@left.indent or self::x:text.line/@left.indent">
						<xsl:value-of select="concat(' left_indent_', x:get-class-name(@left.indent))" />
					</xsl:if>
					<xsl:if test="self::x:para/@right.indent or self::x:text.line/@right.indent">
						<xsl:value-of select="concat(' right_indent_', x:get-class-name(@right.indent))" />
					</xsl:if>
					<xsl:if test="self::x:fm.image.wrap/@quad">
						<xsl:value-of select="concat(' image_wrapper_', x:get-class-name(@quad))" />
					</xsl:if>
			</xsl:attribute>
			<xsl:copy-of select="./@uuid | ./@tocuuid" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:ref">
		<xsl:element name="{name()}">
			<xsl:for-each select="@*[not(name()='reftext')]">
				<xsl:attribute name="{name()}">
					<xsl:value-of select="x:replace-apos(self::node())" />
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:function name="x:replace-apos">
		<xsl:param name="str" />
		<xsl:variable name="apos">'</xsl:variable>
		<xsl:value-of select="replace($str, $apos, '&amp;apos;')" />
	</xsl:function>

	<xsl:template match="x:ital|x:bold">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="x:t">
        <xsl:variable name="footnoteRefId" select="x:footnote-reference-id(.)" />

        <xsl:choose>
            <xsl:when test="$footnoteRefId != ''">
                <xsl:call-template name="addFootnoteReference">
                    <xsl:with-param name="refId" select="$footnoteRefId" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="convertToSpan" />
            </xsl:otherwise>
        </xsl:choose>
	</xsl:template>

	<xsl:template match="x:XPPLink|x:XPPTOCLink">
		<xsl:variable name="uid">
			<xsl:choose>
				<xsl:when test="not(./@uuid)">
					<xsl:value-of select="./@guid" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="./@uuid" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="docId" select="($documentUidMap/x:uuidmap/x:item[@key = $uid and @type = 'book'])[1]" />
		<xsl:variable name="linkedTitle" select="($documentUidMap/x:uuidmap/x:item[@key = $uid and @type = 'book'])[1]/@splitTitleId" />
		<xsl:variable name="prefix">
			<xsl:choose>			
				<xsl:when test="$linkedTitle and $linkedTitle != $currentPartTitle">
					<xsl:value-of select="concat('er:', $linkedTitle, '#')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'er:#'" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$docId">
				<xsl:element name="a">
					<xsl:attribute name="href" select="concat($prefix, $docId, '/', $uid)" />
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="x:XPPSummaryTOCLink">
		<xsl:variable name="docId" select="x:getDocumentId(current()/@uuid)" />

		<xsl:choose>
			<xsl:when test="$docId and not($docId = '') and not($docId = '_pp')">
				<xsl:element name="a">
					<xsl:attribute name="href" select="concat('er:#', $docId, '/sumtoc-', @uuid)" />
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:function name="x:getDocumentId">
		<xsl:param name="uuid" />
		<xsl:variable name="processedUuid">
			<xsl:value-of select="x:process-id($uuid, $isPocketPart)" />
		</xsl:variable>
		<xsl:variable name="docIdFromSummaryTocMap"
			select="$summaryTocDocumentUidMap/x:uuidmap/x:item[@key = $processedUuid]" />
		<xsl:choose>
			<xsl:when test="$docIdFromSummaryTocMap">
				<xsl:value-of select="$docIdFromSummaryTocMap" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="$documentUidMap/x:uuidmap/x:item[@key = $processedUuid and @type = 'sumtoc'][1]" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:template match="x:XPPSummaryTOCAnchor">
		<xsl:element name="a">
			<xsl:attribute name="name" select="concat('sumtoc-', @uuid)" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:cite.query">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

    <xsl:template match="x:tbl">
        <xsl:variable name="pageOrScrollMode">
            <xsl:if test="@mode != ''">
                <xsl:value-of select="@mode" />
            </xsl:if>
        </xsl:variable>
        
        <xsl:variable name="tblClass" select="string-join(('tbl', $pageOrScrollMode)[. != ''],' ')" />
            
        <xsl:element name="div">
            <xsl:attribute name="class" select="$tblClass" />
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:table">
        <xsl:variable name="trTableClass">
            <xsl:if test="number(@cols) > 3 and @tgroupstyle = 'text'">
                <xsl:value-of select="'tr_table'" />
            </xsl:if>
        </xsl:variable>
        
        <xsl:variable name="tableFrameClass">
            <xsl:if test="@frame != ''">
                <xsl:value-of select="concat('table_frame_', @frame)" />
            </xsl:if>
        </xsl:variable>
        
        <xsl:variable name="tableClass" select="string-join(($trTableClass, $tableFrameClass)[. != ''],' ')" />
        
        <xsl:element name="table">
            <xsl:if test="$tableClass != ''">
                <xsl:attribute name="class" select="$tableClass" />
            </xsl:if>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:col">
        <xsl:element name="col">
            <xsl:attribute name="style" select="concat('width:', @width)" />
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:thead | x:tbody | x:colgroup">
        <xsl:element name="{name()}">
            <xsl:copy-of select="@*" />
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

	<xsl:template match="x:row">
		<xsl:element name="tr">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:row/x:entry">
		<xsl:element name="td">
			<xsl:if test="@align">
				<xsl:attribute name="class" select="x:getAlignClass(@align)" />
			</xsl:if>
			<xsl:if test="@colspan">
				<xsl:attribute name="colspan" select="@colspan" />
			</xsl:if>
			<xsl:if test="@rowspan">
				<xsl:attribute name="rowspan" select="@rowspan" />
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:function name="x:getAlignClass">
		<xsl:param name="align"/>
		
		<xsl:if test="$align='left'">
			<xsl:value-of select="'l'"/>
		</xsl:if>
		<xsl:if test="$align='center'">
			<xsl:value-of select="'c'"/>
		</xsl:if>
		<xsl:if test="$align='right'">
			<xsl:value-of select="'r'"/>
		</xsl:if>
	</xsl:function>

	<xsl:template match="x:page.number.ref">
		<xsl:variable name="pgNum" select="./@page-number" />

		<xsl:if test="following::x:page.number[@page-num = $pgNum] and ancestor::x:part.main">
			<xsl:call-template name="addReferenceTag">
				<xsl:with-param name="refId" select="concat('pn.', $pgNum)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
