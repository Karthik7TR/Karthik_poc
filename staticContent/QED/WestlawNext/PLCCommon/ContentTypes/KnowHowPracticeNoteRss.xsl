<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:output method="xml" encoding="utf-8" indent="yes" omit-xml-declaration="yes" />

    <xsl:template match="/">
    <feed xmlns="http://www.w3.org/2005/Atom">
      <xsl:variable name="documentGuid">
        <xsl:value-of select="//md.uuid"/>
      </xsl:variable>
      <title>
        <xsl:value-of select="//Document/document-data/title"/>
      </title>
      <subtitle>
          <xsl:apply-templates select="//Document/n-docbody/practice.note/abstract" />
      </subtitle>
      <xsl:element name="link">
        <xsl:attribute name="href">
          <xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
        </xsl:attribute>
      </xsl:element>
      <xsl:variable name="clipdate">
        <xsl:value-of select="//Document/n-metadata/prism-clipdate"/>
      </xsl:variable>
      <xsl:variable name="updatedDate">
        <xsl:value-of select="substring($clipdate, 1, 4)"/>-<xsl:value-of select="substring($clipdate, 5, 2)"/>-<xsl:value-of select="substring($clipdate, 7, 2)"/>T<xsl:value-of select="substring($clipdate, 9, 2)"/>:<xsl:value-of select="substring($clipdate, 11, 2)"/>:<xsl:value-of select="substring($clipdate, 13, 2)"/>Z</xsl:variable>
      <updated>
        <xsl:value-of select="$updatedDate"/>
      </updated>
      <id>
        <xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
      </id>
      <author>
        <name>Practical Law Brexit</name>
      </author>
      <entry>
        <title>Tracker updated on <xsl:value-of select="$updatedDate"/></title>
        <xsl:element name="link">
          <xsl:attribute name="href">
            <xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
          </xsl:attribute>
        </xsl:element>
        <id>
          <xsl:value-of select="$clipdate"/>
        </id>
        <updated>
          <xsl:value-of select="$updatedDate"/>
        </updated>
        <summary><xsl:value-of select="//Document/document-data/title"/> has been updated – see entry marked “New” in the date column.</summary>
      </entry>
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
</xsl:stylesheet>
