package com.minciallo.dustbin.registry;

import com.minciallo.dustbin.storage.DustbinKind;
import com.minciallo.dustbin.storage.DustbinStorage;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;

public class ModCommands {
	// 26.2 中 CommandSourceStack 已移除旧的 hasPermission(int) 方法，改用 PermissionSet。
	// 单人世界（集成服务端）的玩家源不含 COMMANDS_ADMIN 权限原子，会导致整条指令树在
	// 命令解析时被剪枝，Brigadier 随之反馈"错误的命令参数"。因此这里放宽为：
	//   - 单机模式（isSingleplayer）直接放行（房主天然具备管理权限）；
	//   - 多人服务器才要求显式的 COMMANDS_ADMIN 权限。
	//
	// 注意：`.requires` 谓词会在服务端向客户端同步命令树（ClientboundCommandsPacket）时
	// 被逐一求值，此刻传入的 CommandSourceStack 是临时构造的，其 server 字段为 null。
	// 若直接调用 getServer().isSingleplayer() 会抛 NPE，导致玩家放置失败并报"无效的玩家数据"。
	// 因此必须对 getServer() 判空：为 null 时视为放行（仅影响客户端命令可见性，
	// 真正的权限校验在命令实际执行时由下面 isAdmin 的运行时分支再次把关）。
	private static boolean isAdmin(CommandSourceStack source) {
		MinecraftServer server = source.getServer();
		if (server == null) {
			// 命令树同步阶段：无 server 上下文，先放行以让命令在客户端可见。
			return true;
		}
		return server.isSingleplayer()
				|| source.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, selection) -> {
			dispatcher.register(Commands.literal("dustbin")
					// 不带参数 / all -> 清空全部桶（逐桶报告）
					// 带种类            -> 只清那一种
					.then(Commands.literal("clear")
							.requires(ModCommands::isAdmin)
							.executes(context -> clearAll(context.getSource()))
							.then(Commands.literal("all")
									.executes(context -> clearAll(context.getSource())))
							// 主名与显示名一致，注册名派生的旧名作为别名保留：
							//   NORMAL 的注册名是 dustbin:dustbin，TOOL 的是 dustbin:tool_dustbin。
							.then(clearKind("other", DustbinKind.NORMAL))
							.then(clearKind("normal", DustbinKind.NORMAL))
							.then(clearKind("kitchen", DustbinKind.KITCHEN))
							.then(clearKind("equipment", DustbinKind.TOOL))
							.then(clearKind("tool", DustbinKind.TOOL))
							.then(clearKind("mineral", DustbinKind.MINERAL)))
					// 进桶时间各类共用，语义与 1.0.0 一致
					.then(Commands.literal("settime")
							.requires(ModCommands::isAdmin)
							.then(Commands.argument("minutes", IntegerArgumentType.integer(
									DustbinStorage.MIN_COLLECTION_TICKS / (60 * 20),
									DustbinStorage.MAX_COLLECTION_TICKS / (60 * 20)))
									.executes(context -> setTime(context.getSource(),
											IntegerArgumentType.getInteger(context, "minutes"))))));
		});
	}

	/** {@code /dustbin clear <name>} 的一个子节点：只清空 {@code kind} 这一种桶。 */
	private static LiteralArgumentBuilder<CommandSourceStack> clearKind(String name, DustbinKind kind) {
		return Commands.literal(name).executes(context -> clear(context.getSource(), kind));
	}

	/**
	 * 清空全部种类的桶，并<b>逐桶报告</b>各自清掉了多少组
	 * （而不是只给一个总数 —— 这样能一眼看出哪个桶里有东西）。
	 */
	private static int clearAll(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		DustbinStorage storage = DustbinStorage.get(level);
		int total = 0;
		for (DustbinKind kind : DustbinKind.values()) {
			int cleared = storage.clearAll(kind);
			total += cleared;
			report(source, kind, cleared);
		}
		int sum = total;
		source.sendSuccess(() -> Component.translatable("commands.dustbin.cleared_total", sum), true);
		return total;
	}

	/** 清空指定种类的桶。 */
	private static int clear(CommandSourceStack source, DustbinKind kind) {
		int cleared = DustbinStorage.get(source.getLevel()).clearAll(kind);
		report(source, kind, cleared);
		return cleared;
	}

	private static void report(CommandSourceStack source, DustbinKind kind, int cleared) {
		source.sendSuccess(() -> Component.translatable("commands.dustbin.cleared_kind",
				Component.translatable(kind.blockTranslationKey()), cleared), true);
	}

	private static int setTime(CommandSourceStack source, int minutes) {
		ServerLevel level = source.getLevel();
		DustbinStorage storage = DustbinStorage.get(level);
		storage.setCollectionTicks(minutes * 60 * 20);
		source.sendSuccess(() -> Component.translatable("commands.dustbin.settime", minutes), true);
		return (int) storage.getCollectionTicks();
	}
}
