package com.thomsonreuters.uscl.ereader.format.service;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CitationNormalizerImpl implements CitationNormalizer {
    private static final Pattern CITATION_PATTERN = Pattern.compile("(.*?)(?:\\s?(?:REPEALED|\\sTO\\s).*)?");
    private static final Function<String, String> TO_FIRST_CITE =
            normalizedCite -> Optional.of(CITATION_PATTERN.matcher(normalizedCite))
                    .filter(Matcher::matches)
                    .map(matcher -> matcher.group(1))
                    .orElse(normalizedCite);

    @Override
    public String normalizeCitation(final String cite) {
        return Optional.of(cite)
            .filter(StringUtils::isNotBlank)
            .map(notBlankCite -> notBlankCite.replaceAll("\\p{javaSpaceChar}", " "))
            .map(NormalizationRulesUtil::applyCitationNormalizationRules)
            .map(TO_FIRST_CITE)
            .orElse("");
    }
}
