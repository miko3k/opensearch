package org.deletethis.search.parser;

import java.util.Objects;

public interface PropertyValue {
    enum Predefined implements PropertyValue {
        YES, NO;

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitPredefined(this);
        }
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

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    final class Url implements PropertyValue {
        private final String href;
        private final String value;

        public Url(String href, String value) {
            this.href = href;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Url)) return false;
            Url url = (Url) o;
            return Objects.equals(href, url.href) &&
                    Objects.equals(value, url.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(href, value);
        }

        @Override
        public String toString() {
            return value + " <" + href + ">";
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitUrl(this);
        }

        public String getHref() {
            return href;
        }

        public String getValue() {
            return value;
        }
    }

    interface Visitor<T> {
        T visitLiteral(Literal literal);
        T visitPredefined(Predefined predefined);
        T visitUrl(Url url);
    }

    <T> T accept(Visitor<T> visitor);
}
