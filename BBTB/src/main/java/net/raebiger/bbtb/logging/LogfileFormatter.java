package net.raebiger.bbtb.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogfileFormatter extends Formatter {
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(1000);
		builder.append(df.format(new Date(record.getMillis()))).append(" ");
		builder.append(record.getLevel()).append(" ");
		builder.append(formatMessage(record));
		builder.append(" [").append(record.getSourceClassName()).append(".");
		builder.append(record.getSourceMethodName()).append("]");
		builder.append("\n");
		return builder.toString();
	}

}
