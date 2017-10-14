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
		} else if (message.equals("getConfig")) {
			responseImpl.sendResponse(cmd.getConfig());
		} else if (message.equals("getState")) {
			responseImpl.sendResponse(cmd.getState());
		} else if (message.equals("setFillModeOFF")) {
			responseImpl.sendResponse(cmd.setMode(0));
		} else if (message.equals("setFillModeSLOW")) {
			responseImpl.sendResponse(cmd.setMode(1));
		} else if (message.equals("setFillModeFAST")) {
			responseImpl.sendResponse(cmd.setMode(2));
		} else if (message.equals("setFullModeSWITCH")) {
			responseImpl.sendResponse(cmd.setFull(0));
		} else if (message.equals("setFullModeLEVEL")) {
			responseImpl.sendResponse(cmd.setFull(1));
		} else if (message.equals("startPump")) {
			responseImpl.sendResponse(cmd.pump(1));
		} else if (message.equals("stopPump")) {
			responseImpl.sendResponse(cmd.pump(0));
		} else if (message.equals("openValve")) {
			responseImpl.sendResponse(cmd.valve(1));
		} else if (message.equals("closeValve")) {
			responseImpl.sendResponse(cmd.valve(0));
		} else {
			responseImpl.sendResponse("UNKNOWN: "+message);
		}
		
	}

}
