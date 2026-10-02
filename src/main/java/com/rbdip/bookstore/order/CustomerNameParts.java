package com.rbdip.bookstore.order;

final class CustomerNameParts {

    static CustomerNameParts fromFullName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            return new CustomerNameParts("", "");
        }
        int space = trimmed.indexOf(' ');
        if (space < 0) {
            return new CustomerNameParts(trimmed, "");
        }
        return new CustomerNameParts(trimmed.substring(0, space), trimmed.substring(space + 1).trim());
    }

    private final String firstName;
    private final String lastName;

    private CustomerNameParts(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    String firstName() {
        return firstName;
    }

    String lastName() {
        return lastName;
    }
}
