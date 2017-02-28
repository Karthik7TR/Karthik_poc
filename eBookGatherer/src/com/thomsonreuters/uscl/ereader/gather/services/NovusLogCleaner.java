package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class NovusLogCleaner
{
    private static final Logger log = LogManager.getLogger(NovusLogCleaner.class);

    private static final String NOVUS_LOG_FOLDER = "/home/asadmin/";

    /*
     * Clean up Novus generated Log files every 12 hours so home directory disk space does
     * not fill up
     */
    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    public void cleanupOldNovusFiles()
    {
        final File dir = new File(NOVUS_LOG_FOLDER);

        if (dir.isDirectory())
        {
            final FileFilter fileFilter = new FileFilter()
            {
                @Override
                public boolean accept(final File dir)
                {
                    if (dir.isFile())
                    {
                        final String name = dir.getName();
                        if (StringUtils.isNotBlank(name))
                        {
                            return name.matches("^MC-(Client|Prod).txt.\\d{2}-\\d{2}-\\d{4}$");
                        }
                    }

                    return false;
                }
            };

            final File[] files = dir.listFiles(fileFilter);

            for (final File file : files)
            {
                try
                {
                    log.debug("Novus Log Clean-up: " + file.getCanonicalPath());
                    file.delete();
                }
                catch (final IOException e)
                {
                    log.debug("Novus Log Clean-up failed.", e);
                }
            }
        }
    }
}
