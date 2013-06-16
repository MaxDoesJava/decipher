import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.*;
import java.lang.*;

public class Decipher {
	public static HashMap<String, Integer> dictionary;
	public static HashMap<String, Vector> patterns;
	public static String globalCipher;
	public static int wordFreqCount;
	public static Vector<Mapping> possibleKeys;
	public static Object[] wordsByLength;
	
	public static void main(String[] args) throws IOException {
		/**
		 *  Reading Source Files including your data structures
		 */
		
		/*******************************************************
		 You should do some pre-processing on given text sources and store them in a file so as to not do pre-processing each time.
		 In this part you should read the files and fill the required data structures, like arrays, and assign values to some variables that you will
		 use for finding the plaintext.			
		********************************************************/
		dictionary = new HashMap<String, Integer>();
		patterns = new HashMap<String, Vector>();
		possibleKeys = new Vector<Mapping>();
		wordsByLength = new Object[19];
		/**
		 *  18 is the max word length in sources 1-3
		 */
		for(int i = 0; i <= 18; i++)
			wordsByLength[i] = new HashMap<String, Integer>();
		
		wordFreqCount = 0;
		
		/**
		 * Dictionary pre-processing
		 */
		if(!loadDictionary("dictionary.txt")) {
			parseFile("source1.txt");
			parseFile("source2.txt");
			parseFile("source3.txt");
			
			outputDictionary();
			
			loadDictionary("dictionary.txt");
		}
		
		// TODO: Add source pre-processing for storing plain-text sources to validate deciphered messages
		
		/**
		 *  Reading Input File, Deciphering and Writing to outputFile	
		 */
		List argz = Arrays.asList(args);
		Iterator it = argz.iterator();
		String inputFileName = it.hasNext() ? (String)it.next() : "";
		Long timeLimit = it.hasNext() ? Long.parseLong((String)it.next()) : 6000;
		File inputFile = new File(inputFileName);
		File outputFile = new File("Output.txt");
		String cipherText;
		RandomAccessFile inputFileRAF = new RandomAccessFile(inputFile,"r");
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
		
		/**
		 * Parse cipherText from file 1 line at a time	
		 */
		while ((cipherText = inputFileRAF.readLine()) != null) {
			/**
			 * Reading the cipherText from file
			 */
			
			cipherText = cipherText.toUpperCase();
			//Replace anything that is not A to Z or space with a space character
			cipherText = cipherText.replaceAll("[^A-Z\\s]", " ");
			//Replace all instances of more than one consecutive whitespace character with a single space character
			cipherText = cipherText.replaceAll("\\s+", " ");
			//Replace last whitespace with nothing
			cipherText = cipherText.replaceAll("$ ", "");
			
			String plainText = "";
			
			// Begin timer for deciphering text
			long startTime = System.currentTimeMillis(); // Start time
			long current = System.currentTimeMillis();
			
			do{
				plainText = beginDecipher(cipherText);
				current = System.currentTimeMillis();
				if(!plainText.equals(""))
					break;
			}while(current - startTime < timeLimit);
			
			
			//
			/************************************************************
			
			plainText = FindPlaintext(cipherText);
			
			In FindPlaintext function, you should somehow loop and check if the time that you spent on this cipherText
			is greater than a time limit or not. This means you should come up with a time limit for each deciphering operation. 
			You can decide it by testing on some cases, like for n = 50 TimeLimit = 3 min; n = 100 TimeLimit = 2 min, so on... 
			Your FindPlaintext function should be like this: 
			
			String FindPlainText(String cipherText){
				do{
					FindPlaintextOperation();
					long current = System.currentTimeMillis();
				}while(current - startTime < timeLimit)
				
			}
			
			*******************************************************************/
		
			long endTime = System.currentTimeMillis(); // Finish time
			long totalTime = endTime - startTime; // TotalTime calculation for finding a plaintext
			
			/* Printing output - Output Format: "Plaintext,Time(milli sec.)\n"
			   Each line in outputFile will consist of one <plaintext,time> pair
			   If no plaintext has been found, then replace plaintext with "?" mark. */			
			
			//if(solutionsOpt == 1)
			//	printAllPossibleMatches(cipherText);
			
			if(plainText.equals("")){	// if no plainText has been found for this cipherText
				
				outputWriter.write("?"+","+totalTime+"\n");
				System.out.println("?"+","+totalTime+"\n"); // To see that your code doing some progress,
															// please print out the pairs on the screen.
			}
			else{	// if you have a plainText for this cipherText
					
				outputWriter.write(plainText+","+totalTime+"\n");
				System.out.println(plainText+","+totalTime+"\n"); // To see that your code doing some progress,
																  // please print out the pairs on the screen.
			}			

		}

		inputFileRAF.close();
		outputWriter.close();
		System.out.println("I'm done...");
	}
	
