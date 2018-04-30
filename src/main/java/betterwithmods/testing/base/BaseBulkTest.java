package betterwithmods.testing.base;

import betterwithmods.api.tile.IBulkTile;
import betterwithmods.common.registry.bulk.manager.CraftingManagerBulk;
import betterwithmods.common.registry.bulk.recipes.BulkRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.fest.assertions.Assertions;


public abstract class BaseBulkTest<T extends BulkRecipe > extends BaseTest {

    protected IBulkTile TILE;
    protected CraftingManagerBulk<T> TEST_MANAGER;
    protected T recipe, oreRecipe;

    public BaseBulkTest(IBulkTile TILE) {
        this.TILE = TILE;
    }

    public BaseBulkTest() {
        this(new IBulkTile() {
            private MockInventory inventory = MockInventory.createInventory(0, new ItemStack(Items.FLINT, 64), new ItemStack(Items.BONE, 64), new ItemStack(Items.CLAY_BALL, 64));

            @Override
            public World getWorld() {
                return null;
            }

            @Override
            public BlockPos getPos() {
                return null;
            }

            @Override
            public ItemStackHandler getInventory() {
                return inventory;
            }
        });
    }

    @Before
    public abstract void beforeTest();

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
        testRecipe(recipe);
    }

    @Test
    public void testOredictMatching() {
        testRecipe(oreRecipe);
    }

    @Test
    public void testPriority() {

    }

    private void testRecipe(T recipe) {
        Assertions.assertThat(recipe.isInvalid()).isFalse();

        Assertions.assertThat(TEST_MANAGER.getRecipes()).isEmpty();
        TEST_MANAGER.addRecipe(recipe);
        Assertions.assertThat(TEST_MANAGER.getRecipes()).hasSize(1);

        Assertions.assertThat(TEST_MANAGER.canCraft(TILE, TILE.getInventory())).isTrue();
        Assertions.assertThat(TEST_MANAGER.craftItem(null, TILE, TILE.getInventory())).isNotEmpty();

    }
}