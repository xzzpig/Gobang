package com.github.xzzpig.gobang;

import java.awt.CardLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class GobangWindow {

	private JFrame frame;
	private GameView gameView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GobangWindow window = new GobangWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GobangWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		gameView = new GameView();
		frame.getContentPane().add(gameView, "gameview");
	}

}
