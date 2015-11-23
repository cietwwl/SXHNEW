package com.joyveb.tlol;

import java.net.MalformedURLException;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class HttpAdaptor {

	private int port = 0;

	private String host = null;

	// private String path = null, pathInJar = null;

	TianLongDriver TLOLManager = null;

	public HttpAdaptor(final TianLongDriver TLOLManager, final String host, final int port) {
		this.host = host;
		this.port = port;
		this.TLOLManager = TLOLManager;
	}

	public final void start() throws JMException, MalformedURLException {
		MBeanServer server = MBeanServerFactory.createMBeanServer("TLOL");
		ObjectName serverName = new ObjectName("Http:name=HttpAdaptor");
		server.createMBean("mx4j.tools.adaptor.http.HttpAdaptor", serverName,
				null);
		if (port > 0)
			server.setAttribute(serverName, new Attribute("Port", port));
		else
			System.out.println("Incorrect port value " + port);

		if (host != null)
			server.setAttribute(serverName, new Attribute("Host", host));
		else
			System.out.println("Incorrect null hostname");

		ObjectName processorName = new ObjectName("Http:name=XSLTProcessor");
		server.createMBean("mx4j.tools.adaptor.http.XSLTProcessor",
				processorName, null);
		// if (path != null)
		// server.setAttribute(processorName, new Attribute("File", path));

		server.setAttribute(processorName,
				new Attribute("UseCache", Boolean.valueOf(false)));
		// if (pathInJar != null)
		// server.setAttribute(processorName, new Attribute("PathInJar",
		// pathInJar));

		server.setAttribute(serverName, new Attribute("ProcessorName",
				processorName));

		server.registerMBean(TLOLManager, new ObjectName(
				"TLOL:name=TLOL MANAGER"));

		server.invoke(serverName, "start", null, null);
	}
}
