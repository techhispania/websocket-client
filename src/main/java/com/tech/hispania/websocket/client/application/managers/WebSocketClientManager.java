package com.tech.hispania.websocket.client.application.managers;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;

@Component
public class WebSocketClientManager {

	private final static Logger logger = LogManager.getLogger(WebSocketClientManager.class);

	private WebSocketSession session;

	private boolean connected = false;

	@PostConstruct
	public void connect() throws Exception {
		doConnect();
	}

	public void doConnect() throws Exception {
		StandardWebSocketClient client = new StandardWebSocketClient();

		client.doHandshake(new TextWebSocketHandler() {
			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				logger.info("Connection established with the server");
				WebSocketClientManager.this.session = session;
				connected = true;
			}

			@Override
			protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
				logger.info("Received from server: {}", message.getPayload());
			}

			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
				logger.info("Connection closed. Reconnecting in 5sec...");
				WebSocketClientManager.this.session = null;
				connected = false;

				new Thread(() -> {
					try {
						while (!connected) {
							logger.info("Reconnecting...");
							Thread.sleep(5000);
							doConnect();
						}
					} catch (Exception e) {
						Thread.currentThread().interrupt();
					}
				}).start();
			}
		}, "ws://localhost:8080/ws");
	}

	public void sendMessage(String message) throws IOException {
		if (session != null && session.isOpen()) {
			session.sendMessage(new TextMessage(message));
		} else {
			logger.error("Websocket is not connected. Unable to send message.");
		}
	}
}
