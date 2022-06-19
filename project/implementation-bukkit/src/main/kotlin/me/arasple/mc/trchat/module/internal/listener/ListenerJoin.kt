package me.arasple.mc.trchat.module.internal.listener

import me.arasple.mc.trchat.module.conf.BukkitChannelManager
import me.arasple.mc.trchat.module.display.channel.Channel
import me.arasple.mc.trchat.module.internal.proxy.BukkitProxyManager
import me.arasple.mc.trchat.module.internal.service.Updater
import me.arasple.mc.trchat.util.Internal
import me.arasple.mc.trchat.util.getSession
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendLang

/**
 * @author wlys
 * @since 2021/12/11 23:19
 */
@Internal
@PlatformSide([Platform.BUKKIT])
object ListenerJoin {

    @SubscribeEvent(EventPriority.HIGHEST)
    fun e(e: PlayerJoinEvent) {
        val player = e.player

        if (!BukkitChannelManager.loadedProxyChannels) {
            BukkitProxyManager.sendTrChatMessage(player, "FetchProxyChannels")
            BukkitChannelManager.loadedProxyChannels = true
        }

        Channel.channels.values.filter { it.settings.autoJoin }.forEach {
            if (it.settings.joinPermission == null || player.hasPermission(it.settings.joinPermission)) {
                it.listeners.add(player.uniqueId)
            }
        }
        player.getSession()

        if (player.hasPermission("trchat.admin") && Updater.latest_Version > Updater.current_version && !Updater.notified.contains(player.uniqueId)) {
            player.sendLang("Plugin-Updater-Header", Updater.current_version, Updater.latest_Version)
            player.sendMessage(Updater.information)
            player.sendLang("Plugin-Updater-Footer")
            Updater.notified.add(player.uniqueId)
        }
    }
}