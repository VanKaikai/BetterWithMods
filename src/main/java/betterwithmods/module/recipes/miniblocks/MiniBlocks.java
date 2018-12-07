package betterwithmods.module.recipes.miniblocks;

import betterwithmods.BetterWithMods;
import betterwithmods.client.baking.ModelFactory;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.BWMCreativeTabs;
import betterwithmods.common.BWMOreDictionary;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.camo.BlockDynamic;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.block.recipe.builder.SawRecipeBuilder;
import betterwithmods.common.tile.TileCamo;
import betterwithmods.lib.ModLib;
import betterwithmods.library.common.modularity.impl.Feature;
import betterwithmods.library.common.variants.IBlockVariants;
import betterwithmods.library.utils.GlobalUtils;
import betterwithmods.library.utils.JsonUtils;
import betterwithmods.library.utils.MaterialUtil;
import betterwithmods.library.utils.VariantUtils;
import betterwithmods.module.internal.BlockRegistry;
import betterwithmods.module.internal.RecipeRegistry;
import betterwithmods.module.recipes.AnvilRecipes;
import betterwithmods.module.recipes.miniblocks.client.CamoModel;
import betterwithmods.module.recipes.miniblocks.client.MiniModel;
import betterwithmods.module.recipes.miniblocks.client.StairModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class MiniBlocks extends Feature {
    private static boolean autoGeneration;
    private static boolean requiresAnvil;
    private static Set<Ingredient> WHITELIST;

    @SideOnly(Side.CLIENT)
    public static void registerModel(IRegistry<ModelResourceLocation, IBakedModel> registry, String name, @Nonnull ModelFactory<?> model) {
        registerModel(registry, name, model, model.getVariants());
    }

    @SideOnly(Side.CLIENT)
    public static void registerModel(IRegistry<ModelResourceLocation, IBakedModel> registry, String name, IBakedModel model, Set<String> variants) {
        for (String variant : variants) {
            registry.putObject(new ModelResourceLocation(ModLib.MODID + ":" + name, variant), model);
        }
    }

    private static ResourceLocation getRecipeRegistry(ItemStack output, ItemStack parent) {
        if (parent.getMetadata() > 0)
            return new ResourceLocation(ModLib.MODID, output.getItem().getRegistryName().getPath() + "_" + parent.getItem().getRegistryName().getPath() + "_" + parent.getMetadata());
        return new ResourceLocation(ModLib.MODID, output.getItem().getRegistryName().getPath() + "_" + parent.getItem().getRegistryName().getPath());
    }

    public static void placeMini(World world, BlockPos pos, DynamicType type, IBlockState parent) {
        Material material = parent.getMaterial();
        Block block = DynblockUtils.getDynamicVariant(type, material);
        world.setBlockState(pos, block.getDefaultState());
        TileCamo camo = (TileCamo) world.getTileEntity(pos);
        if (camo != null)
            camo.setState(parent);
    }

    public void createBlocks() {
        for (Material material : MaterialUtil.materials()) {
            String name = MaterialUtil.getMaterialName(material);
            DynblockUtils.addDynamicVariant(DynamicType.SIDING, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.MOULDING, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.CORNER, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.COLUMN, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.PEDESTAL, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.STAIR, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.TABLE, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.GRATE, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.BENCH, material, name);
            DynblockUtils.addDynamicVariant(DynamicType.CHAIR, material, name);
        }

        for (BlockDynamic dynamic : DynblockUtils.DYNAMIC_VARIANT_TABLE.values()) {
            BlockRegistry.registerBlock(dynamic.setCreativeTab(BWMCreativeTabs.MINI_BLOCKS), dynamic.createItemBlock().setRegistryName(dynamic.getRegistryName()));
        }
    }


    @SideOnly(Side.CLIENT)
    public void onPostBake(ModelBakeEvent event) {

        List<ModelFactory> models = Lists.newArrayList();

        models.add(new MiniModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/siding")), "siding"));
        models.add(new MiniModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/moulding")), "moulding"));
        models.add(new MiniModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/corner")), "corner"));
        models.add(new MiniModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/column")), "column"));
        models.add(new MiniModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/pedestal")), "pedestals"));
        models.add(new StairModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/stair")), RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/mini/stair_inner_corner")), "stair"));
        models.add(new MiniModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/chair")), "chair"));
        models.add(new CamoModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/table_supported")), "table").setVariants("normal", "inventory", "supported=true"));
        models.add(new CamoModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/table_unsupported")), "table").setVariants("supported=false"));
        models.add(new CamoModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/bench_supported")), "bench").setVariants("normal", "inventory", "supported=true"));
        models.add(new CamoModel(RenderUtils.getModel(new ResourceLocation(ModLib.MODID, "block/bench_unsupported")), "bench").setVariants("supported=false"));

        for (Material material : MaterialUtil.materials()) {
            String name = MaterialUtil.getMaterialName(material);
            for(ModelFactory<?> model: models) {
                registerModel(event.getModelRegistry(), model.getRegistryName() + "_" + name, model, model.getVariants());
            }
        }
    }

    public Set<Ingredient> loadMiniblockWhitelist() {
        File file = new File(config().path, "betterwithmods/dynamicblocks.json");

        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        if (!Files.exists(file.toPath())) {
            JsonArray DEFAULT_CONFIG = new JsonArray();
            DEFAULT_CONFIG.add(JsonUtils.fromOre("plankWood"));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.COBBLESTONE)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.STONE)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.STONEBRICK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.SANDSTONE)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.RED_SANDSTONE)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.PURPUR_BLOCK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.BRICK_BLOCK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.NETHER_BRICK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.QUARTZ_BLOCK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.GOLD_BLOCK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(new ItemStack(Blocks.IRON_BLOCK)));
            DEFAULT_CONFIG.add(JsonUtils.fromStack(BlockAesthetic.getStack(BlockAesthetic.Type.WHITESTONE)));
            JsonUtils.writeFile(file, DEFAULT_CONFIG);
        }
        JsonObject[] objects = JsonUtils.readerFile(file);
        if (objects != null)
            return Arrays.stream(objects).map(object -> CraftingHelper.getIngredient(object, BetterWithMods.JSON_CONTEXT)).collect(Collectors.toSet());
        return Sets.newHashSet();
    }

    public void registerMiniblocks() {
        WHITELIST = loadMiniblockWhitelist();

        final NonNullList<ItemStack> list = NonNullList.create();

        Iterable<Item> items = autoGeneration
                ? ForgeRegistries.ITEMS
                : WHITELIST.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).map(ItemStack::getItem).collect(Collectors.toSet());

        for (Item item : items) {
            if (!(item instanceof ItemBlock))
                continue;
            try {
                final CreativeTabs ctab = item.getCreativeTab();
                if (ctab != null) {
                    item.getSubItems(ctab, list);
                }
                for (final ItemStack stack : list) {
                    if (!(stack.getItem() instanceof ItemBlock))
                        continue;
                    IBlockState state = GlobalUtils.getStateFromStack(stack);
                    if (state != null && DynblockUtils.isValidMini(state, stack)) {
                        Material material = state.getMaterial();
                        if (MaterialUtil.isValid(material)) {
                            DynblockUtils.MATERIAL_VARIANTS.put(material, state);
                        }
                    }
                }
                list.clear();
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        autoGeneration = loadProperty("Auto Generate Miniblocks", false).setComment("Automatically add miniblocks for many blocks, based on heuristics and probably planetary alignments. WARNING: Exposure to this config option can kill pack developers.").get();
        requiresAnvil = loadProperty("Stone Miniblocks require Anvil recipe", true).setComment("When enabled stone and metal miniblocks will require an anvil recipe, when disabled they will all be made with the saw").get();
        createBlocks();
        DynamicType.registerTiles();
    }

    @Override
    public void onRecipesRegistered(RegistryEvent.Register<IRecipe> event) {
        registerMiniblocks();

        for (Material material : MaterialUtil.materials()) {
            BlockDynamic siding = DynblockUtils.getDynamicVariant(DynamicType.SIDING, material);
            BlockDynamic moulding = DynblockUtils.getDynamicVariant(DynamicType.MOULDING, material);
            BlockDynamic corner = DynblockUtils.getDynamicVariant(DynamicType.CORNER, material);

            event.getRegistry().register(new MiniRecipe(siding, null));
            event.getRegistry().register(new MiniRecipe(moulding, siding));
            event.getRegistry().register(new MiniRecipe(corner, moulding));
        }

        SawRecipeBuilder sawBuilder = new SawRecipeBuilder();
        for (IBlockState parent : DynblockUtils.MATERIAL_VARIANTS.values()) {
            ItemStack parentStack = GlobalUtils.getStackFromState(parent);
            Material material = parent.getMaterial();
            MiniBlockIngredient siding = new MiniBlockIngredient("siding", parentStack);
            MiniBlockIngredient moulding = new MiniBlockIngredient("moulding", parentStack);

            ItemStack columnStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.COLUMN, material), parent, 8);
            ItemStack pedestalStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.PEDESTAL, material), parent, 8);
            ItemStack tableStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.TABLE, material), parent, 1);
            ItemStack benchStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.BENCH, material), parent, 1);
            ItemStack chairStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.CHAIR, material), parent, 2);

            AnvilRecipes.addSteelShapedRecipe(columnStack.getItem().getRegistryName(), columnStack, "XX", "XX", "XX", "XX", 'X', moulding);
            AnvilRecipes.addSteelShapedRecipe(pedestalStack.getItem().getRegistryName(), pedestalStack, " XX ", "BBBB", "BBBB", "BBBB", 'X', siding, 'B', parentStack);

            event.getRegistry().register(new ShapedOreRecipe(chairStack.getItem().getRegistryName(), chairStack, "  S", "SSS", "M M", 'S', siding, 'M', moulding).setMirrored(true).setRegistryName(getRecipeRegistry(chairStack, parentStack)));
            event.getRegistry().register(new ShapedOreRecipe(tableStack.getItem().getRegistryName(), tableStack, "SSS", " M ", " M ", 'S', siding, 'M', moulding).setRegistryName(getRecipeRegistry(tableStack, parentStack)));
            event.getRegistry().register(new ShapedOreRecipe(benchStack.getItem().getRegistryName(), benchStack, "SSS", " M ", 'S', siding, 'M', moulding).setRegistryName(getRecipeRegistry(benchStack, parentStack)));

            IBlockVariants blockVariants = VariantUtils.getVariantFromState(IBlockVariants.EnumBlock.BLOCK, parent);
            if (blockVariants != null) {
                ItemStack fence = blockVariants.getStack(IBlockVariants.EnumBlock.FENCE, 2);
                ItemStack fencegate = blockVariants.getStack(IBlockVariants.EnumBlock.FENCE_GATE, 1);
                ItemStack stairs = blockVariants.getStack(IBlockVariants.EnumBlock.STAIR, 1);
                ItemStack wall = blockVariants.getStack(IBlockVariants.EnumBlock.WALL, 3);
                if (!wall.isEmpty())
                    event.getRegistry().register(new ShapedOreRecipe(wall.getItem().getRegistryName(), wall, "SSS", 'S', siding).setRegistryName(getRecipeRegistry(wall, parentStack)));
                if (!stairs.isEmpty())
                    event.getRegistry().register(new ShapedOreRecipe(stairs.getItem().getRegistryName(), stairs, "M ", "MM", 'M', moulding).setMirrored(true).setRegistryName(getRecipeRegistry(stairs, parentStack)));
                if (!fence.isEmpty())
                    event.getRegistry().register(new ShapedOreRecipe(fence.getItem().getRegistryName(), fence, "MMM", 'M', moulding).setRegistryName(getRecipeRegistry(fence, parentStack)));
                if (!fencegate.isEmpty())
                    event.getRegistry().register(new ShapedOreRecipe(fencegate.getItem().getRegistryName(), fencegate, "MSM", 'M', moulding, 'S', siding).setRegistryName(getRecipeRegistry(fencegate, parentStack)));
            }

            if (!requiresAnvil || material == Material.WOOD) {
                MiniBlockIngredient corner = new MiniBlockIngredient("corner", parentStack);
                ItemStack sidingStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.SIDING, material), parent, 2);
                ItemStack mouldingStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.MOULDING, material), parent, 2);
                ItemStack cornerStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.CORNER, material), parent, 2);
                RecipeRegistry.WOOD_SAW.registerAll(
                        sawBuilder.input(parentStack).outputs(sidingStack).build(),
                        sawBuilder.input(siding).outputs(mouldingStack).build(),
                        sawBuilder.input(moulding).outputs(cornerStack).build()
                );

                if (BWMOreDictionary.isOre(parentStack, "plankWood")) {
                    RecipeRegistry.WOOD_SAW.register(sawBuilder.input(corner).outputs(ItemMaterial.getStack(ItemMaterial.EnumMaterial.WOODEN_GEAR, 2)).build());
                }
            } else {
                ItemStack sidingStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.SIDING, material), parent, 8);
                ItemStack mouldingStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.MOULDING, material), parent, 8);
                ItemStack cornerStack = DynblockUtils.fromParent(DynblockUtils.getDynamicVariant(DynamicType.CORNER, material), parent, 8);

                AnvilRecipes.addSteelShapedRecipe(sidingStack.getItem().getRegistryName(), sidingStack, "XXXX", 'X', parentStack);
                AnvilRecipes.addSteelShapedRecipe(mouldingStack.getItem().getRegistryName(), mouldingStack, "XXXX", 'X', siding);
                AnvilRecipes.addSteelShapedRecipe(cornerStack.getItem().getRegistryName(), cornerStack, "XXXX", 'X', moulding);
            }
        }

    }

    @Override
    public String getDescription() {
        return "Dynamically generate Siding, Mouldings and Corners for many of the blocks in the game.";
    }


}
