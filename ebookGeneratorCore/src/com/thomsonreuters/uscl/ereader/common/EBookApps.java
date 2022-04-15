package com.thomsonreuters.uscl.ereader.common;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public enum EBookApps {
    //Comment to test CICD process as KT session
    MANAGER("Manager", "ebookManager",
            new AppEnv(new String[]{"c708pfmctasdf.int.thomsonreuters.com"}, "9007", "http://ebookmanager-ci.int.thomsonreuters.com"),
            new AppEnv(new String[]{"c281ffzctastf.int.thomsonreuters.com"}, "9003", "http://ebookmanager-demo.int.thomsonreuters.com"),
            new AppEnv(new String[]{"c045ydwctasqf.int.thomsonreuters.com",
                                    "c088jzvctasqf.int.thomsonreuters.com"}, "9001", "http://ebookmanager-qed.int.thomsonreuters.com"),
            new AppEnv(new String[]{"c250ektctaspf.int.thomsonreuters.com",
                                    "c312kjfctaspf.int.thomsonreuters.com"}, "9001", "http://ebookmanager.int.thomsonreuters.com")),

    GATHERER("Gatherer", "ebookGatherer",
             new AppEnv(new String[]{"c708pfmctasdf.int.thomsonreuters.com"}, "9001", StringUtils.EMPTY),
             new AppEnv(new String[]{"c273pevctastf.int.thomsonreuters.com"}, "9001", StringUtils.EMPTY),
             new AppEnv(new String[]{"c311ppwctasqf.int.thomsonreuters.com",
                                     "c531tqhctasqf.int.thomsonreuters.com",
                                     "c931dsectasqf.int.thomsonreuters.com",
                                     "c959hcyctasqf.int.thomsonreuters.com"}, "9001", "http://ebookgatherer-qed.int.thomsonreuters.com"),
             new AppEnv(new String[]{"c378nnactaspf.int.thomsonreuters.com",
                                     "c634tcpctaspf.int.thomsonreuters.com",
                                     "c730rejctaspf.int.thomsonreuters.com",
                                     "c747kpbctaspf.int.thomsonreuters.com"}, "9001", "http://ebookgatherer.int.thomsonreuters.com")),

    GENERATOR("Generator", "ebookGenerator",
              new AppEnv(new String[]{"c708pfmctasdf.int.thomsonreuters.com"}, "9002", StringUtils.EMPTY),
              new AppEnv(new String[]{"c273pevctastf.int.thomsonreuters.com"}, "9002", StringUtils.EMPTY),
              new AppEnv(new String[]{"c311ppwctasqf.int.thomsonreuters.com",
                                      "c531tqhctasqf.int.thomsonreuters.com",
                                      "c931dsectasqf.int.thomsonreuters.com",
                                      "c959hcyctasqf.int.thomsonreuters.com"}, "9002", "http://ebookgenerator-qed.int.thomsonreuters.com"),
              new AppEnv(new String[]{"c378nnactaspf.int.thomsonreuters.com",
                                      "c634tcpctaspf.int.thomsonreuters.com",
                                      "c730rejctaspf.int.thomsonreuters.com",
                                      "c747kpbctaspf.int.thomsonreuters.com"}, "9002", "http://ebookgenerator.int.thomsonreuters.com"));

    private final String name;
    private final String contextPath;
    private final AppEnv workstationEnv;
    private final AppEnv ciEnv;
    private final AppEnv testEnv;
    private final AppEnv qedEnv;
    private final AppEnv prodEnv;

    @SneakyThrows
    EBookApps(final String name, final String contextPath,
              final AppEnv ciEnv, final AppEnv testEnv,
              final AppEnv qedEnv, final AppEnv prodEnv) {
        this.name = name;
        this.contextPath = contextPath;
        this.ciEnv = ciEnv;
        this.testEnv = testEnv;
        this.qedEnv = qedEnv;
        this.prodEnv = prodEnv;

        final String workstationHost = InetAddress.getLocalHost().getHostName();
        final String workstationPort = "8080";
        workstationEnv = new AppEnv(new String[]{workstationHost}, workstationPort,
                                    String.format("http://%s:%s", workstationHost, workstationPort));
    }

    public static List<String> ciServers() {
        return getEnvServers(EBookApps::ci);
    }

    public static List<String> testServers() {
        return  getEnvServers(EBookApps::test);
    }

    public static List<String> qedServers() {
        return  getEnvServers(EBookApps::qed);
    }

    public static List<String> prodServers() {
        return  getEnvServers(EBookApps::prod);
    }

    private static List<String> getEnvServers(final Function<EBookApps, AppEnv> environmentFunction) {
        return Stream.of(values())
            .map(environmentFunction)
            .map(AppEnv::servers)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public String appName() {
        return name;
    }

    public String contextPath() {
        return contextPath;
    }

    public AppEnv workstation() {
        return workstationEnv;
    }

    public AppEnv ci() {
        return ciEnv;
    }

    public AppEnv test() {
        return testEnv;
    }

    public AppEnv qed() {
        return qedEnv;
    }

    public AppEnv prod() {
        return prodEnv;
    }

    public AppEnv getEnv(final String envName) {
        final AppEnv appEnv;
        switch (Optional.ofNullable(envName).orElse(StringUtils.EMPTY)) {
            case "workstation":
                appEnv = workstationEnv;
                break;
            case "cicontent":
                appEnv = ciEnv;
                break;
            case "testcontent":
                appEnv = testEnv;
                break;
            case "preprodcontent":
                appEnv = qedEnv;
                break;
            case "prodcontent":
                appEnv = prodEnv;
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("Unsupported environment name: %s", envName));
        }
        return appEnv;
    }

    public static final class AppEnv {
        private final List<String> servers;
        private final String port;
        private final String webUrl;

        private AppEnv(final String[] servers, final String port, final String webUrl) {
            this.servers = Arrays.asList(servers);
            this.port = port;
            this.webUrl = webUrl;
        }

        public List<String> servers() {
            return servers;
        }

        public String port() {
            return port;
        }

        public String webUrl() {
            return webUrl;
        }
    }
}
