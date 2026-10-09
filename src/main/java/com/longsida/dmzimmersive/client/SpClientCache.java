package com.longsida.dmzimmersive.client;

public class SpClientCache {

    private static int sp = 0;
    private static int lastGrantedLevel = 0;
    private static float tpPool = 0;
    private static float cap = 0;
    private static float poolStr = 0;
    private static float poolSkp = 0;
    private static float poolPwr = 0;
    private static float poolRes = 0;
    private static float poolVit = 0;
    private static float poolEne = 0;

    public static void set(int newSp, int newLastGrantedLevel, float newTpPool, float newCap,
                           float newPoolStr, float newPoolSkp, float newPoolPwr,
                           float newPoolRes, float newPoolVit, float newPoolEne) {
        sp = newSp;
        lastGrantedLevel = newLastGrantedLevel;
        tpPool = newTpPool;
        cap = newCap;
        poolStr = newPoolStr;
        poolSkp = newPoolSkp;
        poolPwr = newPoolPwr;
        poolRes = newPoolRes;
        poolVit = newPoolVit;
        poolEne = newPoolEne;
    }

    public static int getSp() { return sp; }
    public static int getLastGrantedLevel() { return lastGrantedLevel; }
    public static float getTpPool() { return tpPool; }
    public static float getCap() { return cap; }

    public static float getSubPool(String stat) {
        return switch (stat.toUpperCase()) {
            case "STR" -> poolStr;
            case "SKP" -> poolSkp;
            case "PWR" -> poolPwr;
            case "RES" -> poolRes;
            case "VIT" -> poolVit;
            case "ENE" -> poolEne;
            default -> 0;
        };
    }

    public static float getProgressPercent() {
        if (cap <= 0) return 0;
        float p = tpPool / cap * 100f;
        if (p < 0) p = 0;
        if (p > 100) p = 100;
        return p;
    }
}