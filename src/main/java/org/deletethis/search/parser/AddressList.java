package org.deletethis.search.parser;

import java.util.*;

final public class AddressList extends AbstractList<String> {
    private List<String> addresses;

    public AddressList(List<String> addresses) {
        if(addresses.isEmpty()) {
            throw new IllegalArgumentException("empty list!");
        }
        this.addresses = addresses;
    }

    @Override
    public String get(int index) {
        return addresses.get(index);
    }

    @Override
    public int size() {
        return addresses.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressList)) return false;
        AddressList strings = (AddressList) o;
        return addresses.equals(strings.addresses);
    }

    @Override
    public int hashCode() {
        return addresses.hashCode();
    }
}
