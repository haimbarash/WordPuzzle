import java.util.Arrays;
import java.util.Scanner;
import java.math.*;
import java.util.Random;

public class WordPuzzle {
	public static final char HIDDEN_CHAR = '_';
	
	/*
	 * @pre: template is legal for word
	 */
	public static char[] createPuzzleFromTemplate(String word, boolean[] template) {
		String puzzleWord = "";
		for (int i = 0; i< word.length(); i++) {
			if (template[i])
				puzzleWord += HIDDEN_CHAR;
			else
				puzzleWord += word.charAt(i);
		}
		return puzzleWord.toCharArray();
	}

	public static boolean checkLegalTemplate(String word, boolean[] template) {
		if (word.length() != template.length) //Checks the lengths of the word and the template
			return false;
		int[] alphabetHiddenStatus = new int[26];
		int currentLetterAscii;
		boolean trueAppeared = false; //at least one hidden character (true)
		boolean falseAppeared = false;//at least one visible character (false)
		for(int i =0; i<word.length();i++) {
			if (template[i]) //Checks for at least one hidden character (true in template array)
				trueAppeared = true;
			else //  Checks for at least one visible character (false in template array)
				falseAppeared = true;
			currentLetterAscii = word.charAt(i);
			if (alphabetHiddenStatus[currentLetterAscii-97] == 0) { //first appearance of a char in the word
				if (template[i])
					alphabetHiddenStatus[currentLetterAscii-97] = 1; // alphabetHiddenStatus is an int array: 1==true; -1==false; 0== didn't initialized
				else
					alphabetHiddenStatus[currentLetterAscii-97] = -1;
			}
			else {
				if ((alphabetHiddenStatus[currentLetterAscii-97] == 1 && template[i] == false)
						|| (alphabetHiddenStatus[currentLetterAscii-97] == -1 && template[i] == true)) //mismuch between the last appearance of the current letter; 
					return false;
			}
			
		}
		return trueAppeared && falseAppeared;
	}
	
	/*
	 * @pre: 0 < k < word.lenght(), word.length() <= 10
	 */
	public static boolean[][] getAllLegalTemplates(String word, int k){
		int matrixLineIndex=0;
		int templateComb = nChoosek(word.length(),k);
		boolean[][] legalTemplates = new boolean[templateComb][word.length()]; // output matrix
		boolean[] currentTemplate;
		String binnaryStr;
		
		for (int i = k; i< Math.pow(2, word.length());i++) {
			binnaryStr = Integer.toBinaryString(i);
			if(numberOfOnes(binnaryStr)==k) {
				binnaryStr =addZeros(binnaryStr,word.length());
				currentTemplate = onesStrToBollArray(binnaryStr);
				if (checkLegalTemplate(word, currentTemplate)){
					for(int j=0; j<word.length();j++) {
						legalTemplates[matrixLineIndex][j]=currentTemplate[j];
					}
					matrixLineIndex++;					
				}
			}
		}
		return Arrays.copyOfRange(legalTemplates, 0, matrixLineIndex);
	}
	
	
	private static String addZeros(String binnaryStr, int length) {
		char[] charArray = new char[length - binnaryStr.length()];
		Arrays.fill(charArray, '0');
		String str = new String(charArray);
		return str + binnaryStr; 
	}

	private static boolean[] onesStrToBollArray(String binnaryStr) {
		boolean[] currentTemplate= new boolean[binnaryStr.length()];
		for (int i=0;i<binnaryStr.length();i++) {
			if (binnaryStr.charAt(i)=='1')
				currentTemplate[i]=true;
			else
				currentTemplate[i]=false;
			
		}
		return currentTemplate;
	}

	private static int numberOfOnes(String binnaryStr) {
		int countOne = 0;
		for (int i=0;i<binnaryStr.length();i++) {
			if (binnaryStr.charAt(i)== '1')
				countOne++;				
		}
		return countOne;
	}

