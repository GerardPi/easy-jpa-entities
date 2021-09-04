package io.github.gerardpi.easy.jpaentities.test1.persistence;
import java.util.UUID;
import java.util.Objects;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * This is a base class for DTO classes.
 */
public abstract class EntityDto implements Serializable {
  public static final String PROPNAME_ID = "id";
  private final UUID id;

  protected EntityDto(UUID id) {
    this.id = id;
  }

  protected EntityDto() {
    this.id = null;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public UUID getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "id=" + id.toString();
  }

  @JsonIgnore
  public boolean isNew () {
    return this.id == null;
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
    if (!(otherObject instanceof EntityDto)) {
      return false;
    }

    EntityDto otherEntity = (EntityDto) otherObject;
    return Objects.equals(this, otherEntity);
  }

  public final int hashCode() {
    // Id may be null: will be determined by backend, never by frontend
    return id == null ? Objects.hashCode(this) : id.hashCode();
  }
}
