<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x="http://www.sdl.com/xpp"
	exclude-result-prefixes="x">

	<!-- create class with specified selector and body -->
	<xsl:template name="create-class">
		<xsl:param name="selector" />
		<xsl:param name="body" />
		<xsl:value-of select="concat('.',$selector,'{', $body, '}')" />
	</xsl:template>

	<!-- create property with specified name and value -->
	<xsl:function name="x:create-property">
		<xsl:param name="name" />
		<xsl:param name="value" />
		<xsl:value-of select="concat($name, ':', $value, ';')" />
	</xsl:function>

	<!-- convert unit value to rounded em value -->
	<xsl:function name="x:get-em-value">
		<xsl:param name="value" />
		<xsl:if test="string-length($value) != 0">
			<xsl:variable name="lastChar"
				select="substring($value, string-length($value))" />
			<xsl:variable name="v"
				select="number(substring($value, 1, string-length($value)-1))" />
			<xsl:variable name="em-v">
				<xsl:choose>
					<!-- pt -->
					<xsl:when test="$lastChar='q'">
						<xsl:value-of select="$v div 12" />
					</xsl:when>
					<!-- pc -->
					<xsl:when test="$lastChar='p'">
						<xsl:value-of select="$v" />
					</xsl:when>
					<!-- pt -->
					<xsl:when test="$lastChar='d'">
						<xsl:value-of select="$v div 12" />
					</xsl:when>
					<!-- in -->
					<xsl:when test="$lastChar='i'">
						<xsl:value-of select="6*$v" />
					</xsl:when>
					<!-- mm -->
					<xsl:when test="$lastChar='m'">
						<xsl:value-of select="6*$v" />
					</xsl:when>
					<!-- cm -->
					<xsl:when test="$lastChar='z'">
						<xsl:value-of select="6*$v div 2.54" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$v" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="format-number($em-v,'#.#')" />
		</xsl:if>
	</xsl:function>

	<xsl:function name="x:get-em-value-with-zero" as="xs:double" >
		<xsl:param name="value" />
		<xsl:variable name="v" select="x:get-em-value($value)"/>
		<xsl:choose>
			<xsl:when test="$v = 'NaN'">
				<xsl:text>0</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$v" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- get sequence of converted to em values -->
	<xsl:function name="x:get-em-values" as="xs:string*">
		<xsl:param name="values" as="xs:string*" />
		<xsl:for-each select="$values">
			<xsl:sequence select="x:get-em-value(.)" />
		</xsl:for-each>
	</xsl:function>
	
	<xsl:function name="x:get-em-values-with-zero" as="xs:double*">
		<xsl:param name="values" as="xs:string*" />
		<xsl:for-each select="$values">
			<xsl:sequence select="x:get-em-value-with-zero(.)" />
		</xsl:for-each>
	</xsl:function>

	<!-- get property value converted to em -->
	<xsl:function name="x:get-em-property-value">
		<xsl:param name="value" />
		<xsl:if test="string-length($value) != 0">
			<xsl:value-of select="concat(x:get-em-value($value),'em')" />
		</xsl:if>
	</xsl:function>

	<!-- check if specified instruction exist in element -->
	<!-- <xsl:function name="x:contains-instriction" as="xs:boolean"> -->
	<!-- <xsl:param name="node" as="element()" /> -->
	<!-- <xsl:param name="name" /> -->
	<!-- <xsl:variable name="instruction-text" select="processing-instruction('xpp')"/> -->
	<!-- <xsl:value-of select="contains($instruction-text,$name)"/> -->
	<!-- </xsl:function> -->

</xsl:stylesheet>