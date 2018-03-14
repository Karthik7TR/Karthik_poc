<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Dtags.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="FooterCitation">
		<xsl:variable name="citation">
			<xsl:choose>
				<xsl:when test="contains('|w_adm_lrpndl|w_3rd_calman|w_codesccadp|', concat('|',  /Document/document-data/collection , '|'))">
					<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.display.primarycite"/>
				</xsl:when>
				<xsl:when test="/Document/document-data/collection = 'w_codes_leghistory'">
					<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.expandedcite"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite">
						<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite"/>	
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($citation) &gt; 0">
			<div class="&citationClass;">
				<xsl:value-of	select="$citation"	/>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="md.cites" priority="2">
		<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y'] | md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y']" />
		<xsl:variable name="firstLineCite">
			<xsl:apply-templates select="md.first.line.cite" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($displayableCites) &gt; 0">
				<div class="&citesClass;">
					<xsl:for-each select="$displayableCites">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
				</div>
			</xsl:when>
			<xsl:when test="string-length($firstLineCite) &gt; 0">
				<div class="&citesClass;">
					<xsl:copy-of select="$firstLineCite"/>
				</div>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="crdms | rc.gen" />

	<!-- Internal references are messed up in XENA content, did a straight override to keep code clean -->
	<xsl:template match="internal.reference" priority="1">
		<xsl:variable name="refid"  select="translate(@refid, '?', 'Þ')" />
		<xsl:variable name="id"  select="translate(@ID, '?', 'Þ')" />
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0 or string-length($id) &gt; 0">
			<xsl:choose>
				<xsl:when test="key('allElementIds', $refid)">
					<a href="{concat('#&internalLinkIdPrefix;', $refid)}" class="&internalLinkClass;">
						<xsl:if test="string-length($id) &gt; 0">
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&internalLinkIdPrefix;', $id)"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:copy-of select="$contents"/>
						<xsl:comment>anchor</xsl:comment>
					</a>
				</xsl:when>
				<xsl:when test="string-length($id) &gt; 0">
					<a id="{concat('&internalLinkIdPrefix;', $id)}">
						<xsl:comment>anchor</xsl:comment>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$contents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ti | ti2 | til | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | snl | srnl | hc2">
		<xsl:apply-templates />
		<xsl:if test="not(following-sibling::ti or following-sibling::ti2 or following-sibling::til or following-sibling::hg0 or following-sibling::hg1 or following-sibling::hg2 or following-sibling::hg3 or following-sibling::hg4 or following-sibling::hg5 or following-sibling::hg6 or following-sibling::hg7 or following-sibling::hg8 or following-sibling::hg9 or following-sibling::hg10 or following-sibling::hg11 or following-sibling::hg12 or following-sibling::hg13 or following-sibling::hg14 or following-sibling::hg15 or following-sibling::hg16 or following-sibling::hg17 or following-sibling::hg18 or following-sibling::hg19 or following-sibling::snl or following-sibling::srnl or following-sibling::hc2)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!-- Render d1 tags with internal.reference child nodes that don't have any text. This is to fix
			 problems with the target of the currentness link being inside a d1 tag, causing it not to work properly. -->
	<xsl:template match="d1[internal.reference[string-length(normalize-space(.//text())) = 0]]">
		<xsl:apply-templates select="internal.reference" />
	</xsl:template>

<xsl:template match="centv"  />
</xsl:stylesheet>
