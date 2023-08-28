package com.beauver.swagsmp.enums;

import org.bukkit.Material;

public enum ItemModels {

    LIGHTSABER_BLUE("Lightsaber blue", 1, 20, true, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    LIGHTSABER_RED("Lightsaber red", 2, 20, true, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    LIGHTSABER_PURPLE("Lightsaber purple", 3, 20, true, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    LIGHTSABER_GREEN("Lightsaber green", 4, 20, true, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    LIGHTSABER_ORANGE("Lightsaber orange", 5, 20, true, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    HAT("Hat", 1, 20, true, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET),
    SEASON4BADGE("Season 4 badge", 1, -1, false, Material.PAPER),
    SWAG_COIN("Swag Coin", 100, -1, false, Material.PAPER),
    //testing
    FLAG_BI("Bisexual flag", 2, 10, true, Material.PAPER),
    FLAG_GAY("Gay flag", 3, 10, true, Material.PAPER),
    FLAG_LESBIAN("Lesbian flag", 4, 10, true, Material.PAPER),
    FLAG_PRIDE("Rainbow pride flag", 5,10, true, Material.PAPER),
    FLAG_GENDERFLUID("Genderfluid flag", 6,10, true, Material.PAPER),
    FLAG_NONBINARY("Non binary flag", 7,10, true, Material.PAPER),
    FLAG_ASEXUAL("Asexual flag", 8,10, true, Material.PAPER),
    FLAG_AROMANTIC("Aromantic flag", 9,10, true, Material.PAPER),
    FLAG_PAN("Pansexual flag", 10,10, true, Material.PAPER),
    TEST_ITEM_1("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_2("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_3("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_4("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_5("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_6("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_7("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_8("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_9("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_10("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_11("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_12("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_13("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_14("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_15("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_16("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_17("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_18("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_19("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_20("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_21("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_22("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_23("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_24("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_25("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_26("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_27("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_28("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_29("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_30("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_31("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_32("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_33("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_34("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_35("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_36("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_37("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_38("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_39("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_40("test item", 1,-1, false, Material.PUMPKIN),
    TEST_ITEM_41("test item", 1,-1, false, Material.PUMPKIN),

    // Add more items here
    ;

    private final String textureName;
    private final Material[] validMaterials;
    private final int customModelDataId;
    private final int swagCoinCost;
    private final boolean canBeTransformed;

    ItemModels(String textureName, int customModelDataId, int swagCoinCost, boolean canBeTransformed, Material... validMaterials) {
        this.textureName = textureName;
        this.validMaterials = validMaterials;
        this.customModelDataId = customModelDataId;
        this.swagCoinCost = swagCoinCost;
        this.canBeTransformed = canBeTransformed;
    }

    public String getTextureName() {
        return textureName;
    }

    public int getCustomModelDataId() {
        return customModelDataId;
    }

    public int getSwagCoinCost() {
        return swagCoinCost;
    }

    public boolean getTransformable(){
        return canBeTransformed;
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
