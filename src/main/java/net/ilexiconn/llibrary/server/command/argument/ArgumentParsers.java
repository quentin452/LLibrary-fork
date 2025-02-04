package net.ilexiconn.llibrary.server.command.argument;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

/**
 * @author iLexiconn
 * @since 1.0.0
 */
public enum ArgumentParsers implements IArgumentParser {
    INTEGER {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return CommandBase.parseInt(sender, argument);
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return Collections.emptyList();
        }
    },

    BOOLEAN {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return CommandBase.parseBoolean(sender, argument);
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "true", "false");
        }
    },

    STRING {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return argument;
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return Collections.emptyList();
        }
    },

    FLOAT {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return (float) CommandBase.parseDouble(sender, argument);
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return Collections.emptyList();
        }
    },

    DOUBLE {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return CommandBase.parseDouble(sender, argument);
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return Collections.emptyList();
        }
    },

    PLAYER {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return CommandBase.getPlayer(sender, argument);
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }
    },

    ITEM_STACK {
        @Override
        public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
            return new ItemStack(CommandBase.getItemByText(sender, argument));
        }

        @Override
        public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args) {
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, Item.itemRegistry.getKeys());
        }
    };

    public static <T> IArgumentParser<T> getBuiltinParser(Class<T> type) {
        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return INTEGER;
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return BOOLEAN;
        } else if (String.class.isAssignableFrom(type)) {
            return STRING;
        } else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return FLOAT;
        } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return DOUBLE;
        } else if (EntityPlayer.class.isAssignableFrom(type)) {
            return PLAYER;
        } else if (ItemStack.class.isAssignableFrom(type)) {
            return ITEM_STACK;
        } else {
            return null;
        }
    }
}
