package com.massivecraft.factions.engine;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.Collections;

public class EngineMoveChunk extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineMoveChunk i = new EngineMoveChunk();
	public static EngineMoveChunk get() { return i; }
	
	// -------------------------------------------- //
	// MOVE CHUNK: DETECT
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void moveChunkDetect(PlayerMoveEvent event)
	{
		// If the player is moving from one chunk to another ...
		if (MUtil.isSameChunk(event)) return;
		Player player = event.getPlayer();

		// ... gather info on the player and the move ...
		MPlayer mplayer = MPlayer.get(player);

		PS chunkFrom = PS.valueOf(event.getFrom()).getChunk(true);
		PS chunkTo = PS.valueOf(event.getTo()).getChunk(true);

		// ... send info onwards and try auto-claiming.
		sendChunkInfo(mplayer, player, chunkFrom, chunkTo);
		tryAutoClaim(mplayer, chunkTo);
	}

	// -------------------------------------------- //
	// MOVE CHUNK: SEND CHUNK INFO
	// -------------------------------------------- //

	private static void sendChunkInfo(MPlayer mplayer, Player player, PS chunkFrom, PS chunkTo)
	{
		sendAutoMapUpdate(mplayer, player);
		sendFactionTerritoryInfo(mplayer, player, chunkFrom, chunkTo);
	}
	
	private static void sendAutoMapUpdate(MPlayer mplayer, Player player)
	{
		if (!mplayer.isMapAutoUpdating()) return;
		player.performCommand("f mapa");
	}
	
	private static void sendFactionTerritoryInfo(MPlayer mplayer, Player player, PS chunkFrom, PS chunkTo)
	{
		Faction factionFrom = BoardColl.get().getFactionAt(chunkFrom);
		Faction factionTo = BoardColl.get().getFactionAt(chunkTo);
		
		if (factionFrom == factionTo) return;
		
		if (mplayer.isTerritoryInfoTitles())
		{
			String titleMain = parseTerritoryInfo(MConf.get().territoryInfoTitlesMain, mplayer, factionTo);
			String titleSub = parseTerritoryInfo(MConf.get().territoryInfoTitlesSub, mplayer, factionTo);
			int ticksIn = MConf.get().territoryInfoTitlesTicksIn;
			int ticksStay = MConf.get().territoryInfoTitlesTicksStay;
			int ticksOut = MConf.get().territoryInfoTitleTicksOut;
			MixinTitle.get().sendTitleMessage(player, ticksIn, ticksStay, ticksOut, titleMain, titleSub);
		}
		else
		{
			String message = parseTerritoryInfo(MConf.get().territoryInfoChat, mplayer, factionTo);
			player.sendMessage(message);
		}
	}
	
	private static String parseTerritoryInfo(String string, MPlayer mplayer, Faction faction)
	{		
		string = Txt.parse(string);
		string = string.replace("{name}", faction.getName());
		string = string.replace("{relcolor}", faction.getColorTo(mplayer).toString());
		string = string.replace("{desc}", faction.getDescriptionDesc());
		
		return string;
	}
	
	// -------------------------------------------- //
	// MOVE CHUNK: TRY AUTO CLAIM
	// -------------------------------------------- //

	private static void tryAutoClaim(MPlayer mplayer, PS chunkTo)
	{
		// If the player is auto claiming ...
		Faction autoClaimFaction = mplayer.getAutoClaimFaction();
		if (autoClaimFaction == null) return;

		// ... try claim.
		mplayer.tryClaim(autoClaimFaction, Collections.singletonList(chunkTo));
	}
}
