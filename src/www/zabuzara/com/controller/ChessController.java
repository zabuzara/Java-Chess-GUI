package www.zabuzara.com.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import www.zabuzara.com.model.ChessBoard;
import www.zabuzara.com.model.PieceType;
import www.zabuzara.com.tools.Controller;
import www.zabuzara.com.view.ChessBoardApp;

public class ChessController extends Controller<AnchorPane> {
	static private final int RANK_FILE_COUNT = 8;
	private final ChessBoard chessBoard;
	private final GridPane chessBoardAnchorPane;
	private final ToggleButton[][] toggleButtons;
	private final double oldChessBoardWidth;
	private final double oldChessBoardHeight;
	private Integer[][] rankFileMoves;
	private Character[] rankFileSymbol;
	private ChessAPIConnector apiConnector;
	final TextArea movesDisplay;
	
	
	public ChessController (final AnchorPane pane) {
		super(pane);
				
		this.chessBoard = new ChessBoard();
		
		
		this.toggleButtons = new ToggleButton[RANK_FILE_COUNT][RANK_FILE_COUNT];
		this.rankFileMoves = new Integer[2][2];
		this.rankFileSymbol = new Character[2];
		
		this.chessBoardAnchorPane = (GridPane) this.getNode().getChildren().get(0);

		final GridPane chessBoardGridPane = (GridPane) this.chessBoardAnchorPane.getChildren().get(0);
		final GridPane chessBoardAnchor = (GridPane) this.getNode().getChildren().get(0);
		final GridPane panelGridPane = (GridPane) chessBoardAnchorPane.getChildren().get(1);
		this.movesDisplay = (TextArea) panelGridPane.getChildren().get(0);
		final Button resetButton = (Button) panelGridPane.getChildren().get(1);
		
		this.oldChessBoardWidth = chessBoardAnchor.getPrefWidth();
		this.oldChessBoardHeight = chessBoardAnchor.getPrefHeight();

		this.getNode().widthProperty().addListener(event -> resizeHandler());
		this.getNode().heightProperty().addListener(event -> resizeHandler());
		
		for (int index = 0; index < chessBoardGridPane.getChildren().size(); index += 1) {
			final GridPane iconPane = (GridPane) chessBoardGridPane.getChildren().get(index);
			if (iconPane.getChildren().get(0) instanceof ToggleButton) {
				final ToggleButton iconToggleButton = (ToggleButton) iconPane.getChildren().get(0);
				
				final String[] idParts = iconToggleButton.getId().split("");
				final int rank = Integer.valueOf(idParts[0]);
				final int file = Integer.valueOf(idParts[1]);
				this.toggleButtons[rank][file] = iconToggleButton;
				
				// Register Button Events
				iconToggleButton.setOnAction(event -> toggleSelect(rank, file));
			}
		}
		
		resetButton.setOnAction(event -> reset());
		
	
		/**
		 * Connect to API
		 */
		final String token = "5e0e56d84b707694e4b407e05a2817c60dfad6fbee741ac027e247860f58e51bc41ba56500ce19e5ebae80177cb9af1615583919c5573a0a945dac17d6c83c6bc7e4525653b6e59bf2880fc0014524983fd6c7e92b0875292fa8e2d57e0417d6";
		this.apiConnector = new ChessAPIConnector(token, this.chessBoard);
		this.apiConnector.setGameId("6e13fd891968dbd5");
		this.refresh();

	}
	
	private void refresh () {
		this.apiConnector.load();
		this.rankFileMoves = new Integer[2][2];
		this.rankFileSymbol = new Character[2];
	
		for (int rowRank = this.chessBoard.getPieces().length-1; rowRank >= 0; rowRank -= 1) {
			for (int columnFile = 0; columnFile < this.chessBoard.getPieces()[rowRank].length; columnFile += 1) {

				if (this.chessBoard.getPieces()[rowRank][columnFile] != null) {
					Image icon = new Image(this.chessBoard.getPieces()[rowRank][columnFile].avatarPath().toString());
					ImageView iconView = new ImageView(icon);
					this.toggleButtons[rowRank][columnFile].setGraphic(iconView);
				} else {
					Path emptyPath = Paths.get(ChessBoardApp.class.getPackage().getName().replace('.', '/')).resolve("empty.png");
					Image icon = new Image(emptyPath.toString());
					ImageView iconView = new ImageView(icon);
					this.toggleButtons[rowRank][columnFile].setGraphic(iconView);
				}
				this.toggleButtons[rowRank][columnFile].setSelected(false);
				this.toggleButtons[rowRank][columnFile].setRotate(0);		
			}
		}
		this.resizeHandler();
	}
	
