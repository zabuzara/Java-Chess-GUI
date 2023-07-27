package www.zabuzara.com.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import www.zabuzara.com.tools.Json2;
import www.zabuzara.com.model.ChessBoard;
import www.zabuzara.com.model.Operation;
import www.zabuzara.com.model.PieceType;

public class ChessAPIConnector {
    public static final String HEADER_AUTHORIZATION = "Authorization";
	private final String baseUrl = "http://localhost/Chess_API_v2";
	private final String token;
	private String gameId = null;
	private ChessBoard chessboard;
	
	public ChessAPIConnector (final String token, final ChessBoard chessboard) {
		this.token  = "Bearer " + token;
		this.chessboard = chessboard;
	}
	
	private String getContent (final String urlQuery) {
		try {
			System.out.println(urlQuery);
			URL url = new URL(urlQuery);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestProperty("Accept", "application/json");
			http.setRequestProperty("Authorization", this.token);

	        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
	        
	        if (http.getResponseCode() == 200) {
		        String output = in.readLine();
		        http.disconnect();
		        return output;
	        }
	        return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String buildURL (final String endpoint, final Map<String,String> params) {
		String urlQuery = this.baseUrl + "/chess/" + endpoint + (params.size() > 0 ? "?" : "");
		for (Entry<String,String> param : params.entrySet()) {
			urlQuery += (urlQuery.charAt(urlQuery.length()-1) != '?' ? "&" : "") + param.getKey() + "=" + param.getValue();
		}
		return urlQuery;
	}
	
	private void refresh(String jsonString) {
		Map<String, Object> jsonMap = Json2.parseMap(jsonString);
		Object[] pieceBoard = (Object[]) jsonMap.get("piece_board");
		for (int r = 0; r < pieceBoard.length ; r++) {
			Object[] boardRow = (Object[]) pieceBoard[r];
			for (int f = 0; f < boardRow.length ; f++) {
				if (Objects.toString(boardRow[f]).length() > 0) {
					this.chessboard.getPieces()[7 - r][f] = PieceType.valueOf(Objects.toString(boardRow[f]).charAt(0));
				} else {
					this.chessboard.getPieces()[7 - r][f] = null;
				}
			}
		}
	}
	
	public void setGameId (final String gameId) {
		this.gameId = gameId;
	}
	
	public String get (final Map<String,String> params, final Operation operation) {
		return this.getContent(this.buildURL(operation.name().toLowerCase(), params));
	}
	
	public String post (final Map<String,String> params, final Operation operation) {
		return this.buildURL(operation.name().toLowerCase(), params);
	}
	
	public Object move (final String piece, final String source, final String sink) {
		final Map<String,String> params = new HashMap<>();
		params.put("game_id", this.gameId);
		params.put("piece", piece);
		params.put("source", source);
		params.put("sink", sink);
		
		final String response = this.get(params, Operation.MOVE);
		if (response.equals("true"))
			return Boolean.TRUE;
		
		Map<String, Object> jsonMap = Json2.parseMap(response);
		if (jsonMap.containsKey("promotion")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> promotion = (Map<String, Object>) jsonMap.get("promotion");
			return promotion;
		}
	
		return Boolean.FALSE;
	}
	
	public boolean promote (final String piece, final String source, final String sink) {
		final Map<String,String> params = new HashMap<>();
		params.put("game_id", this.gameId);
		params.put("piece", piece);
		params.put("source", source);
		params.put("sink", sink);
		
		final String response = this.get(params, Operation.PROMOTION);
		if (response.equals("true"))
			return Boolean.TRUE;
	
		return Boolean.FALSE;
	}
	
	public boolean reset () {
		return this.getContent(this.baseUrl + "/chess/reset/" + this.gameId).equals("true");
	}
	
	public void load () {
		final String jsonString = this.getContent(this.baseUrl + "/chess/load/" + this.gameId);
		this.refresh(jsonString);
	}
}
