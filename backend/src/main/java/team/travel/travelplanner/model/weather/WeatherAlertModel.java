package team.travel.travelplanner.model.weather;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import team.travel.travelplanner.entity.type.alert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherAlertModel {
    private String id;

    @JsonAlias("areaDesc")
    private String areaDescription;

    private List<String> geocodeSAME;

    private List<String> references;

    private Instant sent;

    private Instant effective;

    private Instant onset;

    private Instant expires;

    private Instant ends;

    // JSON from NWS formats enums in CamelCase while Java enum values are CAPITALIZED_WITH_UNDERSCORES
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertStatus status;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertMessageType messageType;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertCategory category;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertSeverity severity;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertCertainty certainty;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertUrgency urgency;

    private String event;

    private String senderName;

    private String headline;

    private String description;

    private String instruction;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private AlertResponseType response;

    private String geometryWKT;

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

    public String getGeometryWKT() {
        return geometryWKT;
    }

    public void setGeometryWKT(String geometryWKT) {
        this.geometryWKT = geometryWKT;
    }

    @JsonProperty("geocode")
    private void unpackGeocode(Map<String, Object> geocode) {
        this.geocodeSAME = new ArrayList<>();
        if (geocode.get("SAME") instanceof List<?> jsonSAME) {
            for (Object object : jsonSAME) {
                this.geocodeSAME.add(object.toString());
            }
        }
    }

    @JsonProperty("references")
    private void unpackReferences(List<?> references) {
        this.references = new ArrayList<>();
        for (Object object : references) {
            if (object instanceof Map<?, ?> reference) {
                this.references.add(String.valueOf(reference.get("identifier")));
            }
        }
    }
}
