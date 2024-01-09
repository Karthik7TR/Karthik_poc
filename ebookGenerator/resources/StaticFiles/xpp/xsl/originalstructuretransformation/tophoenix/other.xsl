<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	
	<xsl:variable name="special-images" select="document('special-images.xml')" />

	<!-- To support Index conts -->
	<xsl:template match="x:line">
		<xsl:if test="$bundlePartType = 'INDEX'">
			<xsl:variable name="endTagsCount">
				<xsl:variable name="previousEndTagsCount"
					select="count(preceding::x:endtag[@name = 'mte2'])" />
				<xsl:choose>
					<xsl:when test="x:endtag[@name = 'mte2']">
						<xsl:value-of select="$previousEndTagsCount + 1" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$previousEndTagsCount" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="count(preceding::x:tag[@name = 'mte2']) > $endTagsCount">
				<linebreak />
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- TLRkey character support -->
	<xsl:template match="x:TLRkey">
		<xsl:copy />
	</xsl:template>

	<!-- Images -->
	<xsl:template match="x:image">
		<xsl:element name="proview_image">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="name(.)!='id'">
						<xsl:copy-of select="." />
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="id">
							<xsl:value-of select="x:map-id(..)" />
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<xsl:attribute name="table-viewer" select="x:is-exception(.)=false()" />
			<xsl:call-template name="create-classes-attribute">
				<xsl:with-param name="imageNode" select="." />
			</xsl:call-template>
		</xsl:element>
	</xsl:template>

	<xsl:function name="x:map-id">
		<xsl:param name="imageNode" />
		<xsl:choose>
			<xsl:when test="contains($imageNode/@id, '.')">
				<xsl:value-of select="$imageNode/@id" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($imageNode/@id, '.', $imageNode/@type)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="x:is-exception">
		<xsl:param name="imageNode" />
		<xsl:variable name="id" select="$imageNode/@id" />
		<xsl:value-of select="boolean($special-images/images/image[@id=$id])" />
	</xsl:function>
	
	<xsl:template name="create-classes-attribute">
		<xsl:param name="imageNode" />
		<xsl:variable name="id" select="$imageNode/@id" />
		<xsl:variable name="node" select="$special-images/images/image[@id=$id]" />
		<xsl:if test="$node/classes">
			<xsl:attribute name="classes" select="string-join($node/classes/class/@name, ' ')" />
		</xsl:if>
	</xsl:template>

	<!-- External links -->
	<xsl:template match="x:cite.query">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>