package ru.rsmu.facadeEispo.service;

/**
 * @author leonid.
 */
public interface ExcelLayout {
    public static final short CASE_NUMBER = 0;
    public static final short FIRST_NAME = 3;
    public static final short MIDDLE_NAME = 4;
    public static final short LAST_NAME = 2;
    public static final short BIRTH_DATE = 0;
    public static final short EMAIL = 32;
    public static final short PHONE = 31;

    public static final short SNILS = 0;
    public static final short R_FIRST_NAME = 2;
    public static final short R_MIDDLE_NAME = 3;
    public static final short R_LAST_NAME = 1;
    public static final short R_BIRTH_DATE = 6;
    public static final short R_CITIZENSHIP = 7;
    public static final short R_SPECIALITY = 8;
    public static final short R_TARGET = 11;
    public static final short R_FINANCING = 9;
    public static final short R_APPLICATION_DATE = 10;
    public static final short R_EXAM_ORG = 13;
    public static final short R_EXAM_TYPE = 12;
    public static final short R_EXAM_YEAR = 14;

    public static final short SC_SNILS = 0;
    public static final short SC_SPECIALITY = 6;

    public static final short L_SNILS = 0;
    public static final short L_SCHEDULED = 4;

    //ACHIEVEMENTS LAYOUT
    public static final short A_CASE = 1;
    public static final short A_SCORE = 3;
    public static final short A_ACHIEVEMENTS = 2;


}
