package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class ItemOrderLine extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag {
  public static final String PROPNAME_ITEMID = "itemId";
  private final java.util.UUID itemId;
  public static final String PROPNAME_ITEMORDERID = "itemOrderId";
  private final java.util.UUID itemOrderId;
  public static final String PROPNAME_COUNT = "count";
  private final java.lang.Integer count;
  public static final String PROPNAME_AMOUNTPERITEM = "amountPerItem";
  private final java.math.BigDecimal amountPerItem;
  ItemOrderLine() {
    this.itemId = null;
    this.itemOrderId = null;
    this.count = null;
    this.amountPerItem = null;
  }
  ItemOrderLine(Builder builder) {
    super(builder.id, builder.etag, builder.isModified);
    this.itemId = builder.itemId;
    this.itemOrderId = builder.itemOrderId;
    this.count = builder.count;
    this.amountPerItem = builder.amountPerItem;
  }
  public java.util.UUID getItemId () {
    return itemId;
  }
  public java.util.UUID getItemOrderId () {
    return itemOrderId;
  }
  public java.lang.Integer getCount () {
    return count;
  }
  public java.math.BigDecimal getAmountPerItem () {
    return amountPerItem;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";etag=" + this.getEtag()
      + ";itemId=" + this.itemId
      + ";itemOrderId=" + this.itemOrderId
      + ";count=" + this.count
      + ";amountPerItem=" + this.amountPerItem;
  }
  public static Builder create(java.util.UUID id) {
    return new Builder(id);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private java.util.UUID itemId;
    private java.util.UUID itemOrderId;
    private java.lang.Integer count;
    private java.math.BigDecimal amountPerItem;
    private final java.util.UUID id;
    private final java.lang.Integer etag;
    private boolean isModified;
    
    private Builder(java.util.UUID id) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.etag = null;
      this.itemId = null;
      this.itemOrderId = null;
      this.count = null;
      this.amountPerItem = null;
    }
    
    private Builder(ItemOrderLine existing) {
      this.id = existing.getId();
      this.etag = existing.getEtag();
      this.isModified = false;
      this.itemId = existing.itemId;
      this.itemOrderId = existing.itemOrderId;
      this.count = existing.count;
      this.amountPerItem = existing.amountPerItem;
    }
    
    public Builder setItemId(java.util.UUID itemId) {
      this.itemId = itemId;
      this.isModified = true;
      return this;
    }
    public Builder setItemOrderId(java.util.UUID itemOrderId) {
      this.itemOrderId = itemOrderId;
      this.isModified = true;
      return this;
    }
    public Builder setCount(java.lang.Integer count) {
      this.count = count;
      this.isModified = true;
      return this;
    }
    public Builder setAmountPerItem(java.math.BigDecimal amountPerItem) {
      this.amountPerItem = amountPerItem;
      this.isModified = true;
      return this;
    }
    public Builder setItemIdIfNotNull(java.util.UUID itemId) {
      if (itemId != null) {
        this.itemId = itemId;
        this.isModified = true;
      }
      return this;
    }
    public Builder setItemOrderIdIfNotNull(java.util.UUID itemOrderId) {
      if (itemOrderId != null) {
        this.itemOrderId = itemOrderId;
        this.isModified = true;
      }
      return this;
    }
    public Builder setCountIfNotNull(java.lang.Integer count) {
      if (count != null) {
        this.count = count;
        this.isModified = true;
      }
      return this;
    }
    public Builder setAmountPerItemIfNotNull(java.math.BigDecimal amountPerItem) {
      if (amountPerItem != null) {
        this.amountPerItem = amountPerItem;
        this.isModified = true;
      }
      return this;
    }
    public ItemOrderLine build() {
      return new ItemOrderLine(this);
    }
  }
}
