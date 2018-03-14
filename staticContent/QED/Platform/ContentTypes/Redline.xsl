<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:deltaxml="http://www.deltaxml.com/ns/well-formed-delta-v1"
	xmlns:dxx="http://www.deltaxml.com/ns/xml-namespaced-attribute"
	xmlns:dxa="http://www.deltaxml.com/ns/non-namespaced-attribute"
	exclude-result-prefixes="#default deltaxml dxx dxa">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<!--We need it to reuse AddDocumentClasses. But now it is needed to set up priorities to generic templates to overbid Universal values.-->
	<xsl:include href="Universal.xsl" />

	<xsl:variable name="add" select="'&redlineAddClass; &redlineDifferenceClass;'" />
	<xsl:variable name="delete" select="'&redlineDeleteClass; &redlineDifferenceClass;'" />
	<xsl:variable name="unchanged" select="'&redlineUnchangedClass;'" />
	<xsl:variable name="modify" select="'&redlineModifyClass;'" />

	<xsl:template match="/">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&redlineDocumentClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Output elements -->
	<xsl:template name="deltaXml" xmlns:deltaxml="http://www.deltaxml.com/ns/well-formed-delta-v1">

		<xsl:variable name="deltaClass">
			<xsl:choose>
				<xsl:when test="@deltaxml:deltaV2='A'">
					<xsl:value-of select="$delete"/>
				</xsl:when>
				<xsl:when test="@deltaxml:deltaV2='B'">
					<xsl:value-of select="$add"/>
				</xsl:when>
				<xsl:when test="@deltaxml:deltaV2='A=B'">
					<xsl:value-of select="$unchanged"/>
				</xsl:when>
				<xsl:when test="@deltaxml:deltaV2='A!=B'">
					<xsl:value-of select="$modify"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="class">
			<xsl:value-of select="$deltaClass" />
			<xsl:if test="@class">
				<xsl:value-of select="concat(' ', @class)" />
			</xsl:if>
		</xsl:variable>
		<xsl:if test="normalize-space($class)">
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space($class)" />
			</xsl:attribute>
		</xsl:if>

		<xsl:copy-of select="@*[not(local-name()='class' or substring-before(name(), ':')='deltaxml' or substring-before(name(), ':')='dxx' or substring-before(name(), ':')='dxa')]" />

	</xsl:template>

	<xsl:template match="n-metadata" priority="2" />

	<xsl:template match="deltaxml:textGroup" xmlns:deltaxml="http://www.deltaxml.com/ns/well-formed-delta-v1" priority="2">
		<xsl:choose>
			<xsl:when test="@deltaxml:deltaV2='A'">
				<xsl:element name="span">
					<xsl:attribute name="class">
						<xsl:value-of select="$delete"/>
					</xsl:attribute>
					<xsl:value-of select="deltaxml:text[@deltaxml:deltaV2='A']"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@deltaxml:deltaV2='B'">
				<xsl:element name="span">
					<xsl:attribute name="class">
						<xsl:value-of select="$add"/>
					</xsl:attribute>
					<xsl:value-of select="deltaxml:text[@deltaxml:deltaV2='B']"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@deltaxml:deltaV2='A=B'">
				<xsl:element name="span">
					<xsl:attribute name="class">
						<xsl:value-of select="$unchanged"/>
					</xsl:attribute>
					<xsl:value-of select="deltaxml:text[@deltaxml:deltaV2='A=B']"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@deltaxml:deltaV2='A!=B'">
				<xsl:element name="span">
					<xsl:attribute name="class">
						<xsl:value-of select="$delete"/>
					</xsl:attribute>
					<xsl:value-of select="deltaxml:text[@deltaxml:deltaV2='A']"/>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">
						<xsl:value-of select="$add"/>
					</xsl:attribute>
					<xsl:value-of select="deltaxml:text[@deltaxml:deltaV2='B']"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="deltaxml:attributes" xmlns:deltaxml="http://www.deltaxml.com/ns/well-formed-delta-v1" priority="2">
		<!-- Suppress -->
	</xsl:template>

	<xsl:template match="deltaxml:punctuation | deltaxml:word | deltaxml:space | deltaxml:text" xmlns:deltaxml="http://www.deltaxml.com/ns/well-formed-delta-v1" priority="2">
		<xsl:element name="span">
			<xsl:call-template name="deltaXml" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Default, allows HTML tags to re-render -->
	<xsl:template match="*" xmlns:deltaxml="http://www.deltaxml.com/ns/well-formed-delta-v1" priority="1">
		<xsl:element name="{name()}">
			<xsl:call-template name="deltaXml" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


	<!-- **** START: Temporarily added these 2 templates to bypass all above templates. Delete these once the testing is over **** -->

	<!--<xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" />
    </xsl:copy>
  </xsl:template>-->

	<!-- Remove these elements -->
	<!--<xsl:template match="input | img | a" />-->

	<!-- **** END: Temporarily added these 2 templates to bypass all above templates. Delete these once the testing is over **** -->

</xsl:stylesheet>