package me.arasple.mc.trmenu.util

import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.locale.TLocale
import me.arasple.mc.trmenu.TrMenu
import me.arasple.mc.trmenu.api.Extends.replaceWithArguments
import me.clip.placeholderapi.PlaceholderAPI
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/5/30 11:54
 */
object Msger {

    private var debug = false

    fun init() {
        debug = TrMenu.SETTINGS.getBoolean("Options.Debug", false)
    }

    @TFunction.Cancel
    fun cancel() {
        TrMenu.SETTINGS.also {
            it.set("Options.Debug", debug)
            it.saveToFile()
        }
    }

    fun debug(): Boolean {
        this.debug = !this.debug
        return this.debug
    }

    fun debug(isDebug: Boolean) {
        this.debug = isDebug
    }

    fun debug(node: String, vararg args: Any) = debug(Bukkit.getConsoleSender(), node, *args)

    fun debug(sender: CommandSender, node: String, vararg args: Any) = debug(sender, node, sender is Player, *args)

    fun debug(sender: CommandSender, node: String, actionBar: Boolean, vararg args: Any) {
        if (((sender is ConsoleCommandSender) && debug) || (sender is Player && sender.hasMetadata("TrMenu:Debug"))) {
            val msg = TLocale.asString("DEBUG.$node", *args)
            if (actionBar && sender is Player) TLocale.Display.sendActionBar(sender, msg)
            else sender.sendMessage(msg)
        }
    }

    fun replace(player: Player, string: String?): String {
        return replaceWithPlaceholders(player, player.replaceWithArguments(color(string) ?: ""))
    }

    fun replace(player: Player, strings: List<String>): List<String> {
        return strings.map { replace(player, it) }
    }

    private fun replaceWithPlaceholders(player: Player, string: String): String = PlaceholderAPI.setPlaceholders(player, string)

    private fun replaceWithPlaceholders(player: Player, strings: List<String>): List<String> = strings.map { replaceWithPlaceholders(player, it) }

    fun replaceWithBracketPlaceholders(player: Player, string: String): String = PlaceholderAPI.setBracketPlaceholders(player, player.replaceWithArguments(string))

    fun replaceWithBracketPlaceholders(player: Player, strings: List<String>) = strings.map { replaceWithBracketPlaceholders(player, it) }

    @Suppress("DEPRECATION")
    fun containsPlaceholders(string: String?): Boolean = PlaceholderAPI.containsPlaceholders(string) || PlaceholderAPI.containsBracketPlaceholders(string) || (string != null && string.contains("{") && string.contains("}"))

    fun printErrors(node: String, throwable: Throwable, vararg args: String) = TLocale.sendToConsole("ERRORS.$node", *args, throwable.message, throwable.stackTrace.filter { it.toString().contains("me.arasple.mc.trmenu") }.map { it.toString() + "\n" })

    fun printErrors(node: String, vararg args: String) = TLocale.sendToConsole("ERRORS.$node", *args)

    fun color(string: String?): String? = if (string.isNullOrBlank()) string else ChatColor.translateAlternateColorCodes('&', Colors.translate(string))

}