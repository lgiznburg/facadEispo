package ru.rsmu.facadeEispo.service;

/**
 * @author leonid.
 */
public enum EmailType {
    ERROR_NOTIFICATION( "/emails/ErrorNotification.vm","Информация о проблеме с заявлением в ординатуру"),
    PERSON_INFO_COMPLAIN_NOTIFICATION( "/emails/PersonInfoNotification.vm","Информация о проблеме с заявлением в ординатуру"),
    ADDITIONAL_DOCUMENT_VOLUNTEER("/emails/COVIDachievement.vm", "Требуется дополнительный документ");

    private String fileName;
    private String subject;

    EmailType( String fileName, String subject ) {
        this.fileName = fileName;
        this.subject = subject;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSubject() {
        return subject;
    }
}
