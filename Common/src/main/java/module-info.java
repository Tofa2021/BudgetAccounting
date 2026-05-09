module org.example.common {
    exports org.example.dto.request;
    exports org.example.dto.response;
    exports org.example.dto.model;
    exports org.example.enums;
    exports org.example;
    requires static lombok;
    requires jdk.dynalink;
}