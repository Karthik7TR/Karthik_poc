<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianDate.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- NOTE - 8/5/2014 - CBM:
		We are using xsl:value-of for the names of the experts, judge, counsels for claimant and respondent.  This is because the <link> tags that are present in some
		of the documents (e.g. Doc GUID: Ic6440d8fd9ea437ce0440021280d79ee, Cite: 2011 Can. ChildSuppQ 7965) do not link to rendered documents.  These linked 
		to documents are not being transitioned now.  When they are the xsl:value-of can be changed to xsl:apply-templates to get the linking to start showing 
		up again.
	-->

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswFormsPrecedentsClass;'"/>
			</xsl:call-template>

			<!-- Render Prelim -->
			<xsl:apply-templates select ="n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite"/>
			<div class="&headnotesClass; &centerClass;">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass;'"/>
					<xsl:with-param name="contents">
						<xsl:value-of select="n-docbody/quantum/doc_heading/prelims//node()[@source ='court']" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass;'"/>
					<xsl:with-param name="contents">
						<xsl:value-of select="n-docbody/quantum/doc_heading/prelims//node()[@source = 'casena']" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass;'"/>
					<xsl:with-param name="contents">
						<xsl:apply-templates select="n-docbody/quantum/doc_heading/prelims//node()[@source = 'caseci']" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass;'"/>
					<xsl:with-param name="contents">
						<xsl:value-of select="n-docbody/quantum/doc_heading/prelims//node()[@source = 'judge']" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass;'"/>
					<xsl:with-param name="contents">
						<xsl:value-of select="n-docbody/quantum/doc_heading/prelims//node()[@source = 'date']" />
					</xsl:with-param>
				</xsl:call-template>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<!-- render the document -->
			<xsl:apply-templates select="n-docbody/quantum" />

			<xsl:call-template name="EndOfDocument" />
		</div>

	</xsl:template>

	<xsl:template match ="quantum">
		<div>
			<strong>
				<xsl:apply-templates select="caseref/digcite/@label" />
			</strong>
			<xsl:apply-templates select="caseref/digcite" />
		</div>

		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="counsels/@label"/>
			</strong>
			<xsl:for-each select ="counsels/pcounsel/counsel">
				<xsl:choose>
					<xsl:when test="position() = 1">
						<xsl:value-of select="firsname"/>
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:value-of select="lastname"/>
						<xsl:value-of select="designat"/>
						<xsl:value-of select="line"/>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:value-of select="firsname"/>
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:value-of select="lastname"/>
							<xsl:value-of select="designat"/>
							<xsl:value-of select="line"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<xsl:for-each select ="counsels/dcounsel/counsel">
				<div>
					<xsl:value-of select="firsname"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="lastname"/>
					<xsl:value-of select="designat"/>
					<xsl:value-of select="line"/>
				</div>
			</xsl:for-each>
		</div>

		<xsl:apply-templates select="enviroinfo" />

		<xsl:apply-templates select="experts" />

		<xsl:apply-templates select="breachinfo" />

		<xsl:apply-templates select="claimantinfo" />
		<xsl:apply-templates select="estateinfo" />

		<xsl:apply-templates select="csupportinfo" />
		<xsl:apply-templates select="ssupportinfo" />

		<xsl:apply-templates select="employeeinfo" />
		<xsl:apply-templates select="employerinfo" />

		<xsl:apply-templates select="defamationinfo" />

		<xsl:apply-templates select="injuryinfo" />

		<xsl:apply-templates select="awards" />

		<xsl:apply-templates select="headnote | caption" />

		<div class="&paraMainClass;">
			<xsl:apply-templates select="caseref/caseci" />
		</div>

	</xsl:template>

	<xsl:template match="enviroinfo">
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="classoff/@label"/>
			</strong>
			<xsl:apply-templates select ="classoff"/>
		</div>
		<div class="&paraMainClass;">
			<xsl:for-each select="statref">
				<div>
					<strong>
						<xsl:value-of select="@label"/>
					</strong>
					<xsl:value-of select="."/>
				</div>
			</xsl:for-each>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="impact/@label"/>
			</strong>
			<xsl:apply-templates select ="impact"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="waste/@label"/>
			</strong>
			<xsl:apply-templates select ="waste"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="afactor/@label"/>
			</strong>
			<xsl:apply-templates select ="afactor"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="mfactor/@label"/>
			</strong>
			<xsl:apply-templates select ="mfactor"/>
		</div>
	</xsl:template>

	<xsl:template match="breachinfo">
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="plaintiffparticulars/@label"/>
			</strong>
			<xsl:apply-templates select ="plaintiffparticulars"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:apply-templates select ="defendantparticulars/@label"/>
			</strong>
			<xsl:apply-templates select ="defendantparticulars"/>
		</div>
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:apply-templates select ="contracttype/@label"/>
				</strong>
				<xsl:apply-templates select ="contracttype "/>
			</div>
			<div>
				<strong>
					<xsl:apply-templates select ="penalty/@label"/>
				</strong>
				<xsl:apply-templates select ="penalty"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="claimantinfo">
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="claimantassets/@label"/>
			</strong>
			<xsl:apply-templates select="claimantassets"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="relationtodeceased/@label"/>
			</strong>
			<xsl:apply-templates select="relationtodeceased"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="relationshiplength/@label"/>
			</strong>
			<xsl:apply-templates select="relationshiplength"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="claimantage/@label"/>
			</strong>
			<xsl:apply-templates select="claimantage"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="basisofdependency/@label"/>
			</strong>
			<xsl:apply-templates select="basisofdependency"/>
		</div>
	</xsl:template>

	<xsl:template match="estateinfo">
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="estateassets/@label"/>
			</strong>
			<xsl:apply-templates select="estateassets"/>
		</div>
	</xsl:template>

	<xsl:template match="experts">
		<xsl:for-each select="expert ">
			<div class="&paraMainClass;">
				<div>
					<strong>
						<xsl:value-of select="@label"/>
					</strong>
					<xsl:value-of select="titlename"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="firsname"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="lastname"/>
				</div>
				<xsl:for-each select="expertise">
					<div>
						<strong>
							<xsl:value-of select="expertisearea/@label"/>
						</strong>
						<xsl:apply-templates select="expertisearea"/>
					</div>
					<div>
						<strong>
							<xsl:value-of select="expertisespecialty/@label"/>
						</strong>
						<xsl:apply-templates select="expertisespecialty"/>
					</div>
				</xsl:for-each>
			</div>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="csupportinfo">
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="pincome/@label"/>
				</strong>
				<xsl:apply-templates select="pincome"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="dincome/@label"/>
				</strong>
				<xsl:apply-templates select="dincome"/>
			</div>
		</div>
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="exceptioncsg/@label"/>
				</strong>
				<xsl:apply-templates select="exceptioncsg"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="nrelevantchildren/@label"/>
				</strong>
				<xsl:apply-templates select="nrelevantchildren"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="primarycustodian/@label"/>
				</strong>
				<xsl:apply-templates select="primarycustodian"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="ssupportinfo">
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="pincome/@label"/>
				</strong>
				<xsl:apply-templates select="pincome"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="dincome/@label"/>
				</strong>
				<xsl:apply-templates select="dincome"/>
			</div>
		</div>
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="ordertype/@label"/>
				</strong>
				<xsl:apply-templates select="ordertype"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="ndependentchildren/@label"/>
				</strong>
				<xsl:apply-templates select="ndependentchildren"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="relationshiplength/@label"/>
				</strong>
				<xsl:apply-templates select="relationshiplength"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="employeeinfo">
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="employeename/@label"/>
				</strong>
				<xsl:apply-templates select="employeename"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="employeeoccupationcategory/@label"/>
				</strong>
				<xsl:apply-templates select="employeeoccupationcategory"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="employeeoccupationtitle/@label"/>
				</strong>
				<xsl:apply-templates select="employeeoccupationtitle"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="employeesalary/@label"/>
				</strong>
				<xsl:apply-templates select="employeesalary"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="employeeage/@label"/>
				</strong>
				<xsl:apply-templates select="employeeage"/>
			</div>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="employmentlength/@label"/>
			</strong>
			<xsl:apply-templates select="employmentlength"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="unemploymentlength/@label"/>
			</strong>
			<xsl:apply-templates select="unemploymentlength"/>
		</div>
	</xsl:template>

	<xsl:template match="employerinfo">
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="employername/@label"/>
				</strong>
				<xsl:apply-templates select="employername"/>
			</div>
		</div>
		<div>
			<strong>
				<xsl:value-of select="employerindustry/@label"/>
			</strong>
			<xsl:apply-templates select="employerindustry"/>
		</div>
	</xsl:template>

	<xsl:template match="defamationinfo">
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="natureofpublication/@label"/>
				</strong>
				<xsl:apply-templates select="natureofpublication"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="defamationeffect/@label"/>
				</strong>
				<xsl:apply-templates select="defamationeffect"/>
			</div>
			<div class="&paraMainClass;">
				<strong>
					<xsl:value-of select="apology/@label"/>
				</strong>
				<xsl:apply-templates select="apology"/>
			</div>
			<xsl:apply-templates select="justificationplea" />
			<div>
				<strong>
					<xsl:value-of select="privilege/@label"/>
				</strong>
				<xsl:apply-templates select="privilege"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="faircomment/@label"/>
				</strong>
				<xsl:apply-templates select="faircomment"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="malice/@label"/>
				</strong>
				<xsl:apply-templates select="malice"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="occupation/@label"/>
				</strong>
				<xsl:apply-templates select="occupation"/>
			</div>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="defendantparticulars/@label"/>
			</strong>
			<xsl:apply-templates select="defendantparticulars"/>
		</div>
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="gender/@label"/>
			</strong>
			<xsl:apply-templates select="gender"/>
		</div>
	</xsl:template>

	<xsl:template match="injuryinfo">
		<div class="&paraMainClass;">
			<!-- Injuries -->
			<xsl:for-each select="injury">
				<xsl:choose>
					<xsl:when test="@type='primary'">
						<div class="&paraMainClass;">
							<strong>
								<xsl:value-of select="@label"/>
							</strong>
							<xsl:apply-templates select="."/>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<strong>
								<xsl:value-of select="@label"/>
							</strong>
							<xsl:apply-templates select="."/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</div>
		<!-- Duration -->
		<div class="&paraMainClass;">
			<xsl:for-each select="injuryduration">
				<div class="&paraMainClass;">
					<strong>
						<xsl:value-of select="@label"/>
					</strong>
					<xsl:apply-templates select="."/>
				</div>
			</xsl:for-each>
		</div>

		<!-- Cause -->
		<div class="&paraMainClass;">
			<xsl:for-each select="injurycause">
				<div class="&paraMainClass;">
					<strong>
						<xsl:value-of select="@label"/>
					</strong>
					<xsl:apply-templates select="."/>
				</div>
			</xsl:for-each>
		</div>

		<!-- Victim Details -->
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="gender/@label"/>
				</strong>
				<xsl:apply-templates select="gender"/>
			</div>
			<div>
				<strong>
					<xsl:value-of select="age/@label"/>
				</strong>
				<xsl:apply-templates select="age"/>
			</div>
		</div>

		<!-- occupation -->
		<div class="&paraMainClass;">
			<div>
				<strong>
					<xsl:value-of select="occupation/@label"/>
				</strong>
				<xsl:apply-templates select="occupation"/>
			</div>
		</div>

	</xsl:template>

	<xsl:template match="awards">
		<!-- Fines -->
		<div class="&paraMainClass;">
			<xsl:for-each select="fines">
				<div>
					<strong>
						<xsl:value-of select="fine/@label"/>
					</strong>
					<xsl:apply-templates select="fine"/>
				</div>
				<div>
					<strong>
						<xsl:value-of select="finedetails/@label"/>
					</strong>
					<xsl:apply-templates select="finedetails" />
				</div>
			</xsl:for-each>
		</div>

		<!-- Amount -->
		<div class="&paraMainClass;">
			<xsl:for-each select="award">
				<div>
					<strong>
						<xsl:value-of select="@label"/>
					</strong>
					<xsl:apply-templates select="."/>
				</div>
			</xsl:for-each>
			<div>
				<strong>
					<xsl:value-of select="noticeperiodawarded/@label"/>
				</strong>
				<xsl:apply-templates select="noticeperiodawarded" />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="headnote | caption">
		<!-- Summary -->
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="@label"/>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="justificationplea">
		<div class="&paraMainClass;">
			<strong>
				<xsl:value-of select="@label"/>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="sub">
		<sub>
			<xsl:apply-templates />
		</sub>
	</xsl:template>

	<xsl:template match="sup">
		<!--  Superscript can be ignored when found around footnote links. -->
		<xsl:choose>
			<xsl:when test="descendant::a">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<sup>
					<xsl:apply-templates />
				</sup>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="strike">
		<strike>
			<xsl:apply-templates />
		</strike>
	</xsl:template>

</xsl:stylesheet>