package com.pokware.server.game.commands.fromplayer;

import com.pokware.protobuf.EventResponseFactory;
import com.pokware.protobuf.RPCMessages.JoinTableRequest;
import com.pokware.protobuf.RPCMessages.Response;
import com.pokware.ramjet.CommandScheduler;
import com.pokware.server.game.Game;
import com.pokware.server.game.GameCommand;
import com.pokware.server.game.Player;

public class JoinTableCommand extends GameCommand {

	private JoinTableRequest joinTableRequest;
	private long playerId;
	private int requestId;

	public JoinTableCommand(int requestId, long playerId, JoinTableRequest joinTableRequest) {
		super(joinTableRequest.getGameId());
		this.requestId = requestId;
		this.joinTableRequest = joinTableRequest;
		this.playerId = playerId;
	}

	@Override
	public long getTargetId() {
		return joinTableRequest.getGameId();
	}

	@Override
	public int checkPermission(Game game) {
		return 0;
	}

	@Override
	public void executeOn(Game game, CommandScheduler<Game> commandManager) {
		Player player = game.getPlayerLocator().getPlayer(playerId);
		game.addObserver(player);

		Response tableStatusEventResponse = EventResponseFactory.getTableStatusEventResponse(game.getGameId(), game.getTable());
		player.sendMessage(tableStatusEventResponse);
	}	

}
