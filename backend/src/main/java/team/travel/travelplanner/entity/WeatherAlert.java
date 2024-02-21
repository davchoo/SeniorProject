package team.travel.travelplanner.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.entity.type.alert.*;

import java.time.Instant;
import java.util.List;

@Entity
public class WeatherAlert {
    @Id
    @Column(length = 128)
    private String id; // TODO somehow use long ids instead to avoid wasting space in geocodesame and reference tables

    @Column(length = 2048) // Longest so far is 1170 characters
    private String areaDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(length = 6)
    private List<String> geocodeSAME;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "refs", length = 128)
    private List<String> references;

    private Instant sent;

    private Instant effective;

    private Instant onset;

    private Instant expires;

    private Instant ends;

    private AlertStatus status;

    private AlertMessageType messageType;

    private AlertCategory category;

    private AlertSeverity severity;

    private AlertCertainty certainty;

    private AlertUrgency urgency;

    private String event;

    private String senderName;

    private String headline;

    @Column(length = 8192) // Longest description so far was 4841 characters
    private String description;

    @Column(length = 1024)
    private String instruction;

    private AlertResponseType response;

    private Geometry geometry;

    private boolean outdated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public List<String> getGeocodeSAME() {
        return geocodeSAME;
    }

    public void setGeocodeSAME(List<String> geocodeSAME) {
        this.geocodeSAME = geocodeSAME;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public Instant getSent() {
        return sent;
    }

    public void setSent(Instant sent) {
        this.sent = sent;
    }

    public Instant getEffective() {
        return effective;
    }

    public void setEffective(Instant effective) {
        this.effective = effective;
    }

    public Instant getOnset() {
        return onset;
    }

    public void setOnset(Instant onset) {
        this.onset = onset;
    }

    public Instant getExpires() {
        return expires;
    }

    public void setExpires(Instant expires) {
        this.expires = expires;
    }

    public Instant getEnds() {
        return ends;
    }

    public void setEnds(Instant ends) {
        this.ends = ends;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public AlertMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(AlertMessageType messageType) {
        this.messageType = messageType;
    }

    public AlertCategory getCategory() {
        return category;
    }

    public void setCategory(AlertCategory category) {
        this.category = category;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public AlertCertainty getCertainty() {
        return certainty;
    }

    public void setCertainty(AlertCertainty certainty) {
        this.certainty = certainty;
    }

    public AlertUrgency getUrgency() {
        return urgency;
    }

    public void setUrgency(AlertUrgency urgency) {
        this.urgency = urgency;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public AlertResponseType getResponse() {
        return response;
    }

    public void setResponse(AlertResponseType response) {
        this.response = response;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }
}
