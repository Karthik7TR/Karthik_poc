package com.thomsonreuters.uscl.ereader.xpp.strategy.type;

import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.ADDED_REVISED_JUDGES_CARDS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.CORRELATION_TABLE;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.DETAILED_TABLE_OF_CONTENTS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.FILLING_INSTRUCTIONS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.FRONT;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.FULL_SET_JUDGES_CARDS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.IMPOSITION_LIST;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.INDEX;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.KEY_NUMBER_TABLE;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.MAIN_CONTENT;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.SUMMARY_TABLE_OF_CONTENTS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.TABLE_OF_ADDED_CASES;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.TABLE_OF_ADDED_KEY_NUMBERS;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.TABLE_OF_ADDED_LRRE;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.TABLE_OF_CASES;
import static com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType.TABLE_OF_LRRE;
import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

public final class BundleFileTypeTest {

    @Test
    public void shouldGetBundleFileTypeByName() {
        Stream.of(BundleFileTypeTestCase.values()).forEach(testCase -> {
            //when
            final BundleFileType bundleFileType = BundleFileType.getByFileName(testCase.getFileNameExample());
            //then
            assertEquals("Wrong type recognition for file " + testCase.getFileNameExample(), testCase.getBundleFileType(), bundleFileType);
        });
    }

    private enum BundleFileTypeTestCase {
        MAIN_CONTENT_FILE(MAIN_CONTENT, "1-MOPRACV4C_I.DIVXML.main"),
        SUMMARY_TABLE_OF_CONTENTS_FILE(SUMMARY_TABLE_OF_CONTENTS, "10001-volume_1_Summary_Table_of_Contents.DIVXML.main"),
        CORRELATION_TABLE_FILE(CORRELATION_TABLE, "1_Correlation_Table.DIVXML.main"),
        INDEX_FILE(INDEX, "1001-MOPRACV4C_Volume_4C_Index.DIVXML.main"),
        ADDED_REVISED_JUDGES_CARDS_FILE(ADDED_REVISED_JUDGES_CARDS, "1_AddedRevised_Judges_Cards.DIVXML.main"),
        FULL_SET_JUDGES_CARDS_FILE(FULL_SET_JUDGES_CARDS, "1_Full_Set_Judges_Cards.DIVXML.main"),
        TABLE_OF_ADDED_LRRE_FILE(TABLE_OF_ADDED_LRRE, "1_Table_of_Added_LRRE.DIVXML.main"),
        TABLE_OF_LRRE_FILE(TABLE_OF_LRRE, "60007-volume_6_Table_of_LRRE.DIVXML.main"),
        TABLE_OF_CASES_FILE(TABLE_OF_CASES, "60008-volume_6_Table_of_Cases.DIVXML.main"),
        KEY_NUMBER_TABLE_FILE(KEY_NUMBER_TABLE, "1_Key_Number_Table.DIVXML.main"),
        TABLE_OF_ADDED_CASES_FILE(TABLE_OF_ADDED_CASES, "1_Table_of_Added_Cases.DIVXML.main"),
        SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS_FILE(SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS, "4C0002-volume_4C_Summary_and_Detailed_Table_of_Contents.DIVXML.main"),
        DETAILED_TABLE_OF_CONTENTS_FILE(DETAILED_TABLE_OF_CONTENTS, "1_Detailed_Table_of_Contents.DIVXML.main"),

        FRONT_FILE_1(FRONT, "0-LAPRACEVID_Front_vol_1.DIVXML.main"),
        FRONT_FILE_2(FRONT, "0-MOPRACV4C_Front_vol_4C.DIVXML.main"),

        FILLING_INSTRUCTIONS_FILE(FILLING_INSTRUCTIONS, "1-Filing_Instructions.DIVXML.main"),
        TABLE_OF_ADDED_KEY_NUMBERS_FILE(TABLE_OF_ADDED_KEY_NUMBERS, "1_Table_of_Added_Key_Numbers.DIVXML.main"),
        IMPOSITION_LIST_FILE(IMPOSITION_LIST, "1_Imposition_List.DIVXML.main");

        private BundleFileType bundleFileType;
        private String fileNameExample;

        BundleFileTypeTestCase(final BundleFileType bundleFileType, final String fileNameExample) {
            this.bundleFileType = bundleFileType;
            this.fileNameExample = fileNameExample;
        }

        BundleFileType getBundleFileType() {
            return bundleFileType;
        }

        String getFileNameExample() {
            return fileNameExample;
        }
    }

}
