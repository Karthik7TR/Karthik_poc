<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CustomFunctions.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	
	<!-- Template matches for unit testing purposes only -->
	<xsl:template match="/repeatTest">
		<xsl:call-template name="repeat">
			<xsl:with-param name="contents" select="contents/node()" />
			<xsl:with-param name="repetitions" select="repetitions/node()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/replaceTest">
		<xsl:call-template name="replace">
			<xsl:with-param name="string" select="string" />
			<xsl:with-param name="pattern" select="pattern" />
			<xsl:with-param name="replacement" select="replacement/node()" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="/toUpperTest">
		<xsl:call-template name="upper-case">
			<xsl:with-param name="string" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/toLowerTest">
		<xsl:call-template name="lower-case">
			<xsl:with-param name="string" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/endsWithTest">
		<xsl:call-template name="ends-with">
			<xsl:with-param name="string1" select="string1" />
			<xsl:with-param name="string2" select="string2" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/escapeToClassTest">
		<xsl:call-template name="escape-to-class">
			<xsl:with-param name="xmlElementName" select="xmlElementName" />
			<xsl:with-param name="prefix" select="prefix" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/trimTest">
		<xsl:call-template name="trim">
			<xsl:with-param name="string" select="string" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/trimStartTest">
		<xsl:call-template name="trim-start">
			<xsl:with-param name="string" select="string" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/trimEndTest">
		<xsl:call-template name="trim-end">
			<xsl:with-param name="string" select="string" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/normalizeSpaceWithoutTrimmingTest">
		<xsl:call-template name="normalize-space-without-trimming">
			<xsl:with-param name="string" select="string" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/getXpathOfNodeTest">
		<xsl:choose>
			<xsl:when test="@selectionMode = 'root'">
				<xsl:call-template name="get-xpath-of-node">
					<xsl:with-param name="node" select="/" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@selectionMode = 'namespace'">
				<xsl:call-template name="get-xpath-of-node">
					<xsl:with-param name="node" select=".//namespace::currentNamespace" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@selectionMode = 'attribute'">
				<xsl:call-template name="get-xpath-of-node">
					<xsl:with-param name="node" select=".//currentElement[last()]/@currentAttribute" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="get-xpath-of-node">
					<xsl:with-param name="node" select=".//currentElement[last()]/node()[last()]" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>