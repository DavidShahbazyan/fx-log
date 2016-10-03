package org.hildan.fxlog.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.hildan.fxlog.coloring.Colorizer;
import org.hildan.fxlog.coloring.Style;
import org.hildan.fxlog.coloring.StyleRule;
import org.hildan.fxlog.columns.ColumnDefinition;
import org.hildan.fxlog.columns.Columnizer;
import org.hildan.fxlog.filtering.Filter;

/**
 * A generator for the default configuration. It is used as fallback when the built-in config is not available as a
 * resource.
 * <p>
 * It makes it possible for a developer to generate a config programmatically with elements that are not customizable
 * yet via the UI. This is definitely easier than editing the JSON directly.
 */
class DefaultConfig {

    private static final double DEFAULT_DATE_WIDTH = 200;

    private static final double DEFAULT_SEVERITY_WIDTH = 63;

    private static final double DEFAULT_CLASS_WIDTH = 430;

    /**
     * WHen there are a lot of columns.
     */
    private static final double DEFAULT_MSG_WIDTH = 720;

    /**
     * When there are not many columns.
     */
    private static final double DEFAULT_MSG_WIDTH_BIG = 1000;

    private static final double DEFAULT_SID_WIDTH = 300;

    private static final Filter WEBLOGIC_HIGHLIGHT = Filter.findInColumn("msg",
            "(Successfully completed deployment.*)|(EJB Deployed EJB with JNDI name.*)");

    /**
     * Generates the default config programmatically.
     *
     * @return the default config
     */
    static Config generate() {
        Config config = new Config();

        config.getColorizers().add(severityBasedColorizerDark());
        config.getColorizers().add(severityBasedColorizerLight());

        config.getColumnizers().add(weblogicColumnizer());
        config.getColumnizers().add(weblogicEasyTraceColumnizer());
        config.getColumnizers().add(apacheAccessColumnizer());
        config.getColumnizers().add(apacheErrorColumnizer());
        config.getColumnizers().add(amadeusInputLog());

        return config;
    }

