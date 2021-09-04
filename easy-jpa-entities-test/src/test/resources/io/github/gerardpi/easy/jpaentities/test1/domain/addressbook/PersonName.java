package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

@SuppressWarnings("java:S2637")
public class PersonName implements java.io.Serializable {
  public static final String PROPNAME_FIRST = "first";
  private final java.lang.String first;
  public static final String PROPNAME_LAST = "last";
  private final java.lang.String last;
  PersonName() {
    this.first = null;
    this.last = null;
  }
  PersonName(Builder builder) {
    this.first = builder.first;
    this.last = builder.last;
  }
  public java.lang.String getFirst () {
    return first;
  }
  public java.lang.String getLast () {
    return last;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";first=" + this.first
      + ";last=" + this.last;
  }
  public static Builder create() {
    return new Builder();
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private java.lang.String first;
    private java.lang.String last;
    
    private Builder() {
      this.first = null;
      this.last = null;
    }
    
    private Builder(PersonName existing) {
      this.first = existing.first;
      this.last = existing.last;
    }
    
    public Builder setFirst(java.lang.String first) {
      this.first = first;
      return this;
    }
    public Builder setLast(java.lang.String last) {
      this.last = last;
      return this;
    }
    public Builder setFirstIfNotNull(java.lang.String first) {
      if (first != null) {
        this.first = first;
      }
      return this;
    }
    public Builder setLastIfNotNull(java.lang.String last) {
      if (last != null) {
        this.last = last;
      }
      return this;
    }
    public PersonName build() {
      return new PersonName(this);
    }
  }
}
