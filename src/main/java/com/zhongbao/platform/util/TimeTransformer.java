package com.zhongbao.platform.util;

public class TimeTransformer {
    public static double convertToHour(long startMilliSec, long endMilliSec) {
        return (endMilliSec - startMilliSec) / 1000.0 / 60.0 / 60;
    }
}
