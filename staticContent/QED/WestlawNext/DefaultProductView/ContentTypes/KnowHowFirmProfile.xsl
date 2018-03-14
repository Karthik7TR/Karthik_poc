<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="KnowHow.xsl"/>

	<!-- Variable to check if there are any contributors-->
	<xsl:variable name="IsContributors">
		<xsl:choose>
			<xsl:when test="//contributors/child::*[name() = 'contributor']">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!--Document body section-->
	<xsl:template match="n-docbody" priority="1">
		<xsl:apply-templates select="*/prelim | prelim" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<div class="&co24Percent; &coColumn;">
			<div class="&khDivision;" id="contributorPage_contacts">
				<xsl:choose>
					<xsl:when test="$PreviewMode = 'True'">
						<xsl:apply-templates select="/*/ImageMetadata/n-metadata" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="//image.block/image.link" />
					</xsl:otherwise> 
				</xsl:choose>
				<xsl:apply-templates select="*/contact.information | contact.information" />
			</div>
		</div>
		<div id="&coDocContentBody;" class="&co76Percent; &coColumn;">
				<xsl:apply-templates select="*/abstract | abstract"/>
				<xsl:apply-templates select="*/body/*/*[not(self::image.block)]" />
				<xsl:apply-templates select="*/contributors" />
		</div>
		<div class="&co24Percent; &coColumn;">
			<div id="&khRelatedContentOffset;"></div>
		</div>

		<xsl:call-template name="EndOfDocument" />
	</xsl:template>

	<!-- This is used for suppressing the knowhow image block-->
	<xsl:template match="para/paratext[child::image.block]">
	</xsl:template>
	
	<!--added because not available in Universal-->
	<xsl:template match="strong">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>	

	<!-- Contributors section-->
	<xsl:template match="contributors">
		<xsl:if test="$IsContributors = 'true'">
			<div id="&coDocContributorsContainer;" class="&khBox;">
				<h2>&contributorText;</h2>
				<div class="&twoColumnClass;">
				<xsl:for-each select=".//contributor">
					<xsl:choose>
						<xsl:when test="position() mod 2 != 0">
							<span class="&coColumn;">
									<xsl:call-template name="anchorTag">
										<xsl:with-param name="node" select="."/>
									</xsl:call-template>
							</span>
						</xsl:when>
						<xsl:otherwise>
							<span class="&coColumn;">
								<xsl:call-template name="anchorTag">
									<xsl:with-param name="node" select="../contributor[position()+1]"/>
								</xsl:call-template>
							</span>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="anchorTag">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Image template-->
	<xsl:template match="//image.block/image.link | /*/ImageMetadata/n-metadata">
		<div class="&paraMainClass;">
			<xsl:variable name="targetType" select="@ttype" />
			<xsl:variable name="imageClassName" select="'&imageClass;'" />
			<xsl:variable name="mimeType" select="'&jpgMimeType;'" />
			<xsl:variable name="height" select="59" />
			<xsl:variable name="width" select="200" />
			<xsl:variable name="guid">
				<xsl:choose>
					<xsl:when test="string-length(@target) &gt; 30">
						<xsl:value-of select="@target" />
					</xsl:when>
					<xsl:when test="string-length(@tuuid) &gt; 30">
						<xsl:value-of select="@tuuid" />
					</xsl:when>
					<xsl:when test="string-length(@guid) &gt; 30">
						<xsl:value-of select="@guid" />
					</xsl:when>					
					<xsl:otherwise>
						<xsl:text>NotValidImage</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="blobSrc">
				<xsl:call-template name="createBlobLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="mimeType" select="$mimeType"/>
					<xsl:with-param name="forImgTag" select="$DeliveryMode" />
					<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="blobHref">
				<xsl:call-template name="createBlobLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="mimeType" select="$mimeType"/>
					<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				</xsl:call-template>
			</xsl:variable>

				<a class="&imageLinkClass;" href="{$blobHref}" type="{$mimeType}">
					<xsl:call-template name="buildBlobImageElement">
						<xsl:with-param name="src" select="$blobSrc"/>
						<xsl:with-param name="height" select="$height"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="class" select="$imageClassName"/>
					</xsl:call-template>
				</a>
		</div>
	</xsl:template>

	<!-- Contact Information-->
	<xsl:template match="contact.information">
			<xsl:apply-templates/>
	</xsl:template>
	
		<xsl:template match="contact.information/phone">
			<div class="&simpleContentBlockClass;">
				<b>
					<xsl:text>&tText;&nbsp;</xsl:text>
				</b>
					<xsl:apply-templates/>
			</div> 
		</xsl:template>

		<xsl:template match="contact.information/fax">
			<div class="&simpleContentBlockClass;">
				<b>
					<xsl:text>&fText;&nbsp;</xsl:text>
				</b>
					<xsl:apply-templates/>
			</div>  
		</xsl:template>

		<xsl:template match="contact.information/web.address">
			<div class="&simpleContentBlockClass;">
				<b>
					<xsl:text>&wText;&nbsp;</xsl:text>
				</b>
				<a target="_new" class="&pauseSessionOnClickClass;">
					<xsl:attribute name="href">
						<xsl:value-of select="@href"/>
					</xsl:attribute>
					<xsl:attribute name="title">
						<xsl:value-of select="@href"/>
					</xsl:attribute>
					<xsl:apply-templates />
				</a>
			 </div> 
		</xsl:template>
	
	<!-- Practice Area section-->
	<xsl:template match="practice.area">
		<xsl:value-of select ="."/>
		<xsl:if test="not(position()=last())">
			<br/>
		</xsl:if>
	</xsl:template>

	<!-- Remove the last viewed info from the document-->
	<xsl:template name="LastViewed">
	</xsl:template>

</xsl:stylesheet>
