<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:include href="Leader.xsl" forcePlatform="true" />

	<xsl:template name="leaderContent">
		<xsl:param name="parent" />
		<xsl:param name="leadersContents">
			<xsl:apply-templates />
		</xsl:param>
		<xsl:if test="name(.) != 'leader' or string-length(normalize-space($leadersContents)) &gt; 0">
			<div>
				<xsl:choose>
					<xsl:when test="string-length(normalize-space($parent//leaderchar)) &gt; 0">
						<xsl:attribute name="class">
							<xsl:choose>
								<xsl:when test="$parent/leader[following-sibling::*[1][self::leader] or preceding-sibling::*[1][self::leader]]">
									<xsl:text>&leaderFullWidthClass; </xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&leaderTableFullWidthClass; </xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="$parent//leaderchar = '.'">&leaderDotsClass;</xsl:when>
								<xsl:when test="$parent//leaderchar = '_'">&leaderUnderlineClass;</xsl:when>
								<xsl:when test="$parent//leaderchar = '-' or $parent//leaderchar/n-private-char[@charName='TLRdashl']">&leaderDashesClass;</xsl:when>
								<xsl:otherwise>&leaderDotsClass;</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="$parent//leaderchar">
							<xsl:attribute name="class">
								<xsl:text>&leaderTableFullWidthClass; &leaderHiddenClass;</xsl:text>
							</xsl:attribute>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:for-each select="$parent/node()">
					<xsl:choose>
						<xsl:when test="string-length(normalize-space(leaderchar)) &gt; 0">
							<xsl:choose>
								<xsl:when test="leadercon and string-length(leadercon) &gt; 0">
									<div>
										<xsl:attribute name="class">
											<xsl:call-template name="leaderDotsClassName" />
										</xsl:attribute>
										<xsl:variable name="contents">
											<xsl:apply-templates select="leadercon" />
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="string-length($contents) &gt; 0">
												<xsl:copy-of select="$contents"/>
											</xsl:when>
											<xsl:otherwise>
												<![CDATA[ ]]>
											</xsl:otherwise>
										</xsl:choose>
									</div>
								</xsl:when>
								<xsl:when test="self::leader and (following-sibling::*[1][self::leader])">
									<div>
										<xsl:attribute name="class">
											<xsl:choose>
												<xsl:when test="leaderchar = '.'">&leaderDotsClass;</xsl:when>
												<xsl:when test="leaderchar = '_'">&leaderUnderlineClass;</xsl:when>
												<xsl:when test="leaderchar = '-' or leaderchar/n-private-char[@charName='TLRdashl']">&leaderDashesClass;</xsl:when>
												<xsl:otherwise>&leaderDotsClass;</xsl:otherwise>
											</xsl:choose>
										</xsl:attribute>
										<xsl:text>&#160;</xsl:text>
									</div>
								</xsl:when>
								<xsl:otherwise>&#160;</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:variable name="contents">
								<xsl:choose>
									<xsl:when test="name(.) = 'leader'">
										<xsl:apply-templates />
									</xsl:when>
									<xsl:otherwise>
										<xsl:apply-templates select="." />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:if test="string-length($contents) &gt; 0">
								<div>
									<xsl:attribute name="class">
										<xsl:call-template name="leaderDots">
										</xsl:call-template>
									</xsl:attribute>
									<xsl:if test ="$contents =','">
										<xsl:attribute name ="class">
											<xsl:text>&leaderCommaClass;</xsl:text>
										</xsl:attribute>
									</xsl:if>
									<xsl:copy-of select="$contents"/>
								</div>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="signature.line/signature">
		<xsl:if test="//leader" >
			<xsl:call-template name="leaderContent">
				<xsl:with-param name="parent" select="." />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="signature.line//leader">
		<!--<span>LeaderWLN.xsl Current Node: <xsl:value-of select="name(.)"/></span>-->
		<xsl:call-template name="leaderContent">
			<xsl:with-param name="parent" select=".."/>
		</xsl:call-template>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- Template match for unit testing purposes only -->
	<xsl:template match="leaderTest">
		<xsl:choose>
			<xsl:when test="leader">
				<xsl:call-template name="leaderContent">
					<xsl:with-param name="parent" select="." />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
