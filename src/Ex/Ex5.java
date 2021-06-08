package Ex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//총알 클래스
class Pistal2 extends JLabel {

	ImageIcon bullet = new ImageIcon("image/pistal.png");

	public Pistal2() {

		setIcon(bullet);
		setSize(bullet.getIconWidth(), bullet.getIconHeight());
		setLocation(240, 425);
	}
}

//UFO 위에거
class Item extends JLabel {

	ImageIcon ufo = new ImageIcon("image/ufo.png");

	public Item() {
		setIcon(ufo);
		setSize(ufo.getIconWidth(), ufo.getIconHeight());
		setLocation(10, 10);
	}
}

//UFO 아래거
class Item2 extends JLabel {

	ImageIcon ufo = new ImageIcon("image/ufo.png");

	public Item2() {
		setIcon(ufo);
		setSize(ufo.getIconWidth(), ufo.getIconHeight());
		setLocation(20, 100);
	}
}

//위 UFO 움직이게+사라지게 하는 스레드
class ItemTh extends Thread {

	JLabel it;
	JLabel pt;

	public ItemTh(JLabel it, JLabel pt) {
		this.it = it;
		this.pt = pt;

	}

	@Override
	public void run() {
		while (true) { // 총알에 맞는 경우
			if (pt.getX() + pt.getWidth() >= it.getX() && pt.getX() <= it.getX() + it.getWidth()
					&& pt.getY() <= it.getY() + it.getHeight() && pt.getY() + pt.getHeight() >= it.getY()) {
				it.setVisible(false);
				pt.setVisible(false);

				// 총알이랑 UFO 다시 세팅
				try {
					pt.setLocation(pt.getX(), -1);
					sleep(500);
					it.setLocation(0, it.getY());
					it.setVisible(true);
				} catch (Exception e) {
					return;
				}

			}

			// 창 밖을 넘어가는 경우
			if (it.getX() > 500) {
				it.setLocation(0, it.getY());
			}
			it.setLocation(it.getX() + 2, it.getY());
			it.getParent().repaint();

			// 스레드 시간설정
			try {
				sleep(10);
			} catch (InterruptedException e) {
				return;
			}

		}

	}

}

//아래 UFO 움직이게+사라지게 하는 스레드
class ItemTh2 extends Thread {

	JLabel it;
	JLabel pt;

	public ItemTh2(JLabel it, JLabel pt) {
		this.it = it;
		this.pt = pt;
	}

	@Override
	public void run() {

		while (true) {// 총알에 맞는 경우
			if (pt.getX() + pt.getWidth() >= it.getX() && pt.getX() <= it.getX() + it.getWidth()
					&& pt.getY() <= it.getY() + it.getHeight() && pt.getY() + pt.getHeight() >= it.getY()) {
				it.setVisible(false);
				pt.setVisible(false);

				// 총알이랑 UFO 다시 세팅
				try {
					pt.setLocation(pt.getX(), -100);
					sleep(500);
					it.setLocation(0, it.getY());
					it.setVisible(true);
				} catch (Exception e) {
					return;
				}

			}

			// 창 밖을 넘어가는 경우
			if (it.getX() > 500) {

				it.setVisible(false);
				it.setLocation(0, it.getY());
				it.setVisible(true);
			}
			it.setLocation(it.getX() + 5, it.getY());
			it.getParent().repaint();

			// 스레드 시간설정
			try {
				sleep(10);
			} catch (InterruptedException e) {
				return;
			}

		}

	}

}

//판넬 클래스
class MyCanvas2 extends JPanel {

	// 우주 이미지 배경으로 그리기
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		ImageIcon bgImage = new ImageIcon("image/bgImage.jpg");
		Image bg = bgImage.getImage();

		g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
	}

	public MyCanvas2() {
		setLayout(null);

		// 총알
		Pistal2 pt = new Pistal2();
		add(pt);

		// UFO와 스레드
		Item it = new Item();
		add(it);
		Item2 it2 = new Item2();
		add(it2);
		ItemTh iTh = new ItemTh(it, pt);
		ItemTh2 iTh2 = new ItemTh2(it2, pt);
		iTh.start();
		iTh2.start();

		// 키 리스너
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int cnt = 1;

				// 엔터키 누른 경우
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					cnt++;
					for (int i = 1; i < cnt; i++) {
						// 스레드 부착
						PistalTh2 th = new PistalTh2(pt);
						th.start();
					}
				}

				// 방향키 왼쪽키 누른 경우
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (pt.getY() == 425) {
						if (pt.getX() == 0) {
							// 이미 가장 왼쪽에 있는 경우에는 반응 X
						} else {
							pt.setLocation(pt.getX() - 10, pt.getY());
						}
					}
				}

				// 방향키 오른쪽키 누른 경우
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (pt.getY() == 425) {
						if (pt.getX() >= 475 - pt.getWidth()) { // 총알 크기 고려해서 기준 설정
							// 이미 가장 오른쪽에 있는 경우에는 반응 X
						} else {
							pt.setLocation(pt.getX() + 10, pt.getY());
						}
					}
				}
			}

		});
	}

}

//총알 발사하는 스레드
class PistalTh2 extends Thread {
	JLabel pt;

	public PistalTh2(JLabel pt) {
		this.pt = pt;
	}

	@Override
	public void run() {
		while (pt.getY() > 0) {
			if (pt.getY() <= 0) {
				pt.setVisible(false); // 창을 벗어나면 안보이게
			}
			pt.setLocation(pt.getX(), pt.getY() - 10); // 위로 올라가게
			pt.getParent().repaint();

			try {
				sleep(10);
			} catch (InterruptedException e) {
				return;
			}
		}

		// 끝까지 올라간 후(while 끝난 후) 총알 위치 재설정
		pt.setLocation(pt.getX(), 425);
		pt.setVisible(true);
	}
}

public class Ex5 extends JFrame {

	public Ex5() {

		setTitle("총알 맞추기");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MyCanvas2 mp = new MyCanvas2();
		mp.setSize(500, 500);
		setContentPane(mp);

		setSize(500, 500);
		setVisible(true);

		mp.setFocusable(true);
		mp.requestFocus();
	}

	public static void main(String[] args) {
		new Ex5();

	}

}