package com.chuang.tauceti.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 双值对象
 * @param <ONE> 第一个值
 * @param <TWO> 第二个值
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiValue<ONE, TWO> {

    private ONE one;

    private TWO two;
}
