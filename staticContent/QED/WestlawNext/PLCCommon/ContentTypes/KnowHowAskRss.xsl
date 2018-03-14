<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
    <xsl:output method="xml" encoding="utf-8" indent="yes" omit-xml-declaration="no" />

    <xsl:template match="/">
			<feed xmlns="http://www.w3.org/2005/Atom">
				<xsl:variable name="documentGuid">
					<xsl:value-of select="//md.uuid"/>
				</xsl:variable>
				<title>
					<xsl:value-of select="//ask/prelim/title"/>
				</title>
				<subtitle>
					<xsl:apply-templates select="//ask/abstract" />
				</subtitle>
				<xsl:element name="link">
					<xsl:attribute name="href">
						<xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
					</xsl:attribute>
				</xsl:element>
				<updated>
					<xsl:variable name="commentAskDate">
						<xsl:value-of select="(//body/discussion/comments/comment)[last()]/@createdDate"/>
					</xsl:variable>
					<xsl:value-of select="DocumentExtension:ConvertCommentTimeDateStampToRssIsoFormat($commentAskDate)"/>
				</updated>
				<id>
					<xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
				</id>
				<xsl:apply-templates select="//body/discussion" />
			</feed>
    </xsl:template>

	<xsl:template match="abstract">
		<xsl:variable name="abstractDescription">
			<xsl:for-each select="para/paratext">
				<xsl:value-of select="substring(., 0, 100)"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="substring($abstractDescription, 0, 100)"/>
		<xsl:text>...</xsl:text>
	</xsl:template>

	<xsl:template match="comments">
		<xsl:for-each select="comment">
			<xsl:sort select="position()" order="descending"/>
			<xsl:element name="entry" namespace="">
				<title></title>
				<xsl:element name="link">
					<xsl:attribute name="href">
					<xsl:variable name="documentGuid">
						<xsl:value-of select="//md.uuid"/>
					</xsl:variable>
					<xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
						<xsl:text>#</xsl:text>
					<xsl:value-of select="@commentId"/>
					</xsl:attribute>
				</xsl:element>
				<updated>
					<xsl:variable name="commentAskDate">
						<xsl:value-of select="@createdDate"/>
					</xsl:variable>
					<xsl:value-of select="DocumentExtension:ConvertCommentTimeDateStampToRssIsoFormat($commentAskDate)"/>
				</updated>
				<content>
					<xsl:value-of select="substring(.,0,100)" /><xsl:text>...</xsl:text>
				</content>
				<author>
					<name>
						<xsl:choose>
							<xsl:when test="@showAuthor = 'true'">
								<xsl:value-of select="@displayName" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAnonymous;', '&askAnonymousDefault;')"/>
							</xsl:otherwise>
						</xsl:choose>
					</name>
				</author>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
