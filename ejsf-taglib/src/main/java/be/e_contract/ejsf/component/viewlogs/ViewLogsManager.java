/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.viewlogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewLogsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewLogsManager.class);

    private static String getLogPath() throws MalformedObjectNameException, MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mBeanServers.isEmpty()) {
            LOGGER.error("no JMX MBeanServers");
            return null;
        }
        MBeanServer mBeanServer = (MBeanServer) mBeanServers.get(0);
        ObjectName jbossServerLogDirObjectName = new ObjectName("jboss.as:path=jboss.server.log.dir");
        String logPath = (String) mBeanServer.getAttribute(jbossServerLogDirObjectName, "path");
        return logPath;
    }

    public static List<File> getLogFiles(List<String> includes, List<String> excludes) {
        String logPath;
        try {
            logPath = getLogPath();
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            LOGGER.error("JMX error: " + ex.getMessage(), ex);
            return new LinkedList<>();
        }
        if (null == logPath) {
            return new LinkedList<>();
        }
        File logDirectory = new File(logPath);
        List<File> logFiles = new LinkedList<>(Arrays.asList(logDirectory.listFiles()));
        if (null != includes) {
            Set<File> result = new HashSet<>();
            for (String include : includes) {
                logFiles.stream()
                        .filter(file -> file.getName().matches(include))
                        .collect(Collectors.toCollection(() -> result));
            }
            logFiles = new LinkedList<>(result);
        }
        if (null != excludes) {
            for (String exclude : excludes) {
                List<File> result = new LinkedList<>();
                logFiles.stream()
                        .filter(file -> !file.getName().matches(exclude))
                        .collect(Collectors.toCollection(() -> result));
                logFiles = result;
            }
        }
        logFiles.sort((File f1, File f2) -> f1.getName().compareTo(f2.getName()));
        return logFiles;
    }

    public static File findLogFile(String resourceName) {
        String logPath;
        try {
            logPath = getLogPath();
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            LOGGER.error("JMX error: " + ex.getMessage(), ex);
            return null;
        }
        File logDirectory = new File(logPath);
        File[] logFiles = logDirectory.listFiles();
        for (File logFile : logFiles) {
            if (logFile.getName().equals(resourceName)) {
                return logFile;
            }
        }
        return null;
    }
}
