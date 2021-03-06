package com.jnetty.core.container;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jnetty.core.Config.ServiceConfig;
import com.jnetty.core.servlet.listener.event.EventUtils;
import com.jnetty.util.log.JNettyLogger;
import com.jnetty.util.servlet.SimpleMapper;

public class DefaultContainer implements Container {
	private ServiceConfig serviceConfig = null;
	private SimpleMapper mapper = null;
	private Container next = null;

	public void invoke(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpServlet servlet = mapper.getHttpServlet(request);
			if (servlet != null) {
				response.setContentType("text/html");
				servlet.service(request, response);
				servlet.destroy();
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			JNettyLogger.log(e);
		} finally {
			if (next != null) {
				next.invoke(request, response);
			}
		}

		this.serviceConfig.listenerManager.fireEvent(EventUtils.REQUEST_DESTROYED,
				this.serviceConfig.listenerManager.getEventUtils().buildServletRequestEvent(request));
	}

	public ServiceConfig getConfig() {
		return this.serviceConfig;
	}

	public void setConfig(ServiceConfig config) {
		this.serviceConfig = config;
	}

	public void initialize() {
		this.mapper = new SimpleMapper(this.serviceConfig);
	}

	public void setNext(Container container) {
		next = container;
	}

	public Container getNext() {
		return next;
	}

	public void start() {

	}

	public void stop() {

	}
}
