package betterwithmods.module.hardcore.crafting.brewing;

import betterwithmods.common.BWMItems;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.library.common.modularity.impl.Feature;
import betterwithmods.library.lib.ReflectionLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;


public class HCBrewing extends Feature {
    private static boolean removeMovementPotions;
    private static boolean waterBreathingAnyFish;
    private static boolean removeWitchPotionDrops;
    private static boolean modPotionCompat;
    private static int potionStackSize;
    private boolean tryChangePotions;

    private static boolean isWitchDropBlacklisted(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.GLOWSTONE_DUST || item == Items.SUGAR || item == Items.SPIDER_EYE || item == Items.GUNPOWDER || item instanceof ItemPotion;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void mobDrops(LivingDropsEvent evt) {
        Entity entity = evt.getEntityLiving();
        List<EntityItem> drops = evt.getDrops();

        if (entity instanceof EntityWitch) {
            Iterator<EntityItem> iterator = drops.iterator();
            EntityItem item = iterator.next();
            ItemStack stack = item.getItem();
            if (stack.getItem() == Items.REDSTONE)
                item.setItem(ItemMaterial.getStack(ItemMaterial.EnumMaterial.WITCH_WART, stack.getCount()));
            else if (removeWitchPotionDrops && isWitchDropBlacklisted(stack))
                iterator.remove();
        } else if (entity instanceof EntitySquid) {
            if (entity.world.rand.nextInt(100) < 10) {
                entity.entityDropItem(ItemMaterial.getStack(ItemMaterial.EnumMaterial.MYSTERY_GLAND), 0);
            }
        }

    }

    @Override
    public String getDescription() {
        return "Modifies and rebalances vanilla brewing recipes and makes potions stack up to 8.";
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        removeMovementPotions = loadProperty("Remove Movement Potions", true).setComment("Removes recipes for Speed and Leaping potions.").get();
        waterBreathingAnyFish = loadProperty("Water Breathing Any Fish", true).setComment("Any fish works for brewing Water Breathing potions.").get();
        removeWitchPotionDrops = loadProperty("Remove Witch Ingredient Drops", true).setComment("Removes redstone and glowstone from witch drops").get();
        modPotionCompat = loadProperty("Modded Potion Compatibility", true).setComment("Similarly modifies non-vanilla potions.").get();
        potionStackSize = loadProperty("Potion Stacksize", 8).setComment("Maximum stacksize of potion items.").get();
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

        //Unfortunately still have to clear the vanilla registry or JEI will think they still exist.
        List<Object> itemConversions = ReflectionHelper.getPrivateValue(PotionHelper.class, null, ReflectionLib.POTIONHELPER_ITEM_CONVERSIONS);
        List<Object> typeConversions = ReflectionHelper.getPrivateValue(PotionHelper.class, null, ReflectionLib.POTIONHELPER_TYPE_CONVERSIONS);
        List<Object> moddedPotions = typeConversions.stream().filter(this::isModdedPotion).collect(Collectors.toList());

        itemConversions.clear();
        typeConversions.clear();

        tryChangePotions = true;

        Items.POTIONITEM.setMaxStackSize(potionStackSize);
        Items.SPLASH_POTION.setMaxStackSize(potionStackSize);
        Items.LINGERING_POTION.setMaxStackSize(potionStackSize);

        Ingredient extender = ItemMaterial.getIngredient(ItemMaterial.EnumMaterial.WITCH_WART);
        Ingredient strengthener = ItemMaterial.getIngredient(ItemMaterial.EnumMaterial.BRIMSTONE);
        Ingredient inverter = ItemMaterial.getIngredient(ItemMaterial.EnumMaterial.POISON_SAC);
        Ingredient awkward = Ingredient.fromItem(Items.NETHER_WART);
        Ingredient fireResistance = Ingredient.fromItem(Items.MAGMA_CREAM);
        Ingredient nightVision = Ingredient.fromItem(Items.SPIDER_EYE);
        Ingredient poison = Ingredient.fromStacks(new ItemStack(Blocks.RED_MUSHROOM));
        Ingredient regeneration = Ingredient.fromItem(Items.GHAST_TEAR);
        Ingredient strength = Ingredient.fromItem(Items.BLAZE_POWDER);
        Ingredient swiftness = Ingredient.fromItem(Items.SUGAR);
        Ingredient leaping = Ingredient.fromItem(Items.RABBIT_FOOT);
        Ingredient waterBreathing = waterBreathingAnyFish ? Ingredient.fromItem(Items.FISH) : Ingredient.fromStacks(new ItemStack(Items.FISH, ItemFishFood.FishType.PUFFERFISH.getMetadata()));
        Ingredient healing = ItemMaterial.getIngredient(ItemMaterial.EnumMaterial.MYSTERY_GLAND);

        //Conversion recipes
        if (tryChangePotions) {
            PotionHelper.addContainerRecipe(Items.POTIONITEM, BWMItems.CREEPER_OYSTER, Items.SPLASH_POTION);
            PotionHelper.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);

            PotionHelper.addMix(PotionTypes.WATER, awkward, PotionTypes.AWKWARD);
            PotionHelper.addMix(PotionTypes.WATER, extender, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, strengthener, PotionTypes.THICK);
            PotionHelper.addMix(PotionTypes.WATER, fireResistance, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, nightVision, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, poison, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, regeneration, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, strength, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, swiftness, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, leaping, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, waterBreathing, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, healing, PotionTypes.MUNDANE);
            PotionHelper.addMix(PotionTypes.WATER, inverter, PotionTypes.MUNDANE);

            PotionHelper.addMix(PotionTypes.AWKWARD, fireResistance, PotionTypes.FIRE_RESISTANCE);
            PotionHelper.addMix(PotionTypes.FIRE_RESISTANCE, extender, PotionTypes.LONG_FIRE_RESISTANCE);

            PotionHelper.addMix(PotionTypes.AWKWARD, nightVision, PotionTypes.NIGHT_VISION);
            PotionHelper.addMix(PotionTypes.NIGHT_VISION, extender, PotionTypes.LONG_NIGHT_VISION);

            PotionHelper.addMix(PotionTypes.AWKWARD, poison, PotionTypes.POISON);
            PotionHelper.addMix(PotionTypes.POISON, strengthener, PotionTypes.STRONG_POISON);
            PotionHelper.addMix(PotionTypes.POISON, extender, PotionTypes.LONG_POISON);

            PotionHelper.addMix(PotionTypes.AWKWARD, regeneration, PotionTypes.REGENERATION);
            PotionHelper.addMix(PotionTypes.REGENERATION, extender, PotionTypes.LONG_REGENERATION);
            PotionHelper.addMix(PotionTypes.REGENERATION, strengthener, PotionTypes.STRONG_REGENERATION);

            PotionHelper.addMix(PotionTypes.AWKWARD, strength, PotionTypes.STRENGTH);
            PotionHelper.addMix(PotionTypes.STRENGTH, strengthener, PotionTypes.STRONG_STRENGTH);
            PotionHelper.addMix(PotionTypes.STRENGTH, extender, PotionTypes.LONG_STRENGTH);

            if (!removeMovementPotions) {
                PotionHelper.addMix(PotionTypes.AWKWARD, swiftness, PotionTypes.SWIFTNESS);
                PotionHelper.addMix(PotionTypes.SWIFTNESS, strengthener, PotionTypes.STRONG_SWIFTNESS);
                PotionHelper.addMix(PotionTypes.SWIFTNESS, extender, PotionTypes.LONG_SWIFTNESS);

                PotionHelper.addMix(PotionTypes.AWKWARD, leaping, PotionTypes.LEAPING);
                PotionHelper.addMix(PotionTypes.LEAPING, extender, PotionTypes.LONG_LEAPING);
                PotionHelper.addMix(PotionTypes.LEAPING, strengthener, PotionTypes.STRONG_LEAPING);

                PotionHelper.addMix(PotionTypes.SWIFTNESS, inverter, PotionTypes.SLOWNESS);
                PotionHelper.addMix(PotionTypes.STRONG_SWIFTNESS, inverter, PotionTypes.SLOWNESS);
                PotionHelper.addMix(PotionTypes.LONG_SWIFTNESS, inverter, PotionTypes.LONG_SLOWNESS);
                PotionHelper.addMix(PotionTypes.LEAPING, inverter, PotionTypes.SLOWNESS);
                PotionHelper.addMix(PotionTypes.STRONG_LEAPING, inverter, PotionTypes.SLOWNESS);
                PotionHelper.addMix(PotionTypes.LONG_LEAPING, inverter, PotionTypes.LONG_SLOWNESS);
                PotionHelper.addMix(PotionTypes.SLOWNESS, extender, PotionTypes.LONG_SLOWNESS);
            }

            PotionHelper.addMix(PotionTypes.AWKWARD, waterBreathing, PotionTypes.WATER_BREATHING);
            PotionHelper.addMix(PotionTypes.WATER_BREATHING, extender, PotionTypes.LONG_WATER_BREATHING);

            PotionHelper.addMix(PotionTypes.AWKWARD, healing, PotionTypes.HEALING);
            PotionHelper.addMix(PotionTypes.HEALING, strengthener, PotionTypes.STRONG_HEALING);

            PotionHelper.addMix(PotionTypes.HEALING, inverter, PotionTypes.HARMING);
            PotionHelper.addMix(PotionTypes.STRONG_HEALING, inverter, PotionTypes.STRONG_HARMING);
            PotionHelper.addMix(PotionTypes.POISON, inverter, PotionTypes.HARMING);
            PotionHelper.addMix(PotionTypes.LONG_POISON, inverter, PotionTypes.HARMING);
            PotionHelper.addMix(PotionTypes.STRONG_POISON, inverter, PotionTypes.STRONG_HARMING);
            PotionHelper.addMix(PotionTypes.HARMING, strengthener, PotionTypes.STRONG_HARMING);

            PotionHelper.addMix(PotionTypes.STRENGTH, inverter, PotionTypes.WEAKNESS);
            PotionHelper.addMix(PotionTypes.STRONG_STRENGTH, inverter, PotionTypes.WEAKNESS);
            PotionHelper.addMix(PotionTypes.LONG_STRENGTH, inverter, PotionTypes.LONG_WEAKNESS);
            PotionHelper.addMix(PotionTypes.WEAKNESS, extender, PotionTypes.LONG_WEAKNESS);

            PotionHelper.addMix(PotionTypes.NIGHT_VISION, inverter, PotionTypes.INVISIBILITY);
            PotionHelper.addMix(PotionTypes.LONG_NIGHT_VISION, inverter, PotionTypes.LONG_INVISIBILITY);
            PotionHelper.addMix(PotionTypes.INVISIBILITY, extender, PotionTypes.LONG_INVISIBILITY);
        }

        if (modPotionCompat) {
            ItemStack extenderToReplace = new ItemStack(Items.REDSTONE);
            ItemStack strengthenerToReplace = new ItemStack(Items.GLOWSTONE_DUST);
            ItemStack inverterToReplace = new ItemStack(Items.FERMENTED_SPIDER_EYE);
            ItemStack splashToReplace = new ItemStack(Items.GUNPOWDER);


            //Technically still possible, but worth?
            for (Object moddedPotion : moddedPotions) {
                Ingredient reagent = MixPredicateHelper.getReagent(moddedPotion);

                PotionType input = MixPredicateHelper.getInputPotionType(moddedPotion);
                PotionType output = MixPredicateHelper.getOutputPotionType(moddedPotion);

                if (reagent.apply(extenderToReplace) && isExtended(input.getEffects(), output.getEffects()))
                    MixPredicateHelper.setReagent(moddedPotion, extender);

                if (reagent.apply(strengthenerToReplace) && isStrong(input.getEffects(), output.getEffects()))
                    MixPredicateHelper.setReagent(moddedPotion, strengthener);
                if (reagent.apply(inverterToReplace) && isInverted(input.getEffects(), output.getEffects()))
                    MixPredicateHelper.setReagent(moddedPotion, inverter);
                typeConversions.add(moddedPotion);
            }

            List<IBrewingRecipe> recipes = ReflectionHelper.getPrivateValue(BrewingRecipeRegistry.class, null, "recipes");
            ListIterator<IBrewingRecipe> iterator = recipes.listIterator();

            while (iterator.hasNext()) {
                IBrewingRecipe recipe = iterator.next();
                if (recipe instanceof AbstractBrewingRecipe) {
                    AbstractBrewingRecipe abstractRecipe = (AbstractBrewingRecipe) recipe;
                    if (abstractRecipe.isIngredient(extenderToReplace) && isExtended(abstractRecipe.getInput(), abstractRecipe.getOutput())) {
                        iterator.remove();
                        iterator.add(new BrewingRecipe(abstractRecipe.getInput(), ItemMaterial.getStack(ItemMaterial.EnumMaterial.WITCH_WART), abstractRecipe.getOutput()));
                    } else if (abstractRecipe.isIngredient(strengthenerToReplace) && isStrong(abstractRecipe.getInput(), abstractRecipe.getOutput())) {
                        iterator.remove();
                        iterator.add(new BrewingRecipe(abstractRecipe.getInput(), ItemMaterial.getStack(ItemMaterial.EnumMaterial.BRIMSTONE), abstractRecipe.getOutput()));
                    } else if (abstractRecipe.isIngredient(inverterToReplace) && isInverted(abstractRecipe.getInput(), abstractRecipe.getOutput())) {
                        iterator.remove();
                        iterator.add(new BrewingRecipe(abstractRecipe.getInput(), ItemMaterial.getStack(ItemMaterial.EnumMaterial.POISON_SAC), abstractRecipe.getOutput()));
                    } else if (abstractRecipe.isIngredient(splashToReplace) && isSplash(abstractRecipe.getInput(), abstractRecipe.getOutput())) {
                        iterator.remove();
                        iterator.add(new BrewingRecipe(abstractRecipe.getInput(), new ItemStack(BWMItems.CREEPER_OYSTER), abstractRecipe.getOutput()));
                    }
                }
            }
        }


    }


