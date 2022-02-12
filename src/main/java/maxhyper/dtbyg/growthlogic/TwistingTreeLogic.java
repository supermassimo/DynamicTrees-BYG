package maxhyper.dtbyg.growthlogic;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.configurations.ConfigurationProperty;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKitConfiguration;
import com.ferreusveritas.dynamictrees.growthlogic.PalmGrowthLogic;
import com.ferreusveritas.dynamictrees.growthlogic.context.DirectionManipulationContext;
import com.ferreusveritas.dynamictrees.growthlogic.context.PositionalSpeciesContext;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TwistingTreeLogic extends GrowthLogicKit {

    public static final ConfigurationProperty<Float> CHANCE_TO_SPLIT = ConfigurationProperty.floatProperty("chance_to_split");
    public static final ConfigurationProperty<Integer> DOWN_PROBABILITY = ConfigurationProperty.integer("down_probability");

    public TwistingTreeLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected GrowthLogicKitConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(CHANCE_TO_SPLIT, 0.01f)
                .with(DOWN_PROBABILITY, 0);
    }

    @Override
    protected void registerProperties() {
        this.register(CHANCE_TO_SPLIT, DOWN_PROBABILITY);
    }

    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration configuration, DirectionManipulationContext context) {
        final World world = context.world();
        final GrowSignal signal = context.signal();
        final int[] probMap = context.probMap();
        final BlockPos pos = context.pos();
        Direction originDir = signal.dir.getOpposite();

        int count = 0;
        for (Direction direction : Direction.values()){
            int rad = TreeHelper.getRadius(world,pos.offset(direction.getNormal()));
            if (rad > 0) count++;
            probMap[direction.ordinal()] = rad + (world.getRandom().nextFloat() < configuration.get(CHANCE_TO_SPLIT) ? 1 : 0);
        }
        //If there are not enough valid branches, just allow any direction except up
        if (count <= 1 || signal.energy < 3){
            probMap[0] = configuration.get(DOWN_PROBABILITY);
            probMap[1] = context.species().getUpProbability();
            probMap[2] = probMap[3] = probMap[4] = probMap[5] = 1;
        }

        probMap[originDir.ordinal()] = 0; // Disable the direction we came from

        return probMap;
    }

    @Override
    public float getEnergy(GrowthLogicKitConfiguration configuration, PositionalSpeciesContext context) {
        long day = context.world().getGameTime() / 24000L;
        int month = (int) day / 30; // Change the hashs every in-game month
        return super.getEnergy(configuration, context) *
                context.species().biomeSuitability(context.world(), context.pos()) +
                (CoordUtils.coordHashCode(context.pos().above(month), 3) %
                        3); // Vary the height energy by a psuedorandom hash function

    }

}
