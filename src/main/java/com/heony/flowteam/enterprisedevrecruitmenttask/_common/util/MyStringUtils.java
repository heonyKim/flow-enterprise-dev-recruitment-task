package com.heony.flowteam.enterprisedevrecruitmenttask._common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MyStringUtils {

    public static boolean isContainsUriPatterns(String[] antPatterns, String targetPath) {
        List<PathPattern> pathPatterns = parsePatterns(antPatterns);
        return pathPatterns.stream().anyMatch(pathPattern -> pathPattern.matches(PathContainer.parsePath(targetPath)));
    }

    private static List<PathPattern> parsePatterns(String[] antPatterns) {
        PathPatternParser parser = PathPatternParser.defaultInstance;
        List<PathPattern> pathPatterns = new ArrayList<>(antPatterns.length);
        for (String pattern : antPatterns) {
            pattern = parser.initFullPathPattern(pattern);
            PathPattern pathPattern = parser.parse(pattern);
            pathPatterns.add(pathPattern);
        }
        return pathPatterns;
    }

}
