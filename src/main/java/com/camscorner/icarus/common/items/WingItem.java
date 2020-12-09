package com.camscorner.icarus.common.items;

import com.camscorner.icarus.Icarus;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import top.theillusivec4.caelus.api.CaelusApi;

import java.util.UUID;

public class WingItem extends TrinketItem
{
	private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
	private ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
	private WingColour primaryColour;
	private WingColour secondaryColour;
	private boolean shouldSlowfall;
	/**
	 * The speed the wings allow the player to travel at. Default: 0.05D.
	 */
	private double speed;
	/**
	 * The speed at which the player will accelerate. Also controls turn radius. Default 0.05D.
	 */
	private double acceleration;

	/**
	 * @param speed The speed the wings allow the player to travel at. Default: 0.05D.
	 * @param acceleration The speed at which the player will accelerate. Also controls turn radius. Default 0.05D.
	 */
	public WingItem(double speed, double acceleration, WingColour primaryColour, WingColour secondaryColour)
	{
		super(new Item.Settings().group(Icarus.ITEM_GROUP).maxCount(1));
		this.builder.put(CaelusApi.ELYTRA_FLIGHT, new EntityAttributeModifier(UUID.fromString("7d9704a0-383f-11eb-adc1-0242ac120002"),
				"Flight", 1, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiers = this.builder.build();
		this.speed = speed;
		this.acceleration = acceleration;
		this.primaryColour = primaryColour;
		this.secondaryColour = secondaryColour;
	}

	/**
	 * The default constructor. It sets {@link WingItem#speed} and {@link WingItem#acceleration} to 0.05D.
	 */
	public WingItem(WingColour primaryColour, WingColour secondaryColour)
	{
		super(new Item.Settings().group(Icarus.ITEM_GROUP).maxCount(1));
		this.builder.put(CaelusApi.ELYTRA_FLIGHT, new EntityAttributeModifier(UUID.fromString("7d9704a0-383f-11eb-adc1-0242ac120002"),
				"Flight", 1, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiers = this.builder.build();
		this.speed = 0.05D;
		this.acceleration = 0.05D;
		this.primaryColour = primaryColour;
		this.secondaryColour = secondaryColour;
	}

	@Override
	public void tick(PlayerEntity player, ItemStack stack)
	{
		if(player.isFallFlying())
		{
			if(player.forwardSpeed > 0)
				applySpeed(player);

			if(player.isSneaking())
				stopFlying(player);
		}
		else
		{
			if(player.isOnGround() || player.isTouchingWater())
				shouldSlowfall = false;

			if(shouldSlowfall)
			{
				player.fallDistance = 0F;
				player.setVelocity(player.getVelocity().x, -0.4, player.getVelocity().z);
			}
		}
	}

	@Override
	public boolean canWearInSlot(String group, String slot)
	{
		return group.equals(SlotGroups.CHEST) && slot.equals(Slots.CAPE);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getTrinketModifiers(String group, String slot, UUID uuid, ItemStack stack)
	{
		return this.attributeModifiers;
	}

	/* TODO
	 * PlayerEntity player;
	 * Item item = TrinketsAPI.getTrinketComponent(player).getStack("chest", "cape").getItem();
	 *
	 * if(item instanceof WingItem)
	 *     item = (WingItem) item;
	 *     item.getPrimaryColour();
	 */

	public WingColour getPrimaryColour()
	{
		return this.primaryColour;
	}

	public WingColour getSecondaryColour()
	{
		return this.secondaryColour;
	}

	public void stopFlying(PlayerEntity player)
	{
		shouldSlowfall = true;
		player.stopFallFlying();
	}

	public void applySpeed(LivingEntity entity)
	{
		shouldSlowfall = false;
		Vec3d rotation = entity.getRotationVector();
		Vec3d velocity = entity.getVelocity();

		entity.setVelocity(velocity.add(rotation.x * speed + (rotation.x * 1.5D - velocity.x) * acceleration,
										rotation.y * speed + (rotation.y * 1.5D - velocity.y) * acceleration,
										rotation.z * speed + (rotation.z * 1.5D - velocity.z) * acceleration));
	}

	public enum WingColour
	{
		WHITE, ORANGE, MAGENTA, LIGHT_BLUE,
		YELLOW, LIME, PINK, GREY,
		LIGHT_GREY, CYAN, PURPLE, BLUE,
		BROWN, GREEN, RED, BLACK,
		NONE
	}
}
