package org.koekepan.herobrineproxy.packet.behaviours;

import org.koekepan.herobrineproxy.behaviour.BehaviourHandler;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.packet.PacketHandler;
import org.koekepan.herobrineproxy.packet.PacketSession;
import org.koekepan.herobrineproxy.packet.behaviours.*;
import org.koekepan.herobrineproxy.packet.behaviours.client.ClientHandshakePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.client.ClientLoginStartPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerJoinGamePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerPluginMessagePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.login.ServerLoginSuccessPacketBehaviour;

import org.koekepan.herobrineproxy.session.ProxySessionV2;

import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientEnchantItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSpectatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.*;
import com.github.steveice10.mc.protocol.packet.login.client.EncryptionResponsePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.*;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.mc.protocol.packet.status.server.*;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

@SuppressWarnings("unused")
public class PacketBehaviours extends BehaviourHandler<Packet> {
	
	private ProxySessionV2 proxySession;
	private ForwardPacketBehaviour serverForwarder;
	private ForwardPacketBehaviour clientForwarder;

	public PacketBehaviours(ProxySessionV2 proxySession) {
		this.proxySession = proxySession;	
		registerDefaultBehaviours();
	}
	
	public void registerDefaultBehaviours() {
		clearBehaviours();
		
		// server packets
		
		// entity
	/*	registerBehaviour(ServerSpawnPlayerPacket.class, new ServerSpawnPlayerPacketBehaviour(state));									// 0x0C Spawn Player
		registerBehaviour(ServerSpawnMobPacket.class, new ServerSpawnMobPacketBehaviour(state));										// 0x0F Spawn Mob
		registerBehaviour(ServerEntityDestroyPacket.class, new ServerDestroyEntitiesPacketBehaviour(state));							// 0x13 Destroy Entities
		registerBehaviour(ServerEntityPositionPacket.class, new ServerEntityPositionPacketBehaviour(state));							// 0x15 Entity Relative Move
		registerBehaviour(ServerEntityRotationPacket.class, new ServerEntityRotationPacketBehaviour(state));							// 0x16 Entity Look
		registerBehaviour(ServerEntityPositionRotationPacket.class, new ServerEntityPositionRotationPacketBehaviour(state));			// 0x17 Entity Look And Relative Move
		registerBehaviour(ServerEntityTeleportPacket.class, new ServerEntityTeleportPacketBehaviour(state));							// 0x18 Entity Teleport
			
		// player
		registerBehaviour(ServerJoinGamePacket.class, new ServerJoinGamePacketBehaviour(state));										// 0x01 Join Game
		registerBehaviour(ServerPlayerPositionRotationPacket.class, new ServerPlayerPositionRotationPacketBehaviour(session, state));	// 0x08 Player Position And Look
		registerBehaviour(ServerPlayerAbilitiesPacket.class, new ServerPlayerAbilitiesPacketBehaviour(state));							// 0x39 Player Abilities
		registerBehaviour(LoginSuccessPacket.class, new ServerLoginSuccessPacketBehaviour(state));										// 0x02 Login Success (Login phase)
		registerBehaviour(LoginDisconnectPacket.class, new ServerLoginDisconnectPacketBehaviour(clientEmulator));						// 0x00 Login Disconnect (Login phase)
		registerBehaviour(ServerDisconnectPacket.class, new ServerDisconnectPacketBehaviour(clientEmulator));						// 0x00 Login Disconnect (Login phase)
		registerBehaviour(ServerPluginMessagePacket.class, new ServerPluginMessagePacketBehaviour(clientEmulator));						// 0x00 Login Disconnect (Login phase)
		
		
		// world
		registerBehaviour(ServerUpdateTimePacket.class, new ServerUpdateTimePacketBehaviour(state));									// 0x03 Time Update
		registerBehaviour(ServerChunkDataPacket.class, new ServerChunkDataPacketBehaviour(state));										// 0x21 Chunk Data
		registerBehaviour(ServerMultiBlockChangePacket.class, new ServerMultiBlockChangePacketBehaviour(state));						// 0x22 Multi Block Change
		registerBehaviour(ServerBlockChangePacket.class, new ServerBlockChangePacketBehaviour(state));									// 0x23 Block Change
	//	registerBehaviour(ServerMultiChunkDataPacket.class, new ServerMultiChunkDataPacketBehaviour(state));							// 0x26 Map Chunk Bulk
		registerBehaviour(ServerExplosionPacket.class, new ServerExplosionPacketBehaviour(state));										// 0x27 Explosion
		*/
		
		// client packet
		//registerBehaviour(HandshakePacket.class, new ClientHandshakePacketBehaviour(proxySession));										// 0x06 Player Position And Look 
		//registerBehaviour(LoginStartPacket.class, new ClientLoginStartPacketBehaviour(proxySession));												// 0x01 Login Start 
	}
	
	
	public void registerForwardingBehaviour() {
		Session clientSession = proxySession.getClient();
		Session serverSession = proxySession.getServer();
		registerClientForwardingBehaviour(clientSession);
		registerServerForwardingBehaviour(serverSession);
	}
	
	
	public void registerClientForwardingBehaviour(Session session) {
	/*	clientForwarder = new ForwardPacketBehaviour(proxySession, false);
		registerBehaviour(LoginDisconnectPacket.class, clientForwarder);
		registerBehaviour(EncryptionRequestPacket.class, clientForwarder);
		registerBehaviour(LoginSuccessPacket.class, new ServerLoginSuccessPacketBehaviour(proxySession));
		registerBehaviour(LoginSetCompressionPacket.class, clientForwarder);

		//registerBehaviour( LoginStartPacket.class, clientForwarder);
		//registerBehaviour( EncryptionResponsePacket.class, clientForwarder);


		registerBehaviour(ServerSpawnObjectPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnExpOrbPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnGlobalEntityPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnMobPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPaintingPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPlayerPacket.class, clientForwarder);
		registerBehaviour(ServerEntityAnimationPacket.class, clientForwarder);
		registerBehaviour(ServerStatisticsPacket.class, clientForwarder);
		registerBehaviour(ServerBlockBreakAnimPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateTileEntityPacket.class, clientForwarder);
		registerBehaviour(ServerBlockValuePacket.class, clientForwarder);
		registerBehaviour(ServerBlockChangePacket.class, clientForwarder);
		registerBehaviour(ServerBossBarPacket.class, clientForwarder);
		registerBehaviour(ServerDifficultyPacket.class, clientForwarder);
		registerBehaviour(ServerTabCompletePacket.class, clientForwarder);
		registerBehaviour(ServerChatPacket.class, clientForwarder);
		registerBehaviour(ServerMultiBlockChangePacket.class, clientForwarder);
		registerBehaviour(ServerConfirmTransactionPacket.class, clientForwarder);
		registerBehaviour(ServerCloseWindowPacket.class, clientForwarder);
		registerBehaviour(ServerOpenWindowPacket.class, clientForwarder);
		registerBehaviour(ServerWindowItemsPacket.class, clientForwarder);
		registerBehaviour(ServerWindowPropertyPacket.class, clientForwarder);
		registerBehaviour(ServerSetSlotPacket.class, clientForwarder);
		registerBehaviour(ServerSetCooldownPacket.class, clientForwarder);
		registerBehaviour(ServerPlaySoundPacket.class, clientForwarder);
		registerBehaviour(ServerDisconnectPacket.class, clientForwarder);
		registerBehaviour(ServerEntityStatusPacket.class, clientForwarder);
		registerBehaviour(ServerExplosionPacket.class, clientForwarder);
		registerBehaviour(ServerUnloadChunkPacket.class, clientForwarder);
		registerBehaviour(ServerNotifyClientPacket.class, clientForwarder);
		registerBehaviour(ServerKeepAlivePacket.class, clientForwarder);
		registerBehaviour(ServerChunkDataPacket.class, clientForwarder);
		registerBehaviour(ServerPlayEffectPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnParticlePacket.class, clientForwarder);
		registerBehaviour(ServerJoinGamePacket.class, new ServerJoinGamePacketBehaviour(proxySession));
		registerBehaviour(ServerMapDataPacket.class, clientForwarder);
		registerBehaviour(ServerEntityPositionPacket.class, clientForwarder);
		registerBehaviour(ServerEntityPositionRotationPacket.class, clientForwarder);
		registerBehaviour(ServerEntityRotationPacket.class, clientForwarder);
		registerBehaviour(ServerEntityMovementPacket.class, clientForwarder);
		registerBehaviour(ServerVehicleMovePacket.class, clientForwarder);
		registerBehaviour(ServerOpenTileEntityEditorPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerAbilitiesPacket.class, clientForwarder);
		registerBehaviour(ServerCombatPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListEntryPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerPositionRotationPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerUseBedPacket.class, clientForwarder);
		registerBehaviour(ServerEntityDestroyPacket.class, clientForwarder);
		registerBehaviour(ServerEntityRemoveEffectPacket.class, clientForwarder);
		registerBehaviour(ServerResourcePackSendPacket.class, clientForwarder);
		registerBehaviour(ServerRespawnPacket.class, clientForwarder);
		registerBehaviour(ServerEntityHeadLookPacket.class, clientForwarder);
		registerBehaviour(ServerWorldBorderPacket.class, clientForwarder);
		registerBehaviour(ServerSwitchCameraPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerChangeHeldItemPacket.class, clientForwarder);
		registerBehaviour(ServerDisplayScoreboardPacket.class, clientForwarder);
		registerBehaviour(ServerEntityMetadataPacket.class, clientForwarder);
		registerBehaviour(ServerEntityAttachPacket.class, clientForwarder);
		registerBehaviour(ServerEntityVelocityPacket.class, clientForwarder);
		registerBehaviour(ServerEntityEquipmentPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerSetExperiencePacket.class, clientForwarder);
		registerBehaviour(ServerPlayerHealthPacket.class, clientForwarder);
		registerBehaviour(ServerScoreboardObjectivePacket.class, clientForwarder);
		registerBehaviour(ServerEntitySetPassengersPacket.class, clientForwarder);
		registerBehaviour(ServerTeamPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateScorePacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPositionPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateTimePacket.class, clientForwarder);
		registerBehaviour(ServerTitlePacket.class, clientForwarder);
		registerBehaviour(ServerPlayBuiltinSoundPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListDataPacket.class, clientForwarder);
		registerBehaviour(ServerEntityCollectItemPacket.class, clientForwarder);
		registerBehaviour(ServerEntityTeleportPacket.class, clientForwarder);
		registerBehaviour(ServerEntityPropertiesPacket.class, clientForwarder);
		registerBehaviour(ServerEntityEffectPacket.class, clientForwarder);
		
		registerBehaviour(StatusResponsePacket.class, clientForwarder);
		registerBehaviour(StatusPongPacket.class, clientForwarder);
		
		registerBehaviour(ServerPluginMessagePacket.class, new ServerPluginMessagePacketBehaviour(proxySession));*/
	}
	
	
	public void registerServerForwardingBehaviour(Session session) {
	/*	serverForwarder = new ForwardPacketBehaviour(proxySession, true);
		
		//registerBehaviour(LoginStartPacket.class, serverForwarder);
		registerBehaviour(EncryptionResponsePacket.class, serverForwarder);

		registerBehaviour(ClientTeleportConfirmPacket.class, serverForwarder);
		registerBehaviour(ClientTabCompletePacket.class, serverForwarder);
		registerBehaviour(ClientChatPacket.class, serverForwarder);
		registerBehaviour(ClientRequestPacket.class, serverForwarder);
		registerBehaviour(ClientSettingsPacket.class, serverForwarder);
		registerBehaviour(ClientConfirmTransactionPacket.class, serverForwarder);
		registerBehaviour(ClientEnchantItemPacket.class, serverForwarder);
		registerBehaviour(ClientWindowActionPacket.class, serverForwarder);
		registerBehaviour(ClientCloseWindowPacket.class, serverForwarder);
		registerBehaviour(ClientPluginMessagePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerInteractEntityPacket.class, serverForwarder);
		registerBehaviour(ClientKeepAlivePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerPositionPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerPositionRotationPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerRotationPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerMovementPacket.class, serverForwarder);
		registerBehaviour(ClientVehicleMovePacket.class, serverForwarder);
		registerBehaviour(ClientSteerBoatPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerAbilitiesPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerActionPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerStatePacket.class, serverForwarder);
		registerBehaviour(ClientSteerVehiclePacket.class, serverForwarder);
		registerBehaviour(ClientResourcePackStatusPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerChangeHeldItemPacket.class, serverForwarder);
		registerBehaviour(ClientCreativeInventoryActionPacket.class, serverForwarder);
		registerBehaviour(ClientUpdateSignPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerSwingArmPacket.class, serverForwarder);
		registerBehaviour(ClientSpectatePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerPlaceBlockPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerUseItemPacket.class, serverForwarder);
		
		registerBehaviour(StatusQueryPacket.class, serverForwarder);
		registerBehaviour(StatusPingPacket.class, serverForwarder);*/
	}
}
