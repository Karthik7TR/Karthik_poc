<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    <xsl:include href="footnotesToMain.xsl"/>

	<xsl:variable name="firstDocFamilyUuid"
		select="(x:root//x:XPPMetaData)[1]/@md.doc_family_uuid" />
    <xsl:param name="footnotesFile" />
	<xsl:variable name="footnotesDocument" select="document($footnotesFile)" />

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:root">
		<root>
			<xsl:call-template name="addSectionbreak">
				<xsl:with-param name="sectionuuid" select="$firstDocFamilyUuid" />
			</xsl:call-template>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:XPPMetaData">
		<xsl:if test="@md.doc_family_uuid!=$firstDocFamilyUuid">
			<xsl:variable name="closestPrecedingMetaElement"
				select="preceding::*[name() = 'XPPMetaData' or name() = 'XPPHier'][1]" />
			<xsl:choose>
				<xsl:when
					test="$closestPrecedingMetaElement/name() = 'XPPHier' and 
						$closestPrecedingMetaElement/@uuid = @parent_uuid">
					<xsl:variable name="xppHierUuid"
						select="$closestPrecedingMetaElement/@uuid" />
					<xsl:variable name="precedingToHier"
						select="$closestPrecedingMetaElement/preceding-sibling::*[1]" />
					<xsl:if
						test="$precedingToHier[name() = 'XPPMetaData' and @uuid=$xppHierUuid][1]">
						<xsl:call-template name="addSectionbreak">
							<xsl:with-param name="sectionuuid" select="@md.doc_family_uuid" />
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="addSectionbreak">
						<xsl:with-param name="sectionuuid" select="@md.doc_family_uuid" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:XPPHier">
		<xsl:variable name="xppHierUuid" select="@uuid" />
		<xsl:variable name="closestFollowingMetaElement"
			select="following::*[name() = 'XPPMetaData' or name() = 'XPPHier'][1]" />
		<xsl:variable name="docFamilyUuid"
			select="$closestFollowingMetaElement/@md.doc_family_uuid" />

		<xsl:variable name="shouldAddSectionBreak"
			select="not($docFamilyUuid=$firstDocFamilyUuid) and
        		 $closestFollowingMetaElement/name() = 'XPPMetaData' and 
        		 $closestFollowingMetaElement/@parent_uuid = @uuid and 
        		 not(preceding-sibling::*[name() = 'XPPMetaData' and @uuid=$xppHierUuid][1])" />

		<xsl:if test="$shouldAddSectionBreak">
			<xsl:call-template name="addSectionbreak">
				<xsl:with-param name="sectionuuid" select="$docFamilyUuid" />
			</xsl:call-template>
		</xsl:if>
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template name="addSectionbreak">
		<xsl:param name="sectionuuid" />
		<xsl:element name="sectionbreak">
			<xsl:attribute name="sectionuuid" select="$sectionuuid" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>