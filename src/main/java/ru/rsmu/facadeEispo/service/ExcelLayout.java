package ru.rsmu.facadeEispo.service;

/**
 * @author leonid.
 */
public interface ExcelLayout {
    short CASE_NUMBER = 0;
    short FIRST_NAME = 3;
    short MIDDLE_NAME = 4;
    short LAST_NAME = 2;
    //short BIRTH_DATE = 0;
    short EMAIL = 32;
    short PHONE = 31;
    short DIPLOMA_DATE = 18;

    short SNILS = 0;
    short R_FIRST_NAME = 2;
    short R_MIDDLE_NAME = 3;
    short R_LAST_NAME = 1;
    short R_BIRTH_DATE = 6;
    short R_CITIZENSHIP = 7;
    short R_SPECIALITY = 8;
    short R_TARGET = 11;
    short R_FINANCING = 9;
    short R_APPLICATION_DATE = 10;
    short R_EXAM_ORG = 13;
    short R_EXAM_TYPE = 12;
    short R_EXAM_YEAR = 14;
    short R_BASE_SPECIALITY = 15;
    short R_EXAM_DATE = 16;
    short R_ACHIEVEMENTS = 17;
    short R_CASE_NUMBER = 18;

    short SC_SNILS = 0;
    short SC_SPECIALITY = 6;

    short L_SNILS = 0;
    short L_SCHEDULED = 4;

    //ACHIEVEMENTS LAYOUT
    short A_CASE = 1;
    short A_SCORE = 3;
    short A_ACHIEVEMENTS = 2;


    //parse orders
    short SPECIALITY = 49;
    short EDU_FORM = 51;
    short EDU_PERIOD = 52;
    short COMPENSATION_TYPE = 53;
    short TARGETING = 54;
    short ORDER_NUMBER = 56;
    short ORDER_DATE = 57;

}
