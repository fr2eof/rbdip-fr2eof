package com.rbdip.bookstore.review;

/**
 * Порт модуля review к данным о покупках. Реализация живёт в order
 * (адаптер), поэтому review не зависит от внутренних классов order.
 */
public interface PurchaseLookup {

    boolean hasAnyOrdersAndItems();
}
