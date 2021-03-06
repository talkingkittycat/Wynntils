/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.core.events.custom;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.Event;


/**
 * Represents events that are triggered when a packet is sent to the client
 * all of them are cancelable (you can avoid it to reach the client processor)
 *
 */
public class PacketEvent extends Event {

    /**
     * Triggered when the client receives a {@link ResourcePackReceived} packet from the server
     *
     */
    public static class ResourcePackReceived extends Event {
        SPacketResourcePackSend packet;
        NetworkManager networkManager;

        public ResourcePackReceived(SPacketResourcePackSend packet, NetworkManager networkManager) {
            this.packet = packet;
            this.networkManager = networkManager;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketResourcePackSend getPacket() {
            return packet;
        }

        public NetworkManager getNetworkManager() {
            return networkManager;
        }
    }

    /**
     * Triggered when the client receives a {@link SPacketOpenWindow} packet from the server
     *
     */
    public static class InventoryReceived extends Event {

        SPacketOpenWindow packet;
        NetHandlerPlayClient playClient;

        public InventoryReceived(SPacketOpenWindow packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketOpenWindow getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

    /**
     * Triggered when the client receives a {@link SPacketWindowItems} packet from the server
     *
     */
    public static class InventoryItemsReceived extends Event {

        SPacketWindowItems packet;
        NetHandlerPlayClient playClient;

        public InventoryItemsReceived(SPacketWindowItems packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketWindowItems getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

    /**
     * Triggered when the client receives a {@link SPacketSpawnObject} packet from the server
     *
     */
    public static class SpawnObject extends Event {

        SPacketSpawnObject packet;
        NetHandlerPlayClient playClient;

        public SpawnObject(SPacketSpawnObject packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketSpawnObject getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

    }

    /**
     * Triggered when the client receives a {@link SPacketPlayerListItem} packet from the server
     *
     */
    public static class TabListChangeEvent extends Event {
        SPacketPlayerListItem packet;
        NetHandlerPlayClient playClient;

        public TabListChangeEvent(SPacketPlayerListItem packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable() {
            return true;
        }

        public SPacketPlayerListItem getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

}
