package com.stebars.cantswimupwaterfalls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> {

	@Shadow public World level;
	@Shadow private AxisAlignedBB bb;
	@Shadow private Vector3d deltaMovement = Vector3d.ZERO;
	@Shadow protected Object2DoubleMap<ITag<Fluid>> fluidHeight = new Object2DoubleArrayMap<>(2);

	protected EntityMixin(Class<Entity> baseClass) {
		super(baseClass);
	}

	@Shadow
	public AxisAlignedBB getBoundingBox() {
		return this.bb;
	}

	@Shadow
	public boolean isPushedByFluid() {
		return true;
	}

	@Shadow
	public Vector3d getDeltaMovement() {
		return this.deltaMovement;
	}

	@Shadow
	public void setDeltaMovement(Vector3d p_213317_1_) {
		this.deltaMovement = p_213317_1_;
	}

	@Shadow
	public ITextComponent getName() {
		return null;
	}

	@Overwrite
	public boolean updateFluidHeightAndDoFluidPushing(ITag<Fluid> p_210500_1_, double p_210500_2_) {
		AxisAlignedBB axisalignedbb = this.getBoundingBox().deflate(0.001D);
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.ceil(axisalignedbb.maxX);
		int k = MathHelper.floor(axisalignedbb.minY);
		int l = MathHelper.ceil(axisalignedbb.maxY);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.ceil(axisalignedbb.maxZ);
		if (!this.level.hasChunksAt(i, k, i1, j, l, j1)) {
			return false;
		} else {
			double d0 = 0.0D;
			boolean flag = this.isPushedByFluid();
			boolean flag1 = false;
			Vector3d vector3d = Vector3d.ZERO;
			int k1 = 0;
			BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

			for(int l1 = i; l1 < j; ++l1) {
				for(int i2 = k; i2 < l; ++i2) {
					for(int j2 = i1; j2 < j1; ++j2) {
						blockpos$mutable.set(l1, i2, j2);
						FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
						if (fluidstate.is(p_210500_1_)) {
							double d1 = (double)((float)i2 + fluidstate.getHeight(this.level, blockpos$mutable));
							if (d1 >= axisalignedbb.minY) {
								flag1 = true;
								d0 = Math.max(d1 - axisalignedbb.minY, d0);
								if (flag) {
									Vector3d vector3d1 = fluidstate.getFlow(this.level, blockpos$mutable);
									if (d0 < 0.4D) {
										vector3d1 = vector3d1.scale(d0);
									}
									// BEGIN added by mixin
									if (fluidstate.hasProperty(BlockStateProperties.FALLING) &&
											fluidstate.getValue(BlockStateProperties.FALLING)) {
										vector3d1 = vector3d1.add(0, -1.51, 0);
										//Log.info("apply downward force for entity ", getName());
									}
									// END added by mixin

									vector3d = vector3d.add(vector3d1);
									++k1;
								}
							}
						}
					}
				}
			}

			if (vector3d.length() > 0.0D) {
				if (k1 > 0) {
					vector3d = vector3d.scale(1.0D / (double)k1);
				}

				if (!(((Entity) (Object) this) instanceof PlayerEntity)) {
					vector3d = vector3d.normalize();
				}

				Vector3d vector3d2 = this.getDeltaMovement();
				vector3d = vector3d.scale(p_210500_2_ * 1.0D);
				double d2 = 0.003D;
				if (Math.abs(vector3d2.x) < 0.003D && Math.abs(vector3d2.z) < 0.003D && vector3d.length() < 0.0045000000000000005D) {
					vector3d = vector3d.normalize().scale(0.0045000000000000005D);
				}

				this.setDeltaMovement(this.getDeltaMovement().add(vector3d));
			}

			this.fluidHeight.put(p_210500_1_, d0);
			return flag1;
		}
	}
}