package com.thomsonreuters.uscl.ereader.gather.services;

public @interface NovusRetryCounter {
    /**
     * Retry count. default value 3
     */
   public static int docRetryCount =  3;
   public static int tocRetryCount =  3;
   public static int nortRetryCount =  3;
}


