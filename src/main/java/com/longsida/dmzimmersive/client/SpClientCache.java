package com.longsida.dmzimmersive.client;

public class SpClientCache {

    private static int sp = 0;
    private static int lastGrantedLevel = 0;

    public static void set(int newSp, int newLastGrantedLevel) {
        sp = newSp;
        lastGrantedLevel = newLastGrantedLevel;
    }

    public static int getSp() {
        return sp;
    }

    public static int getLastGrantedLevel() {
        return lastGrantedLevel;
    }
}