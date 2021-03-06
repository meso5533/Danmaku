package com.danmaku.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.danmaku.conf.ConfManager;
import com.danmaku.engine.Engine;
import com.danmaku.state.OnStateChangedListener;
import com.danmaku.state.State;
import com.danmaku.state.StateManager;
import com.danmaku.zbus.DanmakuZbus;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/* Frame Params */
	private final String DANMAKU_FRAME_TITLE = "Danmaku";
	private final int DANMAKU_FRAME_WIDTH = 250;
	private final int DANMAKU_FRAME_HEIGHT = 320;

	/* Frame Component */
	private JLabel labelHost;
	private JLabel labelPort;
	private JLabel labelMq;
	private JLabel labelToken;
	private JLabel labelTopic;
	private JTextField textHost;
	private JTextField textPort;
	private JTextField textMq;
	private JTextField textToken;
	private JTextField textTopic;
	private JButton btnStart;
	private JButton btnPause;
	private JButton btnStop;

	/* Engine */
	private Engine engine;

	/* DanmakuBoard */
	private DanmakuBoard danmakuBoard;

	public MainFrame(Engine engine) {
		this.engine = engine;

		initFrameParams();
		initStateManager();
		initDanmakuBoard();
	}

	private void initFrameParams() {
		this.setTitle(DANMAKU_FRAME_TITLE);
		this.setLayout(null);
		this.setSize(DANMAKU_FRAME_WIDTH, DANMAKU_FRAME_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initComponent();
		this.validate();
	}

	private void initStateManager() {
		engine.addOnStateChangedListener(new OnStateChangedListener() {

			@Override
			public void OnStateChanged(State oldState, State newState) {
				// TODO Auto-generated method stub
				switch (newState) {
				case STATE_STOP: {
					btnStart.setEnabled(true);
					btnPause.setEnabled(false);
					btnStop.setEnabled(false);
					setEnableAllTextField(true);
				}
					break;
				case STATE_RUNNING: {
					btnStart.setEnabled(false);
					btnPause.setEnabled(true);
					btnStop.setEnabled(false);
					setEnableAllTextField(false);
				}
					break;
				case STATE_PAUSE: {
					btnStart.setEnabled(true);
					btnPause.setEnabled(false);
					btnStop.setEnabled(true);
					setEnableAllTextField(false);
				}
					break;
				}

				if (oldState == State.STATE_RUNNING && newState == State.STATE_STOP) {
					JOptionPane.showMessageDialog(MainFrame.this, "与服务器的连接发生错误，请重新启动或检查您的参数！",
							"ERROR_MESSAGE",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});
		engine.automicChangeState(State.STATE_STOP);
	}

	private void initDanmakuBoard() {
		danmakuBoard = new DanmakuBoard(engine);
		danmakuBoard.show();
		danmakuBoard.toFront();
	}

	private void initComponent() {
		ConfManager conf = ConfManager.getInstance();

		/* init labelHost */
		labelHost = new JLabel();
		labelHost.setText("Host:");
		labelHost.setBounds(10, 20, 75, 25);
		labelHost.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(labelHost);

		/* init labelPort */
		labelPort = new JLabel();
		labelPort.setText("Post:");
		labelPort.setBounds(10, 65, 75, 25);
		labelPort.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(labelPort);

		/* init labelMq */
		labelMq = new JLabel();
		labelMq.setText("Mq:");
		labelMq.setBounds(10, 110, 75, 25);
		labelMq.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(labelMq);

		/* init labelToken */
		labelToken = new JLabel();
		labelToken.setText("Token:");
		labelToken.setBounds(10, 155, 75, 25);
		labelToken.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(labelToken);

		/* init labelTopic */
		labelTopic = new JLabel();
		labelTopic.setText("Topic:");
		labelTopic.setBounds(10, 200, 75, 25);
		labelTopic.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(labelTopic);

		/* init textHost */
		textHost = new JTextField();
		textHost.setText(conf.getProperty("server.host", DanmakuZbus.HOST));
		textHost.setBounds(100, 20, 120, 25);
		textHost.setBorder(BorderFactory.createCompoundBorder(
				new EtchedBorder(), new EmptyBorder(0, 5, 0, 5)));
		this.add(textHost);

		/* init textPort */
		textPort = new JTextField();
		textPort.setText(conf.getProperty("server.port", "" + DanmakuZbus.PORT));
		textPort.setBounds(100, 65, 120, 25);
		textPort.setBorder(BorderFactory.createCompoundBorder(
				new EtchedBorder(), new EmptyBorder(0, 5, 0, 5)));
		this.add(textPort);

		/* init textMq */
		textMq = new JTextField();
		textMq.setText(conf.getProperty("server.mq", "" + DanmakuZbus.MQ));
		textMq.setBounds(100, 110, 120, 25);
		textMq.setBorder(BorderFactory.createCompoundBorder(
				new EtchedBorder(), new EmptyBorder(0, 5, 0, 5)));
		this.add(textMq);

		/* init textToken */
		textToken = new JTextField();
		textToken.setText(conf.getProperty("server.token", "" + DanmakuZbus.TOKEN));
		textToken.setBounds(100, 155, 120, 25);
		textToken.setBorder(BorderFactory.createCompoundBorder(
				new EtchedBorder(), new EmptyBorder(0, 5, 0, 5)));
		this.add(textToken);

		/* init textTopic */
		textTopic = new JTextField();
		textTopic.setText(conf.getProperty("server.topic", DanmakuZbus.TOPIC));
		textTopic.setBounds(100, 200, 120, 25);
		textTopic.setBorder(BorderFactory.createCompoundBorder(
				new EtchedBorder(), new EmptyBorder(0, 5, 0, 5)));
		this.add(textTopic);

		/* init btnStart */
		btnStart = new JButton();
		btnStart.setText("Start");
		btnStart.setBounds(15, 245, 70, 25);
		this.add(btnStart);
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				onStartBtnClicked();
			}

		});

		/* init btnPause */
		btnPause = new JButton();
		btnPause.setText("Pause");
		btnPause.setBounds(90, 245, 70, 25);
		this.add(btnPause);
		btnPause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				onPauseBtnClicked();
			}

		});

		/* init btnStop */
		btnStop = new JButton();
		btnStop.setText("Stop");
		btnStop.setBounds(165, 245, 70, 25);
		this.add(btnStop);
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				onStopBtnClicked();
			}

		});
	}

	private void onStartBtnClicked() {
		if (engine.checkState(State.STATE_STOP)) {
			String host = textHost.getText().trim();
			String port = textPort.getText().trim();
			String mq = textMq.getText().trim();
			String token = textToken.getText().trim();
			String topic = textTopic.getText().trim();

			DanmakuZbus.setZbusConfig(host, port, mq, token, topic);
		}

		engine.automicChangeState(State.STATE_RUNNING);
	}

	private void onPauseBtnClicked() {
		engine.automicChangeState(State.STATE_PAUSE);
	}

	private void onStopBtnClicked() {
		engine.automicChangeState(State.STATE_STOP);
	}

	private void setEnableAllTextField(boolean enable) {
		textHost.setEnabled(enable);
		textPort.setEnabled(enable);
		textMq.setEnabled(enable);
		textTopic.setEnabled(enable);
	}

}
