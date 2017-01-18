<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output indent="no" method="text" />
    <xsl:template match="Vertical">
        <xsl:apply-templates select="CacheVersion" />
    </xsl:template>
    <xsl:template match="CacheVersion">cache.version=<xsl:value-of select="text()" /><xsl:text>
</xsl:text></xsl:template>
</xsl:stylesheet>