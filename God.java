package mainpuyo;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class God extends JFrame {
	PuyoGame pg1 = new PuyoGame();
	PuyoGame pg2 = new PuyoGame();
	int godtsumo[] = new int[256];
	ArrayList<Integer> tsumo = new ArrayList<Integer>();

	public God() {
		Getmomo.Getmomo();
		MainPanel();
		Filltsumo();
		pg1.gettertsumo(godtsumo);
		pg2.gettertsumo(godtsumo);
		pg1.select();
		pg2.select();
	}

	public void MainPanel() {
		setTitle("ぷよぷよ");
		setLayout(new GridLayout(1, 2));
		add(pg1);
		pg1.setSize(pg1.size * pg1.col, pg1.size * pg1.row);
		pg1.setBackground(Color.WHITE);
		add(pg2);
		pg2.setSize(pg2.size * pg2.col, pg2.size * pg2.row);
		pg2.setBackground(Color.WHITE);
		pack();
	}

	// �����c�����X�g
	void Filltsumo() {
		boolean hosei = true;

		for (int i = 1; i < 5; i++) {
			for (int j = 0; j < 64; j++)
				tsumo.add(i);
		}

		while (hosei) {
			hosei = false;
			Collections.shuffle(tsumo);

			for (int i = 0; i < 6; i++) {
				if (tsumo.get(i) == 4)
					hosei = true;
			}
		}

		for (int i = 0; i < 256; i++)
			godtsumo[i] = tsumo.get(i);
	}

	public static void main(String[] args) {
		God g = new God();
		g.setDefaultCloseOperation(EXIT_ON_CLOSE);
		g.setVisible(true);
	}
}

class PuyoGame extends JPanel {
	int row = 15, col = 9;
	int py, px, rotate;
	int chain;
	int size = 40;
	int chainNum = 0;
	int screen_chain = 0;
	int score = 0;
	int moves = 0;
	int best1 = 0;
	int best2 = 0;
	int lef, rig, dow, kx, kz;
	int tsumo[] = new int[256];
	static int momo[][][][] = new int[12][6][5][20];
	int p1[][] = new int[row][col];
	int pp1[][] = new int[row][col];
	int sub[][] = new int[row][col];

	PuyoGame() {
		setPreferredSize(new Dimension(col * size, row * size));
		setFocusable(true);
		addKeyListener(new Key_e());
	}

	// �A��
	void Game() {
		chain = 0;
		int ColorNum = 0;
		int ConnectNum = 0;
		int DeleteNum = 0;
		int scoresub = 0;
		int Color[] = new int[4];
		boolean isDelete = true;
		for (int i = 0; i < 4; i++)
			Color[i] = 1;

		drop();
		while (isDelete) {
			int connect = 0;
			isDelete = false;

			for (int i1 = 2; i1 < row - 1; i1++) {
				for (int i2 = 1; i2 < 7; i2++) {
					// pp1������
					for (int i3 = 2; i3 < row - 1; i3++) {
						for (int i4 = 1; i4 < 7; i4++)
							pp1[i3][i4] = 0;
					}

					if (p1[i1][i2] > 0 && p1[i1][i2] < 5) {
						pp1[i1][i2] = 1;
						connect = search(i1, i2, 1);

						if (connect >= 4) {
							isDelete = true;
							// �F�{�[�i�X
							ColorNum = ColorNum + Color[p1[i1][i2] - 1];
							Color[p1[i1][i2] - 1] = 0;

							Delete();
							ConnectNum = connect;
							DeleteNum = DeleteNum + connect;
							connect = 0;
						}
					}
				}
			}
			if (isDelete)
				chain++;
		}
		drop();

		if (chain > 0)
			chainNum++;

		if (chain != 0) {
			// ���_�v�Z
			scoresub = score_chain(chainNum) + score_color(ColorNum)
					+ score_connect(ConnectNum);
			if (scoresub == 0)
				scoresub = 1;

			score = score + DeleteNum * 10 * scoresub;
			screen_chain = chainNum;
		} else {
			select();
			chainNum = 0;
		}
		repaint();
	}

