import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class hangmen extends Application {

    private final String[] easyWords = {"test", "book", "game", "code", "java"};
    private final String[] mediumWords = {"apple", "table", "house", "chair", "mouse"};
    private final String[] hardWords = {"planet", "laptop", "garden", "bridge", "rocket"};
    private final Random random = new Random();

    private String selectedWord;
    private StringBuilder currentWordState;
    private int remainingAttempts;
    private final int maxAttempts = 6;
    private final Set<Character> guessedLetters = new HashSet<>();
    private int hangmanStep = 0;

    @Override
    public void start(Stage primaryStage) {
        // Main Menu
        VBox mainMenu = new VBox(10);
        mainMenu.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Button startButton = new Button("Start");
        Button aboutButton = new Button("About");
        Button exitButton = new Button("Exit");

        mainMenu.getChildren().addAll(startButton, aboutButton, exitButton);

        Scene mainMenuScene = new Scene(mainMenu, 400, 300);

        // About Action
        aboutButton.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText(null);
            alert.setContentText("Hangman Game - Guess the word before running out of attempts!");
            alert.showAndWait();
        });

        // Exit Action
        exitButton.setOnAction(_ -> primaryStage.close());

        // Difficulty Selection
        VBox difficultyMenu = new VBox(10);
        difficultyMenu.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");
        Button randomButton = new Button("Random");

        difficultyMenu.getChildren().addAll(easyButton, mediumButton, hardButton, randomButton);
        Scene difficultyScene = new Scene(difficultyMenu, 400, 300);

        // Start Button Action
        startButton.setOnAction(_ -> primaryStage.setScene(difficultyScene));

        // Gameplay Screen Setup
        VBox gameScreen = new VBox(10);
        gameScreen.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label wordLabel = new Label();
        Label attemptsLabel = new Label("Remaining Attempts: " + maxAttempts);
        TextField inputField = new TextField();
        inputField.setPromptText("Enter a letter");
        Button guessButton = new Button("Guess");
        Label guessedLettersLabel = new Label("Guessed Letters: ");
        Label messageLabel = new Label();
        Button backToMenuButton = new Button("Back to Main Menu");

        Canvas hangmanCanvas = new Canvas(200, 200);
        GraphicsContext gc = hangmanCanvas.getGraphicsContext2D();
        drawHangman(gc, 0);

        gameScreen.getChildren().addAll(hangmanCanvas, wordLabel, attemptsLabel, inputField, guessButton, guessedLettersLabel, messageLabel, backToMenuButton);
        Scene gameScene = new Scene(gameScreen, 400, 400);

        // Back to Main Menu Action
        backToMenuButton.setOnAction(_ -> primaryStage.setScene(mainMenuScene));

        // Difficulty Actions
        easyButton.setOnAction(_ -> startGame(primaryStage, gameScene, easyWords, wordLabel, attemptsLabel, guessedLettersLabel, messageLabel, gc, guessButton));
        mediumButton.setOnAction(_ -> startGame(primaryStage, gameScene, mediumWords, wordLabel, attemptsLabel, guessedLettersLabel, messageLabel, gc, guessButton));
        hardButton.setOnAction(_ -> startGame(primaryStage, gameScene, hardWords, wordLabel, attemptsLabel, guessedLettersLabel, messageLabel, gc, guessButton));
        randomButton.setOnAction(_ -> {
            String[] allWords = combineArrays(easyWords, mediumWords, hardWords);
            startGame(primaryStage, gameScene, allWords, wordLabel, attemptsLabel, guessedLettersLabel, messageLabel, gc, guessButton);
        });

        // Guess Button Action
        guessButton.setOnAction(_ -> {
            String guess = inputField.getText().toLowerCase();
            inputField.clear();

            if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) {
                messageLabel.setText("Please enter a single valid letter.");
                return;
            }

            char guessedChar = guess.charAt(0);
            if (guessedLetters.contains(guessedChar)) {
                messageLabel.setText("You already guessed that letter!");
                return;
            }

            guessedLetters.add(guessedChar);
            guessedLettersLabel.setText("Guessed Letters: " + guessedLetters);

            if (selectedWord.contains(guess)) {
                for (int i = 0; i < selectedWord.length(); i++) {
                    if (selectedWord.charAt(i) == guessedChar) {
                        currentWordState.setCharAt(i, guessedChar);
                    }
                }
                wordLabel.setText(currentWordState.toString());

                if (currentWordState.toString().equals(selectedWord)) {
                    messageLabel.setText("Congratulations! You guessed the word!");
                    guessButton.setDisable(true);
                }
            } else {
                remainingAttempts--;
                hangmanStep++;
                drawHangman(gc, hangmanStep);
                attemptsLabel.setText("Remaining Attempts: " + remainingAttempts);

                if (remainingAttempts <= 0) {
                    messageLabel.setText("Game Over! The word was: " + selectedWord);
                    guessButton.setDisable(true);
                } else {
                    messageLabel.setText("Incorrect guess. Try again.");
                }
            }
        });

        // Set Main Menu as the starting scene
        primaryStage.setTitle("Hangman Game");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    private void startGame(Stage stage, Scene gameScene, String[] wordPool, Label wordLabel, Label attemptsLabel, Label guessedLettersLabel, Label messageLabel, GraphicsContext gc, Button guessButton) {
        selectedWord = wordPool[random.nextInt(wordPool.length)];
        currentWordState = new StringBuilder("_".repeat(selectedWord.length()));
        remainingAttempts = maxAttempts;
        hangmanStep = 0;
        guessedLetters.clear();

        wordLabel.setText(currentWordState.toString());
        attemptsLabel.setText("Remaining Attempts: " + remainingAttempts);
        guessedLettersLabel.setText("Guessed Letters: ");
        messageLabel.setText("");
        gc.clearRect(0, 0, 200, 200);
        drawHangman(gc, 0);
        guessButton.setDisable(false);
        stage.setScene(gameScene);
    }

    private void drawHangman(GraphicsContext gc, int step) {
        gc.strokeLine(20, 180, 100, 180); // Ground
        gc.strokeLine(60, 180, 60, 20); // Pole
        gc.strokeLine(60, 20, 120, 20); // Top bar
        gc.strokeLine(120, 20, 120, 40); // Rope

        if (step >= 1) gc.strokeOval(110, 40, 20, 20); // Head
        if (step >= 2) gc.strokeLine(120, 60, 120, 100); // Body
        if (step >= 3) gc.strokeLine(120, 70, 100, 90); // Left arm
        if (step >= 4) gc.strokeLine(120, 70, 140, 90); // Right arm
        if (step >= 5) gc.strokeLine(120, 100, 100, 130); // Left leg
        if (step >= 6) gc.strokeLine(120, 100, 140, 130); // Right leg
    }

    private String[] combineArrays(String[]... arrays) {
        return java.util.Arrays.stream(arrays).flatMap(java.util.Arrays::stream).toArray(String[]::new);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
