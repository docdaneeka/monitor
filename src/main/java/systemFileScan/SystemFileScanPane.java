package systemFileScan;

import main.ScriptExecutor;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class SystemFileScanPane extends JPanel {

	private JTextArea authlogTextArea;
	private Thread scanThread;
	private Map<String, Integer> logFileSizes = new LinkedHashMap<String, Integer>();
	private Map<String, Integer> filePrivileges = new LinkedHashMap<String, Integer>();

	public SystemFileScanPane() {
		this.setLayout(null);
		JLabel label1 = new JLabel("System log scanner:");
		label1.setBounds(10, 15, 150, 20);
		logFileSizes.put("/var/log/auth.log", -1);
		logFileSizes.put("/var/log/syslog", -1);
		this.add(label1);
		authlogTextArea = new JTextArea();
		authlogTextArea.setBounds(10, 35, 600, 200);
		this.add(authlogTextArea);

	}

	public void activate() {
		scanThread = new Thread(new Runnable() {

			public void run() {
				while (true) {
					authlogTextArea.setText("");
					authlogTextArea.append("Scanning..." + new Date() + "\n");
					checkFailedLoginAttempts();

					checkLogSizes();

					checkFilePrivileges();

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		authlogTextArea.setText("Scan started on : [" + sdf.format(cal.getTime()) + "].\n");
		scanThread.start();

	}

	private void checkLogSizes() {
		authlogTextArea.append("Checking log sizes\n");
		checkSize("/var/log/auth.log");
		checkSize("/var/log/syslog");
	}

	private void checkFilePrivileges() {
		String[] tmpContent = ScriptExecutor.execScript("ls /tmp").split("\n");
		for (String s : tmpContent) {
			checkPrivilege("/tmp/" + s);
		}
		String[] etcContent = ScriptExecutor.execScript("ls /etc").split("\n");
		for (String s : tmpContent) {
			checkPrivilege("/tmp/" + s);
		}

	}

	private void checkPrivilege(String file) {
		String privileges = ScriptExecutor.execScript("stat -c '%a %n' " + file);
		Matcher matcher = Pattern.compile("\\d+").matcher(privileges);
		matcher.find();
		if (!matcher.matches())
			return;
		int privilegesCode = Integer.valueOf(matcher.group());
		if (filePrivileges.get(file) == null) {
			filePrivileges.put(file, privilegesCode);
		} else {
			if (privilegesCode != filePrivileges.get(file))
				authlogTextArea.append("Privileges " + file + " has been changed!\n");
			else
				filePrivileges.put(file, privilegesCode);
		}
	}

	private void checkSize(String logFile) {
		authlogTextArea.append("Checking size of " + logFile + "\n");
		String logFileSize = ScriptExecutor.execScript("wc -c " + logFile);
		int size = Integer.parseInt(logFileSize.split(" ")[0]);
		authlogTextArea.append(logFile + ", size = " + size + "\n");
		if (logFileSizes.get(logFile) == -1) {
			logFileSizes.put(logFile, size);
		} else {
			if (size < logFileSizes.get(logFile))
				authlogTextArea.append("Size of " + logFile + " has been shrinked!\n");
			logFileSizes.put(logFile, size);
		}
	}

	private void checkFailedLoginAttempts() {
		String failedLoginAttempts = ScriptExecutor.execScript("sudo grep 'authentication failure' /var/log/auth.log");

		if (failedLoginAttempts.length() != 0) {
			String[] split = failedLoginAttempts.split("\n");
			for (String s : split) {
				try {
					String date = s.substring(0, 15);
					String[] split2 = s.split("user=");
					String user = split2[1];

					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

					authlogTextArea.append(sdf.format(cal.getTime()) + "  ::  " + "Failed password @ user : " + user
							+ ", tried to access at + " + date + ".\n");
					// authlogTextArea.append(date + ", failed password @ user :
					// " + user +".");
				} catch (ArrayIndexOutOfBoundsException e) {
					return;
				}
			}
		}
	}
}
