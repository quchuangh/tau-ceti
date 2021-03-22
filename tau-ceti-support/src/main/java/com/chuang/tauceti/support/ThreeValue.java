package com.chuang.tauceti.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 双值对象
 * @param <ONE> 第一个值
 * @param <TWO> 第二个值
 * @param <THREE> 第三个值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreeValue<ONE, TWO, THREE> {

    private ONE one;

    private TWO two;

    private THREE three;

}
