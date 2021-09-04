package io.github.gerardpi.easy.jpaentities.test1.persistence;

import java.util.UUID;
import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This is a base class for JPA entities to be used in Spring-data.
 * It implements Spring's PersistableEntity so it can decide based on the isNew field to decide wether to use merge or persist.
 * Only use this super class when you don't care about possible concurrency issues for an entity.
 */
@MappedSuperclass
public abstract class PersistableEntity implements Serializable, org.springframework.data.domain.Persistable<UUID> {
  public static final String PROPNAME_ID = "id";
  @Id
  private final UUID id;

  @javax.persistence.Transient
  private final boolean isPersisted;
  @javax.persistence.Transient
  private final boolean isModified;

  protected PersistableEntity(UUID id, boolean isPersisted, boolean isModified) {
    this.id = id;
    this.isPersisted = isPersisted;
    this.isModified = isModified;
  }

  protected PersistableEntity() {
    // ORM requires this default constructor
    this.id = null;
    this.isPersisted = true;
    this.isModified = false;
  }

  /**
   * This method indicates that the entity with the current values was the result of
   * creating a modified version from an existing entity using a Builder (then the return value is true),
   * or freshly fetched from the database (the the return value is false).
   */
  public boolean isModified() {
      return this.isModified;
  }

  public UUID getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "id=" + id.toString();
  }

  @Override
  public boolean isNew() {
    return !isPersisted;
  }

  /**
   * Object equality check, this is done using the ID property of the objects.
   * Should be overriden with care, call super.equals if you want id equality
   * to count.
   */
  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (!(otherObject instanceof PersistableEntity)) {
      return false;
    }

    PersistableEntity otherPersistableEntity = (PersistableEntity) otherObject;
    // Id is never null.
    return getId().equals(otherPersistableEntity.getId());
  }

  public final int hashCode() {
    // Id is never null.
    return id.hashCode();
  }
}
