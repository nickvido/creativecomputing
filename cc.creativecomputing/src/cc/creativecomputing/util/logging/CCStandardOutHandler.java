package cc.creativecomputing.util.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CCStandardOutHandler extends Handler {

	public CCStandardOutHandler() {
		super();
	}

	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		
		if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
			// print error messages in red in eclipse console window
			System.err.print(getFormatter().format(record));		
		} else {
			System.out.print(getFormatter().format(record));
		}
	}

	public void flush() {
		System.out.flush();
	}

	public void close() {
	}
}