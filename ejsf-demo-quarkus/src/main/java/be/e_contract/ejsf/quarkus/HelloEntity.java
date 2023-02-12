/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.quarkus;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "hello")
public class HelloEntity implements Serializable {

    @Id
    private String name;

    public HelloEntity() {
        super();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        HelloEntity rhs = (HelloEntity) obj;
        return new EqualsBuilder()
                .append(this.name, rhs.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.name).toHashCode();
    }
}
