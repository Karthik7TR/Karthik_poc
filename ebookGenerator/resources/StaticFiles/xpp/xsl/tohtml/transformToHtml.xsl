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
	<xsl:param name="pagePrefix" />
	<xsl:param name="divXmlName" />
	<xsl:param name="documentUidMapDoc" />
	<xsl:param name="summaryTocDocumentUidMapDoc" />
	<xsl:param name="isPocketPart" />
	<xsl:param name="entitiesDocType" />
	<xsl:param name="bundleFileType" />
	
	<xsl:variable name="documentUidMap" select="document($documentUidMapDoc)" />
	<xsl:variable name="summaryTocDocumentUidMap" select="document($summaryTocDocumentUidMapDoc)" />

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
		<xsl:variable name="apostrophe">'</xsl:variable>
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

	<xsl:template match="x:XPPHier | x:XPPMetaData">
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
					<xsl:if test="@pre.leading">
						<xsl:value-of select="concat(' pre_leading_', x:get-class-name(@pre.leading))" />
					</xsl:if>
			</xsl:attribute>
			<xsl:copy-of select="./@uuid | ./@tocuuid" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:ref">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*[not(name()='reftext')]" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

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
		<xsl:variable name="docId" select="$documentUidMap/x:uuidmap/x:item[@key = $uid and @type = 'book']" />
		<xsl:choose>
			<xsl:when test="$docId">
				<xsl:element name="a">
					<xsl:attribute name="href" select="concat('er:#', $docId, '/', $uid)" />
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
<!--    TODO: move to img step -->
<!-- 	<xsl:template match="x:image.block"> -->
<!-- 		<xsl:param name="quote">"</xsl:param> -->
<!-- 		<xsl:param name="ident">ident="</xsl:param> -->
<!-- 		<xsl:variable name='guid' select='substring-before(substring-after(self::node(),$ident),".")' /> -->
<!-- 		<xsl:element name="img"> -->
<!-- 			<xsl:attribute name="class" select="'tr_image'" /> -->
<!-- 			<xsl:attribute name="assetid" select="concat('er:#', $guid)" /> -->
<!-- 		</xsl:element> -->
<!-- 	</xsl:template> -->

	<xsl:template match="x:table">
		<xsl:if test="not(./@cont)">
		
		</xsl:if>
		<xsl:element name="table">
			<xsl:if test="number(@cols) > 3 and @tgroupstyle = 'text'">
				<xsl:attribute name="class" select="'tr_table'" />
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:thead">
		<xsl:element name="thead">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:tbody">
		<xsl:element name="tbody">
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
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:page.number.ref">
		<xsl:variable name="pgNum" select="./@page-number" />

		<xsl:if test="following::x:page.number[@page-num = $pgNum] and ancestor::x:part.main">
			<xsl:call-template name="addReferenceTag">
				<xsl:with-param name="refId" select="concat('pn.', $pgNum)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>