package com.tech.hispania.websocket.client.presentation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.tech.hispania.websocket.client.application.managers.WebSocketClientManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChatController {

	private final WebSocketClientManager webSocketClientManager;

	public ChatController(WebSocketClientManager webSocketClientManager) {
		this.webSocketClientManager = webSocketClientManager;
	}

	@GetMapping("/chat")
	public String index(Model model) {
		return "chat";
	}

	@PostMapping("/send")
	public String sendMessage(@RequestParam String message, Model model) {
		try {
			webSocketClientManager.sendMessage(message);
			model.addAttribute("status", "Message sent: " + message);
		} catch (Exception e) {
			model.addAttribute("status", "Error sending message: " + e.getMessage());
		}
		return "chat";
	}
}
