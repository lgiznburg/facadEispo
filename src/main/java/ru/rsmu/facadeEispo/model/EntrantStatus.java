package ru.rsmu.facadeEispo.model;

/**
 * @author leonid.
 */
public enum EntrantStatus {
    NEW,   // новый
    UPDATED,  // изменения
    SUBMITTED, // передано в любом исходе
    FOREIGNER,  // иностранец
    RETIRED, // отзыв
    ENFORCED, // нам плевать
    EXPELLED  //отчисление
}
