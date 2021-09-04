package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class PersonAddress extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag {
  public static final String PROPNAME_ADDRESSID = "addressId";
  @javax.persistence.JoinColumn(nullable = false, table = "address", referencedColumnName = "id")
  private final java.util.UUID addressId;
  public static final String PROPNAME_PERSONID = "personId";
  @javax.persistence.JoinColumn(nullable = false, table = "person", referencedColumnName = "id")
  private final java.util.UUID personId;
  public static final String PROPNAME_TYPES = "types";
  @javax.persistence.Convert(converter = io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.SortedPersonAddressTypeSetConverter.class)
  private final java.util.SortedSet<io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddressType> types;
  PersonAddress() {
    this.addressId = null;
    this.personId = null;
    this.types = null;
  }
  PersonAddress(Builder builder) {
    super(builder.id, builder.etag, builder.isModified);
    this.addressId = builder.addressId;
    this.personId = builder.personId;
    this.types = com.google.common.collect.ImmutableSortedSet.copyOf(builder.types);
  }
  public java.util.UUID getAddressId () {
    return addressId;
  }
  public java.util.UUID getPersonId () {
    return personId;
  }
  public java.util.SortedSet<io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddressType> getTypes () {
    return types;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";etag=" + this.getEtag()
      + ";addressId=" + this.addressId
      + ";personId=" + this.personId
      + ";types=" + this.types;
  }
  public static Builder create(java.util.UUID id) {
    return new Builder(id);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private java.util.UUID addressId;
    private java.util.UUID personId;
    private java.util.SortedSet<io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddressType> types;
    private final java.util.UUID id;
    private final java.lang.Integer etag;
    private boolean isModified;
    
    private Builder(java.util.UUID id) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.etag = null;
      this.addressId = null;
      this.personId = null;
      this.types = null;
    }
    
    private Builder(PersonAddress existing) {
      this.id = existing.getId();
      this.etag = existing.getEtag();
      this.isModified = false;
      this.addressId = existing.addressId;
      this.personId = existing.personId;
      this.types = existing.types;
    }
    
    public Builder setAddressId(java.util.UUID addressId) {
      this.addressId = addressId;
      this.isModified = true;
      return this;
    }
    public Builder setPersonId(java.util.UUID personId) {
      this.personId = personId;
      this.isModified = true;
      return this;
    }
    public Builder setTypes(java.util.SortedSet<io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddressType> types) {
      this.types = types;
      this.isModified = true;
      return this;
    }
    /**
     * CAUTION: EXPERIMENTAL 
     * If the entity used to create the builder already contained this collection
     * then that collection probably is immutable.
     * Before using this add... method, first replace it with a mutable copy using the setter.
     * and only then use this add... method.
     * If the collection contains nested objects, you probably want to create some algorithm
     * specifically to make it possible to manipulate it and then use the setter to put it into the builder.
     */
    public Builder addType(io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddressType type) {
      if (this.types == null) {
        this.types = new java.util.TreeSet<>();
      }
      this.types.add(type);
      return this;
    }
    public Builder setAddressIdIfNotNull(java.util.UUID addressId) {
      if (addressId != null) {
        this.addressId = addressId;
        this.isModified = true;
      }
      return this;
    }
    public Builder setPersonIdIfNotNull(java.util.UUID personId) {
      if (personId != null) {
        this.personId = personId;
        this.isModified = true;
      }
      return this;
    }
    public Builder setTypesIfNotNull(java.util.SortedSet<io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddressType> types) {
      if (types != null) {
        this.types = types;
        this.isModified = true;
      }
      return this;
    }
    public PersonAddress build() {
      return new PersonAddress(this);
    }
  }
}
