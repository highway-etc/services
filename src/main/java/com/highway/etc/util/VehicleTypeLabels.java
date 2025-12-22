package com.highway.etc.util;

import java.util.HashMap;
import java.util.Map;

public final class VehicleTypeLabels {

    private static final Map<String, String> CODE_TO_NAME = new HashMap<>();

    static {
        CODE_TO_NAME.put("01", "大型汽车");
        CODE_TO_NAME.put("02", "小型汽车");
        CODE_TO_NAME.put("03", "使馆汽车");
        CODE_TO_NAME.put("04", "领馆汽车");
        CODE_TO_NAME.put("05", "境外汽车");
        CODE_TO_NAME.put("06", "外籍汽车");
        CODE_TO_NAME.put("07", "普通摩托车");
        CODE_TO_NAME.put("08", "轻便摩托车");
        CODE_TO_NAME.put("16", "教练汽车");
        CODE_TO_NAME.put("20", "公交客车");
        CODE_TO_NAME.put("21", "出租客运");
        CODE_TO_NAME.put("22", "旅游客车");
        CODE_TO_NAME.put("23", "警用车辆");
        CODE_TO_NAME.put("24", "消防车辆");
        CODE_TO_NAME.put("25", "救护车辆");
        CODE_TO_NAME.put("26", "工程救险");
        CODE_TO_NAME.put("31", "武警车辆");
        CODE_TO_NAME.put("32", "军队车辆");
        CODE_TO_NAME.put("51", "大功率摩托");
        CODE_TO_NAME.put("52", "新能源小型");
        CODE_TO_NAME.put("53", "新能源大型");
        CODE_TO_NAME.put("54", "新能源货车");
    }

    private VehicleTypeLabels() {
    }

    public static String toName(String codeOrName) {
        if (codeOrName == null || codeOrName.isBlank()) {
            return "未知车型";
        }
        String normalized = codeOrName.trim();
        if (CODE_TO_NAME.containsKey(normalized)) {
            return CODE_TO_NAME.get(normalized);
        }
        // Some data may already be a human-readable name.
        return normalized;
    }
}
