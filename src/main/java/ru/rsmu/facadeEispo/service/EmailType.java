package ru.rsmu.facadeEispo.service;

/**
 * @author leonid.
 */
public enum EmailType {
    ERROR_NOTIFICATION( "/emails/ErrorNotification.vm","Информация о проблеме с заявлением в ординатуру");

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
