package org.mitre.host.util;

import java.net.URL;

import org.mitre.exception.SystemException;
import org.mitre.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostUtils {

	private static final Logger logger = LoggerFactory.getLogger(HostUtils.class);
	
	public static final String CURRENT_HOST_URL_ATTR = "currentHostUrl";
	
	public static final String CURRENT_CONTEXT_PATH = "currentContextPath";

	public static void setCurrentHost(URL hostUrl) {
		ThreadUtils.set(CURRENT_HOST_URL_ATTR, hostUrl);
	}

	public static URL getCurrentHost() {
		return (URL) ThreadUtils.get(CURRENT_HOST_URL_ATTR);
	}
	
	public static void setCurrentContextPath(String contextPath) {
		ThreadUtils.set(CURRENT_CONTEXT_PATH, contextPath);
	}

	public static String getCurrentContextPath() {
		return (String) ThreadUtils.get(CURRENT_CONTEXT_PATH);
	}
	
	public static String getCurrentRunningFullPath() {
		URL url = getCurrentHost();
		if(url == null) {
			throw new SystemException("Current Host is not set");
		}
		String contextPath = getCurrentContextPath();
		if(contextPath == null) {
			throw new SystemException("Current Servlet Path is not set");
		}
		StringBuffer fullPath = new StringBuffer();
		fullPath.append(url.getProtocol());
		fullPath.append("://");
		fullPath.append(url.getHost());
		if(url.getPort() > 0 && url.getPort() != 80) {
			fullPath.append(":");
			fullPath.append(url.getPort());
		}
		fullPath.append(contextPath);
		fullPath.append("/");
		logger.debug("Host: " + fullPath.toString());
		return fullPath.toString();
	}
}
