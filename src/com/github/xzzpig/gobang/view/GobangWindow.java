package com.github.xzzpig.gobang.view;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.github.xzzpig.gobang.Chest;
import com.github.xzzpig.gobang.GameControler;
import com.github.xzzpig.gobang.MultiGameControler;
import com.github.xzzpig.gobang.SingleGameControler;
import com.github.xzzpig.gobang.WebGameControler;

import net.miginfocom.swing.MigLayout;

public class GobangWindow {

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

	private JFrame frame;
	private GameView gameView;
	private JMenu menu_config;
	private JMenuItem menuItem_size;
	private JMenu menu_multi;
	private JMenuItem menuItem_Server;
	private JMenuItem menuItem_Client;

	private JMenuItem menuItem_inRoom;

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
		frame.getContentPane().setLayout(new MigLayout("", "[778px,grow]", "[grow]"));
		gameView = new GameView();
		frame.getContentPane().add(gameView, "cell 0 0,grow");

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		menu_config = new JMenu("\u8BBE\u7F6E");
		menuBar.add(menu_config);

		menuItem_size = new JMenuItem("\u68CB\u76D8\u5927\u5C0F");
		menuItem_size.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println(event.getActionCommand());
				int size = 0;
				while (true) {
					String size_s = JOptionPane.showInputDialog(null, "棋盘大小", "五子棋", MessageType.INFO.ordinal());
					try {
						size = Integer.parseInt(size_s);
					} catch (Exception e) {
					}
					if (size != 0)
						break;
				}
				new Chest(size);
				GobangWindow.this.frame.paint(frame.getGraphics());
			}
		});
		menu_config.add(menuItem_size);

		menu_multi = new JMenu("\u591A\u4EBA");
		menuBar.add(menu_multi);

		menuItem_Server = new JMenuItem("\u4F5C\u4E3A\u4E3B\u673A");
		menuItem_Server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new MultiGameControler();
					JOptionPane.showMessageDialog(null, "服务器启动成功");
				} catch (UnknownHostException e) {
					JOptionPane.showMessageDialog(frame, "服务器启动失败");
					return;
				}

			}
		});
		menu_multi.add(menuItem_Server);

		menuItem_Client = new JMenuItem("\u8FDE\u63A5\u4E3B\u673A");
		menuItem_Client.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = null;
				while (ip == null) {
					ip = JOptionPane.showInputDialog(null, "服务器IP", "五子棋", MessageType.INFO.ordinal());
				}
				new Chest(Chest.getInstance().getSize());
				GameControler.getInstance().reset();
				try {
					new MultiGameControler(ip, 1597);
				} catch (UnknownHostException e) {
					JOptionPane.showMessageDialog(frame, "服务器连接失败");
					return;
				}
			}
		});
		menu_multi.add(menuItem_Client);

		menuItem_inRoom = new JMenuItem("\u52A0\u5165\u623F\u95F4");
		menuItem_inRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String room = null;
				while (room == null) {
					room = JOptionPane.showInputDialog(null, "加入房间名称", "五子棋", MessageType.INFO.ordinal());
				}
				try {
					GameControler.getInstance().reset();
					new Chest();
					new WebGameControler(room);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "房间已加满");
					new Chest();
					new SingleGameControler();
				}
			}
		});
		menu_multi.add(menuItem_inRoom);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (frame.isEnabled()) {
					frame.paint(frame.getGraphics());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