	// �A������return
	int search(int k1, int k2, int connect) {
		if (k1 > 2 && p1[k1 - 1][k2] == p1[k1][k2] && pp1[k1 - 1][k2] == 0) {
			pp1[k1 - 1][k2] = 1;
			connect = search(k1 - 1, k2, connect + 1);
		}
		if (k1 < row - 1 && p1[k1 + 1][k2] == p1[k1][k2]
				&& pp1[k1 + 1][k2] == 0) {
			pp1[k1 + 1][k2] = 1;
			connect = search(k1 + 1, k2, connect + 1);
		}
		if (k2 > 0 && p1[k1][k2 - 1] == p1[k1][k2] && pp1[k1][k2 - 1] == 0) {
			pp1[k1][k2 - 1] = 1;
			connect = search(k1, k2 - 1, connect + 1);
		}
		if (k2 < 7 && p1[k1][k2 + 1] == p1[k1][k2] && pp1[k1][k2 + 1] == 0) {
			pp1[k1][k2 + 1] = 1;
			connect = search(k1, k2 + 1, connect + 1);
		}
		return connect;
	}

	// �c������
	void Delete() {
		for (int i1 = 0; i1 < row; i1++) {
			for (int i2 = 0; i2 < col; i2++) {
				if (pp1[i1][i2] > 0) {
					p1[i1][i2] = 0;
				}
			}
		}
	}

	void gettertsumo(int t[]) {
		for (int i = 0; i < 256; i++)
			tsumo[i] = t[i];
	}

	void select() {
		py = 2;
		px = 3;
		rotate = 0;
		moves++;

		// �c�����[�v
		if (moves == 127)
			moves = -1;

		if (p1[2][col - 1] == 0) {
			// �����c���ݒ�
			if (p1[1][col - 1] == 0) {
				p1[1][3] = tsumo[0];
				p1[2][3] = tsumo[1];
				p1[0][col - 1] = tsumo[2];
				p1[1][col - 1] = tsumo[3];
				p1[3][col - 1] = tsumo[4];
				p1[4][col - 1] = tsumo[5];
			} else {
				p1[1][3] = p1[0][col - 1];
				p1[2][3] = p1[1][col - 1];
				p1[0][col - 1] = p1[3][col - 1];
				p1[1][col - 1] = p1[4][col - 1];
				p1[3][col - 1] = tsumo[moves * 2 + 2];
				p1[4][col - 1] = tsumo[moves * 2 + 3];
			}
		}
	}

