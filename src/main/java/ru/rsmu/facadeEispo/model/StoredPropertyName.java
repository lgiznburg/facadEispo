package ru.rsmu.facadeEispo.model;

/**
 * @author leonid.
 */
public enum StoredPropertyName {
    SYSTEM_OID("System", "Our OID", "1.2.643.5.1.13.13.12.4.77.4", StoredPropertyType.STRING),
    SYSTEM_CAMPAIGN_ID("System", "Campaign ID", "1", StoredPropertyType.INTEGER)
    ;

    private String groupName;
    private String name;
    private String defaultValue;
    private StoredPropertyType type;
    private boolean editable = true;

    StoredPropertyName( String groupName, String name, String defaultValue, StoredPropertyType type ) {
        this.groupName = groupName;
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    StoredPropertyName( String groupName, String name, String defaultValue, StoredPropertyType type, boolean editable ) {
        this.groupName = groupName;
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
        this.editable = editable;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public StoredPropertyType getType() {
        return type;
    }

    public boolean isEditable() {
        return editable;
    }
}
