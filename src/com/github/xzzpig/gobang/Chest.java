package com.github.xzzpig.gobang;

public class Chest {

	public enum Piece {
		BLACK, WHITE;
	}

	private static Chest instance;

	public static Chest getInstance() {
		if (instance == null)
			new Chest();
		return instance;
	}

	private int size;

	private int[] lastpiece;

	private Piece[][] data;

	public Chest() {
		this(15);
	}

	public Chest(int size) {
		data = new Piece[size][size];
		this.size = size;
		instance = this;
	}

	public boolean canPutPiece(int x, int y) {
		if (x >= size || y >= getSize() || x < 0 || y < 0)
			return false;
		return data[x][y] == null;
	}

	public int[] getLastPieceLoc() {
		return lastpiece;
	}

	public Piece getPiece(int x, int y) {
		return data[x][y];
	}

	public int getSize() {
		return size;
	}

	public boolean isWin(int x, int y) {
		Piece piece = getPiece(x, y);

		int d = 0;
		for (int i = x - 1; i >= 0; i--) {
			if (getPiece(i, y) == piece)
				d++;
			else
				break;
		}
		for (int i = x + 1; i < getSize(); i++) {
			if (getPiece(i, y) == piece)
				d++;
			else
				break;
		}
		if (d == 4)
			return true;

		d = 0;
		for (int i = y - 1; i >= 0; i--) {
			if (getPiece(x, i) == piece)
				d++;
			else
				break;
		}
		for (int i = y + 1; i < getSize(); i++) {
			if (getPiece(x, i) == piece)
				d++;
			else
				break;
		}
		if (d == 4)
			return true;

		d = 0;
		for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
			if (getPiece(i, j) == piece)
				d++;
			else
				break;
		}
		for (int i = x + 1, j = y + 1; i < getSize() && j < getSize(); i++, j++) {
			if (getPiece(i, j) == piece)
				d++;
			else
				break;
		}
		if (d == 4)
			return true;

		d = 0;
		for (int i = x - 1, j = y + 1; i >= 0 && j < getSize(); i--, j++) {
			if (getPiece(i, j) == piece)
				d++;
			else
				break;
		}
		for (int i = x + 1, j = y - 1; i < getSize() && j >= 0; i++, j--) {
			if (getPiece(i, j) == piece)
				d++;
			else
				break;
		}
		if (d == 4)
			return true;
		return false;
	}

	public void putPiece(int x, int y, Piece piece) {
		data[x][y] = piece;
		lastpiece = new int[] { x, y };
	}
}
