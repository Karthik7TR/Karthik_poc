<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="utils.xsl" />
	<xsl:import href="transform-utils.xsl" />
	<xsl:output omit-xml-declaration="yes" method="text" indent="no" />
	<xsl:template match="/">
		<xsl:apply-templates />		
		<xsl:call-template name="createLeftIndentStyles" />
		<xsl:call-template name="createRightIndentStyles" />
		<xsl:call-template name="createPreLeadingStyles" />
		<xsl:call-template name="createImageFlowStyles" />
	</xsl:template>
	
	<xsl:template name="createLeftIndentStyles">
		<xsl:for-each select="distinct-values(//x:tag[@name = 'text.line' or @name = 'para']/x:attr[@name = 'left.indent']/text())">
			<xsl:call-template name="create-class">
				<xsl:with-param name="selector" select="concat('left_indent_', x:get-class-name(.))" />
				<xsl:with-param name="body" select="x:create-property('margin-left', x:get-indent-value(.))" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="createRightIndentStyles">
		<xsl:for-each select="distinct-values(//x:tag[@name = 'text.line' or @name = 'para']/x:attr[@name = 'right.indent']/text())">
			<xsl:call-template name="create-class">
				<xsl:with-param name="selector" select="concat('right_indent_', x:get-class-name(.))" />
				<xsl:with-param name="body" select="x:create-property('margin-right', x:get-indent-value(.))" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="createPreLeadingStyles">
		<xsl:for-each select="distinct-values(//x:tag[@name = 'text.line' or @name = 'para' or @name = 'fm.image.wrap']/x:attr[@name = 'pre.leading']/text())">
			<xsl:call-template name="create-class">
				<xsl:with-param name="selector" select="concat('pre_leading_', x:get-class-name(.))" />
				<xsl:with-param name="body" select="x:create-property('margin-top', x:get-indent-value(.))" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="createImageFlowStyles">
		<xsl:for-each select="distinct-values(//x:tag[@name = 'fm.image.wrap']/x:attr[@name = 'quad']/text())">
			<xsl:call-template name="create-class">
				<xsl:with-param name="selector" select="concat('quad', '_', .)" />
				<xsl:with-param name="body">
					<xsl:variable name="floating" select="x:get-alignValue(., false())" />
					<xsl:value-of select="concat(x:create-property('float', $floating), 
												 x:create-property('width', '20%'),
												 x:create-property(concat('margin-', x:get-alignValue(., true())), '1%'))" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="create-class">
				<xsl:with-param name="selector" select="concat('image_wrapper', '_', .)" />
				<xsl:with-param name="body" select="x:create-property('overflow', 'hidden')" />
			</xsl:call-template>
		</xsl:for-each>
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

	<xsl:function name="x:get-indent-value">
		<xsl:param name="value" />
		<xsl:analyze-string regex="([0-9\.]+)([a-zA-Z]){{1}}" select="$value">
			<xsl:matching-substring>
				<xsl:variable name="numValue" select="regex-group(1)" />
				<xsl:variable name="units">
					<xsl:choose>
						<xsl:when test="regex-group(2) = 'p'">
							<xsl:value-of select="3" />
						</xsl:when>
						<xsl:when test="regex-group(2) = 'q'">
							<xsl:value-of select="0.1" />
						</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat(string(number($numValue) * $units), '%')" />
			</xsl:matching-substring>
		</xsl:analyze-string>
	</xsl:function>

	<xsl:function name="x:get-alignValue">
		<xsl:param name="value" />
		<xsl:param name="reverse" />
		<xsl:choose>
			<xsl:when test="($value = 'l' and $reverse = false()) or ($value = 'r' and $reverse = true())">
				<xsl:value-of select="'left'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'right'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>
