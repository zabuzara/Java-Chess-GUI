package www.zabuzara.com.view;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import www.zabuzara.com.controller.ChessController;
import www.zabuzara.com.model.PieceType;

/**
 * Chess board console I/O text-application facade using object-oriented programming
 * techniques and an optimal exception handling strategy.
 */
public class ChessBoardApp extends Application {
    static private final Path APP_ICON_PATH = Paths.get(ChessBoardApp.class.getPackage().getName().replace('.', '/')).resolve("chess.jpg");
//    static private final Path APP_ICON_PATH = Paths.get("/media").resolve("chess.jpg");
    static private final int WINDOW_WIDTH = 900;
    static private final int WINDOW_HEIGHT = 600;
    static private final int RANK_FILE_COUNT = 10;
    static private final int MAX_VALUE = Integer.MAX_VALUE;
    static private final int ICON_WIDTH_HEIGHT =  WINDOW_WIDTH / RANK_FILE_COUNT;
    static private final double CHESS_BOARD_PADDING = WINDOW_HEIGHT / RANK_FILE_COUNT;
    static private final String LIGHT_COLUMN = "#FFA74F";
    static private final String DARK_COLUMN = "#C46200";
    static private char alphabet = 'A';

    public ChessBoardApp (){  
//        run();
    }


//    int minmax(int depth, int index, boolean isMax, int[] scores, int h){
//
//        if (depth == h) {
//            return scores[index];
//        }
//
//        if (isMax) {
//                return Math.max(minmax(depth + 1, index * 2, false, scores, h), minmax(depth + 1, index * 2 + 1, false, scores, h));
//        } else {
//            return Math.min(minmax(depth + 1, index * 2, true, scores, h), minmax(depth + 1, index * 2 + 1, true, scores, h));
//        }
//       
//    }
//
//    int log2(int n){
//        return (n == 1) ? 0 : 1 + log2(n / 2);
//    }
//
//    void run () {
//        int[] scores = new int[] {1, 2};
//        int n = scores.length;
//        int h = log2(n);
//        int res = minmax(0, 0, true, scores, h);
//		try {
//			URL url = new URL("http://localhost/Chess_API/API/Main.php");
//			URLConnection urlConnection = url.openConnection();
//	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//	        String inputLine;
//	        while ((inputLine = bufferedReader.readLine()) != null) {    
//	        	System.out.println(inputLine);
//	        }
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start (final Stage window) throws Exception {    	
        final AnchorPane rootPane = this.newRootPane();
        final Scene sceneGraph = new Scene(rootPane);

        /**
         * Instance of ChessController
         */
        new ChessController(rootPane);

        /**
         * set window properties
         */
        window.setWidth(WINDOW_WIDTH);
        window.setHeight(WINDOW_HEIGHT);
        window.setTitle("Chess");
        window.getIcons().add(new Image(APP_ICON_PATH.toString()));
        window.setScene(sceneGraph);
        // show window
        window.show();		
    }

    private AnchorPane newRootPane () {
        final AnchorPane rootPane = new AnchorPane();
        final GridPane chessBoardPane = this.newChessBoardPane();
        rootPane.setStyle("-fx-background-color:"+DARK_COLUMN+";");
        double padding = CHESS_BOARD_PADDING;
        AnchorPane.setTopAnchor(rootPane, padding);
        AnchorPane.setLeftAnchor(rootPane, padding);
        AnchorPane.setRightAnchor(rootPane, padding);
        AnchorPane.setBottomAnchor(rootPane, padding);
        rootPane.getChildren().add(chessBoardPane);
        return rootPane;
    }

