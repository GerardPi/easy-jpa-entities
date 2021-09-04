package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class Currency extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntity {
  public static final String PROPNAME_CODE = "code";
  @javax.validation.constraints.Size(min=3, max=3)
  @javax.validation.constraints.NotNull
  @javax.persistence.Column(unique = true)
  private final java.lang.String code;
  public static final String PROPNAME_NAME = "name";
  @javax.validation.constraints.NotNull
  @javax.persistence.Column(unique = true)
  private final java.lang.String name;
  Currency() {
    this.code = null;
    this.name = null;
  }
  Currency(Builder builder) {
    super(builder.id, builder.isPersisted, builder.isModified);
    this.code = builder.code;
    this.name = builder.name;
  }
  public java.lang.String getCode () {
    return code;
  }
  public java.lang.String getName () {
    return name;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";code=" + this.code
      + ";name=" + this.name;
  }
  public static Builder create(java.util.UUID id) {
    return new Builder(id);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private java.lang.String code;
    private java.lang.String name;
    private final java.util.UUID id;
    private final boolean isPersisted;
    private boolean isModified;
    
    private Builder(java.util.UUID id) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.isPersisted = false;
      this.code = null;
      this.name = null;
    }
    
    private Builder(Currency existing) {
      this.id = existing.getId();
      this.isPersisted = true;
      this.isModified = false;
      this.code = existing.code;
      this.name = existing.name;
    }
    
    public Builder setCode(java.lang.String code) {
      this.code = code;
      this.isModified = true;
      return this;
    }
    public Builder setName(java.lang.String name) {
      this.name = name;
      this.isModified = true;
      return this;
    }
    public Builder setCodeIfNotNull(java.lang.String code) {
      if (code != null) {
        this.code = code;
        this.isModified = true;
      }
      return this;
    }
    public Builder setNameIfNotNull(java.lang.String name) {
      if (name != null) {
        this.name = name;
        this.isModified = true;
      }
      return this;
    }
    public Currency build() {
      return new Currency(this);
    }
  }
}
