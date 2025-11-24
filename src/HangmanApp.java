import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

/*
* The purpose of this program is to create a Hangman game using JavaFX, allowing the user to guess letters
* to reveal a hidden 5-letter word while tracking incorrect guesses with hangman images.
*
* Written by Marah Awad =D
*/

public class HangmanApp extends Application {

    //number of wrong guesses allowed before the game ends
    private int maxWrongGuesses = 6;

    //interface components
    private ImageView hangmanImageView;
    private Label wordLabel, guessedLettersLabel, messageLabel;
    private TextField guessField;
    private Button guessButton, newGameButton;

    //data for the game
    private ArrayList<String> wordList = new ArrayList<>(); //stores all 5-letter words
    private String secretWord; //the word to guess
    private StringBuilder displayedWord; //what the player sees (ex: _ a _ _ e)
    private String guessedLetters = ""; //keeps track of all guessed letters
    private int wrongGuesses; //counts the number of incorrect guesses

    @Override
    public void start(Stage primaryStage) {
        // load all words at startup
        loadWords();

        // create and set up the user interface components
        hangmanImageView = new ImageView();
        hangmanImageView.setFitWidth(250);
        hangmanImageView.setPreserveRatio(true);

        //label showing the word with underscores and revealed letters
        wordLabel = new Label();

        //labels for guessed letters and status messages
        guessedLettersLabel = new Label("Guessed Letters: ");
        messageLabel = new Label();

        //text field for entering guesses
        guessField = new TextField();
        guessField.setPromptText("Enter a letter");
        guessField.setPrefWidth(50);

        //buttons =P
        guessButton = new Button("Guess");
        newGameButton = new Button("New Game");

        //button actions
        guessButton.setOnAction(e -> handleGuess());
        newGameButton.setOnAction(e -> startNewGame());

        //arrange everything vertically on the right side
        VBox gameArea = new VBox(10);
        gameArea.getChildren().addAll(wordLabel, guessedLettersLabel, guessField, guessButton, messageLabel, newGameButton);

        //arrange image to the left, and game area to the right
        HBox root = new HBox(20);
        root.getChildren().addAll(hangmanImageView, gameArea);

        //create and show the window
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Hangman Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start first round of the game
        startNewGame();
    }

    //read 5-letter words from the file and stores them in a wordlist
    private void loadWords() {
        File file = new File("resources/wordlist.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (Exception e) {
            throw new RuntimeException("Could not open wordlist.txt");
        }

        //adds only words with exactly 5 letters
        while (scanner.hasNext()) {
            String word = scanner.next().trim().toLowerCase();
            if (word.length() == 5) {
                wordList.add(word);
            }
        }
        scanner.close();
    }

    //starts or resets a new game
    private void startNewGame() {
        if (wordList.isEmpty()) {
            messageLabel.setText("No words loaded!");
            return;
        }

        //choose a random word from the list
        Random rand = new Random();
        secretWord = wordList.get(rand.nextInt(wordList.size()));
        System.out.println("Secret word (for debugging): " + secretWord);

        //resets all game variables
        displayedWord = new StringBuilder("_".repeat(secretWord.length()));
        guessedLetters = "";
        wrongGuesses = 0;

        //reset all visuals
        updateWordLabel();
        guessedLettersLabel.setText("Guessed Letters: ");
        messageLabel.setText("");
        hangmanImageView.setImage(new Image("file:resources/hangman0.png"));

        //re-enable input and clear text field
        guessField.setDisable(false);
        guessButton.setDisable(false);
        guessField.clear();
    }

    //handles each letter guess from the user
    private void handleGuess() {
        String input = guessField.getText().toLowerCase().trim();

        //make sure user entered exactly one letter
        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            messageLabel.setText("Please enter a single letter.");
            guessField.clear();
            return;
        }

        char guessedChar = input.charAt(0);

        //check if the letter was already guessed
        if (guessedLetters.contains(String.valueOf(guessedChar))) {
            messageLabel.setText("You already guessed that letter!");
            guessField.clear();
            return;
        }

        //add the guessed letter to our list
        guessedLetters += guessedChar + " ";
        guessedLettersLabel.setText("Guessed Letters: " + guessedLetters);

        //case 1: correct guess
        if (secretWord.indexOf(guessedChar) >= 0) {
            // Correct guess
            for (int i = 0; i < secretWord.length(); i++) {
                if (secretWord.charAt(i) == guessedChar) {
                    displayedWord.setCharAt(i, guessedChar);
                }
            }
            updateWordLabel();
            messageLabel.setText("Good guess!");

            //check if the player won
            if (displayedWord.toString().equals(secretWord)) {
                messageLabel.setText("Congratulations! You won!");
                endGame();
            }
        //case 2: incorrect guess
        } else {
            // Wrong guess
            wrongGuesses++;
            hangmanImageView.setImage(new Image("file:resources/hangman" + wrongGuesses + ".png"));
            messageLabel.setText("Incorrect guess!");

            //check if player lost
            if (wrongGuesses >= maxWrongGuesses) {
                messageLabel.setText("Game over! Word: " + secretWord);
                endGame();
            }
        }

        //clear text field after every game
        guessField.clear();
    }

    //updates the label showing the current word (adds spaces between underscores)
    private void updateWordLabel() {
        String spacedWord = displayedWord.toString().replaceAll("", " ").trim();
        wordLabel.setText(spacedWord);
    }

    //disables guessing after win or loss
    private void endGame() {
        guessField.setDisable(true);
        guessButton.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
