package www.zabuzara.com.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Instances of this class model chess boards.
 */
public class ChessBoard {
	static public final int RANK_FILE_COUNT = 8;

	private final PieceType[][] pieces;
	private final List<String> moves;


	/**
	 * Initializes a new instance.
	 */
	public ChessBoard () {
		this.pieces = new PieceType[RANK_FILE_COUNT][RANK_FILE_COUNT];
		this.moves = new ArrayList<>();
		this.reset();
	}
	
	/**
	 * Returns pieces
	 * @return pieces
	 */
	public PieceType[][] getPieces () {
		return this.pieces;
	}

	/**
	 * Resets this board instance.
	 */
	public void reset () {
		this.pieces[0][0] = this.pieces[0][7] = PieceType.WHITE_ROOK;
		this.pieces[0][1] = this.pieces[0][6] = PieceType.WHITE_KNIGHT;
		this.pieces[0][2] = this.pieces[0][5] = PieceType.WHITE_BISHOP;
		this.pieces[0][3] = PieceType.WHITE_QUEEN;
		this.pieces[0][4] = PieceType.WHITE_KING;

		Arrays.fill(this.pieces[1], PieceType.WHITE_PAWN);
		for (int rank = 2; rank < 6; ++rank) Arrays.fill(this.pieces[rank], null);
		Arrays.fill(this.pieces[6], PieceType.BLACK_PAWN);

		this.pieces[7][0] = this.pieces[7][7] = PieceType.BLACK_ROOK;
		this.pieces[7][1] = this.pieces[7][6] = PieceType.BLACK_KNIGHT;
		this.pieces[7][2] = this.pieces[7][5] = PieceType.BLACK_BISHOP;
		this.pieces[7][3] = PieceType.BLACK_QUEEN;
		this.pieces[7][4] = PieceType.BLACK_KING;

		this.moves.clear();
	}


	/**
	 * Returns the moves.
	 * @return the move history
	 */
	public List<String> moves () {
		return this.moves;
	}


	/**
	 * Returns a display text for this board instance.
	 * @return the display text
	 */
	public String toDisplayString () {
		final StringBuilder factory = new StringBuilder();

		for (int rank = RANK_FILE_COUNT - 1; rank >= 0; --rank) {
			factory.append((char) ('1' + rank)).append("  ");
			for (final PieceType piece : this.pieces[rank])
				factory.append(' ').append(piece == null ? '-' : piece.symbol());
			factory.append('\n');
		}

		factory.append("\n    a b c d e f g h");
		return factory.toString();
	}


	/**
	 * Moves a piece.
	 * @param sourceRank the source rank
	 * @param sourceFile the source file
	 * @param sinkRank the sink rank
	 * @param sinkFile he sink file
	 */
	public void move (final int sourceRank, final int sourceFile, final int sinkRank, final int sinkFile) {
		if (sourceRank < 0 | sourceRank >= RANK_FILE_COUNT | sourceFile < 0 | sourceFile >= RANK_FILE_COUNT | sinkRank < 0 | sinkRank >= RANK_FILE_COUNT | sinkFile < 0 | sinkFile >= RANK_FILE_COUNT) throw new IllegalArgumentException();

		final boolean whiteActive = this.moves.size() % 2 == 0;
		final PieceType sourcePiece = this.pieces[sourceRank][sourceFile];
		if (sourcePiece == null || whiteActive == sourcePiece.isBlack()) throw new IllegalArgumentException();
		final PieceType sinkPiece = this.pieces[sinkRank][sinkFile];
		if (sinkPiece != null && whiteActive == sinkPiece.isWhite()) throw new IllegalArgumentException();

		this.pieces[sourceRank][sourceFile] = null;
		this.pieces[sinkRank][sinkFile] = sourcePiece;
		this.moves.add(convertMove(sourceRank, sourceFile, sinkRank, sinkFile));
	}


	/**
	 * Converts the given move from it's text representation into it's index
	 * representation: "e2-e4" -> [1, 4, 3, 4]
	 * @param move the move in text representation
	 * @return the move in index representation
	 */
	static public int[] convertMove (final String move) {
		if (move.length() != 5 || move.charAt(2) != '-') throw new IllegalArgumentException();

		final int[] result = new int[4];
		result[0] = move.charAt(1) - '1';
		result[1] = Character.toLowerCase(move.charAt(0)) - 'a';
		result[2] = move.charAt(4) - '1';
		result[3] = Character.toLowerCase(move.charAt(3)) - 'a';
		for (int position : result)
			if (position < 0 | position >= RANK_FILE_COUNT) throw new IllegalArgumentException();

		return result;
	}


	/**
	 * Converts the given move from it's index representation into it's text
	 * representation: [1, 4, 3, 4] -> "e2-e4"  
	 * @param move the move in index representation
	 * @return the move in text representation
	 */
	static public String convertMove (final int... move) {
		if (move.length != 4) throw new IllegalArgumentException();
		for (int position : move)
			if (position < 0 | position >= RANK_FILE_COUNT) throw new IllegalArgumentException();

		return String.format("%c%c-%c%c", move[1] + 'a', move[0] + '1', move[3] + 'a', move[2] + '1');
	}
}
