package com.github.xzzpig.gobang;

public abstract class GameControler {
	private static GameControler instance;

	enum Side {
		BLACK, WHITE;
	}

	public GameControler() {
		instance = this;
	}

	public abstract Side getSide();

	public abstract boolean canPut(int x, int y);

	public abstract void put(int x, int y);

	public boolean isWin(int x, int y) {
		return Chest.getInstance().isWin(x, y);
	}

	public abstract void reset();

	public static GameControler getInstance() {
		return instance;
	}

	public abstract void printIfWin(int x, int y);
}
