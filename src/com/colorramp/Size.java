package com.colorramp;

public class Size {
  private Size() {}
  public static final int width = 1000;
  public static final int height = 800;

  public static final Pair<Integer, Integer> RES480 = new Pair<>(854, 480);
  public static final Pair<Integer, Integer> RES720 = new Pair<>(1280, 720);
  public static final Pair<Integer, Integer> RES1080 = new Pair<>(1920, 1080);
  public static final Pair<Integer, Integer> RES1440 = new Pair<>(2560, 1440);
  public static final Pair<Integer, Integer> RES4K = new Pair<>(3840, 2160);
}
