package com.thomsonreuters.uscl.ereader;

public final class FrontMatterFileName {
    private FrontMatterFileName() { }
    // ===== Standard names for FRONT MATTER Files =====
    /** This is the header for the entire front matter pages (Used only with the +"Anchor") */
    public static final String PUBLISHING_INFORMATION = "PublishingInformation";
    /** This is the Title Page. */
    public static final String FRONT_MATTER_TITLE = "FrontMatterTitle";
    /** This is the Copyright Page. */
    public static final String COPYRIGHT = "Copyright";

    /** This is the prefix of the file name for user entered additional front matter
     *  (add the page id to guarantee uniqueness of filename) */
    public static final String ADDITIONAL_FRONT_MATTER = "AdditionalFrontMatter";

    /** This is the ADDITIONAL INFORMATION OR RESEARCH ASSISTANCE */
    public static final String RESEARCH_ASSISTANCE = "ResearchAssistance";
    /** This is the WestlawNext GUIDE */
    public static final String WESTLAW = "Westlaw";

    /** This will be appended to the file names to generate unique anchor names for each page. */
    public static final String ANCHOR = "Anchor";
}
