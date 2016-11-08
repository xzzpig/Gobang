package com.github.xzzpig.gobang;

import javax.swing.JOptionPane;

import com.github.xzzpig.gobang.Chest.Piece;

public class SingleGameControler extends GameControler {

	private Side side;

	public SingleGameControler() {
		super();
	}

	@Override
	public Side getSide() {
		if (side == null)
			side = Side.BLACK;
		return side;
	}

	@Override
	public boolean canPut(int x, int y) {
		return Chest.getInstance().canPutPiece(x, y);
	}

	@Override
	public void put(int x, int y) {
		if (!canPut(x, y))
			return;
		getSide();
		if (side == Side.BLACK) {
			Chest.getInstance().putPiece(x, y, Piece.BLACK);
			side = Side.WHITE;
		} else {
			Chest.getInstance().putPiece(x, y, Piece.WHITE);
			side = Side.BLACK;
		}
	}

	@Override
	public void reset() {
		side = Side.BLACK;
	}

	@Override
	public void printIfWin(int x, int y) {
		if (isWin(x, y)) {
			String side;
			if (GameControler.getInstance().getSide() == Side.BLACK)
				side = "白方";
			else
				side = "黑方";
			JOptionPane.showMessageDialog(null, "游戏结束\n" + side + "胜");
			new Chest();
			reset();
		}
	}

}
