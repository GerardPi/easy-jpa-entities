package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

@javax.persistence.Entity
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
@SuppressWarnings("java:S2637")
public class Item extends io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag {
  public static final String PROPNAME_FRIENDLYID = "friendlyId";
  private final java.lang.String friendlyId;
  public static final String PROPNAME_TYPE = "type";
  @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
  private final ItemType type;
  public static final String PROPNAME_NAME = "name";
  private final java.lang.String name;
  public static final String PROPNAME_TEXTS = "texts";
  @javax.persistence.Column(nullable = false, length = 4096)
  @javax.persistence.Convert(converter = LocalizedTextsMapConverter.class)
  private final java.util.SortedMap<io.github.gerardpi.easy.jpaentities.test1.domain.Lang, java.util.SortedMap<ItemTextType, String>> texts;
  public static final String PROPNAME_ATTRIBUTES = "attributes";
  @javax.persistence.Column(nullable = false)
  @javax.persistence.Convert(converter = AttributeKeyToStringMapConverter.class)
  private final java.util.SortedMap<ItemAttributeKey, String> attributes;
  public static final String PROPNAME_IMAGENAMES = "imageNames";
  @javax.persistence.Convert(converter = io.github.gerardpi.easy.jpaentities.test1.persistence.SortedStringSetConverter.class)
  private final java.util.SortedSet<String> imageNames;
  Item() {
    this.friendlyId = null;
    this.type = null;
    this.name = null;
    this.texts = null;
    this.attributes = null;
    this.imageNames = null;
  }
  Item(Builder builder) {
    super(builder.id, builder.etag, builder.isModified);
    this.friendlyId = builder.friendlyId;
    this.type = builder.type;
    this.name = builder.name;
    this.texts = builder.texts;
    this.attributes = builder.attributes;
    this.imageNames = com.google.common.collect.ImmutableSortedSet.copyOf(builder.imageNames);
  }
  public java.lang.String getFriendlyId () {
    return friendlyId;
  }
  public ItemType getType () {
    return type;
  }
  public java.lang.String getName () {
    return name;
  }
  public java.util.SortedMap<io.github.gerardpi.easy.jpaentities.test1.domain.Lang, java.util.SortedMap<ItemTextType, String>> getTexts () {
    return texts;
  }
  public java.util.SortedMap<ItemAttributeKey, String> getAttributes () {
    return attributes;
  }
  public java.util.SortedSet<String> getImageNames () {
    return imageNames;
  }
  @Override
  public String toString() {
    return "class=" + this.getClass().getName()
      + ";id="+ this.getId()
      + ";isModified="+ this.isModified()
      + ";etag=" + this.getEtag()
      + ";friendlyId=" + this.friendlyId
      + ";type=" + this.type
      + ";name=" + this.name
      + ";texts=" + this.texts
      + ";attributes=" + this.attributes
      + ";imageNames=" + this.imageNames;
  }
  public static Builder create(java.util.UUID id, java.lang.String friendlyId) {
    return new Builder(id, friendlyId);
  }
  
  public Builder modify() {
    return new Builder(this);
  }
  
  public static class Builder {
    private final java.lang.String friendlyId;
    private ItemType type;
    private java.lang.String name;
    private java.util.SortedMap<io.github.gerardpi.easy.jpaentities.test1.domain.Lang, java.util.SortedMap<ItemTextType, String>> texts;
    private java.util.SortedMap<ItemAttributeKey, String> attributes;
    private java.util.SortedSet<String> imageNames;
    private final java.util.UUID id;
    private final java.lang.Integer etag;
    private boolean isModified;
    
    private Builder(java.util.UUID id, java.lang.String friendlyId) {
      this.id = java.util.Objects.requireNonNull(id);
      this.isModified = false;
      this.etag = null;
      this.friendlyId = friendlyId;
      this.type = null;
      this.name = null;
      this.texts = null;
      this.attributes = null;
      this.imageNames = null;
    }
    
    private Builder(Item existing) {
      this.id = existing.getId();
      this.etag = existing.getEtag();
      this.isModified = false;
      this.friendlyId = existing.friendlyId;
      this.type = existing.type;
      this.name = existing.name;
      this.texts = existing.texts;
      this.attributes = existing.attributes;
      this.imageNames = existing.imageNames;
    }
    
    public Builder setType(ItemType type) {
      this.type = type;
      this.isModified = true;
      return this;
    }
    public Builder setName(java.lang.String name) {
      this.name = name;
      this.isModified = true;
      return this;
    }
    public Builder setTexts(java.util.SortedMap<io.github.gerardpi.easy.jpaentities.test1.domain.Lang, java.util.SortedMap<ItemTextType, String>> texts) {
      this.texts = texts;
      this.isModified = true;
      return this;
    }
    public Builder setAttributes(java.util.SortedMap<ItemAttributeKey, String> attributes) {
      this.attributes = attributes;
      this.isModified = true;
      return this;
    }
    public Builder setImageNames(java.util.SortedSet<String> imageNames) {
      this.imageNames = imageNames;
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
    public Builder addImageNames(String imageNames) {
      if (this.imageNames == null) {
        this.imageNames = new java.util.TreeSet<>();
      }
      this.imageNames.add(imageNames);
      return this;
    }
    public Builder setTypeIfNotNull(ItemType type) {
      if (type != null) {
        this.type = type;
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
    public Builder setTextsIfNotNull(java.util.SortedMap<io.github.gerardpi.easy.jpaentities.test1.domain.Lang, java.util.SortedMap<ItemTextType, String>> texts) {
      if (texts != null) {
        this.texts = texts;
        this.isModified = true;
      }
      return this;
    }
    public Builder setAttributesIfNotNull(java.util.SortedMap<ItemAttributeKey, String> attributes) {
      if (attributes != null) {
        this.attributes = attributes;
        this.isModified = true;
      }
      return this;
    }
    public Builder setImageNamesIfNotNull(java.util.SortedSet<String> imageNames) {
      if (imageNames != null) {
        this.imageNames = imageNames;
        this.isModified = true;
      }
      return this;
    }
    public Item build() {
      return new Item(this);
    }
  }
}
