<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->

<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />

	<!-- Global Variable START -->
	<xsl:variable name="tags_to_transfrom" select="'[p] [div] [table] [tr] [ul] [li] [ol] [h1] [h2] [h3] [h4] [h5] [h6]'" />
	<xsl:variable name="tags_with_space" select="'[p] [div] [th] [td] [br]'" />
	<xsl:variable name="replaceableChars">“”’&nbsp;&#xA;</xsl:variable>
	<xsl:variable name="replacedChars">""'  </xsl:variable>
	<!-- Global Variable END -->

	<!-- Main Transformation Templates START -->
	<xsl:template match="/">
		<xsl:element name="div">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="p[normalize-space(translate(., '&nbsp;', '')) 
						and not(descendant::tr or descendant::ul or descendant::ol or descendant::li or descendant::h1 or descendant::h2 or descendant::h3 or descendant::h4 or descendant::h5 or descendant::h6)]"
				  priority="1">
		<xsl:element name="{name()}">
			<xsl:call-template name="normalize_node" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="table[normalize-space(translate(., '&nbsp;', ''))]
						| ul[normalize-space(translate(., '&nbsp;', ''))]"
						priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="tr[normalize-space(translate(., '&nbsp;', ''))]
						| li[normalize-space(translate(., '&nbsp;', ''))]
						| ol[normalize-space(translate(., '&nbsp;', ''))]
						| h1[normalize-space(translate(., '&nbsp;', ''))]
						| h1[normalize-space(translate(., '&nbsp;', ''))]
						| h2[normalize-space(translate(., '&nbsp;', ''))]
						| h3[normalize-space(translate(., '&nbsp;', ''))]
						| h4[normalize-space(translate(., '&nbsp;', ''))]
						| h5[normalize-space(translate(., '&nbsp;', ''))]
						| h6[normalize-space(translate(., '&nbsp;', ''))]"
					priority="1">
		<xsl:element name="p">
			<xsl:call-template name="normalize_node"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="div[normalize-space(translate(., '&nbsp;', ''))
						and not(descendant::p or descendant::tr or descendant::ul or descendant::ol or descendant::li or descendant::h1 or descendant::h2 or descendant::h3 or descendant::h4 or descendant::h5 or descendant::h6)]"
					priority="1">
		<xsl:element name="p">
			<xsl:call-template name="normalize_node"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:if test="normalize-space()">
			<xsl:element name="p">
				<xsl:call-template name="normalize_text"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="*[not(node())] | *[not(node()[2]) and node()/self::text() and not(normalize-space(translate(., '&nbsp;', '')))]" />

	<xsl:template match="*">
		<xsl:call-template name="wrap_paragraph" />
	</xsl:template>
	<!-- Main Transformation Templates END -->

	<!-- Help Functions START -->
	<xsl:template name="normalize_node">
		<xsl:param name="nodes" select="node()"/>
		<xsl:variable name="full_text">
			<xsl:call-template name="join">
				<xsl:with-param name="nodes" select = "$nodes" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="normalize_text">
			<xsl:with-param name="text" select = "$full_text" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="normalize_text">
		<xsl:param name="text" select="."/>
		<xsl:value-of select="normalize-space(translate($text, $replaceableChars, $replacedChars))" />
	</xsl:template>

	<xsl:template name="join">
		<xsl:param name="nodes"/>
		<xsl:param name="sep" select="' '"/>

		<xsl:if test="count($nodes) > 0">
			<xsl:choose>
				<xsl:when test="$nodes[1]/node()">
					<xsl:call-template name="join">
						<xsl:with-param name="nodes" select="$nodes[1]/node()"/>
						<xsl:with-param name="sep" select="$sep"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$nodes[1]" />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="contains($tags_with_space, concat('[', name($nodes[1]), ']'))">
				<xsl:value-of select="$sep" />
			</xsl:if>

			<xsl:if test="count($nodes) > 1">
				<xsl:call-template name="join">
					<xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
					<xsl:with-param name="sep" select="$sep"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="wrap_paragraph">
		<xsl:param name="nodes" select="node()"/>

		<xsl:if test="count($nodes[normalize-space(translate(., '&nbsp;', ''))]) > 0">
			<xsl:variable name="tags_in_tags_to_transform"
						select="$nodes[
							contains($tags_to_transfrom, concat('[', name(), ']'))
							or descendant::node()[contains($tags_to_transfrom, concat('[', name(), ']'))]]" />
			<xsl:variable name="tag_in_tags_to_transform" select="$tags_in_tags_to_transform[1]" />

			<xsl:choose>
				<xsl:when test="$tag_in_tags_to_transform">
					<xsl:variable name="tags_to_wrap"
								select="$tag_in_tags_to_transform/preceding-sibling::node()[
											count(following-sibling::node()[
													contains($tags_to_transfrom, concat('[', name(), ']')) 
													or descendant::node()[contains($tags_to_transfrom, concat('[', name(), ']'))]])
												= count($tags_in_tags_to_transform) 
											and not(contains($tags_to_transfrom, concat('[', name(), ']')) 
													or descendant::node()[contains($tags_to_transfrom, concat('[', name(), ']'))]) 
											and normalize-space(translate(., '&nbsp;', ''))]" />
					<xsl:if test="count($tags_to_wrap) > 0">
						<xsl:element name="p">
							<xsl:call-template name="normalize_node">
								<xsl:with-param name="nodes" select="$tags_to_wrap" />
							</xsl:call-template>
						</xsl:element>
					</xsl:if>

					<xsl:variable name="following_tags_in_tags_to_transform"
									select="$tag_in_tags_to_transform 
											| $tag_in_tags_to_transform/following-sibling::node()[
												(contains($tags_to_transfrom, concat('[', name(), ']'))
												or descendant::node()[contains($tags_to_transfrom, concat('[', name(), ']'))])
												and count(preceding-sibling::node()[
															not(contains($tags_to_transfrom, concat('[', name(), ']')) 
															or descendant::node()[contains($tags_to_transfrom, concat('[', name(), ']'))]) 
																					and normalize-space(translate(., '&nbsp;', ''))]) 
														= count($tags_to_wrap)]"/>

					<xsl:apply-templates select="$following_tags_in_tags_to_transform"/>

					<xsl:call-template name="wrap_paragraph">
						<xsl:with-param name="nodes" select="$following_tags_in_tags_to_transform[last()]/following-sibling::node()" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="p">
						<xsl:call-template name="normalize_node">
							<xsl:with-param name="nodes" select="$nodes" />
						</xsl:call-template>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<!-- Help Functions END -->

</xsl:stylesheet>