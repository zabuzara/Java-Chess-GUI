package www.zabuzara.com.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import www.zabuzara.com.view.ChessBoardApp;

/**
 * Instances of this enum model chess pieces.
 */
public enum PieceType {
	BLACK_KING('k', "black-king.png"),
	BLACK_QUEEN('q', "black-queen.png"),
	BLACK_ROOK('r', "black-rook.png"),
	BLACK_KNIGHT('n', "black-knight.png"),
	BLACK_BISHOP('b', "black-bishop.png"),
	BLACK_PAWN('p', "black-pawn.png"),
	WHITE_KING('K', "white-king.png"),
	WHITE_QUEEN('Q', "white-queen.png"),
	WHITE_ROOK('R', "white-rook.png"),
	WHITE_KNIGHT('N', "white-knight.png"),
	WHITE_BISHOP('B', "white-bishop.png"),
	WHITE_PAWN('P', "white-pawn.png");

	private final char symbol;
	private final Path avatarPath;	


	/**
	 * Initializes a new instance.
	 * @param symbol the symbol
	 * @param avatarFileName the avatar's file name
	 */
	private PieceType (final char symbol, final String avatarFileName) {
		this.symbol = symbol;
		this.avatarPath = Paths.get(ChessBoardApp.class.getPackage().getName().replace('.', '/')).resolve(avatarFileName);
//		this.avatarPath = Paths.get("/media").resolve(avatarFileName);
	}


	/**
	 * Returns the symbol.
	 * @return the symbol
	 */
	public char symbol () {
		return this.symbol;
	}


	/**
	 * Returns the avatar path.
	 * @return the relative avatar path
	 */
	public Path avatarPath () {
		return this.avatarPath;
	}


	/**
	 * Returns true if this piece is black, false otherwise.
	 * @return whether or not this piece is black
	 */
	public boolean isBlack () {
		return Character.isLowerCase(this.symbol);
	}


	/**
	 * Returns true if this piece is white, false otherwise.
	 * @return whether or not this piece is white
	 */
	public boolean isWhite () {
		return Character.isUpperCase(this.symbol);
	}


	/**
	 * Returns the chess piece associated with the given symbol.
	 * @param symbol the symbol
	 * @return the associated chess piece
	 */
	static public PieceType valueOf (final char symbol) {
		switch (symbol) {
			case 'k': return BLACK_KING;
			case 'q': return BLACK_QUEEN;
			case 'r': return BLACK_ROOK;
			case 'n': return BLACK_KNIGHT;
			case 'b': return BLACK_BISHOP;
			case 'p': return BLACK_PAWN;
			case 'K': return WHITE_KING;
			case 'Q': return WHITE_QUEEN;
			case 'R': return WHITE_ROOK;
			case 'N': return WHITE_KNIGHT;
			case 'B': return WHITE_BISHOP;
			case 'P': return WHITE_PAWN;
			default:  throw new IllegalArgumentException();
		}
	}
}
