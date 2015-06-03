package org.twizer.android.datamodel;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Generated by jsonschema2pojo
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class Trend {

    @Expose
    private String name;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return Boolean.TRUE;
        }
        if (!(other instanceof Trend)) {
            return Boolean.FALSE;
        }
        Trend rhs = ((Trend) other);
        return new EqualsBuilder().append(name, rhs.name).isEquals();
    }

}