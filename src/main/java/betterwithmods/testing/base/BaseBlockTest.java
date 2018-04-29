package betterwithmods.testing.base;

import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.BlockRecipe;
import betterwithmods.testing.base.world.FakeWorld;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.fest.assertions.Assertions;


public abstract class BaseBlockTest<T extends BlockRecipe> extends BaseTest {

    protected final BlockPos origin = BlockPos.ORIGIN;
    protected FakeWorld world;
    protected CraftingManagerBlock<T> TEST_MANAGER;
    protected T recipe, oreRecipe, blockDropRecipe;

    public abstract void beforeTest();

    @Test
    public void testFakeWorld() {
        world.setBlockToAir(origin);
        Assertions.assertThat(world.getBlockState(origin)).isEqualTo(Blocks.AIR.getDefaultState());
        world.setBlockState(origin, Blocks.STONE.getDefaultState());
        Assertions.assertThat(world.getBlockState(origin)).isEqualTo(Blocks.STONE.getDefaultState());
    }

    @Test
    public void testRecipeAddition() {
        Assertions.assertThat(recipe.isInvalid()).isFalse();
        Assertions.assertThat(TEST_MANAGER.getRecipes()).isEmpty();
        TEST_MANAGER.addRecipe(recipe);
        Assertions.assertThat(TEST_MANAGER.getRecipes()).hasSize(1);
    }

    @Test
    public void testRecipeRemoval() {
        Assertions.assertThat(recipe.isInvalid()).isFalse();
        Assertions.assertThat(TEST_MANAGER.getRecipes()).isEmpty();
        TEST_MANAGER.addRecipe(recipe);
        Assertions.assertThat(TEST_MANAGER.getRecipes()).isNotEmpty();
        TEST_MANAGER.remove(recipe);
    }

    @Test
    public void testRecipeMatching() {
        testRecipe(recipe, origin);
    }

    @Test
    public void testOredictMatching() {
        testRecipe(oreRecipe, origin);
    }

    @Test
    public void testBlockDropMatching() {
        testRecipe(blockDropRecipe, origin.up());
    }


    @Test
    public void testPriority() {

    }

    protected void testRecipe(T recipe, BlockPos pos) {
        Assertions.assertThat(recipe.isInvalid()).isFalse();
        Assertions.assertThat(TEST_MANAGER.getRecipes()).isEmpty();
        TEST_MANAGER.addRecipe(recipe);
        Assertions.assertThat(TEST_MANAGER.getRecipes()).hasSize(1);
        Assertions.assertThat(TEST_MANAGER.canCraft(world, origin, world.getBlockState(pos))).isTrue();
        Assertions.assertThat(TEST_MANAGER.craftItem(world, origin, world.getBlockState(pos))).isNotEmpty();
        TEST_MANAGER.getRecipes().clear();
    }
}

