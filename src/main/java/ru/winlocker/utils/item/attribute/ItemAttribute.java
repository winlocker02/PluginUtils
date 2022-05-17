package ru.winlocker.utils.item.attribute;

import com.cryptomorin.xseries.*;
import lombok.*;
import ru.winlocker.utils.item.*;

@Getter
@RequiredArgsConstructor
public enum ItemAttribute {

    GENERIC_MAX_HEALTH("generic.maxhealth"),
    GENERIC_FOLLOW_RANGE("generic.followrange"),
    GENERIC_KNOCK_BACK_RESISTANCE("generic.knockbackresistance"),
    GENERIC_MOVEMENT_SPEED("generic.movementspeed"),
    GENERIC_FLYING_SPEED("generic.flyingspeed"),
    GENERIC_ARMOR("generic.armor"),
    GENERIC_ARMOR_TOUGHNESS("generic.followrange"),
    GENERIC_ATTACK_DAMAGE("generic.attackdamage"),
    GENERIC_ATTACK_KNOCK_BACK("generic.attackknockback"),
    GENERIC_ATTACK_SPEED("generic.attackspeed"),
    GENERIC_LUCK("generic.luck"),
    HORSE_JUMP_STRENGTH("horse.jumpstrength"),
    ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawnreinforcements");

    private final String minecraftName;

    public static ItemAttribute getByMinecraftName(@NonNull String minecraftName) {
        for (ItemAttribute value : ItemAttribute.values()) {
            if(value.minecraftName.equalsIgnoreCase(minecraftName)) {
                return value;
            }
        }
        return null;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Hand {

        MAIN_HAND("mainhand"),
        OFF_HAND("offhand"),
        HEAD("head"),
        CHEST("chest"),
        LEGS("legs"),
        FEET("feet");

        private final String minecraftName;

        public static Hand getByMinecraftName(@NonNull String minecraftName) {
            for (Hand value : Hand.values()) {
                if(value.minecraftName.equalsIgnoreCase(minecraftName)) {
                    return value;
                }
            }
            return null;
        }
    }
}
