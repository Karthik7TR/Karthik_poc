<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<!-- This is used by EDGAR because large (>80mb) documents render very slow due to the number of tables. -->
	<xsl:template match="table" priority="1">
		<table style="width: 100%">
			<xsl:apply-templates  />
		</table>
	</xsl:template>

	<xsl:template match="tgroup" priority="1">
		<tr>
			<xsl:for-each select="colspec">
				<th>
					<xsl:if test="@colwidth">
						<xsl:attribute name="width">
							<xsl:value-of select="@colwidth" />
						</xsl:attribute>
					</xsl:if>
				</th>
			</xsl:for-each>
		</tr>

		<xsl:apply-templates select="tbody" />
	</xsl:template>

	<!-- Storing in variables < 1sec -->
	<xsl:template match="row">
		<xsl:variable  select ="ancestor::tbl/@row-shade" name="alternate"/>
		<tr>
			<xsl:if test ="$alternate = 'alt'">
				<xsl:attribute name="class">
					<xsl:value-of select="concat('co_stripeRow',(position() mod 2)+1)"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:for-each select="entry">
				<xsl:variable name="colname" select="number(./@colname)" />

				<!-- Previous Element Variables -->
				<xsl:variable name="previous" select="preceding-sibling::entry[1]" />
				<xsl:variable name="previousNameEnd" select="number($previous/@nameend)" />
				<xsl:variable name="previousColspan">
					<xsl:choose>
						<xsl:when test="string(number($previousNameEnd)) != 'NaN'">
							<xsl:value-of select="$previousNameEnd"/>
						</xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="previousColumn" select="number($previous/@colname)" />

				<!-- Calculate actual cell position -->
				<xsl:variable name="pos">
					<xsl:choose>
						<xsl:when test="$previousColspan > 1">
							<xsl:value-of select="$previousColspan + 1"/>
						</xsl:when>
						<xsl:when test="string(number($previousColumn)) != 'NaN'">
							<xsl:value-of select="$previousColumn + 1"/>
						</xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<!-- Calculate colspan -->
				<xsl:variable name="colspan">
					<xsl:choose>
						<xsl:when test="string(number(@nameend)) != 'NaN'">
							<xsl:value-of select="number(@nameend) - $colname + 1" />
						</xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<!-- Add empty cells since not all are in xml -->
				<xsl:call-template name="addEmptyCells">
					<xsl:with-param name="currentColumn" select="$colname" />
					<xsl:with-param name="renderedColumns" select="$pos" />
					<xsl:with-param name="iteration" select="0" />
				</xsl:call-template>

				<td>
					<xsl:if test="$colspan > 1">
						<xsl:attribute name="colspan">
							<xsl:value-of select="$colspan"/>
						</xsl:attribute>
					</xsl:if>

					<xsl:if test="@align">
						<xsl:attribute name="align">
							<xsl:value-of select="@align"/>
						</xsl:attribute>
					</xsl:if>

					<!-- This could probably be removed with little impact besides a lot less chunks.-->
					<!-- 50chunks 0secs -->
					<!--<xsl:if test="@valign">
						<xsl:attribute name="valign">
							<xsl:value-of select="@valign"/>
						</xsl:attribute>
					</xsl:if>-->

					<!-- Gives empty rows a 'break' so that it shows alternating colors -->
					<xsl:choose>
						<xsl:when test ="$alternate = 'alt'">
							<xsl:choose>
								<xsl:when test="normalize-space()">
									<xsl:apply-templates />
								</xsl:when>
								<xsl:when test="not($previous)">
									<br />
								</xsl:when>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates />
						</xsl:otherwise>
					</xsl:choose>
				</td>

				<!-- xml sometimes stops short of the total number of columns, this will fill it in.-->
				<xsl:variable name="following" select="following-sibling::entry[1]" />
				<xsl:if test="not($following)">
					<xsl:variable select ="ancestor::tgroup" name="tgroup"/>
					<xsl:call-template name="addEmptyCells">
						<xsl:with-param name="currentColumn" select="count($tgroup/colspec)" />
						<xsl:with-param name="renderedColumns" select="($pos + $colspan -1)" />
						<xsl:with-param name="iteration" select="0" />
					</xsl:call-template>
				</xsl:if>

			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template name="addEmptyCells">
		<xsl:param name="currentColumn" />
		<xsl:param name="renderedColumns" />
		<xsl:param name="iteration" />

		<xsl:choose>
			<xsl:when test="$currentColumn > $renderedColumns">
				<xsl:call-template name="addEmptyCells">
					<xsl:with-param name="currentColumn" select="$currentColumn" />
					<xsl:with-param name="renderedColumns" select="$renderedColumns + 1" />
					<xsl:with-param name="iteration" select="$iteration + 1" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$iteration > 0">
				<td>
					<xsl:attribute name="colspan">
						<xsl:value-of select="$iteration"/>
					</xsl:attribute>
				</td>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
