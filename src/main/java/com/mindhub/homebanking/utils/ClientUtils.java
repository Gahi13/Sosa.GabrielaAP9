package com.mindhub.homebanking.utils;

public class ClientUtils {
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}