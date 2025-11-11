package dev.thezexquex.yasmpp.modules.captcha;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class HaCaptcha {
    private Player player;
    private boolean finished = false;
    private CaptchaResult result = CaptchaResult.SUCCESS;
    private int attempts = 0;
    private int maxAttempts = 3;
    private CaptchaSet captchaSet = CaptchaSet.netherBlocks();
    private List<CaptchaField> captchaFields;

    public HaCaptcha(Player player) {
        this.player = player;
    }

    public CaptchaResult getResult() {
        return result;
    }

    public boolean isFinished() {
        return finished;
    }

    public void finished(boolean finished) {
        this.finished = finished;
    }

    public void generateCaptcha() {
        this.captchaSet = CaptchaSet.random();
        var shuffled = new ArrayList<>(captchaSet.captchaFields());
        Collections.shuffle(shuffled);

        this.captchaFields = shuffled.subList(0, Math.min(shuffled.size(), 16));
    }

    public List<CaptchaField> captchaFields() {
        return captchaFields;
    }

    public void complete(Consumer<CaptchaResult> resultHandler) {
        finished(true);
        for (CaptchaField captchaField : captchaFields) {
            if (captchaField.isFilled() && !captchaField.isCorrectAnswer() || captchaField.isCorrectAnswer() && !captchaField.isFilled()) {
                result = CaptchaResult.FAILURE;
                break;
            }
        }
        resultHandler.accept(result);
        if (result == CaptchaResult.SUCCESS) {
            player.playSound(player, "minecraft:entity.player.levelup", 1, 1);
        } else {
            player.playSound(player, "minecraft:block.anvil.destroy", 1, 1);
        }
    }

    public CaptchaSet captchaSet() {
        return captchaSet;
    }
}
