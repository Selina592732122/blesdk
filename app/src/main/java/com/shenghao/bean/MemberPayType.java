package com.shenghao.bean;


import com.shenghao.utils.PayUtils;

/**
 * 会员套餐类型
 */
public class MemberPayType {
    private String amountType;  //套餐类型，ONE_YEAR/TWO_YEAR/THREE_YEAR
    private int amount; //折后金额，单位分
    private int originalAmount; //原价
    private String desc;    //1年/2年/3年
    private boolean isSelected;

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(int originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDisplayAmount() {
        return PayUtils.stripDoubleTrailingZeros(this.amount / 100.0);
    }

    public String getDisplayOriginalAmount() {
        return PayUtils.stripDoubleTrailingZeros(this.originalAmount / 100.0);
    }

    public void copyToSelf(MemberPayType memberPayType) {
        memberPayType.setAmountType(this.getAmountType());
        memberPayType.setDesc(this.getDesc());
        memberPayType.setAmount(this.getAmount());
        memberPayType.setOriginalAmount(this.getOriginalAmount());
        memberPayType.setSelected(this.isSelected());
    }

}