	private static int nChoosek(int n, int k) {
		int nFactorial = 1;
		int kFactorial = 1;
		int nMinuskFactorial = 1;
		for (int i =1; i<=n;i++) {
			nFactorial = nFactorial*i;
			if (i==k)
				kFactorial = nFactorial;
			if (i== (n-k))
				nMinuskFactorial = nFactorial;			
		}
		return nFactorial/(kFactorial*nMinuskFactorial);
	}

	/*
	 * @pre: puzzle is a legal puzzle constructed from word, guess is in [a...z]
	 */
	public static int applyGuess(char guess, String word, char[] puzzle) {
		int count = 0;
		if (word.indexOf(guess) >-1) {
			for (int i = 0 ; i < word.length() ; i++) {
				if (word.charAt(i) == guess && puzzle[i]=='_') {
					count++;
					puzzle[i] = guess;
				}
			}
		}
		return count;
	}
	

	/*
	 * @pre: puzzle is a legal puzzle constructed from word
	 * @pre: puzzle contains at least one hidden character. 
	 * @pre: there are at least 2 letters that don't appear in word, and the user didn't guess
	 */
	public static char[] getHint(String word, char[] puzzle, boolean[] already_guessed) {
		int hiddenInPuzzle_count = 0;
		String hiddenInPuzzle_str = "";
		int leftToGuess_count = 0;
		String leftToGuess_str = "";
		char[] hint= new char[2];
		for (int i =0;i < puzzle.length; i++) {
			if (puzzle[i] == '_') {
				hiddenInPuzzle_count++;
				hiddenInPuzzle_str += word.charAt(i);
			}
		}
		for (int i =0;i < already_guessed.length; i++) {
			if (already_guessed[i] == false && hiddenInPuzzle_str.indexOf((char)(97 + i)) < 0
					 && word.indexOf((char)(97 + i)) < 0) {
				leftToGuess_count++;
				leftToGuess_str += (char)(97 + i);				
			}
		}
        // create instance of Random class
        Random rand = new Random(19);
  
        // Generate random integers in range 0 to the string length
        int rand_int1 = rand.nextInt(hiddenInPuzzle_str.length());
        int rand_int2 = rand.nextInt(leftToGuess_str.length());
        if (hiddenInPuzzle_str.charAt(rand_int1) < leftToGuess_str.charAt(rand_int2)) {
        	hint[0] = hiddenInPuzzle_str.charAt(rand_int1);
        	hint[1] = leftToGuess_str.charAt(rand_int2);
        }
        else {
        	hint[1] = hiddenInPuzzle_str.charAt(rand_int1);
        	hint[0] = leftToGuess_str.charAt(rand_int2);
        }
        return hint;
        
	}

	

	public static char[] mainTemplateSettings(String word, Scanner inputScanner) {
		printSettingsMessage();
		int settingsInProsses = 1;
		while (settingsInProsses == 1) {
			printSelectTemplate();
			int selectedTemplate = inputScanner.nextInt();
			if (selectedTemplate == 1) {
				printSelectNumberOfHiddenChars();
				int selectedNumberOfHiddenChars = inputScanner.nextInt();
				boolean[][] legalTemplates =getAllLegalTemplates(word, selectedNumberOfHiddenChars);
				if (legalTemplates.length<0) {
					printWrongTemplateParameters();
				}
				else {
					Random rand = new Random(19);
					// Generate random integers in range 0 to str length
					int rand_int1 = rand.nextInt(legalTemplates.length);
					//inputScanner.close();
					return createPuzzleFromTemplate(word, legalTemplates[rand_int1]);
				}
			}
			if (selectedTemplate == 2) {
				printEnterPuzzleTemplate();
				String str_userTemplate = inputScanner.next();
				String[] tokens_userTemplate = str_userTemplate.split(",");
				boolean[] userTemplateToCheck = new boolean[tokens_userTemplate.length];
				for(int i=0;i<tokens_userTemplate.length;i++) {
					if(tokens_userTemplate[i].equals("X"))
						userTemplateToCheck[i]=false;
					else
						if(tokens_userTemplate[i].equals("_"))
							userTemplateToCheck[i]=true;
				}
				if (checkLegalTemplate(word, userTemplateToCheck)) {
					return createPuzzleFromTemplate(word, userTemplateToCheck);
				}
				else
					printWrongTemplateParameters();			
			}
		
		}
		return null;
	}
	
