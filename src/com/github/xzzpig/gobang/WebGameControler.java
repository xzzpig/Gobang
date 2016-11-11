package com.github.xzzpig.gobang;

import javax.swing.JOptionPane;

import com.github.xzzpig.gobang.Chest.Piece;
import com.github.xzzpig.pigapi.TUrl;
import com.github.xzzpig.pigapi.json.JSONObject;

public class WebGameControler extends GameControler {
	private String room;
	private Side side;
	private boolean inroom, myturn;

	public WebGameControler(String roomname) throws Exception {
		room = roomname;
		String html = TUrl.getHtml("http://www.xzzpig.com/gobang/index.php?command=inRoom&room=" + roomname);
		JSONObject jsonObject = new JSONObject(html);
		if (jsonObject.optBoolean("result")) {
			inroom = true;
		} else {
			throw new Exception(jsonObject.optString("reason"));
		}
		if (jsonObject.getInt("side") == 1) {
			side = Side.BLACK;
			JOptionPane.showMessageDialog(null, "加入成功\n等待白方加入");
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							if (TUrl.getHtml("http://www.xzzpig.com/gobang/index.php?command=isStart").contains("1")) {
								JOptionPane.showMessageDialog(null, "白方加入\n游戏开始");
								return;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		} else {
			side = Side.WHITE;
			JOptionPane.showMessageDialog(null, "加入成功\n游戏开始");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (inroom) {
					try {
						String turn = TUrl.getHtml("http://www.xzzpig.com/gobang/index.php?command=getturn");
						myturn = turn.contains(side.ordinal() + "");
						if (myturn) {
							JSONObject json;
							try {
								json = new JSONObject(
										TUrl.getHtml("http://www.xzzpig.com/gobang/index.php?command=getLastPiece"));
								int x = json.optInt("x"), y = json.optInt("y"), p = json.optInt("piece");
								Piece piece = Piece.WHITE;
								if (p == 0)
									piece = Piece.BLACK;
								Chest.getInstance().putPiece(x, y, piece);
								printIfWin(x, y);
							} catch (Exception e) {
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public boolean canPut(int x, int y) {
		if (!inroom)
			return false;
		if (!Chest.getInstance().canPutPiece(x, y))
			return false;
		if (!myturn)
			return false;
		return true;
	}

	@Override
	public Side getSide() {
		return side;
	}

	@Override
	public void printIfWin(int x, int y) {
		if (isWin(x, y)) {
			String side;
			if (Chest.getInstance().getPiece(x, y) == Piece.BLACK) {
				side = "黑方";
			} else
				side = "白方";
			JOptionPane.showMessageDialog(null, "游戏结束\n" + side + "胜");
			new Chest();
			new SingleGameControler();
			reset();
		}
	}

	@Override
	public void put(int x, int y) {
		if (!canPut(x, y))
			return;
		if (side == Side.BLACK) {
			Chest.getInstance().putPiece(x, y, Piece.BLACK);
		} else
			Chest.getInstance().putPiece(x, y, Piece.WHITE);
		myturn = false;
		TUrl.getHtml("http://www.xzzpig.com/gobang/index.php?command=putPiece&x=" + x + "&y=" + y);
	}

	@Override
	public void reset() {
		TUrl.getHtml("http://www.xzzpig.com/gobang/index.php?command=delRoom&room=" + room);
		side = null;
		inroom = false;
		myturn = false;
	}

}
