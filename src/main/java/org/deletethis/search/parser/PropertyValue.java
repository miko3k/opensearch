package org.deletethis.search.parser;

import java.util.Objects;

public interface PropertyValue {
    enum Predefined implements PropertyValue {
        YES, NO
    }

    final class Literal implements PropertyValue {
        private final String value;

        public Literal(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Literal)) return false;
            Literal that = (Literal) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public java.lang.String toString() {
            return value;
        }
    }
}
