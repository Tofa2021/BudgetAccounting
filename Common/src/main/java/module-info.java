module org.example.common {
    exports org.example.dto.request;
    exports org.example.dto.response;
    exports org.example.dto.model;
    exports org.example.enums;
    exports org.example.dto.request.household;
    exports org.example.dto.request.operation;
    exports org.example.dto.request.user;
    exports org.example.dto.request.account;
    exports org.example.dto.request.household_member;
    exports org.example;
    requires static lombok;
    requires jdk.dynalink;
}