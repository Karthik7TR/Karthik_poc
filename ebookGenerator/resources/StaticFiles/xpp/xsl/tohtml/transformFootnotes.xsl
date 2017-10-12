<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:part.footnotes">
        <section class="tr_footnotes">
            <xsl:apply-templates />
        </section>
    </xsl:template>
    
    <xsl:template match="x:footnote">
        <xsl:variable name="cssClasses">
            <xsl:choose>
                <xsl:when test="@hidden">
                    <xsl:value-of select="'tr_footnote footnoteHidden'" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'tr_footnote'" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:element name="div">
            <xsl:attribute name="class" select="$cssClasses"/>
            <div class="footnote">
                <xsl:apply-templates />
            </div>
        </xsl:element>
        
        <xsl:if test="./x:footnote.body//x:page.number[1]">
			<xsl:apply-templates select="./x:footnote.body//x:page.number[1]" mode="place-page-number" />	
		</xsl:if>
    </xsl:template>
    
    <xsl:template match="x:page.number">
		<xsl:if test="not(ancestor::x:footnote)">	
			<xsl:apply-templates select="self::node()" mode="place-page-number" />				
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="x:page.number" mode="place-page-number">
		<xsl:call-template name="addPageNumber">
			<xsl:with-param name="pgNum" select="./@page-num" />
		</xsl:call-template>
	</xsl:template>
    
    <xsl:template match="x:footnote.body">
        <div class="er_rp_search_volume_content_data">
            <xsl:element name="div">
                <xsl:attribute name="class">
                    <xsl:value-of select="concat('footnote_body ', @class)" />
                </xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </div>
    </xsl:template>
    
    <xsl:template match="x:foots">
        <xsl:copy>
            <xsl:copy-of select="./@id" />
            <xsl:apply-templates />
        </xsl:copy>
        <xsl:if test="not(following-sibling::*[1][self::x:para.text])">
	        <xsl:element name="div">
	        	<xsl:attribute name="class" select="'no-indent'" />
	        </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="x:footnote.reference[@class='show_in_main']">
         <div class="er_rp_search_volume_content_data">
             <sup class="show_in_main">
                 <span class="tr_footnote_name">
                    <xsl:value-of select="x:t/text()" />
                 </span>
             </sup>
         </div>
    </xsl:template>
    
    <xsl:template match="x:footnote.reference[@class='show_in_footnotes']">
        <xsl:variable name="footnote" select="ancestor::x:footnote[1]" />

        <xsl:element name="sup">
        
            <xsl:if test="not($footnote/@referenceOnDifferentPageId)">
                <xsl:call-template name="addAnchorTag">
                    <xsl:with-param name="refId" select="$footnote/@id" />
                    <xsl:with-param name="excludeText" select="true()" />
                </xsl:call-template>
            </xsl:if>
                    
            <div class="er_rp_search_volume_content_data">
                <xsl:choose>
                    <xsl:when test="$footnote/@referenceOnDifferentPageId">
                        <xsl:call-template name="addReferenceToAnotherPage">
                            <xsl:with-param name="footnote" select="$footnote"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="addAnchorTag">
                            <xsl:with-param name="refId" select="$footnote/@id" />
                            <xsl:with-param name="excludeName" select="true()" />
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:footnote.reference[@class='show_in_main_and_footnotes']">
        <xsl:variable name="footnote" select="ancestor::x:footnote[1]" />
        
        <xsl:element name="sup">
            <xsl:choose>
                <xsl:when test="$footnote/@referenceOnDifferentPageId">
                    <xsl:call-template name="addReferenceToAnotherPage">
                        <xsl:with-param name="footnote" select="$footnote"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="addAnchorTag">
                        <xsl:with-param name="refId" select="$footnote/@id" />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:footnote/x:t[@style='footnote cgt']">
        <div class="er_rp_search_volume_content_data">
            <xsl:call-template name="convertToSpan" />
        </div>
    </xsl:template>
    
    <xsl:template match="x:xref[@type='footnote']">
        <xsl:if test="@hidden">
            <xsl:call-template name="addReferenceTag">
                <xsl:with-param name="refId" select="@id" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="x:hidden.footnote.reference">
        <xsl:call-template name="addAnchorTag">
            <xsl:with-param name="refId" select="@refId" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:function name="x:footnote-reference-id">
        <xsl:param name="node" as="node()"/>
        
        <xsl:variable name="cgtSuper" select="contains($node/@style, 'cgt') and contains($node/@style, 'super')"/>
        <xsl:if test="$cgtSuper" >
            <xsl:value-of select="$node/following-sibling::*[1]/self::x:xref/@id"/>
        </xsl:if>
    </xsl:function>
    
    <xsl:template name="addFootnoteReference">
        <xsl:param name="refId" />
        
        <xsl:element name="sup">
            <xsl:attribute name="class">
                <xsl:value-of select="x:get-class-name(name(.))" />
                <xsl:if test="@style">
                    <xsl:value-of select="concat(' font_', replace(x:get-class-name(@style), 'super', ''))" />
                </xsl:if>
            </xsl:attribute>

            <xsl:call-template name="addReferenceTag">
                <xsl:with-param name="refId" select="$refId" />
            </xsl:call-template>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="addReferenceTag">
        <xsl:param name="refId" />
        <xsl:variable name="ftnname" select="concat('ftn.', x:fix-lrre-page($refId))"/>
        <xsl:element name="a">
            <xsl:attribute name="ftnname" select="$ftnname" />
            <xsl:attribute name="name" select="x:fix-lrre-page(concat('f', $refId))" />
            <xsl:attribute name="href" select="concat('#', $ftnname)" />
            <xsl:attribute name="class" select="'tr_ftn'" />
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="addAnchorTag">
        <xsl:param name="refId" />
        <xsl:param name="excludeName" />
        <xsl:param name="excludeText" />
        
        <xsl:element name="a">
            <xsl:attribute name="class" select="'tr_ftn'" />
            <xsl:attribute name="href" select="''" />
            <xsl:attribute name="ftnname" select="x:fix-lrre-page(concat('f', $refId))" />
            <xsl:if test="not($excludeName)">
                <xsl:attribute name="name" select="x:fix-lrre-page(concat('ftn.', $refId))" />
            </xsl:if>
            <xsl:if test="not($excludeText)">
                <xsl:apply-templates />
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="addReferenceToAnotherPage">
        <xsl:param name="footnote" />
        
        <xsl:element name="a">
            <xsl:attribute name="class" select="'tr_ftn'" />
            <xsl:attribute name="name" select="concat('ftn.', $footnote/@id)" />
        </xsl:element>
        <xsl:element name="a">
            <xsl:attribute name="href" select="concat('er:#', $footnote/@xrefDoc, '/f', $footnote/@xrefId)" />
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="addPageNumber">
    	<xsl:param name="pgNum" />
    	
    	<div class="tr_footnote">
           	<div class="footnote">
           		<xsl:if test="preceding::x:part.main//x:page.number.ref[@page-number = $pgNum]">
           			<xsl:call-template name="addAnchorTag">
           				<xsl:with-param name="refId" select="concat('pn.', $pgNum)" />
           			</xsl:call-template>
           		</xsl:if>
           		<div class="footnote_body page_number">
               		<div class="page_number">
						<xsl:value-of select="$pgNum" />
					</div>
               	</div>
           	</div>
        </div>
    </xsl:template>
    
</xsl:stylesheet>