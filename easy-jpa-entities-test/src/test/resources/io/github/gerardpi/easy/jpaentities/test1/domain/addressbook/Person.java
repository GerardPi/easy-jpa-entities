package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class Person extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag {
  public static final String PROPNAME_NAME = "name";
  @javax.persistence.Embedded
  private final io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName name;
  public static final String PROPNAME_DATEOFBIRTH = "dateOfBirth";
  @javax.persistence.Column(columnDefinition = "DATE")
  private final java.time.LocalDate dateOfBirth;
  Person() {
    this.name = null;
    this.dateOfBirth = null;
  }
  Person(Builder builder) {
    super(builder.id, builder.etag, builder.isModified);
    this.name = builder.name;
    this.dateOfBirth = builder.dateOfBirth;
  }
  public io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName getName () {
    return name;
  }
  public java.time.LocalDate getDateOfBirth () {
    return dateOfBirth;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";etag=" + this.getEtag()
      + ";name=" + this.name
      + ";dateOfBirth=" + this.dateOfBirth;
  }
  public static Builder create(java.util.UUID id) {
    return new Builder(id);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName name;
    private java.time.LocalDate dateOfBirth;
    private final java.util.UUID id;
    private final java.lang.Integer etag;
    private boolean isModified;
    
    private Builder(java.util.UUID id) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.etag = null;
      this.name = null;
      this.dateOfBirth = null;
    }
    
    private Builder(Person existing) {
      this.id = existing.getId();
      this.etag = existing.getEtag();
      this.isModified = false;
      this.name = existing.name;
      this.dateOfBirth = existing.dateOfBirth;
    }
    
    public Builder setName(io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName name) {
      this.name = name;
      this.isModified = true;
      return this;
    }
    public Builder setDateOfBirth(java.time.LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
      this.isModified = true;
      return this;
    }
    public Builder setNameIfNotNull(io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName name) {
      if (name != null) {
        this.name = name;
        this.isModified = true;
      }
      return this;
    }
    public Builder setDateOfBirthIfNotNull(java.time.LocalDate dateOfBirth) {
      if (dateOfBirth != null) {
        this.dateOfBirth = dateOfBirth;
        this.isModified = true;
      }
      return this;
    }
    public Person build() {
      return new Person(this);
    }
  }
}
