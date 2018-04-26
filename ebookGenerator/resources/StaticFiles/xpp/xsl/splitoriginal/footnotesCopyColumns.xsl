<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:import href="../footnotesUtil.xsl"/>
    
    <xsl:template match="x:pagebreak">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	
		<xsl:if test="$fileType='MAIN'">
			<xsl:variable name="pagebreak" select="$footnotesDocument//x:pagebreak[@num=current()/@num]" />
			<xsl:if test="x:shouldCopyColumn($pagebreak)=true()">
				<xsl:variable name="columnsTag"
					select="$pagebreak/following::x:columns[1]" />
				<xsl:for-each select="$columnsTag//x:footnote">
					<xsl:variable name="footnote" select="current()" />
	
					<xsl:if test="x:shouldCopyXref($footnote)=true()">
						<xsl:element name="xref">
							<xsl:attribute name="id"
								select="concat($footnote/@id, '-', $COLUMN_COPY)" />
							<xsl:attribute name="origId" select="$footnote/@origId" />
							<xsl:attribute name="type" select="'footnote'" />
							<xsl:attribute name="hidden" select="true()" />
							<xsl:attribute name="orig" select="false()" />
						</xsl:element>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:if>
	</xsl:template>
    
    <xsl:template match="x:columns">
        <xsl:choose>
            <xsl:when test="$fileType='FOOTNOTE'">
                <xsl:variable name="isSplitBySectionbreaks" select="count(.//x:sectionbreak)!=0" />
                
                <xsl:choose>
                    <xsl:when test="$isSplitBySectionbreaks">
                        <xsl:copy>
                            <xsl:apply-templates select="node()|@*" mode="additionalColumnFootnotesNosectionbreaks"/>
                        </xsl:copy>
                        <xsl:apply-templates select="node()|@*" mode="hiddenFoootnotesNoColumns"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy>
                            <xsl:apply-templates select="node()|@*"/>
                        </xsl:copy>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="x:sectionbreak" mode="additionalColumnFootnotesNosectionbreaks"/>
    
    <xsl:template match="x:footnote" mode="additionalColumnFootnotesNosectionbreaks">
    	<xsl:if test="x:shouldCopyFootnote(.)=true()">
            <xsl:element name="footnote">
                <xsl:attribute name="id" select="concat(@id, '-', $COLUMN_COPY)" />
                <xsl:attribute name="referenceOnDifferentPageId" select="true()" />
                <xsl:attribute name="xrefId" select="@id" />
                <xsl:attribute name="xrefDoc" select="preceding::x:sectionbreak[1]/@sectionuuid" />
                <xsl:apply-templates select="node()|@*[not(name()='id')]" mode="#current" />
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="x:footnote.reference[@class='show_in_main'] | 
                         x:footnote.body[@class='show_in_main']" 
                  mode="additionalColumnFootnotesNosectionbreaks" />
    
    <xsl:template match="x:column" mode="hiddenFoootnotesNoColumns">
        <xsl:apply-templates select="node()|@*" mode="hiddenFoootnotesNoColumns"/>
    </xsl:template>
    
    <xsl:template match="x:footnote" mode="hiddenFoootnotesNoColumns">
        <xsl:element name="footnote">
            <xsl:attribute name="hidden" select="true()" />
            <xsl:apply-templates select="current()/*|@*[not(name()='hidden')]" />
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>