    private GridPane newChessBoardPane () {
        // create chessboard pane
        final GridPane chessBoardAnchorPane = new GridPane();
        // set chessboard pane properties
        chessBoardAnchorPane.setStyle("-fx-background-color:"+DARK_COLUMN+";");

        final GridPane chessBoardGridPane = new GridPane();

        for (int rank = RANK_FILE_COUNT-1, reverseRank = 0; rank >= 0; rank -= 1, reverseRank += 1) {
            alphabet = rank > 0 ? 'A' : alphabet;

            for (int file = 0; file < RANK_FILE_COUNT; file +=1) {
                // column count
                /**
                 * Set ChessBoard color
                 */
                final boolean isLight = (rank % 2 == 0 & file % 2 == 0) | (rank % 2 != 0 & file % 2 != 0);
                final String columnColor = isLight ? LIGHT_COLUMN : DARK_COLUMN;
                // create inner pane for toggle button
                GridPane column = new GridPane();

                // set column gridpane properties
                column.setPrefWidth(MAX_VALUE);
                column.setPrefHeight(MAX_VALUE);
                column.setAlignment(Pos.CENTER);

                if ((rank < RANK_FILE_COUNT-1 && file > 0) && (rank > 0 && file < 9)) {
                    // set chess board color
                    column.setStyle("-fx-background-color:"+columnColor+";");

                    // get piece icon path
                    PieceType pieceType = this.getPieceType(rank-1, file-1);

                    // create toggle button
                    ToggleButton iconFigure = new ToggleButton();
                    // set toggle button properties
                    iconFigure.setCursor(Cursor.OPEN_HAND);
                    iconFigure.setStyle("-fx-background-color:transparent;");

                    if (pieceType != null) {
                            // create icon
                            Image icon = new Image(pieceType.avatarPath().toString());
                            ImageView iconView = new ImageView(icon);
                            // set image view properties
                            iconView.setFitWidth(ICON_WIDTH_HEIGHT);
                            iconView.setFitHeight(ICON_WIDTH_HEIGHT);
                            // create toggle button with image
                            iconFigure = new ToggleButton("",iconView);
                            iconFigure.setId(String.valueOf(reverseRank-1)+String.valueOf(file-1)+pieceType.symbol());
                    } else {
                            Path emptyPath = Paths.get(ChessBoardApp.class.getPackage().getName().replace('.', '/')).resolve("empty.png");
//                            Path emptyPath = Paths.get("/media").resolve("empty.png");
                            // create icon for empty column
                            Image icon = new Image(emptyPath.toString());
                            ImageView iconView = new ImageView(icon);
                            // create toggle button with image
                            iconFigure = new ToggleButton("",iconView);
                            iconFigure.setId(String.valueOf(reverseRank-1)+String.valueOf(file-1));
                    }
                    iconFigure.setStyle("-fx-background-color:"+columnColor+";");

                    // add toggle button to inner pane
                    column.getChildren().add(iconFigure);
                } else {
                    // file-rank label
                    Label fileRankLabel = new Label(this.getRankFileLabel(rank, file, reverseRank));
                    fileRankLabel.setStyle("-fx-background-color:#964B00; -fx-text-fill:#D0B49F; -fx-text-weight:bold;");
                    fileRankLabel.setPrefSize(ICON_WIDTH_HEIGHT, ICON_WIDTH_HEIGHT);
                    fileRankLabel.setFont(new Font("Verdana",18));
                    fileRankLabel.setAlignment(Pos.CENTER);
                    column.setStyle("-fx-background-color:#964B00;");
                    column.getChildren().add(fileRankLabel);
                }
                // add inner pane to parent pane
                chessBoardGridPane.add(column, file, rank);
            }
        }

        GridPane panelPane = new GridPane();
        panelPane.setPrefWidth(MAX_VALUE);

        TextArea moveDisplay = new TextArea();
        moveDisplay.setPrefHeight(MAX_VALUE);
        moveDisplay.setEditable(false);
        moveDisplay.setFont(new Font("FreeMono",16));
        Button resetButton = new Button("Reset");
        resetButton.setAlignment(Pos.CENTER);
        resetButton.setPrefWidth(MAX_VALUE);
        resetButton.setPadding(new Insets(15,10,15,10));
        resetButton.setFont(new Font("FreeMono",20));

        panelPane.add(moveDisplay,0,0);
        panelPane.add(resetButton,0,1);

        chessBoardAnchorPane.add(chessBoardGridPane,0,0);
        chessBoardAnchorPane.add(panelPane,1,0);

        // return chessboard pane
        return chessBoardAnchorPane;
    }

    private String getRankFileLabel (final int rank, final int file, final int reverseRank) {
        if ((rank == 0 && file > 0 && file < 9) || (rank == 9 && file > 0 && file < 9)) {
            return Character.toString((alphabet++));
        }	
        if ((file == 0 && rank > 0 && rank < 9) || (file == 9 && rank > 0 && rank < 9)) {
            return Integer.toString(reverseRank);
        }
        return "";
    }

    /**
     * Returns PieceType with given rank and file
     * @param rank
     * @param file
     * @return PiceType
     */
    private PieceType getPieceType (final int rank, final int file) {	
        if (rank == 0 || rank == 7) {
            if (rank == 0 && (file == 0 || file == 7)) return PieceType.BLACK_ROOK;
            if (rank == 0 && (file == 1 || file == 6)) return PieceType.BLACK_KNIGHT;
            if (rank == 0 && (file == 2 || file == 5)) return PieceType.BLACK_BISHOP;
            if (rank == 0 && file == 3) return PieceType.BLACK_QUEEN;
            if (rank == 0 && file == 4) return PieceType.BLACK_KING;
            if (rank == 7 && (file == 0 || file == 7)) return PieceType.WHITE_ROOK;
            if (rank == 7 && (file == 1 || file == 6)) return PieceType.WHITE_KNIGHT;
            if (rank == 7 && (file == 2 || file == 5)) return PieceType.WHITE_BISHOP;
            if (rank == 7 && file == 3) return PieceType.WHITE_QUEEN;
            if (rank == 7 && file == 4) return PieceType.WHITE_KING;
        } else if (rank == 1 || rank == 6) {
            if (rank == 1 && file < 8) return PieceType.BLACK_PAWN;
            if (rank == 6 && file < 8) return PieceType.WHITE_PAWN;
        }
        return null;
    }

    /**
     * Application entry point.
     * @param args the runtime arguments
     * @throws IOException if there is an I/O related problem
     */
    static public void main (final String[] args) throws IOException {
            launch(args);
    }
}
