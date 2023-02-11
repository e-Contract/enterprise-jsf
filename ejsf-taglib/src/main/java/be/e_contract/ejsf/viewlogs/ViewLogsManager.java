/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.viewlogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ViewLogsManager {

    private static String getLogPath() throws MalformedObjectNameException, MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mBeanServers.isEmpty()) {
            return null;
        }
        MBeanServer mBeanServer = (MBeanServer) mBeanServers.get(0);
        ObjectName jbossServerLogDirObjectName = new ObjectName("jboss.as:path=jboss.server.log.dir");
        String logPath = (String) mBeanServer.getAttribute(jbossServerLogDirObjectName, "path");
        return logPath;
    }

    public static List<File> getLogFiles() {
        String logPath;
        try {
            logPath = getLogPath();
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            return new LinkedList<>();
        }
        if (null == logPath) {
            return new LinkedList<>();
        }
        File logDirectory = new File(logPath);
        List<File> logFiles = new LinkedList<>(Arrays.asList(logDirectory.listFiles()));
        logFiles.sort((File f1, File f2) -> f1.getName().compareTo(f2.getName()));
        return logFiles;
    }

    public static File findLogFile(String resourceName) {
        String logPath;
        try {
            logPath = getLogPath();
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
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
