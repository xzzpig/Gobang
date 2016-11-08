package com.github.xzzpig.gobang;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.xzzpig.gobang.Chest.Piece;
import com.github.xzzpig.pigapi.json.JSONObject;

public class MultiGameControler extends GameControler {

	private WebSocketServer server;
	private WebSocketClient client;
	private WebSocket webSocket;

	private Side side;

	private boolean connected, myturn;

	public MultiGameControler() throws UnknownHostException {
		server = new WebSocketServer(new InetSocketAddress(1597)) {
			@Override
			public void onOpen(WebSocket conn, ClientHandshake handshake) {
				if (webSocket != null)
					return;
				connected = true;
				webSocket = conn;
				myturn = true;
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("command", "resize");
				jsonObject.accumulate("size", Chest.getInstance().getSize());
				sendMessage(jsonObject);
				JOptionPane.showMessageDialog(null, "白方连接成功\n开始游戏");
			}

			@Override
			public void onMessage(WebSocket conn, String message) {
				JSONObject jsonObject = new JSONObject(message);
				String command = jsonObject.optString("command");
				switch (command) {
				case "put":
					int x = jsonObject.optInt("x"), y = jsonObject.optInt("y");
					Chest.getInstance().putPiece(x, y, Piece.WHITE);
					myturn = true;
					printIfWin(x, y);
					break;
				case "win":
					String side = jsonObject.optString("side");
					JOptionPane.showMessageDialog(null, "游戏结束\n" + side + "胜");
					break;
				}
			}

			@Override
			public void onError(WebSocket conn, Exception ex) {
			}

			@Override
			public void onClose(WebSocket conn, int code, String reason, boolean remote) {
				connected = false;
				JOptionPane.showMessageDialog(null, "白方断开连接");
				new Chest(Chest.getInstance().getSize());
				new SingleGameControler();
			}
		};
		server.start();
		side = Side.BLACK;
		myturn = true;
	}

	public MultiGameControler(String ip, int port) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(ip);
		ip = address.getHostAddress();
		client = new WebSocketClient(URI.create("ws://" + ip + ":" + port)) {

			@Override
			public void onOpen(ServerHandshake handshakedata) {
				connected = true;
				JOptionPane.showMessageDialog(null, "服务器连接成功\n开始游戏");
			}

			@Override
			public void onMessage(String message) {
				JSONObject jsonObject = new JSONObject(message);
				String command = jsonObject.optString("command");
				switch (command) {
				case "put":
					int x = jsonObject.optInt("x"), y = jsonObject.optInt("y");
					Chest.getInstance().putPiece(x, y, Piece.BLACK);
					myturn = true;
					break;
				case "resize":
					int size = jsonObject.optInt("size");
					new Chest(size);
					break;
				case "win":
					String side = jsonObject.optString("side");
					JOptionPane.showMessageDialog(null, "游戏结束\n" + side + "胜");
					break;
				}
			}

			@Override
			public void onError(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				connected = false;
				JOptionPane.showMessageDialog(null, "断开连接");
				new Chest(Chest.getInstance().getSize());
				new SingleGameControler();
			}
		};
		client.connect();
		side = Side.WHITE;
		webSocket = client;
	}

	@Override
	public Side getSide() {
		return side;
	}

	@Override
	public boolean canPut(int x, int y) {
		if (!connected)
			return false;
		if (!Chest.getInstance().canPutPiece(x, y))
			return false;
		if (!myturn)
			return false;
		return true;
	}

	@Override
	public void put(int x, int y) {
		if (!canPut(x, y))
			return;
		if (side == Side.BLACK) {
			Chest.getInstance().putPiece(x, y, Piece.BLACK);
		} else {
			Chest.getInstance().putPiece(x, y, Piece.WHITE);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate("command", "put");
		jsonObject.accumulate("x", x);
		jsonObject.accumulate("y", y);
		sendMessage(jsonObject);
		myturn = false;
	}

	@Override
	public void reset() {
		webSocket.close();
		webSocket = null;
		try {
			server.stop(1);
			server = null;
		} catch (Exception e) {
		}
		try {
			client.close();
			client = null;
		} catch (Exception e) {
		}
		side = null;
		connected = false;
		myturn = false;
	}

	private void sendMessage(JSONObject message) {
		webSocket.send(message.toString());
	}

	@Override
	public void printIfWin(int x, int y) {
		if (isWin(x, y) && side == Side.BLACK) {
			String side;
			if (!myturn)
				side = "黑方";
			else
				side = "白方";
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("command", "win");
			jsonObject.accumulate("side", side);
			sendMessage(jsonObject);
			JOptionPane.showMessageDialog(null, "游戏结束\n" + side + "胜");
			new Chest();
			reset();
		}
	}
}
