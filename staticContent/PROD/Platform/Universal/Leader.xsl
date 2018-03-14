<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="leaderContent">
		<xsl:param name="parent" />
		<div>
			<xsl:variable name ="leaders" select="$parent/leader"/>
			<xsl:choose>
				<xsl:when test="$leaders">
					<xsl:attribute name="class">
						<xsl:choose>
							<xsl:when test="$leaders[following-sibling::*[1][self::leader] or preceding-sibling::*[1][self::leader]]">
								<xsl:text>&leaderFullWidthClass; </xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>&leaderTableFullWidthClass; </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:if test="$leaders/leaderchar">
							<xsl:choose>
								<xsl:when test="string-length(normalize-space($leaders/leaderchar)) &gt; 0">
									<xsl:choose>
										<xsl:when test="$leaders/leaderchar = '.'">&leaderDotsClass;</xsl:when>
										<xsl:when test="$leaders/leaderchar = '_'">&leaderUnderlineClass;</xsl:when>
										<xsl:when test="$leaders/leaderchar = '-' or $leaders/leaderchar/n-private-char[@charName='TLRdashl']">&leaderDashesClass;</xsl:when>
										<xsl:otherwise>&leaderDotsClass;</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&leaderHiddenClass;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:attribute>
					<xsl:for-each select="$parent/node()[following-sibling::leader or self::leader]">
						<xsl:choose>
							<xsl:when test="processing-instruction()"/>
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
<xsl:if test="$contents = '.' or $contents = '_' or $contents = '-'">
	<xsl:attribute name="style">
		<xsl:text>opacity:0</xsl:text>
	</xsl:attribute>
</xsl:if>
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
									<xsl:when test="self::leader and following-sibling::*[1][self::leader]">
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
									<xsl:otherwise>
										<xsl:text>&#160;</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:variable name="contents">
									<xsl:apply-templates select="." />
								</xsl:variable>
								<xsl:if test="string-length($contents) &gt; 0">
									<div>
										<xsl:attribute name="class">
											<xsl:call-template name="leaderWrapperClassName">
											</xsl:call-template>
										</xsl:attribute>
<xsl:if test="$contents = '.' or $contents = '_' or $contents = '-'">
	<xsl:attribute name="style">
		<xsl:text>opacity:0</xsl:text>
	</xsl:attribute>
</xsl:if>
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
					<xsl:variable name="closure">
						<xsl:apply-templates select="$parent/node()[preceding-sibling::leader and not(following-sibling::leader)][name() != 'leader']"/>
					</xsl:variable>
					<xsl:if test="string-length(string($closure)) &gt; 0">
						<div>
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="$leaders[last()][(string-length(normalize-space(leaderchar)) &gt; 0 or $leaders/leaderchar/n-private-char[@charName='TLRdashl']) and leadercon]">
										<xsl:call-template name="leaderDotsClassName"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="leaderWrapperClassName"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:copy-of select="$closure"/>
						</div>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$parent/node()"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template name="leaderDotsClassName">
		<xsl:text>&leaderWrapperClass; &floatRight;</xsl:text>
	</xsl:template>

	<xsl:template name="leaderWrapperClassName">
		<xsl:text>&leaderWrapperClass;</xsl:text>
	</xsl:template>

	<xsl:template name="leaderDots">
		<xsl:param name="precedingSibling" select="preceding-sibling::*[1][name()]"/>
		<xsl:choose>
			<xsl:when test="$precedingSibling[name() = 'leader'] and string-length(normalize-space($precedingSibling/leaderchar)) &gt; 0 and $precedingSibling/leadercon">
				<xsl:call-template name="leaderDotsClassName" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="leaderWrapperClassName" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="signature.line//leader">
		<xsl:call-template name="leaderContent">
			<xsl:with-param name="parent" select=".."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="added.material[leader]">
		<ins>
			<xsl:call-template name="leaderContent">
				<xsl:with-param name="parent" select="."/>
			</xsl:call-template>
		</ins>
	</xsl:template>
	
		<xsl:template match="deleted.material[leader]">
		<del>
			<xsl:call-template name="leaderContent">
				<xsl:with-param name="parent" select="."/>
			</xsl:call-template>
		</del>
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