    private static Columnizer weblogicColumnizer() {
        ObservableList<ColumnDefinition> columnDefinitions = FXCollections.observableArrayList();
        columnDefinitions.add(new ColumnDefinition("Date/Time", "datetime", DEFAULT_DATE_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Severity", "severity", DEFAULT_SEVERITY_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Subsystem", "subsystem", false));
        columnDefinitions.add(new ColumnDefinition("Machine Name", "machine", false));
        columnDefinitions.add(new ColumnDefinition("Server Name", "server", false));
        columnDefinitions.add(new ColumnDefinition("Thread ID", "thread", false));
        columnDefinitions.add(new ColumnDefinition("User ID", "user", false));
        columnDefinitions.add(new ColumnDefinition("Transaction ID", "transaction", false));
        columnDefinitions.add(new ColumnDefinition("Diagnostic Context ID", "context", false));
        columnDefinitions.add(new ColumnDefinition("Timestamp", "timestamp", false));
        columnDefinitions.add(new ColumnDefinition("Message ID", "msgId", false));
        columnDefinitions.add(new ColumnDefinition("Class", "class", DEFAULT_CLASS_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Message", "msg", DEFAULT_MSG_WIDTH));
        columnDefinitions.add(new ColumnDefinition("JSessionID", "sessionid", DEFAULT_SID_WIDTH));

        String logStart = "####" //
                + "<(?<datetime>[^>]*?)>" //
                + " ?<(?<severity>[^>]*?)>" //
                + " ?<(?<subsystem>[^>]*?)>" //
                + " ?<(?<machine>[^>]*?)>" //
                + " ?<(?<server>[^>]*?)>" //
                + " ?<(?<thread>[^>]*?)>" //
                + " ?<(?<user>.*?)>" //
                + " ?<(?<transaction>[^>]*?)>" //
                + " ?<(?<context>[^>]*?)>" //
                + " ?<(?<timestamp>[^>]*?)>" //
                + " ?<(?<msgId>[^>]*?)>" //
                + "( ?<(?<class>[^>]*?)>)?" //
                + " ?<(?<msg>.*?)" // message start
                + "(;jsessionid=(?<sessionid>[^>]*))?" // optional session id
                + ">?"; // optional end of message (if not continued)
        String logEnd = "(?<msg>[^>]*)(;jsessionid=(?<sid>.*?))?>"; // end of msg and optional session id
        String logCenter = "(?<msg>.*)";
        ObservableList<String> regexps = FXCollections.observableArrayList(logStart, logEnd, logCenter);
        return new Columnizer("Weblogic Server Log", columnDefinitions, regexps);
    }

    private static Columnizer weblogicEasyTraceColumnizer() {
        ObservableList<ColumnDefinition> columnDefinitions = FXCollections.observableArrayList();
        columnDefinitions.add(new ColumnDefinition("Date/Time", "datetime", DEFAULT_DATE_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Severity", "severity", DEFAULT_SEVERITY_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Subsystem", "subsystem", false));
        columnDefinitions.add(new ColumnDefinition("Machine Name", "machine", false));
        columnDefinitions.add(new ColumnDefinition("Server Name", "server", false));
        columnDefinitions.add(new ColumnDefinition("Thread ID", "thread", false));
        columnDefinitions.add(new ColumnDefinition("Class", "class", DEFAULT_CLASS_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Message", "msg", DEFAULT_MSG_WIDTH));
        columnDefinitions.add(new ColumnDefinition("JSessionID", "sessionid", DEFAULT_SID_WIDTH));

        ObservableList<String> regexps = FXCollections.observableArrayList(//
                "<(?<datetime>[^>]*?)> ?<(?<severity>[^>]*?)> ?<(?<subsystem>[^>]*?)> ?<(?<machine>[^>]*?)> ?"
                        + "<(?<server>[^>]*?)> ?<(?<thread>[^>]*?)>( ?<(?<class>[^>]*?)>)? ?"
                        + "<(?<msg>.*?)(;jsessionid=(?<sessionid>.*))?>?", // log beginning
                "(?<msg>[^>]*)(;jsessionid=(?<sid>.*?))?>", // end of message
                "(?<msg>.*)"); // middle of log message on new line
        return new Columnizer("Weblogic (processed by EasyTrace)", columnDefinitions, regexps);
    }

    private static Columnizer apacheAccessColumnizer() {
        ObservableList<ColumnDefinition> columnDefinitions = FXCollections.observableArrayList();
        columnDefinitions.add(new ColumnDefinition("Client", "client", 205));
        columnDefinitions.add(new ColumnDefinition("User", "user", 80));
        columnDefinitions.add(new ColumnDefinition("Username", "username", 75));
        columnDefinitions.add(new ColumnDefinition("Date/Time", "datetime", DEFAULT_DATE_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Request", "request", 700));
        columnDefinitions.add(new ColumnDefinition("Resp. Code", "rstatus", 80));
        columnDefinitions.add(new ColumnDefinition("Resp. Size (bytes)", "rsize", 110));
        columnDefinitions.add(new ColumnDefinition("Referer", "referer", 60));
        columnDefinitions.add(new ColumnDefinition("User-Agent", "useragent", 80));

        ObservableList<String> regexps = FXCollections.observableArrayList(
                "(?<client>\\S+) (?<user>\\S+) (?<username>\\S+) \\[(?<datetime>[^\\]]*?)\\] \"(?<request>[^\"]*?)\" "
                        + "(?<rstatus>\\S+) (?<rsize>\\S+)( \"(?<referer>[^\"]*?)\" \"(?<useragent>[^\"]*?)\")?");
        return new Columnizer("Apache Access Log", columnDefinitions, regexps);
    }

    private static Columnizer apacheErrorColumnizer() {
        ObservableList<ColumnDefinition> columnDefinitions = FXCollections.observableArrayList();
        columnDefinitions.add(new ColumnDefinition("Date/Time", "datetime", DEFAULT_DATE_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Severity", "severity", DEFAULT_SEVERITY_WIDTH));
        columnDefinitions.add(new ColumnDefinition("Client", "client", 75));
        columnDefinitions.add(new ColumnDefinition("Message", "msg", DEFAULT_MSG_WIDTH_BIG));

        String full = "\\[(?<datetime>[^\\]]*?)\\] \\[(?<severity>[^\\]]*?)\\] \\[(?<client>\\]*?)\\] (?<msg>.*)";
        String noClient = "\\[(?<datetime>[^\\]]*?)\\] \\[(?<severity>[^\\]]*?)\\] (?<msg>.*)";
        String noClientNoSev = "\\[(?<datetime>[^\\]]*?)\\] (?<msg>.*)";
        String defaultToMsg = "(?<msg>.*)";
        ObservableList<String> regexps = FXCollections.observableArrayList(full, noClient, noClientNoSev, defaultToMsg);
        return new Columnizer("Apache Error Log", columnDefinitions, regexps);
    }

    private static Columnizer amadeusInputLog() {
        ObservableList<ColumnDefinition> columnDefinitions = FXCollections.observableArrayList();
        columnDefinitions.add(new ColumnDefinition("Year", "year", 40));
        columnDefinitions.add(new ColumnDefinition("M", "month", 23));
        columnDefinitions.add(new ColumnDefinition("D", "day", 23));
        columnDefinitions.add(new ColumnDefinition("h", "hours", 23));
        columnDefinitions.add(new ColumnDefinition("m", "minutes", 23));
        columnDefinitions.add(new ColumnDefinition("s", "seconds", 23));
        columnDefinitions.add(new ColumnDefinition("ms", "millis", 30));
        columnDefinitions.add(new ColumnDefinition("Domain", "domain", 400));
        columnDefinitions.add(new ColumnDefinition("Action", "action", 60));
        columnDefinitions.add(new ColumnDefinition("Parameters", "params", 2000));

        ObservableList<String> regexps = FXCollections.observableArrayList(
                "(?<year>[0-9]{4})(?<month>[0-9]{2})(?<day>[0-9]{2})(?<hours>[0-9]{2})(?<minutes>[0-9]{2})"
                        + "(?<seconds>[0-9]{2})"
                        + "(?<millis>[0-9]{3}).*?(?<domain>http.*)/(?<action>.*?)\\?(?<params>.*)");
        return new Columnizer("Amadeus input.log", columnDefinitions, regexps);
    }

    private static Colorizer severityBasedColorizerLight() {
        StyleRule highlightRule = new StyleRule("Highlight", WEBLOGIC_HIGHLIGHT, Style.HIGHLIGHT);
        StyleRule errorRule = new StyleRule("Error", Filter.ERROR_SEVERITY, Style.DARK_RED);
        StyleRule warnRule = new StyleRule("Warn", Filter.WARN_SEVERITY, Style.DARK_ORANGE);
        StyleRule infoRule = new StyleRule("Info", Filter.INFO_SEVERITY, Style.DARK_GREEN);
        StyleRule debugRule = new StyleRule("Debug", Filter.DEBUG_SEVERITY, Style.DARK_BLUE);
        StyleRule noticeRule = new StyleRule("Notice", Filter.NOTICE_SEVERITY, Style.DEFAULT);
        StyleRule stackTraceHead = new StyleRule("Stacktrace Head", Filter.STACKTRACE_HEAD, new Style(Style.DARK_RED));
        StyleRule stackTraceBody = new StyleRule("Stacktrace Body", Filter.STACKTRACE_BODY, new Style(Style.DARK_RED));
        StyleRule defaultRule = new StyleRule("Default", Filter.MATCH_ALL, Style.BLACK);

        return new Colorizer("Severity (for light theme)",
                FXCollections.observableArrayList(highlightRule, errorRule, warnRule, infoRule, debugRule, noticeRule,
                        stackTraceHead, stackTraceBody, defaultRule));
    }

    private static Colorizer severityBasedColorizerDark() {
        StyleRule highlightRule = new StyleRule("Highlight", WEBLOGIC_HIGHLIGHT, Style.HIGHLIGHT);
        StyleRule errorRule = new StyleRule("Error", Filter.ERROR_SEVERITY, Style.RED);
        StyleRule warnRule = new StyleRule("Warn", Filter.WARN_SEVERITY, Style.ORANGE);
        StyleRule infoRule = new StyleRule("Info", Filter.INFO_SEVERITY, Style.GREEN);
        StyleRule debugRule = new StyleRule("Debug", Filter.DEBUG_SEVERITY, Style.BLUE);
        StyleRule noticeRule = new StyleRule("Notice", Filter.NOTICE_SEVERITY, Style.DEFAULT);
        StyleRule stackTraceHeadRule = new StyleRule("Stacktrace Head", Filter.STACKTRACE_HEAD, new Style(Style.RED));
        StyleRule stackTraceBodyRule = new StyleRule("Stacktrace Body", Filter.STACKTRACE_BODY, new Style(Style.RED));
        StyleRule defaultRule = new StyleRule("Default", Filter.MATCH_ALL, Style.LIGHT_GRAY);

        return new Colorizer("Severity (for dark theme)",
                FXCollections.observableArrayList(highlightRule, errorRule, warnRule, infoRule, debugRule, noticeRule,
                        stackTraceHeadRule, stackTraceBodyRule, defaultRule));
    }
}