	// �l�߂�
	void drop() {
		for (int i1 = 1; i1 < 7; i1++) {
			if (p1[0][i1] != 0)
				p1[0][i1] = 0;
		}

		for (int i1 = 1; i1 < 7; i1++) {
			for (int i2 = 0; i2 < row - 2; i2++) {
				if (p1[i2][i1] != 0 && p1[i2 + 1][i1] == 0) {
					p1[i2 + 1][i1] = p1[i2][i1];
					p1[i2][i1] = 0;
					i2 = 0;
				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (p1[2][3] == 0) {
			g.setColor(new Color(255, 200, 200));
			g.fillRect(3 * size, 2 * size, size, size);
		}

		for (int i1 = 0; i1 < row; i1++) {
			for (int i2 = 0; i2 < col; i2++) {
				p1[i1][0] = 6;
				p1[i1][col - 2] = 6;
				p1[row - 1][i2] = 6;

				for (int a = 0; a < 2; a++) {
					if (p1[a][i2] == 0) {
						g.setColor(Color.LIGHT_GRAY);
						g.fillRect(i2 * size, a * size, size, size);
					}
				}

				switch (p1[i1][i2]) {
				case 1:
					g.setColor(Color.RED);
					g.fillOval(i2 * size, i1 * size, size, size);
					break;
				case 2:
					g.setColor(Color.BLUE);
					g.fillOval(i2 * size, i1 * size, size, size);
					break;
				case 3:
					g.setColor(Color.GREEN);
					g.fillOval(i2 * size, i1 * size, size, size);
					break;
				case 4:
					g.setColor(Color.YELLOW);
					g.fillOval(i2 * size, i1 * size, size, size);
					break;
				case 5:
					g.setColor(Color.GRAY);
					g.fillOval(i2 * size, i1 * size, size, size);
					break;
				case 6:
					g.setColor(Color.BLACK);
					g.fillRect(i2 * size, i1 * size, size, size);
					break;
				}
			}
		}
		// �f�[�^�\��
		g.setColor(Color.RED);
		g.drawString(screen_chain + "連鎖", 10, 20);
		g.drawString(score + "点", 10, 40);
		g.drawString(moves + "手", 10, 60);
	}

	class Key_e extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (rotate == 0) {
					if (p1[py][px - 1] == 0 && px > 1) {
						p1[py][px - 1] = p1[py][px]; // ��
						p1[py - 1][px - 1] = p1[py - 1][px];
						p1[py][px] = 0;
						p1[py - 1][px] = 0;
						px--;
						repaint();
					}
				} else if (rotate == 1) {
					if (p1[py][px - 1] == 0 && px > 1) {
						p1[py][px - 1] = p1[py][px]; // ��
						p1[py][px] = p1[py][px + 1];
						p1[py][px + 1] = 0;
						px--;
						repaint();
					}
				} else if (rotate == 2) {
					if (p1[py + 1][px - 1] == 0 && px > 1) {
						p1[py][px - 1] = p1[py][px]; // ��
						p1[py + 1][px - 1] = p1[py + 1][px];
						p1[py][px] = 0;
						p1[py + 1][px] = 0;
						px--;
						repaint();
					}
				} else if (rotate == 3) {
					if (p1[py][px - 2] == 0 && px - 1 > 1) {
						p1[py][px - 2] = p1[py][px - 1];
						p1[py][px - 1] = p1[py][px]; // ��
						p1[py][px] = 0;
						px--;
						repaint();
					}
				}

			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (rotate == 0) {
					if (p1[py][px + 1] == 0 && px < col - 2) {
						p1[py][px + 1] = p1[py][px]; // ��
						p1[py - 1][px + 1] = p1[py - 1][px];
						p1[py][px] = 0;
						p1[py - 1][px] = 0;
						px++;
						repaint();
					}
				} else if (rotate == 1) {
					if (p1[py][px + 2] == 0 && px + 1 < col - 2) {
						p1[py][px + 2] = p1[py][px + 1];
						p1[py][px + 1] = p1[py][px]; // ��
						p1[py][px] = 0;
						px++;
						repaint();
					}
				} else if (rotate == 2) {
					if (p1[py + 1][px + 1] == 0 && px < col - 2) {
						p1[py][px + 1] = p1[py][px]; // ��
						p1[py + 1][px + 1] = p1[py + 1][px];
						p1[py][px] = 0;
						p1[py + 1][px] = 0;
						px++;
						repaint();
					}
				} else if (rotate == 3) {
					if (p1[py][px + 1] == 0 && px < col - 2) {
						p1[py][px + 1] = p1[py][px]; // ��
						p1[py][px] = p1[py][px - 1];
						p1[py][px - 1] = 0;
						px++;
						repaint();
					}
				}
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				Game();
			} else if (e.getKeyCode() == KeyEvent.VK_M) {
				put(best1);
				put(best2);
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				int best = 0; // 一番良い手
				int icchi = 0; // 一致個数
				int point = 0;
				int subicchi = 0;
				int num = 0; // ０以外の数代入
				int submoves = moves;
				boolean flag = true; // break用

				for (int i1 = 0; i1 < row; i1++) {
					for (int i2 = 0; i2 < col; i2++) {
						sub[i1][i2] = p1[i1][i2];
					}
				}

				for (int i = 1; i <= 22; i++) {
					for (int i1 = 1; i1 <= 22; i1++) {
						for (int i2 = 1; i2 <= 22; i2++) {
							point = 0;
							put(i);
							put(i1);
							put(i2);
							
							if(screen_chain != 0){
								moves = submoves;
								
								for (int i7 = 0; i7 < row; i7++) {
									for (int i8 = 0; i8 < col; i8++) {
										p1[i7][i8] = sub[i7][i8];
									}
								}
								break;
							}

							for (int i3 = 0; i3 < 20; i3++) { // データ数入力
								icchi = 0;
								flag = true;
								for (int i4 = 0; i4 < 5; i4++) {
									num = 0;
									for (int i5 = 0; i5 < 12; i5++) {
										for (int i6 = 0; i6 < 6; i6++) {
											if (momo[i5][i6][i4][i3]
													* p1[i5 + 2][i6 + 1] != 0) {
												if (num == 0)
													num = momo[i5][i6][i4][i3]
															* p1[i5 + 2][i6 + 1];

												if (num != momo[i5][i6][i4][i3]
														* p1[i5 + 2][i6 + 1]) {
													flag = false;
													break;
												}
											}
										}
										if (flag == false)
											break;
									}
									if (flag == false)
										break;
									
									icchi++;
								}
								if(icchi == 5)
									point++;
							}
							if (point > subicchi) {
								System.out.println("更新");
								subicchi = point;
								best = i;
								best1 = i1;
								best2 = i2;
							}
							moves = submoves;

							for (int i7 = 0; i7 < row; i7++) {
								for (int i8 = 0; i8 < col; i8++) {
									p1[i7][i8] = sub[i7][i8];
								}
							}
						}
					}
				}
				System.out.println(subicchi);
				put(best);

				/*
				 * } else if (e.getKeyCode() == KeyEvent.VK_B) { // �z��߂� for
				 * (int i3 = 0; i3 < row; i3++) { for (int i4 = 0; i4 < col;
				 * i4++) { p1[i3][i4] = sub[i3][i4]; } } moves--; repaint(); }
				 * else if (e.getKeyCode() == KeyEvent.VK_UP) { int subchain =
				 * 0; int submoves = moves; int best = 0; int hakka[][] = new
				 * int[row][col];
				 * 
				 * // ���̔z��R�s�[ for (int i1 = 0; i1 < row; i1++) { for (int
				 * i2 = 0; i2 < col; i2++) { sub[i1][i2] = p1[i1][i2]; } }
				 * 
				 * for (int i1 = 1; i1 <= 22; i1++) { for (int i2 = 1; i2 <= 22;
				 * i2++) { screen_chain = 0; put(i1); put(i2);
				 * 
				 * if (screen_chain == 0) { // ���̔z��R�s�[ for (int i3 = 0; i3
				 * < row; i3++) { for (int i4 = 0; i4 < col; i4++) {
				 * hakka[i3][i4] = p1[i3][i4]; } } for (int clr = 1; clr <= 4;
				 * clr++) { for (int i = 1; i <= 6; i++) { p1[1][3] = 0;
				 * p1[2][3] = 0; p1[1][i] = clr; Game2(); if (screen_chain >
				 * subchain) { System.out.println(screen_chain); subchain =
				 * screen_chain; best = i1; }
				 * 
				 * // �z��߂� for (int i3 = 0; i3 < row; i3++) { for (int i4 = 0;
				 * i4 < col; i4++) { p1[i3][i4] = hakka[i3][i4]; } } } } } //
				 * �z��߂� for (int i3 = 0; i3 < row; i3++) { for (int i4 = 0; i4
				 * < col; i4++) { p1[i3][i4] = sub[i3][i4]; } } moves =
				 * submoves; } } put(best);
				 */
			} else if (e.getKeyCode() == KeyEvent.VK_X) {
				if (rotate == 0) {
					if (p1[py][px + 1] != 0 && p1[py][px - 1] != 0) {
						rotate = 4;
					} else if (p1[py][px + 1] != 0 || px == col - 2) {
						p1[py][px - 1] = p1[py][px]; // ��
						p1[py][px] = p1[py - 1][px];
						p1[py - 1][px] = 0;
						px--;
						rotate = 1;
					} else {
						p1[py][px + 1] = p1[py - 1][px];
						p1[py - 1][px] = 0;
						rotate = 1;
					}
					repaint();
				} else if (rotate == 1) {
					if (p1[py + 1][px] != 0 && py == 0) {
					} else if (p1[py + 1][px] != 0 && py != 0) {
						p1[py - 1][px] = p1[py][px]; // ��
						p1[py][px] = p1[py][px + 1];
						p1[py][px + 1] = 0;
						py--;
						rotate = 2;
					} else {
						p1[py + 1][px] = p1[py][px + 1];
						p1[py][px + 1] = 0;
						rotate = 2;
					}
					repaint();
				} else if (rotate == 2) {
					if (p1[py][px + 1] != 0 && p1[py][px - 1] != 0) {
						rotate = 5;
					} else if (p1[py][px - 1] != 0 || px == 1) {
						p1[py][px + 1] = p1[py][px];
						p1[py][px] = p1[py + 1][px]; // ��
						p1[py + 1][px] = 0;
						px++;
						rotate = 3;
					} else {
						p1[py][px - 1] = p1[py + 1][px];
						p1[py + 1][px] = 0;
						rotate = 3;
					}
					repaint();
				} else if (rotate == 3) {
					if (py == 0) {
					} else {
						p1[py - 1][px] = p1[py][px - 1];
						p1[py][px - 1] = 0;
						rotate = 0;
					}
					repaint();
				} else if (rotate == 4) {
					if (p1[py + 1][px] != 0) {
						int a = p1[py - 1][px];
						p1[py - 1][px] = p1[py][px];
						p1[py][px] = a;
						py--;
					} else {
						p1[py + 1][px] = p1[py - 1][px];
						p1[py - 1][px] = 0;
					}
					rotate = 2;
					repaint();
				} else if (rotate == 5) {
					p1[py - 1][px] = p1[py + 1][px];
					p1[py + 1][px] = 0;
					rotate = 0;
					repaint();
				}

			} else if (e.getKeyCode() == KeyEvent.VK_Z) {
				if (rotate == 0) {
					if (p1[py][px + 1] != 0 && p1[py][px - 1] != 0) {
						rotate = 4;
					} else if (p1[py][px - 1] != 0 || px == 1) {
						p1[py][px + 1] = p1[py][px]; // ��
						p1[py][px] = p1[py - 1][px];
						p1[py - 1][px] = 0;
						px++;
						rotate = 3;
					} else {
						p1[py][px - 1] = p1[py - 1][px];
						p1[py - 1][px] = 0;
						rotate = 3;
					}
					repaint();
				} else if (rotate == 1) {
					if (py == 0) {
					} else {
						p1[py - 1][px] = p1[py][px + 1];
						p1[py][px + 1] = 0;
						rotate = 0;
					}
					repaint();
				} else if (rotate == 2) {
					if (p1[py][px + 1] != 0 && p1[py][px - 1] != 0) {
						rotate = 5;
					} else if (p1[py][px + 1] != 0 || px == col - 2) {
						p1[py][px - 1] = p1[py][px];
						p1[py][px] = p1[py + 1][px]; // ��
						p1[py + 1][px] = 0;
						px--;
						rotate = 1;
					} else {
						p1[py][px + 1] = p1[py + 1][px];
						p1[py + 1][px] = 0;
						rotate = 1;
					}
					repaint();
				} else if (rotate == 3) {
					if (p1[py + 1][px] != 0 && py == 0) {
					} else if (p1[py + 1][px] != 0 && py != 0) {
						p1[py - 1][px] = p1[py][px]; // ��
						p1[py][px] = p1[py][px - 1];
						p1[py][px - 1] = 0;
						py--;
						rotate = 2;
					} else {
						p1[py + 1][px] = p1[py][px - 1];
						p1[py][px - 1] = 0;
						rotate = 2;
					}
					repaint();
				} else if (rotate == 4) {
					if (p1[py + 1][px] != 0) {
						int a = p1[py - 1][px];
						p1[py - 1][px] = p1[py][px];
						p1[py][px] = a;
						py--;
					} else {
						p1[py + 1][px] = p1[py - 1][px];
						p1[py - 1][px] = 0;
					}
					rotate = 2;
					repaint();
				} else if (rotate == 5) {
					p1[py - 1][px] = p1[py + 1][px];
					p1[py + 1][px] = 0;
					rotate = 0;
					repaint();
				}
			}
		}
	}

