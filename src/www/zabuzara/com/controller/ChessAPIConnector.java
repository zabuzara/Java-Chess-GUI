package www.zabuzara.com.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import www.zabuzara.com.tools.Json2;
import www.zabuzara.com.model.Operation;
//import www.zabuzara.com.model.PieceType;

public class ChessAPIConnector {
	private final String apiURL = "https://chess.toolchain.tech/api/";
	private final String apiKey;
	private final String apiPassword;
	private String gameId = null;
//	private PieceType[][] pieces = new PieceType[8][8];
	
	public ChessAPIConnector (final String apiKey, final String apiPass) {
		this.apiKey  = "apiKey=" + apiKey  + "&";
		this.apiPassword = "apiPassword=" + apiPass + "&";
	}
	
	private String getContent (final String urlQuery, final Operation operation) {
		try {
			URL url = new URL(urlQuery);
			URLConnection urlConnection = url.openConnection();
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	        String inputLine = bufferedReader.readLine();
//	        while ((inputLine = bufferedReader.readLine()) != null) {  
	        
	       
	        if (operation == Operation.LOAD) {
		        this.refresh(inputLine);
	        }else {
	        	return (inputLine);
	        }
	        bufferedReader.close();
//	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String buildURL (final Map<String,String> params) {
		String urlQuery = this.apiURL + this.apiKey + this.apiPassword + "gameId=" + this.gameId;
		for (Entry<String,String> param : params.entrySet()) {
			urlQuery += "&" + param.getKey() + "=" + param.getValue();
		}
//		System.out.println(urlQuery);
		return urlQuery;
	}
	
	private void refresh(String jsonString) {
		Map<String, Object> jsonMap = Json2.parseMap(jsonString.substring(1, jsonString.length()-1));
		System.out.println(jsonMap);
		Object[] pieceBoard = (Object[]) jsonMap.get("piece-board");
		for (int i = 0; i < pieceBoard.length ; i++) {
			Object[] boardRow = (Object[]) pieceBoard[i];
			for (int j = 0; j < boardRow.length ; j++) {
				System.out.print(Objects.toString(boardRow[j], " "));
			}
			System.out.println();
		}
	}
	
	public void setGameId (final String gameId) {
		this.gameId = gameId;
	}
	
	public String get (final Map<String,String> params, final Operation operation) {
		return this.getContent(this.buildURL(params), operation);
	}
	
	public String post (final Map<String,String> params) {
		return this.buildURL(params);
	}
	
	public boolean move (final String piece, final String source, final String sink) {
		final Map<String,String> params = new HashMap<>();
		params.put("apiOperation", Operation.MOVE.toString());
		params.put("piece", piece);
		params.put("source", source);
		params.put("sink", sink);
		params.put("gameId", this.gameId);
		return this.get(params, Operation.MOVE).equals("true");
	}
	
	public boolean load () {
//		System.out.println(this.getContent("https://www.zabuzara.com", Operation.LOAD));
		final Map<String,String> params = new HashMap<>();
		params.put("apiOperation", Operation.LOAD.toString());
//		System.out.println(this.get(params, Operation.LOAD));
		return false;
	}
}
