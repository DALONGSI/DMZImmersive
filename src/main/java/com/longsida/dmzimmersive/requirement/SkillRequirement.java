package com.longsida.dmzimmersive.requirement;

import java.util.HashMap;
import java.util.Map;

public class SkillRequirement {

    /** 六项属性的需求。key 是 STR/SKP/PWR/RES/VIT/ENE */
    public Map<String, StatReq> stats = new HashMap<>();

    public static class StatReq {
        public int base = 0;
        public int perLevel = 0;
        public int cap = Integer.MAX_VALUE;

        public StatReq() {}

        public StatReq(int base, int perLevel, int cap) {
            this.base = base;
            this.perLevel = perLevel;
            this.cap = cap;
        }

        /** 计算当前等级下的需求 */
        public int resolve(int level) {
            long value = (long) base + (long) perLevel * level;
            if (value > cap) value = cap;
            if (value < 0) value = 0;
            return (int) value;
        }
    }
}