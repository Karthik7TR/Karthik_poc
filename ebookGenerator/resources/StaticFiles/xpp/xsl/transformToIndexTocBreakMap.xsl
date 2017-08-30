<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns="http://www.sdl.com/xpp" xmlns:x="http://www.sdl.com/xpp" 
                exclude-result-prefixes="x">
                      
    <xsl:template match="x:root">
    	<indexbreaks>
    	<xsl:if test="count(//x:pagebreak)>20">
    		<xsl:apply-templates>
    			<xsl:with-param name="firstPage" select="x:pagebreak[1]/@num" />
    		</xsl:apply-templates>
    	</xsl:if>
    	</indexbreaks>
    </xsl:template>    
    
    <xsl:template match="x:pagebreak">
    	<xsl:param name="firstPage" />
    	<xsl:variable name="pageNum" select="./@num" />
		
		<xsl:if test="$pageNum = $firstPage or $pageNum mod 20 = 0">
			<xsl:variable name="letter">
				<xsl:apply-templates select="following::x:l1" mode="extracting" />
			</xsl:variable>
		
			<xsl:element name="indexbreak">
				<xsl:attribute name="startChar" select="substring($letter, 1, 1)" />
				<xsl:attribute name="endChar">
					<xsl:variable name="lastChar">
						<xsl:apply-templates select="following::x:pagebreak[@num mod 20 = 0]" mode="get-last-char" />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$lastChar = ''">
							<xsl:value-of select="following::x:l1[last()]/substring(x:t[1]/text(), 1, 1)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$lastChar" />
						</xsl:otherwise>
					</xsl:choose>
					
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
    </xsl:template> 
    
    <xsl:template match="x:pagebreak" mode="get-last-char">	
		<xsl:value-of select="preceding::x:l1[1]/substring(x:t[1]/text(), 1, 1)" />
    </xsl:template> 
    
    <xsl:template match="x:l1" mode="extracting">
        <xsl:variable name="currentLetter" select="substring(./x:t[1]/text(), 1, 1)" />
		<xsl:variable name="previousLetter" select="substring(preceding::x:l1[1]/x:t[1]/text(), 1, 1)" />
		
		<xsl:if test="$currentLetter != $previousLetter">
			<xsl:value-of select="$currentLetter" />
		</xsl:if>
    </xsl:template>
    
    <xsl:template match="text()" />
</xsl:stylesheet>