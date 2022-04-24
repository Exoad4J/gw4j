package com.colorramp;

public class UnsafePair<E> {
  public E first, second;
  public E note;

  public UnsafePair(E first, E second) {
    this.first = first;
    this.second = second;
    note = null;
  }

  public UnsafePair(E first, E second, E note) {
    this.first = first;
    this.second = second;
    this.note = note;
  }
}
