package Ex;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Ex.GamePanel.TargetThread;

public class Ex5_T extends JFrame {

	public Ex5_T() {

		// 게임판넬
		GamePanel p = new GamePanel();
		setContentPane(p);

		setSize(500, 400);
		setResizable(false); // 창크기 고정
		setVisible(true);

		p.statrGame();
	}

	public static void main(String[] args) {
		new Ex5_T();

	}

}

//게임판넬 - 리스너, 스레드
class GamePanel extends JPanel {

	TargetThread targetThread = null;
	JLabel baseLabel, targetLabel, bulletLabel;

	public GamePanel() {
		setLayout(null);

		// 총대
		baseLabel = new JLabel();
		baseLabel.setSize(40, 40);
		baseLabel.setOpaque(true);
		baseLabel.setBackground(Color.BLACK);

		// 목표물
		ImageIcon img = new ImageIcon("image/ufo.png");
		targetLabel = new JLabel(img);
		targetLabel.setSize(img.getIconWidth(), img.getIconHeight());

		// 총알
		bulletLabel = new JLabel();
		bulletLabel.setSize(10, 10);
		bulletLabel.setOpaque(true);
		bulletLabel.setBackground(Color.RED);

		// 붙이기
		add(baseLabel);
		add(targetLabel);
		add(bulletLabel);

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				// 방향키 왼쪽키 누른 경우
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (bulletLabel.getX() == 0) {
							// 이미 가장 왼쪽에 있는 경우에는 반응 X
						} else {
							bulletLabel.setLocation(bulletLabel.getX() - 10, bulletLabel.getY());
						}
					
				}

				// 방향키 오른쪽키 누른 경우
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (bulletLabel.getY() == 425) {
						if (bulletLabel.getX() >= 475 - bulletLabel.getWidth()) { // 총알 크기 고려해서 기준 설정
							// 이미 가장 오른쪽에 있는 경우에는 반응 X
						} else {
							bulletLabel.setLocation(bulletLabel.getX() + 10, bulletLabel.getY());
						}
					}
				}
			}

		});

		setFocusable(true);
		requestFocus();

	}

	// 게임시작을 해주는 메소드
	void statrGame() {
		// 총대, 총알, 목표물 위치 지정
		baseLabel.setLocation(this.getWidth() / 2 - 20, this.getHeight() - baseLabel.getHeight());
		bulletLabel.setLocation(getWidth() / 2 - 5, this.getHeight() - baseLabel.getHeight() - bulletLabel.getHeight());
		targetLabel.setLocation(0, 0);

		// 리스너 - 스레드를 실행
		targetThread = new TargetThread(targetLabel);
		targetThread.start();

		// 총대를 클릭하면 총알이 나가도록 하는 리스너
		baseLabel.addMouseListener(new MouseAdapter() {

			BulletThread bulletThread = null;

			@Override
			public void mouseClicked(MouseEvent e) {

				bulletThread = new BulletThread(bulletLabel, targetLabel, targetThread);
				bulletThread.start();

			}
		});
	}

	// 목표물 스레드
	class TargetThread extends Thread {

		JLabel target;

		// 반복할 일
		public TargetThread(JLabel target) {
			this.target = target;
		}

		@Override
		public void run() {
			while (true) {
				int randomX = (int) (Math.random() * 6);
				int randomY;

				if ((int) (Math.random() * 2 + 1) % 2 == 0) {
					randomY = (int) (Math.random() * 6);
				} else {
					randomY = (int) (Math.random() * -6);

				}

				int x = target.getX() + randomX;
				int y = target.getY() + randomY;

				if (x > GamePanel.this.getWidth()) {
					target.setLocation(0, (int) (Math.random() * 200));
				} else {
					for (int i = 0; i < 20; i++) {
						target.setLocation(x, y);
					}
				}
				target.getParent().repaint();

				try {
					sleep(20);
				} catch (InterruptedException e) { // catch문에 타깃이 총알에 맞았을때 경우를 실행
					target.setLocation(0, (int) (Math.random() * 200));
					target.getParent().repaint();
					try {
						sleep(100);
					} catch (InterruptedException e2) {
					}
				}
			}
		}
	}

	// 총알 스레드
	class BulletThread extends Thread {

		JLabel bullet, target; // Jlabel 대신 JComponent도 가능
		Thread targetThread;
		// 반복할일
		// - 맞은메소드
		// - 체크메소드(총알과 목표물의 위치값)

		public BulletThread(JLabel bullet, JLabel target, Thread targetThread) {
			this.bullet = bullet;
			this.target = target;
			this.targetThread = targetThread;
		}

		@Override
		public void run() {
			while (true) {
				// 이동, 맞았는지 체크
				if (hit()) {
					targetThread.interrupt();
					bullet.setLocation(bullet.getX(), baseLabel.getY() - bulletLabel.getHeight());
					// 총알이 붙은 판넬->getParent
					bullet.getParent().repaint();

					return;
				} else {
					int x = bullet.getX();
					int y = bullet.getY() - 5;

					if (y < 0) {
						bullet.setLocation(bullet.getX(), bullet.getParent().getHeight() - 50);
						bullet.getParent().repaint();
						return;
					}
					bullet.setLocation(x, y);
					bullet.getParent().repaint();

				}
				try {
					sleep(10);
				} catch (InterruptedException e) {

				}

			}

		}

		// 총알에 맞았을 경우를 체크하는 메소드
		private boolean hit() {
			if (targetCondition(bullet.getX(), bullet.getY())
					|| targetCondition(bullet.getX() + bullet.getWidth() - 1, bullet.getY())
					|| targetCondition(bullet.getX() + bullet.getWidth() - 1, bullet.getY() + bullet.getHeight() - 1)
					|| targetCondition(bullet.getX(), bullet.getY() + bullet.getHeight() - 1)) {

				return true;
			} else {
				return false;
			}

		}

		// 총알이 맞았는지를 체크
		private boolean targetCondition(int x, int y) {
			if (((target.getX() <= x) && (target.getX() + target.getWidth() >= x))
					&& ((target.getY() < y) && (target.getY() + target.getHeight() >= y))) {
				return true;
			} else {
				return false;
			}

		}
	}
}