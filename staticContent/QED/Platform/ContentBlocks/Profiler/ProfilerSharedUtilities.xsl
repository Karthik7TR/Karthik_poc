<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 5/13/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Profiler Contact Information -->
	<xsl:template name="ProfilerContactInformation">
		<table cellpadding="0" cellspacing="0" class="&profilerTable;" id="&expertWitnessProfileId;">
			<tbody>
				<tr>
					<!--column 1-->
					<td class="&profilerColumnOne;">
						<div class="&profilerColumn_wrapper;">
							<div class="&profilerTitle; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
								<strong>
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactInformationKey;', '&profilerContactInformation;')"/>
								</strong>
							</div>
							<xsl:apply-templates select="name.block"/>
							<xsl:apply-templates select="organization.block"/>
							<xsl:apply-templates select="address.block"/>
						</div>
					</td>

					<!--column 2-->
					<td class="&profilerColumnTwo;">
						<div class="&profilerColumn_wrapper;">
							<xsl:apply-templates select="contact.block"/>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<!-- Profiler Content -->
	<xsl:template name="ProfilerContent">
		<xsl:apply-templates select="position.block"/>
		<xsl:apply-templates select="appointed.by.block"/>
		<xsl:apply-templates select="education.block"/>
		<xsl:apply-templates select="admitted.block"/>
		<xsl:apply-templates select="court.info.block"/>
		<xsl:apply-templates select="court.jurisdiction.block"/>
		<!--<xsl:apply-templates select="affiliation.block"/> Why was this in 2 places in Web2? -->
		<xsl:apply-templates select="narrative.text.block"/>
		<xsl:apply-templates select="litigation.percent.block"/>
		<xsl:apply-templates select="certification.block"/>
		<xsl:apply-templates select="published.works.block"/>
		<xsl:apply-templates select="representative.cases.block"/>
		<xsl:apply-templates select="representative.clients.block"/>
		<xsl:apply-templates select="reference.block"/>
		<xsl:apply-templates select="class.block"/>
		<xsl:apply-templates select="honor.block"/>
		<xsl:apply-templates select="honors.block"/>
		<xsl:apply-templates select="past.positions.block"/>
		<xsl:apply-templates select="pro.bono.block"/>
		<xsl:apply-templates select="joined.firm.block"/>
		<xsl:apply-templates select="previous.firm.names.block"/>
		<xsl:apply-templates select="addl.firm.info.block/ancill.bus.prac.block"/>
		<xsl:apply-templates select="affiliation.block"/>
		<xsl:apply-templates select="addl.firm.info.block/addl.firm.personnel.block"/>
		<xsl:apply-templates select="born.block"/>
		<xsl:apply-templates select="other.office.block"/>
		<xsl:apply-templates select="language.block"/>
		<xsl:apply-templates select="frat.sorority.block"/>
		<xsl:apply-templates select="range.block"/>
		<xsl:apply-templates select="expertise.block"/>
		<xsl:apply-templates select="disclaimer.block"/>
		<xsl:apply-templates select="message.block"/>
	</xsl:template>

	<!-- Expert Witness Contact Information -->
	<xsl:template name="ExpertWitnessContactInformation">
		<xsl:param name="isRTGEnabled" select="true()"/>
		<table cellpadding="0" cellspacing="0" class="&profilerTable;" id="&expertWitnessProfileId;">
			<tbody>
				<tr>
					<td class="&profilerImage;">
						<xsl:if test="not(/Document/ImageMetadata and /Document/ImageMetadata/n-metadata/image.metadata.block/md.image.format = 'image/jpeg')">
							<img src="{$Images}&noPhotoAvailablePath;">
								<xsl:attribute name="alt">
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewNoPhotoAvailableKey;', '&ewNoPhotoAvailable;')"/>
								</xsl:attribute>
							</img>
						</xsl:if>
						<xsl:apply-templates select="figure"/>
					</td>
					<xsl:choose>
						<!-- RTG -->
						<xsl:when test="source.line = 'RTG' and $isRTGEnabled">
							<xsl:call-template name="ExpertWitnessContactInfo_RTG" />
						</xsl:when>
						<!-- Non-RTG -->
						<xsl:otherwise>
							<xsl:call-template name="ExpertWitnessContactInfo_NonRTG" />
						</xsl:otherwise>
					</xsl:choose>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="ExpertWitnessContactInfo_RTG">
		<xsl:if test="not($DeliveryMode)">
			<td class="&profilerColumnOne;">
				<div class="&profilerColumn_wrapper;">
					<div class="&profilerTitle; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
						<strong>
              <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactInformationKey;', '&profilerContactInformation;')"/>
						</strong>
					</div>
					<!-- RTG Push email form -->
					<xsl:variable name="contactInfo">
						<xsl:value-of select="name.block/name"/>
						<xsl:text>|</xsl:text>
						<xsl:value-of select="name.block/location"/>
						<xsl:text>|</xsl:text>
						<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.uuid"/>
					</xsl:variable>
					<input type="hidden" id="&expertWitnessRTGFormId;" value="{$contactInfo}"/>
				</div>
			</td>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ExpertWitnessContactInfo_NonRTG">
		<!--column 1-->
		<td class="&profilerColumnOne;">
			<div class="&profilerColumn_wrapper;">
				<div class="&profilerTitle; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
					<strong>
            <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactInformationKey;', '&profilerContactInformation;')"/>
					</strong>
				</div>

				<div>
					<xsl:apply-templates select="name.block/name"/>
				</div>
				<xsl:apply-templates select="address.block"/>
			</div>
		</td>

		<!--column 2-->
		<td class="&profilerColumnTwo;">
			<div class="&profilerColumn_wrapper;">
				<xsl:apply-templates select="contact.block"/>
			</div>
		</td>
	</xsl:template>

	<!-- Expert Witness Content-->
	<xsl:template name="ExpertWitnessContent">
		<xsl:if test="source.line = 'RTG' and $DeliveryMode">
			<div class="&headtextClass;">
				<xsl:value-of select="name.block/name"/>							
			</div>
			<div class="&paratextMainClass;">
				<xsl:value-of select="name.block/location"/>
			</div>
		</xsl:if>

		<xsl:apply-templates select="job.description"/>
		<xsl:apply-templates select="expertise.block"/>
		<xsl:apply-templates select="affiliation.block"/>
		<xsl:apply-templates select="organization.block"/>
		<xsl:apply-templates select="position.block"/>
		<xsl:apply-templates select="biography"/>
		<xsl:apply-templates select="personal.statement"/>
		<xsl:apply-templates select="witness.work.statement"/>
		<xsl:apply-templates select="resume"/>
		<xsl:apply-templates select="language.block"/>
		<xsl:apply-templates select="representative.clients.block"/>
		<xsl:apply-templates select="speaker.topics"/>
		<xsl:apply-templates select="speaker.experience.statement"/>
		<xsl:apply-templates select="times.testified"/>
		<xsl:apply-templates select="testimony.statement"/>
		<xsl:apply-templates select="area.of.expertise.development"/>
		<xsl:apply-templates select="area.of.expertise.conference"/>
		<xsl:apply-templates select="area.of.expertise.rec.book"/>
		<xsl:apply-templates select="area.of.expertise.rec.website"/>
		<xsl:apply-templates select="communication.preferences"/>
	</xsl:template>

	<xsl:template match="head" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="headtext" priority="1">
		<div class="&headtextClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Profiler Global Suppressions - should this be moved someplace else? -->
	<xsl:template match="born.block"/>
	<xsl:template match="court.jurisdiction.block"/>
	<xsl:template match="license.block"/>

</xsl:stylesheet>