	void Game2() {
		do {
			Game();
		} while (p1[1][3] == 0);
	}

	void put(int i1) {
		switch (i1) {
		case 1:
			p1[1][1] = p1[1][3];
			p1[2][1] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 2:
			p1[1][2] = p1[1][3];
			p1[2][2] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 3:
			Game();
			break;
		case 4:
			p1[1][4] = p1[1][3];
			p1[2][4] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 5:
			p1[1][5] = p1[1][3];
			p1[2][5] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 6:
			p1[1][6] = p1[1][3];
			p1[2][6] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 7:
			p1[2][1] = p1[1][3];
			p1[1][1] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 8:
			p1[2][2] = p1[1][3];
			p1[1][2] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 9:
			p1[0][3] = p1[2][3];
			p1[2][3] = p1[1][3];
			p1[1][3] = p1[0][3];
			p1[0][3] = 0;
			Game();
			break;
		case 10:
			p1[2][4] = p1[1][3];
			p1[1][4] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 11:
			p1[2][5] = p1[1][3];
			p1[1][5] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 12:
			p1[2][6] = p1[1][3];
			p1[1][6] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 13:
			p1[2][1] = p1[2][3];
			p1[2][2] = p1[1][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 14:
			p1[2][2] = p1[2][3];
			p1[2][3] = p1[1][3];
			p1[1][3] = 0;
			Game();
			break;
		case 15:
			p1[2][4] = p1[1][3];
			p1[1][3] = 0;
			Game();
			break;
		case 16:
			p1[2][4] = p1[2][3];
			p1[2][5] = p1[1][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 17:
			p1[2][5] = p1[2][3];
			p1[2][6] = p1[1][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 18:
			p1[2][1] = p1[1][3];
			p1[2][2] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 19:
			p1[2][2] = p1[1][3];
			p1[1][3] = 0;
			Game();
			break;
		case 20:
			p1[2][4] = p1[2][3];
			p1[2][3] = p1[1][3];
			p1[1][3] = 0;
			Game();
			break;
		case 21:
			p1[2][4] = p1[1][3];
			p1[2][5] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		case 22:
			p1[2][5] = p1[1][3];
			p1[2][6] = p1[2][3];
			p1[1][3] = 0;
			p1[2][3] = 0;
			Game();
			break;
		}
	}

	int score_chain(int rensa) {
		if (rensa == 2)
			return 8;
		else if (rensa == 3)
			return 16;
		else if (rensa == 4)
			return 32;
		else if (rensa == 5)
			return 64;
		else if (rensa == 6)
			return 96;
		else if (rensa == 7)
			return 128;
		else if (rensa == 8)
			return 160;
		else if (rensa == 9)
			return 192;
		else if (rensa == 10)
			return 224;
		else if (rensa == 11)
			return 256;
		else if (rensa == 12)
			return 288;
		else if (rensa == 13)
			return 320;
		else if (rensa == 14)
			return 352;
		else if (rensa == 15)
			return 384;
		else if (rensa == 16)
			return 416;
		else if (rensa == 17)
			return 448;
		else if (rensa == 18)
			return 480;
		else if (rensa == 19)
			return 512;
		else
			return 0;
	}

	int score_color(int color) {
		if (color == 2)
			return 3;
		else if (color == 3)
			return 6;
		else if (color == 4)
			return 12;
		else if (color == 5)
			return 24;
		else
			return 0;
	}

	int score_connect(int connect) {
		if (connect == 5)
			return 2;
		else if (connect == 6)
			return 3;
		else if (connect == 7)
			return 4;
		else if (connect == 8)
			return 5;
		else if (connect == 9)
			return 6;
		else if (connect == 10)
			return 7;
		else if (connect >= 11)
			return 10;
		else
			return 0;
	}
}