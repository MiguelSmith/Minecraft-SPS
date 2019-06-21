package org.koekepan.herobrineproxy.packet.behaviours;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.BehaviourHandler;
import org.koekepan.herobrineproxy.packet.behaviours.login.LoginSetCompressionPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.login.MigrateLoginSuccessPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.login.ServerLoginSuccessPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.login.ServerPlayerPositionPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.MigrateJoinGamePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityDestroyPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityEffectPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityHeadLookPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityMetadataPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityMovementPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityPositionPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityPositionRotationPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityRotationPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityStatusPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityTeleportPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityVelocityPacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerJoinGamePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerPlayBuiltinSoundPacketBehaviour;
//import org.koekepan.herobrineproxy.packet.behaviours.server.ServerJoinGamePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerPluginMessagePacketBehaviour;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerSpawnMobPacketBehaviour;
//import org.koekepan.herobrineproxy.packet.behaviours.login.ServerLoginSuccessPacketBehaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.IServerSession;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.*;
import com.github.steveice10.mc.protocol.packet.login.server.*;
import com.github.steveice10.mc.protocol.packet.status.server.*;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerSessionPacketBehaviours extends BehaviourHandler<Packet> {

	private IProxySessionNew proxySession;
	private IServerSession serverSession;
	private ForwardPacketBehaviour clientForwarder;

	public ServerSessionPacketBehaviours(IProxySessionNew proxySession, IServerSession serverSession) {	
		this.proxySession = proxySession;
		this.serverSession = serverSession;
	}
	

	/*public void registerDefaultBehaviours() {
		clearBehaviours();
		this.registerForwardingBehaviour();
	}*/
	
	public void registerSPSBehaviour(SPSEntityTracker entityTracker) {
	//	ConsoleIO.println("ServerSessionPacketBehaviours::registerForwardingBehaviours => Clearing behaviours before registering new behaviours");
		clearBehaviours();
		clientForwarder = new ForwardPacketBehaviour(proxySession, false);
		registerBehaviour(LoginDisconnectPacket.class, clientForwarder);
		registerBehaviour(EncryptionRequestPacket.class, clientForwarder);
		registerBehaviour(LoginSuccessPacket.class, new ServerLoginSuccessPacketBehaviour(proxySession));
		registerBehaviour(LoginSetCompressionPacket.class, clientForwarder);

		registerBehaviour(ServerSpawnObjectPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnExpOrbPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnGlobalEntityPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPaintingPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPlayerPacket.class, clientForwarder);
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
		registerBehaviour(ServerExplosionPacket.class, clientForwarder);
		registerBehaviour(ServerUnloadChunkPacket.class, clientForwarder);
		registerBehaviour(ServerNotifyClientPacket.class, clientForwarder);
		registerBehaviour(ServerKeepAlivePacket.class, clientForwarder);
		registerBehaviour(ServerChunkDataPacket.class, clientForwarder);
		registerBehaviour(ServerPlayEffectPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnParticlePacket.class, clientForwarder);
		registerBehaviour(ServerJoinGamePacket.class, new ServerJoinGamePacketBehaviour(proxySession, serverSession));
		//registerBehaviour(ServerPlayerPositionRotationPacket.class, clientForwarder);
		registerBehaviour(ServerMapDataPacket.class, clientForwarder);
		registerBehaviour(ServerVehicleMovePacket.class, clientForwarder);
		registerBehaviour(ServerOpenTileEntityEditorPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerAbilitiesPacket.class, clientForwarder);
		registerBehaviour(ServerCombatPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListEntryPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerUseBedPacket.class, clientForwarder);
		registerBehaviour(ServerResourcePackSendPacket.class, clientForwarder);
		registerBehaviour(ServerRespawnPacket.class, clientForwarder);
		registerBehaviour(ServerWorldBorderPacket.class, clientForwarder);
		registerBehaviour(ServerSwitchCameraPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerChangeHeldItemPacket.class, clientForwarder);
		registerBehaviour(ServerDisplayScoreboardPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerPositionRotationPacket.class, new ServerPlayerPositionPacketBehaviour(proxySession));
		//registerBehaviour(ServerPlayerPositionRotationPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerSetExperiencePacket.class, clientForwarder);
		registerBehaviour(ServerPlayerHealthPacket.class, clientForwarder);
		registerBehaviour(ServerScoreboardObjectivePacket.class, clientForwarder);
		registerBehaviour(ServerEntitySetPassengersPacket.class, clientForwarder);
		registerBehaviour(ServerTeamPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateScorePacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPositionPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateTimePacket.class, clientForwarder);
		registerBehaviour(ServerTitlePacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListDataPacket.class, clientForwarder);
		registerBehaviour(ServerEntityAnimationPacket.class, clientForwarder);
		registerBehaviour(ServerEntityRemoveEffectPacket.class, clientForwarder);
		registerBehaviour(ServerEntityAttachPacket.class, clientForwarder);
		registerBehaviour(ServerEntityEquipmentPacket.class, clientForwarder);
		registerBehaviour(ServerEntityCollectItemPacket.class, clientForwarder);
		registerBehaviour(ServerEntityPropertiesPacket.class, clientForwarder);
		
		registerBehaviour(StatusResponsePacket.class, clientForwarder);
		registerBehaviour(StatusPongPacket.class, clientForwarder);
		
		registerBehaviour(ServerPluginMessagePacket.class, new ServerPluginMessagePacketBehaviour(proxySession));
		
		///////////////////////////////////
		// everything to do with entities//
		///////////////////////////////////
		
		registerBehaviour(ServerEntityPositionPacket.class, new ServerEntityPositionPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityPositionPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityPositionRotationPacket.class, new ServerEntityPositionRotationPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityPositionRotationPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityRotationPacket.class, new ServerEntityRotationPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityRotationPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityMovementPacket.class, new ServerEntityMovementPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityMovementPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityStatusPacket.class, new ServerEntityStatusPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityStatusPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityDestroyPacket.class, new ServerEntityDestroyPacketBehaviour(entityTracker));		
		//registerBehaviour(ServerEntityDestroyPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityMetadataPacket.class, new ServerEntityMetadataPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityMetadataPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityVelocityPacket.class, new ServerEntityVelocityPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityVelocityPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityTeleportPacket.class, new ServerEntityTeleportPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityTeleportPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityEffectPacket.class, new ServerEntityEffectPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityEffectPacket.class, clientForwarder);
		
		registerBehaviour(ServerEntityHeadLookPacket.class, new ServerEntityHeadLookPacketBehaviour(entityTracker));
		//registerBehaviour(ServerEntityHeadLookPacket.class, clientForwarder);
		
		registerBehaviour(ServerSpawnMobPacket.class, new ServerSpawnMobPacketBehaviour(entityTracker));
		//registerBehaviour(ServerSpawnMobPacket.class, clientForwarder);
		
		registerBehaviour(ServerPlayBuiltinSoundPacket.class, new ServerPlayBuiltinSoundPacketBehaviour(entityTracker));
		//registerBehaviour(ServerPlayBuiltinSoundPacket.class, clientForwarder);
	}
	
	
	public void registerForwardingBehaviour() {
	//	ConsoleIO.println("ServerSessionPacketBehaviours::registerForwardingBehaviours => Clearing behaviours before registering new behaviours");
		clearBehaviours();
		clientForwarder = new ForwardPacketBehaviour(proxySession, false);
		registerBehaviour(LoginDisconnectPacket.class, clientForwarder);
		registerBehaviour(EncryptionRequestPacket.class, clientForwarder);
		registerBehaviour(LoginSuccessPacket.class, new ServerLoginSuccessPacketBehaviour(proxySession));
		registerBehaviour(LoginSetCompressionPacket.class, clientForwarder);

		registerBehaviour(ServerSpawnObjectPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnExpOrbPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnGlobalEntityPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnMobPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPaintingPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPlayerPacket.class, clientForwarder);
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
		registerBehaviour(ServerExplosionPacket.class, clientForwarder);
		registerBehaviour(ServerUnloadChunkPacket.class, clientForwarder);
		registerBehaviour(ServerNotifyClientPacket.class, clientForwarder);
		registerBehaviour(ServerKeepAlivePacket.class, clientForwarder);
		registerBehaviour(ServerChunkDataPacket.class, clientForwarder);
		registerBehaviour(ServerPlayEffectPacket.class, clientForwarder);
		registerBehaviour(ServerSpawnParticlePacket.class, clientForwarder);
		registerBehaviour(ServerJoinGamePacket.class, new ServerJoinGamePacketBehaviour(proxySession, serverSession));
		//registerBehaviour(ServerPlayerPositionRotationPacket.class, clientForwarder);
		registerBehaviour(ServerMapDataPacket.class, clientForwarder);
		registerBehaviour(ServerVehicleMovePacket.class, clientForwarder);
		registerBehaviour(ServerOpenTileEntityEditorPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerAbilitiesPacket.class, clientForwarder);
		registerBehaviour(ServerCombatPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListEntryPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerUseBedPacket.class, clientForwarder);
		registerBehaviour(ServerResourcePackSendPacket.class, clientForwarder);
		registerBehaviour(ServerRespawnPacket.class, clientForwarder);
		registerBehaviour(ServerEntityHeadLookPacket.class, clientForwarder);
		registerBehaviour(ServerWorldBorderPacket.class, clientForwarder);
		registerBehaviour(ServerSwitchCameraPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerChangeHeldItemPacket.class, clientForwarder);
		registerBehaviour(ServerDisplayScoreboardPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerPositionRotationPacket.class, new ServerPlayerPositionPacketBehaviour(proxySession));
		//registerBehaviour(ServerPlayerPositionRotationPacket.class, clientForwarder);
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
		
		registerBehaviour(StatusResponsePacket.class, clientForwarder);
		registerBehaviour(StatusPongPacket.class, clientForwarder);
		
		registerBehaviour(ServerPluginMessagePacket.class, new ServerPluginMessagePacketBehaviour(proxySession));
		
		registerBehaviour(ServerEntityPositionPacket.class, clientForwarder);
		registerBehaviour(ServerEntityPositionRotationPacket.class, clientForwarder);
		registerBehaviour(ServerEntityRotationPacket.class, clientForwarder);
		registerBehaviour(ServerEntityMovementPacket.class, clientForwarder);
		registerBehaviour(ServerEntityAnimationPacket.class, clientForwarder);
		registerBehaviour(ServerEntityStatusPacket.class, clientForwarder);
		registerBehaviour(ServerEntityDestroyPacket.class, clientForwarder);
		registerBehaviour(ServerEntityRemoveEffectPacket.class, clientForwarder);
		registerBehaviour(ServerEntityMetadataPacket.class, clientForwarder);
		registerBehaviour(ServerEntityAttachPacket.class, clientForwarder);
		registerBehaviour(ServerEntityVelocityPacket.class, clientForwarder);
		registerBehaviour(ServerEntityEquipmentPacket.class, clientForwarder);
		registerBehaviour(ServerEntityCollectItemPacket.class, clientForwarder);
		registerBehaviour(ServerEntityTeleportPacket.class, clientForwarder);
		registerBehaviour(ServerEntityPropertiesPacket.class, clientForwarder);
		registerBehaviour(ServerEntityEffectPacket.class, clientForwarder);
	}
	
	
	public void registerMigrationBehaviour() {
		this.clearBehaviours();
		clientForwarder = new ForwardPacketBehaviour(proxySession, false);
		registerBehaviour(LoginDisconnectPacket.class, clientForwarder);
		registerBehaviour(EncryptionRequestPacket.class, clientForwarder);
		registerBehaviour(LoginSuccessPacket.class, new MigrateLoginSuccessPacketBehaviour(proxySession) );
		registerBehaviour(LoginSetCompressionPacket.class, new LoginSetCompressionPacketBehaviour());

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
		registerBehaviour(ServerJoinGamePacket.class, new MigrateJoinGamePacketBehaviour(proxySession, serverSession));
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
		registerBehaviour(ServerPlayerPositionRotationPacket.class, new ServerPlayerPositionPacketBehaviour(proxySession));
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
		
		registerBehaviour(ServerPluginMessagePacket.class, new ServerPluginMessagePacketBehaviour(proxySession));
	}
}