    private boolean isModdedPotion(Object predicate) {
        ResourceLocation registryName = MixPredicateHelper.getOutputPotionType(predicate).getRegistryName();
        //If there's no registry name it's surely modded as only modders make dumb mistakes like that
        return registryName == null || (!registryName.getNamespace().toLowerCase().equals("minecraft") && !registryName.getNamespace().toLowerCase().equals("betterwithmods"));
    }

    public boolean isExtended(ItemStack potionA, ItemStack potionB) {
        List<PotionEffect> effectsA = PotionUtils.getEffectsFromStack(potionA);
        List<PotionEffect> effectsB = PotionUtils.getEffectsFromStack(potionB);

        return isExtended(effectsA, effectsB); //Not equal because of fuse potions in Extra Alchemy
    }

    private boolean isExtended(List<PotionEffect> effectsA, List<PotionEffect> effectsB) {
        if (effectsA.size() != 1 || effectsB.size() != 1)
            return false;

        PotionEffect effectA = effectsA.get(0);
        PotionEffect effectB = effectsB.get(0);

        return isExtended(effectA, effectB);
    }

    private boolean isExtended(PotionEffect effectA, PotionEffect effectB) {
        return effectA.getPotion().equals(effectB.getPotion()) && effectA.getDuration() != effectB.getDuration();
    }

