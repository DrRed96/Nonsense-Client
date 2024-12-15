package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventMovementInput;
import wtf.bhopper.nonsense.module.impl.movement.FastSneak;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        EventMovementInput event = new EventMovementInput(this.moveForward, this.moveStrafe, this.jump, this.sneak);
        Nonsense.getEventBus().post(event);
        this.moveForward = event.forwards;
        this.moveStrafe = event.strafe;
        this.jump = event.jump;
        this.sneak = event.sneak;

        if (this.sneak && !Nonsense.module(FastSneak.class).isToggled()) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
