<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="utils.xsl" />
	<xsl:output omit-xml-declaration="yes" method="text" indent="no" />
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>
	<!-- create all global fonts styles -->
	<xsl:template match="x:styles/x:style">
		<xsl:variable name="font-weight" select="x:get-font-weight(@fv)" />
		<xsl:variable name="font-style" select="x:get-font-style(@fv)" />

		<xsl:call-template name="create-class">
			<xsl:with-param name="selector" select="concat('font_', x:get-class-name(@name))" />
			<xsl:with-param name="body">
				<xsl:value-of
					select="x:create-property('font-size', x:get-em-property-value(@size))" />
				<xsl:if test="$font-weight != ''">
					<xsl:value-of select="x:create-property('font-weight', $font-weight)" />
				</xsl:if>
				<xsl:if test="$font-style != ''">
					<xsl:value-of select="x:create-property('font-style', $font-style)" />
				</xsl:if>

				<xsl:choose>
					<xsl:when test="@cm='smallcap'">
						<xsl:value-of select="x:create-property('font-variant', 'small-caps')" />
					</xsl:when>
					<xsl:when test="@cm='upper'">
						<xsl:value-of select="x:create-property('text-transform', 'uppercase')" />
					</xsl:when>
					<xsl:when test="@cm='lower'">
						<xsl:value-of select="x:create-property('text-transform', 'lowercase')" />
					</xsl:when>
				</xsl:choose>
				<xsl:if test="@underline">
					<xsl:value-of select="x:create-property('text-decoration', 'underline')" />
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="text()" />

	<xsl:function name="x:get-font-weight">
		<xsl:param name="fv" />
		<xsl:if test="$fv = '1' or $fv = '3'">
			<xsl:text>bold</xsl:text>
		</xsl:if>
	</xsl:function>

	<xsl:function name="x:get-font-style">
		<xsl:param name="fv" />
		<xsl:if test="$fv = '2' or $fv = '3'">
			<xsl:text>italic</xsl:text>
		</xsl:if>
	</xsl:function>

</xsl:stylesheet>