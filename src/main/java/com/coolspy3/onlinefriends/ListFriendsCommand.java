package com.coolspy3.onlinefriends;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ListFriendsCommand {

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        if(event.getMessage().matches("/of( .*)?")) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
            OnlineFriends.list();
        }
    }

}
