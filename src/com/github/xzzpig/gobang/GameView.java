package com.github.xzzpig.gobang;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

import com.github.xzzpig.gobang.Chest.Piece;

public class GameView extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	float dis[];

	private int loc_t[] = new int[] { -1, -1 };

	public GameView() {
		new SingleGameControler();
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent event) {
				int[] loc = getFixedLocAt(event.getX(), event.getY());
				if (loc_t[0] != loc[0] || loc_t[1] != loc[1]) {
					paint(getGraphics());
					if (!GameControler.getInstance().canPut(loc[0] - 1, loc[1] - 1))
						return;
					Graphics graphics = getGraphics();
					graphics.setColor(Color.GRAY);
					graphics.drawRect((int) (loc[0] * dis[0] - dis[0] / 2), (int) (loc[1] * dis[1] - dis[1] / 2),
							(int) dis[0], (int) dis[1]);
					loc_t = loc;
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int[] loc = getFixedLocAt(event.getX(), event.getY());
				int x = loc[0] - 1, y = loc[1] - 1;
				GameControler.getInstance().put(x, y);
				paint(getGraphics());
				GameControler.getInstance().printIfWin(x, y);
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Chest chest = Chest.getInstance();
		int size = chest.getSize();
		measure();
		drawBack(g);
		drawLine(g, size);
		drawPiece(g, chest, size);
	}

	private void drawBack(Graphics g) {
		g.setColor(Color.getHSBColor(30, 240, 96));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private void drawPiece(Graphics g, Chest chest, int size) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Piece piece = chest.getPiece(i, j);
				if (piece == Piece.BLACK)
					g.setColor(Color.BLACK);
				else if (piece == Piece.WHITE)
					g.setColor(Color.WHITE);
				else
					continue;
				int w = (int) dis[0] * 2 / 3, h = (int) dis[1] * 2 / 3, x = (int) ((i + 1) * dis[0]) - w / 2,
						y = (int) ((j + 1) * dis[1]) - h / 2;
				g.fillOval(x, y, w, h);
				if (i == Chest.getInstance().getLastPieceLoc()[0] && j == Chest.getInstance().getLastPieceLoc()[1]) {
					if (piece == Piece.BLACK)
						g.setColor(Color.WHITE);
					else if (piece == Piece.WHITE)
						g.setColor(Color.BLACK);
					g.drawLine((int) (((i + 1) * dis[0]) - dis[0] / 8), (int) (((j + 1) * dis[1])),
							(int) (((i + 1) * dis[0]) + dis[0] / 8), (int) (((j + 1) * dis[1])));
					g.drawLine((int) (((i + 1) * dis[0])), (int) (((j + 1) * dis[1]) - dis[1] / 8),
							(int) (((i + 1) * dis[0])), (int) (((j + 1) * dis[1]) + dis[1] / 8));
				}
			}
		}

	}

	private void drawLine(Graphics g, int size) {
		g.setColor(Color.BLACK);
		size++;
		for (int i = 1; i < size; i++) {
			g.drawLine(0, (int) (dis[1] * i), getWidth(), (int) (dis[1] * i));
			g.drawLine((int) (dis[0] * i), 0, (int) (dis[0] * i), getHeight());
		}
	}

	private void measure() {
		Chest chest = Chest.getInstance();
		int size = chest.getSize() + 1;
		dis = new float[] { (float) getWidth() / size, (float) getHeight() / size };
	}

	private int[] getFixedLocAt(int x, int y) {
		int a = (int) ((x) / dis[0] + 0.5), b = (int) (y / dis[1] + 0.5);
		return new int[] { a, b };
	}
}
