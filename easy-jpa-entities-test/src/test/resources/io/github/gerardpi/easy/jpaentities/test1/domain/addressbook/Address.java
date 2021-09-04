package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class Address extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag {
  public static final String PROPNAME_COUNTRYCODE = "countryCode";
  private final java.lang.String countryCode;
  public static final String PROPNAME_CITY = "city";
  private final java.lang.String city;
  public static final String PROPNAME_POSTALCODE = "postalCode";
  private final java.lang.String postalCode;
  public static final String PROPNAME_STREET = "street";
  private final java.lang.String street;
  public static final String PROPNAME_HOUSENUMBER = "houseNumber";
  private final java.lang.String houseNumber;
  Address() {
    this.countryCode = null;
    this.city = null;
    this.postalCode = null;
    this.street = null;
    this.houseNumber = null;
  }
  Address(Builder builder) {
    super(builder.id, builder.etag, builder.isModified);
    this.countryCode = builder.countryCode;
    this.city = builder.city;
    this.postalCode = builder.postalCode;
    this.street = builder.street;
    this.houseNumber = builder.houseNumber;
  }
  public java.lang.String getCountryCode () {
    return countryCode;
  }
  public java.lang.String getCity () {
    return city;
  }
  public java.lang.String getPostalCode () {
    return postalCode;
  }
  public java.lang.String getStreet () {
    return street;
  }
  public java.lang.String getHouseNumber () {
    return houseNumber;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";etag=" + this.getEtag()
      + ";countryCode=" + this.countryCode
      + ";city=" + this.city
      + ";postalCode=" + this.postalCode
      + ";street=" + this.street
      + ";houseNumber=" + this.houseNumber;
  }
  public static Builder create(java.util.UUID id) {
    return new Builder(id);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private java.lang.String countryCode;
    private java.lang.String city;
    private java.lang.String postalCode;
    private java.lang.String street;
    private java.lang.String houseNumber;
    private final java.util.UUID id;
    private final java.lang.Integer etag;
    private boolean isModified;
    
    private Builder(java.util.UUID id) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.etag = null;
      this.countryCode = null;
      this.city = null;
      this.postalCode = null;
      this.street = null;
      this.houseNumber = null;
    }
    
    private Builder(Address existing) {
      this.id = existing.getId();
      this.etag = existing.getEtag();
      this.isModified = false;
      this.countryCode = existing.countryCode;
      this.city = existing.city;
      this.postalCode = existing.postalCode;
      this.street = existing.street;
      this.houseNumber = existing.houseNumber;
    }
    
    public Builder setCountryCode(java.lang.String countryCode) {
      this.countryCode = countryCode;
      this.isModified = true;
      return this;
    }
    public Builder setCity(java.lang.String city) {
      this.city = city;
      this.isModified = true;
      return this;
    }
    public Builder setPostalCode(java.lang.String postalCode) {
      this.postalCode = postalCode;
      this.isModified = true;
      return this;
    }
    public Builder setStreet(java.lang.String street) {
      this.street = street;
      this.isModified = true;
      return this;
    }
    public Builder setHouseNumber(java.lang.String houseNumber) {
      this.houseNumber = houseNumber;
      this.isModified = true;
      return this;
    }
    public Builder setCountryCodeIfNotNull(java.lang.String countryCode) {
      if (countryCode != null) {
        this.countryCode = countryCode;
        this.isModified = true;
      }
      return this;
    }
    public Builder setCityIfNotNull(java.lang.String city) {
      if (city != null) {
        this.city = city;
        this.isModified = true;
      }
      return this;
    }
    public Builder setPostalCodeIfNotNull(java.lang.String postalCode) {
      if (postalCode != null) {
        this.postalCode = postalCode;
        this.isModified = true;
      }
      return this;
    }
    public Builder setStreetIfNotNull(java.lang.String street) {
      if (street != null) {
        this.street = street;
        this.isModified = true;
      }
      return this;
    }
    public Builder setHouseNumberIfNotNull(java.lang.String houseNumber) {
      if (houseNumber != null) {
        this.houseNumber = houseNumber;
        this.isModified = true;
      }
      return this;
    }
    public Address build() {
      return new Address(this);
    }
  }
}
