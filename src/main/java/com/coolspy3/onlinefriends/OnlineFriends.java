package com.coolspy3.onlinefriends;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.coolspy3.hypixelapi.APIConfig;
import com.coolspy3.util.ModUtil;
import com.coolspy3.util.ServerJoinEvent;

import me.kbrewster.exceptions.APIException;
import me.kbrewster.mojangapi.MojangAPI;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.FriendsReply;
import net.hypixel.api.reply.FriendsReply.FriendShip;
import net.hypixel.api.reply.StatusReply.Session;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("onlinefriends")
public class OnlineFriends {
    
    public OnlineFriends() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ListFriendsCommand());
    }

    @SubscribeEvent
    public void onServerJoined(ServerJoinEvent event) {
        list();
    }

    public static void list() {
        try {
            String apiKey = APIConfig.requireAPI();
            if(apiKey != null) {
                ModUtil.executeAsync(() -> {
                    HypixelAPI api = new HypixelAPI(UUID.fromString(apiKey));
                    UUID playerUUID = Minecraft.getInstance().player.getUUID();
                    FriendsReply friends = api.getFriends(playerUUID).join();
                    ArrayList<String> onlineFriends = new ArrayList<>();
                    for(FriendShip friendship: friends.getFriendShips()) {
                        UUID other = friendship.getUuidSender().equals(playerUUID) ? friendship.getUuidReceiver() : friendship.getUuidSender();
                        Session status = api.getStatus(other).join().getSession();
                        if(status.isOnline()) {
                            try {
                                onlineFriends.add(TextFormatting.YELLOW + MojangAPI.getName(other) + " - " + status.getGameType().getName() + "_" + status.getMode() + "_" + status.getMap());
                            } catch(APIException | IOException e) {
                                onlineFriends.add(TextFormatting.YELLOW + other.toString());
                            }
                        }
                    }
                    ModUtil.sendMessage(TextFormatting.AQUA + "Online Friends:");
                    if(onlineFriends.isEmpty()) {
                        ModUtil.sendMessage(TextFormatting.YELLOW + "<None>");
                    } else {
                        for(String friend: onlineFriends) {
                            ModUtil.sendMessage(friend);
                        }
                    }
                });
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
