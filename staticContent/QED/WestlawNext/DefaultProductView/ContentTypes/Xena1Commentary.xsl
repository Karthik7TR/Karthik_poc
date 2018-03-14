<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="XenaGlobal.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="PreformattedTextCleaner.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="dbidText" select="/Document/n-docbody/doc/dbid[1]//text()"/>
	<xsl:variable name="isABALGL" select="$dbidText = 'ABALGL'" />
	<xsl:variable name="isABIDOC" select="$dbidText = 'ABIDOC'" />
	<xsl:variable name="isAHLAPUB" select="$dbidText = 'AHLAPUB'" />
	<xsl:variable name="isAMLAWINS" select="$dbidText = 'AMLAWINS'" />
	<xsl:variable name="isATLAAID" select="$dbidText = 'ATLAAID'" />
	<xsl:variable name="isATLAPUB" select="$dbidText = 'ATLAPUB'" />
	<xsl:variable name="isBTSTEXT2" select="$dbidText = 'BTSTEXT2'" />
	<xsl:variable name="isCMATEXT" select="$dbidText = 'CMATEXT'" />
	<xsl:variable name="isEXPTTEXT" select="$dbidText = 'EXPTTEXT'" />
	<xsl:variable name="isGLCLE" select="$dbidText = 'GLCLE'" />
	<xsl:variable name="isICCOMP06" select="$dbidText = 'ICCOMP06'" />
	<xsl:variable name="isICCOMP09" select="$dbidText = 'ICCOMP09'" />
	<xsl:variable name="isICCOMPUB" select="$dbidText = 'ICCOMPUB'" />
	<xsl:variable name="isNAELACLE" select="$dbidText = 'NAELACLE'" />
	<xsl:variable name="isNAELATOC" select="$dbidText = 'NAELATOC'" />
	<xsl:variable name="isNBICLE" select="$dbidText = 'NBICLE'" />
	<xsl:variable name="isNEOFORM" select="$dbidText = 'NEOFORM'" />
	<xsl:variable name="isNEOFRMKY" select="$dbidText = 'NEOFRMKY'" />
	<xsl:variable name="isODENFORM" select="$dbidText = 'ODENFORM'" />
	<xsl:variable name="isPNETTEXT" select="$dbidText = 'PNETTEXT'" />
	<xsl:variable name="isPRLAWIN2" select="$dbidText = 'PRLAWIN2'" />
	<xsl:variable name="isROCKYMT" select="$dbidText = 'ROCKYMT'" />
	<xsl:variable name="isRPDFPUB1" select="$dbidText = 'RPDFPUB1'" />
	<xsl:variable name="isRTNKTEXT" select="$dbidText = 'RTNKTEXT'" />
	<xsl:variable name="isRTNOTEXT" select="$dbidText = 'RTNOTEXT'" />
	<xsl:variable name="isSCML" select="$dbidText = 'SCML'" />
	<xsl:variable name="isSCMLAID" select="$dbidText = 'SCMLAID'" />
	<xsl:variable name="isSTCLECOA" select="$dbidText = 'STCLECOA'" />
	<xsl:variable name="isSTCLES" select="$dbidText = 'STCLES'" />
	<xsl:variable name="isSTEDTEXT" select="$dbidText = 'STEDTEXT'" />

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:if test="$IsCommentaryEnhancementMode">
						<xsl:value-of select="' &commentaryDocumentEnhancementClass;'"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates  />
			<!--Section: Citation-->
			<xsl:choose>
				<xsl:when test="contains(/Document/document-data/collection/text(),'w_3rd_amlawins') or contains(/Document/document-data/collection/text(),'w_3rd_atlapub') or contains(/Document/document-data/collection/text(),'w_3rd_prlawin2')">
					<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation" />
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo"/>
		</div>
	</xsl:template>

	<!--Section: Title for w_3rd_glcle colleciton-->
	<xsl:template match="n-docbody/doc/tndx.title">
		<xsl:variable name="displayTitle">
			<xsl:if test ="./text()">
				<xsl:value-of select="./text()"/>
			</xsl:if>
			<xsl:if test ="parent::node()/tndx.page">
				<xsl:value-of select="parent::node()/tndx.page/text()"/>
			</xsl:if>
		</xsl:variable>
		<div class="&centerClass;">
			<xsl:if test="string-length($displayTitle) &gt; 0">
				<xsl:value-of select="$displayTitle" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y'] | md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y']" />
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
					<span>
						<xsl:if test="string-length($id) &gt; 0">
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&internalLinkIdPrefix;', $id)"/>
							</xsl:attribute>
						</xsl:if>
						<a href="{concat('#&internalLinkIdPrefix;', $refid)}" class="&internalLinkClass;">
							<xsl:copy-of select="$contents"/>
							<xsl:comment>anchor</xsl:comment>
						</a>
					</span>
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

	<xsl:template match="cr">
		<xsl:choose>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isROCKYMT">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:when test="$isICCOMPUB or $isNAELACLE or $isICCOMP06 or $isICCOMP09">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="cr1">
		<xsl:choose>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d1"/>
			</xsl:when>
				<xsl:when test="$isSTCLES or $isSTCLECOA or $isRPDFPUB1 or $isSTEDTEXT or $isRTNOTEXT or $isCMATEXT or $isRTNKTEXT or $isATLAPUB or $isPNETTEXT or $isATLAAID ">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:when test="$isATLAPUB or $isATLAAID">		
				<xsl:call-template name="d7"/>
			</xsl:when>		
			<xsl:otherwise>		
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="cr1[contains(/Document/document-data/collection/text(),'w_3rd_pnettext')]" priority="2"/>
	
	<xsl:template match="cr2">
		<xsl:choose>
			<xsl:when test="$isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isSTEDTEXT or $isCMATEXT">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="cs">
		<xsl:choose>
			<xsl:when test="$isSCMLAID">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isATLAPUB or $isATLAAID">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dj">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dl">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d4"/>
			
			</xsl:when>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d2"/>
			
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d3"/>
			
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dl1">
		<xsl:choose>
			<xsl:when test="$isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dlc1">
		<xsl:choose>
			<xsl:when test="$isEXPTTEXT or $isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dlcl">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dlh">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa0">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isODENFORM or $isNEOFORM or $isNEOFRMKY or $isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC or $isNAELATOC or $isNAELACLE or $isNBICLE or $isEXPTTEXT or $isSCMLAID or $isSCML or $isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABALGL">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa2">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa3">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="-1" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa4">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa5">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa6">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa7">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="12" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa8">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="14" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="14" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="14" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="14" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpa9">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="16" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="16" />
					<xsl:with-param name="line" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="16" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac0">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac1">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2 or $isPNETTEXT">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac2">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB or $isBTSTEXT2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac3">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac4">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac5">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac6">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac7">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac8">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="14" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="14" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="14" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpac9">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="16" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="16" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="16" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpcmc1">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpcmc2">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpcmc3">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpfmc1">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpfmc2">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpfmc3">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpfmc4">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpt1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpt2">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpt3">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpt4">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpt5">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dpt6">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dptk1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dptk2">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dptk3">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dptk4">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dptk5">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fc">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fn">
		<xsl:choose>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna">
		<xsl:choose>
			<xsl:when test="$isABIDOC or $isAMLAWINS or $isNBICLE or $isROCKYMT">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL or $isPRLAWIN2">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna2">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL or $isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna3">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL or $isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna4">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL or $isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna5">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL or $isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna6">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="9" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isNAELATOC or $isNAELACLE or $isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="9" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fna7">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnac2">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnac3">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnac4">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnac5">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnax">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d7">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isNAELATOC or $isNAELACLE or $isNBICLE">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fndj1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fndj2">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnhca">
		<xsl:choose>
			<xsl:when test="$isABALGL">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isPRLAWIN2 or $isROCKYMT">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d5"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnhla">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isPRLAWIN2 or $isROCKYMT">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnti">
		<xsl:choose>
			<xsl:when test="$isABIDOC or $isPRLAWIN2">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isROCKYMT">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnti1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fnti2">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="fntix">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isROCKYMT">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hca">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:when test="$isEXPTTEXT">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d5"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hcb">
		<xsl:choose>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hcb1">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hcb4">
		<xsl:choose>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hcb5">
		<xsl:choose>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hcb6">
		<xsl:choose>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hla1">
		<xsl:choose>
			<xsl:when test="$isEXPTTEXT or $isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isGLCLE or $isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d9">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hla2">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isATLAPUB or $isATLAAID">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d9">
					<xsl:with-param name="line" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hla3">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isGLCLE or $isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hla4">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isAHLAPUB or $isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="6" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hla5">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hla6">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlcm">
		<xsl:choose>
			<xsl:when test="$isAHLAPUB">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlcm1">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlcm2">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d7"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlcm3">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlfm1">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlfm2">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d7"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlfm3">
		<xsl:choose>
			<xsl:when test="$isBTSTEXT2">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlt">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hlt1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isABALGL">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isGLCLE or $isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hltk">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d8"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpa6">
		<xsl:choose>
			<xsl:when test="$isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpt1">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="2" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpt2">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpt3">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpt4">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="16" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="16" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpt5">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="20" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="20" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="10" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="hpt6">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="24" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="24" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
					<xsl:with-param name="line" select="-2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="img">
		<xsl:choose>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="imgid">
		<xsl:choose>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d5"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="lsta1">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="4" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="lsta2">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="8" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="lsta3">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="12" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="lsta4">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="16" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="16" />
					<xsl:with-param name="line" select="-4" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="msg">
		<xsl:choose>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d7"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="nt">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d9">
					<xsl:with-param name="lm" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="5" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="nta1">
		<xsl:choose>
			<xsl:when test="$isPNETTEXT">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="5" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9">
					<xsl:with-param name="lm" select="5" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="nta2">
		<xsl:choose>
			<xsl:when test="$isPNETTEXT">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9">
					<xsl:with-param name="lm" select="10" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="pagbr">
		<xsl:choose>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isPRLAWIN2 or $isROCKYMT">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="paratext">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA or $isGLCLE or $isABALGL or $isPRLAWIN2">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="line" select="2" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="pdfid">
		<xsl:choose>
			<xsl:when test="$isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d5"/>
			</xsl:when>
			<xsl:when test="$isICCOMPUB or $isICCOMP06 or $isICCOMP09">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isROCKYMT">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="ph56">
		<xsl:choose>
			<xsl:when test="$isICCOMPUB or $isICCOMP06 or $isICCOMP09">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="6" />
					<xsl:with-param name="line" select="-1" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="rccl">
		<xsl:choose>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="so">
		<xsl:choose>
			<xsl:when test="$isSTCLECOA">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY or $isSTCLES">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d5"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="so1">
		<xsl:choose>
			<xsl:when test="$isABIDOC or $isSCMLAID or $isSCML">
				<xsl:call-template name="d2"/>
			</xsl:when>
			<xsl:when test="$isAMLAWINS or $isNBICLE or $isROCKYMT">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:when test="$isPRLAWIN2">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d3"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="so2">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d2"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d4"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="so3">
		<xsl:choose>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">
				<xsl:call-template name="d3"/>
			</xsl:when>
			<xsl:when test="$isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d5"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d4"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="spa">
		<xsl:choose>
			<xsl:when test="$isATLAPUB or $isATLAAID">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="ta">
		<xsl:choose>
			<xsl:when test="$isABIDOC or $isAMLAWINS or $isNBICLE or $isPRLAWIN2 or $isROCKYMT">
				<xsl:call-template name="d2"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="ta.gen">
		<xsl:choose>
			<xsl:when test="$isNAELATOC or $isNAELACLE or $isAMLAWINS">
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tai">
		<xsl:choose>
			<xsl:when test="$isSTEDTEXT or $isCMATEXT or $isRPDFPUB1 or $isRTNKTEXT or $isRTNOTEXT">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tbl">
		<xsl:choose>
			<xsl:when test="$isAMLAWINS or $isPRLAWIN2 or $isROCKYMT">
				<xsl:call-template name="d2"/>
			</xsl:when>
			<xsl:when test="$isATLAPUB or $isATLAAID or $isABALGL">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d7"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="ti">
		<xsl:choose>
			<xsl:when test="$isSTCLES or $isSTCLECOA">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d5"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(following-sibling::hg3 or following-sibling::ti2 or following-sibling::til or following-sibling::snl)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>
	<xsl:template match="ti2">
		<xsl:choose>
			<xsl:when test="$isABIDOC">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d3"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(following-sibling::hg3 or following-sibling::ti or following-sibling::til or following-sibling::snl)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>
	<xsl:template match="til">
		<xsl:choose>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d1"/>
			</xsl:when>
			<xsl:when test=" $isCMATEXT">
				<xsl:call-template name="d4"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d9"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(following-sibling::hg3 or following-sibling::ti or following-sibling::ti2 or following-sibling::snl)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tla">
		<xsl:choose>
			<xsl:when test="$isABIDOC or $isBTSTEXT2 or $isNAELATOC or $isNAELACLE">
				<xsl:call-template name="d7"/>
			</xsl:when>
			<xsl:when test="$isSCMLAID or $isSCML">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="wrh">
		<xsl:choose>
			<xsl:when test="$isODENFORM or $isNEOFORM or $isNEOFRMKY">			
				<xsl:call-template name="d6"/>
			</xsl:when>
			<xsl:otherwise>		
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="snl">
		<xsl:choose>
			<xsl:when test="$isICCOMPUB or $isICCOMP06 or $isICCOMP09">
				<xsl:call-template name="d9"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d1"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(following-sibling::hg3 or following-sibling::ti2 or following-sibling::til or following-sibling::ti)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!-- Replace hg3 tags with d2 tags except in a few specific cases. -->
	<xsl:template match="hg3">
		<xsl:call-template name="d2"/>
		<xsl:if test="not(following-sibling::snl or following-sibling::ti2 or following-sibling::til or following-sibling::ti)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dpfmspc1">
		<div class="&simpleContentBlockClass; &preformattedTextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="dpfmspc1//text()" priority="1">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="notPreformatted" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dpx">
		<xsl:if test="not(contains(//d9/text(), ', type L and ENTER'))">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="dpx.gen">
		<xsl:if test="not(contains(text(), ', type L and ENTER'))">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>
	
	<!-- Section: Footer Citation -->
	<xsl:template match="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation">
		<div class="&citationClass;">
			<xsl:apply-templates select ="md.primarycite/md.primarycite.info"/>
	  </div>
	</xsl:template>
	
	<xsl:template match="starpage.anchor[contains('|w_3rd_clecorsk|', concat('|', /Document/document-data/collection, '|'))]" priority="2" />

	<xsl:template match="dpa1[child::leader] | dpa2[child::leader] | dpa3[child::leader]" priority="1">
		<div>
			<xsl:call-template name="leaderContent">
				<xsl:with-param name="parent" select="."/>
			</xsl:call-template>
		</div>
		<br/>
	</xsl:template>

</xsl:stylesheet>
