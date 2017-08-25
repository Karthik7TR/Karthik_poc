<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="../transform-utils.xsl" />
    <xsl:include href="transformFootnotesToLinks.xsl" />
    <xsl:include href="transformTlrKey.xsl" />
    <xsl:include href="transformImagesTags.xsl" />
    
    <xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:param name="fileBaseName" />
	<xsl:param name="pagePrefix" />
	<xsl:param name="divXmlName" />
	<xsl:param name="documentUidMapDoc" />
	<xsl:param name="entitiesDocType" />
	
	<xsl:variable name="documentUidMap" select="document($documentUidMapDoc)" />

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
		<xsl:variable name="previousPageLabel" select="./@prev_page" />
		
		<xsl:choose>
			<xsl:when test="ancestor::x:part.footnotes and $previousPageLabel != '' and $previousPageLabel != '0'">
				<div class="tr_footnote">
            		<div class="footnote">
            			<xsl:element name="a">
            				<xsl:attribute name="class" select="'tr_ftn'" />
            				<xsl:attribute name="href" select="'javascript:void(0)'" />
            				<xsl:attribute name="name" select="concat('ftn.pn.', $previousPageLabel)" />
            				<xsl:attribute name="ftnname" select="concat('pn.', $previousPageLabel)" />
            			</xsl:element>
            			<div class="footnote_body">
                			<div class="page_number">
								<xsl:value-of select="$previousPageLabel" />
							</div>
                		</div>
            		</div>
        		</div>
			</xsl:when>
			<xsl:when test="ancestor::x:part.main and $previousPageLabel != '' and $previousPageLabel != '0'">
				<xsl:element name="a">
            		<xsl:attribute name="class" select="'tr_ftn footnote_access_point'" />
            		<xsl:attribute name="href" select="concat('#', 'ftn.pn.', $previousPageLabel)" />
            		<xsl:attribute name="name" select="concat('pn.', $previousPageLabel)" />
            		<xsl:attribute name="ftnname" select="concat('ftn.pn.', $previousPageLabel)" />
            	</xsl:element>            		
			</xsl:when>
		</xsl:choose>
	
		<xsl:variable name="apostrophe">'</xsl:variable>
		<xsl:processing-instruction name="pb" select="concat('label', '=', $apostrophe, $pagePrefix, ./@num, $apostrophe, '?')" />
	</xsl:template>

	<xsl:template match="x:XPPHier">
		<xsl:element name="a">
			<xsl:attribute name="name" select="./@uuid" />
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
	
	<xsl:template match="x:XPPMetaData" />

	<xsl:template match="x:ref">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:ital|x:bold">
		<xsl:apply-templates />
	</xsl:template>
    
	<xsl:template match="x:t">
        <xsl:variable name="footnoteRefId" select="x:footnote-reference-id(.)" />

        <xsl:choose>
            <xsl:when test="number($footnoteRefId)">
                <xsl:call-template name="addFootnoteReference">
                    <xsl:with-param name="refId" select="$footnoteRefId" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="span">
                    <xsl:attribute name="class">
                        <xsl:value-of select="x:get-class-name(name(.))" />
                        <xsl:if test="@style">
                            <xsl:value-of select="concat(' font_', x:get-class-name(@style))" />
                        	<xsl:variable name="tAmount" select="count(preceding-sibling::x:t) + 1" />
                            <xsl:if test="(following-sibling::x:pagebreak[count(preceding-sibling::x:t)=$tAmount] or not(following-sibling::x:t)) and @style='dt'">
                            	<xsl:value-of select="concat(' ', 'last_dt')"/>
                            </xsl:if>
                        </xsl:if>
                        <xsl:variable name="pagebreakSibling" select="preceding-sibling::x:pagebreak" />
                        <xsl:if test="$pagebreakSibling and count($pagebreakSibling/following-sibling::x:t intersect preceding-sibling::x:t)=0">
                            <xsl:value-of select="concat(' ', 'no-indent')" />
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:apply-templates />
                </xsl:element>
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
		<xsl:variable name="docId" select="$documentUidMap/x:uuidmap/x:item[@key = $uid]" />
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

	<xsl:template match="x:cite.query">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
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

	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>
</xsl:stylesheet>