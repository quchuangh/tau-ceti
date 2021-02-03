package com.chuang.tauceti.tools.basic.util;

public class RadixUtils {

    public static final int MAX_RADIX = 62;

    private final static char[] digits = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
            'o' , 'p' , 'q' , 'r' , 's' , 't' ,
            'u' , 'v' , 'w' , 'x' , 'y' , 'z' ,
            'A' , 'B' , 'C' , 'D' , 'E' , 'F' ,
            'G' , 'H' , 'I' , 'J' , 'K' , 'L' ,
            'M' , 'N' , 'O' , 'P' , 'Q' , 'R' ,
            'S' , 'T' , 'U' , 'V' , 'W' , 'X' ,
            'Y' , 'Z'
    };

    public static String radix(long num, int radix) {
        if(radix < 36 || radix > MAX_RADIX) {
            return Long.toString(num, radix);
        }
        return radix(num, digits, radix);
    }

    private static String radix(long num, char[] digits, int radix) {
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (num < 0);

        if (!negative) {
            num = -num;
        }

        while (num <= -radix) {
            buf[charPos--] = digits[(int)(-(num % radix))];
            num = num / radix;
        }
        buf[charPos] = digits[(int)(-num)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (65 - charPos));
    }

    public static String radix(long num, char[] digits) {
        return radix(num, digits, digits.length);
    }

}
