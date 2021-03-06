package com.danmaku.zbus;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zstacks.zbus.client.MqAdmin;
import org.zstacks.zbus.client.MqConfig;
import org.zstacks.zbus.protocol.Proto;
import org.zstacks.znet.Message;
import org.zstacks.znet.RemotingClient;

import com.danmaku.model.DanmakuModel;

public class DanmakuFetcher extends MqAdmin implements Closeable {
	private final static Logger logger = LoggerFactory.getLogger(DanmakuFetcher.class);

	private String topic;
	private RemotingClient client;
	private boolean isFetching = false;

	private List<OnFetchListener> listeners = new ArrayList<OnFetchListener>();

	private ExecutorService pool = Executors.newSingleThreadExecutor();

	public DanmakuFetcher(MqConfig config) throws IOException {
		super(config);
		this.topic = config.getTopic();
		this.client = broker.getClient(myClientHint());
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void startFetch() throws IOException {
		if (!isFetching) {
			isFetching = true;

			pool.submit(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					while (isFetching) {
						loop();
					}
				}

			});
		}
	}

	private Message subMessage;

	private Message subTopicMessage() throws IOException, InterruptedException {
		if (subMessage == null) {
			subMessage = new Message();
			subMessage.setCommand(Proto.Consume);
			subMessage.setMq(mq);
			subMessage.setTopic(topic);
		}

		if (this.client == null)
			return null;

		Message danmakuMsg = this.client.invokeSync(subMessage, 10000);
		return danmakuMsg;
	}

	private void loop() {
		Message danmakuMsg = null;
		try {
			danmakuMsg = subTopicMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 发生在获取时的异常才算数，发生在非获取时可能是由于client被关闭时还在等待订阅的消息所引起的，属于正常情况
			if (isFetching) {
				e.printStackTrace();
				stopFetch();
				notifyError();
				return;
			}
		}
		if (null == danmakuMsg) {
			// 超时重新获取
			return;
		} else {
			logger.info(danmakuMsg.toString());
			if (danmakuMsg.isStatus200()) {
				try {
					DanmakuModel danmaku = DanmakuModel.fromMessage(danmakuMsg);
					notifyReceive(danmaku);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				stopFetch();
				notifyError();
				return;
			}
		}
	}

	public void stopFetch() {
		isFetching = false;
	}

	public interface OnFetchListener {
		public void onReceive(DanmakuModel danmaku);

		public void onError();
	}

	public void addOnFetchListener(OnFetchListener l) {
		this.listeners.add(l);
	}

	public void removeOnFetchListener(OnFetchListener l) {
		this.listeners.remove(l);
	}

	private void notifyReceive(DanmakuModel danmaku) {
		for (OnFetchListener l : listeners) {
			l.onReceive(danmaku);
		}
	}

	private void notifyError() {
		for (OnFetchListener l : listeners) {
			l.onError();
		}
	}

	private void closeClient() {
		try {
			if (this.client != null) {
				this.client.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.client = null;
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		stopFetch();
		closeClient();
		pool.shutdown();
	}
}
