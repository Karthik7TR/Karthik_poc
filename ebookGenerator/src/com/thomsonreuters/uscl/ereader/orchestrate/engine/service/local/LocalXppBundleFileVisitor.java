package com.thomsonreuters.uscl.ereader.orchestrate.engine.service.local;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;

public class LocalXppBundleFileVisitor extends SimpleFileVisitor<Path> {
    private static final String MATERIAL = "material";
    private static final Pattern MATERIAL_PATTERN = Pattern.compile(String.format(".*_(?<%s>\\d+)_.*\\.(tar.gz|zip)", MATERIAL));

    private final BiFunction<String, String, XppBundleArchive> createXppBundleFunction;
    private final Function<XppBundleArchive, String> sendJmsMessageFunction;
    private final AtomicInteger handledFilesCount = new AtomicInteger(0);

    LocalXppBundleFileVisitor(final BiFunction<String, String, XppBundleArchive> createXppBundleFunction,
                              final Function<XppBundleArchive, String> sendJmsMessageFunction) {
        Objects.requireNonNull(createXppBundleFunction);
        Objects.requireNonNull(sendJmsMessageFunction);
        this.createXppBundleFunction = createXppBundleFunction;
        this.sendJmsMessageFunction = sendJmsMessageFunction;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        final Matcher materialMatcher = MATERIAL_PATTERN.matcher(file.getFileName().toString());
        if (materialMatcher.find()) {
            final XppBundleArchive xppBundleArchive = createXppBundleFunction.apply(
                materialMatcher.group(MATERIAL), file.toAbsolutePath().toString());
            sendJmsMessageFunction.apply(xppBundleArchive);
            handledFilesCount.incrementAndGet();
        }
        return super.visitFile(file, attrs);
    }

    public int getHandledFilesCount() {
        return handledFilesCount.get();
    }
}
