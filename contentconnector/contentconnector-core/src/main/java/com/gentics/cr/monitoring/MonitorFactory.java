package com.gentics.cr.monitoring;

import java.text.DecimalFormat;

import com.gentics.cr.configuration.GenericConfiguration;

public class MonitorFactory {

	private static boolean monitoringEnabled = false;
	private static final String MONITOR_ENABLE_KEY = "monitoring";

	public static synchronized void init(GenericConfiguration config) {
		String enabled = config.getString(MONITOR_ENABLE_KEY);
		if (enabled != null) {
			monitoringEnabled = Boolean.parseBoolean(enabled);
		}
	}

	public static UseCase startUseCase(String identifyer) {
		if (monitoringEnabled) {
			return new UseCase(com.jamonapi.MonitorFactory.start(identifyer), monitoringEnabled);
		}
		return new UseCase(null, monitoringEnabled);

	}

	public static String getSimpleReport() {
		String ret = "<table class=\"report_table\">";
		if (monitoringEnabled) {
			int maxField = 7;

			String[] header = com.jamonapi.MonitorFactory.getHeader();
			if (header != null) {
				ret += "<tr>";
				for (int i = 0; i <= maxField; i++) {
					ret += "<th>" + header[i] + "</th>";
				}
				ret += "</tr>";
			}
			Object[][] data = com.jamonapi.MonitorFactory.getData();

			DecimalFormat output = new DecimalFormat("#0.00");
			if (data != null) {
				for (int j = 0; j < data.length; j++) {
					if (j % 2 == 0) {
						ret += "<tr class=\"even\">";
					} else {
						ret += "<tr class=\"odd\">";
					}
					for (int i = 0; i <= maxField; i++) {
						Object obj = data[j][i];

						if (obj instanceof Double) {
							obj = output.format(obj);
						}
						if (i > 0) {
							ret += "<td class=\"value\">" + obj.toString() + "</td>";
						} else {
							ret += "<td>" + obj.toString() + "</td>";
						}
					}
					ret += "</tr>";
				}
			}
		}
		ret += "</table>";
		return ret;
	}
}