	public static void mainGame(String word, char[] puzzle, Scanner inputScanner){
		printGameStageMessage();
		int leftAttempts = numberOfHiddenChars(puzzle) + 3;
		boolean[] already_guessed= new boolean[26];
		char[] hints= new char[2];
		int gameInProsses = 1;
		while (gameInProsses == 1) {
			System.out.println(String.valueOf(puzzle));
			printEnterYourGuessMessage();
			String selectedTemplate = inputScanner.next();
			//ask for hint
			if (selectedTemplate.charAt(0)=='H') {
				hints = getHint(word, puzzle, already_guessed);
				printHint(hints);				
			}
			//Guess try
			if (selectedTemplate.charAt(0)!='H' && selectedTemplate.charAt(0)>96 && selectedTemplate.charAt(0)<123) {
				already_guessed[selectedTemplate.charAt(0)-97] = true;
				//Correct guess:
				if(applyGuess(selectedTemplate.charAt(0), word, puzzle)>0) {
					//Success- the player won the game
					if(numberOfHiddenChars(puzzle) == 0) {
						printWinMessage();	
						gameInProsses = 0;
					}
					else {
						leftAttempts--;
						printCorrectGuess(leftAttempts);						
					}
				}
				else {
					leftAttempts--;
					printWrongGuess(leftAttempts);
				}
			}
			//wrong input
			if (selectedTemplate.charAt(0)!='H' && (selectedTemplate.charAt(0)<97 || selectedTemplate.charAt(0)>122)) {
				System.out.println("Wrong input, please type your guess (a-z) or H for a hint (;");
			}
			if (leftAttempts==0) {
				printGameOver();
				gameInProsses = 0;
			}
		}
	}
				
//	* @pre: puzzle is a legal puzzle constructed from word				
private static int numberOfHiddenChars(char[] puzzle) {
		int count = 0;
		for(int i=0;i<puzzle.length;i++) {
			if (puzzle[i] == '_')
				count++;
		}		
		return count;
	}

	public static void main(String[] args) throws Exception { 
		if (args.length < 1){
			throw new Exception("You must specify one argument to this program");
		}
		String wordForPuzzle = args[0].toLowerCase();
		if (wordForPuzzle.length() > 10){
			throw new Exception("The word should not contain more than 10 characters");
		}
		Scanner inputScanner = new Scanner(System.in);
		char[] puzzle = mainTemplateSettings(wordForPuzzle, inputScanner);
		mainGame(wordForPuzzle, puzzle, inputScanner);
		inputScanner.close();
	}


	public static void printSettingsMessage() {
		System.out.println("--- Settings stage ---");
	}

	public static void printEnterWord() {
		System.out.println("Enter word:");
	}
	
	public static void printSelectNumberOfHiddenChars(){
		System.out.println("Enter number of hidden characters:");
	}
	public static void printSelectTemplate() {
		System.out.println("Choose a (1) random or (2) manual template:");
	}
	
	public static void printWrongTemplateParameters() {
		System.out.println("Cannot generate puzzle, try again.");
	}
	
	public static void printEnterPuzzleTemplate() {
		System.out.println("Enter your puzzle template:");
	}


	public static void printPuzzle(char[] puzzle) {
		System.out.println(puzzle);
	}


	public static void printGameStageMessage() {
		System.out.println("--- Game stage ---");
	}

	public static void printEnterYourGuessMessage() {
		System.out.println("Enter your guess:");
	}

	public static void printHint(char[] hist){
		System.out.println(String.format("Here's a hint for you: choose either %s or %s.", hist[0] ,hist[1]));

	}
	public static void printCorrectGuess(int attemptsNum) {
		System.out.println("Correct Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWrongGuess(int attemptsNum) {
		System.out.println("Wrong Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWinMessage() {
		System.out.println("Congratulations! You solved the puzzle!");
	}

	public static void printGameOver() {
		System.out.println("Game over!");
	}

}
