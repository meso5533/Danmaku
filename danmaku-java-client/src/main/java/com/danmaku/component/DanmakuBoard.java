package com.danmaku.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.danmaku.engine.DanmakuEngine;
import com.danmaku.engine.DanmakuSet;
import com.danmaku.model.DanmakuModel;
import com.danmaku.state.StateManager;
import com.danmaku.state.StateManager.State;
import com.danmaku.util.NativeUtil;
import com.danmaku.util.ScreenUtil;

public class DanmakuBoard extends JDialog {

	private static final long serialVersionUID = -4142280682395283885L;
	private static final String TITLE = "Danmaku-Board";

	private JPanel danmakuPanel = new DanmakuPanel();
	private StateManager stateManager;
	private DanmakuEngine engine;
	private DanmakuSet danmakuSet;

	/*
	 * ����Ƿ���ù�JNI����ʹ����͸������ֹ�ظ�����
	 */
	private boolean hasSetTransparent = false;

	public DanmakuBoard(StateManager stateManager) {
		this.stateManager = stateManager;
		this.setUndecorated(true);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle(TITLE);
		this.setBounds(0, 0, ScreenUtil.getScreenWidth(),
				ScreenUtil.getscreenHeightWitoutTaskbar());
		this.getContentPane().setBackground(new Color(1, 1, 1));
		this.validate();
		this.setContentPane(danmakuPanel);

		/*
		 * Color(1, 1, 1) ���Զ����һ���������ɫ
		 * ������JNI����ʱ�������ɫ��ͬ�����ؽ����͸��
		 */
		danmakuPanel.setBackground(new Color(1, 1, 1));

		engine = new DanmakuEngine(stateManager, this);
		danmakuSet = engine.getDanmakuSet();

		engine.run();

	}

	@SuppressWarnings("deprecation")
	public void show() {
		super.show();
		if (!hasSetTransparent) {
			NativeUtil.SetWindowBackgroundTransparent(1, 1, 1,
					DanmakuBoard.TITLE);
			hasSetTransparent = true;
		}
	}

	/* �����ƹ�����DanmakuPanel��ʵ��ֻ��Ϊ��ʹ���ƹ��̱Ƚ�ƽ�� */
	public class DanmakuPanel extends JPanel {
		private static final long serialVersionUID = 988932744923127264L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (hasSetTransparent
					&& stateManager.checkState(State.STATE_RUNNING)) {

				// DanmakuBoard.this.toFront();

				Graphics2D g2 = (Graphics2D) g;

				try {
					danmakuSet.lock();
					Iterator<DanmakuModel> it = danmakuSet.getDanmakus().iterator();
					while (it.hasNext()) {
						DanmakuModel danmaku = it.next();

						/* ��Ļ����ʾ�ĵ�Ļ����. */
						String danmakuStr = danmaku.username + ":" + danmaku.content;

						/* ���û��ʲ�����. */
						g2.setColor(getColor(danmaku.colorRed, danmaku.colorGreen,
								danmaku.colorBlue));
						g2.setFont(new Font("΢���ź�", Font.BOLD, danmaku.fontSize));
						g2.drawString(danmakuStr, danmaku.x, danmaku.y);

						/* �����ƶ���Ļ����. */
						danmaku.x -= danmaku.speed;

						/* ���㵯Ļ�ĳ���. */
						FontRenderContext frc = g2.getFontRenderContext();
						Rectangle2D rect = g2.getFont()
								.getStringBounds(danmakuStr, frc);
						int lengthInPix = (int) rect.getWidth();

						/* ����Ļ�Ѿ�������Ļ����ɾ�� */
						if (danmaku.x < -lengthInPix) {
							it.remove();
						}
					}
					danmakuSet.unLock();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private Color getColor(int R, int G, int B) {
			if (R >= 0 && R <= 255 && G >= 0 && G <= 255 && B >= 0 && B <= 255) {
				return new Color(R, G, B);
			} else {
				return new Color(0, 0, 0);
			}
		}
	}
}