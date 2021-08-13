package ru.rsmu.facadeEispo.model;

/**
 * @author leonid.
 */
public enum RequestStatus {
    NEW,     //новый
    CONFIRMED,  // передано-подтверждено
    REJECTED, // передано-отклонено
    RETIRED, // отзыв
    TERMINATED, // передано-отозвано
    REFRESHING  // обновление : сначала удалить, потом передать
}