    public boolean isStrong(ItemStack potionA, ItemStack potionB) {
        List<PotionEffect> effectsA = PotionUtils.getEffectsFromStack(potionA);
        List<PotionEffect> effectsB = PotionUtils.getEffectsFromStack(potionB);

        return isStrong(effectsA, effectsB);
    }

    private boolean isStrong(List<PotionEffect> effectsA, List<PotionEffect> effectsB) {
        if (effectsA.size() != 1 || effectsB.size() != 1)
            return false;

        PotionEffect effectA = effectsA.get(0);
        PotionEffect effectB = effectsB.get(0);

        return isStrong(effectA, effectB);
    }

    private boolean isStrong(PotionEffect effectA, PotionEffect effectB) {
        return effectA.getPotion().equals(effectB.getPotion()) && effectA.getAmplifier() < effectB.getAmplifier();
    }

    public boolean isInverted(ItemStack potionA, ItemStack potionB) {
        List<PotionEffect> effectsA = PotionUtils.getEffectsFromStack(potionA);
        List<PotionEffect> effectsB = PotionUtils.getEffectsFromStack(potionB);

        return isInverted(effectsA, effectsB);
    }

    private boolean isInverted(List<PotionEffect> effectsA, List<PotionEffect> effectsB) {
        if (effectsA.size() != 1 || effectsB.size() != 1)
            return false;

        PotionEffect effectA = effectsA.get(0);
        PotionEffect effectB = effectsB.get(0);

        return isInverted(effectA, effectB);
    }

    private boolean isInverted(PotionEffect effectA, PotionEffect effectB) {
        return !effectA.getPotion().equals(effectB.getPotion());
    }

    public boolean isSplash(ItemStack potionA, ItemStack potionB) {
        List<PotionEffect> effectsA = PotionUtils.getEffectsFromStack(potionA);
        List<PotionEffect> effectsB = PotionUtils.getEffectsFromStack(potionB);

        if (effectsA.size() != 1 || effectsB.size() != 1)
            return false;

        PotionEffect effectA = effectsA.get(0);
        PotionEffect effectB = effectsB.get(0);

        return effectA.getPotion().equals(effectB.getPotion()) && potionB.getItem() instanceof ItemSplashPotion;
    }


    @Override
    public boolean hasEvent() {
        return true;
    }

}
