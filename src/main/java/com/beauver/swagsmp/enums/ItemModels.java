package com.beauver.swagsmp.enums;

import org.bukkit.Material;

public enum ItemModels {

    LIGHTSABER("lightsaber", 1, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    LIGHTSABER_RED("lightsaber", 2, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    // Add more items here
    ;

    private final String textureName;
    private final Material[] validMaterials;
    private final int customModelDataId;

    ItemModels(String textureName, int customModelDataId, Material... validMaterials) {
        this.textureName = textureName;
        this.validMaterials = validMaterials;
        this.customModelDataId = customModelDataId;
    }

    public String getTextureName() {
        return textureName;
    }

    public int getCustomModelDataId() {
        return customModelDataId;
    }

    public boolean isValidMaterial(Material material) {
        for (Material validMaterial : validMaterials) {
            if (material == validMaterial) {
                return true;
            }
        }
        return false;
    }

    public Material[] getValidMaterials() {
        return validMaterials;
    }

}
