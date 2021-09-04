package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class ItemOrder extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag {
  public static final String PROPNAME_PERSONID = "personId";
  private final java.util.UUID personId;
  public static final String PROPNAME_DESCRIPTION = "description";
  private final java.lang.String description;
  public static final String PROPNAME_DATETIME = "dateTime";
  @javax.persistence.Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private final java.time.OffsetDateTime dateTime;
  ItemOrder() {
    this.personId = null;
    this.description = null;
    this.dateTime = null;
  }
  ItemOrder(Builder builder) {
    super(builder.id, builder.etag, builder.isModified);
    this.personId = builder.personId;
    this.description = builder.description;
    this.dateTime = builder.dateTime;
  }
  public java.util.UUID getPersonId () {
    return personId;
  }
  public java.lang.String getDescription () {
    return description;
  }
  public java.time.OffsetDateTime getDateTime () {
    return dateTime;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";etag=" + this.getEtag()
      + ";personId=" + this.personId
      + ";description=" + this.description
      + ";dateTime=" + this.dateTime;
  }
  public static Builder create(java.util.UUID id) {
    return new Builder(id);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private java.util.UUID personId;
    private java.lang.String description;
    private java.time.OffsetDateTime dateTime;
    private final java.util.UUID id;
    private final java.lang.Integer etag;
    private boolean isModified;
    
    private Builder(java.util.UUID id) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.etag = null;
      this.personId = null;
      this.description = null;
      this.dateTime = null;
    }
    
    private Builder(ItemOrder existing) {
      this.id = existing.getId();
      this.etag = existing.getEtag();
      this.isModified = false;
      this.personId = existing.personId;
      this.description = existing.description;
      this.dateTime = existing.dateTime;
    }
    
    public Builder setPersonId(java.util.UUID personId) {
      this.personId = personId;
      this.isModified = true;
      return this;
    }
    public Builder setDescription(java.lang.String description) {
      this.description = description;
      this.isModified = true;
      return this;
    }
    public Builder setDateTime(java.time.OffsetDateTime dateTime) {
      this.dateTime = dateTime;
      this.isModified = true;
      return this;
    }
    public Builder setPersonIdIfNotNull(java.util.UUID personId) {
      if (personId != null) {
        this.personId = personId;
        this.isModified = true;
      }
      return this;
    }
    public Builder setDescriptionIfNotNull(java.lang.String description) {
      if (description != null) {
        this.description = description;
        this.isModified = true;
      }
      return this;
    }
    public Builder setDateTimeIfNotNull(java.time.OffsetDateTime dateTime) {
      if (dateTime != null) {
        this.dateTime = dateTime;
        this.isModified = true;
      }
      return this;
    }
    public ItemOrder build() {
      return new ItemOrder(this);
    }
  }
}