	/**
	 * printAllPossibleMatches()
	 * @param String cipher
	 * @return void
	 * @function Prints current possible key matches for given cipher 
	 */
	public static void printAllPossibleMatches(String cipher){
		Iterator it = possibleKeys.iterator();
		System.out.println("Possible matches:\n");
		
		while(it.hasNext()) {
			String plainText = decrypt((Mapping)it.next(), cipher);
			System.out.println(plainText);
		}
	}
	
	/**
	 * beginDecipher()
	 * @param String cipher
	 * @return String plainText
	 * @function Wrapper function for Decipher method to initialize deciphering process
	 */
	public static String beginDecipher(String cipher) {
		globalCipher = cipher;
		String plainText = "";
		
		// Split ciphertext into string array
		//String[] cipherWords = cipher.split(" ");
		String[] sortedCipherWords = cipher.split(" ");
		// Sort by length
		Arrays.sort(sortedCipherWords, new StringLengthComparator());
		Stack<String> words = new Stack<String>();
		Mapping finalMap = new Mapping();
		
		for(int i = sortedCipherWords.length-1; i >= 0; i--)
			words.push(sortedCipherWords[i]);
		
		// TODO: words stack error-prone if empty - Option 1: Add conditional wrapper to encapsulate word pattern processing; Option 2: Fix decipher() logic to encapsulate word, pattern, candidate iteration 
		String word = words.pop();
		String pattern = pattern(word);
		//System.out.println(word + ": " + pattern);
		Vector candidates = (Vector) patterns.get(pattern);
		Iterator it = candidates.iterator();
		while(it.hasNext())
		{
			String s = (String)it.next();
			finalMap = new Mapping(word, s);
			
			finalMap = decipher(finalMap, words);
			if(validKey(finalMap))
				break;
		}
		
		if(!validKey(finalMap))
		{
			Iterator it1 = possibleKeys.iterator();
			while(it1.hasNext())
			{
				Mapping possibleMap = (Mapping)it1.next();
				if(evaluate(finalMap) < evaluate(possibleMap))
				{
					//System.out.println("Old Key:" + decrypt(finalMap, cipher));
					finalMap = possibleMap;
					//System.out.println("New Key:" + decrypt(possibleMap, cipher));
				}
			}
		}
		
		// Chomping for calculating accuracy
		plainText = decrypt(finalMap, cipher);
		/*StringBuilder s = new StringBuilder();
		List lst = Arrays.asList(plainText.split(" "));
		Iterator it2 = lst.iterator();
		while(it2.hasNext())
			s.append(it2.next());
		plainText = s.toString();
		*/
		return plainText;
	}
	
	/**
	 * decipher()
	 * @param Mapping currentMap
	 * @param Stack<String> words
	 * @return Mapping currentMap
	 * @function Recursively deciphers correct Mapping
	 */
	public static Mapping decipher(Mapping currentMap, Stack<String> words){
		if(words.empty())
			return currentMap;
		
		Mapping preservedMapping = new Mapping(currentMap);
		String word = words.pop();
		String pattern = pattern(word);
		//System.out.println(word + ": " + pattern);
		Vector candidates = (Vector) patterns.get(pattern);
		
		Iterator it = candidates.iterator();
		while(it.hasNext())
		{
			String s = (String)it.next();
			Mapping map = new Mapping(word, s);
			
			//System.out.println(s);

			if(isConsistent(currentMap, map))
			{
			//System.out.println(s);							
			//System.out.print("Current Mapping:       ");
			//currentMap.print();
			//System.out.print("New Mapping:           ");
			//map.print();
								
				Mapping mergedMap = mergeKeys(currentMap, map);
				
				//System.out.print("Merged Mapping:        ");
				//map.print();
				
				Mapping newMap = decipher(mergedMap, words);
				
				//System.out.print("Returned Mapping:      ");
				//map.print();
				
				if(validKey(newMap))
				{
					//possibleKeys.add(map);
					currentMap = newMap;
					return currentMap;
				} /*else if(validKey(mergedMap))
				{
					currentMap = mergedMap;
					return currentMap;
				}*/
				else
				{
					possibleKeys.add(currentMap);
					currentMap = preservedMapping;
				}
			}
		}
		if(words.empty() || !words.peek().equals(word))
			words.push(word);
		return currentMap;
	}
	
