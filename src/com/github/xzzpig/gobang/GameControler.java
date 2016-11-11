package com.github.xzzpig.gobang;

public abstract class GameControler {
	enum Side {
		BLACK, WHITE;
	}

	private static GameControler instance;

	public static GameControler getInstance() {
		return instance;
	}

	public GameControler() {
		instance = this;
	}

	public abstract boolean canPut(int x, int y);

	public abstract Side getSide();

	public boolean isWin(int x, int y) {
		return Chest.getInstance().isWin(x, y);
	}

	public abstract void printIfWin(int x, int y);

	public abstract void put(int x, int y);

	public abstract void reset();
}
