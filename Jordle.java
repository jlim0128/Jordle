import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
/**
 * Simple game that gives 6 chances to guess 5-letter word.
 * @author jlim80
 * @version 4-18-2022
 */
public class Jordle extends Application {
    private Stage startStage;
    private Scene startScene;
    private Scene mainScene;
    private BorderPane root;
    private GridPane centerPane;
    private StackPane rightPane;
    private String inputKey;
    private int gridX = 0;
    private int gridY = 0;
    private Button restartButton;
    private Label statusLabel = new Label();
    private Label letter;
    private String randword;
    private String[] answer = new String[5];
    private String[] guess = new String[5];
    private StackPane letterPane;
    private boolean[] appearedOrNot = {false, false, false, false, false};
    private boolean consumeInput = false;
    /**
     * Main method for the class.
     * @param args Array of String representing the terminal command.
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Start method for the game.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            createStartScene(primaryStage);
            createMainScene(primaryStage);

            primaryStage.setScene(startScene);
            primaryStage.setTitle("Jordle");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Method that creates the main scene of the game.
     * @param primaryStage Stage representing the primary stage.
     */
    private void createMainScene(Stage primaryStage) {
        primaryStage.setTitle("Jordle");
        root = new BorderPane();
        mainScene = new Scene(root, 500, 700);
        root.setStyle("-fx-background-color: #303030");
        root.requestFocus();

        //TOP
        VBox topPane = new VBox();
        topPane.setStyle("-fx-background-color: #303030");
        //Text: Title
        DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#000"), 3, 5, 7, 7);
        Label topText = new Label("J O R D L E");
        topText.setFont(new Font("Tahoma", 80));
        topText.setTextFill(Color.web("F9BF3B"));
        topText.setEffect(dropShadow);
        topPane.getChildren().addAll(topText);
        topPane.setAlignment(Pos.TOP_CENTER);
        topPane.setSpacing(5);
        root.setTop(topPane);
        //BOTTOM
        HBox bottomPane = new HBox();
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setPadding(new Insets(20, 10, 20, 10));
        bottomPane.setStyle("-fx-background-color: #303030");
        //Text: Status
        statusLabel.setText("Try guessing a word!");
        statusLabel.setFont(new Font("Tahoma", 15));
        statusLabel.setTextFill(Color.web("F9BF3B"));
        //Button: Restart
        restartButton = new Button();
        restartButton.setText("Restart");
        restartButton.setOnAction(e -> {
            root.requestFocus();
            restart();
        });
        restartButton.setStyle("-fx-background-color: #F9BF3B; -fx-font-family: arial; -fx-font-size: 15");
        //Button: Instruction
        Button bottomBtn2 = new Button();
        bottomBtn2.setText("Instruction");
        bottomBtn2.setStyle("-fx-background-color: #F9BF3B; -fx-font-family: arial; -fx-font-size: 15");
        bottomBtn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage instStage = new Stage();
                instStage.setTitle("Instruction");
                StackPane instPane = new StackPane();
                Label instLabel = new Label("Welcome to Jordle! You have 6 chances to"
                    + "guess the 5-letter word. Good Luck!");
                instLabel.setWrapText(true);
                instPane.setStyle("-fx-background-color: white; -fx-border-color: black");
                instLabel.setStyle("-fx-font-size: 15; -fx-padding: 10px; -fx-text-alignment: CENTER");
                instPane.getChildren().add(instLabel);
                instPane.setAlignment(Pos.CENTER);
                instStage.setScene(new Scene(instPane, 200, 200));
                instStage.show();
                root.requestFocus();
            }
        });
        bottomPane.getChildren().addAll(statusLabel, restartButton, bottomBtn2);
        bottomPane.setSpacing(15);
        root.setBottom(bottomPane);
        //RIGHT
        rightPane = new StackPane();
        rightPane.setMinWidth(20);
        rightPane.setBackground(new Background(new BackgroundFill(
            Color.web("303030"), new CornerRadii(0), new Insets(0))));
        root.setRight(rightPane);
        //LEFT
        StackPane leftPane = new StackPane();
        leftPane.setMinWidth(20);
        leftPane.setBackground(new Background(new BackgroundFill(Color.web("303030"),
            new CornerRadii(0), new Insets(0))));
        root.setLeft(leftPane);
        //CENTER
        centerPane = new GridPane();
        centerPane.setStyle("-fx-background-color: #B5C5C6;-fx-background-radius: 10 10 10 10");
        generateWord();
        //GRIDS: Generate blank spaces
        initializeGrids();
        root.setCenter(centerPane);
        initializeKeyInputs();
    }
    /**
     * Method that creates customized button.
     * @param buttonName String representing the name of the button
     * @return StackPane representing area to be clicked for an action.
     */
    private StackPane createButton(String buttonName) {
        StackPane clickPane = new StackPane();
        DropShadow boxShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#000"), 5, 5, 5, 5);
        clickPane.setStyle("-fx-background-color: #F9BF3B; -fx-background-radius: 5 5 5 5");
        clickPane.setMaxWidth(80);
        clickPane.setMinHeight(80);
        clickPane.setEffect(boxShadow);
        Label name = new Label(buttonName);
        name.setStyle("-fx-font-family: arial; -fx-text-fill: black; -fx-font-size: 20; -fx-font-weight: bold");
        clickPane.getChildren().add(name);
        clickPane.setAlignment(Pos.CENTER);
        clickPane.setOnMouseClicked(e -> {
            startStage.setScene(mainScene);
            startStage.setTitle("Jordle");
        });
        return clickPane;
    }
    /**
     * Method that creates grids for initialization for the game.
     */
    private void initializeGrids() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                letterPane = new StackPane();
                letterPane.setMinSize(70, 70);
                letterPane.setStyle("-fx-background-color: white; -fx-background-radius: 5 5 5 5");
                centerPane.addColumn(i, letterPane);
            }
        }
        centerPane.setHgap(5);
        centerPane.setVgap(5);
        centerPane.setAlignment(Pos.CENTER);
    }
    /**
     * Method that initializes how to handle when detects specific key input from the user.
     */
    private void initializeKeyInputs() {
        mainScene.setOnKeyPressed(e -> {
            if (!consumeInput) {
                if (gridX == 0 && e.getCode() == KeyCode.BACK_SPACE) {
                    Alert boundaryAlert = new Alert(AlertType.ERROR, "Cannot backspace more!");
                    boundaryAlert.showAndWait();
                } else if (e.getCode() == KeyCode.BACK_SPACE) {
                    backspace();
                }
                if (e.getText().length() == 1 && e.getText().matches("[a-z]")) {
                    handleKeyInputs(e);
                }
                if (e.getCode() == KeyCode.ENTER && gridX != 5) {
                    Alert notEnoughLetter = new Alert(AlertType.ERROR, "Please enter 5 letters!");
                    notEnoughLetter.showAndWait();
                } else if (e.getCode() == KeyCode.ENTER) {
                    resetBooleanArray(appearedOrNot);
                    checkGuess();
                    enterHandler();
                }
            } else {
                assert true;
            }
        });
    }
    /**
     * Method that handles BACKSPACE key input.
     * @param centerPane GridPane representing the main pane that displays the game.
     */
    private void backspace() {
        gridX--;
        letterPane = new StackPane();
        letterPane.setMinSize(70, 70);
        letterPane.setStyle("-fx-background-color: white; -fx-background-radius: 5 5 5 5");
        centerPane.add(letterPane, gridX, gridY);
    }
    /**
     * Method that resets boolean array which is used whether the letter has appeared in the guess word.
     * @param arr boolean array representing that manages each letter's appearance in the guess word.
     */
    private void resetBooleanArray(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = false;
        }
    }
    /**
     * Method that changes the color of grids upon the guess entered.
     * @param centerPane GridPane representing the main pane that displays the game.
     */
    private void checkGuess() {
        Map<String, Integer> mapGreen = new HashMap<>();
        Map<String, Integer> mapYellow = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            letter = new Label();
            letterPane = new StackPane(letter);
            if (answer[i].equals(guess[i])) {
                mapGreen.put(answer[i], i);
                letter.setText(answer[i].toUpperCase());
                letter.setStyle("-fx-font-family: sans-serif; -fx-text-fill: white;"
                    + " -fx-font-size: 20; -fx-font-weight: bold");
                letterPane.setStyle("-fx-background-color: #2ECC71; -fx-background-radius: 5 5 5 5");
                System.out.println(mapGreen.size());
            } else if (!mapGreen.containsKey(guess[i]) && !mapYellow.containsKey(guess[i]) && checkIfInOtherPos(i)) {
                mapYellow.put(guess[i], i);
                letter.setText(guess[i].toUpperCase());
                letter.setStyle("-fx-font-family: sans-serif; -fx-text-fill: white; -fx-font-size: 20;"
                    + "-fx-font-weight: bold");
                letterPane.setStyle("-fx-background-color: #F9BF3B; -fx-background-radius: 5 5 5 5");
            } else {
                letter.setText(guess[i].toUpperCase());
                letter.setStyle("-fx-font-family: sans-serif; -fx-text-fill: white; -fx-font-size: 20;"
                    + "-fx-font-weight: bold");
                letterPane.setStyle("-fx-background-color: #95A5A6; -fx-background-radius: 5 5 5 5");
            }
            centerPane.add(letterPane, i, gridY);
            letter.setAlignment(Pos.CENTER);
        }
        if (mapGreen.size() == 5) {
            statusLabel.setText("Congratulation! You've guessed the word!");
            consumeInput = true;
            restartButton.requestFocus();
        }
    }
    /**
     * Method that checks if the letter is in other position of the answer word.
     * @param currentPos int representing the current position of the guess word.
     * @return boolean representing if the letter in current position exists in different position of the answer word.
     */
    private boolean checkIfInOtherPos(int currentPos) {
        for (int i = 0; i < 5; i++) {
            if (!guess[i].equals(answer[i]) && guess[currentPos].equals(answer[i])) {
                return true;
            }
        }
        return false;
    }
    /**
     * Method that handles 'ENTER' key, specifically when the user reaches 6th guess.
     */
    private void enterHandler() {
        gridY++;
        gridX = 0;
        if (!consumeInput && gridY > 5) {
            consumeInput = true;
            statusLabel.setText("Game Over! The word was \"" + randword + "\".");
            statusLabel.setStyle("-fx-font-family: arial");
            statusLabel.setWrapText(true);
            Alert restartAlert = new Alert(AlertType.ERROR, "Please Restart!");
            restartAlert.showAndWait();
            restartButton.requestFocus();
        }
    }
    /**
     * Method for restarting the game, which intializes the game again.
     */
    private void restart() {
        statusLabel.setText("Try guessing a word!");
        consumeInput = false;
        inputKey = new String();
        gridX = 0;
        gridY = 0;
        generateWord();
        centerPane.getChildren().clear();
        initializeGrids();
    }
    /**
     * Primary method for handling, displaying key inputs on the grid.
     * Prompts error message upon length limit of key input.
     * @param e KeyEvent representing the event when key is pressed.
     * @param centerPane GridPane representing the main pane that displays the game.
     */
    private void handleKeyInputs(KeyEvent e) {
        if (gridX == 5) {
            Alert exceedingError = new Alert(Alert.AlertType.ERROR, "You cannot enter longer than 5 letters!");
            exceedingError.showAndWait();
        } else if (gridX < 5 && gridY < 6) {
            setInputKey(e.getText());
            guess[gridX] = inputKey;
            letter = new Label();
            letter.setStyle("-fx-font-family: sans-serif; -fx-text-fill: #808080;"
                + " -fx-font-size: 20; -fx-font-weight: bold");
            letter.setText(inputKey.toUpperCase());
            letterPane = new StackPane(letter);
            centerPane.add(letterPane, gridX, gridY);
            letter.setAlignment(Pos.CENTER);
            gridX++;
        } else {
            assert true;
        }
    }
    /**
     * Setter method for inputKey.
     * @param inputKey String representing each input key.
     */
    private void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }
    /**
     * Method that creates the start scene of the application. Displays the 'PLAY' button.
     * @param startStage Stage representing the stage of the game.
     */
    private void createStartScene(Stage startStage) {
        this.startStage = startStage;
        if (startScene == null) {
            VBox box = new VBox();
            box.setStyle("-fx-background-color: #303030");
            //TITLE
            DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#000"), 3, 5, 7, 7);
            Label title = new Label("J O R D L E");
            title.setFont(new Font("Tahoma", 80));
            title.setTextFill(Color.web("F9BF3B"));
            title.setEffect(dropShadow);
            //Button: PLAY
            StackPane playButton = createButton("PLAY!");
            box.getChildren().addAll(title, playButton);
            playButton.requestFocus();
            box.setAlignment(Pos.CENTER);
            box.setSpacing(50);
            startScene = new Scene(box, 500, 700);
        }
    }
    /**
     * Method that generates word by randomly choosing a word from the given list, then stores each letter in an array.
     */
    private void generateWord() {
        randword = Words.list.get((int) ((Math.random() * Words.list.size())));
        for (int i = 0; i < answer.length; i++) {
            answer[i] = randword.substring(i, i + 1);
        }
    }
}