	/**
	 * validKey()
	 * @param Mapping map
	 * @return bool
	 * @function Validates given key mapping against cipher text and word dictionary
	 */
	public static boolean validKey(Mapping map) {
		String[] cipherWords = globalCipher.split(" ");
		int numCorrect = 0;

		for(int i = 0; i < cipherWords.length; i++)
		{
			String message = decrypt(map, cipherWords[i]);
			if(message.length() == cipherWords[i].length() && dictionary.containsKey(message))
				numCorrect++;
		} 
		
		if(numCorrect == cipherWords.length)
			return true;
		
		return false;
	}
	
	/**
	 * evaluate()
	 * @param Mapping map
	 * @return float score
	 * @function Calculates scoring for a given Mapping to evaluate accuracy
	 */
	public static float evaluate(Mapping map) {
		String[] cipherWords = globalCipher.split(" ");
		float score = 0;

		for(int i = 0; i < cipherWords.length; i++)
		{
			String message = decrypt(map, cipherWords[i]);
			if(message.length() == cipherWords[i].length() && dictionary.containsKey(message))
				score += message.length()*(.35);
		}
		
		// TODO: Add final validation against plain-text sources using recursive substring search/comparisons - if no match is found, key mapping is false
		
		return score;
	}
	
	public static String decrypt(Mapping mapping, String cipher) {
		char[] key = mapping.getKey();
		String plainText = "";
		for(int i = 0; i < cipher.length(); i++)
		{
			if((int)cipher.charAt(i)-(int)'A' < 0)
				plainText += " ";
			else if(key[(int)cipher.charAt(i)-(int)'A'] == (char)0)
				plainText += "*";
			else
				plainText += key[(int)cipher.charAt(i)-(int)'A'];
		}
		
		return plainText;
	}
	
	public static Mapping mergeKeys(Mapping currentMapping, Mapping newMapping) {
		char[] currentKey = currentMapping.getKey();
		char[] newKey = newMapping.getKey();
		
		for(int i = 0; i < 26; i++)
		{
			if(currentKey[i] == (char)0)
				currentKey[i] = newKey[i];
		}
		
		currentMapping.setKey(currentKey);
		
		return currentMapping;
	}
	
	public static Mapping unmerge() {
		return new Mapping();
	}
	
	/**
	 * isConsistent()
	 * @param Mapping currentMapping
	 * @param Mapping newMapping
	 * @return bool
	 * @function Checks Mapping consistency
	 */
	public static boolean isConsistent(Mapping currentMapping, Mapping newMapping) {
		char[] currentKey = currentMapping.getKey();
		char[] newKey = newMapping.getKey();
		
		
		for(int i = 0; i < 26; i++)
		{
			// Check for redundant mappings
			for(int j = 0; j < 26; j++)
			{
				if(newKey[j] == (char)0)
					continue;
				if(newKey[j] == currentKey[i] && i != j)
					return false;
			}
			
			// Check for conflicted mappings
			if(currentKey[i] == (char)0 || newKey[i] == (char)0)
				continue;

			if(currentKey[i] != newKey[i])
				return false;
				

		}

		return true;
	}

	public static class StringLengthComparator implements Comparator<String> {
		public int compare(String o1, String o2) {
			if (o1.length() > o2.length()) {
			  return -1;
			} else if (o1.length() < o2.length()) {
			  return 1;
			} else {
			  return 0;
			}
		}
	}
	
