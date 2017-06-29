package no.kreutzer.utils;

import no.kreutzer.utils.JavaWebSocketClient.WebSocketCommandResponse;

public class WebSocketMessageHandlerImpl implements WebSocketMessageHandler {
	SocketCommand cmd;

	public WebSocketMessageHandlerImpl(SocketCommand cmd) {
		this.cmd = cmd;
	}

	@Override
	public void onMessage(String message, WebSocketCommandResponse responseImpl) {
		if (message.equals("calStart")) {
			responseImpl.sendResponse(cmd.calStart());
		} else if (message.equals("calStop")) {
			responseImpl.sendResponse(cmd.calStop());
		} else {
			responseImpl.sendResponse("UNKNOWN");
		}
		
	}

}
