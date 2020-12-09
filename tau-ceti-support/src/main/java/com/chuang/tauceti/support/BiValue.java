package com.chuang.tauceti.support;

/**
 * 双值对象
 * @param <ONE> 第一个值
 * @param <TWO> 第二个值
 */
public class BiValue<ONE, TWO> {

    private ONE one;

    private TWO two;

    public BiValue(ONE one, TWO two) {
        this.one = one;
        this.two = two;
    }

    public BiValue() {}

    public ONE getOne() {
        return one;
    }

    public void setOne(ONE one) {
        this.one = one;
    }

    public TWO getTwo() {
        return two;
    }

    public void setTwo(TWO two) {
        this.two = two;
    }
}