	public static class FrequencyComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if(dictionary.get(o1) < dictionary.get(o2))
				return -1;
			else if(dictionary.get(o1) > dictionary.get(o2))
				return 1;
			else
				return 0;
		}
	}
	
	/**
	*	parseFile()
	*	@param String fileName
	*	@return void
	*	@function Compiles the given fileName and creates a dictionary of all words 
	*/
	public static void parseFile(String fileName) {
		File srcFile = new File(fileName);

		try {
			Scanner fileScanner = new Scanner(srcFile);
			StringBuilder rawInput = new StringBuilder();
			
			while (fileScanner.hasNextLine()) {
				rawInput.append(fileScanner.nextLine());
				rawInput.append(" ");
			}
			
			String input = rawInput.toString();
		
			input = input.toUpperCase();
			//Replace anything that is not A to Z or space with a space character
			input = input.replaceAll("[^A-Z\\s]", " ");
			//Replace all instances of more than one consecutive whitespace character with a single space character
			input = input.replaceAll("\\s+", " ");
			//Replace last whitespace with nothing
			input = input.replaceAll("$ ", "");

			
			String[] wordList = input.split(" ");
			Arrays.sort(wordList);
			
			for(int i = 0; i < wordList.length; i++)
			{
				//Insert into dictionary
				if(dictionary.containsKey(wordList[i]))
				{
					int count = dictionary.get(wordList[i]);
					dictionary.put(wordList[i], count+1);
				}
				else
				{
					dictionary.put(wordList[i], 1);
				}
			}

			fileScanner.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * outputDictionary()
	 * @param
	 * @return void
	 * @function Outputs contents of dictionary file 
	 */
	public static void outputDictionary()
	{
		File outputFile = new File("dictionary.txt");
		
		try{
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
			
			// Sort dictionary
			TreeMap sortedDictionary = new TreeMap(dictionary);
			Iterator it = sortedDictionary.entrySet().iterator();
			// Output to file
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				outputWriter.write(entry.getKey() + " " + entry.getValue());
				outputWriter.newLine();
			}
			
			outputWriter.close();
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * pattern()
	 * @param String word
	 * @return StringBuffer patturnBuffer
	 * @function Converts a word into a pattern
	 */
	public static String pattern(String word) {
		StringBuffer patternBuffer = new StringBuffer();
		patternBuffer.setLength(0);
		Mapping patternMapping = new Mapping();
		int count = 0;
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if(patternMapping.getKey()[(int)c - (int)'A'] == (char)0)
			{
				patternMapping.getKey()[(int)c - (int)'A'] = (char)(count + (int)'A');
				count++;
			}
		}
		for(int i = 0; i < word.length(); i++)
		{
			char c = word.charAt(i);
			char p = patternMapping.getKey()[(int)c - (int)'A'];
			patternBuffer.append(p);
		}
		return patternBuffer.toString();
	}
	
	/**
	 * patternMatches()
	 * @param String word
	 * @return Vector vec
	 * @function Constructs collection of pattern matches for a given word
	 */
	public static Vector patternMatches(String word) {
		String pat = pattern(word);
		Vector vec = (Vector) patterns.get(pat);
		if (vec == null) {
			vec = new Vector();
			vec.add(word);
			patterns.put(pat, vec);
		}
		else if (!vec.contains(word))
		{
			vec.add(word);
			//Collections.sort(vec, new FrequencyComparator());
		}
		return vec;
	}
	
	/**
	 * loadDictionary()
	 * @param String fileName
	 * @return bool
	 * @function Loads existing dictionary file
	 */
	public static boolean loadDictionary(String fileName) {
		File f = new File(fileName);
		try {
			Scanner fileScanner = new Scanner(f);
			String word = "";
			String pattern = "";
			int freq = 0;
			
			while (fileScanner.hasNextLine()) {
				word = fileScanner.nextLine();
				String[] words = word.split(" ");
				word = words[0];
				freq = Integer.parseInt(words[1]);
				wordFreqCount += freq;
				
				if(!dictionary.containsKey(word))
				{
					dictionary.put(word, freq);

					HashMap<String, Integer> matches = (HashMap<String, Integer>)wordsByLength[word.length()];
					if(!matches.containsKey(word))
						((HashMap<String, Integer>)wordsByLength[word.length()]).put(word, freq);
				}

				patternMatches(word);		// Check for pattern matches and add word if necessary
			}
			
			fileScanner.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

}