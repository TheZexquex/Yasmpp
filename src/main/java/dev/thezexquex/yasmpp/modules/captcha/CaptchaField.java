package dev.thezexquex.yasmpp.modules.captcha;

import org.bukkit.inventory.ItemStack;

public class CaptchaField {
    private final ItemStack itemStack;
    private final boolean isCorrectAnswer;
    private boolean isFilled = false;

    public CaptchaField(ItemStack itemStack, boolean isCorrectAnswer) {
        this.itemStack = itemStack;
        this.isCorrectAnswer = isCorrectAnswer;
    }

    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }

    public void filled(boolean filled) {
        isFilled = filled;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public boolean isFilled() {
        return isFilled;
    }
}
