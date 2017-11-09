package com.thomsonreuters.uscl.ereader.xpp.utils.bundle;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.jetbrains.annotations.NotNull;

public final class BundleUtils {
    private BundleUtils() {
    }

    public static boolean isPocketPart(@NotNull final XppBundle bundle) {
        return "supp".equalsIgnoreCase(bundle.getProductType());
    }
}
