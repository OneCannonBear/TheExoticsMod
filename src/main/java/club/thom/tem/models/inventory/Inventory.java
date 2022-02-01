package club.thom.tem.models.inventory;

import club.thom.tem.models.inventory.item.ArmourPieceData;
import club.thom.tem.models.inventory.item.InventoryItemData;
import club.thom.tem.models.inventory.item.PetData;
import club.thom.tem.models.inventory.item.PetSkinData;
import club.thom.tem.models.messages.ClientMessages.InventoryItem;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Inventory {
    // Can be enderchest, storage, pets menu, actual inventory, etc
    // Contains InventoryItemData array of items
    private final NBTTagCompound data;
    public Inventory(String base64EncodedNBT) {
        data = processNbtString(base64EncodedNBT);
    }

    private static final Logger logger = LogManager.getLogger(Inventory.class);

    public static NBTTagCompound processNbtString(String base64EncodedNBT) {
        NBTTagCompound nbtData;
        try {
            nbtData = CompressedStreamTools.readCompressed(Base64.getDecoder().wrap(
                    new ByteArrayInputStream(base64EncodedNBT.getBytes(StandardCharsets.UTF_8))));
        } catch (IOException e) {
            logger.error("Error while parsing nbt", e);
            return null;
        }
        return nbtData;
    }

    public static List<InventoryItemData> nbtToItems(NBTTagCompound data) {
        ArrayList<InventoryItemData> items = new ArrayList<>();
        NBTTagList list = data.getTagList("i", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            if (ArmourPieceData.isValidItem(item)) {
                items.add(new ArmourPieceData(item));
            } else if (PetData.isValidItem(item)) {
                items.add(new PetData(item));
            } else if (PetSkinData.isValidItem(item)) {
                items.add(new PetSkinData(item));
            }
        }
        return items;
    }

    public List<InventoryItem> getItems() {
        List<InventoryItem> items = new ArrayList<>();
        for (InventoryItemData itemData : nbtToItems(data)) {
            items.add(itemData.toInventoryItem());
        }
        return items;
    }
}
