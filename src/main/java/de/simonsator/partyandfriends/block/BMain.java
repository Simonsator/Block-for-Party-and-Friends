package de.simonsator.partyandfriends.block;

import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.api.events.command.FriendshipCommandEvent;
import de.simonsator.partyandfriends.api.events.command.party.InviteEvent;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.block.subcommands.Block;
import de.simonsator.partyandfriends.block.subcommands.BlockList;
import de.simonsator.partyandfriends.block.subcommands.UnBlock;
import de.simonsator.partyandfriends.communication.sql.MySQLData;
import de.simonsator.partyandfriends.communication.sql.pool.PoolData;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.friends.subcommands.Add;
import de.simonsator.partyandfriends.main.Main;
import de.simonsator.partyandfriends.pafplayers.manager.PAFPlayerManagerMySQL;
import de.simonsator.partyandfriends.pafplayers.mysql.OnlinePAFPlayerMySQL;
import de.simonsator.partyandfriends.pafplayers.mysql.PAFPlayerMySQL;
import de.simonsator.partyandfriends.party.command.PartyCommand;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.simonsator.partyandfriends.utilities.PatterCollection.PLAYER_PATTERN;

public class BMain extends PAFExtension implements Listener {
	private BConnection connection;

	@Override
	public void onEnable() {
		try {
			ConfigurationCreator configuration = new BConfigurationCreator(new File(getConfigFolder(), "config.yml"), this);
			PoolData poolData = new PoolData(Main.getInstance().getGeneralConfig().getInt("MySQL.Pool.MinPoolSize"),
					Main.getInstance().getGeneralConfig().getInt("MySQL.Pool.MaxPoolSize"),
					Main.getInstance().getGeneralConfig().getInt("MySQL.Pool.InitialPoolSize"),
					Main.getInstance().getGeneralConfig().getInt("MySQL.Pool.IdleConnectionTestPeriod"),
					Main.getInstance().getGeneralConfig().getBoolean("MySQL.Pool.TestConnectionOnCheckin"),
					Main.getInstance().getGeneralConfig().getString("MySQL.Pool.ConnectionPool"));
			connection = new BConnection(new MySQLData(Main.getInstance().getGeneralConfig().getString("MySQL.Host"),
					Main.getInstance().getGeneralConfig().getString("MySQL.Username"), Main.getInstance().getGeneralConfig().getString("MySQL.Password"),
					Main.getInstance().getGeneralConfig().getInt("MySQL.Port"), Main.getInstance().getGeneralConfig().getString("MySQL.Database"),
					Main.getInstance().getGeneralConfig().getString("MySQL.TablePrefix"), Main.getInstance().getGeneralConfig().getBoolean("MySQL.UseSSL")), poolData);
			ProxyServer.getInstance().getPluginManager().registerListener(this, this);
			Friends.getInstance().addCommand(new Block(configuration.getStringList("Commands.Block.Name"), configuration.getInt("Commands.Block.Priority"), configuration.getString("Messages.Block.Permission"), configuration.getString("Messages.Block.CommandUsage"), this, configuration));
			Friends.getInstance().addCommand(new UnBlock(configuration.getStringList("Commands.UnBlock.Name"), configuration.getInt("Commands.UnBlock.Priority"), configuration.getString("Messages.UnBlock.Permission"), configuration.getString("Messages.UnBlock.CommandUsage"), this, configuration));
			if (configuration.getBoolean("Commands.BlockList.Use"))
				Friends.getInstance().addCommand(new BlockList(this, configuration));
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		ProxyServer.getInstance().getPluginManager().unregisterListeners(this);
	}

	@EventHandler
	public void onInvite(InviteEvent pEvent) {
		if (isBlocked(pEvent.getExecutor(), pEvent.getInteractPlayer()) || isBlocked(pEvent.getInteractPlayer(), pEvent.getExecutor())) {
			pEvent.getCaller().sendError(pEvent.getExecutor(), new TextComponent(TextComponent.fromLegacyText(PartyCommand.getInstance().getPrefix()
					+ Main.getInstance().getMessages().getString("Party.Command.Invite.CanNotInviteThisPlayer"))));
			pEvent.setCancelled(true);
		}
	}

	@EventHandler
	public void onAdd(FriendshipCommandEvent pEvent) {
		if (pEvent.getCaller().getClass().equals(Add.class))
			if (isBlocked(pEvent.getExecutor(), pEvent.getInteractPlayer()) || isBlocked(pEvent.getInteractPlayer(), pEvent.getExecutor())) {
				pEvent.getCaller().sendError(pEvent.getExecutor(), new TextComponent(TextComponent.fromLegacyText(Friends.getInstance().getPrefix() + PLAYER_PATTERN.matcher(Main.getInstance().getMessages().getString("Friends.Command.Add.CanNotSendThisPlayer")).replaceFirst(pEvent.getInteractPlayer().getName()))));
				pEvent.setCancelled(true);
			}
	}


	public boolean isBlocked(PAFPlayer pBlocker, PAFPlayer pBlocked) {
		return connection.isBlocked(((PAFPlayerMySQL) pBlocker.getPAFPlayer()).getPlayerID(),
				((PAFPlayerMySQL) pBlocked.getPAFPlayer()).getPlayerID());
	}

	public void addBlock(OnlinePAFPlayer pBlocker, PAFPlayer pBlocked) {
		connection.addBlock(((OnlinePAFPlayerMySQL) pBlocker).getPlayerID(),
				((PAFPlayerMySQL) pBlocked.getPAFPlayer()).getPlayerID());
	}

	public void removeBlock(OnlinePAFPlayer pBlocker, PAFPlayer pBlocked) {
		connection.removeBlock(((OnlinePAFPlayerMySQL) pBlocker).getPlayerID(),
				((PAFPlayerMySQL) pBlocked.getPAFPlayer()).getPlayerID());
	}

	public List<PAFPlayer> getBlockedPlayers(OnlinePAFPlayer pPlayer) {
		List<Integer> idList = connection.getBlockedPlayers(((PAFPlayerMySQL) pPlayer.getPAFPlayer()).getPlayerID());
		List<PAFPlayer> pafPlayers = new ArrayList<>(idList.size());
		for (int id : idList)
			pafPlayers.add(((PAFPlayerManagerMySQL) PAFPlayerManager.getInstance()).getPlayer(id));
		return pafPlayers;
	}
}
