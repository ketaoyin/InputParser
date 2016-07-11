import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class InputParser {

	static HashMap<String, ArrayList<String>> ApiDatabase = new HashMap<String, ArrayList<String>>();

	public static void main(String[] args) throws IOException {

		String inputFileName = "";
		if (args.length > 0)
			inputFileName = args[0];
		else
			System.out.println("No file entered.");

		// Grab import statements
		ArrayList<String> importStatements = new ArrayList<String>();
		Scanner importGrabber = new Scanner(new File(inputFileName));
		while (importGrabber.hasNextLine()) {
			String line = importGrabber.nextLine();
			if (line.length() >= 6 && line.substring(0, 6).equals("import")) {
				importStatements.add(line.substring(7, line.length() - 1));
			}
		}

		// Creates HashMap for API database
		createDatabase(importStatements);

		// Records console output to file
		PrintStream original = System.out; // Saves original PrintStream
		File outputFile = new File("output/" + inputFileName + ".txt");
		FileOutputStream fos = new FileOutputStream(outputFile);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);

		// Parses into AST tree and grab all methods
		File inputFile = new File(inputFileName);
		Scanner scanner = new Scanner(inputFile);

		String fileString = scanner.nextLine();
		while (scanner.hasNextLine()) {
			fileString = fileString + "\n" + scanner.nextLine();
		}

		char[] charArray = fileString.toCharArray();
		parse(charArray, inputFileName);

		ps.flush();
		fos.flush();
		ps.close();
		fos.close();
		System.setOut(original); // Reset printStream

		// Go back into output file to find methods
		ArrayList<String> methodCalls = new ArrayList<String>();

		Scanner scannerIn = new Scanner(outputFile);

		while (scannerIn.hasNextLine()) {
			methodCalls.add(scannerIn.nextLine());
		}

		// Write summary info to output file
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write(inputFileName + "\n");

		HashMap<String, Integer> outputTable = new HashMap<String, Integer>(); // (<Package>,<Class>,<Method>
																				// ,
																				// countOfOccurances)

		for (int i = 0; i < methodCalls.size(); i++) {
			if (ApiDatabase.containsKey(methodCalls.get(i))) {
				ArrayList<String> pcInfo = ApiDatabase.get(methodCalls.get(i));
				for (int j = 0; j < pcInfo.size(); j++) {
					String key = pcInfo.get(j) + "," + methodCalls.get(i);
					if (outputTable.containsKey(key))
						outputTable.put(key, outputTable.get(key) + 1);
					else
						outputTable.put(key, 1);
				}
			}

		}

		Set<String> outputTableKeySet = outputTable.keySet();
		Iterator<String> outputTableKeySetItr = outputTableKeySet.iterator();

		while (outputTableKeySetItr.hasNext()) {
			String key = outputTableKeySetItr.next();
			String line = key + "," + outputTable.get(key);
			bw.write(line + "\n");
		}

		bw.flush();
		bw.close();
	}

	private static void parse(char[] arr, String infile_name) {
		SimpleASTVisitor astv = new SimpleASTVisitor(arr, infile_name);
		astv.parse();
	}

	public static void createDatabase(ArrayList<String> importStatements) throws FileNotFoundException {
		File file = new File("API Database.txt");
		Scanner scanner = new Scanner(file);
		String line = "";
		
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			
			String key = line.substring(line.lastIndexOf(",") + 1).trim();
			String value = line.substring(0, line.lastIndexOf(",")).trim();
			String classNameOnly = value.substring(value.indexOf(",") + 1).trim();
			String packageNameOnly = value.substring(0, value.indexOf(",")).trim();
			
			if (importStatements.contains(classNameOnly)) {
				System.out.println(value);
				if (ApiDatabase.containsKey(key)) {
					ArrayList<String> list = ApiDatabase.get(key);
					list.add(value);
					ApiDatabase.put(key, list);
				} else {
					ArrayList<String> list = new ArrayList<String>();
					list.add(value);
					ApiDatabase.put(key, list);
				}
			}
		}
	}
}
