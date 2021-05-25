package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Driver {

	private List<String> m_Flags = new ArrayList<>();

	private String fileName = null;
	private Parser m_Parser;

	public Driver(String[] args) {
		m_Parser = new Parser();
		parseArgs(args);
	}

	private void parseArgs(String[] args) {
		for (String str : args) {
			if (fileName == null)
				fileName = str;
			else
				m_Flags.add(str.substring(2));
		}
	}

	public void run() {
		String code = "";
		try (FileInputStream reader = new FileInputStream(new File(fileName))) {
			int curr = 0;
			while ((curr = reader.read()) != -1)
				code += (char) curr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			m_Parser.parse(code);
		} catch (RuntimeException re) {
			System.err.println(re.getMessage());
			re.printStackTrace();
		}
	}

	public boolean flag(String flag) {
		return m_Flags.contains(flag);
	}

}