	private void reset () {
		this.apiConnector.reset();
		this.chessBoard.reset();
		
//		Map<String, String> params = new HashMap<>();
//		params.put("apiOperation",  Operation.RESET.toString());
//		System.out.println((this.apiConnector.get(params, Operation.RESET).equals("true")) ? "game reset" : "game not reset");
		
		this.movesDisplay.setText("");
		this.rankFileMoves = new Integer[2][2];
		this.rankFileSymbol = new Character[2];
	
		for (int rowRank = this.chessBoard.getPieces().length-1; rowRank >= 0; rowRank -= 1) {
			for (int columnFile = 0; columnFile < this.chessBoard.getPieces()[rowRank].length; columnFile += 1) {

				if (this.chessBoard.getPieces()[rowRank][columnFile] != null) {
					Image icon = new Image(this.chessBoard.getPieces()[rowRank][columnFile].avatarPath().toString());
					ImageView iconView = new ImageView(icon);
					this.toggleButtons[rowRank][columnFile].setGraphic(iconView);
				} else {
					Path emptyPath = Paths.get(ChessBoardApp.class.getPackage().getName().replace('.', '/')).resolve("empty.png");
					Image icon = new Image(emptyPath.toString());
					ImageView iconView = new ImageView(icon);
					this.toggleButtons[rowRank][columnFile].setGraphic(iconView);
				}
				this.toggleButtons[rowRank][columnFile].setSelected(false);
				this.toggleButtons[rowRank][columnFile].setRotate(0);		
			}
		}
		this.resizeHandler();
	}
	
