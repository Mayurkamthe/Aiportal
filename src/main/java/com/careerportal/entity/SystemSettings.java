package com.careerportal.entity;

import jakarta.persistence.*;

/** SystemSettings entity - stores admin configurable settings */
@Entity
@Table(name = "system_settings")
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String settingKey;

    @Column(length = 2000)
    private String settingValue;

    private String description;

    public SystemSettings() {}

    public SystemSettings(String key, String value, String description) {
        this.settingKey = key;
        this.settingValue = value;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
