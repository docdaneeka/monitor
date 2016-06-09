package systemFileScan;

import main.ScriptExecutor;

import javax.swing.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//propfiles=nullPtr
//filesizes=nullPtr
public class SystemFileScanPane extends JPanel {
	Properties prop = new Properties();

	private JTextArea authlogTextArea;
	private Thread scanThread;
	private Map<String, Integer> logFileSizes = new LinkedHashMap<String, Integer>();
	private Map<String, Integer> filePrivileges = new LinkedHashMap<String, Integer>();
	private List<String> additionalPrivilegeFiles = new ArrayList<String>();
	private List<String> additionalSizeFiles = new ArrayList<String>();
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public SystemFileScanPane() {
		InputStream input = null;
		try {
			System.out.println(System.getProperty("user.dir"));
			input = new FileInputStream("res/scan.properties");
			prop.load(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fileSizesAdditionalInput = prop.getProperty("filesizes");
		String filePrivilegesAdditionalInput = prop.getProperty("filesizes");
		String[] fileSizesAdds = fileSizesAdditionalInput.split(",");
		String[] filePrivilegesAdds = filePrivilegesAdditionalInput.split(",");
		for (String fileSizeAdd : fileSizesAdds) {
			if (fileSizeAdd.equals("nullPtr"))
				break;
			additionalSizeFiles.add(fileSizeAdd);
			checkSize(fileSizeAdd);
		}
		for (String filePrivileges : fileSizesAdds) {
			if (filePrivileges.equals("nullPtr"))
				break;
			additionalPrivilegeFiles.add(filePrivileges);
			checkPrivilege(filePrivileges);
		}
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
				// authlogTextArea.setText("");
				// authlogTextArea.append("Scanning..." + new Date() + "\n");
				while (true) {
					// authlogTextArea.setText("");

					checkFailedLoginAttempts();

					checkLogSizes();

					checkFilePrivileges();

					try {
						Thread.sleep(8000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		authlogTextArea.setText("Scan started on : [" + sdf.format(cal.getTime()) + "].\n");
		scanThread.start();

	}

	private void checkLogSizes() {
		// authlogTextArea.append("Checking log sizes\n");
		checkSize("/var/log/auth.log");
		checkSize("/var/log/syslog");
		for (String s : additionalSizeFiles)
			checkSize(s);
	}

	private void checkFilePrivileges() {
		String[] tmpContent = ScriptExecutor.execScript("ls /tmp").split("\n");
		for (String s : tmpContent) {
			// System.out.println("tmp " + s);
			checkPrivilege("/tmp/" + s);
		}
		for (String s : additionalPrivilegeFiles)
			checkPrivilege(s);
		String[] etcContent = ScriptExecutor.execScript("ls /etc").split("\n");
		for (String s : etcContent) {
			// System.out.println("etc " + s);
			checkPrivilege("/tmp/" + s);
		}

	}

	private void checkPrivilege(String file) {
		int found = 0;
		String privileges = ScriptExecutor.execScript("stat -c '%a %n' " + file);
		//// System.out.println(privileges);
		if (privileges.contains("unity_support")) {
			System.out.println(privileges);
			found = 1;
		}
		// Matcher matcher = Pattern.compile("([0-9]+)").matcher(privileges);
		// matcher.find();
		// if (!matcher.matches()){
		// if(found == 1) System.out.println("Didnt find that");
		// // System.out.println("Didnt find");
		// return;
		// }
		int privilegesCode = -1;
		try {
			privilegesCode = Integer.parseInt(privileges.split(" ")[0]);
		} catch (NumberFormatException e) {
			return;
		}
		if (found == 1)
			System.out.println(file + ", " + privilegesCode);
		if (filePrivileges.get(file) == null) {
			System.out.println("Jeszcze nie dodano");
			filePrivileges.put(file, privilegesCode);
		} else {
			System.out.println("Zachowany kod" + filePrivileges.get(file));
			if (privilegesCode != filePrivileges.get(file))
				authlogTextArea.append(sdf.format(cal.getTime()) + " Privileges " + file + " has been changed!\n");

			filePrivileges.put(file, privilegesCode);
		}
	}

	private void checkSize(String logFile) {
		// authlogTextArea.append("Checking size of " + logFile + "\n");
		String logFileSize = ScriptExecutor.execScript("wc -c " + logFile);
		int size = Integer.parseInt(logFileSize.split(" ")[0]);
		// authlogTextArea.append(logFile + ", size = " + size + "\n");
		if (logFileSizes.get(logFile) == -1) {
			logFileSizes.put(logFile, size);
		} else {
			if (size < logFileSizes.get(logFile)) {
				int difference = logFileSizes.get(logFile) - size;
				authlogTextArea.append(sdf.format(cal.getTime()) + " Size of " + logFile
						+ " has been unexpectedly shrinked by" + difference + "bytes!\n");
			}
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