	private void toggleSelect (final int rank, final int file) {	
		for (int rowRank = this.chessBoard.getPieces()[rank].length-1; rowRank > 0; rowRank -= 1) {
			for (int columnFile = 0; columnFile < this.chessBoard.getPieces()[rank].length; columnFile += 1) {
				this.toggleButtons[rowRank][columnFile].setSelected(false);
				this.toggleButtons[rowRank][columnFile].setRotate(0);
			}
		}

		if (Arrays.asList(rankFileMoves[0][0]).contains(null) && this.rankFileSymbol[0] == null) {
			if (this.chessBoard.getPieces()[rank][file] != null) {
				this.rankFileSymbol[0] = this.chessBoard.getPieces()[rank][file].symbol();
				this.rankFileMoves[0] = new Integer[]{rank, file};
				this.toggleButtons[rank][file].setRotate(15);
			}
		} else {
			this.rankFileMoves[1] = new Integer[]{rank, file};

			if (this.isSelfPiece(this.rankFileMoves[0], this.rankFileMoves[1])) {
				this.toggleButtons[this.rankFileMoves[0][0]][this.rankFileMoves[0][1]].setRotate(0);
				this.rankFileMoves = new Integer[2][2];
				this.rankFileSymbol = new Character[2];
			} else {
				if (this.chessBoard.getPieces()[rank][file] != null) {
					this.rankFileSymbol[1] = this.chessBoard.getPieces()[rank][file].symbol() ;
				}

				if (this.rankFileSymbol[1] != null && isSelfColor(this.rankFileSymbol[0], this.rankFileSymbol[1])){
					this.rankFileSymbol[0] = this.chessBoard.getPieces()[rank][file].symbol();
					this.rankFileSymbol[1] = null;
					this.toggleButtons[this.rankFileMoves[0][0]][this.rankFileMoves[0][1]].setRotate(0);

					this.rankFileMoves[0] = new Integer[]{rank, file};
					this.rankFileMoves[1] = new Integer[2];
					this.toggleButtons[rank][file].setRotate(15);
				} else {		
					try {
						final String rawMove = ChessBoard.convertMove(this.rankFileMoves[0][0],this.rankFileMoves[0][1],this.rankFileMoves[1][0],this.rankFileMoves[1][1]);
						final String source = rawMove.substring(0,2).toUpperCase();
						final String sink = rawMove.substring(rawMove.length()-2).toUpperCase();
						
						final Object result = this.apiConnector.move(this.rankFileSymbol[0].toString(), source, sink);
						
						
						if (result instanceof Map) {
							@SuppressWarnings("unchecked")
							final Map<String, Object> promotion = (Map<String, Object>) result;
							final String promoteSource = Objects.toString(promotion.get("source"));
							final String promoteSink = Objects.toString(promotion.get("sink"));
							final boolean promoteSide = (boolean) promotion.get("side");
							
							Stage popupwindow=new Stage();
							popupwindow.initModality(Modality.APPLICATION_MODAL);
							popupwindow.setTitle("Promotion");
							Label label1= new Label("Choose piece to promote");
							Button button1= new Button("Close this pop up window");
							button1.setOnAction(e -> popupwindow.close());
							VBox layout= new VBox(10);
							HBox toPromotePiecesBox = new HBox();
							toPromotePiecesBox.setAlignment(Pos.CENTER);
							toPromotePiecesBox.setSpacing(5);
							final char[] symbols = promoteSide ? new char[]{'Q', 'R', 'B', 'N'} : new char[]{'q', 'r', 'b', 'n'};
							for(char symbol: symbols) {
	                            Image icon = new Image(PieceType.valueOf(symbol).avatarPath().toString());
	                            ImageView iconView = new ImageView(icon);
	                            iconView.setFitWidth(30);
	                            iconView.setFitHeight(30);
	                            Button iconFigure = new Button("",iconView);
	                            iconFigure.setId(String.valueOf(symbol));
	                            iconFigure.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
	                            	Button target = (Button) e.getTarget();
	                            	
	                            	this.apiConnector.promote(target.getId(), promoteSource, promoteSink);
	    							
	                            	this.chessBoard.move(this.rankFileMoves[0][0], this.rankFileMoves[0][1], this.rankFileMoves[1][0], this.rankFileMoves[1][1]);
	    							ImageView oldImageView = (ImageView) this.toggleButtons[this.rankFileMoves[0][0]][this.rankFileMoves[0][1]].getGraphic();										
	    							this.toggleButtons[rank][file].setGraphic(oldImageView);
	    							int moveIndex = this.chessBoard.moves().size()/2 + 1;
	    							String output = this.movesDisplay.getText() + (this.chessBoard.moves().size() == 1 ? (moveIndex+".\t ") : "");
	    							String delimiter = (this.chessBoard.moves().size() + 1) % 2 == 1 ? "\n"+moveIndex+".\t " : ", ";
	    							String move = this.chessBoard.moves().get(this.chessBoard.moves().size() - 1);
	    							this.movesDisplay.setText(output + move + delimiter);
	    							
	                            	popupwindow.close();
	                            	this.refresh();
	                            });
	                           
	                            toPromotePiecesBox.getChildren().add(iconFigure);
							}
						
			
							layout.getChildren().addAll(label1, toPromotePiecesBox);
							layout.setAlignment(Pos.CENTER);
							layout.setPadding(new Insets(10));
							Scene scene1= new Scene(layout);
							popupwindow.setScene(scene1);
							popupwindow.showAndWait();

						}
						
						Boolean isValidMove = (Boolean) result;
						if (isValidMove) {
//							
							this.chessBoard.move(this.rankFileMoves[0][0], this.rankFileMoves[0][1], this.rankFileMoves[1][0], this.rankFileMoves[1][1]);
							ImageView oldImageView = (ImageView) this.toggleButtons[this.rankFileMoves[0][0]][this.rankFileMoves[0][1]].getGraphic();										
							this.toggleButtons[rank][file].setGraphic(oldImageView);
							int moveIndex = this.chessBoard.moves().size()/2 + 1;
							String output = this.movesDisplay.getText() + (this.chessBoard.moves().size() == 1 ? (moveIndex+".\t ") : "");
							String delimiter = (this.chessBoard.moves().size() + 1) % 2 == 1 ? "\n"+moveIndex+".\t " : ", ";
							String move = this.chessBoard.moves().get(this.chessBoard.moves().size() - 1);
							this.movesDisplay.setText(output + move + delimiter);
							this.refresh();
						} else {
							System.out.println("invalid move");
							this.toggleButtons[rank][file].setRotate(0);
						}
					} catch (Exception e) {
						
					}
					
					this.rankFileMoves = new Integer[2][2];
					this.rankFileSymbol = new Character[2];
				}
			}
		}		
	}
	
	private boolean isSelfPiece (final Integer[] sourceRankFile, final Integer[] sinkRankFile) {
		return sourceRankFile[0] == sinkRankFile[0] && sourceRankFile[1] == sinkRankFile[1];
	}
	
	private boolean isSelfColor (final char sourcePiece, final char sinkPiece) {
		return  (Character.isUpperCase(sourcePiece) && Character.isUpperCase(sinkPiece)) ||
				(Character.isLowerCase(sourcePiece) && Character.isLowerCase(sinkPiece));
	}
	
	/**
	 * Resizing Event for responsive ChessBoard
	 */
	private void resizeHandler () {
		final GridPane chessBoardAnchorPane = (GridPane) this.getNode().getChildren().get(0);
		final GridPane chessBoardGridPane = (GridPane) chessBoardAnchorPane.getChildren().get(0);
		final GridPane panelGridPane = (GridPane) chessBoardAnchorPane.getChildren().get(1);

		final double newChessBoardAnchorWidth = this.getNode().widthProperty().get();
		final double newChessBoardAnchorHeight = this.getNode().heightProperty().get();
		
		double chessBoardSquareSize = newChessBoardAnchorWidth > newChessBoardAnchorHeight ? newChessBoardAnchorHeight : newChessBoardAnchorWidth;
		double widthDiff = (newChessBoardAnchorWidth - oldChessBoardWidth);
		double heightDiff = (newChessBoardAnchorHeight - oldChessBoardHeight);
		
		double oldLeftRightAnchor = AnchorPane.getLeftAnchor(this.getNode());
		double oldTopBottomAnchor = AnchorPane.getTopAnchor(this.getNode());
		double newLeftRightAnchor = oldLeftRightAnchor + (widthDiff / 2.0);
		double newTopBottomAnchor = oldTopBottomAnchor + (heightDiff / 2.0);
	
		AnchorPane.setLeftAnchor(this.getNode(), newLeftRightAnchor);
		AnchorPane.setRightAnchor(this.getNode(), newLeftRightAnchor);
		AnchorPane.setTopAnchor(this.getNode(), newTopBottomAnchor);
		AnchorPane.setBottomAnchor(this.getNode(), newTopBottomAnchor);

		chessBoardAnchorPane.setPrefSize(newChessBoardAnchorWidth, chessBoardSquareSize);
		chessBoardGridPane.setPrefSize(chessBoardSquareSize, chessBoardSquareSize);
		panelGridPane.setPrefSize(newChessBoardAnchorWidth-chessBoardSquareSize, chessBoardSquareSize);
//		
//		final GridPane chessBoardGridPaneNew = chessBoardGridPane;
//		final GridPane panelGridPaneNew = panelGridPane;
		
//		System.out.println(chessBoardAnchorPane.getWidth());
		
		if (chessBoardAnchorPane.getChildren().size() > 0 && panelGridPane.getWidth() < 60) {
//			chessBoardAnchorPane.getChildren().remove(1);
//			chessBoardAnchorPane.add(panelGridPaneNew, 0, 1);
//			System.out.println("change element position from right to bottom");
		} else if (chessBoardAnchorPane.getChildren().size() > 0 && panelGridPane.getWidth() > 60) {
//			chessBoardAnchorPane.getChildren().remove(1);
//			chessBoardAnchorPane.add(panelGridPaneNew, 0, 1);
//			System.out.println("change element position from bottom to right");
			
		}
	
		final double iconViewSize = chessBoardSquareSize / (RANK_FILE_COUNT+2) * 0.85;
		
		for (int index = 0; index < chessBoardGridPane.getChildren().size(); index += 1) {
			final GridPane iconPane = (GridPane) chessBoardGridPane.getChildren().get(index);
			if (iconPane.getChildren().get(0) instanceof ToggleButton) {
				final ToggleButton iconToggleButton = (ToggleButton) iconPane.getChildren().get(0);
				iconToggleButton.setPrefSize(iconViewSize, iconViewSize);
				iconToggleButton.setPadding(new Insets(0));
				
				final ImageView iconView = (ImageView) iconToggleButton.getGraphic();
				iconView.setFitWidth(iconViewSize);
				iconView.setFitHeight(iconViewSize);
			}
			if (iconPane.getChildren().get(0) instanceof Label) {
				final Label labelView = (Label) iconPane.getChildren().get(0);
				labelView.setPrefSize(iconViewSize, iconViewSize);
				labelView.setTextAlignment(TextAlignment.CENTER);
				labelView.setFont(Font.font(iconViewSize/3));
			}
		}
	}
}
