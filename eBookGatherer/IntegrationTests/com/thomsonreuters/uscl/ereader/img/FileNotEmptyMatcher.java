package com.thomsonreuters.uscl.ereader.img;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class FileNotEmptyMatcher extends BaseMatcher<Object>
{
    public static FileNotEmptyMatcher isNotEmptyFile()
    {
        return new FileNotEmptyMatcher();
    }

    @Override
    public boolean matches(final Object item)
    {
        final File file = (File) item;
        return file.exists() && file.length() > 0L;
    }

    @Override
    public void describeTo(final Description description)
    {
        description.appendText("should be not empty file");
    }

    @Override
    public void describeMismatch(final Object item, final Description description)
    {
        final File file = (File) item;
        if (!file.exists())
        {
            description.appendText("file ").appendValue(file.getAbsolutePath()).appendText(" not exist");
        }
        else
        {
            description.appendText("file was empty");
        }
    }
}
