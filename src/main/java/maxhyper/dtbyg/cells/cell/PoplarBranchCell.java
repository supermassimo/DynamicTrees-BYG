package maxhyper.dtbyg.cells.cell;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import net.minecraft.util.Direction;

public class PoplarBranchCell implements ICell {
	
	@Override
	public int getValue() {
		return 5;
	}

	static final int[] map = {3, 4, 4, 4, 4, 4};
	
	@Override
	public int getValueFromSide(Direction side) {
		return map[side.ordinal()];
	}

}